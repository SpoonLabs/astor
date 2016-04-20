package org.apache.commons.math.distribution;


public class WeibullDistributionTest extends org.apache.commons.math.distribution.ContinuousDistributionAbstractTest {
	public WeibullDistributionTest(java.lang.String arg0) {
		super(arg0);
	}

	@java.lang.Override
	public org.apache.commons.math.distribution.WeibullDistribution makeDistribution() {
		return new org.apache.commons.math.distribution.WeibullDistributionImpl(1.2 , 2.1);
	}

	@java.lang.Override
	public double[] makeCumulativeTestPoints() {
		return new double[]{ 0.00664355180993 , 0.0454328283309 , 0.0981162737374 , 0.176713524579 , 0.321946865392 , 10.5115496887 , 7.4976304671 , 6.23205600701 , 5.23968436955 , 4.2079028257 };
	}

	@java.lang.Override
	public double[] makeCumulativeTestValues() {
		return new double[]{ 0.001 , 0.01 , 0.025 , 0.05 , 0.1 , 0.999 , 0.99 , 0.975 , 0.95 , 0.9 };
	}

	@java.lang.Override
	public double[] makeDensityTestValues() {
		return new double[]{ 0.180535929306 , 0.262801138133 , 0.301905425199 , 0.330899152971 , 0.353441418887 , 7.88590320203E-4 , 0.00737060094841 , 0.0177576041516 , 0.0343043442574 , 0.065664589369 };
	}

	public void testInverseCumulativeProbabilityExtremes() throws java.lang.Exception {
		setInverseCumulativeTestPoints(new double[]{ 0.0 , 1.0 });
		setInverseCumulativeTestValues(new double[]{ 0.0 , java.lang.Double.POSITIVE_INFINITY });
		verifyInverseCumulativeProbabilities();
	}

	public void testAlpha() {
		org.apache.commons.math.distribution.WeibullDistribution distribution = ((org.apache.commons.math.distribution.WeibullDistribution)(getDistribution()));
		double expected = java.lang.Math.random();
		distribution.setShape(expected);
		junit.framework.Assert.assertEquals(expected, distribution.getShape(), 0.0);
	}

	public void testBeta() {
		org.apache.commons.math.distribution.WeibullDistribution distribution = ((org.apache.commons.math.distribution.WeibullDistribution)(getDistribution()));
		double expected = java.lang.Math.random();
		distribution.setScale(expected);
		junit.framework.Assert.assertEquals(expected, distribution.getScale(), 0.0);
	}

	public void testSetAlpha() {
		org.apache.commons.math.distribution.WeibullDistribution distribution = ((org.apache.commons.math.distribution.WeibullDistribution)(getDistribution()));
		try {
			distribution.setShape(0.0);
			junit.framework.Assert.fail("Can not have 0.0 alpha.");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			distribution.setShape(-1.0);
			junit.framework.Assert.fail("Can not have negative alpha.");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testSetBeta() {
		org.apache.commons.math.distribution.WeibullDistribution distribution = ((org.apache.commons.math.distribution.WeibullDistribution)(getDistribution()));
		try {
			distribution.setScale(0.0);
			junit.framework.Assert.fail("Can not have 0.0 beta.");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			distribution.setScale(-1.0);
			junit.framework.Assert.fail("Can not have negative beta.");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}
}

