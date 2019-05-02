package fr.inria.astor.approaches.tos.operator.metaevaltos.simple;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.tos.operator.metaevaltos.MetaGenerator;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;
import spoon.support.reflect.code.CtBlockImpl;

/**
 * 
 * @author Matias Martinez
 *
 */
public class SingleTryOperator extends StatementOperatorInstance {

	CtStatement statementToWrap;

	public SingleTryOperator(ModificationPoint modificationPoint, CtStatement statementToWrap,
			AstorOperator astoroperator) {
		super();

		this.statementToWrap = statementToWrap;

		super.setOperationApplied(astoroperator);
		super.setModificationPoint(modificationPoint);

	}

	@Override
	public boolean applyModification() {

		CtStatement original = (CtStatement) MetaGenerator.geOriginalElement(statementToWrap);
		this.setParentBlock(original.getParent(CtBlock.class));

		CtTry tryNew = MutationSupporter.getFactory().createTry();
		List<CtCatch> catchers = new ArrayList<>();
		CtCatch catchEx1 = MutationSupporter.getFactory().createCtCatch("e", Exception.class, new CtBlockImpl());
		catchers.add(catchEx1);
		tryNew.setCatchers(catchers);
		CtBlock tryBoddy = new CtBlockImpl();
		tryNew.setBody(tryBoddy);

		CtStatement stmtC = statementToWrap.clone();

		MutationSupporter.clearPosition(stmtC);
		tryBoddy.addStatement(stmtC);

		//
		super.setOriginal(original);
		super.setModified(tryNew);
		//

		return super.applyModification();
	}

}
