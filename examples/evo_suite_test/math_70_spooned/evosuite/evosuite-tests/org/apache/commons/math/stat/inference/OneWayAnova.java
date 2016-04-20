package org.apache.commons.math.stat.inference;


public interface OneWayAnova {
	double anovaFValue(java.util.Collection<double[]> categoryData) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException;

	double anovaPValue(java.util.Collection<double[]> categoryData) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException;

	boolean anovaTest(java.util.Collection<double[]> categoryData, double alpha) throws java.lang.IllegalArgumentException, org.apache.commons.math.MathException;
}

