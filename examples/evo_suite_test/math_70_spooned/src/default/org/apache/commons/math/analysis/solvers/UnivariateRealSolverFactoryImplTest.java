package org.apache.commons.math.analysis.solvers;


public class UnivariateRealSolverFactoryImplTest extends junit.framework.TestCase {
	private org.apache.commons.math.analysis.solvers.UnivariateRealSolverFactory factory;

	@java.lang.Override
	protected void setUp() throws java.lang.Exception {
		super.setUp();
		factory = new org.apache.commons.math.analysis.solvers.UnivariateRealSolverFactoryImpl();
	}

	@java.lang.Override
	protected void tearDown() throws java.lang.Exception {
		factory = null;
		super.tearDown();
	}

	public void testNewBisectionSolverValid() {
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = factory.newBisectionSolver();
		junit.framework.Assert.assertNotNull(solver);
		junit.framework.Assert.assertTrue((solver instanceof org.apache.commons.math.analysis.solvers.BisectionSolver));
	}

	public void testNewNewtonSolverValid() {
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = factory.newNewtonSolver();
		junit.framework.Assert.assertNotNull(solver);
		junit.framework.Assert.assertTrue((solver instanceof org.apache.commons.math.analysis.solvers.NewtonSolver));
	}

	public void testNewBrentSolverValid() {
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = factory.newBrentSolver();
		junit.framework.Assert.assertNotNull(solver);
		junit.framework.Assert.assertTrue((solver instanceof org.apache.commons.math.analysis.solvers.BrentSolver));
	}

	public void testNewSecantSolverValid() {
		org.apache.commons.math.analysis.solvers.UnivariateRealSolver solver = factory.newSecantSolver();
		junit.framework.Assert.assertNotNull(solver);
		junit.framework.Assert.assertTrue((solver instanceof org.apache.commons.math.analysis.solvers.SecantSolver));
	}
}

