package org.apache.commons.math.random;


public class JDKRandomGenerator extends java.util.Random implements org.apache.commons.math.random.RandomGenerator {
	private static final long serialVersionUID = -7745277476784028798L;

	public void setSeed(int seed) {
		setSeed(((long)(seed)));
	}

	public void setSeed(int[] seed) {
		final long prime = 4294967291L;
		long combined = 0L;
		for (int s : seed) {
			combined = (combined * prime) + s;
		}
		setSeed(combined);
	}
}

