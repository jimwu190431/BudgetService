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
			int diffDay = endDate.getDayOfMonth() - startDate.getDayOfMonth() + 1;
			int monthDays = startDate.lengthOfMonth();
			int budgetAmount = Optional.ofNullable(budgetMap.get(getBudgetKey(startDate)))//
					.map(budget -> budget.getAmount())//
					.orElse(0);

			return (double) (diffDay * budgetAmount) / (double) monthDays;
		}

		double tempBudgetAmount = 0.0;
		tempBudgetAmount += getStartMonthBudgetAmount(startDate);
		tempBudgetAmount += getEndMonthBudgetAmount(endDate);
		tempBudgetAmount += getBetweenMonthBudgetAmount(startDate, endDate);
		return tempBudgetAmount;
	}

	private double getStartMonthBudgetAmount(LocalDate startDate) {
		return 0.0;
	}

	private double getEndMonthBudgetAmount(LocalDate endDate) {
		return 0.0;
	}

	private double getBetweenMonthBudgetAmount(LocalDate startDate, LocalDate endDate) {
		return 0.0;
	}

	private String getBudgetKey(LocalDate localDate) {
		return localDate.format(DateTimeFormatter.ofPattern("yyyyMM"));
	}
}