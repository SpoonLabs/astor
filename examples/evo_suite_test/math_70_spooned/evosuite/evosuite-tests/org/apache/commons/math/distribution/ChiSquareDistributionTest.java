package org.apache.commons.math.distribution;


public class ChiSquareDistributionTest extends org.apache.commons.math.distribution.ContinuousDistributionAbstractTest {
	public ChiSquareDistributionTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.distribution.ChiSquaredDistribution makeDistribution() {
		return new org.apache.commons.math.distribution.ChiSquaredDistributionImpl(5.0);
	}

	@java.lang.Override
	public double[] makeCumulativeTestPoints() {
		return new double[]{ 0.210212602629 , 0.554298076728 , 0.831211613487 , 1.14547622606 , 1.61030798696 , 20.5150056524 , 15.0862724694 , 12.832501994 , 11.0704976935 , 9.23635689978 };
	}

	@java.lang.Override
	public double[] makeCumulativeTestValues() {
		return new double[]{ 0.001 , 0.01 , 0.025 , 0.05 , 0.1 , 0.999 , 0.99 , 0.975 , 0.95 , 0.9 };
	}

	@java.lang.Override
	public double[] makeInverseCumulativeTestPoints() {
		return new double[]{ 0 , 0.001 , 0.01 , 0.025 , 0.05 , 0.1 , 0.999 , 0.99 , 0.975 , 0.95 , 0.9 , 1 };
	}

	@java.lang.Override
	public double[] makeInverseCumulativeTestValues() {
		return new double[]{ 0 , 0.210212602629 , 0.554298076728 , 0.831211613487 , 1.14547622606 , 1.61030798696 , 20.5150056524 , 15.0862724694 , 12.832501994 , 11.0704976935 , 9.23635689978 , java.lang.Double.POSITIVE_INFINITY };
	}

	@java.lang.Override
	public double[] makeDensityTestValues() {
		return new double[]{ 0.0115379817652 , 0.0415948507811 , 0.0665060119842 , 0.0919455953114 , 0.121472591024 , 4.33630076361E-4 , 0.00412780610309 , 0.00999340341045 , 0.0193246438937 , 0.0368460089216 };
	}

	@java.lang.Override
	protected void setUp() throws java.lang.Exception {
		super.setUp();
		setTolerance(1.0E-9);
	}

	public void testSmallDf() throws java.lang.Exception {
		setDistribution(new org.apache.commons.math.distribution.ChiSquaredDistributionImpl(0.1));
		setTolerance(1.0E-4);
		setCumulativeTestPoints(new double[]{ 1.168926E-60 , 1.168926E-40 , 1.063132E-32 , 1.144775E-26 , 1.168926E-20 , 5.472917 , 2.175255 , 1.13438 , 0.5318646 , 0.1526342 });
		setInverseCumulativeTestValues(getCumulativeTestPoints());
		setInverseCumulativeTestPoints(getCumulativeTestValues());
		verifyCumulativeProbabilities();
		verifyInverseCumulativeProbabilities();
	}

	public void testDfAccessors() {
		org.apache.commons.math.distribution.ChiSquaredDistribution distribution = ((org.apache.commons.math.distribution.ChiSquaredDistribution)(getDistribution()));
		junit.framework.Assert.assertEquals(5.0, distribution.getDegreesOfFreedom(), java.lang.Double.MIN_VALUE);
		distribution.setDegreesOfFreedom(4.0);
		junit.framework.Assert.assertEquals(4.0, distribution.getDegreesOfFreedom(), java.lang.Double.MIN_VALUE);
		try {
			distribution.setDegreesOfFreedom(0.0);
			junit.framework.Assert.fail("Expecting IllegalArgumentException for df = 0");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testDensity() {
		double[] x = new double[]{ -0.1 , 1.0E-6 , 0.5 , 1 , 2 , 5 };
		checkDensity(1, x, new double[]{ 0.0 , 398.94208093034 , 0.43939128947 , 0.24197072452 , 0.10377687436 , 0.01464498256 });
		checkDensity(0.1, x, new double[]{ 0.0 , 24864.53997 , 0.07464238732 , 0.03009077718 , 0.009447299159 , 8.827199396E-4 });
		checkDensity(2, x, new double[]{ 0.0 , 0.49999975 , 0.38940039154 , 0.30326532986 , 0.18393972059 , 0.04104249931 });
		checkDensity(10, x, new double[]{ 0.0 , 1.302082682E-27 , 6.337896998E-5 , 7.897534632E-4 , 0.007664155024 , 0.06680094289 });
	}

	private void checkDensity(double df, double[] x, double[] expected) {
		org.apache.commons.math.distribution.ChiSquaredDistribution d = new org.apache.commons.math.distribution.ChiSquaredDistributionImpl(df);
		for (int i = 0 ; i < (x.length) ; i++) {
			junit.framework.Assert.assertEquals(expected[i], d.density(x[i]), 1.0E-5);
		}
	}
}

