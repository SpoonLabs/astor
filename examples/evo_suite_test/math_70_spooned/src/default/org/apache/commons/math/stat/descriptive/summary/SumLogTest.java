package org.apache.commons.math.stat.descriptive.summary;


public class SumLogTest extends org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest {
	protected org.apache.commons.math.stat.descriptive.summary.SumOfLogs stat;

	public SumLogTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.UnivariateStatistic getUnivariateStatistic() {
		return new org.apache.commons.math.stat.descriptive.summary.SumOfLogs();
	}

	@java.lang.Override
	public double expectedValue() {
		return this.sumLog;
	}

	public void testSpecialValues() {
		org.apache.commons.math.stat.descriptive.summary.SumOfLogs sum = new org.apache.commons.math.stat.descriptive.summary.SumOfLogs();
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(sum.getResult()));
		sum.increment(1.0);
		junit.framework.Assert.assertFalse(java.lang.Double.isNaN(sum.getResult()));
		sum.increment(0.0);
		junit.framework.Assert.assertEquals(java.lang.Double.NEGATIVE_INFINITY, sum.getResult(), 0);
		sum.increment(java.lang.Double.POSITIVE_INFINITY);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(sum.getResult()));
		sum.clear();
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(sum.getResult()));
		sum.increment(java.lang.Double.POSITIVE_INFINITY);
		junit.framework.Assert.assertEquals(java.lang.Double.POSITIVE_INFINITY, sum.getResult(), 0);
		sum.increment(-2.0);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(sum.getResult()));
	}
}

