package org.apache.commons.math.analysis.solvers;


public class LaguerreSolver extends org.apache.commons.math.analysis.solvers.UnivariateRealSolverImpl {
	private static final java.lang.String NON_POLYNOMIAL_FUNCTION_MESSAGE = "function is not polynomial";

	private static final java.lang.String NON_POSITIVE_DEGREE_MESSAGE = "polynomial degree must be positive: degree={0}";

	@java.lang.Deprecated
	private final org.apache.commons.math.analysis.polynomials.PolynomialFunction p;

	@java.lang.Deprecated
	public LaguerreSolver(org.apache.commons.math.analysis.UnivariateRealFunction f) throws java.lang.IllegalArgumentException {
		super(f, 100, 1.0E-6);
		if (f instanceof org.apache.commons.math.analysis.polynomials.PolynomialFunction) {
			p = ((org.apache.commons.math.analysis.polynomials.PolynomialFunction)(f));
		} else {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(NON_POLYNOMIAL_FUNCTION_MESSAGE);
		}
	}

	public LaguerreSolver() {
		super(100, 1.0E-6);
		p = null;
	}

	@java.lang.Deprecated
	public org.apache.commons.math.analysis.polynomials.PolynomialFunction getPolynomialFunction() {
		return new org.apache.commons.math.analysis.polynomials.PolynomialFunction(p.getCoefficients());
	}

	@java.lang.Deprecated
	public double solve(final double min, final double max) throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		return solve(p, min, max);
	}

	@java.lang.Deprecated
	public double solve(final double min, final double max, final double initial) throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		return solve(p, min, max, initial);
	}

	public double solve(final org.apache.commons.math.analysis.UnivariateRealFunction f, final double min, final double max, final double initial) throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		if ((f.value(min)) == 0.0) {
			return min;
		} 
		if ((f.value(max)) == 0.0) {
			return max;
		} 
		if ((f.value(initial)) == 0.0) {
			return initial;
		} 
		verifyBracketing(min, max, f);
		verifySequence(min, initial, max);
		if (isBracketing(min, initial, f)) {
			return solve(f, min, initial);
		} else {
			return solve(f, initial, max);
		}
	}

	public double solve(final org.apache.commons.math.analysis.UnivariateRealFunction f, final double min, final double max) throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		if (!(f instanceof org.apache.commons.math.analysis.polynomials.PolynomialFunction)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(NON_POLYNOMIAL_FUNCTION_MESSAGE);
		} 
		if ((f.value(min)) == 0.0) {
			return min;
		} 
		if ((f.value(max)) == 0.0) {
			return max;
		} 
		verifyBracketing(min, max, f);
		double[] coefficients = ((org.apache.commons.math.analysis.polynomials.PolynomialFunction)(f)).getCoefficients();
		org.apache.commons.math.complex.Complex[] c = new org.apache.commons.math.complex.Complex[coefficients.length];
		for (int i = 0 ; i < (coefficients.length) ; i++) {
			c[i] = new org.apache.commons.math.complex.Complex(coefficients[i] , 0.0);
		}
		org.apache.commons.math.complex.Complex initial = new org.apache.commons.math.complex.Complex((0.5 * (min + max)) , 0.0);
		org.apache.commons.math.complex.Complex z = solve(c, initial);
		if (isRootOK(min, max, z)) {
			setResult(z.getReal(), iterationCount);
			return result;
		} 
		org.apache.commons.math.complex.Complex[] root = solveAll(c, initial);
		for (int i = 0 ; i < (root.length) ; i++) {
			if (isRootOK(min, max, root[i])) {
				setResult(root[i].getReal(), iterationCount);
				return result;
			} 
		}
		throw new org.apache.commons.math.ConvergenceException();
	}

	protected boolean isRootOK(double min, double max, org.apache.commons.math.complex.Complex z) {
		double tolerance = java.lang.Math.max(((relativeAccuracy) * (z.abs())), absoluteAccuracy);
		return (isSequence(min, z.getReal(), max)) && (((java.lang.Math.abs(z.getImaginary())) <= tolerance) || ((z.abs()) <= (functionValueAccuracy)));
	}

	public org.apache.commons.math.complex.Complex[] solveAll(double[] coefficients, double initial) throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		org.apache.commons.math.complex.Complex[] c = new org.apache.commons.math.complex.Complex[coefficients.length];
		org.apache.commons.math.complex.Complex z = new org.apache.commons.math.complex.Complex(initial , 0.0);
		for (int i = 0 ; i < (c.length) ; i++) {
			c[i] = new org.apache.commons.math.complex.Complex(coefficients[i] , 0.0);
		}
		return solveAll(c, z);
	}

	public org.apache.commons.math.complex.Complex[] solveAll(org.apache.commons.math.complex.Complex[] coefficients, org.apache.commons.math.complex.Complex initial) throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.MaxIterationsExceededException {
		int n = (coefficients.length) - 1;
		int iterationCount = 0;
		if (n < 1) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(NON_POSITIVE_DEGREE_MESSAGE, n);
		} 
		org.apache.commons.math.complex.Complex[] c = new org.apache.commons.math.complex.Complex[n + 1];
		for (int i = 0 ; i <= n ; i++) {
			c[i] = coefficients[i];
		}
		org.apache.commons.math.complex.Complex[] root = new org.apache.commons.math.complex.Complex[n];
		for (int i = 0 ; i < n ; i++) {
			org.apache.commons.math.complex.Complex[] subarray = new org.apache.commons.math.complex.Complex[(n - i) + 1];
			java.lang.System.arraycopy(c, 0, subarray, 0, subarray.length);
			root[i] = solve(subarray, initial);
			org.apache.commons.math.complex.Complex newc = c[(n - i)];
			org.apache.commons.math.complex.Complex oldc = null;
			for (int j = (n - i) - 1 ; j >= 0 ; j--) {
				oldc = c[j];
				c[j] = newc;
				newc = oldc.add(newc.multiply(root[i]));
			}
			iterationCount += this.iterationCount;
		}
		resultComputed = true;
		this.iterationCount = iterationCount;
		return root;
	}

	public org.apache.commons.math.complex.Complex solve(org.apache.commons.math.complex.Complex[] coefficients, org.apache.commons.math.complex.Complex initial) throws org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.MaxIterationsExceededException {
		int n = (coefficients.length) - 1;
		if (n < 1) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(NON_POSITIVE_DEGREE_MESSAGE, n);
		} 
		org.apache.commons.math.complex.Complex N = new org.apache.commons.math.complex.Complex(n , 0.0);
		org.apache.commons.math.complex.Complex N1 = new org.apache.commons.math.complex.Complex((n - 1) , 0.0);
		int i = 1;
		org.apache.commons.math.complex.Complex pv = null;
		org.apache.commons.math.complex.Complex dv = null;
		org.apache.commons.math.complex.Complex d2v = null;
		org.apache.commons.math.complex.Complex G = null;
		org.apache.commons.math.complex.Complex G2 = null;
		org.apache.commons.math.complex.Complex H = null;
		org.apache.commons.math.complex.Complex delta = null;
		org.apache.commons.math.complex.Complex denominator = null;
		org.apache.commons.math.complex.Complex z = initial;
		org.apache.commons.math.complex.Complex oldz = new org.apache.commons.math.complex.Complex(java.lang.Double.POSITIVE_INFINITY , java.lang.Double.POSITIVE_INFINITY);
		while (i <= (maximalIterationCount)) {
			pv = coefficients[n];
			dv = org.apache.commons.math.complex.Complex.ZERO;
			d2v = org.apache.commons.math.complex.Complex.ZERO;
			for (int j = n - 1 ; j >= 0 ; j--) {
				d2v = dv.add(z.multiply(d2v));
				dv = pv.add(z.multiply(dv));
				pv = coefficients[j].add(z.multiply(pv));
			}
			d2v = d2v.multiply(new org.apache.commons.math.complex.Complex(2.0 , 0.0));
			double tolerance = java.lang.Math.max(((relativeAccuracy) * (z.abs())), absoluteAccuracy);
			if ((z.subtract(oldz).abs()) <= tolerance) {
				resultComputed = true;
				iterationCount = i;
				return z;
			} 
			if ((pv.abs()) <= (functionValueAccuracy)) {
				resultComputed = true;
				iterationCount = i;
				return z;
			} 
			G = dv.divide(pv);
			G2 = G.multiply(G);
			H = G2.subtract(d2v.divide(pv));
			delta = N1.multiply(N.multiply(H).subtract(G2));
			org.apache.commons.math.complex.Complex deltaSqrt = delta.sqrt();
			org.apache.commons.math.complex.Complex dplus = G.add(deltaSqrt);
			org.apache.commons.math.complex.Complex dminus = G.subtract(deltaSqrt);
			denominator = (dplus.abs()) > (dminus.abs()) ? dplus : dminus;
			if (denominator.equals(new org.apache.commons.math.complex.Complex(0.0 , 0.0))) {
				z = z.add(new org.apache.commons.math.complex.Complex(absoluteAccuracy , absoluteAccuracy));
				oldz = new org.apache.commons.math.complex.Complex(java.lang.Double.POSITIVE_INFINITY , java.lang.Double.POSITIVE_INFINITY);
			} else {
				oldz = z;
				z = z.subtract(N.divide(denominator));
			}
			i++;
		}
		throw new org.apache.commons.math.MaxIterationsExceededException(maximalIterationCount);
	}
}

