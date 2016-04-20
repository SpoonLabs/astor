package org.apache.commons.math.stat;


public class CertifiedDataTest extends junit.framework.TestCase {
	protected double mean = java.lang.Double.NaN;

	protected double std = java.lang.Double.NaN;

	public CertifiedDataTest(java.lang.String name) {
		super(name);
	}

	public void testSummaryStatistics() throws java.lang.Exception {
		org.apache.commons.math.stat.descriptive.SummaryStatistics u = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		loadStats("data/PiDigits.txt", u);
		junit.framework.Assert.assertEquals("PiDigits: std", std, u.getStandardDeviation(), 1.0E-13);
		junit.framework.Assert.assertEquals("PiDigits: mean", mean, u.getMean(), 1.0E-13);
		loadStats("data/Mavro.txt", u);
		junit.framework.Assert.assertEquals("Mavro: std", std, u.getStandardDeviation(), 1.0E-14);
		junit.framework.Assert.assertEquals("Mavro: mean", mean, u.getMean(), 1.0E-14);
		loadStats("data/Michelso.txt", u);
		junit.framework.Assert.assertEquals("Michelso: std", std, u.getStandardDeviation(), 1.0E-13);
		junit.framework.Assert.assertEquals("Michelso: mean", mean, u.getMean(), 1.0E-13);
		loadStats("data/NumAcc1.txt", u);
		junit.framework.Assert.assertEquals("NumAcc1: std", std, u.getStandardDeviation(), 1.0E-14);
		junit.framework.Assert.assertEquals("NumAcc1: mean", mean, u.getMean(), 1.0E-14);
		loadStats("data/NumAcc2.txt", u);
		junit.framework.Assert.assertEquals("NumAcc2: std", std, u.getStandardDeviation(), 1.0E-14);
		junit.framework.Assert.assertEquals("NumAcc2: mean", mean, u.getMean(), 1.0E-14);
	}

	public void testDescriptiveStatistics() throws java.lang.Exception {
		org.apache.commons.math.stat.descriptive.DescriptiveStatistics u = new org.apache.commons.math.stat.descriptive.DescriptiveStatistics();
		loadStats("data/PiDigits.txt", u);
		junit.framework.Assert.assertEquals("PiDigits: std", std, u.getStandardDeviation(), 1.0E-14);
		junit.framework.Assert.assertEquals("PiDigits: mean", mean, u.getMean(), 1.0E-14);
		loadStats("data/Mavro.txt", u);
		junit.framework.Assert.assertEquals("Mavro: std", std, u.getStandardDeviation(), 1.0E-14);
		junit.framework.Assert.assertEquals("Mavro: mean", mean, u.getMean(), 1.0E-14);
		loadStats("data/Michelso.txt", u);
		junit.framework.Assert.assertEquals("Michelso: std", std, u.getStandardDeviation(), 1.0E-14);
		junit.framework.Assert.assertEquals("Michelso: mean", mean, u.getMean(), 1.0E-14);
		loadStats("data/NumAcc1.txt", u);
		junit.framework.Assert.assertEquals("NumAcc1: std", std, u.getStandardDeviation(), 1.0E-14);
		junit.framework.Assert.assertEquals("NumAcc1: mean", mean, u.getMean(), 1.0E-14);
		loadStats("data/NumAcc2.txt", u);
		junit.framework.Assert.assertEquals("NumAcc2: std", std, u.getStandardDeviation(), 1.0E-14);
		junit.framework.Assert.assertEquals("NumAcc2: mean", mean, u.getMean(), 1.0E-14);
	}

	private void loadStats(java.lang.String resource, java.lang.Object u) throws java.lang.Exception {
		org.apache.commons.math.stat.descriptive.DescriptiveStatistics d = null;
		org.apache.commons.math.stat.descriptive.SummaryStatistics s = null;
		if (u instanceof org.apache.commons.math.stat.descriptive.DescriptiveStatistics) {
			d = ((org.apache.commons.math.stat.descriptive.DescriptiveStatistics)(u));
		} else {
			s = ((org.apache.commons.math.stat.descriptive.SummaryStatistics)(u));
		}
		u.getClass().getDeclaredMethod("clear", new java.lang.Class[]{  }).invoke(u, new java.lang.Object[]{  });
		mean = java.lang.Double.NaN;
		std = java.lang.Double.NaN;
		java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(org.apache.commons.math.stat.CertifiedDataTest.class.getResourceAsStream(resource)));
		java.lang.String line = null;
		for (int j = 0 ; j < 60 ; j++) {
			line = in.readLine();
			if (j == 40) {
				mean = java.lang.Double.parseDouble(line.substring(((line.lastIndexOf(":")) + 1)).trim());
			} 
			if (j == 41) {
				std = java.lang.Double.parseDouble(line.substring(((line.lastIndexOf(":")) + 1)).trim());
			} 
		}
		line = in.readLine();
		while (line != null) {
			if (d != null) {
				d.addValue(java.lang.Double.parseDouble(line.trim()));
			} else {
				s.addValue(java.lang.Double.parseDouble(line.trim()));
			}
			line = in.readLine();
		}
		in.close();
	}
}

