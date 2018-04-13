package fr.inria.astor.approaches.tos.entity.placeholders;

import java.util.List;

import fr.inria.astor.approaches.tos.core.PatchGenerator;
import fr.inria.astor.approaches.tos.entity.TOSEntity;
import fr.inria.astor.approaches.tos.entity.transf.Transformation;
import fr.inria.astor.core.entities.ModificationPoint;
import spoon.reflect.code.CtCodeElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public abstract class Placeholder {

	TOSEntity tos;

	public TOSEntity getTos() {
		return tos;
	}

	public void setTos(TOSEntity tos) {
		this.tos = tos;
	}

	public abstract void apply();

	public abstract void revert();

	public abstract List<CtCodeElement> getAffectedElements();

	public abstract List<Transformation> visit(ModificationPoint modificationPoint, PatchGenerator visitor);

	public String toString(){
		return this.getClass().getSimpleName()+ ": "+getAffectedElements();
	}
}
