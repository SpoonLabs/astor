package org.apache.commons.math.fraction;


public class ProperBigFractionFormat extends org.apache.commons.math.fraction.BigFractionFormat {
	private static final long serialVersionUID = -6337346779577272307L;

	private java.text.NumberFormat wholeFormat;

	public ProperBigFractionFormat() {
		this(org.apache.commons.math.fraction.AbstractFormat.getDefaultNumberFormat());
	}

	public ProperBigFractionFormat(final java.text.NumberFormat format) {
		this(format, ((java.text.NumberFormat)(format.clone())), ((java.text.NumberFormat)(format.clone())));
	}

	public ProperBigFractionFormat(final java.text.NumberFormat wholeFormat ,final java.text.NumberFormat numeratorFormat ,final java.text.NumberFormat denominatorFormat) {
		super(numeratorFormat, denominatorFormat);
		setWholeFormat(wholeFormat);
	}

	@java.lang.Override
	public java.lang.StringBuffer format(final org.apache.commons.math.fraction.BigFraction fraction, final java.lang.StringBuffer toAppendTo, final java.text.FieldPosition pos) {
		pos.setBeginIndex(0);
		pos.setEndIndex(0);
		java.math.BigInteger num = fraction.getNumerator();
		java.math.BigInteger den = fraction.getDenominator();
		java.math.BigInteger whole = num.divide(den);
		num = num.remainder(den);
		if (!(java.math.BigInteger.ZERO.equals(whole))) {
			getWholeFormat().format(whole, toAppendTo, pos);
			toAppendTo.append(' ');
			if ((num.compareTo(java.math.BigInteger.ZERO)) < 0) {
				num = num.negate();
			} 
		} 
		getNumeratorFormat().format(num, toAppendTo, pos);
		toAppendTo.append(" / ");
		getDenominatorFormat().format(den, toAppendTo, pos);
		return toAppendTo;
	}

	public java.text.NumberFormat getWholeFormat() {
		return wholeFormat;
	}

	@java.lang.Override
	public org.apache.commons.math.fraction.BigFraction parse(final java.lang.String source, final java.text.ParsePosition pos) {
		org.apache.commons.math.fraction.BigFraction ret = super.parse(source, pos);
		if (ret != null) {
			return ret;
		} 
		final int initialIndex = pos.getIndex();
		org.apache.commons.math.fraction.AbstractFormat.parseAndIgnoreWhitespace(source, pos);
		java.math.BigInteger whole = parseNextBigInteger(source, pos);
		if (whole == null) {
			pos.setIndex(initialIndex);
			return null;
		} 
		org.apache.commons.math.fraction.AbstractFormat.parseAndIgnoreWhitespace(source, pos);
		java.math.BigInteger num = parseNextBigInteger(source, pos);
		if (num == null) {
			pos.setIndex(initialIndex);
			return null;
		} 
		if ((num.compareTo(java.math.BigInteger.ZERO)) < 0) {
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
		if ((den.compareTo(java.math.BigInteger.ZERO)) < 0) {
			pos.setIndex(initialIndex);
			return null;
		} 
		boolean wholeIsNeg = (whole.compareTo(java.math.BigInteger.ZERO)) < 0;
		if (wholeIsNeg) {
			whole = whole.negate();
		} 
		num = whole.multiply(den).add(num);
		if (wholeIsNeg) {
			num = num.negate();
		} 
		return new org.apache.commons.math.fraction.BigFraction(num , den);
	}

	public void setWholeFormat(final java.text.NumberFormat format) {
		if (format == null) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("whole format can not be null");
		} 
		this.wholeFormat = format;
	}
}

