package org.apache.commons.math.linear;


public class FieldLUDecompositionImpl<T extends org.apache.commons.math.FieldElement<T>> implements org.apache.commons.math.linear.FieldLUDecomposition<T> {
	private final org.apache.commons.math.Field<T> field;

	private T[][] lu;

	private int[] pivot;

	private boolean even;

	private boolean singular;

	private org.apache.commons.math.linear.FieldMatrix<T> cachedL;

	private org.apache.commons.math.linear.FieldMatrix<T> cachedU;

	private org.apache.commons.math.linear.FieldMatrix<T> cachedP;

	public FieldLUDecompositionImpl(org.apache.commons.math.linear.FieldMatrix<T> matrix) throws org.apache.commons.math.linear.NonSquareMatrixException {
		if (!(matrix.isSquare())) {
			throw new org.apache.commons.math.linear.NonSquareMatrixException(matrix.getRowDimension() , matrix.getColumnDimension());
		} 
		final int m = matrix.getColumnDimension();
		field = matrix.getField();
		lu = matrix.getData();
		pivot = new int[m];
		cachedL = null;
		cachedU = null;
		cachedP = null;
		for (int row = 0 ; row < m ; row++) {
			pivot[row] = row;
		}
		even = true;
		singular = false;
		for (int col = 0 ; col < m ; col++) {
			T sum = field.getZero();
			for (int row = 0 ; row < col ; row++) {
				final T[] luRow = lu[row];
				sum = luRow[col];
				for (int i = 0 ; i < row ; i++) {
					sum = sum.subtract(luRow[i].multiply(lu[i][col]));
				}
				luRow[col] = sum;
			}
			int nonZero = col;
			for (int row = col ; row < m ; row++) {
				final T[] luRow = lu[row];
				sum = luRow[col];
				for (int i = 0 ; i < col ; i++) {
					sum = sum.subtract(luRow[i].multiply(lu[i][col]));
				}
				luRow[col] = sum;
				if (lu[nonZero][col].equals(field.getZero())) {
					++nonZero;
				} 
			}
			if (nonZero >= m) {
				singular = true;
				return ;
			} 
			if (nonZero != col) {
				T tmp = field.getZero();
				for (int i = 0 ; i < m ; i++) {
					tmp = lu[nonZero][i];
					lu[nonZero][i] = lu[col][i];
					lu[col][i] = tmp;
				}
				int temp = pivot[nonZero];
				pivot[nonZero] = pivot[col];
				pivot[col] = temp;
				even = !(even);
			} 
			final T luDiag = lu[col][col];
			for (int row = col + 1 ; row < m ; row++) {
				final T[] luRow = lu[row];
				luRow[col] = luRow[col].divide(luDiag);
			}
		}
	}

	public org.apache.commons.math.linear.FieldMatrix<T> getL() {
		if (((cachedL) == null) && (!(singular))) {
			final int m = pivot.length;
			cachedL = new org.apache.commons.math.linear.Array2DRowFieldMatrix<T>(field , m , m);
			for (int i = 0 ; i < m ; ++i) {
				final T[] luI = lu[i];
				for (int j = 0 ; j < i ; ++j) {
					cachedL.setEntry(i, j, luI[j]);
				}
				cachedL.setEntry(i, i, field.getOne());
			}
		} 
		return cachedL;
	}

	public org.apache.commons.math.linear.FieldMatrix<T> getU() {
		if (((cachedU) == null) && (!(singular))) {
			final int m = pivot.length;
			cachedU = new org.apache.commons.math.linear.Array2DRowFieldMatrix<T>(field , m , m);
			for (int i = 0 ; i < m ; ++i) {
				final T[] luI = lu[i];
				for (int j = i ; j < m ; ++j) {
					cachedU.setEntry(i, j, luI[j]);
				}
			}
		} 
		return cachedU;
	}

	public org.apache.commons.math.linear.FieldMatrix<T> getP() {
		if (((cachedP) == null) && (!(singular))) {
			final int m = pivot.length;
			cachedP = new org.apache.commons.math.linear.Array2DRowFieldMatrix<T>(field , m , m);
			for (int i = 0 ; i < m ; ++i) {
				cachedP.setEntry(i, pivot[i], field.getOne());
			}
		} 
		return cachedP;
	}

	public int[] getPivot() {
		return pivot.clone();
	}

	public T getDeterminant() {
		if (singular) {
			return field.getZero();
		} else {
			final int m = pivot.length;
			T determinant = even ? field.getOne() : field.getZero().subtract(field.getOne());
			for (int i = 0 ; i < m ; i++) {
				determinant = determinant.multiply(lu[i][i]);
			}
			return determinant;
		}
	}

	public org.apache.commons.math.linear.FieldDecompositionSolver<T> getSolver() {
		return new org.apache.commons.math.linear.FieldLUDecompositionImpl.Solver<T>(field , lu , pivot , singular);
	}

	private static class Solver<T extends org.apache.commons.math.FieldElement<T>> implements org.apache.commons.math.linear.FieldDecompositionSolver<T> {
		private static final long serialVersionUID = -6353105415121373022L;

		private final org.apache.commons.math.Field<T> field;

		private final T[][] lu;

		private final int[] pivot;

		private final boolean singular;

		private Solver(final org.apache.commons.math.Field<T> field ,final T[][] lu ,final int[] pivot ,final boolean singular) {
			this.field = field;
			this.lu = lu;
			this.pivot = pivot;
			this.singular = singular;
		}

		public boolean isNonSingular() {
			return !(singular);
		}

		public T[] solve(T[] b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException {
			final int m = pivot.length;
			if ((b.length) != m) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("vector length mismatch: got {0} but expected {1}", b.length, m);
			} 
			if (singular) {
				throw new org.apache.commons.math.linear.SingularMatrixException();
			} 
			@java.lang.SuppressWarnings(value = "unchecked")
			final T[] bp = ((T[])(java.lang.reflect.Array.newInstance(field.getZero().getClass(), m)));
			for (int row = 0 ; row < m ; row++) {
				bp[row] = b[pivot[row]];
			}
			for (int col = 0 ; col < m ; col++) {
				final T bpCol = bp[col];
				for (int i = col + 1 ; i < m ; i++) {
					bp[i] = bp[i].subtract(bpCol.multiply(lu[i][col]));
				}
			}
			for (int col = m - 1 ; col >= 0 ; col--) {
				bp[col] = bp[col].divide(lu[col][col]);
				final T bpCol = bp[col];
				for (int i = 0 ; i < col ; i++) {
					bp[i] = bp[i].subtract(bpCol.multiply(lu[i][col]));
				}
			}
			return bp;
		}

		public org.apache.commons.math.linear.FieldVector<T> solve(org.apache.commons.math.linear.FieldVector<T> b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException {
			try {
				return solve(((org.apache.commons.math.linear.ArrayFieldVector<T>)(b)));
			} catch (java.lang.ClassCastException cce) {
				final int m = pivot.length;
				if ((b.getDimension()) != m) {
					throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("vector length mismatch: got {0} but expected {1}", b.getDimension(), m);
				} 
				if (singular) {
					throw new org.apache.commons.math.linear.SingularMatrixException();
				} 
				@java.lang.SuppressWarnings(value = "unchecked")
				final T[] bp = ((T[])(java.lang.reflect.Array.newInstance(field.getZero().getClass(), m)));
				for (int row = 0 ; row < m ; row++) {
					bp[row] = b.getEntry(pivot[row]);
				}
				for (int col = 0 ; col < m ; col++) {
					final T bpCol = bp[col];
					for (int i = col + 1 ; i < m ; i++) {
						bp[i] = bp[i].subtract(bpCol.multiply(lu[i][col]));
					}
				}
				for (int col = m - 1 ; col >= 0 ; col--) {
					bp[col] = bp[col].divide(lu[col][col]);
					final T bpCol = bp[col];
					for (int i = 0 ; i < col ; i++) {
						bp[i] = bp[i].subtract(bpCol.multiply(lu[i][col]));
					}
				}
				return new org.apache.commons.math.linear.ArrayFieldVector<T>(bp , false);
			}
		}

		public org.apache.commons.math.linear.ArrayFieldVector<T> solve(org.apache.commons.math.linear.ArrayFieldVector<T> b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException {
			return new org.apache.commons.math.linear.ArrayFieldVector<T>(solve(b.getDataRef()) , false);
		}

		public org.apache.commons.math.linear.FieldMatrix<T> solve(org.apache.commons.math.linear.FieldMatrix<T> b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException {
			final int m = pivot.length;
			if ((b.getRowDimension()) != m) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("dimensions mismatch: got {0}x{1} but expected {2}x{3}", b.getRowDimension(), b.getColumnDimension(), m, "n");
			} 
			if (singular) {
				throw new org.apache.commons.math.linear.SingularMatrixException();
			} 
			final int nColB = b.getColumnDimension();
			@java.lang.SuppressWarnings(value = "unchecked")
			final T[][] bp = ((T[][])(java.lang.reflect.Array.newInstance(field.getZero().getClass(), new int[]{ m , nColB })));
			for (int row = 0 ; row < m ; row++) {
				final T[] bpRow = bp[row];
				final int pRow = pivot[row];
				for (int col = 0 ; col < nColB ; col++) {
					bpRow[col] = b.getEntry(pRow, col);
				}
			}
			for (int col = 0 ; col < m ; col++) {
				final T[] bpCol = bp[col];
				for (int i = col + 1 ; i < m ; i++) {
					final T[] bpI = bp[i];
					final T luICol = lu[i][col];
					for (int j = 0 ; j < nColB ; j++) {
						bpI[j] = bpI[j].subtract(bpCol[j].multiply(luICol));
					}
				}
			}
			for (int col = m - 1 ; col >= 0 ; col--) {
				final T[] bpCol = bp[col];
				final T luDiag = lu[col][col];
				for (int j = 0 ; j < nColB ; j++) {
					bpCol[j] = bpCol[j].divide(luDiag);
				}
				for (int i = 0 ; i < col ; i++) {
					final T[] bpI = bp[i];
					final T luICol = lu[i][col];
					for (int j = 0 ; j < nColB ; j++) {
						bpI[j] = bpI[j].subtract(bpCol[j].multiply(luICol));
					}
				}
			}
			return new org.apache.commons.math.linear.Array2DRowFieldMatrix<T>(bp , false);
		}

		public org.apache.commons.math.linear.FieldMatrix<T> getInverse() throws org.apache.commons.math.linear.InvalidMatrixException {
			final int m = pivot.length;
			final T one = field.getOne();
			org.apache.commons.math.linear.FieldMatrix<T> identity = new org.apache.commons.math.linear.Array2DRowFieldMatrix<T>(field , m , m);
			for (int i = 0 ; i < m ; ++i) {
				identity.setEntry(i, i, one);
			}
			return solve(identity);
		}
	}
}

