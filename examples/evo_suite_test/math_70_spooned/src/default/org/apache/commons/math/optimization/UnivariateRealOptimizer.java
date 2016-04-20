package org.apache.commons.math.optimization;


public interface UnivariateRealOptimizer extends org.apache.commons.math.ConvergingAlgorithm {
	void setMaxEvaluations(int maxEvaluations);

	int getMaxEvaluations();

	int getEvaluations();

	double optimize(org.apache.commons.math.analysis.UnivariateRealFunction f, org.apache.commons.math.optimization.GoalType goalType, double min, double max) throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException;

	double optimize(org.apache.commons.math.analysis.UnivariateRealFunction f, org.apache.commons.math.optimization.GoalType goalType, double min, double max, double startValue) throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException;

	double getResult();

	double getFunctionValue();
}

