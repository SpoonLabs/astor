package org.apache.commons.math.fraction;


public class BigFractionTest extends junit.framework.TestCase {
	private void assertFraction(int expectedNumerator, int expectedDenominator, org.apache.commons.math.fraction.BigFraction actual) {
		junit.framework.Assert.assertEquals(expectedNumerator, actual.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(expectedDenominator, actual.getDenominatorAsInt());
	}

	private void assertFraction(long expectedNumerator, long expectedDenominator, org.apache.commons.math.fraction.BigFraction actual) {
		junit.framework.Assert.assertEquals(expectedNumerator, actual.getNumeratorAsLong());
		junit.framework.Assert.assertEquals(expectedDenominator, actual.getDenominatorAsLong());
	}

	public void testConstructor() {
		assertFraction(0, 1, new org.apache.commons.math.fraction.BigFraction(0 , 1));
		assertFraction(0, 1, new org.apache.commons.math.fraction.BigFraction(0L , 2L));
		assertFraction(0, 1, new org.apache.commons.math.fraction.BigFraction(0 , -1));
		assertFraction(1, 2, new org.apache.commons.math.fraction.BigFraction(1 , 2));
		assertFraction(1, 2, new org.apache.commons.math.fraction.BigFraction(2 , 4));
		assertFraction(-1, 2, new org.apache.commons.math.fraction.BigFraction(-1 , 2));
		assertFraction(-1, 2, new org.apache.commons.math.fraction.BigFraction(1 , -2));
		assertFraction(-1, 2, new org.apache.commons.math.fraction.BigFraction(-2 , 4));
		assertFraction(-1, 2, new org.apache.commons.math.fraction.BigFraction(2 , -4));
		assertFraction(11, 1, new org.apache.commons.math.fraction.BigFraction(11));
		assertFraction(11, 1, new org.apache.commons.math.fraction.BigFraction(11L));
		assertFraction(11, 1, new org.apache.commons.math.fraction.BigFraction(new java.math.BigInteger("11")));
		try {
			assertFraction(0, 1, new org.apache.commons.math.fraction.BigFraction(1.0E-14 , 1.0E-5 , 100));
			assertFraction(2, 5, new org.apache.commons.math.fraction.BigFraction(0.40000000000001 , 1.0E-5 , 100));
			assertFraction(15, 1, new org.apache.commons.math.fraction.BigFraction(15.0000000000001 , 1.0E-5 , 100));
		} catch (org.apache.commons.math.ConvergenceException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
		junit.framework.Assert.assertEquals(1.0E-14, new org.apache.commons.math.fraction.BigFraction(1.0E-14).doubleValue(), 0.0);
		junit.framework.Assert.assertEquals(0.40000000000001, new org.apache.commons.math.fraction.BigFraction(0.40000000000001).doubleValue(), 0.0);
		junit.framework.Assert.assertEquals(15.0000000000001, new org.apache.commons.math.fraction.BigFraction(15.0000000000001).doubleValue(), 0.0);
		assertFraction(3602879701896487L, 9007199254740992L, new org.apache.commons.math.fraction.BigFraction(0.40000000000001));
		assertFraction(1055531162664967L, 70368744177664L, new org.apache.commons.math.fraction.BigFraction(15.0000000000001));
		try {
			new org.apache.commons.math.fraction.BigFraction(null , java.math.BigInteger.ONE);
		} catch (java.lang.NullPointerException npe) {
		}
		try {
			new org.apache.commons.math.fraction.BigFraction(java.math.BigInteger.ONE , null);
		} catch (java.lang.NullPointerException npe) {
		}
		try {
			new org.apache.commons.math.fraction.BigFraction(java.math.BigInteger.ONE , java.math.BigInteger.ZERO);
		} catch (java.lang.ArithmeticException npe) {
		}
		try {
			new org.apache.commons.math.fraction.BigFraction((2.0 * (java.lang.Integer.MAX_VALUE)) , 1.0E-5 , 100000);
		} catch (org.apache.commons.math.fraction.FractionConversionException fce) {
		}
	}

	public void testGoldenRatio() {
		try {
			new org.apache.commons.math.fraction.BigFraction(((1 + (java.lang.Math.sqrt(5))) / 2) , 1.0E-12 , 25);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.ConvergenceException ce) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testDoubleConstructor() throws org.apache.commons.math.ConvergenceException {
		assertFraction(1, 2, new org.apache.commons.math.fraction.BigFraction((((double)(1)) / ((double)(2))) , 1.0E-5 , 100));
		assertFraction(1, 3, new org.apache.commons.math.fraction.BigFraction((((double)(1)) / ((double)(3))) , 1.0E-5 , 100));
		assertFraction(2, 3, new org.apache.commons.math.fraction.BigFraction((((double)(2)) / ((double)(3))) , 1.0E-5 , 100));
		assertFraction(1, 4, new org.apache.commons.math.fraction.BigFraction((((double)(1)) / ((double)(4))) , 1.0E-5 , 100));
		assertFraction(3, 4, new org.apache.commons.math.fraction.BigFraction((((double)(3)) / ((double)(4))) , 1.0E-5 , 100));
		assertFraction(1, 5, new org.apache.commons.math.fraction.BigFraction((((double)(1)) / ((double)(5))) , 1.0E-5 , 100));
		assertFraction(2, 5, new org.apache.commons.math.fraction.BigFraction((((double)(2)) / ((double)(5))) , 1.0E-5 , 100));
		assertFraction(3, 5, new org.apache.commons.math.fraction.BigFraction((((double)(3)) / ((double)(5))) , 1.0E-5 , 100));
		assertFraction(4, 5, new org.apache.commons.math.fraction.BigFraction((((double)(4)) / ((double)(5))) , 1.0E-5 , 100));
		assertFraction(1, 6, new org.apache.commons.math.fraction.BigFraction((((double)(1)) / ((double)(6))) , 1.0E-5 , 100));
		assertFraction(5, 6, new org.apache.commons.math.fraction.BigFraction((((double)(5)) / ((double)(6))) , 1.0E-5 , 100));
		assertFraction(1, 7, new org.apache.commons.math.fraction.BigFraction((((double)(1)) / ((double)(7))) , 1.0E-5 , 100));
		assertFraction(2, 7, new org.apache.commons.math.fraction.BigFraction((((double)(2)) / ((double)(7))) , 1.0E-5 , 100));
		assertFraction(3, 7, new org.apache.commons.math.fraction.BigFraction((((double)(3)) / ((double)(7))) , 1.0E-5 , 100));
		assertFraction(4, 7, new org.apache.commons.math.fraction.BigFraction((((double)(4)) / ((double)(7))) , 1.0E-5 , 100));
		assertFraction(5, 7, new org.apache.commons.math.fraction.BigFraction((((double)(5)) / ((double)(7))) , 1.0E-5 , 100));
		assertFraction(6, 7, new org.apache.commons.math.fraction.BigFraction((((double)(6)) / ((double)(7))) , 1.0E-5 , 100));
		assertFraction(1, 8, new org.apache.commons.math.fraction.BigFraction((((double)(1)) / ((double)(8))) , 1.0E-5 , 100));
		assertFraction(3, 8, new org.apache.commons.math.fraction.BigFraction((((double)(3)) / ((double)(8))) , 1.0E-5 , 100));
		assertFraction(5, 8, new org.apache.commons.math.fraction.BigFraction((((double)(5)) / ((double)(8))) , 1.0E-5 , 100));
		assertFraction(7, 8, new org.apache.commons.math.fraction.BigFraction((((double)(7)) / ((double)(8))) , 1.0E-5 , 100));
		assertFraction(1, 9, new org.apache.commons.math.fraction.BigFraction((((double)(1)) / ((double)(9))) , 1.0E-5 , 100));
		assertFraction(2, 9, new org.apache.commons.math.fraction.BigFraction((((double)(2)) / ((double)(9))) , 1.0E-5 , 100));
		assertFraction(4, 9, new org.apache.commons.math.fraction.BigFraction((((double)(4)) / ((double)(9))) , 1.0E-5 , 100));
		assertFraction(5, 9, new org.apache.commons.math.fraction.BigFraction((((double)(5)) / ((double)(9))) , 1.0E-5 , 100));
		assertFraction(7, 9, new org.apache.commons.math.fraction.BigFraction((((double)(7)) / ((double)(9))) , 1.0E-5 , 100));
		assertFraction(8, 9, new org.apache.commons.math.fraction.BigFraction((((double)(8)) / ((double)(9))) , 1.0E-5 , 100));
		assertFraction(1, 10, new org.apache.commons.math.fraction.BigFraction((((double)(1)) / ((double)(10))) , 1.0E-5 , 100));
		assertFraction(3, 10, new org.apache.commons.math.fraction.BigFraction((((double)(3)) / ((double)(10))) , 1.0E-5 , 100));
		assertFraction(7, 10, new org.apache.commons.math.fraction.BigFraction((((double)(7)) / ((double)(10))) , 1.0E-5 , 100));
		assertFraction(9, 10, new org.apache.commons.math.fraction.BigFraction((((double)(9)) / ((double)(10))) , 1.0E-5 , 100));
		assertFraction(1, 11, new org.apache.commons.math.fraction.BigFraction((((double)(1)) / ((double)(11))) , 1.0E-5 , 100));
		assertFraction(2, 11, new org.apache.commons.math.fraction.BigFraction((((double)(2)) / ((double)(11))) , 1.0E-5 , 100));
		assertFraction(3, 11, new org.apache.commons.math.fraction.BigFraction((((double)(3)) / ((double)(11))) , 1.0E-5 , 100));
		assertFraction(4, 11, new org.apache.commons.math.fraction.BigFraction((((double)(4)) / ((double)(11))) , 1.0E-5 , 100));
		assertFraction(5, 11, new org.apache.commons.math.fraction.BigFraction((((double)(5)) / ((double)(11))) , 1.0E-5 , 100));
		assertFraction(6, 11, new org.apache.commons.math.fraction.BigFraction((((double)(6)) / ((double)(11))) , 1.0E-5 , 100));
		assertFraction(7, 11, new org.apache.commons.math.fraction.BigFraction((((double)(7)) / ((double)(11))) , 1.0E-5 , 100));
		assertFraction(8, 11, new org.apache.commons.math.fraction.BigFraction((((double)(8)) / ((double)(11))) , 1.0E-5 , 100));
		assertFraction(9, 11, new org.apache.commons.math.fraction.BigFraction((((double)(9)) / ((double)(11))) , 1.0E-5 , 100));
		assertFraction(10, 11, new org.apache.commons.math.fraction.BigFraction((((double)(10)) / ((double)(11))) , 1.0E-5 , 100));
	}

	public void testDigitLimitConstructor() throws org.apache.commons.math.ConvergenceException {
		assertFraction(2, 5, new org.apache.commons.math.fraction.BigFraction(0.4 , 9));
		assertFraction(2, 5, new org.apache.commons.math.fraction.BigFraction(0.4 , 99));
		assertFraction(2, 5, new org.apache.commons.math.fraction.BigFraction(0.4 , 999));
		assertFraction(3, 5, new org.apache.commons.math.fraction.BigFraction(0.6152 , 9));
		assertFraction(8, 13, new org.apache.commons.math.fraction.BigFraction(0.6152 , 99));
		assertFraction(510, 829, new org.apache.commons.math.fraction.BigFraction(0.6152 , 999));
		assertFraction(769, 1250, new org.apache.commons.math.fraction.BigFraction(0.6152 , 9999));
	}

	public void testEpsilonLimitConstructor() throws org.apache.commons.math.ConvergenceException {
		assertFraction(2, 5, new org.apache.commons.math.fraction.BigFraction(0.4 , 1.0E-5 , 100));
		assertFraction(3, 5, new org.apache.commons.math.fraction.BigFraction(0.6152 , 0.02 , 100));
		assertFraction(8, 13, new org.apache.commons.math.fraction.BigFraction(0.6152 , 0.001 , 100));
		assertFraction(251, 408, new org.apache.commons.math.fraction.BigFraction(0.6152 , 1.0E-4 , 100));
		assertFraction(251, 408, new org.apache.commons.math.fraction.BigFraction(0.6152 , 1.0E-5 , 100));
		assertFraction(510, 829, new org.apache.commons.math.fraction.BigFraction(0.6152 , 1.0E-6 , 100));
		assertFraction(769, 1250, new org.apache.commons.math.fraction.BigFraction(0.6152 , 1.0E-7 , 100));
	}

	public void testCompareTo() {
		org.apache.commons.math.fraction.BigFraction first = new org.apache.commons.math.fraction.BigFraction(1 , 2);
		org.apache.commons.math.fraction.BigFraction second = new org.apache.commons.math.fraction.BigFraction(1 , 3);
		org.apache.commons.math.fraction.BigFraction third = new org.apache.commons.math.fraction.BigFraction(1 , 2);
		junit.framework.Assert.assertEquals(0, first.compareTo(first));
		junit.framework.Assert.assertEquals(0, first.compareTo(third));
		junit.framework.Assert.assertEquals(1, first.compareTo(second));
		junit.framework.Assert.assertEquals(-1, second.compareTo(first));
		org.apache.commons.math.fraction.BigFraction pi1 = new org.apache.commons.math.fraction.BigFraction(1068966896 , 340262731);
		org.apache.commons.math.fraction.BigFraction pi2 = new org.apache.commons.math.fraction.BigFraction(411557987 , 131002976);
		junit.framework.Assert.assertEquals(-1, pi1.compareTo(pi2));
		junit.framework.Assert.assertEquals(1, pi2.compareTo(pi1));
		junit.framework.Assert.assertEquals(0.0, ((pi1.doubleValue()) - (pi2.doubleValue())), 1.0E-20);
	}

	public void testDoubleValue() {
		org.apache.commons.math.fraction.BigFraction first = new org.apache.commons.math.fraction.BigFraction(1 , 2);
		org.apache.commons.math.fraction.BigFraction second = new org.apache.commons.math.fraction.BigFraction(1 , 3);
		junit.framework.Assert.assertEquals(0.5, first.doubleValue(), 0.0);
		junit.framework.Assert.assertEquals((1.0 / 3.0), second.doubleValue(), 0.0);
	}

	public void testFloatValue() {
		org.apache.commons.math.fraction.BigFraction first = new org.apache.commons.math.fraction.BigFraction(1 , 2);
		org.apache.commons.math.fraction.BigFraction second = new org.apache.commons.math.fraction.BigFraction(1 , 3);
		junit.framework.Assert.assertEquals(0.5F, first.floatValue(), 0.0F);
		junit.framework.Assert.assertEquals(((float)(1.0 / 3.0)), second.floatValue(), 0.0F);
	}

	public void testIntValue() {
		org.apache.commons.math.fraction.BigFraction first = new org.apache.commons.math.fraction.BigFraction(1 , 2);
		org.apache.commons.math.fraction.BigFraction second = new org.apache.commons.math.fraction.BigFraction(3 , 2);
		junit.framework.Assert.assertEquals(0, first.intValue());
		junit.framework.Assert.assertEquals(1, second.intValue());
	}

	public void testLongValue() {
		org.apache.commons.math.fraction.BigFraction first = new org.apache.commons.math.fraction.BigFraction(1 , 2);
		org.apache.commons.math.fraction.BigFraction second = new org.apache.commons.math.fraction.BigFraction(3 , 2);
		junit.framework.Assert.assertEquals(0L, first.longValue());
		junit.framework.Assert.assertEquals(1L, second.longValue());
	}

	public void testConstructorDouble() {
		assertFraction(1, 2, new org.apache.commons.math.fraction.BigFraction(0.5));
		assertFraction(6004799503160661L, 18014398509481984L, new org.apache.commons.math.fraction.BigFraction((1.0 / 3.0)));
		assertFraction(6124895493223875L, 36028797018963968L, new org.apache.commons.math.fraction.BigFraction((17.0 / 100.0)));
		assertFraction(1784551352345559L, 562949953421312L, new org.apache.commons.math.fraction.BigFraction((317.0 / 100.0)));
		assertFraction(-1, 2, new org.apache.commons.math.fraction.BigFraction(-0.5));
		assertFraction(-6004799503160661L, 18014398509481984L, new org.apache.commons.math.fraction.BigFraction(((-1.0) / 3.0)));
		assertFraction(-6124895493223875L, 36028797018963968L, new org.apache.commons.math.fraction.BigFraction((17.0 / (-100.0))));
		assertFraction(-1784551352345559L, 562949953421312L, new org.apache.commons.math.fraction.BigFraction(((-317.0) / 100.0)));
		for (double v : new double[]{ java.lang.Double.NaN , java.lang.Double.NEGATIVE_INFINITY , java.lang.Double.POSITIVE_INFINITY }) {
			try {
				new org.apache.commons.math.fraction.BigFraction(v);
				junit.framework.Assert.fail("expected exception");
			} catch (java.lang.IllegalArgumentException iae) {
			}
		}
		junit.framework.Assert.assertEquals(1L, new org.apache.commons.math.fraction.BigFraction(java.lang.Double.MAX_VALUE).getDenominatorAsLong());
		junit.framework.Assert.assertEquals(1L, new org.apache.commons.math.fraction.BigFraction(java.lang.Double.longBitsToDouble(4503599627370496L)).getNumeratorAsLong());
		junit.framework.Assert.assertEquals(1L, new org.apache.commons.math.fraction.BigFraction(java.lang.Double.MIN_VALUE).getNumeratorAsLong());
	}

	public void testAbs() {
		org.apache.commons.math.fraction.BigFraction a = new org.apache.commons.math.fraction.BigFraction(10 , 21);
		org.apache.commons.math.fraction.BigFraction b = new org.apache.commons.math.fraction.BigFraction(-10 , 21);
		org.apache.commons.math.fraction.BigFraction c = new org.apache.commons.math.fraction.BigFraction(10 , -21);
		assertFraction(10, 21, a.abs());
		assertFraction(10, 21, b.abs());
		assertFraction(10, 21, c.abs());
	}

	public void testReciprocal() {
		org.apache.commons.math.fraction.BigFraction f = null;
		f = new org.apache.commons.math.fraction.BigFraction(50 , 75);
		f = f.reciprocal();
		junit.framework.Assert.assertEquals(3, f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(2, f.getDenominatorAsInt());
		f = new org.apache.commons.math.fraction.BigFraction(4 , 3);
		f = f.reciprocal();
		junit.framework.Assert.assertEquals(3, f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(4, f.getDenominatorAsInt());
		f = new org.apache.commons.math.fraction.BigFraction(-15 , 47);
		f = f.reciprocal();
		junit.framework.Assert.assertEquals(-47, f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(15, f.getDenominatorAsInt());
		f = new org.apache.commons.math.fraction.BigFraction(0 , 3);
		try {
			f = f.reciprocal();
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
		f = new org.apache.commons.math.fraction.BigFraction(java.lang.Integer.MAX_VALUE , 1);
		f = f.reciprocal();
		junit.framework.Assert.assertEquals(1, f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(java.lang.Integer.MAX_VALUE, f.getDenominatorAsInt());
	}

	public void testNegate() {
		org.apache.commons.math.fraction.BigFraction f = null;
		f = new org.apache.commons.math.fraction.BigFraction(50 , 75);
		f = f.negate();
		junit.framework.Assert.assertEquals(-2, f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(3, f.getDenominatorAsInt());
		f = new org.apache.commons.math.fraction.BigFraction(-50 , 75);
		f = f.negate();
		junit.framework.Assert.assertEquals(2, f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(3, f.getDenominatorAsInt());
		f = new org.apache.commons.math.fraction.BigFraction(((java.lang.Integer.MAX_VALUE) - 1) , java.lang.Integer.MAX_VALUE);
		f = f.negate();
		junit.framework.Assert.assertEquals(((java.lang.Integer.MIN_VALUE) + 2), f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(java.lang.Integer.MAX_VALUE, f.getDenominatorAsInt());
	}

	public void testAdd() {
		org.apache.commons.math.fraction.BigFraction a = new org.apache.commons.math.fraction.BigFraction(1 , 2);
		org.apache.commons.math.fraction.BigFraction b = new org.apache.commons.math.fraction.BigFraction(2 , 3);
		assertFraction(1, 1, a.add(a));
		assertFraction(7, 6, a.add(b));
		assertFraction(7, 6, b.add(a));
		assertFraction(4, 3, b.add(b));
		org.apache.commons.math.fraction.BigFraction f1 = new org.apache.commons.math.fraction.BigFraction(((java.lang.Integer.MAX_VALUE) - 1) , 1);
		org.apache.commons.math.fraction.BigFraction f2 = org.apache.commons.math.fraction.BigFraction.ONE;
		org.apache.commons.math.fraction.BigFraction f = f1.add(f2);
		junit.framework.Assert.assertEquals(java.lang.Integer.MAX_VALUE, f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(1, f.getDenominatorAsInt());
		f1 = new org.apache.commons.math.fraction.BigFraction(-1 , (((13 * 13) * 2) * 2));
		f2 = new org.apache.commons.math.fraction.BigFraction(-2 , ((13 * 17) * 2));
		f = f1.add(f2);
		junit.framework.Assert.assertEquals(((((13 * 13) * 17) * 2) * 2), f.getDenominatorAsInt());
		junit.framework.Assert.assertEquals(((-17) - ((2 * 13) * 2)), f.getNumeratorAsInt());
		try {
			f.add(((org.apache.commons.math.fraction.BigFraction)(null)));
			junit.framework.Assert.fail("expecting NullPointerException");
		} catch (java.lang.NullPointerException ex) {
		}
		f1 = new org.apache.commons.math.fraction.BigFraction(1 , (32768 * 3));
		f2 = new org.apache.commons.math.fraction.BigFraction(1 , 59049);
		f = f1.add(f2);
		junit.framework.Assert.assertEquals(52451, f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(1934917632, f.getDenominatorAsInt());
		f1 = new org.apache.commons.math.fraction.BigFraction(java.lang.Integer.MIN_VALUE , 3);
		f2 = new org.apache.commons.math.fraction.BigFraction(1 , 3);
		f = f1.add(f2);
		junit.framework.Assert.assertEquals(((java.lang.Integer.MIN_VALUE) + 1), f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(3, f.getDenominatorAsInt());
		f1 = new org.apache.commons.math.fraction.BigFraction(((java.lang.Integer.MAX_VALUE) - 1) , 1);
		f = f1.add(java.math.BigInteger.ONE);
		junit.framework.Assert.assertEquals(java.lang.Integer.MAX_VALUE, f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(1, f.getDenominatorAsInt());
		f = f.add(java.math.BigInteger.ZERO);
		junit.framework.Assert.assertEquals(java.lang.Integer.MAX_VALUE, f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(1, f.getDenominatorAsInt());
		f1 = new org.apache.commons.math.fraction.BigFraction(((java.lang.Integer.MAX_VALUE) - 1) , 1);
		f = f1.add(1);
		junit.framework.Assert.assertEquals(java.lang.Integer.MAX_VALUE, f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(1, f.getDenominatorAsInt());
		f = f.add(0);
		junit.framework.Assert.assertEquals(java.lang.Integer.MAX_VALUE, f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(1, f.getDenominatorAsInt());
		f1 = new org.apache.commons.math.fraction.BigFraction(((java.lang.Integer.MAX_VALUE) - 1) , 1);
		f = f1.add(1L);
		junit.framework.Assert.assertEquals(java.lang.Integer.MAX_VALUE, f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(1, f.getDenominatorAsInt());
		f = f.add(0L);
		junit.framework.Assert.assertEquals(java.lang.Integer.MAX_VALUE, f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(1, f.getDenominatorAsInt());
	}

	public void testDivide() {
		org.apache.commons.math.fraction.BigFraction a = new org.apache.commons.math.fraction.BigFraction(1 , 2);
		org.apache.commons.math.fraction.BigFraction b = new org.apache.commons.math.fraction.BigFraction(2 , 3);
		assertFraction(1, 1, a.divide(a));
		assertFraction(3, 4, a.divide(b));
		assertFraction(4, 3, b.divide(a));
		assertFraction(1, 1, b.divide(b));
		org.apache.commons.math.fraction.BigFraction f1 = new org.apache.commons.math.fraction.BigFraction(3 , 5);
		org.apache.commons.math.fraction.BigFraction f2 = org.apache.commons.math.fraction.BigFraction.ZERO;
		try {
			f1.divide(f2);
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
		f1 = new org.apache.commons.math.fraction.BigFraction(0 , 5);
		f2 = new org.apache.commons.math.fraction.BigFraction(2 , 7);
		org.apache.commons.math.fraction.BigFraction f = f1.divide(f2);
		junit.framework.Assert.assertSame(org.apache.commons.math.fraction.BigFraction.ZERO, f);
		f1 = new org.apache.commons.math.fraction.BigFraction(2 , 7);
		f2 = org.apache.commons.math.fraction.BigFraction.ONE;
		f = f1.divide(f2);
		junit.framework.Assert.assertEquals(2, f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(7, f.getDenominatorAsInt());
		f1 = new org.apache.commons.math.fraction.BigFraction(1 , java.lang.Integer.MAX_VALUE);
		f = f1.divide(f1);
		junit.framework.Assert.assertEquals(1, f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(1, f.getDenominatorAsInt());
		f1 = new org.apache.commons.math.fraction.BigFraction(java.lang.Integer.MIN_VALUE , java.lang.Integer.MAX_VALUE);
		f2 = new org.apache.commons.math.fraction.BigFraction(1 , java.lang.Integer.MAX_VALUE);
		f = f1.divide(f2);
		junit.framework.Assert.assertEquals(java.lang.Integer.MIN_VALUE, f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(1, f.getDenominatorAsInt());
		try {
			f.divide(((org.apache.commons.math.fraction.BigFraction)(null)));
			junit.framework.Assert.fail("expecting NullPointerException");
		} catch (java.lang.NullPointerException ex) {
		}
		f1 = new org.apache.commons.math.fraction.BigFraction(java.lang.Integer.MIN_VALUE , java.lang.Integer.MAX_VALUE);
		f = f1.divide(java.math.BigInteger.valueOf(java.lang.Integer.MIN_VALUE));
		junit.framework.Assert.assertEquals(java.lang.Integer.MAX_VALUE, f.getDenominatorAsInt());
		junit.framework.Assert.assertEquals(1, f.getNumeratorAsInt());
		f1 = new org.apache.commons.math.fraction.BigFraction(java.lang.Integer.MIN_VALUE , java.lang.Integer.MAX_VALUE);
		f = f1.divide(java.lang.Integer.MIN_VALUE);
		junit.framework.Assert.assertEquals(java.lang.Integer.MAX_VALUE, f.getDenominatorAsInt());
		junit.framework.Assert.assertEquals(1, f.getNumeratorAsInt());
		f1 = new org.apache.commons.math.fraction.BigFraction(java.lang.Integer.MIN_VALUE , java.lang.Integer.MAX_VALUE);
		f = f1.divide(((long)(java.lang.Integer.MIN_VALUE)));
		junit.framework.Assert.assertEquals(java.lang.Integer.MAX_VALUE, f.getDenominatorAsInt());
		junit.framework.Assert.assertEquals(1, f.getNumeratorAsInt());
	}

	public void testMultiply() {
		org.apache.commons.math.fraction.BigFraction a = new org.apache.commons.math.fraction.BigFraction(1 , 2);
		org.apache.commons.math.fraction.BigFraction b = new org.apache.commons.math.fraction.BigFraction(2 , 3);
		assertFraction(1, 4, a.multiply(a));
		assertFraction(1, 3, a.multiply(b));
		assertFraction(1, 3, b.multiply(a));
		assertFraction(4, 9, b.multiply(b));
		org.apache.commons.math.fraction.BigFraction f1 = new org.apache.commons.math.fraction.BigFraction(java.lang.Integer.MAX_VALUE , 1);
		org.apache.commons.math.fraction.BigFraction f2 = new org.apache.commons.math.fraction.BigFraction(java.lang.Integer.MIN_VALUE , java.lang.Integer.MAX_VALUE);
		org.apache.commons.math.fraction.BigFraction f = f1.multiply(f2);
		junit.framework.Assert.assertEquals(java.lang.Integer.MIN_VALUE, f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(1, f.getDenominatorAsInt());
		f = f2.multiply(java.lang.Integer.MAX_VALUE);
		junit.framework.Assert.assertEquals(java.lang.Integer.MIN_VALUE, f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(1, f.getDenominatorAsInt());
		f = f2.multiply(((long)(java.lang.Integer.MAX_VALUE)));
		junit.framework.Assert.assertEquals(java.lang.Integer.MIN_VALUE, f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(1, f.getDenominatorAsInt());
		try {
			f.multiply(((org.apache.commons.math.fraction.BigFraction)(null)));
			junit.framework.Assert.fail("expecting NullPointerException");
		} catch (java.lang.NullPointerException ex) {
		}
	}

	public void testSubtract() {
		org.apache.commons.math.fraction.BigFraction a = new org.apache.commons.math.fraction.BigFraction(1 , 2);
		org.apache.commons.math.fraction.BigFraction b = new org.apache.commons.math.fraction.BigFraction(2 , 3);
		assertFraction(0, 1, a.subtract(a));
		assertFraction(-1, 6, a.subtract(b));
		assertFraction(1, 6, b.subtract(a));
		assertFraction(0, 1, b.subtract(b));
		org.apache.commons.math.fraction.BigFraction f = new org.apache.commons.math.fraction.BigFraction(1 , 1);
		try {
			f.subtract(((org.apache.commons.math.fraction.BigFraction)(null)));
			junit.framework.Assert.fail("expecting NullPointerException");
		} catch (java.lang.NullPointerException ex) {
		}
		org.apache.commons.math.fraction.BigFraction f1 = new org.apache.commons.math.fraction.BigFraction(1 , (32768 * 3));
		org.apache.commons.math.fraction.BigFraction f2 = new org.apache.commons.math.fraction.BigFraction(1 , 59049);
		f = f1.subtract(f2);
		junit.framework.Assert.assertEquals(-13085, f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(1934917632, f.getDenominatorAsInt());
		f1 = new org.apache.commons.math.fraction.BigFraction(java.lang.Integer.MIN_VALUE , 3);
		f2 = new org.apache.commons.math.fraction.BigFraction(1 , 3).negate();
		f = f1.subtract(f2);
		junit.framework.Assert.assertEquals(((java.lang.Integer.MIN_VALUE) + 1), f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(3, f.getDenominatorAsInt());
		f1 = new org.apache.commons.math.fraction.BigFraction(java.lang.Integer.MAX_VALUE , 1);
		f2 = org.apache.commons.math.fraction.BigFraction.ONE;
		f = f1.subtract(f2);
		junit.framework.Assert.assertEquals(((java.lang.Integer.MAX_VALUE) - 1), f.getNumeratorAsInt());
		junit.framework.Assert.assertEquals(1, f.getDenominatorAsInt());
	}

	public void testBigDecimalValue() {
		junit.framework.Assert.assertEquals(new java.math.BigDecimal(0.5), new org.apache.commons.math.fraction.BigFraction(1 , 2).bigDecimalValue());
		junit.framework.Assert.assertEquals(new java.math.BigDecimal("0.0003"), new org.apache.commons.math.fraction.BigFraction(3 , 10000).bigDecimalValue());
		junit.framework.Assert.assertEquals(new java.math.BigDecimal("0"), new org.apache.commons.math.fraction.BigFraction(1 , 3).bigDecimalValue(java.math.BigDecimal.ROUND_DOWN));
		junit.framework.Assert.assertEquals(new java.math.BigDecimal("0.333"), new org.apache.commons.math.fraction.BigFraction(1 , 3).bigDecimalValue(3, java.math.BigDecimal.ROUND_DOWN));
	}

	public void testEqualsAndHashCode() {
		org.apache.commons.math.fraction.BigFraction zero = new org.apache.commons.math.fraction.BigFraction(0 , 1);
		org.apache.commons.math.fraction.BigFraction nullFraction = null;
		junit.framework.Assert.assertTrue(zero.equals(zero));
		junit.framework.Assert.assertFalse(zero.equals(nullFraction));
		junit.framework.Assert.assertFalse(zero.equals(java.lang.Double.valueOf(0)));
		org.apache.commons.math.fraction.BigFraction zero2 = new org.apache.commons.math.fraction.BigFraction(0 , 2);
		junit.framework.Assert.assertTrue(zero.equals(zero2));
		junit.framework.Assert.assertEquals(zero.hashCode(), zero2.hashCode());
		org.apache.commons.math.fraction.BigFraction one = new org.apache.commons.math.fraction.BigFraction(1 , 1);
		junit.framework.Assert.assertFalse(((one.equals(zero)) || (zero.equals(one))));
		junit.framework.Assert.assertTrue(one.equals(org.apache.commons.math.fraction.BigFraction.ONE));
	}

	public void testGetReducedFraction() {
		org.apache.commons.math.fraction.BigFraction threeFourths = new org.apache.commons.math.fraction.BigFraction(3 , 4);
		junit.framework.Assert.assertTrue(threeFourths.equals(org.apache.commons.math.fraction.BigFraction.getReducedFraction(6, 8)));
		junit.framework.Assert.assertTrue(org.apache.commons.math.fraction.BigFraction.ZERO.equals(org.apache.commons.math.fraction.BigFraction.getReducedFraction(0, -1)));
		try {
			org.apache.commons.math.fraction.BigFraction.getReducedFraction(1, 0);
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
		junit.framework.Assert.assertEquals(org.apache.commons.math.fraction.BigFraction.getReducedFraction(2, java.lang.Integer.MIN_VALUE).getNumeratorAsInt(), -1);
		junit.framework.Assert.assertEquals(org.apache.commons.math.fraction.BigFraction.getReducedFraction(1, -1).getNumeratorAsInt(), -1);
	}

	public void testPow() {
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.BigFraction(8192 , 1594323), new org.apache.commons.math.fraction.BigFraction(2 , 3).pow(13));
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.BigFraction(8192 , 1594323), new org.apache.commons.math.fraction.BigFraction(2 , 3).pow(13L));
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.BigFraction(8192 , 1594323), new org.apache.commons.math.fraction.BigFraction(2 , 3).pow(java.math.BigInteger.valueOf(13L)));
		junit.framework.Assert.assertEquals(org.apache.commons.math.fraction.BigFraction.ONE, new org.apache.commons.math.fraction.BigFraction(2 , 3).pow(0));
		junit.framework.Assert.assertEquals(org.apache.commons.math.fraction.BigFraction.ONE, new org.apache.commons.math.fraction.BigFraction(2 , 3).pow(0L));
		junit.framework.Assert.assertEquals(org.apache.commons.math.fraction.BigFraction.ONE, new org.apache.commons.math.fraction.BigFraction(2 , 3).pow(java.math.BigInteger.valueOf(0L)));
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.BigFraction(1594323 , 8192), new org.apache.commons.math.fraction.BigFraction(2 , 3).pow(-13));
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.BigFraction(1594323 , 8192), new org.apache.commons.math.fraction.BigFraction(2 , 3).pow(-13L));
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.BigFraction(1594323 , 8192), new org.apache.commons.math.fraction.BigFraction(2 , 3).pow(java.math.BigInteger.valueOf(-13L)));
	}

	public void testMath340() {
		org.apache.commons.math.fraction.BigFraction fractionA = new org.apache.commons.math.fraction.BigFraction(0.00131);
		org.apache.commons.math.fraction.BigFraction fractionB = new org.apache.commons.math.fraction.BigFraction(0.37).reciprocal();
		org.apache.commons.math.fraction.BigFraction errorResult = fractionA.multiply(fractionB);
		org.apache.commons.math.fraction.BigFraction correctResult = new org.apache.commons.math.fraction.BigFraction(fractionA.getNumerator().multiply(fractionB.getNumerator()) , fractionA.getDenominator().multiply(fractionB.getDenominator()));
		junit.framework.Assert.assertEquals(correctResult, errorResult);
	}

	public void testSerial() throws org.apache.commons.math.fraction.FractionConversionException {
		org.apache.commons.math.fraction.BigFraction[] fractions = new org.apache.commons.math.fraction.BigFraction[]{ new org.apache.commons.math.fraction.BigFraction(3 , 4) , org.apache.commons.math.fraction.BigFraction.ONE , org.apache.commons.math.fraction.BigFraction.ZERO , new org.apache.commons.math.fraction.BigFraction(17) , new org.apache.commons.math.fraction.BigFraction(java.lang.Math.PI , 1000) , new org.apache.commons.math.fraction.BigFraction(-5 , 2) };
		for (org.apache.commons.math.fraction.BigFraction fraction : fractions) {
			junit.framework.Assert.assertEquals(fraction, org.apache.commons.math.TestUtils.serializeAndRecover(fraction));
		}
	}
}

