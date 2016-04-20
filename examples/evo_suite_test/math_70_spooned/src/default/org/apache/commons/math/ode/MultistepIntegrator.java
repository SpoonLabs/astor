package org.apache.commons.math.ode;


public abstract class MultistepIntegrator extends org.apache.commons.math.ode.nonstiff.AdaptiveStepsizeIntegrator {
	protected double[] scaled;

	protected org.apache.commons.math.linear.Array2DRowRealMatrix nordsieck;

	private org.apache.commons.math.ode.FirstOrderIntegrator starter;

	private final int nSteps;

	private double exp;

	private double safety;

	private double minReduction;

	private double maxGrowth;

	protected MultistepIntegrator(final java.lang.String name ,final int nSteps ,final int order ,final double minStep ,final double maxStep ,final double scalAbsoluteTolerance ,final double scalRelativeTolerance) {
		super(name, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
		if (nSteps <= 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0} method needs at least one previous point", name);
		} 
		starter = new org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator(minStep , maxStep , scalAbsoluteTolerance , scalRelativeTolerance);
		this.nSteps = nSteps;
		exp = (-1.0) / order;
		setSafety(0.9);
		setMinReduction(0.2);
		setMaxGrowth(java.lang.Math.pow(2.0, -(exp)));
	}

	protected MultistepIntegrator(final java.lang.String name ,final int nSteps ,final int order ,final double minStep ,final double maxStep ,final double[] vecAbsoluteTolerance ,final double[] vecRelativeTolerance) {
		super(name, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
		starter = new org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator(minStep , maxStep , vecAbsoluteTolerance , vecRelativeTolerance);
		this.nSteps = nSteps;
		exp = (-1.0) / order;
		setSafety(0.9);
		setMinReduction(0.2);
		setMaxGrowth(java.lang.Math.pow(2.0, -(exp)));
	}

	public org.apache.commons.math.ode.ODEIntegrator getStarterIntegrator() {
		return starter;
	}

	public void setStarterIntegrator(org.apache.commons.math.ode.FirstOrderIntegrator starterIntegrator) {
		this.starter = starterIntegrator;
	}

	protected void start(final double t0, final double[] y0, final double t) throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException {
		starter.clearEventHandlers();
		starter.clearStepHandlers();
		starter.addStepHandler(new NordsieckInitializer(y0.length));
		try {
			starter.integrate(new CountingDifferentialEquations(y0.length), t0, y0, t, new double[y0.length]);
		} catch (org.apache.commons.math.ode.DerivativeException de) {
			if (!(de instanceof org.apache.commons.math.ode.MultistepIntegrator.InitializationCompletedMarkerException)) {
				throw de;
			} 
		}
		starter.clearStepHandlers();
	}

	protected abstract org.apache.commons.math.linear.Array2DRowRealMatrix initializeHighOrderDerivatives(final double[] first, final double[][] multistep);

	public double getMinReduction() {
		return minReduction;
	}

	public void setMinReduction(final double minReduction) {
		this.minReduction = minReduction;
	}

	public double getMaxGrowth() {
		return maxGrowth;
	}

	public void setMaxGrowth(final double maxGrowth) {
		this.maxGrowth = maxGrowth;
	}

	public double getSafety() {
		return safety;
	}

	public void setSafety(final double safety) {
		this.safety = safety;
	}

	protected double computeStepGrowShrinkFactor(final double error) {
		return java.lang.Math.min(maxGrowth, java.lang.Math.max(minReduction, ((safety) * (java.lang.Math.pow(error, exp)))));
	}

	public static interface NordsieckTransformer {
		org.apache.commons.math.linear.RealMatrix initializeHighOrderDerivatives(double[] first, double[][] multistep);
	}

	private class NordsieckInitializer implements org.apache.commons.math.ode.sampling.StepHandler {
		private final int n;

		public NordsieckInitializer(final int n) {
			this.n = n;
		}

		public void handleStep(org.apache.commons.math.ode.sampling.StepInterpolator interpolator, boolean isLast) throws org.apache.commons.math.ode.DerivativeException {
			final double prev = interpolator.getPreviousTime();
			final double curr = interpolator.getCurrentTime();
			stepStart = prev;
			stepSize = (curr - prev) / ((nSteps) + 1);
			interpolator.setInterpolatedTime(prev);
			scaled = interpolator.getInterpolatedDerivatives().clone();
			for (int j = 0 ; j < (n) ; ++j) {
				scaled[j] *= stepSize;
			}
			final double[][] multistep = new double[nSteps][];
			for (int i = 1 ; i <= (nSteps) ; ++i) {
				interpolator.setInterpolatedTime((prev + ((stepSize) * i)));
				final double[] msI = interpolator.getInterpolatedDerivatives().clone();
				for (int j = 0 ; j < (n) ; ++j) {
					msI[j] *= stepSize;
				}
				multistep[(i - 1)] = msI;
			}
			nordsieck = initializeHighOrderDerivatives(scaled, multistep);
			throw new org.apache.commons.math.ode.MultistepIntegrator.InitializationCompletedMarkerException();
		}

		public boolean requiresDenseOutput() {
			return true;
		}

		public void reset() {
		}
	}

	private static class InitializationCompletedMarkerException extends org.apache.commons.math.ode.DerivativeException {
		private static final long serialVersionUID = -4105805787353488365L;

		public InitializationCompletedMarkerException() {
			super(((java.lang.Throwable)(null)));
		}
	}

	private class CountingDifferentialEquations implements org.apache.commons.math.ode.FirstOrderDifferentialEquations {
		private final int dimension;

		public CountingDifferentialEquations(final int dimension) {
			this.dimension = dimension;
		}

		public void computeDerivatives(double t, double[] y, double[] dot) throws org.apache.commons.math.ode.DerivativeException {
			org.apache.commons.math.ode.MultistepIntegrator.this.computeDerivatives(t, y, dot);
		}

		public int getDimension() {
			return dimension;
		}
	}
}

