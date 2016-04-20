package org.apache.commons.math;


public class MathConfigurationExceptionTest extends junit.framework.TestCase {
	public void testConstructor() {
		org.apache.commons.math.MathConfigurationException ex = new org.apache.commons.math.MathConfigurationException();
		junit.framework.Assert.assertNull(ex.getCause());
		junit.framework.Assert.assertEquals("", ex.getMessage());
		junit.framework.Assert.assertEquals("", ex.getMessage(java.util.Locale.FRENCH));
	}

	public void testConstructorPatternArguments() {
		java.lang.String pattern = "a {0}x{1} matrix cannot be a rotation matrix";
		java.lang.Object[] arguments = new java.lang.Object[]{ java.lang.Integer.valueOf(6) , java.lang.Integer.valueOf(4) };
		org.apache.commons.math.MathConfigurationException ex = new org.apache.commons.math.MathConfigurationException(pattern , arguments);
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
		org.apache.commons.math.MathConfigurationException ex = new org.apache.commons.math.MathConfigurationException(cause);
		junit.framework.Assert.assertEquals(cause, ex.getCause());
	}

	public void testConstructorPatternArgumentsCause() {
		java.lang.String pattern = "a {0}x{1} matrix cannot be a rotation matrix";
		java.lang.Object[] arguments = new java.lang.Object[]{ java.lang.Integer.valueOf(6) , java.lang.Integer.valueOf(4) };
		java.lang.String inMsg = "inner message";
		java.lang.Exception cause = new java.lang.Exception(inMsg);
		org.apache.commons.math.MathConfigurationException ex = new org.apache.commons.math.MathConfigurationException(cause , pattern , arguments);
		junit.framework.Assert.assertEquals(cause, ex.getCause());
		junit.framework.Assert.assertEquals(pattern, ex.getPattern());
		junit.framework.Assert.assertEquals(arguments.length, ex.getArguments().length);
		for (int i = 0 ; i < (arguments.length) ; ++i) {
			junit.framework.Assert.assertEquals(arguments[i], ex.getArguments()[i]);
		}
		junit.framework.Assert.assertFalse(pattern.equals(ex.getMessage()));
		junit.framework.Assert.assertFalse(ex.getMessage().equals(ex.getMessage(java.util.Locale.FRENCH)));
	}
}

