package org.apache.commons.math.stat.descriptive;


public interface StatisticalMultivariateSummary {
	int getDimension();

	double[] getMean();

	org.apache.commons.math.linear.RealMatrix getCovariance();

	double[] getStandardDeviation();

	double[] getMax();

	double[] getMin();

	long getN();

	double[] getGeometricMean();

	double[] getSum();

	double[] getSumSq();

	double[] getSumLog();
}

