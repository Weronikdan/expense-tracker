package com.weronikdan.service;

import com.weronikdan.model.Expense;
import com.weronikdan.storage.FileStorage;

import java.util.List;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

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

    public void printSummary() {
        Map<String, Double> categorySummary = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));

        categorySummary.forEach((category, total) -> {
            System.out.printf("%-15s £%.2f%n", category, total);
        });

        double total = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
        System.out.printf("%-15s £%.2f%n", "TOTAL", total);
    }



}
