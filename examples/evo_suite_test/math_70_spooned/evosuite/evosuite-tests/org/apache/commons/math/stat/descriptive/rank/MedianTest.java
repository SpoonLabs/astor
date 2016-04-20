package org.apache.commons.math.stat.descriptive.rank;


public class MedianTest extends org.apache.commons.math.stat.descriptive.UnivariateStatisticAbstractTest {
	protected org.apache.commons.math.stat.descriptive.rank.Median stat;

	public MedianTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.UnivariateStatistic getUnivariateStatistic() {
		return new org.apache.commons.math.stat.descriptive.rank.Median();
	}

	@java.lang.Override
	public double expectedValue() {
		return this.median;
	}
}

