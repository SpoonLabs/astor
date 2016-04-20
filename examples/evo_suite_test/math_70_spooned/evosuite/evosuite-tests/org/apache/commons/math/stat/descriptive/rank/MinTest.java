package org.apache.commons.math.stat.descriptive.rank;


public class MinTest extends org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest {
	protected org.apache.commons.math.stat.descriptive.rank.Min stat;

	public MinTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.UnivariateStatistic getUnivariateStatistic() {
		return new org.apache.commons.math.stat.descriptive.rank.Min();
	}

	@java.lang.Override
	public double expectedValue() {
		return this.min;
	}

	public void testSpecialValues() {
		double[] testArray = new double[]{ 0.0 , java.lang.Double.NaN , java.lang.Double.POSITIVE_INFINITY , java.lang.Double.NEGATIVE_INFINITY };
		org.apache.commons.math.stat.descriptive.rank.Min min = new org.apache.commons.math.stat.descriptive.rank.Min();
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(min.getResult()));
		min.increment(testArray[0]);
		junit.framework.Assert.assertEquals(0.0, min.getResult(), 0);
		min.increment(testArray[1]);
		junit.framework.Assert.assertEquals(0.0, min.getResult(), 0);
		min.increment(testArray[2]);
		junit.framework.Assert.assertEquals(0.0, min.getResult(), 0);
		min.increment(testArray[3]);
		junit.framework.Assert.assertEquals(java.lang.Double.NEGATIVE_INFINITY, min.getResult(), 0);
		junit.framework.Assert.assertEquals(java.lang.Double.NEGATIVE_INFINITY, min.evaluate(testArray), 0);
	}

	public void testNaNs() {
		org.apache.commons.math.stat.descriptive.rank.Min min = new org.apache.commons.math.stat.descriptive.rank.Min();
		double nan = java.lang.Double.NaN;
		junit.framework.Assert.assertEquals(2.0, min.evaluate(new double[]{ nan , 2.0 , 3.0 }), 0);
		junit.framework.Assert.assertEquals(1.0, min.evaluate(new double[]{ 1.0 , nan , 3.0 }), 0);
		junit.framework.Assert.assertEquals(1.0, min.evaluate(new double[]{ 1.0 , 2.0 , nan }), 0);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(min.evaluate(new double[]{ nan , nan , nan })));
	}
}

