package org.apache.commons.math.util;


public final class MathUtilsTest extends junit.framework.TestCase {
	public MathUtilsTest(java.lang.String name) {
		super(name);
	}

	private static final java.util.List<java.util.Map<java.lang.Integer, java.lang.Long>> binomialCache = new java.util.ArrayList<java.util.Map<java.lang.Integer, java.lang.Long>>();

	private long binomialCoefficient(int n, int k) throws java.lang.ArithmeticException {
		if ((binomialCache.size()) > n) {
			java.lang.Long cachedResult = binomialCache.get(n).get(java.lang.Integer.valueOf(k));
			if (cachedResult != null) {
				return cachedResult.longValue();
			} 
		} 
		long result = -1;
		if ((n == k) || (k == 0)) {
			result = 1;
		} else if ((k == 1) || (k == (n - 1))) {
			result = n;
		} else {
			if (k < (n - 100)) {
				binomialCoefficient((n - 100), k);
			} 
			if (k > 100) {
				binomialCoefficient((n - 100), (k - 100));
			} 
			result = org.apache.commons.math.util.MathUtils.addAndCheck(binomialCoefficient((n - 1), (k - 1)), binomialCoefficient((n - 1), k));
		}
		if (result == (-1)) {
			throw new java.lang.ArithmeticException("error computing binomial coefficient");
		} 
		for (int i = binomialCache.size() ; i < (n + 1) ; i++) {
			binomialCache.add(new java.util.HashMap<java.lang.Integer, java.lang.Long>());
		}
		binomialCache.get(n).put(java.lang.Integer.valueOf(k), java.lang.Long.valueOf(result));
		return result;
	}

	private long factorial(int n) {
		long result = 1;
		for (int i = 2 ; i <= n ; i++) {
			result *= i;
		}
		return result;
	}

	public void test0Choose0() {
		junit.framework.Assert.assertEquals(org.apache.commons.math.util.MathUtils.binomialCoefficientDouble(0, 0), 1.0, 0);
		junit.framework.Assert.assertEquals(org.apache.commons.math.util.MathUtils.binomialCoefficientLog(0, 0), 0.0, 0);
		junit.framework.Assert.assertEquals(org.apache.commons.math.util.MathUtils.binomialCoefficient(0, 0), 1);
	}

	public void testAddAndCheck() {
		int big = java.lang.Integer.MAX_VALUE;
		int bigNeg = java.lang.Integer.MIN_VALUE;
		junit.framework.Assert.assertEquals(big, org.apache.commons.math.util.MathUtils.addAndCheck(big, 0));
		try {
			org.apache.commons.math.util.MathUtils.addAndCheck(big, 1);
			junit.framework.Assert.fail("Expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
		try {
			org.apache.commons.math.util.MathUtils.addAndCheck(bigNeg, -1);
			junit.framework.Assert.fail("Expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
	}

	public void testAddAndCheckLong() {
		long max = java.lang.Long.MAX_VALUE;
		long min = java.lang.Long.MIN_VALUE;
		junit.framework.Assert.assertEquals(max, org.apache.commons.math.util.MathUtils.addAndCheck(max, 0L));
		junit.framework.Assert.assertEquals(min, org.apache.commons.math.util.MathUtils.addAndCheck(min, 0L));
		junit.framework.Assert.assertEquals(max, org.apache.commons.math.util.MathUtils.addAndCheck(0L, max));
		junit.framework.Assert.assertEquals(min, org.apache.commons.math.util.MathUtils.addAndCheck(0L, min));
		junit.framework.Assert.assertEquals(1, org.apache.commons.math.util.MathUtils.addAndCheck(-1L, 2L));
		junit.framework.Assert.assertEquals(1, org.apache.commons.math.util.MathUtils.addAndCheck(2L, -1L));
		junit.framework.Assert.assertEquals(-3, org.apache.commons.math.util.MathUtils.addAndCheck(-2L, -1L));
		junit.framework.Assert.assertEquals(min, org.apache.commons.math.util.MathUtils.addAndCheck((min + 1), -1L));
		testAddAndCheckLongFailure(max, 1L);
		testAddAndCheckLongFailure(min, -1L);
		testAddAndCheckLongFailure(1L, max);
		testAddAndCheckLongFailure(-1L, min);
	}

	private void testAddAndCheckLongFailure(long a, long b) {
		try {
			org.apache.commons.math.util.MathUtils.addAndCheck(a, b);
			junit.framework.Assert.fail("Expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
	}

	public void testBinomialCoefficient() {
		long[] bcoef5 = new long[]{ 1 , 5 , 10 , 10 , 5 , 1 };
		long[] bcoef6 = new long[]{ 1 , 6 , 15 , 20 , 15 , 6 , 1 };
		for (int i = 0 ; i < 6 ; i++) {
			junit.framework.Assert.assertEquals(("5 choose " + i), bcoef5[i], org.apache.commons.math.util.MathUtils.binomialCoefficient(5, i));
		}
		for (int i = 0 ; i < 7 ; i++) {
			junit.framework.Assert.assertEquals(("6 choose " + i), bcoef6[i], org.apache.commons.math.util.MathUtils.binomialCoefficient(6, i));
		}
		for (int n = 1 ; n < 10 ; n++) {
			for (int k = 0 ; k <= n ; k++) {
				junit.framework.Assert.assertEquals(((n + " choose ") + k), binomialCoefficient(n, k), org.apache.commons.math.util.MathUtils.binomialCoefficient(n, k));
				junit.framework.Assert.assertEquals(((n + " choose ") + k), binomialCoefficient(n, k), org.apache.commons.math.util.MathUtils.binomialCoefficientDouble(n, k), java.lang.Double.MIN_VALUE);
				junit.framework.Assert.assertEquals(((n + " choose ") + k), java.lang.Math.log(binomialCoefficient(n, k)), org.apache.commons.math.util.MathUtils.binomialCoefficientLog(n, k), 1.0E-11);
			}
		}
		int[] n = new int[]{ 34 , 66 , 100 , 1500 , 1500 };
		int[] k = new int[]{ 17 , 33 , 10 , 1500 - 4 , 4 };
		for (int i = 0 ; i < (n.length) ; i++) {
			long expected = binomialCoefficient(n[i], k[i]);
			junit.framework.Assert.assertEquals((((n[i]) + " choose ") + (k[i])), expected, org.apache.commons.math.util.MathUtils.binomialCoefficient(n[i], k[i]));
			junit.framework.Assert.assertEquals((((n[i]) + " choose ") + (k[i])), expected, org.apache.commons.math.util.MathUtils.binomialCoefficientDouble(n[i], k[i]), 0.0);
			junit.framework.Assert.assertEquals((((("log(" + (n[i])) + " choose ") + (k[i])) + ")"), java.lang.Math.log(expected), org.apache.commons.math.util.MathUtils.binomialCoefficientLog(n[i], k[i]), 0.0);
		}
	}

	public void testBinomialCoefficientLarge() throws java.lang.Exception {
		for (int n = 0 ; n <= 200 ; n++) {
			for (int k = 0 ; k <= n ; k++) {
				long ourResult = -1;
				long exactResult = -1;
				boolean shouldThrow = false;
				boolean didThrow = false;
				try {
					ourResult = org.apache.commons.math.util.MathUtils.binomialCoefficient(n, k);
				} catch (java.lang.ArithmeticException ex) {
					didThrow = true;
				}
				try {
					exactResult = binomialCoefficient(n, k);
				} catch (java.lang.ArithmeticException ex) {
					shouldThrow = true;
				}
				junit.framework.Assert.assertEquals(((n + " choose ") + k), exactResult, ourResult);
				junit.framework.Assert.assertEquals(((n + " choose ") + k), shouldThrow, didThrow);
				junit.framework.Assert.assertTrue(((n + " choose ") + k), ((n > 66) || (!didThrow)));
				if ((!shouldThrow) && (exactResult > 1)) {
					junit.framework.Assert.assertEquals(((n + " choose ") + k), 1.0, ((org.apache.commons.math.util.MathUtils.binomialCoefficientDouble(n, k)) / exactResult), 1.0E-10);
					junit.framework.Assert.assertEquals(((n + " choose ") + k), 1, ((org.apache.commons.math.util.MathUtils.binomialCoefficientLog(n, k)) / (java.lang.Math.log(exactResult))), 1.0E-10);
				} 
			}
		}
		long ourResult = org.apache.commons.math.util.MathUtils.binomialCoefficient(300, 3);
		long exactResult = binomialCoefficient(300, 3);
		junit.framework.Assert.assertEquals(exactResult, ourResult);
		ourResult = org.apache.commons.math.util.MathUtils.binomialCoefficient(700, 697);
		exactResult = binomialCoefficient(700, 697);
		junit.framework.Assert.assertEquals(exactResult, ourResult);
		try {
			org.apache.commons.math.util.MathUtils.binomialCoefficient(700, 300);
			junit.framework.Assert.fail("Expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
		int n = 10000;
		ourResult = org.apache.commons.math.util.MathUtils.binomialCoefficient(n, 3);
		exactResult = binomialCoefficient(n, 3);
		junit.framework.Assert.assertEquals(exactResult, ourResult);
		junit.framework.Assert.assertEquals(1, ((org.apache.commons.math.util.MathUtils.binomialCoefficientDouble(n, 3)) / exactResult), 1.0E-10);
		junit.framework.Assert.assertEquals(1, ((org.apache.commons.math.util.MathUtils.binomialCoefficientLog(n, 3)) / (java.lang.Math.log(exactResult))), 1.0E-10);
	}

	public void testBinomialCoefficientFail() {
		try {
			org.apache.commons.math.util.MathUtils.binomialCoefficient(4, 5);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.util.MathUtils.binomialCoefficientDouble(4, 5);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.util.MathUtils.binomialCoefficientLog(4, 5);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.util.MathUtils.binomialCoefficient(-1, -2);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.util.MathUtils.binomialCoefficientDouble(-1, -2);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.util.MathUtils.binomialCoefficientLog(-1, -2);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.util.MathUtils.binomialCoefficient(67, 30);
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
		try {
			org.apache.commons.math.util.MathUtils.binomialCoefficient(67, 34);
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
		double x = org.apache.commons.math.util.MathUtils.binomialCoefficientDouble(1030, 515);
		junit.framework.Assert.assertTrue("expecting infinite binomial coefficient", java.lang.Double.isInfinite(x));
	}

	public void testCompareTo() {
		junit.framework.Assert.assertEquals(0, org.apache.commons.math.util.MathUtils.compareTo(152.33, 152.32, 0.011));
		junit.framework.Assert.assertTrue(((org.apache.commons.math.util.MathUtils.compareTo(152.308, 152.32, 0.011)) < 0));
		junit.framework.Assert.assertTrue(((org.apache.commons.math.util.MathUtils.compareTo(152.33, 152.318, 0.011)) > 0));
	}

	public void testCosh() {
		double x = 3.0;
		double expected = 10.06766;
		junit.framework.Assert.assertEquals(expected, org.apache.commons.math.util.MathUtils.cosh(x), 1.0E-5);
	}

	public void testCoshNaN() {
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(org.apache.commons.math.util.MathUtils.cosh(java.lang.Double.NaN)));
	}

	public void testEquals() {
		double[] testArray = new double[]{ java.lang.Double.NaN , java.lang.Double.POSITIVE_INFINITY , java.lang.Double.NEGATIVE_INFINITY , 1.0 , 0.0 };
		for (int i = 0 ; i < (testArray.length) ; i++) {
			for (int j = 0 ; j < (testArray.length) ; j++) {
				if (i == j) {
					junit.framework.Assert.assertTrue(org.apache.commons.math.util.MathUtils.equals(testArray[i], testArray[j]));
					junit.framework.Assert.assertTrue(org.apache.commons.math.util.MathUtils.equals(testArray[j], testArray[i]));
				} else {
					junit.framework.Assert.assertTrue(!(org.apache.commons.math.util.MathUtils.equals(testArray[i], testArray[j])));
					junit.framework.Assert.assertTrue(!(org.apache.commons.math.util.MathUtils.equals(testArray[j], testArray[i])));
				}
			}
		}
	}

	public void testEqualsWithAllowedDelta() {
		junit.framework.Assert.assertTrue(org.apache.commons.math.util.MathUtils.equals(153.0, 153.0, 0.0625));
		junit.framework.Assert.assertTrue(org.apache.commons.math.util.MathUtils.equals(153.0, 153.0625, 0.0625));
		junit.framework.Assert.assertTrue(org.apache.commons.math.util.MathUtils.equals(152.9375, 153.0, 0.0625));
		junit.framework.Assert.assertTrue(org.apache.commons.math.util.MathUtils.equals(java.lang.Double.NaN, java.lang.Double.NaN, 1.0));
		junit.framework.Assert.assertTrue(org.apache.commons.math.util.MathUtils.equals(java.lang.Double.POSITIVE_INFINITY, java.lang.Double.POSITIVE_INFINITY, 1.0));
		junit.framework.Assert.assertTrue(org.apache.commons.math.util.MathUtils.equals(java.lang.Double.NEGATIVE_INFINITY, java.lang.Double.NEGATIVE_INFINITY, 1.0));
		junit.framework.Assert.assertFalse(org.apache.commons.math.util.MathUtils.equals(java.lang.Double.NEGATIVE_INFINITY, java.lang.Double.POSITIVE_INFINITY, 1.0));
		junit.framework.Assert.assertFalse(org.apache.commons.math.util.MathUtils.equals(153.0, 153.0625, 0.0624));
		junit.framework.Assert.assertFalse(org.apache.commons.math.util.MathUtils.equals(152.9374, 153.0, 0.0625));
	}

	public void testEqualsWithAllowedUlps() {
		junit.framework.Assert.assertTrue(org.apache.commons.math.util.MathUtils.equals(153, 153, 1));
		junit.framework.Assert.assertTrue(org.apache.commons.math.util.MathUtils.equals(153, 153.00000000000003, 1));
		junit.framework.Assert.assertFalse(org.apache.commons.math.util.MathUtils.equals(153, 153.00000000000006, 1));
		junit.framework.Assert.assertTrue(org.apache.commons.math.util.MathUtils.equals(153, 152.99999999999997, 1));
		junit.framework.Assert.assertFalse(org.apache.commons.math.util.MathUtils.equals(153, 152.99999999999994, 1));
		junit.framework.Assert.assertTrue(org.apache.commons.math.util.MathUtils.equals(-128, -127.99999999999999, 1));
		junit.framework.Assert.assertFalse(org.apache.commons.math.util.MathUtils.equals(-128, -127.99999999999997, 1));
		junit.framework.Assert.assertTrue(org.apache.commons.math.util.MathUtils.equals(-128, -128.00000000000003, 1));
		junit.framework.Assert.assertFalse(org.apache.commons.math.util.MathUtils.equals(-128, -128.00000000000006, 1));
		junit.framework.Assert.assertTrue(org.apache.commons.math.util.MathUtils.equals(java.lang.Double.POSITIVE_INFINITY, java.lang.Double.POSITIVE_INFINITY, 1));
		junit.framework.Assert.assertTrue(org.apache.commons.math.util.MathUtils.equals(java.lang.Double.MAX_VALUE, java.lang.Double.POSITIVE_INFINITY, 1));
		junit.framework.Assert.assertTrue(org.apache.commons.math.util.MathUtils.equals(java.lang.Double.NEGATIVE_INFINITY, java.lang.Double.NEGATIVE_INFINITY, 1));
		junit.framework.Assert.assertTrue(org.apache.commons.math.util.MathUtils.equals(-(java.lang.Double.MAX_VALUE), java.lang.Double.NEGATIVE_INFINITY, 1));
		junit.framework.Assert.assertTrue(org.apache.commons.math.util.MathUtils.equals(java.lang.Double.NaN, java.lang.Double.NaN, 1));
		junit.framework.Assert.assertFalse(org.apache.commons.math.util.MathUtils.equals(java.lang.Double.NEGATIVE_INFINITY, java.lang.Double.POSITIVE_INFINITY, 100000));
	}

	public void testArrayEquals() {
		junit.framework.Assert.assertFalse(org.apache.commons.math.util.MathUtils.equals(new double[]{ 1.0 }, null));
		junit.framework.Assert.assertFalse(org.apache.commons.math.util.MathUtils.equals(null, new double[]{ 1.0 }));
		junit.framework.Assert.assertTrue(org.apache.commons.math.util.MathUtils.equals(((double[])(null)), ((double[])(null))));
		junit.framework.Assert.assertFalse(org.apache.commons.math.util.MathUtils.equals(new double[]{ 1.0 }, new double[0]));
		junit.framework.Assert.assertTrue(org.apache.commons.math.util.MathUtils.equals(new double[]{ 1.0 }, new double[]{ 1.0 }));
		junit.framework.Assert.assertTrue(org.apache.commons.math.util.MathUtils.equals(new double[]{ java.lang.Double.NaN , java.lang.Double.POSITIVE_INFINITY , java.lang.Double.NEGATIVE_INFINITY , 1.0 , 0.0 }, new double[]{ java.lang.Double.NaN , java.lang.Double.POSITIVE_INFINITY , java.lang.Double.NEGATIVE_INFINITY , 1.0 , 0.0 }));
		junit.framework.Assert.assertFalse(org.apache.commons.math.util.MathUtils.equals(new double[]{ java.lang.Double.POSITIVE_INFINITY }, new double[]{ java.lang.Double.NEGATIVE_INFINITY }));
		junit.framework.Assert.assertFalse(org.apache.commons.math.util.MathUtils.equals(new double[]{ 1.0 }, new double[]{ org.apache.commons.math.util.MathUtils.nextAfter(1.0, 2.0) }));
	}

	public void testFactorial() {
		for (int i = 1 ; i < 21 ; i++) {
			junit.framework.Assert.assertEquals((i + "! "), factorial(i), org.apache.commons.math.util.MathUtils.factorial(i));
			junit.framework.Assert.assertEquals((i + "! "), factorial(i), org.apache.commons.math.util.MathUtils.factorialDouble(i), java.lang.Double.MIN_VALUE);
			junit.framework.Assert.assertEquals((i + "! "), java.lang.Math.log(factorial(i)), org.apache.commons.math.util.MathUtils.factorialLog(i), 1.0E-11);
		}
		junit.framework.Assert.assertEquals("0", 1, org.apache.commons.math.util.MathUtils.factorial(0));
		junit.framework.Assert.assertEquals("0", 1.0, org.apache.commons.math.util.MathUtils.factorialDouble(0), 1.0E-14);
		junit.framework.Assert.assertEquals("0", 0.0, org.apache.commons.math.util.MathUtils.factorialLog(0), 1.0E-14);
	}

	public void testFactorialFail() {
		try {
			org.apache.commons.math.util.MathUtils.factorial(-1);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.util.MathUtils.factorialDouble(-1);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.util.MathUtils.factorialLog(-1);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.util.MathUtils.factorial(21);
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
		junit.framework.Assert.assertTrue("expecting infinite factorial value", java.lang.Double.isInfinite(org.apache.commons.math.util.MathUtils.factorialDouble(171)));
	}

	public void testGcd() {
		int a = 30;
		int b = 50;
		int c = 77;
		junit.framework.Assert.assertEquals(0, org.apache.commons.math.util.MathUtils.gcd(0, 0));
		junit.framework.Assert.assertEquals(b, org.apache.commons.math.util.MathUtils.gcd(0, b));
		junit.framework.Assert.assertEquals(a, org.apache.commons.math.util.MathUtils.gcd(a, 0));
		junit.framework.Assert.assertEquals(b, org.apache.commons.math.util.MathUtils.gcd(0, -b));
		junit.framework.Assert.assertEquals(a, org.apache.commons.math.util.MathUtils.gcd(-a, 0));
		junit.framework.Assert.assertEquals(10, org.apache.commons.math.util.MathUtils.gcd(a, b));
		junit.framework.Assert.assertEquals(10, org.apache.commons.math.util.MathUtils.gcd(-a, b));
		junit.framework.Assert.assertEquals(10, org.apache.commons.math.util.MathUtils.gcd(a, -b));
		junit.framework.Assert.assertEquals(10, org.apache.commons.math.util.MathUtils.gcd(-a, -b));
		junit.framework.Assert.assertEquals(1, org.apache.commons.math.util.MathUtils.gcd(a, c));
		junit.framework.Assert.assertEquals(1, org.apache.commons.math.util.MathUtils.gcd(-a, c));
		junit.framework.Assert.assertEquals(1, org.apache.commons.math.util.MathUtils.gcd(a, -c));
		junit.framework.Assert.assertEquals(1, org.apache.commons.math.util.MathUtils.gcd(-a, -c));
		junit.framework.Assert.assertEquals((3 * (1 << 15)), org.apache.commons.math.util.MathUtils.gcd((3 * (1 << 20)), (9 * (1 << 15))));
		junit.framework.Assert.assertEquals(java.lang.Integer.MAX_VALUE, org.apache.commons.math.util.MathUtils.gcd(java.lang.Integer.MAX_VALUE, 0));
		junit.framework.Assert.assertEquals(java.lang.Integer.MAX_VALUE, org.apache.commons.math.util.MathUtils.gcd(-(java.lang.Integer.MAX_VALUE), 0));
		junit.framework.Assert.assertEquals((1 << 30), org.apache.commons.math.util.MathUtils.gcd((1 << 30), -(java.lang.Integer.MIN_VALUE)));
		try {
			org.apache.commons.math.util.MathUtils.gcd(java.lang.Integer.MIN_VALUE, 0);
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException expected) {
		}
		try {
			org.apache.commons.math.util.MathUtils.gcd(0, java.lang.Integer.MIN_VALUE);
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException expected) {
		}
		try {
			org.apache.commons.math.util.MathUtils.gcd(java.lang.Integer.MIN_VALUE, java.lang.Integer.MIN_VALUE);
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException expected) {
		}
	}

	public void testGcdLong() {
		long a = 30;
		long b = 50;
		long c = 77;
		junit.framework.Assert.assertEquals(0, org.apache.commons.math.util.MathUtils.gcd(0L, 0));
		junit.framework.Assert.assertEquals(b, org.apache.commons.math.util.MathUtils.gcd(0, b));
		junit.framework.Assert.assertEquals(a, org.apache.commons.math.util.MathUtils.gcd(a, 0));
		junit.framework.Assert.assertEquals(b, org.apache.commons.math.util.MathUtils.gcd(0, -b));
		junit.framework.Assert.assertEquals(a, org.apache.commons.math.util.MathUtils.gcd(-a, 0));
		junit.framework.Assert.assertEquals(10, org.apache.commons.math.util.MathUtils.gcd(a, b));
		junit.framework.Assert.assertEquals(10, org.apache.commons.math.util.MathUtils.gcd(-a, b));
		junit.framework.Assert.assertEquals(10, org.apache.commons.math.util.MathUtils.gcd(a, -b));
		junit.framework.Assert.assertEquals(10, org.apache.commons.math.util.MathUtils.gcd(-a, -b));
		junit.framework.Assert.assertEquals(1, org.apache.commons.math.util.MathUtils.gcd(a, c));
		junit.framework.Assert.assertEquals(1, org.apache.commons.math.util.MathUtils.gcd(-a, c));
		junit.framework.Assert.assertEquals(1, org.apache.commons.math.util.MathUtils.gcd(a, -c));
		junit.framework.Assert.assertEquals(1, org.apache.commons.math.util.MathUtils.gcd(-a, -c));
		junit.framework.Assert.assertEquals((3L * (1L << 45)), org.apache.commons.math.util.MathUtils.gcd((3L * (1L << 50)), (9L * (1L << 45))));
		junit.framework.Assert.assertEquals((1L << 45), org.apache.commons.math.util.MathUtils.gcd((1L << 45), java.lang.Long.MIN_VALUE));
		junit.framework.Assert.assertEquals(java.lang.Long.MAX_VALUE, org.apache.commons.math.util.MathUtils.gcd(java.lang.Long.MAX_VALUE, 0L));
		junit.framework.Assert.assertEquals(java.lang.Long.MAX_VALUE, org.apache.commons.math.util.MathUtils.gcd(-(java.lang.Long.MAX_VALUE), 0L));
		junit.framework.Assert.assertEquals(1, org.apache.commons.math.util.MathUtils.gcd(60247241209L, 153092023L));
		try {
			org.apache.commons.math.util.MathUtils.gcd(java.lang.Long.MIN_VALUE, 0);
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException expected) {
		}
		try {
			org.apache.commons.math.util.MathUtils.gcd(0, java.lang.Long.MIN_VALUE);
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException expected) {
		}
		try {
			org.apache.commons.math.util.MathUtils.gcd(java.lang.Long.MIN_VALUE, java.lang.Long.MIN_VALUE);
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException expected) {
		}
	}

	public void testGcdConsistency() {
		int[] primeList = new int[]{ 19 , 23 , 53 , 67 , 73 , 79 , 101 , 103 , 111 , 131 };
		java.util.ArrayList<java.lang.Integer> primes = new java.util.ArrayList<java.lang.Integer>();
		for (int i = 0 ; i < (primeList.length) ; i++) {
			primes.add(java.lang.Integer.valueOf(primeList[i]));
		}
		org.apache.commons.math.random.RandomDataImpl randomData = new org.apache.commons.math.random.RandomDataImpl();
		for (int i = 0 ; i < 20 ; i++) {
			java.lang.Object[] sample = randomData.nextSample(primes, 4);
			int p1 = ((java.lang.Integer)(sample[0])).intValue();
			int p2 = ((java.lang.Integer)(sample[1])).intValue();
			int p3 = ((java.lang.Integer)(sample[2])).intValue();
			int p4 = ((java.lang.Integer)(sample[3])).intValue();
			int i1 = (p1 * p2) * p3;
			int i2 = (p1 * p2) * p4;
			int gcd = p1 * p2;
			junit.framework.Assert.assertEquals(gcd, org.apache.commons.math.util.MathUtils.gcd(i1, i2));
			long l1 = i1;
			long l2 = i2;
			junit.framework.Assert.assertEquals(gcd, org.apache.commons.math.util.MathUtils.gcd(l1, l2));
		}
	}

	public void testHash() {
		double[] testArray = new double[]{ java.lang.Double.NaN , java.lang.Double.POSITIVE_INFINITY , java.lang.Double.NEGATIVE_INFINITY , 1.0 , 0.0 , 1.0E-14 , 1 + 1.0E-14 , java.lang.Double.MIN_VALUE , java.lang.Double.MAX_VALUE };
		for (int i = 0 ; i < (testArray.length) ; i++) {
			for (int j = 0 ; j < (testArray.length) ; j++) {
				if (i == j) {
					junit.framework.Assert.assertEquals(org.apache.commons.math.util.MathUtils.hash(testArray[i]), org.apache.commons.math.util.MathUtils.hash(testArray[j]));
					junit.framework.Assert.assertEquals(org.apache.commons.math.util.MathUtils.hash(testArray[j]), org.apache.commons.math.util.MathUtils.hash(testArray[i]));
				} else {
					junit.framework.Assert.assertTrue(((org.apache.commons.math.util.MathUtils.hash(testArray[i])) != (org.apache.commons.math.util.MathUtils.hash(testArray[j]))));
					junit.framework.Assert.assertTrue(((org.apache.commons.math.util.MathUtils.hash(testArray[j])) != (org.apache.commons.math.util.MathUtils.hash(testArray[i]))));
				}
			}
		}
	}

	public void testArrayHash() {
		junit.framework.Assert.assertEquals(0, org.apache.commons.math.util.MathUtils.hash(((double[])(null))));
		junit.framework.Assert.assertEquals(org.apache.commons.math.util.MathUtils.hash(new double[]{ java.lang.Double.NaN , java.lang.Double.POSITIVE_INFINITY , java.lang.Double.NEGATIVE_INFINITY , 1.0 , 0.0 }), org.apache.commons.math.util.MathUtils.hash(new double[]{ java.lang.Double.NaN , java.lang.Double.POSITIVE_INFINITY , java.lang.Double.NEGATIVE_INFINITY , 1.0 , 0.0 }));
		junit.framework.Assert.assertFalse(((org.apache.commons.math.util.MathUtils.hash(new double[]{ 1.0 })) == (org.apache.commons.math.util.MathUtils.hash(new double[]{ org.apache.commons.math.util.MathUtils.nextAfter(1.0, 2.0) }))));
		junit.framework.Assert.assertFalse(((org.apache.commons.math.util.MathUtils.hash(new double[]{ 1.0 })) == (org.apache.commons.math.util.MathUtils.hash(new double[]{ 1.0 , 1.0 }))));
	}

	public void testPermutedArrayHash() {
		double[] original = new double[10];
		double[] permuted = new double[10];
		org.apache.commons.math.random.RandomDataImpl random = new org.apache.commons.math.random.RandomDataImpl();
		for (int i = 0 ; i < 10 ; i++) {
			original[i] = random.nextUniform((i + 0.5), (i + 0.75));
		}
		boolean isIdentity = true;
		do {
			int[] permutation = random.nextPermutation(10, 10);
			for (int i = 0 ; i < 10 ; i++) {
				if (i != (permutation[i])) {
					isIdentity = false;
				} 
				permuted[i] = original[permutation[i]];
			}
		} while (isIdentity );
		junit.framework.Assert.assertFalse(((org.apache.commons.math.util.MathUtils.hash(original)) == (org.apache.commons.math.util.MathUtils.hash(permuted))));
	}

	public void testIndicatorByte() {
		junit.framework.Assert.assertEquals(((byte)(1)), org.apache.commons.math.util.MathUtils.indicator(((byte)(2))));
		junit.framework.Assert.assertEquals(((byte)(1)), org.apache.commons.math.util.MathUtils.indicator(((byte)(0))));
		junit.framework.Assert.assertEquals(((byte)(-1)), org.apache.commons.math.util.MathUtils.indicator(((byte)(-2))));
	}

	public void testIndicatorDouble() {
		double delta = 0.0;
		junit.framework.Assert.assertEquals(1.0, org.apache.commons.math.util.MathUtils.indicator(2.0), delta);
		junit.framework.Assert.assertEquals(1.0, org.apache.commons.math.util.MathUtils.indicator(0.0), delta);
		junit.framework.Assert.assertEquals(-1.0, org.apache.commons.math.util.MathUtils.indicator(-2.0), delta);
		junit.framework.Assert.assertEquals(java.lang.Double.NaN, org.apache.commons.math.util.MathUtils.indicator(java.lang.Double.NaN));
	}

	public void testIndicatorFloat() {
		float delta = 0.0F;
		junit.framework.Assert.assertEquals(1.0F, org.apache.commons.math.util.MathUtils.indicator(2.0F), delta);
		junit.framework.Assert.assertEquals(1.0F, org.apache.commons.math.util.MathUtils.indicator(0.0F), delta);
		junit.framework.Assert.assertEquals(-1.0F, org.apache.commons.math.util.MathUtils.indicator(-2.0F), delta);
	}

	public void testIndicatorInt() {
		junit.framework.Assert.assertEquals(1, org.apache.commons.math.util.MathUtils.indicator(2));
		junit.framework.Assert.assertEquals(1, org.apache.commons.math.util.MathUtils.indicator(0));
		junit.framework.Assert.assertEquals(-1, org.apache.commons.math.util.MathUtils.indicator(-2));
	}

	public void testIndicatorLong() {
		junit.framework.Assert.assertEquals(1L, org.apache.commons.math.util.MathUtils.indicator(2L));
		junit.framework.Assert.assertEquals(1L, org.apache.commons.math.util.MathUtils.indicator(0L));
		junit.framework.Assert.assertEquals(-1L, org.apache.commons.math.util.MathUtils.indicator(-2L));
	}

	public void testIndicatorShort() {
		junit.framework.Assert.assertEquals(((short)(1)), org.apache.commons.math.util.MathUtils.indicator(((short)(2))));
		junit.framework.Assert.assertEquals(((short)(1)), org.apache.commons.math.util.MathUtils.indicator(((short)(0))));
		junit.framework.Assert.assertEquals(((short)(-1)), org.apache.commons.math.util.MathUtils.indicator(((short)(-2))));
	}

	public void testLcm() {
		int a = 30;
		int b = 50;
		int c = 77;
		junit.framework.Assert.assertEquals(0, org.apache.commons.math.util.MathUtils.lcm(0, b));
		junit.framework.Assert.assertEquals(0, org.apache.commons.math.util.MathUtils.lcm(a, 0));
		junit.framework.Assert.assertEquals(b, org.apache.commons.math.util.MathUtils.lcm(1, b));
		junit.framework.Assert.assertEquals(a, org.apache.commons.math.util.MathUtils.lcm(a, 1));
		junit.framework.Assert.assertEquals(150, org.apache.commons.math.util.MathUtils.lcm(a, b));
		junit.framework.Assert.assertEquals(150, org.apache.commons.math.util.MathUtils.lcm(-a, b));
		junit.framework.Assert.assertEquals(150, org.apache.commons.math.util.MathUtils.lcm(a, -b));
		junit.framework.Assert.assertEquals(150, org.apache.commons.math.util.MathUtils.lcm(-a, -b));
		junit.framework.Assert.assertEquals(2310, org.apache.commons.math.util.MathUtils.lcm(a, c));
		junit.framework.Assert.assertEquals(((1 << 20) * 15), org.apache.commons.math.util.MathUtils.lcm(((1 << 20) * 3), ((1 << 20) * 5)));
		junit.framework.Assert.assertEquals(0, org.apache.commons.math.util.MathUtils.lcm(0, 0));
		try {
			org.apache.commons.math.util.MathUtils.lcm(java.lang.Integer.MIN_VALUE, 1);
			junit.framework.Assert.fail("Expecting ArithmeticException");
		} catch (java.lang.ArithmeticException expected) {
		}
		try {
			org.apache.commons.math.util.MathUtils.lcm(java.lang.Integer.MIN_VALUE, (1 << 20));
			junit.framework.Assert.fail("Expecting ArithmeticException");
		} catch (java.lang.ArithmeticException expected) {
		}
		try {
			org.apache.commons.math.util.MathUtils.lcm(java.lang.Integer.MAX_VALUE, ((java.lang.Integer.MAX_VALUE) - 1));
			junit.framework.Assert.fail("Expecting ArithmeticException");
		} catch (java.lang.ArithmeticException expected) {
		}
	}

	public void testLcmLong() {
		long a = 30;
		long b = 50;
		long c = 77;
		junit.framework.Assert.assertEquals(0, org.apache.commons.math.util.MathUtils.lcm(0, b));
		junit.framework.Assert.assertEquals(0, org.apache.commons.math.util.MathUtils.lcm(a, 0));
		junit.framework.Assert.assertEquals(b, org.apache.commons.math.util.MathUtils.lcm(1, b));
		junit.framework.Assert.assertEquals(a, org.apache.commons.math.util.MathUtils.lcm(a, 1));
		junit.framework.Assert.assertEquals(150, org.apache.commons.math.util.MathUtils.lcm(a, b));
		junit.framework.Assert.assertEquals(150, org.apache.commons.math.util.MathUtils.lcm(-a, b));
		junit.framework.Assert.assertEquals(150, org.apache.commons.math.util.MathUtils.lcm(a, -b));
		junit.framework.Assert.assertEquals(150, org.apache.commons.math.util.MathUtils.lcm(-a, -b));
		junit.framework.Assert.assertEquals(2310, org.apache.commons.math.util.MathUtils.lcm(a, c));
		junit.framework.Assert.assertEquals(java.lang.Long.MAX_VALUE, org.apache.commons.math.util.MathUtils.lcm(60247241209L, 153092023L));
		junit.framework.Assert.assertEquals(((1L << 50) * 15), org.apache.commons.math.util.MathUtils.lcm(((1L << 45) * 3), ((1L << 50) * 5)));
		junit.framework.Assert.assertEquals(0L, org.apache.commons.math.util.MathUtils.lcm(0L, 0L));
		try {
			org.apache.commons.math.util.MathUtils.lcm(java.lang.Long.MIN_VALUE, 1);
			junit.framework.Assert.fail("Expecting ArithmeticException");
		} catch (java.lang.ArithmeticException expected) {
		}
		try {
			org.apache.commons.math.util.MathUtils.lcm(java.lang.Long.MIN_VALUE, (1 << 20));
			junit.framework.Assert.fail("Expecting ArithmeticException");
		} catch (java.lang.ArithmeticException expected) {
		}
		junit.framework.Assert.assertEquals((((long)(java.lang.Integer.MAX_VALUE)) * ((java.lang.Integer.MAX_VALUE) - 1)), org.apache.commons.math.util.MathUtils.lcm(((long)(java.lang.Integer.MAX_VALUE)), ((java.lang.Integer.MAX_VALUE) - 1)));
		try {
			org.apache.commons.math.util.MathUtils.lcm(java.lang.Long.MAX_VALUE, ((java.lang.Long.MAX_VALUE) - 1));
			junit.framework.Assert.fail("Expecting ArithmeticException");
		} catch (java.lang.ArithmeticException expected) {
		}
	}

	public void testLog() {
		junit.framework.Assert.assertEquals(2.0, org.apache.commons.math.util.MathUtils.log(2, 4), 0);
		junit.framework.Assert.assertEquals(3.0, org.apache.commons.math.util.MathUtils.log(2, 8), 0);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(org.apache.commons.math.util.MathUtils.log(-1, 1)));
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(org.apache.commons.math.util.MathUtils.log(1, -1)));
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(org.apache.commons.math.util.MathUtils.log(0, 0)));
		junit.framework.Assert.assertEquals(0, org.apache.commons.math.util.MathUtils.log(0, 10), 0);
		junit.framework.Assert.assertEquals(java.lang.Double.NEGATIVE_INFINITY, org.apache.commons.math.util.MathUtils.log(10, 0), 0);
	}

	public void testMulAndCheck() {
		int big = java.lang.Integer.MAX_VALUE;
		int bigNeg = java.lang.Integer.MIN_VALUE;
		junit.framework.Assert.assertEquals(big, org.apache.commons.math.util.MathUtils.mulAndCheck(big, 1));
		try {
			org.apache.commons.math.util.MathUtils.mulAndCheck(big, 2);
			junit.framework.Assert.fail("Expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
		try {
			org.apache.commons.math.util.MathUtils.mulAndCheck(bigNeg, 2);
			junit.framework.Assert.fail("Expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
	}

	public void testMulAndCheckLong() {
		long max = java.lang.Long.MAX_VALUE;
		long min = java.lang.Long.MIN_VALUE;
		junit.framework.Assert.assertEquals(max, org.apache.commons.math.util.MathUtils.mulAndCheck(max, 1L));
		junit.framework.Assert.assertEquals(min, org.apache.commons.math.util.MathUtils.mulAndCheck(min, 1L));
		junit.framework.Assert.assertEquals(0L, org.apache.commons.math.util.MathUtils.mulAndCheck(max, 0L));
		junit.framework.Assert.assertEquals(0L, org.apache.commons.math.util.MathUtils.mulAndCheck(min, 0L));
		junit.framework.Assert.assertEquals(max, org.apache.commons.math.util.MathUtils.mulAndCheck(1L, max));
		junit.framework.Assert.assertEquals(min, org.apache.commons.math.util.MathUtils.mulAndCheck(1L, min));
		junit.framework.Assert.assertEquals(0L, org.apache.commons.math.util.MathUtils.mulAndCheck(0L, max));
		junit.framework.Assert.assertEquals(0L, org.apache.commons.math.util.MathUtils.mulAndCheck(0L, min));
		junit.framework.Assert.assertEquals(1L, org.apache.commons.math.util.MathUtils.mulAndCheck(-1L, -1L));
		junit.framework.Assert.assertEquals(min, org.apache.commons.math.util.MathUtils.mulAndCheck((min / 2), 2));
		testMulAndCheckLongFailure(max, 2L);
		testMulAndCheckLongFailure(2L, max);
		testMulAndCheckLongFailure(min, 2L);
		testMulAndCheckLongFailure(2L, min);
		testMulAndCheckLongFailure(min, -1L);
		testMulAndCheckLongFailure(-1L, min);
	}

	private void testMulAndCheckLongFailure(long a, long b) {
		try {
			org.apache.commons.math.util.MathUtils.mulAndCheck(a, b);
			junit.framework.Assert.fail("Expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
	}

	public void testNextAfter() {
		junit.framework.Assert.assertEquals(16.0, org.apache.commons.math.util.MathUtils.nextAfter(15.999999999999998, 34.27555555555555), 0.0);
		junit.framework.Assert.assertEquals(-15.999999999999996, org.apache.commons.math.util.MathUtils.nextAfter(-15.999999999999998, 34.27555555555555), 0.0);
		junit.framework.Assert.assertEquals(15.999999999999996, org.apache.commons.math.util.MathUtils.nextAfter(15.999999999999998, 2.142222222222222), 0.0);
		junit.framework.Assert.assertEquals(-15.999999999999996, org.apache.commons.math.util.MathUtils.nextAfter(-15.999999999999998, 2.142222222222222), 0.0);
		junit.framework.Assert.assertEquals(8.000000000000002, org.apache.commons.math.util.MathUtils.nextAfter(8.0, 34.27555555555555), 0.0);
		junit.framework.Assert.assertEquals(-7.999999999999999, org.apache.commons.math.util.MathUtils.nextAfter(-8.0, 34.27555555555555), 0.0);
		junit.framework.Assert.assertEquals(7.999999999999999, org.apache.commons.math.util.MathUtils.nextAfter(8.0, 2.142222222222222), 0.0);
		junit.framework.Assert.assertEquals(-7.999999999999999, org.apache.commons.math.util.MathUtils.nextAfter(-8.0, 2.142222222222222), 0.0);
		junit.framework.Assert.assertEquals(2.308922399667661E-4, org.apache.commons.math.util.MathUtils.nextAfter(2.3089223996676606E-4, 2.308922399667661E-4), 0.0);
		junit.framework.Assert.assertEquals(2.308922399667661E-4, org.apache.commons.math.util.MathUtils.nextAfter(2.3089223996676606E-4, 2.3089223996676606E-4), 0.0);
		junit.framework.Assert.assertEquals(2.3089223996676603E-4, org.apache.commons.math.util.MathUtils.nextAfter(2.3089223996676606E-4, 2.3089223996676603E-4), 0.0);
		junit.framework.Assert.assertEquals(2.3089223996676603E-4, org.apache.commons.math.util.MathUtils.nextAfter(2.3089223996676606E-4, -2.308922399667661E-4), 0.0);
		junit.framework.Assert.assertEquals(2.3089223996676603E-4, org.apache.commons.math.util.MathUtils.nextAfter(2.3089223996676606E-4, -2.3089223996676606E-4), 0.0);
		junit.framework.Assert.assertEquals(2.3089223996676603E-4, org.apache.commons.math.util.MathUtils.nextAfter(2.3089223996676606E-4, -2.3089223996676603E-4), 0.0);
		junit.framework.Assert.assertEquals(-2.3089223996676603E-4, org.apache.commons.math.util.MathUtils.nextAfter(-2.3089223996676606E-4, 2.308922399667661E-4), 0.0);
		junit.framework.Assert.assertEquals(-2.3089223996676603E-4, org.apache.commons.math.util.MathUtils.nextAfter(-2.3089223996676606E-4, 2.3089223996676606E-4), 0.0);
		junit.framework.Assert.assertEquals(-2.3089223996676603E-4, org.apache.commons.math.util.MathUtils.nextAfter(-2.3089223996676606E-4, 2.3089223996676603E-4), 0.0);
		junit.framework.Assert.assertEquals(-2.308922399667661E-4, org.apache.commons.math.util.MathUtils.nextAfter(-2.3089223996676606E-4, -2.308922399667661E-4), 0.0);
		junit.framework.Assert.assertEquals(-2.308922399667661E-4, org.apache.commons.math.util.MathUtils.nextAfter(-2.3089223996676606E-4, -2.3089223996676606E-4), 0.0);
		junit.framework.Assert.assertEquals(-2.3089223996676603E-4, org.apache.commons.math.util.MathUtils.nextAfter(-2.3089223996676606E-4, -2.3089223996676603E-4), 0.0);
	}

	public void testNextAfterSpecialCases() {
		junit.framework.Assert.assertTrue(java.lang.Double.isInfinite(org.apache.commons.math.util.MathUtils.nextAfter(java.lang.Double.NEGATIVE_INFINITY, 0)));
		junit.framework.Assert.assertTrue(java.lang.Double.isInfinite(org.apache.commons.math.util.MathUtils.nextAfter(java.lang.Double.POSITIVE_INFINITY, 0)));
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(org.apache.commons.math.util.MathUtils.nextAfter(java.lang.Double.NaN, 0)));
		junit.framework.Assert.assertTrue(java.lang.Double.isInfinite(org.apache.commons.math.util.MathUtils.nextAfter(java.lang.Double.MAX_VALUE, java.lang.Double.POSITIVE_INFINITY)));
		junit.framework.Assert.assertTrue(java.lang.Double.isInfinite(org.apache.commons.math.util.MathUtils.nextAfter(-(java.lang.Double.MAX_VALUE), java.lang.Double.NEGATIVE_INFINITY)));
		junit.framework.Assert.assertEquals(java.lang.Double.MIN_VALUE, org.apache.commons.math.util.MathUtils.nextAfter(0, 1), 0);
		junit.framework.Assert.assertEquals(-(java.lang.Double.MIN_VALUE), org.apache.commons.math.util.MathUtils.nextAfter(0, -1), 0);
		junit.framework.Assert.assertEquals(0, org.apache.commons.math.util.MathUtils.nextAfter(java.lang.Double.MIN_VALUE, -1), 0);
		junit.framework.Assert.assertEquals(0, org.apache.commons.math.util.MathUtils.nextAfter(-(java.lang.Double.MIN_VALUE), 1), 0);
	}

	public void testScalb() {
		junit.framework.Assert.assertEquals(0.0, org.apache.commons.math.util.MathUtils.scalb(0.0, 5), 1.0E-15);
		junit.framework.Assert.assertEquals(32.0, org.apache.commons.math.util.MathUtils.scalb(1.0, 5), 1.0E-15);
		junit.framework.Assert.assertEquals((1.0 / 32.0), org.apache.commons.math.util.MathUtils.scalb(1.0, -5), 1.0E-15);
		junit.framework.Assert.assertEquals(java.lang.Math.PI, org.apache.commons.math.util.MathUtils.scalb(java.lang.Math.PI, 0), 1.0E-15);
		junit.framework.Assert.assertTrue(java.lang.Double.isInfinite(org.apache.commons.math.util.MathUtils.scalb(java.lang.Double.POSITIVE_INFINITY, 1)));
		junit.framework.Assert.assertTrue(java.lang.Double.isInfinite(org.apache.commons.math.util.MathUtils.scalb(java.lang.Double.NEGATIVE_INFINITY, 1)));
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(org.apache.commons.math.util.MathUtils.scalb(java.lang.Double.NaN, 1)));
	}

	public void testNormalizeAngle() {
		for (double a = -15.0 ; a <= 15.0 ; a += 0.1) {
			for (double b = -15.0 ; b <= 15.0 ; b += 0.2) {
				double c = org.apache.commons.math.util.MathUtils.normalizeAngle(a, b);
				junit.framework.Assert.assertTrue(((b - (java.lang.Math.PI)) <= c));
				junit.framework.Assert.assertTrue((c <= (b + (java.lang.Math.PI))));
				double twoK = java.lang.Math.rint(((a - c) / (java.lang.Math.PI)));
				junit.framework.Assert.assertEquals(c, (a - (twoK * (java.lang.Math.PI))), 1.0E-14);
			}
		}
	}

	public void testNormalizeArray() {
		double[] testValues1 = new double[]{ 1 , 1 , 2 };
		org.apache.commons.math.TestUtils.assertEquals(new double[]{ 0.25 , 0.25 , 0.5 }, org.apache.commons.math.util.MathUtils.normalizeArray(testValues1, 1), java.lang.Double.MIN_VALUE);
		double[] testValues2 = new double[]{ -1 , -1 , 1 };
		org.apache.commons.math.TestUtils.assertEquals(new double[]{ 1 , 1 , -1 }, org.apache.commons.math.util.MathUtils.normalizeArray(testValues2, 1), java.lang.Double.MIN_VALUE);
		double[] testValues3 = new double[]{ -1 , -1 , java.lang.Double.NaN , 1 , java.lang.Double.NaN };
		org.apache.commons.math.TestUtils.assertEquals(new double[]{ 1 , 1 , java.lang.Double.NaN , -1 , java.lang.Double.NaN }, org.apache.commons.math.util.MathUtils.normalizeArray(testValues3, 1), java.lang.Double.MIN_VALUE);
		double[] zeroSum = new double[]{ -1 , 1 };
		try {
			org.apache.commons.math.util.MathUtils.normalizeArray(zeroSum, 1);
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
		double[] hasInf = new double[]{ 1 , 2 , 1 , java.lang.Double.NEGATIVE_INFINITY };
		try {
			org.apache.commons.math.util.MathUtils.normalizeArray(hasInf, 1);
			junit.framework.Assert.fail("expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
		try {
			org.apache.commons.math.util.MathUtils.normalizeArray(testValues1, java.lang.Double.POSITIVE_INFINITY);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.util.MathUtils.normalizeArray(testValues1, java.lang.Double.NaN);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testRoundDouble() {
		double x = 1.23456789;
		junit.framework.Assert.assertEquals(1.23, org.apache.commons.math.util.MathUtils.round(x, 2), 0.0);
		junit.framework.Assert.assertEquals(1.235, org.apache.commons.math.util.MathUtils.round(x, 3), 0.0);
		junit.framework.Assert.assertEquals(1.2346, org.apache.commons.math.util.MathUtils.round(x, 4), 0.0);
		junit.framework.Assert.assertEquals(39.25, org.apache.commons.math.util.MathUtils.round(39.245, 2), 0.0);
		junit.framework.Assert.assertEquals(39.24, org.apache.commons.math.util.MathUtils.round(39.245, 2, java.math.BigDecimal.ROUND_DOWN), 0.0);
		double xx = 39.0;
		xx = xx + (245.0 / 1000.0);
		junit.framework.Assert.assertEquals(39.25, org.apache.commons.math.util.MathUtils.round(xx, 2), 0.0);
		junit.framework.Assert.assertEquals(30.1, org.apache.commons.math.util.MathUtils.round(30.095, 2), 0.0);
		junit.framework.Assert.assertEquals(30.1, org.apache.commons.math.util.MathUtils.round(30.095, 1), 0.0);
		junit.framework.Assert.assertEquals(33.1, org.apache.commons.math.util.MathUtils.round(33.095, 1), 0.0);
		junit.framework.Assert.assertEquals(33.1, org.apache.commons.math.util.MathUtils.round(33.095, 2), 0.0);
		junit.framework.Assert.assertEquals(50.09, org.apache.commons.math.util.MathUtils.round(50.085, 2), 0.0);
		junit.framework.Assert.assertEquals(50.19, org.apache.commons.math.util.MathUtils.round(50.185, 2), 0.0);
		junit.framework.Assert.assertEquals(50.01, org.apache.commons.math.util.MathUtils.round(50.005, 2), 0.0);
		junit.framework.Assert.assertEquals(30.01, org.apache.commons.math.util.MathUtils.round(30.005, 2), 0.0);
		junit.framework.Assert.assertEquals(30.65, org.apache.commons.math.util.MathUtils.round(30.645, 2), 0.0);
		junit.framework.Assert.assertEquals(1.24, org.apache.commons.math.util.MathUtils.round(x, 2, java.math.BigDecimal.ROUND_CEILING), 0.0);
		junit.framework.Assert.assertEquals(1.235, org.apache.commons.math.util.MathUtils.round(x, 3, java.math.BigDecimal.ROUND_CEILING), 0.0);
		junit.framework.Assert.assertEquals(1.2346, org.apache.commons.math.util.MathUtils.round(x, 4, java.math.BigDecimal.ROUND_CEILING), 0.0);
		junit.framework.Assert.assertEquals(-1.23, org.apache.commons.math.util.MathUtils.round(-x, 2, java.math.BigDecimal.ROUND_CEILING), 0.0);
		junit.framework.Assert.assertEquals(-1.234, org.apache.commons.math.util.MathUtils.round(-x, 3, java.math.BigDecimal.ROUND_CEILING), 0.0);
		junit.framework.Assert.assertEquals(-1.2345, org.apache.commons.math.util.MathUtils.round(-x, 4, java.math.BigDecimal.ROUND_CEILING), 0.0);
		junit.framework.Assert.assertEquals(1.23, org.apache.commons.math.util.MathUtils.round(x, 2, java.math.BigDecimal.ROUND_DOWN), 0.0);
		junit.framework.Assert.assertEquals(1.234, org.apache.commons.math.util.MathUtils.round(x, 3, java.math.BigDecimal.ROUND_DOWN), 0.0);
		junit.framework.Assert.assertEquals(1.2345, org.apache.commons.math.util.MathUtils.round(x, 4, java.math.BigDecimal.ROUND_DOWN), 0.0);
		junit.framework.Assert.assertEquals(-1.23, org.apache.commons.math.util.MathUtils.round(-x, 2, java.math.BigDecimal.ROUND_DOWN), 0.0);
		junit.framework.Assert.assertEquals(-1.234, org.apache.commons.math.util.MathUtils.round(-x, 3, java.math.BigDecimal.ROUND_DOWN), 0.0);
		junit.framework.Assert.assertEquals(-1.2345, org.apache.commons.math.util.MathUtils.round(-x, 4, java.math.BigDecimal.ROUND_DOWN), 0.0);
		junit.framework.Assert.assertEquals(1.23, org.apache.commons.math.util.MathUtils.round(x, 2, java.math.BigDecimal.ROUND_FLOOR), 0.0);
		junit.framework.Assert.assertEquals(1.234, org.apache.commons.math.util.MathUtils.round(x, 3, java.math.BigDecimal.ROUND_FLOOR), 0.0);
		junit.framework.Assert.assertEquals(1.2345, org.apache.commons.math.util.MathUtils.round(x, 4, java.math.BigDecimal.ROUND_FLOOR), 0.0);
		junit.framework.Assert.assertEquals(-1.24, org.apache.commons.math.util.MathUtils.round(-x, 2, java.math.BigDecimal.ROUND_FLOOR), 0.0);
		junit.framework.Assert.assertEquals(-1.235, org.apache.commons.math.util.MathUtils.round(-x, 3, java.math.BigDecimal.ROUND_FLOOR), 0.0);
		junit.framework.Assert.assertEquals(-1.2346, org.apache.commons.math.util.MathUtils.round(-x, 4, java.math.BigDecimal.ROUND_FLOOR), 0.0);
		junit.framework.Assert.assertEquals(1.23, org.apache.commons.math.util.MathUtils.round(x, 2, java.math.BigDecimal.ROUND_HALF_DOWN), 0.0);
		junit.framework.Assert.assertEquals(1.235, org.apache.commons.math.util.MathUtils.round(x, 3, java.math.BigDecimal.ROUND_HALF_DOWN), 0.0);
		junit.framework.Assert.assertEquals(1.2346, org.apache.commons.math.util.MathUtils.round(x, 4, java.math.BigDecimal.ROUND_HALF_DOWN), 0.0);
		junit.framework.Assert.assertEquals(-1.23, org.apache.commons.math.util.MathUtils.round(-x, 2, java.math.BigDecimal.ROUND_HALF_DOWN), 0.0);
		junit.framework.Assert.assertEquals(-1.235, org.apache.commons.math.util.MathUtils.round(-x, 3, java.math.BigDecimal.ROUND_HALF_DOWN), 0.0);
		junit.framework.Assert.assertEquals(-1.2346, org.apache.commons.math.util.MathUtils.round(-x, 4, java.math.BigDecimal.ROUND_HALF_DOWN), 0.0);
		junit.framework.Assert.assertEquals(1.234, org.apache.commons.math.util.MathUtils.round(1.2345, 3, java.math.BigDecimal.ROUND_HALF_DOWN), 0.0);
		junit.framework.Assert.assertEquals(-1.234, org.apache.commons.math.util.MathUtils.round(-1.2345, 3, java.math.BigDecimal.ROUND_HALF_DOWN), 0.0);
		junit.framework.Assert.assertEquals(1.23, org.apache.commons.math.util.MathUtils.round(x, 2, java.math.BigDecimal.ROUND_HALF_EVEN), 0.0);
		junit.framework.Assert.assertEquals(1.235, org.apache.commons.math.util.MathUtils.round(x, 3, java.math.BigDecimal.ROUND_HALF_EVEN), 0.0);
		junit.framework.Assert.assertEquals(1.2346, org.apache.commons.math.util.MathUtils.round(x, 4, java.math.BigDecimal.ROUND_HALF_EVEN), 0.0);
		junit.framework.Assert.assertEquals(-1.23, org.apache.commons.math.util.MathUtils.round(-x, 2, java.math.BigDecimal.ROUND_HALF_EVEN), 0.0);
		junit.framework.Assert.assertEquals(-1.235, org.apache.commons.math.util.MathUtils.round(-x, 3, java.math.BigDecimal.ROUND_HALF_EVEN), 0.0);
		junit.framework.Assert.assertEquals(-1.2346, org.apache.commons.math.util.MathUtils.round(-x, 4, java.math.BigDecimal.ROUND_HALF_EVEN), 0.0);
		junit.framework.Assert.assertEquals(1.234, org.apache.commons.math.util.MathUtils.round(1.2345, 3, java.math.BigDecimal.ROUND_HALF_EVEN), 0.0);
		junit.framework.Assert.assertEquals(-1.234, org.apache.commons.math.util.MathUtils.round(-1.2345, 3, java.math.BigDecimal.ROUND_HALF_EVEN), 0.0);
		junit.framework.Assert.assertEquals(1.236, org.apache.commons.math.util.MathUtils.round(1.2355, 3, java.math.BigDecimal.ROUND_HALF_EVEN), 0.0);
		junit.framework.Assert.assertEquals(-1.236, org.apache.commons.math.util.MathUtils.round(-1.2355, 3, java.math.BigDecimal.ROUND_HALF_EVEN), 0.0);
		junit.framework.Assert.assertEquals(1.23, org.apache.commons.math.util.MathUtils.round(x, 2, java.math.BigDecimal.ROUND_HALF_UP), 0.0);
		junit.framework.Assert.assertEquals(1.235, org.apache.commons.math.util.MathUtils.round(x, 3, java.math.BigDecimal.ROUND_HALF_UP), 0.0);
		junit.framework.Assert.assertEquals(1.2346, org.apache.commons.math.util.MathUtils.round(x, 4, java.math.BigDecimal.ROUND_HALF_UP), 0.0);
		junit.framework.Assert.assertEquals(-1.23, org.apache.commons.math.util.MathUtils.round(-x, 2, java.math.BigDecimal.ROUND_HALF_UP), 0.0);
		junit.framework.Assert.assertEquals(-1.235, org.apache.commons.math.util.MathUtils.round(-x, 3, java.math.BigDecimal.ROUND_HALF_UP), 0.0);
		junit.framework.Assert.assertEquals(-1.2346, org.apache.commons.math.util.MathUtils.round(-x, 4, java.math.BigDecimal.ROUND_HALF_UP), 0.0);
		junit.framework.Assert.assertEquals(1.235, org.apache.commons.math.util.MathUtils.round(1.2345, 3, java.math.BigDecimal.ROUND_HALF_UP), 0.0);
		junit.framework.Assert.assertEquals(-1.235, org.apache.commons.math.util.MathUtils.round(-1.2345, 3, java.math.BigDecimal.ROUND_HALF_UP), 0.0);
		junit.framework.Assert.assertEquals(-1.23, org.apache.commons.math.util.MathUtils.round(-1.23, 2, java.math.BigDecimal.ROUND_UNNECESSARY), 0.0);
		junit.framework.Assert.assertEquals(1.23, org.apache.commons.math.util.MathUtils.round(1.23, 2, java.math.BigDecimal.ROUND_UNNECESSARY), 0.0);
		try {
			org.apache.commons.math.util.MathUtils.round(1.234, 2, java.math.BigDecimal.ROUND_UNNECESSARY);
			junit.framework.Assert.fail();
		} catch (java.lang.ArithmeticException ex) {
		}
		junit.framework.Assert.assertEquals(1.24, org.apache.commons.math.util.MathUtils.round(x, 2, java.math.BigDecimal.ROUND_UP), 0.0);
		junit.framework.Assert.assertEquals(1.235, org.apache.commons.math.util.MathUtils.round(x, 3, java.math.BigDecimal.ROUND_UP), 0.0);
		junit.framework.Assert.assertEquals(1.2346, org.apache.commons.math.util.MathUtils.round(x, 4, java.math.BigDecimal.ROUND_UP), 0.0);
		junit.framework.Assert.assertEquals(-1.24, org.apache.commons.math.util.MathUtils.round(-x, 2, java.math.BigDecimal.ROUND_UP), 0.0);
		junit.framework.Assert.assertEquals(-1.235, org.apache.commons.math.util.MathUtils.round(-x, 3, java.math.BigDecimal.ROUND_UP), 0.0);
		junit.framework.Assert.assertEquals(-1.2346, org.apache.commons.math.util.MathUtils.round(-x, 4, java.math.BigDecimal.ROUND_UP), 0.0);
		try {
			org.apache.commons.math.util.MathUtils.round(1.234, 2, 1923);
			junit.framework.Assert.fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		junit.framework.Assert.assertEquals(39.25, org.apache.commons.math.util.MathUtils.round(39.245, 2, java.math.BigDecimal.ROUND_HALF_UP), 0.0);
		org.apache.commons.math.TestUtils.assertEquals(java.lang.Double.NaN, org.apache.commons.math.util.MathUtils.round(java.lang.Double.NaN, 2), 0.0);
		junit.framework.Assert.assertEquals(0.0, org.apache.commons.math.util.MathUtils.round(0.0, 2), 0.0);
		junit.framework.Assert.assertEquals(java.lang.Double.POSITIVE_INFINITY, org.apache.commons.math.util.MathUtils.round(java.lang.Double.POSITIVE_INFINITY, 2), 0.0);
		junit.framework.Assert.assertEquals(java.lang.Double.NEGATIVE_INFINITY, org.apache.commons.math.util.MathUtils.round(java.lang.Double.NEGATIVE_INFINITY, 2), 0.0);
	}

	public void testRoundFloat() {
		float x = 1.2345679F;
		junit.framework.Assert.assertEquals(1.23F, org.apache.commons.math.util.MathUtils.round(x, 2), 0.0);
		junit.framework.Assert.assertEquals(1.235F, org.apache.commons.math.util.MathUtils.round(x, 3), 0.0);
		junit.framework.Assert.assertEquals(1.2346F, org.apache.commons.math.util.MathUtils.round(x, 4), 0.0);
		junit.framework.Assert.assertEquals(30.1F, org.apache.commons.math.util.MathUtils.round(30.095F, 2), 0.0F);
		junit.framework.Assert.assertEquals(30.1F, org.apache.commons.math.util.MathUtils.round(30.095F, 1), 0.0F);
		junit.framework.Assert.assertEquals(50.09F, org.apache.commons.math.util.MathUtils.round(50.085F, 2), 0.0F);
		junit.framework.Assert.assertEquals(50.19F, org.apache.commons.math.util.MathUtils.round(50.185F, 2), 0.0F);
		junit.framework.Assert.assertEquals(50.01F, org.apache.commons.math.util.MathUtils.round(50.005F, 2), 0.0F);
		junit.framework.Assert.assertEquals(30.01F, org.apache.commons.math.util.MathUtils.round(30.005F, 2), 0.0F);
		junit.framework.Assert.assertEquals(30.65F, org.apache.commons.math.util.MathUtils.round(30.645F, 2), 0.0F);
		junit.framework.Assert.assertEquals(1.24F, org.apache.commons.math.util.MathUtils.round(x, 2, java.math.BigDecimal.ROUND_CEILING), 0.0);
		junit.framework.Assert.assertEquals(1.235F, org.apache.commons.math.util.MathUtils.round(x, 3, java.math.BigDecimal.ROUND_CEILING), 0.0);
		junit.framework.Assert.assertEquals(1.2346F, org.apache.commons.math.util.MathUtils.round(x, 4, java.math.BigDecimal.ROUND_CEILING), 0.0);
		junit.framework.Assert.assertEquals(-1.23F, org.apache.commons.math.util.MathUtils.round(-x, 2, java.math.BigDecimal.ROUND_CEILING), 0.0);
		junit.framework.Assert.assertEquals(-1.234F, org.apache.commons.math.util.MathUtils.round(-x, 3, java.math.BigDecimal.ROUND_CEILING), 0.0);
		junit.framework.Assert.assertEquals(-1.2345F, org.apache.commons.math.util.MathUtils.round(-x, 4, java.math.BigDecimal.ROUND_CEILING), 0.0);
		junit.framework.Assert.assertEquals(1.23F, org.apache.commons.math.util.MathUtils.round(x, 2, java.math.BigDecimal.ROUND_DOWN), 0.0);
		junit.framework.Assert.assertEquals(1.234F, org.apache.commons.math.util.MathUtils.round(x, 3, java.math.BigDecimal.ROUND_DOWN), 0.0);
		junit.framework.Assert.assertEquals(1.2345F, org.apache.commons.math.util.MathUtils.round(x, 4, java.math.BigDecimal.ROUND_DOWN), 0.0);
		junit.framework.Assert.assertEquals(-1.23F, org.apache.commons.math.util.MathUtils.round(-x, 2, java.math.BigDecimal.ROUND_DOWN), 0.0);
		junit.framework.Assert.assertEquals(-1.234F, org.apache.commons.math.util.MathUtils.round(-x, 3, java.math.BigDecimal.ROUND_DOWN), 0.0);
		junit.framework.Assert.assertEquals(-1.2345F, org.apache.commons.math.util.MathUtils.round(-x, 4, java.math.BigDecimal.ROUND_DOWN), 0.0);
		junit.framework.Assert.assertEquals(1.23F, org.apache.commons.math.util.MathUtils.round(x, 2, java.math.BigDecimal.ROUND_FLOOR), 0.0);
		junit.framework.Assert.assertEquals(1.234F, org.apache.commons.math.util.MathUtils.round(x, 3, java.math.BigDecimal.ROUND_FLOOR), 0.0);
		junit.framework.Assert.assertEquals(1.2345F, org.apache.commons.math.util.MathUtils.round(x, 4, java.math.BigDecimal.ROUND_FLOOR), 0.0);
		junit.framework.Assert.assertEquals(-1.24F, org.apache.commons.math.util.MathUtils.round(-x, 2, java.math.BigDecimal.ROUND_FLOOR), 0.0);
		junit.framework.Assert.assertEquals(-1.235F, org.apache.commons.math.util.MathUtils.round(-x, 3, java.math.BigDecimal.ROUND_FLOOR), 0.0);
		junit.framework.Assert.assertEquals(-1.2346F, org.apache.commons.math.util.MathUtils.round(-x, 4, java.math.BigDecimal.ROUND_FLOOR), 0.0);
		junit.framework.Assert.assertEquals(1.23F, org.apache.commons.math.util.MathUtils.round(x, 2, java.math.BigDecimal.ROUND_HALF_DOWN), 0.0);
		junit.framework.Assert.assertEquals(1.235F, org.apache.commons.math.util.MathUtils.round(x, 3, java.math.BigDecimal.ROUND_HALF_DOWN), 0.0);
		junit.framework.Assert.assertEquals(1.2346F, org.apache.commons.math.util.MathUtils.round(x, 4, java.math.BigDecimal.ROUND_HALF_DOWN), 0.0);
		junit.framework.Assert.assertEquals(-1.23F, org.apache.commons.math.util.MathUtils.round(-x, 2, java.math.BigDecimal.ROUND_HALF_DOWN), 0.0);
		junit.framework.Assert.assertEquals(-1.235F, org.apache.commons.math.util.MathUtils.round(-x, 3, java.math.BigDecimal.ROUND_HALF_DOWN), 0.0);
		junit.framework.Assert.assertEquals(-1.2346F, org.apache.commons.math.util.MathUtils.round(-x, 4, java.math.BigDecimal.ROUND_HALF_DOWN), 0.0);
		junit.framework.Assert.assertEquals(1.234F, org.apache.commons.math.util.MathUtils.round(1.2345F, 3, java.math.BigDecimal.ROUND_HALF_DOWN), 0.0);
		junit.framework.Assert.assertEquals(-1.234F, org.apache.commons.math.util.MathUtils.round(-1.2345F, 3, java.math.BigDecimal.ROUND_HALF_DOWN), 0.0);
		junit.framework.Assert.assertEquals(1.23F, org.apache.commons.math.util.MathUtils.round(x, 2, java.math.BigDecimal.ROUND_HALF_EVEN), 0.0);
		junit.framework.Assert.assertEquals(1.235F, org.apache.commons.math.util.MathUtils.round(x, 3, java.math.BigDecimal.ROUND_HALF_EVEN), 0.0);
		junit.framework.Assert.assertEquals(1.2346F, org.apache.commons.math.util.MathUtils.round(x, 4, java.math.BigDecimal.ROUND_HALF_EVEN), 0.0);
		junit.framework.Assert.assertEquals(-1.23F, org.apache.commons.math.util.MathUtils.round(-x, 2, java.math.BigDecimal.ROUND_HALF_EVEN), 0.0);
		junit.framework.Assert.assertEquals(-1.235F, org.apache.commons.math.util.MathUtils.round(-x, 3, java.math.BigDecimal.ROUND_HALF_EVEN), 0.0);
		junit.framework.Assert.assertEquals(-1.2346F, org.apache.commons.math.util.MathUtils.round(-x, 4, java.math.BigDecimal.ROUND_HALF_EVEN), 0.0);
		junit.framework.Assert.assertEquals(1.234F, org.apache.commons.math.util.MathUtils.round(1.2345F, 3, java.math.BigDecimal.ROUND_HALF_EVEN), 0.0);
		junit.framework.Assert.assertEquals(-1.234F, org.apache.commons.math.util.MathUtils.round(-1.2345F, 3, java.math.BigDecimal.ROUND_HALF_EVEN), 0.0);
		junit.framework.Assert.assertEquals(1.236F, org.apache.commons.math.util.MathUtils.round(1.2355F, 3, java.math.BigDecimal.ROUND_HALF_EVEN), 0.0);
		junit.framework.Assert.assertEquals(-1.236F, org.apache.commons.math.util.MathUtils.round(-1.2355F, 3, java.math.BigDecimal.ROUND_HALF_EVEN), 0.0);
		junit.framework.Assert.assertEquals(1.23F, org.apache.commons.math.util.MathUtils.round(x, 2, java.math.BigDecimal.ROUND_HALF_UP), 0.0);
		junit.framework.Assert.assertEquals(1.235F, org.apache.commons.math.util.MathUtils.round(x, 3, java.math.BigDecimal.ROUND_HALF_UP), 0.0);
		junit.framework.Assert.assertEquals(1.2346F, org.apache.commons.math.util.MathUtils.round(x, 4, java.math.BigDecimal.ROUND_HALF_UP), 0.0);
		junit.framework.Assert.assertEquals(-1.23F, org.apache.commons.math.util.MathUtils.round(-x, 2, java.math.BigDecimal.ROUND_HALF_UP), 0.0);
		junit.framework.Assert.assertEquals(-1.235F, org.apache.commons.math.util.MathUtils.round(-x, 3, java.math.BigDecimal.ROUND_HALF_UP), 0.0);
		junit.framework.Assert.assertEquals(-1.2346F, org.apache.commons.math.util.MathUtils.round(-x, 4, java.math.BigDecimal.ROUND_HALF_UP), 0.0);
		junit.framework.Assert.assertEquals(1.235F, org.apache.commons.math.util.MathUtils.round(1.2345F, 3, java.math.BigDecimal.ROUND_HALF_UP), 0.0);
		junit.framework.Assert.assertEquals(-1.235F, org.apache.commons.math.util.MathUtils.round(-1.2345F, 3, java.math.BigDecimal.ROUND_HALF_UP), 0.0);
		junit.framework.Assert.assertEquals(-1.23F, org.apache.commons.math.util.MathUtils.round(-1.23F, 2, java.math.BigDecimal.ROUND_UNNECESSARY), 0.0);
		junit.framework.Assert.assertEquals(1.23F, org.apache.commons.math.util.MathUtils.round(1.23F, 2, java.math.BigDecimal.ROUND_UNNECESSARY), 0.0);
		try {
			org.apache.commons.math.util.MathUtils.round(1.234F, 2, java.math.BigDecimal.ROUND_UNNECESSARY);
			junit.framework.Assert.fail();
		} catch (java.lang.ArithmeticException ex) {
		}
		junit.framework.Assert.assertEquals(1.24F, org.apache.commons.math.util.MathUtils.round(x, 2, java.math.BigDecimal.ROUND_UP), 0.0);
		junit.framework.Assert.assertEquals(1.235F, org.apache.commons.math.util.MathUtils.round(x, 3, java.math.BigDecimal.ROUND_UP), 0.0);
		junit.framework.Assert.assertEquals(1.2346F, org.apache.commons.math.util.MathUtils.round(x, 4, java.math.BigDecimal.ROUND_UP), 0.0);
		junit.framework.Assert.assertEquals(-1.24F, org.apache.commons.math.util.MathUtils.round(-x, 2, java.math.BigDecimal.ROUND_UP), 0.0);
		junit.framework.Assert.assertEquals(-1.235F, org.apache.commons.math.util.MathUtils.round(-x, 3, java.math.BigDecimal.ROUND_UP), 0.0);
		junit.framework.Assert.assertEquals(-1.2346F, org.apache.commons.math.util.MathUtils.round(-x, 4, java.math.BigDecimal.ROUND_UP), 0.0);
		try {
			org.apache.commons.math.util.MathUtils.round(1.234F, 2, 1923);
			junit.framework.Assert.fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		org.apache.commons.math.TestUtils.assertEquals(java.lang.Float.NaN, org.apache.commons.math.util.MathUtils.round(java.lang.Float.NaN, 2), 0.0F);
		junit.framework.Assert.assertEquals(0.0F, org.apache.commons.math.util.MathUtils.round(0.0F, 2), 0.0F);
		junit.framework.Assert.assertEquals(java.lang.Float.POSITIVE_INFINITY, org.apache.commons.math.util.MathUtils.round(java.lang.Float.POSITIVE_INFINITY, 2), 0.0F);
		junit.framework.Assert.assertEquals(java.lang.Float.NEGATIVE_INFINITY, org.apache.commons.math.util.MathUtils.round(java.lang.Float.NEGATIVE_INFINITY, 2), 0.0F);
	}

	public void testSignByte() {
		junit.framework.Assert.assertEquals(((byte)(1)), org.apache.commons.math.util.MathUtils.sign(((byte)(2))));
		junit.framework.Assert.assertEquals(((byte)(0)), org.apache.commons.math.util.MathUtils.sign(((byte)(0))));
		junit.framework.Assert.assertEquals(((byte)(-1)), org.apache.commons.math.util.MathUtils.sign(((byte)(-2))));
	}

	public void testSignDouble() {
		double delta = 0.0;
		junit.framework.Assert.assertEquals(1.0, org.apache.commons.math.util.MathUtils.sign(2.0), delta);
		junit.framework.Assert.assertEquals(0.0, org.apache.commons.math.util.MathUtils.sign(0.0), delta);
		junit.framework.Assert.assertEquals(-1.0, org.apache.commons.math.util.MathUtils.sign(-2.0), delta);
		org.apache.commons.math.TestUtils.assertSame(((-0.0) / 0.0), org.apache.commons.math.util.MathUtils.sign(java.lang.Double.NaN));
	}

	public void testSignFloat() {
		float delta = 0.0F;
		junit.framework.Assert.assertEquals(1.0F, org.apache.commons.math.util.MathUtils.sign(2.0F), delta);
		junit.framework.Assert.assertEquals(0.0F, org.apache.commons.math.util.MathUtils.sign(0.0F), delta);
		junit.framework.Assert.assertEquals(-1.0F, org.apache.commons.math.util.MathUtils.sign(-2.0F), delta);
		org.apache.commons.math.TestUtils.assertSame(java.lang.Float.NaN, org.apache.commons.math.util.MathUtils.sign(java.lang.Float.NaN));
	}

	public void testSignInt() {
		junit.framework.Assert.assertEquals(1, org.apache.commons.math.util.MathUtils.sign(2));
		junit.framework.Assert.assertEquals(0, org.apache.commons.math.util.MathUtils.sign(0));
		junit.framework.Assert.assertEquals(-1, org.apache.commons.math.util.MathUtils.sign(-2));
	}

	public void testSignLong() {
		junit.framework.Assert.assertEquals(1L, org.apache.commons.math.util.MathUtils.sign(2L));
		junit.framework.Assert.assertEquals(0L, org.apache.commons.math.util.MathUtils.sign(0L));
		junit.framework.Assert.assertEquals(-1L, org.apache.commons.math.util.MathUtils.sign(-2L));
	}

	public void testSignShort() {
		junit.framework.Assert.assertEquals(((short)(1)), org.apache.commons.math.util.MathUtils.sign(((short)(2))));
		junit.framework.Assert.assertEquals(((short)(0)), org.apache.commons.math.util.MathUtils.sign(((short)(0))));
		junit.framework.Assert.assertEquals(((short)(-1)), org.apache.commons.math.util.MathUtils.sign(((short)(-2))));
	}

	public void testSinh() {
		double x = 3.0;
		double expected = 10.01787;
		junit.framework.Assert.assertEquals(expected, org.apache.commons.math.util.MathUtils.sinh(x), 1.0E-5);
	}

	public void testSinhNaN() {
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(org.apache.commons.math.util.MathUtils.sinh(java.lang.Double.NaN)));
	}

	public void testSubAndCheck() {
		int big = java.lang.Integer.MAX_VALUE;
		int bigNeg = java.lang.Integer.MIN_VALUE;
		junit.framework.Assert.assertEquals(big, org.apache.commons.math.util.MathUtils.subAndCheck(big, 0));
		junit.framework.Assert.assertEquals((bigNeg + 1), org.apache.commons.math.util.MathUtils.subAndCheck(bigNeg, -1));
		junit.framework.Assert.assertEquals(-1, org.apache.commons.math.util.MathUtils.subAndCheck(bigNeg, -big));
		try {
			org.apache.commons.math.util.MathUtils.subAndCheck(big, -1);
			junit.framework.Assert.fail("Expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
		try {
			org.apache.commons.math.util.MathUtils.subAndCheck(bigNeg, 1);
			junit.framework.Assert.fail("Expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
	}

	public void testSubAndCheckErrorMessage() {
		int big = java.lang.Integer.MAX_VALUE;
		try {
			org.apache.commons.math.util.MathUtils.subAndCheck(big, -1);
			junit.framework.Assert.fail("Expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
			junit.framework.Assert.assertEquals("overflow: subtract", ex.getMessage());
		}
	}

	public void testSubAndCheckLong() {
		long max = java.lang.Long.MAX_VALUE;
		long min = java.lang.Long.MIN_VALUE;
		junit.framework.Assert.assertEquals(max, org.apache.commons.math.util.MathUtils.subAndCheck(max, 0));
		junit.framework.Assert.assertEquals(min, org.apache.commons.math.util.MathUtils.subAndCheck(min, 0));
		junit.framework.Assert.assertEquals(-max, org.apache.commons.math.util.MathUtils.subAndCheck(0, max));
		junit.framework.Assert.assertEquals((min + 1), org.apache.commons.math.util.MathUtils.subAndCheck(min, -1));
		junit.framework.Assert.assertEquals(-1, org.apache.commons.math.util.MathUtils.subAndCheck(((-max) - 1), -max));
		junit.framework.Assert.assertEquals(max, org.apache.commons.math.util.MathUtils.subAndCheck(-1, ((-1) - max)));
		testSubAndCheckLongFailure(0L, min);
		testSubAndCheckLongFailure(max, -1L);
		testSubAndCheckLongFailure(min, 1L);
	}

	private void testSubAndCheckLongFailure(long a, long b) {
		try {
			org.apache.commons.math.util.MathUtils.subAndCheck(a, b);
			junit.framework.Assert.fail("Expecting ArithmeticException");
		} catch (java.lang.ArithmeticException ex) {
		}
	}

	public void testPow() {
		junit.framework.Assert.assertEquals(1801088541, org.apache.commons.math.util.MathUtils.pow(21, 7));
		junit.framework.Assert.assertEquals(1, org.apache.commons.math.util.MathUtils.pow(21, 0));
		try {
			org.apache.commons.math.util.MathUtils.pow(21, -7);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
		}
		junit.framework.Assert.assertEquals(1801088541, org.apache.commons.math.util.MathUtils.pow(21, 7L));
		junit.framework.Assert.assertEquals(1, org.apache.commons.math.util.MathUtils.pow(21, 0L));
		try {
			org.apache.commons.math.util.MathUtils.pow(21, -7L);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
		}
		junit.framework.Assert.assertEquals(1801088541L, org.apache.commons.math.util.MathUtils.pow(21L, 7));
		junit.framework.Assert.assertEquals(1L, org.apache.commons.math.util.MathUtils.pow(21L, 0));
		try {
			org.apache.commons.math.util.MathUtils.pow(21L, -7);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
		}
		junit.framework.Assert.assertEquals(1801088541L, org.apache.commons.math.util.MathUtils.pow(21L, 7L));
		junit.framework.Assert.assertEquals(1L, org.apache.commons.math.util.MathUtils.pow(21L, 0L));
		try {
			org.apache.commons.math.util.MathUtils.pow(21L, -7L);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
		}
		java.math.BigInteger twentyOne = java.math.BigInteger.valueOf(21L);
		junit.framework.Assert.assertEquals(java.math.BigInteger.valueOf(1801088541L), org.apache.commons.math.util.MathUtils.pow(twentyOne, 7));
		junit.framework.Assert.assertEquals(java.math.BigInteger.ONE, org.apache.commons.math.util.MathUtils.pow(twentyOne, 0));
		try {
			org.apache.commons.math.util.MathUtils.pow(twentyOne, -7);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
		}
		junit.framework.Assert.assertEquals(java.math.BigInteger.valueOf(1801088541L), org.apache.commons.math.util.MathUtils.pow(twentyOne, 7L));
		junit.framework.Assert.assertEquals(java.math.BigInteger.ONE, org.apache.commons.math.util.MathUtils.pow(twentyOne, 0L));
		try {
			org.apache.commons.math.util.MathUtils.pow(twentyOne, -7L);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
		}
		junit.framework.Assert.assertEquals(java.math.BigInteger.valueOf(1801088541L), org.apache.commons.math.util.MathUtils.pow(twentyOne, java.math.BigInteger.valueOf(7L)));
		junit.framework.Assert.assertEquals(java.math.BigInteger.ONE, org.apache.commons.math.util.MathUtils.pow(twentyOne, java.math.BigInteger.ZERO));
		try {
			org.apache.commons.math.util.MathUtils.pow(twentyOne, java.math.BigInteger.valueOf(-7L));
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
		}
		java.math.BigInteger bigOne = new java.math.BigInteger(("1543786922199448028351389769265814882661837148" + ("4763915343722775611762713982220306372888519211" + "560905579993523402015636025177602059044911261")));
		junit.framework.Assert.assertEquals(bigOne, org.apache.commons.math.util.MathUtils.pow(twentyOne, 103));
		junit.framework.Assert.assertEquals(bigOne, org.apache.commons.math.util.MathUtils.pow(twentyOne, 103L));
		junit.framework.Assert.assertEquals(bigOne, org.apache.commons.math.util.MathUtils.pow(twentyOne, java.math.BigInteger.valueOf(103L)));
	}

	public void testL1DistanceDouble() {
		double[] p1 = new double[]{ 2.5 , 0.0 };
		double[] p2 = new double[]{ -0.5 , 4.0 };
		junit.framework.Assert.assertEquals(7.0, org.apache.commons.math.util.MathUtils.distance1(p1, p2));
	}

	public void testL1DistanceInt() {
		int[] p1 = new int[]{ 3 , 0 };
		int[] p2 = new int[]{ 0 , 4 };
		junit.framework.Assert.assertEquals(7, org.apache.commons.math.util.MathUtils.distance1(p1, p2));
	}

	public void testL2DistanceDouble() {
		double[] p1 = new double[]{ 2.5 , 0.0 };
		double[] p2 = new double[]{ -0.5 , 4.0 };
		junit.framework.Assert.assertEquals(5.0, org.apache.commons.math.util.MathUtils.distance(p1, p2));
	}

	public void testL2DistanceInt() {
		int[] p1 = new int[]{ 3 , 0 };
		int[] p2 = new int[]{ 0 , 4 };
		junit.framework.Assert.assertEquals(5.0, org.apache.commons.math.util.MathUtils.distance(p1, p2));
	}

	public void testLInfDistanceDouble() {
		double[] p1 = new double[]{ 2.5 , 0.0 };
		double[] p2 = new double[]{ -0.5 , 4.0 };
		junit.framework.Assert.assertEquals(4.0, org.apache.commons.math.util.MathUtils.distanceInf(p1, p2));
	}

	public void testLInfDistanceInt() {
		int[] p1 = new int[]{ 3 , 0 };
		int[] p2 = new int[]{ 0 , 4 };
		junit.framework.Assert.assertEquals(4, org.apache.commons.math.util.MathUtils.distanceInf(p1, p2));
	}

	public void testCheckOrder() {
		org.apache.commons.math.util.MathUtils.checkOrder(new double[]{ -15 , -5.5 , -1 , 2 , 15 }, 1, true);
		org.apache.commons.math.util.MathUtils.checkOrder(new double[]{ -15 , -5.5 , -1 , 2 , 2 }, 1, false);
		org.apache.commons.math.util.MathUtils.checkOrder(new double[]{ 3 , -5.5 , -11 , -27.5 }, -1, true);
		org.apache.commons.math.util.MathUtils.checkOrder(new double[]{ 3 , 0 , 0 , -5.5 , -11 , -27.5 }, -1, false);
		try {
			org.apache.commons.math.util.MathUtils.checkOrder(new double[]{ -15 , -5.5 , -1 , -1 , 2 , 15 }, 1, true);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException e) {
		}
		try {
			org.apache.commons.math.util.MathUtils.checkOrder(new double[]{ -15 , -5.5 , -1 , -2 , 2 }, 1, false);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException e) {
		}
		try {
			org.apache.commons.math.util.MathUtils.checkOrder(new double[]{ 3 , 3 , -5.5 , -11 , -27.5 }, -1, true);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException e) {
		}
		try {
			org.apache.commons.math.util.MathUtils.checkOrder(new double[]{ 3 , -1 , 0 , -5.5 , -11 , -27.5 }, -1, false);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException e) {
		}
	}
}

