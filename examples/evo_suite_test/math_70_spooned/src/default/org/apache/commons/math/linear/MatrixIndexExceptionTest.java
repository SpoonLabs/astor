package org.apache.commons.math.linear;


public class MatrixIndexExceptionTest extends junit.framework.TestCase {
	public void testConstructorMessage() {
		java.lang.String msg = "message";
		org.apache.commons.math.linear.MatrixIndexException ex = new org.apache.commons.math.linear.MatrixIndexException(msg);
		junit.framework.Assert.assertEquals(msg, ex.getMessage());
	}
}

