package org.apache.commons.math.distribution;


public class GammaDistributionTest extends org.apache.commons.math.distribution.ContinuousDistributionAbstractTest {
	public GammaDistributionTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.distribution.GammaDistribution makeDistribution() {
		return new org.apache.commons.math.distribution.GammaDistributionImpl(4.0 , 2.0);
	}

	@java.lang.Override
	public double[] makeCumulativeTestPoints() {
		return new double[]{ 0.857104827257 , 1.64649737269 , 2.17973074725 , 2.7326367935 , 3.48953912565 , 26.1244815584 , 20.0902350297 , 17.5345461395 , 15.5073130559 , 13.3615661365 };
	}

	@java.lang.Override
	public double[] makeCumulativeTestValues() {
		return new double[]{ 0.001 , 0.01 , 0.025 , 0.05 , 0.1 , 0.999 , 0.99 , 0.975 , 0.95 , 0.9 };
	}

	@java.lang.Override
	public double[] makeDensityTestValues() {
		return new double[]{ 0.00427280075546 , 0.0204117166709 , 0.0362756163658 , 0.0542113174239 , 0.0773195272491 , 3.94468852816E-4 , 0.00366559696761 , 0.00874649473311 , 0.0166712508128 , 0.0311798227954 };
	}

	@java.lang.Override
	protected void setUp() throws java.lang.Exception {
		super.setUp();
		setTolerance(1.0E-9);
	}

	public void testParameterAccessors() {
		org.apache.commons.math.distribution.GammaDistribution distribution = ((org.apache.commons.math.distribution.GammaDistribution)(getDistribution()));
		junit.framework.Assert.assertEquals(4.0, distribution.getAlpha(), 0);
		distribution.setAlpha(3.0);
		junit.framework.Assert.assertEquals(3.0, distribution.getAlpha(), 0);
		junit.framework.Assert.assertEquals(2.0, distribution.getBeta(), 0);
		distribution.setBeta(4.0);
		junit.framework.Assert.assertEquals(4.0, distribution.getBeta(), 0);
		try {
			distribution.setAlpha(0.0);
			junit.framework.Assert.fail("Expecting IllegalArgumentException for alpha = 0");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			distribution.setBeta(0.0);
			junit.framework.Assert.fail("Expecting IllegalArgumentException for beta = 0");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testProbabilities() throws java.lang.Exception {
		testProbability(-1.0, 4.0, 2.0, 0.0);
		testProbability(15.501, 4.0, 2.0, 0.9499);
		testProbability(0.504, 4.0, 1.0, 0.0018);
		testProbability(10.011, 1.0, 2.0, 0.9933);
		testProbability(5.0, 2.0, 2.0, 0.7127);
	}

	public void testValues() throws java.lang.Exception {
		testValue(15.501, 4.0, 2.0, 0.9499);
		testValue(0.504, 4.0, 1.0, 0.0018);
		testValue(10.011, 1.0, 2.0, 0.9933);
		testValue(5.0, 2.0, 2.0, 0.7127);
	}

	private void testProbability(double x, double a, double b, double expected) throws java.lang.Exception {
		org.apache.commons.math.distribution.GammaDistribution distribution = new org.apache.commons.math.distribution.GammaDistributionImpl(a , b);
		double actual = distribution.cumulativeProbability(x);
		junit.framework.Assert.assertEquals(("probability for " + x), expected, actual, 0.001);
	}

	private void testValue(double expected, double a, double b, double p) throws java.lang.Exception {
		org.apache.commons.math.distribution.GammaDistribution distribution = new org.apache.commons.math.distribution.GammaDistributionImpl(a , b);
		double actual = distribution.inverseCumulativeProbability(p);
		junit.framework.Assert.assertEquals(("critical value for " + p), expected, actual, 0.001);
	}

	public void testDensity() {
		double[] x = new double[]{ -0.1 , 1.0E-6 , 0.5 , 1 , 2 , 5 };
		checkDensity(1, 1, x, new double[]{ 0.0 , 0.999999000001 , 0.606530659713 , 0.367879441171 , 0.135335283237 , 0.006737946999 });
		checkDensity(2, 1, x, new double[]{ 0.0 , 9.99999E-7 , 0.303265329856 , 0.367879441171 , 0.270670566473 , 0.033689734995 });
		checkDensity(4, 1, x, new double[]{ 0.0 , 1.666665E-19 , 0.01263605541 , 0.0613132402 , 0.1804470443 , 0.1403738958 });
		checkDensity(4, 10, x, new double[]{ 0.0 , 1.66665E-15 , 1.403738958 , 0.0756665496 , 2.74820483E-5 , 4.01822885E-17 });
		checkDensity(0.1, 10, x, new double[]{ 0.0 , 33239.53832 , 0.00166384901 , 6.007786726E-6 , 1.461647647E-10 , 5.996008322E-24 });
		checkDensity(0.1, 20, x, new double[]{ 0.0 , 35624.89883 , 1.201557345E-5 , 2.923295295E-10 , 3.228910843E-19 , 1.239484589E-45 });
		checkDensity(0.1, 4, x, new double[]{ 0.0 , 30329.38388 , 0.03049322494 , 0.002211502311 , 2.170613371E-5 , 5.846590589E-11 });
		checkDensity(0.1, 1, x, new double[]{ 0.0 , 26403.34143 , 0.1189704437 , 0.03866916944 , 0.007623306235 , 1.66384901E-4 });
	}

	private void checkDensity(double alpha, double rate, double[] x, double[] expected) {
		org.apache.commons.math.distribution.GammaDistribution d = new org.apache.commons.math.distribution.GammaDistributionImpl(alpha , (1 / rate));
		for (int i = 0 ; i < (x.length) ; i++) {
			junit.framework.Assert.assertEquals(expected[i], d.density(x[i]), 1.0E-5);
		}
	}

	public void testInverseCumulativeProbabilityExtremes() throws java.lang.Exception {
		setInverseCumulativeTestPoints(new double[]{ 0 , 1 });
		setInverseCumulativeTestValues(new double[]{ 0 , java.lang.Double.POSITIVE_INFINITY });
		verifyInverseCumulativeProbabilities();
	}
}

