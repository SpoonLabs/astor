package org.apache.commons.math.analysis.solvers;


public class RiddersSolver extends org.apache.commons.math.analysis.solvers.UnivariateRealSolverImpl {
	@java.lang.Deprecated
	public RiddersSolver(org.apache.commons.math.analysis.UnivariateRealFunction f) {
		super(f, 100, 1.0E-6);
	}

	public RiddersSolver() {
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
		if ((f.value(min)) == 0.0) {
			return min;
		} 
		if ((f.value(max)) == 0.0) {
			return max;
		} 
		if ((f.value(initial)) == 0.0) {
			return initial;
		} 
		verifyBracketing(min, max, f);
		verifySequence(min, initial, max);
		if (isBracketing(min, initial, f)) {
			return solve(f, min, initial);
		} else {
			return solve(f, initial, max);
		}
	}

	public double solve(final org.apache.commons.math.analysis.UnivariateRealFunction f, final double min, final double max) throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.MaxIterationsExceededException {
		double x1 = min;
		double y1 = f.value(x1);
		double x2 = max;
		double y2 = f.value(x2);
		if (y1 == 0.0) {
			return min;
		} 
		if (y2 == 0.0) {
			return max;
		} 
		verifyBracketing(min, max, f);
		int i = 1;
		double oldx = java.lang.Double.POSITIVE_INFINITY;
		while (i <= (maximalIterationCount)) {
			final double x3 = 0.5 * (x1 + x2);
			final double y3 = f.value(x3);
			if ((java.lang.Math.abs(y3)) <= (functionValueAccuracy)) {
				setResult(x3, i);
				return result;
			} 
			final double delta = 1 - ((y1 * y2) / (y3 * y3));
			final double correction = (((org.apache.commons.math.util.MathUtils.sign(y2)) * (org.apache.commons.math.util.MathUtils.sign(y3))) * (x3 - x1)) / (java.lang.Math.sqrt(delta));
			final double x = x3 - correction;
			final double y = f.value(x);
			final double tolerance = java.lang.Math.max(((relativeAccuracy) * (java.lang.Math.abs(x))), absoluteAccuracy);
			if ((java.lang.Math.abs((x - oldx))) <= tolerance) {
				setResult(x, i);
				return result;
			} 
			if ((java.lang.Math.abs(y)) <= (functionValueAccuracy)) {
				setResult(x, i);
				return result;
			} 
			if (correction > 0.0) {
				if (((org.apache.commons.math.util.MathUtils.sign(y1)) + (org.apache.commons.math.util.MathUtils.sign(y))) == 0.0) {
					x2 = x;
					y2 = y;
				} else {
					x1 = x;
					x2 = x3;
					y1 = y;
					y2 = y3;
				}
			} else {
				if (((org.apache.commons.math.util.MathUtils.sign(y2)) + (org.apache.commons.math.util.MathUtils.sign(y))) == 0.0) {
					x1 = x;
					y1 = y;
				} else {
					x1 = x3;
					x2 = x;
					y1 = y3;
					y2 = y;
				}
			}
			oldx = x;
			i++;
		}
		throw new org.apache.commons.math.MaxIterationsExceededException(maximalIterationCount);
	}
}

