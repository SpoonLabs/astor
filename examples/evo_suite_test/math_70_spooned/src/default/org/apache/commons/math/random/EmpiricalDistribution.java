package org.apache.commons.math.random;


public interface EmpiricalDistribution {
	void load(double[] dataArray);

	void load(java.io.File file) throws java.io.IOException;

	void load(java.net.URL url) throws java.io.IOException;

	double getNextValue() throws java.lang.IllegalStateException;

	org.apache.commons.math.stat.descriptive.StatisticalSummary getSampleStats() throws java.lang.IllegalStateException;

	boolean isLoaded();

	int getBinCount();

	java.util.List<org.apache.commons.math.stat.descriptive.SummaryStatistics> getBinStats();

	double[] getUpperBounds();
}

