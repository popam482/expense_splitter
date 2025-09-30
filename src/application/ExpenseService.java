package application;

import java.io.*;
import java.util.*;

public class ExpenseService {

    public static List<Expense> loadExpenses(Group currentGroup) {
        List<Expense> expenses = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("groups/" + currentGroup.getName() + ".csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String parts[] = line.split(";");
                if (parts.length < 4) continue;

                String payer = parts[0];
                String description = parts[1];
                double amount = Double.parseDouble(parts[2]);
                List<String> participants = Arrays.asList(parts[3].split(","));
                expenses.add(new Expense(payer, description, amount, participants));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    public static void writeExpense(Group currentGroup, Expense expense) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("groups/" + currentGroup.getName() + ".csv", true))) {
            bw.write(expense.getPayer() + ";" + expense.getDescription() + ";" + expense.getAmount() + ";" + String.join(",", expense.getParticipants()));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveExpenses(Group currentGroup, List<Expense> expenses) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("groups/" + currentGroup.getName() + ".csv"))) {
            for (Expense exp : expenses) {
                bw.write(exp.getPayer() + ";" + exp.getDescription() + ";" + exp.getAmount() + ";" + String.join(",", exp.getParticipants()));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
