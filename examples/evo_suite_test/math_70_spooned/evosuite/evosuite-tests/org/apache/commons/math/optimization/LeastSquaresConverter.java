package org.apache.commons.math.optimization;


public class LeastSquaresConverter implements org.apache.commons.math.analysis.MultivariateRealFunction {
	private final org.apache.commons.math.analysis.MultivariateVectorialFunction function;

	private final double[] observations;

	private final double[] weights;

	private final org.apache.commons.math.linear.RealMatrix scale;

	public LeastSquaresConverter(final org.apache.commons.math.analysis.MultivariateVectorialFunction function ,final double[] observations) {
		this.function = function;
		this.observations = observations.clone();
		this.weights = null;
		this.scale = null;
	}

	public LeastSquaresConverter(final org.apache.commons.math.analysis.MultivariateVectorialFunction function ,final double[] observations ,final double[] weights) throws java.lang.IllegalArgumentException {
		if ((observations.length) != (weights.length)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("dimension mismatch {0} != {1}", observations.length, weights.length);
		} 
		this.function = function;
		this.observations = observations.clone();
		this.weights = weights.clone();
		this.scale = null;
	}

	public LeastSquaresConverter(final org.apache.commons.math.analysis.MultivariateVectorialFunction function ,final double[] observations ,final org.apache.commons.math.linear.RealMatrix scale) throws java.lang.IllegalArgumentException {
		if ((observations.length) != (scale.getColumnDimension())) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("dimension mismatch {0} != {1}", observations.length, scale.getColumnDimension());
		} 
		this.function = function;
		this.observations = observations.clone();
		this.weights = null;
		this.scale = scale.copy();
	}

	public double value(final double[] point) throws org.apache.commons.math.FunctionEvaluationException {
		final double[] residuals = function.value(point);
		if ((residuals.length) != (observations.length)) {
			throw new org.apache.commons.math.FunctionEvaluationException(point , "dimension mismatch {0} != {1}" , residuals.length , observations.length);
		} 
		for (int i = 0 ; i < (residuals.length) ; ++i) {
			residuals[i] -= observations[i];
		}
		double sumSquares = 0;
		if ((weights) != null) {
			for (int i = 0 ; i < (residuals.length) ; ++i) {
				final double ri = residuals[i];
				sumSquares += ((weights[i]) * ri) * ri;
			}
		} else {
			if ((scale) != null) {
				for (final double yi : scale.operate(residuals)) {
					sumSquares += yi * yi;
				}
			} else {
				for (final double ri : residuals) {
					sumSquares += ri * ri;
				}
			}
		}
		return sumSquares;
	}
}

