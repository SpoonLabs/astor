package org.apache.commons.math.analysis;


public class MonitoredFunction implements org.apache.commons.math.analysis.UnivariateRealFunction {
	public MonitoredFunction(org.apache.commons.math.analysis.UnivariateRealFunction f) {
		callsCount = 0;
		this.f = f;
	}

	public void setCallsCount(int callsCount) {
		this.callsCount = callsCount;
	}

	public int getCallsCount() {
		return callsCount;
	}

	public double value(double x) throws org.apache.commons.math.FunctionEvaluationException {
		++(callsCount);
		return f.value(x);
	}

	private int callsCount;

	private org.apache.commons.math.analysis.UnivariateRealFunction f;
}

