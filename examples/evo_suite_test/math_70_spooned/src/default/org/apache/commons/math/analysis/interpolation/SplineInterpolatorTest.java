package org.apache.commons.math.analysis.interpolation;


public class SplineInterpolatorTest extends junit.framework.TestCase {
	protected double knotTolerance = 1.0E-12;

	protected double coefficientTolerance = 1.0E-6;

	protected double interpolationTolerance = 0.01;

	public SplineInterpolatorTest(java.lang.String name) {
		super(name);
	}

	public void testInterpolateLinearDegenerateTwoSegment() throws java.lang.Exception {
		double[] x = new double[]{ 0.0 , 0.5 , 1.0 };
		double[] y = new double[]{ 0.0 , 0.5 , 1.0 };
		org.apache.commons.math.analysis.interpolation.UnivariateRealInterpolator i = new org.apache.commons.math.analysis.interpolation.SplineInterpolator();
		org.apache.commons.math.analysis.UnivariateRealFunction f = i.interpolate(x, y);
		verifyInterpolation(f, x, y);
		verifyConsistency(((org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction)(f)), x);
		org.apache.commons.math.analysis.polynomials.PolynomialFunction[] polynomials = ((org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction)(f)).getPolynomials();
		double[] target = new double[]{ y[0] , 1.0 };
		org.apache.commons.math.TestUtils.assertEquals(polynomials[0].getCoefficients(), target, coefficientTolerance);
		target = new double[]{ y[1] , 1.0 };
		org.apache.commons.math.TestUtils.assertEquals(polynomials[1].getCoefficients(), target, coefficientTolerance);
		junit.framework.Assert.assertEquals(0.0, f.value(0.0), interpolationTolerance);
		junit.framework.Assert.assertEquals(0.4, f.value(0.4), interpolationTolerance);
		junit.framework.Assert.assertEquals(1.0, f.value(1.0), interpolationTolerance);
	}

	public void testInterpolateLinearDegenerateThreeSegment() throws java.lang.Exception {
		double[] x = new double[]{ 0.0 , 0.5 , 1.0 , 1.5 };
		double[] y = new double[]{ 0.0 , 0.5 , 1.0 , 1.5 };
		org.apache.commons.math.analysis.interpolation.UnivariateRealInterpolator i = new org.apache.commons.math.analysis.interpolation.SplineInterpolator();
		org.apache.commons.math.analysis.UnivariateRealFunction f = i.interpolate(x, y);
		verifyInterpolation(f, x, y);
		org.apache.commons.math.analysis.polynomials.PolynomialFunction[] polynomials = ((org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction)(f)).getPolynomials();
		double[] target = new double[]{ y[0] , 1.0 };
		org.apache.commons.math.TestUtils.assertEquals(polynomials[0].getCoefficients(), target, coefficientTolerance);
		target = new double[]{ y[1] , 1.0 };
		org.apache.commons.math.TestUtils.assertEquals(polynomials[1].getCoefficients(), target, coefficientTolerance);
		target = new double[]{ y[2] , 1.0 };
		org.apache.commons.math.TestUtils.assertEquals(polynomials[2].getCoefficients(), target, coefficientTolerance);
		junit.framework.Assert.assertEquals(0, f.value(0), interpolationTolerance);
		junit.framework.Assert.assertEquals(1.4, f.value(1.4), interpolationTolerance);
		junit.framework.Assert.assertEquals(1.5, f.value(1.5), interpolationTolerance);
	}

	public void testInterpolateLinear() throws java.lang.Exception {
		double[] x = new double[]{ 0.0 , 0.5 , 1.0 };
		double[] y = new double[]{ 0.0 , 0.5 , 0.0 };
		org.apache.commons.math.analysis.interpolation.UnivariateRealInterpolator i = new org.apache.commons.math.analysis.interpolation.SplineInterpolator();
		org.apache.commons.math.analysis.UnivariateRealFunction f = i.interpolate(x, y);
		verifyInterpolation(f, x, y);
		verifyConsistency(((org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction)(f)), x);
		org.apache.commons.math.analysis.polynomials.PolynomialFunction[] polynomials = ((org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction)(f)).getPolynomials();
		double[] target = new double[]{ y[0] , 1.5 , 0.0 , -2.0 };
		org.apache.commons.math.TestUtils.assertEquals(polynomials[0].getCoefficients(), target, coefficientTolerance);
		target = new double[]{ y[1] , 0.0 , -3.0 , 2.0 };
		org.apache.commons.math.TestUtils.assertEquals(polynomials[1].getCoefficients(), target, coefficientTolerance);
	}

	public void testInterpolateSin() throws java.lang.Exception {
		double[] x = new double[]{ 0.0 , (java.lang.Math.PI) / 6.0 , (java.lang.Math.PI) / 2.0 , (5.0 * (java.lang.Math.PI)) / 6.0 , java.lang.Math.PI , (7.0 * (java.lang.Math.PI)) / 6.0 , (3.0 * (java.lang.Math.PI)) / 2.0 , (11.0 * (java.lang.Math.PI)) / 6.0 , 2.0 * (java.lang.Math.PI) };
		double[] y = new double[]{ 0.0 , 0.5 , 1.0 , 0.5 , 0.0 , -0.5 , -1.0 , -0.5 , 0.0 };
		org.apache.commons.math.analysis.interpolation.UnivariateRealInterpolator i = new org.apache.commons.math.analysis.interpolation.SplineInterpolator();
		org.apache.commons.math.analysis.UnivariateRealFunction f = i.interpolate(x, y);
		verifyInterpolation(f, x, y);
		verifyConsistency(((org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction)(f)), x);
		org.apache.commons.math.analysis.polynomials.PolynomialFunction[] polynomials = ((org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction)(f)).getPolynomials();
		double[] target = new double[]{ y[0] , 1.002676 , 0.0 , -0.17415829 };
		org.apache.commons.math.TestUtils.assertEquals(polynomials[0].getCoefficients(), target, coefficientTolerance);
		target = new double[]{ y[1] , 0.8594367 , -0.2735672 , -0.08707914 };
		org.apache.commons.math.TestUtils.assertEquals(polynomials[1].getCoefficients(), target, coefficientTolerance);
		target = new double[]{ y[2] , 1.471804E-17 , -0.5471344 , 0.08707914 };
		org.apache.commons.math.TestUtils.assertEquals(polynomials[2].getCoefficients(), target, coefficientTolerance);
		target = new double[]{ y[3] , -0.8594367 , -0.2735672 , 0.17415829 };
		org.apache.commons.math.TestUtils.assertEquals(polynomials[3].getCoefficients(), target, coefficientTolerance);
		target = new double[]{ y[4] , -1.002676 , 6.548562E-17 , 0.17415829 };
		org.apache.commons.math.TestUtils.assertEquals(polynomials[4].getCoefficients(), target, coefficientTolerance);
		target = new double[]{ y[5] , -0.8594367 , 0.2735672 , 0.08707914 };
		org.apache.commons.math.TestUtils.assertEquals(polynomials[5].getCoefficients(), target, coefficientTolerance);
		target = new double[]{ y[6] , 3.466465E-16 , 0.5471344 , -0.08707914 };
		org.apache.commons.math.TestUtils.assertEquals(polynomials[6].getCoefficients(), target, coefficientTolerance);
		target = new double[]{ y[7] , 0.8594367 , 0.2735672 , -0.17415829 };
		org.apache.commons.math.TestUtils.assertEquals(polynomials[7].getCoefficients(), target, coefficientTolerance);
		junit.framework.Assert.assertEquals(((java.lang.Math.sqrt(2.0)) / 2.0), f.value(((java.lang.Math.PI) / 4.0)), interpolationTolerance);
		junit.framework.Assert.assertEquals(((java.lang.Math.sqrt(2.0)) / 2.0), f.value(((3.0 * (java.lang.Math.PI)) / 4.0)), interpolationTolerance);
	}

	public void testIllegalArguments() throws org.apache.commons.math.MathException {
		org.apache.commons.math.analysis.interpolation.UnivariateRealInterpolator i = new org.apache.commons.math.analysis.interpolation.SplineInterpolator();
		try {
			double[] xval = new double[]{ 0.0 , 1.0 };
			double[] yval = new double[]{ 0.0 , 1.0 , 2.0 };
			i.interpolate(xval, yval);
			junit.framework.Assert.fail("Failed to detect data set array with different sizes.");
		} catch (java.lang.IllegalArgumentException iae) {
		}
		try {
			double[] xval = new double[]{ 0.0 , 1.0 , 0.5 };
			double[] yval = new double[]{ 0.0 , 1.0 , 2.0 };
			i.interpolate(xval, yval);
			junit.framework.Assert.fail("Failed to detect unsorted arguments.");
		} catch (java.lang.IllegalArgumentException iae) {
		}
	}

	protected void verifyInterpolation(org.apache.commons.math.analysis.UnivariateRealFunction f, double[] x, double[] y) throws java.lang.Exception {
		for (int i = 0 ; i < (x.length) ; i++) {
			junit.framework.Assert.assertEquals(f.value(x[i]), y[i], knotTolerance);
		}
	}

	protected void verifyConsistency(org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction f, double[] x) throws java.lang.Exception {
		org.apache.commons.math.analysis.polynomials.PolynomialFunction[] polynomials = f.getPolynomials();
		for (int i = 1 ; i < ((x.length) - 2) ; i++) {
			junit.framework.Assert.assertEquals(polynomials[i].value(((x[(i + 1)]) - (x[i]))), polynomials[(i + 1)].value(0), 0.1);
			junit.framework.Assert.assertEquals(polynomials[i].derivative().value(((x[(i + 1)]) - (x[i]))), polynomials[(i + 1)].derivative().value(0), 0.5);
			junit.framework.Assert.assertEquals(polynomials[i].polynomialDerivative().derivative().value(((x[(i + 1)]) - (x[i]))), polynomials[(i + 1)].polynomialDerivative().derivative().value(0), 0.5);
		}
	}
}

