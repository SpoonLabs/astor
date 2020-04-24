package fr.inria.astor.approaches.jmutrepair.operators;

import java.util.List;

import fr.inria.astor.approaches.jmutrepair.MutantCtElement;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 * 
 * @author Matias Martinez
 *
 */
@SuppressWarnings("rawtypes")
public class ReturnExpresionMutOp extends ExpresionMutOp {

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {

		return (point.getCodeElement() instanceof CtReturn)
				&& (point.getCodeElement().getElements(new TypeFilter(CtBinaryOperator.class)).size() > 0
						|| point.getCodeElement().getElements(new TypeFilter(CtUnaryOperator.class)).size() > 0);

	}

	protected OperatorInstance createModificationInstance(ModificationPoint point, MutantCtElement fix)
			throws IllegalAccessException {
		CtReturn targetIF = (CtReturn) point.getCodeElement();
		OperatorInstance operation = new OperatorInstance();
		operation.setOriginal(targetIF.getReturnedExpression());
		operation.setOperationApplied(this);
		operation.setModificationPoint(point);
		operation.setModified(fix.getElement());

		return operation;
	}

	/** Return the list of CtElements Mutanted */
	@Override
	public List<MutantCtElement> getMutants(CtElement element) {

		CtReturn targetIF = (CtReturn) element;
		List<MutantCtElement> mutations = null;
		mutations = this.mutatorBinary.execute(targetIF.getReturnedExpression());
		return mutations;
	}

	@Override
	public boolean updateProgramVariant(OperatorInstance opInstance, ProgramVariant p) {
		return false;
	}

}
