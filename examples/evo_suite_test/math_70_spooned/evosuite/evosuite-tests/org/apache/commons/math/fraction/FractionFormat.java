package org.apache.commons.math.fraction;


public class FractionFormat extends org.apache.commons.math.fraction.AbstractFormat {
	private static final long serialVersionUID = 3008655719530972611L;

	public FractionFormat() {
	}

	public FractionFormat(final java.text.NumberFormat format) {
		super(format);
	}

	public FractionFormat(final java.text.NumberFormat numeratorFormat ,final java.text.NumberFormat denominatorFormat) {
		super(numeratorFormat, denominatorFormat);
	}

	public static java.util.Locale[] getAvailableLocales() {
		return java.text.NumberFormat.getAvailableLocales();
	}

	public static java.lang.String formatFraction(org.apache.commons.math.fraction.Fraction f) {
		return org.apache.commons.math.fraction.FractionFormat.getImproperInstance().format(f);
	}

	public static org.apache.commons.math.fraction.FractionFormat getImproperInstance() {
		return org.apache.commons.math.fraction.FractionFormat.getImproperInstance(java.util.Locale.getDefault());
	}

	public static org.apache.commons.math.fraction.FractionFormat getImproperInstance(final java.util.Locale locale) {
		return new org.apache.commons.math.fraction.FractionFormat(org.apache.commons.math.fraction.AbstractFormat.getDefaultNumberFormat(locale));
	}

	public static org.apache.commons.math.fraction.FractionFormat getProperInstance() {
		return org.apache.commons.math.fraction.FractionFormat.getProperInstance(java.util.Locale.getDefault());
	}

	public static org.apache.commons.math.fraction.FractionFormat getProperInstance(final java.util.Locale locale) {
		return new org.apache.commons.math.fraction.ProperFractionFormat(org.apache.commons.math.fraction.AbstractFormat.getDefaultNumberFormat(locale));
	}

	protected static java.text.NumberFormat getDefaultNumberFormat() {
		return org.apache.commons.math.fraction.AbstractFormat.getDefaultNumberFormat(java.util.Locale.getDefault());
	}

	public java.lang.StringBuffer format(final org.apache.commons.math.fraction.Fraction fraction, final java.lang.StringBuffer toAppendTo, final java.text.FieldPosition pos) {
		pos.setBeginIndex(0);
		pos.setEndIndex(0);
		getNumeratorFormat().format(fraction.getNumerator(), toAppendTo, pos);
		toAppendTo.append(" / ");
		getDenominatorFormat().format(fraction.getDenominator(), toAppendTo, pos);
		return toAppendTo;
	}

	@java.lang.Override
	public java.lang.StringBuffer format(final java.lang.Object obj, final java.lang.StringBuffer toAppendTo, final java.text.FieldPosition pos) {
		java.lang.StringBuffer ret = null;
		if (obj instanceof org.apache.commons.math.fraction.Fraction) {
			ret = format(((org.apache.commons.math.fraction.Fraction)(obj)), toAppendTo, pos);
		} else {
			if (obj instanceof java.lang.Number) {
				try {
					ret = format(new org.apache.commons.math.fraction.Fraction(((java.lang.Number)(obj)).doubleValue()), toAppendTo, pos);
				} catch (org.apache.commons.math.ConvergenceException ex) {
					throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("cannot convert given object to a fraction number: {0}", ex.getLocalizedMessage());
				}
			} else {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("cannot format given object as a fraction number");
			}
		}
		return ret;
	}

	@java.lang.Override
	public org.apache.commons.math.fraction.Fraction parse(final java.lang.String source) throws java.text.ParseException {
		final java.text.ParsePosition parsePosition = new java.text.ParsePosition(0);
		final org.apache.commons.math.fraction.Fraction result = parse(source, parsePosition);
		if ((parsePosition.getIndex()) == 0) {
			throw org.apache.commons.math.MathRuntimeException.createParseException(parsePosition.getErrorIndex(), "unparseable fraction number: \"{0}\"", source);
		} 
		return result;
	}

	@java.lang.Override
	public org.apache.commons.math.fraction.Fraction parse(final java.lang.String source, final java.text.ParsePosition pos) {
		final int initialIndex = pos.getIndex();
		org.apache.commons.math.fraction.AbstractFormat.parseAndIgnoreWhitespace(source, pos);
		final java.lang.Number num = getNumeratorFormat().parse(source, pos);
		if (num == null) {
			pos.setIndex(initialIndex);
			return null;
		} 
		final int startIndex = pos.getIndex();
		final char c = org.apache.commons.math.fraction.AbstractFormat.parseNextCharacter(source, pos);
		switch (c) {
			case 0 :
				return new org.apache.commons.math.fraction.Fraction(num.intValue() , 1);
			case '/' :
				break;
			default :
				pos.setIndex(initialIndex);
				pos.setErrorIndex(startIndex);
				return null;
		}
		org.apache.commons.math.fraction.AbstractFormat.parseAndIgnoreWhitespace(source, pos);
		final java.lang.Number den = getDenominatorFormat().parse(source, pos);
		if (den == null) {
			pos.setIndex(initialIndex);
			return null;
		} 
		return new org.apache.commons.math.fraction.Fraction(num.intValue() , den.intValue());
	}
}

