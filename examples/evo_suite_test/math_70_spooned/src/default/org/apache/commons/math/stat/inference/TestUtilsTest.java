package org.apache.commons.math.stat.inference;


public class TestUtilsTest extends junit.framework.TestCase {
	public TestUtilsTest(java.lang.String name) {
		super(name);
	}

	public void testChiSquare() throws java.lang.Exception {
		long[] observed = new long[]{ 10 , 9 , 11 };
		double[] expected = new double[]{ 10 , 10 , 10 };
		junit.framework.Assert.assertEquals("chi-square statistic", 0.2, org.apache.commons.math.stat.inference.TestUtils.chiSquare(expected, observed), 1.0E-11);
		junit.framework.Assert.assertEquals("chi-square p-value", 0.904837418036, org.apache.commons.math.stat.inference.TestUtils.chiSquareTest(expected, observed), 1.0E-10);
		long[] observed1 = new long[]{ 500 , 623 , 72 , 70 , 31 };
		double[] expected1 = new double[]{ 485 , 541 , 82 , 61 , 37 };
		junit.framework.Assert.assertEquals("chi-square test statistic", 9.023307936427388, org.apache.commons.math.stat.inference.TestUtils.chiSquare(expected1, observed1), 1.0E-10);
		junit.framework.Assert.assertEquals("chi-square p-value", 0.06051952647453607, org.apache.commons.math.stat.inference.TestUtils.chiSquareTest(expected1, observed1), 1.0E-9);
		junit.framework.Assert.assertTrue("chi-square test reject", org.apache.commons.math.stat.inference.TestUtils.chiSquareTest(expected1, observed1, 0.07));
		junit.framework.Assert.assertTrue("chi-square test accept", !(org.apache.commons.math.stat.inference.TestUtils.chiSquareTest(expected1, observed1, 0.05)));
		try {
			org.apache.commons.math.stat.inference.TestUtils.chiSquareTest(expected1, observed1, 95);
			junit.framework.Assert.fail("alpha out of range, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		long[] tooShortObs = new long[]{ 0 };
		double[] tooShortEx = new double[]{ 1 };
		try {
			org.apache.commons.math.stat.inference.TestUtils.chiSquare(tooShortEx, tooShortObs);
			junit.framework.Assert.fail("arguments too short, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		long[] unMatchedObs = new long[]{ 0 , 1 , 2 , 3 };
		double[] unMatchedEx = new double[]{ 1 , 1 , 2 };
		try {
			org.apache.commons.math.stat.inference.TestUtils.chiSquare(unMatchedEx, unMatchedObs);
			junit.framework.Assert.fail("arrays have different lengths, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		expected[0] = 0;
		try {
			org.apache.commons.math.stat.inference.TestUtils.chiSquareTest(expected, observed, 0.01);
			junit.framework.Assert.fail("bad expected count, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		expected[0] = 1;
		observed[0] = -1;
		try {
			org.apache.commons.math.stat.inference.TestUtils.chiSquareTest(expected, observed, 0.01);
			junit.framework.Assert.fail("bad expected count, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testChiSquareIndependence() throws java.lang.Exception {
		long[][] counts = new long[][]{ new long[]{ 40 , 22 , 43 } , new long[]{ 91 , 21 , 28 } , new long[]{ 60 , 10 , 22 } };
		junit.framework.Assert.assertEquals("chi-square test statistic", 22.709027688, org.apache.commons.math.stat.inference.TestUtils.chiSquare(counts), 1.0E-9);
		junit.framework.Assert.assertEquals("chi-square p-value", 1.44751460134E-4, org.apache.commons.math.stat.inference.TestUtils.chiSquareTest(counts), 1.0E-9);
		junit.framework.Assert.assertTrue("chi-square test reject", org.apache.commons.math.stat.inference.TestUtils.chiSquareTest(counts, 2.0E-4));
		junit.framework.Assert.assertTrue("chi-square test accept", !(org.apache.commons.math.stat.inference.TestUtils.chiSquareTest(counts, 1.0E-4)));
		long[][] counts2 = new long[][]{ new long[]{ 10 , 15 } , new long[]{ 30 , 40 } , new long[]{ 60 , 90 } };
		junit.framework.Assert.assertEquals("chi-square test statistic", 0.168965517241, org.apache.commons.math.stat.inference.TestUtils.chiSquare(counts2), 1.0E-9);
		junit.framework.Assert.assertEquals("chi-square p-value", 0.918987499852, org.apache.commons.math.stat.inference.TestUtils.chiSquareTest(counts2), 1.0E-9);
		junit.framework.Assert.assertTrue("chi-square test accept", !(org.apache.commons.math.stat.inference.TestUtils.chiSquareTest(counts2, 0.1)));
		long[][] counts3 = new long[][]{ new long[]{ 40 , 22 , 43 } , new long[]{ 91 , 21 , 28 } , new long[]{ 60 , 10 } };
		try {
			org.apache.commons.math.stat.inference.TestUtils.chiSquare(counts3);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		long[][] counts4 = new long[][]{ new long[]{ 40 , 22 , 43 } };
		try {
			org.apache.commons.math.stat.inference.TestUtils.chiSquare(counts4);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		long[][] counts5 = new long[][]{ new long[]{ 40 } , new long[]{ 40 } , new long[]{ 30 } , new long[]{ 10 } };
		try {
			org.apache.commons.math.stat.inference.TestUtils.chiSquare(counts5);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		long[][] counts6 = new long[][]{ new long[]{ 10 , -2 } , new long[]{ 30 , 40 } , new long[]{ 60 , 90 } };
		try {
			org.apache.commons.math.stat.inference.TestUtils.chiSquare(counts6);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.stat.inference.TestUtils.chiSquareTest(counts, 0);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testChiSquareLargeTestStatistic() throws java.lang.Exception {
		double[] exp = new double[]{ 3389119.5 , 649136.6 , 285745.4 , 2.535736476E7 , 1.129118978E7 , 543628.0 , 232921.0 , 437665.75 };
		long[] obs = new long[]{ 2372383 , 584222 , 257170 , 17750155 , 7903832 , 489265 , 209628 , 393899 };
		org.apache.commons.math.stat.inference.ChiSquareTestImpl csti = new org.apache.commons.math.stat.inference.ChiSquareTestImpl();
		double cst = csti.chiSquareTest(exp, obs);
		junit.framework.Assert.assertEquals("chi-square p-value", 0.0, cst, 0.001);
		junit.framework.Assert.assertEquals("chi-square test statistic", 114875.90421929007, org.apache.commons.math.stat.inference.TestUtils.chiSquare(exp, obs), 1.0E-9);
	}

	public void testChiSquareZeroCount() throws java.lang.Exception {
		long[][] counts = new long[][]{ new long[]{ 40 , 0 , 4 } , new long[]{ 91 , 1 , 2 } , new long[]{ 60 , 2 , 0 } };
		junit.framework.Assert.assertEquals("chi-square test statistic", 9.67444662263, org.apache.commons.math.stat.inference.TestUtils.chiSquare(counts), 1.0E-9);
		junit.framework.Assert.assertEquals("chi-square p-value", 0.0462835770603, org.apache.commons.math.stat.inference.TestUtils.chiSquareTest(counts), 1.0E-9);
	}

	private double[] tooShortObs = new double[]{ 1.0 };

	private double[] emptyObs = new double[]{  };

	private org.apache.commons.math.stat.descriptive.SummaryStatistics emptyStats = new org.apache.commons.math.stat.descriptive.SummaryStatistics();

	public void testOneSampleT() throws java.lang.Exception {
		double[] observed = new double[]{ 93.0 , 103.0 , 95.0 , 101.0 , 91.0 , 105.0 , 96.0 , 94.0 , 101.0 , 88.0 , 98.0 , 94.0 , 101.0 , 92.0 , 95.0 };
		double mu = 100.0;
		org.apache.commons.math.stat.descriptive.SummaryStatistics sampleStats = null;
		sampleStats = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		for (int i = 0 ; i < (observed.length) ; i++) {
			sampleStats.addValue(observed[i]);
		}
		junit.framework.Assert.assertEquals("t statistic", -2.81976445346, org.apache.commons.math.stat.inference.TestUtils.t(mu, observed), 1.0E-9);
		junit.framework.Assert.assertEquals("t statistic", -2.81976445346, org.apache.commons.math.stat.inference.TestUtils.t(mu, sampleStats), 1.0E-9);
		junit.framework.Assert.assertEquals("p value", 0.0136390585873, org.apache.commons.math.stat.inference.TestUtils.tTest(mu, observed), 1.0E-9);
		junit.framework.Assert.assertEquals("p value", 0.0136390585873, org.apache.commons.math.stat.inference.TestUtils.tTest(mu, sampleStats), 1.0E-9);
		try {
			org.apache.commons.math.stat.inference.TestUtils.t(mu, ((double[])(null)));
			junit.framework.Assert.fail("arguments too short, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.stat.inference.TestUtils.t(mu, ((org.apache.commons.math.stat.descriptive.SummaryStatistics)(null)));
			junit.framework.Assert.fail("arguments too short, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.stat.inference.TestUtils.t(mu, emptyObs);
			junit.framework.Assert.fail("arguments too short, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.stat.inference.TestUtils.t(mu, emptyStats);
			junit.framework.Assert.fail("arguments too short, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.stat.inference.TestUtils.t(mu, tooShortObs);
			junit.framework.Assert.fail("insufficient data to compute t statistic, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.stat.inference.TestUtils.tTest(mu, tooShortObs);
			junit.framework.Assert.fail("insufficient data to perform t test, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.stat.inference.TestUtils.t(mu, ((org.apache.commons.math.stat.descriptive.SummaryStatistics)(null)));
			junit.framework.Assert.fail("insufficient data to compute t statistic, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.stat.inference.TestUtils.tTest(mu, ((org.apache.commons.math.stat.descriptive.SummaryStatistics)(null)));
			junit.framework.Assert.fail("insufficient data to perform t test, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testOneSampleTTest() throws java.lang.Exception {
		double[] oneSidedP = new double[]{ 2.0 , 0.0 , 6.0 , 6.0 , 3.0 , 3.0 , 2.0 , 3.0 , -6.0 , 6.0 , 6.0 , 6.0 , 3.0 , 0.0 , 1.0 , 1.0 , 0.0 , 2.0 , 3.0 , 3.0 };
		org.apache.commons.math.stat.descriptive.SummaryStatistics oneSidedPStats = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		for (int i = 0 ; i < (oneSidedP.length) ; i++) {
			oneSidedPStats.addValue(oneSidedP[i]);
		}
		junit.framework.Assert.assertEquals("one sample t stat", 3.86485535541, org.apache.commons.math.stat.inference.TestUtils.t(0.0, oneSidedP), 1.0E-9);
		junit.framework.Assert.assertEquals("one sample t stat", 3.86485535541, org.apache.commons.math.stat.inference.TestUtils.t(0.0, oneSidedPStats), 1.0E-10);
		junit.framework.Assert.assertEquals("one sample p value", 5.21637019637E-4, ((org.apache.commons.math.stat.inference.TestUtils.tTest(0.0, oneSidedP)) / 2.0), 1.0E-9);
		junit.framework.Assert.assertEquals("one sample p value", 5.21637019637E-4, ((org.apache.commons.math.stat.inference.TestUtils.tTest(0.0, oneSidedPStats)) / 2.0), 1.0E-4);
		junit.framework.Assert.assertTrue("one sample t-test reject", org.apache.commons.math.stat.inference.TestUtils.tTest(0.0, oneSidedP, 0.01));
		junit.framework.Assert.assertTrue("one sample t-test reject", org.apache.commons.math.stat.inference.TestUtils.tTest(0.0, oneSidedPStats, 0.01));
		junit.framework.Assert.assertTrue("one sample t-test accept", !(org.apache.commons.math.stat.inference.TestUtils.tTest(0.0, oneSidedP, 1.0E-4)));
		junit.framework.Assert.assertTrue("one sample t-test accept", !(org.apache.commons.math.stat.inference.TestUtils.tTest(0.0, oneSidedPStats, 1.0E-4)));
		try {
			org.apache.commons.math.stat.inference.TestUtils.tTest(0.0, oneSidedP, 95);
			junit.framework.Assert.fail("alpha out of range, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.stat.inference.TestUtils.tTest(0.0, oneSidedPStats, 95);
			junit.framework.Assert.fail("alpha out of range, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testTwoSampleTHeterscedastic() throws java.lang.Exception {
		double[] sample1 = new double[]{ 7.0 , -4.0 , 18.0 , 17.0 , -3.0 , -5.0 , 1.0 , 10.0 , 11.0 , -2.0 };
		double[] sample2 = new double[]{ -1.0 , 12.0 , -1.0 , -3.0 , 3.0 , -5.0 , 5.0 , 2.0 , -11.0 , -1.0 , -3.0 };
		org.apache.commons.math.stat.descriptive.SummaryStatistics sampleStats1 = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		for (int i = 0 ; i < (sample1.length) ; i++) {
			sampleStats1.addValue(sample1[i]);
		}
		org.apache.commons.math.stat.descriptive.SummaryStatistics sampleStats2 = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		for (int i = 0 ; i < (sample2.length) ; i++) {
			sampleStats2.addValue(sample2[i]);
		}
		junit.framework.Assert.assertEquals("two sample heteroscedastic t stat", 1.60371728768, org.apache.commons.math.stat.inference.TestUtils.t(sample1, sample2), 1.0E-10);
		junit.framework.Assert.assertEquals("two sample heteroscedastic t stat", 1.60371728768, org.apache.commons.math.stat.inference.TestUtils.t(sampleStats1, sampleStats2), 1.0E-10);
		junit.framework.Assert.assertEquals("two sample heteroscedastic p value", 0.128839369622, org.apache.commons.math.stat.inference.TestUtils.tTest(sample1, sample2), 1.0E-10);
		junit.framework.Assert.assertEquals("two sample heteroscedastic p value", 0.128839369622, org.apache.commons.math.stat.inference.TestUtils.tTest(sampleStats1, sampleStats2), 1.0E-10);
		junit.framework.Assert.assertTrue("two sample heteroscedastic t-test reject", org.apache.commons.math.stat.inference.TestUtils.tTest(sample1, sample2, 0.2));
		junit.framework.Assert.assertTrue("two sample heteroscedastic t-test reject", org.apache.commons.math.stat.inference.TestUtils.tTest(sampleStats1, sampleStats2, 0.2));
		junit.framework.Assert.assertTrue("two sample heteroscedastic t-test accept", !(org.apache.commons.math.stat.inference.TestUtils.tTest(sample1, sample2, 0.1)));
		junit.framework.Assert.assertTrue("two sample heteroscedastic t-test accept", !(org.apache.commons.math.stat.inference.TestUtils.tTest(sampleStats1, sampleStats2, 0.1)));
		try {
			org.apache.commons.math.stat.inference.TestUtils.tTest(sample1, sample2, 0.95);
			junit.framework.Assert.fail("alpha out of range, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.stat.inference.TestUtils.tTest(sampleStats1, sampleStats2, 0.95);
			junit.framework.Assert.fail("alpha out of range, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.stat.inference.TestUtils.tTest(sample1, tooShortObs, 0.01);
			junit.framework.Assert.fail("insufficient data, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.stat.inference.TestUtils.tTest(sampleStats1, ((org.apache.commons.math.stat.descriptive.SummaryStatistics)(null)), 0.01);
			junit.framework.Assert.fail("insufficient data, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.stat.inference.TestUtils.tTest(sample1, tooShortObs);
			junit.framework.Assert.fail("insufficient data, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.stat.inference.TestUtils.tTest(sampleStats1, ((org.apache.commons.math.stat.descriptive.SummaryStatistics)(null)));
			junit.framework.Assert.fail("insufficient data, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.stat.inference.TestUtils.t(sample1, tooShortObs);
			junit.framework.Assert.fail("insufficient data, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.stat.inference.TestUtils.t(sampleStats1, ((org.apache.commons.math.stat.descriptive.SummaryStatistics)(null)));
			junit.framework.Assert.fail("insufficient data, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testTwoSampleTHomoscedastic() throws java.lang.Exception {
		double[] sample1 = new double[]{ 2 , 4 , 6 , 8 , 10 , 97 };
		double[] sample2 = new double[]{ 4 , 6 , 8 , 10 , 16 };
		org.apache.commons.math.stat.descriptive.SummaryStatistics sampleStats1 = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		for (int i = 0 ; i < (sample1.length) ; i++) {
			sampleStats1.addValue(sample1[i]);
		}
		org.apache.commons.math.stat.descriptive.SummaryStatistics sampleStats2 = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		for (int i = 0 ; i < (sample2.length) ; i++) {
			sampleStats2.addValue(sample2[i]);
		}
		junit.framework.Assert.assertEquals("two sample homoscedastic t stat", 0.73096310086, org.apache.commons.math.stat.inference.TestUtils.homoscedasticT(sample1, sample2), 1.0E-10);
		junit.framework.Assert.assertEquals("two sample homoscedastic p value", 0.4833963785, org.apache.commons.math.stat.inference.TestUtils.homoscedasticTTest(sampleStats1, sampleStats2), 1.0E-10);
		junit.framework.Assert.assertTrue("two sample homoscedastic t-test reject", org.apache.commons.math.stat.inference.TestUtils.homoscedasticTTest(sample1, sample2, 0.49));
		junit.framework.Assert.assertTrue("two sample homoscedastic t-test accept", !(org.apache.commons.math.stat.inference.TestUtils.homoscedasticTTest(sample1, sample2, 0.48)));
	}

	public void testSmallSamples() throws java.lang.Exception {
		double[] sample1 = new double[]{ 1.0 , 3.0 };
		double[] sample2 = new double[]{ 4.0 , 5.0 };
		junit.framework.Assert.assertEquals(-2.2360679775, org.apache.commons.math.stat.inference.TestUtils.t(sample1, sample2), 1.0E-10);
		junit.framework.Assert.assertEquals(0.198727388935, org.apache.commons.math.stat.inference.TestUtils.tTest(sample1, sample2), 1.0E-10);
	}

	public void testPaired() throws java.lang.Exception {
		double[] sample1 = new double[]{ 1.0 , 3.0 , 5.0 , 7.0 };
		double[] sample2 = new double[]{ 0.0 , 6.0 , 11.0 , 2.0 };
		double[] sample3 = new double[]{ 5.0 , 7.0 , 8.0 , 10.0 };
		junit.framework.Assert.assertEquals(-0.3133, org.apache.commons.math.stat.inference.TestUtils.pairedT(sample1, sample2), 1.0E-4);
		junit.framework.Assert.assertEquals(0.774544295819, org.apache.commons.math.stat.inference.TestUtils.pairedTTest(sample1, sample2), 1.0E-10);
		junit.framework.Assert.assertEquals(0.001208, org.apache.commons.math.stat.inference.TestUtils.pairedTTest(sample1, sample3), 1.0E-6);
		junit.framework.Assert.assertFalse(org.apache.commons.math.stat.inference.TestUtils.pairedTTest(sample1, sample3, 0.001));
		junit.framework.Assert.assertTrue(org.apache.commons.math.stat.inference.TestUtils.pairedTTest(sample1, sample3, 0.002));
	}

	private double[] classA = new double[]{ 93.0 , 103.0 , 95.0 , 101.0 };

	private double[] classB = new double[]{ 99.0 , 92.0 , 102.0 , 100.0 , 102.0 };

	private double[] classC = new double[]{ 110.0 , 115.0 , 111.0 , 117.0 , 128.0 };

	private java.util.List<double[]> classes = new java.util.ArrayList<double[]>();

	private org.apache.commons.math.stat.inference.OneWayAnova oneWayAnova = new org.apache.commons.math.stat.inference.OneWayAnovaImpl();

	public void testOneWayAnovaUtils() throws java.lang.Exception {
		classes.add(classA);
		classes.add(classB);
		classes.add(classC);
		junit.framework.Assert.assertEquals(oneWayAnova.anovaFValue(classes), org.apache.commons.math.stat.inference.TestUtils.oneWayAnovaFValue(classes), 1.0E-11);
		junit.framework.Assert.assertEquals(oneWayAnova.anovaPValue(classes), org.apache.commons.math.stat.inference.TestUtils.oneWayAnovaPValue(classes), 1.0E-11);
		junit.framework.Assert.assertEquals(oneWayAnova.anovaTest(classes, 0.01), org.apache.commons.math.stat.inference.TestUtils.oneWayAnovaTest(classes, 0.01));
	}
}

