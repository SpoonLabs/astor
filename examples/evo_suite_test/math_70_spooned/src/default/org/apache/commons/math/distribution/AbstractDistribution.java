package org.apache.commons.math.distribution;


public abstract class AbstractDistribution implements java.io.Serializable , org.apache.commons.math.distribution.Distribution {
	private static final long serialVersionUID = -38038050983108802L;

	protected AbstractDistribution() {
		super();
	}

	public double cumulativeProbability(double x0, double x1) throws org.apache.commons.math.MathException {
		if (x0 > x1) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("lower endpoint ({0}) must be less than or equal to upper endpoint ({1})", x0, x1);
		} 
		return (cumulativeProbability(x1)) - (cumulativeProbability(x0));
	}
}

