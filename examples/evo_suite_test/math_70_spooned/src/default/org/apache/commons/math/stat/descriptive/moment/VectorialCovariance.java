package org.apache.commons.math.stat.descriptive.moment;


public class VectorialCovariance implements java.io.Serializable {
	private static final long serialVersionUID = 4118372414238930270L;

	private final double[] sums;

	private final double[] productsSums;

	private final boolean isBiasCorrected;

	private long n;

	public VectorialCovariance(int dimension ,boolean isBiasCorrected) {
		sums = new double[dimension];
		productsSums = new double[(dimension * (dimension + 1)) / 2];
		n = 0;
		this.isBiasCorrected = isBiasCorrected;
	}

	public void increment(double[] v) throws org.apache.commons.math.DimensionMismatchException {
		if ((v.length) != (sums.length)) {
			throw new org.apache.commons.math.DimensionMismatchException(v.length , sums.length);
		} 
		int k = 0;
		for (int i = 0 ; i < (v.length) ; ++i) {
			sums[i] += v[i];
			for (int j = 0 ; j <= i ; ++j) {
				productsSums[k++] += (v[i]) * (v[j]);
			}
		}
		(n)++;
	}

	public org.apache.commons.math.linear.RealMatrix getResult() {
		int dimension = sums.length;
		org.apache.commons.math.linear.RealMatrix result = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(dimension, dimension);
		if ((n) > 1) {
			double c = 1.0 / ((n) * (isBiasCorrected ? (n) - 1 : n));
			int k = 0;
			for (int i = 0 ; i < dimension ; ++i) {
				for (int j = 0 ; j <= i ; ++j) {
					double e = c * (((n) * (productsSums[k++])) - ((sums[i]) * (sums[j])));
					result.setEntry(i, j, e);
					result.setEntry(j, i, e);
				}
			}
		} 
		return result;
	}

	public long getN() {
		return n;
	}

	public void clear() {
		n = 0;
		java.util.Arrays.fill(sums, 0.0);
		java.util.Arrays.fill(productsSums, 0.0);
	}

	@java.lang.Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + (isBiasCorrected ? 1231 : 1237);
		result = (prime * result) + ((int)(((n) ^ ((n) >>> 32))));
		result = (prime * result) + (java.util.Arrays.hashCode(productsSums));
		result = (prime * result) + (java.util.Arrays.hashCode(sums));
		return result;
	}

	@java.lang.Override
	public boolean equals(java.lang.Object obj) {
		if ((this) == obj)
			return true;
		
		if (!(obj instanceof org.apache.commons.math.stat.descriptive.moment.VectorialCovariance))
			return false;
		
		org.apache.commons.math.stat.descriptive.moment.VectorialCovariance other = ((org.apache.commons.math.stat.descriptive.moment.VectorialCovariance)(obj));
		if ((isBiasCorrected) != (other.isBiasCorrected))
			return false;
		
		if ((n) != (other.n))
			return false;
		
		if (!(java.util.Arrays.equals(productsSums, other.productsSums)))
			return false;
		
		if (!(java.util.Arrays.equals(sums, other.sums)))
			return false;
		
		return true;
	}
}

