package org.apache.commons.math.stat.descriptive;


public final class SynchronizedMultivariateSummaryStatisticsTest extends org.apache.commons.math.stat.descriptive.MultivariateSummaryStatisticsTest {
	public SynchronizedMultivariateSummaryStatisticsTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	protected org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics createMultivariateSummaryStatistics(int k, boolean isCovarianceBiasCorrected) {
		return new org.apache.commons.math.stat.descriptive.SynchronizedMultivariateSummaryStatistics(k , isCovarianceBiasCorrected);
	}
}

