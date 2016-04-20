package org.apache.commons.math.linear;


@java.lang.Deprecated
public final class BigMatrixImplTest extends junit.framework.TestCase {
	protected java.lang.String[][] testDataString = new java.lang.String[][]{ new java.lang.String[]{ "1" , "2" , "3" } , new java.lang.String[]{ "2" , "5" , "3" } , new java.lang.String[]{ "1" , "0" , "8" } };

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

	public BigMatrixImplTest(java.lang.String name) {
		super(name);
	}

	public static final double[] asDouble(java.math.BigDecimal[] data) {
		double[] d = new double[data.length];
		for (int i = 0 ; i < (d.length) ; i++) {
			d[i] = data[i].doubleValue();
		}
		return d;
	}

	public static final double[][] asDouble(java.math.BigDecimal[][] data) {
		double[][] d = new double[data.length][data[0].length];
		for (int i = 0 ; i < (d.length) ; i++) {
			for (int j = 0 ; j < (d[i].length) ; j++)
				d[i][j] = data[i][j].doubleValue();
		}
		return d;
	}

	public static final java.math.BigDecimal[] asBigDecimal(double[] data) {
		java.math.BigDecimal[] d = new java.math.BigDecimal[data.length];
		for (int i = 0 ; i < (d.length) ; i++) {
			d[i] = new java.math.BigDecimal(data[i]);
		}
		return d;
	}

	public static final java.math.BigDecimal[][] asBigDecimal(double[][] data) {
		java.math.BigDecimal[][] d = new java.math.BigDecimal[data.length][data[0].length];
		for (int i = 0 ; i < (d.length) ; i++) {
			for (int j = 0 ; j < (data[i].length) ; j++) {
				d[i][j] = new java.math.BigDecimal(data[i][j]);
			}
		}
		return d;
	}

	public void testDimensions() {
		org.apache.commons.math.linear.BigMatrixImpl m = new org.apache.commons.math.linear.BigMatrixImpl(testData);
		org.apache.commons.math.linear.BigMatrixImpl m2 = new org.apache.commons.math.linear.BigMatrixImpl(testData2);
		junit.framework.Assert.assertEquals("testData row dimension", 3, m.getRowDimension());
		junit.framework.Assert.assertEquals("testData column dimension", 3, m.getColumnDimension());
		junit.framework.Assert.assertTrue("testData is square", m.isSquare());
		junit.framework.Assert.assertEquals("testData2 row dimension", m2.getRowDimension(), 2);
		junit.framework.Assert.assertEquals("testData2 column dimension", m2.getColumnDimension(), 3);
		junit.framework.Assert.assertTrue("testData2 is not square", !(m2.isSquare()));
	}

	public void testCopyFunctions() {
		org.apache.commons.math.linear.BigMatrixImpl m1 = new org.apache.commons.math.linear.BigMatrixImpl(testData);
		org.apache.commons.math.linear.BigMatrixImpl m2 = new org.apache.commons.math.linear.BigMatrixImpl(m1.getData());
		junit.framework.Assert.assertEquals(m2, m1);
		org.apache.commons.math.linear.BigMatrixImpl m3 = new org.apache.commons.math.linear.BigMatrixImpl(testData);
		org.apache.commons.math.linear.BigMatrixImpl m4 = new org.apache.commons.math.linear.BigMatrixImpl(m3.getData() , false);
		junit.framework.Assert.assertEquals(m4, m3);
	}

	public void testConstructors() {
		org.apache.commons.math.linear.BigMatrix m1 = new org.apache.commons.math.linear.BigMatrixImpl(testData);
		org.apache.commons.math.linear.BigMatrix m2 = new org.apache.commons.math.linear.BigMatrixImpl(testDataString);
		org.apache.commons.math.linear.BigMatrix m3 = new org.apache.commons.math.linear.BigMatrixImpl(org.apache.commons.math.linear.BigMatrixImplTest.asBigDecimal(testData));
		org.apache.commons.math.linear.BigMatrix m4 = new org.apache.commons.math.linear.BigMatrixImpl(org.apache.commons.math.linear.BigMatrixImplTest.asBigDecimal(testData) , true);
		org.apache.commons.math.linear.BigMatrix m5 = new org.apache.commons.math.linear.BigMatrixImpl(org.apache.commons.math.linear.BigMatrixImplTest.asBigDecimal(testData) , false);
		assertClose("double, string", m1, m2, java.lang.Double.MIN_VALUE);
		assertClose("double, BigDecimal", m1, m3, java.lang.Double.MIN_VALUE);
		assertClose("string, BigDecimal", m2, m3, java.lang.Double.MIN_VALUE);
		assertClose("double, BigDecimal/true", m1, m4, java.lang.Double.MIN_VALUE);
		assertClose("double, BigDecimal/false", m1, m5, java.lang.Double.MIN_VALUE);
		try {
			new org.apache.commons.math.linear.BigMatrixImpl(new java.lang.String[][]{ new java.lang.String[]{ "0" , "hello" , "1" } });
			junit.framework.Assert.fail("Expecting NumberFormatException");
		} catch (java.lang.NumberFormatException ex) {
		}
		try {
			new org.apache.commons.math.linear.BigMatrixImpl(new java.lang.String[][]{  });
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			new org.apache.commons.math.linear.BigMatrixImpl(new java.lang.String[][]{ new java.lang.String[]{  } , new java.lang.String[]{  } });
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			new org.apache.commons.math.linear.BigMatrixImpl(new java.lang.String[][]{ new java.lang.String[]{ "a" , "b" } , new java.lang.String[]{ "c" } });
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			new org.apache.commons.math.linear.BigMatrixImpl(0 , 1);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			new org.apache.commons.math.linear.BigMatrixImpl(1 , 0);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testAdd() {
		org.apache.commons.math.linear.BigMatrixImpl m = new org.apache.commons.math.linear.BigMatrixImpl(testData);
		org.apache.commons.math.linear.BigMatrixImpl mInv = new org.apache.commons.math.linear.BigMatrixImpl(testDataInv);
		org.apache.commons.math.linear.BigMatrix mPlusMInv = m.add(mInv);
		double[][] sumEntries = org.apache.commons.math.linear.BigMatrixImplTest.asDouble(mPlusMInv.getData());
		for (int row = 0 ; row < (m.getRowDimension()) ; row++) {
			for (int col = 0 ; col < (m.getColumnDimension()) ; col++) {
				junit.framework.Assert.assertEquals("sum entry entry", testDataPlusInv[row][col], sumEntries[row][col], entryTolerance);
			}
		}
	}

	public void testAddFail() {
		org.apache.commons.math.linear.BigMatrixImpl m = new org.apache.commons.math.linear.BigMatrixImpl(testData);
		org.apache.commons.math.linear.BigMatrixImpl m2 = new org.apache.commons.math.linear.BigMatrixImpl(testData2);
		try {
			m.add(m2);
			junit.framework.Assert.fail("IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testNorm() {
		org.apache.commons.math.linear.BigMatrixImpl m = new org.apache.commons.math.linear.BigMatrixImpl(testData);
		org.apache.commons.math.linear.BigMatrixImpl m2 = new org.apache.commons.math.linear.BigMatrixImpl(testData2);
		junit.framework.Assert.assertEquals("testData norm", 14.0, m.getNorm().doubleValue(), entryTolerance);
		junit.framework.Assert.assertEquals("testData2 norm", 7.0, m2.getNorm().doubleValue(), entryTolerance);
	}

	public void testPlusMinus() {
		org.apache.commons.math.linear.BigMatrixImpl m = new org.apache.commons.math.linear.BigMatrixImpl(testData);
		org.apache.commons.math.linear.BigMatrixImpl m2 = new org.apache.commons.math.linear.BigMatrixImpl(testDataInv);
		assertClose("m-n = m + -n", m.subtract(m2), m2.scalarMultiply(new java.math.BigDecimal(-1.0)).add(m), entryTolerance);
		try {
			m.subtract(new org.apache.commons.math.linear.BigMatrixImpl(testData2));
			junit.framework.Assert.fail("Expecting illegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testMultiply() {
		org.apache.commons.math.linear.BigMatrixImpl m = new org.apache.commons.math.linear.BigMatrixImpl(testData);
		org.apache.commons.math.linear.BigMatrixImpl mInv = new org.apache.commons.math.linear.BigMatrixImpl(testDataInv);
		org.apache.commons.math.linear.BigMatrixImpl identity = new org.apache.commons.math.linear.BigMatrixImpl(id);
		org.apache.commons.math.linear.BigMatrixImpl m2 = new org.apache.commons.math.linear.BigMatrixImpl(testData2);
		assertClose("inverse multiply", m.multiply(mInv), identity, entryTolerance);
		assertClose("inverse multiply", mInv.multiply(m), identity, entryTolerance);
		assertClose("identity multiply", m.multiply(identity), m, entryTolerance);
		assertClose("identity multiply", identity.multiply(mInv), mInv, entryTolerance);
		assertClose("identity multiply", m2.multiply(identity), m2, entryTolerance);
		try {
			m.multiply(new org.apache.commons.math.linear.BigMatrixImpl(bigSingular));
			junit.framework.Assert.fail("Expecting illegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	private double[][] d3 = new double[][]{ new double[]{ 1 , 2 , 3 , 4 } , new double[]{ 5 , 6 , 7 , 8 } };

	private double[][] d4 = new double[][]{ new double[]{ 1 } , new double[]{ 2 } , new double[]{ 3 } , new double[]{ 4 } };

	private double[][] d5 = new double[][]{ new double[]{ 30 } , new double[]{ 70 } };

	public void testMultiply2() {
		org.apache.commons.math.linear.BigMatrix m3 = new org.apache.commons.math.linear.BigMatrixImpl(d3);
		org.apache.commons.math.linear.BigMatrix m4 = new org.apache.commons.math.linear.BigMatrixImpl(d4);
		org.apache.commons.math.linear.BigMatrix m5 = new org.apache.commons.math.linear.BigMatrixImpl(d5);
		assertClose("m3*m4=m5", m3.multiply(m4), m5, entryTolerance);
	}

	public void testIsSingular() {
		org.apache.commons.math.linear.BigMatrixImpl m = new org.apache.commons.math.linear.BigMatrixImpl(singular);
		junit.framework.Assert.assertTrue("singular", m.isSingular());
		m = new org.apache.commons.math.linear.BigMatrixImpl(bigSingular);
		junit.framework.Assert.assertTrue("big singular", m.isSingular());
		m = new org.apache.commons.math.linear.BigMatrixImpl(id);
		junit.framework.Assert.assertTrue("identity nonsingular", !(m.isSingular()));
		m = new org.apache.commons.math.linear.BigMatrixImpl(testData);
		junit.framework.Assert.assertTrue("testData nonsingular", !(m.isSingular()));
	}

	public void testInverse() {
		org.apache.commons.math.linear.BigMatrixImpl m = new org.apache.commons.math.linear.BigMatrixImpl(testData);
		org.apache.commons.math.linear.BigMatrix mInv = new org.apache.commons.math.linear.BigMatrixImpl(testDataInv);
		assertClose("inverse", mInv, m.inverse(), normTolerance);
		assertClose("inverse^2", m, m.inverse().inverse(), 1.0E-11);
		m = new org.apache.commons.math.linear.BigMatrixImpl(testData2);
		try {
			m.inverse();
			junit.framework.Assert.fail("Expecting InvalidMatrixException");
		} catch (org.apache.commons.math.linear.InvalidMatrixException ex) {
		}
		m = new org.apache.commons.math.linear.BigMatrixImpl(singular);
		try {
			m.inverse();
			junit.framework.Assert.fail("Expecting InvalidMatrixException");
		} catch (org.apache.commons.math.linear.InvalidMatrixException ex) {
		}
	}

	public void testSolve() {
		org.apache.commons.math.linear.BigMatrixImpl m = new org.apache.commons.math.linear.BigMatrixImpl(testData);
		org.apache.commons.math.linear.BigMatrix mInv = new org.apache.commons.math.linear.BigMatrixImpl(testDataInv);
		assertClose("inverse-operate", org.apache.commons.math.linear.BigMatrixImplTest.asDouble(mInv.operate(org.apache.commons.math.linear.BigMatrixImplTest.asBigDecimal(testVector))), org.apache.commons.math.linear.BigMatrixImplTest.asDouble(m.solve(org.apache.commons.math.linear.BigMatrixImplTest.asBigDecimal(testVector))), normTolerance);
		try {
			org.apache.commons.math.linear.BigMatrixImplTest.asDouble(m.solve(org.apache.commons.math.linear.BigMatrixImplTest.asBigDecimal(testVector2)));
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		org.apache.commons.math.linear.BigMatrix bs = new org.apache.commons.math.linear.BigMatrixImpl(bigSingular);
		try {
			bs.solve(bs);
			junit.framework.Assert.fail("Expecting InvalidMatrixException");
		} catch (org.apache.commons.math.linear.InvalidMatrixException ex) {
		}
		try {
			m.solve(bs);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			new org.apache.commons.math.linear.BigMatrixImpl(testData2).solve(bs);
			junit.framework.Assert.fail("Expecting illegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			new org.apache.commons.math.linear.BigMatrixImpl(testData2).luDecompose();
			junit.framework.Assert.fail("Expecting InvalidMatrixException");
		} catch (org.apache.commons.math.linear.InvalidMatrixException ex) {
		}
	}

	public void testDeterminant() {
		org.apache.commons.math.linear.BigMatrix m = new org.apache.commons.math.linear.BigMatrixImpl(bigSingular);
		junit.framework.Assert.assertEquals("singular determinant", 0, m.getDeterminant().doubleValue(), 0);
		m = new org.apache.commons.math.linear.BigMatrixImpl(detData);
		junit.framework.Assert.assertEquals("nonsingular test", -3.0, m.getDeterminant().doubleValue(), normTolerance);
		m = new org.apache.commons.math.linear.BigMatrixImpl(detData2);
		junit.framework.Assert.assertEquals("nonsingular R test 1", -2.0, m.getDeterminant().doubleValue(), normTolerance);
		m = new org.apache.commons.math.linear.BigMatrixImpl(testData);
		junit.framework.Assert.assertEquals("nonsingular  R test 2", -1.0, m.getDeterminant().doubleValue(), normTolerance);
		try {
			double d = new org.apache.commons.math.linear.BigMatrixImpl(testData2).getDeterminant().doubleValue();
			junit.framework.Assert.fail(("Expecting InvalidMatrixException, got " + d));
		} catch (org.apache.commons.math.linear.InvalidMatrixException ex) {
		}
	}

	public void testTrace() {
		org.apache.commons.math.linear.BigMatrix m = new org.apache.commons.math.linear.BigMatrixImpl(id);
		junit.framework.Assert.assertEquals("identity trace", 3.0, m.getTrace().doubleValue(), entryTolerance);
		m = new org.apache.commons.math.linear.BigMatrixImpl(testData2);
		try {
			double t = m.getTrace().doubleValue();
			junit.framework.Assert.fail(("Expecting NonSquareMatrixException, got " + t));
		} catch (org.apache.commons.math.linear.NonSquareMatrixException ex) {
		}
	}

	public void testScalarAdd() {
		org.apache.commons.math.linear.BigMatrix m = new org.apache.commons.math.linear.BigMatrixImpl(testData);
		assertClose("scalar add", new org.apache.commons.math.linear.BigMatrixImpl(testDataPlus2), m.scalarAdd(new java.math.BigDecimal(2.0)), entryTolerance);
	}

	public void testOperate() {
		org.apache.commons.math.linear.BigMatrix m = new org.apache.commons.math.linear.BigMatrixImpl(id);
		double[] x = org.apache.commons.math.linear.BigMatrixImplTest.asDouble(m.operate(org.apache.commons.math.linear.BigMatrixImplTest.asBigDecimal(testVector)));
		assertClose("identity operate", testVector, x, entryTolerance);
		m = new org.apache.commons.math.linear.BigMatrixImpl(bigSingular);
		try {
			org.apache.commons.math.linear.BigMatrixImplTest.asDouble(m.operate(org.apache.commons.math.linear.BigMatrixImplTest.asBigDecimal(testVector)));
			junit.framework.Assert.fail("Expecting illegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testMath209() {
		org.apache.commons.math.linear.BigMatrix a = new org.apache.commons.math.linear.BigMatrixImpl(new java.math.BigDecimal[][]{ new java.math.BigDecimal[]{ new java.math.BigDecimal(1) , new java.math.BigDecimal(2) } , new java.math.BigDecimal[]{ new java.math.BigDecimal(3) , new java.math.BigDecimal(4) } , new java.math.BigDecimal[]{ new java.math.BigDecimal(5) , new java.math.BigDecimal(6) } } , false);
		java.math.BigDecimal[] b = a.operate(new java.math.BigDecimal[]{ new java.math.BigDecimal(1) , new java.math.BigDecimal(1) });
		junit.framework.Assert.assertEquals(a.getRowDimension(), b.length);
		junit.framework.Assert.assertEquals(3.0, b[0].doubleValue(), 1.0E-12);
		junit.framework.Assert.assertEquals(7.0, b[1].doubleValue(), 1.0E-12);
		junit.framework.Assert.assertEquals(11.0, b[2].doubleValue(), 1.0E-12);
	}

	public void testTranspose() {
		org.apache.commons.math.linear.BigMatrix m = new org.apache.commons.math.linear.BigMatrixImpl(testData);
		assertClose("inverse-transpose", m.inverse().transpose(), m.transpose().inverse(), normTolerance);
		m = new org.apache.commons.math.linear.BigMatrixImpl(testData2);
		org.apache.commons.math.linear.BigMatrix mt = new org.apache.commons.math.linear.BigMatrixImpl(testData2T);
		assertClose("transpose", mt, m.transpose(), normTolerance);
	}

	public void testPremultiplyVector() {
		org.apache.commons.math.linear.BigMatrix m = new org.apache.commons.math.linear.BigMatrixImpl(testData);
		assertClose("premultiply", org.apache.commons.math.linear.BigMatrixImplTest.asDouble(m.preMultiply(org.apache.commons.math.linear.BigMatrixImplTest.asBigDecimal(testVector))), preMultTest, normTolerance);
		m = new org.apache.commons.math.linear.BigMatrixImpl(bigSingular);
		try {
			m.preMultiply(org.apache.commons.math.linear.BigMatrixImplTest.asBigDecimal(testVector));
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testPremultiply() {
		org.apache.commons.math.linear.BigMatrix m3 = new org.apache.commons.math.linear.BigMatrixImpl(d3);
		org.apache.commons.math.linear.BigMatrix m4 = new org.apache.commons.math.linear.BigMatrixImpl(d4);
		org.apache.commons.math.linear.BigMatrix m5 = new org.apache.commons.math.linear.BigMatrixImpl(d5);
		assertClose("m3*m4=m5", m4.preMultiply(m3), m5, entryTolerance);
		org.apache.commons.math.linear.BigMatrixImpl m = new org.apache.commons.math.linear.BigMatrixImpl(testData);
		org.apache.commons.math.linear.BigMatrixImpl mInv = new org.apache.commons.math.linear.BigMatrixImpl(testDataInv);
		org.apache.commons.math.linear.BigMatrixImpl identity = new org.apache.commons.math.linear.BigMatrixImpl(id);
		new org.apache.commons.math.linear.BigMatrixImpl(testData2);
		assertClose("inverse multiply", m.preMultiply(mInv), identity, entryTolerance);
		assertClose("inverse multiply", mInv.preMultiply(m), identity, entryTolerance);
		assertClose("identity multiply", m.preMultiply(identity), m, entryTolerance);
		assertClose("identity multiply", identity.preMultiply(mInv), mInv, entryTolerance);
		try {
			m.preMultiply(new org.apache.commons.math.linear.BigMatrixImpl(bigSingular));
			junit.framework.Assert.fail("Expecting illegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testGetVectors() {
		org.apache.commons.math.linear.BigMatrix m = new org.apache.commons.math.linear.BigMatrixImpl(testData);
		assertClose("get row", m.getRowAsDoubleArray(0), testDataRow1, entryTolerance);
		assertClose("get col", m.getColumnAsDoubleArray(2), testDataCol3, entryTolerance);
		try {
			m.getRowAsDoubleArray(10);
			junit.framework.Assert.fail("expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
		try {
			m.getColumnAsDoubleArray(-1);
			junit.framework.Assert.fail("expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
	}

	public void testLUDecomposition() throws java.lang.Exception {
		org.apache.commons.math.linear.BigMatrixImpl m = new org.apache.commons.math.linear.BigMatrixImpl(testData);
		org.apache.commons.math.linear.BigMatrix lu = m.getLUMatrix();
		assertClose("LU decomposition", lu, new org.apache.commons.math.linear.BigMatrixImpl(testDataLU), normTolerance);
		verifyDecomposition(m, lu);
		m = new org.apache.commons.math.linear.BigMatrixImpl(luData);
		lu = m.getLUMatrix();
		assertClose("LU decomposition", lu, new org.apache.commons.math.linear.BigMatrixImpl(luDataLUDecomposition), normTolerance);
		verifyDecomposition(m, lu);
		m = new org.apache.commons.math.linear.BigMatrixImpl(testDataMinus);
		lu = m.getLUMatrix();
		verifyDecomposition(m, lu);
		m = new org.apache.commons.math.linear.BigMatrixImpl(id);
		lu = m.getLUMatrix();
		verifyDecomposition(m, lu);
		try {
			m = new org.apache.commons.math.linear.BigMatrixImpl(bigSingular);
			lu = m.getLUMatrix();
			junit.framework.Assert.fail("Expecting InvalidMatrixException");
		} catch (org.apache.commons.math.linear.InvalidMatrixException ex) {
		}
		try {
			m = new org.apache.commons.math.linear.BigMatrixImpl(testData2);
			lu = m.getLUMatrix();
			junit.framework.Assert.fail("Expecting InvalidMatrixException");
		} catch (org.apache.commons.math.linear.InvalidMatrixException ex) {
		}
	}

	public void testSubMatrix() {
		org.apache.commons.math.linear.BigMatrix m = new org.apache.commons.math.linear.BigMatrixImpl(subTestData);
		org.apache.commons.math.linear.BigMatrix mRows23Cols00 = new org.apache.commons.math.linear.BigMatrixImpl(subRows23Cols00);
		org.apache.commons.math.linear.BigMatrix mRows00Cols33 = new org.apache.commons.math.linear.BigMatrixImpl(subRows00Cols33);
		org.apache.commons.math.linear.BigMatrix mRows01Cols23 = new org.apache.commons.math.linear.BigMatrixImpl(subRows01Cols23);
		org.apache.commons.math.linear.BigMatrix mRows02Cols13 = new org.apache.commons.math.linear.BigMatrixImpl(subRows02Cols13);
		org.apache.commons.math.linear.BigMatrix mRows03Cols12 = new org.apache.commons.math.linear.BigMatrixImpl(subRows03Cols12);
		org.apache.commons.math.linear.BigMatrix mRows03Cols123 = new org.apache.commons.math.linear.BigMatrixImpl(subRows03Cols123);
		org.apache.commons.math.linear.BigMatrix mRows20Cols123 = new org.apache.commons.math.linear.BigMatrixImpl(subRows20Cols123);
		org.apache.commons.math.linear.BigMatrix mRows31Cols31 = new org.apache.commons.math.linear.BigMatrixImpl(subRows31Cols31);
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

	public void testGetColumnMatrix() {
		org.apache.commons.math.linear.BigMatrix m = new org.apache.commons.math.linear.BigMatrixImpl(subTestData);
		org.apache.commons.math.linear.BigMatrix mColumn1 = new org.apache.commons.math.linear.BigMatrixImpl(subColumn1);
		org.apache.commons.math.linear.BigMatrix mColumn3 = new org.apache.commons.math.linear.BigMatrixImpl(subColumn3);
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

	public void testGetRowMatrix() {
		org.apache.commons.math.linear.BigMatrix m = new org.apache.commons.math.linear.BigMatrixImpl(subTestData);
		org.apache.commons.math.linear.BigMatrix mRow0 = new org.apache.commons.math.linear.BigMatrixImpl(subRow0);
		org.apache.commons.math.linear.BigMatrix mRow3 = new org.apache.commons.math.linear.BigMatrixImpl(subRow3);
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

	public void testEqualsAndHashCode() {
		org.apache.commons.math.linear.BigMatrixImpl m = new org.apache.commons.math.linear.BigMatrixImpl(testData);
		org.apache.commons.math.linear.BigMatrixImpl m1 = ((org.apache.commons.math.linear.BigMatrixImpl)(m.copy()));
		org.apache.commons.math.linear.BigMatrixImpl mt = ((org.apache.commons.math.linear.BigMatrixImpl)(m.transpose()));
		junit.framework.Assert.assertTrue(((m.hashCode()) != (mt.hashCode())));
		junit.framework.Assert.assertEquals(m.hashCode(), m1.hashCode());
		junit.framework.Assert.assertEquals(m, m);
		junit.framework.Assert.assertEquals(m, m1);
		junit.framework.Assert.assertFalse(m.equals(null));
		junit.framework.Assert.assertFalse(m.equals(mt));
		junit.framework.Assert.assertFalse(m.equals(new org.apache.commons.math.linear.BigMatrixImpl(bigSingular)));
		m = new org.apache.commons.math.linear.BigMatrixImpl(new java.lang.String[][]{ new java.lang.String[]{ "2.0" } });
		m1 = new org.apache.commons.math.linear.BigMatrixImpl(new java.lang.String[][]{ new java.lang.String[]{ "2.00" } });
		junit.framework.Assert.assertTrue(((m.hashCode()) != (m1.hashCode())));
		junit.framework.Assert.assertFalse(m.equals(m1));
	}

	public void testToString() {
		org.apache.commons.math.linear.BigMatrixImpl m = new org.apache.commons.math.linear.BigMatrixImpl(testData);
		junit.framework.Assert.assertEquals("BigMatrixImpl{{1,2,3},{2,5,3},{1,0,8}}", m.toString());
		m = new org.apache.commons.math.linear.BigMatrixImpl();
		junit.framework.Assert.assertEquals("BigMatrixImpl{}", m.toString());
	}

	public void testSetSubMatrix() throws java.lang.Exception {
		java.math.BigDecimal[][] detData3 = org.apache.commons.math.linear.MatrixUtils.createBigMatrix(detData2).getData();
		org.apache.commons.math.linear.BigMatrixImpl m = new org.apache.commons.math.linear.BigMatrixImpl(testData);
		m.setSubMatrix(detData3, 1, 1);
		org.apache.commons.math.linear.BigMatrix expected = org.apache.commons.math.linear.MatrixUtils.createBigMatrix(new double[][]{ new double[]{ 1.0 , 2.0 , 3.0 } , new double[]{ 2.0 , 1.0 , 3.0 } , new double[]{ 1.0 , 2.0 , 4.0 } });
		junit.framework.Assert.assertEquals(expected, m);
		m.setSubMatrix(detData3, 0, 0);
		expected = org.apache.commons.math.linear.MatrixUtils.createBigMatrix(new double[][]{ new double[]{ 1.0 , 3.0 , 3.0 } , new double[]{ 2.0 , 4.0 , 3.0 } , new double[]{ 1.0 , 2.0 , 4.0 } });
		junit.framework.Assert.assertEquals(expected, m);
		java.math.BigDecimal[][] testDataPlus3 = org.apache.commons.math.linear.MatrixUtils.createBigMatrix(testDataPlus2).getData();
		m.setSubMatrix(testDataPlus3, 0, 0);
		expected = org.apache.commons.math.linear.MatrixUtils.createBigMatrix(new double[][]{ new double[]{ 3.0 , 4.0 , 5.0 } , new double[]{ 4.0 , 7.0 , 5.0 } , new double[]{ 3.0 , 2.0 , 10.0 } });
		junit.framework.Assert.assertEquals(expected, m);
		org.apache.commons.math.linear.BigMatrixImpl matrix = ((org.apache.commons.math.linear.BigMatrixImpl)(org.apache.commons.math.linear.MatrixUtils.createBigMatrix(new double[][]{ new double[]{ 1 , 2 , 3 , 4 } , new double[]{ 5 , 6 , 7 , 8 } , new double[]{ 9 , 0 , 1 , 2 } })));
		matrix.setSubMatrix(new java.math.BigDecimal[][]{ new java.math.BigDecimal[]{ new java.math.BigDecimal(3) , new java.math.BigDecimal(4) } , new java.math.BigDecimal[]{ new java.math.BigDecimal(5) , new java.math.BigDecimal(6) } }, 1, 1);
		expected = org.apache.commons.math.linear.MatrixUtils.createBigMatrix(new java.math.BigDecimal[][]{ new java.math.BigDecimal[]{ new java.math.BigDecimal(1) , new java.math.BigDecimal(2) , new java.math.BigDecimal(3) , new java.math.BigDecimal(4) } , new java.math.BigDecimal[]{ new java.math.BigDecimal(5) , new java.math.BigDecimal(3) , new java.math.BigDecimal(4) , new java.math.BigDecimal(8) } , new java.math.BigDecimal[]{ new java.math.BigDecimal(9) , new java.math.BigDecimal(5) , new java.math.BigDecimal(6) , new java.math.BigDecimal(2) } });
		junit.framework.Assert.assertEquals(expected, matrix);
		try {
			m.setSubMatrix(matrix.getData(), 1, 1);
			junit.framework.Assert.fail("expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException e) {
		}
		try {
			m.setSubMatrix(null, 1, 1);
			junit.framework.Assert.fail("expecting NullPointerException");
		} catch (java.lang.NullPointerException e) {
		}
		try {
			m.setSubMatrix(new java.math.BigDecimal[][]{ new java.math.BigDecimal[]{ new java.math.BigDecimal(1) } , new java.math.BigDecimal[]{ new java.math.BigDecimal(2) , new java.math.BigDecimal(3) } }, 0, 0);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
		}
		try {
			m.setSubMatrix(new java.math.BigDecimal[][]{ new java.math.BigDecimal[]{  } }, 0, 0);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
		}
	}

	protected void assertClose(java.lang.String msg, org.apache.commons.math.linear.BigMatrix m, org.apache.commons.math.linear.BigMatrix n, double tolerance) {
		junit.framework.Assert.assertTrue(msg, ((m.subtract(n).getNorm().doubleValue()) < tolerance));
	}

	protected void assertClose(java.lang.String msg, double[] m, double[] n, double tolerance) {
		if ((m.length) != (n.length)) {
			junit.framework.Assert.fail("vectors not same length");
		} 
		for (int i = 0 ; i < (m.length) ; i++) {
			junit.framework.Assert.assertEquals((((msg + " ") + i) + " elements differ"), m[i], n[i], tolerance);
		}
	}

	protected void splitLU(org.apache.commons.math.linear.BigMatrix lu, java.math.BigDecimal[][] lowerData, java.math.BigDecimal[][] upperData) throws org.apache.commons.math.linear.InvalidMatrixException {
		if (((((!(lu.isSquare())) || ((lowerData.length) != (lowerData[0].length))) || ((upperData.length) != (upperData[0].length))) || ((lowerData.length) != (upperData.length))) || ((lowerData.length) != (lu.getRowDimension()))) {
			throw new org.apache.commons.math.linear.InvalidMatrixException("incorrect dimensions");
		} 
		int n = lu.getRowDimension();
		for (int i = 0 ; i < n ; i++) {
			for (int j = 0 ; j < n ; j++) {
				if (j < i) {
					lowerData[i][j] = lu.getEntry(i, j);
					upperData[i][j] = new java.math.BigDecimal(0);
				} else if (i == j) {
					lowerData[i][j] = new java.math.BigDecimal(1);
					upperData[i][j] = lu.getEntry(i, j);
				} else {
					lowerData[i][j] = new java.math.BigDecimal(0);
					upperData[i][j] = lu.getEntry(i, j);
				}
			}
		}
	}

	protected org.apache.commons.math.linear.BigMatrix permuteRows(org.apache.commons.math.linear.BigMatrix matrix, int[] permutation) {
		if ((!(matrix.isSquare())) || ((matrix.getRowDimension()) != (permutation.length))) {
			throw new java.lang.IllegalArgumentException("dimension mismatch");
		} 
		int n = matrix.getRowDimension();
		int m = matrix.getColumnDimension();
		java.math.BigDecimal[][] out = new java.math.BigDecimal[m][n];
		for (int i = 0 ; i < n ; i++) {
			for (int j = 0 ; j < m ; j++) {
				out[i][j] = matrix.getEntry(permutation[i], j);
			}
		}
		return new org.apache.commons.math.linear.BigMatrixImpl(out);
	}

	protected void verifyDecomposition(org.apache.commons.math.linear.BigMatrix matrix, org.apache.commons.math.linear.BigMatrix lu) throws java.lang.Exception {
		int n = matrix.getRowDimension();
		java.math.BigDecimal[][] lowerData = new java.math.BigDecimal[n][n];
		java.math.BigDecimal[][] upperData = new java.math.BigDecimal[n][n];
		splitLU(lu, lowerData, upperData);
		org.apache.commons.math.linear.BigMatrix lower = new org.apache.commons.math.linear.BigMatrixImpl(lowerData);
		org.apache.commons.math.linear.BigMatrix upper = new org.apache.commons.math.linear.BigMatrixImpl(upperData);
		int[] permutation = ((org.apache.commons.math.linear.BigMatrixImpl)(matrix)).getPermutation();
		org.apache.commons.math.linear.BigMatrix permuted = permuteRows(matrix, permutation);
		assertClose("lu decomposition does not work", permuted, lower.multiply(upper), normTolerance);
	}
}

