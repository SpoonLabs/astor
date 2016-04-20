package org.apache.commons.math.distribution;


public interface BinomialDistribution extends org.apache.commons.math.distribution.IntegerDistribution {
	int getNumberOfTrials();

	double getProbabilityOfSuccess();

	@java.lang.Deprecated
	void setNumberOfTrials(int trials);

	@java.lang.Deprecated
	void setProbabilityOfSuccess(double p);
}

