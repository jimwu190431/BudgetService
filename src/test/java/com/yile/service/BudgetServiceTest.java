package com.yile.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.yile.repo.entity.Budget;
import com.yile.repo.inte.IBudgetRepo;

public class BudgetServiceTest {

	private IBudgetRepo repo;
	private BudgetService service;

	@Before
	public void before() {
		repo = Mockito.mock(IBudgetRepo.class);
		service = new BudgetService(repo);

		List<Budget> budgets = new ArrayList<>();
		budgets.add(createBudget("202111", 30 * 1));
		budgets.add(createBudget("202112", 31 * 10));
		budgets.add(createBudget("202201", 31 * 100));
		budgets.add(createBudget("202202", 28 * 1000));
		budgets.add(createBudget("202203", 31 * 10000));
		Mockito.when(repo.getAll()).thenReturn(budgets);
	}

	private Budget createBudget(String yearMonth, int amount) {
		Budget budget = new Budget();
		budget.setYearMonth(yearMonth);
		budget.setAmount(amount);
		return budget;
	}

	@Test
	public void test_day_error() {
		double result = service.query(LocalDate.of(2022, 01, 31), LocalDate.of(2022, 01, 01));
		Assert.assertEquals(0.0, result, 0.00);
	}

	@Test
	public void test_same_month_same_day() {
		double result = service.query(LocalDate.of(2022, 01, 01), LocalDate.of(2022, 01, 01));
		Assert.assertEquals(1 * 100, result, 0.00);
	}

	@Test
	public void test_same_month_diff_day() {
		double result = service.query(LocalDate.of(2022, 02, 01), LocalDate.of(2022, 02, 02));
		Assert.assertEquals(2 * 1000, result, 0.00);
	}

	@Test
	public void test_all_month() {
		double result = service.query(LocalDate.of(2022, 03, 01), LocalDate.of(2022, 03, 31));
		Assert.assertEquals(31 * 10000, result, 0.00);
	}
}