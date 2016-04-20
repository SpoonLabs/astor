package org.apache.commons.math.distribution;


public class NormalDistributionTest extends org.apache.commons.math.distribution.ContinuousDistributionAbstractTest {
	public NormalDistributionTest(java.lang.String arg0) {
		super(arg0);
	}

	@java.lang.Override
	public org.apache.commons.math.distribution.NormalDistribution makeDistribution() {
		return new org.apache.commons.math.distribution.NormalDistributionImpl(2.1 , 1.4);
	}

	@java.lang.Override
	public double[] makeCumulativeTestPoints() {
		return new double[]{ -2.226325228634938 , -1.156887023657177 , -0.643949578356075 , -0.2027950777320613 , 0.305827808237559 , 6.42632522863494 , 5.35688702365718 , 4.843949578356074 , 4.40279507773206 , 3.89417219176244 };
	}

	@java.lang.Override
	public double[] makeCumulativeTestValues() {
		return new double[]{ 0.001 , 0.01 , 0.025 , 0.05 , 0.1 , 0.999 , 0.99 , 0.975 , 0.95 , 0.9 };
	}

	@java.lang.Override
	public double[] makeDensityTestValues() {
		return new double[]{ 0.00240506434076 , 0.019037244431 , 0.0417464784322 , 0.0736683145538 , 0.12535595138 , 0.00240506434076 , 0.019037244431 , 0.0417464784322 , 0.0736683145538 , 0.12535595138 };
	}

	protected double defaultTolerance = org.apache.commons.math.distribution.NormalDistributionImpl.DEFAULT_INVERSE_ABSOLUTE_ACCURACY;

	@java.lang.Override
	protected void setUp() throws java.lang.Exception {
		super.setUp();
		setTolerance(defaultTolerance);
	}

	private void verifyQuantiles() throws java.lang.Exception {
		org.apache.commons.math.distribution.NormalDistribution distribution = ((org.apache.commons.math.distribution.NormalDistribution)(getDistribution()));
		double mu = distribution.getMean();
		double sigma = distribution.getStandardDeviation();
		setCumulativeTestPoints(new double[]{ mu - (2 * sigma) , mu - sigma , mu , mu + sigma , mu + (2 * sigma) , mu + (3 * sigma) , mu + (4 * sigma) , mu + (5 * sigma) });
		setCumulativeTestValues(new double[]{ 0.02275013194817921 , 0.158655253931457 , 0.5 , 0.841344746068543 , 0.977249868051821 , 0.99865010196837 , 0.999968328758167 , 0.999999713348428 });
		verifyCumulativeProbabilities();
	}

	public void testQuantiles() throws java.lang.Exception {
		setDensityTestValues(new double[]{ 0.0385649760808 , 0.172836231799 , 0.284958771715 , 0.172836231799 , 0.0385649760808 , 0.00316560600853 , 9.55930184035E-5 , 1.06194251052E-6 });
		verifyQuantiles();
		verifyDensities();
		setDistribution(new org.apache.commons.math.distribution.NormalDistributionImpl(0 , 1));
		setDensityTestValues(new double[]{ 0.0539909665132 , 0.241970724519 , 0.398942280401 , 0.241970724519 , 0.0539909665132 , 0.00443184841194 , 1.33830225765E-4 , 1.48671951473E-6 });
		verifyQuantiles();
		verifyDensities();
		setDistribution(new org.apache.commons.math.distribution.NormalDistributionImpl(0 , 0.1));
		setDensityTestValues(new double[]{ 0.539909665132 , 2.41970724519 , 3.98942280401 , 2.41970724519 , 0.539909665132 , 0.0443184841194 , 0.00133830225765 , 1.48671951473E-5 });
		verifyQuantiles();
		verifyDensities();
	}

	public void testInverseCumulativeProbabilityExtremes() throws java.lang.Exception {
		setInverseCumulativeTestPoints(new double[]{ 0 , 1 });
		setInverseCumulativeTestValues(new double[]{ java.lang.Double.NEGATIVE_INFINITY , java.lang.Double.POSITIVE_INFINITY });
		verifyInverseCumulativeProbabilities();
	}

	public void testGetMean() {
		org.apache.commons.math.distribution.NormalDistribution distribution = ((org.apache.commons.math.distribution.NormalDistribution)(getDistribution()));
		junit.framework.Assert.assertEquals(2.1, distribution.getMean(), 0);
	}

	public void testSetMean() throws java.lang.Exception {
		double mu = java.lang.Math.random();
		org.apache.commons.math.distribution.NormalDistribution distribution = ((org.apache.commons.math.distribution.NormalDistribution)(getDistribution()));
		distribution.setMean(mu);
		verifyQuantiles();
	}

	public void testGetStandardDeviation() {
		org.apache.commons.math.distribution.NormalDistribution distribution = ((org.apache.commons.math.distribution.NormalDistribution)(getDistribution()));
		junit.framework.Assert.assertEquals(1.4, distribution.getStandardDeviation(), 0);
	}

	public void testSetStandardDeviation() throws java.lang.Exception {
		double sigma = 0.1 + (java.lang.Math.random());
		org.apache.commons.math.distribution.NormalDistribution distribution = ((org.apache.commons.math.distribution.NormalDistribution)(getDistribution()));
		distribution.setStandardDeviation(sigma);
		junit.framework.Assert.assertEquals(sigma, distribution.getStandardDeviation(), 0);
		verifyQuantiles();
		try {
			distribution.setStandardDeviation(0);
			junit.framework.Assert.fail("Expecting IllegalArgumentException for sd = 0");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testDensity() {
		double[] x = new double[]{ -2 , -1 , 0 , 1 , 2 };
		checkDensity(0, 1, x, new double[]{ 0.05399096651 , 0.24197072452 , 0.3989422804 , 0.24197072452 , 0.05399096651 });
		checkDensity(1.1, 1, x, new double[]{ 0.003266819056 , 0.04398359598 , 0.217852177033 , 0.396952547477 , 0.266085249899 });
	}

	private void checkDensity(double mean, double sd, double[] x, double[] expected) {
		org.apache.commons.math.distribution.NormalDistribution d = new org.apache.commons.math.distribution.NormalDistributionImpl(mean , sd);
		for (int i = 0 ; i < (x.length) ; i++) {
			junit.framework.Assert.assertEquals(expected[i], d.density(x[i]), 1.0E-9);
		}
	}

	public void testExtremeValues() throws java.lang.Exception {
		org.apache.commons.math.distribution.NormalDistribution distribution = ((org.apache.commons.math.distribution.NormalDistribution)(getDistribution()));
		distribution.setMean(0);
		distribution.setStandardDeviation(1);
		for (int i = 0 ; i < 100 ; i += 5) {
			double lowerTail = distribution.cumulativeProbability(-i);
			double upperTail = distribution.cumulativeProbability(i);
			if (i < 10) {
				junit.framework.Assert.assertTrue((lowerTail > 0.0));
				junit.framework.Assert.assertTrue((upperTail < 1.0));
			} else {
				junit.framework.Assert.assertTrue((lowerTail < 1.0E-5));
				junit.framework.Assert.assertTrue((upperTail > 0.99999));
			}
		}
	}

	public void testMath280() throws org.apache.commons.math.MathException {
		org.apache.commons.math.distribution.NormalDistribution normal = new org.apache.commons.math.distribution.NormalDistributionImpl(0 , 1);
		double result = normal.inverseCumulativeProbability(0.9986501019683698);
		junit.framework.Assert.assertEquals(3.0, result, defaultTolerance);
		result = normal.inverseCumulativeProbability(0.841344746068543);
		junit.framework.Assert.assertEquals(1.0, result, defaultTolerance);
		result = normal.inverseCumulativeProbability(0.9999683287581673);
		junit.framework.Assert.assertEquals(4.0, result, defaultTolerance);
		result = normal.inverseCumulativeProbability(0.9772498680518209);
		junit.framework.Assert.assertEquals(2.0, result, defaultTolerance);
	}
}

