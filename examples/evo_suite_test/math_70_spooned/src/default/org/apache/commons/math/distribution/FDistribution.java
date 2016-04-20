package org.apache.commons.math.distribution;


public interface FDistribution extends org.apache.commons.math.distribution.ContinuousDistribution {
	@java.lang.Deprecated
	void setNumeratorDegreesOfFreedom(double degreesOfFreedom);

	double getNumeratorDegreesOfFreedom();

	@java.lang.Deprecated
	void setDenominatorDegreesOfFreedom(double degreesOfFreedom);

	double getDenominatorDegreesOfFreedom();
}

