package org.apache.commons.math.random;


public class RandomDataTest extends org.apache.commons.math.RetryTestCase {
	public RandomDataTest(java.lang.String name) {
		super(name);
		randomData = new org.apache.commons.math.random.RandomDataImpl();
	}

	protected long smallSampleSize = 1000;

	protected double[] expected = new double[]{ 250 , 250 , 250 , 250 };

	protected int largeSampleSize = 10000;

	private java.lang.String[] hex = new java.lang.String[]{ "0" , "1" , "2" , "3" , "4" , "5" , "6" , "7" , "8" , "9" , "a" , "b" , "c" , "d" , "e" , "f" };

	protected org.apache.commons.math.random.RandomDataImpl randomData = null;

	protected org.apache.commons.math.stat.inference.ChiSquareTestImpl testStatistic = new org.apache.commons.math.stat.inference.ChiSquareTestImpl();

	public void testNextIntExtremeValues() {
		int x = randomData.nextInt(java.lang.Integer.MIN_VALUE, java.lang.Integer.MAX_VALUE);
		int y = randomData.nextInt(java.lang.Integer.MIN_VALUE, java.lang.Integer.MAX_VALUE);
		junit.framework.Assert.assertFalse((x == y));
	}

	public void testNextLongExtremeValues() {
		long x = randomData.nextLong(java.lang.Long.MIN_VALUE, java.lang.Long.MAX_VALUE);
		long y = randomData.nextLong(java.lang.Long.MIN_VALUE, java.lang.Long.MAX_VALUE);
		junit.framework.Assert.assertFalse((x == y));
	}

	public void testNextInt() {
		try {
			randomData.nextInt(4, 3);
			junit.framework.Assert.fail("IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		org.apache.commons.math.stat.Frequency freq = new org.apache.commons.math.stat.Frequency();
		int value = 0;
		for (int i = 0 ; i < (smallSampleSize) ; i++) {
			value = randomData.nextInt(0, 3);
			junit.framework.Assert.assertTrue("nextInt range", ((value >= 0) && (value <= 3)));
			freq.addValue(value);
		}
		long[] observed = new long[4];
		for (int i = 0 ; i < 4 ; i++) {
			observed[i] = freq.getCount(i);
		}
		junit.framework.Assert.assertTrue("chi-square test -- will fail about 1 in 1000 times", ((testStatistic.chiSquare(expected, observed)) < 16.27));
	}

	public void testNextLong() {
		try {
			randomData.nextLong(4, 3);
			junit.framework.Assert.fail("IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		org.apache.commons.math.stat.Frequency freq = new org.apache.commons.math.stat.Frequency();
		long value = 0;
		for (int i = 0 ; i < (smallSampleSize) ; i++) {
			value = randomData.nextLong(0, 3);
			junit.framework.Assert.assertTrue("nextInt range", ((value >= 0) && (value <= 3)));
			freq.addValue(value);
		}
		long[] observed = new long[4];
		for (int i = 0 ; i < 4 ; i++) {
			observed[i] = freq.getCount(i);
		}
		junit.framework.Assert.assertTrue("chi-square test -- will fail about 1 in 1000 times", ((testStatistic.chiSquare(expected, observed)) < 16.27));
	}

	public void testNextSecureLong() {
		try {
			randomData.nextSecureLong(4, 3);
			junit.framework.Assert.fail("IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		org.apache.commons.math.stat.Frequency freq = new org.apache.commons.math.stat.Frequency();
		long value = 0;
		for (int i = 0 ; i < (smallSampleSize) ; i++) {
			value = randomData.nextSecureLong(0, 3);
			junit.framework.Assert.assertTrue("nextInt range", ((value >= 0) && (value <= 3)));
			freq.addValue(value);
		}
		long[] observed = new long[4];
		for (int i = 0 ; i < 4 ; i++) {
			observed[i] = freq.getCount(i);
		}
		junit.framework.Assert.assertTrue("chi-square test -- will fail about 1 in 1000 times", ((testStatistic.chiSquare(expected, observed)) < 16.27));
	}

	public void testNextSecureInt() {
		try {
			randomData.nextSecureInt(4, 3);
			junit.framework.Assert.fail("IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		org.apache.commons.math.stat.Frequency freq = new org.apache.commons.math.stat.Frequency();
		int value = 0;
		for (int i = 0 ; i < (smallSampleSize) ; i++) {
			value = randomData.nextSecureInt(0, 3);
			junit.framework.Assert.assertTrue("nextInt range", ((value >= 0) && (value <= 3)));
			freq.addValue(value);
		}
		long[] observed = new long[4];
		for (int i = 0 ; i < 4 ; i++) {
			observed[i] = freq.getCount(i);
		}
		junit.framework.Assert.assertTrue("chi-square test -- will fail about 1 in 1000 times", ((testStatistic.chiSquare(expected, observed)) < 16.27));
	}

	public void testNextPoisson() {
		try {
			randomData.nextPoisson(0);
			junit.framework.Assert.fail("zero mean -- expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		org.apache.commons.math.stat.Frequency f = new org.apache.commons.math.stat.Frequency();
		for (int i = 0 ; i < (largeSampleSize) ; i++) {
			try {
				f.addValue(randomData.nextPoisson(4.0));
			} catch (java.lang.Exception ex) {
				junit.framework.Assert.fail(ex.getMessage());
			}
		}
		long cumFreq = (((((f.getCount(0)) + (f.getCount(1))) + (f.getCount(2))) + (f.getCount(3))) + (f.getCount(4))) + (f.getCount(5));
		long sumFreq = f.getSumFreq();
		double cumPct = (java.lang.Double.valueOf(cumFreq).doubleValue()) / (java.lang.Double.valueOf(sumFreq).doubleValue());
		junit.framework.Assert.assertEquals("cum Poisson(4)", cumPct, 0.7851, 0.2);
		try {
			randomData.nextPoisson(-1);
			junit.framework.Assert.fail("negative mean supplied -- IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			randomData.nextPoisson(0);
			junit.framework.Assert.fail("0 mean supplied -- IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void _testNextPoissonConsistency() throws java.lang.Exception {
		for (int i = 1 ; i < 100 ; i++) {
			checkNextPoissonConsistency(i);
		}
		org.apache.commons.math.random.RandomData randomData = new org.apache.commons.math.random.RandomDataImpl();
		for (int i = 1 ; i < 10 ; i++) {
			checkNextPoissonConsistency(randomData.nextUniform(1, 1000));
		}
		for (int i = 1 ; i < 10 ; i++) {
			checkNextPoissonConsistency(randomData.nextUniform(1000, 3000));
		}
	}

	public void checkNextPoissonConsistency(double mean) throws java.lang.Exception {
		int sampleSize = 1000;
		int minExpectedCount = 7;
		long maxObservedValue = 0;
		double alpha = 0.001;
		org.apache.commons.math.stat.Frequency frequency = new org.apache.commons.math.stat.Frequency();
		for (int i = 0 ; i < sampleSize ; i++) {
			long value = randomData.nextPoisson(mean);
			if (value > maxObservedValue) {
				maxObservedValue = value;
			} 
			frequency.addValue(value);
		}
		org.apache.commons.math.distribution.PoissonDistribution poissonDistribution = new org.apache.commons.math.distribution.PoissonDistributionImpl(mean);
		int lower = 1;
		while (((poissonDistribution.cumulativeProbability((lower - 1))) * sampleSize) < minExpectedCount) {
			lower++;
		}
		int upper = ((int)(5 * mean));
		while (((1 - (poissonDistribution.cumulativeProbability((upper - 1)))) * sampleSize) < minExpectedCount) {
			upper--;
		}
		int binWidth = 1;
		boolean widthSufficient = false;
		double lowerBinMass = 0;
		double upperBinMass = 0;
		while (!widthSufficient) {
			lowerBinMass = poissonDistribution.cumulativeProbability(lower, ((lower + binWidth) - 1));
			upperBinMass = poissonDistribution.cumulativeProbability(((upper - binWidth) + 1), upper);
			widthSufficient = ((java.lang.Math.min(lowerBinMass, upperBinMass)) * sampleSize) >= minExpectedCount;
			binWidth++;
		}
		java.util.List<java.lang.Integer> binBounds = new java.util.ArrayList<java.lang.Integer>();
		binBounds.add(lower);
		int bound = lower + binWidth;
		while (bound < (upper - binWidth)) {
			binBounds.add(bound);
			bound += binWidth;
		}
		binBounds.add(bound);
		binBounds.add(upper);
		final int binCount = (binBounds.size()) + 1;
		long[] observed = new long[binCount];
		double[] expected = new double[binCount];
		observed[0] = 0;
		for (int i = 0 ; i < lower ; i++) {
			observed[0] += frequency.getCount(i);
		}
		expected[0] = (poissonDistribution.cumulativeProbability((lower - 1))) * sampleSize;
		observed[(binCount - 1)] = 0;
		for (int i = upper ; i <= maxObservedValue ; i++) {
			observed[(binCount - 1)] += frequency.getCount(i);
		}
		expected[(binCount - 1)] = (1 - (poissonDistribution.cumulativeProbability((upper - 1)))) * sampleSize;
		for (int i = 1 ; i < (binCount - 1) ; i++) {
			observed[i] = 0;
			for (int j = binBounds.get((i - 1)) ; j < (binBounds.get(i)) ; j++) {
				observed[i] += frequency.getCount(j);
			}
			expected[i] = ((poissonDistribution.cumulativeProbability(((binBounds.get(i)) - 1))) - (poissonDistribution.cumulativeProbability(((binBounds.get((i - 1))) - 1)))) * sampleSize;
		}
		org.apache.commons.math.stat.inference.ChiSquareTest chiSquareTest = new org.apache.commons.math.stat.inference.ChiSquareTestImpl();
		try {
			junit.framework.Assert.assertFalse(chiSquareTest.chiSquareTest(expected, observed, alpha));
		} catch (junit.framework.AssertionFailedError ex) {
			java.lang.StringBuffer msgBuffer = new java.lang.StringBuffer();
			java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");
			msgBuffer.append("Chisquare test failed for mean = ");
			msgBuffer.append(mean);
			msgBuffer.append(" p-value = ");
			msgBuffer.append(chiSquareTest.chiSquareTest(expected, observed));
			msgBuffer.append(" chisquare statistic = ");
			msgBuffer.append(chiSquareTest.chiSquare(expected, observed));
			msgBuffer.append(". \n");
			msgBuffer.append("bin\t\texpected\tobserved\n");
			for (int i = 0 ; i < (expected.length) ; i++) {
				msgBuffer.append("[");
				msgBuffer.append((i == 0 ? 1 : binBounds.get((i - 1))));
				msgBuffer.append(",");
				msgBuffer.append((i == (binBounds.size()) ? "inf" : binBounds.get(i)));
				msgBuffer.append(")");
				msgBuffer.append("\t\t");
				msgBuffer.append(df.format(expected[i]));
				msgBuffer.append("\t\t");
				msgBuffer.append(observed[i]);
				msgBuffer.append("\n");
			}
			msgBuffer.append("This test can fail randomly due to sampling error with probability ");
			msgBuffer.append(alpha);
			msgBuffer.append(".");
			junit.framework.Assert.fail(msgBuffer.toString());
		}
	}

	public void testNextHex() {
		try {
			randomData.nextHexString(-1);
			junit.framework.Assert.fail("negative length supplied -- IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			randomData.nextHexString(0);
			junit.framework.Assert.fail("zero length supplied -- IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		java.lang.String hexString = randomData.nextHexString(3);
		if ((hexString.length()) != 3) {
			junit.framework.Assert.fail("incorrect length for generated string");
		} 
		hexString = randomData.nextHexString(1);
		if ((hexString.length()) != 1) {
			junit.framework.Assert.fail("incorrect length for generated string");
		} 
		try {
			hexString = randomData.nextHexString(0);
			junit.framework.Assert.fail("zero length requested -- expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		if ((hexString.length()) != 1) {
			junit.framework.Assert.fail("incorrect length for generated string");
		} 
		org.apache.commons.math.stat.Frequency f = new org.apache.commons.math.stat.Frequency();
		for (int i = 0 ; i < (smallSampleSize) ; i++) {
			hexString = randomData.nextHexString(100);
			if ((hexString.length()) != 100) {
				junit.framework.Assert.fail("incorrect length for generated string");
			} 
			for (int j = 0 ; j < (hexString.length()) ; j++) {
				f.addValue(hexString.substring(j, (j + 1)));
			}
		}
		double[] expected = new double[16];
		long[] observed = new long[16];
		for (int i = 0 ; i < 16 ; i++) {
			expected[i] = (((double)(smallSampleSize)) * 100) / 16;
			observed[i] = f.getCount(hex[i]);
		}
		junit.framework.Assert.assertTrue("chi-square test -- will fail about 1 in 1000 times", ((testStatistic.chiSquare(expected, observed)) < 37.7));
	}

	public void testNextSecureHex() {
		try {
			randomData.nextSecureHexString(-1);
			junit.framework.Assert.fail("negative length -- IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			randomData.nextSecureHexString(0);
			junit.framework.Assert.fail("zero length -- IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		java.lang.String hexString = randomData.nextSecureHexString(3);
		if ((hexString.length()) != 3) {
			junit.framework.Assert.fail("incorrect length for generated string");
		} 
		hexString = randomData.nextSecureHexString(1);
		if ((hexString.length()) != 1) {
			junit.framework.Assert.fail("incorrect length for generated string");
		} 
		try {
			hexString = randomData.nextSecureHexString(0);
			junit.framework.Assert.fail("zero length requested -- expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		if ((hexString.length()) != 1) {
			junit.framework.Assert.fail("incorrect length for generated string");
		} 
		org.apache.commons.math.stat.Frequency f = new org.apache.commons.math.stat.Frequency();
		for (int i = 0 ; i < (smallSampleSize) ; i++) {
			hexString = randomData.nextSecureHexString(100);
			if ((hexString.length()) != 100) {
				junit.framework.Assert.fail("incorrect length for generated string");
			} 
			for (int j = 0 ; j < (hexString.length()) ; j++) {
				f.addValue(hexString.substring(j, (j + 1)));
			}
		}
		double[] expected = new double[16];
		long[] observed = new long[16];
		for (int i = 0 ; i < 16 ; i++) {
			expected[i] = (((double)(smallSampleSize)) * 100) / 16;
			observed[i] = f.getCount(hex[i]);
		}
		junit.framework.Assert.assertTrue("chi-square test -- will fail about 1 in 1000 times", ((testStatistic.chiSquare(expected, observed)) < 37.7));
	}

	public void testNextUniform() {
		try {
			randomData.nextUniform(4, 3);
			junit.framework.Assert.fail("IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			randomData.nextUniform(3, 3);
			junit.framework.Assert.fail("IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		double[] expected = new double[]{ 500 , 500 };
		long[] observed = new long[]{ 0 , 0 };
		double lower = -1.0;
		double upper = 20.0;
		double midpoint = (lower + upper) / 2.0;
		double result = 0;
		for (int i = 0 ; i < 1000 ; i++) {
			result = randomData.nextUniform(lower, upper);
			if ((result == lower) || (result == upper)) {
				junit.framework.Assert.fail(("generated value equal to an endpoint: " + result));
			} 
			if (result < midpoint) {
				(observed[0])++;
			} else {
				(observed[1])++;
			}
		}
		junit.framework.Assert.assertTrue("chi-square test -- will fail about 1 in 1000 times", ((testStatistic.chiSquare(expected, observed)) < 10.83));
	}

	public void testNextUniformExclusiveEndpoints() {
		for (int i = 0 ; i < 1000 ; i++) {
			double u = randomData.nextUniform(0.99, 1);
			junit.framework.Assert.assertTrue(((u > 0.99) && (u < 1)));
		}
	}

	public void testNextGaussian() {
		try {
			randomData.nextGaussian(0, 0);
			junit.framework.Assert.fail("zero sigma -- IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		org.apache.commons.math.stat.descriptive.SummaryStatistics u = new org.apache.commons.math.stat.descriptive.SummaryStatistics();
		for (int i = 0 ; i < (largeSampleSize) ; i++) {
			u.addValue(randomData.nextGaussian(0, 1));
		}
		double xbar = u.getMean();
		double s = u.getStandardDeviation();
		double n = u.getN();
		junit.framework.Assert.assertTrue((((java.lang.Math.abs(xbar)) / (s / (java.lang.Math.sqrt(n)))) < 3.29));
	}

	public void testNextExponential() {
		try {
			randomData.nextExponential(-1);
			junit.framework.Assert.fail("negative mean -- expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			randomData.nextExponential(0);
			junit.framework.Assert.fail("zero mean -- expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		long cumFreq = 0;
		double v = 0;
		for (int i = 0 ; i < (largeSampleSize) ; i++) {
			v = randomData.nextExponential(1);
			junit.framework.Assert.assertTrue("exponential deviate postive", (v > 0));
			if (v < 2) {
				cumFreq++;
			} 
		}
		junit.framework.Assert.assertEquals("exponential cumulative distribution", (((double)(cumFreq)) / ((double)(largeSampleSize))), 0.8646647167633873, 0.2);
	}

	public void testConfig() {
		randomData.reSeed(1000);
		double v = randomData.nextUniform(0, 1);
		randomData.reSeed();
		junit.framework.Assert.assertTrue("different seeds", ((java.lang.Math.abs((v - (randomData.nextUniform(0, 1))))) > 1.0E-11));
		randomData.reSeed(1000);
		junit.framework.Assert.assertEquals("same seeds", v, randomData.nextUniform(0, 1), 1.0E-11);
		randomData.reSeedSecure(1000);
		java.lang.String hex = randomData.nextSecureHexString(40);
		randomData.reSeedSecure();
		junit.framework.Assert.assertTrue("different seeds", !(hex.equals(randomData.nextSecureHexString(40))));
		randomData.reSeedSecure(1000);
		junit.framework.Assert.assertTrue("same seeds", !(hex.equals(randomData.nextSecureHexString(40))));
		org.apache.commons.math.random.RandomDataImpl rd = new org.apache.commons.math.random.RandomDataImpl();
		rd.reSeed(100);
		rd.nextLong(1, 2);
		org.apache.commons.math.random.RandomDataImpl rd2 = new org.apache.commons.math.random.RandomDataImpl();
		rd2.reSeedSecure(2000);
		rd2.nextSecureLong(1, 2);
		rd = new org.apache.commons.math.random.RandomDataImpl();
		rd.reSeed();
		rd.nextLong(1, 2);
		rd2 = new org.apache.commons.math.random.RandomDataImpl();
		rd2.reSeedSecure();
		rd2.nextSecureLong(1, 2);
	}

	public void testNextSample() {
		java.lang.Object[][] c = new java.lang.Object[][]{ new java.lang.Object[]{ "0" , "1" } , new java.lang.Object[]{ "0" , "2" } , new java.lang.Object[]{ "0" , "3" } , new java.lang.Object[]{ "0" , "4" } , new java.lang.Object[]{ "1" , "2" } , new java.lang.Object[]{ "1" , "3" } , new java.lang.Object[]{ "1" , "4" } , new java.lang.Object[]{ "2" , "3" } , new java.lang.Object[]{ "2" , "4" } , new java.lang.Object[]{ "3" , "4" } };
		long[] observed = new long[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 };
		double[] expected = new double[]{ 100 , 100 , 100 , 100 , 100 , 100 , 100 , 100 , 100 , 100 };
		java.util.HashSet<java.lang.Object> cPop = new java.util.HashSet<java.lang.Object>();
		for (int i = 0 ; i < 5 ; i++) {
			cPop.add(java.lang.Integer.toString(i));
		}
		java.lang.Object[] sets = new java.lang.Object[10];
		for (int i = 0 ; i < 10 ; i++) {
			java.util.HashSet<java.lang.Object> hs = new java.util.HashSet<java.lang.Object>();
			hs.add(c[i][0]);
			hs.add(c[i][1]);
			sets[i] = hs;
		}
		for (int i = 0 ; i < 1000 ; i++) {
			java.lang.Object[] cSamp = randomData.nextSample(cPop, 2);
			(observed[findSample(sets, cSamp)])++;
		}
		junit.framework.Assert.assertTrue("chi-square test -- will fail about 1 in 1000 times", ((testStatistic.chiSquare(expected, observed)) < 27.88));
		java.util.HashSet<java.lang.Object> hs = new java.util.HashSet<java.lang.Object>();
		hs.add("one");
		java.lang.Object[] one = randomData.nextSample(hs, 1);
		java.lang.String oneString = ((java.lang.String)(one[0]));
		if (((one.length) != 1) || (!(oneString.equals("one")))) {
			junit.framework.Assert.fail("bad sample for set size = 1, sample size = 1");
		} 
		try {
			one = randomData.nextSample(hs, 2);
			junit.framework.Assert.fail("sample size > set size, expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			hs = new java.util.HashSet<java.lang.Object>();
			one = randomData.nextSample(hs, 0);
			junit.framework.Assert.fail("n = k = 0, expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	@java.lang.SuppressWarnings(value = "unchecked")
	private int findSample(java.lang.Object[] u, java.lang.Object[] samp) {
		for (int i = 0 ; i < (u.length) ; i++) {
			java.util.HashSet<java.lang.Object> set = ((java.util.HashSet<java.lang.Object>)(u[i]));
			java.util.HashSet<java.lang.Object> sampSet = new java.util.HashSet<java.lang.Object>();
			for (int j = 0 ; j < (samp.length) ; j++) {
				sampSet.add(samp[j]);
			}
			if (set.equals(sampSet)) {
				return i;
			} 
		}
		junit.framework.Assert.fail((((("sample not found:{" + (samp[0])) + ",") + (samp[1])) + "}"));
		return -1;
	}

	public void testNextPermutation() {
		int[][] p = new int[][]{ new int[]{ 0 , 1 , 2 } , new int[]{ 0 , 2 , 1 } , new int[]{ 1 , 0 , 2 } , new int[]{ 1 , 2 , 0 } , new int[]{ 2 , 0 , 1 } , new int[]{ 2 , 1 , 0 } };
		long[] observed = new long[]{ 0 , 0 , 0 , 0 , 0 , 0 };
		double[] expected = new double[]{ 100 , 100 , 100 , 100 , 100 , 100 };
		for (int i = 0 ; i < 600 ; i++) {
			int[] perm = randomData.nextPermutation(3, 3);
			(observed[findPerm(p, perm)])++;
		}
		junit.framework.Assert.assertTrue("chi-square test -- will fail about 1 in 1000 times", ((testStatistic.chiSquare(expected, observed)) < 20.52));
		int[] perm = randomData.nextPermutation(1, 1);
		if (((perm.length) != 1) || ((perm[0]) != 0)) {
			junit.framework.Assert.fail("bad permutation for n = 1, sample k = 1");
			try {
				perm = randomData.nextPermutation(2, 3);
				junit.framework.Assert.fail("permutation k > n, expecting IllegalArgumentException");
			} catch (java.lang.IllegalArgumentException ex) {
			}
			try {
				perm = randomData.nextPermutation(0, 0);
				junit.framework.Assert.fail("permutation k = n = 0, expecting IllegalArgumentException");
			} catch (java.lang.IllegalArgumentException ex) {
			}
			try {
				perm = randomData.nextPermutation(-1, -3);
				junit.framework.Assert.fail("permutation k < n < 0, expecting IllegalArgumentException");
			} catch (java.lang.IllegalArgumentException ex) {
			}
		} 
	}

	private int findPerm(int[][] p, int[] samp) {
		for (int i = 0 ; i < (p.length) ; i++) {
			boolean good = true;
			for (int j = 0 ; j < (samp.length) ; j++) {
				if ((samp[j]) != (p[i][j])) {
					good = false;
				} 
			}
			if (good) {
				return i;
			} 
		}
		junit.framework.Assert.fail("permutation not found");
		return -1;
	}
}

