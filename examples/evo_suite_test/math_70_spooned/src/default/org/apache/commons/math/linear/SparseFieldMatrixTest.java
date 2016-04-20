package org.apache.commons.math.linear;


public class SparseFieldMatrixTest extends junit.framework.TestCase {
	protected org.apache.commons.math.fraction.Fraction[][] id = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(0) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(0) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(1) } };

	protected org.apache.commons.math.fraction.Fraction[][] testData = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(8) } };

	protected org.apache.commons.math.fraction.Fraction[][] testDataLU = null;

	protected org.apache.commons.math.fraction.Fraction[][] testDataPlus2 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(5) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(5) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(10) } };

	protected org.apache.commons.math.fraction.Fraction[][] testDataMinus = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(-1) , new org.apache.commons.math.fraction.Fraction(-2) , new org.apache.commons.math.fraction.Fraction(-3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(-2) , new org.apache.commons.math.fraction.Fraction(-5) , new org.apache.commons.math.fraction.Fraction(-3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(-1) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(-8) } };

	protected org.apache.commons.math.fraction.Fraction[] testDataRow1 = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) };

	protected org.apache.commons.math.fraction.Fraction[] testDataCol3 = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(8) };

	protected org.apache.commons.math.fraction.Fraction[][] testDataInv = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(-40) , new org.apache.commons.math.fraction.Fraction(16) , new org.apache.commons.math.fraction.Fraction(9) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(13) , new org.apache.commons.math.fraction.Fraction(-5) , new org.apache.commons.math.fraction.Fraction(-3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(-2) , new org.apache.commons.math.fraction.Fraction(-1) } };

	protected org.apache.commons.math.fraction.Fraction[] preMultTest = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(8) , new org.apache.commons.math.fraction.Fraction(12) , new org.apache.commons.math.fraction.Fraction(33) };

	protected org.apache.commons.math.fraction.Fraction[][] testData2 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(3) } };

	protected org.apache.commons.math.fraction.Fraction[][] testData2T = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(5) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(3) } };

	protected org.apache.commons.math.fraction.Fraction[][] testDataPlusInv = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(-39) , new org.apache.commons.math.fraction.Fraction(18) , new org.apache.commons.math.fraction.Fraction(12) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(15) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(0) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(6) , new org.apache.commons.math.fraction.Fraction(-2) , new org.apache.commons.math.fraction.Fraction(7) } };

	protected org.apache.commons.math.fraction.Fraction[][] luData = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(7) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(6) , new org.apache.commons.math.fraction.Fraction(9) , new org.apache.commons.math.fraction.Fraction(8) } };

	protected org.apache.commons.math.fraction.Fraction[][] luDataLUDecomposition = null;

	protected org.apache.commons.math.fraction.Fraction[][] singular = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) } };

	protected org.apache.commons.math.fraction.Fraction[][] bigSingular = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(256) , new org.apache.commons.math.fraction.Fraction(1930) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(6) , new org.apache.commons.math.fraction.Fraction(8) } };

	protected org.apache.commons.math.fraction.Fraction[][] detData = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(6) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(8) , new org.apache.commons.math.fraction.Fraction(10) } };

	protected org.apache.commons.math.fraction.Fraction[][] detData2 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(4) } };

	protected org.apache.commons.math.fraction.Fraction[] testVector = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) };

	protected org.apache.commons.math.fraction.Fraction[] testVector2 = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) };

	protected org.apache.commons.math.fraction.Fraction[][] subTestData = null;

	protected org.apache.commons.math.fraction.Fraction[][] subRows02Cols13 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(4) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(8) } };

	protected org.apache.commons.math.fraction.Fraction[][] subRows03Cols12 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(6) } };

	protected org.apache.commons.math.fraction.Fraction[][] subRows03Cols123 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(6) , new org.apache.commons.math.fraction.Fraction(7) } };

	protected org.apache.commons.math.fraction.Fraction[][] subRows20Cols123 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(6) , new org.apache.commons.math.fraction.Fraction(8) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) } };

	protected org.apache.commons.math.fraction.Fraction[][] subRows31Cols31 = null;

	protected org.apache.commons.math.fraction.Fraction[][] subRows01Cols23 = null;

	protected org.apache.commons.math.fraction.Fraction[][] subRows23Cols00 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) } };

	protected org.apache.commons.math.fraction.Fraction[][] subRows00Cols33 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) } };

	protected org.apache.commons.math.fraction.Fraction[][] subRow0 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) } };

	protected org.apache.commons.math.fraction.Fraction[][] subRow3 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(6) , new org.apache.commons.math.fraction.Fraction(7) } };

	protected org.apache.commons.math.fraction.Fraction[][] subColumn1 = null;

	protected org.apache.commons.math.fraction.Fraction[][] subColumn3 = null;

	protected double entryTolerance = 1.0E-15;

	protected double normTolerance = 1.0E-13;

	protected org.apache.commons.math.Field<org.apache.commons.math.fraction.Fraction> field = org.apache.commons.math.fraction.FractionField.getInstance();

	public SparseFieldMatrixTest(java.lang.String name) {
		super(name);
		setupFractionArrays();
	}

	private void setupFractionArrays() {
		try {
			testDataLU = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0.5) , new org.apache.commons.math.fraction.Fraction(-2.5) , new org.apache.commons.math.fraction.Fraction(6.5) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0.5) , new org.apache.commons.math.fraction.Fraction(0.2) , new org.apache.commons.math.fraction.Fraction(0.2) } };
			luDataLUDecomposition = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(6) , new org.apache.commons.math.fraction.Fraction(9) , new org.apache.commons.math.fraction.Fraction(8) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(7) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0.33333333333333) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(0.33333333333333) } };
			subTestData = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1.5) , new org.apache.commons.math.fraction.Fraction(2.5) , new org.apache.commons.math.fraction.Fraction(3.5) , new org.apache.commons.math.fraction.Fraction(4.5) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(6) , new org.apache.commons.math.fraction.Fraction(8) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(6) , new org.apache.commons.math.fraction.Fraction(7) } };
			subRows31Cols31 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(5) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4.5) , new org.apache.commons.math.fraction.Fraction(2.5) } };
			subRows01Cols23 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(3.5) , new org.apache.commons.math.fraction.Fraction(4.5) } };
			subColumn1 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2.5) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(5) } };
			subColumn3 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4.5) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(8) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(7) } };
		} catch (org.apache.commons.math.fraction.FractionConversionException e) {
		}
	}

	public void testDimensions() {
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> m = createSparseMatrix(testData);
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> m2 = createSparseMatrix(testData2);
		junit.framework.Assert.assertEquals("testData row dimension", 3, m.getRowDimension());
		junit.framework.Assert.assertEquals("testData column dimension", 3, m.getColumnDimension());
		junit.framework.Assert.assertTrue("testData is square", m.isSquare());
		junit.framework.Assert.assertEquals("testData2 row dimension", m2.getRowDimension(), 2);
		junit.framework.Assert.assertEquals("testData2 column dimension", m2.getColumnDimension(), 3);
		junit.framework.Assert.assertTrue("testData2 is not square", !(m2.isSquare()));
	}

	public void testCopyFunctions() {
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> m1 = createSparseMatrix(testData);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m2 = m1.copy();
		junit.framework.Assert.assertEquals(m1.getClass(), m2.getClass());
		junit.framework.Assert.assertEquals(m2, m1);
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> m3 = createSparseMatrix(testData);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m4 = m3.copy();
		junit.framework.Assert.assertEquals(m3.getClass(), m4.getClass());
		junit.framework.Assert.assertEquals(m4, m3);
	}

	public void testAdd() {
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> m = createSparseMatrix(testData);
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> mInv = createSparseMatrix(testDataInv);
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> mDataPlusInv = createSparseMatrix(testDataPlusInv);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> mPlusMInv = m.add(mInv);
		for (int row = 0 ; row < (m.getRowDimension()) ; row++) {
			for (int col = 0 ; col < (m.getColumnDimension()) ; col++) {
				junit.framework.Assert.assertEquals("sum entry entry", mDataPlusInv.getEntry(row, col).doubleValue(), mPlusMInv.getEntry(row, col).doubleValue(), entryTolerance);
			}
		}
	}

	public void testAddFail() {
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> m = createSparseMatrix(testData);
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> m2 = createSparseMatrix(testData2);
		try {
			m.add(m2);
			junit.framework.Assert.fail("IllegalArgumentException expected");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testPlusMinus() {
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> m = createSparseMatrix(testData);
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> n = createSparseMatrix(testDataInv);
		assertClose("m-n = m + -n", m.subtract(n), n.scalarMultiply(new org.apache.commons.math.fraction.Fraction(-1)).add(m), entryTolerance);
		try {
			m.subtract(createSparseMatrix(testData2));
			junit.framework.Assert.fail("Expecting illegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testMultiply() {
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> m = createSparseMatrix(testData);
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> mInv = createSparseMatrix(testDataInv);
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> identity = createSparseMatrix(id);
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> m2 = createSparseMatrix(testData2);
		assertClose("inverse multiply", m.multiply(mInv), identity, entryTolerance);
		assertClose("inverse multiply", m.multiply(new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(testDataInv)), identity, entryTolerance);
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

	private org.apache.commons.math.fraction.Fraction[][] d3 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(6) , new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(8) } };

	private org.apache.commons.math.fraction.Fraction[][] d4 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) } };

	private org.apache.commons.math.fraction.Fraction[][] d5 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(30) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(70) } };

	public void testMultiply2() {
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m3 = createSparseMatrix(d3);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m4 = createSparseMatrix(d4);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m5 = createSparseMatrix(d5);
		assertClose("m3*m4=m5", m3.multiply(m4), m5, entryTolerance);
	}

	public void testTrace() {
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m = createSparseMatrix(id);
		junit.framework.Assert.assertEquals("identity trace", 3.0, m.getTrace().doubleValue(), entryTolerance);
		m = createSparseMatrix(testData2);
		try {
			m.getTrace();
			junit.framework.Assert.fail("Expecting NonSquareMatrixException");
		} catch (org.apache.commons.math.linear.NonSquareMatrixException ex) {
		}
	}

	public void testScalarAdd() {
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m = createSparseMatrix(testData);
		assertClose("scalar add", createSparseMatrix(testDataPlus2), m.scalarAdd(new org.apache.commons.math.fraction.Fraction(2)), entryTolerance);
	}

	public void testOperate() {
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m = createSparseMatrix(id);
		assertClose("identity operate", testVector, m.operate(testVector), entryTolerance);
		assertClose("identity operate", testVector, m.operate(new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(testVector)).getData(), entryTolerance);
		m = createSparseMatrix(bigSingular);
		try {
			m.operate(testVector);
			junit.framework.Assert.fail("Expecting illegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testMath209() {
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> a = createSparseMatrix(new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(6) } });
		org.apache.commons.math.fraction.Fraction[] b = a.operate(new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(1) });
		junit.framework.Assert.assertEquals(a.getRowDimension(), b.length);
		junit.framework.Assert.assertEquals(3.0, b[0].doubleValue(), 1.0E-12);
		junit.framework.Assert.assertEquals(7.0, b[1].doubleValue(), 1.0E-12);
		junit.framework.Assert.assertEquals(11.0, b[2].doubleValue(), 1.0E-12);
	}

	public void testTranspose() {
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m = createSparseMatrix(testData);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> mIT = new org.apache.commons.math.linear.FieldLUDecompositionImpl<org.apache.commons.math.fraction.Fraction>(m).getSolver().getInverse().transpose();
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> mTI = new org.apache.commons.math.linear.FieldLUDecompositionImpl<org.apache.commons.math.fraction.Fraction>(m.transpose()).getSolver().getInverse();
		assertClose("inverse-transpose", mIT, mTI, normTolerance);
		m = createSparseMatrix(testData2);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> mt = createSparseMatrix(testData2T);
		assertClose("transpose", mt, m.transpose(), normTolerance);
	}

	public void testPremultiplyVector() {
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m = createSparseMatrix(testData);
		assertClose("premultiply", m.preMultiply(testVector), preMultTest, normTolerance);
		assertClose("premultiply", m.preMultiply(new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(testVector).getData()), preMultTest, normTolerance);
		m = createSparseMatrix(bigSingular);
		try {
			m.preMultiply(testVector);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testPremultiply() {
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m3 = createSparseMatrix(d3);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m4 = createSparseMatrix(d4);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m5 = createSparseMatrix(d5);
		assertClose("m3*m4=m5", m4.preMultiply(m3), m5, entryTolerance);
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> m = createSparseMatrix(testData);
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> mInv = createSparseMatrix(testDataInv);
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> identity = createSparseMatrix(id);
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
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m = createSparseMatrix(testData);
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
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m = createSparseMatrix(testData);
		junit.framework.Assert.assertEquals("get entry", m.getEntry(0, 1).doubleValue(), 2.0, entryTolerance);
		try {
			m.getEntry(10, 4);
			junit.framework.Assert.fail("Expecting MatrixIndexException");
		} catch (org.apache.commons.math.linear.MatrixIndexException ex) {
		}
	}

	public void testExamples() {
		org.apache.commons.math.fraction.Fraction[][] matrixData = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(3) } };
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m = createSparseMatrix(matrixData);
		org.apache.commons.math.fraction.Fraction[][] matrixData2 = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(5) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(7) } };
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> n = createSparseMatrix(matrixData2);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> p = m.multiply(n);
		junit.framework.Assert.assertEquals(2, p.getRowDimension());
		junit.framework.Assert.assertEquals(2, p.getColumnDimension());
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> pInverse = new org.apache.commons.math.linear.FieldLUDecompositionImpl<org.apache.commons.math.fraction.Fraction>(p).getSolver().getInverse();
		junit.framework.Assert.assertEquals(2, pInverse.getRowDimension());
		junit.framework.Assert.assertEquals(2, pInverse.getColumnDimension());
		org.apache.commons.math.fraction.Fraction[][] coefficientsData = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(-2) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(-1) , new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(6) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(-3) , new org.apache.commons.math.fraction.Fraction(-5) } };
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> coefficients = createSparseMatrix(coefficientsData);
		org.apache.commons.math.fraction.Fraction[] constants = new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(-2) , new org.apache.commons.math.fraction.Fraction(1) };
		org.apache.commons.math.fraction.Fraction[] solution = new org.apache.commons.math.linear.FieldLUDecompositionImpl<org.apache.commons.math.fraction.Fraction>(coefficients).getSolver().solve(constants);
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(2).multiply(solution[0]).add(new org.apache.commons.math.fraction.Fraction(3).multiply(solution[1])).subtract(new org.apache.commons.math.fraction.Fraction(2).multiply(solution[2])).doubleValue(), constants[0].doubleValue(), 1.0E-12);
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(-1).multiply(solution[0]).add(new org.apache.commons.math.fraction.Fraction(7).multiply(solution[1])).add(new org.apache.commons.math.fraction.Fraction(6).multiply(solution[2])).doubleValue(), constants[1].doubleValue(), 1.0E-12);
		junit.framework.Assert.assertEquals(new org.apache.commons.math.fraction.Fraction(4).multiply(solution[0]).subtract(new org.apache.commons.math.fraction.Fraction(3).multiply(solution[1])).subtract(new org.apache.commons.math.fraction.Fraction(5).multiply(solution[2])).doubleValue(), constants[2].doubleValue(), 1.0E-12);
	}

	public void testSubMatrix() {
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m = createSparseMatrix(subTestData);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> mRows23Cols00 = createSparseMatrix(subRows23Cols00);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> mRows00Cols33 = createSparseMatrix(subRows00Cols33);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> mRows01Cols23 = createSparseMatrix(subRows01Cols23);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> mRows02Cols13 = createSparseMatrix(subRows02Cols13);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> mRows03Cols12 = createSparseMatrix(subRows03Cols12);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> mRows03Cols123 = createSparseMatrix(subRows03Cols123);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> mRows20Cols123 = createSparseMatrix(subRows20Cols123);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> mRows31Cols31 = createSparseMatrix(subRows31Cols31);
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
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m = createSparseMatrix(subTestData);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> mRow0 = createSparseMatrix(subRow0);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> mRow3 = createSparseMatrix(subRow3);
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
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m = createSparseMatrix(subTestData);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> mColumn1 = createSparseMatrix(subColumn1);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> mColumn3 = createSparseMatrix(subColumn3);
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
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m = createSparseMatrix(subTestData);
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> mRow0 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(subRow0[0]);
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> mRow3 = new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(subRow3[0]);
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
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m = createSparseMatrix(subTestData);
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> mColumn1 = columnToVector(subColumn1);
		org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> mColumn3 = columnToVector(subColumn3);
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

	private org.apache.commons.math.linear.FieldVector<org.apache.commons.math.fraction.Fraction> columnToVector(org.apache.commons.math.fraction.Fraction[][] column) {
		org.apache.commons.math.fraction.Fraction[] data = new org.apache.commons.math.fraction.Fraction[column.length];
		for (int i = 0 ; i < (data.length) ; ++i) {
			data[i] = column[i][0];
		}
		return new org.apache.commons.math.linear.ArrayFieldVector<org.apache.commons.math.fraction.Fraction>(data , false);
	}

	public void testEqualsAndHashCode() {
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> m = createSparseMatrix(testData);
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> m1 = ((org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction>)(m.copy()));
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> mt = ((org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction>)(m.transpose()));
		junit.framework.Assert.assertTrue(((m.hashCode()) != (mt.hashCode())));
		junit.framework.Assert.assertEquals(m.hashCode(), m1.hashCode());
		junit.framework.Assert.assertEquals(m, m);
		junit.framework.Assert.assertEquals(m, m1);
		junit.framework.Assert.assertFalse(m.equals(null));
		junit.framework.Assert.assertFalse(m.equals(mt));
		junit.framework.Assert.assertFalse(m.equals(createSparseMatrix(bigSingular)));
	}

	public void testSetSubMatrix() throws java.lang.Exception {
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> m = createSparseMatrix(testData);
		m.setSubMatrix(detData2, 1, 1);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> expected = createSparseMatrix(new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(4) } });
		junit.framework.Assert.assertEquals(expected, m);
		m.setSubMatrix(detData2, 0, 0);
		expected = createSparseMatrix(new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(4) } });
		junit.framework.Assert.assertEquals(expected, m);
		m.setSubMatrix(testDataPlus2, 0, 0);
		expected = createSparseMatrix(new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(5) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(5) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(10) } });
		junit.framework.Assert.assertEquals(expected, m);
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> matrix = createSparseMatrix(new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(6) , new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(8) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(9) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) } });
		matrix.setSubMatrix(new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(6) } }, 1, 1);
		expected = createSparseMatrix(new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) , new org.apache.commons.math.fraction.Fraction(8) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(9) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(6) , new org.apache.commons.math.fraction.Fraction(2) } });
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
			new org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction>(field , 0 , 0);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
		}
		try {
			m.setSubMatrix(new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) } }, 0, 0);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
		}
		try {
			m.setSubMatrix(new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{  } }, 0, 0);
			junit.framework.Assert.fail("expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
		}
	}

	protected void assertClose(java.lang.String msg, org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> m, org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> n, double tolerance) {
		for (int i = 0 ; i < (m.getRowDimension()) ; i++) {
			for (int j = 0 ; j < (m.getColumnDimension()) ; j++) {
				junit.framework.Assert.assertEquals(msg, m.getEntry(i, j).doubleValue(), n.getEntry(i, j).doubleValue(), tolerance);
			}
		}
	}

	protected void assertClose(java.lang.String msg, org.apache.commons.math.fraction.Fraction[] m, org.apache.commons.math.fraction.Fraction[] n, double tolerance) {
		if ((m.length) != (n.length)) {
			junit.framework.Assert.fail("vectors not same length");
		} 
		for (int i = 0 ; i < (m.length) ; i++) {
			junit.framework.Assert.assertEquals((((msg + " ") + i) + " elements differ"), m[i].doubleValue(), n[i].doubleValue(), tolerance);
		}
	}

	private org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> createSparseMatrix(org.apache.commons.math.fraction.Fraction[][] data) {
		org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction> matrix = new org.apache.commons.math.linear.SparseFieldMatrix<org.apache.commons.math.fraction.Fraction>(field , data.length , data[0].length);
		for (int row = 0 ; row < (data.length) ; row++) {
			for (int col = 0 ; col < (data[row].length) ; col++) {
				matrix.setEntry(row, col, data[row][col]);
			}
		}
		return matrix;
	}
}

