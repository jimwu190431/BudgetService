package com.yile.service;

import org.junit.Assert;
import org.junit.Test;

public class BudgetServiceTest {

	@Test
	public void test1() {
		BudgetService service = new BudgetService();
		double result = service.query(null, null);
		Assert.assertEquals(result, 0.0, 0.00);
	}
}