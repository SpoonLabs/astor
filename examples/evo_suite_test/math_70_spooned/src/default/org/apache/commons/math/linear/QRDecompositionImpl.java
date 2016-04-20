package org.apache.commons.math.linear;


public class QRDecompositionImpl implements org.apache.commons.math.linear.QRDecomposition {
	private double[][] qrt;

	private double[] rDiag;

	private org.apache.commons.math.linear.RealMatrix cachedQ;

	private org.apache.commons.math.linear.RealMatrix cachedQT;

	private org.apache.commons.math.linear.RealMatrix cachedR;

	private org.apache.commons.math.linear.RealMatrix cachedH;

	public QRDecompositionImpl(org.apache.commons.math.linear.RealMatrix matrix) {
		final int m = matrix.getRowDimension();
		final int n = matrix.getColumnDimension();
		qrt = matrix.transpose().getData();
		rDiag = new double[java.lang.Math.min(m, n)];
		cachedQ = null;
		cachedQT = null;
		cachedR = null;
		cachedH = null;
		for (int minor = 0 ; minor < (java.lang.Math.min(m, n)) ; minor++) {
			final double[] qrtMinor = qrt[minor];
			double xNormSqr = 0;
			for (int row = minor ; row < m ; row++) {
				final double c = qrtMinor[row];
				xNormSqr += c * c;
			}
			final double a = (qrtMinor[minor]) > 0 ? -(java.lang.Math.sqrt(xNormSqr)) : java.lang.Math.sqrt(xNormSqr);
			rDiag[minor] = a;
			if (a != 0.0) {
				qrtMinor[minor] -= a;
				for (int col = minor + 1 ; col < n ; col++) {
					final double[] qrtCol = qrt[col];
					double alpha = 0;
					for (int row = minor ; row < m ; row++) {
						alpha -= (qrtCol[row]) * (qrtMinor[row]);
					}
					alpha /= a * (qrtMinor[minor]);
					for (int row = minor ; row < m ; row++) {
						qrtCol[row] -= alpha * (qrtMinor[row]);
					}
				}
			} 
		}
	}

	public org.apache.commons.math.linear.RealMatrix getR() {
		if ((cachedR) == null) {
			final int n = qrt.length;
			final int m = qrt[0].length;
			cachedR = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(m, n);
			for (int row = (java.lang.Math.min(m, n)) - 1 ; row >= 0 ; row--) {
				cachedR.setEntry(row, row, rDiag[row]);
				for (int col = row + 1 ; col < n ; col++) {
					cachedR.setEntry(row, col, qrt[col][row]);
				}
			}
		} 
		return cachedR;
	}

	public org.apache.commons.math.linear.RealMatrix getQ() {
		if ((cachedQ) == null) {
			cachedQ = getQT().transpose();
		} 
		return cachedQ;
	}

	public org.apache.commons.math.linear.RealMatrix getQT() {
		if ((cachedQT) == null) {
			final int n = qrt.length;
			final int m = qrt[0].length;
			cachedQT = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(m, m);
			for (int minor = m - 1 ; minor >= (java.lang.Math.min(m, n)) ; minor--) {
				cachedQT.setEntry(minor, minor, 1.0);
			}
			for (int minor = (java.lang.Math.min(m, n)) - 1 ; minor >= 0 ; minor--) {
				final double[] qrtMinor = qrt[minor];
				cachedQT.setEntry(minor, minor, 1.0);
				if ((qrtMinor[minor]) != 0.0) {
					for (int col = minor ; col < m ; col++) {
						double alpha = 0;
						for (int row = minor ; row < m ; row++) {
							alpha -= (cachedQT.getEntry(col, row)) * (qrtMinor[row]);
						}
						alpha /= (rDiag[minor]) * (qrtMinor[minor]);
						for (int row = minor ; row < m ; row++) {
							cachedQT.addToEntry(col, row, ((-alpha) * (qrtMinor[row])));
						}
					}
				} 
			}
		} 
		return cachedQT;
	}

	public org.apache.commons.math.linear.RealMatrix getH() {
		if ((cachedH) == null) {
			final int n = qrt.length;
			final int m = qrt[0].length;
			cachedH = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(m, n);
			for (int i = 0 ; i < m ; ++i) {
				for (int j = 0 ; j < (java.lang.Math.min((i + 1), n)) ; ++j) {
					cachedH.setEntry(i, j, ((qrt[j][i]) / (-(rDiag[j]))));
				}
			}
		} 
		return cachedH;
	}

	public org.apache.commons.math.linear.DecompositionSolver getSolver() {
		return new org.apache.commons.math.linear.QRDecompositionImpl.Solver(qrt , rDiag);
	}

	private static class Solver implements org.apache.commons.math.linear.DecompositionSolver {
		private final double[][] qrt;

		private final double[] rDiag;

		private Solver(final double[][] qrt ,final double[] rDiag) {
			this.qrt = qrt;
			this.rDiag = rDiag;
		}

		public boolean isNonSingular() {
			for (double diag : rDiag) {
				if (diag == 0) {
					return false;
				} 
			}
			return true;
		}

		public double[] solve(double[] b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException {
			final int n = qrt.length;
			final int m = qrt[0].length;
			if ((b.length) != m) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("vector length mismatch: got {0} but expected {1}", b.length, m);
			} 
			if (!(isNonSingular())) {
				throw new org.apache.commons.math.linear.SingularMatrixException();
			} 
			final double[] x = new double[n];
			final double[] y = b.clone();
			for (int minor = 0 ; minor < (java.lang.Math.min(m, n)) ; minor++) {
				final double[] qrtMinor = qrt[minor];
				double dotProduct = 0;
				for (int row = minor ; row < m ; row++) {
					dotProduct += (y[row]) * (qrtMinor[row]);
				}
				dotProduct /= (rDiag[minor]) * (qrtMinor[minor]);
				for (int row = minor ; row < m ; row++) {
					y[row] += dotProduct * (qrtMinor[row]);
				}
			}
			for (int row = (rDiag.length) - 1 ; row >= 0 ; --row) {
				y[row] /= rDiag[row];
				final double yRow = y[row];
				final double[] qrtRow = qrt[row];
				x[row] = yRow;
				for (int i = 0 ; i < row ; i++) {
					y[i] -= yRow * (qrtRow[i]);
				}
			}
			return x;
		}

		public org.apache.commons.math.linear.RealVector solve(org.apache.commons.math.linear.RealVector b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException {
			try {
				return solve(((org.apache.commons.math.linear.ArrayRealVector)(b)));
			} catch (java.lang.ClassCastException cce) {
				return new org.apache.commons.math.linear.ArrayRealVector(solve(b.getData()) , false);
			}
		}

		public org.apache.commons.math.linear.ArrayRealVector solve(org.apache.commons.math.linear.ArrayRealVector b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException {
			return new org.apache.commons.math.linear.ArrayRealVector(solve(b.getDataRef()) , false);
		}

		public org.apache.commons.math.linear.RealMatrix solve(org.apache.commons.math.linear.RealMatrix b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException {
			final int n = qrt.length;
			final int m = qrt[0].length;
			if ((b.getRowDimension()) != m) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("dimensions mismatch: got {0}x{1} but expected {2}x{3}", b.getRowDimension(), b.getColumnDimension(), m, "n");
			} 
			if (!(isNonSingular())) {
				throw new org.apache.commons.math.linear.SingularMatrixException();
			} 
			final int columns = b.getColumnDimension();
			final int blockSize = org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE;
			final int cBlocks = ((columns + blockSize) - 1) / blockSize;
			final double[][] xBlocks = org.apache.commons.math.linear.BlockRealMatrix.createBlocksLayout(n, columns);
			final double[][] y = new double[b.getRowDimension()][blockSize];
			final double[] alpha = new double[blockSize];
			for (int kBlock = 0 ; kBlock < cBlocks ; ++kBlock) {
				final int kStart = kBlock * blockSize;
				final int kEnd = java.lang.Math.min((kStart + blockSize), columns);
				final int kWidth = kEnd - kStart;
				b.copySubMatrix(0, (m - 1), kStart, (kEnd - 1), y);
				for (int minor = 0 ; minor < (java.lang.Math.min(m, n)) ; minor++) {
					final double[] qrtMinor = qrt[minor];
					final double factor = 1.0 / ((rDiag[minor]) * (qrtMinor[minor]));
					java.util.Arrays.fill(alpha, 0, kWidth, 0.0);
					for (int row = minor ; row < m ; ++row) {
						final double d = qrtMinor[row];
						final double[] yRow = y[row];
						for (int k = 0 ; k < kWidth ; ++k) {
							alpha[k] += d * (yRow[k]);
						}
					}
					for (int k = 0 ; k < kWidth ; ++k) {
						alpha[k] *= factor;
					}
					for (int row = minor ; row < m ; ++row) {
						final double d = qrtMinor[row];
						final double[] yRow = y[row];
						for (int k = 0 ; k < kWidth ; ++k) {
							yRow[k] += (alpha[k]) * d;
						}
					}
				}
				for (int j = (rDiag.length) - 1 ; j >= 0 ; --j) {
					final int jBlock = j / blockSize;
					final int jStart = jBlock * blockSize;
					final double factor = 1.0 / (rDiag[j]);
					final double[] yJ = y[j];
					final double[] xBlock = xBlocks[((jBlock * cBlocks) + kBlock)];
					int index = (j - jStart) * kWidth;
					for (int k = 0 ; k < kWidth ; ++k) {
						yJ[k] *= factor;
						xBlock[index++] = yJ[k];
					}
					final double[] qrtJ = qrt[j];
					for (int i = 0 ; i < j ; ++i) {
						final double rIJ = qrtJ[i];
						final double[] yI = y[i];
						for (int k = 0 ; k < kWidth ; ++k) {
							yI[k] -= (yJ[k]) * rIJ;
						}
					}
				}
			}
			return new org.apache.commons.math.linear.BlockRealMatrix(n , columns , xBlocks , false);
		}

		public org.apache.commons.math.linear.RealMatrix getInverse() throws org.apache.commons.math.linear.InvalidMatrixException {
			return solve(org.apache.commons.math.linear.MatrixUtils.createRealIdentityMatrix(rDiag.length));
		}
	}
}

