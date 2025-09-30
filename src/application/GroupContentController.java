package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class GroupContentController {

    Group currentGroup;
    User currentUser;

    @FXML Label groupName;
    @FXML Label membersOfTheGroup;
    @FXML Button backToGroups, expenseButton, balancesButton, statisticsButton, exportButton;
    @FXML TextField searchField;
    @FXML ComboBox<String> userFilter;
    @FXML ListView<String> expenseListView;

    private List<Expense> expensesList;
    private StatisticsService statisticsService = new StatisticsService();
    private ChartService chartService = new ChartService();
    private PDFExporter pdfExporter = new PDFExporter();

    private ContextMenu menu;
    
    protected void initializeGroup(Group group, User user) {
        currentGroup = group;
        currentUser = user;

        groupName.setText(group.getName());
        expensesList = ExpenseService.loadExpenses(currentGroup);

        displayMembers();
        refreshExpenseList();

        userFilter.getItems().addAll("All users");
        userFilter.getItems().addAll(currentGroup.getMembers());
        userFilter.setValue("All users");

        searchField.textProperty().addListener((obs, oldVal, newVal) -> 
        filterExpenses(newVal, userFilter.getValue())
    );
    userFilter.valueProperty().addListener((obs, oldVal, newVal) -> 
        filterExpenses(searchField.getText(), newVal)
    );

        expenseListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) showExpenseDetails();
        });
     
         menu = new ContextMenu();

            MenuItem deleteItem = new MenuItem("Delete expense");
            deleteItem.setOnAction(e -> deleteExpense());

            MenuItem detailsItem = new MenuItem("Show expense details");
            detailsItem.setOnAction(e -> handleShowData());

            MenuItem balancesItem = new MenuItem("Show balances");
            balancesItem.setOnAction(e -> handleShowBalances());

            MenuItem chartItem = new MenuItem("Expenses chart");
            chartItem.setOnAction(e -> handleShowChart());

            MenuItem statsItem = new MenuItem("Statistics");
            statsItem.setOnAction(e -> handleShowStatistics());

            MenuItem exportItem = new MenuItem("Export to PDF");
            exportItem.setOnAction(e -> handleExportPDF());

            menu.getItems().addAll(deleteItem, detailsItem, balancesItem, chartItem, statsItem, exportItem);
            expenseListView.setContextMenu(menu);

        
    }

    
    @FXML
    private void handleShowData() {
        int index = expenseListView.getSelectionModel().getSelectedIndex();
        if (index < 0) return;

        Expense exp = expensesList.get(index);
        String details = statisticsService.getExpenseDetails(exp);

        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Expense details");
        info.setHeaderText("Details for " + exp.getDescription());
        info.setContentText(details);
        info.showAndWait();
    }

    @FXML
    private void handleShowBalances() {
        List<String> results = statisticsService.calculateBalances(expensesList, currentGroup.getMembers());

        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Final balances");
        info.setHeaderText("Who owes whom:");
        info.setContentText(String.join("\n", results));
        info.showAndWait();
    }

    @FXML
    private void handleShowStatistics() {
        String stats = statisticsService.getStatistics(expensesList, currentGroup.getMembers());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Statistics");
        alert.setHeaderText("📊 Group Expense Statistics");
        alert.setContentText(stats);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    @FXML
    private void handleShowChart() {
        ObservableList<PieChart.Data> data = chartService.getPieChartData(currentGroup, expensesList);

        PieChart chart = new PieChart(data);
        chart.setTitle("Expenses chart per members");

        Stage chartStage = new Stage();
        chartStage.setTitle("Expenses Chart");
        chartStage.setScene(new Scene(chart, 500, 400));
        chartStage.show();
    }

    @FXML
    private void handleExportPDF() {
        try {
            File pdfFile = pdfExporter.exportExpensesToPDF(currentGroup, expensesList);

            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("PDF export");
            info.setHeaderText("PDF saved successfully");
            info.setContentText("File saved at: " + pdfFile.getAbsolutePath());
            info.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("PDF export");
            alert.setHeaderText("Error while saving PDF");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    
    @FXML
    private void addExpense() {

        ChoiceDialog<String> payerDialog = new ChoiceDialog<>(currentUser.getUsername(), currentGroup.getMembers());
        payerDialog.setTitle("Add Expense");
        payerDialog.setHeaderText("Select the payer");
        payerDialog.setContentText("Payer:");
        String payer = payerDialog.showAndWait().orElse(null);
        if (payer == null) return;


        TextInputDialog descDialog = new TextInputDialog();
        descDialog.setTitle("Add Expense");
        descDialog.setHeaderText("Enter description of the expense");
        descDialog.setContentText("Description:");
        String description = descDialog.showAndWait().orElse(null);
        if (description == null || description.isEmpty()) return;


        TextInputDialog amountDialog = new TextInputDialog();
        amountDialog.setTitle("Add Expense");
        amountDialog.setHeaderText("Enter amount of the expense");
        amountDialog.setContentText("Amount:");
        double amount;
        try {
            String amountStr = amountDialog.showAndWait().orElse(null);
            if (amountStr == null) return;
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showAlert("Invalid input", "Amount must be a positive number");
            return;
        }

        List<String> members = currentGroup.getMembers();
        List<String> participants = FXCollections.observableArrayList(members);
        List<String> selectedParticipants = new ArrayList<>();
        ChoiceDialog<String> participantDialog = new ChoiceDialog<>(members.get(0), members);
        participantDialog.setTitle("Add Expense");
        participantDialog.setHeaderText("Select participants (multiple times to add all)");
        participantDialog.setContentText("Participant:");
        while (true) {
            String participant = participantDialog.showAndWait().orElse(null);
            if (participant == null) break;
            if (!selectedParticipants.contains(participant)) {
                selectedParticipants.add(participant);
            }
            if (selectedParticipants.size() == members.size()) break;
        }

        Expense newExpense = new Expense(payer, description, amount, selectedParticipants);
        expensesList.add(newExpense);
        ExpenseService.writeExpense(currentGroup, newExpense);

        refreshExpenseList();
    }

    
    
    private void refreshExpenseList() {
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Expense e : expensesList) {
            items.add(e.getPayer() + " paid " + e.getAmount() + " for " + e.getDescription() +
                      " (shared with " + e.getParticipants().size() + " members)");
        }
        expenseListView.setItems(items);
    }

    private void displayMembers() {
        List<String> members = currentGroup.getMembers();
        String text = (members.size() <= 3) ? String.join(", ", members)
                     : String.join(", ", members.subList(0, 3)) + " and " + (members.size() - 3) + " more";
        membersOfTheGroup.setText("Members: " + text);

        membersOfTheGroup.setOnMouseClicked(e -> showAlert("All members", String.join("\n", members)));
    }

    @FXML
    private void searchDescription() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterExpenses(newValue, userFilter.getValue());
        });
    }

    @FXML
    private void filterByUser() {
        userFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            filterExpenses(searchField.getText(), newVal);
        });
    }

    private void filterExpenses(String searchText, String user) {
        if (searchText == null) searchText = "";
        if (user == null) user = "All users";

        expenseListView.getItems().clear();

        for (Expense e : expensesList) {
            boolean matchesSearch = e.getDescription().toLowerCase().contains(searchText.toLowerCase());
            boolean matchesUser = user.equals("All users") || e.getPayer().equals(user);
            if (matchesSearch && matchesUser) {
                expenseListView.getItems().add(
                    e.getPayer() + " paid " + e.getAmount() + " for " + e.getDescription() +
                    " (shared with " + e.getParticipants().size() + " members)"
                );
            }
        }
    }
    
    @FXML
    private void backToGroups() {
        try {
            Stage stage = Main.getPrimaryStage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GroupMenu.fxml"));
            Parent root = loader.load();
            GroupMenuController controller = loader.getController();
            controller.initialize(currentUser);

            Scene scene = new Scene(root); 
            scene.getStylesheets().add(getClass().getResource("menu.css").toExternalForm()); // atașezi CSS-ul aici

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void showExpenseDetails() {
        int index = expenseListView.getSelectionModel().getSelectedIndex();
        if (index < 0) return;
        Expense e = expensesList.get(index);
        showAlert("Expense details", statisticsService.getExpenseDetails(e));
    }

    @FXML
    private void showBalances() {
        List<String> results = statisticsService.calculateBalances(expensesList, currentGroup.getMembers());
        showAlert("Final balances", String.join("\n", results));
    }

    @FXML
    private void showStatistics() {
        String stats = statisticsService.getStatistics(expensesList, currentGroup.getMembers());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Statistics");
        alert.setHeaderText("📊 Group Expense Statistics");
        alert.setContentText(stats);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
        

    }

    @FXML
    private void showExpensesChart() {
        ObservableList<PieChart.Data> data = chartService.getPieChartData(currentGroup, expensesList);
        PieChart chart = new PieChart(data);
        chart.setTitle("Expenses chart per members");

        Stage chartStage = new Stage();
        chartStage.setTitle("Expenses Chart");
        chartStage.setScene(new Scene(chart, 500, 400));
        chartStage.show();
    }

    @FXML
    private void exportExpensesToPDF() {
        try {
            File pdfFile = pdfExporter.exportExpensesToPDF(currentGroup, expensesList);
            showAlert("PDF export", "File saved at: " + pdfFile.getAbsolutePath());
        } catch (IOException e) {
            showAlert("PDF export error", e.getMessage());
        }
    }

    @FXML
    private void deleteExpense() {
        int index = expenseListView.getSelectionModel().getSelectedIndex();
        if (index < 0) return;

        Expense e = expensesList.get(index);
        if (!e.getPayer().equals(currentUser.getUsername())) {
            showAlert("Delete expense", "You are not the payer!");
            return;
        }

        expensesList.remove(index);
        ExpenseService.saveExpenses(currentGroup, expensesList);
        refreshExpenseList();
        showAlert("Delete expense", "Expense deleted successfully");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

    }
}
