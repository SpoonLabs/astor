package org.apache.commons.math.linear;


public class EigenSolverTest extends junit.framework.TestCase {
	private double[] refValues;

	private org.apache.commons.math.linear.RealMatrix matrix;

	public EigenSolverTest(java.lang.String name) {
		super(name);
	}

	public void testNonInvertible() {
		java.util.Random r = new java.util.Random(9994100315209L);
		org.apache.commons.math.linear.RealMatrix m = org.apache.commons.math.linear.EigenDecompositionImplTest.createTestMatrix(r, new double[]{ 1.0 , 0.0 , -1.0 , -2.0 , -3.0 });
		org.apache.commons.math.linear.DecompositionSolver es = new org.apache.commons.math.linear.EigenDecompositionImpl(m , org.apache.commons.math.util.MathUtils.SAFE_MIN).getSolver();
		junit.framework.Assert.assertFalse(es.isNonSingular());
		try {
			es.getInverse();
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.linear.InvalidMatrixException ime) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testInvertible() {
		java.util.Random r = new java.util.Random(9994100315209L);
		org.apache.commons.math.linear.RealMatrix m = org.apache.commons.math.linear.EigenDecompositionImplTest.createTestMatrix(r, new double[]{ 1.0 , 0.5 , -1.0 , -2.0 , -3.0 });
		org.apache.commons.math.linear.DecompositionSolver es = new org.apache.commons.math.linear.EigenDecompositionImpl(m , org.apache.commons.math.util.MathUtils.SAFE_MIN).getSolver();
		junit.framework.Assert.assertTrue(es.isNonSingular());
		org.apache.commons.math.linear.RealMatrix inverse = es.getInverse();
		org.apache.commons.math.linear.RealMatrix error = m.multiply(inverse).subtract(org.apache.commons.math.linear.MatrixUtils.createRealIdentityMatrix(m.getRowDimension()));
		junit.framework.Assert.assertEquals(0, error.getNorm(), 4.0E-15);
	}

	public void testSolveDimensionErrors() {
		org.apache.commons.math.linear.DecompositionSolver es = new org.apache.commons.math.linear.EigenDecompositionImpl(matrix , org.apache.commons.math.util.MathUtils.SAFE_MIN).getSolver();
		org.apache.commons.math.linear.RealMatrix b = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[2][2]);
		try {
			es.solve(b);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException iae) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		try {
			es.solve(b.getColumn(0));
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException iae) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		try {
			es.solve(new org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl(b.getColumn(0)));
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException iae) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testSolve() {
		org.apache.commons.math.linear.RealMatrix m = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 91 , 5 , 29 , 32 , 40 , 14 } , new double[]{ 5 , 34 , -1 , 0 , 2 , -1 } , new double[]{ 29 , -1 , 12 , 9 , 21 , 8 } , new double[]{ 32 , 0 , 9 , 14 , 9 , 0 } , new double[]{ 40 , 2 , 21 , 9 , 51 , 19 } , new double[]{ 14 , -1 , 8 , 0 , 19 , 14 } });
		org.apache.commons.math.linear.DecompositionSolver es = new org.apache.commons.math.linear.EigenDecompositionImpl(m , org.apache.commons.math.util.MathUtils.SAFE_MIN).getSolver();
		org.apache.commons.math.linear.RealMatrix b = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 1561 , 269 , 188 } , new double[]{ 69 , -21 , 70 } , new double[]{ 739 , 108 , 63 } , new double[]{ 324 , 86 , 59 } , new double[]{ 1624 , 194 , 107 } , new double[]{ 796 , 69 , 36 } });
		org.apache.commons.math.linear.RealMatrix xRef = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 1 , 2 , 1 } , new double[]{ 2 , -1 , 2 } , new double[]{ 4 , 2 , 3 } , new double[]{ 8 , -1 , 0 } , new double[]{ 16 , 2 , 0 } , new double[]{ 32 , -1 , 0 } });
		org.apache.commons.math.linear.RealMatrix solution = es.solve(b);
		junit.framework.Assert.assertEquals(0, solution.subtract(xRef).getNorm(), 2.5E-12);
		for (int i = 0 ; i < (b.getColumnDimension()) ; ++i) {
			junit.framework.Assert.assertEquals(0, new org.apache.commons.math.linear.ArrayRealVector(es.solve(b.getColumn(i))).subtract(xRef.getColumnVector(i)).getNorm(), 2.0E-11);
		}
		for (int i = 0 ; i < (b.getColumnDimension()) ; ++i) {
			junit.framework.Assert.assertEquals(0, es.solve(b.getColumnVector(i)).subtract(xRef.getColumnVector(i)).getNorm(), 2.0E-11);
		}
		for (int i = 0 ; i < (b.getColumnDimension()) ; ++i) {
			org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl v = new org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl(b.getColumn(i));
			junit.framework.Assert.assertEquals(0, es.solve(v).subtract(xRef.getColumnVector(i)).getNorm(), 2.0E-11);
		}
	}

	@java.lang.Override
	public void setUp() {
		refValues = new double[]{ 2.003 , 2.002 , 2.001 , 1.001 , 1.0 , 0.001 };
		matrix = org.apache.commons.math.linear.EigenDecompositionImplTest.createTestMatrix(new java.util.Random(35992629946426L), refValues);
	}

	@java.lang.Override
	public void tearDown() {
		refValues = null;
		matrix = null;
	}
}

