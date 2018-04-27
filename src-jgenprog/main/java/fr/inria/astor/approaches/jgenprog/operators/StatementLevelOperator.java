package fr.inria.astor.approaches.jgenprog.operators;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtExecutable;

/**
 * Operator that affects to one statement S and the parent of S is a block
 * 
 * @author Matias Martinez
 *
 */
public abstract class StatementLevelOperator extends AstorOperator {

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {

		return (point.getCodeElement() instanceof CtStatement);
	}

	/**
	 * Updates the implicitly of a block. Workarround for Spoon 5.4.0
	 * @param block
	 * @param isInsert
	 */
	public void updateBlockImplicitly(CtBlock block, boolean isInsert) {

		if (!block.isImplicit() && block.getStatements().size() == 1 && !(block.getParent() instanceof CtExecutable)) {
			block.setImplicit(true);
		} else {
			if (isInsert) {
				if (block.isImplicit() && block.getStatements().size() > 1) {
					block.setImplicit(false);
				}
			} else {// Delete
				if (block.isImplicit() && block.getStatements().size() == 0) {
					block.setImplicit(false);
				}
			}
		}
	}

}
