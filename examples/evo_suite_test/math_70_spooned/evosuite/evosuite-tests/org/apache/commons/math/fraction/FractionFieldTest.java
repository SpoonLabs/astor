package org.apache.commons.math.fraction;


public class FractionFieldTest {
	@org.junit.Test
	public void testZero() {
		org.junit.Assert.assertEquals(org.apache.commons.math.fraction.Fraction.ZERO, org.apache.commons.math.fraction.FractionField.getInstance().getZero());
	}

	@org.junit.Test
	public void testOne() {
		org.junit.Assert.assertEquals(org.apache.commons.math.fraction.Fraction.ONE, org.apache.commons.math.fraction.FractionField.getInstance().getOne());
	}

	@org.junit.Test
	public void testSerial() {
		org.apache.commons.math.fraction.FractionField field = org.apache.commons.math.fraction.FractionField.getInstance();
		org.junit.Assert.assertTrue((field == (org.apache.commons.math.TestUtils.serializeAndRecover(field))));
	}
}

