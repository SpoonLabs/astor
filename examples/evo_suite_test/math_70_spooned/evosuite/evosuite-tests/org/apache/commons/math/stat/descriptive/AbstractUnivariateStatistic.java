package org.apache.commons.math.stat.descriptive;


public abstract class AbstractUnivariateStatistic implements org.apache.commons.math.stat.descriptive.UnivariateStatistic {
	public double evaluate(final double[] values) {
		test(values, 0, 0);
		return evaluate(values, 0, values.length);
	}

	public abstract double evaluate(final double[] values, final int begin, final int length);

	public abstract org.apache.commons.math.stat.descriptive.UnivariateStatistic copy();

	protected boolean test(final double[] values, final int begin, final int length) {
		if (values == null) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("input values array is null");
		} 
		if (begin < 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("start position cannot be negative ({0})", begin);
		} 
		if (length < 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("length cannot be negative ({0})", length);
		} 
		if ((begin + length) > (values.length)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("subarray ends after array end");
		} 
		if (length == 0) {
			return false;
		} 
		return true;
	}

	protected boolean test(final double[] values, final double[] weights, final int begin, final int length) {
		if (weights == null) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("input weights array is null");
		} 
		if ((weights.length) != (values.length)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("Different number of weights and values");
		} 
		boolean containsPositiveWeight = false;
		for (int i = begin ; i < (begin + length) ; i++) {
			if (java.lang.Double.isNaN(weights[i])) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("NaN weight at index {0}", i);
			} 
			if (java.lang.Double.isInfinite(weights[i])) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("Infinite weight at index {0}", i);
			} 
			if ((weights[i]) < 0) {
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("negative weight {0} at index {1} ", weights[i], i);
			} 
			if ((!containsPositiveWeight) && ((weights[i]) > 0.0)) {
				containsPositiveWeight = true;
			} 
		}
		if (!containsPositiveWeight) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("weight array must contain at least one non-zero value");
		} 
		return test(values, begin, length);
	}
}

