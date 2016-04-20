package org.apache.commons.math.optimization.fitting;


public class HarmonicFitter {
	private final org.apache.commons.math.optimization.fitting.CurveFitter fitter;

	private double[] parameters;

	public HarmonicFitter(final org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer optimizer) {
		this.fitter = new org.apache.commons.math.optimization.fitting.CurveFitter(optimizer);
		parameters = null;
	}

	public HarmonicFitter(final org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer optimizer ,final double[] initialGuess) {
		this.fitter = new org.apache.commons.math.optimization.fitting.CurveFitter(optimizer);
		this.parameters = initialGuess.clone();
	}

	public void addObservedPoint(double weight, double x, double y) {
		fitter.addObservedPoint(weight, x, y);
	}

	public org.apache.commons.math.optimization.fitting.HarmonicFunction fit() throws org.apache.commons.math.optimization.OptimizationException {
		try {
			if ((parameters) == null) {
				final org.apache.commons.math.optimization.fitting.WeightedObservedPoint[] observations = fitter.getObservations();
				if ((observations.length) < 4) {
					throw new org.apache.commons.math.optimization.OptimizationException("sample contains {0} observed points, at least {1} are required" , observations.length , 4);
				} 
				org.apache.commons.math.optimization.fitting.HarmonicCoefficientsGuesser guesser = new org.apache.commons.math.optimization.fitting.HarmonicCoefficientsGuesser(observations);
				guesser.guess();
				parameters = new double[]{ guesser.getGuessedAmplitude() , guesser.getGuessedPulsation() , guesser.getGuessedPhase() };
			} 
			double[] fitted = fitter.fit(new org.apache.commons.math.optimization.fitting.HarmonicFitter.ParametricHarmonicFunction(), parameters);
			return new org.apache.commons.math.optimization.fitting.HarmonicFunction(fitted[0] , fitted[1] , fitted[2]);
		} catch (org.apache.commons.math.FunctionEvaluationException fee) {
			throw org.apache.commons.math.MathRuntimeException.createInternalError(fee);
		}
	}

	private static class ParametricHarmonicFunction implements org.apache.commons.math.optimization.fitting.ParametricRealFunction {
		public double value(double x, double[] parameters) {
			final double a = parameters[0];
			final double omega = parameters[1];
			final double phi = parameters[2];
			return a * (java.lang.Math.cos(((omega * x) + phi)));
		}

		public double[] gradient(double x, double[] parameters) {
			final double a = parameters[0];
			final double omega = parameters[1];
			final double phi = parameters[2];
			final double alpha = (omega * x) + phi;
			final double cosAlpha = java.lang.Math.cos(alpha);
			final double sinAlpha = java.lang.Math.sin(alpha);
			return new double[]{ cosAlpha , ((-a) * x) * sinAlpha , (-a) * sinAlpha };
		}
	}
}

