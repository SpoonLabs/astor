package org.apache.commons.math.distribution;


public interface HasDensity<P> {
	double density(P x) throws org.apache.commons.math.MathException;
}

