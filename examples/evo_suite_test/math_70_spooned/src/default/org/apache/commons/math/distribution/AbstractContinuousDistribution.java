package org.apache.commons.math.distribution;


public abstract class AbstractContinuousDistribution extends org.apache.commons.math.distribution.AbstractDistribution implements java.io.Serializable , org.apache.commons.math.distribution.ContinuousDistribution {
	private static final long serialVersionUID = -38038050983108802L;

	private double solverAbsoluteAccuracy = org.apache.commons.math.analysis.solvers.BrentSolver.DEFAULT_ABSOLUTE_ACCURACY;

	protected AbstractContinuousDistribution() {
		super();
	}

	public double density(double x) throws org.apache.commons.math.MathRuntimeException {
		throw new org.apache.commons.math.MathRuntimeException(new java.lang.UnsupportedOperationException() , "This distribution does not have a density function implemented");
	}

	public double inverseCumulativeProbability(final double p) throws org.apache.commons.math.MathException {
		if ((p < 0.0) || (p > 1.0)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0} out of [{1}, {2}] range", p, 0.0, 1.0);
		} 
		org.apache.commons.math.analysis.UnivariateRealFunction rootFindingFunction = new org.apache.commons.math.analysis.UnivariateRealFunction() {
			public double value(double x) throws org.apache.commons.math.FunctionEvaluationException {
				double ret = java.lang.Double.NaN;
				try {
					ret = (cumulativeProbability(x)) - p;
				} catch (org.apache.commons.math.MathException ex) {
					throw new org.apache.commons.math.FunctionEvaluationException(ex , x , ex.getPattern() , ex.getArguments());
				}
				if (java.lang.Double.isNaN(ret)) {
					throw new org.apache.commons.math.FunctionEvaluationException(x , "Cumulative probability function returned NaN for argument {0} p = {1}" , x , p);
				} 
				return ret;
			}
		};
		double lowerBound = getDomainLowerBound(p);
		double upperBound = getDomainUpperBound(p);
		double[] bracket = null;
		try {
			bracket = org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.bracket(rootFindingFunction, getInitialDomain(p), lowerBound, upperBound);
		} catch (org.apache.commons.math.ConvergenceException ex) {
			if ((java.lang.Math.abs(rootFindingFunction.value(lowerBound))) < (getSolverAbsoluteAccuracy())) {
				return lowerBound;
			} 
			if ((java.lang.Math.abs(rootFindingFunction.value(upperBound))) < (getSolverAbsoluteAccuracy())) {
				return upperBound;
			} 
			throw new org.apache.commons.math.MathException(ex);
		}
		double root = org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.solve(rootFindingFunction, bracket[0], bracket[1], getSolverAbsoluteAccuracy());
		return root;
	}

	protected abstract double getInitialDomain(double p);

	protected abstract double getDomainLowerBound(double p);

	protected abstract double getDomainUpperBound(double p);

	protected double getSolverAbsoluteAccuracy() {
		return solverAbsoluteAccuracy;
	}
}

