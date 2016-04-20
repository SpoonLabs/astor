package org.apache.commons.math.stat.descriptive;


public class MultivariateSummaryStatistics implements java.io.Serializable , org.apache.commons.math.stat.descriptive.StatisticalMultivariateSummary {
	private static final long serialVersionUID = 2271900808994826718L;

	private int k;

	private long n = 0;

	private org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] sumImpl;

	private org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] sumSqImpl;

	private org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] minImpl;

	private org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] maxImpl;

	private org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] sumLogImpl;

	private org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] geoMeanImpl;

	private org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] meanImpl;

	private org.apache.commons.math.stat.descriptive.moment.VectorialCovariance covarianceImpl;

	public MultivariateSummaryStatistics(int k ,boolean isCovarianceBiasCorrected) {
		this.k = k;
		sumImpl = new org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[k];
		sumSqImpl = new org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[k];
		minImpl = new org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[k];
		maxImpl = new org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[k];
		sumLogImpl = new org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[k];
		geoMeanImpl = new org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[k];
		meanImpl = new org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[k];
		for (int i = 0 ; i < k ; ++i) {
			sumImpl[i] = new org.apache.commons.math.stat.descriptive.summary.Sum();
			sumSqImpl[i] = new org.apache.commons.math.stat.descriptive.summary.SumOfSquares();
			minImpl[i] = new org.apache.commons.math.stat.descriptive.rank.Min();
			maxImpl[i] = new org.apache.commons.math.stat.descriptive.rank.Max();
			sumLogImpl[i] = new org.apache.commons.math.stat.descriptive.summary.SumOfLogs();
			geoMeanImpl[i] = new org.apache.commons.math.stat.descriptive.moment.GeometricMean();
			meanImpl[i] = new org.apache.commons.math.stat.descriptive.moment.Mean();
		}
		covarianceImpl = new org.apache.commons.math.stat.descriptive.moment.VectorialCovariance(k , isCovarianceBiasCorrected);
	}

	public void addValue(double[] value) throws org.apache.commons.math.DimensionMismatchException {
		checkDimension(value.length);
		for (int i = 0 ; i < (k) ; ++i) {
			double v = value[i];
			sumImpl[i].increment(v);
			sumSqImpl[i].increment(v);
			minImpl[i].increment(v);
			maxImpl[i].increment(v);
			sumLogImpl[i].increment(v);
			geoMeanImpl[i].increment(v);
			meanImpl[i].increment(v);
		}
		covarianceImpl.increment(value);
		(n)++;
	}

	public int getDimension() {
		return k;
	}

	public long getN() {
		return n;
	}

	private double[] getResults(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] stats) {
		double[] results = new double[stats.length];
		for (int i = 0 ; i < (results.length) ; ++i) {
			results[i] = stats[i].getResult();
		}
		return results;
	}

	public double[] getSum() {
		return getResults(sumImpl);
	}

	public double[] getSumSq() {
		return getResults(sumSqImpl);
	}

	public double[] getSumLog() {
		return getResults(sumLogImpl);
	}

	public double[] getMean() {
		return getResults(meanImpl);
	}

	public double[] getStandardDeviation() {
		double[] stdDev = new double[k];
		if ((getN()) < 1) {
			java.util.Arrays.fill(stdDev, java.lang.Double.NaN);
		} else {
			if ((getN()) < 2) {
				java.util.Arrays.fill(stdDev, 0.0);
			} else {
				org.apache.commons.math.linear.RealMatrix matrix = covarianceImpl.getResult();
				for (int i = 0 ; i < (k) ; ++i) {
					stdDev[i] = java.lang.Math.sqrt(matrix.getEntry(i, i));
				}
			}
		}
		return stdDev;
	}

	public org.apache.commons.math.linear.RealMatrix getCovariance() {
		return covarianceImpl.getResult();
	}

	public double[] getMax() {
		return getResults(maxImpl);
	}

	public double[] getMin() {
		return getResults(minImpl);
	}

	public double[] getGeometricMean() {
		return getResults(geoMeanImpl);
	}

	@java.lang.Override
	public java.lang.String toString() {
		java.lang.StringBuffer outBuffer = new java.lang.StringBuffer();
		outBuffer.append("MultivariateSummaryStatistics:\n");
		outBuffer.append((("n: " + (getN())) + "\n"));
		append(outBuffer, getMin(), "min: ", ", ", "\n");
		append(outBuffer, getMax(), "max: ", ", ", "\n");
		append(outBuffer, getMean(), "mean: ", ", ", "\n");
		append(outBuffer, getGeometricMean(), "geometric mean: ", ", ", "\n");
		append(outBuffer, getSumSq(), "sum of squares: ", ", ", "\n");
		append(outBuffer, getSumLog(), "sum of logarithms: ", ", ", "\n");
		append(outBuffer, getStandardDeviation(), "standard deviation: ", ", ", "\n");
		outBuffer.append((("covariance: " + (getCovariance().toString())) + "\n"));
		return outBuffer.toString();
	}

	private void append(java.lang.StringBuffer buffer, double[] data, java.lang.String prefix, java.lang.String separator, java.lang.String suffix) {
		buffer.append(prefix);
		for (int i = 0 ; i < (data.length) ; ++i) {
			if (i > 0) {
				buffer.append(separator);
			} 
			buffer.append(data[i]);
		}
		buffer.append(suffix);
	}

	public void clear() {
		this.n = 0;
		for (int i = 0 ; i < (k) ; ++i) {
			minImpl[i].clear();
			maxImpl[i].clear();
			sumImpl[i].clear();
			sumLogImpl[i].clear();
			sumSqImpl[i].clear();
			geoMeanImpl[i].clear();
			meanImpl[i].clear();
		}
		covarianceImpl.clear();
	}

	@java.lang.Override
	public boolean equals(java.lang.Object object) {
		if (object == (this)) {
			return true;
		} 
		if ((object instanceof org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics) == false) {
			return false;
		} 
		org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics stat = ((org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics)(object));
		return ((((((((org.apache.commons.math.util.MathUtils.equals(stat.getGeometricMean(), getGeometricMean())) && (org.apache.commons.math.util.MathUtils.equals(stat.getMax(), getMax()))) && (org.apache.commons.math.util.MathUtils.equals(stat.getMean(), getMean()))) && (org.apache.commons.math.util.MathUtils.equals(stat.getMin(), getMin()))) && (org.apache.commons.math.util.MathUtils.equals(stat.getN(), getN()))) && (org.apache.commons.math.util.MathUtils.equals(stat.getSum(), getSum()))) && (org.apache.commons.math.util.MathUtils.equals(stat.getSumSq(), getSumSq()))) && (org.apache.commons.math.util.MathUtils.equals(stat.getSumLog(), getSumLog()))) && (stat.getCovariance().equals(getCovariance()));
	}

	@java.lang.Override
	public int hashCode() {
		int result = 31 + (org.apache.commons.math.util.MathUtils.hash(getGeometricMean()));
		result = (result * 31) + (org.apache.commons.math.util.MathUtils.hash(getGeometricMean()));
		result = (result * 31) + (org.apache.commons.math.util.MathUtils.hash(getMax()));
		result = (result * 31) + (org.apache.commons.math.util.MathUtils.hash(getMean()));
		result = (result * 31) + (org.apache.commons.math.util.MathUtils.hash(getMin()));
		result = (result * 31) + (org.apache.commons.math.util.MathUtils.hash(getN()));
		result = (result * 31) + (org.apache.commons.math.util.MathUtils.hash(getSum()));
		result = (result * 31) + (org.apache.commons.math.util.MathUtils.hash(getSumSq()));
		result = (result * 31) + (org.apache.commons.math.util.MathUtils.hash(getSumLog()));
		result = (result * 31) + (getCovariance().hashCode());
		return result;
	}

	private void setImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] newImpl, org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] oldImpl) throws java.lang.IllegalStateException, org.apache.commons.math.DimensionMismatchException {
		checkEmpty();
		checkDimension(newImpl.length);
		java.lang.System.arraycopy(newImpl, 0, oldImpl, 0, newImpl.length);
	}

	public org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] getSumImpl() {
		return sumImpl.clone();
	}

	public void setSumImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] sumImpl) throws org.apache.commons.math.DimensionMismatchException {
		setImpl(sumImpl, this.sumImpl);
	}

	public org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] getSumsqImpl() {
		return sumSqImpl.clone();
	}

	public void setSumsqImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] sumsqImpl) throws org.apache.commons.math.DimensionMismatchException {
		setImpl(sumsqImpl, this.sumSqImpl);
	}

	public org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] getMinImpl() {
		return minImpl.clone();
	}

	public void setMinImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] minImpl) throws org.apache.commons.math.DimensionMismatchException {
		setImpl(minImpl, this.minImpl);
	}

	public org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] getMaxImpl() {
		return maxImpl.clone();
	}

	public void setMaxImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] maxImpl) throws org.apache.commons.math.DimensionMismatchException {
		setImpl(maxImpl, this.maxImpl);
	}

	public org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] getSumLogImpl() {
		return sumLogImpl.clone();
	}

	public void setSumLogImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] sumLogImpl) throws org.apache.commons.math.DimensionMismatchException {
		setImpl(sumLogImpl, this.sumLogImpl);
	}

	public org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] getGeoMeanImpl() {
		return geoMeanImpl.clone();
	}

	public void setGeoMeanImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] geoMeanImpl) throws org.apache.commons.math.DimensionMismatchException {
		setImpl(geoMeanImpl, this.geoMeanImpl);
	}

	public org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] getMeanImpl() {
		return meanImpl.clone();
	}

	public void setMeanImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] meanImpl) throws org.apache.commons.math.DimensionMismatchException {
		setImpl(meanImpl, this.meanImpl);
	}

	private void checkEmpty() {
		if ((n) > 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalStateException("{0} values have been added before statistic is configured", n);
		} 
	}

	private void checkDimension(int dimension) throws org.apache.commons.math.DimensionMismatchException {
		if (dimension != (k)) {
			throw new org.apache.commons.math.DimensionMismatchException(dimension , k);
		} 
	}
}

