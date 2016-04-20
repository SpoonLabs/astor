package org.apache.commons.math;


public class MathExceptionTest extends junit.framework.TestCase {
	public void testConstructor() {
		org.apache.commons.math.MathException ex = new org.apache.commons.math.MathException();
		junit.framework.Assert.assertNull(ex.getCause());
		junit.framework.Assert.assertEquals("", ex.getMessage());
		junit.framework.Assert.assertEquals("", ex.getMessage(java.util.Locale.FRENCH));
	}

	public void testConstructorPatternArguments() {
		java.lang.String pattern = "a {0}x{1} matrix cannot be a rotation matrix";
		java.lang.Object[] arguments = new java.lang.Object[]{ java.lang.Integer.valueOf(6) , java.lang.Integer.valueOf(4) };
		org.apache.commons.math.MathException ex = new org.apache.commons.math.MathException(pattern , arguments);
		junit.framework.Assert.assertNull(ex.getCause());
		junit.framework.Assert.assertEquals(pattern, ex.getPattern());
		junit.framework.Assert.assertEquals(arguments.length, ex.getArguments().length);
		for (int i = 0 ; i < (arguments.length) ; ++i) {
			junit.framework.Assert.assertEquals(arguments[i], ex.getArguments()[i]);
		}
		junit.framework.Assert.assertFalse(pattern.equals(ex.getMessage()));
		junit.framework.Assert.assertFalse(ex.getMessage().equals(ex.getMessage(java.util.Locale.FRENCH)));
	}

	public void testConstructorCause() {
		java.lang.String inMsg = "inner message";
		java.lang.Exception cause = new java.lang.Exception(inMsg);
		org.apache.commons.math.MathException ex = new org.apache.commons.math.MathException(cause);
		junit.framework.Assert.assertEquals(cause, ex.getCause());
	}

	public void testConstructorPatternArgumentsCause() {
		java.lang.String pattern = "a {0}x{1} matrix cannot be a rotation matrix";
		java.lang.Object[] arguments = new java.lang.Object[]{ java.lang.Integer.valueOf(6) , java.lang.Integer.valueOf(4) };
		java.lang.String inMsg = "inner message";
		java.lang.Exception cause = new java.lang.Exception(inMsg);
		org.apache.commons.math.MathException ex = new org.apache.commons.math.MathException(cause , pattern , arguments);
		junit.framework.Assert.assertEquals(cause, ex.getCause());
		junit.framework.Assert.assertEquals(pattern, ex.getPattern());
		junit.framework.Assert.assertEquals(arguments.length, ex.getArguments().length);
		for (int i = 0 ; i < (arguments.length) ; ++i) {
			junit.framework.Assert.assertEquals(arguments[i], ex.getArguments()[i]);
		}
		junit.framework.Assert.assertFalse(pattern.equals(ex.getMessage()));
		junit.framework.Assert.assertFalse(ex.getMessage().equals(ex.getMessage(java.util.Locale.FRENCH)));
	}

	public void testPrintStackTrace() {
		java.lang.String outMsg = "outer message";
		java.lang.String inMsg = "inner message";
		org.apache.commons.math.MathException cause = new org.apache.commons.math.MathConfigurationException(inMsg);
		org.apache.commons.math.MathException ex = new org.apache.commons.math.MathException(cause , outMsg);
		java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
		java.io.PrintStream ps = new java.io.PrintStream(baos);
		ex.printStackTrace(ps);
		java.lang.String stack = baos.toString();
		java.lang.String outerMsg = "org.apache.commons.math.MathException: outer message";
		java.lang.String innerMsg = "Caused by: " + "org.apache.commons.math.MathConfigurationException: inner message";
		junit.framework.Assert.assertTrue(stack.startsWith(outerMsg));
		junit.framework.Assert.assertTrue(((stack.indexOf(innerMsg)) > 0));
		java.io.PrintWriter pw = new java.io.PrintWriter(ps , true);
		ex.printStackTrace(pw);
		stack = baos.toString();
		junit.framework.Assert.assertTrue(stack.startsWith(outerMsg));
		junit.framework.Assert.assertTrue(((stack.indexOf(innerMsg)) > 0));
	}

	public void testSerialization() {
		java.lang.String outMsg = "outer message";
		java.lang.String inMsg = "inner message";
		org.apache.commons.math.MathException cause = new org.apache.commons.math.MathConfigurationException(inMsg);
		org.apache.commons.math.MathException ex = new org.apache.commons.math.MathException(cause , outMsg);
		org.apache.commons.math.MathException image = ((org.apache.commons.math.MathException)(org.apache.commons.math.TestUtils.serializeAndRecover(ex)));
		java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
		java.io.PrintStream ps = new java.io.PrintStream(baos);
		ex.printStackTrace(ps);
		java.lang.String stack = baos.toString();
		java.io.ByteArrayOutputStream baos2 = new java.io.ByteArrayOutputStream();
		java.io.PrintStream ps2 = new java.io.PrintStream(baos2);
		image.printStackTrace(ps2);
		java.lang.String stack2 = baos2.toString();
		boolean jdkSupportsNesting = false;
		try {
			java.lang.Throwable.class.getDeclaredMethod("getCause", new java.lang.Class[0]);
			jdkSupportsNesting = true;
		} catch (java.lang.NoSuchMethodException e) {
			jdkSupportsNesting = false;
		}
		if (jdkSupportsNesting) {
			junit.framework.Assert.assertEquals(stack, stack2);
		} else {
			junit.framework.Assert.assertTrue(((stack2.indexOf(inMsg)) != (-1)));
			junit.framework.Assert.assertTrue(((stack2.indexOf("MathConfigurationException")) != (-1)));
		}
	}
}

