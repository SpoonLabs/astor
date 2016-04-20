package org.apache.commons.math.util;


public abstract class CompositeFormat extends java.text.Format {
	private static final long serialVersionUID = 5358685519349262494L;

	protected static java.text.NumberFormat getDefaultNumberFormat() {
		return org.apache.commons.math.util.CompositeFormat.getDefaultNumberFormat(java.util.Locale.getDefault());
	}

	protected static java.text.NumberFormat getDefaultNumberFormat(final java.util.Locale locale) {
		final java.text.NumberFormat nf = java.text.NumberFormat.getInstance(locale);
		nf.setMaximumFractionDigits(2);
		return nf;
	}

	protected void parseAndIgnoreWhitespace(final java.lang.String source, final java.text.ParsePosition pos) {
		parseNextCharacter(source, pos);
		pos.setIndex(((pos.getIndex()) - 1));
	}

	protected char parseNextCharacter(final java.lang.String source, final java.text.ParsePosition pos) {
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

	private java.lang.Number parseNumber(final java.lang.String source, final double value, final java.text.ParsePosition pos) {
		java.lang.Number ret = null;
		java.lang.StringBuffer sb = new java.lang.StringBuffer();
		sb.append('(');
		sb.append(value);
		sb.append(')');
		final int n = sb.length();
		final int startIndex = pos.getIndex();
		final int endIndex = startIndex + n;
		if (endIndex < (source.length())) {
			if ((source.substring(startIndex, endIndex).compareTo(sb.toString())) == 0) {
				ret = java.lang.Double.valueOf(value);
				pos.setIndex(endIndex);
			} 
		} 
		return ret;
	}

	protected java.lang.Number parseNumber(final java.lang.String source, final java.text.NumberFormat format, final java.text.ParsePosition pos) {
		final int startIndex = pos.getIndex();
		java.lang.Number number = format.parse(source, pos);
		final int endIndex = pos.getIndex();
		if (startIndex == endIndex) {
			final double[] special = new double[]{ java.lang.Double.NaN , java.lang.Double.POSITIVE_INFINITY , java.lang.Double.NEGATIVE_INFINITY };
			for (int i = 0 ; i < (special.length) ; ++i) {
				number = parseNumber(source, special[i], pos);
				if (number != null) {
					break;
				} 
			}
		} 
		return number;
	}

	protected boolean parseFixedstring(final java.lang.String source, final java.lang.String expected, final java.text.ParsePosition pos) {
		final int startIndex = pos.getIndex();
		final int endIndex = startIndex + (expected.length());
		if (((startIndex >= (source.length())) || (endIndex > (source.length()))) || ((source.substring(startIndex, endIndex).compareTo(expected)) != 0)) {
			pos.setIndex(startIndex);
			pos.setErrorIndex(startIndex);
			return false;
		} 
		pos.setIndex(endIndex);
		return true;
	}

	protected java.lang.StringBuffer formatDouble(final double value, final java.text.NumberFormat format, final java.lang.StringBuffer toAppendTo, final java.text.FieldPosition pos) {
		if ((java.lang.Double.isNaN(value)) || (java.lang.Double.isInfinite(value))) {
			toAppendTo.append('(');
			toAppendTo.append(value);
			toAppendTo.append(')');
		} else {
			format.format(value, toAppendTo, pos);
		}
		return toAppendTo;
	}
}

