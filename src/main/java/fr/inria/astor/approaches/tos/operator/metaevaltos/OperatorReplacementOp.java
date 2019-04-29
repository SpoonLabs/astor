package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import fr.inria.astor.approaches.cardumen.FineGrainedExpressionReplaceOperator;
import fr.inria.astor.approaches.tos.operator.metaevaltos.simple.SingleOperatorChangeOperator;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
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

/**
 * 
 * @author Matias Martinez
 *
 */
public class OperatorReplacementOp extends FineGrainedExpressionReplaceOperator
		implements MetaOperator, IOperatorWithTargetElement {

	List<BinaryOperatorKind> logicalOps = Arrays.asList(BinaryOperatorKind.AND, BinaryOperatorKind.OR);
	List<BinaryOperatorKind> relationalOps = Arrays.asList(BinaryOperatorKind.EQ, BinaryOperatorKind.NE,
			BinaryOperatorKind.GE, BinaryOperatorKind.GT, BinaryOperatorKind.LE, BinaryOperatorKind.LT

	);

	private CtElement targetElement = null;

	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {
		log.error("This op needs ingredients");
		return null;
	}

	@Override
	public List<MetaOperatorInstance> createMetaOperatorInstances(ModificationPoint modificationPoint) {

		List<MetaOperatorInstance> opsMega = new ArrayList();

// get all binary expressions
		List<CtBinaryOperator> booleanExpressionsInModificationPoints = null;

		if (targetElement == null) {
			booleanExpressionsInModificationPoints = modificationPoint.getCodeElement()
					.getElements(new TypeFilter<>(CtBinaryOperator.class)).stream()
					.filter(e -> SupportOperators.isBooleanType(e)).collect(Collectors.toList());
		} else {
			booleanExpressionsInModificationPoints = new ArrayList<>();
			booleanExpressionsInModificationPoints.add((CtBinaryOperator) this.targetElement);
		}

		log.debug("\nLogicExp: \n" + modificationPoint);

// As difference with var replacement, a metamutant for each expression
		for (CtExpression<Boolean> expressionToExpand : booleanExpressionsInModificationPoints) {

			int variableCounter = 0;
			// The return type of the new method correspond to the type of variable to
			// change

			List<Ingredient> ingredients = this.computeIngredientsFromExpressionExplansion(modificationPoint,
					expressionToExpand);

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
			MetaOperatorInstance megaOp = MetaGenerator.createMetaFineGrainedReplacement(modificationPoint,
					expressionToExpand, variableCounter, ingredients, parameters, realParameters, this, returnType);

			opsMega.add(megaOp);

		} // End variable

		return opsMega;
	}

	private List<Ingredient> computeIngredientsFromExpressionExplansion(ModificationPoint modificationPoint,
			CtExpression previousExpression) {

		List<Ingredient> ingredientsNewBinaryExpressions = new ArrayList();

		if (!(previousExpression instanceof CtBinaryOperator))
			return ingredientsNewBinaryExpressions;

		ingredientsNewBinaryExpressions.addAll(getNewBinary((CtBinaryOperator) previousExpression, logicalOps));
		ingredientsNewBinaryExpressions.addAll(getNewBinary((CtBinaryOperator) previousExpression, relationalOps));

		return ingredientsNewBinaryExpressions;
	}

	public List<Ingredient> getNewBinary(CtBinaryOperator oldBinary, List<BinaryOperatorKind> ops) {
		List<Ingredient> mutationAll = new ArrayList<>();

		if (ops.contains(oldBinary.getKind())) {

			for (BinaryOperatorKind binaryOperatorKind2 : ops) {

				if (binaryOperatorKind2.equals(oldBinary.getKind()))
					continue;

				CtBinaryOperator binaryOp = MutationSupporter.getFactory().Code().createBinaryOperator(
						oldBinary.getLeftHandOperand().clone(), oldBinary.getRightHandOperand(), binaryOperatorKind2);
				MutationSupporter.clearPosition(binaryOp);
				Ingredient newIngredientExtended = new Ingredient(binaryOp);
				newIngredientExtended.setDerivedFrom(oldBinary);
				mutationAll.add(newIngredientExtended);
				newIngredientExtended.getMetadata().put("left_original", oldBinary.getLeftHandOperand());
				newIngredientExtended.getMetadata().put("right_original", oldBinary.getRightHandOperand());
				newIngredientExtended.getMetadata().put("operator", binaryOperatorKind2);
			}
		}

		return mutationAll;
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

		OperatorInstance opInstace = new SingleOperatorChangeOperator(modificationPoint,
				(CtBinaryOperator) expressionSource, (CtExpression) ingredient.getMetadata().get("left_original"),
				(CtExpression) ingredient.getMetadata().get("right_original"),
				(BinaryOperatorKind) ingredient.getMetadata().get("operator"), this);
		opsOfVariant.add(opInstace);

		return opInstace;
	}

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {

		return (point.getCodeElement() instanceof CtStatement);

	}

	@Override
	public void setTargetElement(CtElement target) {
		this.targetElement = target;

	}

	@Override
	public boolean checkTargetCompatibility(CtElement e) {

		return e instanceof CtBinaryOperator && SupportOperators.isBooleanType((CtExpression) e);
	}

	@Override
	public String identifier() {
		return "binOperatorModif";
	}
}