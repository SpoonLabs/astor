package org.apache.commons.math.linear;


public class InvalidMatrixExceptionTest extends junit.framework.TestCase {
	public void testConstructorMessage() {
		java.lang.String msg = "message";
		org.apache.commons.math.linear.InvalidMatrixException ex = new org.apache.commons.math.linear.InvalidMatrixException(msg);
		junit.framework.Assert.assertEquals(msg, ex.getMessage());
	}
}

