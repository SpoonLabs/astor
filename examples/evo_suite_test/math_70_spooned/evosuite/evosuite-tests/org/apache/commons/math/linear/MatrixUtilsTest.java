package org.apache.commons.math.linear;


public final class MatrixUtilsTest extends junit.framework.TestCase {
	protected double[][] testData = new double[][]{ new double[]{ 1.0 , 2.0 , 3.0 } , new double[]{ 2.0 , 5.0 , 3.0 } , new double[]{ 1.0 , 0.0 , 8.0 } };

	protected double[][] nullMatrix = null;

	protected double[] row = new double[]{ 1 , 2 , 3 };

	protected java.math.BigDecimal[] bigRow = new java.math.BigDecimal[]{ new java.math.BigDecimal(1) , new java.math.BigDecimal(2) , new java.math.BigDecimal(3) };

	protected java.lang.String[] stringRow = new java.lang.String[]{ "1" , "2" , "3" };

	protected org.apache.commons.math.fraction.Fraction[] fractionRow = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) };

	protected double[][] rowMatrix = new double[][]{ new double[]{ 1 , 2 , 3 } };

	protected java.math.BigDecimal[][] bigRowMatrix = new java.math.BigDecimal[][]{ new java.math.BigDecimal[]{ new java.math.BigDecimal(1) , new java.math.BigDecimal(2) , new java.math.BigDecimal(3) } };

	protected java.lang.String[][] stringRowMatrix = new java.lang.String[][]{ new java.lang.String[]{ "1" , "2" , "3" } };

	protected org.apache.commons.math.fraction.Fraction[][] fractionRowMatrix = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) } };

	protected double[] col = new double[]{ 0 , 4 , 6 };

	protected java.math.BigDecimal[] bigCol = new java.math.BigDecimal[]{ new java.math.BigDecimal(0) , new java.math.BigDecimal(4) , new java.math.BigDecimal(6) };

	protected java.lang.String[] stringCol = new java.lang.String[]{ "0" , "4" , "6" };

	protected org.apache.commons.math.fraction.Fraction[] fractionCol = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(6) };

	protected double[] nullDoubleArray = null;

	protected double[][] colMatrix = new double[][]{ new double[]{ 0 } , new double[]{ 4 } , new double[]{ 6 } };

	protected java.math.BigDecimal[][] bigColMatrix = new java.math.BigDecimal[][]{ new java.math.BigDecimal[]{ new java.math.BigDecimal(0) } , new java.math.BigDecimal[]{ new java.math.BigDecimal(4) } , new java.math.BigDecimal[]{ new java.math.BigDecimal(6) } };

	protected java.lang.String[][] stringColMatrix = new java.lang.String[][]{ new java.lang.String[]{ "0" } , new java.lang.String[]{ "4" } , new java.lang.String[]{ "6" } };

	protected org.apache.commons.math.fraction.Fraction[][] fractionColMatrix = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(6) } };

	public MatrixUtilsTest(java.lang.String name) {
		super(name);
	}

	public void testCreateRealMatrix() {
		junit.framework.Assert.assertEquals(new org.apache.commons.math.linear.BlockRealMatrix(testData), org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData));
		try {
			org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 1 } , new double[]{ 1 , 2 } });
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{  } , new double[]{  } });
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.linear.MatrixUtils.createRealMatrix(null);
			junit.framework.Assert.fail("Expecting NullPointerException");
		} catch (java.lang.NullPointerException ex) {
		}
	}

	public void testcreateFieldMatrix() {
		junit.framework.Assert.assertEquals(new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(org.apache.commons.math.linear.MatrixUtilsTest.asFraction(testData)), org.apache.commons.math.linear.MatrixUtils.createFieldMatrix(org.apache.commons.math.linear.MatrixUtilsTest.asFraction(testData)));
		junit.framework.Assert.assertEquals(new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(fractionColMatrix), org.apache.commons.math.linear.MatrixUtils.createFieldMatrix(fractionColMatrix));
		try {
			org.apache.commons.math.linear.MatrixUtils.createFieldMatrix(org.apache.commons.math.linear.MatrixUtilsTest.asFraction(new double[][]{ new double[]{ 1 } , new double[]{ 1 , 2 } }));
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.linear.MatrixUtils.createFieldMatrix(org.apache.commons.math.linear.MatrixUtilsTest.asFraction(new double[][]{ new double[]{  } , new double[]{  } }));
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.linear.MatrixUtils.createFieldMatrix(((org.apache.commons.math.fraction.Fraction[][])(null)));
			junit.framework.Assert.fail("Expecting NullPointerException");
		} catch (java.lang.NullPointerException ex) {
		}
	}

	@java.lang.Deprecated
	public void testCreateBigMatrix() {
		junit.framework.Assert.assertEquals(new org.apache.commons.math.linear.BigMatrixImpl(testData), org.apache.commons.math.linear.MatrixUtils.createBigMatrix(testData));
		junit.framework.Assert.assertEquals(new org.apache.commons.math.linear.BigMatrixImpl(org.apache.commons.math.linear.BigMatrixImplTest.asBigDecimal(testData) , true), org.apache.commons.math.linear.MatrixUtils.createBigMatrix(org.apache.commons.math.linear.BigMatrixImplTest.asBigDecimal(testData), false));
		junit.framework.Assert.assertEquals(new org.apache.commons.math.linear.BigMatrixImpl(org.apache.commons.math.linear.BigMatrixImplTest.asBigDecimal(testData) , false), org.apache.commons.math.linear.MatrixUtils.createBigMatrix(org.apache.commons.math.linear.BigMatrixImplTest.asBigDecimal(testData), true));
		junit.framework.Assert.assertEquals(new org.apache.commons.math.linear.BigMatrixImpl(bigColMatrix), org.apache.commons.math.linear.MatrixUtils.createBigMatrix(bigColMatrix));
		junit.framework.Assert.assertEquals(new org.apache.commons.math.linear.BigMatrixImpl(stringColMatrix), org.apache.commons.math.linear.MatrixUtils.createBigMatrix(stringColMatrix));
		try {
			org.apache.commons.math.linear.MatrixUtils.createBigMatrix(new double[][]{ new double[]{ 1 } , new double[]{ 1 , 2 } });
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.linear.MatrixUtils.createBigMatrix(new double[][]{ new double[]{  } , new double[]{  } });
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.linear.MatrixUtils.createBigMatrix(nullMatrix);
			junit.framework.Assert.fail("Expecting NullPointerException");
		} catch (java.lang.NullPointerException ex) {
		}
	}

	public void testCreateRowRealMatrix() {
		junit.framework.Assert.assertEquals(org.apache.commons.math.linear.MatrixUtils.createRowRealMatrix(row), new org.apache.commons.math.linear.BlockRealMatrix(rowMatrix));
		try {
			org.apache.commons.math.linear.MatrixUtils.createRowRealMatrix(new double[]{  });
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.linear.MatrixUtils.createRowRealMatrix(null);
			junit.framework.Assert.fail("Expecting NullPointerException");
		} catch (java.lang.NullPointerException ex) {
		}
	}

	public void testCreateRowFieldMatrix() {
		junit.framework.Assert.assertEquals(org.apache.commons.math.linear.MatrixUtils.createRowFieldMatrix(org.apache.commons.math.linear.MatrixUtilsTest.asFraction(row)), new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(org.apache.commons.math.linear.MatrixUtilsTest.asFraction(rowMatrix)));
		junit.framework.Assert.assertEquals(org.apache.commons.math.linear.MatrixUtils.createRowFieldMatrix(fractionRow), new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(fractionRowMatrix));
		try {
			org.apache.commons.math.linear.MatrixUtils.createRowFieldMatrix(new org.apache.commons.math.fraction.Fraction[]{  });
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.linear.MatrixUtils.createRowFieldMatrix(((org.apache.commons.math.fraction.Fraction[])(null)));
			junit.framework.Assert.fail("Expecting NullPointerException");
		} catch (java.lang.NullPointerException ex) {
		}
	}

	@java.lang.Deprecated
	public void testCreateRowBigMatrix() {
		junit.framework.Assert.assertEquals(org.apache.commons.math.linear.MatrixUtils.createRowBigMatrix(row), new org.apache.commons.math.linear.BigMatrixImpl(rowMatrix));
		junit.framework.Assert.assertEquals(org.apache.commons.math.linear.MatrixUtils.createRowBigMatrix(bigRow), new org.apache.commons.math.linear.BigMatrixImpl(bigRowMatrix));
		junit.framework.Assert.assertEquals(org.apache.commons.math.linear.MatrixUtils.createRowBigMatrix(stringRow), new org.apache.commons.math.linear.BigMatrixImpl(stringRowMatrix));
		try {
			org.apache.commons.math.linear.MatrixUtils.createRowBigMatrix(new double[]{  });
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.linear.MatrixUtils.createRowBigMatrix(nullDoubleArray);
			junit.framework.Assert.fail("Expecting NullPointerException");
		} catch (java.lang.NullPointerException ex) {
		}
	}

	public void testCreateColumnRealMatrix() {
		junit.framework.Assert.assertEquals(org.apache.commons.math.linear.MatrixUtils.createColumnRealMatrix(col), new org.apache.commons.math.linear.BlockRealMatrix(colMatrix));
		try {
			org.apache.commons.math.linear.MatrixUtils.createColumnRealMatrix(new double[]{  });
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.linear.MatrixUtils.createColumnRealMatrix(null);
			junit.framework.Assert.fail("Expecting NullPointerException");
		} catch (java.lang.NullPointerException ex) {
		}
	}

	public void testCreateColumnFieldMatrix() {
		junit.framework.Assert.assertEquals(org.apache.commons.math.linear.MatrixUtils.createColumnFieldMatrix(org.apache.commons.math.linear.MatrixUtilsTest.asFraction(col)), new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(org.apache.commons.math.linear.MatrixUtilsTest.asFraction(colMatrix)));
		junit.framework.Assert.assertEquals(org.apache.commons.math.linear.MatrixUtils.createColumnFieldMatrix(fractionCol), new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(fractionColMatrix));
		try {
			org.apache.commons.math.linear.MatrixUtils.createColumnFieldMatrix(new org.apache.commons.math.fraction.Fraction[]{  });
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.linear.MatrixUtils.createColumnFieldMatrix(((org.apache.commons.math.fraction.Fraction[])(null)));
			junit.framework.Assert.fail("Expecting NullPointerException");
		} catch (java.lang.NullPointerException ex) {
		}
	}

	@java.lang.Deprecated
	public void testCreateColumnBigMatrix() {
		junit.framework.Assert.assertEquals(org.apache.commons.math.linear.MatrixUtils.createColumnBigMatrix(col), new org.apache.commons.math.linear.BigMatrixImpl(colMatrix));
		junit.framework.Assert.assertEquals(org.apache.commons.math.linear.MatrixUtils.createColumnBigMatrix(bigCol), new org.apache.commons.math.linear.BigMatrixImpl(bigColMatrix));
		junit.framework.Assert.assertEquals(org.apache.commons.math.linear.MatrixUtils.createColumnBigMatrix(stringCol), new org.apache.commons.math.linear.BigMatrixImpl(stringColMatrix));
		try {
			org.apache.commons.math.linear.MatrixUtils.createColumnBigMatrix(new double[]{  });
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			org.apache.commons.math.linear.MatrixUtils.createColumnBigMatrix(nullDoubleArray);
			junit.framework.Assert.fail("Expecting NullPointerException");
		} catch (java.lang.NullPointerException ex) {
		}
	}

	protected void checkIdentityMatrix(org.apache.commons.math.linear.RealMatrix m) {
		for (int i = 0 ; i < (m.getRowDimension()) ; i++) {
			for (int j = 0 ; j < (m.getColumnDimension()) ; j++) {
				if (i == j) {
					junit.framework.Assert.assertEquals(m.getEntry(i, j), 1.0, 0);
				} else {
					junit.framework.Assert.assertEquals(m.getEntry(i, j), 0.0, 0);
				}
			}
		}
	}

	public void testCreateIdentityMatrix() {
		checkIdentityMatrix(org.apache.commons.math.linear.MatrixUtils.createRealIdentityMatrix(3));
		checkIdentityMatrix(org.apache.commons.math.linear.MatrixUtils.createRealIdentityMatrix(2));
		checkIdentityMatrix(org.apache.commons.math.linear.MatrixUtils.createRealIdentityMatrix(1));
		try {
			org.apache.commons.math.linear.MatrixUtils.createRealIdentityMatrix(0);
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	protected void checkIdentityFieldMatrix(org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m) {
		for (int i = 0 ; i < (m.getRowDimension()) ; i++) {
			for (int j = 0 ; j < (m.getColumnDimension()) ; j++) {
				if (i == j) {
					junit.framework.Assert.assertEquals(m.getEntry(i, j), org.apache.commons.math.fraction.Fraction.ONE);
				} else {
					junit.framework.Assert.assertEquals(m.getEntry(i, j), org.apache.commons.math.fraction.Fraction.ZERO);
				}
			}
		}
	}

	public void testcreateFieldIdentityMatrix() {
		checkIdentityFieldMatrix(org.apache.commons.math.linear.MatrixUtils.createFieldIdentityMatrix(org.apache.commons.math.fraction.FractionField.getInstance(), 3));
		checkIdentityFieldMatrix(org.apache.commons.math.linear.MatrixUtils.createFieldIdentityMatrix(org.apache.commons.math.fraction.FractionField.getInstance(), 2));
		checkIdentityFieldMatrix(org.apache.commons.math.linear.MatrixUtils.createFieldIdentityMatrix(org.apache.commons.math.fraction.FractionField.getInstance(), 1));
		try {
			org.apache.commons.math.linear.MatrixUtils.createRealIdentityMatrix(0);
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testBigFractionConverter() {
		org.apache.commons.math.fraction.BigFraction[][] bfData = new org.apache.commons.math.fraction.BigFraction[][]{ new org.apache.commons.math.fraction.BigFraction[]{ new org.apache.commons.math.fraction.BigFraction(1) , new org.apache.commons.math.fraction.BigFraction(2) , new org.apache.commons.math.fraction.BigFraction(3) } , new org.apache.commons.math.fraction.BigFraction[]{ new org.apache.commons.math.fraction.BigFraction(2) , new org.apache.commons.math.fraction.BigFraction(5) , new org.apache.commons.math.fraction.BigFraction(3) } , new org.apache.commons.math.fraction.BigFraction[]{ new org.apache.commons.math.fraction.BigFraction(1) , new org.apache.commons.math.fraction.BigFraction(0) , new org.apache.commons.math.fraction.BigFraction(8) } };
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.BigFraction> m = new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.BigFraction>(bfData , false);
		org.apache.commons.math.linear.RealMatrix converted = org.apache.commons.math.linear.MatrixUtils.bigFractionMatrixToRealMatrix(m);
		org.apache.commons.math.linear.RealMatrix reference = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData , false);
		junit.framework.Assert.assertEquals(0.0, converted.subtract(reference).getNorm(), 0.0);
	}

	public void testFractionConverter() {
		org.apache.commons.math.fraction.Fraction[][] fData = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(8) } };
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m = new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(fData , false);
		org.apache.commons.math.linear.RealMatrix converted = org.apache.commons.math.linear.MatrixUtils.fractionMatrixToRealMatrix(m);
		org.apache.commons.math.linear.RealMatrix reference = new org.apache.commons.math.linear.Array2DRowRealMatrix(testData , false);
		junit.framework.Assert.assertEquals(0.0, converted.subtract(reference).getNorm(), 0.0);
	}

	public static final org.apache.commons.math.fraction.Fraction[][] asFraction(double[][] data) {
		org.apache.commons.math.fraction.Fraction[][] d = new org.apache.commons.math.fraction.Fraction[data.length][];
		try {
			for (int i = 0 ; i < (data.length) ; ++i) {
				double[] dataI = data[i];
				org.apache.commons.math.fraction.Fraction[] dI = new org.apache.commons.math.fraction.Fraction[dataI.length];
				for (int j = 0 ; j < (dataI.length) ; ++j) {
					dI[j] = new org.apache.commons.math.fraction.Fraction(dataI[j]);
				}
				d[i] = dI;
			}
		} catch (org.apache.commons.math.fraction.FractionConversionException fce) {
			junit.framework.Assert.fail(fce.getMessage());
		}
		return d;
	}

	public static final org.apache.commons.math.fraction.Fraction[] asFraction(double[] data) {
		org.apache.commons.math.fraction.Fraction[] d = new org.apache.commons.math.fraction.Fraction[data.length];
		try {
			for (int i = 0 ; i < (data.length) ; ++i) {
				d[i] = new org.apache.commons.math.fraction.Fraction(data[i]);
			}
		} catch (org.apache.commons.math.fraction.FractionConversionException fce) {
			junit.framework.Assert.fail(fce.getMessage());
		}
		return d;
	}

	@java.lang.Deprecated
	protected void checkIdentityBigMatrix(org.apache.commons.math.linear.BigMatrix m) {
		for (int i = 0 ; i < (m.getRowDimension()) ; i++) {
			for (int j = 0 ; j < (m.getColumnDimension()) ; j++) {
				if (i == j) {
					junit.framework.Assert.assertEquals(m.getEntry(i, j), org.apache.commons.math.linear.BigMatrixImpl.ONE);
				} else {
					junit.framework.Assert.assertEquals(m.getEntry(i, j), org.apache.commons.math.linear.BigMatrixImpl.ZERO);
				}
			}
		}
	}

	@java.lang.Deprecated
	public void testCreateBigIdentityMatrix() {
		checkIdentityBigMatrix(org.apache.commons.math.linear.MatrixUtils.createBigIdentityMatrix(3));
		checkIdentityBigMatrix(org.apache.commons.math.linear.MatrixUtils.createBigIdentityMatrix(2));
		checkIdentityBigMatrix(org.apache.commons.math.linear.MatrixUtils.createBigIdentityMatrix(1));
		try {
			org.apache.commons.math.linear.MatrixUtils.createRealIdentityMatrix(0);
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}
}

