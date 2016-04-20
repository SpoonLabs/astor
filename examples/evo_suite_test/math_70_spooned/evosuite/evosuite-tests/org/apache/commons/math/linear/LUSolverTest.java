package org.apache.commons.math.linear;


public class LUSolverTest extends junit.framework.TestCase {
	private double[][] testData = new double[][]{ new double[]{ 1.0 , 2.0 , 3.0 } , new double[]{ 2.0 , 5.0 , 3.0 } , new double[]{ 1.0 , 0.0 , 8.0 } };

	private double[][] luData = new double[][]{ new double[]{ 2.0 , 3.0 , 3.0 } , new double[]{ 0.0 , 5.0 , 7.0 } , new double[]{ 6.0 , 9.0 , 8.0 } };

	private double[][] singular = new double[][]{ new double[]{ 2.0 , 3.0 } , new double[]{ 2.0 , 3.0 } };

	private double[][] bigSingular = new double[][]{ new double[]{ 1.0 , 2.0 , 3.0 , 4.0 } , new double[]{ 2.0 , 5.0 , 3.0 , 4.0 } , new double[]{ 7.0 , 3.0 , 256.0 , 1930.0 } , new double[]{ 3.0 , 7.0 , 6.0 , 8.0 } };

	public LUSolverTest(java.lang.String name) {
		super(name);
	}

	public void testThreshold() {
		final org.apache.commons.math.linear.RealMatrix matrix = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 1.0 , 2.0 , 3.0 } , new double[]{ 2.0 , 5.0 , 3.0 } , new double[]{ 4.000001 , 9.0 , 9.0 } });
		junit.framework.Assert.assertFalse(new org.apache.commons.math.linear.LUDecompositionImpl(matrix , 1.0E-5).getSolver().isNonSingular());
		junit.framework.Assert.assertTrue(new org.apache.commons.math.linear.LUDecompositionImpl(matrix , 1.0E-10).getSolver().isNonSingular());
	}

	public void testSingular() {
		org.apache.commons.math.linear.DecompositionSolver solver = new org.apache.commons.math.linear.LUDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData)).getSolver();
		junit.framework.Assert.assertTrue(solver.isNonSingular());
		solver = new org.apache.commons.math.linear.LUDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(singular)).getSolver();
		junit.framework.Assert.assertFalse(solver.isNonSingular());
		solver = new org.apache.commons.math.linear.LUDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(bigSingular)).getSolver();
		junit.framework.Assert.assertFalse(solver.isNonSingular());
	}

	public void testSolveDimensionErrors() {
		org.apache.commons.math.linear.DecompositionSolver solver = new org.apache.commons.math.linear.LUDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData)).getSolver();
		org.apache.commons.math.linear.RealMatrix b = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[2][2]);
		try {
			solver.solve(b);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException iae) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		try {
			solver.solve(b.getColumn(0));
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException iae) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		try {
			solver.solve(new org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl(b.getColumn(0)));
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException iae) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testSolveSingularityErrors() {
		org.apache.commons.math.linear.DecompositionSolver solver = new org.apache.commons.math.linear.LUDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(singular)).getSolver();
		org.apache.commons.math.linear.RealMatrix b = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[2][2]);
		try {
			solver.solve(b);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.linear.InvalidMatrixException ime) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		try {
			solver.solve(b.getColumn(0));
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.linear.InvalidMatrixException ime) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		try {
			solver.solve(b.getColumnVector(0));
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.linear.InvalidMatrixException ime) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		try {
			solver.solve(new org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl(b.getColumn(0)));
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.linear.InvalidMatrixException ime) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testSolve() {
		org.apache.commons.math.linear.DecompositionSolver solver = new org.apache.commons.math.linear.LUDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData)).getSolver();
		org.apache.commons.math.linear.RealMatrix b = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 1 , 0 } , new double[]{ 2 , -5 } , new double[]{ 3 , 1 } });
		org.apache.commons.math.linear.RealMatrix xRef = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 19 , -71 } , new double[]{ -6 , 22 } , new double[]{ -2 , 9 } });
		junit.framework.Assert.assertEquals(0, solver.solve(b).subtract(xRef).getNorm(), 1.0E-13);
		for (int i = 0 ; i < (b.getColumnDimension()) ; ++i) {
			junit.framework.Assert.assertEquals(0, new org.apache.commons.math.linear.ArrayRealVector(solver.solve(b.getColumn(i))).subtract(xRef.getColumnVector(i)).getNorm(), 1.0E-13);
		}
		for (int i = 0 ; i < (b.getColumnDimension()) ; ++i) {
			junit.framework.Assert.assertEquals(0, solver.solve(b.getColumnVector(i)).subtract(xRef.getColumnVector(i)).getNorm(), 1.0E-13);
		}
		for (int i = 0 ; i < (b.getColumnDimension()) ; ++i) {
			org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl v = new org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl(b.getColumn(i));
			junit.framework.Assert.assertEquals(0, solver.solve(v).subtract(xRef.getColumnVector(i)).getNorm(), 1.0E-13);
		}
	}

	public void testDeterminant() {
		junit.framework.Assert.assertEquals(-1, getDeterminant(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData)), 1.0E-15);
		junit.framework.Assert.assertEquals(-10, getDeterminant(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(luData)), 1.0E-14);
		junit.framework.Assert.assertEquals(0, getDeterminant(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(singular)), 1.0E-17);
		junit.framework.Assert.assertEquals(0, getDeterminant(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(bigSingular)), 1.0E-10);
	}

	private double getDeterminant(org.apache.commons.math.linear.RealMatrix m) {
		return new org.apache.commons.math.linear.LUDecompositionImpl(m).getDeterminant();
	}
}

