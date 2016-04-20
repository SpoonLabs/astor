package org.apache.commons.math.stat.descriptive.summary;


public class SumTest extends org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest {
	protected org.apache.commons.math.stat.descriptive.summary.Sum stat;

	public SumTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.UnivariateStatistic getUnivariateStatistic() {
		return new org.apache.commons.math.stat.descriptive.summary.Sum();
	}

	@java.lang.Override
	public double expectedValue() {
		return this.sum;
	}

	public double expectedWeightedValue() {
		return this.weightedSum;
	}

	public void testSpecialValues() {
		org.apache.commons.math.stat.descriptive.summary.Sum sum = new org.apache.commons.math.stat.descriptive.summary.Sum();
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(sum.getResult()));
		sum.increment(1);
		junit.framework.Assert.assertEquals(1, sum.getResult(), 0);
		sum.increment(java.lang.Double.POSITIVE_INFINITY);
		junit.framework.Assert.assertEquals(java.lang.Double.POSITIVE_INFINITY, sum.getResult(), 0);
		sum.increment(java.lang.Double.NEGATIVE_INFINITY);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(sum.getResult()));
		sum.increment(1);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(sum.getResult()));
	}

	public void testWeightedSum() {
		org.apache.commons.math.stat.descriptive.summary.Sum sum = new org.apache.commons.math.stat.descriptive.summary.Sum();
		junit.framework.Assert.assertEquals(expectedWeightedValue(), sum.evaluate(testArray, testWeightsArray, 0, testArray.length), getTolerance());
		junit.framework.Assert.assertEquals(expectedValue(), sum.evaluate(testArray, unitWeightsArray, 0, testArray.length), getTolerance());
	}
}

