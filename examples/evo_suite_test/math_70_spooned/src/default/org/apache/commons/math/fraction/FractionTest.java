package org.apache.commons.math.fraction;


public class FractionTest extends junit.framework.TestCase {
	private void assertFraction(int expectedNumerator, int expectedDenominator, org.apache.commons.math.fraction.Fraction actual) {
		junit.framework.Assert.assertEquals(expectedNumerator, actual.getNumerator());
		junit.framework.Assert.assertEquals(expectedDenominator, actual.getDenominator());
	}

	public void testConstructor() {
		assertFraction(0, 1, new org.apache.commons.math.fraction.Fraction(0 , 1));
		assertFraction(0, 1, new org.apache.commons.math.fraction.Fraction(0 , 2));
		assertFraction(0, 1, new org.apache.commons.math.fraction.Fraction(0 , -1));
		assertFraction(1, 2, new org.apache.commons.math.fraction.Fraction(1 , 2));
		assertFraction(1, 2, new org.apache.commons.math.fraction.Fraction(2 , 4));
		assertFraction(-1, 2, new org.apache.commons.math.fraction.Fraction(-1 , 2));
		assertFraction(-1, 2, new org.apache.commons.math.fraction.Fraction(1 , -2));
		assertFraction(-1, 2, new org.apache.commons.math.fraction.Fraction(-2 , 4));
		assertFraction(-1, 2, new org.apache.commons.math.fraction.Fraction(2 , -4));
		try {
			new org.apache.commons.math.fraction.Fraction(java.lang.Integer.MIN_VALUE , -1);
			junit.framework.Assert.fail();
		} catch (java.lang.ArithmeticException ex) {
		}
		try {
			new org.apache.commons.math.fraction.Fraction(1 , java.lang.Integer.MIN_VALUE);
			junit.framework.Assert.fail();
		} catch (java.lang.ArithmeticException ex) {
		}
		try {
			assertFraction(0, 1, new org.apache.commons.math.fraction.Fraction(1.0E-14));
			assertFraction(2, 5, new org.apache.commons.math.fraction.Fraction(0.40000000000001));
			assertFraction(15, 1, new org.apache.commons.math.fraction.Fraction(15.0000000000001));
		} catch (org.apache.commons.math.ConvergenceException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testGoldenRatio() {
		try {
			new org.apache.commons.math.fraction.Fraction(((1 + (java.lang.Math.sqrt(5))) / 2) , 1.0E-12 , 25);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.ConvergenceException ce) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testDoubleConstructor() throws org.apache.commons.math.ConvergenceException {
		assertFraction(1, 2, new org.apache.commons.math.fraction.Fraction((((double)(1)) / ((double)(2)))));
		assertFraction(1, 3, new org.apache.commons.math.fraction.Fraction((((double)(1)) / ((double)(3)))));
		assertFraction(2, 3, new org.apache.commons.math.fraction.Fraction((((double)(2)) / ((double)(3)))));
		assertFraction(1, 4, new org.apache.commons.math.fraction.Fraction((((double)(1)) / ((double)(4)))));
		assertFraction(3, 4, new org.apache.commons.math.fraction.Fraction((((double)(3)) / ((double)(4)))));
		assertFraction(1, 5, new org.apache.commons.math.fraction.Fraction((((double)(1)) / ((double)(5)))));
		assertFraction(2, 5, new org.apache.commons.math.fraction.Fraction((((double)(2)) / ((double)(5)))));
		assertFraction(3, 5, new org.apache.commons.math.fraction.Fraction((((double)(3)) / ((double)(5)))));
		assertFraction(4, 5, new org.apache.commons.math.fraction.Fraction((((double)(4)) / ((double)(5)))));
		assertFraction(1, 6, new org.apache.commons.math.fraction.Fraction((((double)(1)) / ((double)(6)))));
		assertFraction(5, 6, new org.apache.commons.math.fraction.Fraction((((double)(5)) / ((double)(6)))));
		assertFraction(1, 7, new org.apache.commons.math.fraction.Fraction((((double)(1)) / ((double)(7)))));
		assertFraction(2, 7, new org.apache.commons.math.fraction.Fraction((((double)(2)) / ((double)(7)))));
		assertFraction(3, 7, new org.apache.commons.math.fraction.Fraction((((double)(3)) / ((double)(7)))));
		assertFraction(4, 7, new org.apache.commons.math.fraction.Fraction((((double)(4)) / ((double)(7)))));
		assertFraction(5, 7, new org.apache.commons.math.fraction.Fraction((((double)(5)) / ((double)(7)))));
		assertFraction(6, 7, new org.apache.commons.math.fraction.Fraction((((double)(6)) / ((double)(7)))));
		assertFraction(1, 8, new org.apache.commons.math.fraction.Fraction((((double)(1)) / ((double)(8)))));
		assertFraction(3, 8, new org.apache.commons.math.fraction.Fraction((((double)(3)) / ((double)(8)))));
		assertFraction(5, 8, new org.apache.commons.math.fraction.Fraction((((double)(5)) / ((double)(8)))));
		assertFraction(7, 8, new org.apache.commons.math.fraction.Fraction((((double)(7)) / ((double)(8)))));
		assertFraction(1, 9, new org.apache.commons.math.fraction.Fraction((((double)(1)) / ((double)(9)))));
		assertFraction(2, 9, new org.apache.commons.math.fraction.Fraction((((double)(2)) / ((double)(9)))));
		assertFraction(4, 9, new org.apache.commons.math.fraction.Fraction((((double)(4)) / ((double)(9)))));
		assertFraction(5, 9, new org.apache.commons.math.fraction.Fraction((((double)(5)) / ((double)(9)))));
		assertFraction(7, 9, new org.apache.commons.math.fraction.Fraction((((double)(7)) / ((double)(9)))));
		assertFraction(8, 9, new org.apache.commons.math.fraction.Fraction((((double)(8)) / ((double)(9)))));
		assertFraction(1, 10, new org.apache.commons.math.fraction.Fraction((((double)(1)) / ((double)(10)))));
		assertFraction(3, 10, new org.apache.commons.math.fraction.Fraction((((double)(3)) / ((double)(10)))));
		assertFraction(7, 10, new org.apache.commons.math.fraction.Fraction((((double)(7)) / ((double)(10)))));
		assertFraction(9, 10, new org.apache.commons.math.fraction.Fraction((((double)(9)) / ((double)(10)))));
		assertFraction(1, 11, new org.apache.commons.math.fraction.Fraction((((double)(1)) / ((double)(11)))));
		assertFraction(2, 11, new org.apache.commons.math.fraction.Fraction((((double)(2)) / ((double)(11)))));
		assertFraction(3, 11, new org.apache.commons.math.fraction.Fraction((((double)(3)) / ((double)(11)))));
		assertFraction(4, 11, new org.apache.commons.math.fraction.Fraction((((double)(4)) / ((double)(11)))));
		assertFraction(5, 11, new org.apache.commons.math.fraction.Fraction((((double)(5)) / ((double)(11)))));
		assertFraction(6, 11, new org.apache.commons.math.fraction.Fraction((((double)(6)) / ((double)(11)))));
		assertFraction(7, 11, new org.apache.commons.math.fraction.Fraction((((double)(7)) / ((double)(11)))));
		assertFraction(8, 11, new org.apache.commons.math.fraction.Fraction((((double)(8)) / ((double)(11)))));
		assertFraction(9, 11, new org.apache.commons.math.fraction.Fraction((((double)(9)) / ((double)(11)))));
		assertFraction(10, 11, new org.apache.commons.math.fraction.Fraction((((double)(10)) / ((double)(11)))));
	}

	public void testDigitLimitConstructor() throws org.apache.commons.math.ConvergenceException {
		assertFraction(2, 5, new org.apache.commons.math.fraction.Fraction(0.4 , 9));
		assertFraction(2, 5, new org.apache.commons.math.fraction.Fraction(0.4 , 99));
		assertFraction(2, 5, new org.apache.commons.math.fraction.Fraction(0.4 , 999));
		assertFraction(3, 5, new org.apache.commons.math.fraction.Fraction(0.6152 , 9));
		assertFraction(8, 13, new org.apache.commons.math.fraction.Fraction(0.6152 , 99));
		assertFraction(510, 829, new org.apache.commons.math.fraction.Fraction(0.6152 , 999));
		assertFraction(769, 1250, new org.apache.commons.math.fraction.Fraction(0.6152 , 9999));
	}

	public void testIntegerOverflow() {
		checkIntegerOverflow(0.7500000000145519);
		checkIntegerOverflow(1.0E10);
	}

	private void checkIntegerOverflow(double a) {
		try {
			new org.apache.commons.math.fraction.Fraction(a , 1.0E-12 , 1000);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.ConvergenceException ce) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testEpsilonLimitConstructor() throws org.apache.commons.math.ConvergenceException {
		assertFraction(2, 5, new org.apache.commons.math.fraction.Fraction(0.4 , 1.0E-5 , 100));
		assertFraction(3, 5, new org.apache.commons.math.fraction.Fraction(0.6152 , 0.02 , 100));
		assertFraction(8, 13, new org.apache.commons.math.fraction.Fraction(0.6152 , 0.001 , 100));
		assertFraction(251, 408, new org.apache.commons.math.fraction.Fraction(0.6152 , 1.0E-4 , 100));
		assertFraction(251, 408, new org.apache.commons.math.fraction.Fraction(0.6152 , 1.0E-5 , 100));
		assertFraction(510, 829, new org.apache.commons.math.fraction.Fraction(0.6152 , 1.0E-6 , 100));
		assertFraction(769, 1250, new org.apache.commons.math.fraction.Fraction(0.6152 , 1.0E-7 , 100));
	}

	public void testCompareTo() {
		org.apache.commons.math.fraction.Fraction first = new org.apache.commons.math.fraction.Fraction(1 , 2);
		org.apache.commons.math.fraction.Fraction second = new org.apache.commons.math.fraction.Fraction(1 , 3);
		org.apache.commons.math.fraction.Fraction third = new org.apache.commons.math.fraction.Fraction(1 , 2);
		junit.framework.Assert.assertEquals(0, first.compareTo(first));
		junit.framework.Assert.assertEquals(0, first.compareTo(third));
		junit.framework.Assert.assertEquals(1, first.compareTo(second));
		junit.framework.Assert.assertEquals(-1, second.compareTo(first));
		org.apache.commons.math.fraction.Fraction pi1 = new org.apache.commons.math.fraction.Fraction(1068966896 , 340262731);
		org.apache.commons.math.fraction.Fraction pi2 = new org.apache.commons.math.fraction.Fraction(411557987 , 131002976);
		junit.framework.Assert.assertEquals(-1, pi1.compareTo(pi2));
		junit.framework.Assert.assertEquals(1, pi2.compareTo(pi1));
		junit.framework.Assert.assertEquals(0.0, ((pi1.doubleValue()) - (pi2.doubleValue())), 1.0E-20);
	}

	public void testDoubleValue() {
		org.apache.commons.math.fraction.Fraction first = new org.apache.commons.math.fraction.Fraction(1 , 2);
		org.apache.commons.math.fraction.Fraction second = new org.apache.commons.math.fraction.Fraction(1 , 3);
		junit.framework.Assert.assertEquals(0.5, first.doubleValue(), 0.0);
		junit.framework.Assert.assertEquals((1.0 / 3.0), second.doubleValue(), 0.0);
	}

	public void testFloatValue() {
		org.apache.commons.math.fraction.Fraction first = new org.apache.commons.math.fraction.Fraction(1 , 2);
		org.apache.commons.math.fraction.Fraction second = new org.apache.commons.math.fraction.Fraction(1 , 3);
		junit.framework.Assert.assertEquals(0.5F, first.floatValue(), 0.0F);
		junit.framework.Assert.assertEquals(((float)(1.0 / 3.0)), second.floatValue(), 0.0F);
	}

	public void testIntValue() {
		org.apache.commons.math.fraction.Fraction first = new org.apache.commons.math.fraction.Fraction(1 , 2);
		org.apache.commons.math.fraction.Fraction second = new org.apache.commons.math.fraction.Fraction(3 , 2);
		junit.framework.Assert.assertEquals(0, first.intValue());
		junit.framework.Assert.assertEquals(1, second.intValue());
	}

	public void testLongValue() {
		org.apache.commons.math.fraction.Fraction first = new org.apache.commons.math.fraction.Fraction(1 , 2);
		org.apache.commons.math.fraction.Fraction second = new org.apache.commons.math.fraction.Fraction(3 , 2);
		junit.framework.Assert.assertEquals(0L, first.longValue());
		junit.framework.Assert.assertEquals(1L, second.longValue());
	}

	public void testConstructorDouble() {
		try {
			assertFraction(1, 2, new org.apache.commons.math.fraction.Fraction(0.5));
			assertFraction(1, 3, new org.apache.commons.math.fraction.Fraction((1.0 / 3.0)));
			assertFraction(17, 100, new org.apache.commons.math.fraction.Fraction((17.0 / 100.0)));
			assertFraction(317, 100, new org.apache.commons.math.fraction.Fraction((317.0 / 100.0)));
			assertFraction(-1, 2, new org.apache.commons.math.fraction.Fraction(-0.5));
			assertFraction(-1, 3, new org.apache.commons.math.fraction.Fraction(((-1.0) / 3.0)));
			assertFraction(-17, 100, new org.apache.commons.math.fraction.Fraction((17.0 / (-100.0))));
			assertFraction(-317, 100, new org.apache.commons.math.fraction.Fraction(((-317.0) / 100.0)));
		} catch (org.apache.commons.math.ConvergenceException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testAbs() {
		org.apache.commons.math.fraction.Fraction a = new org.apache.commons.math.fraction.Fraction(10 , 21);
		org.apache.commons.math.fraction.Fraction b = new org.apache.commons.math.fraction.Fraction(-10 , 21);
		org.apache.commons.math.fraction.Fraction c = new org.apache.commons.math.fraction.Fraction(10 , -21);
		assertFraction(10, 21, a.abs());
		assertFraction(10, 21, b.abs());
		assertFraction(10, 21, c.abs());
	}

	public void testReciprocal() {
		org.apache.commons.math.fraction.Fraction f = null;
		f = new org.apache.commons.math.fraction.Fraction(50 , 75);
		f = f.reciprocal();
		junit.framework.Assert.assertEquals(3, f.getNumerator());
		junit.framework.Assert.assertEquals(2, f.getDenominator());
		f = new org.apache.commons.math.fraction.Fraction(4 , 3);
		f = f.reciprocal();
		junit.framework.Assert.assertEquals(3, f.getNumerator());
		junit.framework.Assert.assertEquals(4, f.getDenominator());
		f = new org.apache.commons.math.fraction.Fraction(-15 , 47);
		f = f.reciprocal();
		junit.framework.Assert.assertEquals(-47, f.getNumerator());
		junit.framework.Assert.assertEquals(15, f.getDenominator());
		f = new org.apache.commons.math.fraction.Fraction(0 , 3);
		try {
			f = f.reciprocal();
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
		f = new org.apache.commons.math.fraction.Fraction(java.lang.Integer.MAX_VALUE , 1);
		f = f.reciprocal();
		junit.framework.Assert.assertEquals(1, f.getNumerator());
		junit.framework.Assert.assertEquals(java.lang.Integer.MAX_VALUE, f.getDenominator());
	}

	public void testNegate() {
		org.apache.commons.math.fraction.Fraction f = null;
		f = new org.apache.commons.math.fraction.Fraction(50 , 75);
		f = f.negate();
		junit.framework.Assert.assertEquals(-2, f.getNumerator());
		junit.framework.Assert.assertEquals(3, f.getDenominator());
		f = new org.apache.commons.math.fraction.Fraction(-50 , 75);
		f = f.negate();
		junit.framework.Assert.assertEquals(2, f.getNumerator());
		junit.framework.Assert.assertEquals(3, f.getDenominator());
		f = new org.apache.commons.math.fraction.Fraction(((java.lang.Integer.MAX_VALUE) - 1) , java.lang.Integer.MAX_VALUE);
		f = f.negate();
		junit.framework.Assert.assertEquals(((java.lang.Integer.MIN_VALUE) + 2), f.getNumerator());
		junit.framework.Assert.assertEquals(java.lang.Integer.MAX_VALUE, f.getDenominator());
		f = new org.apache.commons.math.fraction.Fraction(java.lang.Integer.MIN_VALUE , 1);
		try {
			f = f.negate();
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
	}

	public void testAdd() {
		org.apache.commons.math.fraction.Fraction a = new org.apache.commons.math.fraction.Fraction(1 , 2);
		org.apache.commons.math.fraction.Fraction b = new org.apache.commons.math.fraction.Fraction(2 , 3);
		assertFraction(1, 1, a.add(a));
		assertFraction(7, 6, a.add(b));
		assertFraction(7, 6, b.add(a));
		assertFraction(4, 3, b.add(b));
		org.apache.commons.math.fraction.Fraction f1 = new org.apache.commons.math.fraction.Fraction(((java.lang.Integer.MAX_VALUE) - 1) , 1);
		org.apache.commons.math.fraction.Fraction f2 = org.apache.commons.math.fraction.Fraction.ONE;
		org.apache.commons.math.fraction.Fraction f = f1.add(f2);
		junit.framework.Assert.assertEquals(java.lang.Integer.MAX_VALUE, f.getNumerator());
		junit.framework.Assert.assertEquals(1, f.getDenominator());
		f = f1.add(1);
		junit.framework.Assert.assertEquals(java.lang.Integer.MAX_VALUE, f.getNumerator());
		junit.framework.Assert.assertEquals(1, f.getDenominator());
		f1 = new org.apache.commons.math.fraction.Fraction(-1 , (((13 * 13) * 2) * 2));
		f2 = new org.apache.commons.math.fraction.Fraction(-2 , ((13 * 17) * 2));
		f = f1.add(f2);
		junit.framework.Assert.assertEquals(((((13 * 13) * 17) * 2) * 2), f.getDenominator());
		junit.framework.Assert.assertEquals(((-17) - ((2 * 13) * 2)), f.getNumerator());
		try {
			f.add(null);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		f1 = new org.apache.commons.math.fraction.Fraction(1 , (32768 * 3));
		f2 = new org.apache.commons.math.fraction.Fraction(1 , 59049);
		f = f1.add(f2);
		junit.framework.Assert.assertEquals(52451, f.getNumerator());
		junit.framework.Assert.assertEquals(1934917632, f.getDenominator());
		f1 = new org.apache.commons.math.fraction.Fraction(java.lang.Integer.MIN_VALUE , 3);
		f2 = new org.apache.commons.math.fraction.Fraction(1 , 3);
		f = f1.add(f2);
		junit.framework.Assert.assertEquals(((java.lang.Integer.MIN_VALUE) + 1), f.getNumerator());
		junit.framework.Assert.assertEquals(3, f.getDenominator());
		f1 = new org.apache.commons.math.fraction.Fraction(((java.lang.Integer.MAX_VALUE) - 1) , 1);
		f2 = org.apache.commons.math.fraction.Fraction.ONE;
		f = f1.add(f2);
		junit.framework.Assert.assertEquals(java.lang.Integer.MAX_VALUE, f.getNumerator());
		junit.framework.Assert.assertEquals(1, f.getDenominator());
		try {
			f = f.add(org.apache.commons.math.fraction.Fraction.ONE);
			junit.framework.Assert.fail(("expecting ArithmeticException but got: " + (f.toString())));
		} catch (java.lang.ArithmeticException ex) {
		}
		f1 = new org.apache.commons.math.fraction.Fraction(java.lang.Integer.MIN_VALUE , 5);
		f2 = new org.apache.commons.math.fraction.Fraction(-1 , 5);
		try {
			f = f1.add(f2);
			junit.framework.Assert.fail(("expecting ArithmeticException but got: " + (f.toString())));
		} catch (java.lang.ArithmeticException ex) {
		}
		try {
			f = new org.apache.commons.math.fraction.Fraction(-(java.lang.Integer.MAX_VALUE) , 1);
			f = f.add(f);
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
		try {
			f = new org.apache.commons.math.fraction.Fraction(-(java.lang.Integer.MAX_VALUE) , 1);
			f = f.add(f);
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
		f1 = new org.apache.commons.math.fraction.Fraction(3 , 327680);
		f2 = new org.apache.commons.math.fraction.Fraction(2 , 59049);
		try {
			f = f1.add(f2);
			junit.framework.Assert.fail(("expecting ArithmeticException but got: " + (f.toString())));
		} catch (java.lang.ArithmeticException ex) {
		}
	}

	public void testDivide() {
		org.apache.commons.math.fraction.Fraction a = new org.apache.commons.math.fraction.Fraction(1 , 2);
		org.apache.commons.math.fraction.Fraction b = new org.apache.commons.math.fraction.Fraction(2 , 3);
		assertFraction(1, 1, a.divide(a));
		assertFraction(3, 4, a.divide(b));
		assertFraction(4, 3, b.divide(a));
		assertFraction(1, 1, b.divide(b));
		org.apache.commons.math.fraction.Fraction f1 = new org.apache.commons.math.fraction.Fraction(3 , 5);
		org.apache.commons.math.fraction.Fraction f2 = org.apache.commons.math.fraction.Fraction.ZERO;
		try {
			f1.divide(f2);
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
		f1 = new org.apache.commons.math.fraction.Fraction(0 , 5);
		f2 = new org.apache.commons.math.fraction.Fraction(2 , 7);
		org.apache.commons.math.fraction.Fraction f = f1.divide(f2);
		junit.framework.Assert.assertSame(org.apache.commons.math.fraction.Fraction.ZERO, f);
		f1 = new org.apache.commons.math.fraction.Fraction(2 , 7);
		f2 = org.apache.commons.math.fraction.Fraction.ONE;
		f = f1.divide(f2);
		junit.framework.Assert.assertEquals(2, f.getNumerator());
		junit.framework.Assert.assertEquals(7, f.getDenominator());
		f1 = new org.apache.commons.math.fraction.Fraction(1 , java.lang.Integer.MAX_VALUE);
		f = f1.divide(f1);
		junit.framework.Assert.assertEquals(1, f.getNumerator());
		junit.framework.Assert.assertEquals(1, f.getDenominator());
		f1 = new org.apache.commons.math.fraction.Fraction(java.lang.Integer.MIN_VALUE , java.lang.Integer.MAX_VALUE);
		f2 = new org.apache.commons.math.fraction.Fraction(1 , java.lang.Integer.MAX_VALUE);
		f = f1.divide(f2);
		junit.framework.Assert.assertEquals(java.lang.Integer.MIN_VALUE, f.getNumerator());
		junit.framework.Assert.assertEquals(1, f.getDenominator());
		try {
			f.divide(null);
			junit.framework.Assert.fail("IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			f1 = new org.apache.commons.math.fraction.Fraction(1 , java.lang.Integer.MAX_VALUE);
			f = f1.divide(f1.reciprocal());
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
		try {
			f1 = new org.apache.commons.math.fraction.Fraction(1 , -(java.lang.Integer.MAX_VALUE));
			f = f1.divide(f1.reciprocal());
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
		f1 = new org.apache.commons.math.fraction.Fraction(6 , 35);
		f = f1.divide(15);
		junit.framework.Assert.assertEquals(2, f.getNumerator());
		junit.framework.Assert.assertEquals(175, f.getDenominator());
	}

	public void testMultiply() {
		org.apache.commons.math.fraction.Fraction a = new org.apache.commons.math.fraction.Fraction(1 , 2);
		org.apache.commons.math.fraction.Fraction b = new org.apache.commons.math.fraction.Fraction(2 , 3);
		assertFraction(1, 4, a.multiply(a));
		assertFraction(1, 3, a.multiply(b));
		assertFraction(1, 3, b.multiply(a));
		assertFraction(4, 9, b.multiply(b));
		org.apache.commons.math.fraction.Fraction f1 = new org.apache.commons.math.fraction.Fraction(java.lang.Integer.MAX_VALUE , 1);
		org.apache.commons.math.fraction.Fraction f2 = new org.apache.commons.math.fraction.Fraction(java.lang.Integer.MIN_VALUE , java.lang.Integer.MAX_VALUE);
		org.apache.commons.math.fraction.Fraction f = f1.multiply(f2);
		junit.framework.Assert.assertEquals(java.lang.Integer.MIN_VALUE, f.getNumerator());
		junit.framework.Assert.assertEquals(1, f.getDenominator());
		try {
			f.multiply(null);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		f1 = new org.apache.commons.math.fraction.Fraction(6 , 35);
		f = f1.multiply(15);
		junit.framework.Assert.assertEquals(18, f.getNumerator());
		junit.framework.Assert.assertEquals(7, f.getDenominator());
	}

	public void testSubtract() {
		org.apache.commons.math.fraction.Fraction a = new org.apache.commons.math.fraction.Fraction(1 , 2);
		org.apache.commons.math.fraction.Fraction b = new org.apache.commons.math.fraction.Fraction(2 , 3);
		assertFraction(0, 1, a.subtract(a));
		assertFraction(-1, 6, a.subtract(b));
		assertFraction(1, 6, b.subtract(a));
		assertFraction(0, 1, b.subtract(b));
		org.apache.commons.math.fraction.Fraction f = new org.apache.commons.math.fraction.Fraction(1 , 1);
		try {
			f.subtract(null);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		org.apache.commons.math.fraction.Fraction f1 = new org.apache.commons.math.fraction.Fraction(1 , (32768 * 3));
		org.apache.commons.math.fraction.Fraction f2 = new org.apache.commons.math.fraction.Fraction(1 , 59049);
		f = f1.subtract(f2);
		junit.framework.Assert.assertEquals(-13085, f.getNumerator());
		junit.framework.Assert.assertEquals(1934917632, f.getDenominator());
		f1 = new org.apache.commons.math.fraction.Fraction(java.lang.Integer.MIN_VALUE , 3);
		f2 = new org.apache.commons.math.fraction.Fraction(1 , 3).negate();
		f = f1.subtract(f2);
		junit.framework.Assert.assertEquals(((java.lang.Integer.MIN_VALUE) + 1), f.getNumerator());
		junit.framework.Assert.assertEquals(3, f.getDenominator());
		f1 = new org.apache.commons.math.fraction.Fraction(java.lang.Integer.MAX_VALUE , 1);
		f2 = org.apache.commons.math.fraction.Fraction.ONE;
		f = f1.subtract(f2);
		junit.framework.Assert.assertEquals(((java.lang.Integer.MAX_VALUE) - 1), f.getNumerator());
		junit.framework.Assert.assertEquals(1, f.getDenominator());
		f = f1.subtract(1);
		junit.framework.Assert.assertEquals(((java.lang.Integer.MAX_VALUE) - 1), f.getNumerator());
		junit.framework.Assert.assertEquals(1, f.getDenominator());
		try {
			f1 = new org.apache.commons.math.fraction.Fraction(1 , java.lang.Integer.MAX_VALUE);
			f2 = new org.apache.commons.math.fraction.Fraction(1 , ((java.lang.Integer.MAX_VALUE) - 1));
			f = f1.subtract(f2);
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
		f1 = new org.apache.commons.math.fraction.Fraction(java.lang.Integer.MIN_VALUE , 5);
		f2 = new org.apache.commons.math.fraction.Fraction(1 , 5);
		try {
			f = f1.subtract(f2);
			junit.framework.Assert.fail(("expecting ArithmeticException but got: " + (f.toString())));
		} catch (java.lang.ArithmeticException ex) {
		}
		try {
			f = new org.apache.commons.math.fraction.Fraction(java.lang.Integer.MIN_VALUE , 1);
			f = f.subtract(org.apache.commons.math.fraction.Fraction.ONE);
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
		try {
			f = new org.apache.commons.math.fraction.Fraction(java.lang.Integer.MAX_VALUE , 1);
			f = f.subtract(org.apache.commons.math.fraction.Fraction.ONE.negate());
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
		f1 = new org.apache.commons.math.fraction.Fraction(3 , 327680);
		f2 = new org.apache.commons.math.fraction.Fraction(2 , 59049);
		try {
			f = f1.subtract(f2);
			junit.framework.Assert.fail(("expecting ArithmeticException but got: " + (f.toString())));
		} catch (java.lang.ArithmeticException ex) {
		}
	}

	public void testEqualsAndHashCode() {
		org.apache.commons.math.fraction.Fraction zero = new org.apache.commons.math.fraction.Fraction(0 , 1);
		org.apache.commons.math.fraction.Fraction nullFraction = null;
		junit.framework.Assert.assertTrue(zero.equals(zero));
		junit.framework.Assert.assertFalse(zero.equals(nullFraction));
		junit.framework.Assert.assertFalse(zero.equals(java.lang.Double.valueOf(0)));
		org.apache.commons.math.fraction.Fraction zero2 = new org.apache.commons.math.fraction.Fraction(0 , 2);
		junit.framework.Assert.assertTrue(zero.equals(zero2));
		junit.framework.Assert.assertEquals(zero.hashCode(), zero2.hashCode());
		org.apache.commons.math.fraction.Fraction one = new org.apache.commons.math.fraction.Fraction(1 , 1);
		junit.framework.Assert.assertFalse(((one.equals(zero)) || (zero.equals(one))));
	}

	public void testGetReducedFraction() {
		org.apache.commons.math.fraction.Fraction threeFourths = new org.apache.commons.math.fraction.Fraction(3 , 4);
		junit.framework.Assert.assertTrue(threeFourths.equals(org.apache.commons.math.fraction.Fraction.getReducedFraction(6, 8)));
		junit.framework.Assert.assertTrue(org.apache.commons.math.fraction.Fraction.ZERO.equals(org.apache.commons.math.fraction.Fraction.getReducedFraction(0, -1)));
		try {
			org.apache.commons.math.fraction.Fraction.getReducedFraction(1, 0);
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
		junit.framework.Assert.assertEquals(org.apache.commons.math.fraction.Fraction.getReducedFraction(2, java.lang.Integer.MIN_VALUE).getNumerator(), -1);
		junit.framework.Assert.assertEquals(org.apache.commons.math.fraction.Fraction.getReducedFraction(1, -1).getNumerator(), -1);
	}

	public void testToString() {
		junit.framework.Assert.assertEquals("0", new org.apache.commons.math.fraction.Fraction(0 , 3).toString());
		junit.framework.Assert.assertEquals("3", new org.apache.commons.math.fraction.Fraction(6 , 2).toString());
		junit.framework.Assert.assertEquals("2 / 3", new org.apache.commons.math.fraction.Fraction(18 , 27).toString());
	}

	public void testSerial() throws org.apache.commons.math.fraction.FractionConversionException {
		org.apache.commons.math.fraction.Fraction[] fractions = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(3 , 4) , org.apache.commons.math.fraction.Fraction.ONE , org.apache.commons.math.fraction.Fraction.ZERO , new org.apache.commons.math.fraction.Fraction(17) , new org.apache.commons.math.fraction.Fraction(java.lang.Math.PI , 1000) , new org.apache.commons.math.fraction.Fraction(-5 , 2) };
		for (org.apache.commons.math.fraction.Fraction fraction : fractions) {
			junit.framework.Assert.assertEquals(fraction, org.apache.commons.math.TestUtils.serializeAndRecover(fraction));
		}
	}
}

