package org.apache.commons.math.complex;


public abstract class ComplexFormatAbstractTest extends junit.framework.TestCase {
	org.apache.commons.math.util.CompositeFormat complexFormat = null;

	org.apache.commons.math.complex.ComplexFormat complexFormatJ = null;

	protected abstract java.util.Locale getLocale();

	protected abstract char getDecimalCharacter();

	@java.lang.Override
	protected void setUp() throws java.lang.Exception {
		complexFormat = org.apache.commons.math.complex.ComplexFormat.getInstance(getLocale());
		complexFormatJ = org.apache.commons.math.complex.ComplexFormat.getInstance(getLocale());
		complexFormatJ.setImaginaryCharacter("j");
	}

	public void testSimpleNoDecimals() {
		org.apache.commons.math.complex.Complex c = new org.apache.commons.math.complex.Complex(1 , 1);
		java.lang.String expected = "1 + 1i";
		java.lang.String actual = complexFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testSimpleWithDecimals() {
		org.apache.commons.math.complex.Complex c = new org.apache.commons.math.complex.Complex(1.23 , 1.43);
		java.lang.String expected = ((("1" + (getDecimalCharacter())) + "23 + 1") + (getDecimalCharacter())) + "43i";
		java.lang.String actual = complexFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testSimpleWithDecimalsTrunc() {
		org.apache.commons.math.complex.Complex c = new org.apache.commons.math.complex.Complex(1.2323 , 1.4343);
		java.lang.String expected = ((("1" + (getDecimalCharacter())) + "23 + 1") + (getDecimalCharacter())) + "43i";
		java.lang.String actual = complexFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testNegativeReal() {
		org.apache.commons.math.complex.Complex c = new org.apache.commons.math.complex.Complex(-1.2323 , 1.4343);
		java.lang.String expected = ((("-1" + (getDecimalCharacter())) + "23 + 1") + (getDecimalCharacter())) + "43i";
		java.lang.String actual = complexFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testNegativeImaginary() {
		org.apache.commons.math.complex.Complex c = new org.apache.commons.math.complex.Complex(1.2323 , -1.4343);
		java.lang.String expected = ((("1" + (getDecimalCharacter())) + "23 - 1") + (getDecimalCharacter())) + "43i";
		java.lang.String actual = complexFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testNegativeBoth() {
		org.apache.commons.math.complex.Complex c = new org.apache.commons.math.complex.Complex(-1.2323 , -1.4343);
		java.lang.String expected = ((("-1" + (getDecimalCharacter())) + "23 - 1") + (getDecimalCharacter())) + "43i";
		java.lang.String actual = complexFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testZeroReal() {
		org.apache.commons.math.complex.Complex c = new org.apache.commons.math.complex.Complex(0.0 , -1.4343);
		java.lang.String expected = ("0 - 1" + (getDecimalCharacter())) + "43i";
		java.lang.String actual = complexFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testZeroImaginary() {
		org.apache.commons.math.complex.Complex c = new org.apache.commons.math.complex.Complex(30.233 , 0);
		java.lang.String expected = ("30" + (getDecimalCharacter())) + "23";
		java.lang.String actual = complexFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testDifferentImaginaryChar() {
		org.apache.commons.math.complex.Complex c = new org.apache.commons.math.complex.Complex(1 , 1);
		java.lang.String expected = "1 + 1j";
		java.lang.String actual = complexFormatJ.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testStaticFormatComplex() {
		java.util.Locale defaultLocal = java.util.Locale.getDefault();
		java.util.Locale.setDefault(getLocale());
		org.apache.commons.math.complex.Complex c = new org.apache.commons.math.complex.Complex(232.222 , -342.33);
		java.lang.String expected = ((("232" + (getDecimalCharacter())) + "22 - 342") + (getDecimalCharacter())) + "33i";
		java.lang.String actual = org.apache.commons.math.complex.ComplexFormat.formatComplex(c);
		junit.framework.Assert.assertEquals(expected, actual);
		java.util.Locale.setDefault(defaultLocal);
	}

	public void testNan() {
		org.apache.commons.math.complex.Complex c = new org.apache.commons.math.complex.Complex(java.lang.Double.NaN , java.lang.Double.NaN);
		java.lang.String expected = "(NaN) + (NaN)i";
		java.lang.String actual = complexFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testPositiveInfinity() {
		org.apache.commons.math.complex.Complex c = new org.apache.commons.math.complex.Complex(java.lang.Double.POSITIVE_INFINITY , java.lang.Double.POSITIVE_INFINITY);
		java.lang.String expected = "(Infinity) + (Infinity)i";
		java.lang.String actual = complexFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testNegativeInfinity() {
		org.apache.commons.math.complex.Complex c = new org.apache.commons.math.complex.Complex(java.lang.Double.NEGATIVE_INFINITY , java.lang.Double.NEGATIVE_INFINITY);
		java.lang.String expected = "(-Infinity) - (Infinity)i";
		java.lang.String actual = complexFormat.format(c);
		junit.framework.Assert.assertEquals(expected, actual);
	}

	public void testParseSimpleNoDecimals() {
		java.lang.String source = "1 + 1i";
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(1 , 1);
		try {
			org.apache.commons.math.complex.Complex actual = ((org.apache.commons.math.complex.Complex)(complexFormat.parseObject(source)));
			junit.framework.Assert.assertEquals(expected, actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseSimpleWithDecimals() {
		java.lang.String source = ((("1" + (getDecimalCharacter())) + "23 + 1") + (getDecimalCharacter())) + "43i";
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(1.23 , 1.43);
		try {
			org.apache.commons.math.complex.Complex actual = ((org.apache.commons.math.complex.Complex)(complexFormat.parseObject(source)));
			junit.framework.Assert.assertEquals(expected, actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseSimpleWithDecimalsTrunc() {
		java.lang.String source = ((("1" + (getDecimalCharacter())) + "2323 + 1") + (getDecimalCharacter())) + "4343i";
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(1.2323 , 1.4343);
		try {
			org.apache.commons.math.complex.Complex actual = ((org.apache.commons.math.complex.Complex)(complexFormat.parseObject(source)));
			junit.framework.Assert.assertEquals(expected, actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseNegativeReal() {
		java.lang.String source = ((("-1" + (getDecimalCharacter())) + "2323 + 1") + (getDecimalCharacter())) + "4343i";
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(-1.2323 , 1.4343);
		try {
			org.apache.commons.math.complex.Complex actual = ((org.apache.commons.math.complex.Complex)(complexFormat.parseObject(source)));
			junit.framework.Assert.assertEquals(expected, actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseNegativeImaginary() {
		java.lang.String source = ((("1" + (getDecimalCharacter())) + "2323 - 1") + (getDecimalCharacter())) + "4343i";
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(1.2323 , -1.4343);
		try {
			org.apache.commons.math.complex.Complex actual = ((org.apache.commons.math.complex.Complex)(complexFormat.parseObject(source)));
			junit.framework.Assert.assertEquals(expected, actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseNegativeBoth() {
		java.lang.String source = ((("-1" + (getDecimalCharacter())) + "2323 - 1") + (getDecimalCharacter())) + "4343i";
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(-1.2323 , -1.4343);
		try {
			org.apache.commons.math.complex.Complex actual = ((org.apache.commons.math.complex.Complex)(complexFormat.parseObject(source)));
			junit.framework.Assert.assertEquals(expected, actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseZeroReal() {
		java.lang.String source = ((("0" + (getDecimalCharacter())) + "0 - 1") + (getDecimalCharacter())) + "4343i";
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(0.0 , -1.4343);
		try {
			org.apache.commons.math.complex.Complex actual = ((org.apache.commons.math.complex.Complex)(complexFormat.parseObject(source)));
			junit.framework.Assert.assertEquals(expected, actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseZeroImaginary() {
		java.lang.String source = ("-1" + (getDecimalCharacter())) + "2323";
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(-1.2323 , 0);
		try {
			org.apache.commons.math.complex.Complex actual = ((org.apache.commons.math.complex.Complex)(complexFormat.parseObject(source)));
			junit.framework.Assert.assertEquals(expected, actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseDifferentImaginaryChar() {
		java.lang.String source = ((("-1" + (getDecimalCharacter())) + "2323 - 1") + (getDecimalCharacter())) + "4343j";
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(-1.2323 , -1.4343);
		try {
			org.apache.commons.math.complex.Complex actual = ((org.apache.commons.math.complex.Complex)(complexFormatJ.parseObject(source)));
			junit.framework.Assert.assertEquals(expected, actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParseNan() {
		java.lang.String source = "(NaN) + (NaN)i";
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(java.lang.Double.NaN , java.lang.Double.NaN);
		try {
			org.apache.commons.math.complex.Complex actual = ((org.apache.commons.math.complex.Complex)(complexFormat.parseObject(source)));
			junit.framework.Assert.assertEquals(expected, actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testParsePositiveInfinity() {
		java.lang.String source = "(Infinity) + (Infinity)i";
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(java.lang.Double.POSITIVE_INFINITY , java.lang.Double.POSITIVE_INFINITY);
		try {
			org.apache.commons.math.complex.Complex actual = ((org.apache.commons.math.complex.Complex)(complexFormat.parseObject(source)));
			junit.framework.Assert.assertEquals(expected, actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testPaseNegativeInfinity() {
		java.lang.String source = "(-Infinity) - (Infinity)i";
		org.apache.commons.math.complex.Complex expected = new org.apache.commons.math.complex.Complex(java.lang.Double.NEGATIVE_INFINITY , java.lang.Double.NEGATIVE_INFINITY);
		try {
			org.apache.commons.math.complex.Complex actual = ((org.apache.commons.math.complex.Complex)(complexFormat.parseObject(source)));
			junit.framework.Assert.assertEquals(expected, actual);
		} catch (java.text.ParseException ex) {
			junit.framework.Assert.fail(ex.getMessage());
		}
	}

	public void testConstructorSingleFormat() {
		java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
		org.apache.commons.math.complex.ComplexFormat cf = new org.apache.commons.math.complex.ComplexFormat(nf);
		junit.framework.Assert.assertNotNull(cf);
		junit.framework.Assert.assertEquals(nf, cf.getRealFormat());
	}

	public void testGetImaginaryFormat() {
		java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
		org.apache.commons.math.complex.ComplexFormat cf = new org.apache.commons.math.complex.ComplexFormat();
		junit.framework.Assert.assertNotSame(nf, cf.getImaginaryFormat());
		cf.setImaginaryFormat(nf);
		junit.framework.Assert.assertSame(nf, cf.getImaginaryFormat());
	}

	public void testSetImaginaryFormatNull() {
		try {
			org.apache.commons.math.complex.ComplexFormat cf = new org.apache.commons.math.complex.ComplexFormat();
			cf.setImaginaryFormat(null);
			junit.framework.Assert.fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testSetRealFormatNull() {
		try {
			org.apache.commons.math.complex.ComplexFormat cf = new org.apache.commons.math.complex.ComplexFormat();
			cf.setRealFormat(null);
			junit.framework.Assert.fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testGetRealFormat() {
		java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
		org.apache.commons.math.complex.ComplexFormat cf = new org.apache.commons.math.complex.ComplexFormat();
		junit.framework.Assert.assertNotSame(nf, cf.getRealFormat());
		cf.setRealFormat(nf);
		junit.framework.Assert.assertSame(nf, cf.getRealFormat());
	}

	public void testSetImaginaryCharacterNull() {
		try {
			org.apache.commons.math.complex.ComplexFormat cf = new org.apache.commons.math.complex.ComplexFormat();
			cf.setImaginaryCharacter(null);
			junit.framework.Assert.fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testSetImaginaryCharacterEmpty() {
		try {
			org.apache.commons.math.complex.ComplexFormat cf = new org.apache.commons.math.complex.ComplexFormat();
			cf.setImaginaryCharacter("");
			junit.framework.Assert.fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testFormatNumber() {
		org.apache.commons.math.util.CompositeFormat cf = org.apache.commons.math.complex.ComplexFormat.getInstance(getLocale());
		java.lang.Double pi = java.lang.Double.valueOf(java.lang.Math.PI);
		java.lang.String text = cf.format(pi);
		junit.framework.Assert.assertEquals((("3" + (getDecimalCharacter())) + "14"), text);
	}

	public void testFormatObject() {
		try {
			org.apache.commons.math.util.CompositeFormat cf = new org.apache.commons.math.complex.ComplexFormat();
			java.lang.Object object = new java.lang.Object();
			cf.format(object);
			junit.framework.Assert.fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testForgottenImaginaryCharacter() {
		java.text.ParsePosition pos = new java.text.ParsePosition(0);
		junit.framework.Assert.assertNull(new org.apache.commons.math.complex.ComplexFormat().parse("1 + 1", pos));
		junit.framework.Assert.assertEquals(5, pos.getErrorIndex());
	}
}

