package org.apache.commons.math.stat.descriptive.moment;


public class GeometricMeanTest extends org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest {
	protected org.apache.commons.math.stat.descriptive.moment.GeometricMean stat;

	public GeometricMeanTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.UnivariateStatistic getUnivariateStatistic() {
		return new org.apache.commons.math.stat.descriptive.moment.GeometricMean();
	}

	@java.lang.Override
	public double expectedValue() {
		return this.geoMean;
	}

	public void testSpecialValues() {
		org.apache.commons.math.stat.descriptive.moment.GeometricMean mean = new org.apache.commons.math.stat.descriptive.moment.GeometricMean();
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(mean.getResult()));
		mean.increment(1.0);
		junit.framework.Assert.assertFalse(java.lang.Double.isNaN(mean.getResult()));
		mean.increment(0.0);
		junit.framework.Assert.assertEquals(0.0, mean.getResult(), 0);
		mean.increment(java.lang.Double.POSITIVE_INFINITY);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(mean.getResult()));
		mean.clear();
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(mean.getResult()));
		mean.increment(java.lang.Double.POSITIVE_INFINITY);
		junit.framework.Assert.assertEquals(java.lang.Double.POSITIVE_INFINITY, mean.getResult(), 0);
		mean.increment(-2.0);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(mean.getResult()));
	}
}

