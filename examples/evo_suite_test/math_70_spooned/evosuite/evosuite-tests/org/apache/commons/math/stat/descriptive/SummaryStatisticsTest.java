package org.apache.commons.math.stat.descriptive;


public class SummaryStatisticsTest extends junit.framework.TestCase {
	private double one = 1;

	private float twoF = 2;

	private long twoL = 2;

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

	public SummaryStatisticsTest(java.lang.String name) {
		super(name);
	}

	protected org.apache.commons.math.stat.descriptive.SummaryStatistics createSummaryStatistics() {
		return new org.apache.commons.math.stat.descriptive.SummaryStatistics();
	}

	public void testStats() {
		org.apache.commons.math.stat.descriptive.SummaryStatistics u = createSummaryStatistics();
		junit.framework.Assert.assertEquals("total count", 0, u.getN(), tolerance);
		u.addValue(one);
		u.addValue(twoF);
		u.addValue(twoL);
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
		org.apache.commons.math.stat.descriptive.SummaryStatistics u = createSummaryStatistics();
		junit.framework.Assert.assertTrue("Mean of n = 0 set should be NaN", java.lang.Double.isNaN(u.getMean()));
		junit.framework.Assert.assertTrue("Standard Deviation of n = 0 set should be NaN", java.lang.Double.isNaN(u.getStandardDeviation()));
		junit.framework.Assert.assertTrue("Variance of n = 0 set should be NaN", java.lang.Double.isNaN(u.getVariance()));
		u.addValue(one);
		junit.framework.Assert.assertTrue("mean should be one (n = 1)", ((u.getMean()) == (one)));
		junit.framework.Assert.assertTrue(("geometric should be one (n = 1) instead it is " + (u.getGeometricMean())), ((u.getGeometricMean()) == (one)));
		junit.framework.Assert.assertTrue("Std should be zero (n = 1)", ((u.getStandardDeviation()) == 0.0));
		junit.framework.Assert.assertTrue("variance should be zero (n = 1)", ((u.getVariance()) == 0.0));
		u.addValue(twoF);
		junit.framework.Assert.assertTrue("Std should not be zero (n = 2)", ((u.getStandardDeviation()) != 0.0));
		junit.framework.Assert.assertTrue("variance should not be zero (n = 2)", ((u.getVariance()) != 0.0));
	}

	public void testProductAndGeometricMean() throws java.lang.Exception {
		org.apache.commons.math.stat.descriptive.SummaryStatistics u = createSummaryStatistics();
		u.addValue(1.0);
		u.addValue(2.0);
		u.addValue(3.0);
		u.addValue(4.0);
		junit.framework.Assert.assertEquals("Geometric mean not expected", 2.213364, u.getGeometricMean(), 1.0E-5);
	}

	public void testNaNContracts() {
		org.apache.commons.math.stat.descriptive.SummaryStatistics u = createSummaryStatistics();
		junit.framework.Assert.assertTrue("mean not NaN", java.lang.Double.isNaN(u.getMean()));
		junit.framework.Assert.assertTrue("min not NaN", java.lang.Double.isNaN(u.getMin()));
		junit.framework.Assert.assertTrue("std dev not NaN", java.lang.Double.isNaN(u.getStandardDeviation()));
		junit.framework.Assert.assertTrue("var not NaN", java.lang.Double.isNaN(u.getVariance()));
		junit.framework.Assert.assertTrue("geom mean not NaN", java.lang.Double.isNaN(u.getGeometricMean()));
		u.addValue(1.0);
		junit.framework.Assert.assertEquals("mean not expected", 1.0, u.getMean(), java.lang.Double.MIN_VALUE);
		junit.framework.Assert.assertEquals("variance not expected", 0.0, u.getVariance(), java.lang.Double.MIN_VALUE);
		junit.framework.Assert.assertEquals("geometric mean not expected", 1.0, u.getGeometricMean(), java.lang.Double.MIN_VALUE);
		u.addValue(-1.0);
		junit.framework.Assert.assertTrue("geom mean not NaN", java.lang.Double.isNaN(u.getGeometricMean()));
		u.addValue(0.0);
		junit.framework.Assert.assertTrue("geom mean not NaN", java.lang.Double.isNaN(u.getGeometricMean()));
	}

	public void testGetSummary() {
		org.apache.commons.math.stat.descriptive.SummaryStatistics u = createSummaryStatistics();
		org.apache.commons.math.stat.descriptive.StatisticalSummary summary = u.getSummary();
		verifySummary(u, summary);
		u.addValue(1.0);
		summary = u.getSummary();
		verifySummary(u, summary);
		u.addValue(2.0);
		summary = u.getSummary();
		verifySummary(u, summary);
		u.addValue(2.0);
		summary = u.getSummary();
		verifySummary(u, summary);
	}

	public void testSerialization() {
		org.apache.commons.math.stat.descriptive.SummaryStatistics u = createSummaryStatistics();
		org.apache.commons.math.TestUtils.checkSerializedEquality(u);
		org.apache.commons.math.stat.descriptive.SummaryStatistics s = ((org.apache.commons.math.stat.descriptive.SummaryStatistics)(org.apache.commons.math.TestUtils.serializeAndRecover(u)));
		org.apache.commons.math.stat.descriptive.StatisticalSummary summary = s.getSummary();
		verifySummary(u, summary);
		u.addValue(2.0);
		u.addValue(1.0);
		u.addValue(3.0);
		u.addValue(4.0);
		u.addValue(5.0);
		org.apache.commons.math.TestUtils.checkSerializedEquality(u);
		s = ((org.apache.commons.math.stat.descriptive.SummaryStatistics)(org.apache.commons.math.TestUtils.serializeAndRecover(u)));
		summary = s.getSummary();
		verifySummary(u, summary);
	}

	public void testEqualsAndHashCode() {
		org.apache.commons.math.stat.descriptive.SummaryStatistics u = createSummaryStatistics();
		org.apache.commons.math.stat.descriptive.SummaryStatistics t = null;
		int emptyHash = u.hashCode();
		junit.framework.Assert.assertTrue("reflexive", u.equals(u));
		junit.framework.Assert.assertFalse("non-null compared to null", u.equals(t));
		junit.framework.Assert.assertFalse("wrong type", u.equals(java.lang.Double.valueOf(0)));
		t = createSummaryStatistics();
		junit.framework.Assert.assertTrue("empty instances should be equal", t.equals(u));
		junit.framework.Assert.assertTrue("empty instances should be equal", u.equals(t));
		junit.framework.Assert.assertEquals("empty hash code", emptyHash, t.hashCode());
		u.addValue(2.0);
		u.addValue(1.0);
		u.addValue(3.0);
		u.addValue(4.0);
		junit.framework.Assert.assertFalse("different n's should make instances not equal", t.equals(u));
		junit.framework.Assert.assertFalse("different n's should make instances not equal", u.equals(t));
		junit.framework.Assert.assertTrue("different n's should make hashcodes different", ((u.hashCode()) != (t.hashCode())));
		t.addValue(2.0);
		t.addValue(1.0);
		t.addValue(3.0);
		t.addValue(4.0);
		junit.framework.Assert.assertTrue("summaries based on same data should be equal", t.equals(u));
		junit.framework.Assert.assertTrue("summaries based on same data should be equal", u.equals(t));
		junit.framework.Assert.assertEquals("summaries based on same data should have same hashcodes", u.hashCode(), t.hashCode());
		u.clear();
		t.clear();
		junit.framework.Assert.assertTrue("empty instances should be equal", t.equals(u));
		junit.framework.Assert.assertTrue("empty instances should be equal", u.equals(t));
		junit.framework.Assert.assertEquals("empty hash code", emptyHash, t.hashCode());
		junit.framework.Assert.assertEquals("empty hash code", emptyHash, u.hashCode());
	}

	public void testCopy() throws java.lang.Exception {
		org.apache.commons.math.stat.descriptive.SummaryStatistics u = createSummaryStatistics();
		u.addValue(2.0);
		u.addValue(1.0);
		u.addValue(3.0);
		u.addValue(4.0);
		org.apache.commons.math.stat.descriptive.SummaryStatistics v = new org.apache.commons.math.stat.descriptive.SummaryStatistics(u);
		junit.framework.Assert.assertEquals(u, v);
		junit.framework.Assert.assertEquals(v, u);
		junit.framework.Assert.assertTrue(((v.geoMean) == (v.getGeoMeanImpl())));
		junit.framework.Assert.assertTrue(((v.mean) == (v.getMeanImpl())));
		junit.framework.Assert.assertTrue(((v.min) == (v.getMinImpl())));
		junit.framework.Assert.assertTrue(((v.max) == (v.getMaxImpl())));
		junit.framework.Assert.assertTrue(((v.sum) == (v.getSumImpl())));
		junit.framework.Assert.assertTrue(((v.sumsq) == (v.getSumsqImpl())));
		junit.framework.Assert.assertTrue(((v.sumLog) == (v.getSumLogImpl())));
		junit.framework.Assert.assertTrue(((v.variance) == (v.getVarianceImpl())));
		u.addValue(7.0);
		u.addValue(9.0);
		u.addValue(11.0);
		u.addValue(23.0);
		v.addValue(7.0);
		v.addValue(9.0);
		v.addValue(11.0);
		v.addValue(23.0);
		junit.framework.Assert.assertEquals(u, v);
		junit.framework.Assert.assertEquals(v, u);
		u.clear();
		u.setSumImpl(new org.apache.commons.math.stat.descriptive.summary.Sum());
		org.apache.commons.math.stat.descriptive.SummaryStatistics.copy(u, v);
		junit.framework.Assert.assertEquals(u.sum, v.sum);
		junit.framework.Assert.assertEquals(u.getSumImpl(), v.getSumImpl());
	}

	private void verifySummary(org.apache.commons.math.stat.descriptive.SummaryStatistics u, org.apache.commons.math.stat.descriptive.StatisticalSummary s) {
		junit.framework.Assert.assertEquals("N", s.getN(), u.getN());
		org.apache.commons.math.TestUtils.assertEquals("sum", s.getSum(), u.getSum(), tolerance);
		org.apache.commons.math.TestUtils.assertEquals("var", s.getVariance(), u.getVariance(), tolerance);
		org.apache.commons.math.TestUtils.assertEquals("std", s.getStandardDeviation(), u.getStandardDeviation(), tolerance);
		org.apache.commons.math.TestUtils.assertEquals("mean", s.getMean(), u.getMean(), tolerance);
		org.apache.commons.math.TestUtils.assertEquals("min", s.getMin(), u.getMin(), tolerance);
		org.apache.commons.math.TestUtils.assertEquals("max", s.getMax(), u.getMax(), tolerance);
	}

	public void testSetterInjection() throws java.lang.Exception {
		org.apache.commons.math.stat.descriptive.SummaryStatistics u = createSummaryStatistics();
		u.setMeanImpl(new org.apache.commons.math.stat.descriptive.summary.Sum());
		u.setSumLogImpl(new org.apache.commons.math.stat.descriptive.summary.Sum());
		u.addValue(1);
		u.addValue(3);
		junit.framework.Assert.assertEquals(4, u.getMean(), 1.0E-14);
		junit.framework.Assert.assertEquals(4, u.getSumOfLogs(), 1.0E-14);
		junit.framework.Assert.assertEquals(java.lang.Math.exp(2), u.getGeometricMean(), 1.0E-14);
		u.clear();
		u.addValue(1);
		u.addValue(2);
		junit.framework.Assert.assertEquals(3, u.getMean(), 1.0E-14);
		u.clear();
		u.setMeanImpl(new org.apache.commons.math.stat.descriptive.moment.Mean());
	}

	public void testSetterIllegalState() throws java.lang.Exception {
		org.apache.commons.math.stat.descriptive.SummaryStatistics u = createSummaryStatistics();
		u.addValue(1);
		u.addValue(3);
		try {
			u.setMeanImpl(new org.apache.commons.math.stat.descriptive.summary.Sum());
			junit.framework.Assert.fail("Expecting IllegalStateException");
		} catch (java.lang.IllegalStateException ex) {
		}
	}
}

