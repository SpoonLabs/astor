package org.apache.commons.math.optimization;


public interface MultivariateRealOptimizer {
	void setMaxIterations(int maxIterations);

	int getMaxIterations();

	void setMaxEvaluations(int maxEvaluations);

	int getMaxEvaluations();

	int getIterations();

	int getEvaluations();

	void setConvergenceChecker(org.apache.commons.math.optimization.RealConvergenceChecker checker);

	org.apache.commons.math.optimization.RealConvergenceChecker getConvergenceChecker();

	org.apache.commons.math.optimization.RealPointValuePair optimize(org.apache.commons.math.analysis.MultivariateRealFunction f, org.apache.commons.math.optimization.GoalType goalType, double[] startPoint) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException;
}

