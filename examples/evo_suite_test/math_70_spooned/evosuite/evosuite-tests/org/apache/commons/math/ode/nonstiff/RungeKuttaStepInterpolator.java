package org.apache.commons.math.ode.nonstiff;


abstract class RungeKuttaStepInterpolator extends org.apache.commons.math.ode.sampling.AbstractStepInterpolator {
	protected double[][] yDotK;

	protected org.apache.commons.math.ode.AbstractIntegrator integrator;

	protected RungeKuttaStepInterpolator() {
		super();
		yDotK = null;
		integrator = null;
	}

	public RungeKuttaStepInterpolator(final org.apache.commons.math.ode.nonstiff.RungeKuttaStepInterpolator interpolator) {
		super(interpolator);
		if ((interpolator.currentState) != null) {
			final int dimension = currentState.length;
			yDotK = new double[interpolator.yDotK.length][];
			for (int k = 0 ; k < (interpolator.yDotK.length) ; ++k) {
				yDotK[k] = new double[dimension];
				java.lang.System.arraycopy(interpolator.yDotK[k], 0, yDotK[k], 0, dimension);
			}
		} else {
			yDotK = null;
		}
		integrator = null;
	}

	public void reinitialize(final org.apache.commons.math.ode.AbstractIntegrator rkIntegrator, final double[] y, final double[][] yDotArray, final boolean forward) {
		reinitialize(y, forward);
		this.yDotK = yDotArray;
		this.integrator = rkIntegrator;
	}

	@java.lang.Override
	public void writeExternal(final java.io.ObjectOutput out) throws java.io.IOException {
		writeBaseExternal(out);
		final int n = (currentState) == null ? -1 : currentState.length;
		final int kMax = (yDotK) == null ? -1 : yDotK.length;
		out.writeInt(kMax);
		for (int k = 0 ; k < kMax ; ++k) {
			for (int i = 0 ; i < n ; ++i) {
				out.writeDouble(yDotK[k][i]);
			}
		}
	}

	@java.lang.Override
	public void readExternal(final java.io.ObjectInput in) throws java.io.IOException {
		final double t = readBaseExternal(in);
		final int n = (currentState) == null ? -1 : currentState.length;
		final int kMax = in.readInt();
		yDotK = kMax < 0 ? null : new double[kMax][];
		for (int k = 0 ; k < kMax ; ++k) {
			yDotK[k] = n < 0 ? null : new double[n];
			for (int i = 0 ; i < n ; ++i) {
				yDotK[k][i] = in.readDouble();
			}
		}
		integrator = null;
		if ((currentState) != null) {
			setInterpolatedTime(t);
		} else {
			interpolatedTime = t;
		}
	}
}

