package org.apache.commons.math.ode;


public class TestProblemHandler implements org.apache.commons.math.ode.sampling.StepHandler {
	private org.apache.commons.math.ode.TestProblemAbstract problem;

	private double maxValueError;

	private double maxTimeError;

	private double lastError;

	private double lastTime;

	private org.apache.commons.math.ode.ODEIntegrator integrator;

	private double expectedStepStart;

	public TestProblemHandler(org.apache.commons.math.ode.TestProblemAbstract problem ,org.apache.commons.math.ode.ODEIntegrator integrator) {
		this.problem = problem;
		this.integrator = integrator;
		reset();
	}

	public boolean requiresDenseOutput() {
		return true;
	}

	public void reset() {
		maxValueError = 0;
		maxTimeError = 0;
		lastError = 0;
		expectedStepStart = java.lang.Double.NaN;
	}

	public void handleStep(org.apache.commons.math.ode.sampling.StepInterpolator interpolator, boolean isLast) throws org.apache.commons.math.ode.DerivativeException {
		double start = integrator.getCurrentStepStart();
		if ((java.lang.Math.abs(((start - (problem.getInitialTime())) / (integrator.getCurrentSignedStepsize())))) > 0.001) {
			if (!(java.lang.Double.isNaN(expectedStepStart))) {
				maxTimeError = java.lang.Math.max(maxTimeError, java.lang.Math.abs((start - (expectedStepStart))));
			} 
			expectedStepStart = start + (integrator.getCurrentSignedStepsize());
		} 
		double pT = interpolator.getPreviousTime();
		double cT = interpolator.getCurrentTime();
		double[] errorScale = problem.getErrorScale();
		if (isLast) {
			double[] interpolatedY = interpolator.getInterpolatedState();
			double[] theoreticalY = problem.computeTheoreticalState(cT);
			for (int i = 0 ; i < (interpolatedY.length) ; ++i) {
				double error = java.lang.Math.abs(((interpolatedY[i]) - (theoreticalY[i])));
				lastError = java.lang.Math.max(error, lastError);
			}
			lastTime = cT;
		} 
		for (int k = 0 ; k <= 20 ; ++k) {
			double time = pT + ((k * (cT - pT)) / 20);
			interpolator.setInterpolatedTime(time);
			double[] interpolatedY = interpolator.getInterpolatedState();
			double[] theoreticalY = problem.computeTheoreticalState(interpolator.getInterpolatedTime());
			for (int i = 0 ; i < (interpolatedY.length) ; ++i) {
				double error = (errorScale[i]) * (java.lang.Math.abs(((interpolatedY[i]) - (theoreticalY[i]))));
				maxValueError = java.lang.Math.max(error, maxValueError);
			}
		}
	}

	public double getMaximalValueError() {
		return maxValueError;
	}

	public double getMaximalTimeError() {
		return maxTimeError;
	}

	public double getLastError() {
		return lastError;
	}

	public double getLastTime() {
		return lastTime;
	}
}

