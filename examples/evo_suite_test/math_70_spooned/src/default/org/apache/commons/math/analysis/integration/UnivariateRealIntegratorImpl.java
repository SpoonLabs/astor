package org.apache.commons.math.analysis.integration;


public abstract class UnivariateRealIntegratorImpl extends org.apache.commons.math.ConvergingAlgorithmImpl implements org.apache.commons.math.analysis.integration.UnivariateRealIntegrator {
	private static final long serialVersionUID = 6248808456637441533L;

	protected int minimalIterationCount;

	protected int defaultMinimalIterationCount;

	protected boolean resultComputed = false;

	protected double result;

	@java.lang.Deprecated
	protected org.apache.commons.math.analysis.UnivariateRealFunction f;

	@java.lang.Deprecated
	protected UnivariateRealIntegratorImpl(final org.apache.commons.math.analysis.UnivariateRealFunction f ,final int defaultMaximalIterationCount) throws java.lang.IllegalArgumentException {
		super(defaultMaximalIterationCount, 1.0E-15);
		if (f == null) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("function is null");
		} 
		this.f = f;
		setRelativeAccuracy(1.0E-6);
		this.defaultMinimalIterationCount = 3;
		this.minimalIterationCount = defaultMinimalIterationCount;
		verifyIterationCount();
	}

	protected UnivariateRealIntegratorImpl(final int defaultMaximalIterationCount) throws java.lang.IllegalArgumentException {
		super(defaultMaximalIterationCount, 1.0E-15);
		setRelativeAccuracy(1.0E-6);
		this.defaultMinimalIterationCount = 3;
		this.minimalIterationCount = defaultMinimalIterationCount;
		verifyIterationCount();
	}

	public double getResult() throws java.lang.IllegalStateException {
		if (resultComputed) {
			return result;
		} else {
			throw org.apache.commons.math.MathRuntimeException.createIllegalStateException("no result available");
		}
	}

	protected final void setResult(double newResult, int iterationCount) {
		this.result = newResult;
		this.iterationCount = iterationCount;
		this.resultComputed = true;
	}

	protected final void clearResult() {
		this.iterationCount = 0;
		this.resultComputed = false;
	}

	public void setMinimalIterationCount(int count) {
		minimalIterationCount = count;
	}

	public int getMinimalIterationCount() {
		return minimalIterationCount;
	}

	public void resetMinimalIterationCount() {
		minimalIterationCount = defaultMinimalIterationCount;
	}

	protected void verifyInterval(double lower, double upper) throws java.lang.IllegalArgumentException {
		if (lower >= upper) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("endpoints do not specify an interval: [{0}, {1}]", lower, upper);
		} 
	}

	protected void verifyIterationCount() throws java.lang.IllegalArgumentException {
		if (((minimalIterationCount) <= 0) || ((maximalIterationCount) <= (minimalIterationCount))) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("invalid iteration limits: min={0}, max={1}", minimalIterationCount, maximalIterationCount);
		} 
	}
}

