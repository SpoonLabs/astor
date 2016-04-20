package org.apache.commons.math.analysis.solvers;


public class BisectionSolver extends org.apache.commons.math.analysis.solvers.UnivariateRealSolverImpl {
	@java.lang.Deprecated
	public BisectionSolver(org.apache.commons.math.analysis.UnivariateRealFunction f) {
		super(f, 100, 1.0E-6);
	}

	public BisectionSolver() {
		super(100, 1.0E-6);
	}

	@java.lang.Deprecated
	public double solve(double min, double max, double initial) throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.MaxIterationsExceededException {
		return solve(f, min, max);
	}

	@java.lang.Deprecated
	public double solve(double min, double max) throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.MaxIterationsExceededException {
		return solve(f, min, max);
	}

	public double solve(final org.apache.commons.math.analysis.UnivariateRealFunction f, double min, double max, double initial) throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.MaxIterationsExceededException {
		return solve(min, max);
	}

	public double solve(final org.apache.commons.math.analysis.UnivariateRealFunction f, double min, double max) throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.MaxIterationsExceededException {
		clearResult();
		verifyInterval(min, max);
		double m;
		double fm;
		double fmin;
		int i = 0;
		while (i < (maximalIterationCount)) {
			m = org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.midpoint(min, max);
			fmin = f.value(min);
			fm = f.value(m);
			if ((fm * fmin) > 0.0) {
				min = m;
			} else {
				max = m;
			}
			if ((java.lang.Math.abs((max - min))) <= (absoluteAccuracy)) {
				m = org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.midpoint(min, max);
				setResult(m, i);
				return m;
			} 
			++i;
		}
		throw new org.apache.commons.math.MaxIterationsExceededException(maximalIterationCount);
	}
}

