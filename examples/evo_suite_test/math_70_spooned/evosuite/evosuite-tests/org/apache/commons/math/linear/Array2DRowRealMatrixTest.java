package org.apache.commons.math.linear;


public final class Array2DRowRealMatrixTest extends junit.framework.TestCase {
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

	public Array2DRowRealMatrixTest(java.lang.String name) {
		super(name);
	}

	public void testDimensions() {
		org.apache.commons.math.linear.Array2DRowRealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData);
		org.apache.commons.math.linear.Array2DRowRealMatrix m2 = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData2);
		junit.framework.Assert.assertEquals("testData row dimension", 3, m.getRowDimension());
		junit.framework.Assert.assertEquals("testData column dimension", 3, m.getColumnDimension());
		junit.framework.Assert.assertTrue("testData is square", m.isSquare());
		junit.framework.Assert.assertEquals("testData2 row dimension", m2.getRowDimension(), 2);
		junit.framework.Assert.assertEquals("testData2 column dimension", m2.getColumnDimension(), 3);
		junit.framework.Assert.assertTrue("testData2 is not square", !(m2.isSquare()));
	}

	public void testCopyFunctions() {
		org.apache.commons.math.linear.Array2DRowRealMatrix m1 = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData);
		org.apache.commons.math.linear.Array2DRowRealMatrix m2 = new org.apache.commons.math.linear.Array2DRowRealMatrix(m1.getData());
		junit.framework.Assert.assertEquals(m2, m1);
		org.apache.commons.math.linear.Array2DRowRealMatrix m3 = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData);
		org.apache.commons.math.linear.Array2DRowRealMatrix m4 = new org.apache.commons.math.linear.Array2DRowRealMatrix(m3.getData() , false);
		junit.framework.Assert.assertEquals(m4, m3);
	}

	public void testAdd() {
		org.apache.commons.math.linear.Array2DRowRealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData);
		org.apache.commons.math.linear.Array2DRowRealMatrix mInv = new org.apache.commons.math.linear.Array2DRowRealMatrix(testDataInv);
		org.apache.commons.math.linear.RealMatrix mPlusMInv = m.add(mInv);
		double[][] sumEntries = mPlusMInv.getData();
		for (int row = 0 ; row < (m.getRowDimension()) ; row++) {
			for (int col = 0 ; col < (m.getColumnDimension()) ; col++) {
				junit.framework.Assert.assertEquals("sum entry entry", testDataPlusInv[row][col], sumEntries[row][col], entryTolerance);
			}
		}
	}

	public void testAddFail() {
		org.apache.commons.math.linear.Array2DRowRealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData);
		org.apache.commons.math.linear.Array2DRowRealMatrix m2 = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData2);
		try {
			m.add(m2);
			junit.framework.Assert.fail("IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testNorm() {
		org.apache.commons.math.linear.Array2DRowRealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData);
		org.apache.commons.math.linear.Array2DRowRealMatrix m2 = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData2);
		junit.framework.Assert.assertEquals("testData norm", 14.0, m.getNorm(), entryTolerance);
		junit.framework.Assert.assertEquals("testData2 norm", 7.0, m2.getNorm(), entryTolerance);
	}

	public void testFrobeniusNorm() {
		org.apache.commons.math.linear.Array2DRowRealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData);
		org.apache.commons.math.linear.Array2DRowRealMatrix m2 = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData2);
		junit.framework.Assert.assertEquals("testData Frobenius norm", java.lang.Math.sqrt(117.0), m.getFrobeniusNorm(), entryTolerance);
		junit.framework.Assert.assertEquals("testData2 Frobenius norm", java.lang.Math.sqrt(52.0), m2.getFrobeniusNorm(), entryTolerance);
	}

	public void testPlusMinus() {
		org.apache.commons.math.linear.Array2DRowRealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData);
		org.apache.commons.math.linear.Array2DRowRealMatrix m2 = new org.apache.commons.math.linear.Array2DRowRealMatrix(testDataInv);
		org.apache.commons.math.TestUtils.assertEquals("m-n = m + -n", m.subtract(m2), m2.scalarMultiply(-1.0).add(m), entryTolerance);
		try {
			m.subtract(new org.apache.commons.math.linear.Array2DRowRealMatrix(testData2));
			junit.framework.Assert.fail("Expecting illegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testMultiply() {
		org.apache.commons.math.linear.Array2DRowRealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData);
		org.apache.commons.math.linear.Array2DRowRealMatrix mInv = new org.apache.commons.math.linear.Array2DRowRealMatrix(testDataInv);
		org.apache.commons.math.linear.Array2DRowRealMatrix identity = new org.apache.commons.math.linear.Array2DRowRealMatrix(id);
		org.apache.commons.math.linear.Array2DRowRealMatrix m2 = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData2);
		org.apache.commons.math.TestUtils.assertEquals("inverse multiply", m.multiply(mInv), identity, entryTolerance);
		org.apache.commons.math.TestUtils.assertEquals("inverse multiply", mInv.multiply(m), identity, entryTolerance);
		org.apache.commons.math.TestUtils.assertEquals("identity multiply", m.multiply(identity), m, entryTolerance);
		org.apache.commons.math.TestUtils.assertEquals("identity multiply", identity.multiply(mInv), mInv, entryTolerance);
		org.apache.commons.math.TestUtils.assertEquals("identity multiply", m2.multiply(identity), m2, entryTolerance);
		try {
			m.multiply(new org.apache.commons.math.linear.Array2DRowRealMatrix(bigSingular));
			junit.framework.Assert.fail("Expecting illegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	private double[][] d3 = new double[][]{ new double[]{ 1 , 2 , 3 , 4 } , new double[]{ 5 , 6 , 7 , 8 } };

	private double[][] d4 = new double[][]{ new double[]{ 1 } , new double[]{ 2 } , new double[]{ 3 } , new double[]{ 4 } };

	private double[][] d5 = new double[][]{ new double[]{ 30 } , new double[]{ 70 } };

	public void testMultiply2() {
		org.apache.commons.math.linear.RealMatrix m3 = new org.apache.commons.math.linear.Array2DRowRealMatrix(d3);
		org.apache.commons.math.linear.RealMatrix m4 = new org.apache.commons.math.linear.Array2DRowRealMatrix(d4);
		org.apache.commons.math.linear.RealMatrix m5 = new org.apache.commons.math.linear.Array2DRowRealMatrix(d5);
		org.apache.commons.math.TestUtils.assertEquals("m3*m4=m5", m3.multiply(m4), m5, entryTolerance);
	}

	public void testTrace() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(id);
		junit.framework.Assert.assertEquals("identity trace", 3.0, m.getTrace(), entryTolerance);
		m = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData2);
		try {
			m.getTrace();
			junit.framework.Assert.fail("Expecting NonSquareMatrixException");
		} catch (org.apache.commons.math.linear.NonSquareMatrixException ex) {
		}
	}

	public void testScalarAdd() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData);
		org.apache.commons.math.TestUtils.assertEquals("scalar add", new org.apache.commons.math.linear.Array2DRowRealMatrix(testDataPlus2), m.scalarAdd(2.0), entryTolerance);
	}

	public void testOperate() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(id);
		org.apache.commons.math.TestUtils.assertEquals("identity operate", testVector, m.operate(testVector), entryTolerance);
		org.apache.commons.math.TestUtils.assertEquals("identity operate", testVector, m.operate(new org.apache.commons.math.linear.ArrayRealVector(testVector)).getData(), entryTolerance);
		m = new org.apache.commons.math.linear.Array2DRowRealMatrix(bigSingular);
		try {
			m.operate(testVector);
			junit.framework.Assert.fail("Expecting illegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testMath209() {
		org.apache.commons.math.linear.RealMatrix a = new org.apache.commons.math.linear.Array2DRowRealMatrix(new double[][]{ new double[]{ 1 , 2 } , new double[]{ 3 , 4 } , new double[]{ 5 , 6 } } , false);
		double[] b = a.operate(new double[]{ 1 , 1 });
		junit.framework.Assert.assertEquals(a.getRowDimension(), b.length);
		junit.framework.Assert.assertEquals(3.0, b[0], 1.0E-12);
		junit.framework.Assert.assertEquals(7.0, b[1], 1.0E-12);
		junit.framework.Assert.assertEquals(11.0, b[2], 1.0E-12);
	}

	public void testTranspose() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData);
		org.apache.commons.math.linear.RealMatrix mIT = new org.apache.commons.math.linear.LUDecompositionImpl(m).getSolver().getInverse().transpose();
		org.apache.commons.math.linear.RealMatrix mTI = new org.apache.commons.math.linear.LUDecompositionImpl(m.transpose()).getSolver().getInverse();
		org.apache.commons.math.TestUtils.assertEquals("inverse-transpose", mIT, mTI, normTolerance);
		m = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData2);
		org.apache.commons.math.linear.RealMatrix mt = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData2T);
		org.apache.commons.math.TestUtils.assertEquals("transpose", mt, m.transpose(), normTolerance);
	}

	public void testPremultiplyVector() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData);
		org.apache.commons.math.TestUtils.assertEquals("premultiply", m.preMultiply(testVector), preMultTest, normTolerance);
		org.apache.commons.math.TestUtils.assertEquals("premultiply", m.preMultiply(new org.apache.commons.math.linear.ArrayRealVector(testVector).getData()), preMultTest, normTolerance);
		m = new org.apache.commons.math.linear.Array2DRowRealMatrix(bigSingular);
		try {
			m.preMultiply(testVector);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testPremultiply() {
		org.apache.commons.math.linear.RealMatrix m3 = new org.apache.commons.math.linear.Array2DRowRealMatrix(d3);
		org.apache.commons.math.linear.RealMatrix m4 = new org.apache.commons.math.linear.Array2DRowRealMatrix(d4);
		org.apache.commons.math.linear.RealMatrix m5 = new org.apache.commons.math.linear.Array2DRowRealMatrix(d5);
		org.apache.commons.math.TestUtils.assertEquals("m3*m4=m5", m4.preMultiply(m3), m5, entryTolerance);
		org.apache.commons.math.linear.Array2DRowRealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData);
		org.apache.commons.math.linear.Array2DRowRealMatrix mInv = new org.apache.commons.math.linear.Array2DRowRealMatrix(testDataInv);
		org.apache.commons.math.linear.Array2DRowRealMatrix identity = new org.apache.commons.math.linear.Array2DRowRealMatrix(id);
		org.apache.commons.math.TestUtils.assertEquals("inverse multiply", m.preMultiply(mInv), identity, entryTolerance);
		org.apache.commons.math.TestUtils.assertEquals("inverse multiply", mInv.preMultiply(m), identity, entryTolerance);
		org.apache.commons.math.TestUtils.assertEquals("identity multiply", m.preMultiply(identity), m, entryTolerance);
		org.apache.commons.math.TestUtils.assertEquals("identity multiply", identity.preMultiply(mInv), mInv, entryTolerance);
		try {
			m.preMultiply(new org.apache.commons.math.linear.Array2DRowRealMatrix(bigSingular));
			junit.framework.Assert.fail("Expecting illegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testGetVectors() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData);
		org.apache.commons.math.TestUtils.assertEquals("get row", m.getRow(0), testDataRow1, entryTolerance);
		org.apache.commons.math.TestUtils.assertEquals("get col", m.getColumn(2), testDataCol3, entryTolerance);
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
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData);
		junit.framework.Assert.assertEquals("get entry", m.getEntry(0, 1), 2.0, entryTolerance);
		try {
			m.getEntry(10, 4);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
	}

	public void testExamples() {
		double[][] matrixData = new double[][]{ new double[]{ 1.0 , 2.0 , 3.0 } , new double[]{ 2.0 , 5.0 , 3.0 } };
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(matrixData);
		double[][] matrixData2 = new double[][]{ new double[]{ 1.0 , 2.0 } , new double[]{ 2.0 , 5.0 } , new double[]{ 1.0 , 7.0 } };
		org.apache.commons.math.linear.RealMatrix n = new org.apache.commons.math.linear.Array2DRowRealMatrix(matrixData2);
		org.apache.commons.math.linear.RealMatrix p = m.multiply(n);
		junit.framework.Assert.assertEquals(2, p.getRowDimension());
		junit.framework.Assert.assertEquals(2, p.getColumnDimension());
		org.apache.commons.math.linear.RealMatrix pInverse = new org.apache.commons.math.linear.LUDecompositionImpl(p).getSolver().getInverse();
		junit.framework.Assert.assertEquals(2, pInverse.getRowDimension());
		junit.framework.Assert.assertEquals(2, pInverse.getColumnDimension());
		double[][] coefficientsData = new double[][]{ new double[]{ 2 , 3 , -2 } , new double[]{ -1 , 7 , 6 } , new double[]{ 4 , -3 , -5 } };
		org.apache.commons.math.linear.RealMatrix coefficients = new org.apache.commons.math.linear.Array2DRowRealMatrix(coefficientsData);
		double[] constants = new double[]{ 1 , -2 , 1 };
		double[] solution = new org.apache.commons.math.linear.LUDecompositionImpl(coefficients).getSolver().solve(constants);
		junit.framework.Assert.assertEquals((((2 * (solution[0])) + (3 * (solution[1]))) - (2 * (solution[2]))), constants[0], 1.0E-12);
		junit.framework.Assert.assertEquals(((((-1) * (solution[0])) + (7 * (solution[1]))) + (6 * (solution[2]))), constants[1], 1.0E-12);
		junit.framework.Assert.assertEquals((((4 * (solution[0])) - (3 * (solution[1]))) - (5 * (solution[2]))), constants[2], 1.0E-12);
	}

	public void testGetSubMatrix() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(subTestData);
		checkGetSubMatrix(m, subRows23Cols00, 2, 3, 0, 0, false);
		checkGetSubMatrix(m, subRows00Cols33, 0, 0, 3, 3, false);
		checkGetSubMatrix(m, subRows01Cols23, 0, 1, 2, 3, false);
		checkGetSubMatrix(m, subRows02Cols13, new int[]{ 0 , 2 }, new int[]{ 1 , 3 }, false);
		checkGetSubMatrix(m, subRows03Cols12, new int[]{ 0 , 3 }, new int[]{ 1 , 2 }, false);
		checkGetSubMatrix(m, subRows03Cols123, new int[]{ 0 , 3 }, new int[]{ 1 , 2 , 3 }, false);
		checkGetSubMatrix(m, subRows20Cols123, new int[]{ 2 , 0 }, new int[]{ 1 , 2 , 3 }, false);
		checkGetSubMatrix(m, subRows31Cols31, new int[]{ 3 , 1 }, new int[]{ 3 , 1 }, false);
		checkGetSubMatrix(m, subRows31Cols31, new int[]{ 3 , 1 }, new int[]{ 3 , 1 }, false);
		checkGetSubMatrix(m, null, 1, 0, 2, 4, true);
		checkGetSubMatrix(m, null, -1, 1, 2, 2, true);
		checkGetSubMatrix(m, null, 1, 0, 2, 2, true);
		checkGetSubMatrix(m, null, 1, 0, 2, 4, true);
		checkGetSubMatrix(m, null, new int[]{  }, new int[]{ 0 }, true);
		checkGetSubMatrix(m, null, new int[]{ 0 }, new int[]{ 4 }, true);
	}

	private void checkGetSubMatrix(org.apache.commons.math.linear.RealMatrix m, double[][] reference, int startRow, int endRow, int startColumn, int endColumn, boolean mustFail) {
		try {
			org.apache.commons.math.linear.RealMatrix sub = m.getSubMatrix(startRow, endRow, startColumn, endColumn);
			junit.framework.Assert.assertEquals(new org.apache.commons.math.linear.Array2DRowRealMatrix(reference), sub);
			if (mustFail) {
				junit.framework.Assert.fail("Expecting MatrixIndexException");
			} 
		} catch (org.apache.commons.math.linear.MatrixIndexException e) {
			if (!mustFail) {
				throw e;
			} 
		}
	}

	private void checkGetSubMatrix(org.apache.commons.math.linear.RealMatrix m, double[][] reference, int[] selectedRows, int[] selectedColumns, boolean mustFail) {
		try {
			org.apache.commons.math.linear.RealMatrix sub = m.getSubMatrix(selectedRows, selectedColumns);
			junit.framework.Assert.assertEquals(new org.apache.commons.math.linear.Array2DRowRealMatrix(reference), sub);
			if (mustFail) {
				junit.framework.Assert.fail("Expecting MatrixIndexException");
			} 
		} catch (org.apache.commons.math.linear.MatrixIndexException e) {
			if (!mustFail) {
				throw e;
			} 
		}
	}

	public void testCopySubMatrix() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(subTestData);
		checkCopy(m, subRows23Cols00, 2, 3, 0, 0, false);
		checkCopy(m, subRows00Cols33, 0, 0, 3, 3, false);
		checkCopy(m, subRows01Cols23, 0, 1, 2, 3, false);
		checkCopy(m, subRows02Cols13, new int[]{ 0 , 2 }, new int[]{ 1 , 3 }, false);
		checkCopy(m, subRows03Cols12, new int[]{ 0 , 3 }, new int[]{ 1 , 2 }, false);
		checkCopy(m, subRows03Cols123, new int[]{ 0 , 3 }, new int[]{ 1 , 2 , 3 }, false);
		checkCopy(m, subRows20Cols123, new int[]{ 2 , 0 }, new int[]{ 1 , 2 , 3 }, false);
		checkCopy(m, subRows31Cols31, new int[]{ 3 , 1 }, new int[]{ 3 , 1 }, false);
		checkCopy(m, subRows31Cols31, new int[]{ 3 , 1 }, new int[]{ 3 , 1 }, false);
		checkCopy(m, null, 1, 0, 2, 4, true);
		checkCopy(m, null, -1, 1, 2, 2, true);
		checkCopy(m, null, 1, 0, 2, 2, true);
		checkCopy(m, null, 1, 0, 2, 4, true);
		checkCopy(m, null, new int[]{  }, new int[]{ 0 }, true);
		checkCopy(m, null, new int[]{ 0 }, new int[]{ 4 }, true);
	}

	private void checkCopy(org.apache.commons.math.linear.RealMatrix m, double[][] reference, int startRow, int endRow, int startColumn, int endColumn, boolean mustFail) {
		try {
			double[][] sub = reference == null ? new double[1][1] : new double[reference.length][reference[0].length];
			m.copySubMatrix(startRow, endRow, startColumn, endColumn, sub);
			junit.framework.Assert.assertEquals(new org.apache.commons.math.linear.Array2DRowRealMatrix(reference), new org.apache.commons.math.linear.Array2DRowRealMatrix(sub));
			if (mustFail) {
				junit.framework.Assert.fail("Expecting MatrixIndexException");
			} 
		} catch (org.apache.commons.math.linear.MatrixIndexException e) {
			if (!mustFail) {
				throw e;
			} 
		}
	}

	private void checkCopy(org.apache.commons.math.linear.RealMatrix m, double[][] reference, int[] selectedRows, int[] selectedColumns, boolean mustFail) {
		try {
			double[][] sub = reference == null ? new double[1][1] : new double[reference.length][reference[0].length];
			m.copySubMatrix(selectedRows, selectedColumns, sub);
			junit.framework.Assert.assertEquals(new org.apache.commons.math.linear.Array2DRowRealMatrix(reference), new org.apache.commons.math.linear.Array2DRowRealMatrix(sub));
			if (mustFail) {
				junit.framework.Assert.fail("Expecting MatrixIndexException");
			} 
		} catch (org.apache.commons.math.linear.MatrixIndexException e) {
			if (!mustFail) {
				throw e;
			} 
		}
	}

	public void testGetRowMatrix() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(subTestData);
		org.apache.commons.math.linear.RealMatrix mRow0 = new org.apache.commons.math.linear.Array2DRowRealMatrix(subRow0);
		org.apache.commons.math.linear.RealMatrix mRow3 = new org.apache.commons.math.linear.Array2DRowRealMatrix(subRow3);
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

	public void testSetRowMatrix() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(subTestData);
		org.apache.commons.math.linear.RealMatrix mRow3 = new org.apache.commons.math.linear.Array2DRowRealMatrix(subRow3);
		junit.framework.Assert.assertNotSame(mRow3, m.getRowMatrix(0));
		m.setRowMatrix(0, mRow3);
		junit.framework.Assert.assertEquals(mRow3, m.getRowMatrix(0));
		try {
			m.setRowMatrix(-1, mRow3);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
		try {
			m.setRowMatrix(0, m);
			junit.framework.Assert.fail("Expecting InvalidMatrixException");
		} catch (org.apache.commons.math.linear.InvalidMatrixException ex) {
		}
	}

	public void testGetColumnMatrix() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(subTestData);
		org.apache.commons.math.linear.RealMatrix mColumn1 = new org.apache.commons.math.linear.Array2DRowRealMatrix(subColumn1);
		org.apache.commons.math.linear.RealMatrix mColumn3 = new org.apache.commons.math.linear.Array2DRowRealMatrix(subColumn3);
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

	public void testSetColumnMatrix() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(subTestData);
		org.apache.commons.math.linear.RealMatrix mColumn3 = new org.apache.commons.math.linear.Array2DRowRealMatrix(subColumn3);
		junit.framework.Assert.assertNotSame(mColumn3, m.getColumnMatrix(1));
		m.setColumnMatrix(1, mColumn3);
		junit.framework.Assert.assertEquals(mColumn3, m.getColumnMatrix(1));
		try {
			m.setColumnMatrix(-1, mColumn3);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
		try {
			m.setColumnMatrix(0, m);
			junit.framework.Assert.fail("Expecting InvalidMatrixException");
		} catch (org.apache.commons.math.linear.InvalidMatrixException ex) {
		}
	}

	public void testGetRowVector() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(subTestData);
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

	public void testSetRowVector() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(subTestData);
		org.apache.commons.math.linear.RealVector mRow3 = new org.apache.commons.math.linear.ArrayRealVector(subRow3[0]);
		junit.framework.Assert.assertNotSame(mRow3, m.getRowMatrix(0));
		m.setRowVector(0, mRow3);
		junit.framework.Assert.assertEquals(mRow3, m.getRowVector(0));
		try {
			m.setRowVector(-1, mRow3);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
		try {
			m.setRowVector(0, new org.apache.commons.math.linear.ArrayRealVector(5));
			junit.framework.Assert.fail("Expecting InvalidMatrixException");
		} catch (org.apache.commons.math.linear.InvalidMatrixException ex) {
		}
	}

	public void testGetColumnVector() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(subTestData);
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

	public void testSetColumnVector() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(subTestData);
		org.apache.commons.math.linear.RealVector mColumn3 = columnToVector(subColumn3);
		junit.framework.Assert.assertNotSame(mColumn3, m.getColumnVector(1));
		m.setColumnVector(1, mColumn3);
		junit.framework.Assert.assertEquals(mColumn3, m.getColumnVector(1));
		try {
			m.setColumnVector(-1, mColumn3);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
		try {
			m.setColumnVector(0, new org.apache.commons.math.linear.ArrayRealVector(5));
			junit.framework.Assert.fail("Expecting InvalidMatrixException");
		} catch (org.apache.commons.math.linear.InvalidMatrixException ex) {
		}
	}

	private org.apache.commons.math.linear.RealVector columnToVector(double[][] column) {
		double[] data = new double[column.length];
		for (int i = 0 ; i < (data.length) ; ++i) {
			data[i] = column[i][0];
		}
		return new org.apache.commons.math.linear.ArrayRealVector(data , false);
	}

	public void testGetRow() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(subTestData);
		checkArrays(subRow0[0], m.getRow(0));
		checkArrays(subRow3[0], m.getRow(3));
		try {
			m.getRow(-1);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
		try {
			m.getRow(4);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
	}

	public void testSetRow() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(subTestData);
		junit.framework.Assert.assertTrue(((subRow3[0][0]) != (m.getRow(0)[0])));
		m.setRow(0, subRow3[0]);
		checkArrays(subRow3[0], m.getRow(0));
		try {
			m.setRow(-1, subRow3[0]);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
		try {
			m.setRow(0, new double[5]);
			junit.framework.Assert.fail("Expecting InvalidMatrixException");
		} catch (org.apache.commons.math.linear.InvalidMatrixException ex) {
		}
	}

	public void testGetColumn() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(subTestData);
		double[] mColumn1 = columnToArray(subColumn1);
		double[] mColumn3 = columnToArray(subColumn3);
		checkArrays(mColumn1, m.getColumn(1));
		checkArrays(mColumn3, m.getColumn(3));
		try {
			m.getColumn(-1);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
		try {
			m.getColumn(4);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
	}

	public void testSetColumn() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(subTestData);
		double[] mColumn3 = columnToArray(subColumn3);
		junit.framework.Assert.assertTrue(((mColumn3[0]) != (m.getColumn(1)[0])));
		m.setColumn(1, mColumn3);
		checkArrays(mColumn3, m.getColumn(1));
		try {
			m.setColumn(-1, mColumn3);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
		try {
			m.setColumn(0, new double[5]);
			junit.framework.Assert.fail("Expecting InvalidMatrixException");
		} catch (org.apache.commons.math.linear.InvalidMatrixException ex) {
		}
	}

	private double[] columnToArray(double[][] column) {
		double[] data = new double[column.length];
		for (int i = 0 ; i < (data.length) ; ++i) {
			data[i] = column[i][0];
		}
		return data;
	}

	private void checkArrays(double[] expected, double[] actual) {
		junit.framework.Assert.assertEquals(expected.length, actual.length);
		for (int i = 0 ; i < (expected.length) ; ++i) {
			junit.framework.Assert.assertEquals(expected[i], actual[i]);
		}
	}

	public void testEqualsAndHashCode() {
		org.apache.commons.math.linear.Array2DRowRealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData);
		org.apache.commons.math.linear.Array2DRowRealMatrix m1 = ((org.apache.commons.math.linear.Array2DRowRealMatrix)(m.copy()));
		org.apache.commons.math.linear.Array2DRowRealMatrix mt = ((org.apache.commons.math.linear.Array2DRowRealMatrix)(m.transpose()));
		junit.framework.Assert.assertTrue(((m.hashCode()) != (mt.hashCode())));
		junit.framework.Assert.assertEquals(m.hashCode(), m1.hashCode());
		junit.framework.Assert.assertEquals(m, m);
		junit.framework.Assert.assertEquals(m, m1);
		junit.framework.Assert.assertFalse(m.equals(null));
		junit.framework.Assert.assertFalse(m.equals(mt));
		junit.framework.Assert.assertFalse(m.equals(new org.apache.commons.math.linear.Array2DRowRealMatrix(bigSingular)));
	}

	public void testToString() {
		org.apache.commons.math.linear.Array2DRowRealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData);
		junit.framework.Assert.assertEquals("Array2DRowRealMatrix{{1.0,2.0,3.0},{2.0,5.0,3.0},{1.0,0.0,8.0}}", m.toString());
		m = new org.apache.commons.math.linear.Array2DRowRealMatrix();
		junit.framework.Assert.assertEquals("Array2DRowRealMatrix{}", m.toString());
	}

	public void testSetSubMatrix() throws java.lang.Exception {
		org.apache.commons.math.linear.Array2DRowRealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData);
		m.setSubMatrix(detData2, 1, 1);
		org.apache.commons.math.linear.RealMatrix expected = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 1.0 , 2.0 , 3.0 } , new double[]{ 2.0 , 1.0 , 3.0 } , new double[]{ 1.0 , 2.0 , 4.0 } });
		junit.framework.Assert.assertEquals(expected, m);
		m.setSubMatrix(detData2, 0, 0);
		expected = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 1.0 , 3.0 , 3.0 } , new double[]{ 2.0 , 4.0 , 3.0 } , new double[]{ 1.0 , 2.0 , 4.0 } });
		junit.framework.Assert.assertEquals(expected, m);
		m.setSubMatrix(testDataPlus2, 0, 0);
		expected = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 3.0 , 4.0 , 5.0 } , new double[]{ 4.0 , 7.0 , 5.0 } , new double[]{ 3.0 , 2.0 , 10.0 } });
		junit.framework.Assert.assertEquals(expected, m);
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
		org.apache.commons.math.linear.Array2DRowRealMatrix m2 = new org.apache.commons.math.linear.Array2DRowRealMatrix();
		try {
			m2.setSubMatrix(testData, 0, 1);
			junit.framework.Assert.fail("expecting IllegalStateException");
		} catch (java.lang.IllegalStateException e) {
		}
		try {
			m2.setSubMatrix(testData, 1, 0);
			junit.framework.Assert.fail("expecting IllegalStateException");
		} catch (java.lang.IllegalStateException e) {
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

	public void testWalk() {
		int rows = 150;
		int columns = 75;
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(rows , columns);
		m.walkInRowOrder(new org.apache.commons.math.linear.Array2DRowRealMatrixTest.SetVisitor());
		org.apache.commons.math.linear.Array2DRowRealMatrixTest.GetVisitor getVisitor = new org.apache.commons.math.linear.Array2DRowRealMatrixTest.GetVisitor();
		m.walkInOptimizedOrder(getVisitor);
		junit.framework.Assert.assertEquals((rows * columns), getVisitor.getCount());
		m = new org.apache.commons.math.linear.Array2DRowRealMatrix(rows , columns);
		m.walkInRowOrder(new org.apache.commons.math.linear.Array2DRowRealMatrixTest.SetVisitor(), 1, (rows - 2), 1, (columns - 2));
		getVisitor = new org.apache.commons.math.linear.Array2DRowRealMatrixTest.GetVisitor();
		m.walkInOptimizedOrder(getVisitor, 1, (rows - 2), 1, (columns - 2));
		junit.framework.Assert.assertEquals(((rows - 2) * (columns - 2)), getVisitor.getCount());
		for (int i = 0 ; i < rows ; ++i) {
			junit.framework.Assert.assertEquals(0.0, m.getEntry(i, 0), 0);
			junit.framework.Assert.assertEquals(0.0, m.getEntry(i, (columns - 1)), 0);
		}
		for (int j = 0 ; j < columns ; ++j) {
			junit.framework.Assert.assertEquals(0.0, m.getEntry(0, j), 0);
			junit.framework.Assert.assertEquals(0.0, m.getEntry((rows - 1), j), 0);
		}
		m = new org.apache.commons.math.linear.Array2DRowRealMatrix(rows , columns);
		m.walkInColumnOrder(new org.apache.commons.math.linear.Array2DRowRealMatrixTest.SetVisitor());
		getVisitor = new org.apache.commons.math.linear.Array2DRowRealMatrixTest.GetVisitor();
		m.walkInOptimizedOrder(getVisitor);
		junit.framework.Assert.assertEquals((rows * columns), getVisitor.getCount());
		m = new org.apache.commons.math.linear.Array2DRowRealMatrix(rows , columns);
		m.walkInColumnOrder(new org.apache.commons.math.linear.Array2DRowRealMatrixTest.SetVisitor(), 1, (rows - 2), 1, (columns - 2));
		getVisitor = new org.apache.commons.math.linear.Array2DRowRealMatrixTest.GetVisitor();
		m.walkInOptimizedOrder(getVisitor, 1, (rows - 2), 1, (columns - 2));
		junit.framework.Assert.assertEquals(((rows - 2) * (columns - 2)), getVisitor.getCount());
		for (int i = 0 ; i < rows ; ++i) {
			junit.framework.Assert.assertEquals(0.0, m.getEntry(i, 0), 0);
			junit.framework.Assert.assertEquals(0.0, m.getEntry(i, (columns - 1)), 0);
		}
		for (int j = 0 ; j < columns ; ++j) {
			junit.framework.Assert.assertEquals(0.0, m.getEntry(0, j), 0);
			junit.framework.Assert.assertEquals(0.0, m.getEntry((rows - 1), j), 0);
		}
		m = new org.apache.commons.math.linear.Array2DRowRealMatrix(rows , columns);
		m.walkInOptimizedOrder(new org.apache.commons.math.linear.Array2DRowRealMatrixTest.SetVisitor());
		getVisitor = new org.apache.commons.math.linear.Array2DRowRealMatrixTest.GetVisitor();
		m.walkInRowOrder(getVisitor);
		junit.framework.Assert.assertEquals((rows * columns), getVisitor.getCount());
		m = new org.apache.commons.math.linear.Array2DRowRealMatrix(rows , columns);
		m.walkInOptimizedOrder(new org.apache.commons.math.linear.Array2DRowRealMatrixTest.SetVisitor(), 1, (rows - 2), 1, (columns - 2));
		getVisitor = new org.apache.commons.math.linear.Array2DRowRealMatrixTest.GetVisitor();
		m.walkInRowOrder(getVisitor, 1, (rows - 2), 1, (columns - 2));
		junit.framework.Assert.assertEquals(((rows - 2) * (columns - 2)), getVisitor.getCount());
		for (int i = 0 ; i < rows ; ++i) {
			junit.framework.Assert.assertEquals(0.0, m.getEntry(i, 0), 0);
			junit.framework.Assert.assertEquals(0.0, m.getEntry(i, (columns - 1)), 0);
		}
		for (int j = 0 ; j < columns ; ++j) {
			junit.framework.Assert.assertEquals(0.0, m.getEntry(0, j), 0);
			junit.framework.Assert.assertEquals(0.0, m.getEntry((rows - 1), j), 0);
		}
		m = new org.apache.commons.math.linear.Array2DRowRealMatrix(rows , columns);
		m.walkInOptimizedOrder(new org.apache.commons.math.linear.Array2DRowRealMatrixTest.SetVisitor());
		getVisitor = new org.apache.commons.math.linear.Array2DRowRealMatrixTest.GetVisitor();
		m.walkInColumnOrder(getVisitor);
		junit.framework.Assert.assertEquals((rows * columns), getVisitor.getCount());
		m = new org.apache.commons.math.linear.Array2DRowRealMatrix(rows , columns);
		m.walkInOptimizedOrder(new org.apache.commons.math.linear.Array2DRowRealMatrixTest.SetVisitor(), 1, (rows - 2), 1, (columns - 2));
		getVisitor = new org.apache.commons.math.linear.Array2DRowRealMatrixTest.GetVisitor();
		m.walkInColumnOrder(getVisitor, 1, (rows - 2), 1, (columns - 2));
		junit.framework.Assert.assertEquals(((rows - 2) * (columns - 2)), getVisitor.getCount());
		for (int i = 0 ; i < rows ; ++i) {
			junit.framework.Assert.assertEquals(0.0, m.getEntry(i, 0), 0);
			junit.framework.Assert.assertEquals(0.0, m.getEntry(i, (columns - 1)), 0);
		}
		for (int j = 0 ; j < columns ; ++j) {
			junit.framework.Assert.assertEquals(0.0, m.getEntry(0, j), 0);
			junit.framework.Assert.assertEquals(0.0, m.getEntry((rows - 1), j), 0);
		}
	}

	public void testSerial() {
		org.apache.commons.math.linear.Array2DRowRealMatrix m = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData);
		junit.framework.Assert.assertEquals(m, org.apache.commons.math.TestUtils.serializeAndRecover(m));
	}

	private static class SetVisitor extends org.apache.commons.math.linear.DefaultRealMatrixChangingVisitor {
		@java.lang.Override
		public double visit(int i, int j, double value) {
			return i + (j / 1024.0);
		}
	}

	private static class GetVisitor extends org.apache.commons.math.linear.DefaultRealMatrixPreservingVisitor {
		private int count = 0;

		@java.lang.Override
		public void visit(int i, int j, double value) {
			++(count);
			junit.framework.Assert.assertEquals((i + (j / 1024.0)), value, 0.0);
		}

		public int getCount() {
			return count;
		}
	}

	protected void splitLU(org.apache.commons.math.linear.RealMatrix lu, double[][] lowerData, double[][] upperData) throws org.apache.commons.math.linear.InvalidMatrixException {
		if (((((!(lu.isSquare())) || ((lowerData.length) != (lowerData[0].length))) || ((upperData.length) != (upperData[0].length))) || ((lowerData.length) != (upperData.length))) || ((lowerData.length) != (lu.getRowDimension()))) {
			throw new org.apache.commons.math.linear.InvalidMatrixException("incorrect dimensions");
		} 
		int n = lu.getRowDimension();
		for (int i = 0 ; i < n ; i++) {
			for (int j = 0 ; j < n ; j++) {
				if (j < i) {
					lowerData[i][j] = lu.getEntry(i, j);
					upperData[i][j] = 0.0;
				} else {
					if (i == j) {
						lowerData[i][j] = 1.0;
						upperData[i][j] = lu.getEntry(i, j);
					} else {
						lowerData[i][j] = 0.0;
						upperData[i][j] = lu.getEntry(i, j);
					}
				}
			}
		}
	}

	protected org.apache.commons.math.linear.RealMatrix permuteRows(org.apache.commons.math.linear.RealMatrix matrix, int[] permutation) {
		if ((!(matrix.isSquare())) || ((matrix.getRowDimension()) != (permutation.length))) {
			throw new java.lang.IllegalArgumentException("dimension mismatch");
		} 
		int n = matrix.getRowDimension();
		int m = matrix.getColumnDimension();
		double[][] out = new double[m][n];
		for (int i = 0 ; i < n ; i++) {
			for (int j = 0 ; j < m ; j++) {
				out[i][j] = matrix.getEntry(permutation[i], j);
			}
		}
		return new org.apache.commons.math.linear.Array2DRowRealMatrix(out);
	}
}

