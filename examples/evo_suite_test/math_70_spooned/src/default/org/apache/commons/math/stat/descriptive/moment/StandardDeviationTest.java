package org.apache.commons.math.stat.descriptive.moment;


public class StandardDeviationTest extends org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest {
	protected org.apache.commons.math.stat.descriptive.moment.StandardDeviation stat;

	public StandardDeviationTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.UnivariateStatistic getUnivariateStatistic() {
		return new org.apache.commons.math.stat.descriptive.moment.StandardDeviation();
	}

	@java.lang.Override
	public double expectedValue() {
		return this.std;
	}

	public void testNaN() {
		org.apache.commons.math.stat.descriptive.moment.StandardDeviation std = new org.apache.commons.math.stat.descriptive.moment.StandardDeviation();
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(std.getResult()));
		std.increment(1.0);
		junit.framework.Assert.assertEquals(0.0, std.getResult(), 0);
	}

	public void testPopulation() {
		double[] values = new double[]{ -1.0 , 3.1 , 4.0 , -2.1 , 22.0 , 11.7 , 3.0 , 14.0 };
		double sigma = populationStandardDeviation(values);
		org.apache.commons.math.stat.descriptive.moment.SecondMoment m = new org.apache.commons.math.stat.descriptive.moment.SecondMoment();
		m.evaluate(values);
		org.apache.commons.math.stat.descriptive.moment.StandardDeviation s1 = new org.apache.commons.math.stat.descriptive.moment.StandardDeviation();
		s1.setBiasCorrected(false);
		junit.framework.Assert.assertEquals(sigma, s1.evaluate(values), 1.0E-14);
		s1.incrementAll(values);
		junit.framework.Assert.assertEquals(sigma, s1.getResult(), 1.0E-14);
		s1 = new org.apache.commons.math.stat.descriptive.moment.StandardDeviation(false , m);
		junit.framework.Assert.assertEquals(sigma, s1.getResult(), 1.0E-14);
		s1 = new org.apache.commons.math.stat.descriptive.moment.StandardDeviation(false);
		junit.framework.Assert.assertEquals(sigma, s1.evaluate(values), 1.0E-14);
		s1.incrementAll(values);
		junit.framework.Assert.assertEquals(sigma, s1.getResult(), 1.0E-14);
	}

	protected double populationStandardDeviation(double[] v) {
		double mean = new org.apache.commons.math.stat.descriptive.moment.Mean().evaluate(v);
		double sum = 0;
		for (int i = 0 ; i < (v.length) ; i++) {
			sum += ((v[i]) - mean) * ((v[i]) - mean);
		}
		return java.lang.Math.sqrt((sum / (v.length)));
	}
}

