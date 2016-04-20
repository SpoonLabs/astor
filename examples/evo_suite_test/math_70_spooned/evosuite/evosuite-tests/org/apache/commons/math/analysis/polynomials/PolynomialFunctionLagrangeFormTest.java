package org.apache.commons.math.analysis.polynomials;


public final class PolynomialFunctionLagrangeFormTest extends junit.framework.TestCase {
	public void testLinearFunction() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.polynomials.PolynomialFunctionLagrangeForm p;
		double[] c;
		double z;
		double expected;
		double result;
		double tolerance = 1.0E-12;
		double[] x = new double[]{ 0.0 , 3.0 };
		double[] y = new double[]{ -4.0 , 0.5 };
		p = new org.apache.commons.math.analysis.polynomials.PolynomialFunctionLagrangeForm(x , y);
		z = 2.0;
		expected = -1.0;
		result = p.value(z);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		z = 4.5;
		expected = 2.75;
		result = p.value(z);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		z = 6.0;
		expected = 5.0;
		result = p.value(z);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		junit.framework.Assert.assertEquals(1, p.degree());
		c = p.getCoefficients();
		junit.framework.Assert.assertEquals(2, c.length);
		junit.framework.Assert.assertEquals(-4.0, c[0], tolerance);
		junit.framework.Assert.assertEquals(1.5, c[1], tolerance);
	}

	public void testQuadraticFunction() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.polynomials.PolynomialFunctionLagrangeForm p;
		double[] c;
		double z;
		double expected;
		double result;
		double tolerance = 1.0E-12;
		double[] x = new double[]{ 0.0 , -1.0 , 0.5 };
		double[] y = new double[]{ -3.0 , -6.0 , 0.0 };
		p = new org.apache.commons.math.analysis.polynomials.PolynomialFunctionLagrangeForm(x , y);
		z = 1.0;
		expected = 4.0;
		result = p.value(z);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		z = 2.5;
		expected = 22.0;
		result = p.value(z);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		z = -2.0;
		expected = -5.0;
		result = p.value(z);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		junit.framework.Assert.assertEquals(2, p.degree());
		c = p.getCoefficients();
		junit.framework.Assert.assertEquals(3, c.length);
		junit.framework.Assert.assertEquals(-3.0, c[0], tolerance);
		junit.framework.Assert.assertEquals(5.0, c[1], tolerance);
		junit.framework.Assert.assertEquals(2.0, c[2], tolerance);
	}

	public void testQuinticFunction() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.polynomials.PolynomialFunctionLagrangeForm p;
		double[] c;
		double z;
		double expected;
		double result;
		double tolerance = 1.0E-12;
		double[] x = new double[]{ 1.0 , -1.0 , 2.0 , 3.0 , -3.0 , 0.5 };
		double[] y = new double[]{ 0.0 , 0.0 , -24.0 , 0.0 , -144.0 , 2.34375 };
		p = new org.apache.commons.math.analysis.polynomials.PolynomialFunctionLagrangeForm(x , y);
		z = 0.0;
		expected = 0.0;
		result = p.value(z);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		z = -2.0;
		expected = 0.0;
		result = p.value(z);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		z = 4.0;
		expected = 360.0;
		result = p.value(z);
		junit.framework.Assert.assertEquals(expected, result, tolerance);
		junit.framework.Assert.assertEquals(5, p.degree());
		c = p.getCoefficients();
		junit.framework.Assert.assertEquals(6, c.length);
		junit.framework.Assert.assertEquals(0.0, c[0], tolerance);
		junit.framework.Assert.assertEquals(6.0, c[1], tolerance);
		junit.framework.Assert.assertEquals(1.0, c[2], tolerance);
		junit.framework.Assert.assertEquals(-7.0, c[3], tolerance);
		junit.framework.Assert.assertEquals(-1.0, c[4], tolerance);
		junit.framework.Assert.assertEquals(1.0, c[5], tolerance);
	}

	public void testParameters() throws java.lang.Exception {
		try {
			double[] x = new double[]{ 1.0 };
			double[] y = new double[]{ 2.0 };
			new org.apache.commons.math.analysis.polynomials.PolynomialFunctionLagrangeForm(x , y);
			junit.framework.Assert.fail("Expecting IllegalArgumentException - bad input array length");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			double[] x = new double[]{ 1.0 , 2.0 , 3.0 , 4.0 };
			double[] y = new double[]{ 0.0 , -4.0 , -24.0 };
			new org.apache.commons.math.analysis.polynomials.PolynomialFunctionLagrangeForm(x , y);
			junit.framework.Assert.fail("Expecting IllegalArgumentException - mismatch input arrays");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}
}

