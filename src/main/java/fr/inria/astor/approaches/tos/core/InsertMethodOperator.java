package fr.inria.astor.approaches.tos.core;

import java.util.List;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

public class InsertMethodOperator extends AstorOperator {

	CtMethod newMethod = null;
	CtClass parentClass = null;

	@Override
	public boolean applyChangesInModel(OperatorInstance opInstance, ProgramVariant p) {
		CtMethod newMethod = (CtMethod) opInstance.getModified();
		parentClass = opInstance.getOriginal().getParent(CtClass.class);
		if (parentClass != null) {
			parentClass.addMethod(newMethod);
			return true;
		}
		return false;
	}

	@Override
	public boolean undoChangesInModel(OperatorInstance opInstance, ProgramVariant p) {
		if (parentClass != null) {
			parentClass.removeMethod(newMethod);
			return true;
		}

		return false;
	}

	@Override
	public boolean updateProgramVariant(OperatorInstance opInstance, ProgramVariant p) {
		return false;
	}

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {

		return true;
	}

	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {
		return null;
	}

}
