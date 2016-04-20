package org.apache.commons.math.fraction;


public class BigFractionFormatTest extends junit.framework.TestCase {
	org.apache.commons.math.fraction.BigFractionFormat properFormat = null;

	org.apache.commons.math.fraction.BigFractionFormat improperFormat = null;

	protected java.util.Locale getLocale() {
		return java.util.Locale.getDefault();
	}

	@java.lang.Override
	protected void setUp() throws java.lang.Exception {
		properFormat = org.apache.commons.math.fraction.BigFractionFormat.getProperInstance(getLocale());
		improperFormat = org.apache.commons.math.fraction.BigFractionFormat.getImproperInstance(getLocale());
	}

	public void testFormat() {
		org.apache.commons.math.fraction.BigFraction c = new org.apache.commons.math.fraction.BigFraction(1 , 2);
		java.lang.String expected = "1 / 2";
		java.lang.String actual = properFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
		actual = improperFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testFormatNegative() {
		org.apache.commons.math.fraction.BigFraction c = new org.apache.commons.math.fraction.BigFraction(-1 , 2);
		java.lang.String expected = "-1 / 2";
		java.lang.String actual = properFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
		actual = improperFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testFormatZero() {
		org.apache.commons.math.fraction.BigFraction c = new org.apache.commons.math.fraction.BigFraction(0 , 1);
		java.lang.String expected = "0 / 1";
		java.lang.String actual = properFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
		actual = improperFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testFormatImproper() {
		org.apache.commons.math.fraction.BigFraction c = new org.apache.commons.math.fraction.BigFraction(5 , 3);
		java.lang.String actual = properFormat.format(c);
		junit.framework.Assert.assertEquals("1 2 / 3", actual);
		actual = improperFormat.format(c);
		junit.framework.Assert.assertEquals("5 / 3", actual);
	}

	public void testFormatImproperNegative() {
		org.apache.commons.math.fraction.BigFraction c = new org.apache.commons.math.fraction.BigFraction(-5 , 3);
		java.lang.String actual = properFormat.format(c);
		junit.framework.Assert.assertEquals("-1 2 / 3", actual);
		actual = improperFormat.format(c);
		junit.framework.Assert.assertEquals("-5 / 3", actual);
	}

	public void testParse() {
		java.lang.String source = "1 / 2";
		try {
			org.apache.commons.math.fraction.BigFraction c = properFormat.parse(source);
			junit.framework.Assert.assertNotNull(c);
			junit.framework.Assert.assertEquals(java.math.BigInteger.ONE, c.getNumerator());
			junit.framework.Assert.assertEquals(java.math.BigInteger.valueOf(2L), c.getDenominator());
			c = improperFormat.parse(source);
			junit.framework.Assert.assertNotNull(c);
			junit.framework.Assert.assertEquals(java.math.BigInteger.ONE, c.getNumerator());
			junit.framework.Assert.assertEquals(java.math.BigInteger.valueOf(2L), c.getDenominator());
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseInteger() {
		java.lang.String source = "10";
		try {
			org.apache.commons.math.fraction.BigFraction c = properFormat.parse(source);
			junit.framework.Assert.assertNotNull(c);
			junit.framework.Assert.assertEquals(java.math.BigInteger.TEN, c.getNumerator());
			junit.framework.Assert.assertEquals(java.math.BigInteger.ONE, c.getDenominator());
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
		try {
			org.apache.commons.math.fraction.BigFraction c = improperFormat.parse(source);
			junit.framework.Assert.assertNotNull(c);
			junit.framework.Assert.assertEquals(java.math.BigInteger.TEN, c.getNumerator());
			junit.framework.Assert.assertEquals(java.math.BigInteger.ONE, c.getDenominator());
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseInvalid() {
		java.lang.String source = "a";
		java.lang.String msg = "should not be able to parse '10 / a'.";
		try {
			properFormat.parse(source);
			junit.framework.Assert.fail(msg);
		} catch (java.text.ParseException ex) {
		}
		try {
			improperFormat.parse(source);
			junit.framework.Assert.fail(msg);
		} catch (java.text.ParseException ex) {
		}
	}

	public void testParseInvalidDenominator() {
		java.lang.String source = "10 / a";
		java.lang.String msg = "should not be able to parse '10 / a'.";
		try {
			properFormat.parse(source);
			junit.framework.Assert.fail(msg);
		} catch (java.text.ParseException ex) {
		}
		try {
			improperFormat.parse(source);
			junit.framework.Assert.fail(msg);
		} catch (java.text.ParseException ex) {
		}
	}

	public void testParseNegative() {
		try {
			java.lang.String source = "-1 / 2";
			org.apache.commons.math.fraction.BigFraction c = properFormat.parse(source);
			junit.framework.Assert.assertNotNull(c);
			junit.framework.Assert.assertEquals(-1, c.getNumeratorAsInt());
			junit.framework.Assert.assertEquals(2, c.getDenominatorAsInt());
			c = improperFormat.parse(source);
			junit.framework.Assert.assertNotNull(c);
			junit.framework.Assert.assertEquals(-1, c.getNumeratorAsInt());
			junit.framework.Assert.assertEquals(2, c.getDenominatorAsInt());
			source = "1 / -2";
			c = properFormat.parse(source);
			junit.framework.Assert.assertNotNull(c);
			junit.framework.Assert.assertEquals(-1, c.getNumeratorAsInt());
			junit.framework.Assert.assertEquals(2, c.getDenominatorAsInt());
			c = improperFormat.parse(source);
			junit.framework.Assert.assertNotNull(c);
			junit.framework.Assert.assertEquals(-1, c.getNumeratorAsInt());
			junit.framework.Assert.assertEquals(2, c.getDenominatorAsInt());
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseProper() {
		java.lang.String source = "1 2 / 3";
		try {
			org.apache.commons.math.fraction.BigFraction c = properFormat.parse(source);
			junit.framework.Assert.assertNotNull(c);
			junit.framework.Assert.assertEquals(5, c.getNumeratorAsInt());
			junit.framework.Assert.assertEquals(3, c.getDenominatorAsInt());
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
		try {
			improperFormat.parse(source);
			junit.framework.Assert.fail("invalid improper fraction.");
		} catch (java.text.ParseException ex) {
		}
	}

	public void testParseProperNegative() {
		java.lang.String source = "-1 2 / 3";
		try {
			org.apache.commons.math.fraction.BigFraction c = properFormat.parse(source);
			junit.framework.Assert.assertNotNull(c);
			junit.framework.Assert.assertEquals(-5, c.getNumeratorAsInt());
			junit.framework.Assert.assertEquals(3, c.getDenominatorAsInt());
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
		try {
			improperFormat.parse(source);
			junit.framework.Assert.fail("invalid improper fraction.");
		} catch (java.text.ParseException ex) {
		}
	}

	public void testParseProperInvalidMinus() {
		java.lang.String source = "2 -2 / 3";
		try {
			properFormat.parse(source);
			junit.framework.Assert.fail("invalid minus in improper fraction.");
		} catch (java.text.ParseException ex) {
		}
		source = "2 2 / -3";
		try {
			properFormat.parse(source);
			junit.framework.Assert.fail("invalid minus in improper fraction.");
		} catch (java.text.ParseException ex) {
		}
	}

	public void testParseBig() throws java.text.ParseException {
		org.apache.commons.math.fraction.BigFraction f1 = improperFormat.parse(("167213075789791382630275400487886041651764456874403" + (" / " + "53225575123090058458126718248444563466137046489291")));
		junit.framework.Assert.assertEquals(java.lang.Math.PI, f1.doubleValue(), 0.0);
		org.apache.commons.math.fraction.BigFraction f2 = properFormat.parse(("3 " + ("7536350420521207255895245742552351253353317406530" + (" / " + "53225575123090058458126718248444563466137046489291"))));
		junit.framework.Assert.assertEquals(java.lang.Math.PI, f2.doubleValue(), 0.0);
		junit.framework.Assert.assertEquals(f1, f2);
		java.math.BigDecimal pi = new java.math.BigDecimal("3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117068");
		junit.framework.Assert.assertEquals(pi, f1.bigDecimalValue(99, java.math.BigDecimal.ROUND_HALF_EVEN));
	}

	public void testNumeratorFormat() {
		java.text.NumberFormat old = properFormat.getNumeratorFormat();
		java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
		nf.setParseIntegerOnly(true);
		properFormat.setNumeratorFormat(nf);
		junit.framework.Assert.assertEquals(nf, properFormat.getNumeratorFormat());
		properFormat.setNumeratorFormat(old);
		old = improperFormat.getNumeratorFormat();
		nf = java.text.NumberFormat.getInstance();
		nf.setParseIntegerOnly(true);
		improperFormat.setNumeratorFormat(nf);
		junit.framework.Assert.assertEquals(nf, improperFormat.getNumeratorFormat());
		improperFormat.setNumeratorFormat(old);
	}

	public void testDenominatorFormat() {
		java.text.NumberFormat old = properFormat.getDenominatorFormat();
		java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
		nf.setParseIntegerOnly(true);
		properFormat.setDenominatorFormat(nf);
		junit.framework.Assert.assertEquals(nf, properFormat.getDenominatorFormat());
		properFormat.setDenominatorFormat(old);
		old = improperFormat.getDenominatorFormat();
		nf = java.text.NumberFormat.getInstance();
		nf.setParseIntegerOnly(true);
		improperFormat.setDenominatorFormat(nf);
		junit.framework.Assert.assertEquals(nf, improperFormat.getDenominatorFormat());
		improperFormat.setDenominatorFormat(old);
	}

	public void testWholeFormat() {
		org.apache.commons.math.fraction.ProperBigFractionFormat format = ((org.apache.commons.math.fraction.ProperBigFractionFormat)(properFormat));
		java.text.NumberFormat old = format.getWholeFormat();
		java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
		nf.setParseIntegerOnly(true);
		format.setWholeFormat(nf);
		junit.framework.Assert.assertEquals(nf, format.getWholeFormat());
		format.setWholeFormat(old);
	}

	public void testLongFormat() {
		junit.framework.Assert.assertEquals("10 / 1", improperFormat.format(10L));
	}

	public void testDoubleFormat() {
		junit.framework.Assert.assertEquals("1 / 16", improperFormat.format(0.0625));
	}
}

