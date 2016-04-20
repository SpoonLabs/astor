package org.apache.commons.math.distribution;


public class ZipfDistributionTest extends org.apache.commons.math.distribution.IntegerDistributionAbstractTest {
	public ZipfDistributionTest(java.lang.String name) {
		super(name);
	}

	@java.lang.Override
	public org.apache.commons.math.distribution.IntegerDistribution makeDistribution() {
		return new org.apache.commons.math.distribution.ZipfDistributionImpl(10 , 1);
	}

	@java.lang.Override
	public int[] makeDensityTestPoints() {
		return new int[]{ -1 , 0 , 1 , 2 , 3 , 4 , 5 , 6 , 7 , 8 , 9 , 10 , 11 };
	}

	@java.lang.Override
	public double[] makeDensityTestValues() {
		return new double[]{ 0.0 , 0.0 , 0.3414 , 0.1707 , 0.1138 , 0.0854 , 0.0683 , 0.0569 , 0.0488 , 0.0427 , 0.0379 , 0.0341 , 0.0 };
	}

	@java.lang.Override
	public int[] makeCumulativeTestPoints() {
		return makeDensityTestPoints();
	}

	@java.lang.Override
	public double[] makeCumulativeTestValues() {
		return new double[]{ 0.0 , 0.0 , 0.3414 , 0.5121 , 0.6259 , 0.7113 , 0.7796 , 0.8365 , 0.8852 , 0.9279 , 0.9659 , 1.0 , 1.0 };
	}

	@java.lang.Override
	public double[] makeInverseCumulativeTestPoints() {
		return new double[]{ 0 , 0.001 , 0.01 , 0.025 , 0.05 , 0.3414 , 0.3415 , 0.999 , 0.99 , 0.975 , 0.95 , 0.9 , 1 };
	}

	@java.lang.Override
	public int[] makeInverseCumulativeTestValues() {
		return new int[]{ 0 , 0 , 0 , 0 , 0 , 0 , 1 , 9 , 9 , 9 , 8 , 7 , 10 };
	}
}

