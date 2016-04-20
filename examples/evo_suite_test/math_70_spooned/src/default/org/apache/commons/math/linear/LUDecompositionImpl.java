package org.apache.commons.math.linear;


public class LUDecompositionImpl implements org.apache.commons.math.linear.LUDecomposition {
	private static final double DEFAULT_TOO_SMALL = 1.0E-11;

	private static final java.lang.String VECTOR_LENGTH_MISMATCH_MESSAGE = "vector length mismatch: got {0} but expected {1}";

	private double[][] lu;

	private int[] pivot;

	private boolean even;

	private boolean singular;

	private org.apache.commons.math.linear.RealMatrix cachedL;

	private org.apache.commons.math.linear.RealMatrix cachedU;

	private org.apache.commons.math.linear.RealMatrix cachedP;

	public LUDecompositionImpl(org.apache.commons.math.linear.RealMatrix matrix) throws org.apache.commons.math.linear.InvalidMatrixException {
		this(matrix, DEFAULT_TOO_SMALL);
	}

	public LUDecompositionImpl(org.apache.commons.math.linear.RealMatrix matrix ,double singularityThreshold) throws org.apache.commons.math.linear.NonSquareMatrixException {
		if (!(matrix.isSquare())) {
			throw new org.apache.commons.math.linear.NonSquareMatrixException(matrix.getRowDimension() , matrix.getColumnDimension());
		} 
		final int m = matrix.getColumnDimension();
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
			double sum = 0;
			for (int row = 0 ; row < col ; row++) {
				final double[] luRow = lu[row];
				sum = luRow[col];
				for (int i = 0 ; i < row ; i++) {
					sum -= (luRow[i]) * (lu[i][col]);
				}
				luRow[col] = sum;
			}
			int max = col;
			double largest = java.lang.Double.NEGATIVE_INFINITY;
			for (int row = col ; row < m ; row++) {
				final double[] luRow = lu[row];
				sum = luRow[col];
				for (int i = 0 ; i < col ; i++) {
					sum -= (luRow[i]) * (lu[i][col]);
				}
				luRow[col] = sum;
				if ((java.lang.Math.abs(sum)) > largest) {
					largest = java.lang.Math.abs(sum);
					max = row;
				} 
			}
			if ((java.lang.Math.abs(lu[max][col])) < singularityThreshold) {
				singular = true;
				return ;
			} 
			if (max != col) {
				double tmp = 0;
				final double[] luMax = lu[max];
				final double[] luCol = lu[col];
				for (int i = 0 ; i < m ; i++) {
					tmp = luMax[i];
					luMax[i] = luCol[i];
					luCol[i] = tmp;
				}
				int temp = pivot[max];
				pivot[max] = pivot[col];
				pivot[col] = temp;
				even = !(even);
			} 
			final double luDiag = lu[col][col];
			for (int row = col + 1 ; row < m ; row++) {
				lu[row][col] /= luDiag;
			}
		}
	}

	public org.apache.commons.math.linear.RealMatrix getL() {
		if (((cachedL) == null) && (!(singular))) {
			final int m = pivot.length;
			cachedL = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(m, m);
			for (int i = 0 ; i < m ; ++i) {
				final double[] luI = lu[i];
				for (int j = 0 ; j < i ; ++j) {
					cachedL.setEntry(i, j, luI[j]);
				}
				cachedL.setEntry(i, i, 1.0);
			}
		} 
		return cachedL;
	}

	public org.apache.commons.math.linear.RealMatrix getU() {
		if (((cachedU) == null) && (!(singular))) {
			final int m = pivot.length;
			cachedU = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(m, m);
			for (int i = 0 ; i < m ; ++i) {
				final double[] luI = lu[i];
				for (int j = i ; j < m ; ++j) {
					cachedU.setEntry(i, j, luI[j]);
				}
			}
		} 
		return cachedU;
	}

	public org.apache.commons.math.linear.RealMatrix getP() {
		if (((cachedP) == null) && (!(singular))) {
			final int m = pivot.length;
			cachedP = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(m, m);
			for (int i = 0 ; i < m ; ++i) {
				cachedP.setEntry(i, pivot[i], 1.0);
			}
		} 
		return cachedP;
	}

	public int[] getPivot() {
		return pivot.clone();
	}

	public double getDeterminant() {
		if (singular) {
			return 0;
		} else {
			final int m = pivot.length;
			double determinant = even ? 1 : -1;
			for (int i = 0 ; i < m ; i++) {
				determinant *= lu[i][i];
			}
			return determinant;
		}
	}

	public org.apache.commons.math.linear.DecompositionSolver getSolver() {
		return new org.apache.commons.math.linear.LUDecompositionImpl.Solver(lu , pivot , singular);
	}

	private static class Solver implements org.apache.commons.math.linear.DecompositionSolver {
		private final double[][] lu;

		private final int[] pivot;

		private final boolean singular;

		private Solver(final double[][] lu ,final int[] pivot ,final boolean singular) {
			this.lu = lu;
			this.pivot = pivot;
			this.singular = singular;
		}

		public boolean isNonSingular() {
			return !(singular);
		}

		public double[] solve(double[] b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException {
			final int m = pivot.length;
			if ((b.length) != m) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(org.apache.commons.math.linear.LUDecompositionImpl.VECTOR_LENGTH_MISMATCH_MESSAGE, b.length, m);
			} 
			if (singular) {
				throw new org.apache.commons.math.linear.SingularMatrixException();
			} 
			final double[] bp = new double[m];
			for (int row = 0 ; row < m ; row++) {
				bp[row] = b[pivot[row]];
			}
			for (int col = 0 ; col < m ; col++) {
				final double bpCol = bp[col];
				for (int i = col + 1 ; i < m ; i++) {
					bp[i] -= bpCol * (lu[i][col]);
				}
			}
			for (int col = m - 1 ; col >= 0 ; col--) {
				bp[col] /= lu[col][col];
				final double bpCol = bp[col];
				for (int i = 0 ; i < col ; i++) {
					bp[i] -= bpCol * (lu[i][col]);
				}
			}
			return bp;
		}

		public org.apache.commons.math.linear.RealVector solve(org.apache.commons.math.linear.RealVector b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException {
			try {
				return solve(((org.apache.commons.math.linear.ArrayRealVector)(b)));
			} catch (java.lang.ClassCastException cce) {
				final int m = pivot.length;
				if ((b.getDimension()) != m) {
					throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(org.apache.commons.math.linear.LUDecompositionImpl.VECTOR_LENGTH_MISMATCH_MESSAGE, b.getDimension(), m);
				} 
				if (singular) {
					throw new org.apache.commons.math.linear.SingularMatrixException();
				} 
				final double[] bp = new double[m];
				for (int row = 0 ; row < m ; row++) {
					bp[row] = b.getEntry(pivot[row]);
				}
				for (int col = 0 ; col < m ; col++) {
					final double bpCol = bp[col];
					for (int i = col + 1 ; i < m ; i++) {
						bp[i] -= bpCol * (lu[i][col]);
					}
				}
				for (int col = m - 1 ; col >= 0 ; col--) {
					bp[col] /= lu[col][col];
					final double bpCol = bp[col];
					for (int i = 0 ; i < col ; i++) {
						bp[i] -= bpCol * (lu[i][col]);
					}
				}
				return new org.apache.commons.math.linear.ArrayRealVector(bp , false);
			}
		}

		public org.apache.commons.math.linear.ArrayRealVector solve(org.apache.commons.math.linear.ArrayRealVector b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException {
			return new org.apache.commons.math.linear.ArrayRealVector(solve(b.getDataRef()) , false);
		}

		public org.apache.commons.math.linear.RealMatrix solve(org.apache.commons.math.linear.RealMatrix b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException {
			final int m = pivot.length;
			if ((b.getRowDimension()) != m) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("dimensions mismatch: got {0}x{1} but expected {2}x{3}", b.getRowDimension(), b.getColumnDimension(), m, "n");
			} 
			if (singular) {
				throw new org.apache.commons.math.linear.SingularMatrixException();
			} 
			final int nColB = b.getColumnDimension();
			final double[][] bp = new double[m][nColB];
			for (int row = 0 ; row < m ; row++) {
				final double[] bpRow = bp[row];
				final int pRow = pivot[row];
				for (int col = 0 ; col < nColB ; col++) {
					bpRow[col] = b.getEntry(pRow, col);
				}
			}
			for (int col = 0 ; col < m ; col++) {
				final double[] bpCol = bp[col];
				for (int i = col + 1 ; i < m ; i++) {
					final double[] bpI = bp[i];
					final double luICol = lu[i][col];
					for (int j = 0 ; j < nColB ; j++) {
						bpI[j] -= (bpCol[j]) * luICol;
					}
				}
			}
			for (int col = m - 1 ; col >= 0 ; col--) {
				final double[] bpCol = bp[col];
				final double luDiag = lu[col][col];
				for (int j = 0 ; j < nColB ; j++) {
					bpCol[j] /= luDiag;
				}
				for (int i = 0 ; i < col ; i++) {
					final double[] bpI = bp[i];
					final double luICol = lu[i][col];
					for (int j = 0 ; j < nColB ; j++) {
						bpI[j] -= (bpCol[j]) * luICol;
					}
				}
			}
			return new org.apache.commons.math.linear.Array2DRowRealMatrix(bp , false);
		}

		public org.apache.commons.math.linear.RealMatrix getInverse() throws org.apache.commons.math.linear.InvalidMatrixException {
			return solve(org.apache.commons.math.linear.MatrixUtils.createRealIdentityMatrix(pivot.length));
		}
	}
}

