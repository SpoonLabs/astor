package org.apache.commons.math.linear;


class BiDiagonalTransformer {
	private final double[][] householderVectors;

	private final double[] main;

	private final double[] secondary;

	private org.apache.commons.math.linear.RealMatrix cachedU;

	private org.apache.commons.math.linear.RealMatrix cachedB;

	private org.apache.commons.math.linear.RealMatrix cachedV;

	public BiDiagonalTransformer(org.apache.commons.math.linear.RealMatrix matrix) {
		final int m = matrix.getRowDimension();
		final int n = matrix.getColumnDimension();
		final int p = java.lang.Math.min(m, n);
		householderVectors = matrix.getData();
		main = new double[p];
		secondary = new double[p - 1];
		cachedU = null;
		cachedB = null;
		cachedV = null;
		if (m >= n) {
			transformToUpperBiDiagonal();
		} else {
			transformToLowerBiDiagonal();
		}
	}

	public org.apache.commons.math.linear.RealMatrix getU() {
		if ((cachedU) == null) {
			final int m = householderVectors.length;
			final int n = householderVectors[0].length;
			final int p = main.length;
			final int diagOffset = m >= n ? 0 : 1;
			final double[] diagonal = m >= n ? main : secondary;
			cachedU = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(m, m);
			for (int k = m - 1 ; k >= p ; --k) {
				cachedU.setEntry(k, k, 1);
			}
			for (int k = p - 1 ; k >= diagOffset ; --k) {
				final double[] hK = householderVectors[k];
				cachedU.setEntry(k, k, 1);
				if ((hK[(k - diagOffset)]) != 0.0) {
					for (int j = k ; j < m ; ++j) {
						double alpha = 0;
						for (int i = k ; i < m ; ++i) {
							alpha -= (cachedU.getEntry(i, j)) * (householderVectors[i][(k - diagOffset)]);
						}
						alpha /= (diagonal[(k - diagOffset)]) * (hK[(k - diagOffset)]);
						for (int i = k ; i < m ; ++i) {
							cachedU.addToEntry(i, j, ((-alpha) * (householderVectors[i][(k - diagOffset)])));
						}
					}
				} 
			}
			if (diagOffset > 0) {
				cachedU.setEntry(0, 0, 1);
			} 
		} 
		return cachedU;
	}

	public org.apache.commons.math.linear.RealMatrix getB() {
		if ((cachedB) == null) {
			final int m = householderVectors.length;
			final int n = householderVectors[0].length;
			cachedB = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(m, n);
			for (int i = 0 ; i < (main.length) ; ++i) {
				cachedB.setEntry(i, i, main[i]);
				if (m < n) {
					if (i > 0) {
						cachedB.setEntry(i, (i - 1), secondary[(i - 1)]);
					} 
				} else {
					if (i < ((main.length) - 1)) {
						cachedB.setEntry(i, (i + 1), secondary[i]);
					} 
				}
			}
		} 
		return cachedB;
	}

	public org.apache.commons.math.linear.RealMatrix getV() {
		if ((cachedV) == null) {
			final int m = householderVectors.length;
			final int n = householderVectors[0].length;
			final int p = main.length;
			final int diagOffset = m >= n ? 1 : 0;
			final double[] diagonal = m >= n ? secondary : main;
			cachedV = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(n, n);
			for (int k = n - 1 ; k >= p ; --k) {
				cachedV.setEntry(k, k, 1);
			}
			for (int k = p - 1 ; k >= diagOffset ; --k) {
				final double[] hK = householderVectors[(k - diagOffset)];
				cachedV.setEntry(k, k, 1);
				if ((hK[k]) != 0.0) {
					for (int j = k ; j < n ; ++j) {
						double beta = 0;
						for (int i = k ; i < n ; ++i) {
							beta -= (cachedV.getEntry(i, j)) * (hK[i]);
						}
						beta /= (diagonal[(k - diagOffset)]) * (hK[k]);
						for (int i = k ; i < n ; ++i) {
							cachedV.addToEntry(i, j, ((-beta) * (hK[i])));
						}
					}
				} 
			}
			if (diagOffset > 0) {
				cachedV.setEntry(0, 0, 1);
			} 
		} 
		return cachedV;
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

	boolean isUpperBiDiagonal() {
		return (householderVectors.length) >= (householderVectors[0].length);
	}

	private void transformToUpperBiDiagonal() {
		final int m = householderVectors.length;
		final int n = householderVectors[0].length;
		for (int k = 0 ; k < n ; k++) {
			double xNormSqr = 0;
			for (int i = k ; i < m ; ++i) {
				final double c = householderVectors[i][k];
				xNormSqr += c * c;
			}
			final double[] hK = householderVectors[k];
			final double a = (hK[k]) > 0 ? -(java.lang.Math.sqrt(xNormSqr)) : java.lang.Math.sqrt(xNormSqr);
			main[k] = a;
			if (a != 0.0) {
				hK[k] -= a;
				for (int j = k + 1 ; j < n ; ++j) {
					double alpha = 0;
					for (int i = k ; i < m ; ++i) {
						final double[] hI = householderVectors[i];
						alpha -= (hI[j]) * (hI[k]);
					}
					alpha /= a * (householderVectors[k][k]);
					for (int i = k ; i < m ; ++i) {
						final double[] hI = householderVectors[i];
						hI[j] -= alpha * (hI[k]);
					}
				}
			} 
			if (k < (n - 1)) {
				xNormSqr = 0;
				for (int j = k + 1 ; j < n ; ++j) {
					final double c = hK[j];
					xNormSqr += c * c;
				}
				final double b = (hK[(k + 1)]) > 0 ? -(java.lang.Math.sqrt(xNormSqr)) : java.lang.Math.sqrt(xNormSqr);
				secondary[k] = b;
				if (b != 0.0) {
					hK[(k + 1)] -= b;
					for (int i = k + 1 ; i < m ; ++i) {
						final double[] hI = householderVectors[i];
						double beta = 0;
						for (int j = k + 1 ; j < n ; ++j) {
							beta -= (hI[j]) * (hK[j]);
						}
						beta /= b * (hK[(k + 1)]);
						for (int j = k + 1 ; j < n ; ++j) {
							hI[j] -= beta * (hK[j]);
						}
					}
				} 
			} 
		}
	}

	private void transformToLowerBiDiagonal() {
		final int m = householderVectors.length;
		final int n = householderVectors[0].length;
		for (int k = 0 ; k < m ; k++) {
			final double[] hK = householderVectors[k];
			double xNormSqr = 0;
			for (int j = k ; j < n ; ++j) {
				final double c = hK[j];
				xNormSqr += c * c;
			}
			final double a = (hK[k]) > 0 ? -(java.lang.Math.sqrt(xNormSqr)) : java.lang.Math.sqrt(xNormSqr);
			main[k] = a;
			if (a != 0.0) {
				hK[k] -= a;
				for (int i = k + 1 ; i < m ; ++i) {
					final double[] hI = householderVectors[i];
					double alpha = 0;
					for (int j = k ; j < n ; ++j) {
						alpha -= (hI[j]) * (hK[j]);
					}
					alpha /= a * (householderVectors[k][k]);
					for (int j = k ; j < n ; ++j) {
						hI[j] -= alpha * (hK[j]);
					}
				}
			} 
			if (k < (m - 1)) {
				final double[] hKp1 = householderVectors[(k + 1)];
				xNormSqr = 0;
				for (int i = k + 1 ; i < m ; ++i) {
					final double c = householderVectors[i][k];
					xNormSqr += c * c;
				}
				final double b = (hKp1[k]) > 0 ? -(java.lang.Math.sqrt(xNormSqr)) : java.lang.Math.sqrt(xNormSqr);
				secondary[k] = b;
				if (b != 0.0) {
					hKp1[k] -= b;
					for (int j = k + 1 ; j < n ; ++j) {
						double beta = 0;
						for (int i = k + 1 ; i < m ; ++i) {
							final double[] hI = householderVectors[i];
							beta -= (hI[j]) * (hI[k]);
						}
						beta /= b * (hKp1[k]);
						for (int i = k + 1 ; i < m ; ++i) {
							final double[] hI = householderVectors[i];
							hI[j] -= beta * (hI[k]);
						}
					}
				} 
			} 
		}
	}
}

