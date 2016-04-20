package org.apache.commons.math.random;


public class TestRandomGenerator extends org.apache.commons.math.random.AbstractRandomGenerator {
	private java.util.Random random = new java.util.Random();

	@java.lang.Override
	public void setSeed(long seed) {
		clear();
		random.setSeed(seed);
	}

	@java.lang.Override
	public double nextDouble() {
		return random.nextDouble();
	}
}

