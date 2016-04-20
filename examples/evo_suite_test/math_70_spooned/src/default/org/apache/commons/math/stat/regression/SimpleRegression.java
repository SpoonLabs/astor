package org.apache.commons.math.stat.regression;


public class SimpleRegression implements java.io.Serializable {
	private static final long serialVersionUID = -3004689053607543335L;

	private org.apache.commons.math.distribution.TDistribution distribution;

	private double sumX = 0.0;

	private double sumXX = 0.0;

	private double sumY = 0.0;

	private double sumYY = 0.0;

	private double sumXY = 0.0;

	private long n = 0;

	private double xbar = 0;

	private double ybar = 0;

	public SimpleRegression() {
		this(new org.apache.commons.math.distribution.TDistributionImpl(1.0));
	}

	public SimpleRegression(org.apache.commons.math.distribution.TDistribution t) {
		super();
		setDistribution(t);
	}

	public void addData(double x, double y) {
		if ((n) == 0) {
			xbar = x;
			ybar = y;
		} else {
			double dx = x - (xbar);
			double dy = y - (ybar);
			sumXX += ((dx * dx) * ((double)(n))) / ((n) + 1.0);
			sumYY += ((dy * dy) * ((double)(n))) / ((n) + 1.0);
			sumXY += ((dx * dy) * ((double)(n))) / ((n) + 1.0);
			xbar += dx / ((n) + 1.0);
			ybar += dy / ((n) + 1.0);
		}
		sumX += x;
		sumY += y;
		(n)++;
		if ((n) > 2) {
			distribution.setDegreesOfFreedom(((n) - 2));
		} 
	}

	public void removeData(double x, double y) {
		if ((n) > 0) {
			double dx = x - (xbar);
			double dy = y - (ybar);
			sumXX -= ((dx * dx) * ((double)(n))) / ((n) - 1.0);
			sumYY -= ((dy * dy) * ((double)(n))) / ((n) - 1.0);
			sumXY -= ((dx * dy) * ((double)(n))) / ((n) - 1.0);
			xbar -= dx / ((n) - 1.0);
			ybar -= dy / ((n) - 1.0);
			sumX -= x;
			sumY -= y;
			(n)--;
			if ((n) > 2) {
				distribution.setDegreesOfFreedom(((n) - 2));
			} 
		} 
	}

	public void addData(double[][] data) {
		for (int i = 0 ; i < (data.length) ; i++) {
			addData(data[i][0], data[i][1]);
		}
	}

	public void removeData(double[][] data) {
		for (int i = 0 ; (i < (data.length)) && ((n) > 0) ; i++) {
			removeData(data[i][0], data[i][1]);
		}
	}

	public void clear() {
		sumX = 0.0;
		sumXX = 0.0;
		sumY = 0.0;
		sumYY = 0.0;
		sumXY = 0.0;
		n = 0;
	}

	public long getN() {
		return n;
	}

	public double predict(double x) {
		double b1 = getSlope();
		return (getIntercept(b1)) + (b1 * x);
	}

	public double getIntercept() {
		return getIntercept(getSlope());
	}

	public double getSlope() {
		if ((n) < 2) {
			return java.lang.Double.NaN;
		} 
		if ((java.lang.Math.abs(sumXX)) < (10 * (java.lang.Double.MIN_VALUE))) {
			return java.lang.Double.NaN;
		} 
		return (sumXY) / (sumXX);
	}

	public double getSumSquaredErrors() {
		return java.lang.Math.max(0.0, ((sumYY) - (((sumXY) * (sumXY)) / (sumXX))));
	}

	public double getTotalSumSquares() {
		if ((n) < 2) {
			return java.lang.Double.NaN;
		} 
		return sumYY;
	}

	public double getXSumSquares() {
		if ((n) < 2) {
			return java.lang.Double.NaN;
		} 
		return sumXX;
	}

	public double getSumOfCrossProducts() {
		return sumXY;
	}

	public double getRegressionSumSquares() {
		return getRegressionSumSquares(getSlope());
	}

	public double getMeanSquareError() {
		if ((n) < 3) {
			return java.lang.Double.NaN;
		} 
		return (getSumSquaredErrors()) / ((n) - 2);
	}

	public double getR() {
		double b1 = getSlope();
		double result = java.lang.Math.sqrt(getRSquare());
		if (b1 < 0) {
			result = -result;
		} 
		return result;
	}

	public double getRSquare() {
		double ssto = getTotalSumSquares();
		return (ssto - (getSumSquaredErrors())) / ssto;
	}

	public double getInterceptStdErr() {
		return java.lang.Math.sqrt(((getMeanSquareError()) * ((1.0 / ((double)(n))) + (((xbar) * (xbar)) / (sumXX)))));
	}

	public double getSlopeStdErr() {
		return java.lang.Math.sqrt(((getMeanSquareError()) / (sumXX)));
	}

	public double getSlopeConfidenceInterval() throws org.apache.commons.math.MathException {
		return getSlopeConfidenceInterval(0.05);
	}

	public double getSlopeConfidenceInterval(double alpha) throws org.apache.commons.math.MathException {
		if ((alpha >= 1) || (alpha <= 0)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("out of bounds significance level {0}, must be between {1} and {2}", alpha, 0.0, 1.0);
		} 
		return (getSlopeStdErr()) * (distribution.inverseCumulativeProbability((1.0 - (alpha / 2.0))));
	}

	public double getSignificance() throws org.apache.commons.math.MathException {
		return 2.0 * (1.0 - (distribution.cumulativeProbability(((java.lang.Math.abs(getSlope())) / (getSlopeStdErr())))));
	}

	private double getIntercept(double slope) {
		return ((sumY) - (slope * (sumX))) / (n);
	}

	private double getRegressionSumSquares(double slope) {
		return (slope * slope) * (sumXX);
	}

	public void setDistribution(org.apache.commons.math.distribution.TDistribution value) {
		distribution = value;
		if ((n) > 2) {
			distribution.setDegreesOfFreedom(((n) - 2));
		} 
	}
}

