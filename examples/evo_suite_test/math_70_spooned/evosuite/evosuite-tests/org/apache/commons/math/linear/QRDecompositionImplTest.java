package org.apache.commons.math.linear;


public class QRDecompositionImplTest extends junit.framework.TestCase {
	double[][] testData3x3NonSingular = new double[][]{ new double[]{ 12 , -51 , 4 } , new double[]{ 6 , 167 , -68 } , new double[]{ -4 , 24 , -41 } };

	double[][] testData3x3Singular = new double[][]{ new double[]{ 1 , 4 , 7 } , new double[]{ 2 , 5 , 8 } , new double[]{ 3 , 6 , 9 } };

	double[][] testData3x4 = new double[][]{ new double[]{ 12 , -51 , 4 , 1 } , new double[]{ 6 , 167 , -68 , 2 } , new double[]{ -4 , 24 , -41 , 3 } };

	double[][] testData4x3 = new double[][]{ new double[]{ 12 , -51 , 4 } , new double[]{ 6 , 167 , -68 } , new double[]{ -4 , 24 , -41 } , new double[]{ -5 , 34 , 7 } };

	private static final double entryTolerance = 1.0E-15;

	private static final double normTolerance = 1.0E-13;

	public QRDecompositionImplTest(java.lang.String name) {
		super(name);
	}

	public void testDimensions() {
		checkDimension(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData3x3NonSingular));
		checkDimension(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData4x3));
		checkDimension(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData3x4));
		java.util.Random r = new java.util.Random(643895747384642L);
		int p = (5 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE)) / 4;
		int q = (7 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE)) / 4;
		checkDimension(createTestMatrix(r, p, q));
		checkDimension(createTestMatrix(r, q, p));
	}

	private void checkDimension(org.apache.commons.math.linear.RealMatrix m) {
		int rows = m.getRowDimension();
		int columns = m.getColumnDimension();
		org.apache.commons.math.linear.QRDecomposition qr = new org.apache.commons.math.linear.QRDecompositionImpl(m);
		junit.framework.Assert.assertEquals(rows, qr.getQ().getRowDimension());
		junit.framework.Assert.assertEquals(rows, qr.getQ().getColumnDimension());
		junit.framework.Assert.assertEquals(rows, qr.getR().getRowDimension());
		junit.framework.Assert.assertEquals(columns, qr.getR().getColumnDimension());
	}

	public void testAEqualQR() {
		checkAEqualQR(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData3x3NonSingular));
		checkAEqualQR(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData3x3Singular));
		checkAEqualQR(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData3x4));
		checkAEqualQR(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData4x3));
		java.util.Random r = new java.util.Random(643895747384642L);
		int p = (5 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE)) / 4;
		int q = (7 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE)) / 4;
		checkAEqualQR(createTestMatrix(r, p, q));
		checkAEqualQR(createTestMatrix(r, q, p));
	}

	private void checkAEqualQR(org.apache.commons.math.linear.RealMatrix m) {
		org.apache.commons.math.linear.QRDecomposition qr = new org.apache.commons.math.linear.QRDecompositionImpl(m);
		double norm = qr.getQ().multiply(qr.getR()).subtract(m).getNorm();
		junit.framework.Assert.assertEquals(0, norm, normTolerance);
	}

	public void testQOrthogonal() {
		checkQOrthogonal(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData3x3NonSingular));
		checkQOrthogonal(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData3x3Singular));
		checkQOrthogonal(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData3x4));
		checkQOrthogonal(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData4x3));
		java.util.Random r = new java.util.Random(643895747384642L);
		int p = (5 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE)) / 4;
		int q = (7 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE)) / 4;
		checkQOrthogonal(createTestMatrix(r, p, q));
		checkQOrthogonal(createTestMatrix(r, q, p));
	}

	private void checkQOrthogonal(org.apache.commons.math.linear.RealMatrix m) {
		org.apache.commons.math.linear.QRDecomposition qr = new org.apache.commons.math.linear.QRDecompositionImpl(m);
		org.apache.commons.math.linear.RealMatrix eye = org.apache.commons.math.linear.MatrixUtils.createRealIdentityMatrix(m.getRowDimension());
		double norm = qr.getQT().multiply(qr.getQ()).subtract(eye).getNorm();
		junit.framework.Assert.assertEquals(0, norm, normTolerance);
	}

	public void testRUpperTriangular() {
		org.apache.commons.math.linear.RealMatrix matrix = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData3x3NonSingular);
		checkUpperTriangular(new org.apache.commons.math.linear.QRDecompositionImpl(matrix).getR());
		matrix = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData3x3Singular);
		checkUpperTriangular(new org.apache.commons.math.linear.QRDecompositionImpl(matrix).getR());
		matrix = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData3x4);
		checkUpperTriangular(new org.apache.commons.math.linear.QRDecompositionImpl(matrix).getR());
		matrix = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData4x3);
		checkUpperTriangular(new org.apache.commons.math.linear.QRDecompositionImpl(matrix).getR());
		java.util.Random r = new java.util.Random(643895747384642L);
		int p = (5 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE)) / 4;
		int q = (7 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE)) / 4;
		matrix = createTestMatrix(r, p, q);
		checkUpperTriangular(new org.apache.commons.math.linear.QRDecompositionImpl(matrix).getR());
		matrix = createTestMatrix(r, p, q);
		checkUpperTriangular(new org.apache.commons.math.linear.QRDecompositionImpl(matrix).getR());
	}

	private void checkUpperTriangular(org.apache.commons.math.linear.RealMatrix m) {
		m.walkInOptimizedOrder(new org.apache.commons.math.linear.DefaultRealMatrixPreservingVisitor() {
			@java.lang.Override
			public void visit(int row, int column, double value) {
				if (column < row) {
					junit.framework.Assert.assertEquals(0.0, value, org.apache.commons.math.linear.QRDecompositionImplTest.entryTolerance);
				} 
			}
		});
	}

	public void testHTrapezoidal() {
		org.apache.commons.math.linear.RealMatrix matrix = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData3x3NonSingular);
		checkTrapezoidal(new org.apache.commons.math.linear.QRDecompositionImpl(matrix).getH());
		matrix = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData3x3Singular);
		checkTrapezoidal(new org.apache.commons.math.linear.QRDecompositionImpl(matrix).getH());
		matrix = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData3x4);
		checkTrapezoidal(new org.apache.commons.math.linear.QRDecompositionImpl(matrix).getH());
		matrix = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData4x3);
		checkTrapezoidal(new org.apache.commons.math.linear.QRDecompositionImpl(matrix).getH());
		java.util.Random r = new java.util.Random(643895747384642L);
		int p = (5 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE)) / 4;
		int q = (7 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE)) / 4;
		matrix = createTestMatrix(r, p, q);
		checkTrapezoidal(new org.apache.commons.math.linear.QRDecompositionImpl(matrix).getH());
		matrix = createTestMatrix(r, p, q);
		checkTrapezoidal(new org.apache.commons.math.linear.QRDecompositionImpl(matrix).getH());
	}

	private void checkTrapezoidal(org.apache.commons.math.linear.RealMatrix m) {
		m.walkInOptimizedOrder(new org.apache.commons.math.linear.DefaultRealMatrixPreservingVisitor() {
			@java.lang.Override
			public void visit(int row, int column, double value) {
				if (column > row) {
					junit.framework.Assert.assertEquals(0.0, value, org.apache.commons.math.linear.QRDecompositionImplTest.entryTolerance);
				} 
			}
		});
	}

	public void testMatricesValues() {
		org.apache.commons.math.linear.QRDecomposition qr = new org.apache.commons.math.linear.QRDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData3x3NonSingular));
		org.apache.commons.math.linear.RealMatrix qRef = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ (-12.0) / 14.0 , 69.0 / 175.0 , (-58.0) / 175.0 } , new double[]{ (-6.0) / 14.0 , (-158.0) / 175.0 , 6.0 / 175.0 } , new double[]{ 4.0 / 14.0 , (-30.0) / 175.0 , (-165.0) / 175.0 } });
		org.apache.commons.math.linear.RealMatrix rRef = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ -14.0 , -21.0 , 14.0 } , new double[]{ 0.0 , -175.0 , 70.0 } , new double[]{ 0.0 , 0.0 , 35.0 } });
		org.apache.commons.math.linear.RealMatrix hRef = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 26.0 / 14.0 , 0.0 , 0.0 } , new double[]{ 6.0 / 14.0 , 648.0 / 325.0 , 0.0 } , new double[]{ (-4.0) / 14.0 , 36.0 / 325.0 , 2.0 } });
		org.apache.commons.math.linear.RealMatrix q = qr.getQ();
		junit.framework.Assert.assertEquals(0, q.subtract(qRef).getNorm(), 1.0E-13);
		org.apache.commons.math.linear.RealMatrix qT = qr.getQT();
		junit.framework.Assert.assertEquals(0, qT.subtract(qRef.transpose()).getNorm(), 1.0E-13);
		org.apache.commons.math.linear.RealMatrix r = qr.getR();
		junit.framework.Assert.assertEquals(0, r.subtract(rRef).getNorm(), 1.0E-13);
		org.apache.commons.math.linear.RealMatrix h = qr.getH();
		junit.framework.Assert.assertEquals(0, h.subtract(hRef).getNorm(), 1.0E-13);
		junit.framework.Assert.assertTrue((q == (qr.getQ())));
		junit.framework.Assert.assertTrue((r == (qr.getR())));
		junit.framework.Assert.assertTrue((h == (qr.getH())));
	}

	private org.apache.commons.math.linear.RealMatrix createTestMatrix(final java.util.Random r, final int rows, final int columns) {
		org.apache.commons.math.linear.RealMatrix m = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(rows, columns);
		m.walkInOptimizedOrder(new org.apache.commons.math.linear.DefaultRealMatrixChangingVisitor() {
			@java.lang.Override
			public double visit(int row, int column, double value) throws org.apache.commons.math.linear.MatrixVisitorException {
				return (2.0 * (r.nextDouble())) - 1.0;
			}
		});
		return m;
	}
}

