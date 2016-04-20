package org.apache.commons.math.random;


public class UncorrelatedRandomVectorGeneratorTest extends junit.framework.TestCase {
	public UncorrelatedRandomVectorGeneratorTest(java.lang.String name) {
		super(name);
		mean = null;
		standardDeviation = null;
		generator = null;
	}

	public void testMeanAndCorrelation() throws org.apache.commons.math.DimensionMismatchException {
		org.apache.commons.math.stat.descriptive.moment.VectorialMean meanStat = new org.apache.commons.math.stat.descriptive.moment.VectorialMean(mean.length);
		org.apache.commons.math.stat.descriptive.moment.VectorialCovariance covStat = new org.apache.commons.math.stat.descriptive.moment.VectorialCovariance(mean.length , true);
		for (int i = 0 ; i < 10000 ; ++i) {
			double[] v = generator.nextVector();
			meanStat.increment(v);
			covStat.increment(v);
		}
		double[] estimatedMean = meanStat.getResult();
		double scale;
		org.apache.commons.math.linear.RealMatrix estimatedCorrelation = covStat.getResult();
		for (int i = 0 ; i < (estimatedMean.length) ; ++i) {
			junit.framework.Assert.assertEquals(mean[i], estimatedMean[i], 0.07);
			for (int j = 0 ; j < i ; ++j) {
				scale = (standardDeviation[i]) * (standardDeviation[j]);
				junit.framework.Assert.assertEquals(0, ((estimatedCorrelation.getEntry(i, j)) / scale), 0.03);
			}
			scale = (standardDeviation[i]) * (standardDeviation[i]);
			junit.framework.Assert.assertEquals(1, ((estimatedCorrelation.getEntry(i, i)) / scale), 0.025);
		}
	}

	@java.lang.Override
	public void setUp() {
		mean = new double[]{ 0.0 , 1.0 , -3.0 , 2.3 };
		standardDeviation = new double[]{ 1.0 , 2.0 , 10.0 , 0.1 };
		org.apache.commons.math.random.RandomGenerator rg = new org.apache.commons.math.random.JDKRandomGenerator();
		rg.setSeed(17399225432L);
		generator = new org.apache.commons.math.random.UncorrelatedRandomVectorGenerator(mean , standardDeviation , new org.apache.commons.math.random.GaussianRandomGenerator(rg));
	}

	@java.lang.Override
	public void tearDown() {
		mean = null;
		standardDeviation = null;
		generator = null;
	}

	private double[] mean;

	private double[] standardDeviation;

	private org.apache.commons.math.random.UncorrelatedRandomVectorGenerator generator;
}

