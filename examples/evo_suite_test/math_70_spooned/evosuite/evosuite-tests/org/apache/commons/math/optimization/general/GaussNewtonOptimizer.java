package org.apache.commons.math.optimization.general;


public class GaussNewtonOptimizer extends org.apache.commons.math.optimization.general.AbstractLeastSquaresOptimizer {
	private final boolean useLU;

	public GaussNewtonOptimizer(final boolean useLU) {
		this.useLU = useLU;
	}

	@java.lang.Override
	public org.apache.commons.math.optimization.VectorialPointValuePair doOptimize() throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.VectorialPointValuePair current = null;
		for (boolean converged = false ; !converged ; ) {
			incrementIterationsCounter();
			org.apache.commons.math.optimization.VectorialPointValuePair previous = current;
			updateResidualsAndCost();
			updateJacobian();
			current = new org.apache.commons.math.optimization.VectorialPointValuePair(point , objective);
			final double[] b = new double[cols];
			final double[][] a = new double[cols][cols];
			for (int i = 0 ; i < (rows) ; ++i) {
				final double[] grad = jacobian[i];
				final double weight = residualsWeights[i];
				final double residual = (objective[i]) - (targetValues[i]);
				final double wr = weight * residual;
				for (int j = 0 ; j < (cols) ; ++j) {
					b[j] += wr * (grad[j]);
				}
				for (int k = 0 ; k < (cols) ; ++k) {
					double[] ak = a[k];
					double wgk = weight * (grad[k]);
					for (int l = 0 ; l < (cols) ; ++l) {
						ak[l] += wgk * (grad[l]);
					}
				}
			}
			try {
				org.apache.commons.math.linear.RealMatrix mA = new org.apache.commons.math.linear.BlockRealMatrix(a);
				org.apache.commons.math.linear.DecompositionSolver solver = useLU ? new org.apache.commons.math.linear.LUDecompositionImpl(mA).getSolver() : new org.apache.commons.math.linear.QRDecompositionImpl(mA).getSolver();
				final double[] dX = solver.solve(b);
				for (int i = 0 ; i < (cols) ; ++i) {
					point[i] += dX[i];
				}
			} catch (org.apache.commons.math.linear.InvalidMatrixException e) {
				throw new org.apache.commons.math.optimization.OptimizationException("unable to solve: singular problem");
			}
			if (previous != null) {
				converged = checker.converged(getIterations(), previous, current);
			} 
		}
		return current;
	}
}

