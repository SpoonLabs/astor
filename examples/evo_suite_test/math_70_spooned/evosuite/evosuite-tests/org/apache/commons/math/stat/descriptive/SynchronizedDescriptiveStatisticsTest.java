package org.apache.commons.math.stat.descriptive;


public final class SynchronizedDescriptiveStatisticsTest extends org.apache.commons.math.stat.descriptive.DescriptiveStatisticsTest {
	public SynchronizedDescriptiveStatisticsTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	protected org.apache.commons.math.stat.descriptive.DescriptiveStatistics createDescriptiveStatistics() {
		return new org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics();
	}
}

