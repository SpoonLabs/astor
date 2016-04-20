package org.apache.commons.math.distribution;


public class CauchyDistributionTest extends org.apache.commons.math.distribution.ContinuousDistributionAbstractTest {
	public CauchyDistributionTest(java.lang.String arg0) {
		super(arg0);
	}

	protected double defaultTolerance = org.apache.commons.math.distribution.NormalDistributionImpl.DEFAULT_INVERSE_ABSOLUTE_ACCURACY;

	@java.lang.Override
	protected void setUp() throws java.lang.Exception {
		super.setUp();
		setTolerance(defaultTolerance);
	}

	@java.lang.Override
	public org.apache.commons.math.distribution.CauchyDistribution makeDistribution() {
		return new org.apache.commons.math.distribution.CauchyDistributionImpl(1.2 , 2.1);
	}

	@java.lang.Override
	public double[] makeCumulativeTestPoints() {
		return new double[]{ -667.24856187 , -65.6230835029 , -25.483029946 , -12.0588781808 , -5.26313542807 , 669.64856187 , 68.0230835029 , 27.883029946 , 14.4588781808 , 7.66313542807 };
	}

	@java.lang.Override
	public double[] makeCumulativeTestValues() {
		return new double[]{ 0.001 , 0.01 , 0.025 , 0.05 , 0.1 , 0.999 , 0.99 , 0.975 , 0.95 , 0.9 };
	}

	@java.lang.Override
	public double[] makeDensityTestValues() {
		return new double[]{ 1.49599158008E-6 , 1.49550440335E-4 , 9.33076881878E-4 , 0.00370933207799 , 0.0144742330437 , 1.49599158008E-6 , 1.49550440335E-4 , 9.33076881878E-4 , 0.00370933207799 , 0.0144742330437 };
	}

	public void testInverseCumulativeProbabilityExtremes() throws java.lang.Exception {
		setInverseCumulativeTestPoints(new double[]{ 0.0 , 1.0 });
		setInverseCumulativeTestValues(new double[]{ java.lang.Double.NEGATIVE_INFINITY , java.lang.Double.POSITIVE_INFINITY });
		verifyInverseCumulativeProbabilities();
	}

	public void testMedian() {
		org.apache.commons.math.distribution.CauchyDistribution distribution = ((org.apache.commons.math.distribution.CauchyDistribution)(getDistribution()));
		double expected = java.lang.Math.random();
		distribution.setMedian(expected);
		junit.framework.Assert.assertEquals(expected, distribution.getMedian(), 0.0);
	}

	public void testScale() {
		org.apache.commons.math.distribution.CauchyDistribution distribution = ((org.apache.commons.math.distribution.CauchyDistribution)(getDistribution()));
		double expected = java.lang.Math.random();
		distribution.setScale(expected);
		junit.framework.Assert.assertEquals(expected, distribution.getScale(), 0.0);
	}

	public void testSetScale() {
		org.apache.commons.math.distribution.CauchyDistribution distribution = ((org.apache.commons.math.distribution.CauchyDistribution)(getDistribution()));
		try {
			distribution.setScale(0.0);
			junit.framework.Assert.fail("Can not have 0.0 scale.");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			distribution.setScale(-1.0);
			junit.framework.Assert.fail("Can not have negative scale.");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}
}

