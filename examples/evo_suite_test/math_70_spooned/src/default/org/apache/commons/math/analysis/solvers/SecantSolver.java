package org.apache.commons.math.analysis.solvers;


public class SecantSolver extends org.apache.commons.math.analysis.solvers.UnivariateRealSolverImpl {
	@java.lang.Deprecated
	public SecantSolver(org.apache.commons.math.analysis.UnivariateRealFunction f) {
		super(f, 100, 1.0E-6);
	}

	public SecantSolver() {
		super(100, 1.0E-6);
	}

	@java.lang.Deprecated
	public double solve(final double min, final double max) throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		return solve(f, min, max);
	}

	@java.lang.Deprecated
	public double solve(final double min, final double max, final double initial) throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		return solve(f, min, max, initial);
	}

	public double solve(final org.apache.commons.math.analysis.UnivariateRealFunction f, final double min, final double max, final double initial) throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.MaxIterationsExceededException {
		return solve(f, min, max);
	}

	public double solve(final org.apache.commons.math.analysis.UnivariateRealFunction f, final double min, final double max) throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.MaxIterationsExceededException {
		clearResult();
		verifyInterval(min, max);
		double x0 = min;
		double x1 = max;
		double y0 = f.value(x0);
		double y1 = f.value(x1);
		if ((y0 * y1) >= 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(("function values at endpoints do not have different signs, " + "endpoints: [{0}, {1}], values: [{2}, {3}]"), min, max, y0, y1);
		} 
		double x2 = x0;
		double y2 = y0;
		double oldDelta = x2 - x1;
		int i = 0;
		while (i < (maximalIterationCount)) {
			if ((java.lang.Math.abs(y2)) < (java.lang.Math.abs(y1))) {
				x0 = x1;
				x1 = x2;
				x2 = x0;
				y0 = y1;
				y1 = y2;
				y2 = y0;
			} 
			if ((java.lang.Math.abs(y1)) <= (functionValueAccuracy)) {
				setResult(x1, i);
				return result;
			} 
			if ((java.lang.Math.abs(oldDelta)) < (java.lang.Math.max(((relativeAccuracy) * (java.lang.Math.abs(x1))), absoluteAccuracy))) {
				setResult(x1, i);
				return result;
			} 
			double delta;
			if ((java.lang.Math.abs(y1)) > (java.lang.Math.abs(y0))) {
				delta = 0.5 * oldDelta;
			} else {
				delta = (x0 - x1) / (1 - (y0 / y1));
				if ((delta / oldDelta) > 1) {
					delta = 0.5 * oldDelta;
				} 
			}
			x0 = x1;
			y0 = y1;
			x1 = x1 + delta;
			y1 = f.value(x1);
			if ((y1 > 0) == (y2 > 0)) {
				x2 = x0;
				y2 = y0;
			} 
			oldDelta = x2 - x1;
			i++;
		}
		throw new org.apache.commons.math.MaxIterationsExceededException(maximalIterationCount);
	}
}

