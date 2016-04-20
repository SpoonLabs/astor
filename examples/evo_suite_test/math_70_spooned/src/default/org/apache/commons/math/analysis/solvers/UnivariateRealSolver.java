package org.apache.commons.math.analysis.solvers;


public interface UnivariateRealSolver extends org.apache.commons.math.ConvergingAlgorithm {
	void setFunctionValueAccuracy(double accuracy);

	double getFunctionValueAccuracy();

	void resetFunctionValueAccuracy();

	@java.lang.Deprecated
	double solve(double min, double max) throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException;

	double solve(org.apache.commons.math.analysis.UnivariateRealFunction f, double min, double max) throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException;

	@java.lang.Deprecated
	double solve(double min, double max, double startValue) throws java.lang.IllegalArgumentException, org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException;

	double solve(org.apache.commons.math.analysis.UnivariateRealFunction f, double min, double max, double startValue) throws java.lang.IllegalArgumentException, org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException;

	double getResult();

	double getFunctionValue();
}

