package org.apache.commons.math.optimization.fitting;


public class HarmonicFunction implements org.apache.commons.math.analysis.DifferentiableUnivariateRealFunction {
	private final double a;

	private final double omega;

	private final double phi;

	public HarmonicFunction(double a ,double omega ,double phi) {
		this.a = a;
		this.omega = omega;
		this.phi = phi;
	}

	public double value(double x) {
		return (a) * (java.lang.Math.cos((((omega) * x) + (phi))));
	}

	public org.apache.commons.math.optimization.fitting.HarmonicFunction derivative() {
		return new org.apache.commons.math.optimization.fitting.HarmonicFunction(((a) * (omega)) , omega , ((phi) + ((java.lang.Math.PI) / 2)));
	}

	public double getAmplitude() {
		return a;
	}

	public double getPulsation() {
		return omega;
	}

	public double getPhase() {
		return phi;
	}
}

