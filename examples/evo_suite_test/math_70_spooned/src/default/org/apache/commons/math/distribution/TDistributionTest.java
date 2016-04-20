package org.apache.commons.math.distribution;


public class TDistributionTest extends org.apache.commons.math.distribution.ContinuousDistributionAbstractTest {
	public TDistributionTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.distribution.TDistribution makeDistribution() {
		return new org.apache.commons.math.distribution.TDistributionImpl(5.0);
	}

	@java.lang.Override
	public double[] makeCumulativeTestPoints() {
		return new double[]{ -5.89342953136 , -3.36492999891 , -2.57058183564 , -2.01504837333 , -1.47588404882 , 5.89342953136 , 3.36492999891 , 2.57058183564 , 2.01504837333 , 1.47588404882 };
	}

	@java.lang.Override
	public double[] makeCumulativeTestValues() {
		return new double[]{ 0.001 , 0.01 , 0.025 , 0.05 , 0.1 , 0.999 , 0.99 , 0.975 , 0.95 , 0.9 };
	}

	@java.lang.Override
	public double[] makeDensityTestValues() {
		return new double[]{ 7.56494565517E-4 , 0.0109109752919 , 0.0303377878006 , 0.0637967988952 , 0.128289492005 , 7.56494565517E-4 , 0.0109109752919 , 0.0303377878006 , 0.0637967988952 , 0.128289492005 };
	}

	@java.lang.Override
	protected void setUp() throws java.lang.Exception {
		super.setUp();
		setTolerance(1.0E-9);
	}

	public void testCumulativeProbabilityAgaintStackOverflow() throws java.lang.Exception {
		org.apache.commons.math.distribution.TDistributionImpl td = new org.apache.commons.math.distribution.TDistributionImpl(5.0);
		td.cumulativeProbability(0.1);
		td.cumulativeProbability(0.01);
	}

	public void testSmallDf() throws java.lang.Exception {
		setDistribution(new org.apache.commons.math.distribution.TDistributionImpl(1.0));
		setCumulativeTestPoints(new double[]{ -318.308838986 , -31.8205159538 , -12.7062047362 , -6.31375151468 , -3.07768353718 , 318.308838986 , 31.8205159538 , 12.7062047362 , 6.31375151468 , 3.07768353718 });
		setDensityTestValues(new double[]{ 3.14158231817E-6 , 3.14055924703E-4 , 0.00195946145194 , 0.00778959736375 , 0.0303958893917 , 3.14158231817E-6 , 3.14055924703E-4 , 0.00195946145194 , 0.00778959736375 , 0.0303958893917 });
		setInverseCumulativeTestValues(getCumulativeTestPoints());
		verifyCumulativeProbabilities();
		verifyInverseCumulativeProbabilities();
		verifyDensities();
	}

	public void testInverseCumulativeProbabilityExtremes() throws java.lang.Exception {
		setInverseCumulativeTestPoints(new double[]{ 0 , 1 });
		setInverseCumulativeTestValues(new double[]{ java.lang.Double.NEGATIVE_INFINITY , java.lang.Double.POSITIVE_INFINITY });
		verifyInverseCumulativeProbabilities();
	}

	public void testDfAccessors() {
		org.apache.commons.math.distribution.TDistribution distribution = ((org.apache.commons.math.distribution.TDistribution)(getDistribution()));
		junit.framework.Assert.assertEquals(5.0, distribution.getDegreesOfFreedom(), java.lang.Double.MIN_VALUE);
		distribution.setDegreesOfFreedom(4.0);
		junit.framework.Assert.assertEquals(4.0, distribution.getDegreesOfFreedom(), java.lang.Double.MIN_VALUE);
		try {
			distribution.setDegreesOfFreedom(0.0);
			junit.framework.Assert.fail("Expecting IllegalArgumentException for df = 0");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}
}

