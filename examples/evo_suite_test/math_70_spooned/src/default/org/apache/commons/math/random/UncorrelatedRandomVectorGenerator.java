package org.apache.commons.math.random;


public class UncorrelatedRandomVectorGenerator implements org.apache.commons.math.random.RandomVectorGenerator {
	private final org.apache.commons.math.random.NormalizedRandomGenerator generator;

	private final double[] mean;

	private final double[] standardDeviation;

	public UncorrelatedRandomVectorGenerator(double[] mean ,double[] standardDeviation ,org.apache.commons.math.random.NormalizedRandomGenerator generator) {
		if ((mean.length) != (standardDeviation.length)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("dimension mismatch {0} != {1}", mean.length, standardDeviation.length);
		} 
		this.mean = mean.clone();
		this.standardDeviation = standardDeviation.clone();
		this.generator = generator;
	}

	public UncorrelatedRandomVectorGenerator(int dimension ,org.apache.commons.math.random.NormalizedRandomGenerator generator) {
		mean = new double[dimension];
		standardDeviation = new double[dimension];
		java.util.Arrays.fill(standardDeviation, 1.0);
		this.generator = generator;
	}

	public double[] nextVector() {
		double[] random = new double[mean.length];
		for (int i = 0 ; i < (random.length) ; ++i) {
			random[i] = (mean[i]) + ((standardDeviation[i]) * (generator.nextNormalizedDouble()));
		}
		return random;
	}
}

