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
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
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
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtBlockImpl;
import spoon.support.reflect.code.CtReturnImpl;

/**
 * Initial concept of met engine. Only for test.
 * 
 * @author Matias Martinez
 *
 */
public class InitialConceptMetEngine {

	protected static Logger log = Logger.getLogger(Thread.currentThread().getName());

	public List<OperatorInstance> transform(ProgramVariant variant, ModificationPoint modificationPoint,
			CtExpression expToChange, List<Ingredient> candidates) {

		log.debug("nr candidates " + candidates + " of variant " + variant.getId());

		int id = variant.getId();

		CtType<?> target = expToChange.getParent(CtType.class);
		Set<ModifierKind> modifiers = new HashSet<>();
		modifiers.add(ModifierKind.PRIVATE);
		modifiers.add(ModifierKind.STATIC);

		// The return type of the new method correspond to the type of the expression to
		// change
		CtTypeReference returnType = expToChange.getType();
		String name = "_meta_" + id;

		// The parameters to be included in the new method
		List<CtVariableAccess> varsToBeParameters = collectAllVars(expToChange, candidates);

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

		Set<CtTypeReference<? extends Throwable>> thrownTypes = new HashSet<>();

		CtExpression thisTarget = MutationSupporter.getFactory().createTypeAccess(target.getReference());

		CtMethod<?> megaMethod = MutationSupporter.getFactory().createMethod(target, modifiers, returnType, name,
				parameters, thrownTypes);

		CtInvocation newInvocationToMega = MutationSupporter.getFactory().createInvocation(thisTarget,
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

		// let's start with one, and let's keep the Zero for the default (all ifs are
		// false)
		int candidateNumber = 1;
		Map<Integer, Ingredient> nrCandidate = new HashMap<>();
		for (Ingredient ingredientCandidate : candidates) {

			CtExpression expressionCandidate = (CtExpression) ingredientCandidate.getCode();
			CtCodeSnippetExpression caseCondition = MutationSupporter.getFactory().createCodeSnippetExpression(
					"\"" + candidateNumber + "\".equals(System.getProperty(\"mutnumber\")) ");

			nrCandidate.put(candidateNumber, ingredientCandidate);

			CtIf particularIf = MutationSupporter.getFactory().createIf();
			particularIf.setCondition(caseCondition);
			CtStatement stPrint = MutationSupporter.getFactory().createCodeSnippetStatement(
					"System.out.println(" + "\"\\nPROPERTY met:\" +System.getProperty(\"mutnumber\"))");
			CtBlock particularIfBlock = new CtBlockImpl<>();
			particularIfBlock.addStatement(stPrint);
			particularIf.setThenStatement(particularIfBlock);

			// The return inside the if
			// add a return with the expression
			CtReturn _return = new CtReturnImpl<>();
			_return.setReturnedExpression(expressionCandidate);
			particularIfBlock.addStatement(_return);

			// Add the if tho the methodBlock
			// methodBodyBlock
			tryBoddy.addStatement(particularIf);
			candidateNumber++;

		}
		// By default, return the original
		CtReturn _returnLast = new CtReturnImpl<>();
		CtExpression expCloned = expToChange.clone();
		expCloned.setPosition(new NoSourcePosition());
		clearPosition(expCloned);

		_returnLast.setReturnedExpression(expCloned);
		methodBodyBlock.addStatement(_returnLast);

		// Let's create the operations
		List<OperatorInstance> opsOfVariant = new ArrayList();

		OperatorInstance opInvocation = new OperatorInstance();
		opInvocation.setOperationApplied(new FineGrainedExpressionReplaceOperator());
		opInvocation.setOriginal(expToChange);
		opInvocation.setModified(newInvocationToMega);
		opInvocation.setModificationPoint(modificationPoint);

		opsOfVariant.add(opInvocation);

		OperatorInstance opMethodAdd = new OperatorInstance();
		opMethodAdd.setOperationApplied(new InsertMethodOperator());
		opMethodAdd.setOriginal(expToChange);
		opMethodAdd.setModified(megaMethod);
		opMethodAdd.setModificationPoint(modificationPoint);
		opsOfVariant.add(opMethodAdd);

		System.out.println("method: \n" + megaMethod);

		System.out.println("invocation: \n" + newInvocationToMega);

		return opsOfVariant;
	}

	public void clearPosition(CtExpression expCloned) {
		expCloned.getElements(e -> true).stream().forEach(e -> e.setPosition(new NoSourcePosition()));
	}

	/**
	 * Add all variables from the expression and candidates in a list
	 * 
	 * @param exptochange
	 * @param candidates
	 * @return
	 */
	private List<CtVariableAccess> collectAllVars(CtExpression exptochange, List<Ingredient> candidates) {
		List<CtVariableAccess> varAccess = VariableResolver.collectVariableAccess(exptochange);

		for (Ingredient candidateIngr : candidates) {
			CtElement candidate = candidateIngr.getCode();
			List<CtVariableAccess> varAccessCandidate = VariableResolver.collectVariableAccess(candidate);
			for (CtVariableAccess varX : varAccessCandidate) {
				if (!varAccess.contains(varX)) {
					varAccess.add(varX);
				}
			}
		}

		return varAccess;
	}
}
