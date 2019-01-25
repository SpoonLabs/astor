package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.inria.astor.approaches.cardumen.FineGrainedExpressionReplaceOperator;
import fr.inria.astor.approaches.tos.core.InsertMethodOperator;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.entities.meta.MetaOperator;
import fr.inria.astor.core.entities.meta.MetaOperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.util.MapList;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtBlockImpl;
import spoon.support.reflect.code.CtReturnImpl;

/**
 * Methor wrong reference Case 1: Argument removement. Case 2: Different method
 * name. Case 3: Different number arguments.
 * 
 * @author Matias Martinez
 *
 */
public class MethodCallReplacementByAnotherMethodCallOp extends FineGrainedExpressionReplaceOperator
		implements MetaOperator {

	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {

		List<OperatorInstance> opsOfVariant = new ArrayList();

		List<OperatorInstance> opsMega = new ArrayList();

		MapList<CtInvocation, Ingredient> ingredientsPerInvocation = this
				.retrieveInvocationIngredient(modificationPoint.getCodeElement());
		if (ingredientsPerInvocation.isEmpty()) {
			// Nothing to replace
			return opsMega;
		}

		// Configuring Method to create
		Set<ModifierKind> modifiers = new HashSet<>();
		modifiers.add(ModifierKind.PRIVATE);

		CtType target = null;
		target = modificationPoint.getCodeElement().getParent(CtType.class);
		CtExpression invocationTarget = MutationSupporter.getFactory().createThisAccess(target.getReference());

		// Here, we have to consider if the parent method is static.
		CtMethod parentMethod = modificationPoint.getCodeElement().getParent(CtMethod.class);
		if (parentMethod != null) {

			if (parentMethod.getModifiers().contains(ModifierKind.STATIC)) {
				modifiers.add(ModifierKind.STATIC);
				invocationTarget = MutationSupporter.getFactory().createTypeAccess(target.getReference());

			}
		}

		log.debug("\nMethodInvoReplacement: \n" + modificationPoint);

		// let's start with one, and let's keep the Zero for the default (all ifs are
		// false)
		// TODO: we only can activate one mutant
		int candidateNumber = 0;

		// As difference with var replacement, a metamutant for each expression
		for (CtInvocation invocationToReplace : ingredientsPerInvocation.keySet()) {

			int variableCounter = 0;
			Map<Integer, Ingredient> ingredientOfMapped = new HashMap<>();

			List<Ingredient> ingredients = ingredientsPerInvocation.get(invocationToReplace);

			List<CtVariableAccess> varsToBeParameters = new ArrayList<>();

			CtTypeReference returnTypeOfInvocation = invocationToReplace.getType();

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

			variableCounter++;
			String name = "_meta_" + variableCounter;

			Set<CtTypeReference<? extends Throwable>> thrownTypes = new HashSet<>();

			CtMethod<?> megaMethod = MutationSupporter.getFactory().createMethod(target, modifiers,
					returnTypeOfInvocation, name, parameters, thrownTypes);

			CtInvocation newInvocationToMega = MutationSupporter.getFactory().createInvocation(invocationTarget,
					megaMethod.getReference(), realParameters);

			/// Let's start creating the body of the new method.
			// first the main try
			CtTry tryMethodMain = MutationSupporter.getFactory().createTry();
			List<CtCatch> catchers = new ArrayList<>();
			CtCatch catch1 = MutationSupporter.getFactory().createCtCatch("e", Exception.class, new CtBlockImpl());
			catchers.add(catch1);
			tryMethodMain.setCatchers(catchers);
			CtBlock tryBoddy = new CtBlockImpl();
			tryMethodMain.setBody(tryBoddy);

			CtBlock methodBodyBlock = new CtBlockImpl();
			megaMethod.setBody(methodBodyBlock);
			methodBodyBlock.addStatement(tryMethodMain);

			for (Ingredient ingredientCandidate : ingredients) {

				candidateNumber++;
				CtExpression expressionCandidate = (CtExpression) ingredientCandidate.getCode();
				CtCodeSnippetExpression caseCondition = MutationSupporter.getFactory().createCodeSnippetExpression(
						"\"" + candidateNumber + "\".equals(System.getProperty(\"mutnumber\")) ");

				ingredientOfMapped.put(candidateNumber, ingredientCandidate);

				CtIf particularIf = MutationSupporter.getFactory().createIf();
				particularIf.setCondition(caseCondition);
				CtStatement stPrint = MutationSupporter.getFactory().createCodeSnippetStatement(
						"System.out.println(" + "\"\\nPROPERTY met:\" +System.getProperty(\"mutnumber\"))");
				CtBlock particularIfBlock = new CtBlockImpl<>();
				particularIfBlock.addStatement(stPrint);
				particularIf.setThenStatement(particularIfBlock);

				// The return inside the if
				// add a return with the expression
				CtReturn casereturn = new CtReturnImpl<>();
				casereturn.setReturnedExpression(expressionCandidate);
				particularIfBlock.addStatement(casereturn);

				// Add the if to the methodBlock
				// methodBodyBlock
				tryBoddy.addStatement(particularIf);

			}

			// By default, return the original
			CtReturn defaultReturnLast = new CtReturnImpl<>();
			CtExpression expCloned = invocationToReplace.clone();
			expCloned.setPosition(new NoSourcePosition());
			MutationSupporter.clearPosition(expCloned);

			defaultReturnLast.setReturnedExpression(expCloned);
			methodBodyBlock.addStatement(defaultReturnLast);

			/// ***
			// Up to here, the different cases

			// Now the if to be inserted:
			// 1:

			CtElement elementSource = invocationToReplace;
			// Let's create the operations

			OperatorInstance opInvocation = new OperatorInstance();
			opInvocation.setOperationApplied(new FineGrainedExpressionReplaceOperator());
			opInvocation.setOriginal(elementSource);
			opInvocation.setModified(newInvocationToMega);
			opInvocation.setModificationPoint(modificationPoint);

			opsOfVariant.add(opInvocation);

			// 2:
			// The meta method to be added
			OperatorInstance opMethodAdd = new OperatorInstance();
			opMethodAdd.setOperationApplied(new InsertMethodOperator());
			opMethodAdd.setOriginal(modificationPoint.getCodeElement());
			opMethodAdd.setModified(megaMethod);
			opMethodAdd.setModificationPoint(modificationPoint);
			opsOfVariant.add(opMethodAdd);

			//
			log.debug("method: \n" + megaMethod);

			log.debug("invocation: \n" + newInvocationToMega);

			MetaOperatorInstance opMega = new MetaOperatorInstance(opsOfVariant);
			opMega.setAllIngredients(ingredientOfMapped);
			opMega.setOperationApplied(this);
			opMega.setOriginal(modificationPoint.getCodeElement());
			opMega.setModificationPoint(modificationPoint);

			opsMega.add(opMega);

		} // End variable

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
	 * For case 2
	 * 
	 * @param suspiciousElement
	 * @param context
	 * @return
	 */
	private MapList<CtInvocation, Ingredient> retrieveInvocationIngredient(CtElement suspiciousElement) {

		CtClass classUnderAnalysis = suspiciousElement.getParent(CtClass.class);

		MapList<CtInvocation, Ingredient> similarInvocationResult = new MapList<>();

		List invocations = suspiciousElement.getElements(e -> (e instanceof CtInvocation));
		for (Object invocationInSuspicious : invocations) {
			CtInvocation invocationToReplace = (CtInvocation) invocationInSuspicious;
			CtExecutable minvokedInAffected = invocationToReplace.getExecutable().getDeclaration();

			if (minvokedInAffected == null || !(minvokedInAffected instanceof CtMethod))
				continue;

			CtMethod affectedMethod = (CtMethod) minvokedInAffected;

			List allMethods = SupportOperators.getAllMethodsFromClass(classUnderAnalysis);

			for (Object omethod : allMethods) {

				if (!(omethod instanceof CtMethod))
					continue;

				CtMethod anotherMethod = (CtMethod) omethod;

				if (anotherMethod.getSignature().equals(affectedMethod.getSignature()))
					// It's the same, we discard it.
					continue;

				if (anotherMethod.getSimpleName().equals(affectedMethod.getSimpleName())) {
					// It's override
					// TODO:
					// similarInvocationResult.add(affectedMethod, anotherMethod);

				}

				if (anotherMethod.getType() != null && minvokedInAffected.getType() != null) {

					boolean compatibleReturnTypes = SupportOperators.compareTypes(anotherMethod.getType(),
							minvokedInAffected.getType());
					if (compatibleReturnTypes) {
						// int dist = StringDistance.calculate(anotherMethod.getSimpleName(),
						// minvokedInAffected.getSimpleName());
						// if (dist > 0 && dist < 3) {

						// }
						// CASE 2: Different method name
						if (anotherMethod.getParameters().size() == affectedMethod.getParameters().size()
								&& anotherMethod.getParameters().equals(affectedMethod.getParameters())) {

							CtInvocation newInvocation = invocationToReplace.clone();
							// newInvocation.setLabel(anotherMethod.getSimpleName());
							newInvocation.setExecutable(anotherMethod.getReference());
							Ingredient newIngredient = new Ingredient(newInvocation);
							newIngredient.setDerivedFrom(invocationToReplace);

							similarInvocationResult.add(invocationToReplace, newIngredient);
						}
					}

				}

			}

		}
		return similarInvocationResult;

	}

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {

		// See that the modification points are statements
		return (point.getCodeElement() instanceof CtStatement);
	}
}
