package org.apache.commons.math.stat.inference;


public interface UnknownDistributionChiSquareTest extends org.apache.commons.math.stat.inference.ChiSquareTest {
	double chiSquareDataSetsComparison(long[] observed1, long[] observed2) throws java.lang.IllegalArgumentException;

	double chiSquareTestDataSetsComparison(long[] observed1, long[] observed2) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException;

	boolean chiSquareTestDataSetsComparison(long[] observed1, long[] observed2, double alpha) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException;
}

