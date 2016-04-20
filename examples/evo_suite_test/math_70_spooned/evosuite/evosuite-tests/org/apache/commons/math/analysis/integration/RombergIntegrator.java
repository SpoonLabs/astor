package org.apache.commons.math.analysis.integration;


public class RombergIntegrator extends org.apache.commons.math.analysis.integration.UnivariateRealIntegratorImpl {
	@java.lang.Deprecated
	public RombergIntegrator(org.apache.commons.math.analysis.UnivariateRealFunction f) {
		super(f, 32);
	}

	public RombergIntegrator() {
		super(32);
	}

	@java.lang.Deprecated
	public double integrate(final double min, final double max) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.MaxIterationsExceededException {
		return integrate(f, min, max);
	}

	public double integrate(final org.apache.commons.math.analysis.UnivariateRealFunction f, final double min, final double max) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.MaxIterationsExceededException {
		final int m = (maximalIterationCount) + 1;
		double[] previousRow = new double[m];
		double[] currentRow = new double[m];
		clearResult();
		verifyInterval(min, max);
		verifyIterationCount();
		org.apache.commons.math.analysis.integration.TrapezoidIntegrator qtrap = new org.apache.commons.math.analysis.integration.TrapezoidIntegrator();
		currentRow[0] = qtrap.stage(f, min, max, 0);
		double olds = currentRow[0];
		for (int i = 1 ; i <= (maximalIterationCount) ; ++i) {
			final double[] tmpRow = previousRow;
			previousRow = currentRow;
			currentRow = tmpRow;
			currentRow[0] = qtrap.stage(f, min, max, i);
			for (int j = 1 ; j <= i ; j++) {
				final double r = (1L << (2 * j)) - 1;
				final double tIJm1 = currentRow[(j - 1)];
				currentRow[j] = tIJm1 + ((tIJm1 - (previousRow[(j - 1)])) / r);
			}
			final double s = currentRow[i];
			if (i >= (minimalIterationCount)) {
				final double delta = java.lang.Math.abs((s - olds));
				final double rLimit = ((relativeAccuracy) * ((java.lang.Math.abs(olds)) + (java.lang.Math.abs(s)))) * 0.5;
				if ((delta <= rLimit) || (delta <= (absoluteAccuracy))) {
					setResult(s, i);
					return result;
				} 
			} 
			olds = s;
		}
		throw new org.apache.commons.math.MaxIterationsExceededException(maximalIterationCount);
	}

	@java.lang.Override
	protected void verifyIterationCount() throws java.lang.IllegalArgumentException {
		super.verifyIterationCount();
		if ((maximalIterationCount) > 32) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("invalid iteration limits: min={0}, max={1}", 0, 32);
		} 
	}
}

