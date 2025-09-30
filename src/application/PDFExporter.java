package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class PDFExporter {

    public File exportExpensesToPDF(Group group, List<Expense> expenses) throws IOException {
        File folder = new File("exports");
        if (!folder.exists()) folder.mkdir();

        String fileName = "GroupExpenses_" + group.getName() + ".pdf";
        File file = new File(folder, fileName);

        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream content = new PDPageContentStream(document, page);

        content.setFont(PDType1Font.HELVETICA_BOLD, 16);
        content.beginText();
        content.newLineAtOffset(150, 750);
        content.showText("Expenses for group: " + group.getName());
        content.endText();

        int yPosition = 720;
        content.setFont(PDType1Font.HELVETICA, 12);


        for (Expense e : expenses) {
            if (yPosition < 50) {
                content.close();
                page = new PDPage();
                document.addPage(page);
                content = new PDPageContentStream(document, page);
                content.setFont(PDType1Font.HELVETICA, 12);
                yPosition = 750;
            }
            content.beginText();
            content.newLineAtOffset(50, yPosition);
            String text = e.getPayer() + " paid " + e.getAmount() + " RON for " + e.getDescription() +
                          " (shared with " + e.getParticipants().size() + " members)";
            content.showText(text);
            content.endText();
            yPosition -= 20;
        }

        yPosition -= 20;
        content.setFont(PDType1Font.HELVETICA_BOLD, 12);
        content.beginText();
        content.newLineAtOffset(50, yPosition);
        content.showText("Stats:");
        content.endText();
        yPosition -= 20;

        double sum = expenses.stream().mapToDouble(Expense::getAmount).sum();
        content.setFont(PDType1Font.HELVETICA, 12);
        content.beginText();
        content.newLineAtOffset(50, yPosition);
        content.showText("Total group expenses: " + sum + " RON");
        content.endText();
        yPosition -= 20;

        for (String user : group.getMembers()) {
            double amount = 0;
            int count = 0;
            for (Expense e : expenses) {
                if (e.getPayer().equals(user)) {
                    amount += e.getAmount();
                    count++;
                }
            }
            double avg = (count > 0) ? amount / count : 0;

            content.beginText();
            content.newLineAtOffset(60, yPosition);
            content.showText(user + " - Total: " + amount + " RON, Average: " + String.format("%.2f", avg) + " RON");
            content.endText();
            yPosition -= 20;
        }


        yPosition -= 10;
        content.setFont(PDType1Font.HELVETICA_BOLD, 12);
        content.beginText();
        content.newLineAtOffset(50, yPosition);
        content.showText("Top 3 expenses:");
        content.endText();
        yPosition -= 20;

        List<Expense> sorted = new ArrayList<>(expenses);
        sorted.sort(Comparator.comparingDouble(Expense::getAmount).reversed());
        content.setFont(PDType1Font.HELVETICA, 12);
        for (int i = 0; i < Math.min(3, sorted.size()); i++) {
            Expense e = sorted.get(i);
            content.beginText();
            content.newLineAtOffset(60, yPosition);
            content.showText(e.getPayer() + " paid " + e.getAmount() + " RON for " + e.getDescription());
            content.endText();
            yPosition -= 20;
        }

        yPosition -= 10;
        content.setFont(PDType1Font.HELVETICA_BOLD, 12);
        content.beginText();
        content.newLineAtOffset(50, yPosition);
        content.showText("Who owes whom:");
        content.endText();
        yPosition -= 20;

        List<String> results = Balance.calculatePairwiseDebts(expenses, group.getMembers());
        content.setFont(PDType1Font.HELVETICA, 12);
        for (String r : results) {
            content.beginText();
            content.newLineAtOffset(60, yPosition);
            content.showText(r);
            content.endText();
            yPosition -= 20;
        }

        content.close();
        document.save(file);
        document.close();

        return file;
    }
}
