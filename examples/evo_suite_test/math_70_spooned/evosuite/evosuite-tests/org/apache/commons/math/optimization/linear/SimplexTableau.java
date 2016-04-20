package org.apache.commons.math.optimization.linear;


class SimplexTableau implements java.io.Serializable {
	private static final java.lang.String NEGATIVE_VAR_COLUMN_LABEL = "x-";

	private static final long serialVersionUID = -1369660067587938365L;

	private final org.apache.commons.math.optimization.linear.LinearObjectiveFunction f;

	private final java.util.List<org.apache.commons.math.optimization.linear.LinearConstraint> constraints;

	private final boolean restrictToNonNegative;

	private final java.util.List<java.lang.String> columnLabels = new java.util.ArrayList<java.lang.String>();

	private transient org.apache.commons.math.linear.RealMatrix tableau;

	private final int numDecisionVariables;

	private final int numSlackVariables;

	private int numArtificialVariables;

	private final double epsilon;

	SimplexTableau(final org.apache.commons.math.optimization.linear.LinearObjectiveFunction f ,final java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> constraints ,final org.apache.commons.math.optimization.GoalType goalType ,final boolean restrictToNonNegative ,final double epsilon) {
		this.f = f;
		this.constraints = normalizeConstraints(constraints);
		this.restrictToNonNegative = restrictToNonNegative;
		this.epsilon = epsilon;
		this.numDecisionVariables = (f.getCoefficients().getDimension()) + (restrictToNonNegative ? 0 : 1);
		this.numSlackVariables = (getConstraintTypeCounts(org.apache.commons.math.optimization.linear.Relationship.LEQ)) + (getConstraintTypeCounts(org.apache.commons.math.optimization.linear.Relationship.GEQ));
		this.numArtificialVariables = (getConstraintTypeCounts(org.apache.commons.math.optimization.linear.Relationship.EQ)) + (getConstraintTypeCounts(org.apache.commons.math.optimization.linear.Relationship.GEQ));
		this.tableau = createTableau((goalType == (org.apache.commons.math.optimization.GoalType.MAXIMIZE)));
		initializeColumnLabels();
	}

	protected void initializeColumnLabels() {
		if ((getNumObjectiveFunctions()) == 2) {
			columnLabels.add("W");
		} 
		columnLabels.add("Z");
		for (int i = 0 ; i < (getOriginalNumDecisionVariables()) ; i++) {
			columnLabels.add(("x" + i));
		}
		if (!(restrictToNonNegative)) {
			columnLabels.add(NEGATIVE_VAR_COLUMN_LABEL);
		} 
		for (int i = 0 ; i < (getNumSlackVariables()) ; i++) {
			columnLabels.add(("s" + i));
		}
		for (int i = 0 ; i < (getNumArtificialVariables()) ; i++) {
			columnLabels.add(("a" + i));
		}
		columnLabels.add("RHS");
	}

	protected org.apache.commons.math.linear.RealMatrix createTableau(final boolean maximize) {
		int width = ((((numDecisionVariables) + (numSlackVariables)) + (numArtificialVariables)) + (getNumObjectiveFunctions())) + 1;
		int height = (constraints.size()) + (getNumObjectiveFunctions());
		org.apache.commons.math.linear.Array2DRowRealMatrix matrix = new org.apache.commons.math.linear.Array2DRowRealMatrix(height , width);
		if ((getNumObjectiveFunctions()) == 2) {
			matrix.setEntry(0, 0, -1);
		} 
		int zIndex = (getNumObjectiveFunctions()) == 1 ? 0 : 1;
		matrix.setEntry(zIndex, zIndex, (maximize ? 1 : -1));
		org.apache.commons.math.linear.RealVector objectiveCoefficients = maximize ? f.getCoefficients().mapMultiply(-1) : f.getCoefficients();
		copyArray(objectiveCoefficients.getData(), matrix.getDataRef()[zIndex]);
		matrix.setEntry(zIndex, (width - 1), (maximize ? f.getConstantTerm() : (-1) * (f.getConstantTerm())));
		if (!(restrictToNonNegative)) {
			matrix.setEntry(zIndex, ((getSlackVariableOffset()) - 1), org.apache.commons.math.optimization.linear.SimplexTableau.getInvertedCoeffiecientSum(objectiveCoefficients));
		} 
		int slackVar = 0;
		int artificialVar = 0;
		for (int i = 0 ; i < (constraints.size()) ; i++) {
			org.apache.commons.math.optimization.linear.LinearConstraint constraint = constraints.get(i);
			int row = (getNumObjectiveFunctions()) + i;
			copyArray(constraint.getCoefficients().getData(), matrix.getDataRef()[row]);
			if (!(restrictToNonNegative)) {
				matrix.setEntry(row, ((getSlackVariableOffset()) - 1), org.apache.commons.math.optimization.linear.SimplexTableau.getInvertedCoeffiecientSum(constraint.getCoefficients()));
			} 
			matrix.setEntry(row, (width - 1), constraint.getValue());
			if ((constraint.getRelationship()) == (org.apache.commons.math.optimization.linear.Relationship.LEQ)) {
				matrix.setEntry(row, ((getSlackVariableOffset()) + (slackVar++)), 1);
			} else {
				if ((constraint.getRelationship()) == (org.apache.commons.math.optimization.linear.Relationship.GEQ)) {
					matrix.setEntry(row, ((getSlackVariableOffset()) + (slackVar++)), -1);
				} 
			}
			if (((constraint.getRelationship()) == (org.apache.commons.math.optimization.linear.Relationship.EQ)) || ((constraint.getRelationship()) == (org.apache.commons.math.optimization.linear.Relationship.GEQ))) {
				matrix.setEntry(0, ((getArtificialVariableOffset()) + artificialVar), 1);
				matrix.setEntry(row, ((getArtificialVariableOffset()) + (artificialVar++)), 1);
				matrix.setRowVector(0, matrix.getRowVector(0).subtract(matrix.getRowVector(row)));
			} 
		}
		return matrix;
	}

	public java.util.List<org.apache.commons.math.optimization.linear.LinearConstraint> normalizeConstraints(java.util.Collection<org.apache.commons.math.optimization.linear.LinearConstraint> originalConstraints) {
		java.util.List<org.apache.commons.math.optimization.linear.LinearConstraint> normalized = new java.util.ArrayList<org.apache.commons.math.optimization.linear.LinearConstraint>();
		for (org.apache.commons.math.optimization.linear.LinearConstraint constraint : originalConstraints) {
			normalized.add(normalize(constraint));
		}
		return normalized;
	}

	private org.apache.commons.math.optimization.linear.LinearConstraint normalize(final org.apache.commons.math.optimization.linear.LinearConstraint constraint) {
		if ((constraint.getValue()) < 0) {
			return new org.apache.commons.math.optimization.linear.LinearConstraint(constraint.getCoefficients().mapMultiply(-1) , constraint.getRelationship().oppositeRelationship() , ((-1) * (constraint.getValue())));
		} 
		return new org.apache.commons.math.optimization.linear.LinearConstraint(constraint.getCoefficients() , constraint.getRelationship() , constraint.getValue());
	}

	protected final int getNumObjectiveFunctions() {
		return (this.numArtificialVariables) > 0 ? 2 : 1;
	}

	private int getConstraintTypeCounts(final org.apache.commons.math.optimization.linear.Relationship relationship) {
		int count = 0;
		for (final org.apache.commons.math.optimization.linear.LinearConstraint constraint : constraints) {
			if ((constraint.getRelationship()) == relationship) {
				++count;
			} 
		}
		return count;
	}

	protected static double getInvertedCoeffiecientSum(final org.apache.commons.math.linear.RealVector coefficients) {
		double sum = 0;
		for (double coefficient : coefficients.getData()) {
			sum -= coefficient;
		}
		return sum;
	}

	protected java.lang.Integer getBasicRow(final int col) {
		java.lang.Integer row = null;
		for (int i = 0 ; i < (getHeight()) ; i++) {
			if ((org.apache.commons.math.util.MathUtils.equals(getEntry(i, col), 1.0, epsilon)) && (row == null)) {
				row = i;
			} else {
				if (!(org.apache.commons.math.util.MathUtils.equals(getEntry(i, col), 0.0, epsilon))) {
					return null;
				} 
			}
		}
		return row;
	}

	protected void dropPhase1Objective() {
		if ((getNumObjectiveFunctions()) == 1) {
			return ;
		} 
		java.util.List<java.lang.Integer> columnsToDrop = new java.util.ArrayList<java.lang.Integer>();
		columnsToDrop.add(0);
		for (int i = getNumObjectiveFunctions() ; i < (getArtificialVariableOffset()) ; i++) {
			if ((org.apache.commons.math.util.MathUtils.compareTo(tableau.getEntry(0, i), 0, epsilon)) > 0) {
				columnsToDrop.add(i);
			} 
		}
		for (int i = 0 ; i < (getNumArtificialVariables()) ; i++) {
			int col = i + (getArtificialVariableOffset());
			if ((getBasicRow(col)) == null) {
				columnsToDrop.add(col);
			} 
		}
		double[][] matrix = new double[(getHeight()) - 1][(getWidth()) - (columnsToDrop.size())];
		for (int i = 1 ; i < (getHeight()) ; i++) {
			int col = 0;
			for (int j = 0 ; j < (getWidth()) ; j++) {
				if (!(columnsToDrop.contains(j))) {
					matrix[(i - 1)][col++] = tableau.getEntry(i, j);
				} 
			}
		}
		for (int i = (columnsToDrop.size()) - 1 ; i >= 0 ; i--) {
			columnLabels.remove(((int)(columnsToDrop.get(i))));
		}
		this.tableau = new org.apache.commons.math.linear.Array2DRowRealMatrix(matrix);
		this.numArtificialVariables = 0;
	}

	private void copyArray(final double[] src, final double[] dest) {
		java.lang.System.arraycopy(src, 0, dest, getNumObjectiveFunctions(), src.length);
	}

	boolean isOptimal() {
		for (int i = getNumObjectiveFunctions() ; i < ((getWidth()) - 1) ; i++) {
			if ((org.apache.commons.math.util.MathUtils.compareTo(tableau.getEntry(0, i), 0, epsilon)) < 0) {
				return false;
			} 
		}
		return true;
	}

	protected org.apache.commons.math.optimization.RealPointValuePair getSolution() {
		int negativeVarColumn = columnLabels.indexOf(NEGATIVE_VAR_COLUMN_LABEL);
		java.lang.Integer negativeVarBasicRow = negativeVarColumn > 0 ? getBasicRow(negativeVarColumn) : null;
		double mostNegative = negativeVarBasicRow == null ? 0 : getEntry(negativeVarBasicRow, getRhsOffset());
		java.util.Set<java.lang.Integer> basicRows = new java.util.HashSet<java.lang.Integer>();
		double[] coefficients = new double[getOriginalNumDecisionVariables()];
		for (int i = 0 ; i < (coefficients.length) ; i++) {
			int colIndex = columnLabels.indexOf(("x" + i));
			if (colIndex < 0) {
				coefficients[i] = 0;
				continue;
			} 
			java.lang.Integer basicRow = getBasicRow(colIndex);
			if (basicRows.contains(basicRow)) {
				coefficients[i] = 0;
			} else {
				basicRows.add(basicRow);
				coefficients[i] = (basicRow == null ? 0 : getEntry(basicRow, getRhsOffset())) - (restrictToNonNegative ? 0 : mostNegative);
			}
		}
		return new org.apache.commons.math.optimization.RealPointValuePair(coefficients , f.getValue(coefficients));
	}

	protected void divideRow(final int dividendRow, final double divisor) {
		for (int j = 0 ; j < (getWidth()) ; j++) {
			tableau.setEntry(dividendRow, j, ((tableau.getEntry(dividendRow, j)) / divisor));
		}
	}

	protected void subtractRow(final int minuendRow, final int subtrahendRow, final double multiple) {
		tableau.setRowVector(minuendRow, tableau.getRowVector(minuendRow).subtract(tableau.getRowVector(subtrahendRow).mapMultiply(multiple)));
	}

	protected final int getWidth() {
		return tableau.getColumnDimension();
	}

	protected final int getHeight() {
		return tableau.getRowDimension();
	}

	protected final double getEntry(final int row, final int column) {
		return tableau.getEntry(row, column);
	}

	protected final void setEntry(final int row, final int column, final double value) {
		tableau.setEntry(row, column, value);
	}

	protected final int getSlackVariableOffset() {
		return (getNumObjectiveFunctions()) + (numDecisionVariables);
	}

	protected final int getArtificialVariableOffset() {
		return ((getNumObjectiveFunctions()) + (numDecisionVariables)) + (numSlackVariables);
	}

	protected final int getRhsOffset() {
		return (getWidth()) - 1;
	}

	protected final int getNumDecisionVariables() {
		return numDecisionVariables;
	}

	protected final int getOriginalNumDecisionVariables() {
		return f.getCoefficients().getDimension();
	}

	protected final int getNumSlackVariables() {
		return numSlackVariables;
	}

	protected final int getNumArtificialVariables() {
		return numArtificialVariables;
	}

	protected final double[][] getData() {
		return tableau.getData();
	}

	@java.lang.Override
	public boolean equals(java.lang.Object other) {
		if ((this) == other) {
			return true;
		} 
		if (other instanceof org.apache.commons.math.optimization.linear.SimplexTableau) {
			org.apache.commons.math.optimization.linear.SimplexTableau rhs = ((org.apache.commons.math.optimization.linear.SimplexTableau)(other));
			return ((((((((restrictToNonNegative) == (rhs.restrictToNonNegative)) && ((numDecisionVariables) == (rhs.numDecisionVariables))) && ((numSlackVariables) == (rhs.numSlackVariables))) && ((numArtificialVariables) == (rhs.numArtificialVariables))) && ((epsilon) == (rhs.epsilon))) && (f.equals(rhs.f))) && (constraints.equals(rhs.constraints))) && (tableau.equals(rhs.tableau));
		} 
		return false;
	}

	@java.lang.Override
	public int hashCode() {
		return (((((((java.lang.Boolean.valueOf(restrictToNonNegative).hashCode()) ^ (numDecisionVariables)) ^ (numSlackVariables)) ^ (numArtificialVariables)) ^ (java.lang.Double.valueOf(epsilon).hashCode())) ^ (f.hashCode())) ^ (constraints.hashCode())) ^ (tableau.hashCode());
	}

	private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
		oos.defaultWriteObject();
		org.apache.commons.math.linear.MatrixUtils.serializeRealMatrix(tableau, oos);
	}

	private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
		ois.defaultReadObject();
		org.apache.commons.math.linear.MatrixUtils.deserializeRealMatrix(this, "tableau", ois);
	}
}

