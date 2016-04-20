package org.apache.commons.math.analysis.solvers;


public class UnivariateRealSolverUtils {
	private static final java.lang.String NULL_FUNCTION_MESSAGE = "function is null";

	private UnivariateRealSolverUtils() {
		super();
	}

	public static double solve(org.apache.commons.math.analysis.UnivariateRealFunction f, double x0, double x1) throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.setup(f);
		return org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.LazyHolder.FACTORY.newDefaultSolver().solve(f, x0, x1);
	}

	public static double solve(org.apache.commons.math.analysis.UnivariateRealFunction f, double x0, double x1, double absoluteAccuracy) throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.setup(f);
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.LazyHolder.FACTORY.newDefaultSolver();
		solver.setAbsoluteAccuracy(absoluteAccuracy);
		return solver.solve(f, x0, x1);
	}

	public static double[] bracket(org.apache.commons.math.analysis.UnivariateRealFunction function, double initial, double lowerBound, double upperBound) throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		return org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.bracket(function, initial, lowerBound, upperBound, java.lang.Integer.MAX_VALUE);
	}

	public static double[] bracket(org.apache.commons.math.analysis.UnivariateRealFunction function, double initial, double lowerBound, double upperBound, int maximumIterations) throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		if (function == null) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(NULL_FUNCTION_MESSAGE);
		} 
		if (maximumIterations <= 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("bad value for maximum iterations number: {0}", maximumIterations);
		} 
		if (((initial < lowerBound) || (initial > upperBound)) || (lowerBound >= upperBound)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("invalid bracketing parameters:  lower bound={0},  initial={1}, upper bound={2}", lowerBound, initial, upperBound);
		} 
		double a = initial;
		double b = initial;
		double fa;
		double fb;
		int numIterations = 0;
		do {
			a = java.lang.Math.max((a - 1.0), lowerBound);
			b = java.lang.Math.min((b + 1.0), upperBound);
			fa = function.value(a);
			fb = function.value(b);
			numIterations++;
		} while ((((fa * fb) > 0.0) && (numIterations < maximumIterations)) && ((a > lowerBound) || (b < upperBound)) );
		if ((fa * fb) > 0.0) {
			throw new org.apache.commons.math.ConvergenceException(("number of iterations={0}, maximum iterations={1}, " + ("initial={2}, lower bound={3}, upper bound={4}, final a value={5}, " + "final b value={6}, f(a)={7}, f(b)={8}")) , numIterations , maximumIterations , initial , lowerBound , upperBound , a , b , fa , fb);
		} 
		return new double[]{ a , b };
	}

	public static double midpoint(double a, double b) {
		return (a + b) * 0.5;
	}

	private static void setup(org.apache.commons.math.analysis.UnivariateRealFunction f) {
		if (f == null) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(NULL_FUNCTION_MESSAGE);
		} 
	}

	private static class LazyHolder {
		private static final org.apache.commons.math.analysis.solvers.UnivariateRealSolverFactory FACTORY = org.apache.commons.math.analysis.solvers.UnivariateRealSolverFactory.newInstance();
	}
}

