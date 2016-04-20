package org.apache.commons.math.optimization.linear;


public class LinearObjectiveFunction implements java.io.Serializable {
	private static final long serialVersionUID = -4531815507568396090L;

	private final transient org.apache.commons.math.linear.RealVector coefficients;

	private final double constantTerm;

	public LinearObjectiveFunction(double[] coefficients ,double constantTerm) {
		this(new org.apache.commons.math.linear.ArrayRealVector(coefficients), constantTerm);
	}

	public LinearObjectiveFunction(org.apache.commons.math.linear.RealVector coefficients ,double constantTerm) {
		this.coefficients = coefficients;
		this.constantTerm = constantTerm;
	}

	public org.apache.commons.math.linear.RealVector getCoefficients() {
		return coefficients;
	}

	public double getConstantTerm() {
		return constantTerm;
	}

	public double getValue(final double[] point) {
		return (coefficients.dotProduct(point)) + (constantTerm);
	}

	public double getValue(final org.apache.commons.math.linear.RealVector point) {
		return (coefficients.dotProduct(point)) + (constantTerm);
	}

	@java.lang.Override
	public boolean equals(java.lang.Object other) {
		if ((this) == other) {
			return true;
		} 
		if (other instanceof org.apache.commons.math.optimization.linear.LinearObjectiveFunction) {
			org.apache.commons.math.optimization.linear.LinearObjectiveFunction rhs = ((org.apache.commons.math.optimization.linear.LinearObjectiveFunction)(other));
			return ((constantTerm) == (rhs.constantTerm)) && (coefficients.equals(rhs.coefficients));
		} 
		return false;
	}

	@java.lang.Override
	public int hashCode() {
		return (java.lang.Double.valueOf(constantTerm).hashCode()) ^ (coefficients.hashCode());
	}

	private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
		oos.defaultWriteObject();
		org.apache.commons.math.linear.MatrixUtils.serializeRealVector(coefficients, oos);
	}

	private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
		ois.defaultReadObject();
		org.apache.commons.math.linear.MatrixUtils.deserializeRealVector(this, "coefficients", ois);
	}
}

