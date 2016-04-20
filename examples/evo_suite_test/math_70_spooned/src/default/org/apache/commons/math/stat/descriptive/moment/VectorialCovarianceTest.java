package org.apache.commons.math.stat.descriptive.moment;


public class VectorialCovarianceTest extends junit.framework.TestCase {
	public VectorialCovarianceTest(java.lang.String name) {
		super(name);
		points = null;
	}

	public void testMismatch() {
		try {
			new org.apache.commons.math.stat.descriptive.moment.VectorialCovariance(8 , true).increment(new double[5]);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (org.apache.commons.math.DimensionMismatchException dme) {
			junit.framework.Assert.assertEquals(5, dme.getDimension1());
			junit.framework.Assert.assertEquals(8, dme.getDimension2());
		} catch (java.lang.Exception e) {
			junit.framework.Assert.fail(("wrong exception type caught: " + (e.getClass().getName())));
		}
	}

	public void testSimplistic() throws org.apache.commons.math.DimensionMismatchException {
		org.apache.commons.math.stat.descriptive.moment.VectorialCovariance stat = new org.apache.commons.math.stat.descriptive.moment.VectorialCovariance(2 , true);
		stat.increment(new double[]{ -1.0 , 1.0 });
		stat.increment(new double[]{ 1.0 , -1.0 });
		org.apache.commons.math.linear.RealMatrix c = stat.getResult();
		junit.framework.Assert.assertEquals(2.0, c.getEntry(0, 0), 1.0E-12);
		junit.framework.Assert.assertEquals(-2.0, c.getEntry(1, 0), 1.0E-12);
		junit.framework.Assert.assertEquals(2.0, c.getEntry(1, 1), 1.0E-12);
	}

	public void testBasicStats() throws org.apache.commons.math.DimensionMismatchException {
		org.apache.commons.math.stat.descriptive.moment.VectorialCovariance stat = new org.apache.commons.math.stat.descriptive.moment.VectorialCovariance(points[0].length , true);
		for (int i = 0 ; i < (points.length) ; ++i) {
			stat.increment(points[i]);
		}
		junit.framework.Assert.assertEquals(points.length, stat.getN());
		org.apache.commons.math.linear.RealMatrix c = stat.getResult();
		double[][] refC = new double[][]{ new double[]{ 8.047 , -1.9195 , -3.4445 } , new double[]{ -1.9195 , 1.047 , 3.2795 } , new double[]{ -3.4445 , 3.2795 , 12.207 } };
		for (int i = 0 ; i < (c.getRowDimension()) ; ++i) {
			for (int j = 0 ; j <= i ; ++j) {
				junit.framework.Assert.assertEquals(refC[i][j], c.getEntry(i, j), 1.0E-12);
			}
		}
	}

	public void testSerial() {
		org.apache.commons.math.stat.descriptive.moment.VectorialCovariance stat = new org.apache.commons.math.stat.descriptive.moment.VectorialCovariance(points[0].length , true);
		junit.framework.Assert.assertEquals(stat, org.apache.commons.math.TestUtils.serializeAndRecover(stat));
	}

	@java.lang.Override
	public void setUp() {
		points = new double[][]{ new double[]{ 1.2 , 2.3 , 4.5 } , new double[]{ -0.7 , 2.3 , 5.0 } , new double[]{ 3.1 , 0.0 , -3.1 } , new double[]{ 6.0 , 1.2 , 4.2 } , new double[]{ -0.7 , 2.3 , 5.0 } };
	}

	@java.lang.Override
	public void tearDown() {
		points = null;
	}

	private double[][] points;
}

