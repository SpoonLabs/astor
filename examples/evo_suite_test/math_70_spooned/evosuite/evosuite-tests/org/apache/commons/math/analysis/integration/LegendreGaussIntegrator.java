package org.apache.commons.math.analysis.integration;


public class LegendreGaussIntegrator extends org.apache.commons.math.analysis.integration.UnivariateRealIntegratorImpl {
	private static final double[] ABSCISSAS_2 = new double[]{ (-1.0) / (java.lang.Math.sqrt(3.0)) , 1.0 / (java.lang.Math.sqrt(3.0)) };

	private static final double[] WEIGHTS_2 = new double[]{ 1.0 , 1.0 };

	private static final double[] ABSCISSAS_3 = new double[]{ -(java.lang.Math.sqrt(0.6)) , 0.0 , java.lang.Math.sqrt(0.6) };

	private static final double[] WEIGHTS_3 = new double[]{ 5.0 / 9.0 , 8.0 / 9.0 , 5.0 / 9.0 };

	private static final double[] ABSCISSAS_4 = new double[]{ -(java.lang.Math.sqrt(((15.0 + (2.0 * (java.lang.Math.sqrt(30.0)))) / 35.0))) , -(java.lang.Math.sqrt(((15.0 - (2.0 * (java.lang.Math.sqrt(30.0)))) / 35.0))) , java.lang.Math.sqrt(((15.0 - (2.0 * (java.lang.Math.sqrt(30.0)))) / 35.0)) , java.lang.Math.sqrt(((15.0 + (2.0 * (java.lang.Math.sqrt(30.0)))) / 35.0)) };

	private static final double[] WEIGHTS_4 = new double[]{ (90.0 - (5.0 * (java.lang.Math.sqrt(30.0)))) / 180.0 , (90.0 + (5.0 * (java.lang.Math.sqrt(30.0)))) / 180.0 , (90.0 + (5.0 * (java.lang.Math.sqrt(30.0)))) / 180.0 , (90.0 - (5.0 * (java.lang.Math.sqrt(30.0)))) / 180.0 };

	private static final double[] ABSCISSAS_5 = new double[]{ -(java.lang.Math.sqrt(((35.0 + (2.0 * (java.lang.Math.sqrt(70.0)))) / 63.0))) , -(java.lang.Math.sqrt(((35.0 - (2.0 * (java.lang.Math.sqrt(70.0)))) / 63.0))) , 0.0 , java.lang.Math.sqrt(((35.0 - (2.0 * (java.lang.Math.sqrt(70.0)))) / 63.0)) , java.lang.Math.sqrt(((35.0 + (2.0 * (java.lang.Math.sqrt(70.0)))) / 63.0)) };

	private static final double[] WEIGHTS_5 = new double[]{ (322.0 - (13.0 * (java.lang.Math.sqrt(70.0)))) / 900.0 , (322.0 + (13.0 * (java.lang.Math.sqrt(70.0)))) / 900.0 , 128.0 / 225.0 , (322.0 + (13.0 * (java.lang.Math.sqrt(70.0)))) / 900.0 , (322.0 - (13.0 * (java.lang.Math.sqrt(70.0)))) / 900.0 };

	private final double[] abscissas;

	private final double[] weights;

	public LegendreGaussIntegrator(final int n ,final int defaultMaximalIterationCount) throws java.lang.IllegalArgumentException {
		super(defaultMaximalIterationCount);
		switch (n) {
			case 2 :
				abscissas = ABSCISSAS_2;
				weights = WEIGHTS_2;
				break;
			case 3 :
				abscissas = ABSCISSAS_3;
				weights = WEIGHTS_3;
				break;
			case 4 :
				abscissas = ABSCISSAS_4;
				weights = WEIGHTS_4;
				break;
			case 5 :
				abscissas = ABSCISSAS_5;
				weights = WEIGHTS_5;
				break;
			default :
				throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(("{0} points Legendre-Gauss integrator not supported, " + "number of points must be in the {1}-{2} range"), n, 2, 5);
		}
	}

	@java.lang.Deprecated
	public double integrate(final double min, final double max) throws java.lang.IllegalArgumentException, org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		return integrate(f, min, max);
	}

	public double integrate(final org.apache.commons.math.analysis.UnivariateRealFunction f, final double min, final double max) throws java.lang.IllegalArgumentException, org.apache.commons.math.ConvergenceException, org.apache.commons.math.FunctionEvaluationException {
		clearResult();
		verifyInterval(min, max);
		verifyIterationCount();
		double oldt = stage(f, min, max, 1);
		int n = 2;
		for (int i = 0 ; i < (maximalIterationCount) ; ++i) {
			final double t = stage(f, min, max, n);
			final double delta = java.lang.Math.abs((t - oldt));
			final double limit = java.lang.Math.max(absoluteAccuracy, (((relativeAccuracy) * ((java.lang.Math.abs(oldt)) + (java.lang.Math.abs(t)))) * 0.5));
			if (((i + 1) >= (minimalIterationCount)) && (delta <= limit)) {
				setResult(t, i);
				return result;
			} 
			double ratio = java.lang.Math.min(4, java.lang.Math.pow((delta / limit), (0.5 / (abscissas.length))));
			n = java.lang.Math.max(((int)(ratio * n)), (n + 1));
			oldt = t;
		}
		throw new org.apache.commons.math.MaxIterationsExceededException(maximalIterationCount);
	}

	private double stage(final org.apache.commons.math.analysis.UnivariateRealFunction f, final double min, final double max, final int n) throws org.apache.commons.math.FunctionEvaluationException {
		final double step = (max - min) / n;
		final double halfStep = step / 2.0;
		double midPoint = min + halfStep;
		double sum = 0.0;
		for (int i = 0 ; i < n ; ++i) {
			for (int j = 0 ; j < (abscissas.length) ; ++j) {
				sum += (weights[j]) * (f.value((midPoint + (halfStep * (abscissas[j])))));
			}
			midPoint += step;
		}
		return halfStep * sum;
	}
}

