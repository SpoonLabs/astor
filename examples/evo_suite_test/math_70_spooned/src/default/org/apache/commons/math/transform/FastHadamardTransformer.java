package org.apache.commons.math.transform;


public class FastHadamardTransformer implements org.apache.commons.math.transform.RealTransformer {
	public double[] transform(double[] f) throws java.lang.IllegalArgumentException {
		return fht(f);
	}

	public double[] transform(org.apache.commons.math.analysis.UnivariateRealFunction f, double min, double max, int n) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException {
		return fht(org.apache.commons.math.transform.FastFourierTransformer.sample(f, min, max, n));
	}

	public double[] inversetransform(double[] f) throws java.lang.IllegalArgumentException {
		return org.apache.commons.math.transform.FastFourierTransformer.scaleArray(fht(f), (1.0 / (f.length)));
	}

	public double[] inversetransform(org.apache.commons.math.analysis.UnivariateRealFunction f, double min, double max, int n) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException {
		final double[] unscaled = fht(org.apache.commons.math.transform.FastFourierTransformer.sample(f, min, max, n));
		return org.apache.commons.math.transform.FastFourierTransformer.scaleArray(unscaled, (1.0 / n));
	}

	public int[] transform(int[] f) throws java.lang.IllegalArgumentException {
		return fht(f);
	}

	protected double[] fht(double[] x) throws java.lang.IllegalArgumentException {
		final int n = x.length;
		final int halfN = n / 2;
		if (!(org.apache.commons.math.transform.FastFourierTransformer.isPowerOf2(n))) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0} is not a power of 2", n);
		} 
		double[] yPrevious = new double[n];
		double[] yCurrent = x.clone();
		for (int j = 1 ; j < n ; j <<= 1) {
			final double[] yTmp = yCurrent;
			yCurrent = yPrevious;
			yPrevious = yTmp;
			for (int i = 0 ; i < halfN ; ++i) {
				final int twoI = 2 * i;
				yCurrent[i] = (yPrevious[twoI]) + (yPrevious[(twoI + 1)]);
			}
			for (int i = halfN ; i < n ; ++i) {
				final int twoI = 2 * i;
				yCurrent[i] = (yPrevious[(twoI - n)]) - (yPrevious[((twoI - n) + 1)]);
			}
		}
		return yCurrent;
	}

	protected int[] fht(int[] x) throws java.lang.IllegalArgumentException {
		final int n = x.length;
		final int halfN = n / 2;
		if (!(org.apache.commons.math.transform.FastFourierTransformer.isPowerOf2(n))) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0} is not a power of 2", n);
		} 
		int[] yPrevious = new int[n];
		int[] yCurrent = x.clone();
		for (int j = 1 ; j < n ; j <<= 1) {
			final int[] yTmp = yCurrent;
			yCurrent = yPrevious;
			yPrevious = yTmp;
			for (int i = 0 ; i < halfN ; ++i) {
				final int twoI = 2 * i;
				yCurrent[i] = (yPrevious[twoI]) + (yPrevious[(twoI + 1)]);
			}
			for (int i = halfN ; i < n ; ++i) {
				final int twoI = 2 * i;
				yCurrent[i] = (yPrevious[(twoI - n)]) - (yPrevious[((twoI - n) + 1)]);
			}
		}
		return yCurrent;
	}
}

