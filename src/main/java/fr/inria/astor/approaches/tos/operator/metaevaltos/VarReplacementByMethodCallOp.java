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
import fr.inria.astor.util.MapList;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 * 
 * @author Matias Martinez
 *
 */
public class VarReplacementByMethodCallOp extends FineGrainedExpressionReplaceOperator implements MetaOperator {

	public static final String META_METHOD_LABEL = "_meta_";

	@Override
	public List<MetaOperatorInstance> createMetaOperatorInstances(ModificationPoint modificationPoint) {

		// Let's create one meta per modif point
		List<OperatorInstance> metaOperations = new ArrayList();
		MetaOperatorInstance opMega = new MetaOperatorInstance(metaOperations);
		List<MetaOperatorInstance> opsMega = new ArrayList();

		opsMega.add(opMega);

		// Map that allows to trace the mutant id with the ingredient used
		Map<Integer, Ingredient> ingredientOfMapped = new HashMap<>();

		//
		List<CtVariableAccess> varAccessInModificationPoints = VariableResolver
				.collectVariableAccess(modificationPoint.getCodeElement(), false);

		log.debug("\nModifcationPoint: \n" + modificationPoint);

		MapList<CtTypeReference, CtInvocation> cacheIngredientsPerType = new MapList<>();

		int variableCounter = 0;
		for (CtVariableAccess variableAccessToReplace : varAccessInModificationPoints) {

			List<Ingredient> ingredients = this.computeIngredientsFromVarToReplace(modificationPoint,
					variableAccessToReplace, cacheIngredientsPerType);

			if (ingredients.isEmpty()) {
				continue;
			}

			// The parameters to be included in the new method
			List<CtVariableAccess> varsToBeParametersTemp = ingredients.stream()
					.map(e -> e.getCode().getElements(new TypeFilter<>(CtVariableAccess.class))).flatMap(List::stream)
					.map(CtVariableAccess.class::cast).distinct().collect(Collectors.toList());

			// The variable to be replaced must also be a parameter

			varsToBeParametersTemp.add(variableAccessToReplace);
			// Let's check the names (the previous filter does not filter all duplicates, so
			// to avoid problems we do it manually):

			List<CtVariableAccess> varsToBeParameters = new ArrayList();
			for (CtVariableAccess parameterFound : varsToBeParametersTemp) {
				boolean hasname = false;
				for (CtVariableAccess parameterConsiderer : varsToBeParameters) {
					if (parameterConsiderer.getVariable().getSimpleName()
							.equals(parameterFound.getVariable().getSimpleName())) {
						hasname = true;
						break;
					}
				}
				// any variable with that name, so we add in parameters
				if (!hasname) {
					varsToBeParameters.add(parameterFound);
				}
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

			CtTypeReference returnType = variableAccessToReplace.getType();

			MetaGenerator.createMetaForSingleElement(modificationPoint, variableAccessToReplace, variableCounter,
					ingredients, parameters, realParameters, returnType, metaOperations, ingredientOfMapped);

		} // End variable

		opMega.setAllIngredients(ingredientOfMapped);
		opMega.setOperationApplied(this);
		opMega.setOriginal(modificationPoint.getCodeElement());
		opMega.setModificationPoint(modificationPoint);

		return opsMega;
	}

	protected List<Ingredient> computeIngredientsFromVarToReplace(ModificationPoint modificationPoint,
			CtVariableAccess variableAccessToReplace, MapList<CtTypeReference, CtInvocation> cacheIngredientsPerType) {

		List<Ingredient> ingredients = new ArrayList<>();

		// All the methods in scope

		CtTypeReference variableType = variableAccessToReplace.getType();

		List<CtInvocation> varAll = null;
		if (cacheIngredientsPerType.containsKey(variableType))
			varAll = cacheIngredientsPerType.get(variableType);
		else {

			CtClass classUnderAnalysis = modificationPoint.getCodeElement().getParent(CtClass.class);

			List<CtInvocation> varOb = SupportOperators.retrieveInvocationsFromMethod(variableType, classUnderAnalysis,
					modificationPoint);

			List<CtInvocation> varM = SupportOperators.retrieveInvocationsFromVar(variableType, classUnderAnalysis,
					modificationPoint);

			varAll = new ArrayList();
			varAll.addAll(varOb);
			varAll.addAll(varM);

			// Add into cache

			cacheIngredientsPerType.put(variableType, varAll);
		}

		for (CtInvocation invocationTomethod : varAll) {
			Ingredient ingredient = new Ingredient(invocationTomethod.clone());
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
