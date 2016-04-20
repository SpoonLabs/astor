package org.apache.commons.math.stat.descriptive.moment;


public class FourthMoment extends org.apache.commons.math.stat.descriptive.moment.ThirdMoment implements java.io.Serializable {
	private static final long serialVersionUID = 4763990447117157611L;

	protected double m4;

	public FourthMoment() {
		super();
		m4 = java.lang.Double.NaN;
	}

	public FourthMoment(org.apache.commons.math.stat.descriptive.moment.FourthMoment original) {
		super();
		org.apache.commons.math.stat.descriptive.moment.FourthMoment.copy(original, this);
	}

	@java.lang.Override
	public void increment(final double d) {
		if ((n) < 1) {
			m4 = 0.0;
			m3 = 0.0;
			m2 = 0.0;
			m1 = 0.0;
		} 
		double prevM3 = m3;
		double prevM2 = m2;
		super.increment(d);
		double n0 = n;
		m4 = (((m4) - ((4.0 * (nDev)) * prevM3)) + ((6.0 * (nDevSq)) * prevM2)) + (((n0 * n0) - (3 * (n0 - 1))) * ((((nDevSq) * (nDevSq)) * (n0 - 1)) * n0));
	}

	@java.lang.Override
	public double getResult() {
		return m4;
	}

	@java.lang.Override
	public void clear() {
		super.clear();
		m4 = java.lang.Double.NaN;
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.moment.FourthMoment copy() {
		org.apache.commons.math.stat.descriptive.moment.FourthMoment result = new org.apache.commons.math.stat.descriptive.moment.FourthMoment();
		org.apache.commons.math.stat.descriptive.moment.FourthMoment.copy(this, result);
		return result;
	}

	public static void copy(org.apache.commons.math.stat.descriptive.moment.FourthMoment source, org.apache.commons.math.stat.descriptive.moment.FourthMoment dest) {
		org.apache.commons.math.stat.descriptive.moment.ThirdMoment.copy(source, dest);
		dest.m4 = source.m4;
	}
}

