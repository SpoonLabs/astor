package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.cardumen.FineGrainedExpressionReplaceOperator;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.StatementOperatorInstance;
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
public abstract class MethodXMethodReplacementOp extends FineGrainedExpressionReplaceOperator implements MetaOperator {

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
				putVarsNotDuplicated(ingredient.getCode(), varsToBeParameters);

			}

			// The variable from the existing invocation must also be a parameter
			putVarsNotDuplicated(modificationPoint.getCodeElement(), varsToBeParameters);

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

			MetaOperatorInstance megaOp = MetaGenerator.createMeta(modificationPoint, invocationToReplace,
					invocationCounter, ingredients, parameters, realParameters, this, returnTypeOfInvocation);
			opsMega.add(megaOp);

		} // End invocation

		return opsMega;
	}

	public void putVarsNotDuplicated(CtElement elementToAnalyze, List<CtVariableAccess> varsToBeParameters) {
		List<CtVariableAccess> varsFromExpression = elementToAnalyze.getElements(e -> e instanceof CtVariableAccess);
		for (CtVariableAccess ctVariableAccess : varsFromExpression) {
			if (!varsToBeParameters.contains(ctVariableAccess))
				varsToBeParameters.add(ctVariableAccess);

		}
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
}
