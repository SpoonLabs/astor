package org.apache.commons.math.analysis.solvers;


@org.junit.runner.RunWith(value = org.evosuite.runtime.EvoRunner.class)
@org.evosuite.runtime.EvoRunnerParameters(mockJVMNonDeterminism = true, resetStaticState = true, separateClassLoader = true, useVFS = true, useVNET = true)
public class BisectionSolver_ESTest extends org.apache.commons.math.analysis.solvers.BisectionSolver_ESTest_scaffolding {
	@org.junit.Test(timeout = 4000)
	public void test00() throws java.lang.Throwable {
		org.apache.commons.math.analysis.QuinticFunction quinticFunction0 = new org.apache.commons.math.analysis.QuinticFunction();
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(((org.apache.commons.math.analysis.UnivariateRealFunction)(quinticFunction0)));
		bisectionSolver0.solve(((org.apache.commons.math.analysis.UnivariateRealFunction)(quinticFunction0)), 584.1131131, 1155.5723711036, 1155.5723711036);
	}

	@org.junit.Test(timeout = 4000)
	public void test01() throws java.lang.Throwable {
		org.apache.commons.math.analysis.Expm1Function expm1Function0 = new org.apache.commons.math.analysis.Expm1Function();
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		bisectionSolver0.solve(((org.apache.commons.math.analysis.UnivariateRealFunction)(expm1Function0)), -2148.3548, 8.005476047401316E-6);
	}

	@org.junit.Test(timeout = 4000)
	public void test02() throws java.lang.Throwable {
		org.apache.commons.math.analysis.QuinticFunction quinticFunction0 = new org.apache.commons.math.analysis.QuinticFunction();
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(((org.apache.commons.math.analysis.UnivariateRealFunction)(quinticFunction0)));
		bisectionSolver0.solve(584.1131131, 1155.5723711036, 1.0);
	}

	@org.junit.Test(timeout = 4000)
	public void test03() throws java.lang.Throwable {
		org.apache.commons.math.analysis.Expm1Function expm1Function0 = new org.apache.commons.math.analysis.Expm1Function();
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(((org.apache.commons.math.analysis.UnivariateRealFunction)(expm1Function0)));
		bisectionSolver0.solve(-5.4394E-4, 0.2629029, 0.2629029);
	}

	@org.junit.Test(timeout = 4000)
	public void test04() throws java.lang.Throwable {
		org.apache.commons.math.analysis.Expm1Function expm1Function0 = new org.apache.commons.math.analysis.Expm1Function();
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(((org.apache.commons.math.analysis.UnivariateRealFunction)(expm1Function0)));
		bisectionSolver0.solve(-134.1783398077, 1.0);
	}

	@org.junit.Test(timeout = 4000)
	public void test05() throws java.lang.Throwable {
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		org.apache.commons.math.analysis.Expm1Function expm1Function0 = new org.apache.commons.math.analysis.Expm1Function();
		try {
			bisectionSolver0.solve(((org.apache.commons.math.analysis.UnivariateRealFunction)(expm1Function0)), -348.55, -348.55, -348.55);
			org.junit.Assert.fail("Expecting exception: IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.MathRuntimeException", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test06() throws java.lang.Throwable {
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		bisectionSolver0.setMaximalIterationCount(-1733);
		org.apache.commons.math.analysis.SinFunction sinFunction0 = new org.apache.commons.math.analysis.SinFunction();
		try {
			bisectionSolver0.solve(((org.apache.commons.math.analysis.UnivariateRealFunction)(sinFunction0)), -124.325794, 2066.0, -124.325794);
			org.junit.Assert.fail("Expecting exception: Exception");
		} catch (java.lang.Exception e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.analysis.solvers.BisectionSolver", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test07() throws java.lang.Throwable {
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		try {
			bisectionSolver0.solve(((org.apache.commons.math.analysis.UnivariateRealFunction)(null)), 0.0, 1475.5555734889667, 4909.56324899);
			org.junit.Assert.fail("Expecting exception: NullPointerException");
		} catch (java.lang.NullPointerException e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.analysis.solvers.BisectionSolver", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test08() throws java.lang.Throwable {
		org.apache.commons.math.analysis.Expm1Function expm1Function0 = new org.apache.commons.math.analysis.Expm1Function();
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(((org.apache.commons.math.analysis.UnivariateRealFunction)(expm1Function0)));
		org.apache.commons.math.analysis.MonitoredFunction monitoredFunction0 = new org.apache.commons.math.analysis.MonitoredFunction(((org.apache.commons.math.analysis.UnivariateRealFunction)(expm1Function0)));
		try {
			bisectionSolver0.solve(((org.apache.commons.math.analysis.UnivariateRealFunction)(monitoredFunction0)), 2.0711471544635067E-6, -5.442213599988703E-9);
			org.junit.Assert.fail("Expecting exception: IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.MathRuntimeException", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test09() throws java.lang.Throwable {
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		try {
			bisectionSolver0.solve(((org.apache.commons.math.analysis.UnivariateRealFunction)(null)), -1.0, 0.0);
			org.junit.Assert.fail("Expecting exception: NullPointerException");
		} catch (java.lang.NullPointerException e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.analysis.solvers.BisectionSolver", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test10() throws java.lang.Throwable {
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		try {
			bisectionSolver0.solve(1.0E-6, 1.0E-6, 1.0E-6);
			org.junit.Assert.fail("Expecting exception: IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.MathRuntimeException", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test11() throws java.lang.Throwable {
		org.apache.commons.math.analysis.Expm1Function expm1Function0 = new org.apache.commons.math.analysis.Expm1Function();
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(((org.apache.commons.math.analysis.UnivariateRealFunction)(expm1Function0)));
		bisectionSolver0.setMaximalIterationCount(1672328);
		bisectionSolver0.setAbsoluteAccuracy(-3529.000309258157);
		try {
			bisectionSolver0.solve(-124.325794, 2066.0, -1165.64783582632);
			org.junit.Assert.fail("Expecting exception: TooManyResourcesException");
		} catch (org.evosuite.runtime.TooManyResourcesException e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.evosuite.runtime.LoopCounter", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test12() throws java.lang.Throwable {
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		try {
			bisectionSolver0.solve(-1043.0, 0.0, -1042.9999995143153);
			org.junit.Assert.fail("Expecting exception: NullPointerException");
		} catch (java.lang.NullPointerException e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.analysis.solvers.BisectionSolver", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test13() throws java.lang.Throwable {
		org.apache.commons.math.analysis.QuinticFunction quinticFunction0 = new org.apache.commons.math.analysis.QuinticFunction();
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(((org.apache.commons.math.analysis.UnivariateRealFunction)(quinticFunction0)));
		try {
			bisectionSolver0.solve(1146.0, 1.0);
			org.junit.Assert.fail("Expecting exception: IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.MathRuntimeException", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test14() throws java.lang.Throwable {
		org.apache.commons.math.analysis.SinFunction sinFunction0 = new org.apache.commons.math.analysis.SinFunction();
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(((org.apache.commons.math.analysis.UnivariateRealFunction)(sinFunction0)));
		bisectionSolver0.setMaximalIterationCount(1);
		try {
			bisectionSolver0.solve(0.0, 3291.289081265524);
			org.junit.Assert.fail("Expecting exception: Exception");
		} catch (java.lang.Exception e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.analysis.solvers.BisectionSolver", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test15() throws java.lang.Throwable {
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		try {
			bisectionSolver0.solve(554.2002938, 1405.0075677104728);
			org.junit.Assert.fail("Expecting exception: NullPointerException");
		} catch (java.lang.NullPointerException e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.analysis.solvers.BisectionSolver", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test16() throws java.lang.Throwable {
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = null;
		try {
			bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(((org.apache.commons.math.analysis.UnivariateRealFunction)(null)));
			org.junit.Assert.fail("Expecting exception: IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.MathRuntimeException", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test17() throws java.lang.Throwable {
		org.apache.commons.math.analysis.Expm1Function expm1Function0 = new org.apache.commons.math.analysis.Expm1Function();
		org.apache.commons.math.analysis.MonitoredFunction monitoredFunction0 = new org.apache.commons.math.analysis.MonitoredFunction(((org.apache.commons.math.analysis.UnivariateRealFunction)(expm1Function0)));
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(((org.apache.commons.math.analysis.UnivariateRealFunction)(monitoredFunction0)));
		double double0 = bisectionSolver0.solve(((org.apache.commons.math.analysis.UnivariateRealFunction)(expm1Function0)), 0.0, 1805.75678481413);
		org.junit.Assert.assertEquals(30, bisectionSolver0.getIterationCount());
		org.junit.Assert.assertEquals(4.2043551449061605E-7, double0, 0.01);
	}

	@org.junit.Test(timeout = 4000)
	public void test18() throws java.lang.Throwable {
		org.apache.commons.math.analysis.Expm1Function expm1Function0 = new org.apache.commons.math.analysis.Expm1Function();
		org.apache.commons.math.analysis.MonitoredFunction monitoredFunction0 = new org.apache.commons.math.analysis.MonitoredFunction(((org.apache.commons.math.analysis.UnivariateRealFunction)(expm1Function0)));
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(((org.apache.commons.math.analysis.UnivariateRealFunction)(monitoredFunction0)));
		double double0 = bisectionSolver0.solve(((org.apache.commons.math.analysis.UnivariateRealFunction)(monitoredFunction0)), -1043.0, 0.0, 0.0);
		org.junit.Assert.assertEquals(29, bisectionSolver0.getIterationCount());
		org.junit.Assert.assertEquals(-4.85684722661972E-7, double0, 0.01);
	}

	@org.junit.Test(timeout = 4000)
	public void test19() throws java.lang.Throwable {
		org.apache.commons.math.analysis.Expm1Function expm1Function0 = new org.apache.commons.math.analysis.Expm1Function();
		org.apache.commons.math.analysis.MonitoredFunction monitoredFunction0 = new org.apache.commons.math.analysis.MonitoredFunction(((org.apache.commons.math.analysis.UnivariateRealFunction)(expm1Function0)));
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(((org.apache.commons.math.analysis.UnivariateRealFunction)(monitoredFunction0)));
		try {
			bisectionSolver0.solve(1249.75, 1.7781810894429843E33, 1249.75);
			org.junit.Assert.fail("Expecting exception: Exception");
		} catch (java.lang.Exception e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.analysis.solvers.BisectionSolver", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test20() throws java.lang.Throwable {
		org.apache.commons.math.analysis.SinFunction sinFunction0 = new org.apache.commons.math.analysis.SinFunction();
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(((org.apache.commons.math.analysis.UnivariateRealFunction)(sinFunction0)));
		double double0 = bisectionSolver0.solve(0.0, 3291.289081265524);
		org.junit.Assert.assertEquals(31, bisectionSolver0.getIterationCount());
		org.junit.Assert.assertEquals(3.831564776210026E-7, double0, 0.01);
	}

	@org.junit.Test(timeout = 4000)
	public void test21() throws java.lang.Throwable {
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		org.apache.commons.math.analysis.SinFunction sinFunction0 = new org.apache.commons.math.analysis.SinFunction();
		try {
			bisectionSolver0.solve(((org.apache.commons.math.analysis.UnivariateRealFunction)(sinFunction0)), -1279.391, 8.298171253910538E27);
			org.junit.Assert.fail("Expecting exception: Exception");
		} catch (java.lang.Exception e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.analysis.solvers.BisectionSolver", e);
		}
	}
}

