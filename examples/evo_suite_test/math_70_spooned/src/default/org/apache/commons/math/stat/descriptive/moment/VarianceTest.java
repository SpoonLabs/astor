package org.apache.commons.math.stat.descriptive.moment;


public class VarianceTest extends org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest {
	protected org.apache.commons.math.stat.descriptive.moment.Variance stat;

	public VarianceTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.UnivariateStatistic getUnivariateStatistic() {
		return new org.apache.commons.math.stat.descriptive.moment.Variance();
	}

	@java.lang.Override
	public double expectedValue() {
		return this.var;
	}

	public double expectedWeightedValue() {
		return this.weightedVar;
	}

	public void testNaN() {
		org.apache.commons.math.stat.descriptive.moment.StandardDeviation std = new org.apache.commons.math.stat.descriptive.moment.StandardDeviation();
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(std.getResult()));
		std.increment(1.0);
		junit.framework.Assert.assertEquals(0.0, std.getResult(), 0);
	}

	public void testPopulation() {
		double[] values = new double[]{ -1.0 , 3.1 , 4.0 , -2.1 , 22.0 , 11.7 , 3.0 , 14.0 };
		org.apache.commons.math.stat.descriptive.moment.SecondMoment m = new org.apache.commons.math.stat.descriptive.moment.SecondMoment();
		m.evaluate(values);
		org.apache.commons.math.stat.descriptive.moment.Variance v1 = new org.apache.commons.math.stat.descriptive.moment.Variance();
		v1.setBiasCorrected(false);
		junit.framework.Assert.assertEquals(populationVariance(values), v1.evaluate(values), 1.0E-14);
		v1.incrementAll(values);
		junit.framework.Assert.assertEquals(populationVariance(values), v1.getResult(), 1.0E-14);
		v1 = new org.apache.commons.math.stat.descriptive.moment.Variance(false , m);
		junit.framework.Assert.assertEquals(populationVariance(values), v1.getResult(), 1.0E-14);
		v1 = new org.apache.commons.math.stat.descriptive.moment.Variance(false);
		junit.framework.Assert.assertEquals(populationVariance(values), v1.evaluate(values), 1.0E-14);
		v1.incrementAll(values);
		junit.framework.Assert.assertEquals(populationVariance(values), v1.getResult(), 1.0E-14);
	}

	protected double populationVariance(double[] v) {
		double mean = new org.apache.commons.math.stat.descriptive.moment.Mean().evaluate(v);
		double sum = 0;
		for (int i = 0 ; i < (v.length) ; i++) {
			sum += ((v[i]) - mean) * ((v[i]) - mean);
		}
		return sum / (v.length);
	}

	public void testWeightedVariance() {
		org.apache.commons.math.stat.descriptive.moment.Variance variance = new org.apache.commons.math.stat.descriptive.moment.Variance();
		junit.framework.Assert.assertEquals(expectedWeightedValue(), variance.evaluate(testArray, testWeightsArray, 0, testArray.length), getTolerance());
		junit.framework.Assert.assertEquals(expectedValue(), variance.evaluate(testArray, unitWeightsArray, 0, testArray.length), getTolerance());
		junit.framework.Assert.assertEquals(expectedValue(), variance.evaluate(testArray, org.apache.commons.math.util.MathUtils.normalizeArray(identicalWeightsArray, testArray.length), 0, testArray.length), getTolerance());
	}
}

