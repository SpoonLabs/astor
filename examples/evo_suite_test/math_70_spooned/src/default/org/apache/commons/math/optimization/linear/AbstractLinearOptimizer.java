package org.apache.commons.math.optimization.linear;


public abstract class AbstractLinearOptimizer implements org.apache.commons.math.optimization.linear.LinearOptimizer {
	public static final int DEFAULT_MAX_ITERATIONS = 100;

	protected org.apache.commons.math.optimization.linear.LinearObjectiveFunction function;

	protected java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> linearConstraints;

	protected org.apache.commons.math.optimization.GoalType goal;

	protected boolean nonNegative;

	private int maxIterations;

	private int iterations;

	protected AbstractLinearOptimizer() {
		setMaxIterations(DEFAULT_MAX_ITERATIONS);
	}

	public void setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
	}

	public int getMaxIterations() {
		return maxIterations;
	}

	public int getIterations() {
		return iterations;
	}

	protected void incrementIterationsCounter() throws org.apache.commons.math.optimization.OptimizationException {
		if ((++(iterations)) > (maxIterations)) {
			throw new org.apache.commons.math.optimization.OptimizationException(new org.apache.commons.math.MaxIterationsExceededException(maxIterations));
		} 
	}

	public org.apache.commons.math.optimization.RealPointValuePair optimize(final org.apache.commons.math.optimization.linear.LinearObjectiveFunction f, final java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints, final org.apache.commons.math.optimization.GoalType goalType, final boolean restrictToNonNegative) throws org.apache.commons.math.optimization.OptimizationException {
		this.function = f;
		this.linearConstraints = constraints;
		this.goal = goalType;
		this.nonNegative = restrictToNonNegative;
		iterations = 0;
		return doOptimize();
	}

	protected abstract org.apache.commons.math.optimization.RealPointValuePair doOptimize() throws org.apache.commons.math.optimization.OptimizationException;
}

