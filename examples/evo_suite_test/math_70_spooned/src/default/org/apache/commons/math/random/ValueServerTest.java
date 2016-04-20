package org.apache.commons.math.random;


public final class ValueServerTest extends org.apache.commons.math.RetryTestCase {
	private org.apache.commons.math.random.ValueServer vs = new org.apache.commons.math.random.ValueServer();

	public ValueServerTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public void setUp() {
		vs.setMode(org.apache.commons.math.random.ValueServer.DIGEST_MODE);
		try {
			java.net.URL url = getClass().getResource("testData.txt");
			vs.setValuesFileURL(url);
		} catch (java.lang.Exception ex) {
			junit.framework.Assert.fail("malformed test URL");
		}
	}

	public void testNextDigest() throws java.lang.Exception {
		double next = 0.0;
		double tolerance = 0.1;
		vs.computeDistribution();
		junit.framework.Assert.assertTrue("empirical distribution property", ((vs.getEmpiricalDistribution()) != null));
		org.apache.commons.math.stat.descriptive.SummaryStatistics stats = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		for (int i = 1 ; i < 1000 ; i++) {
			next = vs.getNext();
			stats.addValue(next);
		}
		junit.framework.Assert.assertEquals("mean", 5.069831575018909, stats.getMean(), tolerance);
		junit.framework.Assert.assertEquals("std dev", 1.0173699343977738, stats.getStandardDeviation(), tolerance);
		vs.computeDistribution(500);
		stats = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		for (int i = 1 ; i < 1000 ; i++) {
			next = vs.getNext();
			stats.addValue(next);
		}
		junit.framework.Assert.assertEquals("mean", 5.069831575018909, stats.getMean(), tolerance);
		junit.framework.Assert.assertEquals("std dev", 1.0173699343977738, stats.getStandardDeviation(), tolerance);
	}

	public void testNextDigestFail() throws java.lang.Exception {
		try {
			vs.getNext();
			junit.framework.Assert.fail("Expecting IllegalStateException");
		} catch (java.lang.IllegalStateException ex) {
		}
	}

	public void testEmptyReplayFile() {
		try {
			java.net.URL url = getClass().getResource("emptyFile.txt");
			vs.setMode(org.apache.commons.math.random.ValueServer.REPLAY_MODE);
			vs.setValuesFileURL(url);
			vs.getNext();
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.io.EOFException eof) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testEmptyDigestFile() {
		try {
			java.net.URL url = getClass().getResource("emptyFile.txt");
			vs.setMode(org.apache.commons.math.random.ValueServer.DIGEST_MODE);
			vs.setValuesFileURL(url);
			vs.computeDistribution();
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.io.EOFException eof) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testReplay() throws java.lang.Exception {
		double firstDataValue = 4.038625496201205;
		double secondDataValue = 3.6485326248346936;
		double tolerance = 1.0E-14;
		double compareValue = 0.0;
		vs.setMode(org.apache.commons.math.random.ValueServer.REPLAY_MODE);
		vs.resetReplayFile();
		compareValue = vs.getNext();
		junit.framework.Assert.assertEquals(compareValue, firstDataValue, tolerance);
		compareValue = vs.getNext();
		junit.framework.Assert.assertEquals(compareValue, secondDataValue, tolerance);
		for (int i = 3 ; i < 1001 ; i++) {
			compareValue = vs.getNext();
		}
		compareValue = vs.getNext();
		junit.framework.Assert.assertEquals(compareValue, firstDataValue, tolerance);
		compareValue = vs.getNext();
		junit.framework.Assert.assertEquals(compareValue, secondDataValue, tolerance);
		vs.closeReplayFile();
		vs.closeReplayFile();
	}

	public void testModes() throws java.lang.Exception {
		vs.setMode(org.apache.commons.math.random.ValueServer.CONSTANT_MODE);
		vs.setMu(0);
		junit.framework.Assert.assertEquals("constant mode test", vs.getMu(), vs.getNext(), java.lang.Double.MIN_VALUE);
		vs.setMode(org.apache.commons.math.random.ValueServer.UNIFORM_MODE);
		vs.setMu(2);
		double val = vs.getNext();
		junit.framework.Assert.assertTrue(((val > 0) && (val < 4)));
		vs.setSigma(1);
		vs.setMode(org.apache.commons.math.random.ValueServer.GAUSSIAN_MODE);
		val = vs.getNext();
		junit.framework.Assert.assertTrue("gaussian value close enough to mean", (val < ((vs.getMu()) + (100 * (vs.getSigma())))));
		vs.setMode(org.apache.commons.math.random.ValueServer.EXPONENTIAL_MODE);
		val = vs.getNext();
		junit.framework.Assert.assertTrue((val > 0));
		try {
			vs.setMode(1000);
			vs.getNext();
			junit.framework.Assert.fail("bad mode, expecting IllegalStateException");
		} catch (java.lang.IllegalStateException ex) {
		}
	}

	public void testFill() throws java.lang.Exception {
		vs.setMode(org.apache.commons.math.random.ValueServer.CONSTANT_MODE);
		vs.setMu(2);
		double[] val = new double[5];
		vs.fill(val);
		for (int i = 0 ; i < 5 ; i++) {
			junit.framework.Assert.assertEquals("fill test in place", 2, val[i], java.lang.Double.MIN_VALUE);
		}
		double[] v2 = vs.fill(3);
		for (int i = 0 ; i < 3 ; i++) {
			junit.framework.Assert.assertEquals("fill test in place", 2, v2[i], java.lang.Double.MIN_VALUE);
		}
	}

	public void testProperties() throws java.lang.Exception {
		vs.setMode(org.apache.commons.math.random.ValueServer.CONSTANT_MODE);
		junit.framework.Assert.assertEquals("mode test", org.apache.commons.math.random.ValueServer.CONSTANT_MODE, vs.getMode());
		vs.setValuesFileURL("http://www.apache.org");
		java.net.URL url = vs.getValuesFileURL();
		junit.framework.Assert.assertEquals("valuesFileURL test", "http://www.apache.org", url.toString());
	}
}

