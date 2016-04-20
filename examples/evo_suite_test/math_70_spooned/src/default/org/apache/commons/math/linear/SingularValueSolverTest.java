package org.apache.commons.math.linear;


public class SingularValueSolverTest {
	private double[][] testSquare = new double[][]{ new double[]{ 24.0 / 25.0 , 43.0 / 25.0 } , new double[]{ 57.0 / 25.0 , 24.0 / 25.0 } };

	private static final double normTolerance = 1.0E-13;

	@org.junit.Test
	public void testSolveDimensionErrors() {
		org.apache.commons.math.linear.DecompositionSolver solver = new org.apache.commons.math.linear.SingularValueDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare)).getSolver();
		org.apache.commons.math.linear.RealMatrix b = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[3][2]);
		try {
			solver.solve(b);
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException iae) {
		} catch (java.lang.Exception e) {
			org.junit.Assert.fail("wrong exception caught");
		}
		try {
			solver.solve(b.getColumn(0));
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException iae) {
		} catch (java.lang.Exception e) {
			org.junit.Assert.fail("wrong exception caught");
		}
		try {
			solver.solve(new org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl(b.getColumn(0)));
			org.junit.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException iae) {
		} catch (java.lang.Exception e) {
			org.junit.Assert.fail("wrong exception caught");
		}
	}

	@org.junit.Test
	public void testLeastSquareSolve() {
		org.apache.commons.math.linear.RealMatrix m = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 1.0 , 0.0 } , new double[]{ 0.0 , 0.0 } });
		org.apache.commons.math.linear.DecompositionSolver solver = new org.apache.commons.math.linear.SingularValueDecompositionImpl(m).getSolver();
		org.apache.commons.math.linear.RealMatrix b = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 11 , 12 } , new double[]{ 21 , 22 } });
		org.apache.commons.math.linear.RealMatrix xMatrix = solver.solve(b);
		org.junit.Assert.assertEquals(11, xMatrix.getEntry(0, 0), 1.0E-15);
		org.junit.Assert.assertEquals(12, xMatrix.getEntry(0, 1), 1.0E-15);
		org.junit.Assert.assertEquals(0, xMatrix.getEntry(1, 0), 1.0E-15);
		org.junit.Assert.assertEquals(0, xMatrix.getEntry(1, 1), 1.0E-15);
		double[] xCol = solver.solve(b.getColumn(0));
		org.junit.Assert.assertEquals(11, xCol[0], 1.0E-15);
		org.junit.Assert.assertEquals(0, xCol[1], 1.0E-15);
		org.apache.commons.math.linear.RealVector xColVec = solver.solve(b.getColumnVector(0));
		org.junit.Assert.assertEquals(11, xColVec.getEntry(0), 1.0E-15);
		org.junit.Assert.assertEquals(0, xColVec.getEntry(1), 1.0E-15);
		org.apache.commons.math.linear.RealVector xColOtherVec = solver.solve(new org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl(b.getColumn(0)));
		org.junit.Assert.assertEquals(11, xColOtherVec.getEntry(0), 1.0E-15);
		org.junit.Assert.assertEquals(0, xColOtherVec.getEntry(1), 1.0E-15);
	}

	@org.junit.Test
	public void testSolve() {
		org.apache.commons.math.linear.DecompositionSolver solver = new org.apache.commons.math.linear.SingularValueDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare)).getSolver();
		org.apache.commons.math.linear.RealMatrix b = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 1 , 2 , 3 } , new double[]{ 0 , -5 , 1 } });
		org.apache.commons.math.linear.RealMatrix xRef = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ (-8.0) / 25.0 , (-263.0) / 75.0 , (-29.0) / 75.0 } , new double[]{ 19.0 / 25.0 , 78.0 / 25.0 , 49.0 / 25.0 } });
		org.junit.Assert.assertEquals(0, solver.solve(b).subtract(xRef).getNorm(), normTolerance);
		for (int i = 0 ; i < (b.getColumnDimension()) ; ++i) {
			org.junit.Assert.assertEquals(0, new org.apache.commons.math.linear.ArrayRealVector(solver.solve(b.getColumn(i))).subtract(xRef.getColumnVector(i)).getNorm(), 1.0E-13);
		}
		for (int i = 0 ; i < (b.getColumnDimension()) ; ++i) {
			org.junit.Assert.assertEquals(0, solver.solve(b.getColumnVector(i)).subtract(xRef.getColumnVector(i)).getNorm(), 1.0E-13);
		}
		for (int i = 0 ; i < (b.getColumnDimension()) ; ++i) {
			org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl v = new org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl(b.getColumn(i));
			org.junit.Assert.assertEquals(0, solver.solve(v).subtract(xRef.getColumnVector(i)).getNorm(), 1.0E-13);
		}
	}

	@org.junit.Test
	public void testConditionNumber() {
		org.apache.commons.math.linear.SingularValueDecompositionImpl svd = new org.apache.commons.math.linear.SingularValueDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testSquare));
		org.junit.Assert.assertEquals(3.0, svd.getConditionNumber(), 1.5E-15);
	}

	@org.junit.Test
	public void testMath320B() {
		org.apache.commons.math.linear.RealMatrix rm = new org.apache.commons.math.linear.Array2DRowRealMatrix(new double[][]{ new double[]{ 1.0 , 2.0 } , new double[]{ 1.0 , 2.0 } });
		org.apache.commons.math.linear.SingularValueDecomposition svd = new org.apache.commons.math.linear.SingularValueDecompositionImpl(rm);
		org.apache.commons.math.linear.RealMatrix recomposed = svd.getU().multiply(svd.getS()).multiply(svd.getVT());
		org.junit.Assert.assertEquals(0.0, recomposed.subtract(rm).getNorm(), 2.0E-15);
	}
}

