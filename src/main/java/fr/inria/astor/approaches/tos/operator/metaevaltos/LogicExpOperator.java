package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.cardumen.FineGrainedExpressionReplaceOperator;
import fr.inria.astor.approaches.tos.operator.DynaIngredientOperator;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.IngredientFromDyna;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.entities.meta.MetaOperator;
import fr.inria.astor.core.entities.meta.MetaOperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtBinaryOperatorImpl;

/**
 * 
 * @author Matias Martinez
 *
 */
public class LogicExpOperator extends FineGrainedExpressionReplaceOperator
		implements MetaOperator, DynaIngredientOperator {

	public BinaryOperatorKind operatorKind = BinaryOperatorKind.OR;

	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {
		log.error("This op needs ingredients");
		return null;
	}

	@Override
	public List<MetaOperatorInstance> createMetaOperatorInstances(ModificationPoint modificationPoint) {
		log.error("This op needs ingredients");
		return null;
	}

	@Override
	public CtTypeReference retrieveTargetTypeReference() {
		return MutationSupporter.getFactory().createCtTypeReference(Boolean.class);
	}

	@Override
	public List<MetaOperatorInstance> createMetaOperatorInstances(ModificationPoint modificationPoint,
			List<IngredientFromDyna> ingredientsDynamoth) {

		List<MetaOperatorInstance> opsMega = new ArrayList();

		if (ingredientsDynamoth.isEmpty()) {
			// Nothing to replace
			return opsMega;
		}

		// get all boolean expressions
		List<CtExpression<Boolean>> booleanExpressionsInModificationPoints = modificationPoint.getCodeElement()
				.getElements(e -> e.getType() != null && e.getType().unbox().getSimpleName().equals("boolean"));

		log.debug("\nLogicExp: \n" + modificationPoint);

		// let's start with one, and let's keep the Zero for the default (all ifs are
		// false)

		// As difference with var replacement, a metamutant for each expression
		for (CtExpression<Boolean> expressionToExpand : booleanExpressionsInModificationPoints) {
			// List<OperatorInstance> opsOfVariant = new ArrayList();

			int variableCounter = 0;

			// The return type of the new method correspond to the type of variable to
			// change

			List<Ingredient> ingredients = this.computeIngredientsFromExpressionExplansion(modificationPoint,
					expressionToExpand, ingredientsDynamoth, this.operatorKind);

			// The parameters to be included in the new method
			List<CtVariableAccess> varsToBeParameters = SupportOperators
					.collectAllVarsFromDynaIngredients(ingredientsDynamoth, modificationPoint);

			// The variable from the existing expression must also be a parameter
			List<CtVariableAccess> varsFromExpression = modificationPoint.getCodeElement()
					.getElements(e -> e instanceof CtVariableAccess);
			for (CtVariableAccess ctVariableAccess : varsFromExpression) {
				if (!varsToBeParameters.contains(ctVariableAccess))
					varsToBeParameters.add(ctVariableAccess);

			}

			// List of parameters
			List<CtParameter<?>> parameters = new ArrayList<>();
			List<CtExpression<?>> realParameters = new ArrayList<>();
			for (CtVariableAccess ctVariableAccess : varsToBeParameters) {
				// the parent is null, it is setter latter
				CtParameter pari = MutationSupporter.getFactory().createParameter(null, ctVariableAccess.getType(),
						ctVariableAccess.getVariable().getSimpleName());
				parameters.add(pari);
				realParameters.add(ctVariableAccess.clone().setPositions(new NoSourcePosition()));
			}

			variableCounter++;
			CtTypeReference returnType = MutationSupporter.getFactory().createCtTypeReference(Boolean.class);

			MetaOperatorInstance megaOp = MetaGenerator.createMeta(modificationPoint, expressionToExpand,
					variableCounter, ingredients, parameters, realParameters, this, returnType);
			opsMega.add(megaOp);

		} // End variable

		return opsMega;
	}

	private List<Ingredient> computeIngredientsFromExpressionExplansion(ModificationPoint modificationPoint,
			CtExpression previousExpression, List<IngredientFromDyna> ingredientsDynamoth,
			BinaryOperatorKind operatorKind2) {

		List<Ingredient> ingredientsNewBinaryExpressions = new ArrayList();

		for (IngredientFromDyna ingredientFromDyna : ingredientsDynamoth) {

			CtBinaryOperator binaryOperator = new CtBinaryOperatorImpl<>();
			binaryOperator.setKind(operatorKind2);
			CtExpression previousExpressionCloned = previousExpression.clone();
			MutationSupporter.clearPosition(previousExpressionCloned);
			binaryOperator.setLeftHandOperand(previousExpressionCloned);

			CtExpression newRightExpression = MutationSupporter.getFactory()
					.createCodeSnippetExpression(ingredientFromDyna.getDynmothExpression().toString());
			binaryOperator.setRightHandOperand(newRightExpression);

			//
			binaryOperator.setFactory(MutationSupporter.getFactory());
			binaryOperator.setParent(previousExpression.getParent());

			Ingredient newIngredientExtended = new Ingredient(binaryOperator);
			newIngredientExtended.setDerivedFrom(previousExpression);
			ingredientsNewBinaryExpressions.add(newIngredientExtended);
		}

		return ingredientsNewBinaryExpressions;
	}

	@Override
	public OperatorInstance getConcreteOperatorInstance(MetaOperatorInstance operatorInstance, int metaIdentifier) {

		Ingredient ingredient = operatorInstance.getAllIngredients().get(metaIdentifier);

		ModificationPoint modificationPoint = operatorInstance.getModificationPoint();

		CtExpression expressionSource = (CtExpression) ingredient.getDerivedFrom();
		CtExpression expressionTarget = (CtExpression) ingredient.getCode();

		log.debug("Target element to clean " + expressionTarget);

		MutationSupporter.clearPosition(expressionTarget);

		List<OperatorInstance> opsOfVariant = new ArrayList();

		OperatorInstance opInstace = new StatementOperatorInstance(modificationPoint, this, expressionSource,
				expressionTarget);
		opsOfVariant.add(opInstace);

		return opInstace;
	}

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {

		// See that the modification points are statements
		return (point.getCodeElement() instanceof CtStatement);

	}

}
