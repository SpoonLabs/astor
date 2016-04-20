package org.apache.commons.math.ode.nonstiff;


public class AdamsBashforthIntegrator extends org.apache.commons.math.ode.nonstiff.AdamsIntegrator {
	public AdamsBashforthIntegrator(final int nSteps ,final double minStep ,final double maxStep ,final double scalAbsoluteTolerance ,final double scalRelativeTolerance) throws java.lang.IllegalArgumentException {
		super("Adams-Bashforth", nSteps, nSteps, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
	}

	public AdamsBashforthIntegrator(final int nSteps ,final double minStep ,final double maxStep ,final double[] vecAbsoluteTolerance ,final double[] vecRelativeTolerance) throws java.lang.IllegalArgumentException {
		super("Adams-Bashforth", nSteps, nSteps, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
	}

	@java.lang.Override
	public double integrate(final org.apache.commons.math.ode.FirstOrderDifferentialEquations equations, final double t0, final double[] y0, final double t, final double[] y) throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		final int n = y0.length;
		sanityChecks(equations, t0, y0, t, y);
		setEquations(equations);
		resetEvaluations();
		final boolean forward = t > t0;
		if (y != y0) {
			java.lang.System.arraycopy(y0, 0, y, 0, n);
		} 
		final double[] yDot = new double[n];
		final double[] yTmp = new double[y0.length];
		final org.apache.commons.math.ode.sampling.NordsieckStepInterpolator interpolator = new org.apache.commons.math.ode.sampling.NordsieckStepInterpolator();
		interpolator.reinitialize(y, forward);
		final org.apache.commons.math.ode.sampling.NordsieckStepInterpolator interpolatorTmp = new org.apache.commons.math.ode.sampling.NordsieckStepInterpolator();
		interpolatorTmp.reinitialize(yTmp, forward);
		for (org.apache.commons.math.ode.sampling.StepHandler handler : stepHandlers) {
			handler.reset();
		}
		org.apache.commons.math.ode.events.CombinedEventsManager manager = addEndTimeChecker(t0, t, eventsHandlersManager);
		start(t0, y, t);
		interpolator.reinitialize(stepStart, stepSize, scaled, nordsieck);
		interpolator.storeTime(stepStart);
		final int lastRow = (nordsieck.getRowDimension()) - 1;
		double hNew = stepSize;
		interpolator.rescale(hNew);
		boolean lastStep = false;
		while (!lastStep) {
			interpolator.shift();
			double error = 0;
			for (boolean loop = true ; loop ; ) {
				stepSize = hNew;
				error = 0;
				for (int i = 0 ; i < (y0.length) ; ++i) {
					final double yScale = java.lang.Math.abs(y[i]);
					final double tol = (vecAbsoluteTolerance) == null ? (scalAbsoluteTolerance) + ((scalRelativeTolerance) * yScale) : (vecAbsoluteTolerance[i]) + ((vecRelativeTolerance[i]) * yScale);
					final double ratio = (nordsieck.getEntry(lastRow, i)) / tol;
					error += ratio * ratio;
				}
				error = java.lang.Math.sqrt((error / (y0.length)));
				if (error <= 1.0) {
					final double stepEnd = (stepStart) + (stepSize);
					interpolator.setInterpolatedTime(stepEnd);
					java.lang.System.arraycopy(interpolator.getInterpolatedState(), 0, yTmp, 0, y0.length);
					computeDerivatives(stepEnd, yTmp, yDot);
					final double[] predictedScaled = new double[y0.length];
					for (int j = 0 ; j < (y0.length) ; ++j) {
						predictedScaled[j] = (stepSize) * (yDot[j]);
					}
					final org.apache.commons.math.linear.Array2DRowRealMatrix nordsieckTmp = updateHighOrderDerivativesPhase1(nordsieck);
					updateHighOrderDerivativesPhase2(scaled, predictedScaled, nordsieckTmp);
					interpolatorTmp.reinitialize(stepEnd, stepSize, predictedScaled, nordsieckTmp);
					interpolatorTmp.storeTime(stepStart);
					interpolatorTmp.shift();
					interpolatorTmp.storeTime(stepEnd);
					if (manager.evaluateStep(interpolatorTmp)) {
						final double dt = (manager.getEventTime()) - (stepStart);
						if ((java.lang.Math.abs(dt)) <= (java.lang.Math.ulp(stepStart))) {
							interpolator.storeTime(stepStart);
							java.lang.System.arraycopy(y, 0, yTmp, 0, y0.length);
							hNew = 0;
							stepSize = 0;
							loop = false;
						} else {
							hNew = dt;
							interpolator.rescale(hNew);
						}
					} else {
						scaled = predictedScaled;
						nordsieck = nordsieckTmp;
						interpolator.reinitialize(stepEnd, stepSize, scaled, nordsieck);
						loop = false;
					}
				} else {
					final double factor = computeStepGrowShrinkFactor(error);
					hNew = filterStep(((stepSize) * factor), forward, false);
					interpolator.rescale(hNew);
				}
			}
			final double nextStep = (stepStart) + (stepSize);
			java.lang.System.arraycopy(yTmp, 0, y, 0, n);
			interpolator.storeTime(nextStep);
			manager.stepAccepted(nextStep, y);
			lastStep = manager.stop();
			for (org.apache.commons.math.ode.sampling.StepHandler handler : stepHandlers) {
				interpolator.setInterpolatedTime(nextStep);
				handler.handleStep(interpolator, lastStep);
			}
			stepStart = nextStep;
			if ((!lastStep) && (manager.reset(stepStart, y))) {
				start(stepStart, y, t);
				interpolator.reinitialize(stepStart, stepSize, scaled, nordsieck);
			} 
			if (!lastStep) {
				stepSize = filterStep(stepSize, forward, true);
				final double factor = computeStepGrowShrinkFactor(error);
				final double scaledH = (stepSize) * factor;
				final double nextT = (stepStart) + scaledH;
				final boolean nextIsLast = forward ? nextT >= t : nextT <= t;
				hNew = filterStep(scaledH, forward, nextIsLast);
				interpolator.rescale(hNew);
			} 
		}
		final double stopTime = stepStart;
		stepStart = java.lang.Double.NaN;
		stepSize = java.lang.Double.NaN;
		return stopTime;
	}
}

