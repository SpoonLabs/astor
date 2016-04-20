package org.apache.commons.math.random;


public class UnitSphereRandomVectorGenerator implements org.apache.commons.math.random.RandomVectorGenerator {
	private final org.apache.commons.math.random.RandomGenerator rand;

	private final int dimension;

	public UnitSphereRandomVectorGenerator(final int dimension ,final org.apache.commons.math.random.RandomGenerator rand) {
		this.dimension = dimension;
		this.rand = rand;
	}

	public UnitSphereRandomVectorGenerator(final int dimension) {
		this(dimension, new org.apache.commons.math.random.MersenneTwister());
	}

	public double[] nextVector() {
		final double[] v = new double[dimension];
		double normSq;
		do {
			normSq = 0;
			for (int i = 0 ; i < (dimension) ; i++) {
				final double comp = (2 * (rand.nextDouble())) - 1;
				v[i] = comp;
				normSq += comp * comp;
			}
		} while (normSq > 1 );
		final double f = 1 / (java.lang.Math.sqrt(normSq));
		for (int i = 0 ; i < (dimension) ; i++) {
			v[i] *= f;
		}
		return v;
	}
}

