package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.List;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.meta.MetaOperator;
import fr.inria.astor.core.entities.meta.MetaOperatorInstance;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;

public class MethodCallReplacementByAnotherMethodCallOp extends AstorOperator implements MetaOperator {

	@Override
	public boolean applyChangesInModel(OperatorInstance opInstance, ProgramVariant p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean undoChangesInModel(OperatorInstance opInstance, ProgramVariant p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateProgramVariant(OperatorInstance opInstance, ProgramVariant p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperatorInstance getConcreteOperatorInstance(MetaOperatorInstance operatorInstance, int metaIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

}
