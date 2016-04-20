package org.apache.commons.math;


public interface ConvergingAlgorithm {
	void setMaximalIterationCount(int count);

	int getMaximalIterationCount();

	void resetMaximalIterationCount();

	void setAbsoluteAccuracy(double accuracy);

	double getAbsoluteAccuracy();

	void resetAbsoluteAccuracy();

	void setRelativeAccuracy(double accuracy);

	double getRelativeAccuracy();

	void resetRelativeAccuracy();

	int getIterationCount();
}

