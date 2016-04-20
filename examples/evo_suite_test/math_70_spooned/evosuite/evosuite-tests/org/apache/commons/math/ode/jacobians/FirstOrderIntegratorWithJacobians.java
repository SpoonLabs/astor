package org.apache.commons.math.ode.jacobians;


public class FirstOrderIntegratorWithJacobians {
	private final org.apache.commons.math.ode.FirstOrderIntegrator integrator;

	private final org.apache.commons.math.ode.jacobians.ODEWithJacobians ode;

	private int maxEvaluations;

	private int evaluations;

	public FirstOrderIntegratorWithJacobians(final org.apache.commons.math.ode.FirstOrderIntegrator integrator ,final org.apache.commons.math.ode.jacobians.ParameterizedODE ode ,final double[] p ,final double[] hY ,final double[] hP) {
		checkDimension(ode.getDimension(), hY);
		checkDimension(ode.getParametersDimension(), p);
		checkDimension(ode.getParametersDimension(), hP);
		this.integrator = integrator;
		this.ode = new FiniteDifferencesWrapper(ode , p , hY , hP);
		setMaxEvaluations(-1);
	}

	public FirstOrderIntegratorWithJacobians(final org.apache.commons.math.ode.FirstOrderIntegrator integrator ,final org.apache.commons.math.ode.jacobians.ODEWithJacobians ode) {
		this.integrator = integrator;
		this.ode = ode;
		setMaxEvaluations(-1);
	}

	public void addStepHandler(org.apache.commons.math.ode.jacobians.StepHandlerWithJacobians handler) {
		final int n = ode.getDimension();
		final int k = ode.getParametersDimension();
		integrator.addStepHandler(new org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepHandlerWrapper(handler , n , k));
	}

	public java.util.Collection<org.apache.commons.math.ode.jacobians.StepHandlerWithJacobians> getStepHandlers() {
		final java.util.Collection<org.apache.commons.math.ode.jacobians.StepHandlerWithJacobians> handlers = new java.util.ArrayList<org.apache.commons.math.ode.jacobians.StepHandlerWithJacobians>();
		for (final org.apache.commons.math.ode.sampling.StepHandler handler : integrator.getStepHandlers()) {
			if (handler instanceof org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepHandlerWrapper) {
				handlers.add(((org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepHandlerWrapper)(handler)).getHandler());
			} 
		}
		return handlers;
	}

	public void clearStepHandlers() {
		integrator.clearStepHandlers();
	}

	public void addEventHandler(org.apache.commons.math.ode.jacobians.EventHandlerWithJacobians handler, double maxCheckInterval, double convergence, int maxIterationCount) {
		final int n = ode.getDimension();
		final int k = ode.getParametersDimension();
		integrator.addEventHandler(new org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.EventHandlerWrapper(handler , n , k), maxCheckInterval, convergence, maxIterationCount);
	}

	public java.util.Collection<org.apache.commons.math.ode.jacobians.EventHandlerWithJacobians> getEventHandlers() {
		final java.util.Collection<org.apache.commons.math.ode.jacobians.EventHandlerWithJacobians> handlers = new java.util.ArrayList<org.apache.commons.math.ode.jacobians.EventHandlerWithJacobians>();
		for (final org.apache.commons.math.ode.events.EventHandler handler : integrator.getEventHandlers()) {
			if (handler instanceof org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.EventHandlerWrapper) {
				handlers.add(((org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.EventHandlerWrapper)(handler)).getHandler());
			} 
		}
		return handlers;
	}

	public void clearEventHandlers() {
		integrator.clearEventHandlers();
	}

	public double integrate(final double t0, final double[] y0, final double[][] dY0dP, final double t, final double[] y, final double[][] dYdY0, final double[][] dYdP) throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		final int n = ode.getDimension();
		final int k = ode.getParametersDimension();
		checkDimension(n, y0);
		checkDimension(n, y);
		checkDimension(n, dYdY0);
		checkDimension(n, dYdY0[0]);
		if (k != 0) {
			checkDimension(n, dY0dP);
			checkDimension(k, dY0dP[0]);
			checkDimension(n, dYdP);
			checkDimension(k, dYdP[0]);
		} 
		final double[] z = new double[n * ((1 + n) + k)];
		java.lang.System.arraycopy(y0, 0, z, 0, n);
		for (int i = 0 ; i < n ; ++i) {
			z[((i * (1 + n)) + n)] = 1.0;
			java.lang.System.arraycopy(dY0dP[i], 0, z, ((n * (n + 1)) + (i * k)), k);
		}
		evaluations = 0;
		final double stopTime = integrator.integrate(new MappingWrapper(), t0, z, t, z);
		org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.dispatchCompoundState(z, y, dYdY0, dYdP);
		return stopTime;
	}

	private static void dispatchCompoundState(final double[] z, final double[] y, final double[][] dydy0, final double[][] dydp) {
		final int n = y.length;
		final int k = dydp[0].length;
		java.lang.System.arraycopy(z, 0, y, 0, n);
		for (int i = 0 ; i < n ; ++i) {
			java.lang.System.arraycopy(z, (n * (i + 1)), dydy0[i], 0, n);
		}
		for (int i = 0 ; i < n ; ++i) {
			java.lang.System.arraycopy(z, ((n * (n + 1)) + (i * k)), dydp[i], 0, k);
		}
	}

	public double getCurrentStepStart() {
		return integrator.getCurrentStepStart();
	}

	public double getCurrentSignedStepsize() {
		return integrator.getCurrentSignedStepsize();
	}

	public void setMaxEvaluations(int maxEvaluations) {
		this.maxEvaluations = maxEvaluations < 0 ? java.lang.Integer.MAX_VALUE : maxEvaluations;
	}

	public int getMaxEvaluations() {
		return maxEvaluations;
	}

	public int getEvaluations() {
		return evaluations;
	}

	private void checkDimension(final int expected, final java.lang.Object array) throws java.lang.IllegalArgumentException {
		int arrayDimension = array == null ? 0 : java.lang.reflect.Array.getLength(array);
		if (arrayDimension != expected) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("dimension mismatch {0} != {1}", arrayDimension, expected);
		} 
	}

	private class MappingWrapper implements org.apache.commons.math.ode.FirstOrderDifferentialEquations {
		private final double[] y;

		private final double[] yDot;

		private final double[][] dFdY;

		private final double[][] dFdP;

		public MappingWrapper() {
			final int n = ode.getDimension();
			final int k = ode.getParametersDimension();
			y = new double[n];
			yDot = new double[n];
			dFdY = new double[n][n];
			dFdP = new double[n][k];
		}

		public int getDimension() {
			final int n = y.length;
			final int k = dFdP[0].length;
			return n * ((1 + n) + k);
		}

		public void computeDerivatives(final double t, final double[] z, final double[] zDot) throws org.apache.commons.math.ode.DerivativeException {
			final int n = y.length;
			final int k = dFdP[0].length;
			java.lang.System.arraycopy(z, 0, y, 0, n);
			if ((++(evaluations)) > (maxEvaluations)) {
				throw new org.apache.commons.math.ode.DerivativeException(new org.apache.commons.math.MaxEvaluationsExceededException(maxEvaluations));
			} 
			ode.computeDerivatives(t, y, yDot);
			ode.computeJacobians(t, y, yDot, dFdY, dFdP);
			java.lang.System.arraycopy(yDot, 0, zDot, 0, n);
			for (int i = 0 ; i < n ; ++i) {
				final double[] dFdYi = dFdY[i];
				for (int j = 0 ; j < n ; ++j) {
					double s = 0;
					final int startIndex = n + j;
					int zIndex = startIndex;
					for (int l = 0 ; l < n ; ++l) {
						s += (dFdYi[l]) * (z[zIndex]);
						zIndex += n;
					}
					zDot[(startIndex + (i * n))] = s;
				}
			}
			for (int i = 0 ; i < n ; ++i) {
				final double[] dFdYi = dFdY[i];
				final double[] dFdPi = dFdP[i];
				for (int j = 0 ; j < k ; ++j) {
					double s = dFdPi[j];
					final int startIndex = (n * (n + 1)) + j;
					int zIndex = startIndex;
					for (int l = 0 ; l < n ; ++l) {
						s += (dFdYi[l]) * (z[zIndex]);
						zIndex += k;
					}
					zDot[(startIndex + (i * k))] = s;
				}
			}
		}
	}

	private class FiniteDifferencesWrapper implements org.apache.commons.math.ode.jacobians.ODEWithJacobians {
		private final org.apache.commons.math.ode.jacobians.ParameterizedODE ode;

		private final double[] p;

		private final double[] hY;

		private final double[] hP;

		private final double[] tmpDot;

		public FiniteDifferencesWrapper(final org.apache.commons.math.ode.jacobians.ParameterizedODE ode ,final double[] p ,final double[] hY ,final double[] hP) {
			this.ode = ode;
			this.p = p.clone();
			this.hY = hY.clone();
			this.hP = hP.clone();
			tmpDot = new double[ode.getDimension()];
		}

		public int getDimension() {
			return ode.getDimension();
		}

		public void computeDerivatives(double t, double[] y, double[] yDot) throws org.apache.commons.math.ode.DerivativeException {
			ode.computeDerivatives(t, y, yDot);
		}

		public int getParametersDimension() {
			return ode.getParametersDimension();
		}

		public void computeJacobians(double t, double[] y, double[] yDot, double[][] dFdY, double[][] dFdP) throws org.apache.commons.math.ode.DerivativeException {
			final int n = hY.length;
			final int k = hP.length;
			evaluations += n + k;
			if ((evaluations) > (maxEvaluations)) {
				throw new org.apache.commons.math.ode.DerivativeException(new org.apache.commons.math.MaxEvaluationsExceededException(maxEvaluations));
			} 
			for (int j = 0 ; j < n ; ++j) {
				final double savedYj = y[j];
				y[j] += hY[j];
				ode.computeDerivatives(t, y, tmpDot);
				for (int i = 0 ; i < n ; ++i) {
					dFdY[i][j] = ((tmpDot[i]) - (yDot[i])) / (hY[j]);
				}
				y[j] = savedYj;
			}
			for (int j = 0 ; j < k ; ++j) {
				ode.setParameter(j, ((p[j]) + (hP[j])));
				ode.computeDerivatives(t, y, tmpDot);
				for (int i = 0 ; i < n ; ++i) {
					dFdP[i][j] = ((tmpDot[i]) - (yDot[i])) / (hP[j]);
				}
				ode.setParameter(j, p[j]);
			}
		}
	}

	private static class StepHandlerWrapper implements org.apache.commons.math.ode.sampling.StepHandler {
		private final org.apache.commons.math.ode.jacobians.StepHandlerWithJacobians handler;

		private final int n;

		private final int k;

		public StepHandlerWrapper(final org.apache.commons.math.ode.jacobians.StepHandlerWithJacobians handler ,final int n ,final int k) {
			this.handler = handler;
			this.n = n;
			this.k = k;
		}

		public org.apache.commons.math.ode.jacobians.StepHandlerWithJacobians getHandler() {
			return handler;
		}

		public void handleStep(org.apache.commons.math.ode.sampling.StepInterpolator interpolator, boolean isLast) throws org.apache.commons.math.ode.DerivativeException {
			handler.handleStep(new org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper(interpolator , n , k), isLast);
		}

		public boolean requiresDenseOutput() {
			return handler.requiresDenseOutput();
		}

		public void reset() {
			handler.reset();
		}
	}

	private static class StepInterpolatorWrapper implements org.apache.commons.math.ode.jacobians.StepInterpolatorWithJacobians {
		private org.apache.commons.math.ode.sampling.StepInterpolator interpolator;

		private double[] y;

		private double[][] dydy0;

		private double[][] dydp;

		private double[] yDot;

		private double[][] dydy0Dot;

		private double[][] dydpDot;

		@java.lang.SuppressWarnings(value = "unused")
		public StepInterpolatorWrapper() {
		}

		public StepInterpolatorWrapper(final org.apache.commons.math.ode.sampling.StepInterpolator interpolator ,final int n ,final int k) {
			this.interpolator = interpolator;
			y = new double[n];
			dydy0 = new double[n][n];
			dydp = new double[n][k];
			yDot = new double[n];
			dydy0Dot = new double[n][n];
			dydpDot = new double[n][k];
		}

		public void setInterpolatedTime(double time) {
			interpolator.setInterpolatedTime(time);
		}

		public boolean isForward() {
			return interpolator.isForward();
		}

		public double getPreviousTime() {
			return interpolator.getPreviousTime();
		}

		public double getInterpolatedTime() {
			return interpolator.getInterpolatedTime();
		}

		public double[] getInterpolatedY() throws org.apache.commons.math.ode.DerivativeException {
			double[] extendedState = interpolator.getInterpolatedState();
			java.lang.System.arraycopy(extendedState, 0, y, 0, y.length);
			return y;
		}

		public double[][] getInterpolatedDyDy0() throws org.apache.commons.math.ode.DerivativeException {
			double[] extendedState = interpolator.getInterpolatedState();
			final int n = y.length;
			int start = n;
			for (int i = 0 ; i < n ; ++i) {
				java.lang.System.arraycopy(extendedState, start, dydy0[i], 0, n);
				start += n;
			}
			return dydy0;
		}

		public double[][] getInterpolatedDyDp() throws org.apache.commons.math.ode.DerivativeException {
			double[] extendedState = interpolator.getInterpolatedState();
			final int n = y.length;
			final int k = dydp[0].length;
			int start = n * (n + 1);
			for (int i = 0 ; i < n ; ++i) {
				java.lang.System.arraycopy(extendedState, start, dydp[i], 0, k);
				start += k;
			}
			return dydp;
		}

		public double[] getInterpolatedYDot() throws org.apache.commons.math.ode.DerivativeException {
			double[] extendedDerivatives = interpolator.getInterpolatedDerivatives();
			java.lang.System.arraycopy(extendedDerivatives, 0, yDot, 0, yDot.length);
			return yDot;
		}

		public double[][] getInterpolatedDyDy0Dot() throws org.apache.commons.math.ode.DerivativeException {
			double[] extendedDerivatives = interpolator.getInterpolatedDerivatives();
			final int n = y.length;
			int start = n;
			for (int i = 0 ; i < n ; ++i) {
				java.lang.System.arraycopy(extendedDerivatives, start, dydy0Dot[i], 0, n);
				start += n;
			}
			return dydy0Dot;
		}

		public double[][] getInterpolatedDyDpDot() throws org.apache.commons.math.ode.DerivativeException {
			double[] extendedDerivatives = interpolator.getInterpolatedDerivatives();
			final int n = y.length;
			final int k = dydpDot[0].length;
			int start = n * (n + 1);
			for (int i = 0 ; i < n ; ++i) {
				java.lang.System.arraycopy(extendedDerivatives, start, dydpDot[i], 0, k);
				start += k;
			}
			return dydpDot;
		}

		public double getCurrentTime() {
			return interpolator.getCurrentTime();
		}

		public org.apache.commons.math.ode.jacobians.StepInterpolatorWithJacobians copy() throws org.apache.commons.math.ode.DerivativeException {
			final int n = y.length;
			final int k = dydp[0].length;
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper copied = new org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper(interpolator.copy() , n , k);
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper.copyArray(y, copied.y);
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper.copyArray(dydy0, copied.dydy0);
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper.copyArray(dydp, copied.dydp);
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper.copyArray(yDot, copied.yDot);
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper.copyArray(dydy0Dot, copied.dydy0Dot);
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper.copyArray(dydpDot, copied.dydpDot);
			return copied;
		}

		public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {
			out.writeObject(interpolator);
			out.writeInt(y.length);
			out.writeInt(dydp[0].length);
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper.writeArray(out, y);
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper.writeArray(out, dydy0);
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper.writeArray(out, dydp);
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper.writeArray(out, yDot);
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper.writeArray(out, dydy0Dot);
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper.writeArray(out, dydpDot);
		}

		public void readExternal(java.io.ObjectInput in) throws java.io.IOException, java.lang.ClassNotFoundException {
			interpolator = ((org.apache.commons.math.ode.sampling.StepInterpolator)(in.readObject()));
			final int n = in.readInt();
			final int k = in.readInt();
			y = new double[n];
			dydy0 = new double[n][n];
			dydp = new double[n][k];
			yDot = new double[n];
			dydy0Dot = new double[n][n];
			dydpDot = new double[n][k];
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper.readArray(in, y);
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper.readArray(in, dydy0);
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper.readArray(in, dydp);
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper.readArray(in, yDot);
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper.readArray(in, dydy0Dot);
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper.readArray(in, dydpDot);
		}

		private static void copyArray(final double[] src, final double[] dest) {
			java.lang.System.arraycopy(src, 0, dest, 0, src.length);
		}

		private static void copyArray(final double[][] src, final double[][] dest) {
			for (int i = 0 ; i < (src.length) ; ++i) {
				org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper.copyArray(src[i], dest[i]);
			}
		}

		private static void writeArray(final java.io.ObjectOutput out, final double[] array) throws java.io.IOException {
			for (int i = 0 ; i < (array.length) ; ++i) {
				out.writeDouble(array[i]);
			}
		}

		private static void writeArray(final java.io.ObjectOutput out, final double[][] array) throws java.io.IOException {
			for (int i = 0 ; i < (array.length) ; ++i) {
				org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper.writeArray(out, array[i]);
			}
		}

		private static void readArray(final java.io.ObjectInput in, final double[] array) throws java.io.IOException {
			for (int i = 0 ; i < (array.length) ; ++i) {
				array[i] = in.readDouble();
			}
		}

		private static void readArray(final java.io.ObjectInput in, final double[][] array) throws java.io.IOException {
			for (int i = 0 ; i < (array.length) ; ++i) {
				org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper.readArray(in, array[i]);
			}
		}
	}

	private static class EventHandlerWrapper implements org.apache.commons.math.ode.events.EventHandler {
		private final org.apache.commons.math.ode.jacobians.EventHandlerWithJacobians handler;

		private double[] y;

		private double[][] dydy0;

		private double[][] dydp;

		public EventHandlerWrapper(final org.apache.commons.math.ode.jacobians.EventHandlerWithJacobians handler ,final int n ,final int k) {
			this.handler = handler;
			y = new double[n];
			dydy0 = new double[n][n];
			dydp = new double[n][k];
		}

		public org.apache.commons.math.ode.jacobians.EventHandlerWithJacobians getHandler() {
			return handler;
		}

		public int eventOccurred(double t, double[] z, boolean increasing) throws org.apache.commons.math.ode.events.EventException {
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.dispatchCompoundState(z, y, dydy0, dydp);
			return handler.eventOccurred(t, y, dydy0, dydp, increasing);
		}

		public double g(double t, double[] z) throws org.apache.commons.math.ode.events.EventException {
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.dispatchCompoundState(z, y, dydy0, dydp);
			return handler.g(t, y, dydy0, dydp);
		}

		public void resetState(double t, double[] z) throws org.apache.commons.math.ode.events.EventException {
			org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians.dispatchCompoundState(z, y, dydy0, dydp);
			handler.resetState(t, y, dydy0, dydp);
		}
	}
}

