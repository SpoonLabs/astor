package org.apache.commons.math.fraction;


public class Fraction extends java.lang.Number implements java.io.Serializable , java.lang.Comparable<org.apache.commons.math.fraction.Fraction> , org.apache.commons.math.FieldElement<org.apache.commons.math.fraction.Fraction> {
	public static final org.apache.commons.math.fraction.Fraction TWO = new org.apache.commons.math.fraction.Fraction(2 , 1);

	public static final org.apache.commons.math.fraction.Fraction ONE = new org.apache.commons.math.fraction.Fraction(1 , 1);

	public static final org.apache.commons.math.fraction.Fraction ZERO = new org.apache.commons.math.fraction.Fraction(0 , 1);

	public static final org.apache.commons.math.fraction.Fraction FOUR_FIFTHS = new org.apache.commons.math.fraction.Fraction(4 , 5);

	public static final org.apache.commons.math.fraction.Fraction ONE_FIFTH = new org.apache.commons.math.fraction.Fraction(1 , 5);

	public static final org.apache.commons.math.fraction.Fraction ONE_HALF = new org.apache.commons.math.fraction.Fraction(1 , 2);

	public static final org.apache.commons.math.fraction.Fraction ONE_QUARTER = new org.apache.commons.math.fraction.Fraction(1 , 4);

	public static final org.apache.commons.math.fraction.Fraction ONE_THIRD = new org.apache.commons.math.fraction.Fraction(1 , 3);

	public static final org.apache.commons.math.fraction.Fraction THREE_FIFTHS = new org.apache.commons.math.fraction.Fraction(3 , 5);

	public static final org.apache.commons.math.fraction.Fraction THREE_QUARTERS = new org.apache.commons.math.fraction.Fraction(3 , 4);

	public static final org.apache.commons.math.fraction.Fraction TWO_FIFTHS = new org.apache.commons.math.fraction.Fraction(2 , 5);

	public static final org.apache.commons.math.fraction.Fraction TWO_QUARTERS = new org.apache.commons.math.fraction.Fraction(2 , 4);

	public static final org.apache.commons.math.fraction.Fraction TWO_THIRDS = new org.apache.commons.math.fraction.Fraction(2 , 3);

	public static final org.apache.commons.math.fraction.Fraction MINUS_ONE = new org.apache.commons.math.fraction.Fraction(-1 , 1);

	private static final java.lang.String ZERO_DENOMINATOR_MESSAGE = "zero denominator in fraction {0}/{1}";

	private static final java.lang.String OVERFLOW_MESSAGE = "overflow in fraction {0}/{1}, cannot negate";

	private static final java.lang.String NULL_FRACTION = "null fraction";

	private static final long serialVersionUID = 3698073679419233275L;

	private final int denominator;

	private final int numerator;

	public Fraction(double value) throws org.apache.commons.math.fraction.FractionConversionException {
		this(value, 1.0E-5, 100);
	}

	public Fraction(double value ,double epsilon ,int maxIterations) throws org.apache.commons.math.fraction.FractionConversionException {
		this(value, epsilon, java.lang.Integer.MAX_VALUE, maxIterations);
	}

	public Fraction(double value ,int maxDenominator) throws org.apache.commons.math.fraction.FractionConversionException {
		this(value, 0, maxDenominator, 100);
	}

	private Fraction(double value ,double epsilon ,int maxDenominator ,int maxIterations) throws org.apache.commons.math.fraction.FractionConversionException {
		long overflow = java.lang.Integer.MAX_VALUE;
		double r0 = value;
		long a0 = ((long)(java.lang.Math.floor(r0)));
		if (a0 > overflow) {
			throw new org.apache.commons.math.fraction.FractionConversionException(value , a0 , 1L);
		} 
		if ((java.lang.Math.abs((a0 - value))) < epsilon) {
			this.numerator = ((int)(a0));
			this.denominator = 1;
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
			double r1 = 1.0 / (r0 - a0);
			long a1 = ((long)(java.lang.Math.floor(r1)));
			p2 = (a1 * p1) + p0;
			q2 = (a1 * q1) + q0;
			if ((p2 > overflow) || (q2 > overflow)) {
				throw new org.apache.commons.math.fraction.FractionConversionException(value , p2 , q2);
			} 
			double convergent = ((double)(p2)) / ((double)(q2));
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
			this.numerator = ((int)(p2));
			this.denominator = ((int)(q2));
		} else {
			this.numerator = ((int)(p1));
			this.denominator = ((int)(q1));
		}
	}

	public Fraction(int num) {
		this(num, 1);
	}

	public Fraction(int num ,int den) {
		if (den == 0) {
			throw org.apache.commons.math.MathRuntimeException.createArithmeticException(ZERO_DENOMINATOR_MESSAGE, num, den);
		} 
		if (den < 0) {
			if ((num == (java.lang.Integer.MIN_VALUE)) || (den == (java.lang.Integer.MIN_VALUE))) {
				throw org.apache.commons.math.MathRuntimeException.createArithmeticException(OVERFLOW_MESSAGE, num, den);
			} 
			num = -num;
			den = -den;
		} 
		final int d = org.apache.commons.math.util.MathUtils.gcd(num, den);
		if (d > 1) {
			num /= d;
			den /= d;
		} 
		if (den < 0) {
			num = -num;
			den = -den;
		} 
		this.numerator = num;
		this.denominator = den;
	}

	public org.apache.commons.math.fraction.Fraction abs() {
		org.apache.commons.math.fraction.Fraction ret;
		if ((numerator) >= 0) {
			ret = this;
		} else {
			ret = negate();
		}
		return ret;
	}

	public int compareTo(org.apache.commons.math.fraction.Fraction object) {
		long nOd = ((long)(numerator)) * (object.denominator);
		long dOn = ((long)(denominator)) * (object.numerator);
		return nOd < dOn ? -1 : nOd > dOn ? +1 : 0;
	}

	@java.lang.Override
	public double doubleValue() {
		return ((double)(numerator)) / ((double)(denominator));
	}

	@java.lang.Override
	public boolean equals(java.lang.Object other) {
		if ((this) == other) {
			return true;
		} 
		if (other instanceof org.apache.commons.math.fraction.Fraction) {
			org.apache.commons.math.fraction.Fraction rhs = ((org.apache.commons.math.fraction.Fraction)(other));
			return ((numerator) == (rhs.numerator)) && ((denominator) == (rhs.denominator));
		} 
		return false;
	}

	@java.lang.Override
	public float floatValue() {
		return ((float)(doubleValue()));
	}

	public int getDenominator() {
		return denominator;
	}

	public int getNumerator() {
		return numerator;
	}

	@java.lang.Override
	public int hashCode() {
		return (37 * ((37 * 17) + (numerator))) + (denominator);
	}

	@java.lang.Override
	public int intValue() {
		return ((int)(doubleValue()));
	}

	@java.lang.Override
	public long longValue() {
		return ((long)(doubleValue()));
	}

	public org.apache.commons.math.fraction.Fraction negate() {
		if ((numerator) == (java.lang.Integer.MIN_VALUE)) {
			throw org.apache.commons.math.MathRuntimeException.createArithmeticException(OVERFLOW_MESSAGE, numerator, denominator);
		} 
		return new org.apache.commons.math.fraction.Fraction(-(numerator) , denominator);
	}

	public org.apache.commons.math.fraction.Fraction reciprocal() {
		return new org.apache.commons.math.fraction.Fraction(denominator , numerator);
	}

	public org.apache.commons.math.fraction.Fraction add(org.apache.commons.math.fraction.Fraction fraction) {
		return addSub(fraction, true);
	}

	public org.apache.commons.math.fraction.Fraction add(final int i) {
		return new org.apache.commons.math.fraction.Fraction(((numerator) + (i * (denominator))) , denominator);
	}

	public org.apache.commons.math.fraction.Fraction subtract(org.apache.commons.math.fraction.Fraction fraction) {
		return addSub(fraction, false);
	}

	public org.apache.commons.math.fraction.Fraction subtract(final int i) {
		return new org.apache.commons.math.fraction.Fraction(((numerator) - (i * (denominator))) , denominator);
	}

	private org.apache.commons.math.fraction.Fraction addSub(org.apache.commons.math.fraction.Fraction fraction, boolean isAdd) {
		if (fraction == null) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(NULL_FRACTION);
		} 
		if ((numerator) == 0) {
			return isAdd ? fraction : fraction.negate();
		} 
		if ((fraction.numerator) == 0) {
			return this;
		} 
		int d1 = org.apache.commons.math.util.MathUtils.gcd(denominator, fraction.denominator);
		if (d1 == 1) {
			int uvp = org.apache.commons.math.util.MathUtils.mulAndCheck(numerator, fraction.denominator);
			int upv = org.apache.commons.math.util.MathUtils.mulAndCheck(fraction.numerator, denominator);
			return new org.apache.commons.math.fraction.Fraction((isAdd ? org.apache.commons.math.util.MathUtils.addAndCheck(uvp, upv) : org.apache.commons.math.util.MathUtils.subAndCheck(uvp, upv)) , org.apache.commons.math.util.MathUtils.mulAndCheck(denominator, fraction.denominator));
		} 
		java.math.BigInteger uvp = java.math.BigInteger.valueOf(numerator).multiply(java.math.BigInteger.valueOf(((fraction.denominator) / d1)));
		java.math.BigInteger upv = java.math.BigInteger.valueOf(fraction.numerator).multiply(java.math.BigInteger.valueOf(((denominator) / d1)));
		java.math.BigInteger t = isAdd ? uvp.add(upv) : uvp.subtract(upv);
		int tmodd1 = t.mod(java.math.BigInteger.valueOf(d1)).intValue();
		int d2 = tmodd1 == 0 ? d1 : org.apache.commons.math.util.MathUtils.gcd(tmodd1, d1);
		java.math.BigInteger w = t.divide(java.math.BigInteger.valueOf(d2));
		if ((w.bitLength()) > 31) {
			throw org.apache.commons.math.MathRuntimeException.createArithmeticException("overflow, numerator too large after multiply: {0}", w);
		} 
		return new org.apache.commons.math.fraction.Fraction(w.intValue() , org.apache.commons.math.util.MathUtils.mulAndCheck(((denominator) / d1), ((fraction.denominator) / d2)));
	}

	public org.apache.commons.math.fraction.Fraction multiply(org.apache.commons.math.fraction.Fraction fraction) {
		if (fraction == null) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(NULL_FRACTION);
		} 
		if (((numerator) == 0) || ((fraction.numerator) == 0)) {
			return ZERO;
		} 
		int d1 = org.apache.commons.math.util.MathUtils.gcd(numerator, fraction.denominator);
		int d2 = org.apache.commons.math.util.MathUtils.gcd(fraction.numerator, denominator);
		return org.apache.commons.math.fraction.Fraction.getReducedFraction(org.apache.commons.math.util.MathUtils.mulAndCheck(((numerator) / d1), ((fraction.numerator) / d2)), org.apache.commons.math.util.MathUtils.mulAndCheck(((denominator) / d2), ((fraction.denominator) / d1)));
	}

	public org.apache.commons.math.fraction.Fraction multiply(final int i) {
		return new org.apache.commons.math.fraction.Fraction(((numerator) * i) , denominator);
	}

	public org.apache.commons.math.fraction.Fraction divide(org.apache.commons.math.fraction.Fraction fraction) {
		if (fraction == null) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(NULL_FRACTION);
		} 
		if ((fraction.numerator) == 0) {
			throw org.apache.commons.math.MathRuntimeException.createArithmeticException("the fraction to divide by must not be zero: {0}/{1}", fraction.numerator, fraction.denominator);
		} 
		return multiply(fraction.reciprocal());
	}

	public org.apache.commons.math.fraction.Fraction divide(final int i) {
		return new org.apache.commons.math.fraction.Fraction(numerator , ((denominator) * i));
	}

	public static org.apache.commons.math.fraction.Fraction getReducedFraction(int numerator, int denominator) {
		if (denominator == 0) {
			throw org.apache.commons.math.MathRuntimeException.createArithmeticException(ZERO_DENOMINATOR_MESSAGE, numerator, denominator);
		} 
		if (numerator == 0) {
			return ZERO;
		} 
		if ((denominator == (java.lang.Integer.MIN_VALUE)) && ((numerator & 1) == 0)) {
			numerator /= 2;
			denominator /= 2;
		} 
		if (denominator < 0) {
			if ((numerator == (java.lang.Integer.MIN_VALUE)) || (denominator == (java.lang.Integer.MIN_VALUE))) {
				throw org.apache.commons.math.MathRuntimeException.createArithmeticException(OVERFLOW_MESSAGE, numerator, denominator);
			} 
			numerator = -numerator;
			denominator = -denominator;
		} 
		int gcd = org.apache.commons.math.util.MathUtils.gcd(numerator, denominator);
		numerator /= gcd;
		denominator /= gcd;
		return new org.apache.commons.math.fraction.Fraction(numerator , denominator);
	}

	@java.lang.Override
	public java.lang.String toString() {
		java.lang.String str = null;
		if ((denominator) == 1) {
			str = java.lang.Integer.toString(numerator);
		} else {
			if ((numerator) == 0) {
				str = "0";
			} else {
				str = ((numerator) + " / ") + (denominator);
			}
		}
		return str;
	}

	public org.apache.commons.math.fraction.FractionField getField() {
		return org.apache.commons.math.fraction.FractionField.getInstance();
	}
}

