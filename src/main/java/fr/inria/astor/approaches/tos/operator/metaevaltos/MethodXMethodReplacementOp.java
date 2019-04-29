package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.cardumen.FineGrainedExpressionReplaceOperator;
import fr.inria.astor.approaches.tos.operator.metaevaltos.simple.SimpleMethodReplacement;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.meta.MetaOperator;
import fr.inria.astor.core.entities.meta.MetaOperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.util.MapList;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;

/**
 * Methor wrong reference Case 1: Argument removement. Case 2: Different method
 * name. Case 3: Different number arguments.
 * 
 * @author Matias Martinez
 *
 */
public abstract class MethodXMethodReplacementOp extends FineGrainedExpressionReplaceOperator
		implements MetaOperator, IOperatorWithTargetElement {
	protected CtElement targetElement = null;

	@Override
	public List<MetaOperatorInstance> createMetaOperatorInstances(ModificationPoint modificationPoint) {

		List<MetaOperatorInstance> opsMega = new ArrayList();

		MapList<CtInvocation, Ingredient> ingredientsPerInvocation = this
				.retrieveInvocationIngredient(modificationPoint);
		if (ingredientsPerInvocation.isEmpty()) {
			// Nothing to replace
			return opsMega;
		}

		log.debug("\nMethodInvoReplacement: \n" + modificationPoint);

		// let's start with one, and let's keep the Zero for the default (all ifs are
		// false)

		// As difference with var replacement, a metamutant for each invocation
		for (CtInvocation invocationToReplace : ingredientsPerInvocation.keySet()) {

			int invocationCounter = 0;

			List<Ingredient> ingredients = ingredientsPerInvocation.get(invocationToReplace);

			List<CtVariableAccess> varsToBeParameters = new ArrayList<>();

			// The parameters to be included in the new method
			for (Ingredient ingredient : ingredients) {
				SupportOperators.putVarsNotDuplicated(ingredient.getCode(), varsToBeParameters);

			}

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

			invocationCounter++;

			/// Let's start creating the body of the new method.
			// first the main try
			CtTypeReference returnTypeOfInvocation = invocationToReplace.getType();

			MetaOperatorInstance megaOp = MetaGenerator.createMetaFineGrainedReplacement(modificationPoint,
					invocationToReplace, invocationCounter, ingredients, parameters, realParameters, this,
					returnTypeOfInvocation);
			opsMega.add(megaOp);

		} // End invocation

		return opsMega;
	}

	@Override
	public OperatorInstance getConcreteOperatorInstance(MetaOperatorInstance operatorInstance, int metaIdentifier) {

		Ingredient ingredient = operatorInstance.getAllIngredients().get(metaIdentifier);

		ModificationPoint modificationPoint = operatorInstance.getModificationPoint();

		CtInvocation expressionSource = (CtInvocation) ingredient.getMetadata().get("original");// ingredient.getDerivedFrom();
		CtInvocation expressionTarget = (CtInvocation) ingredient.getMetadata().get("replacement");

		MutationSupporter.clearPosition(expressionTarget);

		List<OperatorInstance> opsOfVariant = new ArrayList();

		OperatorInstance opInstace = new SimpleMethodReplacement(modificationPoint, expressionSource, expressionTarget,
				this);
		opsOfVariant.add(opInstace);

		return opInstace;
	}

	/**
	 * 
	 * 
	 * @param suspiciousElement
	 * @param context
	 * @return
	 */
	public abstract MapList<CtInvocation, Ingredient> retrieveInvocationIngredient(ModificationPoint point);

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

		return target instanceof CtInvocation;
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

}
