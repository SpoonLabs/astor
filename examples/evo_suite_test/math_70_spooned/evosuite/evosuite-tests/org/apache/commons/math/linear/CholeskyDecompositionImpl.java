package org.apache.commons.math.linear;


public class CholeskyDecompositionImpl implements org.apache.commons.math.linear.CholeskyDecomposition {
	public static final double DEFAULT_RELATIVE_SYMMETRY_THRESHOLD = 1.0E-15;

	public static final double DEFAULT_ABSOLUTE_POSITIVITY_THRESHOLD = 1.0E-10;

	private double[][] lTData;

	private org.apache.commons.math.linear.RealMatrix cachedL;

	private org.apache.commons.math.linear.RealMatrix cachedLT;

	public CholeskyDecompositionImpl(final org.apache.commons.math.linear.RealMatrix matrix) throws org.apache.commons.math.linear.NonSquareMatrixException , org.apache.commons.math.linear.NotPositiveDefiniteMatrixException , org.apache.commons.math.linear.NotSymmetricMatrixException {
		this(matrix, DEFAULT_RELATIVE_SYMMETRY_THRESHOLD, DEFAULT_ABSOLUTE_POSITIVITY_THRESHOLD);
	}

	public CholeskyDecompositionImpl(final org.apache.commons.math.linear.RealMatrix matrix ,final double relativeSymmetryThreshold ,final double absolutePositivityThreshold) throws org.apache.commons.math.linear.NonSquareMatrixException , org.apache.commons.math.linear.NotPositiveDefiniteMatrixException , org.apache.commons.math.linear.NotSymmetricMatrixException {
		if (!(matrix.isSquare())) {
			throw new org.apache.commons.math.linear.NonSquareMatrixException(matrix.getRowDimension() , matrix.getColumnDimension());
		} 
		final int order = matrix.getRowDimension();
		lTData = matrix.getData();
		cachedL = null;
		cachedLT = null;
		for (int i = 0 ; i < order ; ++i) {
			final double[] lI = lTData[i];
			for (int j = i + 1 ; j < order ; ++j) {
				final double[] lJ = lTData[j];
				final double lIJ = lI[j];
				final double lJI = lJ[i];
				final double maxDelta = relativeSymmetryThreshold * (java.lang.Math.max(java.lang.Math.abs(lIJ), java.lang.Math.abs(lJI)));
				if ((java.lang.Math.abs((lIJ - lJI))) > maxDelta) {
					throw new org.apache.commons.math.linear.NotSymmetricMatrixException();
				} 
				lJ[i] = 0;
			}
		}
		for (int i = 0 ; i < order ; ++i) {
			final double[] ltI = lTData[i];
			if ((ltI[i]) < absolutePositivityThreshold) {
				throw new org.apache.commons.math.linear.NotPositiveDefiniteMatrixException();
			} 
			ltI[i] = java.lang.Math.sqrt(ltI[i]);
			final double inverse = 1.0 / (ltI[i]);
			for (int q = order - 1 ; q > i ; --q) {
				ltI[q] *= inverse;
				final double[] ltQ = lTData[q];
				for (int p = q ; p < order ; ++p) {
					ltQ[p] -= (ltI[q]) * (ltI[p]);
				}
			}
		}
	}

	public org.apache.commons.math.linear.RealMatrix getL() {
		if ((cachedL) == null) {
			cachedL = getLT().transpose();
		} 
		return cachedL;
	}

	public org.apache.commons.math.linear.RealMatrix getLT() {
		if ((cachedLT) == null) {
			cachedLT = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(lTData);
		} 
		return cachedLT;
	}

	public double getDeterminant() {
		double determinant = 1.0;
		for (int i = 0 ; i < (lTData.length) ; ++i) {
			double lTii = lTData[i][i];
			determinant *= lTii * lTii;
		}
		return determinant;
	}

	public org.apache.commons.math.linear.DecompositionSolver getSolver() {
		return new org.apache.commons.math.linear.CholeskyDecompositionImpl.Solver(lTData);
	}

	private static class Solver implements org.apache.commons.math.linear.DecompositionSolver {
		private final double[][] lTData;

		private Solver(final double[][] lTData) {
			this.lTData = lTData;
		}

		public boolean isNonSingular() {
			return true;
		}

		public double[] solve(double[] b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException {
			final int m = lTData.length;
			if ((b.length) != m) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("vector length mismatch: got {0} but expected {1}", b.length, m);
			} 
			final double[] x = b.clone();
			for (int j = 0 ; j < m ; j++) {
				final double[] lJ = lTData[j];
				x[j] /= lJ[j];
				final double xJ = x[j];
				for (int i = j + 1 ; i < m ; i++) {
					x[i] -= xJ * (lJ[i]);
				}
			}
			for (int j = m - 1 ; j >= 0 ; j--) {
				x[j] /= lTData[j][j];
				final double xJ = x[j];
				for (int i = 0 ; i < j ; i++) {
					x[i] -= xJ * (lTData[i][j]);
				}
			}
			return x;
		}

		public org.apache.commons.math.linear.RealVector solve(org.apache.commons.math.linear.RealVector b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException {
			try {
				return solve(((org.apache.commons.math.linear.ArrayRealVector)(b)));
			} catch (java.lang.ClassCastException cce) {
				final int m = lTData.length;
				if ((b.getDimension()) != m) {
					throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("vector length mismatch: got {0} but expected {1}", b.getDimension(), m);
				} 
				final double[] x = b.getData();
				for (int j = 0 ; j < m ; j++) {
					final double[] lJ = lTData[j];
					x[j] /= lJ[j];
					final double xJ = x[j];
					for (int i = j + 1 ; i < m ; i++) {
						x[i] -= xJ * (lJ[i]);
					}
				}
				for (int j = m - 1 ; j >= 0 ; j--) {
					x[j] /= lTData[j][j];
					final double xJ = x[j];
					for (int i = 0 ; i < j ; i++) {
						x[i] -= xJ * (lTData[i][j]);
					}
				}
				return new org.apache.commons.math.linear.ArrayRealVector(x , false);
			}
		}

		public org.apache.commons.math.linear.ArrayRealVector solve(org.apache.commons.math.linear.ArrayRealVector b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException {
			return new org.apache.commons.math.linear.ArrayRealVector(solve(b.getDataRef()) , false);
		}

		public org.apache.commons.math.linear.RealMatrix solve(org.apache.commons.math.linear.RealMatrix b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException {
			final int m = lTData.length;
			if ((b.getRowDimension()) != m) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("dimensions mismatch: got {0}x{1} but expected {2}x{3}", b.getRowDimension(), b.getColumnDimension(), m, "n");
			} 
			final int nColB = b.getColumnDimension();
			double[][] x = b.getData();
			for (int j = 0 ; j < m ; j++) {
				final double[] lJ = lTData[j];
				final double lJJ = lJ[j];
				final double[] xJ = x[j];
				for (int k = 0 ; k < nColB ; ++k) {
					xJ[k] /= lJJ;
				}
				for (int i = j + 1 ; i < m ; i++) {
					final double[] xI = x[i];
					final double lJI = lJ[i];
					for (int k = 0 ; k < nColB ; ++k) {
						xI[k] -= (xJ[k]) * lJI;
					}
				}
			}
			for (int j = m - 1 ; j >= 0 ; j--) {
				final double lJJ = lTData[j][j];
				final double[] xJ = x[j];
				for (int k = 0 ; k < nColB ; ++k) {
					xJ[k] /= lJJ;
				}
				for (int i = 0 ; i < j ; i++) {
					final double[] xI = x[i];
					final double lIJ = lTData[i][j];
					for (int k = 0 ; k < nColB ; ++k) {
						xI[k] -= (xJ[k]) * lIJ;
					}
				}
			}
			return new org.apache.commons.math.linear.Array2DRowRealMatrix(x , false);
		}

		public org.apache.commons.math.linear.RealMatrix getInverse() throws org.apache.commons.math.linear.InvalidMatrixException {
			return solve(org.apache.commons.math.linear.MatrixUtils.createRealIdentityMatrix(lTData.length));
		}
	}
}

