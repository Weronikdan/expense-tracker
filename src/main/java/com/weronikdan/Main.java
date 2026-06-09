package com.weronikdan;

import com.weronikdan.service.ExpenseService;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    static void main(String[] args) throws Exception {

        ExpenseService expenseService = new ExpenseService();
        Scanner scanner = new Scanner (System.in);

        while (true) {
            System.out.println();
            System.out.println();
            System.out.println("------------ Menu ------------");
            System.out.println("1. Add expense");
            System.out.println("2. View all");
            System.out.println("3. View summary by category");
            System.out.println("4. Delete expense");
            System.out.println("5. View by Category");
            System.out.println("6. Quit");
            System.out.print("Choice: ");
            String choice = scanner.nextLine();
            System.out.println();

            switch (choice) {
                case "1":
                    System.out.print("Enter an expense description: ");
                    String description = scanner.nextLine();
                    System.out.print("Enter a category: ");
                    String category = scanner.nextLine();
                    System.out.print("Enter the amount: ");
                    try {
                        double amount = Double.parseDouble(scanner.nextLine().replace(",", "."));
                        expenseService.addExpense(description, category, amount);
                    } catch (NumberFormatException e){
                        System.out.println("Invalid amount, please enter a number.");
                    } catch (IOException e){
                        System.out.println("Error saving expense: " + e.getMessage());
                    }
                    break;
                case "2":
                    expenseService.printAll();
                    break;
                case "3":
                    expenseService.printSummary();
                    break;
                case "4":
                    expenseService.printAll();
                    System.out.print("Enter the number to delete: ");
                    try {
                        int toDelete = Integer.parseInt(scanner.nextLine());
                        expenseService.deleteExpense(toDelete);
                    } catch (NumberFormatException e){
                        System.out.println("Invalid value. Please try again. ");
                    }
                    break;
                case "5":
                    System.out.print("Enter category: ");
                    try {
                        String userInput = scanner.nextLine();
                        expenseService.printByCategory(userInput);
                    } catch (NumberFormatException e){
                        System.out.println("Invalid value. Please try again. ");
                    }
                    break;
                case "6":
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Please enter one of the options 1-5.");
            }
        }

    }
}
