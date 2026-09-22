package com.maurodelcore.expensetracker.service;

import com.maurodelcore.expensetracker.model.Expense;
import com.maurodelcore.expensetracker.utils.ExpenseDataLoader;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseServiceImplementation implements ExpenseService{

    private final ExpenseDataLoader expenseDataLoader;

    public ExpenseServiceImplementation(ExpenseDataLoader expenseDataLoader) {this.expenseDataLoader = expenseDataLoader;}

    @Override
    public List<Expense> getExpensesByDay(String date) {
        return ExpenseDataLoader.getExpenses().stream() // take the expenses
                .filter(expense -> expense.getDate().equals(date))// filter by date for each expenses get date if equals date
                .toList(); //put the filtered expenses into the list
    }

    @Override
    public List<Expense> getExpensesByCategoryAndMonth(String category, String month) {
        return ExpenseDataLoader.getExpenses().stream()
                .filter(expense -> expense.getCategory().equalsIgnoreCase(category) &&
                        expense.getDate().startsWith(month))
                .toList();
    }

    @Override
    public List<String> getAllExpensesCategories() {
        return ExpenseDataLoader
                .getExpenses() //Get all the expense
                .stream()//convert to a stream
                .map(Expense::getCategory) // transform the element in a stream and get all categories
                .distinct()//remove duplicate
                .toList(); // take all the stream and it transform back to a list
    }

    @Override
    public Optional<Expense> getExpenseById(Long id) {
        return ExpenseDataLoader.getExpenses()
                .stream()
                .filter(expense -> expense.getId().equals(id))
                .findFirst();
    }

    @Override
    public Expense addExpense(Expense expense) {
        expense.setId(ExpenseDataLoader.getNextId());
        ExpenseDataLoader.getExpenses().add(expense);
        return expense;
    }

    @Override
    public boolean deleteExpenseById(Long id) {
        Optional<Expense> expense = getExpenseById(id);
        if (expense.isPresent()) {
            ExpenseDataLoader.getExpenses().remove(expense.get());
            return true;
        }
        return false;
    }

    @Override
    public boolean updateExpense(Expense updatedExpense) { // the user will query an expense
        Optional<Expense> existingExpense = getExpenseById(updatedExpense.getId()); // a variable existingExpense will be set using that user query and using the getExpenseById method
        if (existingExpense.isPresent()) { //if the expense exists
            ExpenseDataLoader.getExpenses().remove(existingExpense.get()); // the existing expense will be removed from the list
            ExpenseDataLoader.getExpenses().add(updatedExpense); // the updated expense will be added to the list
            return true;
        }
        return false;
    }
}
