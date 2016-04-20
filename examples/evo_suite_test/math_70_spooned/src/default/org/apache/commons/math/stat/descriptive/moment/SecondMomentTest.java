package org.apache.commons.math.stat.descriptive.moment;


public class SecondMomentTest extends org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest {
	protected org.apache.commons.math.stat.descriptive.moment.SecondMoment stat;

	public SecondMomentTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.UnivariateStatistic getUnivariateStatistic() {
		return new org.apache.commons.math.stat.descriptive.moment.SecondMoment();
	}

	@java.lang.Override
	public double expectedValue() {
		return this.secondMoment;
	}
}

