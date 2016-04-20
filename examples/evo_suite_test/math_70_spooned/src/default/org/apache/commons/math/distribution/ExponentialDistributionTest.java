package org.apache.commons.math.distribution;


public class ExponentialDistributionTest extends org.apache.commons.math.distribution.ContinuousDistributionAbstractTest {
	public ExponentialDistributionTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	protected void setUp() throws java.lang.Exception {
		super.setUp();
		setTolerance(1.0E-9);
	}

	@java.lang.Override
	public org.apache.commons.math.distribution.ExponentialDistribution makeDistribution() {
		return new org.apache.commons.math.distribution.ExponentialDistributionImpl(5.0);
	}

	@java.lang.Override
	public double[] makeCumulativeTestPoints() {
		return new double[]{ 0.00500250166792 , 0.0502516792675 , 0.126589039921 , 0.256466471938 , 0.526802578289 , 34.5387763949 , 23.0258509299 , 18.4443972706 , 14.9786613678 , 11.512925465 };
	}

	@java.lang.Override
	public double[] makeCumulativeTestValues() {
		return new double[]{ 0.001 , 0.01 , 0.025 , 0.05 , 0.1 , 0.999 , 0.99 , 0.975 , 0.95 , 0.9 };
	}

	@java.lang.Override
	public double[] makeDensityTestValues() {
		return new double[]{ 0.1998 , 0.198 , 0.195 , 0.19 , 0.18 , 2.0E-4 , 0.00200000000002 , 0.00499999999997 , 0.00999999999994 , 0.0199999999999 };
	}

	public void testCumulativeProbabilityExtremes() throws java.lang.Exception {
		setCumulativeTestPoints(new double[]{ -2 , 0 });
		setCumulativeTestValues(new double[]{ 0 , 0 });
		verifyCumulativeProbabilities();
	}

	public void testInverseCumulativeProbabilityExtremes() throws java.lang.Exception {
		setInverseCumulativeTestPoints(new double[]{ 0 , 1 });
		setInverseCumulativeTestValues(new double[]{ 0 , java.lang.Double.POSITIVE_INFINITY });
		verifyInverseCumulativeProbabilities();
	}

	public void testCumulativeProbability2() throws java.lang.Exception {
		double actual = getDistribution().cumulativeProbability(0.25, 0.75);
		junit.framework.Assert.assertEquals(0.0905214, actual, 0.001);
	}

	public void testDensity() {
		org.apache.commons.math.distribution.ExponentialDistribution d1 = new org.apache.commons.math.distribution.ExponentialDistributionImpl(1);
		junit.framework.Assert.assertEquals(0.0, d1.density(-1.0E-9));
		junit.framework.Assert.assertEquals(1.0, d1.density(0.0));
		junit.framework.Assert.assertEquals(0.0, d1.density(1000.0));
		junit.framework.Assert.assertEquals(java.lang.Math.exp(-1), d1.density(1.0));
		junit.framework.Assert.assertEquals(java.lang.Math.exp(-2), d1.density(2.0));
		org.apache.commons.math.distribution.ExponentialDistribution d2 = new org.apache.commons.math.distribution.ExponentialDistributionImpl(3);
		junit.framework.Assert.assertEquals((1 / 3.0), d2.density(0.0));
		junit.framework.Assert.assertEquals(0.2388437702, d2.density(1.0), 1.0E-8);
		junit.framework.Assert.assertEquals(0.1711390397, d2.density(2.0), 1.0E-8);
	}

	public void testMeanAccessors() {
		org.apache.commons.math.distribution.ExponentialDistribution distribution = ((org.apache.commons.math.distribution.ExponentialDistribution)(getDistribution()));
		junit.framework.Assert.assertEquals(5.0, distribution.getMean(), java.lang.Double.MIN_VALUE);
		distribution.setMean(2.0);
		junit.framework.Assert.assertEquals(2.0, distribution.getMean(), java.lang.Double.MIN_VALUE);
		try {
			distribution.setMean(0);
			junit.framework.Assert.fail("Expecting IllegalArgumentException for 0 mean");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}
}

