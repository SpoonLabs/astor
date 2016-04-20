package org.apache.commons.math.util;


public abstract class ContinuedFraction {
	private static final double DEFAULT_EPSILON = 1.0E-8;

	protected ContinuedFraction() {
		super();
	}

	protected abstract double getA(int n, double x);

	protected abstract double getB(int n, double x);

	public double evaluate(double x) throws org.apache.commons.math.MathException {
		return evaluate(x, DEFAULT_EPSILON, java.lang.Integer.MAX_VALUE);
	}

	public double evaluate(double x, double epsilon) throws org.apache.commons.math.MathException {
		return evaluate(x, epsilon, java.lang.Integer.MAX_VALUE);
	}

	public double evaluate(double x, int maxIterations) throws org.apache.commons.math.MathException {
		return evaluate(x, DEFAULT_EPSILON, maxIterations);
	}

	public double evaluate(double x, double epsilon, int maxIterations) throws org.apache.commons.math.MathException {
		double p0 = 1.0;
		double p1 = getA(0, x);
		double q0 = 0.0;
		double q1 = 1.0;
		double c = p1 / q1;
		int n = 0;
		double relativeError = java.lang.Double.MAX_VALUE;
		while ((n < maxIterations) && (relativeError > epsilon)) {
			++n;
			double a = getA(n, x);
			double b = getB(n, x);
			double p2 = (a * p1) + (b * p0);
			double q2 = (a * q1) + (b * q0);
			boolean infinite = false;
			if ((java.lang.Double.isInfinite(p2)) || (java.lang.Double.isInfinite(q2))) {
				double scaleFactor = 1.0;
				double lastScaleFactor = 1.0;
				final int maxPower = 5;
				final double scale = java.lang.Math.max(a, b);
				if (scale <= 0) {
					throw new org.apache.commons.math.ConvergenceException("Continued fraction convergents diverged to +/- infinity for value {0}" , x);
				} 
				infinite = true;
				for (int i = 0 ; i < maxPower ; i++) {
					lastScaleFactor = scaleFactor;
					scaleFactor *= scale;
					if ((a != 0.0) && (a > b)) {
						p2 = (p1 / lastScaleFactor) + ((b / scaleFactor) * p0);
						q2 = (q1 / lastScaleFactor) + ((b / scaleFactor) * q0);
					} else if (b != 0) {
						p2 = ((a / scaleFactor) * p1) + (p0 / lastScaleFactor);
						q2 = ((a / scaleFactor) * q1) + (q0 / lastScaleFactor);
					} 
					infinite = (java.lang.Double.isInfinite(p2)) || (java.lang.Double.isInfinite(q2));
					if (!infinite) {
						break;
					} 
				}
			} 
			if (infinite) {
				throw new org.apache.commons.math.ConvergenceException("Continued fraction convergents diverged to +/- infinity for value {0}" , x);
			} 
			double r = p2 / q2;
			if (java.lang.Double.isNaN(r)) {
				throw new org.apache.commons.math.ConvergenceException("Continued fraction diverged to NaN for value {0}" , x);
			} 
			relativeError = java.lang.Math.abs(((r / c) - 1.0));
			c = p2 / q2;
			p0 = p1;
			p1 = p2;
			q0 = q1;
			q1 = q2;
		}
		if (n >= maxIterations) {
			throw new org.apache.commons.math.MaxIterationsExceededException(maxIterations , "Continued fraction convergents failed to converge for value {0}" , x);
		} 
		return c;
	}
}

