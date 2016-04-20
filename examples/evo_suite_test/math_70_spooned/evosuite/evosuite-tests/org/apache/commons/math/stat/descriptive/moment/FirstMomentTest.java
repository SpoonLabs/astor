package org.apache.commons.math.stat.descriptive.moment;


public class FirstMomentTest extends org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest {
	protected org.apache.commons.math.stat.descriptive.moment.FirstMoment stat;

	public FirstMomentTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.UnivariateStatistic getUnivariateStatistic() {
		return new org.apache.commons.math.stat.descriptive.moment.FirstMoment();
	}

	@java.lang.Override
	public double expectedValue() {
		return this.mean;
	}
}

