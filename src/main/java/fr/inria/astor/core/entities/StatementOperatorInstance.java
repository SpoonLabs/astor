package fr.inria.astor.core.entities;

import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

/**
 * Operation applied to a particular modification point
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class StatementOperatorInstance extends OperatorInstance {

	/**
	 * Parent entity there the mut operation
	 */
	private CtBlock<?> parentBlock = null;

	private boolean isParentBlockImplicit = false;
	/**
	 * Place where the operation is applied in parent
	 */
	private int locationInParent = -1;

	public StatementOperatorInstance() {
		super();
	}

	/**
	 * Creates a modification instance
	 * 
	 * @param modificationPoint
	 * @param operationApplied
	 * @param original
	 * @param modified
	 */
	public StatementOperatorInstance(ModificationPoint modificationPoint, AstorOperator operationApplied,
			CtElement original, CtElement modified) {
		super(modificationPoint, operationApplied, original, modified);

		this.defineParentInformation(modificationPoint);
	}

	public StatementOperatorInstance(ModificationPoint modificationPoint, AstorOperator astorOperator,
			CtElement modified) {
		this(modificationPoint, astorOperator, modificationPoint.getCodeElement(), modified);
	}

	public CtBlock getParentBlock() {
		return parentBlock;
	}

	public void setParentBlock(CtBlock parentBlock) {
		this.parentBlock = parentBlock;
		this.isParentBlockImplicit = parentBlock.isImplicit();
	}

	public int getLocationInParent() {
		return locationInParent;
	}

	public void setLocationInParent(int locationInParent) {
		this.locationInParent = locationInParent;
	}

	public boolean defineParentInformation(ModificationPoint genSusp) {
		CtElement targetStmt = genSusp.getCodeElement();
		CtElement cparent = targetStmt.getParent();
		if ((cparent != null && (cparent instanceof CtBlock))) {
			CtBlock parentBlock = (CtBlock) cparent;
			int location = locationInParent(parentBlock, targetStmt);
			if (location >= 0) {
				this.setParentBlock(parentBlock);
				this.setLocationInParent(location);
				return true;
			}

		} else {
			log.error("Parent null or it is not a block");
		}
		return false;
	}

	/**
	 * Return the position of the element in the block. It searches the same object
	 * instance
	 * 
	 * @param parentBlock
	 * @param line
	 * @param element
	 * @return
	 */
	public int locationInParent(CtBlock parentBlock, CtElement element) {
		int pos = 0;
		for (CtStatement s : parentBlock.getStatements()) {
			if (s == element)
				return pos;
			pos++;
		}

		return -1;

	}

	public boolean isParentBlockImplicit() {
		return isParentBlockImplicit;
	}

	public void setParentBlockImplicit(boolean isParentBlockImplicit) {
		this.isParentBlockImplicit = isParentBlockImplicit;
	}
}
