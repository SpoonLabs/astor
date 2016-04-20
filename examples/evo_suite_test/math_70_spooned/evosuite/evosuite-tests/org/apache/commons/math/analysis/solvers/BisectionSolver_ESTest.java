package org.apache.commons.math.analysis.solvers;


@org.junit.runner.RunWith(value = org.evosuite.runtime.EvoRunner.class)
@org.evosuite.runtime.EvoRunnerParameters(mockJVMNonDeterminism = true, resetStaticState = true, separateClassLoader = true, useVFS = true, useVNET = true)
public class BisectionSolver_ESTest extends org.apache.commons.math.analysis.solvers.BisectionSolver_ESTest_scaffolding {
	@org.junit.Test(timeout = 4000)
	public void test00() throws java.lang.Throwable {
		org.apache.commons.math.analysis.Expm1Function expm1Function0 = new org.apache.commons.math.analysis.Expm1Function();
		org.apache.commons.math.analysis.UnivariateRealFunction univariateRealFunction0 = expm1Function0.derivative();
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(univariateRealFunction0);
		bisectionSolver0.setAbsoluteAccuracy(0.0);
		double double0 = bisectionSolver0.solve(0.0, 1203.227358259, 1.0);
		org.junit.Assert.assertEquals(52, bisectionSolver0.getIterationCount());
		org.junit.Assert.assertEquals(1203.227358259, double0, 0.01);
	}

	@org.junit.Test(timeout = 4000)
	public void test01() throws java.lang.Throwable {
		org.apache.commons.math.analysis.Expm1Function expm1Function0 = new org.apache.commons.math.analysis.Expm1Function();
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(((org.apache.commons.math.analysis.UnivariateRealFunction)(expm1Function0)));
		double double0 = bisectionSolver0.solve(((org.apache.commons.math.analysis.UnivariateRealFunction)(expm1Function0)), 0.0, 2.117582368135751E-36, 0.0);
		org.junit.Assert.assertEquals(5.293955920339377E-37, double0, 0.01);
		org.junit.Assert.assertEquals(0, bisectionSolver0.getIterationCount());
	}

	@org.junit.Test(timeout = 4000)
	public void test02() throws java.lang.Throwable {
		org.apache.commons.math.analysis.QuinticFunction quinticFunction0 = new org.apache.commons.math.analysis.QuinticFunction();
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(((org.apache.commons.math.analysis.UnivariateRealFunction)(quinticFunction0)));
		double double0 = bisectionSolver0.solve(((org.apache.commons.math.analysis.UnivariateRealFunction)(quinticFunction0)), -4688.869543201, -1.0, -1.0);
		org.junit.Assert.assertEquals(32, bisectionSolver0.getIterationCount());
		org.junit.Assert.assertEquals(-1.0000002728699209, double0, 0.01);
	}

	@org.junit.Test(timeout = 4000)
	public void test03() throws java.lang.Throwable {
		org.apache.commons.math.analysis.QuinticFunction quinticFunction0 = new org.apache.commons.math.analysis.QuinticFunction();
		org.apache.commons.math.analysis.Expm1Function expm1Function0 = new org.apache.commons.math.analysis.Expm1Function();
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(((org.apache.commons.math.analysis.UnivariateRealFunction)(expm1Function0)));
		double double0 = bisectionSolver0.solve(((org.apache.commons.math.analysis.UnivariateRealFunction)(quinticFunction0)), 0.0, 1.0E-15);
		org.junit.Assert.assertEquals(2.5E-16, double0, 0.01);
		org.junit.Assert.assertEquals(0, bisectionSolver0.getIterationCount());
	}

	@org.junit.Test(timeout = 4000)
	public void test04() throws java.lang.Throwable {
		org.apache.commons.math.analysis.Expm1Function expm1Function0 = new org.apache.commons.math.analysis.Expm1Function();
		org.apache.commons.math.analysis.UnivariateRealFunction univariateRealFunction0 = expm1Function0.derivative();
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(univariateRealFunction0);
		double double0 = bisectionSolver0.solve(-2982.31880945, 1.0, 1.0);
		org.junit.Assert.assertEquals(31, bisectionSolver0.getIterationCount());
		org.junit.Assert.assertEquals(-2982.318809102696, double0, 0.01);
	}

	@org.junit.Test(timeout = 4000)
	public void test05() throws java.lang.Throwable {
		org.apache.commons.math.analysis.Expm1Function expm1Function0 = new org.apache.commons.math.analysis.Expm1Function();
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(((org.apache.commons.math.analysis.UnivariateRealFunction)(expm1Function0)));
		double double0 = bisectionSolver0.solve(0.0, 1.0E-12);
		org.junit.Assert.assertEquals(0, bisectionSolver0.getIterationCount());
		org.junit.Assert.assertEquals(2.5E-13, double0, 0.01);
	}

	@org.junit.Test(timeout = 4000)
	public void test06() throws java.lang.Throwable {
		org.apache.commons.math.analysis.SinFunction sinFunction0 = new org.apache.commons.math.analysis.SinFunction();
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(((org.apache.commons.math.analysis.UnivariateRealFunction)(sinFunction0)));
		double double0 = bisectionSolver0.solve(-2016.254123630258, 0.0);
		org.junit.Assert.assertEquals(30, bisectionSolver0.getIterationCount());
		org.junit.Assert.assertEquals(-1108.9822069248048, double0, 0.01);
	}

	@org.junit.Test(timeout = 4000)
	public void test07() throws java.lang.Throwable {
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		try {
			bisectionSolver0.solve(((org.apache.commons.math.analysis.UnivariateRealFunction)(null)), 0.0, 0.0, 0.0);
			org.junit.Assert.fail("Expecting exception: IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.MathRuntimeException", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test08() throws java.lang.Throwable {
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		org.apache.commons.math.analysis.QuinticFunction quinticFunction0 = new org.apache.commons.math.analysis.QuinticFunction();
		bisectionSolver0.setMaximalIterationCount(-927);
		try {
			bisectionSolver0.solve(((org.apache.commons.math.analysis.UnivariateRealFunction)(quinticFunction0)), 0.3503984076660156, 557.0, 1.0);
			org.junit.Assert.fail("Expecting exception: Exception");
		} catch (java.lang.Exception e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.analysis.solvers.BisectionSolver", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test09() throws java.lang.Throwable {
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		org.apache.commons.math.analysis.QuinticFunction quinticFunction0 = new org.apache.commons.math.analysis.QuinticFunction();
		try {
			bisectionSolver0.solve(((org.apache.commons.math.analysis.UnivariateRealFunction)(quinticFunction0)), 1.0, 1.0);
			org.junit.Assert.fail("Expecting exception: IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.MathRuntimeException", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test10() throws java.lang.Throwable {
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		try {
			bisectionSolver0.solve(((org.apache.commons.math.analysis.UnivariateRealFunction)(null)), -1567.121909636, 0.0);
			org.junit.Assert.fail("Expecting exception: NullPointerException");
		} catch (java.lang.NullPointerException e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.analysis.solvers.BisectionSolver", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test11() throws java.lang.Throwable {
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		try {
			bisectionSolver0.solve(-875.52475907, -875.52475907, -3772.9171773759085);
			org.junit.Assert.fail("Expecting exception: IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.MathRuntimeException", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test12() throws java.lang.Throwable {
		org.apache.commons.math.analysis.Expm1Function expm1Function0 = new org.apache.commons.math.analysis.Expm1Function();
		org.apache.commons.math.analysis.MonitoredFunction monitoredFunction0 = new org.apache.commons.math.analysis.MonitoredFunction(((org.apache.commons.math.analysis.UnivariateRealFunction)(expm1Function0)));
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(((org.apache.commons.math.analysis.UnivariateRealFunction)(monitoredFunction0)));
		bisectionSolver0.setAbsoluteAccuracy(-289.8);
		try {
			bisectionSolver0.solve(-289.8, 0.2605447570801971, -289.8);
			org.junit.Assert.fail("Expecting exception: Exception");
		} catch (java.lang.Exception e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.analysis.solvers.BisectionSolver", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test13() throws java.lang.Throwable {
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		try {
			bisectionSolver0.solve(0.5, 1553.24562058039, 0.5);
			org.junit.Assert.fail("Expecting exception: NullPointerException");
		} catch (java.lang.NullPointerException e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.analysis.solvers.BisectionSolver", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test14() throws java.lang.Throwable {
		org.apache.commons.math.analysis.Expm1Function expm1Function0 = new org.apache.commons.math.analysis.Expm1Function();
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(((org.apache.commons.math.analysis.UnivariateRealFunction)(expm1Function0)));
		bisectionSolver0.setAbsoluteAccuracy(-1604.6787);
		try {
			bisectionSolver0.solve(-1604.6787, 0.0);
			org.junit.Assert.fail("Expecting exception: Exception");
		} catch (java.lang.Exception e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.analysis.solvers.BisectionSolver", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test15() throws java.lang.Throwable {
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		try {
			bisectionSolver0.solve(-3907.8956384, 439.713944048);
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
		double double0 = bisectionSolver0.solve(((org.apache.commons.math.analysis.UnivariateRealFunction)(monitoredFunction0)), -2213.303500073061, 882.947831285651);
		org.junit.Assert.assertEquals(31, bisectionSolver0.getIterationCount());
		org.junit.Assert.assertEquals(-2.659401939745394E-7, double0, 0.01);
	}

	@org.junit.Test(timeout = 4000)
	public void test18() throws java.lang.Throwable {
		org.apache.commons.math.analysis.SinFunction sinFunction0 = new org.apache.commons.math.analysis.SinFunction();
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(((org.apache.commons.math.analysis.UnivariateRealFunction)(sinFunction0)));
		bisectionSolver0.setMaximalIterationCount(8);
		try {
			bisectionSolver0.solve(((org.apache.commons.math.analysis.UnivariateRealFunction)(sinFunction0)), -4469.7843254918, 0.0);
			org.junit.Assert.fail("Expecting exception: Exception");
		} catch (java.lang.Exception e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.analysis.solvers.BisectionSolver", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test19() throws java.lang.Throwable {
		org.apache.commons.math.analysis.Expm1Function expm1Function0 = new org.apache.commons.math.analysis.Expm1Function();
		org.apache.commons.math.analysis.MonitoredFunction monitoredFunction0 = new org.apache.commons.math.analysis.MonitoredFunction(((org.apache.commons.math.analysis.UnivariateRealFunction)(expm1Function0)));
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver(((org.apache.commons.math.analysis.UnivariateRealFunction)(monitoredFunction0)));
		try {
			bisectionSolver0.solve(548.91, -2.659401939745394E-7);
			org.junit.Assert.fail("Expecting exception: IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.MathRuntimeException", e);
		}
	}

	@org.junit.Test(timeout = 4000)
	public void test20() throws java.lang.Throwable {
		org.apache.commons.math.analysis.solvers.BisectionSolver bisectionSolver0 = new org.apache.commons.math.analysis.solvers.BisectionSolver();
		org.apache.commons.math.analysis.Expm1Function expm1Function0 = new org.apache.commons.math.analysis.Expm1Function();
		try {
			bisectionSolver0.solve(((org.apache.commons.math.analysis.UnivariateRealFunction)(expm1Function0)), 1.0E-15, 6.8147913021677, 6.8147913021677);
			org.junit.Assert.fail("Expecting exception: NullPointerException");
		} catch (java.lang.NullPointerException e) {
			org.evosuite.runtime.EvoAssertions.assertThrownBy("org.apache.commons.math.analysis.solvers.BisectionSolver", e);
		}
	}
}

