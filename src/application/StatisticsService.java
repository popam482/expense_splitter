package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StatisticsService {


    public String getExpenseDetails(Expense exp) {
        StringBuilder sb = new StringBuilder();
        sb.append("Payer: ").append(exp.getPayer()).append("\n");
        sb.append("Amount: ").append(exp.getAmount()).append("\n");
        sb.append("Participants:\n");

        for (String member : exp.getParticipants()) {
            if (!member.equals(exp.getPayer())) {
                sb.append(member)
                  .append(" owes ")
                  .append(exp.getAmount() / exp.getParticipants().size())
                  .append(" to ")
                  .append(exp.getPayer())
                  .append("\n");
            }
        }
        return sb.toString();
    }


    public List<String> calculateBalances(List<Expense> expenses, List<String> members) {
        return Balance.calculatePairwiseDebts(expenses, members);
    }

    public String getStatistics(List<Expense> expenses, List<String> members) {
        double sum = 0;
        for (Expense e : expenses)
            sum += e.getAmount();

        StringBuilder stats = new StringBuilder();
        stats.append("Total group expenses: ").append(sum).append(" RON\n");

        for (String user : members) {
            double amount = 0;
            int count = 0;
            for (Expense e : expenses) {
                if (e.getPayer().equals(user)) {
                    amount += e.getAmount();
                    count++;
                }
            }
            double avg = (count > 0) ? amount / count : 0;
            stats.append("👤 ").append(user)
                 .append(" - Total: ").append(amount)
                 .append(" RON, Average: ").append(String.format("%.2f", avg)).append(" RON\n");
        }

        List<Expense> sorted = new ArrayList<>(expenses);
        sorted.sort(Comparator.comparingDouble(Expense::getAmount).reversed());

        stats.append("\n🏆 Top 3 expenses:\n");
        for (int i = 0; i < Math.min(3, sorted.size()); i++) {
            Expense e = sorted.get(i);
            stats.append("👤 ").append(e.getPayer())
                 .append(" paid ").append(e.getAmount())
                 .append(" RON for ").append(e.getDescription()).append("\n");
        }

        return stats.toString();
    }
}
