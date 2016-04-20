package org.apache.commons.math.distribution;


public class FDistributionTest extends org.apache.commons.math.distribution.ContinuousDistributionAbstractTest {
	public FDistributionTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.distribution.FDistribution makeDistribution() {
		return new org.apache.commons.math.distribution.FDistributionImpl(5.0 , 6.0);
	}

	@java.lang.Override
	public double[] makeCumulativeTestPoints() {
		return new double[]{ 0.0346808448626 , 0.0937009113303 , 0.143313661184 , 0.202008445998 , 0.293728320107 , 20.8026639595 , 8.74589525602 , 5.98756512605 , 4.38737418741 , 3.10751166664 };
	}

	@java.lang.Override
	public double[] makeCumulativeTestValues() {
		return new double[]{ 0.001 , 0.01 , 0.025 , 0.05 , 0.1 , 0.999 , 0.99 , 0.975 , 0.95 , 0.9 };
	}

	@java.lang.Override
	public double[] makeDensityTestValues() {
		return new double[]{ 0.0689156576706 , 0.236735653193 , 0.364074131941 , 0.481570789649 , 0.595880479994 , 1.33443915657E-4 , 0.00286681303403 , 0.00969192007502 , 0.0242883861471 , 0.0605491314658 };
	}

	@java.lang.Override
	protected void setUp() throws java.lang.Exception {
		super.setUp();
		setTolerance(1.0E-9);
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

	public void testDfAccessors() {
		org.apache.commons.math.distribution.FDistribution distribution = ((org.apache.commons.math.distribution.FDistribution)(getDistribution()));
		junit.framework.Assert.assertEquals(5.0, distribution.getNumeratorDegreesOfFreedom(), java.lang.Double.MIN_VALUE);
		distribution.setNumeratorDegreesOfFreedom(4.0);
		junit.framework.Assert.assertEquals(4.0, distribution.getNumeratorDegreesOfFreedom(), java.lang.Double.MIN_VALUE);
		junit.framework.Assert.assertEquals(6.0, distribution.getDenominatorDegreesOfFreedom(), java.lang.Double.MIN_VALUE);
		distribution.setDenominatorDegreesOfFreedom(4.0);
		junit.framework.Assert.assertEquals(4.0, distribution.getDenominatorDegreesOfFreedom(), java.lang.Double.MIN_VALUE);
		try {
			distribution.setNumeratorDegreesOfFreedom(0.0);
			junit.framework.Assert.fail("Expecting IllegalArgumentException for df = 0");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			distribution.setDenominatorDegreesOfFreedom(0.0);
			junit.framework.Assert.fail("Expecting IllegalArgumentException for df = 0");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testLargeDegreesOfFreedom() throws java.lang.Exception {
		org.apache.commons.math.distribution.FDistributionImpl fd = new org.apache.commons.math.distribution.FDistributionImpl(100000.0 , 100000.0);
		double p = fd.cumulativeProbability(0.999);
		double x = fd.inverseCumulativeProbability(p);
		junit.framework.Assert.assertEquals(0.999, x, 1.0E-5);
	}

	public void testSmallDegreesOfFreedom() throws java.lang.Exception {
		org.apache.commons.math.distribution.FDistributionImpl fd = new org.apache.commons.math.distribution.FDistributionImpl(1.0 , 1.0);
		double p = fd.cumulativeProbability(0.975);
		double x = fd.inverseCumulativeProbability(p);
		junit.framework.Assert.assertEquals(0.975, x, 1.0E-5);
		fd.setDenominatorDegreesOfFreedom(2.0);
		p = fd.cumulativeProbability(0.975);
		x = fd.inverseCumulativeProbability(p);
		junit.framework.Assert.assertEquals(0.975, x, 1.0E-5);
	}
}

