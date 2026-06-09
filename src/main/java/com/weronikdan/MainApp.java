package com.weronikdan;

import com.weronikdan.model.Expense;
import com.weronikdan.service.ExpenseService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.IOException;

/*
*   Hierarchical representation:
*       Stage (window)
*          └── Scene (contents)
*                  └── VBox (layout)
*                     ├── HBox inputRow (input fields + button)
*                     └── TableView table (the data)
* */

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        ExpenseService expenseService = new ExpenseService();

        /* Create the table */

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

        /* Add all three columns to the table */
        table.getColumns().addAll(descCol, catCol, amountCol);

        /* Fill the table with the expenses from Expense service on initial load */
        table.getItems().addAll(expenseService.getExpenses());

        /* Input fields */
        TextField descField = new TextField();
        descField.setPromptText("Description");

        TextField catField = new TextField();
        catField.setPromptText("Category");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        Button addButton = new Button("Add");

        /* Event handler - called when button is clicked */
        addButton.setOnAction(e -> {
            try {

                /* user input */
                String desc = descField.getText();
                String cat = catField.getText();
                double amount = Double.parseDouble(amountField.getText().replace(",", "."));

                /* Create a new expense in ExpenseService*/
                expenseService.addExpense(desc, cat, amount);

                /* Refresh the table with new ExpenseService items*/
                table.getItems().clear();
                table.getItems().addAll(expenseService.getExpenses());

                /* Clear input fields */
                descField.clear();
                catField.clear();
                amountField.clear();

            } catch (NumberFormatException ex) {
                System.out.println("Invalid amount");
            } catch (IOException ex) {
                System.out.println("Error saving: " + ex.getMessage());
            }
        });

        /* Layout */

        /* Hbox is a horizontal box; places elements next to eachother. 10 is the spacing in px between items. */
        HBox inputRow = new HBox(10, descField, catField, amountField, addButton);

        /* Vbox is a vertical box; stacks elements on top of eachother */
        VBox root = new VBox(10, inputRow, table);

        /* Window setup */
        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Expense Tracker");
        stage.setScene(scene); /* put the scene inside the window */
        stage.show(); /* Actually show the window */
    }

    public static void main(String[] args) {
        launch(args);
    }
}
