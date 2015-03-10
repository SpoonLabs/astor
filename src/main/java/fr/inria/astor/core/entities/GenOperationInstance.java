package fr.inria.astor.core.entities;

import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtElement;
import fr.inria.astor.core.entities.taxonomy.MutationOperation;
import fr.inria.astor.core.util.StringUtil;

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

}
