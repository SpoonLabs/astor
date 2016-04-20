package org.apache.commons.math;


public class MaxIterationsExceededExceptionTest extends junit.framework.TestCase {
	public void testSimpleConstructor() {
		org.apache.commons.math.MaxIterationsExceededException ex = new org.apache.commons.math.MaxIterationsExceededException(1000000);
		junit.framework.Assert.assertNull(ex.getCause());
		junit.framework.Assert.assertNotNull(ex.getMessage());
		junit.framework.Assert.assertTrue(((ex.getMessage().indexOf("1,000,000")) > 0));
		junit.framework.Assert.assertEquals(1000000, ex.getMaxIterations());
		junit.framework.Assert.assertFalse(ex.getMessage().equals(ex.getMessage(java.util.Locale.FRENCH)));
	}

	public void testComplexConstructor() {
		org.apache.commons.math.MaxIterationsExceededException ex = new org.apache.commons.math.MaxIterationsExceededException(1000000 , "Continued fraction convergents failed to converge for value {0}" , 1234567);
		junit.framework.Assert.assertNull(ex.getCause());
		junit.framework.Assert.assertNotNull(ex.getMessage());
		junit.framework.Assert.assertTrue(((ex.getMessage().indexOf("1,000,000")) < 0));
		junit.framework.Assert.assertTrue(((ex.getMessage().indexOf("1,234,567")) > 0));
		junit.framework.Assert.assertEquals(1000000, ex.getMaxIterations());
		junit.framework.Assert.assertFalse(ex.getMessage().equals(ex.getMessage(java.util.Locale.FRENCH)));
	}
}

