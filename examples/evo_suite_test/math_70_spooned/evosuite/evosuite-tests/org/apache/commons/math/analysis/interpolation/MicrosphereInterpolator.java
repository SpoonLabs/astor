package org.apache.commons.math.analysis.interpolation;


public class MicrosphereInterpolator implements org.apache.commons.math.analysis.interpolation.MultivariateRealInterpolator {
	public static final int DEFAULT_MICROSPHERE_ELEMENTS = 2000;

	public static final int DEFAULT_BRIGHTNESS_EXPONENT = 2;

	private int microsphereElements;

	private int brightnessExponent;

	public MicrosphereInterpolator() {
		this(DEFAULT_MICROSPHERE_ELEMENTS, DEFAULT_BRIGHTNESS_EXPONENT);
	}

	public MicrosphereInterpolator(final int microsphereElements ,final int brightnessExponent) {
		setMicropshereElements(microsphereElements);
		setBrightnessExponent(brightnessExponent);
	}

	public org.apache.commons.math.analysis.MultivariateRealFunction interpolate(final double[][] xval, final double[] yval) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException {
		final org.apache.commons.math.random.UnitSphereRandomVectorGenerator rand = new org.apache.commons.math.random.UnitSphereRandomVectorGenerator(xval[0].length);
		return new org.apache.commons.math.analysis.interpolation.MicrosphereInterpolatingFunction(xval , yval , brightnessExponent , microsphereElements , rand);
	}

	public void setBrightnessExponent(final int brightnessExponent) {
		if (brightnessExponent < 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("brightness exponent should be positive or null, but got {0}", brightnessExponent);
		} 
		this.brightnessExponent = brightnessExponent;
	}

	public void setMicropshereElements(final int elements) {
		if ((microsphereElements) < 0) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("number of microsphere elements must be positive, but got {0}", microsphereElements);
		} 
		this.microsphereElements = elements;
	}
}

