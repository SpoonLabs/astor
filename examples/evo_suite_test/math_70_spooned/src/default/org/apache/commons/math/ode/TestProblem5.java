package org.apache.commons.math.ode;


public class TestProblem5 extends org.apache.commons.math.ode.TestProblem1 {
	private static final long serialVersionUID = 7579233102411804237L;

	public TestProblem5() {
		super();
		setFinalConditions(((2 * (t0)) - (t1)));
	}

	@java.lang.Override
	public org.apache.commons.math.ode.TestProblem5 copy() {
		return new org.apache.commons.math.ode.TestProblem5();
	}
}

