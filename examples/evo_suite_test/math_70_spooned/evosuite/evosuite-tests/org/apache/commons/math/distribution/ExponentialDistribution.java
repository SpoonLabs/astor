package org.apache.commons.math.distribution;


public interface ExponentialDistribution extends org.apache.commons.math.distribution.ContinuousDistribution , org.apache.commons.math.distribution.HasDensity<java.lang.Double> {
	@java.lang.Deprecated
	void setMean(double mean);

	double getMean();

	double density(java.lang.Double x);
}

