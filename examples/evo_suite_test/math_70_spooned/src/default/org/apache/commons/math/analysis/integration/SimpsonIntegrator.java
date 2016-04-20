package org.apache.commons.math.analysis.integration;


public class SimpsonIntegrator extends org.apache.commons.math.analysis.integration.UnivariateRealIntegratorImpl {
	@java.lang.Deprecated
	public SimpsonIntegrator(org.apache.commons.math.analysis.UnivariateRealFunction f) {
		super(f, 64);
	}

	public SimpsonIntegrator() {
		super(64);
	}

	@java.lang.Deprecated
	public double integrate(final double min, final double max) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.MaxIterationsExceededException {
		return integrate(f, min, max);
	}

	public double integrate(final org.apache.commons.math.analysis.UnivariateRealFunction f, final double min, final double max) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.MaxIterationsExceededException {
		clearResult();
		verifyInterval(min, max);
		verifyIterationCount();
		org.apache.commons.math.analysis.integration.TrapezoidIntegrator qtrap = new org.apache.commons.math.analysis.integration.TrapezoidIntegrator();
		if ((minimalIterationCount) == 1) {
			final double s = ((4 * (qtrap.stage(f, min, max, 1))) - (qtrap.stage(f, min, max, 0))) / 3.0;
			setResult(s, 1);
			return result;
		} 
		double olds = 0;
		double oldt = qtrap.stage(f, min, max, 0);
		for (int i = 1 ; i <= (maximalIterationCount) ; ++i) {
			final double t = qtrap.stage(f, min, max, i);
			final double s = ((4 * t) - oldt) / 3.0;
			if (i >= (minimalIterationCount)) {
				final double delta = java.lang.Math.abs((s - olds));
				final double rLimit = ((relativeAccuracy) * ((java.lang.Math.abs(olds)) + (java.lang.Math.abs(s)))) * 0.5;
				if ((delta <= rLimit) || (delta <= (absoluteAccuracy))) {
					setResult(s, i);
					return result;
				} 
			} 
			olds = s;
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

