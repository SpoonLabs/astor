package org.apache.commons.math.fraction;


public class BigFraction extends java.lang.Number implements java.io.Serializable , java.lang.Comparable<org.apache.commons.math.fraction.BigFraction> , org.apache.commons.math.FieldElement<org.apache.commons.math.fraction.BigFraction> {
	public static final org.apache.commons.math.fraction.BigFraction TWO = new org.apache.commons.math.fraction.BigFraction(2);

	public static final org.apache.commons.math.fraction.BigFraction ONE = new org.apache.commons.math.fraction.BigFraction(1);

	public static final org.apache.commons.math.fraction.BigFraction ZERO = new org.apache.commons.math.fraction.BigFraction(0);

	public static final org.apache.commons.math.fraction.BigFraction MINUS_ONE = new org.apache.commons.math.fraction.BigFraction(-1);

	public static final org.apache.commons.math.fraction.BigFraction FOUR_FIFTHS = new org.apache.commons.math.fraction.BigFraction(4 , 5);

	public static final org.apache.commons.math.fraction.BigFraction ONE_FIFTH = new org.apache.commons.math.fraction.BigFraction(1 , 5);

	public static final org.apache.commons.math.fraction.BigFraction ONE_HALF = new org.apache.commons.math.fraction.BigFraction(1 , 2);

	public static final org.apache.commons.math.fraction.BigFraction ONE_QUARTER = new org.apache.commons.math.fraction.BigFraction(1 , 4);

	public static final org.apache.commons.math.fraction.BigFraction ONE_THIRD = new org.apache.commons.math.fraction.BigFraction(1 , 3);

	public static final org.apache.commons.math.fraction.BigFraction THREE_FIFTHS = new org.apache.commons.math.fraction.BigFraction(3 , 5);

	public static final org.apache.commons.math.fraction.BigFraction THREE_QUARTERS = new org.apache.commons.math.fraction.BigFraction(3 , 4);

	public static final org.apache.commons.math.fraction.BigFraction TWO_FIFTHS = new org.apache.commons.math.fraction.BigFraction(2 , 5);

	public static final org.apache.commons.math.fraction.BigFraction TWO_QUARTERS = new org.apache.commons.math.fraction.BigFraction(2 , 4);

	public static final org.apache.commons.math.fraction.BigFraction TWO_THIRDS = new org.apache.commons.math.fraction.BigFraction(2 , 3);

	private static final long serialVersionUID = -5630213147331578515L;

	private static final java.lang.String FORBIDDEN_ZERO_DENOMINATOR = "denominator must be different from 0";

	private static final java.math.BigInteger ONE_HUNDRED_DOUBLE = java.math.BigInteger.valueOf(100);

	private final java.math.BigInteger numerator;

	private final java.math.BigInteger denominator;

	public BigFraction(final java.math.BigInteger num) {
		this(num, java.math.BigInteger.ONE);
	}

	public BigFraction(java.math.BigInteger num ,java.math.BigInteger den) {
		if (num == null) {
			throw org.apache.commons.math.MathRuntimeException.createNullPointerException("numerator is null");
		} 
		if (den == null) {
			throw org.apache.commons.math.MathRuntimeException.createNullPointerException("denominator is null");
		} 
		if (java.math.BigInteger.ZERO.equals(den)) {
			throw org.apache.commons.math.MathRuntimeException.createArithmeticException(FORBIDDEN_ZERO_DENOMINATOR);
		} 
		if (java.math.BigInteger.ZERO.equals(num)) {
			numerator = java.math.BigInteger.ZERO;
			denominator = java.math.BigInteger.ONE;
		} else {
			final java.math.BigInteger gcd = num.gcd(den);
			if ((java.math.BigInteger.ONE.compareTo(gcd)) < 0) {
				num = num.divide(gcd);
				den = den.divide(gcd);
			} 
			if ((java.math.BigInteger.ZERO.compareTo(den)) > 0) {
				num = num.negate();
				den = den.negate();
			} 
			numerator = num;
			denominator = den;
		}
	}

	public BigFraction(final double value) throws java.lang.IllegalArgumentException {
		if (java.lang.Double.isNaN(value)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("cannot convert NaN value");
		} 
		if (java.lang.Double.isInfinite(value)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("cannot convert infinite value");
		} 
		final long bits = java.lang.Double.doubleToLongBits(value);
		final long sign = bits & -9223372036854775808L;
		final long exponent = bits & 9218868437227405312L;
		long m = bits & 4503599627370495L;
		if (exponent != 0) {
			m |= 4503599627370496L;
		} 
		if (sign != 0) {
			m = -m;
		} 
		int k = ((int)((exponent >> 52))) - 1075;
		while (((m & 9007199254740990L) != 0) && ((m & 1) == 0)) {
			m = m >> 1;
			++k;
		}
		if (k < 0) {
			numerator = java.math.BigInteger.valueOf(m);
			denominator = java.math.BigInteger.ZERO.flipBit(-k);
		} else {
			numerator = java.math.BigInteger.valueOf(m).multiply(java.math.BigInteger.ZERO.flipBit(k));
			denominator = java.math.BigInteger.ONE;
		}
	}

	public BigFraction(final double value ,final double epsilon ,final int maxIterations) throws org.apache.commons.math.fraction.FractionConversionException {
		this(value, epsilon, java.lang.Integer.MAX_VALUE, maxIterations);
	}

	private BigFraction(final double value ,final double epsilon ,final int maxDenominator ,int maxIterations) throws org.apache.commons.math.fraction.FractionConversionException {
		long overflow = java.lang.Integer.MAX_VALUE;
		double r0 = value;
		long a0 = ((long)(java.lang.Math.floor(r0)));
		if (a0 > overflow) {
			throw new org.apache.commons.math.fraction.FractionConversionException(value , a0 , 1L);
		} 
		if ((java.lang.Math.abs((a0 - value))) < epsilon) {
			numerator = java.math.BigInteger.valueOf(a0);
			denominator = java.math.BigInteger.ONE;
			return ;
		} 
		long p0 = 1;
		long q0 = 0;
		long p1 = a0;
		long q1 = 1;
		long p2 = 0;
		long q2 = 1;
		int n = 0;
		boolean stop = false;
		do {
			++n;
			final double r1 = 1.0 / (r0 - a0);
			final long a1 = ((long)(java.lang.Math.floor(r1)));
			p2 = (a1 * p1) + p0;
			q2 = (a1 * q1) + q0;
			if ((p2 > overflow) || (q2 > overflow)) {
				throw new org.apache.commons.math.fraction.FractionConversionException(value , p2 , q2);
			} 
			final double convergent = ((double)(p2)) / ((double)(q2));
			if (((n < maxIterations) && ((java.lang.Math.abs((convergent - value))) > epsilon)) && (q2 < maxDenominator)) {
				p0 = p1;
				p1 = p2;
				q0 = q1;
				q1 = q2;
				a0 = a1;
				r0 = r1;
			} else {
				stop = true;
			}
		} while (!stop );
		if (n >= maxIterations) {
			throw new org.apache.commons.math.fraction.FractionConversionException(value , maxIterations);
		} 
		if (q2 < maxDenominator) {
			numerator = java.math.BigInteger.valueOf(p2);
			denominator = java.math.BigInteger.valueOf(q2);
		} else {
			numerator = java.math.BigInteger.valueOf(p1);
			denominator = java.math.BigInteger.valueOf(q1);
		}
	}

	public BigFraction(final double value ,final int maxDenominator) throws org.apache.commons.math.fraction.FractionConversionException {
		this(value, 0, maxDenominator, 100);
	}

	public BigFraction(final int num) {
		this(java.math.BigInteger.valueOf(num), java.math.BigInteger.ONE);
	}

	public BigFraction(final int num ,final int den) {
		this(java.math.BigInteger.valueOf(num), java.math.BigInteger.valueOf(den));
	}

	public BigFraction(final long num) {
		this(java.math.BigInteger.valueOf(num), java.math.BigInteger.ONE);
	}

	public BigFraction(final long num ,final long den) {
		this(java.math.BigInteger.valueOf(num), java.math.BigInteger.valueOf(den));
	}

	public static org.apache.commons.math.fraction.BigFraction getReducedFraction(final int numerator, final int denominator) {
		if (numerator == 0) {
			return ZERO;
		} 
		return new org.apache.commons.math.fraction.BigFraction(numerator , denominator);
	}

	public org.apache.commons.math.fraction.BigFraction abs() {
		return (java.math.BigInteger.ZERO.compareTo(numerator)) <= 0 ? this : negate();
	}

	public org.apache.commons.math.fraction.BigFraction add(final java.math.BigInteger bg) {
		return new org.apache.commons.math.fraction.BigFraction(numerator.add(denominator.multiply(bg)) , denominator);
	}

	public org.apache.commons.math.fraction.BigFraction add(final int i) {
		return add(java.math.BigInteger.valueOf(i));
	}

	public org.apache.commons.math.fraction.BigFraction add(final long l) {
		return add(java.math.BigInteger.valueOf(l));
	}

	public org.apache.commons.math.fraction.BigFraction add(final org.apache.commons.math.fraction.BigFraction fraction) {
		if (ZERO.equals(fraction)) {
			return this;
		} 
		java.math.BigInteger num = null;
		java.math.BigInteger den = null;
		if (denominator.equals(fraction.denominator)) {
			num = numerator.add(fraction.numerator);
			den = denominator;
		} else {
			num = numerator.multiply(fraction.denominator).add(fraction.numerator.multiply(denominator));
			den = denominator.multiply(fraction.denominator);
		}
		return new org.apache.commons.math.fraction.BigFraction(num , den);
	}

	public java.math.BigDecimal bigDecimalValue() {
		return new java.math.BigDecimal(numerator).divide(new java.math.BigDecimal(denominator));
	}

	public java.math.BigDecimal bigDecimalValue(final int roundingMode) {
		return new java.math.BigDecimal(numerator).divide(new java.math.BigDecimal(denominator), roundingMode);
	}

	public java.math.BigDecimal bigDecimalValue(final int scale, final int roundingMode) {
		return new java.math.BigDecimal(numerator).divide(new java.math.BigDecimal(denominator), scale, roundingMode);
	}

	public int compareTo(final org.apache.commons.math.fraction.BigFraction object) {
		java.math.BigInteger nOd = numerator.multiply(object.denominator);
		java.math.BigInteger dOn = denominator.multiply(object.numerator);
		return nOd.compareTo(dOn);
	}

	public org.apache.commons.math.fraction.BigFraction divide(final java.math.BigInteger bg) {
		if (java.math.BigInteger.ZERO.equals(bg)) {
			throw org.apache.commons.math.MathRuntimeException.createArithmeticException(FORBIDDEN_ZERO_DENOMINATOR);
		} 
		return new org.apache.commons.math.fraction.BigFraction(numerator , denominator.multiply(bg));
	}

	public org.apache.commons.math.fraction.BigFraction divide(final int i) {
		return divide(java.math.BigInteger.valueOf(i));
	}

	public org.apache.commons.math.fraction.BigFraction divide(final long l) {
		return divide(java.math.BigInteger.valueOf(l));
	}

	public org.apache.commons.math.fraction.BigFraction divide(final org.apache.commons.math.fraction.BigFraction fraction) {
		if (java.math.BigInteger.ZERO.equals(fraction.numerator)) {
			throw org.apache.commons.math.MathRuntimeException.createArithmeticException(FORBIDDEN_ZERO_DENOMINATOR);
		} 
		return multiply(fraction.reciprocal());
	}

	@java.lang.Override
	public double doubleValue() {
		return (numerator.doubleValue()) / (denominator.doubleValue());
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object other) {
		boolean ret = false;
		if ((this) == other) {
			ret = true;
		} else if (other instanceof org.apache.commons.math.fraction.BigFraction) {
			org.apache.commons.math.fraction.BigFraction rhs = ((org.apache.commons.math.fraction.BigFraction)(other)).reduce();
			org.apache.commons.math.fraction.BigFraction thisOne = reduce();
			ret = (thisOne.numerator.equals(rhs.numerator)) && (thisOne.denominator.equals(rhs.denominator));
		} 
		return ret;
	}

	@java.lang.Override
	public float floatValue() {
		return (numerator.floatValue()) / (denominator.floatValue());
	}

	public java.math.BigInteger getDenominator() {
		return denominator;
	}

	public int getDenominatorAsInt() {
		return denominator.intValue();
	}

	public long getDenominatorAsLong() {
		return denominator.longValue();
	}

	public java.math.BigInteger getNumerator() {
		return numerator;
	}

	public int getNumeratorAsInt() {
		return numerator.intValue();
	}

	public long getNumeratorAsLong() {
		return numerator.longValue();
	}

	@java.lang.Override
	public int hashCode() {
		return (37 * ((37 * 17) + (numerator.hashCode()))) + (denominator.hashCode());
	}

	@java.lang.Override
	public int intValue() {
		return numerator.divide(denominator).intValue();
	}

	@java.lang.Override
	public long longValue() {
		return numerator.divide(denominator).longValue();
	}

	public org.apache.commons.math.fraction.BigFraction multiply(final java.math.BigInteger bg) {
		return new org.apache.commons.math.fraction.BigFraction(bg.multiply(numerator) , denominator);
	}

	public org.apache.commons.math.fraction.BigFraction multiply(final int i) {
		return multiply(java.math.BigInteger.valueOf(i));
	}

	public org.apache.commons.math.fraction.BigFraction multiply(final long l) {
		return multiply(java.math.BigInteger.valueOf(l));
	}

	public org.apache.commons.math.fraction.BigFraction multiply(final org.apache.commons.math.fraction.BigFraction fraction) {
		if ((numerator.equals(java.math.BigInteger.ZERO)) || (fraction.numerator.equals(java.math.BigInteger.ZERO))) {
			return ZERO;
		} 
		return new org.apache.commons.math.fraction.BigFraction(numerator.multiply(fraction.numerator) , denominator.multiply(fraction.denominator));
	}

	public org.apache.commons.math.fraction.BigFraction negate() {
		return new org.apache.commons.math.fraction.BigFraction(numerator.negate() , denominator);
	}

	public double percentageValue() {
		return numerator.divide(denominator).multiply(ONE_HUNDRED_DOUBLE).doubleValue();
	}

	public org.apache.commons.math.fraction.BigFraction pow(final int exponent) {
		if (exponent < 0) {
			return new org.apache.commons.math.fraction.BigFraction(denominator.pow(-exponent) , numerator.pow(-exponent));
		} 
		return new org.apache.commons.math.fraction.BigFraction(numerator.pow(exponent) , denominator.pow(exponent));
	}

	public org.apache.commons.math.fraction.BigFraction pow(final long exponent) {
		if (exponent < 0) {
			return new org.apache.commons.math.fraction.BigFraction(org.apache.commons.math.util.MathUtils.pow(denominator, -exponent) , org.apache.commons.math.util.MathUtils.pow(numerator, -exponent));
		} 
		return new org.apache.commons.math.fraction.BigFraction(org.apache.commons.math.util.MathUtils.pow(numerator, exponent) , org.apache.commons.math.util.MathUtils.pow(denominator, exponent));
	}

	public org.apache.commons.math.fraction.BigFraction pow(final java.math.BigInteger exponent) {
		if ((exponent.compareTo(java.math.BigInteger.ZERO)) < 0) {
			final java.math.BigInteger eNeg = exponent.negate();
			return new org.apache.commons.math.fraction.BigFraction(org.apache.commons.math.util.MathUtils.pow(denominator, eNeg) , org.apache.commons.math.util.MathUtils.pow(numerator, eNeg));
		} 
		return new org.apache.commons.math.fraction.BigFraction(org.apache.commons.math.util.MathUtils.pow(numerator, exponent) , org.apache.commons.math.util.MathUtils.pow(denominator, exponent));
	}

	public double pow(final double exponent) {
		return (java.lang.Math.pow(numerator.doubleValue(), exponent)) / (java.lang.Math.pow(denominator.doubleValue(), exponent));
	}

	public org.apache.commons.math.fraction.BigFraction reciprocal() {
		return new org.apache.commons.math.fraction.BigFraction(denominator , numerator);
	}

	public org.apache.commons.math.fraction.BigFraction reduce() {
		final java.math.BigInteger gcd = numerator.gcd(denominator);
		return new org.apache.commons.math.fraction.BigFraction(numerator.divide(gcd) , denominator.divide(gcd));
	}

	public org.apache.commons.math.fraction.BigFraction subtract(final java.math.BigInteger bg) {
		return new org.apache.commons.math.fraction.BigFraction(numerator.subtract(denominator.multiply(bg)) , denominator);
	}

	public org.apache.commons.math.fraction.BigFraction subtract(final int i) {
		return subtract(java.math.BigInteger.valueOf(i));
	}

	public org.apache.commons.math.fraction.BigFraction subtract(final long l) {
		return subtract(java.math.BigInteger.valueOf(l));
	}

	public org.apache.commons.math.fraction.BigFraction subtract(final org.apache.commons.math.fraction.BigFraction fraction) {
		if (ZERO.equals(fraction)) {
			return this;
		} 
		java.math.BigInteger num = null;
		java.math.BigInteger den = null;
		if (denominator.equals(fraction.denominator)) {
			num = numerator.subtract(fraction.numerator);
			den = denominator;
		} else {
			num = numerator.multiply(fraction.denominator).subtract(fraction.numerator.multiply(denominator));
			den = denominator.multiply(fraction.denominator);
		}
		return new org.apache.commons.math.fraction.BigFraction(num , den);
	}

	@java.lang.Override
	public java.lang.String toString() {
		java.lang.String str = null;
		if (java.math.BigInteger.ONE.equals(denominator)) {
			str = numerator.toString();
		} else if (java.math.BigInteger.ZERO.equals(numerator)) {
			str = "0";
		} else {
			str = ((numerator) + " / ") + (denominator);
		}
		return str;
	}

	public org.apache.commons.math.fraction.BigFractionField getField() {
		return org.apache.commons.math.fraction.BigFractionField.getInstance();
	}
}

