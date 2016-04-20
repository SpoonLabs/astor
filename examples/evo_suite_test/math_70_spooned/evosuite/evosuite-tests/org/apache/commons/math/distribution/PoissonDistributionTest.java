package org.apache.commons.math.distribution;


public class PoissonDistributionTest extends org.apache.commons.math.distribution.IntegerDistributionAbstractTest {
	private static final double DEFAULT_TEST_POISSON_PARAMETER = 4.0;

	public PoissonDistributionTest(java.lang.String name) {
		super(name);
		setTolerance(1.0E-12);
	}

	@java.lang.Override
	public org.apache.commons.math.distribution.IntegerDistribution makeDistribution() {
		return new org.apache.commons.math.distribution.PoissonDistributionImpl(DEFAULT_TEST_POISSON_PARAMETER);
	}

	@java.lang.Override
	public int[] makeDensityTestPoints() {
		return new int[]{ -1 , 0 , 1 , 2 , 3 , 4 , 5 , 10 , 20 };
	}

	@java.lang.Override
	public double[] makeDensityTestValues() {
		return new double[]{ 0.0 , 0.0183156388887 , 0.073262555555 , 0.14652511111 , 0.195366814813 , 0.195366814813 , 0.156293451851 , 0.00529247667642 , 8.27746364655E-9 };
	}

	@java.lang.Override
	public int[] makeCumulativeTestPoints() {
		return new int[]{ -1 , 0 , 1 , 2 , 3 , 4 , 5 , 10 , 20 };
	}

	@java.lang.Override
	public double[] makeCumulativeTestValues() {
		return new double[]{ 0.0 , 0.0183156388887 , 0.0915781944437 , 0.238103305554 , 0.433470120367 , 0.62883693518 , 0.78513038703 , 0.99716023388 , 0.999999998077 };
	}

	@java.lang.Override
	public double[] makeInverseCumulativeTestPoints() {
		return new double[]{ 0.0 , 0.018315638889 , 0.0915781944437 , 0.238103305554 , 0.433470120367 , 0.62883693518 , 0.78513038704 , 0.99716023388 , 0.999999998077 };
	}

	@java.lang.Override
	public int[] makeInverseCumulativeTestValues() {
		return new int[]{ -1 , 0 , 1 , 2 , 3 , 4 , 5 , 10 , 20 };
	}

	public void testNormalApproximateProbability() throws java.lang.Exception {
		org.apache.commons.math.distribution.PoissonDistribution dist = new org.apache.commons.math.distribution.PoissonDistributionImpl(100);
		double result = (dist.normalApproximateProbability(110)) - (dist.normalApproximateProbability(89));
		junit.framework.Assert.assertEquals(0.706281887248, result, 1.0E-10);
		dist.setMean(10000);
		result = (dist.normalApproximateProbability(10200)) - (dist.normalApproximateProbability(9899));
		junit.framework.Assert.assertEquals(0.820070051552, result, 1.0E-10);
	}

	public void testDegenerateInverseCumulativeProbability() throws java.lang.Exception {
		org.apache.commons.math.distribution.PoissonDistribution dist = new org.apache.commons.math.distribution.PoissonDistributionImpl(DEFAULT_TEST_POISSON_PARAMETER);
		junit.framework.Assert.assertEquals(java.lang.Integer.MAX_VALUE, dist.inverseCumulativeProbability(1.0));
		junit.framework.Assert.assertEquals(-1, dist.inverseCumulativeProbability(0.0));
	}

	public void testMean() {
		org.apache.commons.math.distribution.PoissonDistribution dist = new org.apache.commons.math.distribution.PoissonDistributionImpl(DEFAULT_TEST_POISSON_PARAMETER);
		try {
			dist.setMean(-1);
			junit.framework.Assert.fail("negative mean.  IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		dist.setMean(10.0);
		junit.framework.Assert.assertEquals(10.0, dist.getMean(), 0.0);
	}

	public void testLargeMeanCumulativeProbability() {
		org.apache.commons.math.distribution.PoissonDistribution dist = new org.apache.commons.math.distribution.PoissonDistributionImpl(1.0);
		double mean = 1.0;
		while (mean <= 1.0E7) {
			dist.setMean(mean);
			double x = mean * 2.0;
			double dx = x / 10.0;
			double p = java.lang.Double.NaN;
			double sigma = java.lang.Math.sqrt(mean);
			while (x >= 0) {
				try {
					p = dist.cumulativeProbability(x);
					junit.framework.Assert.assertFalse(((("NaN cumulative probability returned for mean = " + mean) + " x = ") + x), java.lang.Double.isNaN(p));
					if (x > (mean - (2 * sigma))) {
						junit.framework.Assert.assertTrue(((("Zero cum probaility returned for mean = " + mean) + " x = ") + x), (p > 0));
					} 
				} catch (org.apache.commons.math.MathException ex) {
					junit.framework.Assert.fail(((((("mean of " + mean) + " and x of ") + x) + " caused ") + (ex.getMessage())));
				}
				x -= dx;
			}
			mean *= 10.0;
		}
	}

	public void testCumulativeProbabilitySpecial() throws java.lang.Exception {
		org.apache.commons.math.distribution.PoissonDistribution dist = new org.apache.commons.math.distribution.PoissonDistributionImpl(1.0);
		dist.setMean(9120);
		checkProbability(dist, 9075);
		checkProbability(dist, 9102);
		dist.setMean(5058);
		checkProbability(dist, 5044);
		dist.setMean(6986);
		checkProbability(dist, 6950);
	}

	private void checkProbability(org.apache.commons.math.distribution.PoissonDistribution dist, double x) throws java.lang.Exception {
		double p = dist.cumulativeProbability(x);
		junit.framework.Assert.assertFalse(((("NaN cumulative probability returned for mean = " + (dist.getMean())) + " x = ") + x), java.lang.Double.isNaN(p));
		junit.framework.Assert.assertTrue(((("Zero cum probability returned for mean = " + (dist.getMean())) + " x = ") + x), (p > 0));
	}

	public void testLargeMeanInverseCumulativeProbability() throws java.lang.Exception {
		org.apache.commons.math.distribution.PoissonDistribution dist = new org.apache.commons.math.distribution.PoissonDistributionImpl(1.0);
		double mean = 1.0;
		while (mean <= 100000.0) {
			dist.setMean(mean);
			double p = 0.1;
			double dp = p;
			while (p < 0.99) {
				double ret = java.lang.Double.NaN;
				try {
					ret = dist.inverseCumulativeProbability(p);
					junit.framework.Assert.assertTrue((p >= (dist.cumulativeProbability(ret))));
					junit.framework.Assert.assertTrue((p < (dist.cumulativeProbability((ret + 1)))));
				} catch (org.apache.commons.math.MathException ex) {
					junit.framework.Assert.fail(((((("mean of " + mean) + " and p of ") + p) + " caused ") + (ex.getMessage())));
				}
				p += dp;
			}
			mean *= 10.0;
		}
	}
}

