package org.apache.commons.math.linear;


public final class BlockRealMatrixTest extends junit.framework.TestCase {
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

	public BlockRealMatrixTest(java.lang.String name) {
		super(name);
	}

	public void testDimensions() {
		org.apache.commons.math.linear.BlockRealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(testData);
		org.apache.commons.math.linear.BlockRealMatrix m2 = new org.apache.commons.math.linear.BlockRealMatrix(testData2);
		junit.framework.Assert.assertEquals("testData row dimension", 3, m.getRowDimension());
		junit.framework.Assert.assertEquals("testData column dimension", 3, m.getColumnDimension());
		junit.framework.Assert.assertTrue("testData is square", m.isSquare());
		junit.framework.Assert.assertEquals("testData2 row dimension", m2.getRowDimension(), 2);
		junit.framework.Assert.assertEquals("testData2 column dimension", m2.getColumnDimension(), 3);
		junit.framework.Assert.assertTrue("testData2 is not square", !(m2.isSquare()));
	}

	public void testCopyFunctions() {
		java.util.Random r = new java.util.Random(66636328996002L);
		org.apache.commons.math.linear.BlockRealMatrix m1 = createRandomMatrix(r, 47, 83);
		org.apache.commons.math.linear.BlockRealMatrix m2 = new org.apache.commons.math.linear.BlockRealMatrix(m1.getData());
		junit.framework.Assert.assertEquals(m1, m2);
		org.apache.commons.math.linear.BlockRealMatrix m3 = new org.apache.commons.math.linear.BlockRealMatrix(testData);
		org.apache.commons.math.linear.BlockRealMatrix m4 = new org.apache.commons.math.linear.BlockRealMatrix(m3.getData());
		junit.framework.Assert.assertEquals(m3, m4);
	}

	public void testAdd() {
		org.apache.commons.math.linear.BlockRealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(testData);
		org.apache.commons.math.linear.BlockRealMatrix mInv = new org.apache.commons.math.linear.BlockRealMatrix(testDataInv);
		org.apache.commons.math.linear.RealMatrix mPlusMInv = m.add(mInv);
		double[][] sumEntries = mPlusMInv.getData();
		for (int row = 0 ; row < (m.getRowDimension()) ; row++) {
			for (int col = 0 ; col < (m.getColumnDimension()) ; col++) {
				junit.framework.Assert.assertEquals("sum entry entry", testDataPlusInv[row][col], sumEntries[row][col], entryTolerance);
			}
		}
	}

	public void testAddFail() {
		org.apache.commons.math.linear.BlockRealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(testData);
		org.apache.commons.math.linear.BlockRealMatrix m2 = new org.apache.commons.math.linear.BlockRealMatrix(testData2);
		try {
			m.add(m2);
			junit.framework.Assert.fail("IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testNorm() {
		org.apache.commons.math.linear.BlockRealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(testData);
		org.apache.commons.math.linear.BlockRealMatrix m2 = new org.apache.commons.math.linear.BlockRealMatrix(testData2);
		junit.framework.Assert.assertEquals("testData norm", 14.0, m.getNorm(), entryTolerance);
		junit.framework.Assert.assertEquals("testData2 norm", 7.0, m2.getNorm(), entryTolerance);
	}

	public void testFrobeniusNorm() {
		org.apache.commons.math.linear.BlockRealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(testData);
		org.apache.commons.math.linear.BlockRealMatrix m2 = new org.apache.commons.math.linear.BlockRealMatrix(testData2);
		junit.framework.Assert.assertEquals("testData Frobenius norm", java.lang.Math.sqrt(117.0), m.getFrobeniusNorm(), entryTolerance);
		junit.framework.Assert.assertEquals("testData2 Frobenius norm", java.lang.Math.sqrt(52.0), m2.getFrobeniusNorm(), entryTolerance);
	}

	public void testPlusMinus() {
		org.apache.commons.math.linear.BlockRealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(testData);
		org.apache.commons.math.linear.BlockRealMatrix m2 = new org.apache.commons.math.linear.BlockRealMatrix(testDataInv);
		assertClose(m.subtract(m2), m2.scalarMultiply(-1.0).add(m), entryTolerance);
		try {
			m.subtract(new org.apache.commons.math.linear.BlockRealMatrix(testData2));
			junit.framework.Assert.fail("Expecting illegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testMultiply() {
		org.apache.commons.math.linear.BlockRealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(testData);
		org.apache.commons.math.linear.BlockRealMatrix mInv = new org.apache.commons.math.linear.BlockRealMatrix(testDataInv);
		org.apache.commons.math.linear.BlockRealMatrix identity = new org.apache.commons.math.linear.BlockRealMatrix(id);
		org.apache.commons.math.linear.BlockRealMatrix m2 = new org.apache.commons.math.linear.BlockRealMatrix(testData2);
		assertClose(m.multiply(mInv), identity, entryTolerance);
		assertClose(mInv.multiply(m), identity, entryTolerance);
		assertClose(m.multiply(identity), m, entryTolerance);
		assertClose(identity.multiply(mInv), mInv, entryTolerance);
		assertClose(m2.multiply(identity), m2, entryTolerance);
		try {
			m.multiply(new org.apache.commons.math.linear.BlockRealMatrix(bigSingular));
			junit.framework.Assert.fail("Expecting illegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testSeveralBlocks() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(35 , 71);
		for (int i = 0 ; i < (m.getRowDimension()) ; ++i) {
			for (int j = 0 ; j < (m.getColumnDimension()) ; ++j) {
				m.setEntry(i, j, (i + (j / 1024.0)));
			}
		}
		org.apache.commons.math.linear.RealMatrix mT = m.transpose();
		junit.framework.Assert.assertEquals(m.getRowDimension(), mT.getColumnDimension());
		junit.framework.Assert.assertEquals(m.getColumnDimension(), mT.getRowDimension());
		for (int i = 0 ; i < (mT.getRowDimension()) ; ++i) {
			for (int j = 0 ; j < (mT.getColumnDimension()) ; ++j) {
				junit.framework.Assert.assertEquals(m.getEntry(j, i), mT.getEntry(i, j), 0);
			}
		}
		org.apache.commons.math.linear.RealMatrix mPm = m.add(m);
		for (int i = 0 ; i < (mPm.getRowDimension()) ; ++i) {
			for (int j = 0 ; j < (mPm.getColumnDimension()) ; ++j) {
				junit.framework.Assert.assertEquals((2 * (m.getEntry(i, j))), mPm.getEntry(i, j), 0);
			}
		}
		org.apache.commons.math.linear.RealMatrix mPmMm = mPm.subtract(m);
		for (int i = 0 ; i < (mPmMm.getRowDimension()) ; ++i) {
			for (int j = 0 ; j < (mPmMm.getColumnDimension()) ; ++j) {
				junit.framework.Assert.assertEquals(m.getEntry(i, j), mPmMm.getEntry(i, j), 0);
			}
		}
		org.apache.commons.math.linear.RealMatrix mTm = mT.multiply(m);
		for (int i = 0 ; i < (mTm.getRowDimension()) ; ++i) {
			for (int j = 0 ; j < (mTm.getColumnDimension()) ; ++j) {
				double sum = 0;
				for (int k = 0 ; k < (mT.getColumnDimension()) ; ++k) {
					sum += (k + (i / 1024.0)) * (k + (j / 1024.0));
				}
				junit.framework.Assert.assertEquals(sum, mTm.getEntry(i, j), 0);
			}
		}
		org.apache.commons.math.linear.RealMatrix mmT = m.multiply(mT);
		for (int i = 0 ; i < (mmT.getRowDimension()) ; ++i) {
			for (int j = 0 ; j < (mmT.getColumnDimension()) ; ++j) {
				double sum = 0;
				for (int k = 0 ; k < (m.getColumnDimension()) ; ++k) {
					sum += (i + (k / 1024.0)) * (j + (k / 1024.0));
				}
				junit.framework.Assert.assertEquals(sum, mmT.getEntry(i, j), 0);
			}
		}
		org.apache.commons.math.linear.RealMatrix sub1 = m.getSubMatrix(2, 9, 5, 20);
		for (int i = 0 ; i < (sub1.getRowDimension()) ; ++i) {
			for (int j = 0 ; j < (sub1.getColumnDimension()) ; ++j) {
				junit.framework.Assert.assertEquals(((i + 2) + ((j + 5) / 1024.0)), sub1.getEntry(i, j), 0);
			}
		}
		org.apache.commons.math.linear.RealMatrix sub2 = m.getSubMatrix(10, 12, 3, 70);
		for (int i = 0 ; i < (sub2.getRowDimension()) ; ++i) {
			for (int j = 0 ; j < (sub2.getColumnDimension()) ; ++j) {
				junit.framework.Assert.assertEquals(((i + 10) + ((j + 3) / 1024.0)), sub2.getEntry(i, j), 0);
			}
		}
		org.apache.commons.math.linear.RealMatrix sub3 = m.getSubMatrix(30, 34, 0, 5);
		for (int i = 0 ; i < (sub3.getRowDimension()) ; ++i) {
			for (int j = 0 ; j < (sub3.getColumnDimension()) ; ++j) {
				junit.framework.Assert.assertEquals(((i + 30) + ((j + 0) / 1024.0)), sub3.getEntry(i, j), 0);
			}
		}
		org.apache.commons.math.linear.RealMatrix sub4 = m.getSubMatrix(30, 32, 62, 65);
		for (int i = 0 ; i < (sub4.getRowDimension()) ; ++i) {
			for (int j = 0 ; j < (sub4.getColumnDimension()) ; ++j) {
				junit.framework.Assert.assertEquals(((i + 30) + ((j + 62) / 1024.0)), sub4.getEntry(i, j), 0);
			}
		}
	}

	private double[][] d3 = new double[][]{ new double[]{ 1 , 2 , 3 , 4 } , new double[]{ 5 , 6 , 7 , 8 } };

	private double[][] d4 = new double[][]{ new double[]{ 1 } , new double[]{ 2 } , new double[]{ 3 } , new double[]{ 4 } };

	private double[][] d5 = new double[][]{ new double[]{ 30 } , new double[]{ 70 } };

	public void testMultiply2() {
		org.apache.commons.math.linear.RealMatrix m3 = new org.apache.commons.math.linear.BlockRealMatrix(d3);
		org.apache.commons.math.linear.RealMatrix m4 = new org.apache.commons.math.linear.BlockRealMatrix(d4);
		org.apache.commons.math.linear.RealMatrix m5 = new org.apache.commons.math.linear.BlockRealMatrix(d5);
		assertClose(m3.multiply(m4), m5, entryTolerance);
	}

	public void testTrace() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(id);
		junit.framework.Assert.assertEquals("identity trace", 3.0, m.getTrace(), entryTolerance);
		m = new org.apache.commons.math.linear.BlockRealMatrix(testData2);
		try {
			m.getTrace();
			junit.framework.Assert.fail("Expecting NonSquareMatrixException");
		} catch (org.apache.commons.math.linear.NonSquareMatrixException ex) {
		}
	}

	public void testScalarAdd() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(testData);
		assertClose(new org.apache.commons.math.linear.BlockRealMatrix(testDataPlus2), m.scalarAdd(2.0), entryTolerance);
	}

	public void testOperate() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(id);
		assertClose(testVector, m.operate(testVector), entryTolerance);
		assertClose(testVector, m.operate(new org.apache.commons.math.linear.ArrayRealVector(testVector)).getData(), entryTolerance);
		m = new org.apache.commons.math.linear.BlockRealMatrix(bigSingular);
		try {
			m.operate(testVector);
			junit.framework.Assert.fail("Expecting illegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testOperateLarge() {
		int p = (7 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE)) / 2;
		int q = (5 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE)) / 2;
		int r = 3 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE);
		java.util.Random random = new java.util.Random(111007463902334L);
		org.apache.commons.math.linear.RealMatrix m1 = createRandomMatrix(random, p, q);
		org.apache.commons.math.linear.RealMatrix m2 = createRandomMatrix(random, q, r);
		org.apache.commons.math.linear.RealMatrix m1m2 = m1.multiply(m2);
		for (int i = 0 ; i < r ; ++i) {
			checkArrays(m1m2.getColumn(i), m1.operate(m2.getColumn(i)));
		}
	}

	public void testOperatePremultiplyLarge() {
		int p = (7 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE)) / 2;
		int q = (5 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE)) / 2;
		int r = 3 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE);
		java.util.Random random = new java.util.Random(111007463902334L);
		org.apache.commons.math.linear.RealMatrix m1 = createRandomMatrix(random, p, q);
		org.apache.commons.math.linear.RealMatrix m2 = createRandomMatrix(random, q, r);
		org.apache.commons.math.linear.RealMatrix m1m2 = m1.multiply(m2);
		for (int i = 0 ; i < p ; ++i) {
			checkArrays(m1m2.getRow(i), m2.preMultiply(m1.getRow(i)));
		}
	}

	public void testMath209() {
		org.apache.commons.math.linear.RealMatrix a = new org.apache.commons.math.linear.BlockRealMatrix(new double[][]{ new double[]{ 1 , 2 } , new double[]{ 3 , 4 } , new double[]{ 5 , 6 } });
		double[] b = a.operate(new double[]{ 1 , 1 });
		junit.framework.Assert.assertEquals(a.getRowDimension(), b.length);
		junit.framework.Assert.assertEquals(3.0, b[0], 1.0E-12);
		junit.framework.Assert.assertEquals(7.0, b[1], 1.0E-12);
		junit.framework.Assert.assertEquals(11.0, b[2], 1.0E-12);
	}

	public void testTranspose() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(testData);
		org.apache.commons.math.linear.RealMatrix mIT = new org.apache.commons.math.linear.LUDecompositionImpl(m).getSolver().getInverse().transpose();
		org.apache.commons.math.linear.RealMatrix mTI = new org.apache.commons.math.linear.LUDecompositionImpl(m.transpose()).getSolver().getInverse();
		assertClose(mIT, mTI, normTolerance);
		m = new org.apache.commons.math.linear.BlockRealMatrix(testData2);
		org.apache.commons.math.linear.RealMatrix mt = new org.apache.commons.math.linear.BlockRealMatrix(testData2T);
		assertClose(mt, m.transpose(), normTolerance);
	}

	public void testPremultiplyVector() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(testData);
		assertClose(m.preMultiply(testVector), preMultTest, normTolerance);
		assertClose(m.preMultiply(new org.apache.commons.math.linear.ArrayRealVector(testVector).getData()), preMultTest, normTolerance);
		m = new org.apache.commons.math.linear.BlockRealMatrix(bigSingular);
		try {
			m.preMultiply(testVector);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testPremultiply() {
		org.apache.commons.math.linear.RealMatrix m3 = new org.apache.commons.math.linear.BlockRealMatrix(d3);
		org.apache.commons.math.linear.RealMatrix m4 = new org.apache.commons.math.linear.BlockRealMatrix(d4);
		org.apache.commons.math.linear.RealMatrix m5 = new org.apache.commons.math.linear.BlockRealMatrix(d5);
		assertClose(m4.preMultiply(m3), m5, entryTolerance);
		org.apache.commons.math.linear.BlockRealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(testData);
		org.apache.commons.math.linear.BlockRealMatrix mInv = new org.apache.commons.math.linear.BlockRealMatrix(testDataInv);
		org.apache.commons.math.linear.BlockRealMatrix identity = new org.apache.commons.math.linear.BlockRealMatrix(id);
		assertClose(m.preMultiply(mInv), identity, entryTolerance);
		assertClose(mInv.preMultiply(m), identity, entryTolerance);
		assertClose(m.preMultiply(identity), m, entryTolerance);
		assertClose(identity.preMultiply(mInv), mInv, entryTolerance);
		try {
			m.preMultiply(new org.apache.commons.math.linear.BlockRealMatrix(bigSingular));
			junit.framework.Assert.fail("Expecting illegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testGetVectors() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(testData);
		assertClose(m.getRow(0), testDataRow1, entryTolerance);
		assertClose(m.getColumn(2), testDataCol3, entryTolerance);
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
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(testData);
		junit.framework.Assert.assertEquals("get entry", m.getEntry(0, 1), 2.0, entryTolerance);
		try {
			m.getEntry(10, 4);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
	}

	public void testExamples() {
		double[][] matrixData = new double[][]{ new double[]{ 1.0 , 2.0 , 3.0 } , new double[]{ 2.0 , 5.0 , 3.0 } };
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(matrixData);
		double[][] matrixData2 = new double[][]{ new double[]{ 1.0 , 2.0 } , new double[]{ 2.0 , 5.0 } , new double[]{ 1.0 , 7.0 } };
		org.apache.commons.math.linear.RealMatrix n = new org.apache.commons.math.linear.BlockRealMatrix(matrixData2);
		org.apache.commons.math.linear.RealMatrix p = m.multiply(n);
		junit.framework.Assert.assertEquals(2, p.getRowDimension());
		junit.framework.Assert.assertEquals(2, p.getColumnDimension());
		org.apache.commons.math.linear.RealMatrix pInverse = new org.apache.commons.math.linear.LUDecompositionImpl(p).getSolver().getInverse();
		junit.framework.Assert.assertEquals(2, pInverse.getRowDimension());
		junit.framework.Assert.assertEquals(2, pInverse.getColumnDimension());
		double[][] coefficientsData = new double[][]{ new double[]{ 2 , 3 , -2 } , new double[]{ -1 , 7 , 6 } , new double[]{ 4 , -3 , -5 } };
		org.apache.commons.math.linear.RealMatrix coefficients = new org.apache.commons.math.linear.BlockRealMatrix(coefficientsData);
		double[] constants = new double[]{ 1 , -2 , 1 };
		double[] solution = new org.apache.commons.math.linear.LUDecompositionImpl(coefficients).getSolver().solve(constants);
		junit.framework.Assert.assertEquals((((2 * (solution[0])) + (3 * (solution[1]))) - (2 * (solution[2]))), constants[0], 1.0E-12);
		junit.framework.Assert.assertEquals(((((-1) * (solution[0])) + (7 * (solution[1]))) + (6 * (solution[2]))), constants[1], 1.0E-12);
		junit.framework.Assert.assertEquals((((4 * (solution[0])) - (3 * (solution[1]))) - (5 * (solution[2]))), constants[2], 1.0E-12);
	}

	public void testGetSubMatrix() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(subTestData);
		checkGetSubMatrix(m, subRows23Cols00, 2, 3, 0, 0);
		checkGetSubMatrix(m, subRows00Cols33, 0, 0, 3, 3);
		checkGetSubMatrix(m, subRows01Cols23, 0, 1, 2, 3);
		checkGetSubMatrix(m, subRows02Cols13, new int[]{ 0 , 2 }, new int[]{ 1 , 3 });
		checkGetSubMatrix(m, subRows03Cols12, new int[]{ 0 , 3 }, new int[]{ 1 , 2 });
		checkGetSubMatrix(m, subRows03Cols123, new int[]{ 0 , 3 }, new int[]{ 1 , 2 , 3 });
		checkGetSubMatrix(m, subRows20Cols123, new int[]{ 2 , 0 }, new int[]{ 1 , 2 , 3 });
		checkGetSubMatrix(m, subRows31Cols31, new int[]{ 3 , 1 }, new int[]{ 3 , 1 });
		checkGetSubMatrix(m, subRows31Cols31, new int[]{ 3 , 1 }, new int[]{ 3 , 1 });
		checkGetSubMatrix(m, null, 1, 0, 2, 4);
		checkGetSubMatrix(m, null, -1, 1, 2, 2);
		checkGetSubMatrix(m, null, 1, 0, 2, 2);
		checkGetSubMatrix(m, null, 1, 0, 2, 4);
		checkGetSubMatrix(m, null, new int[]{  }, new int[]{ 0 });
		checkGetSubMatrix(m, null, new int[]{ 0 }, new int[]{ 4 });
	}

	private void checkGetSubMatrix(org.apache.commons.math.linear.RealMatrix m, double[][] reference, int startRow, int endRow, int startColumn, int endColumn) {
		try {
			org.apache.commons.math.linear.RealMatrix sub = m.getSubMatrix(startRow, endRow, startColumn, endColumn);
			if (reference != null) {
				junit.framework.Assert.assertEquals(new org.apache.commons.math.linear.BlockRealMatrix(reference), sub);
			} else {
				junit.framework.Assert.fail("Expecting MatrixIndexException");
			}
		} catch (org.apache.commons.math.linear.MatrixIndexException e) {
			if (reference != null) {
				throw e;
			} 
		}
	}

	private void checkGetSubMatrix(org.apache.commons.math.linear.RealMatrix m, double[][] reference, int[] selectedRows, int[] selectedColumns) {
		try {
			org.apache.commons.math.linear.RealMatrix sub = m.getSubMatrix(selectedRows, selectedColumns);
			if (reference != null) {
				junit.framework.Assert.assertEquals(new org.apache.commons.math.linear.BlockRealMatrix(reference), sub);
			} else {
				junit.framework.Assert.fail("Expecting MatrixIndexException");
			}
		} catch (org.apache.commons.math.linear.MatrixIndexException e) {
			if (reference != null) {
				throw e;
			} 
		}
	}

	public void testGetSetMatrixLarge() {
		int n = 3 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE);
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(n , n);
		org.apache.commons.math.linear.RealMatrix sub = new org.apache.commons.math.linear.BlockRealMatrix((n - 4) , (n - 4)).scalarAdd(1);
		m.setSubMatrix(sub.getData(), 2, 2);
		for (int i = 0 ; i < n ; ++i) {
			for (int j = 0 ; j < n ; ++j) {
				if ((((i < 2) || (i > (n - 3))) || (j < 2)) || (j > (n - 3))) {
					junit.framework.Assert.assertEquals(0.0, m.getEntry(i, j), 0.0);
				} else {
					junit.framework.Assert.assertEquals(1.0, m.getEntry(i, j), 0.0);
				}
			}
		}
		junit.framework.Assert.assertEquals(sub, m.getSubMatrix(2, (n - 3), 2, (n - 3)));
	}

	public void testCopySubMatrix() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(subTestData);
		checkCopy(m, subRows23Cols00, 2, 3, 0, 0);
		checkCopy(m, subRows00Cols33, 0, 0, 3, 3);
		checkCopy(m, subRows01Cols23, 0, 1, 2, 3);
		checkCopy(m, subRows02Cols13, new int[]{ 0 , 2 }, new int[]{ 1 , 3 });
		checkCopy(m, subRows03Cols12, new int[]{ 0 , 3 }, new int[]{ 1 , 2 });
		checkCopy(m, subRows03Cols123, new int[]{ 0 , 3 }, new int[]{ 1 , 2 , 3 });
		checkCopy(m, subRows20Cols123, new int[]{ 2 , 0 }, new int[]{ 1 , 2 , 3 });
		checkCopy(m, subRows31Cols31, new int[]{ 3 , 1 }, new int[]{ 3 , 1 });
		checkCopy(m, subRows31Cols31, new int[]{ 3 , 1 }, new int[]{ 3 , 1 });
		checkCopy(m, null, 1, 0, 2, 4);
		checkCopy(m, null, -1, 1, 2, 2);
		checkCopy(m, null, 1, 0, 2, 2);
		checkCopy(m, null, 1, 0, 2, 4);
		checkCopy(m, null, new int[]{  }, new int[]{ 0 });
		checkCopy(m, null, new int[]{ 0 }, new int[]{ 4 });
	}

	private void checkCopy(org.apache.commons.math.linear.RealMatrix m, double[][] reference, int startRow, int endRow, int startColumn, int endColumn) {
		try {
			double[][] sub = reference == null ? new double[1][1] : new double[reference.length][reference[0].length];
			m.copySubMatrix(startRow, endRow, startColumn, endColumn, sub);
			if (reference != null) {
				junit.framework.Assert.assertEquals(new org.apache.commons.math.linear.BlockRealMatrix(reference), new org.apache.commons.math.linear.BlockRealMatrix(sub));
			} else {
				junit.framework.Assert.fail("Expecting MatrixIndexException");
			}
		} catch (org.apache.commons.math.linear.MatrixIndexException e) {
			if (reference != null) {
				throw e;
			} 
		}
	}

	private void checkCopy(org.apache.commons.math.linear.RealMatrix m, double[][] reference, int[] selectedRows, int[] selectedColumns) {
		try {
			double[][] sub = reference == null ? new double[1][1] : new double[reference.length][reference[0].length];
			m.copySubMatrix(selectedRows, selectedColumns, sub);
			if (reference != null) {
				junit.framework.Assert.assertEquals(new org.apache.commons.math.linear.BlockRealMatrix(reference), new org.apache.commons.math.linear.BlockRealMatrix(sub));
			} else {
				junit.framework.Assert.fail("Expecting MatrixIndexException");
			}
		} catch (org.apache.commons.math.linear.MatrixIndexException e) {
			if (reference != null) {
				throw e;
			} 
		}
	}

	public void testGetRowMatrix() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(subTestData);
		org.apache.commons.math.linear.RealMatrix mRow0 = new org.apache.commons.math.linear.BlockRealMatrix(subRow0);
		org.apache.commons.math.linear.RealMatrix mRow3 = new org.apache.commons.math.linear.BlockRealMatrix(subRow3);
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
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(subTestData);
		org.apache.commons.math.linear.RealMatrix mRow3 = new org.apache.commons.math.linear.BlockRealMatrix(subRow3);
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

	public void testGetSetRowMatrixLarge() {
		int n = 3 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE);
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(n , n);
		org.apache.commons.math.linear.RealMatrix sub = new org.apache.commons.math.linear.BlockRealMatrix(1 , n).scalarAdd(1);
		m.setRowMatrix(2, sub);
		for (int i = 0 ; i < n ; ++i) {
			for (int j = 0 ; j < n ; ++j) {
				if (i != 2) {
					junit.framework.Assert.assertEquals(0.0, m.getEntry(i, j), 0.0);
				} else {
					junit.framework.Assert.assertEquals(1.0, m.getEntry(i, j), 0.0);
				}
			}
		}
		junit.framework.Assert.assertEquals(sub, m.getRowMatrix(2));
	}

	public void testGetColumnMatrix() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(subTestData);
		org.apache.commons.math.linear.RealMatrix mColumn1 = new org.apache.commons.math.linear.BlockRealMatrix(subColumn1);
		org.apache.commons.math.linear.RealMatrix mColumn3 = new org.apache.commons.math.linear.BlockRealMatrix(subColumn3);
		junit.framework.Assert.assertEquals(mColumn1, m.getColumnMatrix(1));
		junit.framework.Assert.assertEquals(mColumn3, m.getColumnMatrix(3));
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
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(subTestData);
		org.apache.commons.math.linear.RealMatrix mColumn3 = new org.apache.commons.math.linear.BlockRealMatrix(subColumn3);
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

	public void testGetSetColumnMatrixLarge() {
		int n = 3 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE);
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(n , n);
		org.apache.commons.math.linear.RealMatrix sub = new org.apache.commons.math.linear.BlockRealMatrix(n , 1).scalarAdd(1);
		m.setColumnMatrix(2, sub);
		for (int i = 0 ; i < n ; ++i) {
			for (int j = 0 ; j < n ; ++j) {
				if (j != 2) {
					junit.framework.Assert.assertEquals(0.0, m.getEntry(i, j), 0.0);
				} else {
					junit.framework.Assert.assertEquals(1.0, m.getEntry(i, j), 0.0);
				}
			}
		}
		junit.framework.Assert.assertEquals(sub, m.getColumnMatrix(2));
	}

	public void testGetRowVector() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(subTestData);
		org.apache.commons.math.linear.RealVector mRow0 = new org.apache.commons.math.linear.ArrayRealVector(subRow0[0]);
		org.apache.commons.math.linear.RealVector mRow3 = new org.apache.commons.math.linear.ArrayRealVector(subRow3[0]);
		junit.framework.Assert.assertEquals(mRow0, m.getRowVector(0));
		junit.framework.Assert.assertEquals(mRow3, m.getRowVector(3));
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
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(subTestData);
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

	public void testGetSetRowVectorLarge() {
		int n = 3 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE);
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(n , n);
		org.apache.commons.math.linear.RealVector sub = new org.apache.commons.math.linear.ArrayRealVector(n , 1.0);
		m.setRowVector(2, sub);
		for (int i = 0 ; i < n ; ++i) {
			for (int j = 0 ; j < n ; ++j) {
				if (i != 2) {
					junit.framework.Assert.assertEquals(0.0, m.getEntry(i, j), 0.0);
				} else {
					junit.framework.Assert.assertEquals(1.0, m.getEntry(i, j), 0.0);
				}
			}
		}
		junit.framework.Assert.assertEquals(sub, m.getRowVector(2));
	}

	public void testGetColumnVector() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(subTestData);
		org.apache.commons.math.linear.RealVector mColumn1 = columnToVector(subColumn1);
		org.apache.commons.math.linear.RealVector mColumn3 = columnToVector(subColumn3);
		junit.framework.Assert.assertEquals(mColumn1, m.getColumnVector(1));
		junit.framework.Assert.assertEquals(mColumn3, m.getColumnVector(3));
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
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(subTestData);
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

	public void testGetSetColumnVectorLarge() {
		int n = 3 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE);
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(n , n);
		org.apache.commons.math.linear.RealVector sub = new org.apache.commons.math.linear.ArrayRealVector(n , 1.0);
		m.setColumnVector(2, sub);
		for (int i = 0 ; i < n ; ++i) {
			for (int j = 0 ; j < n ; ++j) {
				if (j != 2) {
					junit.framework.Assert.assertEquals(0.0, m.getEntry(i, j), 0.0);
				} else {
					junit.framework.Assert.assertEquals(1.0, m.getEntry(i, j), 0.0);
				}
			}
		}
		junit.framework.Assert.assertEquals(sub, m.getColumnVector(2));
	}

	private org.apache.commons.math.linear.RealVector columnToVector(double[][] column) {
		double[] data = new double[column.length];
		for (int i = 0 ; i < (data.length) ; ++i) {
			data[i] = column[i][0];
		}
		return new org.apache.commons.math.linear.ArrayRealVector(data , false);
	}

	public void testGetRow() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(subTestData);
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
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(subTestData);
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

	public void testGetSetRowLarge() {
		int n = 3 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE);
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(n , n);
		double[] sub = new double[n];
		java.util.Arrays.fill(sub, 1.0);
		m.setRow(2, sub);
		for (int i = 0 ; i < n ; ++i) {
			for (int j = 0 ; j < n ; ++j) {
				if (i != 2) {
					junit.framework.Assert.assertEquals(0.0, m.getEntry(i, j), 0.0);
				} else {
					junit.framework.Assert.assertEquals(1.0, m.getEntry(i, j), 0.0);
				}
			}
		}
		checkArrays(sub, m.getRow(2));
	}

	public void testGetColumn() {
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(subTestData);
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
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(subTestData);
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

	public void testGetSetColumnLarge() {
		int n = 3 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE);
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(n , n);
		double[] sub = new double[n];
		java.util.Arrays.fill(sub, 1.0);
		m.setColumn(2, sub);
		for (int i = 0 ; i < n ; ++i) {
			for (int j = 0 ; j < n ; ++j) {
				if (j != 2) {
					junit.framework.Assert.assertEquals(0.0, m.getEntry(i, j), 0.0);
				} else {
					junit.framework.Assert.assertEquals(1.0, m.getEntry(i, j), 0.0);
				}
			}
		}
		checkArrays(sub, m.getColumn(2));
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
		org.apache.commons.math.linear.BlockRealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(testData);
		org.apache.commons.math.linear.BlockRealMatrix m1 = m.copy();
		org.apache.commons.math.linear.BlockRealMatrix mt = m.transpose();
		junit.framework.Assert.assertTrue(((m.hashCode()) != (mt.hashCode())));
		junit.framework.Assert.assertEquals(m.hashCode(), m1.hashCode());
		junit.framework.Assert.assertEquals(m, m);
		junit.framework.Assert.assertEquals(m, m1);
		junit.framework.Assert.assertFalse(m.equals(null));
		junit.framework.Assert.assertFalse(m.equals(mt));
		junit.framework.Assert.assertFalse(m.equals(new org.apache.commons.math.linear.BlockRealMatrix(bigSingular)));
	}

	public void testToString() {
		org.apache.commons.math.linear.BlockRealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(testData);
		junit.framework.Assert.assertEquals("BlockRealMatrix{{1.0,2.0,3.0},{2.0,5.0,3.0},{1.0,0.0,8.0}}", m.toString());
	}

	public void testSetSubMatrix() throws java.lang.Exception {
		org.apache.commons.math.linear.BlockRealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(testData);
		m.setSubMatrix(detData2, 1, 1);
		org.apache.commons.math.linear.RealMatrix expected = new org.apache.commons.math.linear.BlockRealMatrix(new double[][]{ new double[]{ 1.0 , 2.0 , 3.0 } , new double[]{ 2.0 , 1.0 , 3.0 } , new double[]{ 1.0 , 2.0 , 4.0 } });
		junit.framework.Assert.assertEquals(expected, m);
		m.setSubMatrix(detData2, 0, 0);
		expected = new org.apache.commons.math.linear.BlockRealMatrix(new double[][]{ new double[]{ 1.0 , 3.0 , 3.0 } , new double[]{ 2.0 , 4.0 , 3.0 } , new double[]{ 1.0 , 2.0 , 4.0 } });
		junit.framework.Assert.assertEquals(expected, m);
		m.setSubMatrix(testDataPlus2, 0, 0);
		expected = new org.apache.commons.math.linear.BlockRealMatrix(new double[][]{ new double[]{ 3.0 , 4.0 , 5.0 } , new double[]{ 4.0 , 7.0 , 5.0 } , new double[]{ 3.0 , 2.0 , 10.0 } });
		junit.framework.Assert.assertEquals(expected, m);
		org.apache.commons.math.linear.BlockRealMatrix matrix = new org.apache.commons.math.linear.BlockRealMatrix(new double[][]{ new double[]{ 1 , 2 , 3 , 4 } , new double[]{ 5 , 6 , 7 , 8 } , new double[]{ 9 , 0 , 1 , 2 } });
		matrix.setSubMatrix(new double[][]{ new double[]{ 3 , 4 } , new double[]{ 5 , 6 } }, 1, 1);
		expected = new org.apache.commons.math.linear.BlockRealMatrix(new double[][]{ new double[]{ 1 , 2 , 3 , 4 } , new double[]{ 5 , 3 , 4 , 8 } , new double[]{ 9 , 5 , 6 , 2 } });
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
		org.apache.commons.math.linear.RealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(rows , columns);
		m.walkInRowOrder(new org.apache.commons.math.linear.BlockRealMatrixTest.SetVisitor());
		org.apache.commons.math.linear.BlockRealMatrixTest.GetVisitor getVisitor = new org.apache.commons.math.linear.BlockRealMatrixTest.GetVisitor();
		m.walkInOptimizedOrder(getVisitor);
		junit.framework.Assert.assertEquals((rows * columns), getVisitor.getCount());
		m = new org.apache.commons.math.linear.BlockRealMatrix(rows , columns);
		m.walkInRowOrder(new org.apache.commons.math.linear.BlockRealMatrixTest.SetVisitor(), 1, (rows - 2), 1, (columns - 2));
		getVisitor = new org.apache.commons.math.linear.BlockRealMatrixTest.GetVisitor();
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
		m = new org.apache.commons.math.linear.BlockRealMatrix(rows , columns);
		m.walkInColumnOrder(new org.apache.commons.math.linear.BlockRealMatrixTest.SetVisitor());
		getVisitor = new org.apache.commons.math.linear.BlockRealMatrixTest.GetVisitor();
		m.walkInOptimizedOrder(getVisitor);
		junit.framework.Assert.assertEquals((rows * columns), getVisitor.getCount());
		m = new org.apache.commons.math.linear.BlockRealMatrix(rows , columns);
		m.walkInColumnOrder(new org.apache.commons.math.linear.BlockRealMatrixTest.SetVisitor(), 1, (rows - 2), 1, (columns - 2));
		getVisitor = new org.apache.commons.math.linear.BlockRealMatrixTest.GetVisitor();
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
		m = new org.apache.commons.math.linear.BlockRealMatrix(rows , columns);
		m.walkInOptimizedOrder(new org.apache.commons.math.linear.BlockRealMatrixTest.SetVisitor());
		getVisitor = new org.apache.commons.math.linear.BlockRealMatrixTest.GetVisitor();
		m.walkInRowOrder(getVisitor);
		junit.framework.Assert.assertEquals((rows * columns), getVisitor.getCount());
		m = new org.apache.commons.math.linear.BlockRealMatrix(rows , columns);
		m.walkInOptimizedOrder(new org.apache.commons.math.linear.BlockRealMatrixTest.SetVisitor(), 1, (rows - 2), 1, (columns - 2));
		getVisitor = new org.apache.commons.math.linear.BlockRealMatrixTest.GetVisitor();
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
		m = new org.apache.commons.math.linear.BlockRealMatrix(rows , columns);
		m.walkInOptimizedOrder(new org.apache.commons.math.linear.BlockRealMatrixTest.SetVisitor());
		getVisitor = new org.apache.commons.math.linear.BlockRealMatrixTest.GetVisitor();
		m.walkInColumnOrder(getVisitor);
		junit.framework.Assert.assertEquals((rows * columns), getVisitor.getCount());
		m = new org.apache.commons.math.linear.BlockRealMatrix(rows , columns);
		m.walkInOptimizedOrder(new org.apache.commons.math.linear.BlockRealMatrixTest.SetVisitor(), 1, (rows - 2), 1, (columns - 2));
		getVisitor = new org.apache.commons.math.linear.BlockRealMatrixTest.GetVisitor();
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
		org.apache.commons.math.linear.BlockRealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(testData);
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

	protected void assertClose(org.apache.commons.math.linear.RealMatrix m, org.apache.commons.math.linear.RealMatrix n, double tolerance) {
		junit.framework.Assert.assertTrue(((m.subtract(n).getNorm()) < tolerance));
	}

	protected void assertClose(double[] m, double[] n, double tolerance) {
		if ((m.length) != (n.length)) {
			junit.framework.Assert.fail("vectors not same length");
		} 
		for (int i = 0 ; i < (m.length) ; i++) {
			junit.framework.Assert.assertEquals(m[i], n[i], tolerance);
		}
	}

	private org.apache.commons.math.linear.BlockRealMatrix createRandomMatrix(java.util.Random r, int rows, int columns) {
		org.apache.commons.math.linear.BlockRealMatrix m = new org.apache.commons.math.linear.BlockRealMatrix(rows , columns);
		for (int i = 0 ; i < rows ; ++i) {
			for (int j = 0 ; j < columns ; ++j) {
				m.setEntry(i, j, ((200 * (r.nextDouble())) - 100));
			}
		}
		return m;
	}
}

