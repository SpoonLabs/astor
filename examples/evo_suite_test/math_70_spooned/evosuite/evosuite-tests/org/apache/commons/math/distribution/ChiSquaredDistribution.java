package org.apache.commons.math.distribution;


public interface ChiSquaredDistribution extends org.apache.commons.math.distribution.ContinuousDistribution , org.apache.commons.math.distribution.HasDensity<java.lang.Double> {
	@java.lang.Deprecated
	void setDegreesOfFreedom(double degreesOfFreedom);

	double getDegreesOfFreedom();

	double density(java.lang.Double x);
}

