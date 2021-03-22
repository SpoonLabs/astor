package fr.inria.astor.approaches.tos.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.inria.astor.approaches.cardumen.FineGrainedExpressionReplaceOperator;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.entities.meta.MetaOperator;
import fr.inria.astor.core.entities.meta.MetaOperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtBlockImpl;
import spoon.support.reflect.code.CtReturnImpl;

/**
 * 
 * @author Matias Martinez
 *
 */
public class MetaGenerator {

	private static final String META_CNST = "_meta_";

	public static final String MUT_IDENTIFIER = "mutnumber_";

	public static final String METALL = "metid";

	public static int global_moi_indentifier = 0;

	protected static Logger log = Logger.getLogger(Thread.currentThread().getName());

	public static int getNewIdentifier() {
		return ++global_moi_indentifier;
	}

	public static MetaOperatorInstance createMetaFineGrainedReplacement(ModificationPoint modificationPoint,
			CtExpression elementSource, int variableCounter, List<Ingredient> ingredients,
			List<CtParameter<?>> parameters, List<CtExpression<?>> realParameters, AstorOperator parentOperator,
			CtTypeReference returnType) {

		MetaOperatorInstance opMega = new MetaOperatorInstance((MetaOperator) parentOperator,
				MetaGenerator.getNewIdentifier());

		List<OperatorInstance> opsOfVariant = new ArrayList();
		Map<Integer, Ingredient> ingredientOfMapped = new HashMap<>();

		createMetaForSingleElement(opMega, modificationPoint, elementSource, variableCounter, ingredients, parameters,
				realParameters, returnType, opsOfVariant, ingredientOfMapped);

		opMega.setOperatorInstances(opsOfVariant);
		opMega.setAllIngredients(ingredientOfMapped);
		opMega.setOperationApplied(parentOperator);
		opMega.setOriginal(elementSource);
		opMega.setModificationPoint(modificationPoint);

		return opMega;
	}

	public static void createMetaForSingleElement(MetaOperatorInstance opMega, ModificationPoint modificationPoint,
			CtExpression elementSource, int variableCounter, List<Ingredient> ingredients,
			List<CtParameter<?>> parameters, List<CtExpression<?>> realParameters, CtTypeReference returnType,
			List<OperatorInstance> opsOfVariant, Map<Integer, Ingredient> ingredientOfMapped) {
		CtExpression defaultReturnElement = elementSource;

		realParameters = filterParameter(realParameters);
		// Creation of mega method
		CtMethod<?> megaMethod = createMegaMethod(opMega, modificationPoint, defaultReturnElement, variableCounter,
				ingredients, parameters, ingredientOfMapped, returnType);

		// Invocation to mega
		CtInvocation newInvocationToMega = creationInvocationToMega(modificationPoint, realParameters, megaMethod);

		//
		for (Ingredient ingredient : ingredients) {
			ingredient.getMetadata().put("meta_object", newInvocationToMega);
		}

		// Now the if to be inserted:
		// 1:

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
	}

	public static MetaOperatorInstance createMetaStatementReplacement(ModificationPoint modificationPoint,
			CtElement elementSource, CtExpression defaultReturnElement, int variableCounter,
			List<Ingredient> ingredients, List<CtParameter<?>> parameters, List<CtExpression<?>> realParameters,
			AstorOperator parentOperator, CtTypeReference returnType) {

		MetaOperatorInstance opMega = new MetaOperatorInstance((MetaOperator) parentOperator,
				MetaGenerator.getNewIdentifier());

		List<OperatorInstance> opsOfVariant = new ArrayList();
		Map<Integer, Ingredient> ingredientOfMapped = new HashMap<>();

		realParameters = filterParameter(realParameters);
		// Creation of mega method
		CtMethod<?> megaMethod = createMegaMethod(opMega, modificationPoint, defaultReturnElement, variableCounter,
				ingredients, parameters, ingredientOfMapped, returnType);

		CtInvocation newInvocationToMega = creationInvocationToMega(modificationPoint, realParameters, megaMethod);

		// Now the if to be inserted:

		CtIf ifNew = MutationSupporter.getFactory().createIf();

		CtStatement statementPointed = (CtStatement) modificationPoint.getCodeElement();
		CtStatement statementPointedCloned = statementPointed.clone();
		statementPointedCloned.setPosition(new NoSourcePosition());
		MutationSupporter.clearPosition(statementPointedCloned);

		ifNew.setThenStatement(statementPointedCloned);
		ifNew.setCondition(newInvocationToMega);

		// Let's create the operations
		OperatorInstance opInstace = new StatementOperatorInstance(modificationPoint, parentOperator, statementPointed,
				ifNew);
		opsOfVariant.add(opInstace);

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

		opMega.setOperatorInstances(opsOfVariant);
		opMega.setAllIngredients(ingredientOfMapped);
		opMega.setOperationApplied(parentOperator);
		opMega.setOriginal(modificationPoint.getCodeElement());
		opMega.setModificationPoint(modificationPoint);

		return opMega;
	}

	private static List<CtExpression<?>> filterParameter(List<CtExpression<?>> realParameters) {
		List<CtExpression<?>> parametersN = new ArrayList<>();
		HashMap<String, CtVariableAccess> seen = new HashMap();
		for (CtExpression<?> ctParameter : realParameters) {
			if (ctParameter instanceof CtVariableAccess) {
				CtVariableAccess va = (CtVariableAccess) ctParameter;
				if (!seen.containsKey(va.getVariable().getSimpleName())) {
					parametersN.add(ctParameter);
					seen.put(va.getVariable().getSimpleName(), va);
				} else {
					// we have a var with the same name
					CtVariableAccess other = seen.get(va.getVariable().getSimpleName());
					if (other instanceof CtFieldAccess && !(va instanceof CtFieldAccess)) {
						// replace the field access by the other var access
						int ind = parametersN.indexOf(other);
						parametersN.remove(ind);
						parametersN.add(ind, va);
						seen.put(va.getVariable().getSimpleName(), va);
					}
				}
			}
		}

		return parametersN;
	}

	public static CtInvocation creationInvocationToMega(ModificationPoint modificationPoint,
			List<CtExpression<?>> realParameters, CtMethod<?> megaMethod) {
		CtType target = modificationPoint.getCodeElement().getParent(CtType.class);
		CtExpression invocationTarget = MutationSupporter.getFactory().createThisAccess(target.getReference());

		// Here, we have to consider if the parent method is static.
		CtMethod parentMethod = modificationPoint.getCodeElement().getParent(CtMethod.class);
		if (parentMethod != null) {

			if (parentMethod.getModifiers().contains(ModifierKind.STATIC)) {
				// modifiers.add(ModifierKind.STATIC);
				invocationTarget = MutationSupporter.getFactory().createTypeAccess(target.getReference());

			}
		}

		// Invocation to mega

		CtInvocation newInvocationToMega = MutationSupporter.getFactory().createInvocation(invocationTarget,
				megaMethod.getReference(), realParameters);
		return newInvocationToMega;
	}

	public static CtMethod<?> createMegaMethod(MetaOperatorInstance opMega, ModificationPoint modificationPoint,
			CtExpression defaultReturnElement, int variableCounter, List<Ingredient> ingredients,
			List<CtParameter<?>> parameters, Map<Integer, Ingredient> ingredientOfMapped, CtTypeReference returnType) {

		parameters = filterParameters(parameters);
		int moiIdentifier = opMega.getIdentifier();
		String name = META_CNST + moiIdentifier + "_" + variableCounter;
		CtType<?> target = modificationPoint.getCodeElement().getParent(CtType.class);
		Set<ModifierKind> modifiers = new HashSet<>();
		modifiers.add(ModifierKind.PRIVATE);

		Set<CtTypeReference<? extends Throwable>> thrownTypes = new HashSet<>();

		// Here, we have to consider if the parent method is static.
		CtMethod parentMethod = modificationPoint.getCodeElement().getParent(CtMethod.class);
		if (parentMethod != null) {

			if (parentMethod.getModifiers().contains(ModifierKind.STATIC)) {
				modifiers.add(ModifierKind.STATIC);

			}
			// We add the throws declared by the method

			for (Object obT : parentMethod.getThrownTypes()) {
				CtTypeReference<? extends Throwable> typeEx = ((CtTypeReference<? extends Throwable>) obT).clone();
				typeEx.setPosition(new NoSourcePosition());
				thrownTypes.add(typeEx);

			}
		}

		parameters.stream().forEach(e -> e.setPositions(new NoSourcePosition()));

		CtMethod<?> megaMethod = MutationSupporter.getFactory().createMethod(target, modifiers, returnType, name,
				parameters, thrownTypes);

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
		// Let's start the counter according to the number of operation mutants we
		// already have
		int candidateNumber = ingredientOfMapped.keySet().size();
		for (Ingredient ingredientCandidate : ingredients) {

			candidateNumber++;
			CtExpression expressionCandidate = (CtExpression) ingredientCandidate.getCode();
			CtExpression expCloned = expressionCandidate.clone();
			expCloned.setPosition(new NoSourcePosition());
			MutationSupporter.clearPosition(expCloned);

			CtCodeSnippetExpression caseCondition = MutationSupporter.getFactory().createCodeSnippetExpression("\""
					+ candidateNumber + "\".equals(System.getProperty(\"" + MUT_IDENTIFIER + moiIdentifier + "\")) ");

			ingredientOfMapped.put(candidateNumber, ingredientCandidate);

			CtIf particularIf = MutationSupporter.getFactory().createIf();
			particularIf.setCondition(caseCondition);
			CtBlock particularIfBlock = new CtBlockImpl<>();

			if (ConfigurationProperties.getPropertyBool("meta_add_syso")) {

				CtStatement stPrint = MutationSupporter.getFactory().createCodeSnippetStatement("System.out.println("
						+ "\"\\nPROPERTY met:\" +System.getProperty(\"" + MUT_IDENTIFIER + moiIdentifier + "\"))");
				particularIfBlock.addStatement(stPrint);
			}
			particularIf.setThenStatement(particularIfBlock);

			// The return inside the if
			// add a return with the expression
			CtReturn casereturn = new CtReturnImpl<>();
			casereturn.setReturnedExpression(expCloned);
			particularIfBlock.addStatement(casereturn);

			// Add the if to the methodBlock
			// methodBodyBlock
			tryBoddy.addStatement(particularIf);

		}

		// By default, return the original
		CtReturn defaultReturnLast = new CtReturnImpl<>();
		CtExpression expCloned = defaultReturnElement.clone();
		expCloned.setPosition(new NoSourcePosition());
		MutationSupporter.clearPosition(expCloned);

		defaultReturnLast.setReturnedExpression(expCloned);
		methodBodyBlock.addStatement(defaultReturnLast);
		return megaMethod;
	}

	private static List<CtParameter<?>> filterParameters(List<CtParameter<?>> parameters) {
		List<CtParameter<?>> parametersN = new ArrayList<>();
		Set<String> seen = new HashSet();
		for (CtParameter<?> ctParameter : parameters) {
			if (!seen.contains(ctParameter.getSimpleName())) {
				parametersN.add(ctParameter);
				seen.add(ctParameter.getSimpleName());
			}
		}

		return parametersN;
	}

	public static Map<CtElement, CtElement> sourceTarget = new HashMap<>();

	public static Map<CtElement, CtElement> getSourceTarget() {
		return sourceTarget;
	}

	public static CtElement geOriginalElement(CtElement element) {
		if (!MetaGenerator.getSourceTarget().containsKey(element)) {
			return element;
		}

		if (!getSourceTarget().keySet().stream().filter(e -> e == element).findFirst().isPresent()) {
			return element;
		}

		CtElement replacement = MetaGenerator.getSourceTarget().get(element);

		return (replacement != null) ? replacement : element;

	}

}
