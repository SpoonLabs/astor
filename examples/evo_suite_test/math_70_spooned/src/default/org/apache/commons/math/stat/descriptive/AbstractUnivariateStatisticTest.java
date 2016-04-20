package org.apache.commons.math.stat.descriptive;


public class AbstractUnivariateStatisticTest extends junit.framework.TestCase {
	public AbstractUnivariateStatisticTest(java.lang.String name) {
		super(name);
	}

	protected double[] testArray = new double[]{ 0 , 1 , 2 , 3 , 4 , 5 };

	protected double[] testWeightsArray = new double[]{ 0.3 , 0.2 , 1.3 , 1.1 , 1.0 , 1.8 };

	protected double[] testNegativeWeightsArray = new double[]{ -0.3 , 0.2 , -1.3 , 1.1 , 1.0 , 1.8 };

	protected double[] nullArray = null;

	protected double[] singletonArray = new double[]{ 0 };

	protected org.apache.commons.math.stat.descriptive.moment.Mean testStatistic = new org.apache.commons.math.stat.descriptive.moment.Mean();

	public void testTestPositive() {
		for (int j = 0 ; j < 6 ; j++) {
			for (int i = 1 ; i < (7 - j) ; i++) {
				junit.framework.Assert.assertTrue(testStatistic.test(testArray, 0, i));
			}
		}
		junit.framework.Assert.assertTrue(testStatistic.test(singletonArray, 0, 1));
	}

	public void testTestNegative() {
		junit.framework.Assert.assertFalse(testStatistic.test(singletonArray, 0, 0));
		junit.framework.Assert.assertFalse(testStatistic.test(testArray, 0, 0));
		try {
			testStatistic.test(singletonArray, 2, 1);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			testStatistic.test(testArray, 0, 7);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			testStatistic.test(testArray, -1, 1);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			testStatistic.test(testArray, 0, -1);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			testStatistic.test(nullArray, 0, 1);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			testStatistic.test(testArray, nullArray, 0, 1);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			testStatistic.test(singletonArray, testWeightsArray, 0, 1);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			testStatistic.test(testArray, testNegativeWeightsArray, 0, 6);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}
}

