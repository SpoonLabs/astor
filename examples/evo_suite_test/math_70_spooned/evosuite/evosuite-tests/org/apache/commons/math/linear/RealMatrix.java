package org.apache.commons.math.linear;


public interface RealMatrix extends org.apache.commons.math.linear.AnyMatrix {
	org.apache.commons.math.linear.RealMatrix createMatrix(final int rowDimension, final int columnDimension);

	org.apache.commons.math.linear.RealMatrix copy();

	org.apache.commons.math.linear.RealMatrix add(org.apache.commons.math.linear.RealMatrix m) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.RealMatrix subtract(org.apache.commons.math.linear.RealMatrix m) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.RealMatrix scalarAdd(double d);

	org.apache.commons.math.linear.RealMatrix scalarMultiply(double d);

	org.apache.commons.math.linear.RealMatrix multiply(org.apache.commons.math.linear.RealMatrix m) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.RealMatrix preMultiply(org.apache.commons.math.linear.RealMatrix m) throws java.lang.IllegalArgumentException;

	double[][] getData();

	double getNorm();

	double getFrobeniusNorm();

	org.apache.commons.math.linear.RealMatrix getSubMatrix(int startRow, int endRow, int startColumn, int endColumn) throws org.apache.commons.math.linear.MatrixIndexException;

	org.apache.commons.math.linear.RealMatrix getSubMatrix(int[] selectedRows, int[] selectedColumns) throws org.apache.commons.math.linear.MatrixIndexException;

	void copySubMatrix(int startRow, int endRow, int startColumn, int endColumn, double[][] destination) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.MatrixIndexException;

	void copySubMatrix(int[] selectedRows, int[] selectedColumns, double[][] destination) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.MatrixIndexException;

	void setSubMatrix(double[][] subMatrix, int row, int column) throws org.apache.commons.math.linear.MatrixIndexException;

	org.apache.commons.math.linear.RealMatrix getRowMatrix(int row) throws org.apache.commons.math.linear.MatrixIndexException;

	void setRowMatrix(int row, org.apache.commons.math.linear.RealMatrix matrix) throws org.apache.commons.math.linear.InvalidMatrixException, org.apache.commons.math.linear.MatrixIndexException;

	org.apache.commons.math.linear.RealMatrix getColumnMatrix(int column) throws org.apache.commons.math.linear.MatrixIndexException;

	void setColumnMatrix(int column, org.apache.commons.math.linear.RealMatrix matrix) throws org.apache.commons.math.linear.InvalidMatrixException, org.apache.commons.math.linear.MatrixIndexException;

	org.apache.commons.math.linear.RealVector getRowVector(int row) throws org.apache.commons.math.linear.MatrixIndexException;

	void setRowVector(int row, org.apache.commons.math.linear.RealVector vector) throws org.apache.commons.math.linear.InvalidMatrixException, org.apache.commons.math.linear.MatrixIndexException;

	org.apache.commons.math.linear.RealVector getColumnVector(int column) throws org.apache.commons.math.linear.MatrixIndexException;

	void setColumnVector(int column, org.apache.commons.math.linear.RealVector vector) throws org.apache.commons.math.linear.InvalidMatrixException, org.apache.commons.math.linear.MatrixIndexException;

	double[] getRow(int row) throws org.apache.commons.math.linear.MatrixIndexException;

	void setRow(int row, double[] array) throws org.apache.commons.math.linear.InvalidMatrixException, org.apache.commons.math.linear.MatrixIndexException;

	double[] getColumn(int column) throws org.apache.commons.math.linear.MatrixIndexException;

	void setColumn(int column, double[] array) throws org.apache.commons.math.linear.InvalidMatrixException, org.apache.commons.math.linear.MatrixIndexException;

	double getEntry(int row, int column) throws org.apache.commons.math.linear.MatrixIndexException;

	void setEntry(int row, int column, double value) throws org.apache.commons.math.linear.MatrixIndexException;

	void addToEntry(int row, int column, double increment) throws org.apache.commons.math.linear.MatrixIndexException;

	void multiplyEntry(int row, int column, double factor) throws org.apache.commons.math.linear.MatrixIndexException;

	org.apache.commons.math.linear.RealMatrix transpose();

	@java.lang.Deprecated
	org.apache.commons.math.linear.RealMatrix inverse() throws org.apache.commons.math.linear.InvalidMatrixException;

	@java.lang.Deprecated
	double getDeterminant();

	@java.lang.Deprecated
	boolean isSingular();

	double getTrace() throws org.apache.commons.math.linear.NonSquareMatrixException;

	double[] operate(double[] v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.RealVector operate(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException;

	double[] preMultiply(double[] v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.RealVector preMultiply(org.apache.commons.math.linear.RealVector v) throws java.lang.IllegalArgumentException;

	double walkInRowOrder(org.apache.commons.math.linear.RealMatrixChangingVisitor visitor) throws org.apache.commons.math.linear.MatrixVisitorException;

	double walkInRowOrder(org.apache.commons.math.linear.RealMatrixPreservingVisitor visitor) throws org.apache.commons.math.linear.MatrixVisitorException;

	double walkInRowOrder(org.apache.commons.math.linear.RealMatrixChangingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn) throws org.apache.commons.math.linear.MatrixIndexException, org.apache.commons.math.linear.MatrixVisitorException;

	double walkInRowOrder(org.apache.commons.math.linear.RealMatrixPreservingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn) throws org.apache.commons.math.linear.MatrixIndexException, org.apache.commons.math.linear.MatrixVisitorException;

	double walkInColumnOrder(org.apache.commons.math.linear.RealMatrixChangingVisitor visitor) throws org.apache.commons.math.linear.MatrixVisitorException;

	double walkInColumnOrder(org.apache.commons.math.linear.RealMatrixPreservingVisitor visitor) throws org.apache.commons.math.linear.MatrixVisitorException;

	double walkInColumnOrder(org.apache.commons.math.linear.RealMatrixChangingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn) throws org.apache.commons.math.linear.MatrixIndexException, org.apache.commons.math.linear.MatrixVisitorException;

	double walkInColumnOrder(org.apache.commons.math.linear.RealMatrixPreservingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn) throws org.apache.commons.math.linear.MatrixIndexException, org.apache.commons.math.linear.MatrixVisitorException;

	double walkInOptimizedOrder(org.apache.commons.math.linear.RealMatrixChangingVisitor visitor) throws org.apache.commons.math.linear.MatrixVisitorException;

	double walkInOptimizedOrder(org.apache.commons.math.linear.RealMatrixPreservingVisitor visitor) throws org.apache.commons.math.linear.MatrixVisitorException;

	double walkInOptimizedOrder(org.apache.commons.math.linear.RealMatrixChangingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn) throws org.apache.commons.math.linear.MatrixIndexException, org.apache.commons.math.linear.MatrixVisitorException;

	double walkInOptimizedOrder(org.apache.commons.math.linear.RealMatrixPreservingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn) throws org.apache.commons.math.linear.MatrixIndexException, org.apache.commons.math.linear.MatrixVisitorException;

	@java.lang.Deprecated
	double[] solve(double[] b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException;

	@java.lang.Deprecated
	org.apache.commons.math.linear.RealMatrix solve(org.apache.commons.math.linear.RealMatrix b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException;
}

