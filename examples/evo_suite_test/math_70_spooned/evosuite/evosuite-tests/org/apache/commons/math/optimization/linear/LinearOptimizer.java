package org.apache.commons.math.optimization.linear;


public interface LinearOptimizer {
	void setMaxIterations(int maxIterations);

	int getMaxIterations();

	int getIterations();

	org.apache.commons.math.optimization.RealPointValuePair optimize(org.apache.commons.math.optimization.linear.LinearObjectiveFunction f, java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints, org.apache.commons.math.optimization.GoalType goalType, boolean restrictToNonNegative) throws org.apache.commons.math.optimization.OptimizationException;
}

