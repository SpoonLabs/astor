package org.apache.commons.math.linear;


public final class SparseRealMatrixTest extends junit.framework.TestCase {
	protected double[][] id = new double[][]{ new double[]{ 1.0 , 0.0 , 0.0 } , new double[]{ 0.0 , 1.0 , 0.0 } , new double[]{ 0.0 , 0.0 , 1.0 } };

	protected double[][] testData = new double[][]{ new double[]{ 1.0 , 2.0 , 3.0 } , new double[]{ 2.0 , 5.0 , 3.0 } , new double[]{ 1.0 , 0.0 , 8.0 } };

	protected double[][] testDataLU = new double[][]{ new double[]{ 2.0 , 5.0 , 3.0 } , new double[]{ 0.5 , -2.5 , 6.5 } , new double[]{ 0.5 , 0.2 , 0.2 } };

	protected double[][] testDataPlus2 = new double[][]{ new double[]{ 3.0 , 4.0 , 5.0 } , new double[]{ 4.0 , 7.0 , 5.0 } , new double[]{ 3.0 , 2.0 , 10.0 } };

	protected double[][] testDataMinus = new double[][]{ new double[]{ -1.0 , -2.0 , -3.0 } , new double[]{ -2.0 , -5.0 , -3.0 } , new double[]{ -1.0 , 0.0 , -8.0 } };

	protected double[] testDataRow1 = new double[]{ 1.0 , 2.0 , 3.0 };

	protected double[] testDataCol3 = new double[]{ 3.0 , 3.0 , 8.0 };

	protected double[][] testDataInv = new double[][]{ new double[]{ -40.0 , 16.0 , 9.0 } , new double[]{ 13.0 , -5.0 , -3.0 } , new double[]{ 5.0 , -2.0 , -1.0 } };

	protected double[] preMultTest = new double[]{ 8 , 12 , 33 };

	protected double[][] testData2 = new double[][]{ new double[]{ 1.0 , 2.0 , 3.0 } , new double[]{ 2.0 , 5.0 , 3.0 } };

	protected double[][] testData2T = new double[][]{ new double[]{ 1.0 , 2.0 } , new double[]{ 2.0 , 5.0 } , new double[]{ 3.0 , 3.0 } };

	protected double[][] testDataPlusInv = new double[][]{ new double[]{ -39.0 , 18.0 , 12.0 } , new double[]{ 15.0 , 0.0 , 0.0 } , new double[]{ 6.0 , -2.0 , 7.0 } };

	protected double[][] luData = new double[][]{ new double[]{ 2.0 , 3.0 , 3.0 } , new double[]{ 0.0 , 5.0 , 7.0 } , new double[]{ 6.0 , 9.0 , 8.0 } };

	protected double[][] luDataLUDecomposition = new double[][]{ new double[]{ 6.0 , 9.0 , 8.0 } , new double[]{ 0.0 , 5.0 , 7.0 } , new double[]{ 0.33333333333333 , 0.0 , 0.33333333333333 } };

	protected double[][] singular = new double[][]{ new double[]{ 2.0 , 3.0 } , new double[]{ 2.0 , 3.0 } };

	protected double[][] bigSingular = new double[][]{ new double[]{ 1.0 , 2.0 , 3.0 , 4.0 } , new double[]{ 2.0 , 5.0 , 3.0 , 4.0 } , new double[]{ 7.0 , 3.0 , 256.0 , 1930.0 } , new double[]{ 3.0 , 7.0 , 6.0 , 8.0 } };

	protected double[][] detData = new double[][]{ new double[]{ 1.0 , 2.0 , 3.0 } , new double[]{ 4.0 , 5.0 , 6.0 } , new double[]{ 7.0 , 8.0 , 10.0 } };

	protected double[][] detData2 = new double[][]{ new double[]{ 1.0 , 3.0 } , new double[]{ 2.0 , 4.0 } };

	protected double[] testVector = new double[]{ 1 , 2 , 3 };

	protected double[] testVector2 = new double[]{ 1 , 2 , 3 , 4 };

	protected double[][] subTestData = new double[][]{ new double[]{ 1 , 2 , 3 , 4 } , new double[]{ 1.5 , 2.5 , 3.5 , 4.5 } , new double[]{ 2 , 4 , 6 , 8 } , new double[]{ 4 , 5 , 6 , 7 } };

	protected double[][] subRows02Cols13 = new double[][]{ new double[]{ 2 , 4 } , new double[]{ 4 , 8 } };

	protected double[][] subRows03Cols12 = new double[][]{ new double[]{ 2 , 3 } , new double[]{ 5 , 6 } };

	protected double[][] subRows03Cols123 = new double[][]{ new double[]{ 2 , 3 , 4 } , new double[]{ 5 , 6 , 7 } };

	protected double[][] subRows20Cols123 = new double[][]{ new double[]{ 4 , 6 , 8 } , new double[]{ 2 , 3 , 4 } };

	protected double[][] subRows31Cols31 = new double[][]{ new double[]{ 7 , 5 } , new double[]{ 4.5 , 2.5 } };

	protected double[][] subRows01Cols23 = new double[][]{ new double[]{ 3 , 4 } , new double[]{ 3.5 , 4.5 } };

	protected double[][] subRows23Cols00 = new double[][]{ new double[]{ 2 } , new double[]{ 4 } };

	protected double[][] subRows00Cols33 = new double[][]{ new double[]{ 4 } };

	protected double[][] subRow0 = new double[][]{ new double[]{ 1 , 2 , 3 , 4 } };

	protected double[][] subRow3 = new double[][]{ new double[]{ 4 , 5 , 6 , 7 } };

	protected double[][] subColumn1 = new double[][]{ new double[]{ 2 } , new double[]{ 2.5 } , new double[]{ 4 } , new double[]{ 5 } };

	protected double[][] subColumn3 = new double[][]{ new double[]{ 4 } , new double[]{ 4.5 } , new double[]{ 8 } , new double[]{ 7 } };

	protected double entryTolerance = 1.0E-15;

	protected double normTolerance = 1.0E-13;

	public SparseRealMatrixTest(java.lang.String name) {
		super(name);
	}

	public void testDimensions() {
		org.apache.commons.math.linear.OpenMapRealMatrix m = createSparseMatrix(testData);
		org.apache.commons.math.linear.OpenMapRealMatrix m2 = createSparseMatrix(testData2);
		junit.framework.Assert.assertEquals("testData row dimension", 3, m.getRowDimension());
		junit.framework.Assert.assertEquals("testData column dimension", 3, m.getColumnDimension());
		junit.framework.Assert.assertTrue("testData is square", m.isSquare());
		junit.framework.Assert.assertEquals("testData2 row dimension", m2.getRowDimension(), 2);
		junit.framework.Assert.assertEquals("testData2 column dimension", m2.getColumnDimension(), 3);
		junit.framework.Assert.assertTrue("testData2 is not square", !(m2.isSquare()));
	}

	public void testCopyFunctions() {
		org.apache.commons.math.linear.OpenMapRealMatrix m1 = createSparseMatrix(testData);
		org.apache.commons.math.linear.RealMatrix m2 = m1.copy();
		junit.framework.Assert.assertEquals(m1.getClass(), m2.getClass());
		junit.framework.Assert.assertEquals(m2, m1);
		org.apache.commons.math.linear.OpenMapRealMatrix m3 = createSparseMatrix(testData);
		org.apache.commons.math.linear.RealMatrix m4 = m3.copy();
		junit.framework.Assert.assertEquals(m3.getClass(), m4.getClass());
		junit.framework.Assert.assertEquals(m4, m3);
	}

	public void testAdd() {
		org.apache.commons.math.linear.OpenMapRealMatrix m = createSparseMatrix(testData);
		org.apache.commons.math.linear.OpenMapRealMatrix mInv = createSparseMatrix(testDataInv);
		org.apache.commons.math.linear.OpenMapRealMatrix mDataPlusInv = createSparseMatrix(testDataPlusInv);
		org.apache.commons.math.linear.RealMatrix mPlusMInv = m.add(mInv);
		for (int row = 0 ; row < (m.getRowDimension()) ; row++) {
			for (int col = 0 ; col < (m.getColumnDimension()) ; col++) {
				junit.framework.Assert.assertEquals("sum entry entry", mDataPlusInv.getEntry(row, col), mPlusMInv.getEntry(row, col), entryTolerance);
			}
		}
	}

	public void testAddFail() {
		org.apache.commons.math.linear.OpenMapRealMatrix m = createSparseMatrix(testData);
		org.apache.commons.math.linear.OpenMapRealMatrix m2 = createSparseMatrix(testData2);
		try {
			m.add(m2);
			junit.framework.Assert.fail("IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testNorm() {
		org.apache.commons.math.linear.OpenMapRealMatrix m = createSparseMatrix(testData);
		org.apache.commons.math.linear.OpenMapRealMatrix m2 = createSparseMatrix(testData2);
		junit.framework.Assert.assertEquals("testData norm", 14.0, m.getNorm(), entryTolerance);
		junit.framework.Assert.assertEquals("testData2 norm", 7.0, m2.getNorm(), entryTolerance);
	}

	public void testPlusMinus() {
		org.apache.commons.math.linear.OpenMapRealMatrix m = createSparseMatrix(testData);
		org.apache.commons.math.linear.OpenMapRealMatrix n = createSparseMatrix(testDataInv);
		assertClose("m-n = m + -n", m.subtract(n), n.scalarMultiply(-1.0).add(m), entryTolerance);
		try {
			m.subtract(createSparseMatrix(testData2));
			junit.framework.Assert.fail("Expecting illegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testMultiply() {
		org.apache.commons.math.linear.OpenMapRealMatrix m = createSparseMatrix(testData);
		org.apache.commons.math.linear.OpenMapRealMatrix mInv = createSparseMatrix(testDataInv);
		org.apache.commons.math.linear.OpenMapRealMatrix identity = createSparseMatrix(id);
		org.apache.commons.math.linear.OpenMapRealMatrix m2 = createSparseMatrix(testData2);
		assertClose("inverse multiply", m.multiply(mInv), identity, entryTolerance);
		assertClose("inverse multiply", m.multiply(new org.apache.commons.math.linear.BlockRealMatrix(testDataInv)), identity, entryTolerance);
		assertClose("inverse multiply", mInv.multiply(m), identity, entryTolerance);
		assertClose("identity multiply", m.multiply(identity), m, entryTolerance);
		assertClose("identity multiply", identity.multiply(mInv), mInv, entryTolerance);
		assertClose("identity multiply", m2.multiply(identity), m2, entryTolerance);
		try {
			m.multiply(createSparseMatrix(bigSingular));
			junit.framework.Assert.fail("Expecting illegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	private double[][] d3 = new double[][]{ new double[]{ 1 , 2 , 3 , 4 } , new double[]{ 5 , 6 , 7 , 8 } };

	private double[][] d4 = new double[][]{ new double[]{ 1 } , new double[]{ 2 } , new double[]{ 3 } , new double[]{ 4 } };

	private double[][] d5 = new double[][]{ new double[]{ 30 } , new double[]{ 70 } };

	public void testMultiply2() {
		org.apache.commons.math.linear.RealMatrix m3 = createSparseMatrix(d3);
		org.apache.commons.math.linear.RealMatrix m4 = createSparseMatrix(d4);
		org.apache.commons.math.linear.RealMatrix m5 = createSparseMatrix(d5);
		assertClose("m3*m4=m5", m3.multiply(m4), m5, entryTolerance);
	}

	public void testTrace() {
		org.apache.commons.math.linear.RealMatrix m = createSparseMatrix(id);
		junit.framework.Assert.assertEquals("identity trace", 3.0, m.getTrace(), entryTolerance);
		m = createSparseMatrix(testData2);
		try {
			m.getTrace();
			junit.framework.Assert.fail("Expecting NonSquareMatrixException");
		} catch (org.apache.commons.math.linear.NonSquareMatrixException ex) {
		}
	}

	public void testScalarAdd() {
		org.apache.commons.math.linear.RealMatrix m = createSparseMatrix(testData);
		assertClose("scalar add", createSparseMatrix(testDataPlus2), m.scalarAdd(2.0), entryTolerance);
	}

	public void testOperate() {
		org.apache.commons.math.linear.RealMatrix m = createSparseMatrix(id);
		assertClose("identity operate", testVector, m.operate(testVector), entryTolerance);
		assertClose("identity operate", testVector, m.operate(new org.apache.commons.math.linear.ArrayRealVector(testVector)).getData(), entryTolerance);
		m = createSparseMatrix(bigSingular);
		try {
			m.operate(testVector);
			junit.framework.Assert.fail("Expecting illegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testMath209() {
		org.apache.commons.math.linear.RealMatrix a = createSparseMatrix(new double[][]{ new double[]{ 1 , 2 } , new double[]{ 3 , 4 } , new double[]{ 5 , 6 } });
		double[] b = a.operate(new double[]{ 1 , 1 });
		junit.framework.Assert.assertEquals(a.getRowDimension(), b.length);
		junit.framework.Assert.assertEquals(3.0, b[0], 1.0E-12);
		junit.framework.Assert.assertEquals(7.0, b[1], 1.0E-12);
		junit.framework.Assert.assertEquals(11.0, b[2], 1.0E-12);
	}

	public void testTranspose() {
		org.apache.commons.math.linear.RealMatrix m = createSparseMatrix(testData);
		org.apache.commons.math.linear.RealMatrix mIT = new org.apache.commons.math.linear.LUDecompositionImpl(m).getSolver().getInverse().transpose();
		org.apache.commons.math.linear.RealMatrix mTI = new org.apache.commons.math.linear.LUDecompositionImpl(m.transpose()).getSolver().getInverse();
		assertClose("inverse-transpose", mIT, mTI, normTolerance);
		m = createSparseMatrix(testData2);
		org.apache.commons.math.linear.RealMatrix mt = createSparseMatrix(testData2T);
		assertClose("transpose", mt, m.transpose(), normTolerance);
	}

	public void testPremultiplyVector() {
		org.apache.commons.math.linear.RealMatrix m = createSparseMatrix(testData);
		assertClose("premultiply", m.preMultiply(testVector), preMultTest, normTolerance);
		assertClose("premultiply", m.preMultiply(new org.apache.commons.math.linear.ArrayRealVector(testVector).getData()), preMultTest, normTolerance);
		m = createSparseMatrix(bigSingular);
		try {
			m.preMultiply(testVector);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testPremultiply() {
		org.apache.commons.math.linear.RealMatrix m3 = createSparseMatrix(d3);
		org.apache.commons.math.linear.RealMatrix m4 = createSparseMatrix(d4);
		org.apache.commons.math.linear.RealMatrix m5 = createSparseMatrix(d5);
		assertClose("m3*m4=m5", m4.preMultiply(m3), m5, entryTolerance);
		org.apache.commons.math.linear.OpenMapRealMatrix m = createSparseMatrix(testData);
		org.apache.commons.math.linear.OpenMapRealMatrix mInv = createSparseMatrix(testDataInv);
		org.apache.commons.math.linear.OpenMapRealMatrix identity = createSparseMatrix(id);
		assertClose("inverse multiply", m.preMultiply(mInv), identity, entryTolerance);
		assertClose("inverse multiply", mInv.preMultiply(m), identity, entryTolerance);
		assertClose("identity multiply", m.preMultiply(identity), m, entryTolerance);
		assertClose("identity multiply", identity.preMultiply(mInv), mInv, entryTolerance);
		try {
			m.preMultiply(createSparseMatrix(bigSingular));
			junit.framework.Assert.fail("Expecting illegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testGetVectors() {
		org.apache.commons.math.linear.RealMatrix m = createSparseMatrix(testData);
		assertClose("get row", m.getRow(0), testDataRow1, entryTolerance);
		assertClose("get col", m.getColumn(2), testDataCol3, entryTolerance);
		try {
			m.getRow(10);
			junit.framework.Assert.fail("expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
		try {
			m.getColumn(-1);
			junit.framework.Assert.fail("expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
	}

	public void testGetEntry() {
		org.apache.commons.math.linear.RealMatrix m = createSparseMatrix(testData);
		junit.framework.Assert.assertEquals("get entry", m.getEntry(0, 1), 2.0, entryTolerance);
		try {
			m.getEntry(10, 4);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
	}

	public void testExamples() {
		double[][] matrixData = new double[][]{ new double[]{ 1.0 , 2.0 , 3.0 } , new double[]{ 2.0 , 5.0 , 3.0 } };
		org.apache.commons.math.linear.RealMatrix m = createSparseMatrix(matrixData);
		double[][] matrixData2 = new double[][]{ new double[]{ 1.0 , 2.0 } , new double[]{ 2.0 , 5.0 } , new double[]{ 1.0 , 7.0 } };
		org.apache.commons.math.linear.RealMatrix n = createSparseMatrix(matrixData2);
		org.apache.commons.math.linear.RealMatrix p = m.multiply(n);
		junit.framework.Assert.assertEquals(2, p.getRowDimension());
		junit.framework.Assert.assertEquals(2, p.getColumnDimension());
		org.apache.commons.math.linear.RealMatrix pInverse = new org.apache.commons.math.linear.LUDecompositionImpl(p).getSolver().getInverse();
		junit.framework.Assert.assertEquals(2, pInverse.getRowDimension());
		junit.framework.Assert.assertEquals(2, pInverse.getColumnDimension());
		double[][] coefficientsData = new double[][]{ new double[]{ 2 , 3 , -2 } , new double[]{ -1 , 7 , 6 } , new double[]{ 4 , -3 , -5 } };
		org.apache.commons.math.linear.RealMatrix coefficients = createSparseMatrix(coefficientsData);
		double[] constants = new double[]{ 1 , -2 , 1 };
		double[] solution = new org.apache.commons.math.linear.LUDecompositionImpl(coefficients).getSolver().solve(constants);
		junit.framework.Assert.assertEquals((((2 * (solution[0])) + (3 * (solution[1]))) - (2 * (solution[2]))), constants[0], 1.0E-12);
		junit.framework.Assert.assertEquals(((((-1) * (solution[0])) + (7 * (solution[1]))) + (6 * (solution[2]))), constants[1], 1.0E-12);
		junit.framework.Assert.assertEquals((((4 * (solution[0])) - (3 * (solution[1]))) - (5 * (solution[2]))), constants[2], 1.0E-12);
	}

	public void testSubMatrix() {
		org.apache.commons.math.linear.RealMatrix m = createSparseMatrix(subTestData);
		org.apache.commons.math.linear.RealMatrix mRows23Cols00 = createSparseMatrix(subRows23Cols00);
		org.apache.commons.math.linear.RealMatrix mRows00Cols33 = createSparseMatrix(subRows00Cols33);
		org.apache.commons.math.linear.RealMatrix mRows01Cols23 = createSparseMatrix(subRows01Cols23);
		org.apache.commons.math.linear.RealMatrix mRows02Cols13 = createSparseMatrix(subRows02Cols13);
		org.apache.commons.math.linear.RealMatrix mRows03Cols12 = createSparseMatrix(subRows03Cols12);
		org.apache.commons.math.linear.RealMatrix mRows03Cols123 = createSparseMatrix(subRows03Cols123);
		org.apache.commons.math.linear.RealMatrix mRows20Cols123 = createSparseMatrix(subRows20Cols123);
		org.apache.commons.math.linear.RealMatrix mRows31Cols31 = createSparseMatrix(subRows31Cols31);
		junit.framework.Assert.assertEquals("Rows23Cols00", mRows23Cols00, m.getSubMatrix(2, 3, 0, 0));
		junit.framework.Assert.assertEquals("Rows00Cols33", mRows00Cols33, m.getSubMatrix(0, 0, 3, 3));
		junit.framework.Assert.assertEquals("Rows01Cols23", mRows01Cols23, m.getSubMatrix(0, 1, 2, 3));
		junit.framework.Assert.assertEquals("Rows02Cols13", mRows02Cols13, m.getSubMatrix(new int[]{ 0 , 2 }, new int[]{ 1 , 3 }));
		junit.framework.Assert.assertEquals("Rows03Cols12", mRows03Cols12, m.getSubMatrix(new int[]{ 0 , 3 }, new int[]{ 1 , 2 }));
		junit.framework.Assert.assertEquals("Rows03Cols123", mRows03Cols123, m.getSubMatrix(new int[]{ 0 , 3 }, new int[]{ 1 , 2 , 3 }));
		junit.framework.Assert.assertEquals("Rows20Cols123", mRows20Cols123, m.getSubMatrix(new int[]{ 2 , 0 }, new int[]{ 1 , 2 , 3 }));
		junit.framework.Assert.assertEquals("Rows31Cols31", mRows31Cols31, m.getSubMatrix(new int[]{ 3 , 1 }, new int[]{ 3 , 1 }));
		junit.framework.Assert.assertEquals("Rows31Cols31", mRows31Cols31, m.getSubMatrix(new int[]{ 3 , 1 }, new int[]{ 3 , 1 }));
		try {
			m.getSubMatrix(1, 0, 2, 4);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
		try {
			m.getSubMatrix(-1, 1, 2, 2);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
		try {
			m.getSubMatrix(1, 0, 2, 2);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
		try {
			m.getSubMatrix(1, 0, 2, 4);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
		try {
			m.getSubMatrix(new int[]{  }, new int[]{ 0 });
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
		try {
			m.getSubMatrix(new int[]{ 0 }, new int[]{ 4 });
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
	}

	public void testGetRowMatrix() {
		org.apache.commons.math.linear.RealMatrix m = createSparseMatrix(subTestData);
		org.apache.commons.math.linear.RealMatrix mRow0 = createSparseMatrix(subRow0);
		org.apache.commons.math.linear.RealMatrix mRow3 = createSparseMatrix(subRow3);
		junit.framework.Assert.assertEquals("Row0", mRow0, m.getRowMatrix(0));
		junit.framework.Assert.assertEquals("Row3", mRow3, m.getRowMatrix(3));
		try {
			m.getRowMatrix(-1);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
		try {
			m.getRowMatrix(4);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
	}

	public void testGetColumnMatrix() {
		org.apache.commons.math.linear.RealMatrix m = createSparseMatrix(subTestData);
		org.apache.commons.math.linear.RealMatrix mColumn1 = createSparseMatrix(subColumn1);
		org.apache.commons.math.linear.RealMatrix mColumn3 = createSparseMatrix(subColumn3);
		junit.framework.Assert.assertEquals("Column1", mColumn1, m.getColumnMatrix(1));
		junit.framework.Assert.assertEquals("Column3", mColumn3, m.getColumnMatrix(3));
		try {
			m.getColumnMatrix(-1);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
		try {
			m.getColumnMatrix(4);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
	}

	public void testGetRowVector() {
		org.apache.commons.math.linear.RealMatrix m = createSparseMatrix(subTestData);
		org.apache.commons.math.linear.RealVector mRow0 = new org.apache.commons.math.linear.ArrayRealVector(subRow0[0]);
		org.apache.commons.math.linear.RealVector mRow3 = new org.apache.commons.math.linear.ArrayRealVector(subRow3[0]);
		junit.framework.Assert.assertEquals("Row0", mRow0, m.getRowVector(0));
		junit.framework.Assert.assertEquals("Row3", mRow3, m.getRowVector(3));
		try {
			m.getRowVector(-1);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
		try {
			m.getRowVector(4);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
	}

	public void testGetColumnVector() {
		org.apache.commons.math.linear.RealMatrix m = createSparseMatrix(subTestData);
		org.apache.commons.math.linear.RealVector mColumn1 = columnToVector(subColumn1);
		org.apache.commons.math.linear.RealVector mColumn3 = columnToVector(subColumn3);
		junit.framework.Assert.assertEquals("Column1", mColumn1, m.getColumnVector(1));
		junit.framework.Assert.assertEquals("Column3", mColumn3, m.getColumnVector(3));
		try {
			m.getColumnVector(-1);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
		try {
			m.getColumnVector(4);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
	}

	private org.apache.commons.math.linear.RealVector columnToVector(double[][] column) {
		double[] data = new double[column.length];
		for (int i = 0 ; i < (data.length) ; ++i) {
			data[i] = column[i][0];
		}
		return new org.apache.commons.math.linear.ArrayRealVector(data , false);
	}

	public void testEqualsAndHashCode() {
		org.apache.commons.math.linear.OpenMapRealMatrix m = createSparseMatrix(testData);
		org.apache.commons.math.linear.OpenMapRealMatrix m1 = m.copy();
		org.apache.commons.math.linear.OpenMapRealMatrix mt = ((org.apache.commons.math.linear.OpenMapRealMatrix)(m.transpose()));
		junit.framework.Assert.assertTrue(((m.hashCode()) != (mt.hashCode())));
		junit.framework.Assert.assertEquals(m.hashCode(), m1.hashCode());
		junit.framework.Assert.assertEquals(m, m);
		junit.framework.Assert.assertEquals(m, m1);
		junit.framework.Assert.assertFalse(m.equals(null));
		junit.framework.Assert.assertFalse(m.equals(mt));
		junit.framework.Assert.assertFalse(m.equals(createSparseMatrix(bigSingular)));
	}

	public void testToString() {
		org.apache.commons.math.linear.OpenMapRealMatrix m = createSparseMatrix(testData);
		junit.framework.Assert.assertEquals("OpenMapRealMatrix{{1.0,2.0,3.0},{2.0,5.0,3.0},{1.0,0.0,8.0}}", m.toString());
		m = new org.apache.commons.math.linear.OpenMapRealMatrix(1 , 1);
		junit.framework.Assert.assertEquals("OpenMapRealMatrix{{0.0}}", m.toString());
	}

	public void testSetSubMatrix() throws java.lang.Exception {
		org.apache.commons.math.linear.OpenMapRealMatrix m = createSparseMatrix(testData);
		m.setSubMatrix(detData2, 1, 1);
		org.apache.commons.math.linear.RealMatrix expected = createSparseMatrix(new double[][]{ new double[]{ 1.0 , 2.0 , 3.0 } , new double[]{ 2.0 , 1.0 , 3.0 } , new double[]{ 1.0 , 2.0 , 4.0 } });
		junit.framework.Assert.assertEquals(expected, m);
		m.setSubMatrix(detData2, 0, 0);
		expected = createSparseMatrix(new double[][]{ new double[]{ 1.0 , 3.0 , 3.0 } , new double[]{ 2.0 , 4.0 , 3.0 } , new double[]{ 1.0 , 2.0 , 4.0 } });
		junit.framework.Assert.assertEquals(expected, m);
		m.setSubMatrix(testDataPlus2, 0, 0);
		expected = createSparseMatrix(new double[][]{ new double[]{ 3.0 , 4.0 , 5.0 } , new double[]{ 4.0 , 7.0 , 5.0 } , new double[]{ 3.0 , 2.0 , 10.0 } });
		junit.framework.Assert.assertEquals(expected, m);
		org.apache.commons.math.linear.OpenMapRealMatrix matrix = createSparseMatrix(new double[][]{ new double[]{ 1 , 2 , 3 , 4 } , new double[]{ 5 , 6 , 7 , 8 } , new double[]{ 9 , 0 , 1 , 2 } });
		matrix.setSubMatrix(new double[][]{ new double[]{ 3 , 4 } , new double[]{ 5 , 6 } }, 1, 1);
		expected = createSparseMatrix(new double[][]{ new double[]{ 1 , 2 , 3 , 4 } , new double[]{ 5 , 3 , 4 , 8 } , new double[]{ 9 , 5 , 6 , 2 } });
		junit.framework.Assert.assertEquals(expected, matrix);
		try {
			m.setSubMatrix(testData, 1, 1);
			junit.framework.Assert.fail("expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException e) {
		}
		try {
			m.setSubMatrix(testData, -1, 1);
			junit.framework.Assert.fail("expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException e) {
		}
		try {
			m.setSubMatrix(testData, 1, -1);
			junit.framework.Assert.fail("expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException e) {
		}
		try {
			m.setSubMatrix(null, 1, 1);
			junit.framework.Assert.fail("expecting NullPointerException");
		} catch (java.lang.NullPointerException e) {
		}
		try {
			new org.apache.commons.math.linear.OpenMapRealMatrix(0 , 0);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
		}
		try {
			m.setSubMatrix(new double[][]{ new double[]{ 1 } , new double[]{ 2 , 3 } }, 0, 0);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
		}
		try {
			m.setSubMatrix(new double[][]{ new double[]{  } }, 0, 0);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
		}
	}

	public void testSerial() {
		org.apache.commons.math.linear.OpenMapRealMatrix m = createSparseMatrix(testData);
		junit.framework.Assert.assertEquals(m, org.apache.commons.math.TestUtils.serializeAndRecover(m));
	}

	protected void assertClose(java.lang.String msg, org.apache.commons.math.linear.RealMatrix m, org.apache.commons.math.linear.RealMatrix n, double tolerance) {
		junit.framework.Assert.assertTrue(msg, ((m.subtract(n).getNorm()) < tolerance));
	}

	protected void assertClose(java.lang.String msg, double[] m, double[] n, double tolerance) {
		if ((m.length) != (n.length)) {
			junit.framework.Assert.fail("vectors not same length");
		} 
		for (int i = 0 ; i < (m.length) ; i++) {
			junit.framework.Assert.assertEquals((((msg + " ") + i) + " elements differ"), m[i], n[i], tolerance);
		}
	}

	private org.apache.commons.math.linear.OpenMapRealMatrix createSparseMatrix(double[][] data) {
		org.apache.commons.math.linear.OpenMapRealMatrix matrix = new org.apache.commons.math.linear.OpenMapRealMatrix(data.length , data[0].length);
		for (int row = 0 ; row < (data.length) ; row++) {
			for (int col = 0 ; col < (data[row].length) ; col++) {
				matrix.setEntry(row, col, data[row][col]);
			}
		}
		return matrix;
	}
}

