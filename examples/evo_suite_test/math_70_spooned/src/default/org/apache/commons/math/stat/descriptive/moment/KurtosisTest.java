package org.apache.commons.math.stat.descriptive.moment;


public class KurtosisTest extends org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest {
	protected org.apache.commons.math.stat.descriptive.moment.Kurtosis stat;

	public KurtosisTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.UnivariateStatistic getUnivariateStatistic() {
		return new org.apache.commons.math.stat.descriptive.moment.Kurtosis();
	}

	@java.lang.Override
	public double expectedValue() {
		return this.kurt;
	}

	public void testNaN() {
		org.apache.commons.math.stat.descriptive.moment.Kurtosis kurt = new org.apache.commons.math.stat.descriptive.moment.Kurtosis();
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(kurt.getResult()));
		kurt.increment(1.0);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(kurt.getResult()));
		kurt.increment(1.0);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(kurt.getResult()));
		kurt.increment(1.0);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(kurt.getResult()));
		kurt.increment(1.0);
		junit.framework.Assert.assertFalse(java.lang.Double.isNaN(kurt.getResult()));
	}
}

