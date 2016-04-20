package org.apache.commons.math.stat.descriptive.moment;


public class FirstMoment extends org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic implements java.io.Serializable {
	private static final long serialVersionUID = 6112755307178490473L;

	protected long n;

	protected double m1;

	protected double dev;

	protected double nDev;

	public FirstMoment() {
		n = 0;
		m1 = java.lang.Double.NaN;
		dev = java.lang.Double.NaN;
		nDev = java.lang.Double.NaN;
	}

	public FirstMoment(org.apache.commons.math.stat.descriptive.moment.FirstMoment original) {
		super();
		org.apache.commons.math.stat.descriptive.moment.FirstMoment.copy(original, this);
	}

	@java.lang.Override
	public void increment(final double d) {
		if ((n) == 0) {
			m1 = 0.0;
		} 
		(n)++;
		double n0 = n;
		dev = d - (m1);
		nDev = (dev) / n0;
		m1 += nDev;
	}

	@java.lang.Override
	public void clear() {
		m1 = java.lang.Double.NaN;
		n = 0;
		dev = java.lang.Double.NaN;
		nDev = java.lang.Double.NaN;
	}

	@java.lang.Override
	public double getResult() {
		return m1;
	}

	public long getN() {
		return n;
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.moment.FirstMoment copy() {
		org.apache.commons.math.stat.descriptive.moment.FirstMoment result = new org.apache.commons.math.stat.descriptive.moment.FirstMoment();
		org.apache.commons.math.stat.descriptive.moment.FirstMoment.copy(this, result);
		return result;
	}

	public static void copy(org.apache.commons.math.stat.descriptive.moment.FirstMoment source, org.apache.commons.math.stat.descriptive.moment.FirstMoment dest) {
		dest.n = source.n;
		dest.m1 = source.m1;
		dest.dev = source.dev;
		dest.nDev = source.nDev;
	}
}

