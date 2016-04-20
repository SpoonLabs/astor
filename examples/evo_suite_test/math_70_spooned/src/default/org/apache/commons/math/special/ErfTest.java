package org.apache.commons.math.special;


public class ErfTest extends junit.framework.TestCase {
	public void testErf0() throws org.apache.commons.math.MathException {
		double actual = org.apache.commons.math.special.Erf.erf(0.0);
		double expected = 0.0;
		junit.framework.Assert.assertEquals(expected, actual, 1.0E-5);
	}

	public void testErf1960() throws org.apache.commons.math.MathException {
		double x = 1.96 / (java.lang.Math.sqrt(2.0));
		double actual = org.apache.commons.math.special.Erf.erf(x);
		double expected = 0.95;
		junit.framework.Assert.assertEquals(expected, actual, 1.0E-5);
		actual = org.apache.commons.math.special.Erf.erf(-x);
		expected = -expected;
		junit.framework.Assert.assertEquals(expected, actual, 1.0E-5);
	}

	public void testErf2576() throws org.apache.commons.math.MathException {
		double x = 2.576 / (java.lang.Math.sqrt(2.0));
		double actual = org.apache.commons.math.special.Erf.erf(x);
		double expected = 0.99;
		junit.framework.Assert.assertEquals(expected, actual, 1.0E-5);
		actual = org.apache.commons.math.special.Erf.erf(-x);
		expected = -expected;
		junit.framework.Assert.assertEquals(expected, actual, 1.0E-5);
	}

	public void testErf2807() throws org.apache.commons.math.MathException {
		double x = 2.807 / (java.lang.Math.sqrt(2.0));
		double actual = org.apache.commons.math.special.Erf.erf(x);
		double expected = 0.995;
		junit.framework.Assert.assertEquals(expected, actual, 1.0E-5);
		actual = org.apache.commons.math.special.Erf.erf(-x);
		expected = -expected;
		junit.framework.Assert.assertEquals(expected, actual, 1.0E-5);
	}

	public void testErf3291() throws org.apache.commons.math.MathException {
		double x = 3.291 / (java.lang.Math.sqrt(2.0));
		double actual = org.apache.commons.math.special.Erf.erf(x);
		double expected = 0.999;
		junit.framework.Assert.assertEquals(expected, actual, 1.0E-5);
		actual = org.apache.commons.math.special.Erf.erf(-x);
		expected = -expected;
		junit.framework.Assert.assertEquals(expected, actual, 1.0E-5);
	}

	public void testLargeValues() throws java.lang.Exception {
		for (int i = 1 ; i < 200 ; i++) {
			double result = org.apache.commons.math.special.Erf.erf(i);
			junit.framework.Assert.assertFalse(java.lang.Double.isNaN(result));
			junit.framework.Assert.assertTrue(((result > 0) && (result <= 1)));
		}
	}
}

