package org.apache.commons.math.stat.descriptive.moment;


public class ThirdMoment extends org.apache.commons.math.stat.descriptive.moment.SecondMoment implements java.io.Serializable {
	private static final long serialVersionUID = -7818711964045118679L;

	protected double m3;

	protected double nDevSq;

	public ThirdMoment() {
		super();
		m3 = java.lang.Double.NaN;
		nDevSq = java.lang.Double.NaN;
	}

	public ThirdMoment(org.apache.commons.math.stat.descriptive.moment.ThirdMoment original) {
		org.apache.commons.math.stat.descriptive.moment.ThirdMoment.copy(original, this);
	}

	@java.lang.Override
	public void increment(final double d) {
		if ((n) < 1) {
			m3 = m2 = m1 = 0.0;
		} 
		double prevM2 = m2;
		super.increment(d);
		nDevSq = (nDev) * (nDev);
		double n0 = n;
		m3 = ((m3) - ((3.0 * (nDev)) * prevM2)) + ((((n0 - 1) * (n0 - 2)) * (nDevSq)) * (dev));
	}

	@java.lang.Override
	public double getResult() {
		return m3;
	}

	@java.lang.Override
	public void clear() {
		super.clear();
		m3 = java.lang.Double.NaN;
		nDevSq = java.lang.Double.NaN;
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.moment.ThirdMoment copy() {
		org.apache.commons.math.stat.descriptive.moment.ThirdMoment result = new org.apache.commons.math.stat.descriptive.moment.ThirdMoment();
		org.apache.commons.math.stat.descriptive.moment.ThirdMoment.copy(this, result);
		return result;
	}

	public static void copy(org.apache.commons.math.stat.descriptive.moment.ThirdMoment source, org.apache.commons.math.stat.descriptive.moment.ThirdMoment dest) {
		org.apache.commons.math.stat.descriptive.moment.SecondMoment.copy(source, dest);
		dest.m3 = source.m3;
		dest.nDevSq = source.nDevSq;
	}
}

