package org.apache.commons.math.geometry;


public class Vector3DFormat extends org.apache.commons.math.util.CompositeFormat {
	private static final long serialVersionUID = -5447606608652576301L;

	private static final java.lang.String DEFAULT_PREFIX = "{";

	private static final java.lang.String DEFAULT_SUFFIX = "}";

	private static final java.lang.String DEFAULT_SEPARATOR = "; ";

	private final java.lang.String prefix;

	private final java.lang.String suffix;

	private final java.lang.String separator;

	private final java.lang.String trimmedPrefix;

	private final java.lang.String trimmedSuffix;

	private final java.lang.String trimmedSeparator;

	private java.text.NumberFormat format;

	public Vector3DFormat() {
		this(DEFAULT_PREFIX, DEFAULT_SUFFIX, DEFAULT_SEPARATOR, org.apache.commons.math.util.CompositeFormat.getDefaultNumberFormat());
	}

	public Vector3DFormat(final java.text.NumberFormat format) {
		this(DEFAULT_PREFIX, DEFAULT_SUFFIX, DEFAULT_SEPARATOR, format);
	}

	public Vector3DFormat(final java.lang.String prefix ,final java.lang.String suffix ,final java.lang.String separator) {
		this(prefix, suffix, separator, org.apache.commons.math.util.CompositeFormat.getDefaultNumberFormat());
	}

	public Vector3DFormat(final java.lang.String prefix ,final java.lang.String suffix ,final java.lang.String separator ,final java.text.NumberFormat format) {
		this.prefix = prefix;
		this.suffix = suffix;
		this.separator = separator;
		trimmedPrefix = prefix.trim();
		trimmedSuffix = suffix.trim();
		trimmedSeparator = separator.trim();
		this.format = format;
	}

	public static java.util.Locale[] getAvailableLocales() {
		return java.text.NumberFormat.getAvailableLocales();
	}

	public java.lang.String getPrefix() {
		return prefix;
	}

	public java.lang.String getSuffix() {
		return suffix;
	}

	public java.lang.String getSeparator() {
		return separator;
	}

	public java.text.NumberFormat getFormat() {
		return format;
	}

	public static org.apache.commons.math.geometry.Vector3DFormat getInstance() {
		return org.apache.commons.math.geometry.Vector3DFormat.getInstance(java.util.Locale.getDefault());
	}

	public static org.apache.commons.math.geometry.Vector3DFormat getInstance(final java.util.Locale locale) {
		return new org.apache.commons.math.geometry.Vector3DFormat(org.apache.commons.math.util.CompositeFormat.getDefaultNumberFormat(locale));
	}

	public static java.lang.String formatVector3D(org.apache.commons.math.geometry.Vector3D v) {
		return org.apache.commons.math.geometry.Vector3DFormat.getInstance().format(v);
	}

	public java.lang.StringBuffer format(org.apache.commons.math.geometry.Vector3D vector, java.lang.StringBuffer toAppendTo, java.text.FieldPosition pos) {
		pos.setBeginIndex(0);
		pos.setEndIndex(0);
		toAppendTo.append(prefix);
		formatDouble(vector.getX(), format, toAppendTo, pos);
		toAppendTo.append(separator);
		formatDouble(vector.getY(), format, toAppendTo, pos);
		toAppendTo.append(separator);
		formatDouble(vector.getZ(), format, toAppendTo, pos);
		toAppendTo.append(suffix);
		return toAppendTo;
	}

	@java.lang.Override
	public java.lang.StringBuffer format(java.lang.Object obj, java.lang.StringBuffer toAppendTo, java.text.FieldPosition pos) {
		if (obj instanceof org.apache.commons.math.geometry.Vector3D) {
			return format(((org.apache.commons.math.geometry.Vector3D)(obj)), toAppendTo, pos);
		} 
		throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("cannot format a {0} instance as a 3D vector", obj.getClass().getName());
	}

	public org.apache.commons.math.geometry.Vector3D parse(java.lang.String source) throws java.text.ParseException {
		java.text.ParsePosition parsePosition = new java.text.ParsePosition(0);
		org.apache.commons.math.geometry.Vector3D result = parse(source, parsePosition);
		if ((parsePosition.getIndex()) == 0) {
			throw org.apache.commons.math.MathRuntimeException.createParseException(parsePosition.getErrorIndex(), "unparseable 3D vector: \"{0}\"", source);
		} 
		return result;
	}

	public org.apache.commons.math.geometry.Vector3D parse(java.lang.String source, java.text.ParsePosition pos) {
		int initialIndex = pos.getIndex();
		parseAndIgnoreWhitespace(source, pos);
		if (!(parseFixedstring(source, trimmedPrefix, pos))) {
			return null;
		} 
		parseAndIgnoreWhitespace(source, pos);
		java.lang.Number x = parseNumber(source, format, pos);
		if (x == null) {
			pos.setIndex(initialIndex);
			return null;
		} 
		parseAndIgnoreWhitespace(source, pos);
		if (!(parseFixedstring(source, trimmedSeparator, pos))) {
			return null;
		} 
		parseAndIgnoreWhitespace(source, pos);
		java.lang.Number y = parseNumber(source, format, pos);
		if (y == null) {
			pos.setIndex(initialIndex);
			return null;
		} 
		parseAndIgnoreWhitespace(source, pos);
		if (!(parseFixedstring(source, trimmedSeparator, pos))) {
			return null;
		} 
		parseAndIgnoreWhitespace(source, pos);
		java.lang.Number z = parseNumber(source, format, pos);
		if (z == null) {
			pos.setIndex(initialIndex);
			return null;
		} 
		parseAndIgnoreWhitespace(source, pos);
		if (!(parseFixedstring(source, trimmedSuffix, pos))) {
			return null;
		} 
		return new org.apache.commons.math.geometry.Vector3D(x.doubleValue() , y.doubleValue() , z.doubleValue());
	}

	@java.lang.Override
	public java.lang.Object parseObject(java.lang.String source, java.text.ParsePosition pos) {
		return parse(source, pos);
	}
}

