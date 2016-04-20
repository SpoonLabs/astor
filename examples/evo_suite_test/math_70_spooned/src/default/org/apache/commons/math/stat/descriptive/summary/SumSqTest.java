package org.apache.commons.math.stat.descriptive.summary;


public class SumSqTest extends org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest {
	protected org.apache.commons.math.stat.descriptive.summary.SumOfSquares stat;

	public SumSqTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.UnivariateStatistic getUnivariateStatistic() {
		return new org.apache.commons.math.stat.descriptive.summary.SumOfSquares();
	}

	@java.lang.Override
	public double expectedValue() {
		return this.sumSq;
	}

	public void testSpecialValues() {
		org.apache.commons.math.stat.descriptive.summary.SumOfSquares sumSq = new org.apache.commons.math.stat.descriptive.summary.SumOfSquares();
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(sumSq.getResult()));
		sumSq.increment(2.0);
		junit.framework.Assert.assertEquals(4.0, sumSq.getResult(), 0);
		sumSq.increment(java.lang.Double.POSITIVE_INFINITY);
		junit.framework.Assert.assertEquals(java.lang.Double.POSITIVE_INFINITY, sumSq.getResult(), 0);
		sumSq.increment(java.lang.Double.NEGATIVE_INFINITY);
		junit.framework.Assert.assertEquals(java.lang.Double.POSITIVE_INFINITY, sumSq.getResult(), 0);
		sumSq.increment(java.lang.Double.NaN);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(sumSq.getResult()));
		sumSq.increment(1);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(sumSq.getResult()));
	}
}

