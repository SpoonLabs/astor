package org.apache.commons.math.optimization.general;


public class NonLinearConjugateGradientOptimizer extends org.apache.commons.math.optimization.general.AbstractScalarDifferentiableOptimizer implements org.apache.commons.math.optimization.DifferentiableMultivariateRealOptimizer {
	private final org.apache.commons.math.optimization.general.ConjugateGradientFormula updateFormula;

	private org.apache.commons.math.optimization.general.Preconditioner preconditioner;

	private org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver;

	private double initialStep;

	public NonLinearConjugateGradientOptimizer(final org.apache.commons.math.optimization.general.ConjugateGradientFormula updateFormula) {
		this.updateFormula = updateFormula;
		preconditioner = null;
		solver = null;
		initialStep = 1.0;
	}

	public void setPreconditioner(final org.apache.commons.math.optimization.general.Preconditioner preconditioner) {
		this.preconditioner = preconditioner;
	}

	public void setLineSearchSolver(final org.apache.commons.math.analysis.solvers.UnivariateRealSolver lineSearchSolver) {
		this.solver = lineSearchSolver;
	}

	public void setInitialStep(final double initialStep) {
		if (initialStep <= 0) {
			this.initialStep = 1.0;
		} else {
			this.initialStep = initialStep;
		}
	}

	@java.lang.Override
	protected org.apache.commons.math.optimization.RealPointValuePair doOptimize() throws java.lang.IllegalArgumentException, org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		try {
			if ((preconditioner) == null) {
				preconditioner = new org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer.IdentityPreconditioner();
			} 
			if ((solver) == null) {
				solver = new org.apache.commons.math.analysis.solvers.BrentSolver();
			} 
			final int n = point.length;
			double[] r = computeObjectiveGradient(point);
			if ((goal) == (org.apache.commons.math.optimization.GoalType.MINIMIZE)) {
				for (int i = 0 ; i < n ; ++i) {
					r[i] = -(r[i]);
				}
			} 
			double[] steepestDescent = preconditioner.precondition(point, r);
			double[] searchDirection = steepestDescent.clone();
			double delta = 0;
			for (int i = 0 ; i < n ; ++i) {
				delta += (r[i]) * (searchDirection[i]);
			}
			org.apache.commons.math.optimization.RealPointValuePair current = null;
			while (true) {
				final double objective = computeObjectiveValue(point);
				org.apache.commons.math.optimization.RealPointValuePair previous = current;
				current = new org.apache.commons.math.optimization.RealPointValuePair(point , objective);
				if (previous != null) {
					if (checker.converged(getIterations(), previous, current)) {
						return current;
					} 
				} 
				incrementIterationsCounter();
				double dTd = 0;
				for (final double di : searchDirection) {
					dTd += di * di;
				}
				final org.apache.commons.math.analysis.UnivariateRealFunction lsf = new LineSearchFunction(searchDirection);
				final double step = solver.solve(lsf, 0, findUpperBound(lsf, 0, initialStep));
				for (int i = 0 ; i < (point.length) ; ++i) {
					point[i] += step * (searchDirection[i]);
				}
				r = computeObjectiveGradient(point);
				if ((goal) == (org.apache.commons.math.optimization.GoalType.MINIMIZE)) {
					for (int i = 0 ; i < n ; ++i) {
						r[i] = -(r[i]);
					}
				} 
				final double deltaOld = delta;
				final double[] newSteepestDescent = preconditioner.precondition(point, r);
				delta = 0;
				for (int i = 0 ; i < n ; ++i) {
					delta += (r[i]) * (newSteepestDescent[i]);
				}
				final double beta;
				if ((updateFormula) == (org.apache.commons.math.optimization.general.ConjugateGradientFormula.FLETCHER_REEVES)) {
					beta = delta / deltaOld;
				} else {
					double deltaMid = 0;
					for (int i = 0 ; i < (r.length) ; ++i) {
						deltaMid += (r[i]) * (steepestDescent[i]);
					}
					beta = (delta - deltaMid) / deltaOld;
				}
				steepestDescent = newSteepestDescent;
				if ((((getIterations()) % n) == 0) || (beta < 0)) {
					searchDirection = steepestDescent.clone();
				} else {
					for (int i = 0 ; i < n ; ++i) {
						searchDirection[i] = (steepestDescent[i]) + (beta * (searchDirection[i]));
					}
				}
			}
		} catch (org.apache.commons.math.ConvergenceException ce) {
			throw new org.apache.commons.math.optimization.OptimizationException(ce);
		}
	}

	private double findUpperBound(final org.apache.commons.math.analysis.UnivariateRealFunction f, final double a, final double h) throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.optimization.OptimizationException {
		final double yA = f.value(a);
		double yB = yA;
		for (double step = h ; step < (java.lang.Double.MAX_VALUE) ; step *= java.lang.Math.max(2, (yA / yB))) {
			final double b = a + step;
			yB = f.value(b);
			if ((yA * yB) <= 0) {
				return b;
			} 
		}
		throw new org.apache.commons.math.optimization.OptimizationException("unable to bracket optimum in line search");
	}

	private static class IdentityPreconditioner implements org.apache.commons.math.optimization.general.Preconditioner {
		public double[] precondition(double[] variables, double[] r) {
			return r.clone();
		}
	}

	private class LineSearchFunction implements org.apache.commons.math.analysis.UnivariateRealFunction {
		private final double[] searchDirection;

		public LineSearchFunction(final double[] searchDirection) {
			this.searchDirection = searchDirection;
		}

		public double value(double x) throws org.apache.commons.math.FunctionEvaluationException {
			final double[] shiftedPoint = point.clone();
			for (int i = 0 ; i < (shiftedPoint.length) ; ++i) {
				shiftedPoint[i] += x * (searchDirection[i]);
			}
			final double[] gradient = computeObjectiveGradient(shiftedPoint);
			double dotProduct = 0;
			for (int i = 0 ; i < (gradient.length) ; ++i) {
				dotProduct += (gradient[i]) * (searchDirection[i]);
			}
			return dotProduct;
		}
	}
}

