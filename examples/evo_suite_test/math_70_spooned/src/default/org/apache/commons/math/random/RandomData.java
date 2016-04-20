package org.apache.commons.math.random;


public interface RandomData {
	java.lang.String nextHexString(int len);

	int nextInt(int lower, int upper);

	long nextLong(long lower, long upper);

	java.lang.String nextSecureHexString(int len);

	int nextSecureInt(int lower, int upper);

	long nextSecureLong(long lower, long upper);

	long nextPoisson(double mean);

	double nextGaussian(double mu, double sigma);

	double nextExponential(double mean);

	double nextUniform(double lower, double upper);

	int[] nextPermutation(int n, int k);

	java.lang.Object[] nextSample(java.util.Collection<?> c, int k);
}

