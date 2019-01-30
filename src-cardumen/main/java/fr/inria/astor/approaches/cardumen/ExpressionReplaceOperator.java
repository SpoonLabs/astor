package fr.inria.astor.approaches.cardumen;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ExpressionReplaceOperator extends ReplaceOp {

	@Override
	public boolean applyChangesInModel(OperatorInstance opInstance, ProgramVariant p) {

		CtExpression elementToModify = (CtExpression) opInstance.getOriginal();
		CtExpression elementOriginalCloned = (CtExpression) MutationSupporter.clone(elementToModify);

		CtElement elFixIngredient = opInstance.getModified();

		// we transform the Spoon model

		try {
			opInstance.getModificationPoint().getCodeElement().replace(elFixIngredient);
		} catch (Exception e) {
			log.error("error to modify " + elementOriginalCloned + " to " + elFixIngredient);
			log.equals(e);
			opInstance.setExceptionAtApplied(e);
			return false;
		}

		// I save the original instance
		opInstance.setOriginal(elementOriginalCloned);
		// Finally, we update the modification point (i.e., Astor
		// Representation)
		opInstance.getModificationPoint().setCodeElement(elFixIngredient);

		boolean change = !opInstance.getModificationPoint().getCodeElement().toString()
				.equals(elementOriginalCloned.toString());

		if (!change)
			log.error("Replacement does not work for  modify " + elementOriginalCloned + " to " + elFixIngredient);

		return true;
	}

	@Override
	public boolean undoChangesInModel(OperatorInstance opInstance, ProgramVariant p) {

		// We update the spoon Model
		opInstance.getModificationPoint().getCodeElement().replace(opInstance.getOriginal());
		// Finally, we update the modification point (i.e., Astor
		// Representation)
		opInstance.getModificationPoint().setCodeElement(opInstance.getOriginal());
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

	@Override
	protected OperatorInstance createOperatorInstance(ModificationPoint mp) {
		OperatorInstance operation = new OperatorInstance(mp, this, mp.getCodeElement(), null);
		return operation;
	}

	@Override
	protected OperatorInstance createOperatorInstance(ModificationPoint mp, Ingredient ingredient) {
		CtElement toModif = mp.getCodeElement();
		CtElement ingredCOde = ingredient.getCode();

		// Or both statement or both not statement
		boolean isStmtToModif = isStatement(toModif);
		boolean isStmtnIngr = isStatement(ingredCOde);
		if (isStmtToModif ^ isStmtnIngr)
			return null;

		return super.createOperatorInstance(mp, ingredient);
	}

	public boolean isStatement(CtElement toModif) {

		if (!(toModif instanceof CtStatement))
			return false;

		if (toModif.getParent() instanceof CtBlock)
			return true;

		CtRole roleInParent = toModif.getRoleInParent();

		if (CtRole.BODY.equals(roleInParent) || CtRole.THEN.equals(roleInParent) || CtRole.ELSE.equals(roleInParent))
			return true;

		return false;
	}

}
