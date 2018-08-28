package fr.inria.astor.approaches.jgenprog.operators;

import fr.inria.astor.core.entities.ModificationPoint;
import spoon.reflect.code.CtStatement;

/**
 * Operator that affects to one statement S and the parent of S is a block
 * 
 * @author Matias Martinez
 *
 */
public interface StatementLevelOperator {

	public default boolean canBeAppliedToPoint(ModificationPoint point) {

		return (point.getCodeElement() instanceof CtStatement);
	}

}
