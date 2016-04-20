package org.apache.commons.math.ode.nonstiff;


public class AdamsNordsieckTransformer {
	private static final java.util.Map<java.lang.Integer, org.apache.commons.math.ode.nonstiff.AdamsNordsieckTransformer> CACHE = new java.util.HashMap<java.lang.Integer, org.apache.commons.math.ode.nonstiff.AdamsNordsieckTransformer>();

	private final org.apache.commons.math.linear.Array2DRowRealMatrix initialization;

	private final org.apache.commons.math.linear.Array2DRowRealMatrix update;

	private final double[] c1;

	private AdamsNordsieckTransformer(final int nSteps) {
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.BigFraction> bigP = buildP(nSteps);
		org.apache.commons.math.linear.FieldDecompositionSolver<org.apache.commons.math.fraction.BigFraction> pSolver = new org.apache.commons.math.linear.FieldLUDecompositionImpl<org.apache.commons.math.fraction.BigFraction>(bigP).getSolver();
		org.apache.commons.math.fraction.BigFraction[] u = new org.apache.commons.math.fraction.BigFraction[nSteps];
		java.util.Arrays.fill(u, org.apache.commons.math.fraction.BigFraction.ONE);
		org.apache.commons.math.fraction.BigFraction[] bigC1 = pSolver.solve(u);
		org.apache.commons.math.fraction.BigFraction[][] shiftedP = bigP.getData();
		for (int i = (shiftedP.length) - 1 ; i > 0 ; --i) {
			shiftedP[i] = shiftedP[(i - 1)];
		}
		shiftedP[0] = new org.apache.commons.math.fraction.BigFraction[nSteps];
		java.util.Arrays.fill(shiftedP[0], org.apache.commons.math.fraction.BigFraction.ZERO);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.BigFraction> bigMSupdate = pSolver.solve(new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.BigFraction>(shiftedP , false));
		bigP.walkInOptimizedOrder(new org.apache.commons.math.linear.DefaultFieldMatrixChangingVisitor<org.apache.commons.math.fraction.BigFraction>(org.apache.commons.math.fraction.BigFraction.ZERO) {
			@java.lang.Override
			public org.apache.commons.math.fraction.BigFraction visit(int row, int column, org.apache.commons.math.fraction.BigFraction value) {
				return (column & 1) == 1 ? value : value.negate();
			}
		});
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.BigFraction> bigRInverse = new org.apache.commons.math.linear.FieldLUDecompositionImpl<org.apache.commons.math.fraction.BigFraction>(bigP).getSolver().getInverse();
		initialization = org.apache.commons.math.linear.MatrixUtils.bigFractionMatrixToRealMatrix(bigRInverse);
		update = org.apache.commons.math.linear.MatrixUtils.bigFractionMatrixToRealMatrix(bigMSupdate);
		c1 = new double[nSteps];
		for (int i = 0 ; i < nSteps ; ++i) {
			c1[i] = bigC1[i].doubleValue();
		}
	}

	public static org.apache.commons.math.ode.nonstiff.AdamsNordsieckTransformer getInstance(final int nSteps) {
		synchronized(CACHE) {
			org.apache.commons.math.ode.nonstiff.AdamsNordsieckTransformer t = CACHE.get(nSteps);
			if (t == null) {
				t = new org.apache.commons.math.ode.nonstiff.AdamsNordsieckTransformer(nSteps);
				CACHE.put(nSteps, t);
			} 
			return t;
		}
	}

	public int getNSteps() {
		return c1.length;
	}

	private org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.BigFraction> buildP(final int nSteps) {
		final org.apache.commons.math.fraction.BigFraction[][] pData = new org.apache.commons.math.fraction.BigFraction[nSteps][nSteps];
		for (int i = 0 ; i < (pData.length) ; ++i) {
			final org.apache.commons.math.fraction.BigFraction[] pI = pData[i];
			final int factor = -(i + 1);
			int aj = factor;
			for (int j = 0 ; j < (pI.length) ; ++j) {
				pI[j] = new org.apache.commons.math.fraction.BigFraction((aj * (j + 2)));
				aj *= factor;
			}
		}
		return new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.BigFraction>(pData , false);
	}

	public org.apache.commons.math.linear.Array2DRowRealMatrix initializeHighOrderDerivatives(final double[] first, final double[][] multistep) {
		for (int i = 0 ; i < (multistep.length) ; ++i) {
			final double[] msI = multistep[i];
			for (int j = 0 ; j < (first.length) ; ++j) {
				msI[j] -= first[j];
			}
		}
		return initialization.multiply(new org.apache.commons.math.linear.Array2DRowRealMatrix(multistep , false));
	}

	public org.apache.commons.math.linear.Array2DRowRealMatrix updateHighOrderDerivativesPhase1(final org.apache.commons.math.linear.Array2DRowRealMatrix highOrder) {
		return update.multiply(highOrder);
	}

	public void updateHighOrderDerivativesPhase2(final double[] start, final double[] end, final org.apache.commons.math.linear.Array2DRowRealMatrix highOrder) {
		final double[][] data = highOrder.getDataRef();
		for (int i = 0 ; i < (data.length) ; ++i) {
			final double[] dataI = data[i];
			final double c1I = c1[i];
			for (int j = 0 ; j < (dataI.length) ; ++j) {
				dataI[j] += c1I * ((start[j]) - (end[j]));
			}
		}
	}
}

