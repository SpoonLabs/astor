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
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ConstReplacementOp extends FineGrainedExpressionReplaceOperator
		implements MetaOperator, IOperatorWithTargetElement {

	private CtElement targetElement = null;

	@Override
	public List<MetaOperatorInstance> createMetaOperatorInstances(ModificationPoint modificationPoint) {

		MetaOperatorInstance opMega = new MetaOperatorInstance(this, MetaGenerator.getNewIdentifier());

		List<OperatorInstance> opsOfVariant = new ArrayList();

		List<MetaOperatorInstance> opsMega = new ArrayList();

		Map<Integer, Ingredient> ingredientOfMapped = new HashMap<>();

		List<CtLiteral> literalsInModificationPoints = null;

		if (targetElement == null) {
			literalsInModificationPoints = modificationPoint.getCodeElement()
					.getElements(new TypeFilter<>(CtLiteral.class));
		} else {
			literalsInModificationPoints = new ArrayList<>();
			literalsInModificationPoints.add((CtLiteral) targetElement);
		}

		List<CtLiteral> getAllLiteralsFromClass = modificationPoint.getCtClass()
				.getElements(new TypeFilter<>(CtLiteral.class)).stream().distinct().collect(Collectors.toList());

		log.debug("\nModifcationPoint: \n" + modificationPoint);

		int variableCounter = 0;
		for (CtLiteral variableAccessToReplace : literalsInModificationPoints) {

			// The return type of the new method correspond to the type of variable to
			// change
			CtTypeReference returnType = variableAccessToReplace.getType();

			List<Ingredient> ingredients = this.computeIngredientsFromLiteralsToReplace(modificationPoint,
					variableAccessToReplace, getAllLiteralsFromClass);

			if (ingredients.isEmpty()) {
				// Nothing to replace
				continue;
			}

			// The parameters to be included in the new method
			List<CtVariableAccess> varsToBeParameters = ingredients.stream()
					.filter(e -> e.getCode() instanceof CtVariableAccess)
					.map(Ingredient::getCode)
					.map(CtVariableAccess.class::cast).collect(Collectors.toList());
			// The variable to be replaced must also be a parameter

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

		opsMega.add(opMega);

		return opsMega;
	}

	protected List<Ingredient> computeIngredientsFromLiteralsToReplace(ModificationPoint modificationPoint,
			CtLiteral literalToReplace, List<CtLiteral> otherLiterals) {

		List<Ingredient> ingredients = new ArrayList<>();
		List<CtVariable> varsInContext = modificationPoint.getContextOfModificationPoint();

		for (CtVariable iVarInContext : varsInContext) {

			boolean compatibleVariables = VariableResolver.areTypesCompatible(literalToReplace.getType(),
					iVarInContext.getType());
			if (compatibleVariables) {

				CtVariableAccess iVarAccessFromContext = MutationSupporter.getFactory()
						.createVariableRead(iVarInContext.getReference(), false);
				Ingredient ingredient = new Ingredient(iVarAccessFromContext);
				// we use this property to indicate the old variable to replace
				ingredient.setDerivedFrom(literalToReplace);
				ingredients.add(ingredient);
			}

		}

		for (CtLiteral otherLiteral : otherLiterals) {

			boolean compatibleVariables = VariableResolver.areTypesCompatible(literalToReplace.getType(),
					otherLiteral.getType());
			if (compatibleVariables && !otherLiteral.getValue().equals(literalToReplace.getValue())) {

				Ingredient ingredient = new Ingredient(otherLiteral.clone());
				// we use this property to indicate the old variable to replace
				ingredient.setDerivedFrom(literalToReplace);
				ingredients.add(ingredient);
			}

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

	@Override
	public boolean checkTargetCompatibility(CtElement target) {

		return target instanceof CtLiteral;
	}

	@Override
	public String identifier() {

		return "constChange";
	}
}
