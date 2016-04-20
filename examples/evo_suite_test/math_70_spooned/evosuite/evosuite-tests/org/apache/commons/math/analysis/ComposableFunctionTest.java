package org.apache.commons.math.analysis;


public class ComposableFunctionTest {
	@org.junit.Test
	public void testZero() throws org.apache.commons.math.FunctionEvaluationException {
		org.junit.Assert.assertEquals(0.0, org.apache.commons.math.analysis.ComposableFunction.ZERO.value(1), 1.0E-15);
		org.junit.Assert.assertEquals(0.0, org.apache.commons.math.analysis.ComposableFunction.ZERO.value(2), 1.0E-15);
	}

	@org.junit.Test
	public void testOne() throws org.apache.commons.math.FunctionEvaluationException {
		org.junit.Assert.assertEquals(1.0, org.apache.commons.math.analysis.ComposableFunction.ONE.value(1), 1.0E-15);
		org.junit.Assert.assertEquals(1.0, org.apache.commons.math.analysis.ComposableFunction.ONE.value(2), 1.0E-15);
	}

	@org.junit.Test
	public void testIdentity() throws org.apache.commons.math.FunctionEvaluationException {
		org.junit.Assert.assertEquals(1.0, org.apache.commons.math.analysis.ComposableFunction.IDENTITY.value(1), 1.0E-15);
		org.junit.Assert.assertEquals(2.0, org.apache.commons.math.analysis.ComposableFunction.IDENTITY.value(2), 1.0E-15);
	}

	@org.junit.Test
	public void testRint() throws org.apache.commons.math.FunctionEvaluationException {
		org.junit.Assert.assertEquals(1.0, org.apache.commons.math.analysis.ComposableFunction.RINT.value(0.9), 1.0E-15);
		org.junit.Assert.assertEquals(2.0, org.apache.commons.math.analysis.ComposableFunction.RINT.value(2.2), 1.0E-15);
	}

	@org.junit.Test
	public void testSignum() throws org.apache.commons.math.FunctionEvaluationException {
		org.junit.Assert.assertEquals(1.0, org.apache.commons.math.analysis.ComposableFunction.SIGNUM.value(12.3), 1.0E-15);
		org.junit.Assert.assertEquals(-1.0, org.apache.commons.math.analysis.ComposableFunction.SIGNUM.value(-6), 1.0E-15);
	}

	@org.junit.Test
	public void testComposition() throws org.apache.commons.math.FunctionEvaluationException {
		org.apache.commons.math.analysis.ComposableFunction abs = org.apache.commons.math.analysis.ComposableFunction.ABS;
		org.apache.commons.math.analysis.ComposableFunction acos = org.apache.commons.math.analysis.ComposableFunction.ACOS;
		org.apache.commons.math.analysis.ComposableFunction asin = org.apache.commons.math.analysis.ComposableFunction.ASIN;
		org.apache.commons.math.analysis.ComposableFunction atan = org.apache.commons.math.analysis.ComposableFunction.ATAN;
		org.apache.commons.math.analysis.ComposableFunction cbrt = org.apache.commons.math.analysis.ComposableFunction.CBRT;
		org.apache.commons.math.analysis.ComposableFunction ceil = org.apache.commons.math.analysis.ComposableFunction.CEIL;
		org.apache.commons.math.analysis.ComposableFunction cos = org.apache.commons.math.analysis.ComposableFunction.COS;
		org.apache.commons.math.analysis.ComposableFunction cosh = org.apache.commons.math.analysis.ComposableFunction.COSH;
		org.apache.commons.math.analysis.ComposableFunction exp = org.apache.commons.math.analysis.ComposableFunction.EXP;
		org.apache.commons.math.analysis.ComposableFunction expm1 = org.apache.commons.math.analysis.ComposableFunction.EXPM1;
		org.apache.commons.math.analysis.ComposableFunction floor = org.apache.commons.math.analysis.ComposableFunction.FLOOR;
		org.apache.commons.math.analysis.ComposableFunction id = org.apache.commons.math.analysis.ComposableFunction.IDENTITY;
		org.apache.commons.math.analysis.ComposableFunction log = org.apache.commons.math.analysis.ComposableFunction.LOG;
		org.apache.commons.math.analysis.ComposableFunction log10 = org.apache.commons.math.analysis.ComposableFunction.LOG10;
		org.apache.commons.math.analysis.ComposableFunction negate = org.apache.commons.math.analysis.ComposableFunction.NEGATE;
		org.apache.commons.math.analysis.ComposableFunction sin = org.apache.commons.math.analysis.ComposableFunction.SIN;
		org.apache.commons.math.analysis.ComposableFunction sinh = org.apache.commons.math.analysis.ComposableFunction.SINH;
		org.apache.commons.math.analysis.ComposableFunction sqrt = org.apache.commons.math.analysis.ComposableFunction.SQRT;
		org.apache.commons.math.analysis.ComposableFunction tan = org.apache.commons.math.analysis.ComposableFunction.TAN;
		org.apache.commons.math.analysis.ComposableFunction tanh = org.apache.commons.math.analysis.ComposableFunction.TANH;
		org.apache.commons.math.analysis.ComposableFunction ulp = org.apache.commons.math.analysis.ComposableFunction.ULP;
		org.apache.commons.math.analysis.ComposableFunction f1 = sqrt.of(abs.of(expm1.of(cbrt.of(tanh).of(id))));
		for (double x = 0.1 ; x < 0.9 ; x += 0.01) {
			org.junit.Assert.assertEquals(java.lang.Math.sqrt(java.lang.Math.abs(java.lang.Math.expm1(java.lang.Math.cbrt(java.lang.Math.tanh(x))))), f1.value(x), 1.0E-15);
		}
		org.apache.commons.math.analysis.ComposableFunction f2 = cosh.of(sinh.of(tanh.of(ceil.postCompose(log.postCompose(cosh)))));
		for (double x = 0.1 ; x < 12.9 ; x += 1.0) {
			org.junit.Assert.assertEquals(java.lang.Math.cosh(java.lang.Math.sinh(java.lang.Math.tanh(java.lang.Math.cosh(java.lang.Math.log(java.lang.Math.ceil(x)))))), f2.value(x), 1.0E-15);
		}
		org.apache.commons.math.analysis.ComposableFunction f3 = cos.of(sin.of(tan.of(acos.of(asin.of(log10.of(log.of(ulp)))))));
		for (double x = 1.0E16 ; x < 1.0E17 ; x += 1.0E16) {
			org.junit.Assert.assertEquals(java.lang.Math.cos(java.lang.Math.sin(java.lang.Math.tan(java.lang.Math.acos(java.lang.Math.asin(java.lang.Math.log10(java.lang.Math.log(java.lang.Math.ulp(x)))))))), f3.value(x), 1.0E-15);
		}
		org.apache.commons.math.analysis.ComposableFunction f4 = atan.of(exp.of(negate.of(floor)));
		for (double x = 1.1 ; x < 10.2 ; x += 1.0) {
			org.junit.Assert.assertEquals(java.lang.Math.atan(java.lang.Math.exp(-(java.lang.Math.floor(x)))), f4.value(x), 1.0E-15);
		}
	}

	@org.junit.Test
	public void testCombine() throws org.apache.commons.math.FunctionEvaluationException {
		org.apache.commons.math.analysis.ComposableFunction f = org.apache.commons.math.analysis.ComposableFunction.COS.combine(org.apache.commons.math.analysis.ComposableFunction.ASIN, org.apache.commons.math.analysis.BinaryFunction.POW);
		for (double x = 0.1 ; x < 0.9 ; x += 0.01) {
			org.junit.Assert.assertEquals(java.lang.Math.pow(java.lang.Math.cos(x), java.lang.Math.asin(x)), f.value(x), 1.0E-15);
		}
	}

	@org.junit.Test
	public void testSimpleCombination() throws org.apache.commons.math.FunctionEvaluationException {
		org.apache.commons.math.analysis.ComposableFunction f1 = org.apache.commons.math.analysis.ComposableFunction.COS.add(3);
		org.apache.commons.math.analysis.ComposableFunction f2 = org.apache.commons.math.analysis.ComposableFunction.COS.add(org.apache.commons.math.analysis.ComposableFunction.SIN);
		org.apache.commons.math.analysis.ComposableFunction f3 = org.apache.commons.math.analysis.ComposableFunction.COS.subtract(org.apache.commons.math.analysis.ComposableFunction.SIN);
		org.apache.commons.math.analysis.ComposableFunction f4 = org.apache.commons.math.analysis.ComposableFunction.COS.multiply(org.apache.commons.math.analysis.ComposableFunction.SIN);
		org.apache.commons.math.analysis.ComposableFunction f5 = org.apache.commons.math.analysis.ComposableFunction.COS.multiply(5);
		org.apache.commons.math.analysis.ComposableFunction f6 = org.apache.commons.math.analysis.ComposableFunction.COS.divide(org.apache.commons.math.analysis.ComposableFunction.SIN);
		for (double x = 0.1 ; x < 0.9 ; x += 0.01) {
			org.junit.Assert.assertEquals(((java.lang.Math.cos(x)) + 3), f1.value(x), 1.0E-15);
			org.junit.Assert.assertEquals(((java.lang.Math.cos(x)) + (java.lang.Math.sin(x))), f2.value(x), 1.0E-15);
			org.junit.Assert.assertEquals(((java.lang.Math.cos(x)) - (java.lang.Math.sin(x))), f3.value(x), 1.0E-15);
			org.junit.Assert.assertEquals(((java.lang.Math.cos(x)) * (java.lang.Math.sin(x))), f4.value(x), 1.0E-15);
			org.junit.Assert.assertEquals(((java.lang.Math.cos(x)) * 5), f5.value(x), 1.0E-15);
			org.junit.Assert.assertEquals(((java.lang.Math.cos(x)) / (java.lang.Math.sin(x))), f6.value(x), 1.0E-15);
		}
	}

	@org.junit.Test
	public void testCollector() throws org.apache.commons.math.FunctionEvaluationException {
		org.apache.commons.math.analysis.ComposableFunction f = org.apache.commons.math.analysis.BinaryFunction.POW.fix2ndArgument(2);
		org.junit.Assert.assertEquals(30, f.asCollector().value(new double[]{ 1 , 2 , 3 , 4 }), 1.0E-15);
		org.junit.Assert.assertEquals(33, f.asCollector(3).value(new double[]{ 1 , 2 , 3 , 4 }), 1.0E-15);
		org.junit.Assert.assertEquals(-30, f.asCollector(org.apache.commons.math.analysis.BinaryFunction.SUBTRACT).value(new double[]{ 1 , 2 , 3 , 4 }), 1.0E-15);
		org.junit.Assert.assertEquals(1152, f.asCollector(org.apache.commons.math.analysis.BinaryFunction.MULTIPLY, 2).value(new double[]{ 1 , 2 , 3 , 4 }), 1.0E-15);
	}
}

