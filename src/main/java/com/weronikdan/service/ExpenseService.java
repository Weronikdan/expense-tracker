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

    public void deleteExpense(int index) throws IOException {
        if (index < 1 || index > expenses.size()) {
            System.out.println("Invalid expense number.");
            return;
        }
        this.expenses.remove(index - 1);
        storage.save(this.expenses);
    }

    public void deleteExpense(Expense expense) throws IOException {
        expenses.remove(expense);
        storage.save(expenses);
    }

    public List<Expense> getExpenses() {
        return expenses;
    }


    public void printAll() {
        System.out.println("------------------ All Expenses ------------------");
        for(int i = 0; i<expenses.size(); i++) {
            /* Ex. %-20s left aligned string, padded to 20 char */
            System.out.printf("%d. %-20s %-15s £%.2f%n", i + 1, expenses.get(i).getDescription(), expenses.get(i).getCategory(), expenses.get(i).getAmount());
        }
    }

    public void printSummary() {
        System.out.println("------ Summary by Category ------");

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

    public void printByCategory(String category) {
        List<Expense> filtered = expenses.stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category))
                .toList();

        if (filtered.isEmpty()) {
            System.out.println("No expenses found for category: " + category);
            return;
        }

        filtered.forEach(e -> System.out.printf("%d. %-20s %-15s £%.2f%n",
                expenses.indexOf(e) + 1, e.getDescription(), e.getCategory(), e.getAmount()));
    }

    public List<Expense> getExpensesByCategory(String category) {
        return expenses.stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category))
                .toList();
    }


}
