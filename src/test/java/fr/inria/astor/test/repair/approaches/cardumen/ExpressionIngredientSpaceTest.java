package fr.inria.astor.test.repair.approaches.cardumen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.approaches.cardumen.ExpressionReplaceOperator;
import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.ExpressionIngredientSpaceProcessor;
import fr.inria.astor.core.manipulation.sourcecode.VarMapping;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPool;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPoolLocationType;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.AstorCtIngredientPool;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.ExpressionTypeIngredientSpace;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.InScopeVarsTransformation;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.IngredientTransformationStrategy;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import fr.inria.main.evolution.ExtensionPoints;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;

/**
 * 
 * @author Matias Martinez
 *
 */
@Ignore
public class ExpressionIngredientSpaceTest {

	protected Logger log = Logger.getLogger(this.getClass().getName());

	@Before
	public void setUp() throws Exception {
	}

	/**
	 * This test uses a new ingredient space specially created to manage
	 * expressions.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testM70ExpressionAdaptation() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-parameters", ExtensionPoints.TARGET_CODE_PROCESSOR.identifier + File.pathSeparator
				+ ExpressionIngredientSpaceProcessor.class.getCanonicalName() + File.pathSeparator
				+ ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier + File.pathSeparator
				+ InScopeVarsTransformation.class.getCanonicalName() + File.pathSeparator + "cleantemplates:false");
		command.command.put("-maxgen", "0");// Avoid evolution
		command.command.put(ExtensionPoints.REPAIR_OPERATORS.argument(), ExpressionReplaceOperator.class.getName());
		command.command.put("-scope", ExpressionTypeIngredientSpace.class.getName());
		command.command.put("-flthreshold", "0.1");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		List<ProgramVariant> variantss = main1.getEngine().getVariants();
		assertTrue(variantss.size() > 0);

		JGenProg engine = (JGenProg) main1.getEngine();
		ModificationPoint modificationPoint = variantss.get(0).getModificationPoints().get(14);

		assertEquals("i < (maximalIterationCount)", modificationPoint.getCodeElement().toString());

		// Let's inspect the ingredient space:
		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) engine
				.getIngredientSearchStrategy().getIngredientSpace();
		assertNotNull(ingredientSpace);
		assertTrue(ExpressionTypeIngredientSpace.class.isInstance(ingredientSpace));

		log.debug("Ingredient \n:" + ingredientSpace.getAllIngredients());

		// Let's test the creation of a operator instance.
		OperatorInstance opInstance = engine.createOperatorInstanceForPoint(modificationPoint);
		List<Ingredient> ingredients = ingredientSpace.getIngredients(opInstance.getOriginal(),
				ExpressionReplaceOperator.class.getName());

		log.debug("\nAll ingredients " + ingredients);

		CtElement ingredientTargeted = ingredients.get(4).getCode();

		assertEquals("maximumIterations <= 0", ingredientTargeted.toString());

		IngredientTransformationStrategy transfStrategy = engine.getIngredientTransformationStrategy();
		assertNotNull(transfStrategy);

		assertTrue(InScopeVarsTransformation.class.isInstance(transfStrategy));

		InScopeVarsTransformation inScopeStrategy = (InScopeVarsTransformation) transfStrategy;

		VarMapping mapping = VariableResolver.mapVariablesFromContext(modificationPoint.getContextOfModificationPoint(),
				ingredientTargeted);

		List<CtVariable> variablesMapped = mapping.getMappedVariables().values().iterator().next();
		assertNotNull(variablesMapped);

		// [protected int maximalIterationCount;, protected int
		// defaultMaximalIterationCount;, protected int iterationCount;, int i =
		// 0]
		assertEquals(4, variablesMapped.size());

		List<Ingredient> transformedIngredients = inScopeStrategy.transform(modificationPoint,
				new Ingredient(ingredientTargeted));

		log.debug("\nTransformed ingredients " + transformedIngredients);
		assertEquals(4, transformedIngredients.size());

		for (CtVariable ctVariableInScope : variablesMapped) {

			boolean ingredientTransformedHasVarInScope = false;
			for (Ingredient ingredient : transformedIngredients) {
				System.out.println(
						String.format(" %s %s ", ingredient.getCode().toString(), (ctVariableInScope.getSimpleName())));
				if (ingredient.getCode().toString().contains(ctVariableInScope.getSimpleName())) {
					ingredientTransformedHasVarInScope = true;
					break;
				}
			}
			assertTrue(ingredientTransformedHasVarInScope);

		}
		List<OperatorInstance> operatorsCreated = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			log.debug("\n OPERATION: \n " + i);
			OperatorInstance opi = engine.createOperatorInstanceForPoint(modificationPoint);
			// if(opi != null)
			operatorsCreated.add(opi);
			// else
			// log.debug("\n NULL OPERATION: \n "+i);
		}

		log.debug("ops " + operatorsCreated);
		for (OperatorInstance operatorInstance : operatorsCreated) {
			log.debug("\n opInstance: " + operatorInstance);

		}

		List<String> ingredientsUsed = operatorsCreated.stream().map(OperatorInstance::getModified)
				.map(CtElement::toString).distinct().collect(Collectors.toList());

		log.debug(String.format("ingredients used in ops [%d] %s", ingredientsUsed.size(), ingredientsUsed));

		assertEquals(operatorsCreated.size(), ingredientsUsed.size());

	}

	/**
	 * This test uses a new ingredient space specially created to manage
	 * expressions.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testM70ExpressionOperatorSpaceExpression() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-parameters",
				ExtensionPoints.TARGET_CODE_PROCESSOR.identifier + File.pathSeparator
						+ ExpressionIngredientSpaceProcessor.class.getCanonicalName() + File.pathSeparator
						+ "applytemplates:false");
		command.command.put("-maxgen", "0");// Avoid evolution
		command.command.put(ExtensionPoints.REPAIR_OPERATORS.argument(), ExpressionReplaceOperator.class.getName());
		command.command.put("-scope", ExpressionTypeIngredientSpace.class.getName());

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		List<ProgramVariant> variantss = main1.getEngine().getVariants();
		assertTrue(variantss.size() > 0);

		JGenProg engine = (JGenProg) main1.getEngine();
		ModificationPoint modificationPoint = variantss.get(0).getModificationPoints().get(14);

		assertEquals("i < (maximalIterationCount)", modificationPoint.getCodeElement().toString());

		// Let's inspect the ingredient space:
		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) engine
				.getIngredientSearchStrategy().getIngredientSpace();
		assertNotNull(ingredientSpace);
		assertTrue(ExpressionTypeIngredientSpace.class.isInstance(ingredientSpace));

		log.debug("keys: \n" + ingredientSpace.getLocations());

		log.debug("Ingredient \n:" + ingredientSpace.getAllIngredients());

		// Let's test the creation of a operator instance.
		OperatorInstance opInstance = engine.createOperatorInstanceForPoint(modificationPoint);

		assertNotNull(opInstance);
		assertEquals(ExpressionReplaceOperator.class.getName(), opInstance.getOperationApplied().getClass().getName());

		assertNotNull("Operator replacement needs an ingredient", opInstance.getModified());

		log.debug("Op instance created " + opInstance);
		log.debug("Op instance Ingredient:  " + opInstance.getModified());

		assertTrue(CtBinaryOperator.class.isInstance(opInstance.getOriginal()));
		log.debug("\n Type ingredient: " + ((CtBinaryOperator) opInstance.getOriginal()).getType());
		assertTrue(CtBinaryOperator.class.isInstance(opInstance.getModified()));

		CtBinaryOperator binOpIngredient = (CtBinaryOperator) opInstance.getModified();
		log.debug("\n Type ingredient: " + binOpIngredient.getType());

		assertEquals(((CtBinaryOperator) opInstance.getOriginal()).getType(), binOpIngredient.getType());

		List<Ingredient> ingredients = ingredientSpace.getIngredients(opInstance.getOriginal(),
				ExpressionReplaceOperator.class.getName());

		// let's check all ingredients
		for (Ingredient ingredient : ingredients) {
			assertTrue(CtBinaryOperator.class.isInstance(ingredient));
			assertEquals(((CtBinaryOperator) opInstance.getOriginal()).getType(),
					((CtBinaryOperator) ingredient.getCode()).getType());
		}

	}

	/**
	 * This test checks if it works fine a new operator that works at the expression
	 * level.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testM70ExpressionOperator() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-parameters",
				ExtensionPoints.TARGET_CODE_PROCESSOR.identifier + File.pathSeparator
						+ ExpressionIngredientSpaceProcessor.class.getCanonicalName() + File.pathSeparator
						+ "applytemplates:false");
		command.command.put("-maxgen", "0");// Avoid evolution
		command.command.put(ExtensionPoints.REPAIR_OPERATORS.argument(), ExpressionReplaceOperator.class.getName());

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		List<ProgramVariant> variantss = main1.getEngine().getVariants();
		assertTrue(variantss.size() > 0);

		JGenProg engine = (JGenProg) main1.getEngine();
		ModificationPoint modificationPoint = variantss.get(0).getModificationPoints().get(14);

		assertEquals("i < (maximalIterationCount)", modificationPoint.getCodeElement().toString());

		// Let's test the creation of a operator instance.
		OperatorInstance opInstance = engine.createOperatorInstanceForPoint(modificationPoint);

		assertNotNull(opInstance);
		assertEquals(ExpressionReplaceOperator.class.getName(), opInstance.getOperationApplied().getClass().getName());

		assertNotNull("Operator replacement needs an ingredient", opInstance.getModified());

		log.debug("Op instance created " + opInstance);
		log.debug("Op instance Ingredient:  " + opInstance.getModified());

		// Let's inspect the ingredient space:
		AstorCtIngredientPool ingredientSpace = (AstorCtIngredientPool) engine.getIngredientSearchStrategy()
				.getIngredientSpace();
		assertNotNull(ingredientSpace);

		assertTrue(CtBinaryOperator.class.isInstance(opInstance.getOriginal()));
		log.debug("\n Type ingredient: " + ((CtBinaryOperator) opInstance.getOriginal()).getType());
		assertTrue(CtBinaryOperator.class.isInstance(opInstance.getModified()));

		CtBinaryOperator binOpIngredient = (CtBinaryOperator) opInstance.getModified();
		log.debug("\n Type ingredient: " + binOpIngredient.getType());

	}

	/**
	 * This test checks if astor is able to manipulate expression as element in a
	 * modification point
	 * 
	 * @throws Exception
	 */
	@Test
	public void testM70ExpressionAsModificationPoint() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-parameters",
				ExtensionPoints.TARGET_CODE_PROCESSOR.identifier + File.pathSeparator
						+ ExpressionIngredientSpaceProcessor.class.getCanonicalName() + File.pathSeparator
						+ "applytemplates:false");
		command.command.put("-maxgen", "0");// Avoid evolution

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		List<ProgramVariant> variantss = main1.getEngine().getVariants();
		assertTrue(variantss.size() > 0);

		JGenProg jgp = (JGenProg) main1.getEngine();
		IngredientPoolLocationType ingSpace = (IngredientPoolLocationType) jgp.getIngredientSearchStrategy()
				.getIngredientSpace();

		int i = 0;
		for (ModificationPoint modpoint : variantss.get(0).getModificationPoints()) {
			System.out.println("--> " + (i++) + " " + modpoint.getCodeElement());

		}
		ModificationPoint modificationPoint = variantss.get(0).getModificationPoints().get(14);
		CtExpression originalExp = (CtExpression) modificationPoint.getCodeElement();
		assertEquals("i < (maximalIterationCount)", originalExp.toString());
		CtClass parentClass = originalExp.getParent(CtClass.class);
		String parentClassString = parentClass.toString();

		assertNotNull(parentClass);
		// Let's mutate the expression
		CtExpression clonedExp = (CtExpression) MutationSupporter.clone(originalExp);
		CtBinaryOperator binOpExpr = (CtBinaryOperator) clonedExp;
		// Let's check that the operator that will be inserted is not equal to
		// the current.
		assertNotEquals(BinaryOperatorKind.GT, binOpExpr.getKind());
		// Update operator
		binOpExpr.setKind(BinaryOperatorKind.GT);

		ExpressionReplaceOperator expOperator = new ExpressionReplaceOperator();
		OperatorInstance expOperatorInstance = new StatementOperatorInstance(modificationPoint, expOperator,
				originalExp, clonedExp);

		boolean applied = expOperatorInstance.applyModification();
		assertTrue(applied);

		assertNotEquals(clonedExp, originalExp);
		assertEquals("i > (maximalIterationCount)", clonedExp.toString());
		assertEquals("i > (maximalIterationCount)", modificationPoint.getCodeElement().toString());

		// Let's check that the mutated class is different to the original
		String mutatedClassString = parentClass.toString();
		assertNotEquals(parentClassString, mutatedClassString);

		// Undo operator, we should have the original class
		boolean undo = expOperatorInstance.undoModification();
		String revertedChangeClassString = parentClass.toString();
		assertTrue(undo);
		assertEquals("i < (maximalIterationCount)", modificationPoint.getCodeElement().toString());

		assertEquals(parentClassString, revertedChangeClassString);

	}

	@Test
	public void testM70InspectSpace() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-parameters", ExtensionPoints.TARGET_CODE_PROCESSOR.identifier + File.pathSeparator
				+ ExpressionIngredientSpaceProcessor.class.getCanonicalName() + File.pathSeparator
				+ ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier + File.pathSeparator
				+ InScopeVarsTransformation.class.getCanonicalName() + File.pathSeparator + "applytemplates:false");
		command.command.put("-maxgen", "0");// Avoid evolution
		command.command.put(ExtensionPoints.REPAIR_OPERATORS.argument(), ExpressionReplaceOperator.class.getName());
		command.command.put("-scope", ExpressionTypeIngredientSpace.class.getName());
		command.command.put("-flthreshold", "0.1");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		List<ProgramVariant> variantss = main1.getEngine().getVariants();
		assertTrue(variantss.size() > 0);

		JGenProg engine = (JGenProg) main1.getEngine();
		ModificationPoint modificationPoint = variantss.get(0).getModificationPoints().get(14);

		IngredientPool space = engine.getIngredientSearchStrategy().getIngredientSpace();
		ExpressionTypeIngredientSpace expressionSpace = (ExpressionTypeIngredientSpace) space;
		assertNotNull(expressionSpace);

		// Here, the cuestion is if it return ingredients by scope, expression type, and
		// element class.

	}

}
