package fr.inria.astor.core.loop.evolutionary.spaces.operators;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ModificationInstance;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.loop.evolutionary.population.ProgramVariantFactory;
import spoon.reflect.code.CtStatement;

/**
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public abstract class AstorOperator {

	protected Logger log = Logger.getLogger(this.getClass().getName());
	

	public abstract boolean applyChangesInModel(ModificationInstance opInstance, ProgramVariant p);

	public abstract boolean undoChangesInModel(ModificationInstance opInstance, ProgramVariant p);

	public abstract boolean updateProgramVariant(ModificationInstance opInstance, ProgramVariant p);
	
	/**
	 * Create a modification instance from this operator
	 * @param modificationPoint
	 * @return
	 */
	public List<ModificationInstance> createModificationInstance(SuspiciousModificationPoint modificationPoint){
		
		List<ModificationInstance> instances = new ArrayList<>();
		ModificationInstance modinst =  new ModificationInstance(modificationPoint, this,modificationPoint.getCodeElement(), null);
		modinst.defineParentInformation(modificationPoint);
		instances.add(modinst);
		//return modinst;
		return instances;
	}
	/**
	 * By default, we consider that an operator works at the level of CtStatement.
	 * @param point
	 * @return
	 */
	public  boolean applyToPoint(ModificationPoint point){
		return (point.getCodeElement() instanceof CtStatement);
	};
	
	/**
	 * Operator name
	 * @return
	 */
	public String name() {
		return this.getClass().getSimpleName();
	};
	
	/**
	 * Indicates whether the operator needs ingredients.
	 * @return
	 */
	public boolean needIngredient() {
		return false;
	}
	/**
	 * Operators modifies the list of ModificationPoints of the program variant.
	 * This method adds a new point corresponding to the modification instance passed as parameter 
	 * @param variant
	 * @param operation
	 * @return
	 */
	protected boolean addPoint(ProgramVariant variant, ModificationInstance operation) {
		List<ModificationPoint> modifPoints = variant.getModificationPoints();

		ModificationPoint existingPoints = operation.getModificationPoint();
		ModificationPoint newPoint = null;
		if (existingPoints instanceof SuspiciousModificationPoint)
			newPoint = ProgramVariantFactory.clonePoint((SuspiciousModificationPoint) existingPoints,
					operation.getModified());
		else
			newPoint = ProgramVariantFactory.clonePoint(existingPoints, operation.getModified());

		log.debug("---" + newPoint);
		log.debug("---" + operation);

		return modifPoints.add(newPoint);
	}
	/**
	 * Operators modifies the list of ModificationPoints of the program variant.
	 * This method removes a new point corresponding to the modification instance passed as parameter 
	 *
	 * @param variant
	 * @param operation
	 * @return
	 */
	protected boolean removePoint(ProgramVariant variant, ModificationInstance operation) {
		List<ModificationPoint> modifPoints = variant.getModificationPoints();
		boolean removed = modifPoints.remove(operation.getModificationPoint());
		return removed;

	}
	/**
	 * Return the name of the op
	 */
	public String toString() {
		return this.name();
	}

}
