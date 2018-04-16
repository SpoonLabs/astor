package fr.inria.astor.approaches.tos.entity.placeholders;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.tos.core.PatchGenerator;
import fr.inria.astor.approaches.tos.entity.transf.Transformation;
import fr.inria.astor.core.entities.ModificationPoint;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * 
 * @author Matias Martinez
 *
 */
public class InvocationPlaceholder extends Placeholder {

	protected String newName;
	protected CtAbstractInvocation invocation;
	protected CtTypeReference target;
	protected CtTypeReference type;
	protected String oldName;

	public InvocationPlaceholder() {
	}

	public InvocationPlaceholder(String name, CtAbstractInvocation invocation, CtTypeReference target,
			CtTypeReference type) {
		super();
		this.newName = name;
		this.invocation = invocation;
		this.target = target;
		this.type = type;
	}

	public String getName() {
		return newName;
	}

	public void setName(String name) {
		this.newName = name;
	}

	public CtAbstractInvocation getInvocation() {
		return invocation;
	}

	public void setInvocation(CtAbstractInvocation invocation) {
		this.invocation = invocation;
	}

	public CtTypeReference getTarget() {
		return target;
	}

	public void setTarget(CtTypeReference target) {
		this.target = target;
	}

	public CtTypeReference getType() {
		return type;
	}

	public void setType(CtTypeReference type) {
		this.type = type;
	}

	@Override
	public void apply() {
		CtExecutableReference execr = this.getInvocation().getExecutable();
		this.oldName = execr.getSimpleName();
		execr.setSimpleName(newName);

	}

	@Override
	public void revert() {
		CtExecutableReference execr = this.getInvocation().getExecutable();
		execr.setSimpleName(oldName);

	}

	@Override
	public List<CtCodeElement> getAffectedElements() {
		List<CtCodeElement> affected = new ArrayList<>();
		// TODO: cast...
		affected.add((CtCodeElement) invocation);
		return affected;
	}

	@Override
	public List<Transformation> visit(ModificationPoint modificationPoint, PatchGenerator visitor) {

		return visitor.process(modificationPoint, this);
	}

	public String toString() {
		return this.getClass().getSimpleName() + ": " + this.newName + " --> "
				+ invocation.getExecutable().getSimpleName();
	}
}