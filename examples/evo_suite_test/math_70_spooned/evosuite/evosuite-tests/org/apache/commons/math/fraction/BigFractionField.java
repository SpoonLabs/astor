package org.apache.commons.math.fraction;


public class BigFractionField implements java.io.Serializable , org.apache.commons.math.Field<org.apache.commons.math.fraction.BigFraction> {
	private static final long serialVersionUID = -1699294557189741703L;

	private BigFractionField() {
	}

	public static org.apache.commons.math.fraction.BigFractionField getInstance() {
		return org.apache.commons.math.fraction.BigFractionField.LazyHolder.INSTANCE;
	}

	public org.apache.commons.math.fraction.BigFraction getOne() {
		return org.apache.commons.math.fraction.BigFraction.ONE;
	}

	public org.apache.commons.math.fraction.BigFraction getZero() {
		return org.apache.commons.math.fraction.BigFraction.ZERO;
	}

	private static class LazyHolder {
		private static final org.apache.commons.math.fraction.BigFractionField INSTANCE = new org.apache.commons.math.fraction.BigFractionField();
	}

	private java.lang.Object readResolve() {
		return org.apache.commons.math.fraction.BigFractionField.LazyHolder.INSTANCE;
	}
}

