package org.apache.commons.math.optimization.general;


public abstract class AbstractScalarDifferentiableOptimizer implements org.apache.commons.math.optimization.DifferentiableMultivariateRealOptimizer {
	public static final int DEFAULT_MAX_ITERATIONS = 100;

	protected org.apache.commons.math.optimization.RealConvergenceChecker checker;

	protected org.apache.commons.math.optimization.GoalType goal;

	protected double[] point;

	private int maxIterations;

	private int iterations;

	private int maxEvaluations;

	private int evaluations;

	private int gradientEvaluations;

	private org.apache.commons.math.analysis.DifferentiableMultivariateRealFunction function;

	private org.apache.commons.math.analysis.MultivariateVectorialFunction gradient;

	protected AbstractScalarDifferentiableOptimizer() {
		setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker());
		setMaxIterations(DEFAULT_MAX_ITERATIONS);
		setMaxEvaluations(java.lang.Integer.MAX_VALUE);
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

	public void setMaxEvaluations(int maxEvaluations) {
		this.maxEvaluations = maxEvaluations;
	}

	public int getMaxEvaluations() {
		return maxEvaluations;
	}

	public int getEvaluations() {
		return evaluations;
	}

	public int getGradientEvaluations() {
		return gradientEvaluations;
	}

	public void setConvergenceChecker(org.apache.commons.math.optimization.RealConvergenceChecker convergenceChecker) {
		this.checker = convergenceChecker;
	}

	public org.apache.commons.math.optimization.RealConvergenceChecker getConvergenceChecker() {
		return checker;
	}

	protected void incrementIterationsCounter() throws org.apache.commons.math.optimization.OptimizationException {
		if ((++(iterations)) > (maxIterations)) {
			throw new org.apache.commons.math.optimization.OptimizationException(new org.apache.commons.math.MaxIterationsExceededException(maxIterations));
		} 
	}

	protected double[] computeObjectiveGradient(final double[] evaluationPoint) throws org.apache.commons.math.FunctionEvaluationException {
		++(gradientEvaluations);
		return gradient.value(evaluationPoint);
	}

	protected double computeObjectiveValue(final double[] evaluationPoint) throws org.apache.commons.math.FunctionEvaluationException {
		if ((++(evaluations)) > (maxEvaluations)) {
			throw new org.apache.commons.math.FunctionEvaluationException(new org.apache.commons.math.MaxEvaluationsExceededException(maxEvaluations) , evaluationPoint);
		} 
		return function.value(evaluationPoint);
	}

	public org.apache.commons.math.optimization.RealPointValuePair optimize(final org.apache.commons.math.analysis.DifferentiableMultivariateRealFunction f, final org.apache.commons.math.optimization.GoalType goalType, final double[] startPoint) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		iterations = 0;
		evaluations = 0;
		gradientEvaluations = 0;
		function = f;
		gradient = f.gradient();
		goal = goalType;
		point = startPoint.clone();
		return doOptimize();
	}

	protected abstract org.apache.commons.math.optimization.RealPointValuePair doOptimize() throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException;
}

