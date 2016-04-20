package org.apache.commons.math.analysis.interpolation;


public class TricubicSplineInterpolatingFunction implements org.apache.commons.math.analysis.TrivariateRealFunction {
	private static final double[][] AINV = new double[][]{ new double[]{ 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ -3 , 3 , 0 , 0 , 0 , 0 , 0 , 0 , -2 , -1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 2 , -2 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -3 , 3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -2 , -1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , -2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ -3 , 0 , 3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -2 , 0 , -1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -3 , 0 , 3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -2 , 0 , -1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 9 , -9 , -9 , 9 , 0 , 0 , 0 , 0 , 6 , 3 , -6 , -3 , 0 , 0 , 0 , 0 , 6 , -6 , 3 , -3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 4 , 2 , 2 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ -6 , 6 , 6 , -6 , 0 , 0 , 0 , 0 , -3 , -3 , 3 , 3 , 0 , 0 , 0 , 0 , -4 , 4 , -2 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -2 , -2 , -1 , -1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 2 , 0 , -2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 0 , -2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ -6 , 6 , 6 , -6 , 0 , 0 , 0 , 0 , -4 , -2 , 4 , 2 , 0 , 0 , 0 , 0 , -3 , 3 , -3 , 3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -2 , -1 , -2 , -1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 4 , -4 , -4 , 4 , 0 , 0 , 0 , 0 , 2 , 2 , -2 , -2 , 0 , 0 , 0 , 0 , 2 , -2 , 2 , -2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 1 , 1 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -3 , 3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -2 , -1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , -2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -3 , 3 , 0 , 0 , 0 , 0 , 0 , 0 , -2 , -1 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , -2 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 1 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -3 , 0 , 3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -2 , 0 , -1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -3 , 0 , 3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -2 , 0 , -1 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 9 , -9 , -9 , 9 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 6 , 3 , -6 , -3 , 0 , 0 , 0 , 0 , 6 , -6 , 3 , -3 , 0 , 0 , 0 , 0 , 4 , 2 , 2 , 1 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -6 , 6 , 6 , -6 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -3 , -3 , 3 , 3 , 0 , 0 , 0 , 0 , -4 , 4 , -2 , 2 , 0 , 0 , 0 , 0 , -2 , -2 , -1 , -1 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 0 , -2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 0 , -2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 1 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -6 , 6 , 6 , -6 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -4 , -2 , 4 , 2 , 0 , 0 , 0 , 0 , -3 , 3 , -3 , 3 , 0 , 0 , 0 , 0 , -2 , -1 , -2 , -1 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 4 , -4 , -4 , 4 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 2 , -2 , -2 , 0 , 0 , 0 , 0 , 2 , -2 , 2 , -2 , 0 , 0 , 0 , 0 , 1 , 1 , 1 , 1 , 0 , 0 , 0 , 0 } , new double[]{ -3 , 0 , 0 , 0 , 3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -2 , 0 , 0 , 0 , -1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -3 , 0 , 0 , 0 , 3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -2 , 0 , 0 , 0 , -1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 9 , -9 , 0 , 0 , -9 , 9 , 0 , 0 , 6 , 3 , 0 , 0 , -6 , -3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 6 , -6 , 0 , 0 , 3 , -3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 4 , 2 , 0 , 0 , 2 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ -6 , 6 , 0 , 0 , 6 , -6 , 0 , 0 , -3 , -3 , 0 , 0 , 3 , 3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -4 , 4 , 0 , 0 , -2 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -2 , -2 , 0 , 0 , -1 , -1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -3 , 0 , 0 , 0 , 3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -2 , 0 , 0 , 0 , -1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -3 , 0 , 0 , 0 , 3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -2 , 0 , 0 , 0 , -1 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 9 , -9 , 0 , 0 , -9 , 9 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 6 , 3 , 0 , 0 , -6 , -3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 6 , -6 , 0 , 0 , 3 , -3 , 0 , 0 , 4 , 2 , 0 , 0 , 2 , 1 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -6 , 6 , 0 , 0 , 6 , -6 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -3 , -3 , 0 , 0 , 3 , 3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -4 , 4 , 0 , 0 , -2 , 2 , 0 , 0 , -2 , -2 , 0 , 0 , -1 , -1 , 0 , 0 } , new double[]{ 9 , 0 , -9 , 0 , -9 , 0 , 9 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 6 , 0 , 3 , 0 , -6 , 0 , -3 , 0 , 6 , 0 , -6 , 0 , 3 , 0 , -3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 4 , 0 , 2 , 0 , 2 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 9 , 0 , -9 , 0 , -9 , 0 , 9 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 6 , 0 , 3 , 0 , -6 , 0 , -3 , 0 , 6 , 0 , -6 , 0 , 3 , 0 , -3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 4 , 0 , 2 , 0 , 2 , 0 , 1 , 0 } , new double[]{ -27 , 27 , 27 , -27 , 27 , -27 , -27 , 27 , -18 , -9 , 18 , 9 , 18 , 9 , -18 , -9 , -18 , 18 , -9 , 9 , 18 , -18 , 9 , -9 , -18 , 18 , 18 , -18 , -9 , 9 , 9 , -9 , -12 , -6 , -6 , -3 , 12 , 6 , 6 , 3 , -12 , -6 , 12 , 6 , -6 , -3 , 6 , 3 , -12 , 12 , -6 , 6 , -6 , 6 , -3 , 3 , -8 , -4 , -4 , -2 , -4 , -2 , -2 , -1 } , new double[]{ 18 , -18 , -18 , 18 , -18 , 18 , 18 , -18 , 9 , 9 , -9 , -9 , -9 , -9 , 9 , 9 , 12 , -12 , 6 , -6 , -12 , 12 , -6 , 6 , 12 , -12 , -12 , 12 , 6 , -6 , -6 , 6 , 6 , 6 , 3 , 3 , -6 , -6 , -3 , -3 , 6 , 6 , -6 , -6 , 3 , 3 , -3 , -3 , 8 , -8 , 4 , -4 , 4 , -4 , 2 , -2 , 4 , 4 , 2 , 2 , 2 , 2 , 1 , 1 } , new double[]{ -6 , 0 , 6 , 0 , 6 , 0 , -6 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -3 , 0 , -3 , 0 , 3 , 0 , 3 , 0 , -4 , 0 , 4 , 0 , -2 , 0 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -2 , 0 , -2 , 0 , -1 , 0 , -1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -6 , 0 , 6 , 0 , 6 , 0 , -6 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -3 , 0 , -3 , 0 , 3 , 0 , 3 , 0 , -4 , 0 , 4 , 0 , -2 , 0 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -2 , 0 , -2 , 0 , -1 , 0 , -1 , 0 } , new double[]{ 18 , -18 , -18 , 18 , -18 , 18 , 18 , -18 , 12 , 6 , -12 , -6 , -12 , -6 , 12 , 6 , 9 , -9 , 9 , -9 , -9 , 9 , -9 , 9 , 12 , -12 , -12 , 12 , 6 , -6 , -6 , 6 , 6 , 3 , 6 , 3 , -6 , -3 , -6 , -3 , 8 , 4 , -8 , -4 , 4 , 2 , -4 , -2 , 6 , -6 , 6 , -6 , 3 , -3 , 3 , -3 , 4 , 2 , 4 , 2 , 2 , 1 , 2 , 1 } , new double[]{ -12 , 12 , 12 , -12 , 12 , -12 , -12 , 12 , -6 , -6 , 6 , 6 , 6 , 6 , -6 , -6 , -6 , 6 , -6 , 6 , 6 , -6 , 6 , -6 , -8 , 8 , 8 , -8 , -4 , 4 , 4 , -4 , -3 , -3 , -3 , -3 , 3 , 3 , 3 , 3 , -4 , -4 , 4 , 4 , -2 , -2 , 2 , 2 , -4 , 4 , -4 , 4 , -2 , 2 , -2 , 2 , -2 , -2 , -2 , -2 , -1 , -1 , -1 , -1 } , new double[]{ 2 , 0 , 0 , 0 , -2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 0 , 0 , 0 , -2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ -6 , 6 , 0 , 0 , 6 , -6 , 0 , 0 , -4 , -2 , 0 , 0 , 4 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -3 , 3 , 0 , 0 , -3 , 3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -2 , -1 , 0 , 0 , -2 , -1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 4 , -4 , 0 , 0 , -4 , 4 , 0 , 0 , 2 , 2 , 0 , 0 , -2 , -2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , -2 , 0 , 0 , 2 , -2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 1 , 0 , 0 , 1 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 0 , 0 , 0 , -2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 0 , 0 , 0 , -2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 1 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -6 , 6 , 0 , 0 , 6 , -6 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -4 , -2 , 0 , 0 , 4 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -3 , 3 , 0 , 0 , -3 , 3 , 0 , 0 , -2 , -1 , 0 , 0 , -2 , -1 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 4 , -4 , 0 , 0 , -4 , 4 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 2 , 0 , 0 , -2 , -2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , -2 , 0 , 0 , 2 , -2 , 0 , 0 , 1 , 1 , 0 , 0 , 1 , 1 , 0 , 0 } , new double[]{ -6 , 0 , 6 , 0 , 6 , 0 , -6 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -4 , 0 , -2 , 0 , 4 , 0 , 2 , 0 , -3 , 0 , 3 , 0 , -3 , 0 , 3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -2 , 0 , -1 , 0 , -2 , 0 , -1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -6 , 0 , 6 , 0 , 6 , 0 , -6 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -4 , 0 , -2 , 0 , 4 , 0 , 2 , 0 , -3 , 0 , 3 , 0 , -3 , 0 , 3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , -2 , 0 , -1 , 0 , -2 , 0 , -1 , 0 } , new double[]{ 18 , -18 , -18 , 18 , -18 , 18 , 18 , -18 , 12 , 6 , -12 , -6 , -12 , -6 , 12 , 6 , 12 , -12 , 6 , -6 , -12 , 12 , -6 , 6 , 9 , -9 , -9 , 9 , 9 , -9 , -9 , 9 , 8 , 4 , 4 , 2 , -8 , -4 , -4 , -2 , 6 , 3 , -6 , -3 , 6 , 3 , -6 , -3 , 6 , -6 , 3 , -3 , 6 , -6 , 3 , -3 , 4 , 2 , 2 , 1 , 4 , 2 , 2 , 1 } , new double[]{ -12 , 12 , 12 , -12 , 12 , -12 , -12 , 12 , -6 , -6 , 6 , 6 , 6 , 6 , -6 , -6 , -8 , 8 , -4 , 4 , 8 , -8 , 4 , -4 , -6 , 6 , 6 , -6 , -6 , 6 , 6 , -6 , -4 , -4 , -2 , -2 , 4 , 4 , 2 , 2 , -3 , -3 , 3 , 3 , -3 , -3 , 3 , 3 , -4 , 4 , -2 , 2 , -4 , 4 , -2 , 2 , -2 , -2 , -1 , -1 , -2 , -2 , -1 , -1 } , new double[]{ 4 , 0 , -4 , 0 , -4 , 0 , 4 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 0 , 2 , 0 , -2 , 0 , -2 , 0 , 2 , 0 , -2 , 0 , 2 , 0 , -2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 1 , 0 , 1 , 0 , 1 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 } , new double[]{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 4 , 0 , -4 , 0 , -4 , 0 , 4 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 0 , 2 , 0 , -2 , 0 , -2 , 0 , 2 , 0 , -2 , 0 , 2 , 0 , -2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 1 , 0 , 1 , 0 , 1 , 0 , 1 , 0 } , new double[]{ -12 , 12 , 12 , -12 , 12 , -12 , -12 , 12 , -8 , -4 , 8 , 4 , 8 , 4 , -8 , -4 , -6 , 6 , -6 , 6 , 6 , -6 , 6 , -6 , -6 , 6 , 6 , -6 , -6 , 6 , 6 , -6 , -4 , -2 , -4 , -2 , 4 , 2 , 4 , 2 , -4 , -2 , 4 , 2 , -4 , -2 , 4 , 2 , -3 , 3 , -3 , 3 , -3 , 3 , -3 , 3 , -2 , -1 , -2 , -1 , -2 , -1 , -2 , -1 } , new double[]{ 8 , -8 , -8 , 8 , -8 , 8 , 8 , -8 , 4 , 4 , -4 , -4 , -4 , -4 , 4 , 4 , 4 , -4 , 4 , -4 , -4 , 4 , -4 , 4 , 4 , -4 , -4 , 4 , 4 , -4 , -4 , 4 , 2 , 2 , 2 , 2 , -2 , -2 , -2 , -2 , 2 , 2 , -2 , -2 , 2 , 2 , -2 , -2 , 2 , -2 , 2 , -2 , 2 , -2 , 2 , -2 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 } };

	private final double[] xval;

	private final double[] yval;

	private final double[] zval;

	private final org.apache.commons.math.analysis.interpolation.TricubicSplineFunction[][][] splines;

	public TricubicSplineInterpolatingFunction(double[] x ,double[] y ,double[] z ,double[][][] f ,double[][][] dFdX ,double[][][] dFdY ,double[][][] dFdZ ,double[][][] d2FdXdY ,double[][][] d2FdXdZ ,double[][][] d2FdYdZ ,double[][][] d3FdXdYdZ) throws org.apache.commons.math.DimensionMismatchException {
		final int xLen = x.length;
		final int yLen = y.length;
		final int zLen = z.length;
		if (((((xLen == 0) || (yLen == 0)) || ((z.length) == 0)) || ((f.length) == 0)) || ((f[0].length) == 0)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("no data");
		} 
		if (xLen != (f.length)) {
			throw new org.apache.commons.math.DimensionMismatchException(xLen , f.length);
		} 
		if (xLen != (dFdX.length)) {
			throw new org.apache.commons.math.DimensionMismatchException(xLen , dFdX.length);
		} 
		if (xLen != (dFdY.length)) {
			throw new org.apache.commons.math.DimensionMismatchException(xLen , dFdY.length);
		} 
		if (xLen != (dFdZ.length)) {
			throw new org.apache.commons.math.DimensionMismatchException(xLen , dFdZ.length);
		} 
		if (xLen != (d2FdXdY.length)) {
			throw new org.apache.commons.math.DimensionMismatchException(xLen , d2FdXdY.length);
		} 
		if (xLen != (d2FdXdZ.length)) {
			throw new org.apache.commons.math.DimensionMismatchException(xLen , d2FdXdZ.length);
		} 
		if (xLen != (d2FdYdZ.length)) {
			throw new org.apache.commons.math.DimensionMismatchException(xLen , d2FdYdZ.length);
		} 
		if (xLen != (d3FdXdYdZ.length)) {
			throw new org.apache.commons.math.DimensionMismatchException(xLen , d3FdXdYdZ.length);
		} 
		org.apache.commons.math.util.MathUtils.checkOrder(x, 1, true);
		org.apache.commons.math.util.MathUtils.checkOrder(y, 1, true);
		org.apache.commons.math.util.MathUtils.checkOrder(z, 1, true);
		xval = x.clone();
		yval = y.clone();
		zval = z.clone();
		final int lastI = xLen - 1;
		final int lastJ = yLen - 1;
		final int lastK = zLen - 1;
		splines = new org.apache.commons.math.analysis.interpolation.TricubicSplineFunction[lastI][lastJ][lastK];
		for (int i = 0 ; i < lastI ; i++) {
			if ((f[i].length) != yLen) {
				throw new org.apache.commons.math.DimensionMismatchException(f[i].length , yLen);
			} 
			if ((dFdX[i].length) != yLen) {
				throw new org.apache.commons.math.DimensionMismatchException(dFdX[i].length , yLen);
			} 
			if ((dFdY[i].length) != yLen) {
				throw new org.apache.commons.math.DimensionMismatchException(dFdY[i].length , yLen);
			} 
			if ((dFdZ[i].length) != yLen) {
				throw new org.apache.commons.math.DimensionMismatchException(dFdZ[i].length , yLen);
			} 
			if ((d2FdXdY[i].length) != yLen) {
				throw new org.apache.commons.math.DimensionMismatchException(d2FdXdY[i].length , yLen);
			} 
			if ((d2FdXdZ[i].length) != yLen) {
				throw new org.apache.commons.math.DimensionMismatchException(d2FdXdZ[i].length , yLen);
			} 
			if ((d2FdYdZ[i].length) != yLen) {
				throw new org.apache.commons.math.DimensionMismatchException(d2FdYdZ[i].length , yLen);
			} 
			if ((d3FdXdYdZ[i].length) != yLen) {
				throw new org.apache.commons.math.DimensionMismatchException(d3FdXdYdZ[i].length , yLen);
			} 
			final int ip1 = i + 1;
			for (int j = 0 ; j < lastJ ; j++) {
				if ((f[i][j].length) != zLen) {
					throw new org.apache.commons.math.DimensionMismatchException(f[i][j].length , zLen);
				} 
				if ((dFdX[i][j].length) != zLen) {
					throw new org.apache.commons.math.DimensionMismatchException(dFdX[i][j].length , zLen);
				} 
				if ((dFdY[i][j].length) != zLen) {
					throw new org.apache.commons.math.DimensionMismatchException(dFdY[i][j].length , zLen);
				} 
				if ((dFdZ[i][j].length) != zLen) {
					throw new org.apache.commons.math.DimensionMismatchException(dFdZ[i][j].length , zLen);
				} 
				if ((d2FdXdY[i][j].length) != zLen) {
					throw new org.apache.commons.math.DimensionMismatchException(d2FdXdY[i][j].length , zLen);
				} 
				if ((d2FdXdZ[i][j].length) != zLen) {
					throw new org.apache.commons.math.DimensionMismatchException(d2FdXdZ[i][j].length , zLen);
				} 
				if ((d2FdYdZ[i][j].length) != zLen) {
					throw new org.apache.commons.math.DimensionMismatchException(d2FdYdZ[i][j].length , zLen);
				} 
				if ((d3FdXdYdZ[i][j].length) != zLen) {
					throw new org.apache.commons.math.DimensionMismatchException(d3FdXdYdZ[i][j].length , zLen);
				} 
				final int jp1 = j + 1;
				for (int k = 0 ; k < lastK ; k++) {
					final int kp1 = k + 1;
					final double[] beta = new double[]{ f[i][j][k] , f[ip1][j][k] , f[i][jp1][k] , f[ip1][jp1][k] , f[i][j][kp1] , f[ip1][j][kp1] , f[i][jp1][kp1] , f[ip1][jp1][kp1] , dFdX[i][j][k] , dFdX[ip1][j][k] , dFdX[i][jp1][k] , dFdX[ip1][jp1][k] , dFdX[i][j][kp1] , dFdX[ip1][j][kp1] , dFdX[i][jp1][kp1] , dFdX[ip1][jp1][kp1] , dFdY[i][j][k] , dFdY[ip1][j][k] , dFdY[i][jp1][k] , dFdY[ip1][jp1][k] , dFdY[i][j][kp1] , dFdY[ip1][j][kp1] , dFdY[i][jp1][kp1] , dFdY[ip1][jp1][kp1] , dFdZ[i][j][k] , dFdZ[ip1][j][k] , dFdZ[i][jp1][k] , dFdZ[ip1][jp1][k] , dFdZ[i][j][kp1] , dFdZ[ip1][j][kp1] , dFdZ[i][jp1][kp1] , dFdZ[ip1][jp1][kp1] , d2FdXdY[i][j][k] , d2FdXdY[ip1][j][k] , d2FdXdY[i][jp1][k] , d2FdXdY[ip1][jp1][k] , d2FdXdY[i][j][kp1] , d2FdXdY[ip1][j][kp1] , d2FdXdY[i][jp1][kp1] , d2FdXdY[ip1][jp1][kp1] , d2FdXdZ[i][j][k] , d2FdXdZ[ip1][j][k] , d2FdXdZ[i][jp1][k] , d2FdXdZ[ip1][jp1][k] , d2FdXdZ[i][j][kp1] , d2FdXdZ[ip1][j][kp1] , d2FdXdZ[i][jp1][kp1] , d2FdXdZ[ip1][jp1][kp1] , d2FdYdZ[i][j][k] , d2FdYdZ[ip1][j][k] , d2FdYdZ[i][jp1][k] , d2FdYdZ[ip1][jp1][k] , d2FdYdZ[i][j][kp1] , d2FdYdZ[ip1][j][kp1] , d2FdYdZ[i][jp1][kp1] , d2FdYdZ[ip1][jp1][kp1] , d3FdXdYdZ[i][j][k] , d3FdXdYdZ[ip1][j][k] , d3FdXdYdZ[i][jp1][k] , d3FdXdYdZ[ip1][jp1][k] , d3FdXdYdZ[i][j][kp1] , d3FdXdYdZ[ip1][j][kp1] , d3FdXdYdZ[i][jp1][kp1] , d3FdXdYdZ[ip1][jp1][kp1] };
					splines[i][j][k] = new org.apache.commons.math.analysis.interpolation.TricubicSplineFunction(computeSplineCoefficients(beta));
				}
			}
		}
	}

	public double value(double x, double y, double z) {
		final int i = searchIndex(x, xval);
		if (i == (-1)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0} out of [{1}, {2}] range", x, xval[0], xval[((xval.length) - 1)]);
		} 
		final int j = searchIndex(y, yval);
		if (j == (-1)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0} out of [{1}, {2}] range", y, yval[0], yval[((yval.length) - 1)]);
		} 
		final int k = searchIndex(z, zval);
		if (k == (-1)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0} out of [{1}, {2}] range", z, zval[0], zval[((zval.length) - 1)]);
		} 
		final double xN = (x - (xval[i])) / ((xval[(i + 1)]) - (xval[i]));
		final double yN = (y - (yval[j])) / ((yval[(j + 1)]) - (yval[j]));
		final double zN = (z - (zval[k])) / ((zval[(k + 1)]) - (zval[k]));
		return splines[i][j][k].value(xN, yN, zN);
	}

	private int searchIndex(double c, double[] val) {
		if (c < (val[0])) {
			return -1;
		} 
		final int max = val.length;
		for (int i = 1 ; i < max ; i++) {
			if (c <= (val[i])) {
				return i - 1;
			} 
		}
		return -1;
	}

	private double[] computeSplineCoefficients(double[] beta) {
		final int sz = 64;
		final double[] a = new double[sz];
		for (int i = 0 ; i < sz ; i++) {
			double result = 0;
			final double[] row = AINV[i];
			for (int j = 0 ; j < sz ; j++) {
				result += (row[j]) * (beta[j]);
			}
			a[i] = result;
		}
		return a;
	}
}

class TricubicSplineFunction implements org.apache.commons.math.analysis.TrivariateRealFunction {
	private static final short N = 4;

	private static final short N2 = (N) * (N);

	private final double[][][] a = new double[N][N][N];

	public TricubicSplineFunction(double[] aV) {
		for (int i = 0 ; i < (N) ; i++) {
			for (int j = 0 ; j < (N) ; j++) {
				for (int k = 0 ; k < (N) ; k++) {
					a[i][j][k] = aV[((i + ((N) * j)) + ((N2) * k))];
				}
			}
		}
	}

	public double value(double x, double y, double z) {
		if ((x < 0) || (x > 1)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0} out of [{1}, {2}] range", x, 0, 1);
		} 
		if ((y < 0) || (y > 1)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0} out of [{1}, {2}] range", y, 0, 1);
		} 
		if ((z < 0) || (z > 1)) {
			throw org.apache.commons.math.MathRuntimeException.createIllegalArgumentException("{0} out of [{1}, {2}] range", z, 0, 1);
		} 
		final double x2 = x * x;
		final double x3 = x2 * x;
		final double[] pX = new double[]{ 1 , x , x2 , x3 };
		final double y2 = y * y;
		final double y3 = y2 * y;
		final double[] pY = new double[]{ 1 , y , y2 , y3 };
		final double z2 = z * z;
		final double z3 = z2 * z;
		final double[] pZ = new double[]{ 1 , z , z2 , z3 };
		double result = 0;
		for (int i = 0 ; i < (N) ; i++) {
			for (int j = 0 ; j < (N) ; j++) {
				for (int k = 0 ; k < (N) ; k++) {
					result += (((a[i][j][k]) * (pX[i])) * (pY[j])) * (pZ[k]);
				}
			}
		}
		return result;
	}
}

