package org.apache.commons.math.distribution;


public interface ContinuousDistribution extends org.apache.commons.math.distribution.Distribution {
	double inverseCumulativeProbability(double p) throws org.apache.commons.math.MathException;
}

