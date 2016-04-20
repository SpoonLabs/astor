package org.apache.commons.math.random;


public class CorrelatedRandomVectorGeneratorTest extends junit.framework.TestCase {
	public CorrelatedRandomVectorGeneratorTest(java.lang.String name) {
		super(name);
		mean = null;
		covariance = null;
		generator = null;
	}

	public void testRank() {
		junit.framework.Assert.assertEquals(3, generator.getRank());
	}

	public void testMath226() throws org.apache.commons.math.DimensionMismatchException, org.apache.commons.math.linear.NotPositiveDefiniteMatrixException {
		double[] mean = new double[]{ 1 , 1 , 10 , 1 };
		double[][] cov = new double[][]{ new double[]{ 1 , 3 , 2 , 6 } , new double[]{ 3 , 13 , 16 , 2 } , new double[]{ 2 , 16 , 38 , -1 } , new double[]{ 6 , 2 , -1 , 197 } };
		org.apache.commons.math.linear.RealMatrix covRM = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(cov);
		org.apache.commons.math.random.JDKRandomGenerator jg = new org.apache.commons.math.random.JDKRandomGenerator();
		jg.setSeed(5322145245211L);
		org.apache.commons.math.random.NormalizedRandomGenerator rg = new org.apache.commons.math.random.GaussianRandomGenerator(jg);
		org.apache.commons.math.random.CorrelatedRandomVectorGenerator sg = new org.apache.commons.math.random.CorrelatedRandomVectorGenerator(mean , covRM , 1.0E-5 , rg);
		for (int i = 0 ; i < 10 ; i++) {
			double[] generated = sg.nextVector();
			junit.framework.Assert.assertTrue(((java.lang.Math.abs(((generated[0]) - 1))) > 0.1));
		}
	}

	public void testRootMatrix() {
		org.apache.commons.math.linear.RealMatrix b = generator.getRootMatrix();
		org.apache.commons.math.linear.RealMatrix bbt = b.multiply(b.transpose());
		for (int i = 0 ; i < (covariance.getRowDimension()) ; ++i) {
			for (int j = 0 ; j < (covariance.getColumnDimension()) ; ++j) {
				junit.framework.Assert.assertEquals(covariance.getEntry(i, j), bbt.getEntry(i, j), 1.0E-12);
			}
		}
	}

	public void testMeanAndCovariance() throws org.apache.commons.math.DimensionMismatchException {
		org.apache.commons.math.stat.descriptive.moment.VectorialMean meanStat = new org.apache.commons.math.stat.descriptive.moment.VectorialMean(mean.length);
		org.apache.commons.math.stat.descriptive.moment.VectorialCovariance covStat = new org.apache.commons.math.stat.descriptive.moment.VectorialCovariance(mean.length , true);
		for (int i = 0 ; i < 5000 ; ++i) {
			double[] v = generator.nextVector();
			meanStat.increment(v);
			covStat.increment(v);
		}
		double[] estimatedMean = meanStat.getResult();
		org.apache.commons.math.linear.RealMatrix estimatedCovariance = covStat.getResult();
		for (int i = 0 ; i < (estimatedMean.length) ; ++i) {
			junit.framework.Assert.assertEquals(mean[i], estimatedMean[i], 0.07);
			for (int j = 0 ; j <= i ; ++j) {
				junit.framework.Assert.assertEquals(covariance.getEntry(i, j), estimatedCovariance.getEntry(i, j), ((0.1 * (1.0 + (java.lang.Math.abs(mean[i])))) * (1.0 + (java.lang.Math.abs(mean[j])))));
			}
		}
	}

	@java.lang.Override
	public void setUp() {
		try {
			mean = new double[]{ 0.0 , 1.0 , -3.0 , 2.3 };
			org.apache.commons.math.linear.RealMatrix b = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(4, 3);
			int counter = 0;
			for (int i = 0 ; i < (b.getRowDimension()) ; ++i) {
				for (int j = 0 ; j < (b.getColumnDimension()) ; ++j) {
					b.setEntry(i, j, (1.0 + (0.1 * (++counter))));
				}
			}
			org.apache.commons.math.linear.RealMatrix bbt = b.multiply(b.transpose());
			covariance = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(mean.length, mean.length);
			for (int i = 0 ; i < (covariance.getRowDimension()) ; ++i) {
				covariance.setEntry(i, i, bbt.getEntry(i, i));
				for (int j = 0 ; j < (covariance.getColumnDimension()) ; ++j) {
					double s = bbt.getEntry(i, j);
					covariance.setEntry(i, j, s);
					covariance.setEntry(j, i, s);
				}
			}
			org.apache.commons.math.random.RandomGenerator rg = new org.apache.commons.math.random.JDKRandomGenerator();
			rg.setSeed(17399225432L);
			org.apache.commons.math.random.GaussianRandomGenerator rawGenerator = new org.apache.commons.math.random.GaussianRandomGenerator(rg);
			generator = new org.apache.commons.math.random.CorrelatedRandomVectorGenerator(mean , covariance , (1.0E-12 * (covariance.getNorm())) , rawGenerator);
		} catch (org.apache.commons.math.DimensionMismatchException e) {
			junit.framework.Assert.fail(e.getMessage());
		} catch (org.apache.commons.math.linear.NotPositiveDefiniteMatrixException e) {
			junit.framework.Assert.fail("not positive definite matrix");
		}
	}

	@java.lang.Override
	public void tearDown() {
		mean = null;
		covariance = null;
		generator = null;
	}

	private double[] mean;

	private org.apache.commons.math.linear.RealMatrix covariance;

	private org.apache.commons.math.random.CorrelatedRandomVectorGenerator generator;
}

