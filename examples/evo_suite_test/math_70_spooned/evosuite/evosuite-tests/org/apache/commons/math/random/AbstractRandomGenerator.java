package org.apache.commons.math.random;


public abstract class AbstractRandomGenerator implements org.apache.commons.math.random.RandomGenerator {
	private double cachedNormalDeviate = java.lang.Double.NaN;

	public AbstractRandomGenerator() {
		super();
	}

	public void clear() {
		cachedNormalDeviate = java.lang.Double.NaN;
	}

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

	public abstract void setSeed(long seed);

	public void nextBytes(byte[] bytes) {
		int bytesOut = 0;
		while (bytesOut < (bytes.length)) {
			int randInt = nextInt();
			for (int i = 0 ; i < 3 ; i++) {
				if (i > 0) {
					randInt = randInt >> 8;
				} 
				bytes[bytesOut++] = ((byte)(randInt));
				if (bytesOut == (bytes.length)) {
					return ;
				} 
			}
		}
	}

	public int nextInt() {
		return ((int)((nextDouble()) * (java.lang.Integer.MAX_VALUE)));
	}

	public int nextInt(int n) {
		if (n <= 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("upper bound must be positive ({0})", n);
		} 
		int result = ((int)((nextDouble()) * n));
		return result < n ? result : n - 1;
	}

	public long nextLong() {
		return ((long)((nextDouble()) * (java.lang.Long.MAX_VALUE)));
	}

	public boolean nextBoolean() {
		return (nextDouble()) <= 0.5;
	}

	public float nextFloat() {
		return ((float)(nextDouble()));
	}

	public abstract double nextDouble();

	public double nextGaussian() {
		if (!(java.lang.Double.isNaN(cachedNormalDeviate))) {
			double dev = cachedNormalDeviate;
			cachedNormalDeviate = java.lang.Double.NaN;
			return dev;
		} 
		double v1 = 0;
		double v2 = 0;
		double s = 1;
		while (s >= 1) {
			v1 = (2 * (nextDouble())) - 1;
			v2 = (2 * (nextDouble())) - 1;
			s = (v1 * v1) + (v2 * v2);
		}
		if (s != 0) {
			s = java.lang.Math.sqrt((((-2) * (java.lang.Math.log(s))) / s));
		} 
		cachedNormalDeviate = v2 * s;
		return v1 * s;
	}
}

