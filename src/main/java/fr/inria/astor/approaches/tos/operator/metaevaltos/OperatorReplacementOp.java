package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fr.inria.astor.approaches.cardumen.FineGrainedExpressionReplaceOperator;
import fr.inria.astor.approaches.jmutrepair.MutantCtElement;
import fr.inria.astor.approaches.jmutrepair.operators.LogicalBinaryOperatorMutator;
import fr.inria.astor.approaches.jmutrepair.operators.RelationalBinaryOperatorMutator;
import fr.inria.astor.approaches.tos.core.InsertMethodOperator;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.entities.meta.MetaOperator;
import fr.inria.astor.core.entities.meta.MetaOperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
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
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtBlockImpl;
import spoon.support.reflect.code.CtReturnImpl;

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

		CtType<?> target = modificationPoint.getCodeElement().getParent(CtType.class);
		Set<ModifierKind> modifiers = new HashSet<>();
		modifiers.add(ModifierKind.PRIVATE);
// modifiers.add(ModifierKind.STATIC);
		CtTypeReference returnTypeBoolean = MutationSupporter.getFactory().createCtTypeReference(Boolean.class);

// get all binary expressions
		List<CtBinaryOperator> booleanExpressionsInModificationPoints = modificationPoint.getCodeElement()
				.getElements(new TypeFilter<>(CtBinaryOperator.class)).stream()
				.filter(e -> SupportOperators.isBooleanType(e)).collect(Collectors.toList());

		log.debug("\nLogicExp: \n" + modificationPoint);

		int candidateNumber = 0;

// As difference with var replacement, a metamutant for each expression
		for (CtExpression<Boolean> expressionToExpand : booleanExpressionsInModificationPoints) {
			List<OperatorInstance> opsOfVariant = new ArrayList();

			int variableCounter = 0;
			Map<Integer, Ingredient> ingredientOfMapped = new HashMap<>();

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
			String name = "_meta_" + variableCounter;

			Set<CtTypeReference<? extends Throwable>> thrownTypes = new HashSet<>();

			CtMethod<?> megaMethod = MutationSupporter.getFactory().createMethod(target, modifiers, returnTypeBoolean,
					name, parameters, thrownTypes);

			CtInvocation newInvocationToMega = MutationSupporter.getFactory().createInvocation(
					MutationSupporter.getFactory().createThisAccess(MutationSupporter.getFactory().Type().objectType(),
							true),
					// thisTarget,
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
				CtBlock particularIfBlock = new CtBlockImpl<>();

				if (ConfigurationProperties.getPropertyBool("meta_add_syso")) {

					CtStatement stPrint = MutationSupporter.getFactory().createCodeSnippetStatement(
							"System.out.println(" + "\"\\nPROPERTY met:\" +System.getProperty(\"mutnumber\"))");
					particularIfBlock.addStatement(stPrint);
				}
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
			CtExpression expCloned = expressionToExpand.clone();
			expCloned.setPosition(new NoSourcePosition());
			MutationSupporter.clearPosition(expCloned);

			defaultReturnLast.setReturnedExpression(expCloned);
			methodBodyBlock.addStatement(defaultReturnLast);

			/// ***
			// Up to here, the different cases

			// Now the if to be inserted:
			// 1:

			CtElement elementSource = expressionToExpand;
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