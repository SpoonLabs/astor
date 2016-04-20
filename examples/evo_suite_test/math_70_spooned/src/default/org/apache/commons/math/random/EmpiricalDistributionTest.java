package org.apache.commons.math.random;


public final class EmpiricalDistributionTest extends org.apache.commons.math.RetryTestCase {
	protected org.apache.commons.math.random.EmpiricalDistribution empiricalDistribution = null;

	protected org.apache.commons.math.random.EmpiricalDistribution empiricalDistribution2 = null;

	protected java.io.File file = null;

	protected java.net.URL url = null;

	protected double[] dataArray = null;

	public EmpiricalDistributionTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public void setUp() throws java.io.IOException {
		empiricalDistribution = new org.apache.commons.math.random.EmpiricalDistributionImpl(100);
		url = getClass().getResource("testData.txt");
		empiricalDistribution2 = new org.apache.commons.math.random.EmpiricalDistributionImpl(100);
		java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(url.openStream()));
		java.lang.String str = null;
		java.util.ArrayList<java.lang.Double> list = new java.util.ArrayList<java.lang.Double>();
		while ((str = in.readLine()) != null) {
			list.add(java.lang.Double.valueOf(str));
		}
		in.close();
		in = null;
		dataArray = new double[list.size()];
		int i = 0;
		for (java.lang.Double data : list) {
			dataArray[i] = data.doubleValue();
			i++;
		}
	}

	public void testLoad() throws java.lang.Exception {
		empiricalDistribution.load(url);
		junit.framework.Assert.assertEquals(empiricalDistribution.getSampleStats().getN(), 1000, 1.0E-6);
		junit.framework.Assert.assertEquals(empiricalDistribution.getSampleStats().getMean(), 5.069831575018909, 1.0E-6);
		junit.framework.Assert.assertEquals(empiricalDistribution.getSampleStats().getStandardDeviation(), 1.0173699343977738, 1.0E-6);
	}

	public void testDoubleLoad() throws java.lang.Exception {
		empiricalDistribution2.load(dataArray);
		junit.framework.Assert.assertEquals(empiricalDistribution2.getSampleStats().getN(), 1000, 1.0E-6);
		junit.framework.Assert.assertEquals(empiricalDistribution2.getSampleStats().getMean(), 5.069831575018909, 1.0E-6);
		junit.framework.Assert.assertEquals(empiricalDistribution2.getSampleStats().getStandardDeviation(), 1.0173699343977738, 1.0E-6);
		double[] bounds = ((org.apache.commons.math.random.EmpiricalDistributionImpl)(empiricalDistribution2)).getGeneratorUpperBounds();
		junit.framework.Assert.assertEquals(bounds.length, 100);
		junit.framework.Assert.assertEquals(bounds[99], 1.0, 1.0E-11);
	}

	public void testNext() throws java.lang.Exception {
		tstGen(0.1);
		tstDoubleGen(0.1);
	}

	public void testNexFail() {
		try {
			empiricalDistribution.getNextValue();
			empiricalDistribution2.getNextValue();
			junit.framework.Assert.fail("Expecting IllegalStateException");
		} catch (java.lang.IllegalStateException ex) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testGridTooFine() throws java.lang.Exception {
		empiricalDistribution = new org.apache.commons.math.random.EmpiricalDistributionImpl(1001);
		tstGen(0.1);
		empiricalDistribution2 = new org.apache.commons.math.random.EmpiricalDistributionImpl(1001);
		tstDoubleGen(0.1);
	}

	public void testGridTooFat() throws java.lang.Exception {
		empiricalDistribution = new org.apache.commons.math.random.EmpiricalDistributionImpl(1);
		tstGen(5);
		empiricalDistribution2 = new org.apache.commons.math.random.EmpiricalDistributionImpl(1);
		tstDoubleGen(5);
	}

	public void testBinIndexOverflow() throws java.lang.Exception {
		double[] x = new double[]{ 9474.94326071674 , 2080107.8865462579 };
		new org.apache.commons.math.random.EmpiricalDistributionImpl().load(x);
	}

	public void testSerialization() {
		org.apache.commons.math.random.EmpiricalDistribution dist = new org.apache.commons.math.random.EmpiricalDistributionImpl();
		org.apache.commons.math.random.EmpiricalDistribution dist2 = ((org.apache.commons.math.random.EmpiricalDistribution)(org.apache.commons.math.TestUtils.serializeAndRecover(dist)));
		verifySame(dist, dist2);
		empiricalDistribution2.load(dataArray);
		dist2 = ((org.apache.commons.math.random.EmpiricalDistribution)(org.apache.commons.math.TestUtils.serializeAndRecover(empiricalDistribution2)));
		verifySame(empiricalDistribution2, dist2);
	}

	public void testLoadNullDoubleArray() {
		org.apache.commons.math.random.EmpiricalDistribution dist = new org.apache.commons.math.random.EmpiricalDistributionImpl();
		try {
			dist.load(((double[])(null)));
			junit.framework.Assert.fail("load((double[]) null) expected NullPointerException");
		} catch (java.lang.NullPointerException e) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testLoadNullURL() throws java.lang.Exception {
		org.apache.commons.math.random.EmpiricalDistribution dist = new org.apache.commons.math.random.EmpiricalDistributionImpl();
		try {
			dist.load(((java.net.URL)(null)));
			junit.framework.Assert.fail("load((URL) null) expected NullPointerException");
		} catch (java.lang.NullPointerException e) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testLoadNullFile() throws java.lang.Exception {
		org.apache.commons.math.random.EmpiricalDistribution dist = new org.apache.commons.math.random.EmpiricalDistributionImpl();
		try {
			dist.load(((java.io.File)(null)));
			junit.framework.Assert.fail("load((File) null) expected NullPointerException");
		} catch (java.lang.NullPointerException e) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testGetBinUpperBounds() {
		double[] testData = new double[]{ 0 , 1 , 1 , 2 , 3 , 4 , 4 , 5 , 6 , 7 , 8 , 9 , 10 };
		org.apache.commons.math.random.EmpiricalDistributionImpl dist = new org.apache.commons.math.random.EmpiricalDistributionImpl(5);
		dist.load(testData);
		double[] expectedBinUpperBounds = new double[]{ 2 , 4 , 6 , 8 , 10 };
		double[] expectedGeneratorUpperBounds = new double[]{ 4.0 / 13.0 , 7.0 / 13.0 , 9.0 / 13.0 , 11.0 / 13.0 , 1 };
		double tol = 1.0E-11;
		org.apache.commons.math.TestUtils.assertEquals(expectedBinUpperBounds, dist.getUpperBounds(), tol);
		org.apache.commons.math.TestUtils.assertEquals(expectedGeneratorUpperBounds, dist.getGeneratorUpperBounds(), tol);
	}

	private void verifySame(org.apache.commons.math.random.EmpiricalDistribution d1, org.apache.commons.math.random.EmpiricalDistribution d2) {
		junit.framework.Assert.assertEquals(d1.isLoaded(), d2.isLoaded());
		junit.framework.Assert.assertEquals(d1.getBinCount(), d2.getBinCount());
		junit.framework.Assert.assertEquals(d1.getSampleStats(), d2.getSampleStats());
		if (d1.isLoaded()) {
			for (int i = 0 ; i < (d1.getUpperBounds().length) ; i++) {
				junit.framework.Assert.assertEquals(d1.getUpperBounds()[i], d2.getUpperBounds()[i], 0);
			}
			junit.framework.Assert.assertEquals(d1.getBinStats(), d2.getBinStats());
		} 
	}

	private void tstGen(double tolerance) throws java.lang.Exception {
		empiricalDistribution.load(url);
		org.apache.commons.math.stat.descriptive.SummaryStatistics stats = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		for (int i = 1 ; i < 1000 ; i++) {
			stats.addValue(empiricalDistribution.getNextValue());
		}
		junit.framework.Assert.assertEquals("mean", stats.getMean(), 5.069831575018909, tolerance);
		junit.framework.Assert.assertEquals("std dev", stats.getStandardDeviation(), 1.0173699343977738, tolerance);
	}

	private void tstDoubleGen(double tolerance) throws java.lang.Exception {
		empiricalDistribution2.load(dataArray);
		org.apache.commons.math.stat.descriptive.SummaryStatistics stats = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		for (int i = 1 ; i < 1000 ; i++) {
			stats.addValue(empiricalDistribution2.getNextValue());
		}
		junit.framework.Assert.assertEquals("mean", stats.getMean(), 5.069831575018909, tolerance);
		junit.framework.Assert.assertEquals("std dev", stats.getStandardDeviation(), 1.0173699343977738, tolerance);
	}
}

