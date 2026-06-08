package com.weronikdan;

import com.weronikdan.service.ExpenseService;


public class Main {
    public static void main(String[] args) {
        ExpenseService expenseService = new ExpenseService();

        expenseService.addExpense("Coffee", "Food", 3.5);
        expenseService.addExpense("Tea", "Food", 2.5);
        expenseService.addExpense("Train ticket", "Transport", 24.0);
        expenseService.addExpense("Gas", "Transport", 53.0);

        expenseService.printAll();

    }
}
