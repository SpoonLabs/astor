package org.apache.commons.math.linear;


public class SingularValueDecompositionImpl implements org.apache.commons.math.linear.SingularValueDecomposition {
	private int m;

	private int n;

	private org.apache.commons.math.linear.EigenDecomposition eigenDecomposition;

	private double[] singularValues;

	private org.apache.commons.math.linear.RealMatrix cachedU;

	private org.apache.commons.math.linear.RealMatrix cachedUt;

	private org.apache.commons.math.linear.RealMatrix cachedS;

	private org.apache.commons.math.linear.RealMatrix cachedV;

	private org.apache.commons.math.linear.RealMatrix cachedVt;

	public SingularValueDecompositionImpl(final org.apache.commons.math.linear.RealMatrix matrix) throws org.apache.commons.math.linear.InvalidMatrixException {
		m = matrix.getRowDimension();
		n = matrix.getColumnDimension();
		cachedU = null;
		cachedS = null;
		cachedV = null;
		cachedVt = null;
		double[][] localcopy = matrix.getData();
		double[][] matATA = new double[n][n];
		for (int i = 0 ; i < (n) ; i++) {
			for (int j = i ; j < (n) ; j++) {
				matATA[i][j] = 0.0;
				for (int k = 0 ; k < (m) ; k++) {
					matATA[i][j] += (localcopy[k][i]) * (localcopy[k][j]);
				}
				matATA[j][i] = matATA[i][j];
			}
		}
		double[][] matAAT = new double[m][m];
		for (int i = 0 ; i < (m) ; i++) {
			for (int j = i ; j < (m) ; j++) {
				matAAT[i][j] = 0.0;
				for (int k = 0 ; k < (n) ; k++) {
					matAAT[i][j] += (localcopy[i][k]) * (localcopy[j][k]);
				}
				matAAT[j][i] = matAAT[i][j];
			}
		}
		int p;
		if ((m) >= (n)) {
			p = n;
			eigenDecomposition = new org.apache.commons.math.linear.EigenDecompositionImpl(new org.apache.commons.math.linear.Array2DRowRealMatrix(matATA) , 1.0);
			singularValues = eigenDecomposition.getRealEigenvalues();
			cachedV = eigenDecomposition.getV();
			eigenDecomposition = new org.apache.commons.math.linear.EigenDecompositionImpl(new org.apache.commons.math.linear.Array2DRowRealMatrix(matAAT) , 1.0);
			cachedU = eigenDecomposition.getV().getSubMatrix(0, ((m) - 1), 0, (p - 1));
		} else {
			p = m;
			eigenDecomposition = new org.apache.commons.math.linear.EigenDecompositionImpl(new org.apache.commons.math.linear.Array2DRowRealMatrix(matAAT) , 1.0);
			singularValues = eigenDecomposition.getRealEigenvalues();
			cachedU = eigenDecomposition.getV();
			eigenDecomposition = new org.apache.commons.math.linear.EigenDecompositionImpl(new org.apache.commons.math.linear.Array2DRowRealMatrix(matATA) , 1.0);
			cachedV = eigenDecomposition.getV().getSubMatrix(0, ((n) - 1), 0, (p - 1));
		}
		for (int i = 0 ; i < p ; i++) {
			singularValues[i] = java.lang.Math.sqrt(java.lang.Math.abs(singularValues[i]));
		}
		for (int i = 0 ; i < p ; i++) {
			org.apache.commons.math.linear.RealVector tmp = cachedU.getColumnVector(i);
			double product = matrix.operate(cachedV.getColumnVector(i)).dotProduct(tmp);
			if (product < 0) {
				cachedU.setColumnVector(i, tmp.mapMultiply(-1.0));
			} 
		}
	}

	public org.apache.commons.math.linear.RealMatrix getU() throws org.apache.commons.math.linear.InvalidMatrixException {
		return cachedU;
	}

	public org.apache.commons.math.linear.RealMatrix getUT() throws org.apache.commons.math.linear.InvalidMatrixException {
		if ((cachedUt) == null) {
			cachedUt = getU().transpose();
		} 
		return cachedUt;
	}

	public org.apache.commons.math.linear.RealMatrix getS() throws org.apache.commons.math.linear.InvalidMatrixException {
		if ((cachedS) == null) {
			cachedS = org.apache.commons.math.linear.MatrixUtils.createRealDiagonalMatrix(singularValues);
		} 
		return cachedS;
	}

	public double[] getSingularValues() throws org.apache.commons.math.linear.InvalidMatrixException {
		return singularValues.clone();
	}

	public org.apache.commons.math.linear.RealMatrix getV() throws org.apache.commons.math.linear.InvalidMatrixException {
		return cachedV;
	}

	public org.apache.commons.math.linear.RealMatrix getVT() throws org.apache.commons.math.linear.InvalidMatrixException {
		if ((cachedVt) == null) {
			cachedVt = getV().transpose();
		} 
		return cachedVt;
	}

	public org.apache.commons.math.linear.RealMatrix getCovariance(final double minSingularValue) {
		final int p = singularValues.length;
		int dimension = 0;
		while ((dimension < p) && ((singularValues[dimension]) >= minSingularValue)) {
			++dimension;
		}
		if (dimension == 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("cutoff singular value is {0}, should be at most {1}", minSingularValue, singularValues[0]);
		} 
		final double[][] data = new double[dimension][p];
		getVT().walkInOptimizedOrder(new org.apache.commons.math.linear.DefaultRealMatrixPreservingVisitor() {
			@java.lang.Override
			public void visit(final int row, final int column, final double value) {
				data[row][column] = value / (singularValues[row]);
			}
		}, 0, (dimension - 1), 0, (p - 1));
		org.apache.commons.math.linear.RealMatrix jv = new org.apache.commons.math.linear.Array2DRowRealMatrix(data , false);
		return jv.transpose().multiply(jv);
	}

	public double getNorm() throws org.apache.commons.math.linear.InvalidMatrixException {
		return singularValues[0];
	}

	public double getConditionNumber() throws org.apache.commons.math.linear.InvalidMatrixException {
		return (singularValues[0]) / (singularValues[((singularValues.length) - 1)]);
	}

	public int getRank() throws java.lang.IllegalStateException {
		final double threshold = (java.lang.Math.max(m, n)) * (java.lang.Math.ulp(singularValues[0]));
		for (int i = (singularValues.length) - 1 ; i >= 0 ; --i) {
			if ((singularValues[i]) > threshold) {
				return i + 1;
			} 
		}
		return 0;
	}

	public org.apache.commons.math.linear.DecompositionSolver getSolver() {
		return new org.apache.commons.math.linear.SingularValueDecompositionImpl.Solver(singularValues , getUT() , getV() , ((getRank()) == (java.lang.Math.max(m, n))));
	}

	private static class Solver implements org.apache.commons.math.linear.DecompositionSolver {
		private final org.apache.commons.math.linear.RealMatrix pseudoInverse;

		private boolean nonSingular;

		private Solver(final double[] singularValues ,final org.apache.commons.math.linear.RealMatrix uT ,final org.apache.commons.math.linear.RealMatrix v ,final boolean nonSingular) {
			double[][] suT = uT.getData();
			for (int i = 0 ; i < (singularValues.length) ; ++i) {
				final double a;
				if ((singularValues[i]) > 0) {
					a = 1.0 / (singularValues[i]);
				} else {
					a = 0.0;
				}
				final double[] suTi = suT[i];
				for (int j = 0 ; j < (suTi.length) ; ++j) {
					suTi[j] *= a;
				}
			}
			pseudoInverse = v.multiply(new org.apache.commons.math.linear.Array2DRowRealMatrix(suT , false));
			this.nonSingular = nonSingular;
		}

		public double[] solve(final double[] b) throws java.lang.IllegalArgumentException {
			return pseudoInverse.operate(b);
		}

		public org.apache.commons.math.linear.RealVector solve(final org.apache.commons.math.linear.RealVector b) throws java.lang.IllegalArgumentException {
			return pseudoInverse.operate(b);
		}

		public org.apache.commons.math.linear.RealMatrix solve(final org.apache.commons.math.linear.RealMatrix b) throws java.lang.IllegalArgumentException {
			return pseudoInverse.multiply(b);
		}

		public boolean isNonSingular() {
			return nonSingular;
		}

		public org.apache.commons.math.linear.RealMatrix getInverse() {
			return pseudoInverse;
		}
	}
}

