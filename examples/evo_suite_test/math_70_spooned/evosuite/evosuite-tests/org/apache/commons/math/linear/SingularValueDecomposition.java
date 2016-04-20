package org.apache.commons.math.linear;


public interface SingularValueDecomposition {
	org.apache.commons.math.linear.RealMatrix getU();

	org.apache.commons.math.linear.RealMatrix getUT();

	org.apache.commons.math.linear.RealMatrix getS();

	double[] getSingularValues();

	org.apache.commons.math.linear.RealMatrix getV();

	org.apache.commons.math.linear.RealMatrix getVT();

	org.apache.commons.math.linear.RealMatrix getCovariance(double minSingularValue) throws java.lang.IllegalArgumentException;

	double getNorm();

	double getConditionNumber();

	int getRank();

	org.apache.commons.math.linear.DecompositionSolver getSolver();
}

