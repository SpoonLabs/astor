package org.apache.commons.math.ode.nonstiff;


public abstract class RungeKuttaIntegrator extends org.apache.commons.math.ode.AbstractIntegrator {
	private final double[] c;

	private final double[][] a;

	private final double[] b;

	private final org.apache.commons.math.ode.nonstiff.RungeKuttaStepInterpolator prototype;

	private final double step;

	protected RungeKuttaIntegrator(final java.lang.String name ,final double[] c ,final double[][] a ,final double[] b ,final org.apache.commons.math.ode.nonstiff.RungeKuttaStepInterpolator prototype ,final double step) {
		super(name);
		this.c = c;
		this.a = a;
		this.b = b;
		this.prototype = prototype;
		this.step = java.lang.Math.abs(step);
	}

	public double integrate(final org.apache.commons.math.ode.FirstOrderDifferentialEquations equations, final double t0, final double[] y0, final double t, final double[] y) throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		sanityChecks(equations, t0, y0, t, y);
		setEquations(equations);
		resetEvaluations();
		final boolean forward = t > t0;
		final int stages = (c.length) + 1;
		if (y != y0) {
			java.lang.System.arraycopy(y0, 0, y, 0, y0.length);
		} 
		final double[][] yDotK = new double[stages][];
		for (int i = 0 ; i < stages ; ++i) {
			yDotK[i] = new double[y0.length];
		}
		final double[] yTmp = new double[y0.length];
		org.apache.commons.math.ode.sampling.AbstractStepInterpolator interpolator;
		if ((requiresDenseOutput()) || (!(eventsHandlersManager.isEmpty()))) {
			final org.apache.commons.math.ode.nonstiff.RungeKuttaStepInterpolator rki = ((org.apache.commons.math.ode.nonstiff.RungeKuttaStepInterpolator)(prototype.copy()));
			rki.reinitialize(this, yTmp, yDotK, forward);
			interpolator = rki;
		} else {
			interpolator = new org.apache.commons.math.ode.sampling.DummyStepInterpolator(yTmp , yDotK[(stages - 1)] , forward);
		}
		interpolator.storeTime(t0);
		stepStart = t0;
		stepSize = forward ? step : -(step);
		for (org.apache.commons.math.ode.sampling.StepHandler handler : stepHandlers) {
			handler.reset();
		}
		org.apache.commons.math.ode.events.CombinedEventsManager manager = addEndTimeChecker(t0, t, eventsHandlersManager);
		boolean lastStep = false;
		while (!lastStep) {
			interpolator.shift();
			for (boolean loop = true ; loop ; ) {
				computeDerivatives(stepStart, y, yDotK[0]);
				for (int k = 1 ; k < stages ; ++k) {
					for (int j = 0 ; j < (y0.length) ; ++j) {
						double sum = (a[(k - 1)][0]) * (yDotK[0][j]);
						for (int l = 1 ; l < k ; ++l) {
							sum += (a[(k - 1)][l]) * (yDotK[l][j]);
						}
						yTmp[j] = (y[j]) + ((stepSize) * sum);
					}
					computeDerivatives(((stepStart) + ((c[(k - 1)]) * (stepSize))), yTmp, yDotK[k]);
				}
				for (int j = 0 ; j < (y0.length) ; ++j) {
					double sum = (b[0]) * (yDotK[0][j]);
					for (int l = 1 ; l < stages ; ++l) {
						sum += (b[l]) * (yDotK[l][j]);
					}
					yTmp[j] = (y[j]) + ((stepSize) * sum);
				}
				interpolator.storeTime(((stepStart) + (stepSize)));
				if (manager.evaluateStep(interpolator)) {
					final double dt = (manager.getEventTime()) - (stepStart);
					if ((java.lang.Math.abs(dt)) <= (java.lang.Math.ulp(stepStart))) {
						interpolator.storeTime(stepStart);
						java.lang.System.arraycopy(y, 0, yTmp, 0, y0.length);
						stepSize = 0;
						loop = false;
					} else {
						stepSize = dt;
					}
				} else {
					loop = false;
				}
			}
			final double nextStep = (stepStart) + (stepSize);
			java.lang.System.arraycopy(yTmp, 0, y, 0, y0.length);
			manager.stepAccepted(nextStep, y);
			lastStep = manager.stop();
			interpolator.storeTime(nextStep);
			for (org.apache.commons.math.ode.sampling.StepHandler handler : stepHandlers) {
				handler.handleStep(interpolator, lastStep);
			}
			stepStart = nextStep;
			if ((manager.reset(stepStart, y)) && (!lastStep)) {
				computeDerivatives(stepStart, y, yDotK[0]);
			} 
			stepSize = forward ? step : -(step);
		}
		final double stopTime = stepStart;
		stepStart = java.lang.Double.NaN;
		stepSize = java.lang.Double.NaN;
		return stopTime;
	}
}

