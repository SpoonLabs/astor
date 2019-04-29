package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.inria.astor.approaches.cardumen.FineGrainedExpressionReplaceOperator;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.entities.meta.MetaOperator;
import fr.inria.astor.core.entities.meta.MetaOperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtTypeReference;

public class VarReplacementByAnotherVarOp extends FineGrainedExpressionReplaceOperator
		implements MetaOperator, IOperatorWithTargetElement {

	private CtElement targetElement = null;

	@Override
	public List<MetaOperatorInstance> createMetaOperatorInstances(ModificationPoint modificationPoint) {

		MetaOperatorInstance opMega = new MetaOperatorInstance(this, MetaGenerator.getNewIdentifier());

		List<OperatorInstance> opsOfVariant = new ArrayList();

		Map<Integer, Ingredient> ingredientOfMapped = new HashMap<>();

		//
		List<CtVariableAccess> varAccessInModificationPoints = null;

		if (targetElement == null) {
			varAccessInModificationPoints = VariableResolver.collectVariableAccess(modificationPoint.getCodeElement(),
					// it must be true because, even we have vars with different names, they are
					// different access.
					true);
		} else {
			varAccessInModificationPoints = new ArrayList<>();
			varAccessInModificationPoints.add((CtVariableAccess) targetElement);
		}

		log.debug("\nModifcationPoint: \n" + modificationPoint);

		// let's start with one, and let's keep the Zero for the default (all ifs are
		// false)
		// TODO: we only can activate one mutant

		int variableCounter = 0;
		for (CtVariableAccess variableAccessToReplace : varAccessInModificationPoints) {

			// The return type of the new method correspond to the type of variable to
			// change
			CtTypeReference returnType = variableAccessToReplace.getType();

			List<Ingredient> ingredients = this.computeIngredientsFromVarToReplace(modificationPoint,
					variableAccessToReplace);

			if (ingredients.isEmpty()) {
				// Nothing to replace
				continue;
			}

			// The parameters to be included in the new method
			List<CtVariableAccess> varsToBeParameters = ingredients.stream().map(Ingredient::getCode)
					.map(CtVariableAccess.class::cast).collect(Collectors.toList());
			// The variable to be replaced must also be a parameter
			varsToBeParameters.add(variableAccessToReplace);

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

			MetaGenerator.createMetaForSingleElement(opMega, modificationPoint, variableAccessToReplace,
					variableCounter, ingredients, parameters, realParameters, returnType, opsOfVariant,
					ingredientOfMapped);

		} // End variable

		opMega.setOperatorInstances(opsOfVariant);
		opMega.setAllIngredients(ingredientOfMapped);
		opMega.setOperationApplied(this);
		opMega.setOriginal(modificationPoint.getCodeElement());
		opMega.setModificationPoint(modificationPoint);

		List<MetaOperatorInstance> opsMega = new ArrayList();
		opsMega.add(opMega);

		return opsMega;
	}

	protected List<Ingredient> computeIngredientsFromVarToReplace(ModificationPoint modificationPoint,
			CtVariableAccess variableAccessToReplace) {

		List<Ingredient> ingredients = new ArrayList<>();
		List<CtVariable> varsInContext = modificationPoint.getContextOfModificationPoint();

		for (CtVariable iVarInContext : varsInContext) {

			boolean compatibleVariables = VariableResolver.areVarsCompatible(variableAccessToReplace, iVarInContext);
			if (!compatibleVariables
					|| iVarInContext.getSimpleName().equals(variableAccessToReplace.getVariable().getSimpleName())) {
				continue;
			}

			CtVariableAccess iVarAccessFromContext = MutationSupporter.getFactory()
					.createVariableRead(iVarInContext.getReference(), false);
			Ingredient ingredient = new Ingredient(iVarAccessFromContext);
			// we use this property to indicate the old variable to replace
			ingredient.setDerivedFrom(variableAccessToReplace);
			ingredients.add(ingredient);

		}

		return ingredients;
	}

	@Override
	public OperatorInstance getConcreteOperatorInstance(MetaOperatorInstance operatorInstance, int metaIdentifier) {

		Ingredient ingredient = operatorInstance.getAllIngredients().get(metaIdentifier);

		ModificationPoint modificationPoint = operatorInstance.getModificationPoint();

		CtExpression expressionSource = (CtExpression) ingredient.getDerivedFrom();
		CtExpression expressionTarget = (CtExpression) ingredient.getCode();

		MutationSupporter.clearPosition(expressionTarget);

		List<OperatorInstance> opsOfVariant = new ArrayList();

		OperatorInstance opInstace = // new OperatorInstance(modificationPoint, this, expressionSource,
										// expressionTarget);
				new StatementOperatorInstance(modificationPoint, this, expressionSource, expressionTarget);

		opsOfVariant.add(opInstace);

		return opInstace;
	}

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {

		// See that the modification points are statements
		return (point.getCodeElement() instanceof CtStatement);
	}

	@Override
	public void setTargetElement(CtElement target) {
		this.targetElement = target;

	}

	@Override
	public boolean checkTargetCompatibility(CtElement target) {

		return target instanceof CtVariableAccess;
	}

	@Override
	public String identifier() {
		return "VAR_RW_VAR";
	}
}
