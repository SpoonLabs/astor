package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.inria.astor.approaches.cardumen.FineGrainedExpressionReplaceOperator;
import fr.inria.astor.approaches.jmutrepair.MutantCtElement;
import fr.inria.astor.approaches.jmutrepair.operators.LogicalBinaryOperatorMutator;
import fr.inria.astor.approaches.jmutrepair.operators.RelationalBinaryOperatorMutator;
import fr.inria.astor.core.entities.Ingredient;
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
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

public class OperatorReplacementOp extends FineGrainedExpressionReplaceOperator implements MetaOperator {

	public BinaryOperatorKind operatorKind = BinaryOperatorKind.OR;

	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {
		log.error("This op needs ingredients");
		return null;
	}

	@Override
	public List<MetaOperatorInstance> createMetaOperatorInstances(ModificationPoint modificationPoint) {

		List<MetaOperatorInstance> opsMega = new ArrayList();

// get all binary expressions
		List<CtBinaryOperator> booleanExpressionsInModificationPoints = modificationPoint.getCodeElement()
				.getElements(new TypeFilter<>(CtBinaryOperator.class)).stream()
				.filter(e -> SupportOperators.isBooleanType(e)).collect(Collectors.toList());

		log.debug("\nLogicExp: \n" + modificationPoint);

// As difference with var replacement, a metamutant for each expression
		for (CtExpression<Boolean> expressionToExpand : booleanExpressionsInModificationPoints) {

			int variableCounter = 0;
			// The return type of the new method correspond to the type of variable to
			// change

			List<Ingredient> ingredients = this.computeIngredientsFromExpressionExplansion(modificationPoint,
					expressionToExpand, this.operatorKind);

			// The parameters to be included in the new method
			List<CtVariableAccess> varsToBeParameters = new ArrayList<>();

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

			// let's create the meta
			MetaOperatorInstance megaOp = MetaGenerator.createMeta(modificationPoint, expressionToExpand,
					variableCounter, ingredients, parameters, realParameters, this, returnType);

			opsMega.add(megaOp);

		} // End variable

		return opsMega;
	}

	private List<Ingredient> computeIngredientsFromExpressionExplansion(ModificationPoint modificationPoint,
			CtExpression previousExpression, BinaryOperatorKind operatorKind2) {

		List<Ingredient> ingredientsNewBinaryExpressions = new ArrayList();

		LogicalBinaryOperatorMutator logical = new LogicalBinaryOperatorMutator(MutationSupporter.getFactory());
		RelationalBinaryOperatorMutator relational = new RelationalBinaryOperatorMutator(
				MutationSupporter.getFactory());

		List<MutantCtElement> mutantsLogical = logical.execute(previousExpression);
		List<MutantCtElement> mutantsRelational = relational.execute(previousExpression);

		List<CtElement> mutationAll = new ArrayList<>();

		for (MutantCtElement mi : mutantsLogical) {
			mutationAll.add(mi.getElement());

		}
		for (MutantCtElement mi : mutantsRelational) {
			mutationAll.add(mi.getElement());

		}

		for (CtElement mutated : mutationAll) {

			MutationSupporter.clearPosition(mutated);
			Ingredient newIngredientExtended = new Ingredient(mutated);
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