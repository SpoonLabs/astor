package fr.inria.astor.approaches.tos.operator.metaevaltos.simple;

import fr.inria.astor.approaches.tos.operator.metaevaltos.MetaGenerator;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class SingleUnWrapIfOperator extends StatementOperatorInstance {

	CtIf ifToRemove;
	CtStatement stmtinsideThen;

	public SingleUnWrapIfOperator(ModificationPoint modificationPoint, CtIf ifToRemove, CtStatement stmtinsideThen,
			AstorOperator astoroperator) {
		super();
		this.ifToRemove = ifToRemove;
		this.stmtinsideThen = stmtinsideThen;

		super.setOperationApplied(astoroperator);
		super.setModificationPoint(modificationPoint);

	}

	@Override
	public boolean applyModification() {

		CtStatement stmtinsideThenCloned = getBlock(ifToRemove.getThenStatement()).clone();

		stmtinsideThenCloned.setParent(null);
		MutationSupporter.clearPosition(stmtinsideThenCloned);

		CtStatement original = (CtStatement) MetaGenerator.geOriginalElement(ifToRemove);
		this.setParentBlock(original.getParent(CtBlock.class));

		super.setOriginal(original);
		super.setModified(stmtinsideThenCloned);

		return super.applyModification();
	}

	private CtStatement getBlock(CtStatement thenStatement) {
		if (thenStatement instanceof CtBlock) {
			return getBlock(((CtBlock) thenStatement).getStatement(0));
		}

		return thenStatement;
	}

}
