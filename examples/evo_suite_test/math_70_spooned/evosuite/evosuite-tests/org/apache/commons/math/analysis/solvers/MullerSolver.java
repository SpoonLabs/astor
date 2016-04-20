package org.apache.commons.math.analysis.solvers;


public class MullerSolver extends org.apache.commons.math.analysis.solvers.UnivariateRealSolverImpl {
	@java.lang.Deprecated
	public MullerSolver(org.apache.commons.math.analysis.UnivariateRealFunction f) {
		super(f, 100, 1.0E-6);
	}

	public MullerSolver() {
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
		double x0 = min;
		double y0 = f.value(x0);
		double x2 = max;
		double y2 = f.value(x2);
		double x1 = 0.5 * (x0 + x2);
		double y1 = f.value(x1);
		if (y0 == 0.0) {
			return min;
		} 
		if (y2 == 0.0) {
			return max;
		} 
		verifyBracketing(min, max, f);
		double oldx = java.lang.Double.POSITIVE_INFINITY;
		for (int i = 1 ; i <= (maximalIterationCount) ; ++i) {
			final double d01 = (y1 - y0) / (x1 - x0);
			final double d12 = (y2 - y1) / (x2 - x1);
			final double d012 = (d12 - d01) / (x2 - x0);
			final double c1 = d01 + ((x1 - x0) * d012);
			final double delta = (c1 * c1) - ((4 * y1) * d012);
			final double xplus = x1 + (((-2.0) * y1) / (c1 + (java.lang.Math.sqrt(delta))));
			final double xminus = x1 + (((-2.0) * y1) / (c1 - (java.lang.Math.sqrt(delta))));
			final double x = isSequence(x0, xplus, x2) ? xplus : xminus;
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
			boolean bisect = (((x < x1) && ((x1 - x0) > (0.95 * (x2 - x0)))) || ((x > x1) && ((x2 - x1) > (0.95 * (x2 - x0))))) || (x == x1);
			if (!bisect) {
				x0 = x < x1 ? x0 : x1;
				y0 = x < x1 ? y0 : y1;
				x2 = x > x1 ? x2 : x1;
				y2 = x > x1 ? y2 : y1;
				x1 = x;
				y1 = y;
				oldx = x;
			} else {
				double xm = 0.5 * (x0 + x2);
				double ym = f.value(xm);
				if (((org.apache.commons.math.util.MathUtils.sign(y0)) + (org.apache.commons.math.util.MathUtils.sign(ym))) == 0.0) {
					x2 = xm;
					y2 = ym;
				} else {
					x0 = xm;
					y0 = ym;
				}
				x1 = 0.5 * (x0 + x2);
				y1 = f.value(x1);
				oldx = java.lang.Double.POSITIVE_INFINITY;
			}
		}
		throw new org.apache.commons.math.MaxIterationsExceededException(maximalIterationCount);
	}

	@java.lang.Deprecated
	public double solve2(final double min, final double max) throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.MaxIterationsExceededException {
		return solve2(f, min, max);
	}

	public double solve2(final org.apache.commons.math.analysis.UnivariateRealFunction f, final double min, final double max) throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.MaxIterationsExceededException {
		double x0 = min;
		double y0 = f.value(x0);
		double x1 = max;
		double y1 = f.value(x1);
		double x2 = 0.5 * (x0 + x1);
		double y2 = f.value(x2);
		if (y0 == 0.0) {
			return min;
		} 
		if (y1 == 0.0) {
			return max;
		} 
		verifyBracketing(min, max, f);
		double oldx = java.lang.Double.POSITIVE_INFINITY;
		for (int i = 1 ; i <= (maximalIterationCount) ; ++i) {
			final double q = (x2 - x1) / (x1 - x0);
			final double a = q * ((y2 - ((1 + q) * y1)) + (q * y0));
			final double b = ((((2 * q) + 1) * y2) - (((1 + q) * (1 + q)) * y1)) + ((q * q) * y0);
			final double c = (1 + q) * y2;
			final double delta = (b * b) - ((4 * a) * c);
			double x;
			final double denominator;
			if (delta >= 0.0) {
				double dplus = b + (java.lang.Math.sqrt(delta));
				double dminus = b - (java.lang.Math.sqrt(delta));
				denominator = (java.lang.Math.abs(dplus)) > (java.lang.Math.abs(dminus)) ? dplus : dminus;
			} else {
				denominator = java.lang.Math.sqrt(((b * b) - delta));
			}
			if (denominator != 0) {
				x = x2 - (((2.0 * c) * (x2 - x1)) / denominator);
				while ((x == x1) || (x == x2)) {
					x += absoluteAccuracy;
				}
			} else {
				x = min + ((java.lang.Math.random()) * (max - min));
				oldx = java.lang.Double.POSITIVE_INFINITY;
			}
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
			x0 = x1;
			y0 = y1;
			x1 = x2;
			y1 = y2;
			x2 = x;
			y2 = y;
			oldx = x;
		}
		throw new org.apache.commons.math.MaxIterationsExceededException(maximalIterationCount);
	}
}

