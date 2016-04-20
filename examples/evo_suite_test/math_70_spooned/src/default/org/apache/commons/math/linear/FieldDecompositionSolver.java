package org.apache.commons.math.linear;


public interface FieldDecompositionSolver<T extends org.apache.commons.math.FieldElement<T>> {
	T[] solve(final T[] b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException;

	org.apache.commons.math.linear.FieldVector<T> solve(final org.apache.commons.math.linear.FieldVector<T> b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException;

	org.apache.commons.math.linear.FieldMatrix<T> solve(final org.apache.commons.math.linear.FieldMatrix<T> b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException;

	boolean isNonSingular();

	org.apache.commons.math.linear.FieldMatrix<T> getInverse() throws org.apache.commons.math.linear.InvalidMatrixException;
}

