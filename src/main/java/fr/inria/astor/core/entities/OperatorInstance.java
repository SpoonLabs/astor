package fr.inria.astor.core.entities;

import org.apache.log4j.Logger;

import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.util.StringUtil;
import spoon.reflect.declaration.CtElement;

/**
 * Operation applied to a particular modification point
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class OperatorInstance {

	protected Logger log = Logger.getLogger(OperatorInstance.class.getName());

	/**
	 * Gen where the operation is applied
	 */
	private ModificationPoint modificationPoint = null;
	/**
	 * Original element where the operation is applied
	 */
	private CtElement original = null;
	/**
	 * New element of the modification
	 */
	private CtElement modified = null;

	/**
	 * Kind of the mutation Operation applied
	 */
	private AstorOperator operator = null;

	/**
	 * if an exception occurres where the operation is applied, we save it
	 */
	private Exception exceptionAtApplied = null;
	private boolean successfulyApplied = true;

	private Ingredient ingredient = null;

	public OperatorInstance() {
	}

	/**
	 * Creates a modification instance
	 * 
	 * @param modificationPoint
	 * @param operationApplied
	 * @param original
	 * @param modified
	 */
	public OperatorInstance(ModificationPoint modificationPoint, AstorOperator operationApplied, CtElement original,
			CtElement modified) {
		super();
		this.modificationPoint = modificationPoint;
		this.operator = operationApplied;
		this.original = original;
		this.modified = modified;
	}

	public CtElement getOriginal() {
		return original;
	}

	public void setOriginal(CtElement original) {
		this.original = original;
	}

	public CtElement getModified() {
		return modified;
	}

	public void setModified(CtElement modified) {
		this.modified = modified;
	}

	public AstorOperator getOperationApplied() {
		return operator;
	}

	public void setOperationApplied(AstorOperator operationApplied) {
		this.operator = operationApplied;
	}

	public Exception getExceptionAtApplied() {
		return exceptionAtApplied;
	}

	public void setExceptionAtApplied(Exception exceptionAtApplied) {
		this.exceptionAtApplied = exceptionAtApplied;
	}

	public String toString() {
		String repst = "OP_INSTANCE:\n";

		repst += this.getOperationApplied() + ":(" + this.original.getClass().getCanonicalName() + ") `"
				+ StringUtil.trunc(this.original) + " ` -topatch--> `" + StringUtil.trunc(modified);

		repst += "` (" + ((this.modified != null) ? this.modified.getClass().getCanonicalName() : "null") + ") ";
		// if (this.original.getPosition() != null &&
		// this.original.getPosition().getFile() != null) {
		// repst += "at l: " + this.original.getPosition().getLine() + " on "
		// + this.original.getPosition().getFile().getAbsolutePath();
		// }
		return repst;
	}

	public ModificationPoint getModificationPoint() {
		return modificationPoint;
	}

	public void setModificationPoint(ModificationPoint modificationPoint) {
		this.modificationPoint = modificationPoint;
	}

	public boolean isSuccessfulyApplied() {
		return successfulyApplied;
	}

	public void setSuccessfulyApplied(boolean successfulyApplied) {
		this.successfulyApplied = successfulyApplied;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((modificationPoint == null) ? 0 : modificationPoint.hashCode());
		result = prime * result + ((modified == null) ? 0 : modified.hashCode());
		result = prime * result + ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + ((original == null) ? 0 : original.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OperatorInstance other = (OperatorInstance) obj;
		if (modificationPoint == null) {
			if (other.modificationPoint != null)
				return false;
		} else if (!modificationPoint.equals(other.modificationPoint))
			return false;
		if (modified == null) {
			if (other.modified != null)
				return false;
		} else if (!modified.equals(other.modified))
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		} else if (!operator.equals(other.operator))
			return false;
		if (original == null) {
			if (other.original != null)
				return false;
		} else if (!original.equals(other.original))
			return false;
		return true;
	}

	public IngredientPoolScope getIngredientScope() {
		return (ingredient != null) ? ingredient.scope : null;
	}

	public boolean applyModification() {
		return operator.applyChangesInModel(this, this.getModificationPoint().getProgramVariant());
	}

	public boolean undoModification() {
		return operator.undoChangesInModel(this, this.getModificationPoint().getProgramVariant());
	}

	public void updateProgramVariant() {
		operator.updateProgramVariant(this, this.getModificationPoint().getProgramVariant());
	}

	public Ingredient getIngredient() {
		return ingredient;
	}

	public void setIngredient(Ingredient ingredient) {
		this.ingredient = ingredient;
	}
}
