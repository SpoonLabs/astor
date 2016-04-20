package org.apache.commons.math.linear;


public class FieldLUDecompositionImplTest extends junit.framework.TestCase {
	private org.apache.commons.math.fraction.Fraction[][] testData = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(8) } };

	private org.apache.commons.math.fraction.Fraction[][] testDataMinus = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(-1) , new org.apache.commons.math.fraction.Fraction(-2) , new org.apache.commons.math.fraction.Fraction(-3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(-2) , new org.apache.commons.math.fraction.Fraction(-5) , new org.apache.commons.math.fraction.Fraction(-3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(-1) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(-8) } };

	private org.apache.commons.math.fraction.Fraction[][] luData = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(7) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(6) , new org.apache.commons.math.fraction.Fraction(6) , new org.apache.commons.math.fraction.Fraction(8) } };

	private org.apache.commons.math.fraction.Fraction[][] singular = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) } };

	private org.apache.commons.math.fraction.Fraction[][] bigSingular = new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(5) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(4) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(256) , new org.apache.commons.math.fraction.Fraction(1930) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(7) , new org.apache.commons.math.fraction.Fraction(6) , new org.apache.commons.math.fraction.Fraction(8) } };

	public FieldLUDecompositionImplTest(java.lang.String name) {
		super(name);
	}

	public void testDimensions() {
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> matrix = new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(testData);
		org.apache.commons.math.linear.FieldLUDecomposition<org.apache.commons.math.fraction.Fraction> LU = new org.apache.commons.math.linear.FieldLUDecompositionImpl<org.apache.commons.math.fraction.Fraction>(matrix);
		junit.framework.Assert.assertEquals(testData.length, LU.getL().getRowDimension());
		junit.framework.Assert.assertEquals(testData.length, LU.getL().getColumnDimension());
		junit.framework.Assert.assertEquals(testData.length, LU.getU().getRowDimension());
		junit.framework.Assert.assertEquals(testData.length, LU.getU().getColumnDimension());
		junit.framework.Assert.assertEquals(testData.length, LU.getP().getRowDimension());
		junit.framework.Assert.assertEquals(testData.length, LU.getP().getColumnDimension());
	}

	public void testNonSquare() {
		try {
			new org.apache.commons.math.linear.FieldLUDecompositionImpl<org.apache.commons.math.fraction.Fraction>(new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ org.apache.commons.math.fraction.Fraction.ZERO , org.apache.commons.math.fraction.Fraction.ZERO } , new org.apache.commons.math.fraction.Fraction[]{ org.apache.commons.math.fraction.Fraction.ZERO , org.apache.commons.math.fraction.Fraction.ZERO } , new org.apache.commons.math.fraction.Fraction[]{ org.apache.commons.math.fraction.Fraction.ZERO , org.apache.commons.math.fraction.Fraction.ZERO } }));
		} catch (org.apache.commons.math.linear.InvalidMatrixException ime) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testPAEqualLU() {
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> matrix = new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(testData);
		org.apache.commons.math.linear.FieldLUDecomposition<org.apache.commons.math.fraction.Fraction> lu = new org.apache.commons.math.linear.FieldLUDecompositionImpl<org.apache.commons.math.fraction.Fraction>(matrix);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> l = lu.getL();
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> u = lu.getU();
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> p = lu.getP();
		org.apache.commons.math.TestUtils.assertEquals(p.multiply(matrix), l.multiply(u));
		matrix = new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(testDataMinus);
		lu = new org.apache.commons.math.linear.FieldLUDecompositionImpl<org.apache.commons.math.fraction.Fraction>(matrix);
		l = lu.getL();
		u = lu.getU();
		p = lu.getP();
		org.apache.commons.math.TestUtils.assertEquals(p.multiply(matrix), l.multiply(u));
		matrix = new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(org.apache.commons.math.fraction.FractionField.getInstance() , 17 , 17);
		for (int i = 0 ; i < (matrix.getRowDimension()) ; ++i) {
			matrix.setEntry(i, i, org.apache.commons.math.fraction.Fraction.ONE);
		}
		lu = new org.apache.commons.math.linear.FieldLUDecompositionImpl<org.apache.commons.math.fraction.Fraction>(matrix);
		l = lu.getL();
		u = lu.getU();
		p = lu.getP();
		org.apache.commons.math.TestUtils.assertEquals(p.multiply(matrix), l.multiply(u));
		matrix = new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(singular);
		lu = new org.apache.commons.math.linear.FieldLUDecompositionImpl<org.apache.commons.math.fraction.Fraction>(matrix);
		junit.framework.Assert.assertFalse(lu.getSolver().isNonSingular());
		junit.framework.Assert.assertNull(lu.getL());
		junit.framework.Assert.assertNull(lu.getU());
		junit.framework.Assert.assertNull(lu.getP());
		matrix = new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(bigSingular);
		lu = new org.apache.commons.math.linear.FieldLUDecompositionImpl<org.apache.commons.math.fraction.Fraction>(matrix);
		junit.framework.Assert.assertFalse(lu.getSolver().isNonSingular());
		junit.framework.Assert.assertNull(lu.getL());
		junit.framework.Assert.assertNull(lu.getU());
		junit.framework.Assert.assertNull(lu.getP());
	}

	public void testLLowerTriangular() {
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> matrix = new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(testData);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> l = new org.apache.commons.math.linear.FieldLUDecompositionImpl<org.apache.commons.math.fraction.Fraction>(matrix).getL();
		for (int i = 0 ; i < (l.getRowDimension()) ; i++) {
			junit.framework.Assert.assertEquals(org.apache.commons.math.fraction.Fraction.ONE, l.getEntry(i, i));
			for (int j = i + 1 ; j < (l.getColumnDimension()) ; j++) {
				junit.framework.Assert.assertEquals(org.apache.commons.math.fraction.Fraction.ZERO, l.getEntry(i, j));
			}
		}
	}

	public void testUUpperTriangular() {
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> matrix = new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(testData);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> u = new org.apache.commons.math.linear.FieldLUDecompositionImpl<org.apache.commons.math.fraction.Fraction>(matrix).getU();
		for (int i = 0 ; i < (u.getRowDimension()) ; i++) {
			for (int j = 0 ; j < i ; j++) {
				junit.framework.Assert.assertEquals(org.apache.commons.math.fraction.Fraction.ZERO, u.getEntry(i, j));
			}
		}
	}

	public void testPPermutation() {
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> matrix = new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(testData);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> p = new org.apache.commons.math.linear.FieldLUDecompositionImpl<org.apache.commons.math.fraction.Fraction>(matrix).getP();
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> ppT = p.multiply(p.transpose());
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> id = new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(org.apache.commons.math.fraction.FractionField.getInstance() , p.getRowDimension() , p.getRowDimension());
		for (int i = 0 ; i < (id.getRowDimension()) ; ++i) {
			id.setEntry(i, i, org.apache.commons.math.fraction.Fraction.ONE);
		}
		org.apache.commons.math.TestUtils.assertEquals(id, ppT);
		for (int i = 0 ; i < (p.getRowDimension()) ; i++) {
			int zeroCount = 0;
			int oneCount = 0;
			int otherCount = 0;
			for (int j = 0 ; j < (p.getColumnDimension()) ; j++) {
				final org.apache.commons.math.fraction.Fraction e = p.getEntry(i, j);
				if (e.equals(org.apache.commons.math.fraction.Fraction.ZERO)) {
					++zeroCount;
				} else if (e.equals(org.apache.commons.math.fraction.Fraction.ONE)) {
					++oneCount;
				} else {
					++otherCount;
				}
			}
			junit.framework.Assert.assertEquals(((p.getColumnDimension()) - 1), zeroCount);
			junit.framework.Assert.assertEquals(1, oneCount);
			junit.framework.Assert.assertEquals(0, otherCount);
		}
		for (int j = 0 ; j < (p.getColumnDimension()) ; j++) {
			int zeroCount = 0;
			int oneCount = 0;
			int otherCount = 0;
			for (int i = 0 ; i < (p.getRowDimension()) ; i++) {
				final org.apache.commons.math.fraction.Fraction e = p.getEntry(i, j);
				if (e.equals(org.apache.commons.math.fraction.Fraction.ZERO)) {
					++zeroCount;
				} else if (e.equals(org.apache.commons.math.fraction.Fraction.ONE)) {
					++oneCount;
				} else {
					++otherCount;
				}
			}
			junit.framework.Assert.assertEquals(((p.getRowDimension()) - 1), zeroCount);
			junit.framework.Assert.assertEquals(1, oneCount);
			junit.framework.Assert.assertEquals(0, otherCount);
		}
	}

	public void testSingular() {
		org.apache.commons.math.linear.FieldLUDecomposition<org.apache.commons.math.fraction.Fraction> lu = new org.apache.commons.math.linear.FieldLUDecompositionImpl<org.apache.commons.math.fraction.Fraction>(new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(testData));
		junit.framework.Assert.assertTrue(lu.getSolver().isNonSingular());
		lu = new org.apache.commons.math.linear.FieldLUDecompositionImpl<org.apache.commons.math.fraction.Fraction>(new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(singular));
		junit.framework.Assert.assertFalse(lu.getSolver().isNonSingular());
		lu = new org.apache.commons.math.linear.FieldLUDecompositionImpl<org.apache.commons.math.fraction.Fraction>(new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(bigSingular));
		junit.framework.Assert.assertFalse(lu.getSolver().isNonSingular());
	}

	public void testMatricesValues1() {
		org.apache.commons.math.linear.FieldLUDecomposition<org.apache.commons.math.fraction.Fraction> lu = new org.apache.commons.math.linear.FieldLUDecompositionImpl<org.apache.commons.math.fraction.Fraction>(new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(testData));
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> lRef = new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(0) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(0) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(-2) , new org.apache.commons.math.fraction.Fraction(1) } });
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> uRef = new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(-3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(-1) } });
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> pRef = new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(0) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(0) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(1) } });
		int[] pivotRef = new int[]{ 0 , 1 , 2 };
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> l = lu.getL();
		org.apache.commons.math.TestUtils.assertEquals(lRef, l);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> u = lu.getU();
		org.apache.commons.math.TestUtils.assertEquals(uRef, u);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> p = lu.getP();
		org.apache.commons.math.TestUtils.assertEquals(pRef, p);
		int[] pivot = lu.getPivot();
		for (int i = 0 ; i < (pivotRef.length) ; ++i) {
			junit.framework.Assert.assertEquals(pivotRef[i], pivot[i]);
		}
		junit.framework.Assert.assertTrue((l == (lu.getL())));
		junit.framework.Assert.assertTrue((u == (lu.getU())));
		junit.framework.Assert.assertTrue((p == (lu.getP())));
	}

	public void testMatricesValues2() {
		org.apache.commons.math.linear.FieldLUDecomposition<org.apache.commons.math.fraction.Fraction> lu = new org.apache.commons.math.linear.FieldLUDecompositionImpl<org.apache.commons.math.fraction.Fraction>(new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(luData));
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> lRef = new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(0) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(0) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(1) } });
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> uRef = new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(2) , new org.apache.commons.math.fraction.Fraction(3) , new org.apache.commons.math.fraction.Fraction(3) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(-3) , new org.apache.commons.math.fraction.Fraction(-1) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(4) } });
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> pRef = new org.apache.commons.math.linear.Array2DRowFieldMatrix<org.apache.commons.math.fraction.Fraction>(new org.apache.commons.math.fraction.Fraction[][]{ new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(0) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(1) } , new org.apache.commons.math.fraction.Fraction[]{ new org.apache.commons.math.fraction.Fraction(0) , new org.apache.commons.math.fraction.Fraction(1) , new org.apache.commons.math.fraction.Fraction(0) } });
		int[] pivotRef = new int[]{ 0 , 2 , 1 };
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> l = lu.getL();
		org.apache.commons.math.TestUtils.assertEquals(lRef, l);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> u = lu.getU();
		org.apache.commons.math.TestUtils.assertEquals(uRef, u);
		org.apache.commons.math.linear.FieldMatrix<org.apache.commons.math.fraction.Fraction> p = lu.getP();
		org.apache.commons.math.TestUtils.assertEquals(pRef, p);
		int[] pivot = lu.getPivot();
		for (int i = 0 ; i < (pivotRef.length) ; ++i) {
			junit.framework.Assert.assertEquals(pivotRef[i], pivot[i]);
		}
		junit.framework.Assert.assertTrue((l == (lu.getL())));
		junit.framework.Assert.assertTrue((u == (lu.getU())));
		junit.framework.Assert.assertTrue((p == (lu.getP())));
	}
}

