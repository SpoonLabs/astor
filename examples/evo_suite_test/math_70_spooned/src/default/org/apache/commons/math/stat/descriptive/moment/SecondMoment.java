package org.apache.commons.math.stat.descriptive.moment;


public class SecondMoment extends org.apache.commons.math.stat.descriptive.moment.FirstMoment implements java.io.Serializable {
	private static final long serialVersionUID = 3942403127395076445L;

	protected double m2;

	public SecondMoment() {
		super();
		m2 = java.lang.Double.NaN;
	}

	public SecondMoment(org.apache.commons.math.stat.descriptive.moment.SecondMoment original) {
		super(original);
		this.m2 = original.m2;
	}

	@java.lang.Override
	public void increment(final double d) {
		if ((n) < 1) {
			m1 = m2 = 0.0;
		} 
		super.increment(d);
		m2 += ((((double)(n)) - 1) * (dev)) * (nDev);
	}

	@java.lang.Override
	public void clear() {
		super.clear();
		m2 = java.lang.Double.NaN;
	}

	@java.lang.Override
	public double getResult() {
		return m2;
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.moment.SecondMoment copy() {
		org.apache.commons.math.stat.descriptive.moment.SecondMoment result = new org.apache.commons.math.stat.descriptive.moment.SecondMoment();
		org.apache.commons.math.stat.descriptive.moment.SecondMoment.copy(this, result);
		return result;
	}

	public static void copy(org.apache.commons.math.stat.descriptive.moment.SecondMoment source, org.apache.commons.math.stat.descriptive.moment.SecondMoment dest) {
		org.apache.commons.math.stat.descriptive.moment.FirstMoment.copy(source, dest);
		dest.m2 = source.m2;
	}
}

