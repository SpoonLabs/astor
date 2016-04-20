package org.apache.commons.math.linear;


public class SingularValueDecompositionImplTest extends junit.framework.TestCase {
	private double[][] testSquare = new double[][]{ new double[]{ 24.0 / 25.0 , 43.0 / 25.0 } , new double[]{ 57.0 / 25.0 , 24.0 / 25.0 } };

	private double[][] testNonSquare = new double[][]{ new double[]{ (-540.0) / 625.0 , 963.0 / 625.0 , (-216.0) / 625.0 } , new double[]{ (-1730.0) / 625.0 , (-744.0) / 625.0 , 1008.0 / 625.0 } , new double[]{ (-720.0) / 625.0 , 1284.0 / 625.0 , (-288.0) / 625.0 } , new double[]{ (-360.0) / 625.0 , 192.0 / 625.0 , 1756.0 / 625.0 } };

	private static final double normTolerance = 1.0E-13;

	public SingularValueDecompositionImplTest(java.lang.String name) {
		super(name);
	}

	public void testMoreRows() {
		final double[] singularValues = new double[]{ 123.456 , 2.3 , 1.001 , 0.999 };
		final int rows = (singularValues.length) + 2;
		final int columns = singularValues.length;
		java.util.Random r = new java.util.Random(15338437322523L);
		org.apache.commons.math.linear.SingularValueDecomposition svd = new org.apache.commons.math.linear.SingularValueDecompositionImpl(createTestMatrix(r, rows, columns, singularValues));
		double[] computedSV = svd.getSingularValues();
		junit.framework.Assert.assertEquals(singularValues.length, computedSV.length);
		for (int i = 0 ; i < (singularValues.length) ; ++i) {
			junit.framework.Assert.assertEquals(singularValues[i], computedSV[i], 1.0E-10);
		}
	}

	public void testMoreColumns() {
		final double[] singularValues = new double[]{ 123.456 , 2.3 , 1.001 , 0.999 };
		final int rows = singularValues.length;
		final int columns = (singularValues.length) + 2;
		java.util.Random r = new java.util.Random(732763225836210L);
		org.apache.commons.math.linear.SingularValueDecomposition svd = new org.apache.commons.math.linear.SingularValueDecompositionImpl(createTestMatrix(r, rows, columns, singularValues));
		double[] computedSV = svd.getSingularValues();
		junit.framework.Assert.assertEquals(singularValues.length, computedSV.length);
		for (int i = 0 ; i < (singularValues.length) ; ++i) {
			junit.framework.Assert.assertEquals(singularValues[i], computedSV[i], 1.0E-10);
		}
	}

	public void testDimensions() {
		org.apache.commons.math.linear.RealMatrix matrix = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare);
		final int m = matrix.getRowDimension();
		final int n = matrix.getColumnDimension();
		org.apache.commons.math.linear.SingularValueDecomposition svd = new org.apache.commons.math.linear.SingularValueDecompositionImpl(matrix);
		junit.framework.Assert.assertEquals(m, svd.getU().getRowDimension());
		junit.framework.Assert.assertEquals(m, svd.getU().getColumnDimension());
		junit.framework.Assert.assertEquals(m, svd.getS().getColumnDimension());
		junit.framework.Assert.assertEquals(n, svd.getS().getColumnDimension());
		junit.framework.Assert.assertEquals(n, svd.getV().getRowDimension());
		junit.framework.Assert.assertEquals(n, svd.getV().getColumnDimension());
	}

	public void testHadamard() {
		org.apache.commons.math.linear.RealMatrix matrix = new org.apache.commons.math.linear.Array2DRowRealMatrix(new double[][]{ new double[]{ 15.0 / 2.0 , 5.0 / 2.0 , 9.0 / 2.0 , 3.0 / 2.0 } , new double[]{ 5.0 / 2.0 , 15.0 / 2.0 , 3.0 / 2.0 , 9.0 / 2.0 } , new double[]{ 9.0 / 2.0 , 3.0 / 2.0 , 15.0 / 2.0 , 5.0 / 2.0 } , new double[]{ 3.0 / 2.0 , 9.0 / 2.0 , 5.0 / 2.0 , 15.0 / 2.0 } } , false);
		org.apache.commons.math.linear.SingularValueDecomposition svd = new org.apache.commons.math.linear.SingularValueDecompositionImpl(matrix);
		junit.framework.Assert.assertEquals(16.0, svd.getSingularValues()[0], 1.0E-14);
		junit.framework.Assert.assertEquals(8.0, svd.getSingularValues()[1], 1.0E-14);
		junit.framework.Assert.assertEquals(4.0, svd.getSingularValues()[2], 1.0E-14);
		junit.framework.Assert.assertEquals(2.0, svd.getSingularValues()[3], 1.0E-14);
		org.apache.commons.math.linear.RealMatrix fullCovariance = new org.apache.commons.math.linear.Array2DRowRealMatrix(new double[][]{ new double[]{ 85.0 / 1024 , (-51.0) / 1024 , (-75.0) / 1024 , 45.0 / 1024 } , new double[]{ (-51.0) / 1024 , 85.0 / 1024 , 45.0 / 1024 , (-75.0) / 1024 } , new double[]{ (-75.0) / 1024 , 45.0 / 1024 , 85.0 / 1024 , (-51.0) / 1024 } , new double[]{ 45.0 / 1024 , (-75.0) / 1024 , (-51.0) / 1024 , 85.0 / 1024 } } , false);
		junit.framework.Assert.assertEquals(0.0, fullCovariance.subtract(svd.getCovariance(0.0)).getNorm(), 1.0E-14);
		org.apache.commons.math.linear.RealMatrix halfCovariance = new org.apache.commons.math.linear.Array2DRowRealMatrix(new double[][]{ new double[]{ 5.0 / 1024 , (-3.0) / 1024 , 5.0 / 1024 , (-3.0) / 1024 } , new double[]{ (-3.0) / 1024 , 5.0 / 1024 , (-3.0) / 1024 , 5.0 / 1024 } , new double[]{ 5.0 / 1024 , (-3.0) / 1024 , 5.0 / 1024 , (-3.0) / 1024 } , new double[]{ (-3.0) / 1024 , 5.0 / 1024 , (-3.0) / 1024 , 5.0 / 1024 } } , false);
		junit.framework.Assert.assertEquals(0.0, halfCovariance.subtract(svd.getCovariance(6.0)).getNorm(), 1.0E-14);
	}

	public void testAEqualUSVt() {
		checkAEqualUSVt(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare));
		checkAEqualUSVt(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testNonSquare));
		checkAEqualUSVt(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testNonSquare).transpose());
	}

	public void checkAEqualUSVt(final org.apache.commons.math.linear.RealMatrix matrix) {
		org.apache.commons.math.linear.SingularValueDecomposition svd = new org.apache.commons.math.linear.SingularValueDecompositionImpl(matrix);
		org.apache.commons.math.linear.RealMatrix u = svd.getU();
		org.apache.commons.math.linear.RealMatrix s = svd.getS();
		org.apache.commons.math.linear.RealMatrix v = svd.getV();
		double norm = u.multiply(s).multiply(v.transpose()).subtract(matrix).getNorm();
		junit.framework.Assert.assertEquals(0, norm, normTolerance);
	}

	public void testUOrthogonal() {
		checkOrthogonal(new org.apache.commons.math.linear.SingularValueDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare)).getU());
		checkOrthogonal(new org.apache.commons.math.linear.SingularValueDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testNonSquare)).getU());
		checkOrthogonal(new org.apache.commons.math.linear.SingularValueDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testNonSquare).transpose()).getU());
	}

	public void testVOrthogonal() {
		checkOrthogonal(new org.apache.commons.math.linear.SingularValueDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare)).getV());
		checkOrthogonal(new org.apache.commons.math.linear.SingularValueDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testNonSquare)).getV());
		checkOrthogonal(new org.apache.commons.math.linear.SingularValueDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testNonSquare).transpose()).getV());
	}

	public void checkOrthogonal(final org.apache.commons.math.linear.RealMatrix m) {
		org.apache.commons.math.linear.RealMatrix mTm = m.transpose().multiply(m);
		org.apache.commons.math.linear.RealMatrix id = org.apache.commons.math.linear.MatrixUtils.createRealIdentityMatrix(mTm.getRowDimension());
		junit.framework.Assert.assertEquals(0, mTm.subtract(id).getNorm(), normTolerance);
	}

	public void testMatricesValues1() {
		org.apache.commons.math.linear.SingularValueDecomposition svd = new org.apache.commons.math.linear.SingularValueDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare));
		org.apache.commons.math.linear.RealMatrix uRef = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 3.0 / 5.0 , (-4.0) / 5.0 } , new double[]{ 4.0 / 5.0 , 3.0 / 5.0 } });
		org.apache.commons.math.linear.RealMatrix sRef = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 3.0 , 0.0 } , new double[]{ 0.0 , 1.0 } });
		org.apache.commons.math.linear.RealMatrix vRef = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 4.0 / 5.0 , 3.0 / 5.0 } , new double[]{ 3.0 / 5.0 , (-4.0) / 5.0 } });
		org.apache.commons.math.linear.RealMatrix u = svd.getU();
		junit.framework.Assert.assertEquals(0, u.subtract(uRef).getNorm(), normTolerance);
		org.apache.commons.math.linear.RealMatrix s = svd.getS();
		junit.framework.Assert.assertEquals(0, s.subtract(sRef).getNorm(), normTolerance);
		org.apache.commons.math.linear.RealMatrix v = svd.getV();
		junit.framework.Assert.assertEquals(0, v.subtract(vRef).getNorm(), normTolerance);
		junit.framework.Assert.assertTrue((u == (svd.getU())));
		junit.framework.Assert.assertTrue((s == (svd.getS())));
		junit.framework.Assert.assertTrue((v == (svd.getV())));
	}

	public void useless_testMatricesValues2() {
		org.apache.commons.math.linear.RealMatrix uRef = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 0.0 / 5.0 , 3.0 / 5.0 , 0.0 / 5.0 } , new double[]{ (-4.0) / 5.0 , 0.0 / 5.0 , (-3.0) / 5.0 } , new double[]{ 0.0 / 5.0 , 4.0 / 5.0 , 0.0 / 5.0 } , new double[]{ (-3.0) / 5.0 , 0.0 / 5.0 , 4.0 / 5.0 } });
		org.apache.commons.math.linear.RealMatrix sRef = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 4.0 , 0.0 , 0.0 } , new double[]{ 0.0 , 3.0 , 0.0 } , new double[]{ 0.0 , 0.0 , 2.0 } });
		org.apache.commons.math.linear.RealMatrix vRef = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 80.0 / 125.0 , (-60.0) / 125.0 , 75.0 / 125.0 } , new double[]{ 24.0 / 125.0 , 107.0 / 125.0 , 60.0 / 125.0 } , new double[]{ (-93.0) / 125.0 , (-24.0) / 125.0 , 80.0 / 125.0 } });
		org.apache.commons.math.linear.SingularValueDecomposition svd = new org.apache.commons.math.linear.SingularValueDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testNonSquare));
		org.apache.commons.math.linear.RealMatrix u = svd.getU();
		junit.framework.Assert.assertEquals(0, u.subtract(uRef).getNorm(), normTolerance);
		org.apache.commons.math.linear.RealMatrix s = svd.getS();
		junit.framework.Assert.assertEquals(0, s.subtract(sRef).getNorm(), normTolerance);
		org.apache.commons.math.linear.RealMatrix v = svd.getV();
		junit.framework.Assert.assertEquals(0, v.subtract(vRef).getNorm(), normTolerance);
		junit.framework.Assert.assertTrue((u == (svd.getU())));
		junit.framework.Assert.assertTrue((s == (svd.getS())));
		junit.framework.Assert.assertTrue((v == (svd.getV())));
	}

	public void testConditionNumber() {
		org.apache.commons.math.linear.SingularValueDecompositionImpl svd = new org.apache.commons.math.linear.SingularValueDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare));
		junit.framework.Assert.assertEquals(3.0, svd.getConditionNumber(), 1.5E-15);
	}

	private org.apache.commons.math.linear.RealMatrix createTestMatrix(final java.util.Random r, final int rows, final int columns, final double[] singularValues) {
		final org.apache.commons.math.linear.RealMatrix u = org.apache.commons.math.linear.EigenDecompositionImplTest.createOrthogonalMatrix(r, rows);
		final org.apache.commons.math.linear.RealMatrix d = org.apache.commons.math.linear.EigenDecompositionImplTest.createDiagonalMatrix(singularValues, rows, columns);
		final org.apache.commons.math.linear.RealMatrix v = org.apache.commons.math.linear.EigenDecompositionImplTest.createOrthogonalMatrix(r, columns);
		return u.multiply(d).multiply(v);
	}
}

