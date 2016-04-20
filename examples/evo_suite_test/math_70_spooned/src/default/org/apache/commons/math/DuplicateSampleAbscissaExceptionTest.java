package org.apache.commons.math;


public class DuplicateSampleAbscissaExceptionTest extends junit.framework.TestCase {
	public void testConstructor() {
		org.apache.commons.math.DuplicateSampleAbscissaException ex = new org.apache.commons.math.DuplicateSampleAbscissaException(1.2 , 10 , 11);
		junit.framework.Assert.assertNull(ex.getCause());
		junit.framework.Assert.assertNotNull(ex.getMessage());
		junit.framework.Assert.assertTrue(((ex.getMessage().indexOf("1.2")) > 0));
		junit.framework.Assert.assertEquals(1.2, ex.getDuplicateAbscissa(), 0);
		junit.framework.Assert.assertFalse(ex.getMessage().equals(ex.getMessage(java.util.Locale.FRENCH)));
	}
}

