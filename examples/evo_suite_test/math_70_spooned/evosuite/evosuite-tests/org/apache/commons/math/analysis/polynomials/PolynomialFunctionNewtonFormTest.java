package org.apache.commons.math.analysis.polynomials;


public final class PolynomialFunctionNewtonFormTest extends junit.framework.TestCase {
	public void testLinearFunction() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.polynomials.PolynomialFunctionNewtonForm p;
		double[] coefficients;
		double z;
		double expected;
		double result;
		double tolerance = 1.0E-12;
		double[] a = new double[]{ 2.0 , 1.5 };
		double[] c = new double[]{ 4.0 };
		p = new org.apache.commons.math.analysis.polynomials.PolynomialFunctionNewtonForm(a , c);
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
		coefficients = p.getCoefficients();
		junit.framework.Assert.assertEquals(2, coefficients.length);
		junit.framework.Assert.assertEquals(-4.0, coefficients[0], tolerance);
		junit.framework.Assert.assertEquals(1.5, coefficients[1], tolerance);
	}

	public void testQuadraticFunction() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.polynomials.PolynomialFunctionNewtonForm p;
		double[] coefficients;
		double z;
		double expected;
		double result;
		double tolerance = 1.0E-12;
		double[] a = new double[]{ 4.0 , 3.0 , 2.0 };
		double[] c = new double[]{ 1.0 , -2.0 };
		p = new org.apache.commons.math.analysis.polynomials.PolynomialFunctionNewtonForm(a , c);
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
		coefficients = p.getCoefficients();
		junit.framework.Assert.assertEquals(3, coefficients.length);
		junit.framework.Assert.assertEquals(-3.0, coefficients[0], tolerance);
		junit.framework.Assert.assertEquals(5.0, coefficients[1], tolerance);
		junit.framework.Assert.assertEquals(2.0, coefficients[2], tolerance);
	}

	public void testQuinticFunction() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.polynomials.PolynomialFunctionNewtonForm p;
		double[] coefficients;
		double z;
		double expected;
		double result;
		double tolerance = 1.0E-12;
		double[] a = new double[]{ 0.0 , 6.0 , -6.0 , -6.0 , 1.0 , 1.0 };
		double[] c = new double[]{ 0.0 , 0.0 , 1.0 , -1.0 , 2.0 };
		p = new org.apache.commons.math.analysis.polynomials.PolynomialFunctionNewtonForm(a , c);
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
		coefficients = p.getCoefficients();
		junit.framework.Assert.assertEquals(6, coefficients.length);
		junit.framework.Assert.assertEquals(0.0, coefficients[0], tolerance);
		junit.framework.Assert.assertEquals(6.0, coefficients[1], tolerance);
		junit.framework.Assert.assertEquals(1.0, coefficients[2], tolerance);
		junit.framework.Assert.assertEquals(-7.0, coefficients[3], tolerance);
		junit.framework.Assert.assertEquals(-1.0, coefficients[4], tolerance);
		junit.framework.Assert.assertEquals(1.0, coefficients[5], tolerance);
	}

	public void testParameters() throws java.lang.Exception {
		try {
			double[] a = new double[]{ 1.0 };
			double[] c = new double[]{ 2.0 };
			new org.apache.commons.math.analysis.polynomials.PolynomialFunctionNewtonForm(a , c);
			junit.framework.Assert.fail("Expecting IllegalArgumentException - bad input array length");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			double[] a = new double[]{ 1.0 , 2.0 , 3.0 , 4.0 };
			double[] c = new double[]{ 4.0 , 3.0 , 2.0 , 1.0 };
			new org.apache.commons.math.analysis.polynomials.PolynomialFunctionNewtonForm(a , c);
			junit.framework.Assert.fail("Expecting IllegalArgumentException - mismatch input arrays");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}
}

