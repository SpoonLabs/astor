package org.apache.commons.math.fraction;


public class BigFractionFormat extends org.apache.commons.math.fraction.AbstractFormat implements java.io.Serializable {
	private static final long serialVersionUID = -2932167925527338976L;

	public BigFractionFormat() {
	}

	public BigFractionFormat(final java.text.NumberFormat format) {
		super(format);
	}

	public BigFractionFormat(final java.text.NumberFormat numeratorFormat ,final java.text.NumberFormat denominatorFormat) {
		super(numeratorFormat, denominatorFormat);
	}

	public static java.util.Locale[] getAvailableLocales() {
		return java.text.NumberFormat.getAvailableLocales();
	}

	public static java.lang.String formatBigFraction(final org.apache.commons.math.fraction.BigFraction f) {
		return org.apache.commons.math.fraction.BigFractionFormat.getImproperInstance().format(f);
	}

	public static org.apache.commons.math.fraction.BigFractionFormat getImproperInstance() {
		return org.apache.commons.math.fraction.BigFractionFormat.getImproperInstance(java.util.Locale.getDefault());
	}

	public static org.apache.commons.math.fraction.BigFractionFormat getImproperInstance(final java.util.Locale locale) {
		return new org.apache.commons.math.fraction.BigFractionFormat(org.apache.commons.math.fraction.AbstractFormat.getDefaultNumberFormat(locale));
	}

	public static org.apache.commons.math.fraction.BigFractionFormat getProperInstance() {
		return org.apache.commons.math.fraction.BigFractionFormat.getProperInstance(java.util.Locale.getDefault());
	}

	public static org.apache.commons.math.fraction.BigFractionFormat getProperInstance(final java.util.Locale locale) {
		return new org.apache.commons.math.fraction.ProperBigFractionFormat(org.apache.commons.math.fraction.AbstractFormat.getDefaultNumberFormat(locale));
	}

	public java.lang.StringBuffer format(final org.apache.commons.math.fraction.BigFraction BigFraction, final java.lang.StringBuffer toAppendTo, final java.text.FieldPosition pos) {
		pos.setBeginIndex(0);
		pos.setEndIndex(0);
		getNumeratorFormat().format(BigFraction.getNumerator(), toAppendTo, pos);
		toAppendTo.append(" / ");
		getDenominatorFormat().format(BigFraction.getDenominator(), toAppendTo, pos);
		return toAppendTo;
	}

	@java.lang.Override
	public java.lang.StringBuffer format(final java.lang.Object obj, final java.lang.StringBuffer toAppendTo, final java.text.FieldPosition pos) {
		final java.lang.StringBuffer ret;
		if (obj instanceof org.apache.commons.math.fraction.BigFraction) {
			ret = format(((org.apache.commons.math.fraction.BigFraction)(obj)), toAppendTo, pos);
		} else {
			if (obj instanceof java.math.BigInteger) {
				ret = format(new org.apache.commons.math.fraction.BigFraction(((java.math.BigInteger)(obj))), toAppendTo, pos);
			} else {
				if (obj instanceof java.lang.Number) {
					ret = format(new org.apache.commons.math.fraction.BigFraction(((java.lang.Number)(obj)).doubleValue()), toAppendTo, pos);
				} else {
					throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("cannot format given object as a fraction number");
				}
			}
		}
		return ret;
	}

	@java.lang.Override
	public org.apache.commons.math.fraction.BigFraction parse(final java.lang.String source) throws java.text.ParseException {
		final java.text.ParsePosition parsePosition = new java.text.ParsePosition(0);
		final org.apache.commons.math.fraction.BigFraction result = parse(source, parsePosition);
		if ((parsePosition.getIndex()) == 0) {
			throw org.apache.commons.math.MathRuntimeException.createParseException(parsePosition.getErrorIndex(), "unparseable fraction number: \"{0}\"", source);
		} 
		return result;
	}

	@java.lang.Override
	public org.apache.commons.math.fraction.BigFraction parse(final java.lang.String source, final java.text.ParsePosition pos) {
		final int initialIndex = pos.getIndex();
		org.apache.commons.math.fraction.AbstractFormat.parseAndIgnoreWhitespace(source, pos);
		final java.math.BigInteger num = parseNextBigInteger(source, pos);
		if (num == null) {
			pos.setIndex(initialIndex);
			return null;
		} 
		final int startIndex = pos.getIndex();
		final char c = org.apache.commons.math.fraction.AbstractFormat.parseNextCharacter(source, pos);
		switch (c) {
			case 0 :
				return new org.apache.commons.math.fraction.BigFraction(num);
			case '/' :
				break;
			default :
				pos.setIndex(initialIndex);
				pos.setErrorIndex(startIndex);
				return null;
		}
		org.apache.commons.math.fraction.AbstractFormat.parseAndIgnoreWhitespace(source, pos);
		final java.math.BigInteger den = parseNextBigInteger(source, pos);
		if (den == null) {
			pos.setIndex(initialIndex);
			return null;
		} 
		return new org.apache.commons.math.fraction.BigFraction(num , den);
	}

	protected java.math.BigInteger parseNextBigInteger(final java.lang.String source, final java.text.ParsePosition pos) {
		final int start = pos.getIndex();
		int end = (source.charAt(start)) == '-' ? start + 1 : start;
		while ((end < (source.length())) && (java.lang.Character.isDigit(source.charAt(end)))) {
			++end;
		}
		try {
			java.math.BigInteger n = new java.math.BigInteger(source.substring(start, end));
			pos.setIndex(end);
			return n;
		} catch (java.lang.NumberFormatException nfe) {
			pos.setErrorIndex(start);
			return null;
		}
	}
}

