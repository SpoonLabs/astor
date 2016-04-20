package org.apache.commons.math.stat.descriptive.moment;


public class FourthMomentTest extends org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest {
	protected org.apache.commons.math.stat.descriptive.moment.FourthMoment stat;

	public FourthMomentTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.UnivariateStatistic getUnivariateStatistic() {
		return new org.apache.commons.math.stat.descriptive.moment.FourthMoment();
	}

	@java.lang.Override
	public double expectedValue() {
		return this.fourthMoment;
	}
}

