package org.apache.commons.math.distribution;


public interface Distribution {
	double cumulativeProbability(double x) throws org.apache.commons.math.MathException;

	double cumulativeProbability(double x0, double x1) throws org.apache.commons.math.MathException;
}

