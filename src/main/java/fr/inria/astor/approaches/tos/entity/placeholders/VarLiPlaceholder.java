package fr.inria.astor.approaches.tos.entity.placeholders;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.tos.core.PatchGenerator;
import fr.inria.astor.approaches.tos.entity.transf.Transformation;
import fr.inria.astor.core.entities.ModificationPoint;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtVariableAccess;

/**
 * 
 * @author Matias Martinez
 *
 */
public class VarLiPlaceholder extends Placeholder {

	@SuppressWarnings("rawtypes")
	CtVariableAccess affectedVariable = null;
	String placeholder_name = null;
	String previousname = null;

	public VarLiPlaceholder(CtVariableAccess previousVariable, String name) {
		super();
		this.affectedVariable = previousVariable;
		this.placeholder_name = name;
	}

	@Override
	public void apply() {
		// previousVariable.replace(newLiteral);
		previousname = affectedVariable.getVariable().getSimpleName();
		affectedVariable.getVariable().setSimpleName(placeholder_name);
	
		//Workarround 
		affectedVariable.getFactory().getEnvironment().setNoClasspath(true);
		if (affectedVariable instanceof CtFieldAccess) {
			CtFieldAccess fieldAccess = (CtFieldAccess) affectedVariable;
			fieldAccess.getVariable().setDeclaringType(null);

		}
	}

	@Override
	public void revert() {
		// newLiteral.replace(previousVariable);
		affectedVariable.getVariable().setSimpleName(previousname);
		previousname = null;
	}

	@Override
	public List<CtCodeElement> getAffectedElements() {
		List<CtCodeElement> ces = new ArrayList<>();
		ces.add(affectedVariable);
		return ces;
	}

	@Override
	public List<Transformation> visit(ModificationPoint modificationPoint, PatchGenerator visitor) {
		return visitor.process(modificationPoint, this);
	}

	public CtVariableAccess getAffectedVariable() {
		return affectedVariable;
	}
	
	public String toString(){
		return this.getClass().getSimpleName()+ ": "+placeholder_name + " --> "+affectedVariable;
	}

	public String getPlaceholder_name() {
		return placeholder_name;
	}

	public void setPlaceholder_name(String placeholder_name) {
		this.placeholder_name = placeholder_name;
	}
}
