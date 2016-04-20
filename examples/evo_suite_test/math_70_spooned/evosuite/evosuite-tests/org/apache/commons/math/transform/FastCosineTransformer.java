package org.apache.commons.math.transform;


public class FastCosineTransformer implements org.apache.commons.math.transform.RealTransformer {
	public FastCosineTransformer() {
		super();
	}

	public double[] transform(double[] f) throws java.lang.IllegalArgumentException {
		return fct(f);
	}

	public double[] transform(org.apache.commons.math.analysis.UnivariateRealFunction f, double min, double max, int n) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException {
		double[] data = org.apache.commons.math.transform.FastFourierTransformer.sample(f, min, max, n);
		return fct(data);
	}

	public double[] transform2(double[] f) throws java.lang.IllegalArgumentException {
		double scaling_coefficient = java.lang.Math.sqrt((2.0 / ((f.length) - 1)));
		return org.apache.commons.math.transform.FastFourierTransformer.scaleArray(fct(f), scaling_coefficient);
	}

	public double[] transform2(org.apache.commons.math.analysis.UnivariateRealFunction f, double min, double max, int n) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException {
		double[] data = org.apache.commons.math.transform.FastFourierTransformer.sample(f, min, max, n);
		double scaling_coefficient = java.lang.Math.sqrt((2.0 / (n - 1)));
		return org.apache.commons.math.transform.FastFourierTransformer.scaleArray(fct(data), scaling_coefficient);
	}

	public double[] inversetransform(double[] f) throws java.lang.IllegalArgumentException {
		double scaling_coefficient = 2.0 / ((f.length) - 1);
		return org.apache.commons.math.transform.FastFourierTransformer.scaleArray(fct(f), scaling_coefficient);
	}

	public double[] inversetransform(org.apache.commons.math.analysis.UnivariateRealFunction f, double min, double max, int n) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException {
		double[] data = org.apache.commons.math.transform.FastFourierTransformer.sample(f, min, max, n);
		double scaling_coefficient = 2.0 / (n - 1);
		return org.apache.commons.math.transform.FastFourierTransformer.scaleArray(fct(data), scaling_coefficient);
	}

	public double[] inversetransform2(double[] f) throws java.lang.IllegalArgumentException {
		return transform2(f);
	}

	public double[] inversetransform2(org.apache.commons.math.analysis.UnivariateRealFunction f, double min, double max, int n) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException {
		return transform2(f, min, max, n);
	}

	protected double[] fct(double[] f) throws java.lang.IllegalArgumentException {
		final double[] transformed = new double[f.length];
		final int n = (f.length) - 1;
		if (!(org.apache.commons.math.transform.FastFourierTransformer.isPowerOf2(n))) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0} is not a power of 2 plus one", f.length);
		} 
		if (n == 1) {
			transformed[0] = 0.5 * ((f[0]) + (f[1]));
			transformed[1] = 0.5 * ((f[0]) - (f[1]));
			return transformed;
		} 
		final double[] x = new double[n];
		x[0] = 0.5 * ((f[0]) + (f[n]));
		x[(n >> 1)] = f[(n >> 1)];
		double t1 = 0.5 * ((f[0]) - (f[n]));
		for (int i = 1 ; i < (n >> 1) ; i++) {
			final double a = 0.5 * ((f[i]) + (f[(n - i)]));
			final double b = (java.lang.Math.sin(((i * (java.lang.Math.PI)) / n))) * ((f[i]) - (f[(n - i)]));
			final double c = (java.lang.Math.cos(((i * (java.lang.Math.PI)) / n))) * ((f[i]) - (f[(n - i)]));
			x[i] = a - b;
			x[(n - i)] = a + b;
			t1 += c;
		}
		org.apache.commons.math.transform.FastFourierTransformer transformer = new org.apache.commons.math.transform.FastFourierTransformer();
		org.apache.commons.math.complex.Complex[] y = transformer.transform(x);
		transformed[0] = y[0].getReal();
		transformed[1] = t1;
		for (int i = 1 ; i < (n >> 1) ; i++) {
			transformed[(2 * i)] = y[i].getReal();
			transformed[((2 * i) + 1)] = (transformed[((2 * i) - 1)]) - (y[i].getImaginary());
		}
		transformed[n] = y[(n >> 1)].getReal();
		return transformed;
	}
}

