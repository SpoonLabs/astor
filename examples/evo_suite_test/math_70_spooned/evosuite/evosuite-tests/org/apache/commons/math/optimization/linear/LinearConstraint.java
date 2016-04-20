package org.apache.commons.math.optimization.linear;


public class LinearConstraint implements java.io.Serializable {
	private static final long serialVersionUID = -764632794033034092L;

	private final transient org.apache.commons.math.linear.RealVector coefficients;

	private final org.apache.commons.math.optimization.linear.Relationship relationship;

	private final double value;

	public LinearConstraint(final double[] coefficients ,final org.apache.commons.math.optimization.linear.Relationship relationship ,final double value) {
		this(new org.apache.commons.math.linear.ArrayRealVector(coefficients), relationship, value);
	}

	public LinearConstraint(final org.apache.commons.math.linear.RealVector coefficients ,final org.apache.commons.math.optimization.linear.Relationship relationship ,final double value) {
		this.coefficients = coefficients;
		this.relationship = relationship;
		this.value = value;
	}

	public LinearConstraint(final double[] lhsCoefficients ,final double lhsConstant ,final org.apache.commons.math.optimization.linear.Relationship relationship ,final double[] rhsCoefficients ,final double rhsConstant) {
		double[] sub = new double[lhsCoefficients.length];
		for (int i = 0 ; i < (sub.length) ; ++i) {
			sub[i] = (lhsCoefficients[i]) - (rhsCoefficients[i]);
		}
		this.coefficients = new org.apache.commons.math.linear.ArrayRealVector(sub , false);
		this.relationship = relationship;
		this.value = rhsConstant - lhsConstant;
	}

	public LinearConstraint(final org.apache.commons.math.linear.RealVector lhsCoefficients ,final double lhsConstant ,final org.apache.commons.math.optimization.linear.Relationship relationship ,final org.apache.commons.math.linear.RealVector rhsCoefficients ,final double rhsConstant) {
		this.coefficients = lhsCoefficients.subtract(rhsCoefficients);
		this.relationship = relationship;
		this.value = rhsConstant - lhsConstant;
	}

	public org.apache.commons.math.linear.RealVector getCoefficients() {
		return coefficients;
	}

	public org.apache.commons.math.optimization.linear.Relationship getRelationship() {
		return relationship;
	}

	public double getValue() {
		return value;
	}

	@java.lang.Override
	public boolean equals(java.lang.Object other) {
		if ((this) == other) {
			return true;
		} 
		if (other instanceof org.apache.commons.math.optimization.linear.LinearConstraint) {
			org.apache.commons.math.optimization.linear.LinearConstraint rhs = ((org.apache.commons.math.optimization.linear.LinearConstraint)(other));
			return (((relationship) == (rhs.relationship)) && ((value) == (rhs.value))) && (coefficients.equals(rhs.coefficients));
		} 
		return false;
	}

	@java.lang.Override
	public int hashCode() {
		return ((relationship.hashCode()) ^ (java.lang.Double.valueOf(value).hashCode())) ^ (coefficients.hashCode());
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

