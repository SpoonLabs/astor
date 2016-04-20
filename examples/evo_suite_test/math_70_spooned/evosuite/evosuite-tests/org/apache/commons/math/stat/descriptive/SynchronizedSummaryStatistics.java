package org.apache.commons.math.stat.descriptive;


public class SynchronizedSummaryStatistics extends org.apache.commons.math.stat.descriptive.SummaryStatistics {
	private static final long serialVersionUID = 1909861009042253704L;

	public SynchronizedSummaryStatistics() {
		super();
	}

	public SynchronizedSummaryStatistics(org.apache.commons.math.stat.descriptive.SynchronizedSummaryStatistics original) {
		org.apache.commons.math.stat.descriptive.SynchronizedSummaryStatistics.copy(original, this);
	}

	@java.lang.Override
	public synchronized org.apache.commons.math.stat.descriptive.StatisticalSummary getSummary() {
		return super.getSummary();
	}

	@java.lang.Override
	public synchronized void addValue(double value) {
		super.addValue(value);
	}

	@java.lang.Override
	public synchronized long getN() {
		return super.getN();
	}

	@java.lang.Override
	public synchronized double getSum() {
		return super.getSum();
	}

	@java.lang.Override
	public synchronized double getSumsq() {
		return super.getSumsq();
	}

	@java.lang.Override
	public synchronized double getMean() {
		return super.getMean();
	}

	@java.lang.Override
	public synchronized double getStandardDeviation() {
		return super.getStandardDeviation();
	}

	@java.lang.Override
	public synchronized double getVariance() {
		return super.getVariance();
	}

	@java.lang.Override
	public synchronized double getMax() {
		return super.getMax();
	}

	@java.lang.Override
	public synchronized double getMin() {
		return super.getMin();
	}

	@java.lang.Override
	public synchronized double getGeometricMean() {
		return super.getGeometricMean();
	}

	@java.lang.Override
	public synchronized java.lang.String toString() {
		return super.toString();
	}

	@java.lang.Override
	public synchronized void clear() {
		super.clear();
	}

	@java.lang.Override
	public synchronized boolean equals(java.lang.Object object) {
		return super.equals(object);
	}

	@java.lang.Override
	public synchronized int hashCode() {
		return super.hashCode();
	}

	@java.lang.Override
	public synchronized org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic getSumImpl() {
		return super.getSumImpl();
	}

	@java.lang.Override
	public synchronized void setSumImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic sumImpl) {
		super.setSumImpl(sumImpl);
	}

	@java.lang.Override
	public synchronized org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic getSumsqImpl() {
		return super.getSumsqImpl();
	}

	@java.lang.Override
	public synchronized void setSumsqImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic sumsqImpl) {
		super.setSumsqImpl(sumsqImpl);
	}

	@java.lang.Override
	public synchronized org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic getMinImpl() {
		return super.getMinImpl();
	}

	@java.lang.Override
	public synchronized void setMinImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic minImpl) {
		super.setMinImpl(minImpl);
	}

	@java.lang.Override
	public synchronized org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic getMaxImpl() {
		return super.getMaxImpl();
	}

	@java.lang.Override
	public synchronized void setMaxImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic maxImpl) {
		super.setMaxImpl(maxImpl);
	}

	@java.lang.Override
	public synchronized org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic getSumLogImpl() {
		return super.getSumLogImpl();
	}

	@java.lang.Override
	public synchronized void setSumLogImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic sumLogImpl) {
		super.setSumLogImpl(sumLogImpl);
	}

	@java.lang.Override
	public synchronized org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic getGeoMeanImpl() {
		return super.getGeoMeanImpl();
	}

	@java.lang.Override
	public synchronized void setGeoMeanImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic geoMeanImpl) {
		super.setGeoMeanImpl(geoMeanImpl);
	}

	@java.lang.Override
	public synchronized org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic getMeanImpl() {
		return super.getMeanImpl();
	}

	@java.lang.Override
	public synchronized void setMeanImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic meanImpl) {
		super.setMeanImpl(meanImpl);
	}

	@java.lang.Override
	public synchronized org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic getVarianceImpl() {
		return super.getVarianceImpl();
	}

	@java.lang.Override
	public synchronized void setVarianceImpl(org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic varianceImpl) {
		super.setVarianceImpl(varianceImpl);
	}

	@java.lang.Override
	public synchronized org.apache.commons.math.stat.descriptive.SynchronizedSummaryStatistics copy() {
		org.apache.commons.math.stat.descriptive.SynchronizedSummaryStatistics result = new org.apache.commons.math.stat.descriptive.SynchronizedSummaryStatistics();
		org.apache.commons.math.stat.descriptive.SynchronizedSummaryStatistics.copy(this, result);
		return result;
	}

	public static void copy(org.apache.commons.math.stat.descriptive.SynchronizedSummaryStatistics source, org.apache.commons.math.stat.descriptive.SynchronizedSummaryStatistics dest) {
		synchronized(source) {
			synchronized(dest) {
				org.apache.commons.math.stat.descriptive.SummaryStatistics.copy(source, dest);
			}
		}
	}
}

