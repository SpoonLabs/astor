package org.apache.commons.math.optimization;


public class MultiStartUnivariateRealOptimizer implements org.apache.commons.math.optimization.UnivariateRealOptimizer {
	private static final long serialVersionUID = 5983375963110961019L;

	private final org.apache.commons.math.optimization.UnivariateRealOptimizer optimizer;

	private int maxIterations;

	private int maxEvaluations;

	private int totalIterations;

	private int totalEvaluations;

	private int starts;

	private org.apache.commons.math.random.RandomGenerator generator;

	private double[] optima;

	private double[] optimaValues;

	public MultiStartUnivariateRealOptimizer(final org.apache.commons.math.optimization.UnivariateRealOptimizer optimizer ,final int starts ,final org.apache.commons.math.random.RandomGenerator generator) {
		this.optimizer = optimizer;
		this.totalIterations = 0;
		this.starts = starts;
		this.generator = generator;
		this.optima = null;
		setMaximalIterationCount(java.lang.Integer.MAX_VALUE);
		setMaxEvaluations(java.lang.Integer.MAX_VALUE);
	}

	public double getFunctionValue() {
		return optimizer.getFunctionValue();
	}

	public double getResult() {
		return optimizer.getResult();
	}

	public double getAbsoluteAccuracy() {
		return optimizer.getAbsoluteAccuracy();
	}

	public int getIterationCount() {
		return totalIterations;
	}

	public int getMaximalIterationCount() {
		return maxIterations;
	}

	public int getMaxEvaluations() {
		return maxEvaluations;
	}

	public int getEvaluations() {
		return totalEvaluations;
	}

	public double getRelativeAccuracy() {
		return optimizer.getRelativeAccuracy();
	}

	public void resetAbsoluteAccuracy() {
		optimizer.resetAbsoluteAccuracy();
	}

	public void resetMaximalIterationCount() {
		optimizer.resetMaximalIterationCount();
	}

	public void resetRelativeAccuracy() {
		optimizer.resetRelativeAccuracy();
	}

	public void setAbsoluteAccuracy(double accuracy) {
		optimizer.setAbsoluteAccuracy(accuracy);
	}

	public void setMaximalIterationCount(int count) {
		this.maxIterations = count;
	}

	public void setMaxEvaluations(int maxEvaluations) {
		this.maxEvaluations = maxEvaluations;
	}

	public void setRelativeAccuracy(double accuracy) {
		optimizer.setRelativeAccuracy(accuracy);
	}

	public double[] getOptima() throws java.lang.IllegalStateException {
		if ((optima) == null) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalStateException("no optimum computed yet");
		} 
		return optima.clone();
	}

	public double[] getOptimaValues() throws java.lang.IllegalStateException {
		if ((optimaValues) == null) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalStateException("no optimum computed yet");
		} 
		return optimaValues.clone();
	}

	public double optimize(final org.apache.commons.math.analysis.UnivariateRealFunction f, final org.apache.commons.math.optimization.GoalType goalType, final double min, final double max) throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		optima = new double[starts];
		optimaValues = new double[starts];
		totalIterations = 0;
		totalEvaluations = 0;
		for (int i = 0 ; i < (starts) ; ++i) {
			try {
				optimizer.setMaximalIterationCount(((maxIterations) - (totalIterations)));
				optimizer.setMaxEvaluations(((maxEvaluations) - (totalEvaluations)));
				final double bound1 = i == 0 ? min : min + ((generator.nextDouble()) * (max - min));
				final double bound2 = i == 0 ? max : min + ((generator.nextDouble()) * (max - min));
				optima[i] = optimizer.optimize(f, goalType, java.lang.Math.min(bound1, bound2), java.lang.Math.max(bound1, bound2));
				optimaValues[i] = optimizer.getFunctionValue();
			} catch (org.apache.commons.math.FunctionEvaluationException fee) {
				optima[i] = java.lang.Double.NaN;
				optimaValues[i] = java.lang.Double.NaN;
			} catch (org.apache.commons.math.ConvergenceException ce) {
				optima[i] = java.lang.Double.NaN;
				optimaValues[i] = java.lang.Double.NaN;
			}
			totalIterations += optimizer.getIterationCount();
			totalEvaluations += optimizer.getEvaluations();
		}
		int lastNaN = optima.length;
		for (int i = 0 ; i < lastNaN ; ++i) {
			if (java.lang.Double.isNaN(optima[i])) {
				optima[i] = optima[--lastNaN];
				optima[(lastNaN + 1)] = java.lang.Double.NaN;
				optimaValues[i] = optimaValues[--lastNaN];
				optimaValues[(lastNaN + 1)] = java.lang.Double.NaN;
			} 
		}
		double currX = optima[0];
		double currY = optimaValues[0];
		for (int j = 1 ; j < lastNaN ; ++j) {
			final double prevY = currY;
			currX = optima[j];
			currY = optimaValues[j];
			if ((goalType == (org.apache.commons.math.optimization.GoalType.MAXIMIZE)) ^ (currY < prevY)) {
				int i = j - 1;
				double mIX = optima[i];
				double mIY = optimaValues[i];
				while ((i >= 0) && ((goalType == (org.apache.commons.math.optimization.GoalType.MAXIMIZE)) ^ (currY < mIY))) {
					optima[(i + 1)] = mIX;
					optimaValues[(i + 1)] = mIY;
					if ((i--) != 0) {
						mIX = optima[i];
						mIY = optimaValues[i];
					} else {
						mIX = java.lang.Double.NaN;
						mIY = java.lang.Double.NaN;
					}
				}
				optima[(i + 1)] = currX;
				optimaValues[(i + 1)] = currY;
				currX = optima[j];
				currY = optimaValues[j];
			} 
		}
		if (java.lang.Double.isNaN(optima[0])) {
			throw new org.apache.commons.math.optimization.OptimizationException("none of the {0} start points lead to convergence" , starts);
		} 
		return optima[0];
	}

	public double optimize(final org.apache.commons.math.analysis.UnivariateRealFunction f, final org.apache.commons.math.optimization.GoalType goalType, final double min, final double max, final double startValue) throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		return optimize(f, goalType, min, max);
	}
}

