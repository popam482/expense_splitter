package application;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

public class ChartService {

    public ObservableList<PieChart.Data> getPieChartData(Group group, List<Expense> expenses) {
        List<String> members = group.getMembers();
        double[] totals = new double[members.size()];

        for (Expense exp : expenses) {
            int index = members.indexOf(exp.getPayer());
            if (index >= 0) {
                totals[index] += exp.getAmount();
            }
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (int i = 0; i < members.size(); i++) {
            pieChartData.add(new PieChart.Data(members.get(i), totals[i]));
        }

        return pieChartData;
    }
}
