package org.apache.commons.math.stat.inference;


public class TTestTest extends junit.framework.TestCase {
	protected org.apache.commons.math.stat.inference.TTest testStatistic = new org.apache.commons.math.stat.inference.TTestImpl();

	private double[] tooShortObs = new double[]{ 1.0 };

	private double[] emptyObs = new double[]{  };

	private org.apache.commons.math.stat.descriptive.SummaryStatistics emptyStats = new org.apache.commons.math.stat.descriptive.SummaryStatistics();

	org.apache.commons.math.stat.descriptive.SummaryStatistics tooShortStats = null;

	public TTestTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public void setUp() {
		tooShortStats = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		tooShortStats.addValue(0.0);
	}

	public void testOneSampleT() throws java.lang.Exception {
		double[] observed = new double[]{ 93.0 , 103.0 , 95.0 , 101.0 , 91.0 , 105.0 , 96.0 , 94.0 , 101.0 , 88.0 , 98.0 , 94.0 , 101.0 , 92.0 , 95.0 };
		double mu = 100.0;
		org.apache.commons.math.stat.descriptive.SummaryStatistics sampleStats = null;
		sampleStats = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		for (int i = 0 ; i < (observed.length) ; i++) {
			sampleStats.addValue(observed[i]);
		}
		junit.framework.Assert.assertEquals("t statistic", -2.81976445346, testStatistic.t(mu, observed), 1.0E-9);
		junit.framework.Assert.assertEquals("t statistic", -2.81976445346, testStatistic.t(mu, sampleStats), 1.0E-9);
		junit.framework.Assert.assertEquals("p value", 0.0136390585873, testStatistic.tTest(mu, observed), 1.0E-9);
		junit.framework.Assert.assertEquals("p value", 0.0136390585873, testStatistic.tTest(mu, sampleStats), 1.0E-9);
		try {
			testStatistic.t(mu, ((double[])(null)));
			junit.framework.Assert.fail("arguments too short, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			testStatistic.t(mu, ((org.apache.commons.math.stat.descriptive.SummaryStatistics)(null)));
			junit.framework.Assert.fail("arguments too short, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			testStatistic.t(mu, emptyObs);
			junit.framework.Assert.fail("arguments too short, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			testStatistic.t(mu, emptyStats);
			junit.framework.Assert.fail("arguments too short, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			testStatistic.t(mu, tooShortObs);
			junit.framework.Assert.fail("insufficient data to compute t statistic, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			testStatistic.tTest(mu, tooShortObs);
			junit.framework.Assert.fail("insufficient data to perform t test, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			testStatistic.t(mu, tooShortStats);
			junit.framework.Assert.fail("insufficient data to compute t statistic, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			testStatistic.tTest(mu, tooShortStats);
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
		junit.framework.Assert.assertEquals("one sample t stat", 3.86485535541, testStatistic.t(0.0, oneSidedP), 1.0E-9);
		junit.framework.Assert.assertEquals("one sample t stat", 3.86485535541, testStatistic.t(0.0, oneSidedPStats), 1.0E-10);
		junit.framework.Assert.assertEquals("one sample p value", 5.21637019637E-4, ((testStatistic.tTest(0.0, oneSidedP)) / 2.0), 1.0E-9);
		junit.framework.Assert.assertEquals("one sample p value", 5.21637019637E-4, ((testStatistic.tTest(0.0, oneSidedPStats)) / 2.0), 1.0E-4);
		junit.framework.Assert.assertTrue("one sample t-test reject", testStatistic.tTest(0.0, oneSidedP, 0.01));
		junit.framework.Assert.assertTrue("one sample t-test reject", testStatistic.tTest(0.0, oneSidedPStats, 0.01));
		junit.framework.Assert.assertTrue("one sample t-test accept", !(testStatistic.tTest(0.0, oneSidedP, 1.0E-4)));
		junit.framework.Assert.assertTrue("one sample t-test accept", !(testStatistic.tTest(0.0, oneSidedPStats, 1.0E-4)));
		try {
			testStatistic.tTest(0.0, oneSidedP, 95);
			junit.framework.Assert.fail("alpha out of range, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			testStatistic.tTest(0.0, oneSidedPStats, 95);
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
		junit.framework.Assert.assertEquals("two sample heteroscedastic t stat", 1.60371728768, testStatistic.t(sample1, sample2), 1.0E-10);
		junit.framework.Assert.assertEquals("two sample heteroscedastic t stat", 1.60371728768, testStatistic.t(sampleStats1, sampleStats2), 1.0E-10);
		junit.framework.Assert.assertEquals("two sample heteroscedastic p value", 0.128839369622, testStatistic.tTest(sample1, sample2), 1.0E-10);
		junit.framework.Assert.assertEquals("two sample heteroscedastic p value", 0.128839369622, testStatistic.tTest(sampleStats1, sampleStats2), 1.0E-10);
		junit.framework.Assert.assertTrue("two sample heteroscedastic t-test reject", testStatistic.tTest(sample1, sample2, 0.2));
		junit.framework.Assert.assertTrue("two sample heteroscedastic t-test reject", testStatistic.tTest(sampleStats1, sampleStats2, 0.2));
		junit.framework.Assert.assertTrue("two sample heteroscedastic t-test accept", !(testStatistic.tTest(sample1, sample2, 0.1)));
		junit.framework.Assert.assertTrue("two sample heteroscedastic t-test accept", !(testStatistic.tTest(sampleStats1, sampleStats2, 0.1)));
		try {
			testStatistic.tTest(sample1, sample2, 0.95);
			junit.framework.Assert.fail("alpha out of range, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			testStatistic.tTest(sampleStats1, sampleStats2, 0.95);
			junit.framework.Assert.fail("alpha out of range, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			testStatistic.tTest(sample1, tooShortObs, 0.01);
			junit.framework.Assert.fail("insufficient data, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			testStatistic.tTest(sampleStats1, tooShortStats, 0.01);
			junit.framework.Assert.fail("insufficient data, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			testStatistic.tTest(sample1, tooShortObs);
			junit.framework.Assert.fail("insufficient data, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			testStatistic.tTest(sampleStats1, tooShortStats);
			junit.framework.Assert.fail("insufficient data, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			testStatistic.t(sample1, tooShortObs);
			junit.framework.Assert.fail("insufficient data, IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			testStatistic.t(sampleStats1, tooShortStats);
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
		junit.framework.Assert.assertEquals("two sample homoscedastic t stat", 0.73096310086, testStatistic.homoscedasticT(sample1, sample2), 1.0E-10);
		junit.framework.Assert.assertEquals("two sample homoscedastic p value", 0.4833963785, testStatistic.homoscedasticTTest(sampleStats1, sampleStats2), 1.0E-10);
		junit.framework.Assert.assertTrue("two sample homoscedastic t-test reject", testStatistic.homoscedasticTTest(sample1, sample2, 0.49));
		junit.framework.Assert.assertTrue("two sample homoscedastic t-test accept", !(testStatistic.homoscedasticTTest(sample1, sample2, 0.48)));
	}

	public void testSmallSamples() throws java.lang.Exception {
		double[] sample1 = new double[]{ 1.0 , 3.0 };
		double[] sample2 = new double[]{ 4.0 , 5.0 };
		junit.framework.Assert.assertEquals(-2.2360679775, testStatistic.t(sample1, sample2), 1.0E-10);
		junit.framework.Assert.assertEquals(0.198727388935, testStatistic.tTest(sample1, sample2), 1.0E-10);
	}

	public void testPaired() throws java.lang.Exception {
		double[] sample1 = new double[]{ 1.0 , 3.0 , 5.0 , 7.0 };
		double[] sample2 = new double[]{ 0.0 , 6.0 , 11.0 , 2.0 };
		double[] sample3 = new double[]{ 5.0 , 7.0 , 8.0 , 10.0 };
		junit.framework.Assert.assertEquals(-0.3133, testStatistic.pairedT(sample1, sample2), 1.0E-4);
		junit.framework.Assert.assertEquals(0.774544295819, testStatistic.pairedTTest(sample1, sample2), 1.0E-10);
		junit.framework.Assert.assertEquals(0.001208, testStatistic.pairedTTest(sample1, sample3), 1.0E-6);
		junit.framework.Assert.assertFalse(testStatistic.pairedTTest(sample1, sample3, 0.001));
		junit.framework.Assert.assertTrue(testStatistic.pairedTTest(sample1, sample3, 0.002));
	}
}

