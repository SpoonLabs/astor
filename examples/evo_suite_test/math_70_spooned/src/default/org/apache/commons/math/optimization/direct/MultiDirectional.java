package org.apache.commons.math.optimization.direct;


public class MultiDirectional extends org.apache.commons.math.optimization.direct.DirectSearchOptimizer {
	private final double khi;

	private final double gamma;

	public MultiDirectional() {
		this.khi = 2.0;
		this.gamma = 0.5;
	}

	public MultiDirectional(final double khi ,final double gamma) {
		this.khi = khi;
		this.gamma = gamma;
	}

	@java.lang.Override
	protected void iterateSimplex(final java.util.Comparator<org.apache.commons.math.optimization.RealPointValuePair> comparator) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		final org.apache.commons.math.optimization.RealConvergenceChecker checker = getConvergenceChecker();
		while (true) {
			incrementIterationsCounter();
			final org.apache.commons.math.optimization.RealPointValuePair[] original = simplex;
			final org.apache.commons.math.optimization.RealPointValuePair best = original[0];
			final org.apache.commons.math.optimization.RealPointValuePair reflected = evaluateNewSimplex(original, 1.0, comparator);
			if ((comparator.compare(reflected, best)) < 0) {
				final org.apache.commons.math.optimization.RealPointValuePair[] reflectedSimplex = simplex;
				final org.apache.commons.math.optimization.RealPointValuePair expanded = evaluateNewSimplex(original, khi, comparator);
				if ((comparator.compare(reflected, expanded)) <= 0) {
					simplex = reflectedSimplex;
				} 
				return ;
			} 
			final org.apache.commons.math.optimization.RealPointValuePair contracted = evaluateNewSimplex(original, gamma, comparator);
			if ((comparator.compare(contracted, best)) < 0) {
				return ;
			} 
			final int iter = getIterations();
			boolean converged = true;
			for (int i = 0 ; i < (simplex.length) ; ++i) {
				converged &= checker.converged(iter, original[i], simplex[i]);
			}
			if (converged) {
				return ;
			} 
		}
	}

	private org.apache.commons.math.optimization.RealPointValuePair evaluateNewSimplex(final org.apache.commons.math.optimization.RealPointValuePair[] original, final double coeff, final java.util.Comparator<org.apache.commons.math.optimization.RealPointValuePair> comparator) throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		final double[] xSmallest = original[0].getPointRef();
		final int n = xSmallest.length;
		simplex = new org.apache.commons.math.optimization.RealPointValuePair[n + 1];
		simplex[0] = original[0];
		for (int i = 1 ; i <= n ; ++i) {
			final double[] xOriginal = original[i].getPointRef();
			final double[] xTransformed = new double[n];
			for (int j = 0 ; j < n ; ++j) {
				xTransformed[j] = (xSmallest[j]) + (coeff * ((xSmallest[j]) - (xOriginal[j])));
			}
			simplex[i] = new org.apache.commons.math.optimization.RealPointValuePair(xTransformed , java.lang.Double.NaN , false);
		}
		evaluateSimplex(comparator);
		return simplex[0];
	}
}

