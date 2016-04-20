package org.apache.commons.math.stat.inference;


public class ChiSquareTestImpl implements org.apache.commons.math.stat.inference.UnknownDistributionChiSquareTest {
	private org.apache.commons.math.distribution.ChiSquaredDistribution distribution;

	public ChiSquareTestImpl() {
		this(new org.apache.commons.math.distribution.ChiSquaredDistributionImpl(1.0));
	}

	public ChiSquareTestImpl(org.apache.commons.math.distribution.ChiSquaredDistribution x) {
		super();
		setDistribution(x);
	}

	public double chiSquare(double[] expected, long[] observed) throws java.lang.IllegalArgumentException {
		if ((expected.length) < 2) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("expected array length = {0}, must be at least 2", expected.length);
		} 
		if ((expected.length) != (observed.length)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("dimension mismatch {0} != {1}", expected.length, observed.length);
		} 
		checkPositive(expected);
		checkNonNegative(observed);
		double sumExpected = 0.0;
		double sumObserved = 0.0;
		for (int i = 0 ; i < (observed.length) ; i++) {
			sumExpected += expected[i];
			sumObserved += observed[i];
		}
		double ratio = 1.0;
		boolean rescale = false;
		if ((java.lang.Math.abs((sumExpected - sumObserved))) > 1.0E-5) {
			ratio = sumObserved / sumExpected;
			rescale = true;
		} 
		double sumSq = 0.0;
		for (int i = 0 ; i < (observed.length) ; i++) {
			if (rescale) {
				final double dev = (observed[i]) - (ratio * (expected[i]));
				sumSq += (dev * dev) / (ratio * (expected[i]));
			} else {
				final double dev = (observed[i]) - (expected[i]);
				sumSq += (dev * dev) / (expected[i]);
			}
		}
		return sumSq;
	}

	public double chiSquareTest(double[] expected, long[] observed) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		distribution.setDegreesOfFreedom(((expected.length) - 1.0));
		return 1.0 - (distribution.cumulativeProbability(chiSquare(expected, observed)));
	}

	public boolean chiSquareTest(double[] expected, long[] observed, double alpha) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		if ((alpha <= 0) || (alpha > 0.5)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("out of bounds significance level {0}, must be between {1} and {2}", alpha, 0, 0.5);
		} 
		return (chiSquareTest(expected, observed)) < alpha;
	}

	public double chiSquare(long[][] counts) throws java.lang.IllegalArgumentException {
		checkArray(counts);
		int nRows = counts.length;
		int nCols = counts[0].length;
		double[] rowSum = new double[nRows];
		double[] colSum = new double[nCols];
		double total = 0.0;
		for (int row = 0 ; row < nRows ; row++) {
			for (int col = 0 ; col < nCols ; col++) {
				rowSum[row] += counts[row][col];
				colSum[col] += counts[row][col];
				total += counts[row][col];
			}
		}
		double sumSq = 0.0;
		double expected = 0.0;
		for (int row = 0 ; row < nRows ; row++) {
			for (int col = 0 ; col < nCols ; col++) {
				expected = ((rowSum[row]) * (colSum[col])) / total;
				sumSq += (((counts[row][col]) - expected) * ((counts[row][col]) - expected)) / expected;
			}
		}
		return sumSq;
	}

	public double chiSquareTest(long[][] counts) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		checkArray(counts);
		double df = (((double)(counts.length)) - 1) * (((double)(counts[0].length)) - 1);
		distribution.setDegreesOfFreedom(df);
		return 1 - (distribution.cumulativeProbability(chiSquare(counts)));
	}

	public boolean chiSquareTest(long[][] counts, double alpha) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		if ((alpha <= 0) || (alpha > 0.5)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("out of bounds significance level {0}, must be between {1} and {2}", alpha, 0.0, 0.5);
		} 
		return (chiSquareTest(counts)) < alpha;
	}

	public double chiSquareDataSetsComparison(long[] observed1, long[] observed2) throws java.lang.IllegalArgumentException {
		if ((observed1.length) < 2) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("observed array length = {0}, must be at least 2", observed1.length);
		} 
		if ((observed1.length) != (observed2.length)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("dimension mismatch {0} != {1}", observed1.length, observed2.length);
		} 
		checkNonNegative(observed1);
		checkNonNegative(observed2);
		long countSum1 = 0;
		long countSum2 = 0;
		boolean unequalCounts = false;
		double weight = 0.0;
		for (int i = 0 ; i < (observed1.length) ; i++) {
			countSum1 += observed1[i];
			countSum2 += observed2[i];
		}
		if (countSum1 == 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("observed counts are all 0 in first observed array");
		} 
		if (countSum2 == 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("observed counts are all 0 in second observed array");
		} 
		unequalCounts = countSum1 != countSum2;
		if (unequalCounts) {
			weight = java.lang.Math.sqrt((((double)(countSum1)) / ((double)(countSum2))));
		} 
		double sumSq = 0.0;
		double dev = 0.0;
		double obs1 = 0.0;
		double obs2 = 0.0;
		for (int i = 0 ; i < (observed1.length) ; i++) {
			if (((observed1[i]) == 0) && ((observed2[i]) == 0)) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("observed counts are both zero for entry {0}", i);
			} else {
				obs1 = observed1[i];
				obs2 = observed2[i];
				if (unequalCounts) {
					dev = (obs1 / weight) - (obs2 * weight);
				} else {
					dev = obs1 - obs2;
				}
				sumSq += (dev * dev) / (obs1 + obs2);
			}
		}
		return sumSq;
	}

	public double chiSquareTestDataSetsComparison(long[] observed1, long[] observed2) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		distribution.setDegreesOfFreedom((((double)(observed1.length)) - 1));
		return 1 - (distribution.cumulativeProbability(chiSquareDataSetsComparison(observed1, observed2)));
	}

	public boolean chiSquareTestDataSetsComparison(long[] observed1, long[] observed2, double alpha) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		if ((alpha <= 0) || (alpha > 0.5)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("out of bounds significance level {0}, must be between {1} and {2}", alpha, 0.0, 0.5);
		} 
		return (chiSquareTestDataSetsComparison(observed1, observed2)) < alpha;
	}

	private void checkArray(long[][] in) throws java.lang.IllegalArgumentException {
		if ((in.length) < 2) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("invalid row dimension: {0} (must be at least 2)", in.length);
		} 
		if ((in[0].length) < 2) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("invalid column dimension: {0} (must be at least 2)", in[0].length);
		} 
		checkRectangular(in);
		checkNonNegative(in);
	}

	private void checkRectangular(long[][] in) {
		for (int i = 1 ; i < (in.length) ; i++) {
			if ((in[i].length) != (in[0].length)) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("some rows have length {0} while others have length {1}", in[i].length, in[0].length);
			} 
		}
	}

	private void checkPositive(double[] in) throws java.lang.IllegalArgumentException {
		for (int i = 0 ; i < (in.length) ; i++) {
			if ((in[i]) <= 0) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("element {0} is not positive: {1}", i, in[i]);
			} 
		}
	}

	private void checkNonNegative(long[] in) throws java.lang.IllegalArgumentException {
		for (int i = 0 ; i < (in.length) ; i++) {
			if ((in[i]) < 0) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("element {0} is negative: {1}", i, in[i]);
			} 
		}
	}

	private void checkNonNegative(long[][] in) throws java.lang.IllegalArgumentException {
		for (int i = 0 ; i < (in.length) ; i++) {
			for (int j = 0 ; j < (in[i].length) ; j++) {
				if ((in[i][j]) < 0) {
					throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("element ({0}, {1}) is negative: {2}", i, j, in[i][j]);
				} 
			}
		}
	}

	public void setDistribution(org.apache.commons.math.distribution.ChiSquaredDistribution value) {
		distribution = value;
	}
}

