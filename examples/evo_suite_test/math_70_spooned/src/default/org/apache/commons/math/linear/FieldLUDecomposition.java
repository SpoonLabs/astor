package org.apache.commons.math.linear;


public interface FieldLUDecomposition<T extends org.apache.commons.math.FieldElement<T>> {
	org.apache.commons.math.linear.FieldMatrix<T> getL();

	org.apache.commons.math.linear.FieldMatrix<T> getU();

	org.apache.commons.math.linear.FieldMatrix<T> getP();

	int[] getPivot();

	T getDeterminant();

	org.apache.commons.math.linear.FieldDecompositionSolver<T> getSolver();
}

