package com.weronikdan.storage;

import com.weronikdan.model.Expense;

import java.io.IOException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {
    private static final String FILE_NAME = "expenses.csv";

    /* Method to store all registered expenses */
    public void save(List<Expense> expenses) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Expense e : expenses) {
                writer.write(e.toString());
                writer.newLine();
            }
        }
    }

    /* Method to load all expenses */
    public List<Expense> load () throws IOException {
        List<Expense> expenses = new ArrayList<>();
        File file = new File(FILE_NAME); /* Creates a jaca obj. that represents a file path */
        if (!file.exists()) return expenses; /* Return expenses if file does not exist - do not try to read from a non-existing file! */

        try(BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                expenses.add(new Expense(parts[0], parts[1], Double.parseDouble(parts[2])));
            }
        }
        return expenses;
    }


}
