package org.apache.commons.math.analysis.integration;


public class TrapezoidIntegrator extends org.apache.commons.math.analysis.integration.UnivariateRealIntegratorImpl {
	private double s;

	@java.lang.Deprecated
	public TrapezoidIntegrator(org.apache.commons.math.analysis.UnivariateRealFunction f) {
		super(f, 64);
	}

	public TrapezoidIntegrator() {
		super(64);
	}

	double stage(final org.apache.commons.math.analysis.UnivariateRealFunction f, final double min, final double max, final int n) throws org.apache.commons.math.FunctionEvaluationException {
		if (n == 0) {
			s = (0.5 * (max - min)) * ((f.value(min)) + (f.value(max)));
			return s;
		} else {
			final long np = 1L << (n - 1);
			double sum = 0;
			final double spacing = (max - min) / np;
			double x = min + (0.5 * spacing);
			for (long i = 0 ; i < np ; i++) {
				sum += f.value(x);
				x += spacing;
			}
			s = 0.5 * ((s) + (sum * spacing));
			return s;
		}
	}

	@java.lang.Deprecated
	public double integrate(final double min, final double max) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.MaxIterationsExceededException {
		return integrate(f, min, max);
	}

	public double integrate(final org.apache.commons.math.analysis.UnivariateRealFunction f, final double min, final double max) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.MaxIterationsExceededException {
		clearResult();
		verifyInterval(min, max);
		verifyIterationCount();
		double oldt = stage(f, min, max, 0);
		for (int i = 1 ; i <= (maximalIterationCount) ; ++i) {
			final double t = stage(f, min, max, i);
			if (i >= (minimalIterationCount)) {
				final double delta = java.lang.Math.abs((t - oldt));
				final double rLimit = ((relativeAccuracy) * ((java.lang.Math.abs(oldt)) + (java.lang.Math.abs(t)))) * 0.5;
				if ((delta <= rLimit) || (delta <= (absoluteAccuracy))) {
					setResult(t, i);
					return result;
				} 
			} 
			oldt = t;
		}
		throw new org.apache.commons.math.MaxIterationsExceededException(maximalIterationCount);
	}

	@java.lang.Override
	protected void verifyIterationCount() throws java.lang.IllegalArgumentException {
		super.verifyIterationCount();
		if ((maximalIterationCount) > 64) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("invalid iteration limits: min={0}, max={1}", 0, 64);
		} 
	}
}

