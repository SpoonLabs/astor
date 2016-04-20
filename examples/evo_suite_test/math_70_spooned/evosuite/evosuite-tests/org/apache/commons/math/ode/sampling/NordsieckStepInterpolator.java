package org.apache.commons.math.ode.sampling;


public class NordsieckStepInterpolator extends org.apache.commons.math.ode.sampling.AbstractStepInterpolator {
	private static final long serialVersionUID = -7179861704951334960L;

	protected double[] stateVariation;

	private double scalingH;

	private double referenceTime;

	private double[] scaled;

	private org.apache.commons.math.linear.Array2DRowRealMatrix nordsieck;

	public NordsieckStepInterpolator() {
	}

	public NordsieckStepInterpolator(final org.apache.commons.math.ode.sampling.NordsieckStepInterpolator interpolator) {
		super(interpolator);
		scalingH = interpolator.scalingH;
		referenceTime = interpolator.referenceTime;
		if ((interpolator.scaled) != null) {
			scaled = interpolator.scaled.clone();
		} 
		if ((interpolator.nordsieck) != null) {
			nordsieck = new org.apache.commons.math.linear.Array2DRowRealMatrix(interpolator.nordsieck.getDataRef() , true);
		} 
		if ((interpolator.stateVariation) != null) {
			stateVariation = interpolator.stateVariation.clone();
		} 
	}

	@java.lang.Override
	protected org.apache.commons.math.ode.sampling.StepInterpolator doCopy() {
		return new org.apache.commons.math.ode.sampling.NordsieckStepInterpolator(this);
	}

	@java.lang.Override
	public void reinitialize(final double[] y, final boolean forward) {
		super.reinitialize(y, forward);
		stateVariation = new double[y.length];
	}

	public void reinitialize(final double time, final double stepSize, final double[] scaledDerivative, final org.apache.commons.math.linear.Array2DRowRealMatrix nordsieckVector) {
		this.referenceTime = time;
		this.scalingH = stepSize;
		this.scaled = scaledDerivative;
		this.nordsieck = nordsieckVector;
		setInterpolatedTime(getInterpolatedTime());
	}

	public void rescale(final double stepSize) {
		final double ratio = stepSize / (scalingH);
		for (int i = 0 ; i < (scaled.length) ; ++i) {
			scaled[i] *= ratio;
		}
		final double[][] nData = nordsieck.getDataRef();
		double power = ratio;
		for (int i = 0 ; i < (nData.length) ; ++i) {
			power *= ratio;
			final double[] nDataI = nData[i];
			for (int j = 0 ; j < (nDataI.length) ; ++j) {
				nDataI[j] *= power;
			}
		}
		scalingH = stepSize;
	}

	public double[] getInterpolatedStateVariation() throws org.apache.commons.math.ode.DerivativeException {
		getInterpolatedState();
		return stateVariation;
	}

	@java.lang.Override
	protected void computeInterpolatedStateAndDerivatives(final double theta, final double oneMinusThetaH) {
		final double x = (interpolatedTime) - (referenceTime);
		final double normalizedAbscissa = x / (scalingH);
		java.util.Arrays.fill(stateVariation, 0.0);
		java.util.Arrays.fill(interpolatedDerivatives, 0.0);
		final double[][] nData = nordsieck.getDataRef();
		for (int i = (nData.length) - 1 ; i >= 0 ; --i) {
			final int order = i + 2;
			final double[] nDataI = nData[i];
			final double power = java.lang.Math.pow(normalizedAbscissa, order);
			for (int j = 0 ; j < (nDataI.length) ; ++j) {
				final double d = (nDataI[j]) * power;
				stateVariation[j] += d;
				interpolatedDerivatives[j] += order * d;
			}
		}
		for (int j = 0 ; j < (currentState.length) ; ++j) {
			stateVariation[j] += (scaled[j]) * normalizedAbscissa;
			interpolatedState[j] = (currentState[j]) + (stateVariation[j]);
			interpolatedDerivatives[j] = ((interpolatedDerivatives[j]) + ((scaled[j]) * normalizedAbscissa)) / x;
		}
	}

	@java.lang.Override
	public void writeExternal(final java.io.ObjectOutput out) throws java.io.IOException {
		writeBaseExternal(out);
		out.writeDouble(scalingH);
		out.writeDouble(referenceTime);
		final int n = (currentState) == null ? -1 : currentState.length;
		if ((scaled) == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			for (int j = 0 ; j < n ; ++j) {
				out.writeDouble(scaled[j]);
			}
		}
		if ((nordsieck) == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeObject(nordsieck);
		}
	}

	@java.lang.Override
	public void readExternal(final java.io.ObjectInput in) throws java.io.IOException, java.lang.ClassNotFoundException {
		final double t = readBaseExternal(in);
		scalingH = in.readDouble();
		referenceTime = in.readDouble();
		final int n = (currentState) == null ? -1 : currentState.length;
		final boolean hasScaled = in.readBoolean();
		if (hasScaled) {
			scaled = new double[n];
			for (int j = 0 ; j < n ; ++j) {
				scaled[j] = in.readDouble();
			}
		} else {
			scaled = null;
		}
		final boolean hasNordsieck = in.readBoolean();
		if (hasNordsieck) {
			nordsieck = ((org.apache.commons.math.linear.Array2DRowRealMatrix)(in.readObject()));
		} else {
			nordsieck = null;
		}
		if (hasScaled && hasNordsieck) {
			stateVariation = new double[n];
			setInterpolatedTime(t);
		} else {
			stateVariation = null;
		}
	}
}

