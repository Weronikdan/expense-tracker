package com.weronikdan;

import com.weronikdan.model.Expense;
import com.weronikdan.service.ExpenseService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.HBox;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/*
*   Hierarchy:
*       Stage (window)
*          └── Scene (contents)
*                  └── VBox (layout)
*                     ├── HBox inputRow (input fields + button)
*                     └── TableView table (the data)
* */

public class MainApp extends Application {

    private ExpenseService expenseService;
    private TableView<Expense> table;
    private final String[] activeFilter = {""};
    private PieChart pieChart;
    private BarChart<String, Number> barChart;
    private ComboBox<String> categoryComboBox;
    private ComboBox<String> filterComboBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        expenseService = new ExpenseService();
        table = buildTable();

        categoryComboBox = new ComboBox<>();
        filterComboBox = new ComboBox<>();

        HBox header = buildHeader();
        HBox inputRow = buildInputRow();
        HBox filterRow = buildFilterRow();
        HBox chartRow = buildChartRow();
        filterRow.getStyleClass().add("card");
        filterRow.setPadding(new Insets(8, 0, 8, 0));
        inputRow.getStyleClass().add("card");

        /* Vbox is a vertical box; stacks elements on top of each other */
        VBox root = new VBox(16, header, chartRow, filterRow, table, inputRow);

        root.setPadding(new Insets(20));
        root.setMaxWidth(900);
        root.setStyle("-fx-alignment: center;");
        root.setAlignment(javafx.geometry.Pos.TOP_CENTER);

        VBox wrapper = new VBox(root);
        wrapper.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        wrapper.setStyle("-fx-background-color: #f9f9f9;");

        /* Window setup */

        ScrollPane scrollPane = new ScrollPane(wrapper);
        scrollPane.setFitToWidth(true); /* Stretch to window width*/
        //scrollPane.setFitToHeight(true);

        Scene scene = new Scene(scrollPane, 1000, 800);
        stage.setTitle("Expense Tracker");
        stage.setScene(scene); /* put the scene inside the window */
        stage.show(); /* Actually show the window */

        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    }

    /* builders */

    private HBox buildHeader() {
        Label title = new Label("Expense Tracker");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label user = new Label("Weronika");
        Label date = new Label(LocalDate.now().getMonth() + " " + LocalDate.now().getYear());

        VBox userInfo = new VBox(2, user, date);
        userInfo.setAlignment(Pos.CENTER_RIGHT);

        HBox header = new HBox(10, title, spacer, userInfo);
        header.getStyleClass().add("header");
        return header;
    }

    private HBox buildChartRow() {
        pieChart = buildPieChart();
        barChart = buildBarChart();

        return new HBox(10, pieChart, barChart);
    }

    private TableView<Expense> buildTable() {
        /* Create an empty table to hold Expense objects; empty grid */
        TableView<Expense> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        /* Create a table column with title description; <holding expense objects, display string>*/
        TableColumn<Expense, String> descCol = new TableColumn<>("Description");

        /* Specify how to get its value from an expense object:
         *  e = one row (a cell data object)
         *  e.getValue() = gets the actual Expense object for that row
         * .getDescription() = gets the description from that expense
         * new SimpleStringProperty(...) = wraps it in a JavaFX property object that the table understands
         * */
        descCol.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getDescription()));

        TableColumn<Expense, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getCategory()));

        TableColumn<Expense, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(e -> new SimpleStringProperty(
                String.format("%.2f kr", e.getValue().getAmount())));

        TableColumn<Expense, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(e -> new SimpleStringProperty(
                e.getValue().getDate().toString()));

        /* Add all three columns to the table */
        table.getColumns().addAll(dateCol, descCol, catCol, amountCol);

        /* Fill the table with the expenses from Expense service on initial load */
        table.getItems().addAll(expenseService.getExpenses());

        return table;
    }

    private HBox buildInputRow() {
        /* Input fields */
        TextField descField = new TextField();
        descField.setPromptText("Description");

        categoryComboBox.getItems().addAll(expenseService.getCategories());
        categoryComboBox.setEditable(true);
        categoryComboBox.setPromptText("Category");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        DatePicker datePicker = new DatePicker();


        Button addButton = new Button("Add");
        datePicker.setValue(LocalDate.now()); /* Today is default value */
        datePicker.setShowWeekNumbers(false);
        datePicker.setPromptText("Date");

        /* Event handler - called when add button is clicked */
        addButton.setOnAction(e -> {
            try {

                /* user input */
                String desc = descField.getText();
                String cat = categoryComboBox.getValue();
                double amount = Double.parseDouble(amountField.getText().replace(",", "."));
                LocalDate date = datePicker.getValue();

                if (desc.isEmpty() || cat.isEmpty()) {
                    showAlert("Invalid input", "Please fill in all fields.");
                    return;
                }

                /* Create a new expense in ExpenseService*/
                expenseService.addExpense(desc, cat, amount, date);

                /* Refresh the table with new ExpenseService items*/
                refreshTable();

                /* Refresh the pie chart with new expense */
                refreshPieChart();
                refreshBarChart();
                refreshCategoryList();

                /* Clear input fields */
                descField.clear();
                amountField.clear();

            } catch (NumberFormatException ex) {
                showAlert("Invalid input", ex.getMessage());
            } catch (IOException ex) {
                showAlert("Error", "Error saving: " + ex.getMessage());
            }
        });

        HBox.setHgrow(descField, Priority.ALWAYS);
        HBox.setHgrow(amountField, Priority.ALWAYS);
        HBox.setHgrow(categoryComboBox, Priority.ALWAYS);

        /* HBox is a horizontal box; places elements next to each other. 10 is the spacing in px between items. */
        return new HBox(10, descField, categoryComboBox, amountField, datePicker, addButton);
    }


    private HBox buildFilterRow() {

        filterComboBox.getItems().addAll(expenseService.getCategories());
        filterComboBox.setEditable(false);
        filterComboBox.setPrefWidth(300);
        filterComboBox.setPromptText("Filter by Category");

        filterComboBox.setOnAction(e -> {
            /* user selection */
            String filter = filterComboBox.getValue();
            if (filter == null) return;

            activeFilter[0] = filter;
            refreshTable();
        });

        Button clearButton = buildClearButton();
        Button deleteButton = buildDeleteButton();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        return new HBox(10, filterComboBox, clearButton, spacer, deleteButton);
    }


    private Button buildDeleteButton() {
        Button deleteButton = new Button("Delete");
        deleteButton.setDisable(true);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            deleteButton.setDisable(newVal == null);
        });

        /* Event handler - called when delete button is clicked */
        deleteButton.setOnAction(e -> {
            try {
                Expense selected = table.getSelectionModel().getSelectedItem();

                if (selected == null) {
                    showAlert("Error", "No expense selected. Select an expense to delete. ");
                    return;
                }

                /* Delete an expense in ExpenseService*/
                expenseService.deleteExpense(selected);

                refreshTable();
                refreshPieChart();
                refreshCategoryList();

            } catch (IOException ex) {
                showAlert("Error", "Error deleting: " + ex.getMessage());

            }
        });
        return deleteButton;
    }

    private Button buildClearButton() {
        Button clearButton = new Button("Clear");

        /* Event handler - called when clear button is clicked */
        clearButton.setOnAction(e -> {
            activeFilter[0] = "";
            refreshTable();
            refreshFilterList();
        });

        return clearButton;
    }

    private PieChart buildPieChart() {
        PieChart chart = new PieChart();
        chart.setPrefHeight(400);
        chart.setTitle("Spending by Category");

        expenseService.getSummaryByCategory()
                .forEach((category, total) -> {
                    chart.getData().add(new PieChart.Data(category, total));
                });
        return chart;
    }

    private BarChart<String, Number> buildBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Month");
        yAxis.setLabel("Amount (SEK)");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Monthly Spending");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        barChart.setPrefHeight(400);
        series.setName("Spending");

        expenseService.getMonthlyTotals()
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> series.getData().add(
                        new XYChart.Data<>(entry.getKey(), entry.getValue())));

        barChart.getData().add(series);
        return barChart;
    }


    /* Helpers */
    private void refreshTable() {
        table.getItems().clear();
        if (activeFilter[0].isEmpty()) {
            table.getItems().addAll(expenseService.getExpenses());
        } else {
            table.getItems().addAll(expenseService.getExpensesByCategory(activeFilter[0]));
        }
    }

    private void refreshPieChart() {
        pieChart.getData().clear();
        expenseService.getSummaryByCategory()
                .forEach((category, total) ->
                        pieChart.getData().add(new PieChart.Data(category, total)));
    }

    private void refreshBarChart() {
        barChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Spending");

        expenseService.getMonthlyTotals()
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> series.getData().add(
                        new XYChart.Data<>(entry.getKey(), entry.getValue())));

        barChart.getData().add(series);
    }

    private void refreshCategoryList() {
        categoryComboBox.getItems().clear();
        categoryComboBox.getItems().addAll(expenseService.getCategories());
        categoryComboBox.getEditor().clear();
        categoryComboBox.setValue(null);

        refreshFilterList();
    }

    private void refreshFilterList() {
        filterComboBox.getItems().clear();
        filterComboBox.getItems().addAll(expenseService.getCategories());

        if (!activeFilter[0].isEmpty()) {
            filterComboBox.setValue(activeFilter[0]);
        } else {
            filterComboBox.setValue(null);
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
