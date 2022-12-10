package com.yile.service;

import static java.util.stream.Collectors.toMap;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

import com.yile.repo.entity.Budget;
import com.yile.repo.inte.IBudgetRepo;

public class BudgetService {

	private IBudgetRepo repo;

	public BudgetService(IBudgetRepo repo) {
		this.repo = repo;
	}

	public double query(LocalDate startDate, LocalDate endDate) {
		if (startDate.isAfter(endDate)) {
			return 0.0;
		}

		Map<String, Budget> budgetMap = repo.getAll().stream()//
				.collect(toMap(budget -> budget.getYearMonth(), budget -> budget));

		// 同年同月
		if ((startDate.getYear() == endDate.getYear()) //
				&& (startDate.getMonthValue() == endDate.getMonthValue())) {

			// 只需要計算當月天數
			return getBudgetAmount(budgetMap, startDate, endDate.getDayOfMonth() - startDate.getDayOfMonth() + 1);
		}

		double tempBudgetAmount = 0.0;
		tempBudgetAmount += getStartMonthBudgetAmount(budgetMap, startDate);
		tempBudgetAmount += getEndMonthBudgetAmount(budgetMap, endDate);
		tempBudgetAmount += getBetweenMonthBudgetAmount(budgetMap, startDate, endDate);
		return tempBudgetAmount;
	}

	private double getStartMonthBudgetAmount(Map<String, Budget> budgetMap, LocalDate startDate) {
		return getBudgetAmount(budgetMap, startDate, startDate.lengthOfMonth() - startDate.getDayOfMonth() + 1);
	}

	private double getEndMonthBudgetAmount(Map<String, Budget> budgetMap, LocalDate endDate) {
		return getBudgetAmount(budgetMap, endDate, endDate.getDayOfMonth());
	}

	private double getBetweenMonthBudgetAmount(Map<String, Budget> budgetMap, LocalDate startDate, LocalDate endDate) {
		startDate = startDate.plusMonths(1);

		boolean isSameYearAndMonth = (startDate.getYear() == endDate.getYear())//
				&& (startDate.getMonthValue() == endDate.getMonthValue());

		double tempBudgetAmount = 0.0;
		while (!isSameYearAndMonth) {
			tempBudgetAmount += Optional.ofNullable(budgetMap.get(getBudgetKey(startDate)))//
					.map(budget -> budget.getAmount())//
					.orElse(0);

			startDate = startDate.plusMonths(1);
			isSameYearAndMonth = (startDate.getYear() == endDate.getYear())//
					&& (startDate.getMonthValue() == endDate.getMonthValue());
		}

		return tempBudgetAmount;
	}

	private double getBudgetAmount(Map<String, Budget> budgetMap, LocalDate localDate, int diffDay) {
		int monthDays = localDate.lengthOfMonth();
		int budgetAmount = getBudgetAmount(budgetMap, localDate);
		return (double) (diffDay * budgetAmount) / (double) monthDays;
	}

	private int getBudgetAmount(Map<String, Budget> budgetMap, LocalDate localDate) {
		return Optional.ofNullable(budgetMap.get(getBudgetKey(localDate)))//
				.map(budget -> budget.getAmount())//
				.orElse(0);
	}

	private String getBudgetKey(LocalDate localDate) {
		return localDate.format(DateTimeFormatter.ofPattern("yyyyMM"));
	}
}