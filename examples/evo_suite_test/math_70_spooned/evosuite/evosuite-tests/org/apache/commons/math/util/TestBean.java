package org.apache.commons.math.util;


public class TestBean {
	private java.lang.Double x = java.lang.Double.valueOf(1.0);

	private java.lang.String y = "1.0";

	public java.lang.Double getX() {
		return x;
	}

	public java.lang.String getY() {
		return y;
	}

	public void setX(java.lang.Double double1) {
		x = double1;
	}

	public void setY(java.lang.String string) {
		y = string;
	}

	public java.lang.Double getZ() {
		throw new org.apache.commons.math.MathRuntimeException("?");
	}

	public void setZ(java.lang.Double double1) {
	}
}

