package org.apache.commons.math;


public abstract class ConvergingAlgorithmImpl implements org.apache.commons.math.ConvergingAlgorithm {
	protected double absoluteAccuracy;

	protected double relativeAccuracy;

	protected int maximalIterationCount;

	protected double defaultAbsoluteAccuracy;

	protected double defaultRelativeAccuracy;

	protected int defaultMaximalIterationCount;

	protected int iterationCount;

	protected ConvergingAlgorithmImpl(final int defaultMaximalIterationCount ,final double defaultAbsoluteAccuracy) {
		this.defaultAbsoluteAccuracy = defaultAbsoluteAccuracy;
		this.defaultRelativeAccuracy = 1.0E-14;
		this.absoluteAccuracy = defaultAbsoluteAccuracy;
		this.relativeAccuracy = defaultRelativeAccuracy;
		this.defaultMaximalIterationCount = defaultMaximalIterationCount;
		this.maximalIterationCount = defaultMaximalIterationCount;
		this.iterationCount = 0;
	}

	public int getIterationCount() {
		return iterationCount;
	}

	public void setAbsoluteAccuracy(double accuracy) {
		absoluteAccuracy = accuracy;
	}

	public double getAbsoluteAccuracy() {
		return absoluteAccuracy;
	}

	public void resetAbsoluteAccuracy() {
		absoluteAccuracy = defaultAbsoluteAccuracy;
	}

	public void setMaximalIterationCount(int count) {
		maximalIterationCount = count;
	}

	public int getMaximalIterationCount() {
		return maximalIterationCount;
	}

	public void resetMaximalIterationCount() {
		maximalIterationCount = defaultMaximalIterationCount;
	}

	public void setRelativeAccuracy(double accuracy) {
		relativeAccuracy = accuracy;
	}

	public double getRelativeAccuracy() {
		return relativeAccuracy;
	}

	public void resetRelativeAccuracy() {
		relativeAccuracy = defaultRelativeAccuracy;
	}
}

