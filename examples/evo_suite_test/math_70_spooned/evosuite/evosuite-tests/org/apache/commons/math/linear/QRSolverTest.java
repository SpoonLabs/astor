package org.apache.commons.math.linear;


public class QRSolverTest extends junit.framework.TestCase {
	double[][] testData3x3NonSingular = new double[][]{ new double[]{ 12 , -51 , 4 } , new double[]{ 6 , 167 , -68 } , new double[]{ -4 , 24 , -41 } };

	double[][] testData3x3Singular = new double[][]{ new double[]{ 1 , 2 , 2 } , new double[]{ 2 , 4 , 6 } , new double[]{ 4 , 8 , 12 } };

	double[][] testData3x4 = new double[][]{ new double[]{ 12 , -51 , 4 , 1 } , new double[]{ 6 , 167 , -68 , 2 } , new double[]{ -4 , 24 , -41 , 3 } };

	double[][] testData4x3 = new double[][]{ new double[]{ 12 , -51 , 4 } , new double[]{ 6 , 167 , -68 } , new double[]{ -4 , 24 , -41 } , new double[]{ -5 , 34 , 7 } };

	public QRSolverTest(java.lang.String name) {
		super(name);
	}

	public void testRank() {
		org.apache.commons.math.linear.DecompositionSolver solver = new org.apache.commons.math.linear.QRDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData3x3NonSingular)).getSolver();
		junit.framework.Assert.assertTrue(solver.isNonSingular());
		solver = new org.apache.commons.math.linear.QRDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData3x3Singular)).getSolver();
		junit.framework.Assert.assertFalse(solver.isNonSingular());
		solver = new org.apache.commons.math.linear.QRDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData3x4)).getSolver();
		junit.framework.Assert.assertTrue(solver.isNonSingular());
		solver = new org.apache.commons.math.linear.QRDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData4x3)).getSolver();
		junit.framework.Assert.assertTrue(solver.isNonSingular());
	}

	public void testSolveDimensionErrors() {
		org.apache.commons.math.linear.DecompositionSolver solver = new org.apache.commons.math.linear.QRDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData3x3NonSingular)).getSolver();
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
			solver.solve(b.getColumnVector(0));
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException iae) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testSolveRankErrors() {
		org.apache.commons.math.linear.DecompositionSolver solver = new org.apache.commons.math.linear.QRDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData3x3Singular)).getSolver();
		org.apache.commons.math.linear.RealMatrix b = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[3][2]);
		try {
			solver.solve(b);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.linear.InvalidMatrixException iae) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		try {
			solver.solve(b.getColumn(0));
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.linear.InvalidMatrixException iae) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
		try {
			solver.solve(b.getColumnVector(0));
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.linear.InvalidMatrixException iae) {
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail("wrong exception caught");
		}
	}

	public void testSolve() {
		org.apache.commons.math.linear.QRDecomposition decomposition = new org.apache.commons.math.linear.QRDecompositionImpl(org.apache.commons.math.linear.MatrixUtils.createRealMatrix(testData3x3NonSingular));
		org.apache.commons.math.linear.DecompositionSolver solver = decomposition.getSolver();
		org.apache.commons.math.linear.RealMatrix b = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ -102 , 12250 } , new double[]{ 544 , 24500 } , new double[]{ 167 , -36750 } });
		org.apache.commons.math.linear.RealMatrix xRef = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(new double[][]{ new double[]{ 1 , 2515 } , new double[]{ 2 , 422 } , new double[]{ -3 , 898 } });
		junit.framework.Assert.assertEquals(0, solver.solve(b).subtract(xRef).getNorm(), (2.0E-16 * (xRef.getNorm())));
		for (int i = 0 ; i < (b.getColumnDimension()) ; ++i) {
			final double[] x = solver.solve(b.getColumn(i));
			final double error = new org.apache.commons.math.linear.ArrayRealVector(x).subtract(xRef.getColumnVector(i)).getNorm();
			junit.framework.Assert.assertEquals(0, error, (3.0E-16 * (xRef.getColumnVector(i).getNorm())));
		}
		for (int i = 0 ; i < (b.getColumnDimension()) ; ++i) {
			final org.apache.commons.math.linear.RealVector x = solver.solve(b.getColumnVector(i));
			final double error = x.subtract(xRef.getColumnVector(i)).getNorm();
			junit.framework.Assert.assertEquals(0, error, (3.0E-16 * (xRef.getColumnVector(i).getNorm())));
		}
		for (int i = 0 ; i < (b.getColumnDimension()) ; ++i) {
			org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl v = new org.apache.commons.math.linear.ArrayRealVectorTest.RealVectorTestImpl(b.getColumn(i));
			final org.apache.commons.math.linear.RealVector x = solver.solve(v);
			final double error = x.subtract(xRef.getColumnVector(i)).getNorm();
			junit.framework.Assert.assertEquals(0, error, (3.0E-16 * (xRef.getColumnVector(i).getNorm())));
		}
	}

	public void testOverdetermined() {
		final java.util.Random r = new java.util.Random(5559252868205245L);
		int p = (7 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE)) / 4;
		int q = (5 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE)) / 4;
		org.apache.commons.math.linear.RealMatrix a = createTestMatrix(r, p, q);
		org.apache.commons.math.linear.RealMatrix xRef = createTestMatrix(r, q, ((org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE) + 3));
		org.apache.commons.math.linear.RealMatrix b = a.multiply(xRef);
		final double noise = 0.001;
		b.walkInOptimizedOrder(new org.apache.commons.math.linear.DefaultRealMatrixChangingVisitor() {
			@java.lang.Override
			public double visit(int row, int column, double value) {
				return value * (1.0 + (noise * ((2 * (r.nextDouble())) - 1)));
			}
		});
		org.apache.commons.math.linear.RealMatrix x = new org.apache.commons.math.linear.QRDecompositionImpl(a).getSolver().solve(b);
		junit.framework.Assert.assertEquals(0, x.subtract(xRef).getNorm(), (((0.01 * noise) * p) * q));
	}

	public void testUnderdetermined() {
		final java.util.Random r = new java.util.Random(42185006424567123L);
		int p = (5 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE)) / 4;
		int q = (7 * (org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE)) / 4;
		org.apache.commons.math.linear.RealMatrix a = createTestMatrix(r, p, q);
		org.apache.commons.math.linear.RealMatrix xRef = createTestMatrix(r, q, ((org.apache.commons.math.linear.BlockRealMatrix.BLOCK_SIZE) + 3));
		org.apache.commons.math.linear.RealMatrix b = a.multiply(xRef);
		org.apache.commons.math.linear.RealMatrix x = new org.apache.commons.math.linear.QRDecompositionImpl(a).getSolver().solve(b);
		junit.framework.Assert.assertTrue((((x.subtract(xRef).getNorm()) / (p * q)) > 0.01));
		junit.framework.Assert.assertEquals(0.0, x.getSubMatrix(p, (q - 1), 0, ((x.getColumnDimension()) - 1)).getNorm());
	}

	private org.apache.commons.math.linear.RealMatrix createTestMatrix(final java.util.Random r, final int rows, final int columns) {
		org.apache.commons.math.linear.RealMatrix m = org.apache.commons.math.linear.MatrixUtils.createRealMatrix(rows, columns);
		m.walkInOptimizedOrder(new org.apache.commons.math.linear.DefaultRealMatrixChangingVisitor() {
			@java.lang.Override
			public double visit(int row, int column, double value) throws org.apache.commons.math.linear.MatrixVisitorException {
				return (2.0 * (r.nextDouble())) - 1.0;
			}
		});
		return m;
	}
}

