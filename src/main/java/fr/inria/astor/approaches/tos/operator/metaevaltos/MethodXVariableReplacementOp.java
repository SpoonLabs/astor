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
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtTypeReference;

/**
 * 
 * @author Matias Martinez
 *
 */
public class MethodXVariableReplacementOp extends FineGrainedExpressionReplaceOperator
		implements MetaOperator, IOperatorWithTargetElement {

	private CtElement targetElement = null;

	@Override
	public List<MetaOperatorInstance> createMetaOperatorInstances(ModificationPoint modificationPoint) {

		MetaOperatorInstance opMega = new MetaOperatorInstance(this, MetaGenerator.getNewIdentifier());

		List<OperatorInstance> opsOfVariant = new ArrayList();

		Map<Integer, Ingredient> ingredientOfMapped = new HashMap<>();

		//
		List<CtInvocation> invocationsFromModifPoints = getInvocations(modificationPoint.getCodeElement());

		log.debug("\nModifcationPoint: \n" + modificationPoint);

		// let's start with one, and let's keep the Zero for the default (all ifs are
		// false)
		// TODO: we only can activate one mutant

		int variableCounter = 0;
		for (CtInvocation invocationToReplace : invocationsFromModifPoints) {

			// The return type of the new method correspond to the type of variable to
			// change
			CtTypeReference returnType = invocationToReplace.getType();

			List<Ingredient> ingredients = this.computeIngredientsFromMInvokToReplace(modificationPoint,
					invocationToReplace);

			if (ingredients.isEmpty()) {
				// Nothing to replace
				continue;
			}

			// The parameters to be included in the new method
			List<CtVariableAccess> varsToBeParameters = ingredients.stream().map(Ingredient::getCode)
					.map(CtVariableAccess.class::cast).collect(Collectors.toList());
			// The variable from the existing invocation must also be a parameter
			SupportOperators.putVarsNotDuplicated(modificationPoint.getCodeElement(), varsToBeParameters);

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

			MetaGenerator.createMetaForSingleElement(opMega, modificationPoint, invocationToReplace, variableCounter,
					ingredients, parameters, realParameters, returnType, opsOfVariant, ingredientOfMapped);

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

	protected List<Ingredient> computeIngredientsFromMInvokToReplace(ModificationPoint modificationPoint,
			CtInvocation invocationToReplace) {

		List<Ingredient> ingredients = new ArrayList<>();
		List<CtVariable> varsInContext = modificationPoint.getContextOfModificationPoint();

		for (CtVariable iVarInContext : varsInContext) {

			if (!SupportOperators.checkIsSubtype(iVarInContext.getType(), invocationToReplace.getType())) {
				continue;
			}

			CtVariableAccess iVarAccessFromContext = MutationSupporter.getFactory()
					.createVariableRead(iVarInContext.getReference(), false);
			Ingredient ingredient = new Ingredient(iVarAccessFromContext);
			// we use this property to indicate the old variable to replace
			ingredient.setDerivedFrom(invocationToReplace);
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

	@Override
	public void setTargetElement(CtElement target) {
		this.targetElement = target;

	}

	protected List<CtInvocation> getInvocations(CtElement suspiciousElement) {
		List<CtInvocation> invocations = null;
		if (targetElement == null)
			invocations = suspiciousElement.getElements(e -> (e instanceof CtInvocation));
		else {
			invocations = new ArrayList<>();
			invocations.add((CtInvocation) targetElement);
		}
		return invocations;
	}

	@Override
	public boolean checkTargetCompatibility(CtElement target) {

		return target instanceof CtInvocation;
	}

	@Override
	public String identifier() {

		return "Method_RW_Var";
	}

}
