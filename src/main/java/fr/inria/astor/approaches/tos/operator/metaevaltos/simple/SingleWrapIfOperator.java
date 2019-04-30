package fr.inria.astor.approaches.tos.operator.metaevaltos.simple;

import fr.inria.astor.approaches.tos.operator.metaevaltos.MetaGenerator;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class SingleWrapIfOperator extends StatementOperatorInstance {

	CtExpression<Boolean> ifcondition = null;
	CtStatement statementToWrap;

	public SingleWrapIfOperator(ModificationPoint modificationPoint, CtExpression<Boolean> ifcondition,
			CtStatement statementToWrap, AstorOperator astoroperator) {
		super();
		this.ifcondition = ifcondition;
		this.statementToWrap = statementToWrap;

		super.setOperationApplied(astoroperator);
		super.setModificationPoint(modificationPoint);

		// defineParentInformation(modificationPoint);
	}

	@Override
	public boolean applyModification() {

		CtStatement original = (CtStatement) MetaGenerator.geOriginalElement(statementToWrap);
		this.setParentBlock(original.getParent(CtBlock.class));
		//
		CtIf ifNew = MutationSupporter.getFactory().createIf();
		ifNew.setParent(original.getParent());
		CtStatement originalCloned = original.clone();
		MutationSupporter.clearPosition(originalCloned);
		ifNew.setThenStatement(originalCloned);

		// as difference with the meta, here we put the ingredient evaluated in the
		// meta.
		ifNew.setCondition((CtExpression<Boolean>) ifcondition);

		//

		super.setOriginal(original);
		super.setModified(ifNew);
		//

		return super.applyModification();
	}

}
