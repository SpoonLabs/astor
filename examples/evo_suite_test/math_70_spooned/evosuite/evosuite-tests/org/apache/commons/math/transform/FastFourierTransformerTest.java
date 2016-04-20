package org.apache.commons.math.transform;


public final class FastFourierTransformerTest extends junit.framework.TestCase {
	public void testAdHocData() {
		org.apache.commons.math.transform.FastFourierTransformer transformer = new org.apache.commons.math.transform.FastFourierTransformer();
		org.apache.commons.math.complex.Complex[] result;
		double tolerance = 1.0E-12;
		double[] x = new double[]{ 1.3 , 2.4 , 1.7 , 4.1 , 2.9 , 1.7 , 5.1 , 2.7 };
		org.apache.commons.math.complex.Complex[] y = new org.apache.commons.math.complex.Complex[]{ new org.apache.commons.math.complex.Complex(21.9 , 0.0) , new org.apache.commons.math.complex.Complex(-2.09497474683058 , 1.91507575950825) , new org.apache.commons.math.complex.Complex(-2.6 , 2.7) , new org.apache.commons.math.complex.Complex(-1.10502525316942 , -4.88492424049175) , new org.apache.commons.math.complex.Complex(0.1 , 0.0) , new org.apache.commons.math.complex.Complex(-1.10502525316942 , 4.88492424049175) , new org.apache.commons.math.complex.Complex(-2.6 , -2.7) , new org.apache.commons.math.complex.Complex(-2.09497474683058 , -1.91507575950825) };
		result = transformer.transform(x);
		for (int i = 0 ; i < (result.length) ; i++) {
			junit.framework.Assert.assertEquals(y[i].getReal(), result[i].getReal(), tolerance);
			junit.framework.Assert.assertEquals(y[i].getImaginary(), result[i].getImaginary(), tolerance);
		}
		result = transformer.inversetransform(y);
		for (int i = 0 ; i < (result.length) ; i++) {
			junit.framework.Assert.assertEquals(x[i], result[i].getReal(), tolerance);
			junit.framework.Assert.assertEquals(0.0, result[i].getImaginary(), tolerance);
		}
		double[] x2 = new double[]{ 10.4 , 21.6 , 40.8 , 13.6 , 23.2 , 32.8 , 13.6 , 19.2 };
		org.apache.commons.math.transform.FastFourierTransformer.scaleArray(x2, (1.0 / (java.lang.Math.sqrt(x2.length))));
		org.apache.commons.math.complex.Complex[] y2 = y;
		result = transformer.transform2(y2);
		for (int i = 0 ; i < (result.length) ; i++) {
			junit.framework.Assert.assertEquals(x2[i], result[i].getReal(), tolerance);
			junit.framework.Assert.assertEquals(0.0, result[i].getImaginary(), tolerance);
		}
		result = transformer.inversetransform2(x2);
		for (int i = 0 ; i < (result.length) ; i++) {
			junit.framework.Assert.assertEquals(y2[i].getReal(), result[i].getReal(), tolerance);
			junit.framework.Assert.assertEquals(y2[i].getImaginary(), result[i].getImaginary(), tolerance);
		}
	}

	public void test2DData() {
		org.apache.commons.math.transform.FastFourierTransformer transformer = new org.apache.commons.math.transform.FastFourierTransformer();
		double tolerance = 1.0E-12;
		org.apache.commons.math.complex.Complex[][] input = new org.apache.commons.math.complex.Complex[][]{ new org.apache.commons.math.complex.Complex[]{ new org.apache.commons.math.complex.Complex(1 , 0) , new org.apache.commons.math.complex.Complex(2 , 0) } , new org.apache.commons.math.complex.Complex[]{ new org.apache.commons.math.complex.Complex(3 , 1) , new org.apache.commons.math.complex.Complex(4 , 2) } };
		org.apache.commons.math.complex.Complex[][] goodOutput = new org.apache.commons.math.complex.Complex[][]{ new org.apache.commons.math.complex.Complex[]{ new org.apache.commons.math.complex.Complex(5 , 1.5) , new org.apache.commons.math.complex.Complex(-1 , -0.5) } , new org.apache.commons.math.complex.Complex[]{ new org.apache.commons.math.complex.Complex(-2 , -1.5) , new org.apache.commons.math.complex.Complex(0 , 0.5) } };
		org.apache.commons.math.complex.Complex[][] output = ((org.apache.commons.math.complex.Complex[][])(transformer.mdfft(input, true)));
		org.apache.commons.math.complex.Complex[][] output2 = ((org.apache.commons.math.complex.Complex[][])(transformer.mdfft(output, false)));
		junit.framework.Assert.assertEquals(input.length, output.length);
		junit.framework.Assert.assertEquals(input.length, output2.length);
		junit.framework.Assert.assertEquals(input[0].length, output[0].length);
		junit.framework.Assert.assertEquals(input[0].length, output2[0].length);
		junit.framework.Assert.assertEquals(input[1].length, output[1].length);
		junit.framework.Assert.assertEquals(input[1].length, output2[1].length);
		for (int i = 0 ; i < (input.length) ; i++) {
			for (int j = 0 ; j < (input[0].length) ; j++) {
				junit.framework.Assert.assertEquals(input[i][j].getImaginary(), output2[i][j].getImaginary(), tolerance);
				junit.framework.Assert.assertEquals(input[i][j].getReal(), output2[i][j].getReal(), tolerance);
				junit.framework.Assert.assertEquals(goodOutput[i][j].getImaginary(), output[i][j].getImaginary(), tolerance);
				junit.framework.Assert.assertEquals(goodOutput[i][j].getReal(), output[i][j].getReal(), tolerance);
			}
		}
	}

	public void testSinFunction() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		org.apache.commons.math.transform.FastFourierTransformer transformer = new org.apache.commons.math.transform.FastFourierTransformer();
		org.apache.commons.math.complex.Complex[] result;
		int N = 1 << 8;
		double min;
		double max;
		double tolerance = 1.0E-12;
		min = 0.0;
		max = 2.0 * (java.lang.Math.PI);
		result = transformer.transform(f, min, max, N);
		junit.framework.Assert.assertEquals(0.0, result[1].getReal(), tolerance);
		junit.framework.Assert.assertEquals(-(N >> 1), result[1].getImaginary(), tolerance);
		junit.framework.Assert.assertEquals(0.0, result[(N - 1)].getReal(), tolerance);
		junit.framework.Assert.assertEquals((N >> 1), result[(N - 1)].getImaginary(), tolerance);
		for (int i = 0 ; i < (N - 1) ; i += i == 0 ? 2 : 1) {
			junit.framework.Assert.assertEquals(0.0, result[i].getReal(), tolerance);
			junit.framework.Assert.assertEquals(0.0, result[i].getImaginary(), tolerance);
		}
		min = -(java.lang.Math.PI);
		max = java.lang.Math.PI;
		result = transformer.inversetransform(f, min, max, N);
		junit.framework.Assert.assertEquals(0.0, result[1].getReal(), tolerance);
		junit.framework.Assert.assertEquals(-0.5, result[1].getImaginary(), tolerance);
		junit.framework.Assert.assertEquals(0.0, result[(N - 1)].getReal(), tolerance);
		junit.framework.Assert.assertEquals(0.5, result[(N - 1)].getImaginary(), tolerance);
		for (int i = 0 ; i < (N - 1) ; i += i == 0 ? 2 : 1) {
			junit.framework.Assert.assertEquals(0.0, result[i].getReal(), tolerance);
			junit.framework.Assert.assertEquals(0.0, result[i].getImaginary(), tolerance);
		}
	}

	public void testParameters() throws java.lang.Exception {
		org.apache.commons.math.analysis.UnivariateRealFunction f = new org.apache.commons.math.analysis.SinFunction();
		org.apache.commons.math.transform.FastFourierTransformer transformer = new org.apache.commons.math.transform.FastFourierTransformer();
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

