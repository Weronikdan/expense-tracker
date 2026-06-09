package com.weronikdan.service;

import com.weronikdan.model.Expense;
import com.weronikdan.storage.FileStorage;

import java.util.List;
import java.io.IOException;

public class ExpenseService {

    /* Fields */
    private FileStorage storage = new FileStorage();
    private List<Expense> expenses;

    /* Constructor */
    public ExpenseService() throws IOException {
        this.expenses = storage.load();
    }

    public void addExpense(String description, String category, double amount) throws IOException {
        this.expenses.add(new Expense(description, category, amount));
        storage.save(this.expenses);
    }

    public void printAll() {
        for(Expense expense : expenses ) {
            /* Ex. %-20s left aligned string, padded to 20 char */
            System.out.printf("%-20s %-15s £%.2f%n", expense.getDescription(), expense.getCategory(), expense.getAmount());
        }
    }

}
