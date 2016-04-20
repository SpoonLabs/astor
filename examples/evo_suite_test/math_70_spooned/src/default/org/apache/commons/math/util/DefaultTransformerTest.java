package org.apache.commons.math.util;


public class DefaultTransformerTest extends junit.framework.TestCase {
	public void testTransformDouble() throws java.lang.Exception {
		double expected = 1.0;
		java.lang.Double input = java.lang.Double.valueOf(expected);
		org.apache.commons.math.util.DefaultTransformer t = new org.apache.commons.math.util.DefaultTransformer();
		junit.framework.Assert.assertEquals(expected, t.transform(input), 1.0E-4);
	}

	public void testTransformNull() {
		org.apache.commons.math.util.DefaultTransformer t = new org.apache.commons.math.util.DefaultTransformer();
		try {
			t.transform(null);
			junit.framework.Assert.fail("Expection MathException");
		} catch (org.apache.commons.math.MathException e) {
		}
	}

	public void testTransformInteger() throws java.lang.Exception {
		double expected = 1.0;
		java.lang.Integer input = java.lang.Integer.valueOf(1);
		org.apache.commons.math.util.DefaultTransformer t = new org.apache.commons.math.util.DefaultTransformer();
		junit.framework.Assert.assertEquals(expected, t.transform(input), 1.0E-4);
	}

	public void testTransformBigDecimal() throws java.lang.Exception {
		double expected = 1.0;
		java.math.BigDecimal input = new java.math.BigDecimal("1.0");
		org.apache.commons.math.util.DefaultTransformer t = new org.apache.commons.math.util.DefaultTransformer();
		junit.framework.Assert.assertEquals(expected, t.transform(input), 1.0E-4);
	}

	public void testTransformString() throws java.lang.Exception {
		double expected = 1.0;
		java.lang.String input = "1.0";
		org.apache.commons.math.util.DefaultTransformer t = new org.apache.commons.math.util.DefaultTransformer();
		junit.framework.Assert.assertEquals(expected, t.transform(input), 1.0E-4);
	}

	public void testTransformObject() {
		java.lang.Boolean input = java.lang.Boolean.TRUE;
		org.apache.commons.math.util.DefaultTransformer t = new org.apache.commons.math.util.DefaultTransformer();
		try {
			t.transform(input);
			junit.framework.Assert.fail("Expecting MathException");
		} catch (org.apache.commons.math.MathException e) {
		}
	}

	public void testSerial() {
		junit.framework.Assert.assertEquals(new org.apache.commons.math.util.DefaultTransformer(), org.apache.commons.math.TestUtils.serializeAndRecover(new org.apache.commons.math.util.DefaultTransformer()));
	}
}

