package org.apache.commons.math.linear;


public interface EigenDecomposition {
	org.apache.commons.math.linear.RealMatrix getV();

	org.apache.commons.math.linear.RealMatrix getD();

	org.apache.commons.math.linear.RealMatrix getVT();

	double[] getRealEigenvalues();

	double getRealEigenvalue(int i);

	double[] getImagEigenvalues();

	double getImagEigenvalue(int i);

	org.apache.commons.math.linear.RealVector getEigenvector(int i);

	double getDeterminant();

	org.apache.commons.math.linear.DecompositionSolver getSolver();
}

