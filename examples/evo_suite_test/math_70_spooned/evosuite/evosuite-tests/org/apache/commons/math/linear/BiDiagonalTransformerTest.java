package org.apache.commons.math.linear;


public class BiDiagonalTransformerTest {
	private double[][] testSquare = new double[][]{ new double[]{ 24.0 / 25.0 , 43.0 / 25.0 } , new double[]{ 57.0 / 25.0 , 24.0 / 25.0 } };

	private double[][] testNonSquare = new double[][]{ new double[]{ (-540.0) / 625.0 , 963.0 / 625.0 , (-216.0) / 625.0 } , new double[]{ (-1730.0) / 625.0 , (-744.0) / 625.0 , 1008.0 / 625.0 } , new double[]{ (-720.0) / 625.0 , 1284.0 / 625.0 , (-288.0) / 625.0 } , new double[]{ (-360.0) / 625.0 , 192.0 / 625.0 , 1756.0 / 625.0 } };

	@org.junit.Test
	public void testDimensions() {
		checkdimensions(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare));
		checkdimensions(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testNonSquare));
		checkdimensions(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testNonSquare).transpose());
	}

	private void checkdimensions(org.apache.commons.math.linear.RealMatrix matrix) {
		final int m = matrix.getRowDimension();
		final int n = matrix.getColumnDimension();
		org.apache.commons.math.linear.BiDiagonalTransformer transformer = new org.apache.commons.math.linear.BiDiagonalTransformer(matrix);
		org.junit.Assert.assertEquals(m, transformer.getU().getRowDimension());
		org.junit.Assert.assertEquals(m, transformer.getU().getColumnDimension());
		org.junit.Assert.assertEquals(m, transformer.getB().getRowDimension());
		org.junit.Assert.assertEquals(n, transformer.getB().getColumnDimension());
		org.junit.Assert.assertEquals(n, transformer.getV().getRowDimension());
		org.junit.Assert.assertEquals(n, transformer.getV().getColumnDimension());
	}

	@org.junit.Test
	public void testAEqualUSVt() {
		checkAEqualUSVt(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare));
		checkAEqualUSVt(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testNonSquare));
		checkAEqualUSVt(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testNonSquare).transpose());
	}

	private void checkAEqualUSVt(org.apache.commons.math.linear.RealMatrix matrix) {
		org.apache.commons.math.linear.BiDiagonalTransformer transformer = new org.apache.commons.math.linear.BiDiagonalTransformer(matrix);
		org.apache.commons.math.linear.RealMatrix u = transformer.getU();
		org.apache.commons.math.linear.RealMatrix b = transformer.getB();
		org.apache.commons.math.linear.RealMatrix v = transformer.getV();
		double norm = u.multiply(b).multiply(v.transpose()).subtract(matrix).getNorm();
		org.junit.Assert.assertEquals(0, norm, 1.0E-14);
	}

	@org.junit.Test
	public void testUOrthogonal() {
		checkOrthogonal(new org.apache.commons.math.linear.BiDiagonalTransformer(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare)).getU());
		checkOrthogonal(new org.apache.commons.math.linear.BiDiagonalTransformer(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testNonSquare)).getU());
		checkOrthogonal(new org.apache.commons.math.linear.BiDiagonalTransformer(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testNonSquare).transpose()).getU());
	}

	@org.junit.Test
	public void testVOrthogonal() {
		checkOrthogonal(new org.apache.commons.math.linear.BiDiagonalTransformer(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare)).getV());
		checkOrthogonal(new org.apache.commons.math.linear.BiDiagonalTransformer(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testNonSquare)).getV());
		checkOrthogonal(new org.apache.commons.math.linear.BiDiagonalTransformer(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testNonSquare).transpose()).getV());
	}

	private void checkOrthogonal(org.apache.commons.math.linear.RealMatrix m) {
		org.apache.commons.math.linear.RealMatrix mTm = m.transpose().multiply(m);
		org.apache.commons.math.linear.RealMatrix id = org.apache.commons.math.linear.MatrixUtils.createRealIdentityMatrix(mTm.getRowDimension());
		org.junit.Assert.assertEquals(0, mTm.subtract(id).getNorm(), 1.0E-14);
	}

	@org.junit.Test
	public void testBBiDiagonal() {
		checkBiDiagonal(new org.apache.commons.math.linear.BiDiagonalTransformer(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare)).getB());
		checkBiDiagonal(new org.apache.commons.math.linear.BiDiagonalTransformer(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testNonSquare)).getB());
		checkBiDiagonal(new org.apache.commons.math.linear.BiDiagonalTransformer(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testNonSquare).transpose()).getB());
	}

	private void checkBiDiagonal(org.apache.commons.math.linear.RealMatrix m) {
		final int rows = m.getRowDimension();
		final int cols = m.getColumnDimension();
		for (int i = 0 ; i < rows ; ++i) {
			for (int j = 0 ; j < cols ; ++j) {
				if (rows < cols) {
					if ((i < j) || (i > (j + 1))) {
						org.junit.Assert.assertEquals(0, m.getEntry(i, j), 1.0E-16);
					} 
				} else {
					if ((i < (j - 1)) || (i > j)) {
						org.junit.Assert.assertEquals(0, m.getEntry(i, j), 1.0E-16);
					} 
				}
			}
		}
	}

	@org.junit.Test
	public void testSingularMatrix() {
		org.apache.commons.math.linear.BiDiagonalTransformer transformer = new org.apache.commons.math.linear.BiDiagonalTransformer(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 1.0 , 2.0 , 3.0 } , new double[]{ 2.0 , 3.0 , 4.0 } , new double[]{ 3.0 , 5.0 , 7.0 } }));
		final double s3 = java.lang.Math.sqrt(3.0);
		final double s14 = java.lang.Math.sqrt(14.0);
		final double s1553 = java.lang.Math.sqrt(1553.0);
		org.apache.commons.math.linear.RealMatrix uRef = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ (-1.0) / s14 , 5.0 / (s3 * s14) , 1.0 / s3 } , new double[]{ (-2.0) / s14 , (-4.0) / (s3 * s14) , 1.0 / s3 } , new double[]{ (-3.0) / s14 , 1.0 / (s3 * s14) , (-1.0) / s3 } });
		org.apache.commons.math.linear.RealMatrix bRef = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ -s14 , s1553 / s14 , 0.0 } , new double[]{ 0.0 , ((-87) * s3) / (s14 * s1553) , ((-s3) * s14) / s1553 } , new double[]{ 0.0 , 0.0 , 0.0 } });
		org.apache.commons.math.linear.RealMatrix vRef = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 1.0 , 0.0 , 0.0 } , new double[]{ 0.0 , (-23) / s1553 , 32 / s1553 } , new double[]{ 0.0 , (-32) / s1553 , (-23) / s1553 } });
		org.apache.commons.math.linear.RealMatrix u = transformer.getU();
		org.junit.Assert.assertEquals(0, u.subtract(uRef).getNorm(), 1.0E-14);
		org.apache.commons.math.linear.RealMatrix b = transformer.getB();
		org.junit.Assert.assertEquals(0, b.subtract(bRef).getNorm(), 1.0E-14);
		org.apache.commons.math.linear.RealMatrix v = transformer.getV();
		org.junit.Assert.assertEquals(0, v.subtract(vRef).getNorm(), 1.0E-14);
		org.junit.Assert.assertTrue((u == (transformer.getU())));
		org.junit.Assert.assertTrue((b == (transformer.getB())));
		org.junit.Assert.assertTrue((v == (transformer.getV())));
	}

	@org.junit.Test
	public void testMatricesValues() {
		org.apache.commons.math.linear.BiDiagonalTransformer transformer = new org.apache.commons.math.linear.BiDiagonalTransformer(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare));
		final double s17 = java.lang.Math.sqrt(17.0);
		org.apache.commons.math.linear.RealMatrix uRef = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ (-8) / (5 * s17) , 19 / (5 * s17) } , new double[]{ (-19) / (5 * s17) , (-8) / (5 * s17) } });
		org.apache.commons.math.linear.RealMatrix bRef = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ ((-3) * s17) / 5 , (32 * s17) / 85 } , new double[]{ 0.0 , ((-5) * s17) / 17 } });
		org.apache.commons.math.linear.RealMatrix vRef = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 1.0 , 0.0 } , new double[]{ 0.0 , -1.0 } });
		org.apache.commons.math.linear.RealMatrix u = transformer.getU();
		org.junit.Assert.assertEquals(0, u.subtract(uRef).getNorm(), 1.0E-14);
		org.apache.commons.math.linear.RealMatrix b = transformer.getB();
		org.junit.Assert.assertEquals(0, b.subtract(bRef).getNorm(), 1.0E-14);
		org.apache.commons.math.linear.RealMatrix v = transformer.getV();
		org.junit.Assert.assertEquals(0, v.subtract(vRef).getNorm(), 1.0E-14);
		org.junit.Assert.assertTrue((u == (transformer.getU())));
		org.junit.Assert.assertTrue((b == (transformer.getB())));
		org.junit.Assert.assertTrue((v == (transformer.getV())));
	}

	@org.junit.Test
	public void testUpperOrLower() {
		org.junit.Assert.assertTrue(new org.apache.commons.math.linear.BiDiagonalTransformer(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare)).isUpperBiDiagonal());
		org.junit.Assert.assertTrue(new org.apache.commons.math.linear.BiDiagonalTransformer(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testNonSquare)).isUpperBiDiagonal());
		org.junit.Assert.assertFalse(new org.apache.commons.math.linear.BiDiagonalTransformer(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testNonSquare).transpose()).isUpperBiDiagonal());
	}
}

