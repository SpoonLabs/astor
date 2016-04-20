package org.apache.commons.math.util;


public class BigRealTest {
	@org.junit.Test
	public void testConstructor() {
		org.junit.Assert.assertEquals(1.625, new org.apache.commons.math.util.BigReal(new java.math.BigDecimal("1.625")).doubleValue(), 1.0E-15);
		org.junit.Assert.assertEquals(-5.0, new org.apache.commons.math.util.BigReal(new java.math.BigInteger("-5")).doubleValue(), 1.0E-15);
		org.junit.Assert.assertEquals(-5.0, new org.apache.commons.math.util.BigReal(new java.math.BigInteger("-5") , java.math.MathContext.DECIMAL64).doubleValue(), 1.0E-15);
		org.junit.Assert.assertEquals(0.125, new org.apache.commons.math.util.BigReal(new java.math.BigInteger("125") , 3).doubleValue(), 1.0E-15);
		org.junit.Assert.assertEquals(0.125, new org.apache.commons.math.util.BigReal(new java.math.BigInteger("125") , 3 , java.math.MathContext.DECIMAL64).doubleValue(), 1.0E-15);
		org.junit.Assert.assertEquals(1.625, new org.apache.commons.math.util.BigReal(new char[]{ '1' , '.' , '6' , '2' , '5' }).doubleValue(), 1.0E-15);
		org.junit.Assert.assertEquals(1.625, new org.apache.commons.math.util.BigReal(new char[]{ 'A' , 'A' , '1' , '.' , '6' , '2' , '5' , '9' } , 2 , 5).doubleValue(), 1.0E-15);
		org.junit.Assert.assertEquals(1.625, new org.apache.commons.math.util.BigReal(new char[]{ 'A' , 'A' , '1' , '.' , '6' , '2' , '5' , '9' } , 2 , 5 , java.math.MathContext.DECIMAL64).doubleValue(), 1.0E-15);
		org.junit.Assert.assertEquals(1.625, new org.apache.commons.math.util.BigReal(new char[]{ '1' , '.' , '6' , '2' , '5' } , java.math.MathContext.DECIMAL64).doubleValue(), 1.0E-15);
		org.junit.Assert.assertEquals(1.625, new org.apache.commons.math.util.BigReal(1.625).doubleValue(), 1.0E-15);
		org.junit.Assert.assertEquals(1.625, new org.apache.commons.math.util.BigReal(1.625 , java.math.MathContext.DECIMAL64).doubleValue(), 1.0E-15);
		org.junit.Assert.assertEquals(-5.0, new org.apache.commons.math.util.BigReal(-5).doubleValue(), 1.0E-15);
		org.junit.Assert.assertEquals(-5.0, new org.apache.commons.math.util.BigReal(-5 , java.math.MathContext.DECIMAL64).doubleValue(), 1.0E-15);
		org.junit.Assert.assertEquals(-5.0, new org.apache.commons.math.util.BigReal(-5L).doubleValue(), 1.0E-15);
		org.junit.Assert.assertEquals(-5.0, new org.apache.commons.math.util.BigReal(-5L , java.math.MathContext.DECIMAL64).doubleValue(), 1.0E-15);
		org.junit.Assert.assertEquals(1.625, new org.apache.commons.math.util.BigReal("1.625").doubleValue(), 1.0E-15);
		org.junit.Assert.assertEquals(1.625, new org.apache.commons.math.util.BigReal("1.625" , java.math.MathContext.DECIMAL64).doubleValue(), 1.0E-15);
	}

	@org.junit.Test
	public void testCompareTo() {
		org.apache.commons.math.util.BigReal first = new org.apache.commons.math.util.BigReal((1.0 / 2.0));
		org.apache.commons.math.util.BigReal second = new org.apache.commons.math.util.BigReal((1.0 / 3.0));
		org.apache.commons.math.util.BigReal third = new org.apache.commons.math.util.BigReal((1.0 / 2.0));
		org.junit.Assert.assertEquals(0, first.compareTo(first));
		org.junit.Assert.assertEquals(0, first.compareTo(third));
		org.junit.Assert.assertEquals(1, first.compareTo(second));
		org.junit.Assert.assertEquals(-1, second.compareTo(first));
	}

	public void testAdd() {
		org.apache.commons.math.util.BigReal a = new org.apache.commons.math.util.BigReal("1.2345678");
		org.apache.commons.math.util.BigReal b = new org.apache.commons.math.util.BigReal("8.7654321");
		org.junit.Assert.assertEquals(9.9999999, a.add(b).doubleValue(), 1.0E-15);
	}

	public void testSubtract() {
		org.apache.commons.math.util.BigReal a = new org.apache.commons.math.util.BigReal("1.2345678");
		org.apache.commons.math.util.BigReal b = new org.apache.commons.math.util.BigReal("8.7654321");
		org.junit.Assert.assertEquals(-7.5308643, a.subtract(b).doubleValue(), 1.0E-15);
	}

	public void testDivide() {
		org.apache.commons.math.util.BigReal a = new org.apache.commons.math.util.BigReal("1.0000000000");
		org.apache.commons.math.util.BigReal b = new org.apache.commons.math.util.BigReal("0.0009765625");
		org.junit.Assert.assertEquals(1024.0, a.divide(b).doubleValue(), 1.0E-15);
	}

	public void testMultiply() {
		org.apache.commons.math.util.BigReal a = new org.apache.commons.math.util.BigReal("1024.0");
		org.apache.commons.math.util.BigReal b = new org.apache.commons.math.util.BigReal("0.0009765625");
		org.junit.Assert.assertEquals(1.0, a.multiply(b).doubleValue(), 1.0E-15);
	}

	@org.junit.Test
	public void testDoubleValue() {
		org.junit.Assert.assertEquals(0.5, new org.apache.commons.math.util.BigReal(0.5).doubleValue(), 1.0E-15);
	}

	@org.junit.Test
	public void testBigDecimalValue() {
		java.math.BigDecimal pi = new java.math.BigDecimal("3.1415926535897932384626433832795028841971693993751");
		org.junit.Assert.assertEquals(pi, new org.apache.commons.math.util.BigReal(pi).bigDecimalValue());
		org.junit.Assert.assertEquals(new java.math.BigDecimal(0.5), new org.apache.commons.math.util.BigReal((1.0 / 2.0)).bigDecimalValue());
	}

	@org.junit.Test
	public void testEqualsAndHashCode() {
		org.apache.commons.math.util.BigReal zero = new org.apache.commons.math.util.BigReal(0.0);
		org.apache.commons.math.util.BigReal nullReal = null;
		org.junit.Assert.assertTrue(zero.equals(zero));
		org.junit.Assert.assertFalse(zero.equals(nullReal));
		org.junit.Assert.assertFalse(zero.equals(java.lang.Double.valueOf(0)));
		org.apache.commons.math.util.BigReal zero2 = new org.apache.commons.math.util.BigReal(0.0);
		org.junit.Assert.assertTrue(zero.equals(zero2));
		org.junit.Assert.assertEquals(zero.hashCode(), zero2.hashCode());
		org.apache.commons.math.util.BigReal one = new org.apache.commons.math.util.BigReal(1.0);
		org.junit.Assert.assertFalse(((one.equals(zero)) || (zero.equals(one))));
		org.junit.Assert.assertTrue(one.equals(org.apache.commons.math.util.BigReal.ONE));
	}

	public void testSerial() {
		org.apache.commons.math.util.BigReal[] Reals = new org.apache.commons.math.util.BigReal[]{ new org.apache.commons.math.util.BigReal(3.0) , org.apache.commons.math.util.BigReal.ONE , org.apache.commons.math.util.BigReal.ZERO , new org.apache.commons.math.util.BigReal(17) , new org.apache.commons.math.util.BigReal(java.lang.Math.PI) , new org.apache.commons.math.util.BigReal(-2.5) };
		for (org.apache.commons.math.util.BigReal Real : Reals) {
			org.junit.Assert.assertEquals(Real, org.apache.commons.math.TestUtils.serializeAndRecover(Real));
		}
	}
}

