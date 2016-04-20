package org.apache.commons.math.stat.ranking;


public class NaturalRankingTest extends junit.framework.TestCase {
	private final double[] exampleData = new double[]{ 20 , 17 , 30 , 42.3 , 17 , 50 , java.lang.Double.NaN , java.lang.Double.NEGATIVE_INFINITY , 17 };

	private final double[] tiesFirst = new double[]{ 0 , 0 , 2 , 1 , 4 };

	private final double[] tiesLast = new double[]{ 4 , 4 , 1 , 0 };

	private final double[] multipleNaNs = new double[]{ 0 , 1 , java.lang.Double.NaN , java.lang.Double.NaN };

	private final double[] multipleTies = new double[]{ 3 , 2 , 5 , 5 , 6 , 6 , 1 };

	private final double[] allSame = new double[]{ 0 , 0 , 0 , 0 };

	public NaturalRankingTest(java.lang.String arg0) {
		super(arg0);
	}

	@java.lang.Override
	protected void setUp() throws java.lang.Exception {
		super.setUp();
	}

	@java.lang.Override
	protected void tearDown() throws java.lang.Exception {
		super.tearDown();
	}

	public void testDefault() {
		org.apache.commons.math.stat.ranking.NaturalRanking ranking = new org.apache.commons.math.stat.ranking.NaturalRanking();
		double[] ranks = ranking.rank(exampleData);
		double[] correctRanks = new double[]{ 5 , 3 , 6 , 7 , 3 , 8 , 9 , 1 , 3 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(tiesFirst);
		correctRanks = new double[]{ 1.5 , 1.5 , 4 , 3 , 5 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(tiesLast);
		correctRanks = new double[]{ 3.5 , 3.5 , 2 , 1 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(multipleNaNs);
		correctRanks = new double[]{ 1 , 2 , 3.5 , 3.5 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(multipleTies);
		correctRanks = new double[]{ 3 , 2 , 4.5 , 4.5 , 6.5 , 6.5 , 1 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(allSame);
		correctRanks = new double[]{ 2.5 , 2.5 , 2.5 , 2.5 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
	}

	public void testNaNsMaximalTiesMinimum() {
		org.apache.commons.math.stat.ranking.NaturalRanking ranking = new org.apache.commons.math.stat.ranking.NaturalRanking(org.apache.commons.math.stat.ranking.TiesStrategy.MINIMUM);
		double[] ranks = ranking.rank(exampleData);
		double[] correctRanks = new double[]{ 5 , 2 , 6 , 7 , 2 , 8 , 9 , 1 , 2 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(tiesFirst);
		correctRanks = new double[]{ 1 , 1 , 4 , 3 , 5 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(tiesLast);
		correctRanks = new double[]{ 3 , 3 , 2 , 1 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(multipleNaNs);
		correctRanks = new double[]{ 1 , 2 , 3 , 3 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(multipleTies);
		correctRanks = new double[]{ 3 , 2 , 4 , 4 , 6 , 6 , 1 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(allSame);
		correctRanks = new double[]{ 1 , 1 , 1 , 1 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
	}

	public void testNaNsRemovedTiesSequential() {
		org.apache.commons.math.stat.ranking.NaturalRanking ranking = new org.apache.commons.math.stat.ranking.NaturalRanking(org.apache.commons.math.stat.ranking.NaNStrategy.REMOVED , org.apache.commons.math.stat.ranking.TiesStrategy.SEQUENTIAL);
		double[] ranks = ranking.rank(exampleData);
		double[] correctRanks = new double[]{ 5 , 2 , 6 , 7 , 3 , 8 , 1 , 4 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(tiesFirst);
		correctRanks = new double[]{ 1 , 2 , 4 , 3 , 5 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(tiesLast);
		correctRanks = new double[]{ 3 , 4 , 2 , 1 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(multipleNaNs);
		correctRanks = new double[]{ 1 , 2 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(multipleTies);
		correctRanks = new double[]{ 3 , 2 , 4 , 5 , 6 , 7 , 1 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(allSame);
		correctRanks = new double[]{ 1 , 2 , 3 , 4 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
	}

	public void testNaNsMinimalTiesMaximum() {
		org.apache.commons.math.stat.ranking.NaturalRanking ranking = new org.apache.commons.math.stat.ranking.NaturalRanking(org.apache.commons.math.stat.ranking.NaNStrategy.MINIMAL , org.apache.commons.math.stat.ranking.TiesStrategy.MAXIMUM);
		double[] ranks = ranking.rank(exampleData);
		double[] correctRanks = new double[]{ 6 , 5 , 7 , 8 , 5 , 9 , 2 , 2 , 5 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(tiesFirst);
		correctRanks = new double[]{ 2 , 2 , 4 , 3 , 5 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(tiesLast);
		correctRanks = new double[]{ 4 , 4 , 2 , 1 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(multipleNaNs);
		correctRanks = new double[]{ 3 , 4 , 2 , 2 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(multipleTies);
		correctRanks = new double[]{ 3 , 2 , 5 , 5 , 7 , 7 , 1 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(allSame);
		correctRanks = new double[]{ 4 , 4 , 4 , 4 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
	}

	public void testNaNsMinimalTiesAverage() {
		org.apache.commons.math.stat.ranking.NaturalRanking ranking = new org.apache.commons.math.stat.ranking.NaturalRanking(org.apache.commons.math.stat.ranking.NaNStrategy.MINIMAL);
		double[] ranks = ranking.rank(exampleData);
		double[] correctRanks = new double[]{ 6 , 4 , 7 , 8 , 4 , 9 , 1.5 , 1.5 , 4 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(tiesFirst);
		correctRanks = new double[]{ 1.5 , 1.5 , 4 , 3 , 5 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(tiesLast);
		correctRanks = new double[]{ 3.5 , 3.5 , 2 , 1 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(multipleNaNs);
		correctRanks = new double[]{ 3 , 4 , 1.5 , 1.5 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(multipleTies);
		correctRanks = new double[]{ 3 , 2 , 4.5 , 4.5 , 6.5 , 6.5 , 1 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(allSame);
		correctRanks = new double[]{ 2.5 , 2.5 , 2.5 , 2.5 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
	}

	public void testNaNsFixedTiesRandom() {
		org.apache.commons.math.random.RandomGenerator randomGenerator = new org.apache.commons.math.random.JDKRandomGenerator();
		randomGenerator.setSeed(1000);
		org.apache.commons.math.stat.ranking.NaturalRanking ranking = new org.apache.commons.math.stat.ranking.NaturalRanking(org.apache.commons.math.stat.ranking.NaNStrategy.FIXED , randomGenerator);
		double[] ranks = ranking.rank(exampleData);
		double[] correctRanks = new double[]{ 5 , 4 , 6 , 7 , 3 , 8 , java.lang.Double.NaN , 1 , 4 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(tiesFirst);
		correctRanks = new double[]{ 1 , 1 , 4 , 3 , 5 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(tiesLast);
		correctRanks = new double[]{ 3 , 4 , 2 , 1 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(multipleNaNs);
		correctRanks = new double[]{ 1 , 2 , java.lang.Double.NaN , java.lang.Double.NaN };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(multipleTies);
		correctRanks = new double[]{ 3 , 2 , 5 , 5 , 7 , 6 , 1 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranks = ranking.rank(allSame);
		correctRanks = new double[]{ 1 , 3 , 4 , 4 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
	}

	public void testNaNsAndInfs() {
		double[] data = new double[]{ 0 , java.lang.Double.POSITIVE_INFINITY , java.lang.Double.NaN , java.lang.Double.NEGATIVE_INFINITY };
		org.apache.commons.math.stat.ranking.NaturalRanking ranking = new org.apache.commons.math.stat.ranking.NaturalRanking(org.apache.commons.math.stat.ranking.NaNStrategy.MAXIMAL);
		double[] ranks = ranking.rank(data);
		double[] correctRanks = new double[]{ 2 , 3.5 , 3.5 , 1 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
		ranking = new org.apache.commons.math.stat.ranking.NaturalRanking(org.apache.commons.math.stat.ranking.NaNStrategy.MINIMAL);
		ranks = ranking.rank(data);
		correctRanks = new double[]{ 3 , 4 , 1.5 , 1.5 };
		org.apache.commons.math.TestUtils.assertEquals(correctRanks, ranks, 0.0);
	}
}

