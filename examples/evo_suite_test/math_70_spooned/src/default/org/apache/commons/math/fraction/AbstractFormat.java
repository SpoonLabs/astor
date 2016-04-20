package org.apache.commons.math.fraction;


public abstract class AbstractFormat extends java.text.NumberFormat implements java.io.Serializable {
	private static final long serialVersionUID = -6981118387974191891L;

	protected java.text.NumberFormat denominatorFormat;

	protected java.text.NumberFormat numeratorFormat;

	protected AbstractFormat() {
		this(org.apache.commons.math.fraction.AbstractFormat.getDefaultNumberFormat());
	}

	protected AbstractFormat(final java.text.NumberFormat format) {
		this(format, ((java.text.NumberFormat)(format.clone())));
	}

	protected AbstractFormat(final java.text.NumberFormat numeratorFormat ,final java.text.NumberFormat denominatorFormat) {
		this.numeratorFormat = numeratorFormat;
		this.denominatorFormat = denominatorFormat;
	}

	protected static java.text.NumberFormat getDefaultNumberFormat() {
		return org.apache.commons.math.fraction.AbstractFormat.getDefaultNumberFormat(java.util.Locale.getDefault());
	}

	protected static java.text.NumberFormat getDefaultNumberFormat(final java.util.Locale locale) {
		final java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance(locale);
		nf.setMaximumFractionDigits(0);
		nf.setParseIntegerOnly(true);
		return nf;
	}

	public java.text.NumberFormat getDenominatorFormat() {
		return denominatorFormat;
	}

	public java.text.NumberFormat getNumeratorFormat() {
		return numeratorFormat;
	}

	public void setDenominatorFormat(final java.text.NumberFormat format) {
		if (format == null) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("denominator format can not be null");
		} 
		this.denominatorFormat = format;
	}

	public void setNumeratorFormat(final java.text.NumberFormat format) {
		if (format == null) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("numerator format can not be null");
		} 
		this.numeratorFormat = format;
	}

	protected static void parseAndIgnoreWhitespace(final java.lang.String source, final java.text.ParsePosition pos) {
		org.apache.commons.math.fraction.AbstractFormat.parseNextCharacter(source, pos);
		pos.setIndex(((pos.getIndex()) - 1));
	}

	protected static char parseNextCharacter(final java.lang.String source, final java.text.ParsePosition pos) {
		int index = pos.getIndex();
		final int n = source.length();
		char ret = 0;
		if (index < n) {
			char c;
			do {
				c = source.charAt(index++);
			} while ((java.lang.Character.isWhitespace(c)) && (index < n) );
			pos.setIndex(index);
			if (index < n) {
				ret = c;
			} 
		} 
		return ret;
	}

	@java.lang.Override
	public java.lang.StringBuffer format(final double value, final java.lang.StringBuffer buffer, final java.text.FieldPosition position) {
		return format(java.lang.Double.valueOf(value), buffer, position);
	}

	@java.lang.Override
	public java.lang.StringBuffer format(final long value, final java.lang.StringBuffer buffer, final java.text.FieldPosition position) {
		return format(java.lang.Long.valueOf(value), buffer, position);
	}
}

