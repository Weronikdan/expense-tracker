package com.weronikdan.service;

import com.weronikdan.model.Expense;

import java.util.ArrayList;
import java.util.List;

public class ExpenseService {

    /* Fields */
    private List<Expense> expenses;

    /* Constructor */
    public ExpenseService() {
        this.expenses = new ArrayList<>();
    }

    public void addExpense(String description, String category, double amount) {
        this.expenses.add(new Expense(description, category, amount));
    }

    public void printAll() {
        for(Expense expense : expenses ) {
            /* Ex. %-20s left aligned string, padded to 20 char */
            System.out.printf("%-20s %-15s £%.2f%n", expense.getDescription(), expense.getCategory(), expense.getAmount());
        }
    }

}
