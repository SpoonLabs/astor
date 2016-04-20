package org.apache.commons.math.optimization.linear;


public class SimplexSolverTest {
	@org.junit.Test
	public void testMath272() throws org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.linear.LinearObjectiveFunction f = new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(new double[]{ 2 , 2 , 1 } , 0);
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = new java.util.ArrayList<org.apache.commons.math.optimization.linear.LinearConstraint>();
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 , 1 , 0 } , org.apache.commons.math.optimization.linear.Relationship.GEQ , 1));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 , 0 , 1 } , org.apache.commons.math.optimization.linear.Relationship.GEQ , 1));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 0 , 1 , 0 } , org.apache.commons.math.optimization.linear.Relationship.GEQ , 1));
		org.apache.commons.math.optimization.linear.SimplexSolver solver = new org.apache.commons.math.optimization.linear.SimplexSolver();
		org.apache.commons.math.optimization.RealPointValuePair solution = solver.optimize(f, constraints, org.apache.commons.math.optimization.GoalType.MINIMIZE, true);
		org.junit.Assert.assertEquals(0.0, solution.getPoint()[0], 1.0E-7);
		org.junit.Assert.assertEquals(1.0, solution.getPoint()[1], 1.0E-7);
		org.junit.Assert.assertEquals(1.0, solution.getPoint()[2], 1.0E-7);
		org.junit.Assert.assertEquals(3.0, solution.getValue(), 1.0E-7);
	}

	@org.junit.Test
	public void testMath286() throws org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.linear.LinearObjectiveFunction f = new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(new double[]{ 0.8 , 0.2 , 0.7 , 0.3 , 0.6 , 0.4 } , 0);
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = new java.util.ArrayList<org.apache.commons.math.optimization.linear.LinearConstraint>();
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 , 0 , 1 , 0 , 1 , 0 } , org.apache.commons.math.optimization.linear.Relationship.EQ , 23.0));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 0 , 1 , 0 , 1 , 0 , 1 } , org.apache.commons.math.optimization.linear.Relationship.EQ , 23.0));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 , 0 , 0 , 0 , 0 , 0 } , org.apache.commons.math.optimization.linear.Relationship.GEQ , 10.0));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 0 , 0 , 1 , 0 , 0 , 0 } , org.apache.commons.math.optimization.linear.Relationship.GEQ , 8.0));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 0 , 0 , 0 , 0 , 1 , 0 } , org.apache.commons.math.optimization.linear.Relationship.GEQ , 5.0));
		org.apache.commons.math.optimization.linear.SimplexSolver solver = new org.apache.commons.math.optimization.linear.SimplexSolver();
		org.apache.commons.math.optimization.RealPointValuePair solution = solver.optimize(f, constraints, org.apache.commons.math.optimization.GoalType.MAXIMIZE, true);
		org.junit.Assert.assertEquals(25.8, solution.getValue(), 1.0E-7);
		org.junit.Assert.assertEquals(23.0, (((solution.getPoint()[0]) + (solution.getPoint()[2])) + (solution.getPoint()[4])), 1.0E-7);
		org.junit.Assert.assertEquals(23.0, (((solution.getPoint()[1]) + (solution.getPoint()[3])) + (solution.getPoint()[5])), 1.0E-7);
		org.junit.Assert.assertTrue(((solution.getPoint()[0]) >= (10.0 - 1.0E-7)));
		org.junit.Assert.assertTrue(((solution.getPoint()[2]) >= (8.0 - 1.0E-7)));
		org.junit.Assert.assertTrue(((solution.getPoint()[4]) >= (5.0 - 1.0E-7)));
	}

	@org.junit.Test
	public void testDegeneracy() throws org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.linear.LinearObjectiveFunction f = new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(new double[]{ 0.8 , 0.7 } , 0);
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = new java.util.ArrayList<org.apache.commons.math.optimization.linear.LinearConstraint>();
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 , 1 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 18.0));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 , 0 } , org.apache.commons.math.optimization.linear.Relationship.GEQ , 10.0));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 0 , 1 } , org.apache.commons.math.optimization.linear.Relationship.GEQ , 8.0));
		org.apache.commons.math.optimization.linear.SimplexSolver solver = new org.apache.commons.math.optimization.linear.SimplexSolver();
		org.apache.commons.math.optimization.RealPointValuePair solution = solver.optimize(f, constraints, org.apache.commons.math.optimization.GoalType.MAXIMIZE, true);
		org.junit.Assert.assertEquals(13.6, solution.getValue(), 1.0E-7);
	}

	@org.junit.Test
	public void testMath288() throws org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.linear.LinearObjectiveFunction f = new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(new double[]{ 7 , 3 , 0 , 0 } , 0);
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = new java.util.ArrayList<org.apache.commons.math.optimization.linear.LinearConstraint>();
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 3 , 0 , -5 , 0 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 0.0));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 2 , 0 , 0 , -5 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 0.0));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 0 , 3 , 0 , -5 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 0.0));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 , 0 , 0 , 0 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 1.0));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 0 , 1 , 0 , 0 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 1.0));
		org.apache.commons.math.optimization.linear.SimplexSolver solver = new org.apache.commons.math.optimization.linear.SimplexSolver();
		org.apache.commons.math.optimization.RealPointValuePair solution = solver.optimize(f, constraints, org.apache.commons.math.optimization.GoalType.MAXIMIZE, true);
		org.junit.Assert.assertEquals(10.0, solution.getValue(), 1.0E-7);
	}

	@org.junit.Test
	public void testMath290GEQ() throws org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.linear.LinearObjectiveFunction f = new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(new double[]{ 1 , 5 } , 0);
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = new java.util.ArrayList<org.apache.commons.math.optimization.linear.LinearConstraint>();
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 2 , 0 } , org.apache.commons.math.optimization.linear.Relationship.GEQ , -1.0));
		org.apache.commons.math.optimization.linear.SimplexSolver solver = new org.apache.commons.math.optimization.linear.SimplexSolver();
		org.apache.commons.math.optimization.RealPointValuePair solution = solver.optimize(f, constraints, org.apache.commons.math.optimization.GoalType.MINIMIZE, true);
		org.junit.Assert.assertEquals(0, solution.getValue(), 1.0E-7);
		org.junit.Assert.assertEquals(0, solution.getPoint()[0], 1.0E-7);
		org.junit.Assert.assertEquals(0, solution.getPoint()[1], 1.0E-7);
	}

	@org.junit.Test(expected = org.apache.commons.math.optimization.linear.NoFeasibleSolutionException.class)
	public void testMath290LEQ() throws org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.linear.LinearObjectiveFunction f = new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(new double[]{ 1 , 5 } , 0);
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = new java.util.ArrayList<org.apache.commons.math.optimization.linear.LinearConstraint>();
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 2 , 0 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , -1.0));
		org.apache.commons.math.optimization.linear.SimplexSolver solver = new org.apache.commons.math.optimization.linear.SimplexSolver();
		solver.optimize(f, constraints, org.apache.commons.math.optimization.GoalType.MINIMIZE, true);
	}

	@org.junit.Test
	public void testMath293() throws org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.linear.LinearObjectiveFunction f = new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(new double[]{ 0.8 , 0.2 , 0.7 , 0.3 , 0.4 , 0.6 } , 0);
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = new java.util.ArrayList<org.apache.commons.math.optimization.linear.LinearConstraint>();
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 , 0 , 1 , 0 , 1 , 0 } , org.apache.commons.math.optimization.linear.Relationship.EQ , 30.0));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 0 , 1 , 0 , 1 , 0 , 1 } , org.apache.commons.math.optimization.linear.Relationship.EQ , 30.0));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 0.8 , 0.2 , 0.0 , 0.0 , 0.0 , 0.0 } , org.apache.commons.math.optimization.linear.Relationship.GEQ , 10.0));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 0.0 , 0.0 , 0.7 , 0.3 , 0.0 , 0.0 } , org.apache.commons.math.optimization.linear.Relationship.GEQ , 10.0));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 0.0 , 0.0 , 0.0 , 0.0 , 0.4 , 0.6 } , org.apache.commons.math.optimization.linear.Relationship.GEQ , 10.0));
		org.apache.commons.math.optimization.linear.SimplexSolver solver = new org.apache.commons.math.optimization.linear.SimplexSolver();
		org.apache.commons.math.optimization.RealPointValuePair solution1 = solver.optimize(f, constraints, org.apache.commons.math.optimization.GoalType.MAXIMIZE, true);
		org.junit.Assert.assertEquals(15.7143, solution1.getPoint()[0], 1.0E-4);
		org.junit.Assert.assertEquals(0.0, solution1.getPoint()[1], 1.0E-4);
		org.junit.Assert.assertEquals(14.2857, solution1.getPoint()[2], 1.0E-4);
		org.junit.Assert.assertEquals(0.0, solution1.getPoint()[3], 1.0E-4);
		org.junit.Assert.assertEquals(0.0, solution1.getPoint()[4], 1.0E-4);
		org.junit.Assert.assertEquals(30.0, solution1.getPoint()[5], 1.0E-4);
		org.junit.Assert.assertEquals(40.57143, solution1.getValue(), 1.0E-4);
		double valA = (0.8 * (solution1.getPoint()[0])) + (0.2 * (solution1.getPoint()[1]));
		double valB = (0.7 * (solution1.getPoint()[2])) + (0.3 * (solution1.getPoint()[3]));
		double valC = (0.4 * (solution1.getPoint()[4])) + (0.6 * (solution1.getPoint()[5]));
		f = new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(new double[]{ 0.8 , 0.2 , 0.7 , 0.3 , 0.4 , 0.6 } , 0);
		constraints = new java.util.ArrayList<org.apache.commons.math.optimization.linear.LinearConstraint>();
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 , 0 , 1 , 0 , 1 , 0 } , org.apache.commons.math.optimization.linear.Relationship.EQ , 30.0));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 0 , 1 , 0 , 1 , 0 , 1 } , org.apache.commons.math.optimization.linear.Relationship.EQ , 30.0));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 0.8 , 0.2 , 0.0 , 0.0 , 0.0 , 0.0 } , org.apache.commons.math.optimization.linear.Relationship.GEQ , valA));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 0.0 , 0.0 , 0.7 , 0.3 , 0.0 , 0.0 } , org.apache.commons.math.optimization.linear.Relationship.GEQ , valB));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 0.0 , 0.0 , 0.0 , 0.0 , 0.4 , 0.6 } , org.apache.commons.math.optimization.linear.Relationship.GEQ , valC));
		org.apache.commons.math.optimization.RealPointValuePair solution2 = solver.optimize(f, constraints, org.apache.commons.math.optimization.GoalType.MAXIMIZE, true);
		org.junit.Assert.assertEquals(40.57143, solution2.getValue(), 1.0E-4);
	}

	@org.junit.Test
	public void testSimplexSolver() throws org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.linear.LinearObjectiveFunction f = new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(new double[]{ 15 , 10 } , 7);
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = new java.util.ArrayList<org.apache.commons.math.optimization.linear.LinearConstraint>();
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 , 0 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 2));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 0 , 1 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 3));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 , 1 } , org.apache.commons.math.optimization.linear.Relationship.EQ , 4));
		org.apache.commons.math.optimization.linear.SimplexSolver solver = new org.apache.commons.math.optimization.linear.SimplexSolver();
		org.apache.commons.math.optimization.RealPointValuePair solution = solver.optimize(f, constraints, org.apache.commons.math.optimization.GoalType.MAXIMIZE, false);
		org.junit.Assert.assertEquals(2.0, solution.getPoint()[0], 0.0);
		org.junit.Assert.assertEquals(2.0, solution.getPoint()[1], 0.0);
		org.junit.Assert.assertEquals(57.0, solution.getValue(), 0.0);
	}

	@org.junit.Test
	public void testSingleVariableAndConstraint() throws org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.linear.LinearObjectiveFunction f = new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(new double[]{ 3 } , 0);
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = new java.util.ArrayList<org.apache.commons.math.optimization.linear.LinearConstraint>();
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 10));
		org.apache.commons.math.optimization.linear.SimplexSolver solver = new org.apache.commons.math.optimization.linear.SimplexSolver();
		org.apache.commons.math.optimization.RealPointValuePair solution = solver.optimize(f, constraints, org.apache.commons.math.optimization.GoalType.MAXIMIZE, false);
		org.junit.Assert.assertEquals(10.0, solution.getPoint()[0], 0.0);
		org.junit.Assert.assertEquals(30.0, solution.getValue(), 0.0);
	}

	@org.junit.Test
	public void testModelWithNoArtificialVars() throws org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.linear.LinearObjectiveFunction f = new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(new double[]{ 15 , 10 } , 0);
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = new java.util.ArrayList<org.apache.commons.math.optimization.linear.LinearConstraint>();
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 , 0 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 2));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 0 , 1 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 3));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 , 1 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 4));
		org.apache.commons.math.optimization.linear.SimplexSolver solver = new org.apache.commons.math.optimization.linear.SimplexSolver();
		org.apache.commons.math.optimization.RealPointValuePair solution = solver.optimize(f, constraints, org.apache.commons.math.optimization.GoalType.MAXIMIZE, false);
		org.junit.Assert.assertEquals(2.0, solution.getPoint()[0], 0.0);
		org.junit.Assert.assertEquals(2.0, solution.getPoint()[1], 0.0);
		org.junit.Assert.assertEquals(50.0, solution.getValue(), 0.0);
	}

	@org.junit.Test
	public void testMinimization() throws org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.linear.LinearObjectiveFunction f = new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(new double[]{ -2 , 1 } , -5);
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = new java.util.ArrayList<org.apache.commons.math.optimization.linear.LinearConstraint>();
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 , 2 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 6));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 3 , 2 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 12));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 0 , 1 } , org.apache.commons.math.optimization.linear.Relationship.GEQ , 0));
		org.apache.commons.math.optimization.linear.SimplexSolver solver = new org.apache.commons.math.optimization.linear.SimplexSolver();
		org.apache.commons.math.optimization.RealPointValuePair solution = solver.optimize(f, constraints, org.apache.commons.math.optimization.GoalType.MINIMIZE, false);
		org.junit.Assert.assertEquals(4.0, solution.getPoint()[0], 0.0);
		org.junit.Assert.assertEquals(0.0, solution.getPoint()[1], 0.0);
		org.junit.Assert.assertEquals(-13.0, solution.getValue(), 0.0);
	}

	@org.junit.Test
	public void testSolutionWithNegativeDecisionVariable() throws org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.linear.LinearObjectiveFunction f = new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(new double[]{ -2 , 1 } , 0);
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = new java.util.ArrayList<org.apache.commons.math.optimization.linear.LinearConstraint>();
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 , 1 } , org.apache.commons.math.optimization.linear.Relationship.GEQ , 6));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 , 2 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 14));
		org.apache.commons.math.optimization.linear.SimplexSolver solver = new org.apache.commons.math.optimization.linear.SimplexSolver();
		org.apache.commons.math.optimization.RealPointValuePair solution = solver.optimize(f, constraints, org.apache.commons.math.optimization.GoalType.MAXIMIZE, false);
		org.junit.Assert.assertEquals(-2.0, solution.getPoint()[0], 0.0);
		org.junit.Assert.assertEquals(8.0, solution.getPoint()[1], 0.0);
		org.junit.Assert.assertEquals(12.0, solution.getValue(), 0.0);
	}

	@org.junit.Test(expected = org.apache.commons.math.optimization.linear.NoFeasibleSolutionException.class)
	public void testInfeasibleSolution() throws org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.linear.LinearObjectiveFunction f = new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(new double[]{ 15 } , 0);
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = new java.util.ArrayList<org.apache.commons.math.optimization.linear.LinearConstraint>();
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 1));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 } , org.apache.commons.math.optimization.linear.Relationship.GEQ , 3));
		org.apache.commons.math.optimization.linear.SimplexSolver solver = new org.apache.commons.math.optimization.linear.SimplexSolver();
		solver.optimize(f, constraints, org.apache.commons.math.optimization.GoalType.MAXIMIZE, false);
	}

	@org.junit.Test(expected = org.apache.commons.math.optimization.linear.UnboundedSolutionException.class)
	public void testUnboundedSolution() throws org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.linear.LinearObjectiveFunction f = new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(new double[]{ 15 , 10 } , 0);
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = new java.util.ArrayList<org.apache.commons.math.optimization.linear.LinearConstraint>();
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 , 0 } , org.apache.commons.math.optimization.linear.Relationship.EQ , 2));
		org.apache.commons.math.optimization.linear.SimplexSolver solver = new org.apache.commons.math.optimization.linear.SimplexSolver();
		solver.optimize(f, constraints, org.apache.commons.math.optimization.GoalType.MAXIMIZE, false);
	}

	@org.junit.Test
	public void testRestrictVariablesToNonNegative() throws org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.linear.LinearObjectiveFunction f = new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(new double[]{ 409 , 523 , 70 , 204 , 339 } , 0);
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = new java.util.ArrayList<org.apache.commons.math.optimization.linear.LinearConstraint>();
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 43 , 56 , 345 , 56 , 5 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 4567456));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 12 , 45 , 7 , 56 , 23 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 56454));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 8 , 768 , 0 , 34 , 7456 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 1923421));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 12342 , 2342 , 34 , 678 , 2342 } , org.apache.commons.math.optimization.linear.Relationship.GEQ , 4356));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 45 , 678 , 76 , 52 , 23 } , org.apache.commons.math.optimization.linear.Relationship.EQ , 456356));
		org.apache.commons.math.optimization.linear.SimplexSolver solver = new org.apache.commons.math.optimization.linear.SimplexSolver();
		org.apache.commons.math.optimization.RealPointValuePair solution = solver.optimize(f, constraints, org.apache.commons.math.optimization.GoalType.MAXIMIZE, true);
		org.junit.Assert.assertEquals(2902.92783505155, solution.getPoint()[0], 1.0E-7);
		org.junit.Assert.assertEquals(480.419243986254, solution.getPoint()[1], 1.0E-7);
		org.junit.Assert.assertEquals(0.0, solution.getPoint()[2], 1.0E-7);
		org.junit.Assert.assertEquals(0.0, solution.getPoint()[3], 1.0E-7);
		org.junit.Assert.assertEquals(0.0, solution.getPoint()[4], 1.0E-7);
		org.junit.Assert.assertEquals(1438556.7491409, solution.getValue(), 1.0E-7);
	}

	@org.junit.Test
	public void testEpsilon() throws org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.linear.LinearObjectiveFunction f = new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(new double[]{ 10 , 5 , 1 } , 0);
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = new java.util.ArrayList<org.apache.commons.math.optimization.linear.LinearConstraint>();
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 9 , 8 , 0 } , org.apache.commons.math.optimization.linear.Relationship.EQ , 17));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 0 , 7 , 8 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 7));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 10 , 0 , 2 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 10));
		org.apache.commons.math.optimization.linear.SimplexSolver solver = new org.apache.commons.math.optimization.linear.SimplexSolver();
		org.apache.commons.math.optimization.RealPointValuePair solution = solver.optimize(f, constraints, org.apache.commons.math.optimization.GoalType.MAXIMIZE, false);
		org.junit.Assert.assertEquals(1.0, solution.getPoint()[0], 0.0);
		org.junit.Assert.assertEquals(1.0, solution.getPoint()[1], 0.0);
		org.junit.Assert.assertEquals(0.0, solution.getPoint()[2], 0.0);
		org.junit.Assert.assertEquals(15.0, solution.getValue(), 0.0);
	}

	@org.junit.Test
	public void testTrivialModel() throws org.apache.commons.math.optimization.OptimizationException {
		org.apache.commons.math.optimization.linear.LinearObjectiveFunction f = new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(new double[]{ 1 , 1 } , 0);
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = new java.util.ArrayList<org.apache.commons.math.optimization.linear.LinearConstraint>();
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 , 1 } , org.apache.commons.math.optimization.linear.Relationship.EQ , 0));
		org.apache.commons.math.optimization.linear.SimplexSolver solver = new org.apache.commons.math.optimization.linear.SimplexSolver();
		org.apache.commons.math.optimization.RealPointValuePair solution = solver.optimize(f, constraints, org.apache.commons.math.optimization.GoalType.MAXIMIZE, true);
		org.junit.Assert.assertEquals(0, solution.getValue(), 1.0E-7);
	}

	@org.junit.Test
	public void testLargeModel() throws org.apache.commons.math.optimization.OptimizationException {
		double[] objective = new double[]{ 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 12 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 12 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 12 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 12 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 12 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 12 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 };
		org.apache.commons.math.optimization.linear.LinearObjectiveFunction f = new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(objective , 0);
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = new java.util.ArrayList<org.apache.commons.math.optimization.linear.LinearConstraint>();
		constraints.add(equationFromString(objective.length, "x0 + x1 + x2 + x3 - x12 = 0"));
		constraints.add(equationFromString(objective.length, "x4 + x5 + x6 + x7 + x8 + x9 + x10 + x11 - x13 = 0"));
		constraints.add(equationFromString(objective.length, "x4 + x5 + x6 + x7 + x8 + x9 + x10 + x11 >= 49"));
		constraints.add(equationFromString(objective.length, "x0 + x1 + x2 + x3 >= 42"));
		constraints.add(equationFromString(objective.length, "x14 + x15 + x16 + x17 - x26 = 0"));
		constraints.add(equationFromString(objective.length, "x18 + x19 + x20 + x21 + x22 + x23 + x24 + x25 - x27 = 0"));
		constraints.add(equationFromString(objective.length, "x14 + x15 + x16 + x17 - x12 = 0"));
		constraints.add(equationFromString(objective.length, "x18 + x19 + x20 + x21 + x22 + x23 + x24 + x25 - x13 = 0"));
		constraints.add(equationFromString(objective.length, "x28 + x29 + x30 + x31 - x40 = 0"));
		constraints.add(equationFromString(objective.length, "x32 + x33 + x34 + x35 + x36 + x37 + x38 + x39 - x41 = 0"));
		constraints.add(equationFromString(objective.length, "x32 + x33 + x34 + x35 + x36 + x37 + x38 + x39 >= 49"));
		constraints.add(equationFromString(objective.length, "x28 + x29 + x30 + x31 >= 42"));
		constraints.add(equationFromString(objective.length, "x42 + x43 + x44 + x45 - x54 = 0"));
		constraints.add(equationFromString(objective.length, "x46 + x47 + x48 + x49 + x50 + x51 + x52 + x53 - x55 = 0"));
		constraints.add(equationFromString(objective.length, "x42 + x43 + x44 + x45 - x40 = 0"));
		constraints.add(equationFromString(objective.length, "x46 + x47 + x48 + x49 + x50 + x51 + x52 + x53 - x41 = 0"));
		constraints.add(equationFromString(objective.length, "x56 + x57 + x58 + x59 - x68 = 0"));
		constraints.add(equationFromString(objective.length, "x60 + x61 + x62 + x63 + x64 + x65 + x66 + x67 - x69 = 0"));
		constraints.add(equationFromString(objective.length, "x60 + x61 + x62 + x63 + x64 + x65 + x66 + x67 >= 51"));
		constraints.add(equationFromString(objective.length, "x56 + x57 + x58 + x59 >= 44"));
		constraints.add(equationFromString(objective.length, "x70 + x71 + x72 + x73 - x82 = 0"));
		constraints.add(equationFromString(objective.length, "x74 + x75 + x76 + x77 + x78 + x79 + x80 + x81 - x83 = 0"));
		constraints.add(equationFromString(objective.length, "x70 + x71 + x72 + x73 - x68 = 0"));
		constraints.add(equationFromString(objective.length, "x74 + x75 + x76 + x77 + x78 + x79 + x80 + x81 - x69 = 0"));
		constraints.add(equationFromString(objective.length, "x84 + x85 + x86 + x87 - x96 = 0"));
		constraints.add(equationFromString(objective.length, "x88 + x89 + x90 + x91 + x92 + x93 + x94 + x95 - x97 = 0"));
		constraints.add(equationFromString(objective.length, "x88 + x89 + x90 + x91 + x92 + x93 + x94 + x95 >= 51"));
		constraints.add(equationFromString(objective.length, "x84 + x85 + x86 + x87 >= 44"));
		constraints.add(equationFromString(objective.length, "x98 + x99 + x100 + x101 - x110 = 0"));
		constraints.add(equationFromString(objective.length, "x102 + x103 + x104 + x105 + x106 + x107 + x108 + x109 - x111 = 0"));
		constraints.add(equationFromString(objective.length, "x98 + x99 + x100 + x101 - x96 = 0"));
		constraints.add(equationFromString(objective.length, "x102 + x103 + x104 + x105 + x106 + x107 + x108 + x109 - x97 = 0"));
		constraints.add(equationFromString(objective.length, "x112 + x113 + x114 + x115 - x124 = 0"));
		constraints.add(equationFromString(objective.length, "x116 + x117 + x118 + x119 + x120 + x121 + x122 + x123 - x125 = 0"));
		constraints.add(equationFromString(objective.length, "x116 + x117 + x118 + x119 + x120 + x121 + x122 + x123 >= 49"));
		constraints.add(equationFromString(objective.length, "x112 + x113 + x114 + x115 >= 42"));
		constraints.add(equationFromString(objective.length, "x126 + x127 + x128 + x129 - x138 = 0"));
		constraints.add(equationFromString(objective.length, "x130 + x131 + x132 + x133 + x134 + x135 + x136 + x137 - x139 = 0"));
		constraints.add(equationFromString(objective.length, "x126 + x127 + x128 + x129 - x124 = 0"));
		constraints.add(equationFromString(objective.length, "x130 + x131 + x132 + x133 + x134 + x135 + x136 + x137 - x125 = 0"));
		constraints.add(equationFromString(objective.length, "x140 + x141 + x142 + x143 - x152 = 0"));
		constraints.add(equationFromString(objective.length, "x144 + x145 + x146 + x147 + x148 + x149 + x150 + x151 - x153 = 0"));
		constraints.add(equationFromString(objective.length, "x144 + x145 + x146 + x147 + x148 + x149 + x150 + x151 >= 59"));
		constraints.add(equationFromString(objective.length, "x140 + x141 + x142 + x143 >= 42"));
		constraints.add(equationFromString(objective.length, "x154 + x155 + x156 + x157 - x166 = 0"));
		constraints.add(equationFromString(objective.length, "x158 + x159 + x160 + x161 + x162 + x163 + x164 + x165 - x167 = 0"));
		constraints.add(equationFromString(objective.length, "x154 + x155 + x156 + x157 - x152 = 0"));
		constraints.add(equationFromString(objective.length, "x158 + x159 + x160 + x161 + x162 + x163 + x164 + x165 - x153 = 0"));
		constraints.add(equationFromString(objective.length, "x83 + x82 - x168 = 0"));
		constraints.add(equationFromString(objective.length, "x111 + x110 - x169 = 0"));
		constraints.add(equationFromString(objective.length, "x170 - x182 = 0"));
		constraints.add(equationFromString(objective.length, "x171 - x183 = 0"));
		constraints.add(equationFromString(objective.length, "x172 - x184 = 0"));
		constraints.add(equationFromString(objective.length, "x173 - x185 = 0"));
		constraints.add(equationFromString(objective.length, "x174 - x186 = 0"));
		constraints.add(equationFromString(objective.length, "x175 + x176 - x187 = 0"));
		constraints.add(equationFromString(objective.length, "x177 - x188 = 0"));
		constraints.add(equationFromString(objective.length, "x178 - x189 = 0"));
		constraints.add(equationFromString(objective.length, "x179 - x190 = 0"));
		constraints.add(equationFromString(objective.length, "x180 - x191 = 0"));
		constraints.add(equationFromString(objective.length, "x181 - x192 = 0"));
		constraints.add(equationFromString(objective.length, "x170 - x26 = 0"));
		constraints.add(equationFromString(objective.length, "x171 - x27 = 0"));
		constraints.add(equationFromString(objective.length, "x172 - x54 = 0"));
		constraints.add(equationFromString(objective.length, "x173 - x55 = 0"));
		constraints.add(equationFromString(objective.length, "x174 - x168 = 0"));
		constraints.add(equationFromString(objective.length, "x177 - x169 = 0"));
		constraints.add(equationFromString(objective.length, "x178 - x138 = 0"));
		constraints.add(equationFromString(objective.length, "x179 - x139 = 0"));
		constraints.add(equationFromString(objective.length, "x180 - x166 = 0"));
		constraints.add(equationFromString(objective.length, "x181 - x167 = 0"));
		constraints.add(equationFromString(objective.length, "x193 - x205 = 0"));
		constraints.add(equationFromString(objective.length, "x194 - x206 = 0"));
		constraints.add(equationFromString(objective.length, "x195 - x207 = 0"));
		constraints.add(equationFromString(objective.length, "x196 - x208 = 0"));
		constraints.add(equationFromString(objective.length, "x197 - x209 = 0"));
		constraints.add(equationFromString(objective.length, "x198 + x199 - x210 = 0"));
		constraints.add(equationFromString(objective.length, "x200 - x211 = 0"));
		constraints.add(equationFromString(objective.length, "x201 - x212 = 0"));
		constraints.add(equationFromString(objective.length, "x202 - x213 = 0"));
		constraints.add(equationFromString(objective.length, "x203 - x214 = 0"));
		constraints.add(equationFromString(objective.length, "x204 - x215 = 0"));
		constraints.add(equationFromString(objective.length, "x193 - x182 = 0"));
		constraints.add(equationFromString(objective.length, "x194 - x183 = 0"));
		constraints.add(equationFromString(objective.length, "x195 - x184 = 0"));
		constraints.add(equationFromString(objective.length, "x196 - x185 = 0"));
		constraints.add(equationFromString(objective.length, "x197 - x186 = 0"));
		constraints.add(equationFromString(objective.length, "x198 + x199 - x187 = 0"));
		constraints.add(equationFromString(objective.length, "x200 - x188 = 0"));
		constraints.add(equationFromString(objective.length, "x201 - x189 = 0"));
		constraints.add(equationFromString(objective.length, "x202 - x190 = 0"));
		constraints.add(equationFromString(objective.length, "x203 - x191 = 0"));
		constraints.add(equationFromString(objective.length, "x204 - x192 = 0"));
		org.apache.commons.math.optimization.linear.SimplexSolver solver = new org.apache.commons.math.optimization.linear.SimplexSolver();
		org.apache.commons.math.optimization.RealPointValuePair solution = solver.optimize(f, constraints, org.apache.commons.math.optimization.GoalType.MINIMIZE, true);
		org.junit.Assert.assertEquals(7518.0, solution.getValue(), 1.0E-7);
	}

	private org.apache.commons.math.optimization.linear.LinearConstraint equationFromString(int numCoefficients, java.lang.String s) {
		org.apache.commons.math.optimization.linear.Relationship relationship;
		if (s.contains(">=")) {
			relationship = org.apache.commons.math.optimization.linear.Relationship.GEQ;
		} else if (s.contains("<=")) {
			relationship = org.apache.commons.math.optimization.linear.Relationship.LEQ;
		} else if (s.contains("=")) {
			relationship = org.apache.commons.math.optimization.linear.Relationship.EQ;
		} else {
			throw new java.lang.IllegalArgumentException();
		}
		java.lang.String[] equationParts = s.split("[>|<]?=");
		double rhs = java.lang.Double.parseDouble(equationParts[1].trim());
		double[] lhs = new double[numCoefficients];
		java.lang.String left = equationParts[0].replaceAll(" ?x", "");
		java.lang.String[] coefficients = left.split(" ");
		for (java.lang.String coefficient : coefficients) {
			double value = (coefficient.charAt(0)) == '-' ? -1 : 1;
			int index = java.lang.Integer.parseInt(coefficient.replaceFirst("[+|-]", "").trim());
			lhs[index] = value;
		}
		return new org.apache.commons.math.optimization.linear.LinearConstraint(lhs , relationship , rhs);
	}
}

