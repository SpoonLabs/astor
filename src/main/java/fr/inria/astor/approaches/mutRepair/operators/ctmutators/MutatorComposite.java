package fr.inria.astor.approaches.mutRepair.operators.ctmutators;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;

/**
 * This mutator executes a list of mutant operations.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 * @param <CtExpression>
 */
public class MutatorComposite<CtExpression> extends SpoonMutator {
	/**
	 * List of mutators called by this one.
	 */
	List<SpoonMutator> mutators = new ArrayList<SpoonMutator>();

	/**
	 * Level of complexity of a mutator.
	 * 
	 */
	protected int level = Integer.MAX_VALUE;
	
	/**
	 * Levels of binary operations analyzed by the composite.
	 */
//	protected int depth = 0;

	/**
	 * 
	 * @param factory
	 */

	public MutatorComposite(Factory factory) {
		super(factory);

	}

	@Override
	public List<CtExpression> execute(CtElement toMutate) {
		List<CtExpression> result = new ArrayList<CtExpression>();
		for (SpoonMutator mut : mutators) {
			if (mut.levelMutation() <= this.levelMutation()) {
				mut.setParentMutator(this);
				result.addAll(mut.execute(toMutate));
			}
		}
		return result;
	}

	@Override
	public String key() {
		return null;
	}

	@Override
	public void setup() {

	}

	public List<SpoonMutator> getMutators() {
		return mutators;
	}

	public void setMutators(List<SpoonMutator> mutators) {
		this.mutators = mutators;
	}

	@Override
	public int levelMutation() {

		return level;
	}

}
