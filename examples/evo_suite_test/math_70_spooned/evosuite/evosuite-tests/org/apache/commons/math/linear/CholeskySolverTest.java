package org.apache.commons.math.linear;


public class CholeskySolverTest extends junit.framework.TestCase {
	private double[][] testData = new double[][]{ new double[]{ 1 , 2 , 4 , 7 , 11 } , new double[]{ 2 , 13 , 23 , 38 , 58 } , new double[]{ 4 , 23 , 77 , 122 , 182 } , new double[]{ 7 , 38 , 122 , 294 , 430 } , new double[]{ 11 , 58 , 182 , 430 , 855 } };

	public CholeskySolverTest(java.lang.String name) {
		super(name);
	}

	public void testSolveDimensionErrors() throws org.apache.commons.math.MathException {
		org.apache.commons.math.linear.DecompositionSolver solver = new org.apache.commons.math.linear.CholeskyDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData)).getSolver();
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

	public void testSolve() throws org.apache.commons.math.MathException {
		org.apache.commons.math.linear.DecompositionSolver solver = new org.apache.commons.math.linear.CholeskyDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData)).getSolver();
		org.apache.commons.math.linear.RealMatrix b = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 78 , -13 , 1 } , new double[]{ 414 , -62 , -1 } , new double[]{ 1312 , -202 , -37 } , new double[]{ 2989 , -542 , 145 } , new double[]{ 5510 , -1465 , 201 } });
		org.apache.commons.math.linear.RealMatrix xRef = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 1 , 0 , 1 } , new double[]{ 0 , 1 , 1 } , new double[]{ 2 , 1 , -4 } , new double[]{ 2 , 2 , 2 } , new double[]{ 5 , -3 , 0 } });
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

	public void testDeterminant() throws org.apache.commons.math.MathException {
		junit.framework.Assert.assertEquals(7290000.0, getDeterminant(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData)), 1.0E-15);
	}

	private double getDeterminant(org.apache.commons.math.linear.RealMatrix m) throws org.apache.commons.math.MathException {
		return new org.apache.commons.math.linear.CholeskyDecompositionImpl(m).getDeterminant();
	}
}

