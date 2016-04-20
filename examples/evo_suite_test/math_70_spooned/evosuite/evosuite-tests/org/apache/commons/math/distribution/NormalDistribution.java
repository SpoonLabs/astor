package org.apache.commons.math.distribution;


public interface NormalDistribution extends org.apache.commons.math.distribution.ContinuousDistribution , org.apache.commons.math.distribution.HasDensity<java.lang.Double> {
	double getMean();

	@java.lang.Deprecated
	void setMean(double mean);

	double getStandardDeviation();

	@java.lang.Deprecated
	void setStandardDeviation(double sd);

	double density(java.lang.Double x);
}

