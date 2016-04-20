package org.apache.commons.math.linear;


@java.lang.Deprecated
public interface BigMatrix extends org.apache.commons.math.linear.AnyMatrix {
	org.apache.commons.math.linear.BigMatrix copy();

	org.apache.commons.math.linear.BigMatrix add(org.apache.commons.math.linear.BigMatrix m) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.BigMatrix subtract(org.apache.commons.math.linear.BigMatrix m) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.BigMatrix scalarAdd(java.math.BigDecimal d);

	org.apache.commons.math.linear.BigMatrix scalarMultiply(java.math.BigDecimal d);

	org.apache.commons.math.linear.BigMatrix multiply(org.apache.commons.math.linear.BigMatrix m) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.BigMatrix preMultiply(org.apache.commons.math.linear.BigMatrix m) throws java.lang.IllegalArgumentException;

	java.math.BigDecimal[][] getData();

	double[][] getDataAsDoubleArray();

	int getRoundingMode();

	java.math.BigDecimal getNorm();

	org.apache.commons.math.linear.BigMatrix getSubMatrix(int startRow, int endRow, int startColumn, int endColumn) throws org.apache.commons.math.linear.MatrixIndexException;

	org.apache.commons.math.linear.BigMatrix getSubMatrix(int[] selectedRows, int[] selectedColumns) throws org.apache.commons.math.linear.MatrixIndexException;

	org.apache.commons.math.linear.BigMatrix getRowMatrix(int row) throws org.apache.commons.math.linear.MatrixIndexException;

	org.apache.commons.math.linear.BigMatrix getColumnMatrix(int column) throws org.apache.commons.math.linear.MatrixIndexException;

	java.math.BigDecimal[] getRow(int row) throws org.apache.commons.math.linear.MatrixIndexException;

	double[] getRowAsDoubleArray(int row) throws org.apache.commons.math.linear.MatrixIndexException;

	java.math.BigDecimal[] getColumn(int col) throws org.apache.commons.math.linear.MatrixIndexException;

	double[] getColumnAsDoubleArray(int col) throws org.apache.commons.math.linear.MatrixIndexException;

	java.math.BigDecimal getEntry(int row, int column) throws org.apache.commons.math.linear.MatrixIndexException;

	double getEntryAsDouble(int row, int column) throws org.apache.commons.math.linear.MatrixIndexException;

	org.apache.commons.math.linear.BigMatrix transpose();

	org.apache.commons.math.linear.BigMatrix inverse() throws org.apache.commons.math.linear.InvalidMatrixException;

	java.math.BigDecimal getDeterminant() throws org.apache.commons.math.linear.InvalidMatrixException;

	java.math.BigDecimal getTrace();

	java.math.BigDecimal[] operate(java.math.BigDecimal[] v) throws java.lang.IllegalArgumentException;

	java.math.BigDecimal[] preMultiply(java.math.BigDecimal[] v) throws java.lang.IllegalArgumentException;

	java.math.BigDecimal[] solve(java.math.BigDecimal[] b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException;

	org.apache.commons.math.linear.BigMatrix solve(org.apache.commons.math.linear.BigMatrix b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException;
}

