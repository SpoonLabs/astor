package org.apache.commons.math.optimization.direct;


public abstract class DirectSearchOptimizer implements org.apache.commons.math.optimization.MultivariateRealOptimizer {
	private static final java.lang.String EQUAL_VERTICES_MESSAGE = "equal vertices {0} and {1} in simplex configuration";

	private static final java.lang.String DIMENSION_MISMATCH_MESSAGE = "dimension mismatch {0} != {1}";

	protected org.apache.commons.math.optimization.RealPointValuePair[] simplex;

	private org.apache.commons.math.analysis.MultivariateRealFunction f;

	private org.apache.commons.math.optimization.RealConvergenceChecker checker;

	private int maxIterations;

	private int iterations;

	private int maxEvaluations;

	private int evaluations;

	private double[][] startConfiguration;

	protected DirectSearchOptimizer() {
		setConvergenceChecker(new org.apache.commons.math.optimization.SimpleScalarValueChecker());
		setMaxIterations(java.lang.Integer.MAX_VALUE);
		setMaxEvaluations(java.lang.Integer.MAX_VALUE);
	}

	public void setStartConfiguration(final double[] steps) throws java.lang.IllegalArgumentException {
		final int n = steps.length;
		startConfiguration = new double[n][n];
		for (int i = 0 ; i < n ; ++i) {
			final double[] vertexI = startConfiguration[i];
			for (int j = 0 ; j < (i + 1) ; ++j) {
				if ((steps[j]) == 0.0) {
					throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(EQUAL_VERTICES_MESSAGE, j, (j + 1));
				} 
				java.lang.System.arraycopy(steps, 0, vertexI, 0, (j + 1));
			}
		}
	}

	public void setStartConfiguration(final double[][] referenceSimplex) throws java.lang.IllegalArgumentException {
		final int n = (referenceSimplex.length) - 1;
		if (n < 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("simplex must contain at least one point");
		} 
		startConfiguration = new double[n][n];
		final double[] ref0 = referenceSimplex[0];
		for (int i = 0 ; i < (n + 1) ; ++i) {
			final double[] refI = referenceSimplex[i];
			if ((refI.length) != n) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(DIMENSION_MISMATCH_MESSAGE, refI.length, n);
			} 
			for (int j = 0 ; j < i ; ++j) {
				final double[] refJ = referenceSimplex[j];
				boolean allEquals = true;
				for (int k = 0 ; k < n ; ++k) {
					if ((refI[k]) != (refJ[k])) {
						allEquals = false;
						break;
					} 
				}
				if (allEquals) {
					throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(EQUAL_VERTICES_MESSAGE, i, j);
				} 
			}
			if (i > 0) {
				final double[] confI = startConfiguration[(i - 1)];
				for (int k = 0 ; k < n ; ++k) {
					confI[k] = (refI[k]) - (ref0[k]);
				}
			} 
		}
	}

	public void setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
	}

	public int getMaxIterations() {
		return maxIterations;
	}

	public void setMaxEvaluations(int maxEvaluations) {
		this.maxEvaluations = maxEvaluations;
	}

	public int getMaxEvaluations() {
		return maxEvaluations;
	}

	public int getIterations() {
		return iterations;
	}

	public int getEvaluations() {
		return evaluations;
	}

	public void setConvergenceChecker(org.apache.commons.math.optimization.RealConvergenceChecker convergenceChecker) {
		this.checker = convergenceChecker;
	}

	public org.apache.commons.math.optimization.RealConvergenceChecker getConvergenceChecker() {
		return checker;
	}

	public org.apache.commons.math.optimization.RealPointValuePair optimize(final org.apache.commons.math.analysis.MultivariateRealFunction function, final org.apache.commons.math.optimization.GoalType goalType, final double[] startPoint) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		if ((startConfiguration) == null) {
			final double[] unit = new double[startPoint.length];
			java.util.Arrays.fill(unit, 1.0);
			setStartConfiguration(unit);
		} 
		this.f = function;
		final java.util.Comparator<org.apache.commons.math.optimization.RealPointValuePair> comparator = new java.util.Comparator<org.apache.commons.math.optimization.RealPointValuePair>() {
			public int compare(final org.apache.commons.math.optimization.RealPointValuePair o1, final org.apache.commons.math.optimization.RealPointValuePair o2) {
				final double v1 = o1.getValue();
				final double v2 = o2.getValue();
				return goalType == (org.apache.commons.math.optimization.GoalType.MINIMIZE) ? java.lang.Double.compare(v1, v2) : java.lang.Double.compare(v2, v1);
			}
		};
		iterations = 0;
		evaluations = 0;
		buildSimplex(startPoint);
		evaluateSimplex(comparator);
		org.apache.commons.math.optimization.RealPointValuePair[] previous = new org.apache.commons.math.optimization.RealPointValuePair[simplex.length];
		while (true) {
			if ((iterations) > 0) {
				boolean converged = true;
				for (int i = 0 ; i < (simplex.length) ; ++i) {
					converged &= checker.converged(iterations, previous[i], simplex[i]);
				}
				if (converged) {
					return simplex[0];
				} 
			} 
			java.lang.System.arraycopy(simplex, 0, previous, 0, simplex.length);
			iterateSimplex(comparator);
		}
	}

	protected void incrementIterationsCounter() throws org.apache.commons.math.optimization.OptimizationException {
		if ((++(iterations)) > (maxIterations)) {
			throw new org.apache.commons.math.optimization.OptimizationException(new org.apache.commons.math.MaxIterationsExceededException(maxIterations));
		} 
	}

	protected abstract void iterateSimplex(final java.util.Comparator<org.apache.commons.math.optimization.RealPointValuePair> comparator) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException;

	protected double evaluate(final double[] x) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException {
		if ((++(evaluations)) > (maxEvaluations)) {
			throw new org.apache.commons.math.FunctionEvaluationException(new org.apache.commons.math.MaxEvaluationsExceededException(maxEvaluations) , x);
		} 
		return f.value(x);
	}

	private void buildSimplex(final double[] startPoint) throws java.lang.IllegalArgumentException {
		final int n = startPoint.length;
		if (n != (startConfiguration.length)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(DIMENSION_MISMATCH_MESSAGE, n, startConfiguration.length);
		} 
		simplex = new org.apache.commons.math.optimization.RealPointValuePair[n + 1];
		simplex[0] = new org.apache.commons.math.optimization.RealPointValuePair(startPoint , java.lang.Double.NaN);
		for (int i = 0 ; i < n ; ++i) {
			final double[] confI = startConfiguration[i];
			final double[] vertexI = new double[n];
			for (int k = 0 ; k < n ; ++k) {
				vertexI[k] = (startPoint[k]) + (confI[k]);
			}
			simplex[(i + 1)] = new org.apache.commons.math.optimization.RealPointValuePair(vertexI , java.lang.Double.NaN);
		}
	}

	protected void evaluateSimplex(final java.util.Comparator<org.apache.commons.math.optimization.RealPointValuePair> comparator) throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		for (int i = 0 ; i < (simplex.length) ; ++i) {
			final org.apache.commons.math.optimization.RealPointValuePair vertex = simplex[i];
			final double[] point = vertex.getPointRef();
			if (java.lang.Double.isNaN(vertex.getValue())) {
				simplex[i] = new org.apache.commons.math.optimization.RealPointValuePair(point , evaluate(point) , false);
			} 
		}
		java.util.Arrays.sort(simplex, comparator);
	}

	protected void replaceWorstPoint(org.apache.commons.math.optimization.RealPointValuePair pointValuePair, final java.util.Comparator<org.apache.commons.math.optimization.RealPointValuePair> comparator) {
		int n = (simplex.length) - 1;
		for (int i = 0 ; i < n ; ++i) {
			if ((comparator.compare(simplex[i], pointValuePair)) > 0) {
				org.apache.commons.math.optimization.RealPointValuePair tmp = simplex[i];
				simplex[i] = pointValuePair;
				pointValuePair = tmp;
			} 
		}
		simplex[n] = pointValuePair;
	}
}

