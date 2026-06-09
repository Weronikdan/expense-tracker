package com.weronikdan.model;

public class Expense {

    /* Fields */
    private String description;
    private String category;
    private double amount;

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

    /* Constructor */
    public Expense(String description, String category, double amount) {
        this.description = description;
        this.category = category;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return description + "," + category + "," + amount;
    }


}