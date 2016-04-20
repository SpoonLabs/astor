package org.apache.commons.math.stat.descriptive.moment;


public class MeanTest extends org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest {
	protected org.apache.commons.math.stat.descriptive.moment.Mean stat;

	public MeanTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.UnivariateStatistic getUnivariateStatistic() {
		return new org.apache.commons.math.stat.descriptive.moment.Mean();
	}

	@java.lang.Override
	public double expectedValue() {
		return this.mean;
	}

	public double expectedWeightedValue() {
		return this.weightedMean;
	}

	public void testSmallSamples() {
		org.apache.commons.math.stat.descriptive.moment.Mean mean = new org.apache.commons.math.stat.descriptive.moment.Mean();
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(mean.getResult()));
		mean.increment(1.0);
		junit.framework.Assert.assertEquals(1.0, mean.getResult(), 0);
	}

	public void testWeightedMean() {
		org.apache.commons.math.stat.descriptive.moment.Mean mean = new org.apache.commons.math.stat.descriptive.moment.Mean();
		junit.framework.Assert.assertEquals(expectedWeightedValue(), mean.evaluate(testArray, testWeightsArray, 0, testArray.length), getTolerance());
		junit.framework.Assert.assertEquals(expectedValue(), mean.evaluate(testArray, identicalWeightsArray, 0, testArray.length), getTolerance());
	}
}

