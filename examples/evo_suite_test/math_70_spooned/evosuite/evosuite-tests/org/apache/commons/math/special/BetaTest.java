package org.apache.commons.math.special;


public class BetaTest extends junit.framework.TestCase {
	public BetaTest(java.lang.String name) {
		super(name);
	}

	private void testRegularizedBeta(double expected, double x, double a, double b) {
		try {
			double actual = org.apache.commons.math.special.Beta.regularizedBeta(x, a, b);
			org.apache.commons.math.TestUtils.assertEquals(expected, actual, 1.0E-14);
		} catch (org.apache.commons.math.MathException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	private void testLogBeta(double expected, double a, double b) {
		double actual = org.apache.commons.math.special.Beta.logBeta(a, b);
		org.apache.commons.math.TestUtils.assertEquals(expected, actual, 1.0E-14);
	}

	public void testRegularizedBetaNanPositivePositive() {
		testRegularizedBeta(java.lang.Double.NaN, java.lang.Double.NaN, 1.0, 1.0);
	}

	public void testRegularizedBetaPositiveNanPositive() {
		testRegularizedBeta(java.lang.Double.NaN, 0.5, java.lang.Double.NaN, 1.0);
	}

	public void testRegularizedBetaPositivePositiveNan() {
		testRegularizedBeta(java.lang.Double.NaN, 0.5, 1.0, java.lang.Double.NaN);
	}

	public void testRegularizedBetaNegativePositivePositive() {
		testRegularizedBeta(java.lang.Double.NaN, -0.5, 1.0, 2.0);
	}

	public void testRegularizedBetaPositiveNegativePositive() {
		testRegularizedBeta(java.lang.Double.NaN, 0.5, -1.0, 2.0);
	}

	public void testRegularizedBetaPositivePositiveNegative() {
		testRegularizedBeta(java.lang.Double.NaN, 0.5, 1.0, -2.0);
	}

	public void testRegularizedBetaZeroPositivePositive() {
		testRegularizedBeta(0.0, 0.0, 1.0, 2.0);
	}

	public void testRegularizedBetaPositiveZeroPositive() {
		testRegularizedBeta(java.lang.Double.NaN, 0.5, 0.0, 2.0);
	}

	public void testRegularizedBetaPositivePositiveZero() {
		testRegularizedBeta(java.lang.Double.NaN, 0.5, 1.0, 0.0);
	}

	public void testRegularizedBetaPositivePositivePositive() {
		testRegularizedBeta(0.75, 0.5, 1.0, 2.0);
	}

	public void testLogBetaNanPositive() {
		testLogBeta(java.lang.Double.NaN, java.lang.Double.NaN, 2.0);
	}

	public void testLogBetaPositiveNan() {
		testLogBeta(java.lang.Double.NaN, 1.0, java.lang.Double.NaN);
	}

	public void testLogBetaNegativePositive() {
		testLogBeta(java.lang.Double.NaN, -1.0, 2.0);
	}

	public void testLogBetaPositiveNegative() {
		testLogBeta(java.lang.Double.NaN, 1.0, -2.0);
	}

	public void testLogBetaZeroPositive() {
		testLogBeta(java.lang.Double.NaN, 0.0, 2.0);
	}

	public void testLogBetaPositiveZero() {
		testLogBeta(java.lang.Double.NaN, 1.0, 0.0);
	}

	public void testLogBetaPositivePositive() {
		testLogBeta(-0.693147180559945, 1.0, 2.0);
	}
}

