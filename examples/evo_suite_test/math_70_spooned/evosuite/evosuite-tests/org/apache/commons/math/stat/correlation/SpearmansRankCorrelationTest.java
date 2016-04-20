package org.apache.commons.math.stat.correlation;


public class SpearmansRankCorrelationTest extends org.apache.commons.math.stat.correlation.PearsonsCorrelationTest {
	@java.lang.Override
	protected void setUp() throws java.lang.Exception {
		super.setUp();
	}

	@java.lang.Override
	protected void tearDown() throws java.lang.Exception {
		super.tearDown();
	}

	@java.lang.Override
	public void testLongly() throws java.lang.Exception {
		org.apache.commons.math.linear.RealMatrix matrix = createRealMatrix(longleyData, 16, 7);
		org.apache.commons.math.stat.correlation.SpearmansCorrelation corrInstance = new org.apache.commons.math.stat.correlation.SpearmansCorrelation(matrix);
		org.apache.commons.math.linear.RealMatrix correlationMatrix = corrInstance.getCorrelationMatrix();
		double[] rData = new double[]{ 1 , 0.982352941176471 , 0.985294117647059 , 0.564705882352941 , 0.2264705882352941 , 0.976470588235294 , 0.976470588235294 , 0.982352941176471 , 1 , 0.997058823529412 , 0.664705882352941 , 0.2205882352941176 , 0.997058823529412 , 0.997058823529412 , 0.985294117647059 , 0.997058823529412 , 1 , 0.638235294117647 , 0.2235294117647059 , 0.9941176470588236 , 0.9941176470588236 , 0.564705882352941 , 0.664705882352941 , 0.638235294117647 , 1 , -0.3411764705882353 , 0.685294117647059 , 0.685294117647059 , 0.2264705882352941 , 0.2205882352941176 , 0.2235294117647059 , -0.3411764705882353 , 1 , 0.2264705882352941 , 0.2264705882352941 , 0.976470588235294 , 0.997058823529412 , 0.9941176470588236 , 0.685294117647059 , 0.2264705882352941 , 1 , 1 , 0.976470588235294 , 0.997058823529412 , 0.9941176470588236 , 0.685294117647059 , 0.2264705882352941 , 1 , 1 };
		org.apache.commons.math.TestUtils.assertEquals("Spearman's correlation matrix", createRealMatrix(rData, 7, 7), correlationMatrix, 1.0E-14);
	}

	public void testSwiss() throws java.lang.Exception {
		org.apache.commons.math.linear.RealMatrix matrix = createRealMatrix(swissData, 47, 5);
		org.apache.commons.math.stat.correlation.SpearmansCorrelation corrInstance = new org.apache.commons.math.stat.correlation.SpearmansCorrelation(matrix);
		org.apache.commons.math.linear.RealMatrix correlationMatrix = corrInstance.getCorrelationMatrix();
		double[] rData = new double[]{ 1 , 0.2426642769364176 , -0.660902996352354 , -0.443257690360988 , 0.4136455623012432 , 0.2426642769364176 , 1 , -0.598859938748963 , -0.650463814145816 , 0.2886878090882852 , -0.660902996352354 , -0.598859938748963 , 1 , 0.674603831406147 , -0.4750575257171745 , -0.443257690360988 , -0.650463814145816 , 0.674603831406147 , 1 , -0.1444163088302244 , 0.4136455623012432 , 0.2886878090882852 , -0.4750575257171745 , -0.1444163088302244 , 1 };
		org.apache.commons.math.TestUtils.assertEquals("Spearman's correlation matrix", createRealMatrix(rData, 5, 5), correlationMatrix, 1.0E-14);
	}

	@java.lang.Override
	public void testConstant() {
		double[] noVariance = new double[]{ 1 , 1 , 1 , 1 };
		double[] values = new double[]{ 1 , 2 , 3 , 4 };
		junit.framework.Assert.assertTrue(java.lang.Double.isNaN(new org.apache.commons.math.stat.correlation.SpearmansCorrelation().correlation(noVariance, values)));
	}

	@java.lang.Override
	public void testInsufficientData() {
		double[] one = new double[]{ 1 };
		double[] two = new double[]{ 2 };
		try {
			new org.apache.commons.math.stat.correlation.SpearmansCorrelation().correlation(one, two);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		org.apache.commons.math.linear.RealMatrix matrix = new org.apache.commons.math.linear.BlockRealMatrix(new double[][]{ new double[]{ 0 } , new double[]{ 1 } });
		try {
			new org.apache.commons.math.stat.correlation.SpearmansCorrelation(matrix);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	@java.lang.Override
	public void testConsistency() {
		org.apache.commons.math.linear.RealMatrix matrix = createRealMatrix(longleyData, 16, 7);
		org.apache.commons.math.stat.correlation.SpearmansCorrelation corrInstance = new org.apache.commons.math.stat.correlation.SpearmansCorrelation(matrix);
		double[][] data = matrix.getData();
		double[] x = matrix.getColumn(0);
		double[] y = matrix.getColumn(1);
		junit.framework.Assert.assertEquals(new org.apache.commons.math.stat.correlation.SpearmansCorrelation().correlation(x, y), corrInstance.getCorrelationMatrix().getEntry(0, 1), java.lang.Double.MIN_VALUE);
		org.apache.commons.math.TestUtils.assertEquals("Correlation matrix", corrInstance.getCorrelationMatrix(), new org.apache.commons.math.stat.correlation.SpearmansCorrelation().computeCorrelationMatrix(data), java.lang.Double.MIN_VALUE);
	}

	@java.lang.Override
	public void testStdErrorConsistency() throws java.lang.Exception {
	}

	@java.lang.Override
	public void testCovarianceConsistency() throws java.lang.Exception {
	}
}

