package fr.inria.astor.core.loop.spaces.operators;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.loop.population.ProgramVariantFactory;
import spoon.reflect.code.CtStatement;

/**
 * Class that represents a Operator in Astor framework.
 * New Operators cna be defined by extending this class.
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public abstract class AstorOperator {

	protected Logger log = Logger.getLogger(this.getClass().getName());
	
	/**
	 * Method that applies the changes in the model (i.e., the spoon representation of the program) 
	 * according to the operator. 
	 * @param opInstance Instance of the operator to be applied in the model
	 * @param p program variant to modified
	 * @return true if the changes were applied successfully
	 */
	public abstract boolean applyChangesInModel(OperatorInstance opInstance, ProgramVariant p);

	/**
	 * Method that undo the changes applies by this operator.
	 * @param opInstance Instance of the operator to be applied in the model
	 * @param p program variant to modified
	 * @return true if the changes were applied successfully
	 */
	public abstract boolean undoChangesInModel(OperatorInstance opInstance, ProgramVariant p);

	/**
	 * Some operators add or remove modification points from a program variant. for instance, if a oprator removes statement S at moment T, 
	 * then this statement is not available for applying an operation at T+1.
	 * 
	 * @param opInstance
	 * @param p
	 * @return
	 */
	public abstract boolean updateProgramVariant(OperatorInstance opInstance, ProgramVariant p);
	
	/**
	 * Create a modification instance from this operator
	 * @param modificationPoint
	 * @return
	 */
	public List<OperatorInstance> createOperatorInstance(SuspiciousModificationPoint modificationPoint){
		
		List<OperatorInstance> instances = new ArrayList<>();
		OperatorInstance modinst =  new OperatorInstance(modificationPoint, this,modificationPoint.getCodeElement(), null);
		instances.add(modinst);
		return instances;
	}
	/**
	 * Indicates whether the operator can be applied in the ModificationPoint passed as argument.
	 * 
	 * By default, we consider that an operator works at the level of CtStatement.
	 * @param point location to modify
	 * @return
	 */
	public  boolean canBeAppliedToPoint(ModificationPoint point){
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
	protected boolean addPoint(ProgramVariant variant, OperatorInstance operation) {
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
	protected boolean removePoint(ProgramVariant variant, OperatorInstance operation) {
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
