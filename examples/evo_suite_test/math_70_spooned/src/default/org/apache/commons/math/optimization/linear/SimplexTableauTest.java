package org.apache.commons.math.optimization.linear;


public class SimplexTableauTest {
	@org.junit.Test
	public void testInitialization() {
		org.apache.commons.math.optimization.linear.LinearObjectiveFunction f = createFunction();
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = createConstraints();
		org.apache.commons.math.optimization.linear.SimplexTableau tableau = new org.apache.commons.math.optimization.linear.SimplexTableau(f , constraints , org.apache.commons.math.optimization.GoalType.MAXIMIZE , false , 1.0E-6);
		double[][] expectedInitialTableau = new double[][]{ new double[]{ -1 , 0 , -1 , -1 , 2 , 0 , 0 , 0 , -4 } , new double[]{ 0 , 1 , -15 , -10 , 25 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 1 , 0 , -1 , 1 , 0 , 0 , 2 } , new double[]{ 0 , 0 , 0 , 1 , -1 , 0 , 1 , 0 , 3 } , new double[]{ 0 , 0 , 1 , 1 , -2 , 0 , 0 , 1 , 4 } };
		assertMatrixEquals(expectedInitialTableau, tableau.getData());
	}

	@org.junit.Test
	public void testDropPhase1Objective() {
		org.apache.commons.math.optimization.linear.LinearObjectiveFunction f = createFunction();
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = createConstraints();
		org.apache.commons.math.optimization.linear.SimplexTableau tableau = new org.apache.commons.math.optimization.linear.SimplexTableau(f , constraints , org.apache.commons.math.optimization.GoalType.MAXIMIZE , false , 1.0E-6);
		double[][] expectedTableau = new double[][]{ new double[]{ 1 , -15 , -10 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 1 , 0 , 1 , 0 , 0 , 2 } , new double[]{ 0 , 0 , 1 , 0 , 1 , 0 , 3 } , new double[]{ 0 , 1 , 1 , 0 , 0 , 1 , 4 } };
		tableau.dropPhase1Objective();
		assertMatrixEquals(expectedTableau, tableau.getData());
	}

	@org.junit.Test
	public void testTableauWithNoArtificialVars() {
		org.apache.commons.math.optimization.linear.LinearObjectiveFunction f = new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(new double[]{ 15 , 10 } , 0);
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = new java.util.ArrayList<org.apache.commons.math.optimization.linear.LinearConstraint>();
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 , 0 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 2));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 0 , 1 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 3));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 , 1 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 4));
		org.apache.commons.math.optimization.linear.SimplexTableau tableau = new org.apache.commons.math.optimization.linear.SimplexTableau(f , constraints , org.apache.commons.math.optimization.GoalType.MAXIMIZE , false , 1.0E-6);
		double[][] initialTableau = new double[][]{ new double[]{ 1 , -15 , -10 , 25 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 1 , 0 , -1 , 1 , 0 , 0 , 2 } , new double[]{ 0 , 0 , 1 , -1 , 0 , 1 , 0 , 3 } , new double[]{ 0 , 1 , 1 , -2 , 0 , 0 , 1 , 4 } };
		assertMatrixEquals(initialTableau, tableau.getData());
	}

	@org.junit.Test
	public void testSerial() {
		org.apache.commons.math.optimization.linear.LinearObjectiveFunction f = createFunction();
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = createConstraints();
		org.apache.commons.math.optimization.linear.SimplexTableau tableau = new org.apache.commons.math.optimization.linear.SimplexTableau(f , constraints , org.apache.commons.math.optimization.GoalType.MAXIMIZE , false , 1.0E-6);
		org.junit.Assert.assertEquals(tableau, org.apache.commons.math.TestUtils.serializeAndRecover(tableau));
	}

	private org.apache.commons.math.optimization.linear.LinearObjectiveFunction createFunction() {
		return new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(new double[]{ 15 , 10 } , 0);
	}

	private java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> createConstraints() {
		java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints = new java.util.ArrayList<org.apache.commons.math.optimization.linear.LinearConstraint>();
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 , 0 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 2));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 0 , 1 } , org.apache.commons.math.optimization.linear.Relationship.LEQ , 3));
		constraints.add(new org.apache.commons.math.optimization.linear.LinearConstraint(new double[]{ 1 , 1 } , org.apache.commons.math.optimization.linear.Relationship.EQ , 4));
		return constraints;
	}

	private void assertMatrixEquals(double[][] expected, double[][] result) {
		org.junit.Assert.assertEquals("Wrong number of rows.", expected.length, result.length);
		for (int i = 0 ; i < (expected.length) ; i++) {
			org.junit.Assert.assertEquals("Wrong number of columns.", expected[i].length, result[i].length);
			for (int j = 0 ; j < (expected[i].length) ; j++) {
				org.junit.Assert.assertEquals((((("Wrong value at position [" + i) + ",") + j) + "]"), expected[i][j], result[i][j], 1.0E-15);
			}
		}
	}
}

