package org.apache.commons.math.transform;


public final class FastCosineTransformerTest extends junit.framework.TestCase {
	public void testAdHocData() {
		org.apache.commons.math.transform.FastCosineTransformer transformer = new org.apache.commons.math.transform.FastCosineTransformer();
		double[] result;
		double tolerance = 1.0E-12;
		double[] x = new double[]{ 0.0 , 1.0 , 4.0 , 9.0 , 16.0 , 25.0 , 36.0 , 49.0 , 64.0 };
		double[] y = new double[]{ 172.0 , -105.096569476353 , 27.3137084989848 , -12.9593152353742 , 8.0 , -5.78585076868676 , 4.68629150101524 , -4.15826451958632 , 4.0 };
		result = transformer.transform(x);
		for (int i = 0 ; i < (result.length) ; i++) {
			junit.framework.Assert.assertEquals(y[i], result[i], tolerance);
		}
		result = transformer.inversetransform(y);
		for (int i = 0 ; i < (result.length) ; i++) {
			junit.framework.Assert.assertEquals(x[i], result[i], tolerance);
		}
		org.apache.commons.math.transform.FastFourierTransformer.scaleArray(x, java.lang.Math.sqrt((0.5 * ((x.length) - 1))));
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
		org.apache.commons.math.transform.FastCosineTransformer transformer = new org.apache.commons.math.transform.FastCosineTransformer();
		double min;
		double max;
		double[] result;
		double tolerance = 1.0E-12;
		int N = 9;
		double[] expected = new double[]{ 0.0 , 3.26197262739567 , 0.0 , -2.17958042710327 , 0.0 , -0.648846697642915 , 0.0 , -0.433545502649478 , 0.0 };
		min = 0.0;
		max = ((2.0 * (java.lang.Math.PI)) * N) / (N - 1);
		result = transformer.transform(f, min, max, N);
		for (int i = 0 ; i < N ; i++) {
			junit.framework.Assert.assertEquals(expected[i], result[i], tolerance);
		}
		min = -(java.lang.Math.PI);
		max = ((java.lang.Math.PI) * (N + 1)) / (N - 1);
		result = transformer.transform(f, min, max, N);
		for (int i = 0 ; i < N ; i++) {
			junit.framework.Assert.assertEquals(-(expected[i]), result[i], tolerance);
		}
	}

	public void testParameters() throws java.lang.Exception {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		org.apache.commons.math.transform.FastCosineTransformer transformer = new org.apache.commons.math.transform.FastCosineTransformer();
		try {
			transformer.transform(f, 1, -1, 65);
			junit.framework.Assert.fail("Expecting IllegalArgumentException - bad interval");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			transformer.transform(f, -1, 1, 1);
			junit.framework.Assert.fail("Expecting IllegalArgumentException - bad samples number");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			transformer.transform(f, -1, 1, 64);
			junit.framework.Assert.fail("Expecting IllegalArgumentException - bad samples number");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}
}

