package org.apache.commons.math.stat.regression;


public class GLSMultipleLinearRegression extends org.apache.commons.math.stat.regression.AbstractMultipleLinearRegression {
	private org.apache.commons.math.linear.RealMatrix Omega;

	private org.apache.commons.math.linear.RealMatrix OmegaInverse;

	public void newSampleData(double[] y, double[][] x, double[][] covariance) {
		validateSampleData(x, y);
		newYSampleData(y);
		newXSampleData(x);
		validateCovarianceData(x, covariance);
		newCovarianceData(covariance);
	}

	protected void newCovarianceData(double[][] omega) {
		this.Omega = new org.apache.commons.math.linear.Array2DRowRealMatrix(omega);
		this.OmegaInverse = null;
	}

	protected org.apache.commons.math.linear.RealMatrix getOmegaInverse() {
		if ((OmegaInverse) == null) {
			OmegaInverse = new org.apache.commons.math.linear.LUDecompositionImpl(Omega).getSolver().getInverse();
		} 
		return OmegaInverse;
	}

	@java.lang.Override
	protected org.apache.commons.math.linear.RealVector calculateBeta() {
		org.apache.commons.math.linear.RealMatrix OI = getOmegaInverse();
		org.apache.commons.math.linear.RealMatrix XT = X.transpose();
		org.apache.commons.math.linear.RealMatrix XTOIX = XT.multiply(OI).multiply(X);
		org.apache.commons.math.linear.RealMatrix inverse = new org.apache.commons.math.linear.LUDecompositionImpl(XTOIX).getSolver().getInverse();
		return inverse.multiply(XT).multiply(OI).operate(Y);
	}

	@java.lang.Override
	protected org.apache.commons.math.linear.RealMatrix calculateBetaVariance() {
		org.apache.commons.math.linear.RealMatrix OI = getOmegaInverse();
		org.apache.commons.math.linear.RealMatrix XTOIX = X.transpose().multiply(OI).multiply(X);
		return new org.apache.commons.math.linear.LUDecompositionImpl(XTOIX).getSolver().getInverse();
	}

	@java.lang.Override
	protected double calculateYVariance() {
		org.apache.commons.math.linear.RealVector residuals = calculateResiduals();
		double t = residuals.dotProduct(getOmegaInverse().operate(residuals));
		return t / ((X.getRowDimension()) - (X.getColumnDimension()));
	}
}

