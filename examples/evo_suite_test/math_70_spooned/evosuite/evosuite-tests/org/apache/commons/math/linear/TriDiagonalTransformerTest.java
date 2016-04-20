package org.apache.commons.math.linear;


public class TriDiagonalTransformerTest extends junit.framework.TestCase {
	private double[][] testSquare5 = new double[][]{ new double[]{ 1 , 2 , 3 , 1 , 1 } , new double[]{ 2 , 1 , 1 , 3 , 1 } , new double[]{ 3 , 1 , 1 , 1 , 2 } , new double[]{ 1 , 3 , 1 , 2 , 1 } , new double[]{ 1 , 1 , 2 , 1 , 3 } };

	private double[][] testSquare3 = new double[][]{ new double[]{ 1 , 3 , 4 } , new double[]{ 3 , 2 , 2 } , new double[]{ 4 , 2 , 0 } };

	public TriDiagonalTransformerTest(java.lang.String name) {
		super(name);
	}

	public void testNonSquare() {
		try {
			new org.apache.commons.math.linear.TriDiagonalTransformer(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[3][2]));
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.linear.InvalidMatrixException ime) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testAEqualQTQt() {
		checkAEqualQTQt(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare5));
		checkAEqualQTQt(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare3));
	}

	private void checkAEqualQTQt(org.apache.commons.math.linear.RealMatrix matrix) {
		org.apache.commons.math.linear.TriDiagonalTransformer transformer = new org.apache.commons.math.linear.TriDiagonalTransformer(matrix);
		org.apache.commons.math.linear.RealMatrix q = transformer.getQ();
		org.apache.commons.math.linear.RealMatrix qT = transformer.getQT();
		org.apache.commons.math.linear.RealMatrix t = transformer.getT();
		double norm = q.multiply(t).multiply(qT).subtract(matrix).getNorm();
		junit.framework.Assert.assertEquals(0, norm, 4.0E-15);
	}

	public void testNoAccessBelowDiagonal() {
		checkNoAccessBelowDiagonal(testSquare5);
		checkNoAccessBelowDiagonal(testSquare3);
	}

	private void checkNoAccessBelowDiagonal(double[][] data) {
		double[][] modifiedData = new double[data.length][];
		for (int i = 0 ; i < (data.length) ; ++i) {
			modifiedData[i] = data[i].clone();
			java.util.Arrays.fill(modifiedData[i], 0, i, java.lang.Double.NaN);
		}
		org.apache.commons.math.linear.RealMatrix matrix = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(modifiedData);
		org.apache.commons.math.linear.TriDiagonalTransformer transformer = new org.apache.commons.math.linear.TriDiagonalTransformer(matrix);
		org.apache.commons.math.linear.RealMatrix q = transformer.getQ();
		org.apache.commons.math.linear.RealMatrix qT = transformer.getQT();
		org.apache.commons.math.linear.RealMatrix t = transformer.getT();
		double norm = q.multiply(t).multiply(qT).subtract(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(data)).getNorm();
		junit.framework.Assert.assertEquals(0, norm, 4.0E-15);
	}

	public void testQOrthogonal() {
		checkOrthogonal(new org.apache.commons.math.linear.TriDiagonalTransformer(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare5)).getQ());
		checkOrthogonal(new org.apache.commons.math.linear.TriDiagonalTransformer(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare3)).getQ());
	}

	public void testQTOrthogonal() {
		checkOrthogonal(new org.apache.commons.math.linear.TriDiagonalTransformer(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare5)).getQT());
		checkOrthogonal(new org.apache.commons.math.linear.TriDiagonalTransformer(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare3)).getQT());
	}

	private void checkOrthogonal(org.apache.commons.math.linear.RealMatrix m) {
		org.apache.commons.math.linear.RealMatrix mTm = m.transpose().multiply(m);
		org.apache.commons.math.linear.RealMatrix id = org.apache.commons.math.linear.MatrixUtils.createRealIdentityMatrix(mTm.getRowDimension());
		junit.framework.Assert.assertEquals(0, mTm.subtract(id).getNorm(), 1.0E-15);
	}

	public void testTTriDiagonal() {
		checkTriDiagonal(new org.apache.commons.math.linear.TriDiagonalTransformer(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare5)).getT());
		checkTriDiagonal(new org.apache.commons.math.linear.TriDiagonalTransformer(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare3)).getT());
	}

	private void checkTriDiagonal(org.apache.commons.math.linear.RealMatrix m) {
		final int rows = m.getRowDimension();
		final int cols = m.getColumnDimension();
		for (int i = 0 ; i < rows ; ++i) {
			for (int j = 0 ; j < cols ; ++j) {
				if ((i < (j - 1)) || (i > (j + 1))) {
					junit.framework.Assert.assertEquals(0, m.getEntry(i, j), 1.0E-16);
				} 
			}
		}
	}

	public void testMatricesValues5() {
		checkMatricesValues(testSquare5, new double[][]{ new double[]{ 1.0 , 0.0 , 0.0 , 0.0 , 0.0 } , new double[]{ 0.0 , -0.5163977794943222 , 0.016748280772542083 , 0.839800693771262 , 0.16669620021405473 } , new double[]{ 0.0 , -0.7745966692414833 , -0.4354553000860955 , -0.44989322880603355 , -0.08930153582895772 } , new double[]{ 0.0 , -0.2581988897471611 , 0.6364346693566014 , -0.30263204032131164 , 0.6608313651342882 } , new double[]{ 0.0 , -0.2581988897471611 , 0.6364346693566009 , -0.027289660803112598 , -0.7263191580755246 } }, new double[]{ 1 , 4.4 , 1.433099579242636 , -0.89537362758743 , 2.062274048344794 }, new double[]{ -(java.lang.Math.sqrt(15)) , -3.0832882879592476 , 0.6082710842351517 , 1.1786086405912128 });
	}

	public void testMatricesValues3() {
		checkMatricesValues(testSquare3, new double[][]{ new double[]{ 1.0 , 0.0 , 0.0 } , new double[]{ 0.0 , -0.6 , 0.8 } , new double[]{ 0.0 , -0.8 , -0.6 } }, new double[]{ 1 , 2.64 , -0.64 }, new double[]{ -5 , -1.52 });
	}

	private void checkMatricesValues(double[][] matrix, double[][] qRef, double[] mainDiagnonal, double[] secondaryDiagonal) {
		org.apache.commons.math.linear.TriDiagonalTransformer transformer = new org.apache.commons.math.linear.TriDiagonalTransformer(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(matrix));
		org.apache.commons.math.linear.RealMatrix q = transformer.getQ();
		junit.framework.Assert.assertEquals(0, q.subtract(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(qRef)).getNorm(), 1.0E-14);
		org.apache.commons.math.linear.RealMatrix t = transformer.getT();
		double[][] tData = new double[mainDiagnonal.length][mainDiagnonal.length];
		for (int i = 0 ; i < (mainDiagnonal.length) ; ++i) {
			tData[i][i] = mainDiagnonal[i];
			if (i > 0) {
				tData[i][(i - 1)] = secondaryDiagonal[(i - 1)];
			} 
			if (i < (secondaryDiagonal.length)) {
				tData[i][(i + 1)] = secondaryDiagonal[i];
			} 
		}
		junit.framework.Assert.assertEquals(0, t.subtract(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(tData)).getNorm(), 1.0E-14);
		junit.framework.Assert.assertTrue((q == (transformer.getQ())));
		junit.framework.Assert.assertTrue((t == (transformer.getT())));
	}
}

