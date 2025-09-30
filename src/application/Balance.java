package application;

import java.util.ArrayList;
import java.util.List;

public class Balance {

    public static List<String> calculatePairwiseDebts(List<Expense> expenses, List<String> members) {
        int n = members.size();
        double[][] debt = new double[n][n];

        for (Expense exp : expenses) {
            String payer = exp.getPayer();
            List<String> sharers = new ArrayList<>(exp.getParticipants() == null ? List.of() : exp.getParticipants());

            if (!sharers.contains(payer)) {
                sharers.add(payer);
            }

            int shareCount = sharers.size();
            if (shareCount == 0) continue;
            double share = exp.getAmount() / shareCount;

            int payerIndex = members.indexOf(payer);
            if (payerIndex < 0) continue; 

            for (String s : sharers) {
                if (s.equals(payer)) continue; 
                int i = members.indexOf(s);
                int j = payerIndex;
                if (i < 0) continue; 
                debt[i][j] += share;
            }
        }

        List<String> results = new ArrayList<>();
        double eps = 0.005; 
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double net = debt[i][j] - debt[j][i];
                if (Math.abs(net) > eps) {
                    if (net > 0) {
                        results.add(format(members.get(i)) + " owes " + format(net) + " to " + format(members.get(j)));
                    } else {
                        results.add(format(members.get(j)) + " owes " + format(-net) + " to " + format(members.get(i)));
                    }
                }
            }
        }

        if (results.isEmpty()) {
            results.add("All settled");
        }
        return results;
    }

    private static String format(double x) {
        return String.format("%.2f", x);
    }

    private static String format(String s) {
        return s;
    }
}
