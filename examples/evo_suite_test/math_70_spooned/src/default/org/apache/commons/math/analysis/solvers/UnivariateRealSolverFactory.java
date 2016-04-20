package org.apache.commons.math.analysis.solvers;


public abstract class UnivariateRealSolverFactory {
	protected UnivariateRealSolverFactory() {
	}

	public static org.apache.commons.math.analysis.solvers.UnivariateRealSolverFactory newInstance() {
		return new org.apache.commons.math.analysis.solvers.UnivariateRealSolverFactoryImpl();
	}

	public abstract org.apache.commons.math.analysis.solvers.UnivariateRealSolver newDefaultSolver();

	public abstract org.apache.commons.math.analysis.solvers.UnivariateRealSolver newBisectionSolver();

	public abstract org.apache.commons.math.analysis.solvers.UnivariateRealSolver newBrentSolver();

	public abstract org.apache.commons.math.analysis.solvers.UnivariateRealSolver newNewtonSolver();

	public abstract org.apache.commons.math.analysis.solvers.UnivariateRealSolver newSecantSolver();
}

