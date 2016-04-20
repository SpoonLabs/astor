package org.apache.commons.math.complex;


public class ComplexFormat extends org.apache.commons.math.util.CompositeFormat {
	private static final long serialVersionUID = -3343698360149467646L;

	private static final java.lang.String DEFAULT_IMAGINARY_CHARACTER = "i";

	private java.lang.String imaginaryCharacter;

	private java.text.NumberFormat imaginaryFormat;

	private java.text.NumberFormat realFormat;

	public ComplexFormat() {
		this(DEFAULT_IMAGINARY_CHARACTER, org.apache.commons.math.util.CompositeFormat.getDefaultNumberFormat());
	}

	public ComplexFormat(java.text.NumberFormat format) {
		this(DEFAULT_IMAGINARY_CHARACTER, format);
	}

	public ComplexFormat(java.text.NumberFormat realFormat ,java.text.NumberFormat imaginaryFormat) {
		this(DEFAULT_IMAGINARY_CHARACTER, realFormat, imaginaryFormat);
	}

	public ComplexFormat(java.lang.String imaginaryCharacter) {
		this(imaginaryCharacter, org.apache.commons.math.util.CompositeFormat.getDefaultNumberFormat());
	}

	public ComplexFormat(java.lang.String imaginaryCharacter ,java.text.NumberFormat format) {
		this(imaginaryCharacter, format, ((java.text.NumberFormat)(format.clone())));
	}

	public ComplexFormat(java.lang.String imaginaryCharacter ,java.text.NumberFormat realFormat ,java.text.NumberFormat imaginaryFormat) {
		super();
		setImaginaryCharacter(imaginaryCharacter);
		setImaginaryFormat(imaginaryFormat);
		setRealFormat(realFormat);
	}

	public static java.util.Locale[] getAvailableLocales() {
		return java.text.NumberFormat.getAvailableLocales();
	}

	public static java.lang.String formatComplex(org.apache.commons.math.complex.Complex c) {
		return org.apache.commons.math.complex.ComplexFormat.getInstance().format(c);
	}

	public java.lang.StringBuffer format(org.apache.commons.math.complex.Complex complex, java.lang.StringBuffer toAppendTo, java.text.FieldPosition pos) {
		pos.setBeginIndex(0);
		pos.setEndIndex(0);
		double re = complex.getReal();
		formatDouble(re, getRealFormat(), toAppendTo, pos);
		double im = complex.getImaginary();
		if (im < 0.0) {
			toAppendTo.append(" - ");
			formatDouble(-im, getImaginaryFormat(), toAppendTo, pos);
			toAppendTo.append(getImaginaryCharacter());
		} else if ((im > 0.0) || (java.lang.Double.isNaN(im))) {
			toAppendTo.append(" + ");
			formatDouble(im, getImaginaryFormat(), toAppendTo, pos);
			toAppendTo.append(getImaginaryCharacter());
		} 
		return toAppendTo;
	}

	@java.lang.Override
	public java.lang.StringBuffer format(java.lang.Object obj, java.lang.StringBuffer toAppendTo, java.text.FieldPosition pos) {
		java.lang.StringBuffer ret = null;
		if (obj instanceof org.apache.commons.math.complex.Complex) {
			ret = format(((org.apache.commons.math.complex.Complex)(obj)), toAppendTo, pos);
		} else if (obj instanceof java.lang.Number) {
			ret = format(new org.apache.commons.math.complex.Complex(((java.lang.Number)(obj)).doubleValue() , 0.0), toAppendTo, pos);
		} else {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("cannot format a {0} instance as a complex number", obj.getClass().getName());
		}
		return ret;
	}

	public java.lang.String getImaginaryCharacter() {
		return imaginaryCharacter;
	}

	public java.text.NumberFormat getImaginaryFormat() {
		return imaginaryFormat;
	}

	public static org.apache.commons.math.complex.ComplexFormat getInstance() {
		return org.apache.commons.math.complex.ComplexFormat.getInstance(java.util.Locale.getDefault());
	}

	public static org.apache.commons.math.complex.ComplexFormat getInstance(java.util.Locale locale) {
		java.text.NumberFormat f = org.apache.commons.math.util.CompositeFormat.getDefaultNumberFormat(locale);
		return new org.apache.commons.math.complex.ComplexFormat(f);
	}

	public java.text.NumberFormat getRealFormat() {
		return realFormat;
	}

	public org.apache.commons.math.complex.Complex parse(java.lang.String source) throws java.text.ParseException {
		java.text.ParsePosition parsePosition = new java.text.ParsePosition(0);
		org.apache.commons.math.complex.Complex result = parse(source, parsePosition);
		if ((parsePosition.getIndex()) == 0) {
			throw org.apache.commons.math.MathRuntimeException.createParseException(parsePosition.getErrorIndex(), "unparseable complex number: \"{0}\"", source);
		} 
		return result;
	}

	public org.apache.commons.math.complex.Complex parse(java.lang.String source, java.text.ParsePosition pos) {
		int initialIndex = pos.getIndex();
		parseAndIgnoreWhitespace(source, pos);
		java.lang.Number re = parseNumber(source, getRealFormat(), pos);
		if (re == null) {
			pos.setIndex(initialIndex);
			return null;
		} 
		int startIndex = pos.getIndex();
		char c = parseNextCharacter(source, pos);
		int sign = 0;
		switch (c) {
			case 0 :
				return new org.apache.commons.math.complex.Complex(re.doubleValue() , 0.0);
			case '-' :
				sign = -1;
				break;
			case '+' :
				sign = 1;
				break;
			default :
				pos.setIndex(initialIndex);
				pos.setErrorIndex(startIndex);
				return null;
		}
		parseAndIgnoreWhitespace(source, pos);
		java.lang.Number im = parseNumber(source, getRealFormat(), pos);
		if (im == null) {
			pos.setIndex(initialIndex);
			return null;
		} 
		if (!(parseFixedstring(source, getImaginaryCharacter(), pos))) {
			return null;
		} 
		return new org.apache.commons.math.complex.Complex(re.doubleValue() , ((im.doubleValue()) * sign));
	}

	@java.lang.Override
	public java.lang.Object parseObject(java.lang.String source, java.text.ParsePosition pos) {
		return parse(source, pos);
	}

	public void setImaginaryCharacter(java.lang.String imaginaryCharacter) {
		if ((imaginaryCharacter == null) || ((imaginaryCharacter.length()) == 0)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("empty string for imaginary character");
		} 
		this.imaginaryCharacter = imaginaryCharacter;
	}

	public void setImaginaryFormat(java.text.NumberFormat imaginaryFormat) {
		if (imaginaryFormat == null) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("null imaginary format");
		} 
		this.imaginaryFormat = imaginaryFormat;
	}

	public void setRealFormat(java.text.NumberFormat realFormat) {
		if (realFormat == null) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("null real format");
		} 
		this.realFormat = realFormat;
	}
}

