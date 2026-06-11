package com.weronikdan.model;

import java.time.LocalDate;

public class Expense {

    /* Fields */
    private String description;
    private String category;
    private double amount;
    private LocalDate date;

    /* Getters */
    public String getDescription(){
        return description;
    }

    public String getCategory(){
        return category;
    }

    public double getAmount(){
        return amount;
    }

    public LocalDate getDate() {return date; }

    /* Constructor */
    public Expense(String description, String category, double amount, LocalDate date) {
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    @Override
    public String toString() {
        return description + "," + category + "," + amount + "," + date.toString();
    }


}