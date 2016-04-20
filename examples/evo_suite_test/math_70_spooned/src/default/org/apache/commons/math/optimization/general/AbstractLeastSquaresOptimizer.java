package org.apache.commons.math.optimization.general;


public abstract class AbstractLeastSquaresOptimizer implements org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer {
	public static final int DEFAULT_MAX_ITERATIONS = 100;

	protected org.apache.commons.math.optimization.VectorialConvergenceChecker checker;

	protected double[][] jacobian;

	protected int cols;

	protected int rows;

	protected double[] targetValues;

	protected double[] residualsWeights;

	protected double[] point;

	protected double[] objective;

	protected double[] residuals;

	protected double cost;

	private int maxIterations;

	private int iterations;

	private int maxEvaluations;

	private int objectiveEvaluations;

	private int jacobianEvaluations;

	private org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction function;

	private org.apache.commons.math.analysis.MultivariateMatrixFunction jF;

	protected AbstractLeastSquaresOptimizer() {
		setConvergenceChecker(new org.apache.commons.math.optimization.SimpleVectorialValueChecker());
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
		return objectiveEvaluations;
	}

	public int getJacobianEvaluations() {
		return jacobianEvaluations;
	}

	public void setConvergenceChecker(org.apache.commons.math.optimization.VectorialConvergenceChecker convergenceChecker) {
		this.checker = convergenceChecker;
	}

	public org.apache.commons.math.optimization.VectorialConvergenceChecker getConvergenceChecker() {
		return checker;
	}

	protected void incrementIterationsCounter() throws org.apache.commons.math.optimization.OptimizationException {
		if ((++(iterations)) > (maxIterations)) {
			throw new org.apache.commons.math.optimization.OptimizationException(new org.apache.commons.math.MaxIterationsExceededException(maxIterations));
		} 
	}

	protected void updateJacobian() throws org.apache.commons.math.FunctionEvaluationException {
		++(jacobianEvaluations);
		jacobian = jF.value(point);
		if ((jacobian.length) != (rows)) {
			throw new org.apache.commons.math.FunctionEvaluationException(point , "dimension mismatch {0} != {1}" , jacobian.length , rows);
		} 
		for (int i = 0 ; i < (rows) ; i++) {
			final double[] ji = jacobian[i];
			final double factor = -(java.lang.Math.sqrt(residualsWeights[i]));
			for (int j = 0 ; j < (cols) ; ++j) {
				ji[j] *= factor;
			}
		}
	}

	protected void updateResidualsAndCost() throws org.apache.commons.math.FunctionEvaluationException {
		if ((++(objectiveEvaluations)) > (maxEvaluations)) {
			throw new org.apache.commons.math.FunctionEvaluationException(new org.apache.commons.math.MaxEvaluationsExceededException(maxEvaluations) , point);
		} 
		objective = function.value(point);
		if ((objective.length) != (rows)) {
			throw new org.apache.commons.math.FunctionEvaluationException(point , "dimension mismatch {0} != {1}" , objective.length , rows);
		} 
		cost = 0;
		int index = 0;
		for (int i = 0 ; i < (rows) ; i++) {
			final double residual = (targetValues[i]) - (objective[i]);
			residuals[i] = residual;
			cost += ((residualsWeights[i]) * residual) * residual;
			index += cols;
		}
		cost = java.lang.Math.sqrt(cost);
	}

	public double getRMS() {
		double criterion = 0;
		for (int i = 0 ; i < (rows) ; ++i) {
			final double residual = residuals[i];
			criterion += ((residualsWeights[i]) * residual) * residual;
		}
		return java.lang.Math.sqrt((criterion / (rows)));
	}

	public double getChiSquare() {
		double chiSquare = 0;
		for (int i = 0 ; i < (rows) ; ++i) {
			final double residual = residuals[i];
			chiSquare += (residual * residual) / (residualsWeights[i]);
		}
		return chiSquare;
	}

	public double[][] getCovariances() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		updateJacobian();
		double[][] jTj = new double[cols][cols];
		for (int i = 0 ; i < (cols) ; ++i) {
			for (int j = i ; j < (cols) ; ++j) {
				double sum = 0;
				for (int k = 0 ; k < (rows) ; ++k) {
					sum += (jacobian[k][i]) * (jacobian[k][j]);
				}
				jTj[i][j] = sum;
				jTj[j][i] = sum;
			}
		}
		try {
			org.apache.commons.math.linear.RealMatrix inverse = new org.apache.commons.math.linear.LUDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(jTj)).getSolver().getInverse();
			return inverse.getData();
		} catch (org.apache.commons.math.linear.InvalidMatrixException ime) {
			throw new org.apache.commons.math.optimization.OptimizationException("unable to compute covariances: singular problem");
		}
	}

	public double[] guessParametersErrors() throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		if ((rows) <= (cols)) {
			throw new org.apache.commons.math.optimization.OptimizationException("no degrees of freedom ({0} measurements, {1} parameters)" , rows , cols);
		} 
		double[] errors = new double[cols];
		final double c = java.lang.Math.sqrt(((getChiSquare()) / ((rows) - (cols))));
		double[][] covar = getCovariances();
		for (int i = 0 ; i < (errors.length) ; ++i) {
			errors[i] = (java.lang.Math.sqrt(covar[i][i])) * c;
		}
		return errors;
	}

	public org.apache.commons.math.optimization.VectorialPointValuePair optimize(final org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction f, final double[] target, final double[] weights, final double[] startPoint) throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		if ((target.length) != (weights.length)) {
			throw new org.apache.commons.math.optimization.OptimizationException("dimension mismatch {0} != {1}" , target.length , weights.length);
		} 
		iterations = 0;
		objectiveEvaluations = 0;
		jacobianEvaluations = 0;
		function = f;
		jF = f.jacobian();
		targetValues = target.clone();
		residualsWeights = weights.clone();
		this.point = startPoint.clone();
		this.residuals = new double[target.length];
		rows = target.length;
		cols = point.length;
		jacobian = new double[rows][cols];
		cost = java.lang.Double.POSITIVE_INFINITY;
		return doOptimize();
	}

	protected abstract org.apache.commons.math.optimization.VectorialPointValuePair doOptimize() throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException;
}

