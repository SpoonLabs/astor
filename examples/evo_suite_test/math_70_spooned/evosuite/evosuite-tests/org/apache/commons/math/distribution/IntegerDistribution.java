package org.apache.commons.math.distribution;


public interface IntegerDistribution extends org.apache.commons.math.distribution.DiscreteDistribution {
	double probability(int x);

	double cumulativeProbability(int x) throws org.apache.commons.math.MathException;

	double cumulativeProbability(int x0, int x1) throws org.apache.commons.math.MathException;

	int inverseCumulativeProbability(double p) throws org.apache.commons.math.MathException;
}

