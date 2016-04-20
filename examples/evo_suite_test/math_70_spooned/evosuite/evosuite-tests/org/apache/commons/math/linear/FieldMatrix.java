package org.apache.commons.math.linear;


public interface FieldMatrix<T extends org.apache.commons.math.FieldElement<T>> extends org.apache.commons.math.linear.AnyMatrix {
	org.apache.commons.math.Field<T> getField();

	org.apache.commons.math.linear.FieldMatrix<T> createMatrix(final int rowDimension, final int columnDimension);

	org.apache.commons.math.linear.FieldMatrix<T> copy();

	org.apache.commons.math.linear.FieldMatrix<T> add(org.apache.commons.math.linear.FieldMatrix<T> m) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.FieldMatrix<T> subtract(org.apache.commons.math.linear.FieldMatrix<T> m) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.FieldMatrix<T> scalarAdd(T d);

	org.apache.commons.math.linear.FieldMatrix<T> scalarMultiply(T d);

	org.apache.commons.math.linear.FieldMatrix<T> multiply(org.apache.commons.math.linear.FieldMatrix<T> m) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.FieldMatrix<T> preMultiply(org.apache.commons.math.linear.FieldMatrix<T> m) throws java.lang.IllegalArgumentException;

	T[][] getData();

	org.apache.commons.math.linear.FieldMatrix<T> getSubMatrix(int startRow, int endRow, int startColumn, int endColumn) throws org.apache.commons.math.linear.MatrixIndexException;

	org.apache.commons.math.linear.FieldMatrix<T> getSubMatrix(int[] selectedRows, int[] selectedColumns) throws org.apache.commons.math.linear.MatrixIndexException;

	void copySubMatrix(int startRow, int endRow, int startColumn, int endColumn, T[][] destination) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.MatrixIndexException;

	void copySubMatrix(int[] selectedRows, int[] selectedColumns, T[][] destination) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.MatrixIndexException;

	void setSubMatrix(T[][] subMatrix, int row, int column) throws org.apache.commons.math.linear.MatrixIndexException;

	org.apache.commons.math.linear.FieldMatrix<T> getRowMatrix(int row) throws org.apache.commons.math.linear.MatrixIndexException;

	void setRowMatrix(int row, org.apache.commons.math.linear.FieldMatrix<T> matrix) throws org.apache.commons.math.linear.InvalidMatrixException, org.apache.commons.math.linear.MatrixIndexException;

	org.apache.commons.math.linear.FieldMatrix<T> getColumnMatrix(int column) throws org.apache.commons.math.linear.MatrixIndexException;

	void setColumnMatrix(int column, org.apache.commons.math.linear.FieldMatrix<T> matrix) throws org.apache.commons.math.linear.InvalidMatrixException, org.apache.commons.math.linear.MatrixIndexException;

	org.apache.commons.math.linear.FieldVector<T> getRowVector(int row) throws org.apache.commons.math.linear.MatrixIndexException;

	void setRowVector(int row, org.apache.commons.math.linear.FieldVector<T> vector) throws org.apache.commons.math.linear.InvalidMatrixException, org.apache.commons.math.linear.MatrixIndexException;

	org.apache.commons.math.linear.FieldVector<T> getColumnVector(int column) throws org.apache.commons.math.linear.MatrixIndexException;

	void setColumnVector(int column, org.apache.commons.math.linear.FieldVector<T> vector) throws org.apache.commons.math.linear.InvalidMatrixException, org.apache.commons.math.linear.MatrixIndexException;

	T[] getRow(int row) throws org.apache.commons.math.linear.MatrixIndexException;

	void setRow(int row, T[] array) throws org.apache.commons.math.linear.InvalidMatrixException, org.apache.commons.math.linear.MatrixIndexException;

	T[] getColumn(int column) throws org.apache.commons.math.linear.MatrixIndexException;

	void setColumn(int column, T[] array) throws org.apache.commons.math.linear.InvalidMatrixException, org.apache.commons.math.linear.MatrixIndexException;

	T getEntry(int row, int column) throws org.apache.commons.math.linear.MatrixIndexException;

	void setEntry(int row, int column, T value) throws org.apache.commons.math.linear.MatrixIndexException;

	void addToEntry(int row, int column, T increment) throws org.apache.commons.math.linear.MatrixIndexException;

	void multiplyEntry(int row, int column, T factor) throws org.apache.commons.math.linear.MatrixIndexException;

	org.apache.commons.math.linear.FieldMatrix<T> transpose();

	T getTrace() throws org.apache.commons.math.linear.NonSquareMatrixException;

	T[] operate(T[] v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.FieldVector<T> operate(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException;

	T[] preMultiply(T[] v) throws java.lang.IllegalArgumentException;

	org.apache.commons.math.linear.FieldVector<T> preMultiply(org.apache.commons.math.linear.FieldVector<T> v) throws java.lang.IllegalArgumentException;

	T walkInRowOrder(org.apache.commons.math.linear.FieldMatrixChangingVisitor<T> visitor) throws org.apache.commons.math.linear.MatrixVisitorException;

	T walkInRowOrder(org.apache.commons.math.linear.FieldMatrixPreservingVisitor<T> visitor) throws org.apache.commons.math.linear.MatrixVisitorException;

	T walkInRowOrder(org.apache.commons.math.linear.FieldMatrixChangingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn) throws org.apache.commons.math.linear.MatrixIndexException, org.apache.commons.math.linear.MatrixVisitorException;

	T walkInRowOrder(org.apache.commons.math.linear.FieldMatrixPreservingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn) throws org.apache.commons.math.linear.MatrixIndexException, org.apache.commons.math.linear.MatrixVisitorException;

	T walkInColumnOrder(org.apache.commons.math.linear.FieldMatrixChangingVisitor<T> visitor) throws org.apache.commons.math.linear.MatrixVisitorException;

	T walkInColumnOrder(org.apache.commons.math.linear.FieldMatrixPreservingVisitor<T> visitor) throws org.apache.commons.math.linear.MatrixVisitorException;

	T walkInColumnOrder(org.apache.commons.math.linear.FieldMatrixChangingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn) throws org.apache.commons.math.linear.MatrixIndexException, org.apache.commons.math.linear.MatrixVisitorException;

	T walkInColumnOrder(org.apache.commons.math.linear.FieldMatrixPreservingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn) throws org.apache.commons.math.linear.MatrixIndexException, org.apache.commons.math.linear.MatrixVisitorException;

	T walkInOptimizedOrder(org.apache.commons.math.linear.FieldMatrixChangingVisitor<T> visitor) throws org.apache.commons.math.linear.MatrixVisitorException;

	T walkInOptimizedOrder(org.apache.commons.math.linear.FieldMatrixPreservingVisitor<T> visitor) throws org.apache.commons.math.linear.MatrixVisitorException;

	T walkInOptimizedOrder(org.apache.commons.math.linear.FieldMatrixChangingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn) throws org.apache.commons.math.linear.MatrixIndexException, org.apache.commons.math.linear.MatrixVisitorException;

	T walkInOptimizedOrder(org.apache.commons.math.linear.FieldMatrixPreservingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn) throws org.apache.commons.math.linear.MatrixIndexException, org.apache.commons.math.linear.MatrixVisitorException;
}

