package org.apache.commons.math.fraction;


public class BigFractionFieldTest {
	@org.junit.Test
	public void testZero() {
		org.junit.Assert.assertEquals(org.apache.commons.math.fraction.BigFraction.ZERO, org.apache.commons.math.fraction.BigFractionField.getInstance().getZero());
	}

	@org.junit.Test
	public void testOne() {
		org.junit.Assert.assertEquals(org.apache.commons.math.fraction.BigFraction.ONE, org.apache.commons.math.fraction.BigFractionField.getInstance().getOne());
	}

	@org.junit.Test
	public void testSerial() {
		org.apache.commons.math.fraction.BigFractionField field = org.apache.commons.math.fraction.BigFractionField.getInstance();
		org.junit.Assert.assertTrue((field == (org.apache.commons.math.TestUtils.serializeAndRecover(field))));
	}
}

