package org.apache.commons.math.stat.descriptive;


public final class ListUnivariateImplTest extends junit.framework.TestCase {
	private double one = 1;

	private float two = 2;

	private int three = 3;

	private double mean = 2;

	private double sumSq = 18;

	private double sum = 8;

	private double var = 0.6666666666666666;

	private double std = java.lang.Math.sqrt(var);

	private double n = 4;

	private double min = 1;

	private double max = 3;

	private double tolerance = 1.0E-14;

	public ListUnivariateImplTest(java.lang.String name) {
		super(name);
	}

	public void testStats() {
		java.util.List<java.lang.Object> externalList = new java.util.ArrayList<java.lang.Object>();
		org.apache.commons.math.stat.descriptive.DescriptiveStatistics u = new org.apache.commons.math.stat.descriptive.ListUnivariateImpl(externalList);
		junit.framework.Assert.assertEquals("total count", 0, u.getN(), tolerance);
		u.addValue(one);
		u.addValue(two);
		u.addValue(two);
		u.addValue(three);
		junit.framework.Assert.assertEquals("N", n, u.getN(), tolerance);
		junit.framework.Assert.assertEquals("sum", sum, u.getSum(), tolerance);
		junit.framework.Assert.assertEquals("sumsq", sumSq, u.getSumsq(), tolerance);
		junit.framework.Assert.assertEquals("var", var, u.getVariance(), tolerance);
		junit.framework.Assert.assertEquals("std", std, u.getStandardDeviation(), tolerance);
		junit.framework.Assert.assertEquals("mean", mean, u.getMean(), tolerance);
		junit.framework.Assert.assertEquals("min", min, u.getMin(), tolerance);
		junit.framework.Assert.assertEquals("max", max, u.getMax(), tolerance);
		u.clear();
		junit.framework.Assert.assertEquals("total count", 0, u.getN(), tolerance);
	}

	public void testN0andN1Conditions() throws java.lang.Exception {
		java.util.List<java.lang.Object> list = new java.util.ArrayList<java.lang.Object>();
		org.apache.commons.math.stat.descriptive.DescriptiveStatistics u = new org.apache.commons.math.stat.descriptive.ListUnivariateImpl(list);
		junit.framework.Assert.assertTrue("Mean of n = 0 set should be NaN", java.lang.Double.isNaN(u.getMean()));
		junit.framework.Assert.assertTrue("Standard Deviation of n = 0 set should be NaN", java.lang.Double.isNaN(u.getStandardDeviation()));
		junit.framework.Assert.assertTrue("Variance of n = 0 set should be NaN", java.lang.Double.isNaN(u.getVariance()));
		list.add(java.lang.Double.valueOf(one));
		junit.framework.Assert.assertTrue("Mean of n = 1 set should be value of single item n1", ((u.getMean()) == (one)));
		junit.framework.Assert.assertTrue(("StdDev of n = 1 set should be zero, instead it is: " + (u.getStandardDeviation())), ((u.getStandardDeviation()) == 0));
		junit.framework.Assert.assertTrue("Variance of n = 1 set should be zero", ((u.getVariance()) == 0));
	}

	public void testSkewAndKurtosis() {
		org.apache.commons.math.stat.descriptive.DescriptiveStatistics u = new org.apache.commons.math.stat.descriptive.DescriptiveStatistics();
		double[] testArray = new double[]{ 12.5 , 12 , 11.8 , 14.2 , 14.9 , 14.5 , 21 , 8.2 , 10.3 , 11.3 , 14.1 , 9.9 , 12.2 , 12 , 12.1 , 11 , 19.8 , 11 , 10 , 8.8 , 9 , 12.3 };
		for (int i = 0 ; i < (testArray.length) ; i++) {
			u.addValue(testArray[i]);
		}
		junit.framework.Assert.assertEquals("mean", 12.40455, u.getMean(), 1.0E-4);
		junit.framework.Assert.assertEquals("variance", 10.00236, u.getVariance(), 1.0E-4);
		junit.framework.Assert.assertEquals("skewness", 1.437424, u.getSkewness(), 1.0E-4);
		junit.framework.Assert.assertEquals("kurtosis", 2.37719, u.getKurtosis(), 1.0E-4);
	}

	public void testProductAndGeometricMean() throws java.lang.Exception {
		org.apache.commons.math.stat.descriptive.ListUnivariateImpl u = new org.apache.commons.math.stat.descriptive.ListUnivariateImpl(new java.util.ArrayList<java.lang.Object>());
		u.setWindowSize(10);
		u.addValue(1.0);
		u.addValue(2.0);
		u.addValue(3.0);
		u.addValue(4.0);
		junit.framework.Assert.assertEquals("Geometric mean not expected", 2.213364, u.getGeometricMean(), 1.0E-5);
		for (int i = 0 ; i < 10 ; i++) {
			u.addValue((i + 2));
		}
		junit.framework.Assert.assertEquals("Geometric mean not expected", 5.755931, u.getGeometricMean(), 1.0E-5);
	}

	public void testSerialization() {
		org.apache.commons.math.stat.descriptive.DescriptiveStatistics u = new org.apache.commons.math.stat.descriptive.ListUnivariateImpl();
		junit.framework.Assert.assertEquals("total count", 0, u.getN(), tolerance);
		u.addValue(one);
		u.addValue(two);
		org.apache.commons.math.stat.descriptive.DescriptiveStatistics u2 = ((org.apache.commons.math.stat.descriptive.DescriptiveStatistics)(org.apache.commons.math.TestUtils.serializeAndRecover(u)));
		u2.addValue(two);
		u2.addValue(three);
		junit.framework.Assert.assertEquals("N", n, u2.getN(), tolerance);
		junit.framework.Assert.assertEquals("sum", sum, u2.getSum(), tolerance);
		junit.framework.Assert.assertEquals("sumsq", sumSq, u2.getSumsq(), tolerance);
		junit.framework.Assert.assertEquals("var", var, u2.getVariance(), tolerance);
		junit.framework.Assert.assertEquals("std", std, u2.getStandardDeviation(), tolerance);
		junit.framework.Assert.assertEquals("mean", mean, u2.getMean(), tolerance);
		junit.framework.Assert.assertEquals("min", min, u2.getMin(), tolerance);
		junit.framework.Assert.assertEquals("max", max, u2.getMax(), tolerance);
		u2.clear();
		junit.framework.Assert.assertEquals("total count", 0, u2.getN(), tolerance);
	}
}

