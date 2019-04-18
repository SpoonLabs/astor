package fr.inria.astor.core.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Matias Martinez
 *
 */
public class CompositeOperatorInstance extends OperatorInstance {

	List<OperatorInstance> instances = new ArrayList<>();

	public CompositeOperatorInstance() {
		super();
	}

	public CompositeOperatorInstance(List<OperatorInstance> instances) {
		super();
		this.instances = instances;
	}

	@Override
	public boolean applyModification() {
		boolean applied = true;
		for (OperatorInstance operatorInstance : instances) {
			applied &= operatorInstance.applyModification();
		}
		return applied;
	}

	@Override
	public boolean undoModification() {
		boolean applied = true;
		for (OperatorInstance operatorInstance : instances) {
			applied &= operatorInstance.undoModification();
		}
		return applied;
	}

	public void setOperatorInstances(List<OperatorInstance> instances) {
		this.instances = instances;
	}

}
