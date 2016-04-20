package org.apache.commons.math.stat.inference;


public interface TTest {
	double pairedT(double[] sample1, double[] sample2) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException;

	double pairedTTest(double[] sample1, double[] sample2) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException;

	boolean pairedTTest(double[] sample1, double[] sample2, double alpha) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException;

	double t(double mu, double[] observed) throws java.lang.IllegalArgumentException;

	double t(double mu, org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats) throws java.lang.IllegalArgumentException;

	double homoscedasticT(double[] sample1, double[] sample2) throws java.lang.IllegalArgumentException;

	double t(double[] sample1, double[] sample2) throws java.lang.IllegalArgumentException;

	double t(org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats1, org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats2) throws java.lang.IllegalArgumentException;

	double homoscedasticT(org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats1, org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats2) throws java.lang.IllegalArgumentException;

	double tTest(double mu, double[] sample) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException;

	boolean tTest(double mu, double[] sample, double alpha) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException;

	double tTest(double mu, org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException;

	boolean tTest(double mu, org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats, double alpha) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException;

	double tTest(double[] sample1, double[] sample2) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException;

	double homoscedasticTTest(double[] sample1, double[] sample2) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException;

	boolean tTest(double[] sample1, double[] sample2, double alpha) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException;

	boolean homoscedasticTTest(double[] sample1, double[] sample2, double alpha) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException;

	double tTest(org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats1, org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats2) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException;

	double homoscedasticTTest(org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats1, org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats2) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException;

	boolean tTest(org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats1, org.apache.commons.math.stat.descriptive.StatisticalSummary sampleStats2, double alpha) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException;
}

