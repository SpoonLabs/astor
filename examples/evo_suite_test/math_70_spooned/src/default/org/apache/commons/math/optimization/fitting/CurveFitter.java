package org.apache.commons.math.optimization.fitting;


public class CurveFitter {
	private final org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer optimizer;

	private final java.util.List<org.apache.commons.math.optimization.fitting.WeightedObservedPoint> observations;

	public CurveFitter(final org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer optimizer) {
		this.optimizer = optimizer;
		observations = new java.util.ArrayList<org.apache.commons.math.optimization.fitting.WeightedObservedPoint>();
	}

	public void addObservedPoint(double x, double y) {
		addObservedPoint(1.0, x, y);
	}

	public void addObservedPoint(double weight, double x, double y) {
		observations.add(new org.apache.commons.math.optimization.fitting.WeightedObservedPoint(weight , x , y));
	}

	public void addObservedPoint(org.apache.commons.math.optimization.fitting.WeightedObservedPoint observed) {
		observations.add(observed);
	}

	public org.apache.commons.math.optimization.fitting.WeightedObservedPoint[] getObservations() {
		return observations.toArray(new org.apache.commons.math.optimization.fitting.WeightedObservedPoint[observations.size()]);
	}

	public void clearObservations() {
		observations.clear();
	}

	public double[] fit(final org.apache.commons.math.optimization.fitting.ParametricRealFunction f, final double[] initialGuess) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		double[] target = new double[observations.size()];
		double[] weights = new double[observations.size()];
		int i = 0;
		for (org.apache.commons.math.optimization.fitting.WeightedObservedPoint point : observations) {
			target[i] = point.getY();
			weights[i] = point.getWeight();
			++i;
		}
		org.apache.commons.math.optimization.VectorialPointValuePair optimum = optimizer.optimize(new TheoreticalValuesFunction(f), target, weights, initialGuess);
		return optimum.getPointRef();
	}

	private class TheoreticalValuesFunction implements org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction {
		private final org.apache.commons.math.optimization.fitting.ParametricRealFunction f;

		public TheoreticalValuesFunction(final org.apache.commons.math.optimization.fitting.ParametricRealFunction f) {
			this.f = f;
		}

		public org.apache.commons.math.analysis.MultivariateMatrixFunction jacobian() {
			return new org.apache.commons.math.analysis.MultivariateMatrixFunction() {
				public double[][] value(double[] point) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException {
					final double[][] jacobian = new double[observations.size()][];
					int i = 0;
					for (org.apache.commons.math.optimization.fitting.WeightedObservedPoint observed : observations) {
						jacobian[i++] = f.gradient(observed.getX(), point);
					}
					return jacobian;
				}
			};
		}

		public double[] value(double[] point) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException {
			final double[] values = new double[observations.size()];
			int i = 0;
			for (org.apache.commons.math.optimization.fitting.WeightedObservedPoint observed : observations) {
				values[i++] = f.value(observed.getX(), point);
			}
			return values;
		}
	}
}

