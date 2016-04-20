package org.apache.commons.math.linear;


@java.lang.Deprecated
public class BigMatrixImpl implements java.io.Serializable , org.apache.commons.math.linear.BigMatrix {
	static final java.math.BigDecimal ZERO = new java.math.BigDecimal(0);

	static final java.math.BigDecimal ONE = new java.math.BigDecimal(1);

	private static final java.math.BigDecimal TOO_SMALL = new java.math.BigDecimal(1.0E-11);

	private static final long serialVersionUID = -1011428905656140431L;

	protected java.math.BigDecimal[][] data = null;

	protected java.math.BigDecimal[][] lu = null;

	protected int[] permutation = null;

	protected int parity = 1;

	private int roundingMode = java.math.BigDecimal.ROUND_HALF_UP;

	private int scale = 64;

	public BigMatrixImpl() {
	}

	public BigMatrixImpl(int rowDimension ,int columnDimension) {
		if (rowDimension <= 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("invalid row dimension {0} (must be positive)", rowDimension);
		} 
		if (columnDimension <= 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("invalid column dimension {0} (must be positive)", columnDimension);
		} 
		data = new java.math.BigDecimal[rowDimension][columnDimension];
		lu = null;
	}

	public BigMatrixImpl(java.math.BigDecimal[][] d) {
		copyIn(d);
		lu = null;
	}

	public BigMatrixImpl(java.math.BigDecimal[][] d ,boolean copyArray) {
		if (copyArray) {
			copyIn(d);
		} else {
			if (d == null) {
				throw new java.lang.NullPointerException();
			} 
			final int nRows = d.length;
			if (nRows == 0) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("matrix must have at least one row");
			} 
			final int nCols = d[0].length;
			if (nCols == 0) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("matrix must have at least one column");
			} 
			for (int r = 1 ; r < nRows ; r++) {
				if ((d[r].length) != nCols) {
					throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("some rows have length {0} while others have length {1}", nCols, d[r].length);
				} 
			}
			data = d;
		}
		lu = null;
	}

	public BigMatrixImpl(double[][] d) {
		final int nRows = d.length;
		if (nRows == 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("matrix must have at least one row");
		} 
		final int nCols = d[0].length;
		if (nCols == 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("matrix must have at least one column");
		} 
		for (int row = 1 ; row < nRows ; row++) {
			if ((d[row].length) != nCols) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("some rows have length {0} while others have length {1}", nCols, d[row].length);
			} 
		}
		copyIn(d);
		lu = null;
	}

	public BigMatrixImpl(java.lang.String[][] d) {
		final int nRows = d.length;
		if (nRows == 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("matrix must have at least one row");
		} 
		final int nCols = d[0].length;
		if (nCols == 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("matrix must have at least one column");
		} 
		for (int row = 1 ; row < nRows ; row++) {
			if ((d[row].length) != nCols) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("some rows have length {0} while others have length {1}", nCols, d[row].length);
			} 
		}
		copyIn(d);
		lu = null;
	}

	public BigMatrixImpl(java.math.BigDecimal[] v) {
		final int nRows = v.length;
		data = new java.math.BigDecimal[nRows][1];
		for (int row = 0 ; row < nRows ; row++) {
			data[row][0] = v[row];
		}
	}

	public org.apache.commons.math.linear.BigMatrix copy() {
		return new org.apache.commons.math.linear.BigMatrixImpl(copyOut() , false);
	}

	public org.apache.commons.math.linear.BigMatrix add(org.apache.commons.math.linear.BigMatrix m) throws java.lang.IllegalArgumentException {
		try {
			return add(((org.apache.commons.math.linear.BigMatrixImpl)(m)));
		} catch (java.lang.ClassCastException cce) {
			org.apache.commons.math.linear.MatrixUtils.checkAdditionCompatible(this, m);
			final int rowCount = getRowDimension();
			final int columnCount = getColumnDimension();
			final java.math.BigDecimal[][] outData = new java.math.BigDecimal[rowCount][columnCount];
			for (int row = 0 ; row < rowCount ; row++) {
				final java.math.BigDecimal[] dataRow = data[row];
				final java.math.BigDecimal[] outDataRow = outData[row];
				for (int col = 0 ; col < columnCount ; col++) {
					outDataRow[col] = dataRow[col].add(m.getEntry(row, col));
				}
			}
			return new org.apache.commons.math.linear.BigMatrixImpl(outData , false);
		}
	}

	public org.apache.commons.math.linear.BigMatrixImpl add(org.apache.commons.math.linear.BigMatrixImpl m) throws java.lang.IllegalArgumentException {
		org.apache.commons.math.linear.MatrixUtils.checkAdditionCompatible(this, m);
		final int rowCount = getRowDimension();
		final int columnCount = getColumnDimension();
		final java.math.BigDecimal[][] outData = new java.math.BigDecimal[rowCount][columnCount];
		for (int row = 0 ; row < rowCount ; row++) {
			final java.math.BigDecimal[] dataRow = data[row];
			final java.math.BigDecimal[] mRow = m.data[row];
			final java.math.BigDecimal[] outDataRow = outData[row];
			for (int col = 0 ; col < columnCount ; col++) {
				outDataRow[col] = dataRow[col].add(mRow[col]);
			}
		}
		return new org.apache.commons.math.linear.BigMatrixImpl(outData , false);
	}

	public org.apache.commons.math.linear.BigMatrix subtract(org.apache.commons.math.linear.BigMatrix m) throws java.lang.IllegalArgumentException {
		try {
			return subtract(((org.apache.commons.math.linear.BigMatrixImpl)(m)));
		} catch (java.lang.ClassCastException cce) {
			org.apache.commons.math.linear.MatrixUtils.checkSubtractionCompatible(this, m);
			final int rowCount = getRowDimension();
			final int columnCount = getColumnDimension();
			final java.math.BigDecimal[][] outData = new java.math.BigDecimal[rowCount][columnCount];
			for (int row = 0 ; row < rowCount ; row++) {
				final java.math.BigDecimal[] dataRow = data[row];
				final java.math.BigDecimal[] outDataRow = outData[row];
				for (int col = 0 ; col < columnCount ; col++) {
					outDataRow[col] = dataRow[col].subtract(getEntry(row, col));
				}
			}
			return new org.apache.commons.math.linear.BigMatrixImpl(outData , false);
		}
	}

	public org.apache.commons.math.linear.BigMatrixImpl subtract(org.apache.commons.math.linear.BigMatrixImpl m) throws java.lang.IllegalArgumentException {
		org.apache.commons.math.linear.MatrixUtils.checkSubtractionCompatible(this, m);
		final int rowCount = getRowDimension();
		final int columnCount = getColumnDimension();
		final java.math.BigDecimal[][] outData = new java.math.BigDecimal[rowCount][columnCount];
		for (int row = 0 ; row < rowCount ; row++) {
			final java.math.BigDecimal[] dataRow = data[row];
			final java.math.BigDecimal[] mRow = m.data[row];
			final java.math.BigDecimal[] outDataRow = outData[row];
			for (int col = 0 ; col < columnCount ; col++) {
				outDataRow[col] = dataRow[col].subtract(mRow[col]);
			}
		}
		return new org.apache.commons.math.linear.BigMatrixImpl(outData , false);
	}

	public org.apache.commons.math.linear.BigMatrix scalarAdd(java.math.BigDecimal d) {
		final int rowCount = getRowDimension();
		final int columnCount = getColumnDimension();
		final java.math.BigDecimal[][] outData = new java.math.BigDecimal[rowCount][columnCount];
		for (int row = 0 ; row < rowCount ; row++) {
			final java.math.BigDecimal[] dataRow = data[row];
			final java.math.BigDecimal[] outDataRow = outData[row];
			for (int col = 0 ; col < columnCount ; col++) {
				outDataRow[col] = dataRow[col].add(d);
			}
		}
		return new org.apache.commons.math.linear.BigMatrixImpl(outData , false);
	}

	public org.apache.commons.math.linear.BigMatrix scalarMultiply(java.math.BigDecimal d) {
		final int rowCount = getRowDimension();
		final int columnCount = getColumnDimension();
		final java.math.BigDecimal[][] outData = new java.math.BigDecimal[rowCount][columnCount];
		for (int row = 0 ; row < rowCount ; row++) {
			final java.math.BigDecimal[] dataRow = data[row];
			final java.math.BigDecimal[] outDataRow = outData[row];
			for (int col = 0 ; col < columnCount ; col++) {
				outDataRow[col] = dataRow[col].multiply(d);
			}
		}
		return new org.apache.commons.math.linear.BigMatrixImpl(outData , false);
	}

	public org.apache.commons.math.linear.BigMatrix multiply(org.apache.commons.math.linear.BigMatrix m) throws java.lang.IllegalArgumentException {
		try {
			return multiply(((org.apache.commons.math.linear.BigMatrixImpl)(m)));
		} catch (java.lang.ClassCastException cce) {
			org.apache.commons.math.linear.MatrixUtils.checkMultiplicationCompatible(this, m);
			final int nRows = getRowDimension();
			final int nCols = m.getColumnDimension();
			final int nSum = getColumnDimension();
			final java.math.BigDecimal[][] outData = new java.math.BigDecimal[nRows][nCols];
			for (int row = 0 ; row < nRows ; row++) {
				final java.math.BigDecimal[] dataRow = data[row];
				final java.math.BigDecimal[] outDataRow = outData[row];
				for (int col = 0 ; col < nCols ; col++) {
					java.math.BigDecimal sum = ZERO;
					for (int i = 0 ; i < nSum ; i++) {
						sum = sum.add(dataRow[i].multiply(m.getEntry(i, col)));
					}
					outDataRow[col] = sum;
				}
			}
			return new org.apache.commons.math.linear.BigMatrixImpl(outData , false);
		}
	}

	public org.apache.commons.math.linear.BigMatrixImpl multiply(org.apache.commons.math.linear.BigMatrixImpl m) throws java.lang.IllegalArgumentException {
		org.apache.commons.math.linear.MatrixUtils.checkMultiplicationCompatible(this, m);
		final int nRows = getRowDimension();
		final int nCols = m.getColumnDimension();
		final int nSum = getColumnDimension();
		final java.math.BigDecimal[][] outData = new java.math.BigDecimal[nRows][nCols];
		for (int row = 0 ; row < nRows ; row++) {
			final java.math.BigDecimal[] dataRow = data[row];
			final java.math.BigDecimal[] outDataRow = outData[row];
			for (int col = 0 ; col < nCols ; col++) {
				java.math.BigDecimal sum = ZERO;
				for (int i = 0 ; i < nSum ; i++) {
					sum = sum.add(dataRow[i].multiply(m.data[i][col]));
				}
				outDataRow[col] = sum;
			}
		}
		return new org.apache.commons.math.linear.BigMatrixImpl(outData , false);
	}

	public org.apache.commons.math.linear.BigMatrix preMultiply(org.apache.commons.math.linear.BigMatrix m) throws java.lang.IllegalArgumentException {
		return m.multiply(this);
	}

	public java.math.BigDecimal[][] getData() {
		return copyOut();
	}

	public double[][] getDataAsDoubleArray() {
		final int nRows = getRowDimension();
		final int nCols = getColumnDimension();
		final double[][] d = new double[nRows][nCols];
		for (int i = 0 ; i < nRows ; i++) {
			for (int j = 0 ; j < nCols ; j++) {
				d[i][j] = data[i][j].doubleValue();
			}
		}
		return d;
	}

	public java.math.BigDecimal[][] getDataRef() {
		return data;
	}

	public int getRoundingMode() {
		return roundingMode;
	}

	public void setRoundingMode(int roundingMode) {
		this.roundingMode = roundingMode;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public java.math.BigDecimal getNorm() {
		java.math.BigDecimal maxColSum = ZERO;
		for (int col = 0 ; col < (getColumnDimension()) ; col++) {
			java.math.BigDecimal sum = ZERO;
			for (int row = 0 ; row < (getRowDimension()) ; row++) {
				sum = sum.add(data[row][col].abs());
			}
			maxColSum = maxColSum.max(sum);
		}
		return maxColSum;
	}

	public org.apache.commons.math.linear.BigMatrix getSubMatrix(int startRow, int endRow, int startColumn, int endColumn) throws org.apache.commons.math.linear.MatrixIndexException {
		org.apache.commons.math.linear.MatrixUtils.checkRowIndex(this, startRow);
		org.apache.commons.math.linear.MatrixUtils.checkRowIndex(this, endRow);
		if (startRow > endRow) {
			throw new org.apache.commons.math.linear.MatrixIndexException("initial row {0} after final row {1}" , startRow , endRow);
		} 
		org.apache.commons.math.linear.MatrixUtils.checkColumnIndex(this, startColumn);
		org.apache.commons.math.linear.MatrixUtils.checkColumnIndex(this, endColumn);
		if (startColumn > endColumn) {
			throw new org.apache.commons.math.linear.MatrixIndexException("initial column {0} after final column {1}" , startColumn , endColumn);
		} 
		final java.math.BigDecimal[][] subMatrixData = new java.math.BigDecimal[(endRow - startRow) + 1][(endColumn - startColumn) + 1];
		for (int i = startRow ; i <= endRow ; i++) {
			java.lang.System.arraycopy(data[i], startColumn, subMatrixData[(i - startRow)], 0, ((endColumn - startColumn) + 1));
		}
		return new org.apache.commons.math.linear.BigMatrixImpl(subMatrixData , false);
	}

	public org.apache.commons.math.linear.BigMatrix getSubMatrix(int[] selectedRows, int[] selectedColumns) throws org.apache.commons.math.linear.MatrixIndexException {
		if (((selectedRows.length) * (selectedColumns.length)) == 0) {
			if ((selectedRows.length) == 0) {
				throw new org.apache.commons.math.linear.MatrixIndexException("empty selected row index array");
			} 
			throw new org.apache.commons.math.linear.MatrixIndexException("empty selected column index array");
		} 
		final java.math.BigDecimal[][] subMatrixData = new java.math.BigDecimal[selectedRows.length][selectedColumns.length];
		try {
			for (int i = 0 ; i < (selectedRows.length) ; i++) {
				final java.math.BigDecimal[] subI = subMatrixData[i];
				final java.math.BigDecimal[] dataSelectedI = data[selectedRows[i]];
				for (int j = 0 ; j < (selectedColumns.length) ; j++) {
					subI[j] = dataSelectedI[selectedColumns[j]];
				}
			}
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			for (final int row : selectedRows) {
				org.apache.commons.math.linear.MatrixUtils.checkRowIndex(this, row);
			}
			for (final int column : selectedColumns) {
				org.apache.commons.math.linear.MatrixUtils.checkColumnIndex(this, column);
			}
		}
		return new org.apache.commons.math.linear.BigMatrixImpl(subMatrixData , false);
	}

	public void setSubMatrix(java.math.BigDecimal[][] subMatrix, int row, int column) throws org.apache.commons.math.linear.MatrixIndexException {
		final int nRows = subMatrix.length;
		if (nRows == 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("matrix must have at least one row");
		} 
		final int nCols = subMatrix[0].length;
		if (nCols == 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("matrix must have at least one column");
		} 
		for (int r = 1 ; r < nRows ; r++) {
			if ((subMatrix[r].length) != nCols) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("some rows have length {0} while others have length {1}", nCols, subMatrix[r].length);
			} 
		}
		if ((data) == null) {
			if (row > 0) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalStateException("first {0} rows are not initialized yet", row);
			} 
			if (column > 0) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalStateException("first {0} columns are not initialized yet", column);
			} 
			data = new java.math.BigDecimal[nRows][nCols];
			java.lang.System.arraycopy(subMatrix, 0, data, 0, subMatrix.length);
		} else {
			org.apache.commons.math.linear.MatrixUtils.checkRowIndex(this, row);
			org.apache.commons.math.linear.MatrixUtils.checkColumnIndex(this, column);
			org.apache.commons.math.linear.MatrixUtils.checkRowIndex(this, ((nRows + row) - 1));
			org.apache.commons.math.linear.MatrixUtils.checkColumnIndex(this, ((nCols + column) - 1));
		}
		for (int i = 0 ; i < nRows ; i++) {
			java.lang.System.arraycopy(subMatrix[i], 0, data[(row + i)], column, nCols);
		}
		lu = null;
	}

	public org.apache.commons.math.linear.BigMatrix getRowMatrix(int row) throws org.apache.commons.math.linear.MatrixIndexException {
		org.apache.commons.math.linear.MatrixUtils.checkRowIndex(this, row);
		final int ncols = getColumnDimension();
		final java.math.BigDecimal[][] out = new java.math.BigDecimal[1][ncols];
		java.lang.System.arraycopy(data[row], 0, out[0], 0, ncols);
		return new org.apache.commons.math.linear.BigMatrixImpl(out , false);
	}

	public org.apache.commons.math.linear.BigMatrix getColumnMatrix(int column) throws org.apache.commons.math.linear.MatrixIndexException {
		org.apache.commons.math.linear.MatrixUtils.checkColumnIndex(this, column);
		final int nRows = getRowDimension();
		final java.math.BigDecimal[][] out = new java.math.BigDecimal[nRows][1];
		for (int row = 0 ; row < nRows ; row++) {
			out[row][0] = data[row][column];
		}
		return new org.apache.commons.math.linear.BigMatrixImpl(out , false);
	}

	public java.math.BigDecimal[] getRow(int row) throws org.apache.commons.math.linear.MatrixIndexException {
		org.apache.commons.math.linear.MatrixUtils.checkRowIndex(this, row);
		final int ncols = getColumnDimension();
		final java.math.BigDecimal[] out = new java.math.BigDecimal[ncols];
		java.lang.System.arraycopy(data[row], 0, out, 0, ncols);
		return out;
	}

	public double[] getRowAsDoubleArray(int row) throws org.apache.commons.math.linear.MatrixIndexException {
		org.apache.commons.math.linear.MatrixUtils.checkRowIndex(this, row);
		final int ncols = getColumnDimension();
		final double[] out = new double[ncols];
		for (int i = 0 ; i < ncols ; i++) {
			out[i] = data[row][i].doubleValue();
		}
		return out;
	}

	public java.math.BigDecimal[] getColumn(int col) throws org.apache.commons.math.linear.MatrixIndexException {
		org.apache.commons.math.linear.MatrixUtils.checkColumnIndex(this, col);
		final int nRows = getRowDimension();
		final java.math.BigDecimal[] out = new java.math.BigDecimal[nRows];
		for (int i = 0 ; i < nRows ; i++) {
			out[i] = data[i][col];
		}
		return out;
	}

	public double[] getColumnAsDoubleArray(int col) throws org.apache.commons.math.linear.MatrixIndexException {
		org.apache.commons.math.linear.MatrixUtils.checkColumnIndex(this, col);
		final int nrows = getRowDimension();
		final double[] out = new double[nrows];
		for (int i = 0 ; i < nrows ; i++) {
			out[i] = data[i][col].doubleValue();
		}
		return out;
	}

	public java.math.BigDecimal getEntry(int row, int column) throws org.apache.commons.math.linear.MatrixIndexException {
		try {
			return data[row][column];
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			throw new org.apache.commons.math.linear.MatrixIndexException("no entry at indices ({0}, {1}) in a {2}x{3} matrix" , row , column , getRowDimension() , getColumnDimension());
		}
	}

	public double getEntryAsDouble(int row, int column) throws org.apache.commons.math.linear.MatrixIndexException {
		return getEntry(row, column).doubleValue();
	}

	public org.apache.commons.math.linear.BigMatrix transpose() {
		final int nRows = getRowDimension();
		final int nCols = getColumnDimension();
		final java.math.BigDecimal[][] outData = new java.math.BigDecimal[nCols][nRows];
		for (int row = 0 ; row < nRows ; row++) {
			final java.math.BigDecimal[] dataRow = data[row];
			for (int col = 0 ; col < nCols ; col++) {
				outData[col][row] = dataRow[col];
			}
		}
		return new org.apache.commons.math.linear.BigMatrixImpl(outData , false);
	}

	public org.apache.commons.math.linear.BigMatrix inverse() throws org.apache.commons.math.linear.InvalidMatrixException {
		return solve(org.apache.commons.math.linear.MatrixUtils.createBigIdentityMatrix(getRowDimension()));
	}

	public java.math.BigDecimal getDeterminant() throws org.apache.commons.math.linear.InvalidMatrixException {
		if (!(isSquare())) {
			throw new org.apache.commons.math.linear.NonSquareMatrixException(getRowDimension() , getColumnDimension());
		} 
		if (isSingular()) {
			return ZERO;
		} else {
			java.math.BigDecimal det = (parity) == 1 ? ONE : ONE.negate();
			for (int i = 0 ; i < (getRowDimension()) ; i++) {
				det = det.multiply(lu[i][i]);
			}
			return det;
		}
	}

	public boolean isSquare() {
		return (getColumnDimension()) == (getRowDimension());
	}

	public boolean isSingular() {
		if ((lu) == null) {
			try {
				luDecompose();
				return false;
			} catch (org.apache.commons.math.linear.InvalidMatrixException ex) {
				return true;
			}
		} else {
			return false;
		}
	}

	public int getRowDimension() {
		return data.length;
	}

	public int getColumnDimension() {
		return data[0].length;
	}

	public java.math.BigDecimal getTrace() throws java.lang.IllegalArgumentException {
		if (!(isSquare())) {
			throw new org.apache.commons.math.linear.NonSquareMatrixException(getRowDimension() , getColumnDimension());
		} 
		java.math.BigDecimal trace = data[0][0];
		for (int i = 1 ; i < (getRowDimension()) ; i++) {
			trace = trace.add(data[i][i]);
		}
		return trace;
	}

	public java.math.BigDecimal[] operate(java.math.BigDecimal[] v) throws java.lang.IllegalArgumentException {
		if ((v.length) != (getColumnDimension())) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("vector length mismatch: got {0} but expected {1}", v.length, getColumnDimension());
		} 
		final int nRows = getRowDimension();
		final int nCols = getColumnDimension();
		final java.math.BigDecimal[] out = new java.math.BigDecimal[nRows];
		for (int row = 0 ; row < nRows ; row++) {
			java.math.BigDecimal sum = ZERO;
			for (int i = 0 ; i < nCols ; i++) {
				sum = sum.add(data[row][i].multiply(v[i]));
			}
			out[row] = sum;
		}
		return out;
	}

	public java.math.BigDecimal[] operate(double[] v) throws java.lang.IllegalArgumentException {
		final java.math.BigDecimal[] bd = new java.math.BigDecimal[v.length];
		for (int i = 0 ; i < (bd.length) ; i++) {
			bd[i] = new java.math.BigDecimal(v[i]);
		}
		return operate(bd);
	}

	public java.math.BigDecimal[] preMultiply(java.math.BigDecimal[] v) throws java.lang.IllegalArgumentException {
		final int nRows = getRowDimension();
		if ((v.length) != nRows) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("vector length mismatch: got {0} but expected {1}", v.length, nRows);
		} 
		final int nCols = getColumnDimension();
		final java.math.BigDecimal[] out = new java.math.BigDecimal[nCols];
		for (int col = 0 ; col < nCols ; col++) {
			java.math.BigDecimal sum = ZERO;
			for (int i = 0 ; i < nRows ; i++) {
				sum = sum.add(data[i][col].multiply(v[i]));
			}
			out[col] = sum;
		}
		return out;
	}

	public java.math.BigDecimal[] solve(java.math.BigDecimal[] b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException {
		final int nRows = getRowDimension();
		if ((b.length) != nRows) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("vector length mismatch: got {0} but expected {1}", b.length, nRows);
		} 
		final org.apache.commons.math.linear.BigMatrix bMatrix = new org.apache.commons.math.linear.BigMatrixImpl(b);
		final java.math.BigDecimal[][] solution = ((org.apache.commons.math.linear.BigMatrixImpl)(solve(bMatrix))).getDataRef();
		final java.math.BigDecimal[] out = new java.math.BigDecimal[nRows];
		for (int row = 0 ; row < nRows ; row++) {
			out[row] = solution[row][0];
		}
		return out;
	}

	public java.math.BigDecimal[] solve(double[] b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException {
		final java.math.BigDecimal[] bd = new java.math.BigDecimal[b.length];
		for (int i = 0 ; i < (bd.length) ; i++) {
			bd[i] = new java.math.BigDecimal(b[i]);
		}
		return solve(bd);
	}

	public org.apache.commons.math.linear.BigMatrix solve(org.apache.commons.math.linear.BigMatrix b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException {
		if ((b.getRowDimension()) != (getRowDimension())) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("dimensions mismatch: got {0}x{1} but expected {2}x{3}", b.getRowDimension(), b.getColumnDimension(), getRowDimension(), "n");
		} 
		if (!(isSquare())) {
			throw new org.apache.commons.math.linear.NonSquareMatrixException(getRowDimension() , getColumnDimension());
		} 
		if (isSingular()) {
			throw new org.apache.commons.math.linear.SingularMatrixException();
		} 
		final int nCol = getColumnDimension();
		final int nColB = b.getColumnDimension();
		final int nRowB = b.getRowDimension();
		final java.math.BigDecimal[][] bp = new java.math.BigDecimal[nRowB][nColB];
		for (int row = 0 ; row < nRowB ; row++) {
			final java.math.BigDecimal[] bpRow = bp[row];
			for (int col = 0 ; col < nColB ; col++) {
				bpRow[col] = b.getEntry(permutation[row], col);
			}
		}
		for (int col = 0 ; col < nCol ; col++) {
			for (int i = col + 1 ; i < nCol ; i++) {
				final java.math.BigDecimal[] bpI = bp[i];
				final java.math.BigDecimal[] luI = lu[i];
				for (int j = 0 ; j < nColB ; j++) {
					bpI[j] = bpI[j].subtract(bp[col][j].multiply(luI[col]));
				}
			}
		}
		for (int col = nCol - 1 ; col >= 0 ; col--) {
			final java.math.BigDecimal[] bpCol = bp[col];
			final java.math.BigDecimal luDiag = lu[col][col];
			for (int j = 0 ; j < nColB ; j++) {
				bpCol[j] = bpCol[j].divide(luDiag, scale, roundingMode);
			}
			for (int i = 0 ; i < col ; i++) {
				final java.math.BigDecimal[] bpI = bp[i];
				final java.math.BigDecimal[] luI = lu[i];
				for (int j = 0 ; j < nColB ; j++) {
					bpI[j] = bpI[j].subtract(bp[col][j].multiply(luI[col]));
				}
			}
		}
		return new org.apache.commons.math.linear.BigMatrixImpl(bp , false);
	}

	public void luDecompose() throws org.apache.commons.math.linear.InvalidMatrixException {
		final int nRows = getRowDimension();
		final int nCols = getColumnDimension();
		if (nRows != nCols) {
			throw new org.apache.commons.math.linear.NonSquareMatrixException(getRowDimension() , getColumnDimension());
		} 
		lu = getData();
		permutation = new int[nRows];
		for (int row = 0 ; row < nRows ; row++) {
			permutation[row] = row;
		}
		parity = 1;
		for (int col = 0 ; col < nCols ; col++) {
			java.math.BigDecimal sum = ZERO;
			for (int row = 0 ; row < col ; row++) {
				final java.math.BigDecimal[] luRow = lu[row];
				sum = luRow[col];
				for (int i = 0 ; i < row ; i++) {
					sum = sum.subtract(luRow[i].multiply(lu[i][col]));
				}
				luRow[col] = sum;
			}
			int max = col;
			java.math.BigDecimal largest = ZERO;
			for (int row = col ; row < nRows ; row++) {
				final java.math.BigDecimal[] luRow = lu[row];
				sum = luRow[col];
				for (int i = 0 ; i < col ; i++) {
					sum = sum.subtract(luRow[i].multiply(lu[i][col]));
				}
				luRow[col] = sum;
				if ((sum.abs().compareTo(largest)) == 1) {
					largest = sum.abs();
					max = row;
				} 
			}
			if ((lu[max][col].abs().compareTo(TOO_SMALL)) <= 0) {
				lu = null;
				throw new org.apache.commons.math.linear.SingularMatrixException();
			} 
			if (max != col) {
				java.math.BigDecimal tmp = ZERO;
				for (int i = 0 ; i < nCols ; i++) {
					tmp = lu[max][i];
					lu[max][i] = lu[col][i];
					lu[col][i] = tmp;
				}
				int temp = permutation[max];
				permutation[max] = permutation[col];
				permutation[col] = temp;
				parity = -(parity);
			} 
			final java.math.BigDecimal luDiag = lu[col][col];
			for (int row = col + 1 ; row < nRows ; row++) {
				final java.math.BigDecimal[] luRow = lu[row];
				luRow[col] = luRow[col].divide(luDiag, scale, roundingMode);
			}
		}
	}

	@java.lang.Override
	public java.lang.String toString() {
		java.lang.StringBuffer res = new java.lang.StringBuffer();
		res.append("BigMatrixImpl{");
		if ((data) != null) {
			for (int i = 0 ; i < (data.length) ; i++) {
				if (i > 0) {
					res.append(",");
				} 
				res.append("{");
				for (int j = 0 ; j < (data[0].length) ; j++) {
					if (j > 0) {
						res.append(",");
					} 
					res.append(data[i][j]);
				}
				res.append("}");
			}
		} 
		res.append("}");
		return res.toString();
	}

	@java.lang.Override
	public boolean equals(java.lang.Object object) {
		if (object == (this)) {
			return true;
		} 
		if ((object instanceof org.apache.commons.math.linear.BigMatrixImpl) == false) {
			return false;
		} 
		final org.apache.commons.math.linear.BigMatrix m = ((org.apache.commons.math.linear.BigMatrix)(object));
		final int nRows = getRowDimension();
		final int nCols = getColumnDimension();
		if (((m.getColumnDimension()) != nCols) || ((m.getRowDimension()) != nRows)) {
			return false;
		} 
		for (int row = 0 ; row < nRows ; row++) {
			final java.math.BigDecimal[] dataRow = data[row];
			for (int col = 0 ; col < nCols ; col++) {
				if (!(dataRow[col].equals(m.getEntry(row, col)))) {
					return false;
				} 
			}
		}
		return true;
	}

	@java.lang.Override
	public int hashCode() {
		int ret = 7;
		final int nRows = getRowDimension();
		final int nCols = getColumnDimension();
		ret = (ret * 31) + nRows;
		ret = (ret * 31) + nCols;
		for (int row = 0 ; row < nRows ; row++) {
			final java.math.BigDecimal[] dataRow = data[row];
			for (int col = 0 ; col < nCols ; col++) {
				ret = (ret * 31) + (((11 * (row + 1)) + (17 * (col + 1))) * (dataRow[col].hashCode()));
			}
		}
		return ret;
	}

	protected org.apache.commons.math.linear.BigMatrix getLUMatrix() throws org.apache.commons.math.linear.InvalidMatrixException {
		if ((lu) == null) {
			luDecompose();
		} 
		return new org.apache.commons.math.linear.BigMatrixImpl(lu);
	}

	protected int[] getPermutation() {
		final int[] out = new int[permutation.length];
		java.lang.System.arraycopy(permutation, 0, out, 0, permutation.length);
		return out;
	}

	private java.math.BigDecimal[][] copyOut() {
		final int nRows = getRowDimension();
		final java.math.BigDecimal[][] out = new java.math.BigDecimal[nRows][getColumnDimension()];
		for (int i = 0 ; i < nRows ; i++) {
			java.lang.System.arraycopy(data[i], 0, out[i], 0, data[i].length);
		}
		return out;
	}

	private void copyIn(java.math.BigDecimal[][] in) {
		setSubMatrix(in, 0, 0);
	}

	private void copyIn(double[][] in) {
		final int nRows = in.length;
		final int nCols = in[0].length;
		data = new java.math.BigDecimal[nRows][nCols];
		for (int i = 0 ; i < nRows ; i++) {
			final java.math.BigDecimal[] dataI = data[i];
			final double[] inI = in[i];
			for (int j = 0 ; j < nCols ; j++) {
				dataI[j] = new java.math.BigDecimal(inI[j]);
			}
		}
		lu = null;
	}

	private void copyIn(java.lang.String[][] in) {
		final int nRows = in.length;
		final int nCols = in[0].length;
		data = new java.math.BigDecimal[nRows][nCols];
		for (int i = 0 ; i < nRows ; i++) {
			final java.math.BigDecimal[] dataI = data[i];
			final java.lang.String[] inI = in[i];
			for (int j = 0 ; j < nCols ; j++) {
				dataI[j] = new java.math.BigDecimal(inI[j]);
			}
		}
		lu = null;
	}
}

