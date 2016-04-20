package org.apache.commons.math.stat.descriptive;


public final class MixedListUnivariateImplTest extends junit.framework.TestCase {
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

	private org.apache.commons.math.util.TransformerMap transformers = new org.apache.commons.math.util.TransformerMap();

	public MixedListUnivariateImplTest(java.lang.String name) {
		super(name);
		transformers = new org.apache.commons.math.util.TransformerMap();
		transformers.putTransformer(org.apache.commons.math.stat.descriptive.MixedListUnivariateImplTest.Foo.class, new org.apache.commons.math.stat.descriptive.MixedListUnivariateImplTest.FooTransformer());
		transformers.putTransformer(org.apache.commons.math.stat.descriptive.MixedListUnivariateImplTest.Bar.class, new org.apache.commons.math.stat.descriptive.MixedListUnivariateImplTest.BarTransformer());
	}

	public void testStats() {
		java.util.List<java.lang.Object> externalList = new java.util.ArrayList<java.lang.Object>();
		org.apache.commons.math.stat.descriptive.DescriptiveStatistics u = new org.apache.commons.math.stat.descriptive.ListUnivariateImpl(externalList , transformers);
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
		org.apache.commons.math.stat.descriptive.DescriptiveStatistics u = new org.apache.commons.math.stat.descriptive.ListUnivariateImpl(new java.util.ArrayList<java.lang.Object>() , transformers);
		junit.framework.Assert.assertTrue("Mean of n = 0 set should be NaN", java.lang.Double.isNaN(u.getMean()));
		junit.framework.Assert.assertTrue("Standard Deviation of n = 0 set should be NaN", java.lang.Double.isNaN(u.getStandardDeviation()));
		junit.framework.Assert.assertTrue("Variance of n = 0 set should be NaN", java.lang.Double.isNaN(u.getVariance()));
		u.addValue(one);
		junit.framework.Assert.assertTrue(("Mean of n = 1 set should be value of single item n1, instead it is " + (u.getMean())), ((u.getMean()) == (one)));
		junit.framework.Assert.assertTrue(("StdDev of n = 1 set should be zero, instead it is: " + (u.getStandardDeviation())), ((u.getStandardDeviation()) == 0));
		junit.framework.Assert.assertTrue("Variance of n = 1 set should be zero", ((u.getVariance()) == 0));
	}

	public void testSkewAndKurtosis() {
		org.apache.commons.math.stat.descriptive.ListUnivariateImpl u = new org.apache.commons.math.stat.descriptive.ListUnivariateImpl(new java.util.ArrayList<java.lang.Object>() , transformers);
		u.addObject("12.5");
		u.addObject(java.lang.Integer.valueOf(12));
		u.addObject("11.8");
		u.addObject("14.2");
		u.addObject(new org.apache.commons.math.stat.descriptive.MixedListUnivariateImplTest.Foo());
		u.addObject("14.5");
		u.addObject(java.lang.Long.valueOf(21));
		u.addObject("8.2");
		u.addObject("10.3");
		u.addObject("11.3");
		u.addObject(java.lang.Float.valueOf(14.1F));
		u.addObject("9.9");
		u.addObject("12.2");
		u.addObject(new org.apache.commons.math.stat.descriptive.MixedListUnivariateImplTest.Bar());
		u.addObject("12.1");
		u.addObject("11");
		u.addObject(java.lang.Double.valueOf(19.8));
		u.addObject("11");
		u.addObject("10");
		u.addObject("8.8");
		u.addObject("9");
		u.addObject("12.3");
		junit.framework.Assert.assertEquals("mean", 12.40455, u.getMean(), 1.0E-4);
		junit.framework.Assert.assertEquals("variance", 10.00236, u.getVariance(), 1.0E-4);
		junit.framework.Assert.assertEquals("skewness", 1.437424, u.getSkewness(), 1.0E-4);
		junit.framework.Assert.assertEquals("kurtosis", 2.37719, u.getKurtosis(), 1.0E-4);
	}

	public void testProductAndGeometricMean() throws java.lang.Exception {
		org.apache.commons.math.stat.descriptive.ListUnivariateImpl u = new org.apache.commons.math.stat.descriptive.ListUnivariateImpl(new java.util.ArrayList<java.lang.Object>() , transformers);
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

	public static final class Foo {
		public java.lang.String heresFoo() {
			return "14.9";
		}
	}

	public static final class FooTransformer implements java.io.Serializable , org.apache.commons.math.util.NumberTransformer {
		private static final long serialVersionUID = -4252248129291326127L;

		public double transform(java.lang.Object o) {
			return java.lang.Double.parseDouble(((org.apache.commons.math.stat.descriptive.MixedListUnivariateImplTest.Foo)(o)).heresFoo());
		}
	}

	public static final class Bar {
		public java.lang.String heresBar() {
			return "12.0";
		}
	}

	public static final class BarTransformer implements java.io.Serializable , org.apache.commons.math.util.NumberTransformer {
		private static final long serialVersionUID = -1768345377764262043L;

		public double transform(java.lang.Object o) {
			return java.lang.Double.parseDouble(((org.apache.commons.math.stat.descriptive.MixedListUnivariateImplTest.Bar)(o)).heresBar());
		}
	}
}

