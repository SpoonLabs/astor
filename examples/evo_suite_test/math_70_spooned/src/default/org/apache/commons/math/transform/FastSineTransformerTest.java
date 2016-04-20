package org.apache.commons.math.transform;


public final class FastSineTransformerTest extends junit.framework.TestCase {
	public void testAdHocData() {
		org.apache.commons.math.transform.FastSineTransformer transformer = new org.apache.commons.math.transform.FastSineTransformer();
		double[] result;
		double tolerance = 1.0E-12;
		double[] x = new double[]{ 0.0 , 1.0 , 2.0 , 3.0 , 4.0 , 5.0 , 6.0 , 7.0 };
		double[] y = new double[]{ 0.0 , 20.1093579685034 , -9.65685424949238 , 5.98642305066196 , -4.0 , 2.6727145516772 , -1.65685424949238 , 0.795649469518633 };
		result = transformer.transform(x);
		for (int i = 0 ; i < (result.length) ; i++) {
			junit.framework.Assert.assertEquals(y[i], result[i], tolerance);
		}
		result = transformer.inversetransform(y);
		for (int i = 0 ; i < (result.length) ; i++) {
			junit.framework.Assert.assertEquals(x[i], result[i], tolerance);
		}
		org.apache.commons.math.transform.FastFourierTransformer.scaleArray(x, java.lang.Math.sqrt(((x.length) / 2.0)));
		result = transformer.transform2(y);
		for (int i = 0 ; i < (result.length) ; i++) {
			junit.framework.Assert.assertEquals(x[i], result[i], tolerance);
		}
		result = transformer.inversetransform2(x);
		for (int i = 0 ; i < (result.length) ; i++) {
			junit.framework.Assert.assertEquals(y[i], result[i], tolerance);
		}
	}

	public void testSinFunction() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		org.apache.commons.math.transform.FastSineTransformer transformer = new org.apache.commons.math.transform.FastSineTransformer();
		double min;
		double max;
		double[] result;
		double tolerance = 1.0E-12;
		int N = 1 << 8;
		min = 0.0;
		max = 2.0 * (java.lang.Math.PI);
		result = transformer.transform(f, min, max, N);
		junit.framework.Assert.assertEquals((N >> 1), result[2], tolerance);
		for (int i = 0 ; i < N ; i += i == 1 ? 2 : 1) {
			junit.framework.Assert.assertEquals(0.0, result[i], tolerance);
		}
		min = -(java.lang.Math.PI);
		max = java.lang.Math.PI;
		result = transformer.transform(f, min, max, N);
		junit.framework.Assert.assertEquals(-(N >> 1), result[2], tolerance);
		for (int i = 0 ; i < N ; i += i == 1 ? 2 : 1) {
			junit.framework.Assert.assertEquals(0.0, result[i], tolerance);
		}
	}

	public void testParameters() throws java.lang.Exception {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		org.apache.commons.math.transform.FastSineTransformer transformer = new org.apache.commons.math.transform.FastSineTransformer();
		try {
			transformer.transform(f, 1, -1, 64);
			junit.framework.Assert.fail("Expecting IllegalArgumentException - bad interval");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			transformer.transform(f, -1, 1, 0);
			junit.framework.Assert.fail("Expecting IllegalArgumentException - bad samples number");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			transformer.transform(f, -1, 1, 100);
			junit.framework.Assert.fail("Expecting IllegalArgumentException - bad samples number");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}
}

