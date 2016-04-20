package org.apache.commons.math.stat.descriptive.moment;


public class ThirdMomentTest extends org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest {
	protected org.apache.commons.math.stat.descriptive.moment.ThirdMoment stat;

	public ThirdMomentTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.UnivariateStatistic getUnivariateStatistic() {
		return new org.apache.commons.math.stat.descriptive.moment.ThirdMoment();
	}

	@java.lang.Override
	public double expectedValue() {
		return this.thirdMoment;
	}
}

