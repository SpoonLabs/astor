package org.apache.commons.math.stat.descriptive;


public class SummaryStatistics implements java.io.Serializable , org.apache.commons.math.stat.descriptive.StatisticalSummary {
	private static final long serialVersionUID = -2021321786743555871L;

	protected long n = 0;

	protected org.apache.commons.math.stat.descriptive.moment.SecondMoment secondMoment = new org.apache.commons.math.stat.descriptive.moment.SecondMoment();

	protected org.apache.commons.math.stat.descriptive.summary.Sum sum = new org.apache.commons.math.stat.descriptive.summary.Sum();

	protected org.apache.commons.math.stat.descriptive.summary.SumOfSquares sumsq = new org.apache.commons.math.stat.descriptive.summary.SumOfSquares();

	protected org.apache.commons.math.stat.descriptive.rank.Min min = new org.apache.commons.math.stat.descriptive.rank.Min();

	protected org.apache.commons.math.stat.descriptive.rank.Max max = new org.apache.commons.math.stat.descriptive.rank.Max();

	protected org.apache.commons.math.stat.descriptive.summary.SumOfLogs sumLog = new org.apache.commons.math.stat.descriptive.summary.SumOfLogs();

	protected org.apache.commons.math.stat.descriptive.moment.GeometricMean geoMean = new org.apache.commons.math.stat.descriptive.moment.GeometricMean(sumLog);

	protected org.apache.commons.math.stat.descriptive.moment.Mean mean = new org.apache.commons.math.stat.descriptive.moment.Mean();

	protected org.apache.commons.math.stat.descriptive.moment.Variance variance = new org.apache.commons.math.stat.descriptive.moment.Variance();

	private org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic sumImpl = sum;

	private org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic sumsqImpl = sumsq;

	private org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic minImpl = min;

	private org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic maxImpl = max;

	private org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic sumLogImpl = sumLog;

	private org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic geoMeanImpl = geoMean;

	private org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic meanImpl = mean;

	private org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic varianceImpl = variance;

	public SummaryStatistics() {
	}

	public SummaryStatistics(org.apache.commons.math.stat.descriptive.SummaryStatistics original) {
		org.apache.commons.math.stat.descriptive.SummaryStatistics.copy(original, this);
	}

	public org.apache.commons.math.stat.descriptive.StatisticalSummary getSummary() {
		return new org.apache.commons.math.stat.descriptive.StatisticalSummaryValues(getMean() , getVariance() , getN() , getMax() , getMin() , getSum());
	}

	public void addValue(double value) {
		sumImpl.increment(value);
		sumsqImpl.increment(value);
		minImpl.increment(value);
		maxImpl.increment(value);
		sumLogImpl.increment(value);
		secondMoment.increment(value);
		if (!((meanImpl) instanceof org.apache.commons.math.stat.descriptive.moment.Mean)) {
			meanImpl.increment(value);
		} 
		if (!((varianceImpl) instanceof org.apache.commons.math.stat.descriptive.moment.Variance)) {
			varianceImpl.increment(value);
		} 
		if (!((geoMeanImpl) instanceof org.apache.commons.math.stat.descriptive.moment.GeometricMean)) {
			geoMeanImpl.increment(value);
		} 
		(n)++;
	}

	public long getN() {
		return n;
	}

	public double getSum() {
		return sumImpl.getResult();
	}

	public double getSumsq() {
		return sumsqImpl.getResult();
	}

	public double getMean() {
		if ((mean) == (meanImpl)) {
			return new org.apache.commons.math.stat.descriptive.moment.Mean(secondMoment).getResult();
		} else {
			return meanImpl.getResult();
		}
	}

	public double getStandardDeviation() {
		double stdDev = java.lang.Double.NaN;
		if ((getN()) > 0) {
			if ((getN()) > 1) {
				stdDev = java.lang.Math.sqrt(getVariance());
			} else {
				stdDev = 0.0;
			}
		} 
		return stdDev;
	}

	public double getVariance() {
		if ((varianceImpl) == (variance)) {
			return new org.apache.commons.math.stat.descriptive.moment.Variance(secondMoment).getResult();
		} else {
			return varianceImpl.getResult();
		}
	}

	public double getMax() {
		return maxImpl.getResult();
	}

	public double getMin() {
		return minImpl.getResult();
	}

	public double getGeometricMean() {
		return geoMeanImpl.getResult();
	}

	public double getSumOfLogs() {
		return sumLogImpl.getResult();
	}

	public double getSecondMoment() {
		return secondMoment.getResult();
	}

	@java.lang.Override
	public java.lang.String toString() {
		java.lang.StringBuffer outBuffer = new java.lang.StringBuffer();
		java.lang.String endl = "\n";
		outBuffer.append("SummaryStatistics:").append(endl);
		outBuffer.append("n: ").append(getN()).append(endl);
		outBuffer.append("min: ").append(getMin()).append(endl);
		outBuffer.append("max: ").append(getMax()).append(endl);
		outBuffer.append("mean: ").append(getMean()).append(endl);
		outBuffer.append("geometric mean: ").append(getGeometricMean()).append(endl);
		outBuffer.append("variance: ").append(getVariance()).append(endl);
		outBuffer.append("sum of squares: ").append(getSumsq()).append(endl);
		outBuffer.append("standard deviation: ").append(getStandardDeviation()).append(endl);
		outBuffer.append("sum of logs: ").append(getSumOfLogs()).append(endl);
		return outBuffer.toString();
	}

	public void clear() {
		this.n = 0;
		minImpl.clear();
		maxImpl.clear();
		sumImpl.clear();
		sumLogImpl.clear();
		sumsqImpl.clear();
		geoMeanImpl.clear();
		secondMoment.clear();
		if ((meanImpl) != (mean)) {
			meanImpl.clear();
		} 
		if ((varianceImpl) != (variance)) {
			varianceImpl.clear();
		} 
	}

	@java.lang.Override
	public boolean equals(java.lang.Object object) {
		if (object == (this)) {
			return true;
		} 
		if ((object instanceof org.apache.commons.math.stat.descriptive.SummaryStatistics) == false) {
			return false;
		} 
		org.apache.commons.math.stat.descriptive.SummaryStatistics stat = ((org.apache.commons.math.stat.descriptive.SummaryStatistics)(object));
		return (((((((org.apache.commons.math.util.MathUtils.equals(stat.getGeometricMean(), getGeometricMean())) && (org.apache.commons.math.util.MathUtils.equals(stat.getMax(), getMax()))) && (org.apache.commons.math.util.MathUtils.equals(stat.getMean(), getMean()))) && (org.apache.commons.math.util.MathUtils.equals(stat.getMin(), getMin()))) && (org.apache.commons.math.util.MathUtils.equals(stat.getN(), getN()))) && (org.apache.commons.math.util.MathUtils.equals(stat.getSum(), getSum()))) && (org.apache.commons.math.util.MathUtils.equals(stat.getSumsq(), getSumsq()))) && (org.apache.commons.math.util.MathUtils.equals(stat.getVariance(), getVariance()));
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
		result = (result * 31) + (org.apache.commons.math.util.MathUtils.hash(getSumsq()));
		result = (result * 31) + (org.apache.commons.math.util.MathUtils.hash(getVariance()));
		return result;
	}

	public org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic getSumImpl() {
		return sumImpl;
	}

	public void setSumImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic sumImpl) {
		checkEmpty();
		this.sumImpl = sumImpl;
	}

	public org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic getSumsqImpl() {
		return sumsqImpl;
	}

	public void setSumsqImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic sumsqImpl) {
		checkEmpty();
		this.sumsqImpl = sumsqImpl;
	}

	public org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic getMinImpl() {
		return minImpl;
	}

	public void setMinImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic minImpl) {
		checkEmpty();
		this.minImpl = minImpl;
	}

	public org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic getMaxImpl() {
		return maxImpl;
	}

	public void setMaxImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic maxImpl) {
		checkEmpty();
		this.maxImpl = maxImpl;
	}

	public org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic getSumLogImpl() {
		return sumLogImpl;
	}

	public void setSumLogImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic sumLogImpl) {
		checkEmpty();
		this.sumLogImpl = sumLogImpl;
		geoMean.setSumLogImpl(sumLogImpl);
	}

	public org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic getGeoMeanImpl() {
		return geoMeanImpl;
	}

	public void setGeoMeanImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic geoMeanImpl) {
		checkEmpty();
		this.geoMeanImpl = geoMeanImpl;
	}

	public org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic getMeanImpl() {
		return meanImpl;
	}

	public void setMeanImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic meanImpl) {
		checkEmpty();
		this.meanImpl = meanImpl;
	}

	public org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic getVarianceImpl() {
		return varianceImpl;
	}

	public void setVarianceImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic varianceImpl) {
		checkEmpty();
		this.varianceImpl = varianceImpl;
	}

	private void checkEmpty() {
		if ((n) > 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalStateException("{0} values have been added before statistic is configured", n);
		} 
	}

	public org.apache.commons.math.stat.descriptive.SummaryStatistics copy() {
		org.apache.commons.math.stat.descriptive.SummaryStatistics result = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		org.apache.commons.math.stat.descriptive.SummaryStatistics.copy(this, result);
		return result;
	}

	public static void copy(org.apache.commons.math.stat.descriptive.SummaryStatistics source, org.apache.commons.math.stat.descriptive.SummaryStatistics dest) {
		dest.maxImpl = source.maxImpl.copy();
		dest.meanImpl = source.meanImpl.copy();
		dest.minImpl = source.minImpl.copy();
		dest.sumImpl = source.sumImpl.copy();
		dest.varianceImpl = source.varianceImpl.copy();
		dest.sumLogImpl = source.sumLogImpl.copy();
		dest.sumsqImpl = source.sumsqImpl.copy();
		if ((source.getGeoMeanImpl()) instanceof org.apache.commons.math.stat.descriptive.moment.GeometricMean) {
			dest.geoMeanImpl = new org.apache.commons.math.stat.descriptive.moment.GeometricMean(((org.apache.commons.math.stat.descriptive.summary.SumOfLogs)(dest.sumLogImpl)));
		} else {
			dest.geoMeanImpl = source.geoMeanImpl.copy();
		}
		org.apache.commons.math.stat.descriptive.moment.SecondMoment.copy(source.secondMoment, dest.secondMoment);
		dest.n = source.n;
		if ((source.geoMean) == (source.geoMeanImpl)) {
			dest.geoMean = ((org.apache.commons.math.stat.descriptive.moment.GeometricMean)(dest.geoMeanImpl));
		} else {
			org.apache.commons.math.stat.descriptive.moment.GeometricMean.copy(source.geoMean, dest.geoMean);
		}
		if ((source.max) == (source.maxImpl)) {
			dest.max = ((org.apache.commons.math.stat.descriptive.rank.Max)(dest.maxImpl));
		} else {
			org.apache.commons.math.stat.descriptive.rank.Max.copy(source.max, dest.max);
		}
		if ((source.mean) == (source.meanImpl)) {
			dest.mean = ((org.apache.commons.math.stat.descriptive.moment.Mean)(dest.meanImpl));
		} else {
			org.apache.commons.math.stat.descriptive.moment.Mean.copy(source.mean, dest.mean);
		}
		if ((source.min) == (source.minImpl)) {
			dest.min = ((org.apache.commons.math.stat.descriptive.rank.Min)(dest.minImpl));
		} else {
			org.apache.commons.math.stat.descriptive.rank.Min.copy(source.min, dest.min);
		}
		if ((source.sum) == (source.sumImpl)) {
			dest.sum = ((org.apache.commons.math.stat.descriptive.summary.Sum)(dest.sumImpl));
		} else {
			org.apache.commons.math.stat.descriptive.summary.Sum.copy(source.sum, dest.sum);
		}
		if ((source.variance) == (source.varianceImpl)) {
			dest.variance = ((org.apache.commons.math.stat.descriptive.moment.Variance)(dest.varianceImpl));
		} else {
			org.apache.commons.math.stat.descriptive.moment.Variance.copy(source.variance, dest.variance);
		}
		if ((source.sumLog) == (source.sumLogImpl)) {
			dest.sumLog = ((org.apache.commons.math.stat.descriptive.summary.SumOfLogs)(dest.sumLogImpl));
		} else {
			org.apache.commons.math.stat.descriptive.summary.SumOfLogs.copy(source.sumLog, dest.sumLog);
		}
		if ((source.sumsq) == (source.sumsqImpl)) {
			dest.sumsq = ((org.apache.commons.math.stat.descriptive.summary.SumOfSquares)(dest.sumsqImpl));
		} else {
			org.apache.commons.math.stat.descriptive.summary.SumOfSquares.copy(source.sumsq, dest.sumsq);
		}
	}
}

