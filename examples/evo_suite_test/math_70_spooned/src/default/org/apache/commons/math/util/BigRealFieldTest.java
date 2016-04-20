package org.apache.commons.math.util;


public class BigRealFieldTest {
	@org.junit.Test
	public void testZero() {
		org.junit.Assert.assertEquals(org.apache.commons.math.util.BigReal.ZERO, org.apache.commons.math.util.BigRealField.getInstance().getZero());
	}

	@org.junit.Test
	public void testOne() {
		org.junit.Assert.assertEquals(org.apache.commons.math.util.BigReal.ONE, org.apache.commons.math.util.BigRealField.getInstance().getOne());
	}

	@org.junit.Test
	public void testSerial() {
		org.apache.commons.math.util.BigRealField field = org.apache.commons.math.util.BigRealField.getInstance();
		org.junit.Assert.assertTrue((field == (org.apache.commons.math.TestUtils.serializeAndRecover(field))));
	}
}

