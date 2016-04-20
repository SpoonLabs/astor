package org.apache.commons.math.random;


public class RandomDataImpl implements java.io.Serializable , org.apache.commons.math.random.RandomData {
	private static final long serialVersionUID = -626730818244969716L;

	private org.apache.commons.math.random.RandomGenerator rand = null;

	private java.security.SecureRandom secRand = null;

	public RandomDataImpl() {
	}

	public RandomDataImpl(org.apache.commons.math.random.RandomGenerator rand) {
		super();
		this.rand = rand;
	}

	public java.lang.String nextHexString(int len) {
		if (len <= 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("length must be positive ({0})", len);
		} 
		org.apache.commons.math.random.RandomGenerator ran = getRan();
		java.lang.StringBuffer outBuffer = new java.lang.StringBuffer();
		byte[] randomBytes = new byte[(len / 2) + 1];
		ran.nextBytes(randomBytes);
		for (int i = 0 ; i < (randomBytes.length) ; i++) {
			java.lang.Integer c = java.lang.Integer.valueOf(randomBytes[i]);
			java.lang.String hex = java.lang.Integer.toHexString(((c.intValue()) + 128));
			if ((hex.length()) == 1) {
				hex = "0" + hex;
			} 
			outBuffer.append(hex);
		}
		return outBuffer.toString().substring(0, len);
	}

	public int nextInt(int lower, int upper) {
		if (lower >= upper) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("upper bound ({0}) must be greater than lower bound ({1})", upper, lower);
		} 
		double r = getRan().nextDouble();
		return ((int)(((r * upper) + ((1.0 - r) * lower)) + r));
	}

	public long nextLong(long lower, long upper) {
		if (lower >= upper) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("upper bound ({0}) must be greater than lower bound ({1})", upper, lower);
		} 
		double r = getRan().nextDouble();
		return ((long)(((r * upper) + ((1.0 - r) * lower)) + r));
	}

	public java.lang.String nextSecureHexString(int len) {
		if (len <= 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("length must be positive ({0})", len);
		} 
		java.security.SecureRandom secRan = getSecRan();
		java.security.MessageDigest alg = null;
		try {
			alg = java.security.MessageDigest.getInstance("SHA-1");
		} catch (java.security.NoSuchAlgorithmException ex) {
			throw org.apache.commons.math.MathRuntimeException.createInternalError(ex);
		}
		alg.reset();
		int numIter = (len / 40) + 1;
		java.lang.StringBuffer outBuffer = new java.lang.StringBuffer();
		for (int iter = 1 ; iter < (numIter + 1) ; iter++) {
			byte[] randomBytes = new byte[40];
			secRan.nextBytes(randomBytes);
			alg.update(randomBytes);
			byte[] hash = alg.digest();
			for (int i = 0 ; i < (hash.length) ; i++) {
				java.lang.Integer c = java.lang.Integer.valueOf(hash[i]);
				java.lang.String hex = java.lang.Integer.toHexString(((c.intValue()) + 128));
				if ((hex.length()) == 1) {
					hex = "0" + hex;
				} 
				outBuffer.append(hex);
			}
		}
		return outBuffer.toString().substring(0, len);
	}

	public int nextSecureInt(int lower, int upper) {
		if (lower >= upper) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("upper bound ({0}) must be greater than lower bound ({1})", upper, lower);
		} 
		java.security.SecureRandom sec = getSecRan();
		return lower + ((int)(((sec.nextDouble()) * ((upper - lower) + 1))));
	}

	public long nextSecureLong(long lower, long upper) {
		if (lower >= upper) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("upper bound ({0}) must be greater than lower bound ({1})", upper, lower);
		} 
		java.security.SecureRandom sec = getSecRan();
		return lower + ((long)(((sec.nextDouble()) * ((upper - lower) + 1))));
	}

	public long nextPoisson(double mean) {
		if (mean <= 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("the Poisson mean must be positive ({0})", mean);
		} 
		final org.apache.commons.math.random.RandomGenerator generator = getRan();
		final double pivot = 40.0;
		if (mean < pivot) {
			double p = java.lang.Math.exp(-mean);
			long n = 0;
			double r = 1.0;
			double rnd = 1.0;
			while (n < (1000 * mean)) {
				rnd = generator.nextDouble();
				r = r * rnd;
				if (r >= p) {
					n++;
				} else {
					return n;
				}
			}
			return n;
		} else {
			final double lambda = java.lang.Math.floor(mean);
			final double lambdaFractional = mean - lambda;
			final double logLambda = java.lang.Math.log(lambda);
			final double logLambdaFactorial = org.apache.commons.math.util.MathUtils.factorialLog(((int)(lambda)));
			final long y2 = lambdaFractional < (java.lang.Double.MIN_VALUE) ? 0 : nextPoisson(lambdaFractional);
			final double delta = java.lang.Math.sqrt((lambda * (java.lang.Math.log((((32 * lambda) / (java.lang.Math.PI)) + 1)))));
			final double halfDelta = delta / 2;
			final double twolpd = (2 * lambda) + delta;
			final double a1 = (java.lang.Math.sqrt(((java.lang.Math.PI) * twolpd))) * (java.lang.Math.exp(((1 / 8) * lambda)));
			final double a2 = (twolpd / delta) * (java.lang.Math.exp((((-delta) * (1 + delta)) / twolpd)));
			final double aSum = (a1 + a2) + 1;
			final double p1 = a1 / aSum;
			final double p2 = a2 / aSum;
			final double c1 = 1 / (8 * lambda);
			double x = 0;
			double y = 0;
			double v = 0;
			int a = 0;
			double t = 0;
			double qr = 0;
			double qa = 0;
			for ( ;  ; ) {
				final double u = nextUniform(0.0, 1);
				if (u <= p1) {
					final double n = nextGaussian(0.0, 1.0);
					x = (n * (java.lang.Math.sqrt((lambda + halfDelta)))) - 0.5;
					if ((x > delta) || (x < (-lambda))) {
						continue;
					} 
					y = x < 0 ? java.lang.Math.floor(x) : java.lang.Math.ceil(x);
					final double e = nextExponential(1.0);
					v = ((-e) - ((n * n) / 2)) + c1;
				} else {
					if (u > (p1 + p2)) {
						y = lambda;
						break;
					} else {
						x = delta + ((twolpd / delta) * (nextExponential(1.0)));
						y = java.lang.Math.ceil(x);
						v = (-(nextExponential(1.0))) - ((delta * (x + 1)) / twolpd);
					}
				}
				a = x < 0 ? 1 : 0;
				t = (y * (y + 1)) / (2 * lambda);
				if ((v < (-t)) && (a == 0)) {
					y = lambda + y;
					break;
				} 
				qr = t * ((((2 * y) + 1) / (6 * lambda)) - 1);
				qa = qr - ((t * t) / (3 * (lambda + (a * (y + 1)))));
				if (v < qa) {
					y = lambda + y;
					break;
				} 
				if (v > qr) {
					continue;
				} 
				if (v < (((y * logLambda) - (org.apache.commons.math.util.MathUtils.factorialLog(((int)(y + lambda))))) + logLambdaFactorial)) {
					y = lambda + y;
					break;
				} 
			}
			return y2 + ((long)(y));
		}
	}

	public double nextGaussian(double mu, double sigma) {
		if (sigma <= 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("standard deviation must be positive ({0})", sigma);
		} 
		return (sigma * (getRan().nextGaussian())) + mu;
	}

	public double nextExponential(double mean) {
		if (mean <= 0.0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("mean must be positive ({0})", mean);
		} 
		final org.apache.commons.math.random.RandomGenerator generator = getRan();
		double unif = generator.nextDouble();
		while (unif == 0.0) {
			unif = generator.nextDouble();
		}
		return (-mean) * (java.lang.Math.log(unif));
	}

	public double nextUniform(double lower, double upper) {
		if (lower >= upper) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("upper bound ({0}) must be greater than lower bound ({1})", upper, lower);
		} 
		final org.apache.commons.math.random.RandomGenerator generator = getRan();
		double u = generator.nextDouble();
		while (u <= 0.0) {
			u = generator.nextDouble();
		}
		return lower + (u * (upper - lower));
	}

	private org.apache.commons.math.random.RandomGenerator getRan() {
		if ((rand) == null) {
			rand = new org.apache.commons.math.random.JDKRandomGenerator();
			rand.setSeed(java.lang.System.currentTimeMillis());
		} 
		return rand;
	}

	private java.security.SecureRandom getSecRan() {
		if ((secRand) == null) {
			secRand = new java.security.SecureRandom();
			secRand.setSeed(java.lang.System.currentTimeMillis());
		} 
		return secRand;
	}

	public void reSeed(long seed) {
		if ((rand) == null) {
			rand = new org.apache.commons.math.random.JDKRandomGenerator();
		} 
		rand.setSeed(seed);
	}

	public void reSeedSecure() {
		if ((secRand) == null) {
			secRand = new java.security.SecureRandom();
		} 
		secRand.setSeed(java.lang.System.currentTimeMillis());
	}

	public void reSeedSecure(long seed) {
		if ((secRand) == null) {
			secRand = new java.security.SecureRandom();
		} 
		secRand.setSeed(seed);
	}

	public void reSeed() {
		if ((rand) == null) {
			rand = new org.apache.commons.math.random.JDKRandomGenerator();
		} 
		rand.setSeed(java.lang.System.currentTimeMillis());
	}

	public void setSecureAlgorithm(java.lang.String algorithm, java.lang.String provider) throws java.security.NoSuchAlgorithmException, java.security.NoSuchProviderException {
		secRand = java.security.SecureRandom.getInstance(algorithm, provider);
	}

	public int[] nextPermutation(int n, int k) {
		if (k > n) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("permutation k ({0}) exceeds n ({1})", k, n);
		} 
		if (k == 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("permutation k ({0}) must be positive", k);
		} 
		int[] index = getNatural(n);
		shuffle(index, (n - k));
		int[] result = new int[k];
		for (int i = 0 ; i < k ; i++) {
			result[i] = index[((n - i) - 1)];
		}
		return result;
	}

	public java.lang.Object[] nextSample(java.util.Collection<?> c, int k) {
		int len = c.size();
		if (k > len) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("sample size ({0}) exceeds collection size ({1})");
		} 
		if (k <= 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("sample size must be positive ({0})", k);
		} 
		java.lang.Object[] objects = c.toArray();
		int[] index = nextPermutation(len, k);
		java.lang.Object[] result = new java.lang.Object[k];
		for (int i = 0 ; i < k ; i++) {
			result[i] = objects[index[i]];
		}
		return result;
	}

	private void shuffle(int[] list, int end) {
		int target = 0;
		for (int i = (list.length) - 1 ; i >= end ; i--) {
			if (i == 0) {
				target = 0;
			} else {
				target = nextInt(0, i);
			}
			int temp = list[target];
			list[target] = list[i];
			list[i] = temp;
		}
	}

	private int[] getNatural(int n) {
		int[] natural = new int[n];
		for (int i = 0 ; i < n ; i++) {
			natural[i] = i;
		}
		return natural;
	}
}

