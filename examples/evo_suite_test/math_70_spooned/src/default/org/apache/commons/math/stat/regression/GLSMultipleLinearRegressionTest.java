package org.apache.commons.math.stat.regression;


public class GLSMultipleLinearRegressionTest extends org.apache.commons.math.stat.regression.MultipleLinearRegressionAbstractTest {
	private double[] y;

	private double[][] x;

	private double[][] omega;

	@org.junit.Before
	@java.lang.Override
	public void setUp() {
		y = new double[]{ 11.0 , 12.0 , 13.0 , 14.0 , 15.0 , 16.0 };
		x = new double[6][];
		x[0] = new double[]{ 1.0 , 0 , 0 , 0 , 0 , 0 };
		x[1] = new double[]{ 1.0 , 2.0 , 0 , 0 , 0 , 0 };
		x[2] = new double[]{ 1.0 , 0 , 3.0 , 0 , 0 , 0 };
		x[3] = new double[]{ 1.0 , 0 , 0 , 4.0 , 0 , 0 };
		x[4] = new double[]{ 1.0 , 0 , 0 , 0 , 5.0 , 0 };
		x[5] = new double[]{ 1.0 , 0 , 0 , 0 , 0 , 6.0 };
		omega = new double[6][];
		omega[0] = new double[]{ 1.0 , 0 , 0 , 0 , 0 , 0 };
		omega[1] = new double[]{ 0 , 2.0 , 0 , 0 , 0 , 0 };
		omega[2] = new double[]{ 0 , 0 , 3.0 , 0 , 0 , 0 };
		omega[3] = new double[]{ 0 , 0 , 0 , 4.0 , 0 , 0 };
		omega[4] = new double[]{ 0 , 0 , 0 , 0 , 5.0 , 0 };
		omega[5] = new double[]{ 0 , 0 , 0 , 0 , 0 , 6.0 };
		super.setUp();
	}

	@org.junit.Test(expected = java.lang.IllegalArgumentException.class)
	public void cannotAddXSampleData() {
		createRegression().newSampleData(new double[]{  }, null, null);
	}

	@org.junit.Test(expected = java.lang.IllegalArgumentException.class)
	public void cannotAddNullYSampleData() {
		createRegression().newSampleData(null, new double[][]{  }, null);
	}

	@org.junit.Test(expected = java.lang.IllegalArgumentException.class)
	public void cannotAddSampleDataWithSizeMismatch() {
		double[] y = new double[]{ 1.0 , 2.0 };
		double[][] x = new double[1][];
		x[0] = new double[]{ 1.0 , 0 };
		createRegression().newSampleData(y, x, null);
	}

	@org.junit.Test(expected = java.lang.IllegalArgumentException.class)
	public void cannotAddNullCovarianceData() {
		createRegression().newSampleData(new double[]{  }, new double[][]{  }, null);
	}

	@org.junit.Test(expected = java.lang.IllegalArgumentException.class)
	public void notEnoughData() {
		double[] reducedY = new double[(y.length) - 1];
		double[][] reducedX = new double[(x.length) - 1][];
		double[][] reducedO = new double[(omega.length) - 1][];
		java.lang.System.arraycopy(y, 0, reducedY, 0, reducedY.length);
		java.lang.System.arraycopy(x, 0, reducedX, 0, reducedX.length);
		java.lang.System.arraycopy(omega, 0, reducedO, 0, reducedO.length);
		createRegression().newSampleData(reducedY, reducedX, reducedO);
	}

	@org.junit.Test(expected = java.lang.IllegalArgumentException.class)
	public void cannotAddCovarianceDataWithSampleSizeMismatch() {
		double[] y = new double[]{ 1.0 , 2.0 };
		double[][] x = new double[2][];
		x[0] = new double[]{ 1.0 , 0 };
		x[1] = new double[]{ 0 , 1.0 };
		double[][] omega = new double[1][];
		omega[0] = new double[]{ 1.0 , 0 };
		createRegression().newSampleData(y, x, omega);
	}

	@org.junit.Test(expected = java.lang.IllegalArgumentException.class)
	public void cannotAddCovarianceDataThatIsNotSquare() {
		double[] y = new double[]{ 1.0 , 2.0 };
		double[][] x = new double[2][];
		x[0] = new double[]{ 1.0 , 0 };
		x[1] = new double[]{ 0 , 1.0 };
		double[][] omega = new double[3][];
		omega[0] = new double[]{ 1.0 , 0 };
		omega[1] = new double[]{ 0 , 1.0 };
		omega[2] = new double[]{ 0 , 2.0 };
		createRegression().newSampleData(y, x, omega);
	}

	@java.lang.Override
	protected org.apache.commons.math.stat.regression.GLSMultipleLinearRegression createRegression() {
		org.apache.commons.math.stat.regression.GLSMultipleLinearRegression regression = new org.apache.commons.math.stat.regression.GLSMultipleLinearRegression();
		regression.newSampleData(y, x, omega);
		return regression;
	}

	@java.lang.Override
	protected int getNumberOfRegressors() {
		return x[0].length;
	}

	@java.lang.Override
	protected int getSampleSize() {
		return y.length;
	}
}

