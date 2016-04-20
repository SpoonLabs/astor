package org.apache.commons.math.stat.descriptive.moment;


public class SkewnessTest extends org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest {
	protected org.apache.commons.math.stat.descriptive.moment.Skewness stat;

	public SkewnessTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.UnivariateStatistic getUnivariateStatistic() {
		return new org.apache.commons.math.stat.descriptive.moment.Skewness();
	}

	@java.lang.Override
	public double expectedValue() {
		return this.skew;
	}

	public void testNaN() {
		org.apache.commons.math.stat.descriptive.moment.Skewness skew = new org.apache.commons.math.stat.descriptive.moment.Skewness();
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(skew.getResult()));
		skew.increment(1.0);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(skew.getResult()));
		skew.increment(1.0);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(skew.getResult()));
		skew.increment(1.0);
		junit.framework.Assert.assertFalse(java.lang.Double.isNaN(skew.getResult()));
	}
}

