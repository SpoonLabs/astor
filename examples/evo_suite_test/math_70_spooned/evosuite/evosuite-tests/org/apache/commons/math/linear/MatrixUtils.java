package org.apache.commons.math.linear;


public class MatrixUtils {
	private MatrixUtils() {
		super();
	}

	public static org.apache.commons.math.linear.RealMatrix createRealMatrix(final int rows, final int columns) {
		return (rows * columns) <= 4096 ? new org.apache.commons.math.linear.Array2DRowRealMatrix(rows , columns) : new org.apache.commons.math.linear.BlockRealMatrix(rows , columns);
	}

	public static <T extends org.apache.commons.math.FieldElement<T>>org.apache.commons.math.linear.FieldMatrix<T> createFieldMatrix(final org.apache.commons.math.Field<T> field, final int rows, final int columns) {
		return (rows * columns) <= 4096 ? new org.apache.commons.math.linear.Array2DRowFieldMatrix<T>(field , rows , columns) : new org.apache.commons.math.linear.BlockFieldMatrix<T>(field , rows , columns);
	}

	public static org.apache.commons.math.linear.RealMatrix createRealMatrix(double[][] data) {
		return ((data.length) * (data[0].length)) <= 4096 ? new org.apache.commons.math.linear.Array2DRowRealMatrix(data) : new org.apache.commons.math.linear.BlockRealMatrix(data);
	}

	public static <T extends org.apache.commons.math.FieldElement<T>>org.apache.commons.math.linear.FieldMatrix<T> createFieldMatrix(T[][] data) {
		return ((data.length) * (data[0].length)) <= 4096 ? new org.apache.commons.math.linear.Array2DRowFieldMatrix<T>(data) : new org.apache.commons.math.linear.BlockFieldMatrix<T>(data);
	}

	public static org.apache.commons.math.linear.RealMatrix createRealIdentityMatrix(int dimension) {
		final org.apache.commons.math.linear.RealMatrix m = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(dimension, dimension);
		for (int i = 0 ; i < dimension ; ++i) {
			m.setEntry(i, i, 1.0);
		}
		return m;
	}

	public static <T extends org.apache.commons.math.FieldElement<T>>org.apache.commons.math.linear.FieldMatrix<T> createFieldIdentityMatrix(final org.apache.commons.math.Field<T> field, final int dimension) {
		final T zero = field.getZero();
		final T one = field.getOne();
		@java.lang.SuppressWarnings(value = "unchecked")
		final T[][] d = ((T[][])(java.lang.reflect.Array.newInstance(zero.getClass(), new int[]{ dimension , dimension })));
		for (int row = 0 ; row < dimension ; row++) {
			final T[] dRow = d[row];
			java.util.Arrays.fill(dRow, zero);
			dRow[row] = one;
		}
		return new org.apache.commons.math.linear.Array2DRowFieldMatrix<T>(d , false);
	}

	@java.lang.Deprecated
	public static org.apache.commons.math.linear.BigMatrix createBigIdentityMatrix(int dimension) {
		final java.math.BigDecimal[][] d = new java.math.BigDecimal[dimension][dimension];
		for (int row = 0 ; row < dimension ; row++) {
			final java.math.BigDecimal[] dRow = d[row];
			java.util.Arrays.fill(dRow, org.apache.commons.math.linear.BigMatrixImpl.ZERO);
			dRow[row] = org.apache.commons.math.linear.BigMatrixImpl.ONE;
		}
		return new org.apache.commons.math.linear.BigMatrixImpl(d , false);
	}

	public static org.apache.commons.math.linear.RealMatrix createRealDiagonalMatrix(final double[] diagonal) {
		final org.apache.commons.math.linear.RealMatrix m = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(diagonal.length, diagonal.length);
		for (int i = 0 ; i < (diagonal.length) ; ++i) {
			m.setEntry(i, i, diagonal[i]);
		}
		return m;
	}

	public static <T extends org.apache.commons.math.FieldElement<T>>org.apache.commons.math.linear.FieldMatrix<T> createFieldDiagonalMatrix(final T[] diagonal) {
		final org.apache.commons.math.linear.FieldMatrix<T> m = org.apache.commons.math.linear.MatrixUtils.createFieldMatrix(diagonal[0].getField(), diagonal.length, diagonal.length);
		for (int i = 0 ; i < (diagonal.length) ; ++i) {
			m.setEntry(i, i, diagonal[i]);
		}
		return m;
	}

	@java.lang.Deprecated
	public static org.apache.commons.math.linear.BigMatrix createBigMatrix(double[][] data) {
		return new org.apache.commons.math.linear.BigMatrixImpl(data);
	}

	@java.lang.Deprecated
	public static org.apache.commons.math.linear.BigMatrix createBigMatrix(java.math.BigDecimal[][] data) {
		return new org.apache.commons.math.linear.BigMatrixImpl(data);
	}

	@java.lang.Deprecated
	public static org.apache.commons.math.linear.BigMatrix createBigMatrix(java.math.BigDecimal[][] data, boolean copyArray) {
		return new org.apache.commons.math.linear.BigMatrixImpl(data , copyArray);
	}

	@java.lang.Deprecated
	public static org.apache.commons.math.linear.BigMatrix createBigMatrix(java.lang.String[][] data) {
		return new org.apache.commons.math.linear.BigMatrixImpl(data);
	}

	public static org.apache.commons.math.linear.RealVector createRealVector(double[] data) {
		return new org.apache.commons.math.linear.ArrayRealVector(data , true);
	}

	public static <T extends org.apache.commons.math.FieldElement<T>>org.apache.commons.math.linear.FieldVector<T> createFieldVector(final T[] data) {
		return new org.apache.commons.math.linear.ArrayFieldVector<T>(data , true);
	}

	public static org.apache.commons.math.linear.RealMatrix createRowRealMatrix(double[] rowData) {
		final int nCols = rowData.length;
		final org.apache.commons.math.linear.RealMatrix m = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(1, nCols);
		for (int i = 0 ; i < nCols ; ++i) {
			m.setEntry(0, i, rowData[i]);
		}
		return m;
	}

	public static <T extends org.apache.commons.math.FieldElement<T>>org.apache.commons.math.linear.FieldMatrix<T> createRowFieldMatrix(final T[] rowData) {
		final int nCols = rowData.length;
		if (nCols == 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("matrix must have at least one column");
		} 
		final org.apache.commons.math.linear.FieldMatrix<T> m = org.apache.commons.math.linear.MatrixUtils.createFieldMatrix(rowData[0].getField(), 1, nCols);
		for (int i = 0 ; i < nCols ; ++i) {
			m.setEntry(0, i, rowData[i]);
		}
		return m;
	}

	@java.lang.Deprecated
	public static org.apache.commons.math.linear.BigMatrix createRowBigMatrix(double[] rowData) {
		final int nCols = rowData.length;
		final java.math.BigDecimal[][] data = new java.math.BigDecimal[1][nCols];
		for (int i = 0 ; i < nCols ; ++i) {
			data[0][i] = new java.math.BigDecimal(rowData[i]);
		}
		return new org.apache.commons.math.linear.BigMatrixImpl(data , false);
	}

	@java.lang.Deprecated
	public static org.apache.commons.math.linear.BigMatrix createRowBigMatrix(java.math.BigDecimal[] rowData) {
		final int nCols = rowData.length;
		final java.math.BigDecimal[][] data = new java.math.BigDecimal[1][nCols];
		java.lang.System.arraycopy(rowData, 0, data[0], 0, nCols);
		return new org.apache.commons.math.linear.BigMatrixImpl(data , false);
	}

	@java.lang.Deprecated
	public static org.apache.commons.math.linear.BigMatrix createRowBigMatrix(java.lang.String[] rowData) {
		final int nCols = rowData.length;
		final java.math.BigDecimal[][] data = new java.math.BigDecimal[1][nCols];
		for (int i = 0 ; i < nCols ; ++i) {
			data[0][i] = new java.math.BigDecimal(rowData[i]);
		}
		return new org.apache.commons.math.linear.BigMatrixImpl(data , false);
	}

	public static org.apache.commons.math.linear.RealMatrix createColumnRealMatrix(double[] columnData) {
		final int nRows = columnData.length;
		final org.apache.commons.math.linear.RealMatrix m = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(nRows, 1);
		for (int i = 0 ; i < nRows ; ++i) {
			m.setEntry(i, 0, columnData[i]);
		}
		return m;
	}

	public static <T extends org.apache.commons.math.FieldElement<T>>org.apache.commons.math.linear.FieldMatrix<T> createColumnFieldMatrix(final T[] columnData) {
		final int nRows = columnData.length;
		if (nRows == 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("matrix must have at least one row");
		} 
		final org.apache.commons.math.linear.FieldMatrix<T> m = org.apache.commons.math.linear.MatrixUtils.createFieldMatrix(columnData[0].getField(), nRows, 1);
		for (int i = 0 ; i < nRows ; ++i) {
			m.setEntry(i, 0, columnData[i]);
		}
		return m;
	}

	@java.lang.Deprecated
	public static org.apache.commons.math.linear.BigMatrix createColumnBigMatrix(double[] columnData) {
		final int nRows = columnData.length;
		final java.math.BigDecimal[][] data = new java.math.BigDecimal[nRows][1];
		for (int row = 0 ; row < nRows ; row++) {
			data[row][0] = new java.math.BigDecimal(columnData[row]);
		}
		return new org.apache.commons.math.linear.BigMatrixImpl(data , false);
	}

	@java.lang.Deprecated
	public static org.apache.commons.math.linear.BigMatrix createColumnBigMatrix(java.math.BigDecimal[] columnData) {
		final int nRows = columnData.length;
		final java.math.BigDecimal[][] data = new java.math.BigDecimal[nRows][1];
		for (int row = 0 ; row < nRows ; row++) {
			data[row][0] = columnData[row];
		}
		return new org.apache.commons.math.linear.BigMatrixImpl(data , false);
	}

	@java.lang.Deprecated
	public static org.apache.commons.math.linear.BigMatrix createColumnBigMatrix(java.lang.String[] columnData) {
		int nRows = columnData.length;
		final java.math.BigDecimal[][] data = new java.math.BigDecimal[nRows][1];
		for (int row = 0 ; row < nRows ; row++) {
			data[row][0] = new java.math.BigDecimal(columnData[row]);
		}
		return new org.apache.commons.math.linear.BigMatrixImpl(data , false);
	}

	public static void checkRowIndex(final org.apache.commons.math.linear.AnyMatrix m, final int row) {
		if ((row < 0) || (row >= (m.getRowDimension()))) {
			throw new org.apache.commons.math.linear.MatrixIndexException("row index {0} out of allowed range [{1}, {2}]" , row , 0 , ((m.getRowDimension()) - 1));
		} 
	}

	public static void checkColumnIndex(final org.apache.commons.math.linear.AnyMatrix m, final int column) throws org.apache.commons.math.linear.MatrixIndexException {
		if ((column < 0) || (column >= (m.getColumnDimension()))) {
			throw new org.apache.commons.math.linear.MatrixIndexException("column index {0} out of allowed range [{1}, {2}]" , column , 0 , ((m.getColumnDimension()) - 1));
		} 
	}

	public static void checkSubMatrixIndex(final org.apache.commons.math.linear.AnyMatrix m, final int startRow, final int endRow, final int startColumn, final int endColumn) {
		org.apache.commons.math.linear.MatrixUtils.checkRowIndex(m, startRow);
		org.apache.commons.math.linear.MatrixUtils.checkRowIndex(m, endRow);
		if (startRow > endRow) {
			throw new org.apache.commons.math.linear.MatrixIndexException("initial row {0} after final row {1}" , startRow , endRow);
		} 
		org.apache.commons.math.linear.MatrixUtils.checkColumnIndex(m, startColumn);
		org.apache.commons.math.linear.MatrixUtils.checkColumnIndex(m, endColumn);
		if (startColumn > endColumn) {
			throw new org.apache.commons.math.linear.MatrixIndexException("initial column {0} after final column {1}" , startColumn , endColumn);
		} 
	}

	public static void checkSubMatrixIndex(final org.apache.commons.math.linear.AnyMatrix m, final int[] selectedRows, final int[] selectedColumns) throws org.apache.commons.math.linear.MatrixIndexException {
		if (((selectedRows.length) * (selectedColumns.length)) == 0) {
			if ((selectedRows.length) == 0) {
				throw new org.apache.commons.math.linear.MatrixIndexException("empty selected row index array");
			} 
			throw new org.apache.commons.math.linear.MatrixIndexException("empty selected column index array");
		} 
		for (final int row : selectedRows) {
			org.apache.commons.math.linear.MatrixUtils.checkRowIndex(m, row);
		}
		for (final int column : selectedColumns) {
			org.apache.commons.math.linear.MatrixUtils.checkColumnIndex(m, column);
		}
	}

	public static void checkAdditionCompatible(final org.apache.commons.math.linear.AnyMatrix left, final org.apache.commons.math.linear.AnyMatrix right) throws java.lang.IllegalArgumentException {
		if (((left.getRowDimension()) != (right.getRowDimension())) || ((left.getColumnDimension()) != (right.getColumnDimension()))) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0}x{1} and {2}x{3} matrices are not addition compatible", left.getRowDimension(), left.getColumnDimension(), right.getRowDimension(), right.getColumnDimension());
		} 
	}

	public static void checkSubtractionCompatible(final org.apache.commons.math.linear.AnyMatrix left, final org.apache.commons.math.linear.AnyMatrix right) throws java.lang.IllegalArgumentException {
		if (((left.getRowDimension()) != (right.getRowDimension())) || ((left.getColumnDimension()) != (right.getColumnDimension()))) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0}x{1} and {2}x{3} matrices are not subtraction compatible", left.getRowDimension(), left.getColumnDimension(), right.getRowDimension(), right.getColumnDimension());
		} 
	}

	public static void checkMultiplicationCompatible(final org.apache.commons.math.linear.AnyMatrix left, final org.apache.commons.math.linear.AnyMatrix right) throws java.lang.IllegalArgumentException {
		if ((left.getColumnDimension()) != (right.getRowDimension())) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0}x{1} and {2}x{3} matrices are not multiplication compatible", left.getRowDimension(), left.getColumnDimension(), right.getRowDimension(), right.getColumnDimension());
		} 
	}

	public static org.apache.commons.math.linear.Array2DRowRealMatrix fractionMatrixToRealMatrix(final org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m) {
		final org.apache.commons.math.linear.MatrixUtils.FractionMatrixConverter converter = new org.apache.commons.math.linear.MatrixUtils.FractionMatrixConverter();
		m.walkInOptimizedOrder(converter);
		return converter.getConvertedMatrix();
	}

	private static class FractionMatrixConverter extends org.apache.commons.math.linear.DefaultFieldMatrixPreservingVisitor<org.apache.commons.math.fraction.Fraction> {
		private double[][] data;

		public FractionMatrixConverter() {
			super(org.apache.commons.math.fraction.Fraction.ZERO);
		}

		@java.lang.Override
		public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn) {
			data = new double[rows][columns];
		}

		@java.lang.Override
		public void visit(int row, int column, org.apache.commons.math.fraction.Fraction value) {
			data[row][column] = value.doubleValue();
		}

		org.apache.commons.math.linear.Array2DRowRealMatrix getConvertedMatrix() {
			return new org.apache.commons.math.linear.Array2DRowRealMatrix(data , false);
		}
	}

	public static org.apache.commons.math.linear.Array2DRowRealMatrix bigFractionMatrixToRealMatrix(final org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.BigFraction> m) {
		final org.apache.commons.math.linear.MatrixUtils.BigFractionMatrixConverter converter = new org.apache.commons.math.linear.MatrixUtils.BigFractionMatrixConverter();
		m.walkInOptimizedOrder(converter);
		return converter.getConvertedMatrix();
	}

	private static class BigFractionMatrixConverter extends org.apache.commons.math.linear.DefaultFieldMatrixPreservingVisitor<org.apache.commons.math.fraction.BigFraction> {
		private double[][] data;

		public BigFractionMatrixConverter() {
			super(org.apache.commons.math.fraction.BigFraction.ZERO);
		}

		@java.lang.Override
		public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn) {
			data = new double[rows][columns];
		}

		@java.lang.Override
		public void visit(int row, int column, org.apache.commons.math.fraction.BigFraction value) {
			data[row][column] = value.doubleValue();
		}

		org.apache.commons.math.linear.Array2DRowRealMatrix getConvertedMatrix() {
			return new org.apache.commons.math.linear.Array2DRowRealMatrix(data , false);
		}
	}

	public static void serializeRealVector(final org.apache.commons.math.linear.RealVector vector, final java.io.ObjectOutputStream oos) throws java.io.IOException {
		final int n = vector.getDimension();
		oos.writeInt(n);
		for (int i = 0 ; i < n ; ++i) {
			oos.writeDouble(vector.getEntry(i));
		}
	}

	public static void deserializeRealVector(final java.lang.Object instance, final java.lang.String fieldName, final java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
		try {
			final int n = ois.readInt();
			final double[] data = new double[n];
			for (int i = 0 ; i < n ; ++i) {
				data[i] = ois.readDouble();
			}
			final org.apache.commons.math.linear.RealVector vector = new org.apache.commons.math.linear.ArrayRealVector(data , false);
			final java.lang.reflect.Field f = instance.getClass().getDeclaredField(fieldName);
			f.setAccessible(true);
			f.set(instance, vector);
		} catch (java.lang.NoSuchFieldException nsfe) {
			java.io.IOException ioe = new java.io.IOException();
			ioe.initCause(nsfe);
			throw ioe;
		} catch (java.lang.IllegalAccessException iae) {
			java.io.IOException ioe = new java.io.IOException();
			ioe.initCause(iae);
			throw ioe;
		}
	}

	public static void serializeRealMatrix(final org.apache.commons.math.linear.RealMatrix matrix, final java.io.ObjectOutputStream oos) throws java.io.IOException {
		final int n = matrix.getRowDimension();
		final int m = matrix.getColumnDimension();
		oos.writeInt(n);
		oos.writeInt(m);
		for (int i = 0 ; i < n ; ++i) {
			for (int j = 0 ; j < m ; ++j) {
				oos.writeDouble(matrix.getEntry(i, j));
			}
		}
	}

	public static void deserializeRealMatrix(final java.lang.Object instance, final java.lang.String fieldName, final java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
		try {
			final int n = ois.readInt();
			final int m = ois.readInt();
			final double[][] data = new double[n][m];
			for (int i = 0 ; i < n ; ++i) {
				final double[] dataI = data[i];
				for (int j = 0 ; j < m ; ++j) {
					dataI[j] = ois.readDouble();
				}
			}
			final org.apache.commons.math.linear.RealMatrix matrix = new org.apache.commons.math.linear.Array2DRowRealMatrix(data , false);
			final java.lang.reflect.Field f = instance.getClass().getDeclaredField(fieldName);
			f.setAccessible(true);
			f.set(instance, matrix);
		} catch (java.lang.NoSuchFieldException nsfe) {
			java.io.IOException ioe = new java.io.IOException();
			ioe.initCause(nsfe);
			throw ioe;
		} catch (java.lang.IllegalAccessException iae) {
			java.io.IOException ioe = new java.io.IOException();
			ioe.initCause(iae);
			throw ioe;
		}
	}
}

