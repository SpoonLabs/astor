package org.apache.commons.math.optimization.linear;


public enum Relationship {
EQ("="), LEQ("<="), GEQ(">=");
	private java.lang.String stringValue;
	private Relationship(java.lang.String stringValue) {
		this.stringValue = stringValue;
	}
	@java.lang.Override
	public java.lang.String toString() {
		return stringValue;
	}

	public org.apache.commons.math.optimization.linear.Relationship oppositeRelationship() {
		switch (this) {
			case LEQ :
				return GEQ;
			case GEQ :
				return LEQ;
			default :
				return EQ;
		}
	}
}

