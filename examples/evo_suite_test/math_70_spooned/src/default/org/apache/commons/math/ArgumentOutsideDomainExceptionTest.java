package org.apache.commons.math;


public class ArgumentOutsideDomainExceptionTest extends junit.framework.TestCase {
	public void testConstructor() {
		org.apache.commons.math.ArgumentOutsideDomainException ex = new org.apache.commons.math.ArgumentOutsideDomainException(java.lang.Math.PI , 10.0 , 20.0);
		junit.framework.Assert.assertNull(ex.getCause());
		junit.framework.Assert.assertNotNull(ex.getMessage());
		junit.framework.Assert.assertTrue(((ex.getMessage().indexOf("3.14")) > 0));
		junit.framework.Assert.assertEquals(java.lang.Math.PI, ex.getArgument()[0], 0);
		junit.framework.Assert.assertFalse(ex.getMessage().equals(ex.getMessage(java.util.Locale.FRENCH)));
	}
}

