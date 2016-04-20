package org.apache.commons.math.transform;


public final class FastHadamardTransformerTest extends junit.framework.TestCase {
	public void test8Points() {
		checkAllTransforms(new int[]{ 1 , 4 , -2 , 3 , 0 , 1 , 4 , -1 }, new int[]{ 10 , -4 , 2 , -4 , 2 , -12 , 6 , 8 });
	}

	public void test4Points() {
		checkAllTransforms(new int[]{ 1 , 2 , 3 , 4 }, new int[]{ 10 , -2 , -4 , 0 });
	}

	public void testNoIntInverse() {
		org.apache.commons.math.transform.FastHadamardTransformer transformer = new org.apache.commons.math.transform.FastHadamardTransformer();
		double[] x = transformer.inversetransform(new double[]{ 0 , 1 , 0 , 1 });
		junit.framework.Assert.assertEquals(0.5, x[0], 0);
		junit.framework.Assert.assertEquals(-0.5, x[1], 0);
		junit.framework.Assert.assertEquals(0.0, x[2], 0);
		junit.framework.Assert.assertEquals(0.0, x[3], 0);
	}

	public void test3Points() {
		try {
			new org.apache.commons.math.transform.FastHadamardTransformer().transform(new double[3]);
			junit.framework.Assert.fail("an exception should have been thrown");
		} catch (java.lang.IllegalArgumentException iae) {
		}
	}

	private void checkAllTransforms(int[] x, int[] y) {
		checkDoubleTransform(x, y);
		checkInverseDoubleTransform(x, y);
		checkIntTransform(x, y);
	}

	private void checkDoubleTransform(int[] x, int[] y) {
		org.apache.commons.math.transform.FastHadamardTransformer transformer = new org.apache.commons.math.transform.FastHadamardTransformer();
		double[] dX = new double[x.length];
		for (int i = 0 ; i < (dX.length) ; ++i) {
			dX[i] = x[i];
		}
		double[] dResult = transformer.transform(dX);
		for (int i = 0 ; i < (dResult.length) ; i++) {
			junit.framework.Assert.assertEquals(((double)(y[i])), dResult[i]);
		}
	}

	private void checkIntTransform(int[] x, int[] y) {
		org.apache.commons.math.transform.FastHadamardTransformer transformer = new org.apache.commons.math.transform.FastHadamardTransformer();
		int[] iResult = transformer.transform(x);
		for (int i = 0 ; i < (iResult.length) ; i++) {
			junit.framework.Assert.assertEquals(y[i], iResult[i]);
		}
	}

	private void checkInverseDoubleTransform(int[] x, int[] y) {
		org.apache.commons.math.transform.FastHadamardTransformer transformer = new org.apache.commons.math.transform.FastHadamardTransformer();
		double[] dY = new double[y.length];
		for (int i = 0 ; i < (dY.length) ; ++i) {
			dY[i] = y[i];
		}
		double[] dResult = transformer.inversetransform(dY);
		for (int i = 0 ; i < (dResult.length) ; i++) {
			junit.framework.Assert.assertEquals(((double)(x[i])), dResult[i]);
		}
	}
}

