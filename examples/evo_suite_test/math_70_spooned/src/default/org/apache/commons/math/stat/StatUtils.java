package org.apache.commons.math.stat;


public final class StatUtils {
	private static final org.apache.commons.math.stat.descriptive.UnivariateStatistic SUM = new org.apache.commons.math.stat.descriptive.summary.Sum();

	private static final org.apache.commons.math.stat.descriptive.UnivariateStatistic SUM_OF_SQUARES = new org.apache.commons.math.stat.descriptive.summary.SumOfSquares();

	private static final org.apache.commons.math.stat.descriptive.UnivariateStatistic PRODUCT = new org.apache.commons.math.stat.descriptive.summary.Product();

	private static final org.apache.commons.math.stat.descriptive.UnivariateStatistic SUM_OF_LOGS = new org.apache.commons.math.stat.descriptive.summary.SumOfLogs();

	private static final org.apache.commons.math.stat.descriptive.UnivariateStatistic MIN = new org.apache.commons.math.stat.descriptive.rank.Min();

	private static final org.apache.commons.math.stat.descriptive.UnivariateStatistic MAX = new org.apache.commons.math.stat.descriptive.rank.Max();

	private static final org.apache.commons.math.stat.descriptive.UnivariateStatistic MEAN = new org.apache.commons.math.stat.descriptive.moment.Mean();

	private static final org.apache.commons.math.stat.descriptive.moment.Variance VARIANCE = new org.apache.commons.math.stat.descriptive.moment.Variance();

	private static final org.apache.commons.math.stat.descriptive.rank.Percentile PERCENTILE = new org.apache.commons.math.stat.descriptive.rank.Percentile();

	private static final org.apache.commons.math.stat.descriptive.moment.GeometricMean GEOMETRIC_MEAN = new org.apache.commons.math.stat.descriptive.moment.GeometricMean();

	private StatUtils() {
	}

	public static double sum(final double[] values) {
		return SUM.evaluate(values);
	}

	public static double sum(final double[] values, final int begin, final int length) {
		return SUM.evaluate(values, begin, length);
	}

	public static double sumSq(final double[] values) {
		return SUM_OF_SQUARES.evaluate(values);
	}

	public static double sumSq(final double[] values, final int begin, final int length) {
		return SUM_OF_SQUARES.evaluate(values, begin, length);
	}

	public static double product(final double[] values) {
		return PRODUCT.evaluate(values);
	}

	public static double product(final double[] values, final int begin, final int length) {
		return PRODUCT.evaluate(values, begin, length);
	}

	public static double sumLog(final double[] values) {
		return SUM_OF_LOGS.evaluate(values);
	}

	public static double sumLog(final double[] values, final int begin, final int length) {
		return SUM_OF_LOGS.evaluate(values, begin, length);
	}

	public static double mean(final double[] values) {
		return MEAN.evaluate(values);
	}

	public static double mean(final double[] values, final int begin, final int length) {
		return MEAN.evaluate(values, begin, length);
	}

	public static double geometricMean(final double[] values) {
		return GEOMETRIC_MEAN.evaluate(values);
	}

	public static double geometricMean(final double[] values, final int begin, final int length) {
		return GEOMETRIC_MEAN.evaluate(values, begin, length);
	}

	public static double variance(final double[] values) {
		return VARIANCE.evaluate(values);
	}

	public static double variance(final double[] values, final int begin, final int length) {
		return VARIANCE.evaluate(values, begin, length);
	}

	public static double variance(final double[] values, final double mean, final int begin, final int length) {
		return VARIANCE.evaluate(values, mean, begin, length);
	}

	public static double variance(final double[] values, final double mean) {
		return VARIANCE.evaluate(values, mean);
	}

	public static double max(final double[] values) {
		return MAX.evaluate(values);
	}

	public static double max(final double[] values, final int begin, final int length) {
		return MAX.evaluate(values, begin, length);
	}

	public static double min(final double[] values) {
		return MIN.evaluate(values);
	}

	public static double min(final double[] values, final int begin, final int length) {
		return MIN.evaluate(values, begin, length);
	}

	public static double percentile(final double[] values, final double p) {
		return PERCENTILE.evaluate(values, p);
	}

	public static double percentile(final double[] values, final int begin, final int length, final double p) {
		return PERCENTILE.evaluate(values, begin, length, p);
	}

	public static double sumDifference(final double[] sample1, final double[] sample2) throws java.lang.IllegalArgumentException {
		int n = sample1.length;
		if ((n != (sample2.length)) || (n < 1)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("input arrays must have the same positive length ({0} and {1})", n, sample2.length);
		} 
		double result = 0;
		for (int i = 0 ; i < n ; i++) {
			result += (sample1[i]) - (sample2[i]);
		}
		return result;
	}

	public static double meanDifference(final double[] sample1, final double[] sample2) throws java.lang.IllegalArgumentException {
		return (org.apache.commons.math.stat.StatUtils.sumDifference(sample1, sample2)) / (sample1.length);
	}

	public static double varianceDifference(final double[] sample1, final double[] sample2, double meanDifference) throws java.lang.IllegalArgumentException {
		double sum1 = 0.0;
		double sum2 = 0.0;
		double diff = 0.0;
		int n = sample1.length;
		if ((n < 2) || (n != (sample2.length))) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("input arrays must have the same length and at least two elements ({0} and {1})", n, sample2.length);
		} 
		for (int i = 0 ; i < n ; i++) {
			diff = (sample1[i]) - (sample2[i]);
			sum1 += (diff - meanDifference) * (diff - meanDifference);
			sum2 += diff - meanDifference;
		}
		return (sum1 - ((sum2 * sum2) / n)) / (n - 1);
	}
}

