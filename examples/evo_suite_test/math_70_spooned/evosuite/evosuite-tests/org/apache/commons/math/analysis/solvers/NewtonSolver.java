package org.apache.commons.math.analysis.solvers;


public class NewtonSolver extends org.apache.commons.math.analysis.solvers.UnivariateRealSolverImpl {
	@java.lang.Deprecated
	public NewtonSolver(org.apache.commons.math.analysis.DifferentiableUnivariateRealFunction f) {
		super(f, 100, 1.0E-6);
	}

	public NewtonSolver() {
		super(100, 1.0E-6);
	}

	@java.lang.Deprecated
	public double solve(final double min, final double max) throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.MaxIterationsExceededException {
		return solve(f, min, max);
	}

	@java.lang.Deprecated
	public double solve(final double min, final double max, final double startValue) throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.MaxIterationsExceededException {
		return solve(f, min, max, startValue);
	}

	public double solve(final org.apache.commons.math.analysis.UnivariateRealFunction f, final double min, final double max) throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.MaxIterationsExceededException {
		return solve(f, min, max, org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.midpoint(min, max));
	}

	public double solve(final org.apache.commons.math.analysis.UnivariateRealFunction f, final double min, final double max, final double startValue) throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.MaxIterationsExceededException {
		try {
			final org.apache.commons.math.analysis.UnivariateRealFunction derivative = ((org.apache.commons.math.analysis.DifferentiableUnivariateRealFunction)(f)).derivative();
			clearResult();
			verifySequence(min, startValue, max);
			double x0 = startValue;
			double x1;
			int i = 0;
			while (i < (maximalIterationCount)) {
				x1 = x0 - ((f.value(x0)) / (derivative.value(x0)));
				if ((java.lang.Math.abs((x1 - x0))) <= (absoluteAccuracy)) {
					setResult(x1, i);
					return x1;
				} 
				x0 = x1;
				++i;
			}
			throw new org.apache.commons.math.MaxIterationsExceededException(maximalIterationCount);
		} catch (java.lang.ClassCastException cce) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("function is not differentiable");
		}
	}
}

