package org.apache.commons.math.stat.regression;


public class OLSMultipleLinearRegression extends org.apache.commons.math.stat.regression.AbstractMultipleLinearRegression {
	private org.apache.commons.math.linear.QRDecomposition qr = null;

	public void newSampleData(double[] y, double[][] x) {
		validateSampleData(x, y);
		newYSampleData(y);
		newXSampleData(x);
	}

	@java.lang.Override
	public void newSampleData(double[] data, int nobs, int nvars) {
		super.newSampleData(data, nobs, nvars);
		qr = new org.apache.commons.math.linear.QRDecompositionImpl(X);
	}

	public org.apache.commons.math.linear.RealMatrix calculateHat() {
		org.apache.commons.math.linear.RealMatrix Q = qr.getQ();
		final int p = qr.getR().getColumnDimension();
		final int n = Q.getColumnDimension();
		org.apache.commons.math.linear.Array2DRowRealMatrix augI = new org.apache.commons.math.linear.Array2DRowRealMatrix(n , n);
		double[][] augIData = augI.getDataRef();
		for (int i = 0 ; i < n ; i++) {
			for (int j = 0 ; j < n ; j++) {
				if ((i == j) && (i < p)) {
					augIData[i][j] = 1.0;
				} else {
					augIData[i][j] = 0.0;
				}
			}
		}
		return Q.multiply(augI).multiply(Q.transpose());
	}

	@java.lang.Override
	protected void newXSampleData(double[][] x) {
		this.X = new org.apache.commons.math.linear.Array2DRowRealMatrix(x);
		qr = new org.apache.commons.math.linear.QRDecompositionImpl(X);
	}

	@java.lang.Override
	protected org.apache.commons.math.linear.RealVector calculateBeta() {
		return qr.getSolver().solve(Y);
	}

	@java.lang.Override
	protected org.apache.commons.math.linear.RealMatrix calculateBetaVariance() {
		int p = X.getColumnDimension();
		org.apache.commons.math.linear.RealMatrix Raug = qr.getR().getSubMatrix(0, (p - 1), 0, (p - 1));
		org.apache.commons.math.linear.RealMatrix Rinv = new org.apache.commons.math.linear.LUDecompositionImpl(Raug).getSolver().getInverse();
		return Rinv.multiply(Rinv.transpose());
	}

	@java.lang.Override
	protected double calculateYVariance() {
		org.apache.commons.math.linear.RealVector residuals = calculateResiduals();
		return (residuals.dotProduct(residuals)) / ((X.getRowDimension()) - (X.getColumnDimension()));
	}
}

