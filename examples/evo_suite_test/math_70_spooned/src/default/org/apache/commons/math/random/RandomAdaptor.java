package org.apache.commons.math.random;


public class RandomAdaptor extends java.util.Random implements org.apache.commons.math.random.RandomGenerator {
	private static final long serialVersionUID = 2306581345647615033L;

	private org.apache.commons.math.random.RandomGenerator randomGenerator = null;

	@java.lang.SuppressWarnings(value = "unused")
	private RandomAdaptor() {
	}

	public RandomAdaptor(org.apache.commons.math.random.RandomGenerator randomGenerator) {
		this.randomGenerator = randomGenerator;
	}

	public static java.util.Random createAdaptor(org.apache.commons.math.random.RandomGenerator randomGenerator) {
		return new org.apache.commons.math.random.RandomAdaptor(randomGenerator);
	}

	@java.lang.Override
	public boolean nextBoolean() {
		return randomGenerator.nextBoolean();
	}

	@java.lang.Override
	public void nextBytes(byte[] bytes) {
		randomGenerator.nextBytes(bytes);
	}

	@java.lang.Override
	public double nextDouble() {
		return randomGenerator.nextDouble();
	}

	@java.lang.Override
	public float nextFloat() {
		return randomGenerator.nextFloat();
	}

	@java.lang.Override
	public double nextGaussian() {
		return randomGenerator.nextGaussian();
	}

	@java.lang.Override
	public int nextInt() {
		return randomGenerator.nextInt();
	}

	@java.lang.Override
	public int nextInt(int n) {
		return randomGenerator.nextInt(n);
	}

	@java.lang.Override
	public long nextLong() {
		return randomGenerator.nextLong();
	}

	public void setSeed(int seed) {
		if ((randomGenerator) != null) {
			randomGenerator.setSeed(seed);
		} 
	}

	public void setSeed(int[] seed) {
		if ((randomGenerator) != null) {
			randomGenerator.setSeed(seed);
		} 
	}

	@java.lang.Override
	public void setSeed(long seed) {
		if ((randomGenerator) != null) {
			randomGenerator.setSeed(seed);
		} 
	}
}

