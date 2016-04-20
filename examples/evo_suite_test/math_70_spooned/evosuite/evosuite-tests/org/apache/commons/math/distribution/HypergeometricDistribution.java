package org.apache.commons.math.distribution;


public interface HypergeometricDistribution extends org.apache.commons.math.distribution.IntegerDistribution {
	int getNumberOfSuccesses();

	int getPopulationSize();

	int getSampleSize();

	@java.lang.Deprecated
	void setNumberOfSuccesses(int num);

	@java.lang.Deprecated
	void setPopulationSize(int size);

	@java.lang.Deprecated
	void setSampleSize(int size);
}

