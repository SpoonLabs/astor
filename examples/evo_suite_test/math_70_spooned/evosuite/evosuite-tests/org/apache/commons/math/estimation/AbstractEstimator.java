package org.apache.commons.math.estimation;


@java.lang.Deprecated
public abstract class AbstractEstimator implements org.apache.commons.math.estimation.Estimator {
	public static final int DEFAULT_MAX_COST_EVALUATIONS = 100;

	protected org.apache.commons.math.estimation.WeightedMeasurement[] measurements;

	protected org.apache.commons.math.estimation.EstimatedParameter[] parameters;

	protected double[] jacobian;

	protected int cols;

	protected int rows;

	protected double[] residuals;

	protected double cost;

	private int maxCostEval;

	private int costEvaluations;

	private int jacobianEvaluations;

	protected AbstractEstimator() {
		setMaxCostEval(DEFAULT_MAX_COST_EVALUATIONS);
	}

	public final void setMaxCostEval(int maxCostEval) {
		this.maxCostEval = maxCostEval;
	}

	public final int getCostEvaluations() {
		return costEvaluations;
	}

	public final int getJacobianEvaluations() {
		return jacobianEvaluations;
	}

	protected void updateJacobian() {
		incrementJacobianEvaluationsCounter();
		java.util.Arrays.fill(jacobian, 0);
		int index = 0;
		for (int i = 0 ; i < (rows) ; i++) {
			org.apache.commons.math.estimation.WeightedMeasurement wm = measurements[i];
			double factor = -(java.lang.Math.sqrt(wm.getWeight()));
			for (int j = 0 ; j < (cols) ; ++j) {
				jacobian[index++] = factor * (wm.getPartial(parameters[j]));
			}
		}
	}

	protected final void incrementJacobianEvaluationsCounter() {
		++(jacobianEvaluations);
	}

	protected void updateResidualsAndCost() throws org.apache.commons.math.estimation.EstimationException {
		if ((++(costEvaluations)) > (maxCostEval)) {
			throw new org.apache.commons.math.estimation.EstimationException("maximal number of evaluations exceeded ({0})" , maxCostEval);
		} 
		cost = 0;
		int index = 0;
		for (int i = 0 ; i < (rows) ; i++ , index += cols) {
			org.apache.commons.math.estimation.WeightedMeasurement wm = measurements[i];
			double residual = wm.getResidual();
			residuals[i] = (java.lang.Math.sqrt(wm.getWeight())) * residual;
			cost += ((wm.getWeight()) * residual) * residual;
		}
		cost = java.lang.Math.sqrt(cost);
	}

	public double getRMS(org.apache.commons.math.estimation.EstimationProblem problem) {
		org.apache.commons.math.estimation.WeightedMeasurement[] wm = problem.getMeasurements();
		double criterion = 0;
		for (int i = 0 ; i < (wm.length) ; ++i) {
			double residual = wm[i].getResidual();
			criterion += ((wm[i].getWeight()) * residual) * residual;
		}
		return java.lang.Math.sqrt((criterion / (wm.length)));
	}

	public double getChiSquare(org.apache.commons.math.estimation.EstimationProblem problem) {
		org.apache.commons.math.estimation.WeightedMeasurement[] wm = problem.getMeasurements();
		double chiSquare = 0;
		for (int i = 0 ; i < (wm.length) ; ++i) {
			double residual = wm[i].getResidual();
			chiSquare += (residual * residual) / (wm[i].getWeight());
		}
		return chiSquare;
	}

	public double[][] getCovariances(org.apache.commons.math.estimation.EstimationProblem problem) throws org.apache.commons.math.estimation.EstimationException {
		updateJacobian();
		final int n = problem.getMeasurements().length;
		final int m = problem.getUnboundParameters().length;
		final int max = m * n;
		double[][] jTj = new double[m][m];
		for (int i = 0 ; i < m ; ++i) {
			for (int j = i ; j < m ; ++j) {
				double sum = 0;
				for (int k = 0 ; k < max ; k += m) {
					sum += (jacobian[(k + i)]) * (jacobian[(k + j)]);
				}
				jTj[i][j] = sum;
				jTj[j][i] = sum;
			}
		}
		try {
			org.apache.commons.math.linear.RealMatrix inverse = new org.apache.commons.math.linear.LUDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(jTj)).getSolver().getInverse();
			return inverse.getData();
		} catch (org.apache.commons.math.linear.InvalidMatrixException ime) {
			throw new org.apache.commons.math.estimation.EstimationException("unable to compute covariances: singular problem");
		}
	}

	public double[] guessParametersErrors(org.apache.commons.math.estimation.EstimationProblem problem) throws org.apache.commons.math.estimation.EstimationException {
		int m = problem.getMeasurements().length;
		int p = problem.getUnboundParameters().length;
		if (m <= p) {
			throw new org.apache.commons.math.estimation.EstimationException("no degrees of freedom ({0} measurements, {1} parameters)" , m , p);
		} 
		double[] errors = new double[problem.getUnboundParameters().length];
		final double c = java.lang.Math.sqrt(((getChiSquare(problem)) / (m - p)));
		double[][] covar = getCovariances(problem);
		for (int i = 0 ; i < (errors.length) ; ++i) {
			errors[i] = (java.lang.Math.sqrt(covar[i][i])) * c;
		}
		return errors;
	}

	protected void initializeEstimate(org.apache.commons.math.estimation.EstimationProblem problem) {
		costEvaluations = 0;
		jacobianEvaluations = 0;
		measurements = problem.getMeasurements();
		parameters = problem.getUnboundParameters();
		rows = measurements.length;
		cols = parameters.length;
		jacobian = new double[(rows) * (cols)];
		residuals = new double[rows];
		cost = java.lang.Double.POSITIVE_INFINITY;
	}

	public abstract void estimate(org.apache.commons.math.estimation.EstimationProblem problem) throws org.apache.commons.math.estimation.EstimationException;
}

