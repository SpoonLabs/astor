package org.apache.commons.math.analysis.polynomials;


public class PolynomialSplineFunctionTest extends junit.framework.TestCase {
	protected double tolerance = 1.0E-12;

	protected org.apache.commons.math.analysis.polynomials.PolynomialFunction[] polynomials = new org.apache.commons.math.analysis.polynomials.PolynomialFunction[]{ new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 0.0 , 1.0 , 1.0 }) , new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 2.0 , 1.0 , 1.0 }) , new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 4.0 , 1.0 , 1.0 }) };

	protected double[] knots = new double[]{ -1 , 0 , 1 , 2 };

	protected org.apache.commons.math.analysis.polynomials.PolynomialFunction dp = new org.apache.commons.math.analysis.polynomials.PolynomialFunction(new double[]{ 1.0 , 2.0 });

	public void testConstructor() {
		org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction spline = new org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction(knots , polynomials);
		junit.framework.Assert.assertTrue(java.util.Arrays.equals(knots, spline.getKnots()));
		junit.framework.Assert.assertEquals(1.0, spline.getPolynomials()[0].getCoefficients()[2], 0);
		junit.framework.Assert.assertEquals(3, spline.getN());
		try {
			new org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction(new double[]{ 0 } , polynomials);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			new org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction(new double[]{ 0 , 1 , 2 , 3 , 4 } , polynomials);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			new org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction(new double[]{ 0 , 1 , 3 , 2 } , polynomials);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	public void testValues() throws java.lang.Exception {
		org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction spline = new org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction(knots , polynomials);
		org.apache.commons.math.analysis.UnivariateRealFunction dSpline = spline.derivative();
		double x = -1;
		int index = 0;
		for (int i = 0 ; i < 10 ; i++) {
			x += 0.25;
			index = findKnot(knots, x);
			junit.framework.Assert.assertEquals(("spline function evaluation failed for x=" + x), polynomials[index].value((x - (knots[index]))), spline.value(x), tolerance);
			junit.framework.Assert.assertEquals(("spline derivative evaluation failed for x=" + x), dp.value((x - (knots[index]))), dSpline.value(x), tolerance);
		}
		for (int i = 0 ; i < 3 ; i++) {
			junit.framework.Assert.assertEquals(("spline function evaluation failed for knot=" + (knots[i])), polynomials[i].value(0), spline.value(knots[i]), tolerance);
			junit.framework.Assert.assertEquals(("spline function evaluation failed for knot=" + (knots[i])), dp.value(0), dSpline.value(knots[i]), tolerance);
		}
		try {
			x = spline.value(-1.5);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (org.apache.commons.math.FunctionEvaluationException ex) {
		}
		try {
			x = spline.value(2.5);
			junit.framework.Assert.fail("Expecting IllegalArgumentException");
		} catch (org.apache.commons.math.FunctionEvaluationException ex) {
		}
	}

	protected int findKnot(double[] knots, double x) {
		if ((x < (knots[0])) || (x >= (knots[((knots.length) - 1)]))) {
			throw new java.lang.IllegalArgumentException("x is out of range");
		} 
		for (int i = 0 ; i < (knots.length) ; i++) {
			if ((knots[i]) > x) {
				return i - 1;
			} 
		}
		throw new java.lang.IllegalArgumentException("x is out of range");
	}
}

