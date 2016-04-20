package org.apache.commons.math.linear;


public interface LUDecomposition {
	org.apache.commons.math.linear.RealMatrix getL();

	org.apache.commons.math.linear.RealMatrix getU();

	org.apache.commons.math.linear.RealMatrix getP();

	int[] getPivot();

	double getDeterminant();

	org.apache.commons.math.linear.DecompositionSolver getSolver();
}

