package fr.inria.astor.approaches.mutRepair.operators.ctmutators;

import spoon.reflect.factory.Factory;


/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public abstract class SpoonMutator<T> implements IMutator {
	
	public Factory factory;
	
	
	public SpoonMutator parentMutator  = null;
	
	public SpoonMutator(Factory factory) {
		super();
		this.factory = factory;
	}

	public Factory getFactory() {
		return factory;
	}

	public void setFactory(Factory factory) {
		this.factory = factory;
	}

	public SpoonMutator getParentMutator() {
		return parentMutator;
	}

	public void setParentMutator(SpoonMutator parentMutator) {
		this.parentMutator = parentMutator;
	}

	public abstract int levelMutation();
}
