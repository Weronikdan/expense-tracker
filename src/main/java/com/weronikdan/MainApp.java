package com.weronikdan;

import com.weronikdan.model.Expense;
import com.weronikdan.service.ExpenseService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.HBox;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

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
    private PieChart chart;
    private ComboBox<String> categoryComboBox;
    private ComboBox<String> filterComboBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        expenseService = new ExpenseService();
        table = buildTable();
        chart = buildPieChart();
        categoryComboBox = new ComboBox<>();
        filterComboBox = new ComboBox<>();

        /* HBox is a horizontal box; places elements next to each other. 10 is the spacing in px between items. */
        HBox inputRow = buildInputRow();
        HBox filterRow = buildFilterRow();
        Button deleteButton = buildDeleteButton();

        /* Vbox is a vertical box; stacks elements on top of each other */
        VBox root = new VBox(10, chart, filterRow, table, inputRow, deleteButton);

        /* Window setup */

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true); /* Stretch to window width*/

        Scene scene = new Scene(scrollPane, 800, 600);
        stage.setTitle("Expense Tracker");
        stage.setScene(scene); /* put the scene inside the window */
        stage.show(); /* Actually show the window */
    }

    /* builders */

    private TableView<Expense> buildTable() {
        /* Create an empty table to hold Expense objects; empty grid */
        TableView<Expense> table = new TableView<>();

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
                String.format("£%.2f", e.getValue().getAmount())));

        TableColumn<Expense, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(e -> new SimpleStringProperty(
                e.getValue().getDate().toString()));

        /* Add all three columns to the table */
        table.getColumns().addAll(descCol, catCol, amountCol, dateCol);

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

        /* HBox is a horizontal box; places elements next to each other. 10 is the spacing in px between items. */
        return new HBox(10, descField, categoryComboBox, amountField, datePicker, addButton);
    }


    private HBox buildFilterRow() {

        filterComboBox.getItems().addAll(expenseService.getCategories());
        filterComboBox.setEditable(false);
        filterComboBox.setPromptText("Category");

        filterComboBox.setOnAction(e -> {
            /* user selection */
            String filter = filterComboBox.getValue();
            if (filter == null) return;

            activeFilter[0] = filter;
            refreshTable();
        });

        /* CLEAR FILTER  */
        Button clearButton = buildClearButton();

        return new HBox(10, filterComboBox, clearButton);
    }


    private Button buildDeleteButton() {
        Button deleteButton = new Button("Delete");

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
        chart.setTitle("Spending by Category");

        expenseService.getSummaryByCategory()
                .forEach((category, total) -> {
                    chart.getData().add(new PieChart.Data(category, total));
                });
        return chart;
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
        chart.getData().clear();
        expenseService.getSummaryByCategory()
                .forEach((category, total) ->
                        chart.getData().add(new PieChart.Data(category, total)));
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
