package org.apache.commons.math.optimization;


public class MultiStartDifferentiableMultivariateVectorialOptimizer implements org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer {
	private static final long serialVersionUID = 9206382258980561530L;

	private final org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer optimizer;

	private int maxIterations;

	private int totalIterations;

	private int maxEvaluations;

	private int totalEvaluations;

	private int totalJacobianEvaluations;

	private int starts;

	private org.apache.commons.math.random.RandomVectorGenerator generator;

	private org.apache.commons.math.optimization.VectorialPointValuePair[] optima;

	public MultiStartDifferentiableMultivariateVectorialOptimizer(final org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer optimizer ,final int starts ,final org.apache.commons.math.random.RandomVectorGenerator generator) {
		this.optimizer = optimizer;
		this.totalIterations = 0;
		this.totalEvaluations = 0;
		this.totalJacobianEvaluations = 0;
		this.starts = starts;
		this.generator = generator;
		this.optima = null;
		setMaxIterations(java.lang.Integer.MAX_VALUE);
		setMaxEvaluations(java.lang.Integer.MAX_VALUE);
	}

	public org.apache.commons.math.optimization.VectorialPointValuePair[] getOptima() throws java.lang.IllegalStateException {
		if ((optima) == null) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalStateException("no optimum computed yet");
		} 
		return optima.clone();
	}

	public void setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
	}

	public int getMaxIterations() {
		return maxIterations;
	}

	public int getIterations() {
		return totalIterations;
	}

	public void setMaxEvaluations(int maxEvaluations) {
		this.maxEvaluations = maxEvaluations;
	}

	public int getMaxEvaluations() {
		return maxEvaluations;
	}

	public int getEvaluations() {
		return totalEvaluations;
	}

	public int getJacobianEvaluations() {
		return totalJacobianEvaluations;
	}

	public void setConvergenceChecker(org.apache.commons.math.optimization.VectorialConvergenceChecker checker) {
		optimizer.setConvergenceChecker(checker);
	}

	public org.apache.commons.math.optimization.VectorialConvergenceChecker getConvergenceChecker() {
		return optimizer.getConvergenceChecker();
	}

	public org.apache.commons.math.optimization.VectorialPointValuePair optimize(final org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction f, final double[] target, final double[] weights, final double[] startPoint) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		optima = new org.apache.commons.math.optimization.VectorialPointValuePair[starts];
		totalIterations = 0;
		totalEvaluations = 0;
		totalJacobianEvaluations = 0;
		for (int i = 0 ; i < (starts) ; ++i) {
			try {
				optimizer.setMaxIterations(((maxIterations) - (totalIterations)));
				optimizer.setMaxEvaluations(((maxEvaluations) - (totalEvaluations)));
				optima[i] = optimizer.optimize(f, target, weights, (i == 0 ? startPoint : generator.nextVector()));
			} catch (org.apache.commons.math.FunctionEvaluationException fee) {
				optima[i] = null;
			} catch (org.apache.commons.math.optimization.OptimizationException oe) {
				optima[i] = null;
			}
			totalIterations += optimizer.getIterations();
			totalEvaluations += optimizer.getEvaluations();
			totalJacobianEvaluations += optimizer.getJacobianEvaluations();
		}
		java.util.Arrays.sort(optima, new java.util.Comparator<org.apache.commons.math.optimization.VectorialPointValuePair>() {
			public int compare(final org.apache.commons.math.optimization.VectorialPointValuePair o1, final org.apache.commons.math.optimization.VectorialPointValuePair o2) {
				if (o1 == null) {
					return o2 == null ? 0 : +1;
				} else if (o2 == null) {
					return -1;
				} 
				return java.lang.Double.compare(weightedResidual(o1), weightedResidual(o2));
			}

			private double weightedResidual(final org.apache.commons.math.optimization.VectorialPointValuePair pv) {
				final double[] value = pv.getValueRef();
				double sum = 0;
				for (int i = 0 ; i < (value.length) ; ++i) {
					final double ri = (value[i]) - (target[i]);
					sum += ((weights[i]) * ri) * ri;
				}
				return sum;
			}
		});
		if ((optima[0]) == null) {
			throw new org.apache.commons.math.optimization.OptimizationException("none of the {0} start points lead to convergence" , starts);
		} 
		return optima[0];
	}
}

