package org.apache.commons.math.optimization;


public interface DifferentiableMultivariateVectorialOptimizer {
	void setMaxIterations(int maxIterations);

	int getMaxIterations();

	int getIterations();

	void setMaxEvaluations(int maxEvaluations);

	int getMaxEvaluations();

	int getEvaluations();

	int getJacobianEvaluations();

	void setConvergenceChecker(org.apache.commons.math.optimization.VectorialConvergenceChecker checker);

	org.apache.commons.math.optimization.VectorialConvergenceChecker getConvergenceChecker();

	org.apache.commons.math.optimization.VectorialPointValuePair optimize(org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction f, double[] target, double[] weights, double[] startPoint) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException;
}

