package org.apache.commons.math.random;


public class AbstractRandomGeneratorTest extends org.apache.commons.math.random.RandomDataTest {
	protected org.apache.commons.math.random.TestRandomGenerator testGenerator = new org.apache.commons.math.random.TestRandomGenerator();

	public AbstractRandomGeneratorTest(java.lang.String name) {
		super(name);
		randomData = new org.apache.commons.math.random.RandomDataImpl(testGenerator);
	}

	@java.lang.Override
	public void testNextInt() {
		try {
			testGenerator.nextInt(-1);
			junit.framework.Assert.fail("IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		org.apache.commons.math.stat.Frequency freq = new org.apache.commons.math.stat.Frequency();
		int value = 0;
		for (int i = 0 ; i < (smallSampleSize) ; i++) {
			value = testGenerator.nextInt(4);
			junit.framework.Assert.assertTrue("nextInt range", ((value >= 0) && (value <= 3)));
			freq.addValue(value);
		}
		long[] observed = new long[4];
		for (int i = 0 ; i < 4 ; i++) {
			observed[i] = freq.getCount(i);
		}
		junit.framework.Assert.assertTrue("chi-square test -- will fail about 1 in 1000 times", ((testStatistic.chiSquare(expected, observed)) < 16.27));
	}

	@java.lang.Override
	public void testNextLong() {
		long q1 = (java.lang.Long.MAX_VALUE) / 4;
		long q2 = 2 * q1;
		long q3 = 3 * q1;
		org.apache.commons.math.stat.Frequency freq = new org.apache.commons.math.stat.Frequency();
		long val = 0;
		int value = 0;
		for (int i = 0 ; i < (smallSampleSize) ; i++) {
			val = testGenerator.nextLong();
			if (val < q1) {
				value = 0;
			} else {
				if (val < q2) {
					value = 1;
				} else {
					if (val < q3) {
						value = 2;
					} else {
						value = 3;
					}
				}
			}
			freq.addValue(value);
		}
		long[] observed = new long[4];
		for (int i = 0 ; i < 4 ; i++) {
			observed[i] = freq.getCount(i);
		}
		junit.framework.Assert.assertTrue("chi-square test -- will fail about 1 in 1000 times", ((testStatistic.chiSquare(expected, observed)) < 16.27));
	}

	public void testNextBoolean() {
		long halfSampleSize = (smallSampleSize) / 2;
		double[] expected = new double[]{ halfSampleSize , halfSampleSize };
		long[] observed = new long[2];
		for (int i = 0 ; i < (smallSampleSize) ; i++) {
			if (testGenerator.nextBoolean()) {
				(observed[0])++;
			} else {
				(observed[1])++;
			}
		}
		junit.framework.Assert.assertTrue("chi-square test -- will fail about 1 in 1000 times", ((testStatistic.chiSquare(expected, observed)) < 10.828));
	}

	public void testNextFloat() {
		org.apache.commons.math.stat.Frequency freq = new org.apache.commons.math.stat.Frequency();
		float val = 0;
		int value = 0;
		for (int i = 0 ; i < (smallSampleSize) ; i++) {
			val = testGenerator.nextFloat();
			if (val < 0.25) {
				value = 0;
			} else {
				if (val < 0.5) {
					value = 1;
				} else {
					if (val < 0.75) {
						value = 2;
					} else {
						value = 3;
					}
				}
			}
			freq.addValue(value);
		}
		long[] observed = new long[4];
		for (int i = 0 ; i < 4 ; i++) {
			observed[i] = freq.getCount(i);
		}
		junit.framework.Assert.assertTrue("chi-square test -- will fail about 1 in 1000 times", ((testStatistic.chiSquare(expected, observed)) < 16.27));
	}
}

