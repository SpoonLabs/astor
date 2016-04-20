package org.apache.commons.math.stat.inference;


public class TTestImpl implements org.apache.commons.math.stat.inference.TTest {
	private static final java.lang.String INSUFFICIENT_DATA_MESSAGE = "insufficient data for t statistic, needs at least 2, got {0}";

	private org.apache.commons.math.distribution.TDistribution distribution;

	public TTestImpl() {
		this(new org.apache.commons.math.distribution.TDistributionImpl(1.0));
	}

	public TTestImpl(org.apache.commons.math.distribution.TDistribution t) {
		super();
		setDistribution(t);
	}

	public double pairedT(double[] sample1, double[] sample2) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		checkSampleData(sample1);
		checkSampleData(sample2);
		double meanDifference = org.apache.commons.math.stat.StatUtils.meanDifference(sample1, sample2);
		return t(meanDifference, 0, org.apache.commons.math.stat.StatUtils.varianceDifference(sample1, sample2, meanDifference), sample1.length);
	}

	public double pairedTTest(double[] sample1, double[] sample2) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		double meanDifference = org.apache.commons.math.stat.StatUtils.meanDifference(sample1, sample2);
		return tTest(meanDifference, 0, org.apache.commons.math.stat.StatUtils.varianceDifference(sample1, sample2, meanDifference), sample1.length);
	}

	public boolean pairedTTest(double[] sample1, double[] sample2, double alpha) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		checkSignificanceLevel(alpha);
		return (pairedTTest(sample1, sample2)) < alpha;
	}

	public double t(double mu, double[] observed) throws java.lang.IllegalArgumentException {
		checkSampleData(observed);
		return t(org.apache.commons.math.stat.StatUtils.mean(observed), mu, org.apache.commons.math.stat.StatUtils.variance(observed), observed.length);
	}

	public double t(double mu, org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats) throws java.lang.IllegalArgumentException {
		checkSampleData(sampleStats);
		return t(sampleStats.getMean(), mu, sampleStats.getVariance(), sampleStats.getN());
	}

	public double homoscedasticT(double[] sample1, double[] sample2) throws java.lang.IllegalArgumentException {
		checkSampleData(sample1);
		checkSampleData(sample2);
		return homoscedasticT(org.apache.commons.math.stat.StatUtils.mean(sample1), org.apache.commons.math.stat.StatUtils.mean(sample2), org.apache.commons.math.stat.StatUtils.variance(sample1), org.apache.commons.math.stat.StatUtils.variance(sample2), sample1.length, sample2.length);
	}

	public double t(double[] sample1, double[] sample2) throws java.lang.IllegalArgumentException {
		checkSampleData(sample1);
		checkSampleData(sample2);
		return t(org.apache.commons.math.stat.StatUtils.mean(sample1), org.apache.commons.math.stat.StatUtils.mean(sample2), org.apache.commons.math.stat.StatUtils.variance(sample1), org.apache.commons.math.stat.StatUtils.variance(sample2), sample1.length, sample2.length);
	}

	public double t(org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats1, org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats2) throws java.lang.IllegalArgumentException {
		checkSampleData(sampleStats1);
		checkSampleData(sampleStats2);
		return t(sampleStats1.getMean(), sampleStats2.getMean(), sampleStats1.getVariance(), sampleStats2.getVariance(), sampleStats1.getN(), sampleStats2.getN());
	}

	public double homoscedasticT(org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats1, org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats2) throws java.lang.IllegalArgumentException {
		checkSampleData(sampleStats1);
		checkSampleData(sampleStats2);
		return homoscedasticT(sampleStats1.getMean(), sampleStats2.getMean(), sampleStats1.getVariance(), sampleStats2.getVariance(), sampleStats1.getN(), sampleStats2.getN());
	}

	public double tTest(double mu, double[] sample) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		checkSampleData(sample);
		return tTest(org.apache.commons.math.stat.StatUtils.mean(sample), mu, org.apache.commons.math.stat.StatUtils.variance(sample), sample.length);
	}

	public boolean tTest(double mu, double[] sample, double alpha) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		checkSignificanceLevel(alpha);
		return (tTest(mu, sample)) < alpha;
	}

	public double tTest(double mu, org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		checkSampleData(sampleStats);
		return tTest(sampleStats.getMean(), mu, sampleStats.getVariance(), sampleStats.getN());
	}

	public boolean tTest(double mu, org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats, double alpha) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		checkSignificanceLevel(alpha);
		return (tTest(mu, sampleStats)) < alpha;
	}

	public double tTest(double[] sample1, double[] sample2) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		checkSampleData(sample1);
		checkSampleData(sample2);
		return tTest(org.apache.commons.math.stat.StatUtils.mean(sample1), org.apache.commons.math.stat.StatUtils.mean(sample2), org.apache.commons.math.stat.StatUtils.variance(sample1), org.apache.commons.math.stat.StatUtils.variance(sample2), sample1.length, sample2.length);
	}

	public double homoscedasticTTest(double[] sample1, double[] sample2) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		checkSampleData(sample1);
		checkSampleData(sample2);
		return homoscedasticTTest(org.apache.commons.math.stat.StatUtils.mean(sample1), org.apache.commons.math.stat.StatUtils.mean(sample2), org.apache.commons.math.stat.StatUtils.variance(sample1), org.apache.commons.math.stat.StatUtils.variance(sample2), sample1.length, sample2.length);
	}

	public boolean tTest(double[] sample1, double[] sample2, double alpha) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		checkSignificanceLevel(alpha);
		return (tTest(sample1, sample2)) < alpha;
	}

	public boolean homoscedasticTTest(double[] sample1, double[] sample2, double alpha) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		checkSignificanceLevel(alpha);
		return (homoscedasticTTest(sample1, sample2)) < alpha;
	}

	public double tTest(org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats1, org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats2) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		checkSampleData(sampleStats1);
		checkSampleData(sampleStats2);
		return tTest(sampleStats1.getMean(), sampleStats2.getMean(), sampleStats1.getVariance(), sampleStats2.getVariance(), sampleStats1.getN(), sampleStats2.getN());
	}

	public double homoscedasticTTest(org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats1, org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats2) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		checkSampleData(sampleStats1);
		checkSampleData(sampleStats2);
		return homoscedasticTTest(sampleStats1.getMean(), sampleStats2.getMean(), sampleStats1.getVariance(), sampleStats2.getVariance(), sampleStats1.getN(), sampleStats2.getN());
	}

	public boolean tTest(org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats1, org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats2, double alpha) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		checkSignificanceLevel(alpha);
		return (tTest(sampleStats1, sampleStats2)) < alpha;
	}

	protected double df(double v1, double v2, double n1, double n2) {
		return (((v1 / n1) + (v2 / n2)) * ((v1 / n1) + (v2 / n2))) / (((v1 * v1) / ((n1 * n1) * (n1 - 1.0))) + ((v2 * v2) / ((n2 * n2) * (n2 - 1.0))));
	}

	protected double t(double m, double mu, double v, double n) {
		return (m - mu) / (java.lang.Math.sqrt((v / n)));
	}

	protected double t(double m1, double m2, double v1, double v2, double n1, double n2) {
		return (m1 - m2) / (java.lang.Math.sqrt(((v1 / n1) + (v2 / n2))));
	}

	protected double homoscedasticT(double m1, double m2, double v1, double v2, double n1, double n2) {
		double pooledVariance = (((n1 - 1) * v1) + ((n2 - 1) * v2)) / ((n1 + n2) - 2);
		return (m1 - m2) / (java.lang.Math.sqrt((pooledVariance * ((1.0 / n1) + (1.0 / n2)))));
	}

	protected double tTest(double m, double mu, double v, double n) throws org.apache.commons.math.MathException {
		double t = java.lang.Math.abs(t(m, mu, v, n));
		distribution.setDegreesOfFreedom((n - 1));
		return 2.0 * (distribution.cumulativeProbability(-t));
	}

	protected double tTest(double m1, double m2, double v1, double v2, double n1, double n2) throws org.apache.commons.math.MathException {
		double t = java.lang.Math.abs(t(m1, m2, v1, v2, n1, n2));
		double degreesOfFreedom = 0;
		degreesOfFreedom = df(v1, v2, n1, n2);
		distribution.setDegreesOfFreedom(degreesOfFreedom);
		return 2.0 * (distribution.cumulativeProbability(-t));
	}

	protected double homoscedasticTTest(double m1, double m2, double v1, double v2, double n1, double n2) throws org.apache.commons.math.MathException {
		double t = java.lang.Math.abs(homoscedasticT(m1, m2, v1, v2, n1, n2));
		double degreesOfFreedom = (n1 + n2) - 2;
		distribution.setDegreesOfFreedom(degreesOfFreedom);
		return 2.0 * (distribution.cumulativeProbability(-t));
	}

	public void setDistribution(org.apache.commons.math.distribution.TDistribution value) {
		distribution = value;
	}

	private void checkSignificanceLevel(final double alpha) throws java.lang.IllegalArgumentException {
		if ((alpha <= 0) || (alpha > 0.5)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("out of bounds significance level {0}, must be between {1} and {2}", alpha, 0.0, 0.5);
		} 
	}

	private void checkSampleData(final double[] data) throws java.lang.IllegalArgumentException {
		if ((data == null) || ((data.length) < 2)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(INSUFFICIENT_DATA_MESSAGE, (data == null ? 0 : data.length));
		} 
	}

	private void checkSampleData(final org.apache.commons.math.stat.descriptive.StatisticalSummary stat) throws java.lang.IllegalArgumentException {
		if ((stat == null) || ((stat.getN()) < 2)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(INSUFFICIENT_DATA_MESSAGE, (stat == null ? 0 : stat.getN()));
		} 
	}
}

