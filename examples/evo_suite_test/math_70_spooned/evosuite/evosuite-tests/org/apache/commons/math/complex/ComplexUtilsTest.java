package org.apache.commons.math.complex;


public class ComplexUtilsTest extends junit.framework.TestCase {
	private double inf = java.lang.Double.POSITIVE_INFINITY;

	private double negInf = java.lang.Double.NEGATIVE_INFINITY;

	private double nan = java.lang.Double.NaN;

	private double pi = java.lang.Math.PI;

	private org.apache.commons.math.complex.Complex negInfInf = new org.apache.commons.math.complex.Complex(negInf , inf);

	private org.apache.commons.math.complex.Complex infNegInf = new org.apache.commons.math.complex.Complex(inf , negInf);

	private org.apache.commons.math.complex.Complex infInf = new org.apache.commons.math.complex.Complex(inf , inf);

	private org.apache.commons.math.complex.Complex negInfNegInf = new org.apache.commons.math.complex.Complex(negInf , negInf);

	private org.apache.commons.math.complex.Complex infNaN = new org.apache.commons.math.complex.Complex(inf , nan);

	public void testPolar2Complex() {
		org.apache.commons.math.TestUtils.assertEquals(org.apache.commons.math.complex.Complex.ONE, org.apache.commons.math.complex.ComplexUtils.polar2Complex(1, 0), 1.0E-11);
		org.apache.commons.math.TestUtils.assertEquals(org.apache.commons.math.complex.Complex.ZERO, org.apache.commons.math.complex.ComplexUtils.polar2Complex(0, 1), 1.0E-11);
		org.apache.commons.math.TestUtils.assertEquals(org.apache.commons.math.complex.Complex.ZERO, org.apache.commons.math.complex.ComplexUtils.polar2Complex(0, -1), 1.0E-11);
		org.apache.commons.math.TestUtils.assertEquals(org.apache.commons.math.complex.Complex.I, org.apache.commons.math.complex.ComplexUtils.polar2Complex(1, ((pi) / 2)), 1.0E-11);
		org.apache.commons.math.TestUtils.assertEquals(org.apache.commons.math.complex.Complex.I.negate(), org.apache.commons.math.complex.ComplexUtils.polar2Complex(1, ((-(pi)) / 2)), 1.0E-11);
		double r = 0;
		for (int i = 0 ; i < 5 ; i++) {
			r += i;
			double theta = 0;
			for (int j = 0 ; j < 20 ; j++) {
				theta += (pi) / 6;
				org.apache.commons.math.TestUtils.assertEquals(altPolar(r, theta), org.apache.commons.math.complex.ComplexUtils.polar2Complex(r, theta), 1.0E-11);
			}
			theta = (-2) * (pi);
			for (int j = 0 ; j < 20 ; j++) {
				theta -= (pi) / 6;
				org.apache.commons.math.TestUtils.assertEquals(altPolar(r, theta), org.apache.commons.math.complex.ComplexUtils.polar2Complex(r, theta), 1.0E-11);
			}
		}
	}

	protected org.apache.commons.math.complex.Complex altPolar(double r, double theta) {
		return org.apache.commons.math.complex.Complex.I.multiply(new org.apache.commons.math.complex.Complex(theta , 0)).exp().multiply(new org.apache.commons.math.complex.Complex(r , 0));
	}

	public void testPolar2ComplexIllegalModulus() {
		try {
			org.apache.commons.math.complex.ComplexUtils.polar2Complex(-1, 0);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testPolar2ComplexNaN() {
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, org.apache.commons.math.complex.ComplexUtils.polar2Complex(nan, 1));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, org.apache.commons.math.complex.ComplexUtils.polar2Complex(1, nan));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, org.apache.commons.math.complex.ComplexUtils.polar2Complex(nan, nan));
	}

	public void testPolar2ComplexInf() {
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, org.apache.commons.math.complex.ComplexUtils.polar2Complex(1, inf));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, org.apache.commons.math.complex.ComplexUtils.polar2Complex(1, negInf));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, org.apache.commons.math.complex.ComplexUtils.polar2Complex(inf, inf));
		org.apache.commons.math.TestUtils.assertSame(org.apache.commons.math.complex.Complex.NaN, org.apache.commons.math.complex.ComplexUtils.polar2Complex(inf, negInf));
		org.apache.commons.math.TestUtils.assertSame(infInf, org.apache.commons.math.complex.ComplexUtils.polar2Complex(inf, ((pi) / 4)));
		org.apache.commons.math.TestUtils.assertSame(infNaN, org.apache.commons.math.complex.ComplexUtils.polar2Complex(inf, 0));
		org.apache.commons.math.TestUtils.assertSame(infNegInf, org.apache.commons.math.complex.ComplexUtils.polar2Complex(inf, ((-(pi)) / 4)));
		org.apache.commons.math.TestUtils.assertSame(negInfInf, org.apache.commons.math.complex.ComplexUtils.polar2Complex(inf, ((3 * (pi)) / 4)));
		org.apache.commons.math.TestUtils.assertSame(negInfNegInf, org.apache.commons.math.complex.ComplexUtils.polar2Complex(inf, ((5 * (pi)) / 4)));
	}
}

