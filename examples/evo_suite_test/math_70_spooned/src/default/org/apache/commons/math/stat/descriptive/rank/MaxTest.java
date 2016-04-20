package org.apache.commons.math.stat.descriptive.rank;


public class MaxTest extends org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest {
	protected org.apache.commons.math.stat.descriptive.rank.Max stat;

	public MaxTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.UnivariateStatistic getUnivariateStatistic() {
		return new org.apache.commons.math.stat.descriptive.rank.Max();
	}

	@java.lang.Override
	public double expectedValue() {
		return this.max;
	}

	public void testSpecialValues() {
		double[] testArray = new double[]{ 0.0 , java.lang.Double.NaN , java.lang.Double.NEGATIVE_INFINITY , java.lang.Double.POSITIVE_INFINITY };
		org.apache.commons.math.stat.descriptive.rank.Max max = new org.apache.commons.math.stat.descriptive.rank.Max();
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(max.getResult()));
		max.increment(testArray[0]);
		junit.framework.Assert.assertEquals(0.0, max.getResult(), 0);
		max.increment(testArray[1]);
		junit.framework.Assert.assertEquals(0.0, max.getResult(), 0);
		max.increment(testArray[2]);
		junit.framework.Assert.assertEquals(0.0, max.getResult(), 0);
		max.increment(testArray[3]);
		junit.framework.Assert.assertEquals(java.lang.Double.POSITIVE_INFINITY, max.getResult(), 0);
		junit.framework.Assert.assertEquals(java.lang.Double.POSITIVE_INFINITY, max.evaluate(testArray), 0);
	}

	public void testNaNs() {
		org.apache.commons.math.stat.descriptive.rank.Max max = new org.apache.commons.math.stat.descriptive.rank.Max();
		double nan = java.lang.Double.NaN;
		junit.framework.Assert.assertEquals(3.0, max.evaluate(new double[]{ nan , 2.0 , 3.0 }), 0);
		junit.framework.Assert.assertEquals(3.0, max.evaluate(new double[]{ 1.0 , nan , 3.0 }), 0);
		junit.framework.Assert.assertEquals(2.0, max.evaluate(new double[]{ 1.0 , 2.0 , nan }), 0);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(max.evaluate(new double[]{ nan , nan , nan })));
	}
}

