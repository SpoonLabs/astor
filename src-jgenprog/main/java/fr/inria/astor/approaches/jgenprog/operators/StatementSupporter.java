package fr.inria.astor.approaches.jgenprog.operators;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtExecutable;

/**
 * 
 * @author Matias Martinez
 *
 */
public class StatementSupporter {

	/**
	 * Updates the implicitly of a block. Workarround for Spoon 5.4.0
	 * 
	 * @param block
	 * @param isInsert
	 */
	public static void updateBlockImplicitly(CtBlock block, boolean isInsert) {

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

	public static boolean remove(CtBlock parentBlock, CtStatement fixStatement, int pos) {

		CtStatement s = parentBlock.getStatement(pos);
		// To be sure that the position has the element we
		// want to remove
		if (fixStatement.equals(s)) {
			parentBlock.getStatements().remove(pos);
			return true;
		} else {
			System.out.println("\n fx: " + fixStatement + "\n" + (s));
			throw new IllegalStateException("Undo: Not valid fix position");
		}
	}
}
