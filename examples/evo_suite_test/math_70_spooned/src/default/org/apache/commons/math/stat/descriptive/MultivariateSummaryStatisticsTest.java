package org.apache.commons.math.stat.descriptive;


public class MultivariateSummaryStatisticsTest extends junit.framework.TestCase {
	public MultivariateSummaryStatisticsTest(java.lang.String name) {
		super(name);
	}

	protected org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics createMultivariateSummaryStatistics(int k, boolean isCovarianceBiasCorrected) {
		return new org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics(k , isCovarianceBiasCorrected);
	}

	public void testSetterInjection() throws java.lang.Exception {
		org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics u = createMultivariateSummaryStatistics(2, true);
		u.setMeanImpl(new org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[]{ new org.apache.commons.math.stat.descriptive.MultivariateSummaryStatisticsTest.sumMean() , new org.apache.commons.math.stat.descriptive.MultivariateSummaryStatisticsTest.sumMean() });
		u.addValue(new double[]{ 1 , 2 });
		u.addValue(new double[]{ 3 , 4 });
		junit.framework.Assert.assertEquals(4, u.getMean()[0], 1.0E-14);
		junit.framework.Assert.assertEquals(6, u.getMean()[1], 1.0E-14);
		u.clear();
		u.addValue(new double[]{ 1 , 2 });
		u.addValue(new double[]{ 3 , 4 });
		junit.framework.Assert.assertEquals(4, u.getMean()[0], 1.0E-14);
		junit.framework.Assert.assertEquals(6, u.getMean()[1], 1.0E-14);
		u.clear();
		u.setMeanImpl(new org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[]{ new org.apache.commons.math.stat.descriptive.moment.Mean() , new org.apache.commons.math.stat.descriptive.moment.Mean() });
		u.addValue(new double[]{ 1 , 2 });
		u.addValue(new double[]{ 3 , 4 });
		junit.framework.Assert.assertEquals(2, u.getMean()[0], 1.0E-14);
		junit.framework.Assert.assertEquals(3, u.getMean()[1], 1.0E-14);
		junit.framework.Assert.assertEquals(2, u.getDimension());
	}

	public void testSetterIllegalState() throws java.lang.Exception {
		org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics u = createMultivariateSummaryStatistics(2, true);
		u.addValue(new double[]{ 1 , 2 });
		u.addValue(new double[]{ 3 , 4 });
		try {
			u.setMeanImpl(new org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[]{ new org.apache.commons.math.stat.descriptive.MultivariateSummaryStatisticsTest.sumMean() , new org.apache.commons.math.stat.descriptive.MultivariateSummaryStatisticsTest.sumMean() });
			junit.framework.Assert.fail("Expecting IllegalStateException");
		} catch (java.lang.IllegalStateException ex) {
		}
	}

	public void testToString() throws org.apache.commons.math.DimensionMismatchException {
		org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics stats = createMultivariateSummaryStatistics(2, true);
		stats.addValue(new double[]{ 1 , 3 });
		stats.addValue(new double[]{ 2 , 2 });
		stats.addValue(new double[]{ 3 , 1 });
		java.util.Locale d = java.util.Locale.getDefault();
		java.util.Locale.setDefault(java.util.Locale.US);
		junit.framework.Assert.assertEquals(("MultivariateSummaryStatistics:\n" + ("n: 3\n" + ("min: 1.0, 1.0\n" + ("max: 3.0, 3.0\n" + ("mean: 2.0, 2.0\n" + ("geometric mean: 1.817..., 1.817...\n" + ("sum of squares: 14.0, 14.0\n" + ("sum of logarithms: 1.791..., 1.791...\n" + ("standard deviation: 1.0, 1.0\n" + "covariance: Array2DRowRealMatrix{{1.0,-1.0},{-1.0,1.0}}\n"))))))))), stats.toString().replaceAll("([0-9]+\\.[0-9][0-9][0-9])[0-9]+", "$1..."));
		java.util.Locale.setDefault(d);
	}

	public void testShuffledStatistics() throws org.apache.commons.math.DimensionMismatchException {
		org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics reference = createMultivariateSummaryStatistics(2, true);
		org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics shuffled = createMultivariateSummaryStatistics(2, true);
		org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic[] tmp = shuffled.getGeoMeanImpl();
		shuffled.setGeoMeanImpl(shuffled.getMeanImpl());
		shuffled.setMeanImpl(shuffled.getMaxImpl());
		shuffled.setMaxImpl(shuffled.getMinImpl());
		shuffled.setMinImpl(shuffled.getSumImpl());
		shuffled.setSumImpl(shuffled.getSumsqImpl());
		shuffled.setSumsqImpl(shuffled.getSumLogImpl());
		shuffled.setSumLogImpl(tmp);
		for (int i = 100 ; i > 0 ; --i) {
			reference.addValue(new double[]{ i , i });
			shuffled.addValue(new double[]{ i , i });
		}
		org.apache.commons.math.TestUtils.assertEquals(reference.getMean(), shuffled.getGeometricMean(), 1.0E-10);
		org.apache.commons.math.TestUtils.assertEquals(reference.getMax(), shuffled.getMean(), 1.0E-10);
		org.apache.commons.math.TestUtils.assertEquals(reference.getMin(), shuffled.getMax(), 1.0E-10);
		org.apache.commons.math.TestUtils.assertEquals(reference.getSum(), shuffled.getMin(), 1.0E-10);
		org.apache.commons.math.TestUtils.assertEquals(reference.getSumSq(), shuffled.getSum(), 1.0E-10);
		org.apache.commons.math.TestUtils.assertEquals(reference.getSumLog(), shuffled.getSumSq(), 1.0E-10);
		org.apache.commons.math.TestUtils.assertEquals(reference.getGeometricMean(), shuffled.getSumLog(), 1.0E-10);
	}

	static class sumMean implements org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic {
		private double sum = 0;

		private long n = 0;

		public double evaluate(double[] values, int begin, int length) {
			return 0;
		}

		public double evaluate(double[] values) {
			return 0;
		}

		public void clear() {
			sum = 0;
			n = 0;
		}

		public long getN() {
			return n;
		}

		public double getResult() {
			return sum;
		}

		public void increment(double d) {
			sum += d;
			(n)++;
		}

		public void incrementAll(double[] values, int start, int length) {
		}

		public void incrementAll(double[] values) {
		}

		public org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic copy() {
			return new org.apache.commons.math.stat.descriptive.MultivariateSummaryStatisticsTest.sumMean();
		}
	}

	public void testDimension() {
		try {
			createMultivariateSummaryStatistics(2, true).addValue(new double[3]);
		} catch (org.apache.commons.math.DimensionMismatchException dme) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testStats() throws org.apache.commons.math.DimensionMismatchException {
		org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics u = createMultivariateSummaryStatistics(2, true);
		junit.framework.Assert.assertEquals(0, u.getN());
		u.addValue(new double[]{ 1 , 2 });
		u.addValue(new double[]{ 2 , 3 });
		u.addValue(new double[]{ 2 , 3 });
		u.addValue(new double[]{ 3 , 4 });
		junit.framework.Assert.assertEquals(4, u.getN());
		junit.framework.Assert.assertEquals(8, u.getSum()[0], 1.0E-10);
		junit.framework.Assert.assertEquals(12, u.getSum()[1], 1.0E-10);
		junit.framework.Assert.assertEquals(18, u.getSumSq()[0], 1.0E-10);
		junit.framework.Assert.assertEquals(38, u.getSumSq()[1], 1.0E-10);
		junit.framework.Assert.assertEquals(1, u.getMin()[0], 1.0E-10);
		junit.framework.Assert.assertEquals(2, u.getMin()[1], 1.0E-10);
		junit.framework.Assert.assertEquals(3, u.getMax()[0], 1.0E-10);
		junit.framework.Assert.assertEquals(4, u.getMax()[1], 1.0E-10);
		junit.framework.Assert.assertEquals(2.4849066497880004, u.getSumLog()[0], 1.0E-10);
		junit.framework.Assert.assertEquals(4.276666119016055, u.getSumLog()[1], 1.0E-10);
		junit.framework.Assert.assertEquals(1.8612097182041991, u.getGeometricMean()[0], 1.0E-10);
		junit.framework.Assert.assertEquals(2.9129506302439405, u.getGeometricMean()[1], 1.0E-10);
		junit.framework.Assert.assertEquals(2, u.getMean()[0], 1.0E-10);
		junit.framework.Assert.assertEquals(3, u.getMean()[1], 1.0E-10);
		junit.framework.Assert.assertEquals(java.lang.Math.sqrt((2.0 / 3.0)), u.getStandardDeviation()[0], 1.0E-10);
		junit.framework.Assert.assertEquals(java.lang.Math.sqrt((2.0 / 3.0)), u.getStandardDeviation()[1], 1.0E-10);
		junit.framework.Assert.assertEquals((2.0 / 3.0), u.getCovariance().getEntry(0, 0), 1.0E-10);
		junit.framework.Assert.assertEquals((2.0 / 3.0), u.getCovariance().getEntry(0, 1), 1.0E-10);
		junit.framework.Assert.assertEquals((2.0 / 3.0), u.getCovariance().getEntry(1, 0), 1.0E-10);
		junit.framework.Assert.assertEquals((2.0 / 3.0), u.getCovariance().getEntry(1, 1), 1.0E-10);
		u.clear();
		junit.framework.Assert.assertEquals(0, u.getN());
	}

	public void testN0andN1Conditions() throws java.lang.Exception {
		org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics u = createMultivariateSummaryStatistics(1, true);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(u.getMean()[0]));
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(u.getStandardDeviation()[0]));
		u.addValue(new double[]{ 1 });
		junit.framework.Assert.assertEquals(1.0, u.getMean()[0], 1.0E-10);
		junit.framework.Assert.assertEquals(1.0, u.getGeometricMean()[0], 1.0E-10);
		junit.framework.Assert.assertEquals(0.0, u.getStandardDeviation()[0], 1.0E-10);
		u.addValue(new double[]{ 2 });
		junit.framework.Assert.assertTrue(((u.getStandardDeviation()[0]) > 0));
	}

	public void testNaNContracts() throws org.apache.commons.math.DimensionMismatchException {
		org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics u = createMultivariateSummaryStatistics(1, true);
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(u.getMean()[0]));
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(u.getMin()[0]));
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(u.getStandardDeviation()[0]));
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(u.getGeometricMean()[0]));
		u.addValue(new double[]{ 1.0 });
		junit.framework.Assert.assertFalse(java.lang.Double.isNaN(u.getMean()[0]));
		junit.framework.Assert.assertFalse(java.lang.Double.isNaN(u.getMin()[0]));
		junit.framework.Assert.assertFalse(java.lang.Double.isNaN(u.getStandardDeviation()[0]));
		junit.framework.Assert.assertFalse(java.lang.Double.isNaN(u.getGeometricMean()[0]));
	}

	public void testSerialization() throws org.apache.commons.math.DimensionMismatchException {
		org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics u = createMultivariateSummaryStatistics(2, true);
		org.apache.commons.math.TestUtils.checkSerializedEquality(u);
		org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics s = ((org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics)(org.apache.commons.math.TestUtils.serializeAndRecover(u)));
		junit.framework.Assert.assertEquals(u, s);
		u.addValue(new double[]{ 2.0 , 1.0 });
		u.addValue(new double[]{ 1.0 , 1.0 });
		u.addValue(new double[]{ 3.0 , 1.0 });
		u.addValue(new double[]{ 4.0 , 1.0 });
		u.addValue(new double[]{ 5.0 , 1.0 });
		org.apache.commons.math.TestUtils.checkSerializedEquality(u);
		s = ((org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics)(org.apache.commons.math.TestUtils.serializeAndRecover(u)));
		junit.framework.Assert.assertEquals(u, s);
	}

	public void testEqualsAndHashCode() throws org.apache.commons.math.DimensionMismatchException {
		org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics u = createMultivariateSummaryStatistics(2, true);
		org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics t = null;
		int emptyHash = u.hashCode();
		junit.framework.Assert.assertTrue(u.equals(u));
		junit.framework.Assert.assertFalse(u.equals(t));
		junit.framework.Assert.assertFalse(u.equals(java.lang.Double.valueOf(0)));
		t = createMultivariateSummaryStatistics(2, true);
		junit.framework.Assert.assertTrue(t.equals(u));
		junit.framework.Assert.assertTrue(u.equals(t));
		junit.framework.Assert.assertEquals(emptyHash, t.hashCode());
		u.addValue(new double[]{ 2.0 , 1.0 });
		u.addValue(new double[]{ 1.0 , 1.0 });
		u.addValue(new double[]{ 3.0 , 1.0 });
		u.addValue(new double[]{ 4.0 , 1.0 });
		u.addValue(new double[]{ 5.0 , 1.0 });
		junit.framework.Assert.assertFalse(t.equals(u));
		junit.framework.Assert.assertFalse(u.equals(t));
		junit.framework.Assert.assertTrue(((u.hashCode()) != (t.hashCode())));
		t.addValue(new double[]{ 2.0 , 1.0 });
		t.addValue(new double[]{ 1.0 , 1.0 });
		t.addValue(new double[]{ 3.0 , 1.0 });
		t.addValue(new double[]{ 4.0 , 1.0 });
		t.addValue(new double[]{ 5.0 , 1.0 });
		junit.framework.Assert.assertTrue(t.equals(u));
		junit.framework.Assert.assertTrue(u.equals(t));
		junit.framework.Assert.assertEquals(u.hashCode(), t.hashCode());
		u.clear();
		t.clear();
		junit.framework.Assert.assertTrue(t.equals(u));
		junit.framework.Assert.assertTrue(u.equals(t));
		junit.framework.Assert.assertEquals(emptyHash, t.hashCode());
		junit.framework.Assert.assertEquals(emptyHash, u.hashCode());
	}
}

