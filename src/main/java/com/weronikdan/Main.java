package com.weronikdan;

import com.weronikdan.service.ExpenseService;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    static void main(String[] args) throws Exception {

        ExpenseService expenseService = new ExpenseService();
        Scanner scanner = new Scanner (System.in);

        while (true) {
            System.out.println("1. Add expense");
            System.out.println("2. View all");
            System.out.println("3. View summary by category");
            System.out.println("4. Quit");
            System.out.print("Choice: ");
            String choice = scanner.nextLine();

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
                    System.exit(0);

                default:
                    System.out.println("Invalid option. Please enter one of the options 1-3.");
            }
        }

    }
}
