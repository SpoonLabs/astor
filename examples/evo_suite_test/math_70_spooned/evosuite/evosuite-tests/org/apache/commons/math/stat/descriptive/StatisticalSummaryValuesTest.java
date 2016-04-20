package org.apache.commons.math.stat.descriptive;


public final class StatisticalSummaryValuesTest extends junit.framework.TestCase {
	public StatisticalSummaryValuesTest(java.lang.String name) {
		super(name);
	}

	public void testSerialization() {
		org.apache.commons.math.stat.descriptive.StatisticalSummaryValues u = new org.apache.commons.math.stat.descriptive.StatisticalSummaryValues(1 , 2 , 3 , 4 , 5 , 6);
		org.apache.commons.math.TestUtils.checkSerializedEquality(u);
		org.apache.commons.math.stat.descriptive.StatisticalSummaryValues t = ((org.apache.commons.math.stat.descriptive.StatisticalSummaryValues)(org.apache.commons.math.TestUtils.serializeAndRecover(u)));
		verifyEquality(u, t);
	}

	public void testEqualsAndHashCode() {
		org.apache.commons.math.stat.descriptive.StatisticalSummaryValues u = new org.apache.commons.math.stat.descriptive.StatisticalSummaryValues(1 , 2 , 3 , 4 , 5 , 6);
		org.apache.commons.math.stat.descriptive.StatisticalSummaryValues t = null;
		junit.framework.Assert.assertTrue("reflexive", u.equals(u));
		junit.framework.Assert.assertFalse("non-null compared to null", u.equals(t));
		junit.framework.Assert.assertFalse("wrong type", u.equals(java.lang.Double.valueOf(0)));
		t = new org.apache.commons.math.stat.descriptive.StatisticalSummaryValues(1 , 2 , 3 , 4 , 5 , 6);
		junit.framework.Assert.assertTrue("instances with same data should be equal", t.equals(u));
		junit.framework.Assert.assertEquals("hash code", u.hashCode(), t.hashCode());
		u = new org.apache.commons.math.stat.descriptive.StatisticalSummaryValues(java.lang.Double.NaN , 2 , 3 , 4 , 5 , 6);
		t = new org.apache.commons.math.stat.descriptive.StatisticalSummaryValues(1 , java.lang.Double.NaN , 3 , 4 , 5 , 6);
		junit.framework.Assert.assertFalse("instances based on different data should be different", ((u.equals(t)) || (t.equals(u))));
	}

	private void verifyEquality(org.apache.commons.math.stat.descriptive.StatisticalSummaryValues s, org.apache.commons.math.stat.descriptive.StatisticalSummaryValues u) {
		junit.framework.Assert.assertEquals("N", s.getN(), u.getN());
		org.apache.commons.math.TestUtils.assertEquals("sum", s.getSum(), u.getSum(), 0);
		org.apache.commons.math.TestUtils.assertEquals("var", s.getVariance(), u.getVariance(), 0);
		org.apache.commons.math.TestUtils.assertEquals("std", s.getStandardDeviation(), u.getStandardDeviation(), 0);
		org.apache.commons.math.TestUtils.assertEquals("mean", s.getMean(), u.getMean(), 0);
		org.apache.commons.math.TestUtils.assertEquals("min", s.getMin(), u.getMin(), 0);
		org.apache.commons.math.TestUtils.assertEquals("max", s.getMax(), u.getMax(), 0);
	}
}

