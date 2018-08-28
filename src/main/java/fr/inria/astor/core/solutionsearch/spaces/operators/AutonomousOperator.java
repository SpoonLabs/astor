package fr.inria.astor.core.solutionsearch.spaces.operators;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;

/**
 * 
 * @author Matias Martinez
 *
 */
public abstract class AutonomousOperator extends AstorOperator {

	/**
	 * Create a modification instance from this operator
	 * 
	 * @param modificationPoint
	 * @return
	 */
	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {
		List<OperatorInstance> instances = new ArrayList<>();
		OperatorInstance modinst = this.createOperatorInstance(modificationPoint);
		instances.add(modinst);
		return instances;
	};

	public OperatorInstance createOperatorInstance(ModificationPoint mp) {
		OperatorInstance operation = new OperatorInstance();
		operation.setOriginal(mp.getCodeElement());
		operation.setOperationApplied(this);
		operation.setModificationPoint(mp);
		return operation;
	}

	@Override
	public final boolean needIngredient() {
		return false;
	}

}
