package fr.inria.astor.approaches.jkali.operators;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.jgenprog.operators.InsertBeforeOp;
import fr.inria.astor.approaches.jgenprog.operators.StatementLevelOperator;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.solutionsearch.spaces.operators.AutonomousOperator;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class InsertReturnOp extends AutonomousOperator implements StatementLevelOperator {

	InsertBeforeOp insertOp = new InsertBeforeOp();

	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {
		List<OperatorInstance> instances = new ArrayList<>();

		CtElement createReturn = KaliCodeFactory.createReturn(modificationPoint.getCodeElement());
		if (createReturn != null) {
			OperatorInstance opInsertReturn = new StatementOperatorInstance(modificationPoint, this,
					modificationPoint.getCodeElement(), createReturn);
			instances.add(opInsertReturn);
		}
		return instances;
	}

	@Override
	public boolean applyChangesInModel(OperatorInstance operation, ProgramVariant p) {

		return insertOp.applyChangesInModel(operation, p);

	}

	@Override
	public boolean undoChangesInModel(OperatorInstance opInstance, ProgramVariant p) {
		return insertOp.undoChangesInModel(opInstance, p);
	}

	@Override
	public boolean updateProgramVariant(OperatorInstance opInstance, ProgramVariant p) {
		return insertOp.updateProgramVariant(opInstance, p);
	}

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {

		return point.getCodeElement() instanceof CtStatement && !(point.getCodeElement().toString().startsWith("super")
				|| point.getCodeElement().toString().startsWith("<init>"));
	}

}