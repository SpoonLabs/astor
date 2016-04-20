package org.apache.commons.math.stat.descriptive.moment;


public class Variance extends org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic implements java.io.Serializable , org.apache.commons.math.stat.descriptive.WeightedEvaluation {
	private static final long serialVersionUID = -9111962718267217978L;

	protected org.apache.commons.math.stat.descriptive.moment.SecondMoment moment = null;

	protected boolean incMoment = true;

	private boolean isBiasCorrected = true;

	public Variance() {
		moment = new org.apache.commons.math.stat.descriptive.moment.SecondMoment();
	}

	public Variance(final org.apache.commons.math.stat.descriptive.moment.SecondMoment m2) {
		incMoment = false;
		this.moment = m2;
	}

	public Variance(boolean isBiasCorrected) {
		moment = new org.apache.commons.math.stat.descriptive.moment.SecondMoment();
		this.isBiasCorrected = isBiasCorrected;
	}

	public Variance(boolean isBiasCorrected ,org.apache.commons.math.stat.descriptive.moment.SecondMoment m2) {
		incMoment = false;
		this.moment = m2;
		this.isBiasCorrected = isBiasCorrected;
	}

	public Variance(org.apache.commons.math.stat.descriptive.moment.Variance original) {
		org.apache.commons.math.stat.descriptive.moment.Variance.copy(original, this);
	}

	@java.lang.Override
	public void increment(final double d) {
		if (incMoment) {
			moment.increment(d);
		} 
	}

	@java.lang.Override
	public double getResult() {
		if ((moment.n) == 0) {
			return java.lang.Double.NaN;
		} else if ((moment.n) == 1) {
			return 0.0;
		} else {
			if (isBiasCorrected) {
				return (moment.m2) / ((moment.n) - 1.0);
			} else {
				return (moment.m2) / (moment.n);
			}
		}
	}

	public long getN() {
		return moment.getN();
	}

	@java.lang.Override
	public void clear() {
		if (incMoment) {
			moment.clear();
		} 
	}

	@java.lang.Override
	public double evaluate(final double[] values) {
		if (values == null) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("input values array is null");
		} 
		return evaluate(values, 0, values.length);
	}

	@java.lang.Override
	public double evaluate(final double[] values, final int begin, final int length) {
		double var = java.lang.Double.NaN;
		if (test(values, begin, length)) {
			clear();
			if (length == 1) {
				var = 0.0;
			} else if (length > 1) {
				org.apache.commons.math.stat.descriptive.moment.Mean mean = new org.apache.commons.math.stat.descriptive.moment.Mean();
				double m = mean.evaluate(values, begin, length);
				var = evaluate(values, m, begin, length);
			} 
		} 
		return var;
	}

	public double evaluate(final double[] values, final double[] weights, final int begin, final int length) {
		double var = java.lang.Double.NaN;
		if (test(values, weights, begin, length)) {
			clear();
			if (length == 1) {
				var = 0.0;
			} else if (length > 1) {
				org.apache.commons.math.stat.descriptive.moment.Mean mean = new org.apache.commons.math.stat.descriptive.moment.Mean();
				double m = mean.evaluate(values, weights, begin, length);
				var = evaluate(values, weights, m, begin, length);
			} 
		} 
		return var;
	}

	public double evaluate(final double[] values, final double[] weights) {
		return evaluate(values, weights, 0, values.length);
	}

	public double evaluate(final double[] values, final double mean, final int begin, final int length) {
		double var = java.lang.Double.NaN;
		if (test(values, begin, length)) {
			if (length == 1) {
				var = 0.0;
			} else if (length > 1) {
				double accum = 0.0;
				double dev = 0.0;
				double accum2 = 0.0;
				for (int i = begin ; i < (begin + length) ; i++) {
					dev = (values[i]) - mean;
					accum += dev * dev;
					accum2 += dev;
				}
				double len = length;
				if (isBiasCorrected) {
					var = (accum - ((accum2 * accum2) / len)) / (len - 1.0);
				} else {
					var = (accum - ((accum2 * accum2) / len)) / len;
				}
			} 
		} 
		return var;
	}

	public double evaluate(final double[] values, final double mean) {
		return evaluate(values, mean, 0, values.length);
	}

	public double evaluate(final double[] values, final double[] weights, final double mean, final int begin, final int length) {
		double var = java.lang.Double.NaN;
		if (test(values, weights, begin, length)) {
			if (length == 1) {
				var = 0.0;
			} else if (length > 1) {
				double accum = 0.0;
				double dev = 0.0;
				double accum2 = 0.0;
				for (int i = begin ; i < (begin + length) ; i++) {
					dev = (values[i]) - mean;
					accum += (weights[i]) * (dev * dev);
					accum2 += (weights[i]) * dev;
				}
				double sumWts = 0;
				for (int i = 0 ; i < (weights.length) ; i++) {
					sumWts += weights[i];
				}
				if (isBiasCorrected) {
					var = (accum - ((accum2 * accum2) / sumWts)) / (sumWts - 1.0);
				} else {
					var = (accum - ((accum2 * accum2) / sumWts)) / sumWts;
				}
			} 
		} 
		return var;
	}

	public double evaluate(final double[] values, final double[] weights, final double mean) {
		return evaluate(values, weights, mean, 0, values.length);
	}

	public boolean isBiasCorrected() {
		return isBiasCorrected;
	}

	public void setBiasCorrected(boolean biasCorrected) {
		this.isBiasCorrected = biasCorrected;
	}

	@java.lang.Override
	public org.apache.commons.math.stat.descriptive.moment.Variance copy() {
		org.apache.commons.math.stat.descriptive.moment.Variance result = new org.apache.commons.math.stat.descriptive.moment.Variance();
		org.apache.commons.math.stat.descriptive.moment.Variance.copy(this, result);
		return result;
	}

	public static void copy(org.apache.commons.math.stat.descriptive.moment.Variance source, org.apache.commons.math.stat.descriptive.moment.Variance dest) {
		dest.moment = source.moment.copy();
		dest.isBiasCorrected = source.isBiasCorrected;
		dest.incMoment = source.incMoment;
	}
}

