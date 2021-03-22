package fr.inria.astor.approaches.cardumen;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.approaches.tos.core.MetaGenerator;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class FineGrainedExpressionReplaceOperator extends ReplaceOp {

	CtElement originalParent = null;

	@SuppressWarnings("rawtypes")
	@Override
	public boolean applyChangesInModel(OperatorInstance opInstance, ProgramVariant p) {

		CtExpression elementToModify = (CtExpression) opInstance.getOriginal();
		CtExpression elementOriginalCloned = (CtExpression) MutationSupporter.clone(elementToModify);

		CtElement elFixIngredient = opInstance.getModified();

		MetaGenerator.getSourceTarget().put(elementToModify, elFixIngredient);

		// MetaGenerator.targetSource.put(elementToModify, elFixIngredient);

		this.originalParent = elementToModify.getParent();
		// we transform the Spoon model
		try {
			elementToModify.replace(elFixIngredient);
		} catch (Exception e) {
			log.error("error to modify " + elementOriginalCloned + " to " + elFixIngredient);
			log.error(e);
			e.printStackTrace();
			opInstance.setExceptionAtApplied(e);
			return false;
		}
		opInstance.setOriginal(elementToModify);

		boolean change = !opInstance.getModificationPoint().getCodeElement().toString()
				.equals(elementOriginalCloned.toString());

		if (!change)
			log.error("Replacement does not work for  modify " + elementOriginalCloned + " to " + elFixIngredient);

		return true;
	}

	@Override
	public boolean undoChangesInModel(OperatorInstance opInstance, ProgramVariant p) {

		opInstance.getModified().setParent(this.originalParent);
		// We update the spoon Model
		opInstance.getModified().replace(opInstance.getOriginal());
		// Finally, we update the modification point (i.e., Astor
		// Representation)
		return true;
	}

	@Override
	public boolean updateProgramVariant(OperatorInstance opInstance, ProgramVariant p) {
		// We dont need to update the variant here
		return false;
	}

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {

		return (point.getCodeElement() instanceof CtExpression);
	}

}
