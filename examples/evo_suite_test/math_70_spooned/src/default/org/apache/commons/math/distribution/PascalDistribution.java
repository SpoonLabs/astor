package org.apache.commons.math.distribution;


public interface PascalDistribution extends org.apache.commons.math.distribution.IntegerDistribution {
	int getNumberOfSuccesses();

	double getProbabilityOfSuccess();

	@java.lang.Deprecated
	void setNumberOfSuccesses(int successes);

	@java.lang.Deprecated
	void setProbabilityOfSuccess(double p);
}

