package org.apache.commons.math.analysis.polynomials;


public final class PolynomialFunctionTest extends junit.framework.TestCase {
	protected double tolerance = 1.0E-12;

	public void testConstants() throws org.apache.commons.math.MathException {
		double[] c = new double[]{ 2.5 };
		org.apache.commons.math.analysis.polynomials.PolynomialFunction f = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(c);
		junit.framework.Assert.assertEquals(f.value(0.0), c[0], tolerance);
		junit.framework.Assert.assertEquals(f.value(-1.0), c[0], tolerance);
		junit.framework.Assert.assertEquals(f.value(-123.5), c[0], tolerance);
		junit.framework.Assert.assertEquals(f.value(3.0), c[0], tolerance);
		junit.framework.Assert.assertEquals(f.value(456.89), c[0], tolerance);
		junit.framework.Assert.assertEquals(f.degree(), 0);
		junit.framework.Assert.assertEquals(f.derivative().value(0), 0, tolerance);
		junit.framework.Assert.assertEquals(f.polynomialDerivative().derivative().value(0), 0, tolerance);
	}

	public void testLinear() throws org.apache.commons.math.MathException {
		double[] c = new double[]{ -1.5 , 3.0 };
		org.apache.commons.math.analysis.polynomials.PolynomialFunction f = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(c);
		junit.framework.Assert.assertEquals(f.value(0.0), c[0], tolerance);
		junit.framework.Assert.assertEquals(-4.5, f.value(-1.0), tolerance);
		junit.framework.Assert.assertEquals(-9.0, f.value(-2.5), tolerance);
		junit.framework.Assert.assertEquals(0.0, f.value(0.5), tolerance);
		junit.framework.Assert.assertEquals(3.0, f.value(1.5), tolerance);
		junit.framework.Assert.assertEquals(7.5, f.value(3.0), tolerance);
		junit.framework.Assert.assertEquals(f.degree(), 1);
		junit.framework.Assert.assertEquals(f.polynomialDerivative().derivative().value(0), 0, tolerance);
	}

	public void testQuadratic() {
		double[] c = new double[]{ -2.0 , -3.0 , 2.0 };
		org.apache.commons.math.analysis.polynomials.PolynomialFunction f = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(c);
		junit.framework.Assert.assertEquals(f.value(0.0), c[0], tolerance);
		junit.framework.Assert.assertEquals(0.0, f.value(-0.5), tolerance);
		junit.framework.Assert.assertEquals(0.0, f.value(2.0), tolerance);
		junit.framework.Assert.assertEquals(-2.0, f.value(1.5), tolerance);
		junit.framework.Assert.assertEquals(7.0, f.value(-1.5), tolerance);
		junit.framework.Assert.assertEquals(265.5312, f.value(12.34), tolerance);
	}

	public void testQuintic() {
		double[] c = new double[]{ 0.0 , 0.0 , 15.0 , -13.0 , -3.0 , 1.0 };
		org.apache.commons.math.analysis.polynomials.PolynomialFunction f = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(c);
		junit.framework.Assert.assertEquals(f.value(0.0), c[0], tolerance);
		junit.framework.Assert.assertEquals(0.0, f.value(5.0), tolerance);
		junit.framework.Assert.assertEquals(0.0, f.value(1.0), tolerance);
		junit.framework.Assert.assertEquals(0.0, f.value(-3.0), tolerance);
		junit.framework.Assert.assertEquals(54.84375, f.value(-1.5), tolerance);
		junit.framework.Assert.assertEquals(-8.06637, f.value(1.3), tolerance);
		junit.framework.Assert.assertEquals(f.degree(), 5);
	}

	public void testfirstDerivativeComparison() throws org.apache.commons.math.MathException {
		double[] f_coeff = new double[]{ 3.0 , 6.0 , -2.0 , 1.0 };
		double[] g_coeff = new double[]{ 6.0 , -4.0 , 3.0 };
		double[] h_coeff = new double[]{ -4.0 , 6.0 };
		org.apache.commons.math.analysis.polynomials.PolynomialFunction f = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(f_coeff);
		org.apache.commons.math.analysis.polynomials.PolynomialFunction g = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(g_coeff);
		org.apache.commons.math.analysis.polynomials.PolynomialFunction h = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(h_coeff);
		junit.framework.Assert.assertEquals(f.derivative().value(0.0), g.value(0.0), tolerance);
		junit.framework.Assert.assertEquals(f.derivative().value(1.0), g.value(1.0), tolerance);
		junit.framework.Assert.assertEquals(f.derivative().value(100.0), g.value(100.0), tolerance);
		junit.framework.Assert.assertEquals(f.derivative().value(4.1), g.value(4.1), tolerance);
		junit.framework.Assert.assertEquals(f.derivative().value(-3.25), g.value(-3.25), tolerance);
		junit.framework.Assert.assertEquals(g.derivative().value(java.lang.Math.PI), h.value(java.lang.Math.PI), tolerance);
		junit.framework.Assert.assertEquals(g.derivative().value(java.lang.Math.E), h.value(java.lang.Math.E), tolerance);
	}

	public void testString() {
		org.apache.commons.math.analysis.polynomials.PolynomialFunction p = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ -5.0 , 3.0 , 1.0 });
		checkPolynomial(p, "-5.0 + 3.0 x + x^2");
		checkPolynomial(new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 0.0 , -2.0 , 3.0 }), "-2.0 x + 3.0 x^2");
		checkPolynomial(new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 1.0 , -2.0 , 3.0 }), "1.0 - 2.0 x + 3.0 x^2");
		checkPolynomial(new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 0.0 , 2.0 , 3.0 }), "2.0 x + 3.0 x^2");
		checkPolynomial(new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 1.0 , 2.0 , 3.0 }), "1.0 + 2.0 x + 3.0 x^2");
		checkPolynomial(new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 1.0 , 0.0 , 3.0 }), "1.0 + 3.0 x^2");
		checkPolynomial(new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 0.0 }), "0");
	}

	public void testAddition() {
		org.apache.commons.math.analysis.polynomials.PolynomialFunction p1 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ -2.0 , 1.0 });
		org.apache.commons.math.analysis.polynomials.PolynomialFunction p2 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 2.0 , -1.0 , 0.0 });
		checkNullPolynomial(p1.add(p2));
		p2 = p1.add(p1);
		checkPolynomial(p2, "-4.0 + 2.0 x");
		p1 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 1.0 , -4.0 , 2.0 });
		p2 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ -1.0 , 3.0 , -2.0 });
		p1 = p1.add(p2);
		junit.framework.Assert.assertEquals(1, p1.degree());
		checkPolynomial(p1, "-x");
	}

	public void testSubtraction() {
		org.apache.commons.math.analysis.polynomials.PolynomialFunction p1 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ -2.0 , 1.0 });
		checkNullPolynomial(p1.subtract(p1));
		org.apache.commons.math.analysis.polynomials.PolynomialFunction p2 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ -2.0 , 6.0 });
		p2 = p2.subtract(p1);
		checkPolynomial(p2, "5.0 x");
		p1 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 1.0 , -4.0 , 2.0 });
		p2 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ -1.0 , 3.0 , 2.0 });
		p1 = p1.subtract(p2);
		junit.framework.Assert.assertEquals(1, p1.degree());
		checkPolynomial(p1, "2.0 - 7.0 x");
	}

	public void testMultiplication() {
		org.apache.commons.math.analysis.polynomials.PolynomialFunction p1 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ -3.0 , 2.0 });
		org.apache.commons.math.analysis.polynomials.PolynomialFunction p2 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 3.0 , 2.0 , 1.0 });
		checkPolynomial(p1.multiply(p2), "-9.0 + x^2 + 2.0 x^3");
		p1 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 0.0 , 1.0 });
		p2 = p1;
		for (int i = 2 ; i < 10 ; ++i) {
			p2 = p2.multiply(p1);
			checkPolynomial(p2, ("x^" + i));
		}
	}

	public void testSerial() {
		org.apache.commons.math.analysis.polynomials.PolynomialFunction p2 = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 3.0 , 2.0 , 1.0 });
		junit.framework.Assert.assertEquals(p2, org.apache.commons.math.TestUtils.serializeAndRecover(p2));
	}

	public void testMath341() throws org.apache.commons.math.MathException {
		double[] f_coeff = new double[]{ 3.0 , 6.0 , -2.0 , 1.0 };
		double[] g_coeff = new double[]{ 6.0 , -4.0 , 3.0 };
		double[] h_coeff = new double[]{ -4.0 , 6.0 };
		org.apache.commons.math.analysis.polynomials.PolynomialFunction f = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(f_coeff);
		org.apache.commons.math.analysis.polynomials.PolynomialFunction g = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(g_coeff);
		org.apache.commons.math.analysis.polynomials.PolynomialFunction h = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(h_coeff);
		junit.framework.Assert.assertEquals(f.derivative().value(0.0), g.value(0.0), tolerance);
		junit.framework.Assert.assertEquals(f.derivative().value(1.0), g.value(1.0), tolerance);
		junit.framework.Assert.assertEquals(f.derivative().value(100.0), g.value(100.0), tolerance);
		junit.framework.Assert.assertEquals(f.derivative().value(4.1), g.value(4.1), tolerance);
		junit.framework.Assert.assertEquals(f.derivative().value(-3.25), g.value(-3.25), tolerance);
		junit.framework.Assert.assertEquals(g.derivative().value(java.lang.Math.PI), h.value(java.lang.Math.PI), tolerance);
		junit.framework.Assert.assertEquals(g.derivative().value(java.lang.Math.E), h.value(java.lang.Math.E), tolerance);
	}

	public void checkPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialFunction p, java.lang.String reference) {
		junit.framework.Assert.assertEquals(reference, p.toString());
	}

	private void checkNullPolynomial(org.apache.commons.math.analysis.polynomials.PolynomialFunction p) {
		for (double coefficient : p.getCoefficients()) {
			junit.framework.Assert.assertEquals(0.0, coefficient, 1.0E-15);
		}
	}
}

