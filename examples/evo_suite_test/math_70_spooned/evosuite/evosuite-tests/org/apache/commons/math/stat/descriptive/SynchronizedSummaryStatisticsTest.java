package org.apache.commons.math.stat.descriptive;


public final class SynchronizedSummaryStatisticsTest extends org.apache.commons.math.stat.descriptive.SummaryStatisticsTest {
	public SynchronizedSummaryStatisticsTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	protected org.apache.commons.math.stat.descriptive.SummaryStatistics createSummaryStatistics() {
		return new org.apache.commons.math.stat.descriptive.SynchronizedSummaryStatistics();
	}
}

