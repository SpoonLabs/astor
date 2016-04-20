package org.apache.commons.math.stat.descriptive.moment;


public class VectorialMean implements java.io.Serializable {
	private static final long serialVersionUID = 8223009086481006892L;

	private final org.apache.commons.math.stat.descriptive.moment.Mean[] means;

	public VectorialMean(int dimension) {
		means = new org.apache.commons.math.stat.descriptive.moment.Mean[dimension];
		for (int i = 0 ; i < dimension ; ++i) {
			means[i] = new org.apache.commons.math.stat.descriptive.moment.Mean();
		}
	}

	public void increment(double[] v) throws org.apache.commons.math.DimensionMismatchException {
		if ((v.length) != (means.length)) {
			throw new org.apache.commons.math.DimensionMismatchException(v.length , means.length);
		} 
		for (int i = 0 ; i < (v.length) ; ++i) {
			means[i].increment(v[i]);
		}
	}

	public double[] getResult() {
		double[] result = new double[means.length];
		for (int i = 0 ; i < (result.length) ; ++i) {
			result[i] = means[i].getResult();
		}
		return result;
	}

	public long getN() {
		return (means.length) == 0 ? 0 : means[0].getN();
	}

	@java.lang.Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + (java.util.Arrays.hashCode(means));
		return result;
	}

	@java.lang.Override
	public boolean equals(java.lang.Object obj) {
		if ((this) == obj) {
			return true;
		} 
		if (!(obj instanceof org.apache.commons.math.stat.descriptive.moment.VectorialMean)) {
			return false;
		} 
		org.apache.commons.math.stat.descriptive.moment.VectorialMean other = ((org.apache.commons.math.stat.descriptive.moment.VectorialMean)(obj));
		if (!(java.util.Arrays.equals(means, other.means))) {
			return false;
		} 
		return true;
	}
}

