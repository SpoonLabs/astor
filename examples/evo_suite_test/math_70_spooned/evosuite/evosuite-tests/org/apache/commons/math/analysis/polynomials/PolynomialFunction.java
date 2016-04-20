package org.apache.commons.math.analysis.polynomials;


public class PolynomialFunction implements java.io.Serializable , org.apache.commons.math.analysis.DifferentiableUnivariateRealFunction {
	private static final java.lang.String EMPTY_ARRAY_MESSAGE = "empty polynomials coefficients array";

	private static final long serialVersionUID = -7726511984200295583L;

	private final double[] coefficients;

	public PolynomialFunction(double[] c) {
		super();
		if ((c.length) < 1) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(EMPTY_ARRAY_MESSAGE);
		} 
		int l = c.length;
		while ((l > 1) && ((c[(l - 1)]) == 0)) {
			--l;
		}
		this.coefficients = new double[l];
		java.lang.System.arraycopy(c, 0, this.coefficients, 0, l);
	}

	public double value(double x) {
		return org.apache.commons.math.analysis.polynomials.PolynomialFunction.evaluate(coefficients, x);
	}

	public int degree() {
		return (coefficients.length) - 1;
	}

	public double[] getCoefficients() {
		return coefficients.clone();
	}

	protected static double evaluate(double[] coefficients, double argument) {
		int n = coefficients.length;
		if (n < 1) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(EMPTY_ARRAY_MESSAGE);
		} 
		double result = coefficients[(n - 1)];
		for (int j = n - 2 ; j >= 0 ; j--) {
			result = (argument * result) + (coefficients[j]);
		}
		return result;
	}

	public org.apache.commons.math.analysis.polynomials.PolynomialFunction add(final org.apache.commons.math.analysis.polynomials.PolynomialFunction p) {
		final int lowLength = java.lang.Math.min(coefficients.length, p.coefficients.length);
		final int highLength = java.lang.Math.max(coefficients.length, p.coefficients.length);
		double[] newCoefficients = new double[highLength];
		for (int i = 0 ; i < lowLength ; ++i) {
			newCoefficients[i] = (coefficients[i]) + (p.coefficients[i]);
		}
		java.lang.System.arraycopy(((coefficients.length) < (p.coefficients.length) ? p.coefficients : coefficients), lowLength, newCoefficients, lowLength, (highLength - lowLength));
		return new org.apache.commons.math.analysis.polynomials.PolynomialFunction(newCoefficients);
	}

	public org.apache.commons.math.analysis.polynomials.PolynomialFunction subtract(final org.apache.commons.math.analysis.polynomials.PolynomialFunction p) {
		int lowLength = java.lang.Math.min(coefficients.length, p.coefficients.length);
		int highLength = java.lang.Math.max(coefficients.length, p.coefficients.length);
		double[] newCoefficients = new double[highLength];
		for (int i = 0 ; i < lowLength ; ++i) {
			newCoefficients[i] = (coefficients[i]) - (p.coefficients[i]);
		}
		if ((coefficients.length) < (p.coefficients.length)) {
			for (int i = lowLength ; i < highLength ; ++i) {
				newCoefficients[i] = -(p.coefficients[i]);
			}
		} else {
			java.lang.System.arraycopy(coefficients, lowLength, newCoefficients, lowLength, (highLength - lowLength));
		}
		return new org.apache.commons.math.analysis.polynomials.PolynomialFunction(newCoefficients);
	}

	public org.apache.commons.math.analysis.polynomials.PolynomialFunction negate() {
		double[] newCoefficients = new double[coefficients.length];
		for (int i = 0 ; i < (coefficients.length) ; ++i) {
			newCoefficients[i] = -(coefficients[i]);
		}
		return new org.apache.commons.math.analysis.polynomials.PolynomialFunction(newCoefficients);
	}

	public org.apache.commons.math.analysis.polynomials.PolynomialFunction multiply(final org.apache.commons.math.analysis.polynomials.PolynomialFunction p) {
		double[] newCoefficients = new double[((coefficients.length) + (p.coefficients.length)) - 1];
		for (int i = 0 ; i < (newCoefficients.length) ; ++i) {
			newCoefficients[i] = 0.0;
			for (int j = java.lang.Math.max(0, ((i + 1) - (p.coefficients.length))) ; j < (java.lang.Math.min(coefficients.length, (i + 1))) ; ++j) {
				newCoefficients[i] += (coefficients[j]) * (p.coefficients[(i - j)]);
			}
		}
		return new org.apache.commons.math.analysis.polynomials.PolynomialFunction(newCoefficients);
	}

	protected static double[] differentiate(double[] coefficients) {
		int n = coefficients.length;
		if (n < 1) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(EMPTY_ARRAY_MESSAGE);
		} 
		if (n == 1) {
			return new double[]{ 0 };
		} 
		double[] result = new double[n - 1];
		for (int i = n - 1 ; i > 0 ; i--) {
			result[(i - 1)] = i * (coefficients[i]);
		}
		return result;
	}

	public org.apache.commons.math.analysis.polynomials.PolynomialFunction polynomialDerivative() {
		return new org.apache.commons.math.analysis.polynomials.PolynomialFunction(org.apache.commons.math.analysis.polynomials.PolynomialFunction.differentiate(coefficients));
	}

	public org.apache.commons.math.analysis.UnivariateRealFunction derivative() {
		return polynomialDerivative();
	}

	@java.lang.Override
	public java.lang.String toString() {
		java.lang.StringBuffer s = new java.lang.StringBuffer();
		if ((coefficients[0]) == 0.0) {
			if ((coefficients.length) == 1) {
				return "0";
			} 
		} else {
			s.append(java.lang.Double.toString(coefficients[0]));
		}
		for (int i = 1 ; i < (coefficients.length) ; ++i) {
			if ((coefficients[i]) != 0) {
				if ((s.length()) > 0) {
					if ((coefficients[i]) < 0) {
						s.append(" - ");
					} else {
						s.append(" + ");
					}
				} else {
					if ((coefficients[i]) < 0) {
						s.append("-");
					} 
				}
				double absAi = java.lang.Math.abs(coefficients[i]);
				if ((absAi - 1) != 0) {
					s.append(java.lang.Double.toString(absAi));
					s.append(' ');
				} 
				s.append("x");
				if (i > 1) {
					s.append('^');
					s.append(java.lang.Integer.toString(i));
				} 
			} 
		}
		return s.toString();
	}

	@java.lang.Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + (java.util.Arrays.hashCode(coefficients));
		return result;
	}

	@java.lang.Override
	public boolean equals(java.lang.Object obj) {
		if ((this) == obj) {
			return true;
		} 
		if (!(obj instanceof org.apache.commons.math.analysis.polynomials.PolynomialFunction)) {
			return false;
		} 
		org.apache.commons.math.analysis.polynomials.PolynomialFunction other = ((org.apache.commons.math.analysis.polynomials.PolynomialFunction)(obj));
		if (!(java.util.Arrays.equals(coefficients, other.coefficients))) {
			return false;
		} 
		return true;
	}
}

