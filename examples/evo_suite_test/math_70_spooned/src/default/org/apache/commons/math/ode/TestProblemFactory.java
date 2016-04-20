package org.apache.commons.math.ode;


public class TestProblemFactory {
	private static final org.apache.commons.math.ode.TestProblemAbstract[] pool = new org.apache.commons.math.ode.TestProblemAbstract[]{ new org.apache.commons.math.ode.TestProblem1() , new org.apache.commons.math.ode.TestProblem2() , new org.apache.commons.math.ode.TestProblem3() , new org.apache.commons.math.ode.TestProblem4() , new org.apache.commons.math.ode.TestProblem5() , new org.apache.commons.math.ode.TestProblem6() };

	private TestProblemFactory() {
	}

	public static org.apache.commons.math.ode.TestProblemAbstract[] getProblems() {
		return pool;
	}
}

