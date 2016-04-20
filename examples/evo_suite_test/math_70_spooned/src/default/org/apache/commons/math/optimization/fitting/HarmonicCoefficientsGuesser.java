package org.apache.commons.math.optimization.fitting;


public class HarmonicCoefficientsGuesser {
	private final org.apache.commons.math.optimization.fitting.WeightedObservedPoint[] observations;

	private double a;

	private double omega;

	private double phi;

	public HarmonicCoefficientsGuesser(org.apache.commons.math.optimization.fitting.WeightedObservedPoint[] observations) {
		this.observations = observations.clone();
		a = java.lang.Double.NaN;
		omega = java.lang.Double.NaN;
	}

	public void guess() throws org.apache.commons.math.optimization.OptimizationException {
		sortObservations();
		guessAOmega();
		guessPhi();
	}

	private void sortObservations() {
		org.apache.commons.math.optimization.fitting.WeightedObservedPoint curr = observations[0];
		for (int j = 1 ; j < (observations.length) ; ++j) {
			org.apache.commons.math.optimization.fitting.WeightedObservedPoint prec = curr;
			curr = observations[j];
			if ((curr.getX()) < (prec.getX())) {
				int i = j - 1;
				org.apache.commons.math.optimization.fitting.WeightedObservedPoint mI = observations[i];
				while ((i >= 0) && ((curr.getX()) < (mI.getX()))) {
					observations[(i + 1)] = mI;
					if ((i--) != 0) {
						mI = observations[i];
					} else {
						mI = null;
					}
				}
				observations[(i + 1)] = curr;
				curr = observations[j];
			} 
		}
	}

	private void guessAOmega() throws org.apache.commons.math.optimization.OptimizationException {
		double sx2 = 0.0;
		double sy2 = 0.0;
		double sxy = 0.0;
		double sxz = 0.0;
		double syz = 0.0;
		double currentX = observations[0].getX();
		double currentY = observations[0].getY();
		double f2Integral = 0;
		double fPrime2Integral = 0;
		final double startX = currentX;
		for (int i = 1 ; i < (observations.length) ; ++i) {
			final double previousX = currentX;
			final double previousY = currentY;
			currentX = observations[i].getX();
			currentY = observations[i].getY();
			final double dx = currentX - previousX;
			final double dy = currentY - previousY;
			final double f2StepIntegral = (dx * (((previousY * previousY) + (previousY * currentY)) + (currentY * currentY))) / 3;
			final double fPrime2StepIntegral = (dy * dy) / dx;
			final double x = currentX - startX;
			f2Integral += f2StepIntegral;
			fPrime2Integral += fPrime2StepIntegral;
			sx2 += x * x;
			sy2 += f2Integral * f2Integral;
			sxy += x * f2Integral;
			sxz += x * fPrime2Integral;
			syz += f2Integral * fPrime2Integral;
		}
		double c1 = (sy2 * sxz) - (sxy * syz);
		double c2 = (sxy * sxz) - (sx2 * syz);
		double c3 = (sx2 * sy2) - (sxy * sxy);
		if (((c1 / c2) < 0.0) || ((c2 / c3) < 0.0)) {
			throw new org.apache.commons.math.optimization.OptimizationException("unable to first guess the harmonic coefficients");
		} 
		a = java.lang.Math.sqrt((c1 / c2));
		omega = java.lang.Math.sqrt((c2 / c3));
	}

	private void guessPhi() {
		double fcMean = 0.0;
		double fsMean = 0.0;
		double currentX = observations[0].getX();
		double currentY = observations[0].getY();
		for (int i = 1 ; i < (observations.length) ; ++i) {
			final double previousX = currentX;
			final double previousY = currentY;
			currentX = observations[i].getX();
			currentY = observations[i].getY();
			final double currentYPrime = (currentY - previousY) / (currentX - previousX);
			double omegaX = (omega) * currentX;
			double cosine = java.lang.Math.cos(omegaX);
			double sine = java.lang.Math.sin(omegaX);
			fcMean += (((omega) * currentY) * cosine) - (currentYPrime * sine);
			fsMean += (((omega) * currentY) * sine) + (currentYPrime * cosine);
		}
		phi = java.lang.Math.atan2(-fsMean, fcMean);
	}

	public double getGuessedAmplitude() {
		return a;
	}

	public double getGuessedPulsation() {
		return omega;
	}

	public double getGuessedPhase() {
		return phi;
	}
}

