package org.apache.commons.math.stat;


public final class StatUtilsTest extends junit.framework.TestCase {
	private double one = 1;

	private float two = 2;

	private int three = 3;

	private double mean = 2;

	private double sumSq = 18;

	private double sum = 8;

	private double var = 0.6666666666666666;

	private double min = 1;

	private double max = 3;

	private double tolerance = 1.0E-14;

	private double nan = java.lang.Double.NaN;

	public StatUtilsTest(java.lang.String name) {
		super(name);
	}

	public void testStats() {
		double[] values = new double[]{ one , two , two , three };
		junit.framework.Assert.assertEquals("sum", sum, org.apache.commons.math.stat.StatUtils.sum(values), tolerance);
		junit.framework.Assert.assertEquals("sumsq", sumSq, org.apache.commons.math.stat.StatUtils.sumSq(values), tolerance);
		junit.framework.Assert.assertEquals("var", var, org.apache.commons.math.stat.StatUtils.variance(values), tolerance);
		junit.framework.Assert.assertEquals("var with mean", var, org.apache.commons.math.stat.StatUtils.variance(values, mean), tolerance);
		junit.framework.Assert.assertEquals("mean", mean, org.apache.commons.math.stat.StatUtils.mean(values), tolerance);
		junit.framework.Assert.assertEquals("min", min, org.apache.commons.math.stat.StatUtils.min(values), tolerance);
		junit.framework.Assert.assertEquals("max", max, org.apache.commons.math.stat.StatUtils.max(values), tolerance);
	}

	public void testN0andN1Conditions() throws java.lang.Exception {
		double[] values = new double[0];
		junit.framework.Assert.assertTrue("Mean of n = 0 set should be NaN", java.lang.Double.isNaN(org.apache.commons.math.stat.StatUtils.mean(values)));
		junit.framework.Assert.assertTrue("Variance of n = 0 set should be NaN", java.lang.Double.isNaN(org.apache.commons.math.stat.StatUtils.variance(values)));
		values = new double[]{ one };
		junit.framework.Assert.assertTrue("Mean of n = 1 set should be value of single item n1", ((org.apache.commons.math.stat.StatUtils.mean(values)) == (one)));
		junit.framework.Assert.assertTrue("Variance of n = 1 set should be zero", ((org.apache.commons.math.stat.StatUtils.variance(values)) == 0));
	}

	public void testArrayIndexConditions() throws java.lang.Exception {
		double[] values = new double[]{ 1.0 , 2.0 , 3.0 , 4.0 };
		junit.framework.Assert.assertEquals("Sum not expected", 5.0, org.apache.commons.math.stat.StatUtils.sum(values, 1, 2), java.lang.Double.MIN_VALUE);
		junit.framework.Assert.assertEquals("Sum not expected", 3.0, org.apache.commons.math.stat.StatUtils.sum(values, 0, 2), java.lang.Double.MIN_VALUE);
		junit.framework.Assert.assertEquals("Sum not expected", 7.0, org.apache.commons.math.stat.StatUtils.sum(values, 2, 2), java.lang.Double.MIN_VALUE);
		try {
			org.apache.commons.math.stat.StatUtils.sum(values, 2, 3);
			junit.framework.Assert.assertTrue("Didn't throw exception", false);
		} catch (java.lang.Exception e) {
			junit.framework.Assert.assertTrue(true);
		}
		try {
			org.apache.commons.math.stat.StatUtils.sum(values, -1, 2);
			junit.framework.Assert.assertTrue("Didn't throw exception", false);
		} catch (java.lang.Exception e) {
			junit.framework.Assert.assertTrue(true);
		}
	}

	public void testSumSq() {
		double[] x = null;
		try {
			org.apache.commons.math.stat.StatUtils.sumSq(x);
			junit.framework.Assert.fail("null is not a valid data array.");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.stat.StatUtils.sumSq(x, 0, 4);
			junit.framework.Assert.fail("null is not a valid data array.");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		x = new double[]{  };
		org.apache.commons.math.TestUtils.assertEquals(java.lang.Double.NaN, org.apache.commons.math.stat.StatUtils.sumSq(x), tolerance);
		org.apache.commons.math.TestUtils.assertEquals(java.lang.Double.NaN, org.apache.commons.math.stat.StatUtils.sumSq(x, 0, 0), tolerance);
		x = new double[]{ two };
		org.apache.commons.math.TestUtils.assertEquals(4, org.apache.commons.math.stat.StatUtils.sumSq(x), tolerance);
		org.apache.commons.math.TestUtils.assertEquals(4, org.apache.commons.math.stat.StatUtils.sumSq(x, 0, 1), tolerance);
		x = new double[]{ one , two , two , three };
		org.apache.commons.math.TestUtils.assertEquals(18, org.apache.commons.math.stat.StatUtils.sumSq(x), tolerance);
		org.apache.commons.math.TestUtils.assertEquals(8, org.apache.commons.math.stat.StatUtils.sumSq(x, 1, 2), tolerance);
	}

	public void testProduct() {
		double[] x = null;
		try {
			org.apache.commons.math.stat.StatUtils.product(x);
			junit.framework.Assert.fail("null is not a valid data array.");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.stat.StatUtils.product(x, 0, 4);
			junit.framework.Assert.fail("null is not a valid data array.");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		x = new double[]{  };
		org.apache.commons.math.TestUtils.assertEquals(java.lang.Double.NaN, org.apache.commons.math.stat.StatUtils.product(x), tolerance);
		org.apache.commons.math.TestUtils.assertEquals(java.lang.Double.NaN, org.apache.commons.math.stat.StatUtils.product(x, 0, 0), tolerance);
		x = new double[]{ two };
		org.apache.commons.math.TestUtils.assertEquals(two, org.apache.commons.math.stat.StatUtils.product(x), tolerance);
		org.apache.commons.math.TestUtils.assertEquals(two, org.apache.commons.math.stat.StatUtils.product(x, 0, 1), tolerance);
		x = new double[]{ one , two , two , three };
		org.apache.commons.math.TestUtils.assertEquals(12, org.apache.commons.math.stat.StatUtils.product(x), tolerance);
		org.apache.commons.math.TestUtils.assertEquals(4, org.apache.commons.math.stat.StatUtils.product(x, 1, 2), tolerance);
	}

	public void testSumLog() {
		double[] x = null;
		try {
			org.apache.commons.math.stat.StatUtils.sumLog(x);
			junit.framework.Assert.fail("null is not a valid data array.");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.stat.StatUtils.sumLog(x, 0, 4);
			junit.framework.Assert.fail("null is not a valid data array.");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		x = new double[]{  };
		org.apache.commons.math.TestUtils.assertEquals(java.lang.Double.NaN, org.apache.commons.math.stat.StatUtils.sumLog(x), tolerance);
		org.apache.commons.math.TestUtils.assertEquals(java.lang.Double.NaN, org.apache.commons.math.stat.StatUtils.sumLog(x, 0, 0), tolerance);
		x = new double[]{ two };
		org.apache.commons.math.TestUtils.assertEquals(java.lang.Math.log(two), org.apache.commons.math.stat.StatUtils.sumLog(x), tolerance);
		org.apache.commons.math.TestUtils.assertEquals(java.lang.Math.log(two), org.apache.commons.math.stat.StatUtils.sumLog(x, 0, 1), tolerance);
		x = new double[]{ one , two , two , three };
		org.apache.commons.math.TestUtils.assertEquals((((java.lang.Math.log(one)) + (2.0 * (java.lang.Math.log(two)))) + (java.lang.Math.log(three))), org.apache.commons.math.stat.StatUtils.sumLog(x), tolerance);
		org.apache.commons.math.TestUtils.assertEquals((2.0 * (java.lang.Math.log(two))), org.apache.commons.math.stat.StatUtils.sumLog(x, 1, 2), tolerance);
	}

	public void testMean() {
		double[] x = null;
		try {
			org.apache.commons.math.stat.StatUtils.mean(x, 0, 4);
			junit.framework.Assert.fail("null is not a valid data array.");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		x = new double[]{  };
		org.apache.commons.math.TestUtils.assertEquals(java.lang.Double.NaN, org.apache.commons.math.stat.StatUtils.mean(x, 0, 0), tolerance);
		x = new double[]{ two };
		org.apache.commons.math.TestUtils.assertEquals(two, org.apache.commons.math.stat.StatUtils.mean(x, 0, 1), tolerance);
		x = new double[]{ one , two , two , three };
		org.apache.commons.math.TestUtils.assertEquals(2.5, org.apache.commons.math.stat.StatUtils.mean(x, 2, 2), tolerance);
	}

	public void testVariance() {
		double[] x = null;
		try {
			org.apache.commons.math.stat.StatUtils.variance(x, 0, 4);
			junit.framework.Assert.fail("null is not a valid data array.");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		x = new double[]{  };
		org.apache.commons.math.TestUtils.assertEquals(java.lang.Double.NaN, org.apache.commons.math.stat.StatUtils.variance(x, 0, 0), tolerance);
		x = new double[]{ two };
		org.apache.commons.math.TestUtils.assertEquals(0.0, org.apache.commons.math.stat.StatUtils.variance(x, 0, 1), tolerance);
		x = new double[]{ one , two , two , three };
		org.apache.commons.math.TestUtils.assertEquals(0.5, org.apache.commons.math.stat.StatUtils.variance(x, 2, 2), tolerance);
		x = new double[]{ one , two , two , three };
		org.apache.commons.math.TestUtils.assertEquals(0.5, org.apache.commons.math.stat.StatUtils.variance(x, 2.5, 2, 2), tolerance);
	}

	public void testMax() {
		double[] x = null;
		try {
			org.apache.commons.math.stat.StatUtils.max(x, 0, 4);
			junit.framework.Assert.fail("null is not a valid data array.");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		x = new double[]{  };
		org.apache.commons.math.TestUtils.assertEquals(java.lang.Double.NaN, org.apache.commons.math.stat.StatUtils.max(x, 0, 0), tolerance);
		x = new double[]{ two };
		org.apache.commons.math.TestUtils.assertEquals(two, org.apache.commons.math.stat.StatUtils.max(x, 0, 1), tolerance);
		x = new double[]{ one , two , two , three };
		org.apache.commons.math.TestUtils.assertEquals(three, org.apache.commons.math.stat.StatUtils.max(x, 1, 3), tolerance);
		x = new double[]{ nan , two , three };
		org.apache.commons.math.TestUtils.assertEquals(three, org.apache.commons.math.stat.StatUtils.max(x), tolerance);
		x = new double[]{ one , nan , three };
		org.apache.commons.math.TestUtils.assertEquals(three, org.apache.commons.math.stat.StatUtils.max(x), tolerance);
		x = new double[]{ one , two , nan };
		org.apache.commons.math.TestUtils.assertEquals(two, org.apache.commons.math.stat.StatUtils.max(x), tolerance);
		x = new double[]{ nan , nan , nan };
		org.apache.commons.math.TestUtils.assertEquals(nan, org.apache.commons.math.stat.StatUtils.max(x), tolerance);
	}

	public void testMin() {
		double[] x = null;
		try {
			org.apache.commons.math.stat.StatUtils.min(x, 0, 4);
			junit.framework.Assert.fail("null is not a valid data array.");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		x = new double[]{  };
		org.apache.commons.math.TestUtils.assertEquals(java.lang.Double.NaN, org.apache.commons.math.stat.StatUtils.min(x, 0, 0), tolerance);
		x = new double[]{ two };
		org.apache.commons.math.TestUtils.assertEquals(two, org.apache.commons.math.stat.StatUtils.min(x, 0, 1), tolerance);
		x = new double[]{ one , two , two , three };
		org.apache.commons.math.TestUtils.assertEquals(two, org.apache.commons.math.stat.StatUtils.min(x, 1, 3), tolerance);
		x = new double[]{ nan , two , three };
		org.apache.commons.math.TestUtils.assertEquals(two, org.apache.commons.math.stat.StatUtils.min(x), tolerance);
		x = new double[]{ one , nan , three };
		org.apache.commons.math.TestUtils.assertEquals(one, org.apache.commons.math.stat.StatUtils.min(x), tolerance);
		x = new double[]{ one , two , nan };
		org.apache.commons.math.TestUtils.assertEquals(one, org.apache.commons.math.stat.StatUtils.min(x), tolerance);
		x = new double[]{ nan , nan , nan };
		org.apache.commons.math.TestUtils.assertEquals(nan, org.apache.commons.math.stat.StatUtils.min(x), tolerance);
	}

	public void testPercentile() {
		double[] x = null;
		try {
			org.apache.commons.math.stat.StatUtils.percentile(x, 0.25);
			junit.framework.Assert.fail("null is not a valid data array.");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.stat.StatUtils.percentile(x, 0, 4, 0.25);
			junit.framework.Assert.fail("null is not a valid data array.");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		x = new double[]{  };
		org.apache.commons.math.TestUtils.assertEquals(java.lang.Double.NaN, org.apache.commons.math.stat.StatUtils.percentile(x, 25), tolerance);
		org.apache.commons.math.TestUtils.assertEquals(java.lang.Double.NaN, org.apache.commons.math.stat.StatUtils.percentile(x, 0, 0, 25), tolerance);
		x = new double[]{ two };
		org.apache.commons.math.TestUtils.assertEquals(two, org.apache.commons.math.stat.StatUtils.percentile(x, 25), tolerance);
		org.apache.commons.math.TestUtils.assertEquals(two, org.apache.commons.math.stat.StatUtils.percentile(x, 0, 1, 25), tolerance);
		x = new double[]{ one , two , two , three };
		org.apache.commons.math.TestUtils.assertEquals(2.5, org.apache.commons.math.stat.StatUtils.percentile(x, 70), tolerance);
		org.apache.commons.math.TestUtils.assertEquals(2.5, org.apache.commons.math.stat.StatUtils.percentile(x, 1, 3, 62.5), tolerance);
	}

	public void testDifferenceStats() throws java.lang.Exception {
		double[] sample1 = new double[]{ 1.0 , 2.0 , 3.0 , 4.0 };
		double[] sample2 = new double[]{ 1.0 , 3.0 , 4.0 , 2.0 };
		double[] diff = new double[]{ 0.0 , -1.0 , -1.0 , 2.0 };
		double[] small = new double[]{ 1.0 , 4.0 };
		double meanDifference = org.apache.commons.math.stat.StatUtils.meanDifference(sample1, sample2);
		junit.framework.Assert.assertEquals(org.apache.commons.math.stat.StatUtils.sumDifference(sample1, sample2), org.apache.commons.math.stat.StatUtils.sum(diff), tolerance);
		junit.framework.Assert.assertEquals(meanDifference, org.apache.commons.math.stat.StatUtils.mean(diff), tolerance);
		junit.framework.Assert.assertEquals(org.apache.commons.math.stat.StatUtils.varianceDifference(sample1, sample2, meanDifference), org.apache.commons.math.stat.StatUtils.variance(diff), tolerance);
		try {
			org.apache.commons.math.stat.StatUtils.meanDifference(sample1, small);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.stat.StatUtils.varianceDifference(sample1, small, meanDifference);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			double[] single = new double[]{ 1.0 };
			org.apache.commons.math.stat.StatUtils.varianceDifference(single, single, meanDifference);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testGeometricMean() throws java.lang.Exception {
		double[] test = null;
		try {
			org.apache.commons.math.stat.StatUtils.geometricMean(test);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		test = new double[]{ 2 , 4 , 6 , 8 };
		junit.framework.Assert.assertEquals(java.lang.Math.exp((0.25 * (org.apache.commons.math.stat.StatUtils.sumLog(test)))), org.apache.commons.math.stat.StatUtils.geometricMean(test), java.lang.Double.MIN_VALUE);
		junit.framework.Assert.assertEquals(java.lang.Math.exp((0.5 * (org.apache.commons.math.stat.StatUtils.sumLog(test, 0, 2)))), org.apache.commons.math.stat.StatUtils.geometricMean(test, 0, 2), java.lang.Double.MIN_VALUE);
	}
}

