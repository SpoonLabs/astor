package org.apache.commons.math.complex;


public class ComplexFieldTest {
	@org.junit.Test
	public void testZero() {
		org.junit.Assert.assertEquals(org.apache.commons.math.complex.Complex.ZERO, org.apache.commons.math.complex.ComplexField.getInstance().getZero());
	}

	@org.junit.Test
	public void testOne() {
		org.junit.Assert.assertEquals(org.apache.commons.math.complex.Complex.ONE, org.apache.commons.math.complex.ComplexField.getInstance().getOne());
	}

	@org.junit.Test
	public void testSerial() {
		org.apache.commons.math.complex.ComplexField field = org.apache.commons.math.complex.ComplexField.getInstance();
		org.junit.Assert.assertTrue((field == (org.apache.commons.math.TestUtils.serializeAndRecover(field))));
	}
}

