package org.apache.commons.math.util;


public class DefaultTransformer implements java.io.Serializable , org.apache.commons.math.util.NumberTransformer {
	private static final long serialVersionUID = 4019938025047800455L;

	public double transform(java.lang.Object o) throws org.apache.commons.math.MathException {
		if (o == null) {
			throw new org.apache.commons.math.MathException("Conversion Exception in Transformation, Object is null");
		} 
		if (o instanceof java.lang.Number) {
			return ((java.lang.Number)(o)).doubleValue();
		} 
		try {
			return java.lang.Double.valueOf(o.toString()).doubleValue();
		} catch (java.lang.NumberFormatException e) {
			throw new org.apache.commons.math.MathException(e , "Conversion Exception in Transformation: {0}" , e.getMessage());
		}
	}

	@java.lang.Override
	public boolean equals(java.lang.Object other) {
		if ((this) == other) {
			return true;
		} 
		if (other == null) {
			return false;
		} 
		return other instanceof org.apache.commons.math.util.DefaultTransformer;
	}

	@java.lang.Override
	public int hashCode() {
		return 401993047;
	}
}

