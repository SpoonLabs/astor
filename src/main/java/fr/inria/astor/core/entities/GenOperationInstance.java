package fr.inria.astor.core.entities;

import fr.inria.astor.core.entities.taxonomy.MutationOperation;
import fr.inria.astor.core.loop.evolutionary.spaces.ingredients.IngredientSpaceStrategy;
import fr.inria.astor.core.util.StringUtil;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtElement;

/**
 * Mutation Operation Applied to a particular gen
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class GenOperationInstance {
	/**
	 * Gen where the operation is applied
	 */
	private Gen gen = null;
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
	private MutationOperation operationApplied = null;
	/**
	 * Parent entity there the mut operation
	 */
	private CtBlock<?> parentBlock = null;

	/**
	 * Place where the operation is applied in parent
	 */
	private int locationInParent = -1;

	/**
	 * if an exception occurres where the operation is applied, we save it
	 */
	private Exception exceptionAtApplied = null;
	private boolean successfulyApplied = true;
	
	private IngredientSpaceStrategy ingredientScope = null;
	
	public GenOperationInstance (){}
	
	public GenOperationInstance(Gen gen, MutationOperation operationApplied, CtElement original, CtElement modified) {
		super();
		this.gen = gen;
		this.operationApplied = operationApplied;
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

	public CtBlock getParentBlock() {
		return parentBlock;
	}

	public void setParentBlock(CtBlock parentBlock) {
		this.parentBlock = parentBlock;
	}

	public MutationOperation getOperationApplied() {
		return operationApplied;
	}

	public void setOperationApplied(MutationOperation operationApplied) {
		this.operationApplied = operationApplied;
	}

	public Exception getExceptionAtApplied() {
		return exceptionAtApplied;
	}

	public void setExceptionAtApplied(Exception exceptionAtApplied) {
		this.exceptionAtApplied = exceptionAtApplied;
	}

	public String toString() {
		return "" + this.getOperationApplied() + ": `" + StringUtil.trunc(this.original) + " ` ---> `" + StringUtil.trunc(modified) +"` at pos "+getLocationInParent()+" of parent `"+ StringUtil.trunc(parentBlock)
				+ "`";
	}

	public Gen getGen() {
		return gen;
	}

	public void setGen(Gen gen) {
		this.gen = gen;
	}

	public int getLocationInParent() {
		return locationInParent;
	}

	public void setLocationInParent(int locationInParent) {
		this.locationInParent = locationInParent;
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
		result = prime * result + ((gen == null) ? 0 : gen.hashCode());
		result = prime * result + ((modified == null) ? 0 : modified.hashCode());
		result = prime * result + ((operationApplied == null) ? 0 : operationApplied.hashCode());
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
		GenOperationInstance other = (GenOperationInstance) obj;
		if (gen == null) {
			if (other.gen != null)
				return false;
		} else if (!gen.equals(other.gen))
			return false;
		if (modified == null) {
			if (other.modified != null)
				return false;
		} else if (!modified.equals(other.modified))
			return false;
		if (operationApplied == null) {
			if (other.operationApplied != null)
				return false;
		} else if (!operationApplied.equals(other.operationApplied))
			return false;
		if (original == null) {
			if (other.original != null)
				return false;
		} else if (!original.equals(other.original))
			return false;
		return true;
	}

	public IngredientSpaceStrategy getIngredientScope() {
		return ingredientScope;
	}

	public void setIngredientScope(IngredientSpaceStrategy ingredientScope) {
		this.ingredientScope = ingredientScope;
	}

	
}
