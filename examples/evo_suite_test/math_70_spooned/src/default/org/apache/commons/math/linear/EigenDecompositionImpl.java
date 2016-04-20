package org.apache.commons.math.linear;


public class EigenDecompositionImpl implements org.apache.commons.math.linear.EigenDecomposition {
	private byte maxIter = 30;

	private double[] main;

	private double[] secondary;

	private org.apache.commons.math.linear.TriDiagonalTransformer transformer;

	private double[] realEigenvalues;

	private double[] imagEigenvalues;

	private org.apache.commons.math.linear.ArrayRealVector[] eigenvectors;

	private org.apache.commons.math.linear.RealMatrix cachedV;

	private org.apache.commons.math.linear.RealMatrix cachedD;

	private org.apache.commons.math.linear.RealMatrix cachedVt;

	public EigenDecompositionImpl(final org.apache.commons.math.linear.RealMatrix matrix ,final double splitTolerance) throws org.apache.commons.math.linear.InvalidMatrixException {
		if (isSymmetric(matrix)) {
			transformToTridiagonal(matrix);
			findEigenVectors(transformer.getQ().getData());
		} else {
			throw new org.apache.commons.math.linear.InvalidMatrixException("eigen decomposition of assymetric matrices not supported yet");
		}
	}

	public EigenDecompositionImpl(final double[] main ,final double[] secondary ,final double splitTolerance) throws org.apache.commons.math.linear.InvalidMatrixException {
		this.main = main.clone();
		this.secondary = secondary.clone();
		transformer = null;
		final int size = main.length;
		double[][] z = new double[size][size];
		for (int i = 0 ; i < size ; i++) {
			z[i][i] = 1.0;
		}
		findEigenVectors(z);
	}

	private boolean isSymmetric(final org.apache.commons.math.linear.RealMatrix matrix) {
		final int rows = matrix.getRowDimension();
		final int columns = matrix.getColumnDimension();
		final double eps = ((10 * rows) * columns) * (org.apache.commons.math.util.MathUtils.EPSILON);
		for (int i = 0 ; i < rows ; ++i) {
			for (int j = i + 1 ; j < columns ; ++j) {
				final double mij = matrix.getEntry(i, j);
				final double mji = matrix.getEntry(j, i);
				if ((java.lang.Math.abs((mij - mji))) > ((java.lang.Math.max(java.lang.Math.abs(mij), java.lang.Math.abs(mji))) * eps)) {
					return false;
				} 
			}
		}
		return true;
	}

	public org.apache.commons.math.linear.RealMatrix getV() throws org.apache.commons.math.linear.InvalidMatrixException {
		if ((cachedV) == null) {
			final int m = eigenvectors.length;
			cachedV = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(m, m);
			for (int k = 0 ; k < m ; ++k) {
				cachedV.setColumnVector(k, eigenvectors[k]);
			}
		} 
		return cachedV;
	}

	public org.apache.commons.math.linear.RealMatrix getD() throws org.apache.commons.math.linear.InvalidMatrixException {
		if ((cachedD) == null) {
			cachedD = org.apache.commons.math.linear.MatrixUtils.createRealDiagonalMatrix(realEigenvalues);
		} 
		return cachedD;
	}

	public org.apache.commons.math.linear.RealMatrix getVT() throws org.apache.commons.math.linear.InvalidMatrixException {
		if ((cachedVt) == null) {
			final int m = eigenvectors.length;
			cachedVt = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(m, m);
			for (int k = 0 ; k < m ; ++k) {
				cachedVt.setRowVector(k, eigenvectors[k]);
			}
		} 
		return cachedVt;
	}

	public double[] getRealEigenvalues() throws org.apache.commons.math.linear.InvalidMatrixException {
		return realEigenvalues.clone();
	}

	public double getRealEigenvalue(final int i) throws java.lang.ArrayIndexOutOfBoundsException, org.apache.commons.math.linear.InvalidMatrixException {
		return realEigenvalues[i];
	}

	public double[] getImagEigenvalues() throws org.apache.commons.math.linear.InvalidMatrixException {
		return imagEigenvalues.clone();
	}

	public double getImagEigenvalue(final int i) throws java.lang.ArrayIndexOutOfBoundsException, org.apache.commons.math.linear.InvalidMatrixException {
		return imagEigenvalues[i];
	}

	public org.apache.commons.math.linear.RealVector getEigenvector(final int i) throws java.lang.ArrayIndexOutOfBoundsException, org.apache.commons.math.linear.InvalidMatrixException {
		return eigenvectors[i].copy();
	}

	public double getDeterminant() {
		double determinant = 1;
		for (double lambda : realEigenvalues) {
			determinant *= lambda;
		}
		return determinant;
	}

	public org.apache.commons.math.linear.DecompositionSolver getSolver() {
		return new org.apache.commons.math.linear.EigenDecompositionImpl.Solver(realEigenvalues , imagEigenvalues , eigenvectors);
	}

	private static class Solver implements org.apache.commons.math.linear.DecompositionSolver {
		private double[] realEigenvalues;

		private double[] imagEigenvalues;

		private final org.apache.commons.math.linear.ArrayRealVector[] eigenvectors;

		private Solver(final double[] realEigenvalues ,final double[] imagEigenvalues ,final org.apache.commons.math.linear.ArrayRealVector[] eigenvectors) {
			this.realEigenvalues = realEigenvalues;
			this.imagEigenvalues = imagEigenvalues;
			this.eigenvectors = eigenvectors;
		}

		public double[] solve(final double[] b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException {
			if (!(isNonSingular())) {
				throw new org.apache.commons.math.linear.SingularMatrixException();
			} 
			final int m = realEigenvalues.length;
			if ((b.length) != m) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("vector length mismatch: got {0} but expected {1}", b.length, m);
			} 
			final double[] bp = new double[m];
			for (int i = 0 ; i < m ; ++i) {
				final org.apache.commons.math.linear.ArrayRealVector v = eigenvectors[i];
				final double[] vData = v.getDataRef();
				final double s = (v.dotProduct(b)) / (realEigenvalues[i]);
				for (int j = 0 ; j < m ; ++j) {
					bp[j] += s * (vData[j]);
				}
			}
			return bp;
		}

		public org.apache.commons.math.linear.RealVector solve(final org.apache.commons.math.linear.RealVector b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException {
			if (!(isNonSingular())) {
				throw new org.apache.commons.math.linear.SingularMatrixException();
			} 
			final int m = realEigenvalues.length;
			if ((b.getDimension()) != m) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("vector length mismatch: got {0} but expected {1}", b.getDimension(), m);
			} 
			final double[] bp = new double[m];
			for (int i = 0 ; i < m ; ++i) {
				final org.apache.commons.math.linear.ArrayRealVector v = eigenvectors[i];
				final double[] vData = v.getDataRef();
				final double s = (v.dotProduct(b)) / (realEigenvalues[i]);
				for (int j = 0 ; j < m ; ++j) {
					bp[j] += s * (vData[j]);
				}
			}
			return new org.apache.commons.math.linear.ArrayRealVector(bp , false);
		}

		public org.apache.commons.math.linear.RealMatrix solve(final org.apache.commons.math.linear.RealMatrix b) throws java.lang.IllegalArgumentException, org.apache.commons.math.linear.InvalidMatrixException {
			if (!(isNonSingular())) {
				throw new org.apache.commons.math.linear.SingularMatrixException();
			} 
			final int m = realEigenvalues.length;
			if ((b.getRowDimension()) != m) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("dimensions mismatch: got {0}x{1} but expected {2}x{3}", b.getRowDimension(), b.getColumnDimension(), m, "n");
			} 
			final int nColB = b.getColumnDimension();
			final double[][] bp = new double[m][nColB];
			for (int k = 0 ; k < nColB ; ++k) {
				for (int i = 0 ; i < m ; ++i) {
					final org.apache.commons.math.linear.ArrayRealVector v = eigenvectors[i];
					final double[] vData = v.getDataRef();
					double s = 0;
					for (int j = 0 ; j < m ; ++j) {
						s += (v.getEntry(j)) * (b.getEntry(j, k));
					}
					s /= realEigenvalues[i];
					for (int j = 0 ; j < m ; ++j) {
						bp[j][k] += s * (vData[j]);
					}
				}
			}
			return org.apache.commons.math.linear.MatrixUtils.createRealMatrix(bp);
		}

		public boolean isNonSingular() {
			for (int i = 0 ; i < (realEigenvalues.length) ; ++i) {
				if (((realEigenvalues[i]) == 0) && ((imagEigenvalues[i]) == 0)) {
					return false;
				} 
			}
			return true;
		}

		public org.apache.commons.math.linear.RealMatrix getInverse() throws org.apache.commons.math.linear.InvalidMatrixException {
			if (!(isNonSingular())) {
				throw new org.apache.commons.math.linear.SingularMatrixException();
			} 
			final int m = realEigenvalues.length;
			final double[][] invData = new double[m][m];
			for (int i = 0 ; i < m ; ++i) {
				final double[] invI = invData[i];
				for (int j = 0 ; j < m ; ++j) {
					double invIJ = 0;
					for (int k = 0 ; k < m ; ++k) {
						final double[] vK = eigenvectors[k].getDataRef();
						invIJ += ((vK[i]) * (vK[j])) / (realEigenvalues[k]);
					}
					invI[j] = invIJ;
				}
			}
			return org.apache.commons.math.linear.MatrixUtils.createRealMatrix(invData);
		}
	}

	private void transformToTridiagonal(final org.apache.commons.math.linear.RealMatrix matrix) {
		transformer = new org.apache.commons.math.linear.TriDiagonalTransformer(matrix);
		main = transformer.getMainDiagonalRef();
		secondary = transformer.getSecondaryDiagonalRef();
	}

	private void findEigenVectors(double[][] householderMatrix) {
		double[][] z = householderMatrix.clone();
		final int n = main.length;
		realEigenvalues = new double[n];
		imagEigenvalues = new double[n];
		double[] e = new double[n];
		for (int i = 0 ; i < (n - 1) ; i++) {
			realEigenvalues[i] = main[i];
			e[i] = secondary[i];
		}
		realEigenvalues[(n - 1)] = main[(n - 1)];
		e[(n - 1)] = 0.0;
		double maxAbsoluteValue = 0.0;
		for (int i = 0 ; i < n ; i++) {
			if ((java.lang.Math.abs(realEigenvalues[i])) > maxAbsoluteValue) {
				maxAbsoluteValue = java.lang.Math.abs(realEigenvalues[i]);
			} 
			if ((java.lang.Math.abs(e[i])) > maxAbsoluteValue) {
				maxAbsoluteValue = java.lang.Math.abs(e[i]);
			} 
		}
		if (maxAbsoluteValue != 0.0) {
			for (int i = 0 ; i < n ; i++) {
				if ((java.lang.Math.abs(realEigenvalues[i])) <= ((org.apache.commons.math.util.MathUtils.EPSILON) * maxAbsoluteValue)) {
					realEigenvalues[i] = 0.0;
				} 
				if ((java.lang.Math.abs(e[i])) <= ((org.apache.commons.math.util.MathUtils.EPSILON) * maxAbsoluteValue)) {
					e[i] = 0.0;
				} 
			}
		} 
		for (int j = 0 ; j < n ; j++) {
			int its = 0;
			int m;
			do {
				for (m = j ; m < (n - 1) ; m++) {
					double delta = (java.lang.Math.abs(realEigenvalues[m])) + (java.lang.Math.abs(realEigenvalues[(m + 1)]));
					if (((java.lang.Math.abs(e[m])) + delta) == delta) {
						break;
					} 
				}
				if (m != j) {
					if (its == (maxIter))
						throw new org.apache.commons.math.linear.InvalidMatrixException(new org.apache.commons.math.MaxIterationsExceededException(maxIter));
					
					its++;
					double q = ((realEigenvalues[(j + 1)]) - (realEigenvalues[j])) / (2 * (e[j]));
					double t = java.lang.Math.sqrt((1 + (q * q)));
					if (q < 0.0) {
						q = ((realEigenvalues[m]) - (realEigenvalues[j])) + ((e[j]) / (q - t));
					} else {
						q = ((realEigenvalues[m]) - (realEigenvalues[j])) + ((e[j]) / (q + t));
					}
					double u = 0.0;
					double s = 1.0;
					double c = 1.0;
					int i;
					for (i = m - 1 ; i >= j ; i--) {
						double p = s * (e[i]);
						double h = c * (e[i]);
						if ((java.lang.Math.abs(p)) >= (java.lang.Math.abs(q))) {
							c = q / p;
							t = java.lang.Math.sqrt(((c * c) + 1.0));
							e[(i + 1)] = p * t;
							s = 1.0 / t;
							c = c * s;
						} else {
							s = p / q;
							t = java.lang.Math.sqrt(((s * s) + 1.0));
							e[(i + 1)] = q * t;
							c = 1.0 / t;
							s = s * c;
						}
						if ((e[(i + 1)]) == 0.0) {
							realEigenvalues[(i + 1)] -= u;
							e[m] = 0.0;
							break;
						} 
						q = (realEigenvalues[(i + 1)]) - u;
						t = (((realEigenvalues[i]) - q) * s) + ((2.0 * c) * h);
						u = s * t;
						realEigenvalues[(i + 1)] = q + u;
						q = (c * t) - h;
						for (int ia = 0 ; ia < n ; ia++) {
							p = z[ia][(i + 1)];
							z[ia][(i + 1)] = (s * (z[ia][i])) + (c * p);
							z[ia][i] = (c * (z[ia][i])) - (s * p);
						}
					}
					if (((e[(i + 1)]) == 0.0) && (i >= j))
						continue;
					
					realEigenvalues[j] -= u;
					e[j] = q;
					e[m] = 0.0;
				} 
			} while (m != j );
		}
		for (int i = 0 ; i < n ; i++) {
			int k = i;
			double p = realEigenvalues[i];
			for (int j = i + 1 ; j < n ; j++) {
				if ((realEigenvalues[j]) > p) {
					k = j;
					p = realEigenvalues[j];
				} 
			}
			if (k != i) {
				realEigenvalues[k] = realEigenvalues[i];
				realEigenvalues[i] = p;
				for (int j = 0 ; j < n ; j++) {
					p = z[j][i];
					z[j][i] = z[j][k];
					z[j][k] = p;
				}
			} 
		}
		maxAbsoluteValue = 0.0;
		for (int i = 0 ; i < n ; i++) {
			if ((java.lang.Math.abs(realEigenvalues[i])) > maxAbsoluteValue) {
				maxAbsoluteValue = java.lang.Math.abs(realEigenvalues[i]);
			} 
		}
		if (maxAbsoluteValue != 0.0) {
			for (int i = 0 ; i < n ; i++) {
				if ((java.lang.Math.abs(realEigenvalues[i])) < ((org.apache.commons.math.util.MathUtils.EPSILON) * maxAbsoluteValue)) {
					realEigenvalues[i] = 0.0;
				} 
			}
		} 
		eigenvectors = new org.apache.commons.math.linear.ArrayRealVector[n];
		double[] tmp = new double[n];
		for (int i = 0 ; i < n ; i++) {
			for (int j = 0 ; j < n ; j++) {
				tmp[j] = z[j][i];
			}
			eigenvectors[i] = new org.apache.commons.math.linear.ArrayRealVector(tmp);
		}
	}
}

