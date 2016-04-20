package org.apache.commons.math.linear;


public interface CholeskyDecomposition {
	org.apache.commons.math.linear.RealMatrix getL();

	org.apache.commons.math.linear.RealMatrix getLT();

	double getDeterminant();

	org.apache.commons.math.linear.DecompositionSolver getSolver();
}

