package fr.inria.astor.test.repair.evaluation.other;

import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.loop.spaces.operators.OperatorSelectionStrategy;
import fr.inria.astor.core.loop.spaces.operators.OperatorSpace;;

/**
 * Fake operator selector strategy. It returns an operator according to the line
 * number of the suspicious point.
 * 
 * @author Matias Martinez
 *
 */
public class FakeOperatorSelectionStrategy extends OperatorSelectionStrategy {

	public FakeOperatorSelectionStrategy(OperatorSpace space) {
		super(space);
	}

	@Override
	public AstorOperator getNextOperator(SuspiciousModificationPoint modificationPoint) {

		if (modificationPoint.getSuspicious().getLineNumber() % 10 == 0)
			return this.operatorSpace.getOperators().get(0);
		else
			return this.operatorSpace.getOperators().get(1);
	}

	@Override
	public AstorOperator getNextOperator() {

		return null;
	}

}
