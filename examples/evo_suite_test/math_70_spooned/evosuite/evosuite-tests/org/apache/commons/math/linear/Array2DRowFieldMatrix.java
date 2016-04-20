package org.apache.commons.math.linear;


public class Array2DRowFieldMatrix<T extends org.apache.commons.math.FieldElement<T>> extends org.apache.commons.math.linear.AbstractFieldMatrix<T> implements java.io.Serializable {
	private static final long serialVersionUID = 7260756672015356458L;

	private static final java.lang.String AT_LEAST_ONE_ROW_MESSAGE = "matrix must have at least one row";

	private static final java.lang.String AT_LEAST_ONE_COLUMN_MESSAGE = "matrix must have at least one column";

	private static final java.lang.String DIFFERENT_ROWS_LENGTHS_MESSAGE = "some rows have length {0} while others have length {1}";

	private static final java.lang.String NO_ENTRY_MESSAGE = "no entry at indices ({0}, {1}) in a {2}x{3} matrix";

	private static final java.lang.String VECTOR_LENGTHS_MISMATCH = "vector length mismatch: got {0} but expected {1}";

	protected T[][] data;

	public Array2DRowFieldMatrix(final org.apache.commons.math.Field<T> field) {
		super(field);
	}

	public Array2DRowFieldMatrix(final org.apache.commons.math.Field<T> field ,final int rowDimension ,final int columnDimension) throws java.lang.IllegalArgumentException {
		super(field, rowDimension, columnDimension);
		data = org.apache.commons.math.linear.AbstractFieldMatrix.buildArray(field, rowDimension, columnDimension);
	}

	public Array2DRowFieldMatrix(final T[][] d) throws java.lang.IllegalArgumentException , java.lang.NullPointerException {
		super(org.apache.commons.math.linear.AbstractFieldMatrix.extractField(d));
		copyIn(d);
	}

	public Array2DRowFieldMatrix(final T[][] d ,final boolean copyArray) throws java.lang.IllegalArgumentException , java.lang.NullPointerException {
		super(org.apache.commons.math.linear.AbstractFieldMatrix.extractField(d));
		if (copyArray) {
			copyIn(d);
		} else {
			if (d == null) {
				throw new java.lang.NullPointerException();
			} 
			final int nRows = d.length;
			if (nRows == 0) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(AT_LEAST_ONE_ROW_MESSAGE);
			} 
			final int nCols = d[0].length;
			if (nCols == 0) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(AT_LEAST_ONE_COLUMN_MESSAGE);
			} 
			for (int r = 1 ; r < nRows ; r++) {
				if ((d[r].length) != nCols) {
					throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(DIFFERENT_ROWS_LENGTHS_MESSAGE, nCols, d[r].length);
				} 
			}
			data = d;
		}
	}

	public Array2DRowFieldMatrix(final T[] v) {
		super(org.apache.commons.math.linear.AbstractFieldMatrix.extractField(v));
		final int nRows = v.length;
		data = org.apache.commons.math.linear.AbstractFieldMatrix.buildArray(getField(), nRows, 1);
		for (int row = 0 ; row < nRows ; row++) {
			data[row][0] = v[row];
		}
	}

	@java.lang.Override
	public org.apache.commons.math.linear.FieldMatrix<T> createMatrix(final int rowDimension, final int columnDimension) throws java.lang.IllegalArgumentException {
		return new org.apache.commons.math.linear.Array2DRowFieldMatrix<T>(getField() , rowDimension , columnDimension);
	}

	@java.lang.Override
	public org.apache.commons.math.linear.FieldMatrix<T> copy() {
		return new org.apache.commons.math.linear.Array2DRowFieldMatrix<T>(copyOut() , false);
	}

	@java.lang.Override
	public org.apache.commons.math.linear.FieldMatrix<T> add(final org.apache.commons.math.linear.FieldMatrix<T> m) throws java.lang.IllegalArgumentException {
		try {
			return add(((org.apache.commons.math.linear.Array2DRowFieldMatrix<T>)(m)));
		} catch (java.lang.ClassCastException cce) {
			return super.add(m);
		}
	}

	public org.apache.commons.math.linear.Array2DRowFieldMatrix<T> add(final org.apache.commons.math.linear.Array2DRowFieldMatrix<T> m) throws java.lang.IllegalArgumentException {
		checkAdditionCompatible(m);
		final int rowCount = getRowDimension();
		final int columnCount = getColumnDimension();
		final T[][] outData = org.apache.commons.math.linear.AbstractFieldMatrix.buildArray(getField(), rowCount, columnCount);
		for (int row = 0 ; row < rowCount ; row++) {
			final T[] dataRow = data[row];
			final T[] mRow = m.data[row];
			final T[] outDataRow = outData[row];
			for (int col = 0 ; col < columnCount ; col++) {
				outDataRow[col] = dataRow[col].add(mRow[col]);
			}
		}
		return new org.apache.commons.math.linear.Array2DRowFieldMatrix<T>(outData , false);
	}

	@java.lang.Override
	public org.apache.commons.math.linear.FieldMatrix<T> subtract(final org.apache.commons.math.linear.FieldMatrix<T> m) throws java.lang.IllegalArgumentException {
		try {
			return subtract(((org.apache.commons.math.linear.Array2DRowFieldMatrix<T>)(m)));
		} catch (java.lang.ClassCastException cce) {
			return super.subtract(m);
		}
	}

	public org.apache.commons.math.linear.Array2DRowFieldMatrix<T> subtract(final org.apache.commons.math.linear.Array2DRowFieldMatrix<T> m) throws java.lang.IllegalArgumentException {
		checkSubtractionCompatible(m);
		final int rowCount = getRowDimension();
		final int columnCount = getColumnDimension();
		final T[][] outData = org.apache.commons.math.linear.AbstractFieldMatrix.buildArray(getField(), rowCount, columnCount);
		for (int row = 0 ; row < rowCount ; row++) {
			final T[] dataRow = data[row];
			final T[] mRow = m.data[row];
			final T[] outDataRow = outData[row];
			for (int col = 0 ; col < columnCount ; col++) {
				outDataRow[col] = dataRow[col].subtract(mRow[col]);
			}
		}
		return new org.apache.commons.math.linear.Array2DRowFieldMatrix<T>(outData , false);
	}

	@java.lang.Override
	public org.apache.commons.math.linear.FieldMatrix<T> multiply(final org.apache.commons.math.linear.FieldMatrix<T> m) throws java.lang.IllegalArgumentException {
		try {
			return multiply(((org.apache.commons.math.linear.Array2DRowFieldMatrix<T>)(m)));
		} catch (java.lang.ClassCastException cce) {
			return super.multiply(m);
		}
	}

	public org.apache.commons.math.linear.Array2DRowFieldMatrix<T> multiply(final org.apache.commons.math.linear.Array2DRowFieldMatrix<T> m) throws java.lang.IllegalArgumentException {
		checkMultiplicationCompatible(m);
		final int nRows = getRowDimension();
		final int nCols = m.getColumnDimension();
		final int nSum = getColumnDimension();
		final T[][] outData = org.apache.commons.math.linear.AbstractFieldMatrix.buildArray(getField(), nRows, nCols);
		for (int row = 0 ; row < nRows ; row++) {
			final T[] dataRow = data[row];
			final T[] outDataRow = outData[row];
			for (int col = 0 ; col < nCols ; col++) {
				T sum = getField().getZero();
				for (int i = 0 ; i < nSum ; i++) {
					sum = sum.add(dataRow[i].multiply(m.data[i][col]));
				}
				outDataRow[col] = sum;
			}
		}
		return new org.apache.commons.math.linear.Array2DRowFieldMatrix<T>(outData , false);
	}

	@java.lang.Override
	public T[][] getData() {
		return copyOut();
	}

	public T[][] getDataRef() {
		return data;
	}

	@java.lang.Override
	public void setSubMatrix(final T[][] subMatrix, final int row, final int column) throws org.apache.commons.math.linear.MatrixIndexException {
		if ((data) == null) {
			if (row > 0) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalStateException("first {0} rows are not initialized yet", row);
			} 
			if (column > 0) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalStateException("first {0} columns are not initialized yet", column);
			} 
			final int nRows = subMatrix.length;
			if (nRows == 0) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(AT_LEAST_ONE_ROW_MESSAGE);
			} 
			final int nCols = subMatrix[0].length;
			if (nCols == 0) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(AT_LEAST_ONE_COLUMN_MESSAGE);
			} 
			data = org.apache.commons.math.linear.AbstractFieldMatrix.buildArray(getField(), subMatrix.length, nCols);
			for (int i = 0 ; i < (data.length) ; ++i) {
				if ((subMatrix[i].length) != nCols) {
					throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(DIFFERENT_ROWS_LENGTHS_MESSAGE, nCols, subMatrix[i].length);
				} 
				java.lang.System.arraycopy(subMatrix[i], 0, data[(i + row)], column, nCols);
			}
		} else {
			super.setSubMatrix(subMatrix, row, column);
		}
	}

	@java.lang.Override
	public T getEntry(final int row, final int column) throws org.apache.commons.math.linear.MatrixIndexException {
		try {
			return data[row][column];
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			throw new org.apache.commons.math.linear.MatrixIndexException(NO_ENTRY_MESSAGE , row , column , getRowDimension() , getColumnDimension());
		}
	}

	@java.lang.Override
	public void setEntry(final int row, final int column, final T value) throws org.apache.commons.math.linear.MatrixIndexException {
		try {
			data[row][column] = value;
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			throw new org.apache.commons.math.linear.MatrixIndexException(NO_ENTRY_MESSAGE , row , column , getRowDimension() , getColumnDimension());
		}
	}

	@java.lang.Override
	public void addToEntry(final int row, final int column, final T increment) throws org.apache.commons.math.linear.MatrixIndexException {
		try {
			data[row][column] = data[row][column].add(increment);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			throw new org.apache.commons.math.linear.MatrixIndexException(NO_ENTRY_MESSAGE , row , column , getRowDimension() , getColumnDimension());
		}
	}

	@java.lang.Override
	public void multiplyEntry(final int row, final int column, final T factor) throws org.apache.commons.math.linear.MatrixIndexException {
		try {
			data[row][column] = data[row][column].multiply(factor);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			throw new org.apache.commons.math.linear.MatrixIndexException(NO_ENTRY_MESSAGE , row , column , getRowDimension() , getColumnDimension());
		}
	}

	@java.lang.Override
	public int getRowDimension() {
		return (data) == null ? 0 : data.length;
	}

	@java.lang.Override
	public int getColumnDimension() {
		return ((data) == null) || ((data[0]) == null) ? 0 : data[0].length;
	}

	@java.lang.Override
	public T[] operate(final T[] v) throws java.lang.IllegalArgumentException {
		final int nRows = getRowDimension();
		final int nCols = getColumnDimension();
		if ((v.length) != nCols) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(VECTOR_LENGTHS_MISMATCH, v.length, nCols);
		} 
		final T[] out = org.apache.commons.math.linear.AbstractFieldMatrix.buildArray(getField(), nRows);
		for (int row = 0 ; row < nRows ; row++) {
			final T[] dataRow = data[row];
			T sum = getField().getZero();
			for (int i = 0 ; i < nCols ; i++) {
				sum = sum.add(dataRow[i].multiply(v[i]));
			}
			out[row] = sum;
		}
		return out;
	}

	@java.lang.Override
	public T[] preMultiply(final T[] v) throws java.lang.IllegalArgumentException {
		final int nRows = getRowDimension();
		final int nCols = getColumnDimension();
		if ((v.length) != nRows) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(VECTOR_LENGTHS_MISMATCH, v.length, nRows);
		} 
		final T[] out = org.apache.commons.math.linear.AbstractFieldMatrix.buildArray(getField(), nCols);
		for (int col = 0 ; col < nCols ; ++col) {
			T sum = getField().getZero();
			for (int i = 0 ; i < nRows ; ++i) {
				sum = sum.add(data[i][col].multiply(v[i]));
			}
			out[col] = sum;
		}
		return out;
	}

	@java.lang.Override
	public T walkInRowOrder(final org.apache.commons.math.linear.FieldMatrixChangingVisitor<T> visitor) throws org.apache.commons.math.linear.MatrixVisitorException {
		final int rows = getRowDimension();
		final int columns = getColumnDimension();
		visitor.start(rows, columns, 0, (rows - 1), 0, (columns - 1));
		for (int i = 0 ; i < rows ; ++i) {
			final T[] rowI = data[i];
			for (int j = 0 ; j < columns ; ++j) {
				rowI[j] = visitor.visit(i, j, rowI[j]);
			}
		}
		return visitor.end();
	}

	@java.lang.Override
	public T walkInRowOrder(final org.apache.commons.math.linear.FieldMatrixPreservingVisitor<T> visitor) throws org.apache.commons.math.linear.MatrixVisitorException {
		final int rows = getRowDimension();
		final int columns = getColumnDimension();
		visitor.start(rows, columns, 0, (rows - 1), 0, (columns - 1));
		for (int i = 0 ; i < rows ; ++i) {
			final T[] rowI = data[i];
			for (int j = 0 ; j < columns ; ++j) {
				visitor.visit(i, j, rowI[j]);
			}
		}
		return visitor.end();
	}

	@java.lang.Override
	public T walkInRowOrder(final org.apache.commons.math.linear.FieldMatrixChangingVisitor<T> visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws org.apache.commons.math.linear.MatrixIndexException, org.apache.commons.math.linear.MatrixVisitorException {
		checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
		visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
		for (int i = startRow ; i <= endRow ; ++i) {
			final T[] rowI = data[i];
			for (int j = startColumn ; j <= endColumn ; ++j) {
				rowI[j] = visitor.visit(i, j, rowI[j]);
			}
		}
		return visitor.end();
	}

	@java.lang.Override
	public T walkInRowOrder(final org.apache.commons.math.linear.FieldMatrixPreservingVisitor<T> visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws org.apache.commons.math.linear.MatrixIndexException, org.apache.commons.math.linear.MatrixVisitorException {
		checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
		visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
		for (int i = startRow ; i <= endRow ; ++i) {
			final T[] rowI = data[i];
			for (int j = startColumn ; j <= endColumn ; ++j) {
				visitor.visit(i, j, rowI[j]);
			}
		}
		return visitor.end();
	}

	@java.lang.Override
	public T walkInColumnOrder(final org.apache.commons.math.linear.FieldMatrixChangingVisitor<T> visitor) throws org.apache.commons.math.linear.MatrixVisitorException {
		final int rows = getRowDimension();
		final int columns = getColumnDimension();
		visitor.start(rows, columns, 0, (rows - 1), 0, (columns - 1));
		for (int j = 0 ; j < columns ; ++j) {
			for (int i = 0 ; i < rows ; ++i) {
				final T[] rowI = data[i];
				rowI[j] = visitor.visit(i, j, rowI[j]);
			}
		}
		return visitor.end();
	}

	@java.lang.Override
	public T walkInColumnOrder(final org.apache.commons.math.linear.FieldMatrixPreservingVisitor<T> visitor) throws org.apache.commons.math.linear.MatrixVisitorException {
		final int rows = getRowDimension();
		final int columns = getColumnDimension();
		visitor.start(rows, columns, 0, (rows - 1), 0, (columns - 1));
		for (int j = 0 ; j < columns ; ++j) {
			for (int i = 0 ; i < rows ; ++i) {
				visitor.visit(i, j, data[i][j]);
			}
		}
		return visitor.end();
	}

	@java.lang.Override
	public T walkInColumnOrder(final org.apache.commons.math.linear.FieldMatrixChangingVisitor<T> visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws org.apache.commons.math.linear.MatrixIndexException, org.apache.commons.math.linear.MatrixVisitorException {
		checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
		visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
		for (int j = startColumn ; j <= endColumn ; ++j) {
			for (int i = startRow ; i <= endRow ; ++i) {
				final T[] rowI = data[i];
				rowI[j] = visitor.visit(i, j, rowI[j]);
			}
		}
		return visitor.end();
	}

	@java.lang.Override
	public T walkInColumnOrder(final org.apache.commons.math.linear.FieldMatrixPreservingVisitor<T> visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws org.apache.commons.math.linear.MatrixIndexException, org.apache.commons.math.linear.MatrixVisitorException {
		checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
		visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
		for (int j = startColumn ; j <= endColumn ; ++j) {
			for (int i = startRow ; i <= endRow ; ++i) {
				visitor.visit(i, j, data[i][j]);
			}
		}
		return visitor.end();
	}

	private T[][] copyOut() {
		final int nRows = getRowDimension();
		final T[][] out = org.apache.commons.math.linear.AbstractFieldMatrix.buildArray(getField(), nRows, getColumnDimension());
		for (int i = 0 ; i < nRows ; i++) {
			java.lang.System.arraycopy(data[i], 0, out[i], 0, data[i].length);
		}
		return out;
	}

	private void copyIn(final T[][] in) {
		setSubMatrix(in, 0, 0);
	}
}

