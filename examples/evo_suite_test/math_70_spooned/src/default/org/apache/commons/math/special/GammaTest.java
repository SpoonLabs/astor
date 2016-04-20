package org.apache.commons.math.special;


public class GammaTest extends junit.framework.TestCase {
	public GammaTest(java.lang.String name) {
		super(name);
	}

	private void testRegularizedGamma(double expected, double a, double x) {
		try {
			double actualP = org.apache.commons.math.special.Gamma.regularizedGammaP(a, x);
			double actualQ = org.apache.commons.math.special.Gamma.regularizedGammaQ(a, x);
			org.apache.commons.math.TestUtils.assertEquals(expected, actualP, 1.0E-14);
			org.apache.commons.math.TestUtils.assertEquals(actualP, (1.0 - actualQ), 1.0E-14);
		} catch (org.apache.commons.math.MathException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	private void testLogGamma(double expected, double x) {
		double actual = org.apache.commons.math.special.Gamma.logGamma(x);
		org.apache.commons.math.TestUtils.assertEquals(expected, actual, 1.0E-14);
	}

	public void testRegularizedGammaNanPositive() {
		testRegularizedGamma(java.lang.Double.NaN, java.lang.Double.NaN, 1.0);
	}

	public void testRegularizedGammaPositiveNan() {
		testRegularizedGamma(java.lang.Double.NaN, 1.0, java.lang.Double.NaN);
	}

	public void testRegularizedGammaNegativePositive() {
		testRegularizedGamma(java.lang.Double.NaN, -1.5, 1.0);
	}

	public void testRegularizedGammaPositiveNegative() {
		testRegularizedGamma(java.lang.Double.NaN, 1.0, -1.0);
	}

	public void testRegularizedGammaZeroPositive() {
		testRegularizedGamma(java.lang.Double.NaN, 0.0, 1.0);
	}

	public void testRegularizedGammaPositiveZero() {
		testRegularizedGamma(0.0, 1.0, 0.0);
	}

	public void testRegularizedGammaPositivePositive() {
		testRegularizedGamma(0.632120558828558, 1.0, 1.0);
	}

	public void testLogGammaNan() {
		testLogGamma(java.lang.Double.NaN, java.lang.Double.NaN);
	}

	public void testLogGammaNegative() {
		testLogGamma(java.lang.Double.NaN, -1.0);
	}

	public void testLogGammaZero() {
		testLogGamma(java.lang.Double.NaN, 0.0);
	}

	public void testLogGammaPositive() {
		testLogGamma(0.6931471805599457, 3.0);
	}

	public void testDigammaLargeArgs() {
		double eps = 1.0E-8;
		junit.framework.Assert.assertEquals(4.600161852738087, org.apache.commons.math.special.Gamma.digamma(100), eps);
		junit.framework.Assert.assertEquals(3.901989673427892, org.apache.commons.math.special.Gamma.digamma(50), eps);
		junit.framework.Assert.assertEquals(2.970523992242149, org.apache.commons.math.special.Gamma.digamma(20), eps);
		junit.framework.Assert.assertEquals(2.9958363947076467, org.apache.commons.math.special.Gamma.digamma(20.5), eps);
		junit.framework.Assert.assertEquals(2.262214357094148, org.apache.commons.math.special.Gamma.digamma(10.1), eps);
		junit.framework.Assert.assertEquals(2.116858818900438, org.apache.commons.math.special.Gamma.digamma(8.8), eps);
		junit.framework.Assert.assertEquals(1.8727843350984672, org.apache.commons.math.special.Gamma.digamma(7), eps);
		junit.framework.Assert.assertEquals(0.42278433509846713, org.apache.commons.math.special.Gamma.digamma(2), eps);
		junit.framework.Assert.assertEquals(-100.56088545786868, org.apache.commons.math.special.Gamma.digamma(0.01), eps);
		junit.framework.Assert.assertEquals(-4.039039896592188, org.apache.commons.math.special.Gamma.digamma(-0.8), eps);
		junit.framework.Assert.assertEquals(4.200321004140185, org.apache.commons.math.special.Gamma.digamma(-6.3), eps);
	}

	public void testDigammaSmallArgs() {
		double[] expected = new double[]{ -10.423754940411078 , -100.56088545786868 , -1000.5755719318103 , -10000.577051183514 , -100000.57719921568 , -1000000.57721402 , -1.00000005772155E7 , -1.0000000057721564E8 , -1.0000000005772157E9 , -1.0000000000577215E10 , -1.0000000000057721E11 , -1.0000000000005773E12 , -1.0000000000000578E13 , -1.0000000000000058E14 , -1.0000000000000006E15 , -1.0E16 , -1.0E17 , -1.0E18 , -1.0E19 , -1.0E20 , -1.0E21 , -1.0E22 , -9.999999999999999E22 , -1.0E24 , -1.0E25 , -1.0E26 , -1.0E27 , -1.0E28 , -1.0E29 , -1.0E30 };
		for (double n = 1 ; n < 30 ; n++) {
			checkRelativeError(java.lang.String.format("Test %.0f: ", n), expected[((int)(n - 1))], org.apache.commons.math.special.Gamma.digamma(java.lang.Math.pow(10.0, -n)), 1.0E-8);
		}
	}

	public void testTrigamma() {
		double eps = 1.0E-8;
		double[] data = new double[]{ 1.0E-4 , 1.0000000164469369E8 , 0.001 , 1000001.6425331959 , 0.01 , 10001.621213528313 , 0.1 , 101.43329915079276 , 1 , 1.6449340668482264 , 2 , 0.6449340668482264 , 3 , 0.39493406684822646 , 4 , 0.2838229557371153 , 5 , 0.22132295573711533 , 10 , 0.10516633568168575 , 20 , 0.05127082293520312 , 50 , 0.020201333226697125 , 100 , 0.010050166663333571 };
		for (int i = (data.length) - 2 ; i >= 0 ; i -= 2) {
			junit.framework.Assert.assertEquals(java.lang.String.format("trigamma %.0f", data[i]), data[(i + 1)], org.apache.commons.math.special.Gamma.trigamma(data[i]), eps);
		}
	}

	private void checkRelativeError(java.lang.String msg, double expected, double actual, double tolerance) {
		junit.framework.Assert.assertEquals(msg, expected, actual, java.lang.Math.abs((tolerance * actual)));
	}
}

