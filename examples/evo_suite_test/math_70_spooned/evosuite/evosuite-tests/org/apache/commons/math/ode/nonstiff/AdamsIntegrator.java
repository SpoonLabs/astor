package org.apache.commons.math.ode.nonstiff;


public abstract class AdamsIntegrator extends org.apache.commons.math.ode.MultistepIntegrator {
	private final org.apache.commons.math.ode.nonstiff.AdamsNordsieckTransformer transformer;

	public AdamsIntegrator(final java.lang.String name ,final int nSteps ,final int order ,final double minStep ,final double maxStep ,final double scalAbsoluteTolerance ,final double scalRelativeTolerance) throws java.lang.IllegalArgumentException {
		super(name, nSteps, order, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
		transformer = org.apache.commons.math.ode.nonstiff.AdamsNordsieckTransformer.getInstance(nSteps);
	}

	public AdamsIntegrator(final java.lang.String name ,final int nSteps ,final int order ,final double minStep ,final double maxStep ,final double[] vecAbsoluteTolerance ,final double[] vecRelativeTolerance) throws java.lang.IllegalArgumentException {
		super(name, nSteps, order, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
		transformer = org.apache.commons.math.ode.nonstiff.AdamsNordsieckTransformer.getInstance(nSteps);
	}

	@java.lang.Override
	public abstract double integrate(final org.apache.commons.math.ode.FirstOrderDifferentialEquations equations, final double t0, final double[] y0, final double t, final double[] y) throws org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.IntegratorException;

	@java.lang.Override
	protected org.apache.commons.math.linear.Array2DRowRealMatrix initializeHighOrderDerivatives(final double[] first, final double[][] multistep) {
		return transformer.initializeHighOrderDerivatives(first, multistep);
	}

	public org.apache.commons.math.linear.Array2DRowRealMatrix updateHighOrderDerivativesPhase1(final org.apache.commons.math.linear.Array2DRowRealMatrix highOrder) {
		return transformer.updateHighOrderDerivativesPhase1(highOrder);
	}

	public void updateHighOrderDerivativesPhase2(final double[] start, final double[] end, final org.apache.commons.math.linear.Array2DRowRealMatrix highOrder) {
		transformer.updateHighOrderDerivativesPhase2(start, end, highOrder);
	}
}

