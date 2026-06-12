# Expense Tracker

A personal finance desktop application built with Java and JavaFX, featuring expense tracking, category management, and data visualisation.
 
![Java](https://img.shields.io/badge/Java-21-blue) ![JavaFX](https://img.shields.io/badge/JavaFX-21-blue) ![Maven](https://img.shields.io/badge/Maven-3.x-blue)


 
## Features
 
- **Add, view and delete expenses** with description, category, amount and date
- **Filter expenses by category** using a dynamic dropdown
- **Persistent storage** via CSV file
- **Pie chart** showing spending breakdown by category
- **Bar chart** showing monthly spending trends
- **Dynamic category dropdown** populated from existing expense data
- **Input validation** 
- **Clean, minimal UI** built with JavaFX and custom CSS


<img width="959" height="666" alt="Screenshot 2026-06-12 at 23 14 43" src="https://github.com/user-attachments/assets/c79f1091-8878-4e31-a448-d065ef7f7fe6" />

 
## Getting Started
 
### Prerequisites
 
- Java 21 or higher
- Maven 3.x
### Run the app
 
```bash
git clone https://github.com/weronikdan/expense-tracker.git
cd expense-tracker
mvn javafx:run
```
 

 
## Data Storage
 
Expenses are stored locally in `expenses.csv` in the project root directory. This file is excluded from version control to protect personal financial data.
 
The CSV format is:
```
description,category,amount,date
Coffee,Food,35.00,2026-06-11
```
 

