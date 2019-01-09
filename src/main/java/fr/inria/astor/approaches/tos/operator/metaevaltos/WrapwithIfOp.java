package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.approaches.tos.core.InsertMethodOperator;
import fr.inria.astor.approaches.tos.operator.DynaIngredientOperator;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.IngredientFromDyna;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.entities.meta.MetaOperator;
import fr.inria.astor.core.entities.meta.MetaOperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.lille.repair.expression.access.VariableImpl;
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
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtBlockImpl;
import spoon.support.reflect.code.CtReturnImpl;

/**
 * 
 * @author Matias Martinez
 *
 */
public class WrapwithIfOp extends ReplaceOp implements MetaOperator, DynaIngredientOperator {

	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {
		log.error("This op needs ingredients");
		return null;
	}

	@Override
	public CtTypeReference retrieveTargetTypeReference() {

		return MutationSupporter.getFactory().createCtTypeReference(Boolean.class);
	}

	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint,
			List<IngredientFromDyna> ingredients) {

		ProgramVariant variant = modificationPoint.getProgramVariant();

		int id = variant.getId();

		CtType<?> target = modificationPoint.getCodeElement().getParent(CtType.class);
		Set<ModifierKind> modifiers = new HashSet<>();
		modifiers.add(ModifierKind.PRIVATE);
		modifiers.add(ModifierKind.STATIC);

		// The return type of the new method correspond to the type of the expression to
		// change
		CtTypeReference returnType = MutationSupporter.getFactory().createCtTypeReference(Boolean.class);
		String name = "_meta_" + id;

		// The parameters to be included in the new method
		List<CtVariableAccess> varsToBeParameters = collectAllVars(ingredients, modificationPoint);

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
		int candidateNumber = 0;
		Map<Integer, Ingredient> ingredientOfMapped = new HashMap<>();

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

			// Add the if tho the methodBlock
			// methodBodyBlock
			tryBoddy.addStatement(particularIf);

		}

		// By default, return the original
		CtReturn defaultReturnLast = new CtReturnImpl<>();
		CtExpression expCloned = createDefaultExpression(returnType);
		expCloned.setPosition(new NoSourcePosition());
		MutationSupporter.clearPosition(expCloned);

		defaultReturnLast.setReturnedExpression(expCloned);
		methodBodyBlock.addStatement(defaultReturnLast);

		// Now the if to be inserted:

		CtIf ifNew = MutationSupporter.getFactory().createIf();

		CtStatement statementPointed = (CtStatement) modificationPoint.getCodeElement();
		CtStatement statementPointedCloned = statementPointed.clone();
		statementPointedCloned.setPosition(new NoSourcePosition());
		MutationSupporter.clearPosition(statementPointedCloned);

		ifNew.setThenStatement(statementPointedCloned);
		ifNew.setCondition(newInvocationToMega);

		// Let's create the operations
		List<OperatorInstance> opsOfVariant = new ArrayList();

		OperatorInstance opInstace = new StatementOperatorInstance(modificationPoint, this, statementPointed, ifNew);
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

		MetaOperatorInstance opMega = new MetaOperatorInstance(opsOfVariant);
		opMega.setAllIngredients(ingredientOfMapped);
		opMega.setOperationApplied(this);
		opMega.setOriginal(modificationPoint.getCodeElement());
		opMega.setModificationPoint(modificationPoint);

		List<OperatorInstance> opsMega = new ArrayList();
		opsMega.add(opMega);

		return opsMega;
	}

	private CtExpression createDefaultExpression(CtTypeReference returnType) {

		// By default returns true, which is, in the context of this operator, to
		// executed the wrapped statement
		return MutationSupporter.getFactory().createCodeSnippetExpression("true");
	}

	/**
	 * Add all variables from the expression and candidates in a list
	 * 
	 * @param exptochange
	 * @param candidates
	 * @param modificationPoint
	 * @return
	 */
	private List<CtVariableAccess> collectAllVars(List<IngredientFromDyna> candidates,
			ModificationPoint modificationPoint) {

		List<VariableImpl> dynaVars = new ArrayList<>();
		List<CtVariableAccess> varAccessList = new ArrayList();

		for (IngredientFromDyna candidateIngr : candidates) {
			dynaVars.addAll(candidateIngr.getVariable());
		}

		if (dynaVars.isEmpty()) {
			return varAccessList;
		}

		List<CtVariable> varAccessCandidate = modificationPoint.getContextOfModificationPoint();

		for (VariableImpl aDynaVariable : dynaVars) {

			for (CtVariable aVariableSinScope : varAccessCandidate) {

				if (aVariableSinScope.getSimpleName().equals(aDynaVariable.getVariableName())) {

					CtVariableAccess aVariableRead = MutationSupporter.getFactory()
							.createVariableRead(aVariableSinScope.getReference(), false);
					if (!varAccessList.contains(aVariableRead))
						varAccessList.add(aVariableRead);
				}
			}

		}

		return varAccessList;
	}

	@Override
	public OperatorInstance getConcreteOperatorInstance(MetaOperatorInstance operatorInstance, int metaIdentifier) {

		// We retrieve the information from the operator instance

		Ingredient ingredient = operatorInstance.getAllIngredients().get(metaIdentifier);

		ModificationPoint modificationPoint = operatorInstance.getModificationPoint();

		// We create the new operator
		CtIf ifNew = MutationSupporter.getFactory().createIf();

		CtStatement statementPointed = (CtStatement) modificationPoint.getCodeElement();
		CtStatement statementPointedCloned = statementPointed.clone();

		MutationSupporter.clearPosition(statementPointedCloned);
		ifNew.setThenStatement(statementPointedCloned);

		// as difference with the meta, here we put the ingredient evaluated in the
		// meta.
		ifNew.setCondition((CtExpression<Boolean>) ingredient.getCode());
		// Let's create the operations
		List<OperatorInstance> opsOfVariant = new ArrayList();

		OperatorInstance opInstace = new StatementOperatorInstance(modificationPoint, new ReplaceOp(), statementPointed,
				ifNew);
		opsOfVariant.add(opInstace);

		return opInstace;
	}

}
