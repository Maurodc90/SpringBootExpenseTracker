package com.maurodelcore.expensetracker;

import com.maurodelcore.expensetracker.model.Expense;
import com.maurodelcore.expensetracker.utils.ExpenseDataLoader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;


@SpringBootApplication
public class ExpensetrackerApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ExpensetrackerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<Expense> expenseList = ExpenseDataLoader.getExpenses();


	}
}
