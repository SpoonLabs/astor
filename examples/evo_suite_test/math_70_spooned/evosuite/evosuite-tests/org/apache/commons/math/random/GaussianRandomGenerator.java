package org.apache.commons.math.random;


public class GaussianRandomGenerator implements org.apache.commons.math.random.NormalizedRandomGenerator {
	private final org.apache.commons.math.random.RandomGenerator generator;

	public GaussianRandomGenerator(final org.apache.commons.math.random.RandomGenerator generator) {
		this.generator = generator;
	}

	public double nextNormalizedDouble() {
		return generator.nextGaussian();
	}
}

