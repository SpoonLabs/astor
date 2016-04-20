package org.apache.commons.math.linear;


class TriDiagonalTransformer {
	private final double[][] householderVectors;

	private final double[] main;

	private final double[] secondary;

	private org.apache.commons.math.linear.RealMatrix cachedQ;

	private org.apache.commons.math.linear.RealMatrix cachedQt;

	private org.apache.commons.math.linear.RealMatrix cachedT;

	public TriDiagonalTransformer(org.apache.commons.math.linear.RealMatrix matrix) throws org.apache.commons.math.linear.InvalidMatrixException {
		if (!(matrix.isSquare())) {
			throw new org.apache.commons.math.linear.NonSquareMatrixException(matrix.getRowDimension() , matrix.getColumnDimension());
		} 
		final int m = matrix.getRowDimension();
		householderVectors = matrix.getData();
		main = new double[m];
		secondary = new double[m - 1];
		cachedQ = null;
		cachedQt = null;
		cachedT = null;
		transform();
	}

	public org.apache.commons.math.linear.RealMatrix getQ() {
		if ((cachedQ) == null) {
			cachedQ = getQT().transpose();
		} 
		return cachedQ;
	}

	public org.apache.commons.math.linear.RealMatrix getQT() {
		if ((cachedQt) == null) {
			final int m = householderVectors.length;
			cachedQt = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(m, m);
			for (int k = m - 1 ; k >= 1 ; --k) {
				final double[] hK = householderVectors[(k - 1)];
				final double inv = 1.0 / ((secondary[(k - 1)]) * (hK[k]));
				cachedQt.setEntry(k, k, 1);
				if ((hK[k]) != 0.0) {
					double beta = 1.0 / (secondary[(k - 1)]);
					cachedQt.setEntry(k, k, (1 + (beta * (hK[k]))));
					for (int i = k + 1 ; i < m ; ++i) {
						cachedQt.setEntry(k, i, (beta * (hK[i])));
					}
					for (int j = k + 1 ; j < m ; ++j) {
						beta = 0;
						for (int i = k + 1 ; i < m ; ++i) {
							beta += (cachedQt.getEntry(j, i)) * (hK[i]);
						}
						beta *= inv;
						cachedQt.setEntry(j, k, (beta * (hK[k])));
						for (int i = k + 1 ; i < m ; ++i) {
							cachedQt.addToEntry(j, i, (beta * (hK[i])));
						}
					}
				} 
			}
			cachedQt.setEntry(0, 0, 1);
		} 
		return cachedQt;
	}

	public org.apache.commons.math.linear.RealMatrix getT() {
		if ((cachedT) == null) {
			final int m = main.length;
			cachedT = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(m, m);
			for (int i = 0 ; i < m ; ++i) {
				cachedT.setEntry(i, i, main[i]);
				if (i > 0) {
					cachedT.setEntry(i, (i - 1), secondary[(i - 1)]);
				} 
				if (i < ((main.length) - 1)) {
					cachedT.setEntry(i, (i + 1), secondary[i]);
				} 
			}
		} 
		return cachedT;
	}

	double[][] getHouseholderVectorsRef() {
		return householderVectors;
	}

	double[] getMainDiagonalRef() {
		return main;
	}

	double[] getSecondaryDiagonalRef() {
		return secondary;
	}

	private void transform() {
		final int m = householderVectors.length;
		final double[] z = new double[m];
		for (int k = 0 ; k < (m - 1) ; k++) {
			final double[] hK = householderVectors[k];
			main[k] = hK[k];
			double xNormSqr = 0;
			for (int j = k + 1 ; j < m ; ++j) {
				final double c = hK[j];
				xNormSqr += c * c;
			}
			final double a = (hK[(k + 1)]) > 0 ? -(java.lang.Math.sqrt(xNormSqr)) : java.lang.Math.sqrt(xNormSqr);
			secondary[k] = a;
			if (a != 0.0) {
				hK[(k + 1)] -= a;
				final double beta = (-1) / (a * (hK[(k + 1)]));
				java.util.Arrays.fill(z, (k + 1), m, 0);
				for (int i = k + 1 ; i < m ; ++i) {
					final double[] hI = householderVectors[i];
					final double hKI = hK[i];
					double zI = (hI[i]) * hKI;
					for (int j = i + 1 ; j < m ; ++j) {
						final double hIJ = hI[j];
						zI += hIJ * (hK[j]);
						z[j] += hIJ * hKI;
					}
					z[i] = beta * ((z[i]) + zI);
				}
				double gamma = 0;
				for (int i = k + 1 ; i < m ; ++i) {
					gamma += (z[i]) * (hK[i]);
				}
				gamma *= beta / 2;
				for (int i = k + 1 ; i < m ; ++i) {
					z[i] -= gamma * (hK[i]);
				}
				for (int i = k + 1 ; i < m ; ++i) {
					final double[] hI = householderVectors[i];
					for (int j = i ; j < m ; ++j) {
						hI[j] -= ((hK[i]) * (z[j])) + ((z[i]) * (hK[j]));
					}
				}
			} 
		}
		main[(m - 1)] = householderVectors[(m - 1)][(m - 1)];
	}
}

