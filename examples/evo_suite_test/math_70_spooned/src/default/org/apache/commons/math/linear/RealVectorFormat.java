package org.apache.commons.math.linear;


public class RealVectorFormat extends org.apache.commons.math.util.CompositeFormat {
	private static final long serialVersionUID = -708767813036157690L;

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

	public RealVectorFormat() {
		this(DEFAULT_PREFIX, DEFAULT_SUFFIX, DEFAULT_SEPARATOR, org.apache.commons.math.util.CompositeFormat.getDefaultNumberFormat());
	}

	public RealVectorFormat(final java.text.NumberFormat format) {
		this(DEFAULT_PREFIX, DEFAULT_SUFFIX, DEFAULT_SEPARATOR, format);
	}

	public RealVectorFormat(final java.lang.String prefix ,final java.lang.String suffix ,final java.lang.String separator) {
		this(prefix, suffix, separator, org.apache.commons.math.util.CompositeFormat.getDefaultNumberFormat());
	}

	public RealVectorFormat(final java.lang.String prefix ,final java.lang.String suffix ,final java.lang.String separator ,final java.text.NumberFormat format) {
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

	public static org.apache.commons.math.linear.RealVectorFormat getInstance() {
		return org.apache.commons.math.linear.RealVectorFormat.getInstance(java.util.Locale.getDefault());
	}

	public static org.apache.commons.math.linear.RealVectorFormat getInstance(final java.util.Locale locale) {
		return new org.apache.commons.math.linear.RealVectorFormat(org.apache.commons.math.util.CompositeFormat.getDefaultNumberFormat(locale));
	}

	public static java.lang.String formatRealVector(org.apache.commons.math.linear.RealVector v) {
		return org.apache.commons.math.linear.RealVectorFormat.getInstance().format(v);
	}

	public java.lang.StringBuffer format(org.apache.commons.math.linear.RealVector vector, java.lang.StringBuffer toAppendTo, java.text.FieldPosition pos) {
		pos.setBeginIndex(0);
		pos.setEndIndex(0);
		toAppendTo.append(prefix);
		for (int i = 0 ; i < (vector.getDimension()) ; ++i) {
			if (i > 0) {
				toAppendTo.append(separator);
			} 
			formatDouble(vector.getEntry(i), format, toAppendTo, pos);
		}
		toAppendTo.append(suffix);
		return toAppendTo;
	}

	@java.lang.Override
	public java.lang.StringBuffer format(java.lang.Object obj, java.lang.StringBuffer toAppendTo, java.text.FieldPosition pos) {
		if (obj instanceof org.apache.commons.math.linear.RealVector) {
			return format(((org.apache.commons.math.linear.RealVector)(obj)), toAppendTo, pos);
		} 
		throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("cannot format a {0} instance as a real vector", obj.getClass().getName());
	}

	public org.apache.commons.math.linear.ArrayRealVector parse(java.lang.String source) throws java.text.ParseException {
		java.text.ParsePosition parsePosition = new java.text.ParsePosition(0);
		org.apache.commons.math.linear.ArrayRealVector result = parse(source, parsePosition);
		if ((parsePosition.getIndex()) == 0) {
			throw org.apache.commons.math.MathRuntimeException.createParseException(parsePosition.getErrorIndex(), "unparseable real vector: \"{0}\"", source);
		} 
		return result;
	}

	public org.apache.commons.math.linear.ArrayRealVector parse(java.lang.String source, java.text.ParsePosition pos) {
		int initialIndex = pos.getIndex();
		parseAndIgnoreWhitespace(source, pos);
		if (!(parseFixedstring(source, trimmedPrefix, pos))) {
			return null;
		} 
		java.util.List<java.lang.Number> components = new java.util.ArrayList<java.lang.Number>();
		for (boolean loop = true ; loop ; ) {
			if (!(components.isEmpty())) {
				parseAndIgnoreWhitespace(source, pos);
				if (!(parseFixedstring(source, trimmedSeparator, pos))) {
					loop = false;
				} 
			} 
			if (loop) {
				parseAndIgnoreWhitespace(source, pos);
				java.lang.Number component = parseNumber(source, format, pos);
				if (component != null) {
					components.add(component);
				} else {
					pos.setIndex(initialIndex);
					return null;
				}
			} 
		}
		parseAndIgnoreWhitespace(source, pos);
		if (!(parseFixedstring(source, trimmedSuffix, pos))) {
			return null;
		} 
		double[] data = new double[components.size()];
		for (int i = 0 ; i < (data.length) ; ++i) {
			data[i] = components.get(i).doubleValue();
		}
		return new org.apache.commons.math.linear.ArrayRealVector(data , false);
	}

	@java.lang.Override
	public java.lang.Object parseObject(java.lang.String source, java.text.ParsePosition pos) {
		return parse(source, pos);
	}
}

