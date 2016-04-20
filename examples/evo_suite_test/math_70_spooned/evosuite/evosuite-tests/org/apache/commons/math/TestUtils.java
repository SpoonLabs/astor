package org.apache.commons.math;


public class TestUtils {
	private TestUtils() {
		super();
	}

	public static void assertEquals(double expected, double actual, double delta) {
		org.apache.commons.math.TestUtils.assertEquals(null, expected, actual, delta);
	}

	public static void assertEquals(java.lang.String msg, double expected, double actual, double delta) {
		if (java.lang.Double.isNaN(expected)) {
			junit.framework.Assert.assertTrue((("" + actual) + " is not NaN."), java.lang.Double.isNaN(actual));
		} else {
			junit.framework.Assert.assertEquals(msg, expected, actual, delta);
		}
	}

	public static void assertSame(double expected, double actual) {
		org.apache.commons.math.TestUtils.assertEquals(expected, actual, 0);
	}

	public static void assertSame(org.apache.commons.math.complex.Complex expected, org.apache.commons.math.complex.Complex actual) {
		org.apache.commons.math.TestUtils.assertSame(expected.getReal(), actual.getReal());
		org.apache.commons.math.TestUtils.assertSame(expected.getImaginary(), actual.getImaginary());
	}

	public static void assertEquals(org.apache.commons.math.complex.Complex expected, org.apache.commons.math.complex.Complex actual, double delta) {
		org.apache.commons.math.TestUtils.assertEquals(expected.getReal(), actual.getReal(), delta);
		org.apache.commons.math.TestUtils.assertEquals(expected.getImaginary(), actual.getImaginary(), delta);
	}

	public static void assertEquals(double[] expected, double[] observed, double tolerance) {
		org.apache.commons.math.TestUtils.assertEquals("Array comparison failure", expected, observed, tolerance);
	}

	public static java.lang.Object serializeAndRecover(java.lang.Object o) {
		try {
			java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
			java.io.ObjectOutputStream so = new java.io.ObjectOutputStream(bos);
			so.writeObject(o);
			java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(bos.toByteArray());
			java.io.ObjectInputStream si = new java.io.ObjectInputStream(bis);
			return si.readObject();
		} catch (java.io.IOException ioe) {
			return null;
		} catch (java.lang.ClassNotFoundException cnfe) {
			return null;
		}
	}

	public static void checkSerializedEquality(java.lang.Object object) {
		java.lang.Object object2 = org.apache.commons.math.TestUtils.serializeAndRecover(object);
		junit.framework.Assert.assertEquals("Equals check", object, object2);
		junit.framework.Assert.assertEquals("HashCode check", object.hashCode(), object2.hashCode());
	}

	public static void assertRelativelyEquals(double expected, double actual, double relativeError) {
		org.apache.commons.math.TestUtils.assertRelativelyEquals(null, expected, actual, relativeError);
	}

	public static void assertRelativelyEquals(java.lang.String msg, double expected, double actual, double relativeError) {
		if (java.lang.Double.isNaN(expected)) {
			junit.framework.Assert.assertTrue(msg, java.lang.Double.isNaN(actual));
		} else {
			if (java.lang.Double.isNaN(actual)) {
				junit.framework.Assert.assertTrue(msg, java.lang.Double.isNaN(expected));
			} else {
				if ((java.lang.Double.isInfinite(actual)) || (java.lang.Double.isInfinite(expected))) {
					junit.framework.Assert.assertEquals(expected, actual, relativeError);
				} else {
					if (expected == 0.0) {
						junit.framework.Assert.assertEquals(msg, actual, expected, relativeError);
					} else {
						double absError = (java.lang.Math.abs(expected)) * relativeError;
						junit.framework.Assert.assertEquals(msg, expected, actual, absError);
					}
				}
			}
		}
	}

	public static void assertContains(java.lang.String msg, org.apache.commons.math.complex.Complex[] values, org.apache.commons.math.complex.Complex z, double epsilon) {
		int i = 0;
		boolean found = false;
		while ((!found) && (i < (values.length))) {
			try {
				org.apache.commons.math.TestUtils.assertEquals(values[i], z, epsilon);
				found = true;
			} catch (junit.framework.AssertionFailedError er) {
			}
			i++;
		}
		if (!found) {
			junit.framework.Assert.fail(((msg + " Unable to find ") + (org.apache.commons.math.complex.ComplexFormat.formatComplex(z))));
		} 
	}

	public static void assertContains(org.apache.commons.math.complex.Complex[] values, org.apache.commons.math.complex.Complex z, double epsilon) {
		org.apache.commons.math.TestUtils.assertContains(null, values, z, epsilon);
	}

	public static void assertContains(java.lang.String msg, double[] values, double x, double epsilon) {
		int i = 0;
		boolean found = false;
		while ((!found) && (i < (values.length))) {
			try {
				org.apache.commons.math.TestUtils.assertEquals(values[i], x, epsilon);
				found = true;
			} catch (junit.framework.AssertionFailedError er) {
			}
			i++;
		}
		if (!found) {
			junit.framework.Assert.fail(((msg + " Unable to find") + x));
		} 
	}

	public static void assertContains(double[] values, double x, double epsilon) {
		org.apache.commons.math.TestUtils.assertContains(null, values, x, epsilon);
	}

	public static void assertEquals(java.lang.String msg, org.apache.commons.math.linear.RealMatrix expected, org.apache.commons.math.linear.RealMatrix observed, double tolerance) {
		junit.framework.Assert.assertNotNull((msg + "\nObserved should not be null"), observed);
		if (((expected.getColumnDimension()) != (observed.getColumnDimension())) || ((expected.getRowDimension()) != (observed.getRowDimension()))) {
			java.lang.StringBuffer messageBuffer = new java.lang.StringBuffer(msg);
			messageBuffer.append("\nObserved has incorrect dimensions.");
			messageBuffer.append(((("\nobserved is " + (observed.getRowDimension())) + " x ") + (observed.getColumnDimension())));
			messageBuffer.append(((("\nexpected " + (expected.getRowDimension())) + " x ") + (expected.getColumnDimension())));
			junit.framework.Assert.fail(messageBuffer.toString());
		} 
		org.apache.commons.math.linear.RealMatrix delta = expected.subtract(observed);
		if ((delta.getNorm()) >= tolerance) {
			java.lang.StringBuffer messageBuffer = new java.lang.StringBuffer(msg);
			messageBuffer.append(("\nExpected: " + expected));
			messageBuffer.append(("\nObserved: " + observed));
			messageBuffer.append(("\nexpected - observed: " + delta));
			junit.framework.Assert.fail(messageBuffer.toString());
		} 
	}

	public static void assertEquals(org.apache.commons.math.linear.FieldMatrix<? extends org.apache.commons.math.FieldElement<?>> expected, org.apache.commons.math.linear.FieldMatrix<? extends org.apache.commons.math.FieldElement<?>> observed) {
		junit.framework.Assert.assertNotNull("Observed should not be null", observed);
		if (((expected.getColumnDimension()) != (observed.getColumnDimension())) || ((expected.getRowDimension()) != (observed.getRowDimension()))) {
			java.lang.StringBuffer messageBuffer = new java.lang.StringBuffer();
			messageBuffer.append("Observed has incorrect dimensions.");
			messageBuffer.append(((("\nobserved is " + (observed.getRowDimension())) + " x ") + (observed.getColumnDimension())));
			messageBuffer.append(((("\nexpected " + (expected.getRowDimension())) + " x ") + (expected.getColumnDimension())));
			junit.framework.Assert.fail(messageBuffer.toString());
		} 
		for (int i = 0 ; i < (expected.getRowDimension()) ; ++i) {
			for (int j = 0 ; j < (expected.getColumnDimension()) ; ++j) {
				org.apache.commons.math.FieldElement<?> eij = expected.getEntry(i, j);
				org.apache.commons.math.FieldElement<?> oij = observed.getEntry(i, j);
				junit.framework.Assert.assertEquals(eij, oij);
			}
		}
	}

	public static void assertEquals(java.lang.String msg, double[] expected, double[] observed, double tolerance) {
		java.lang.StringBuffer out = new java.lang.StringBuffer(msg);
		if ((expected.length) != (observed.length)) {
			out.append("\n Arrays not same length. \n");
			out.append("expected has length ");
			out.append(expected.length);
			out.append(" observed length = ");
			out.append(observed.length);
			junit.framework.Assert.fail(out.toString());
		} 
		boolean failure = false;
		for (int i = 0 ; i < (expected.length) ; i++) {
			try {
				org.apache.commons.math.TestUtils.assertEquals(expected[i], observed[i], tolerance);
			} catch (junit.framework.AssertionFailedError ex) {
				failure = true;
				out.append("\n Elements at index ");
				out.append(i);
				out.append(" differ. ");
				out.append(" expected = ");
				out.append(expected[i]);
				out.append(" observed = ");
				out.append(observed[i]);
			}
		}
		if (failure) {
			junit.framework.Assert.fail(out.toString());
		} 
	}

	public static <T extends org.apache.commons.math.FieldElement<T>>void assertEquals(T[] m, T[] n) {
		if ((m.length) != (n.length)) {
			junit.framework.Assert.fail("vectors not same length");
		} 
		for (int i = 0 ; i < (m.length) ; i++) {
			junit.framework.Assert.assertEquals(m[i], n[i]);
		}
	}

	public static double sumSquareDev(double[] values, double target) {
		double sumsq = 0.0;
		for (int i = 0 ; i < (values.length) ; i++) {
			final double dev = (values[i]) - target;
			sumsq += dev * dev;
		}
		return sumsq;
	}
}

