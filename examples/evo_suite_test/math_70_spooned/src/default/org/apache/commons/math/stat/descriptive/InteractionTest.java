package org.apache.commons.math.stat.descriptive;


public class InteractionTest extends junit.framework.TestCase {
	protected double mean = 12.4045454545455;

	protected double var = 10.0023593073593;

	protected double skew = 1.43742372919619;

	protected double kurt = 2.3771912648047;

	protected double tolerance = 1.0E-11;

	protected double[] testArray = new double[]{ 12.5 , 12 , 11.8 , 14.2 , 14.9 , 14.5 , 21 , 8.2 , 10.3 , 11.3 , 14.1 , 9.9 , 12.2 , 12 , 12.1 , 11 , 19.8 , 11 , 10 , 8.8 , 9 , 12.3 };

	public InteractionTest(java.lang.String name) {
		super(name);
	}

	public void testInteraction() {
		org.apache.commons.math.stat.descriptive.moment.FourthMoment m4 = new org.apache.commons.math.stat.descriptive.moment.FourthMoment();
		org.apache.commons.math.stat.descriptive.moment.Mean m = new org.apache.commons.math.stat.descriptive.moment.Mean(m4);
		org.apache.commons.math.stat.descriptive.moment.Variance v = new org.apache.commons.math.stat.descriptive.moment.Variance(m4);
		org.apache.commons.math.stat.descriptive.moment.Skewness s = new org.apache.commons.math.stat.descriptive.moment.Skewness(m4);
		org.apache.commons.math.stat.descriptive.moment.Kurtosis k = new org.apache.commons.math.stat.descriptive.moment.Kurtosis(m4);
		for (int i = 0 ; i < (testArray.length) ; i++) {
			m4.increment(testArray[i]);
		}
		junit.framework.Assert.assertEquals(mean, m.getResult(), tolerance);
		junit.framework.Assert.assertEquals(var, v.getResult(), tolerance);
		junit.framework.Assert.assertEquals(skew, s.getResult(), tolerance);
		junit.framework.Assert.assertEquals(kurt, k.getResult(), tolerance);
	}
}

