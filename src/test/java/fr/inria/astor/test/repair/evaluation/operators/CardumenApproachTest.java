package fr.inria.astor.test.repair.evaluation.operators;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.approaches.cardumen.CardumenApproach;
import fr.inria.astor.approaches.jgenprog.operators.ExpressionReplaceOperator;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.ProbabilisticIngredientStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ExpressionTypeIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.IngredientSpaceScope;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.DynamicIngredient;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.ProbabilisticTransformationStrategy;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.validation.validators.ProgramValidator;
import fr.inria.astor.test.repair.evaluation.regression.MathTests;
import fr.inria.astor.util.CommandSummary;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

public class CardumenApproachTest {

	protected Logger log = Logger.getLogger(this.getClass().getName());

	@Test
	public void testCardumentM70() throws Exception {
		CommandSummary command = MathTests.getMath70Command();
		command.command.put("-mode", ExecutionMode.CARDUMEN.name());
		command.command.put("-flthreshold", "0.01");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		CardumenApproach cardumen = (CardumenApproach) main1.getEngine();

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen.getIngredientStrategy()
				.getIngredientSpace();
		assertNotNull(ingredientSpace);

		ProbabilisticIngredientStrategy ingredientStrategy = (ProbabilisticIngredientStrategy) cardumen
				.getIngredientSearchStrategy();

		assertNotNull(ingredientStrategy);

		ProbabilisticTransformationStrategy probTransformation = (ProbabilisticTransformationStrategy) cardumen
				.getIngredientTransformationStrategy();
		assertNotNull(probTransformation);

		assertTrue(Boolean.valueOf(ConfigurationProperties.getProperty("cleantemplates")));

		assertEquals(1, cardumen.getOperatorSpace().size());

		assertTrue(ExpressionReplaceOperator.class.isInstance(cardumen.getOperatorSpace().getOperators().get(0)));

	}

	@Test
	public void testCardumentM70ScopeLocal() throws Exception {
		CommandSummary command = MathTests.getMath70Command();

		IngredientSpaceScope scope = IngredientSpaceScope.LOCAL;

		command.command.put("-mode", ExecutionMode.CARDUMEN.name());
		command.command.put("-flthreshold", "0.01");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");
		command.command.put("-scope", scope.toString().toLowerCase());

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		CardumenApproach cardumen = (CardumenApproach) main1.getEngine();

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen.getIngredientStrategy()
				.getIngredientSpace();
		assertNotNull(ingredientSpace);

		assertEquals(scope, ingredientSpace.scope);

		ProgramVariant pvar = cardumen.getVariants().get(0);

		CtElement codeElement0 = pvar.getModificationPoints().stream()
				.filter(e -> e.getCodeElement().toString().equals("solve(min, max)")).findFirst().get()
				.getCodeElement();
		List<CtCodeElement> ingredients = ingredientSpace.getIngredients(codeElement0);

		List<String> locations = ingredientSpace.getLocations();

		System.out.println("Locations " + locations);

		for (CtCodeElement ingredientElement : ingredients) {
			assertEquals(codeElement0.getParent(CtType.class).getQualifiedName(),
					ingredientElement.getParent(CtType.class).getQualifiedName());
			assertEquals(((CtExpression) codeElement0).getType().getQualifiedName(),
					((CtExpression) ingredientElement).getType().getQualifiedName());
			assertEquals("org.apache.commons.math.analysis.solvers.BisectionSolver",
					ingredientElement.getParent(CtType.class).getQualifiedName());
		}

		///
		String p1 = "org.apache.commons.math.analysis.solvers.UnivariateRealSolverImpl";
		ModificationPoint mp40 = pvar.getModificationPoints().stream()
				.filter(e -> e.getCtClass().getQualifiedName().equals(p1)).findFirst().get();
		CtElement codeElement40 = mp40.getCodeElement();
		assertEquals(p1, mp40.getCtClass().getQualifiedName());
		List<CtCodeElement> ingredients40 = ingredientSpace.getIngredients(codeElement40);

		List<?> locations40 = ingredientSpace.getLocations();

		boolean withName = false;
		for (Object location : locations40) {
			if (((MultiKey) location).getKey(0).toString()
					.contains("org.apache.commons.math.analysis.solvers.UnivariateRealSolverImp"))
				withName = true;
		}
		assertTrue(withName);

		System.out.println("Locations " + locations40);

		for (CtCodeElement ingredientElement : ingredients40) {
			assertEquals(codeElement40.getParent(CtType.class).getQualifiedName(),
					ingredientElement.getParent(CtType.class).getQualifiedName());
			assertEquals(((CtExpression) codeElement40).getType().getQualifiedName(),
					((CtExpression) ingredientElement).getType().getQualifiedName());
			assertEquals("org.apache.commons.math.analysis.solvers.UnivariateRealSolverImpl",
					ingredientElement.getParent(CtType.class).getQualifiedName());
		}

	}

	@Test
	public void testCardumentM70ScopePackage() throws Exception {
		CommandSummary command = MathTests.getMath70Command();

		IngredientSpaceScope scope = IngredientSpaceScope.PACKAGE;

		command.command.put("-mode", ExecutionMode.CARDUMEN.name());
		command.command.put("-flthreshold", "0.01");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");
		command.command.put("-scope", scope.toString().toLowerCase());

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		CardumenApproach cardumen = (CardumenApproach) main1.getEngine();

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen.getIngredientStrategy()
				.getIngredientSpace();
		assertNotNull(ingredientSpace);

		assertEquals(scope, ingredientSpace.scope);

		ProgramVariant pvar = cardumen.getVariants().get(0);

		CtElement codeElement0 = pvar.getModificationPoints().stream()
				.filter(e -> e.getCodeElement().toString().equals("solve(min, max)")).findFirst().get()
				.getCodeElement();

		List<CtCodeElement> ingredients = ingredientSpace.getIngredients(codeElement0);

		List<String> locations = ingredientSpace.getLocations();

		System.out.println("Locations " + locations);

		for (CtCodeElement ingredientElement : ingredients) {
			assertEquals(codeElement0.getParent(CtPackage.class).getQualifiedName(),
					ingredientElement.getParent(CtPackage.class).getQualifiedName());
			assertEquals(((CtExpression) codeElement0).getType().getQualifiedName(),
					((CtExpression) ingredientElement).getType().getQualifiedName());
			assertEquals("org.apache.commons.math.analysis.solvers",
					ingredientElement.getParent(CtPackage.class).getQualifiedName());
		}

		///

		String p1 = "org.apache.commons.math.analysis.solvers.UnivariateRealSolverImpl";
		ModificationPoint mp40 = pvar.getModificationPoints().stream()
				.filter(e -> e.getCtClass().getQualifiedName().equals(p1)).findFirst().get();
		assertEquals("org.apache.commons.math.analysis.solvers.UnivariateRealSolverImpl",
				mp40.getCtClass().getQualifiedName());
		CtElement codeElement40 = mp40.getCodeElement();

		List<CtCodeElement> ingredients40 = ingredientSpace.getIngredients(codeElement40);

		List<?> locations40 = ingredientSpace.getLocations();

		boolean withName = false;
		for (Object location : locations40) {
			if (((MultiKey) location).getKey(0).toString().equals("org.apache.commons.math.analysis.solvers"))
				withName = true;
		}
		assertTrue(withName);

		System.out.println("Locations " + locations40);

		for (CtCodeElement ingredientElement : ingredients40) {
			assertEquals(codeElement40.getParent(CtPackage.class).getQualifiedName(),
					ingredientElement.getParent(CtPackage.class).getQualifiedName());
			assertEquals(((CtExpression) codeElement40).getType().getQualifiedName(),
					((CtExpression) ingredientElement).getType().getQualifiedName());
			assertEquals("org.apache.commons.math.analysis.solvers",
					ingredientElement.getParent(CtPackage.class).getQualifiedName());
		}

	}

	@Test
	public void testCardumentM70TransformationBugSameCombination() throws Exception {
		CommandSummary command = MathTests.getMath70Command();

		IngredientSpaceScope scope = IngredientSpaceScope.PACKAGE;

		command.command.put("-mode", ExecutionMode.CARDUMEN.name());
		command.command.put("-flthreshold", "0.01");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");
		command.command.put("-scope", scope.toString().toLowerCase());
		command.command.put("-parameters", "disablelog:true");
		int maxCombinations = 30000;
		command.command.put("-parameters", "maxVarCombination:" + maxCombinations + ":disablelog:true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		CardumenApproach cardumen = (CardumenApproach) main1.getEngine();

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen.getIngredientStrategy()
				.getIngredientSpace();
		assertNotNull(ingredientSpace);

		assertEquals(scope, ingredientSpace.scope);

		ProgramVariant pvar = cardumen.getVariants().get(0);
		int mpi = 0;
		for (ModificationPoint mp : pvar.getModificationPoints()) {

			System.out.println("--mp " + (mpi) + " [" + mp.getCodeElement().getClass().getCanonicalName() + "] " + mp);
			CtElement codeElement0 = mp.getCodeElement();
			List<CtCodeElement> ingredients = ingredientSpace.getIngredients(codeElement0);
			int t = 0;
			for (CtCodeElement ctCodeElement : ingredients) {

				System.out.println("--Template-->" + ctCodeElement);

				Ingredient ingredientToModify = new Ingredient(ctCodeElement);

				List<Ingredient> ingredientsTransformed = cardumen.getIngredientTransformationStrategy().transform(mp,
						ingredientToModify);
				System.out.println(
						"--mp " + (mpi) + " [" + mp.getCodeElement().getClass().getCanonicalName() + "] " + mp);

				System.out.println("--Template-" + (t++) + " ->" + ctCodeElement);

				Set<String> ing = new HashSet<>();
				int c = 0;
				for (Ingredient ingredientInstantiated : ingredientsTransformed) {
					c++;
					if (c < 100)
						System.out.println("------Instance-->" + ingredientInstantiated.getCode());

					// TODO: Should not be duplicates
					// assertFalse(ing.contains(ingredientInstantiated.getCode().toString()));
					ing.add(ingredientInstantiated.getCode().toString());

				}
				mpi++;
			}

		}

	}

	@Test
	public void testCardumentM70TransformationBugSameCombination2() throws Exception {
		CommandSummary command = MathTests.getMath70Command();

		IngredientSpaceScope scope = IngredientSpaceScope.PACKAGE;

		command.command.put("-mode", ExecutionMode.CARDUMEN.name());
		command.command.put("-flthreshold", "0.01");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");
		command.command.put("-scope", scope.toString().toLowerCase());
		command.command.put("-parameters", "disablelog:true");
		int maxCombinations = 30000;
		command.command.put("-parameters", "maxVarCombination:" + maxCombinations + ":disablelog:true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		CardumenApproach cardumen = (CardumenApproach) main1.getEngine();

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen.getIngredientStrategy()
				.getIngredientSpace();
		assertNotNull(ingredientSpace);

		assertEquals(scope, ingredientSpace.scope);

		ProgramVariant pvar = cardumen.getVariants().get(0);
		int mpi = 0;
		ModificationPoint mp = pvar.getModificationPoints().get(0);

		System.out.println("--mp " + (mpi) + " [" + mp.getCodeElement().getClass().getCanonicalName() + "] " + mp);
		CtElement codeElement0 = mp.getCodeElement();
		List<CtCodeElement> ingredients = ingredientSpace.getIngredients(codeElement0);
		int t = 0;
		CtCodeElement ctCodeElement = ingredients.stream()
				.filter(e -> e.toString().equals("solve(_UnivariateRealFunction_0, _double_1, _double_2)")).findFirst()
				.get();

		System.out.println("--Template-->" + ctCodeElement);

		Ingredient ingredientToModify = new Ingredient(ctCodeElement);

		List<Ingredient> ingredientsTransformed = cardumen.getIngredientTransformationStrategy().transform(mp,
				ingredientToModify);
		System.out.println("--mp " + (mpi) + " [" + mp.getCodeElement().getClass().getCanonicalName() + "] " + mp);

		System.out.println("--Template-" + (t++) + " ->" + ctCodeElement);

		Set<String> ing = new HashSet<>();

		for (Ingredient ingredientInstantiated : ingredientsTransformed) {

			System.out.println("------Instance-->" + ingredientInstantiated.getCode());

			// TODO: Should not be duplicates
			// assertFalse(ing.contains(ingredientInstantiated.getCode().toString()));
			ing.add(ingredientInstantiated.getCode().toString());

		}

	}

	@Test
	public void testCardumentM70bugHEAP() throws Exception {
		CommandSummary command = MathTests.getMath70Command();

		IngredientSpaceScope scope = IngredientSpaceScope.PACKAGE;

		command.command.put("-mode", ExecutionMode.CARDUMEN.name());
		command.command.put("-flthreshold", "0.01");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");
		command.command.put("-scope", scope.toString().toLowerCase());
		int maxCombinations = 3000;
		command.command.put("-parameters", "maxVarCombination:" + maxCombinations + ":disablelog:true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		assertEquals((Integer) maxCombinations, (Integer) ConfigurationProperties.getPropertyInt("maxVarCombination"));

		CardumenApproach cardumen = (CardumenApproach) main1.getEngine();

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen.getIngredientStrategy()
				.getIngredientSpace();
		assertNotNull(ingredientSpace);

		assertEquals(scope, ingredientSpace.scope);

		ProgramVariant pvar = cardumen.getVariants().get(0);

		ModificationPoint mp = pvar.getModificationPoints().stream()
				.filter(e -> e.getCodeElement().toString().equals("i < (maximalIterationCount)")).findFirst().get();
		CtElement codeElement0 = mp.getCodeElement();
		List<CtCodeElement> ingredients = ingredientSpace.getIngredients(codeElement0);

		String template = "((_double_0 < _double_1) || (_double_0 > _double_2))";

		CtCodeElement ctCodeElement = ingredients.stream().filter(e -> e.toString().equals(template)).findFirst().get();

		System.out.println("MP code: " + codeElement0);

		System.out.println("--Template-->" + ctCodeElement);

		Ingredient ingredientToModify = new Ingredient(ctCodeElement);

		List<Ingredient> ingredientsTransformed = cardumen.getIngredientTransformationStrategy().transform(mp,
				ingredientToModify);

		// 3_13 = 2197
		assertEquals(2197, ingredientsTransformed.size());
		assertTrue(ingredientsTransformed.size() + "vs " + maxCombinations,
				ingredientsTransformed.size() <= maxCombinations);

		Set<String> ing = new HashSet<>();

		int i = 0;
		for (Ingredient ingredient : ingredientsTransformed) {
			System.out.println("--Ing-->" + (i++) + " " + ingredient);
			String transformation = ingredient.getCode().toString();
			assertFalse(ing.contains(transformation));
			ing.add(transformation);

			assertFalse("((max < min) || (min > max))".equals(transformation));
		}

	}

	@Test
	@Ignore
	public void testCardumentM70InspectIngredients() throws Exception {
		CommandSummary command = MathTests.getMath70Command();

		IngredientSpaceScope scope = IngredientSpaceScope.PACKAGE;

		command.command.put("-mode", ExecutionMode.CARDUMEN.name());
		command.command.put("-flthreshold", "0.01");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");
		command.command.put("-scope", scope.toString().toLowerCase());

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		CardumenApproach cardumen = (CardumenApproach) main1.getEngine();

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen.getIngredientStrategy()
				.getIngredientSpace();
		assertNotNull(ingredientSpace);

		assertEquals(scope, ingredientSpace.scope);

		ProgramVariant pvar = cardumen.getVariants().get(0);
		int mpi = 0;
		ModificationPoint mp = pvar.getModificationPoints().get(245);

		System.out.println("--mp " + (mpi) + " [" + mp.getCodeElement().getClass().getCanonicalName() + "] " + mp);
		CtElement codeElement0 = mp.getCodeElement();
		List<CtCodeElement> ingredients = ingredientSpace.getIngredients(codeElement0);

		CtCodeElement ctCodeElement = ingredients.get(23);

		System.out.println("--Template-->" + ctCodeElement);

		Ingredient ingredientToModify = new Ingredient(ctCodeElement);

		List<Ingredient> ingredientsTransformed = cardumen.getIngredientTransformationStrategy().transform(mp,
				ingredientToModify);
		System.out.println("--mp " + (mpi) + " [" + mp.getCodeElement().getClass().getCanonicalName() + "] " + mp);

		int c = 0;
		for (Ingredient ingredientInstantiated : ingredientsTransformed) {
			c++;
			if (c < 100)
				System.out.println("------Instance-->" + ingredientInstantiated.getCode());

		}

	}

	@Test
	public void testCardumentM70IngredientPartialMapped() throws Exception {
		CommandSummary command = MathTests.getMath70Command();

		IngredientSpaceScope scope = IngredientSpaceScope.PACKAGE;

		command.command.put("-mode", ExecutionMode.CARDUMEN.name());
		command.command.put("-flthreshold", "0.01");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");
		command.command.put("-scope", scope.toString().toLowerCase());

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		CardumenApproach cardumen = (CardumenApproach) main1.getEngine();

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen.getIngredientStrategy()
				.getIngredientSpace();
		assertNotNull(ingredientSpace);

		assertEquals(scope, ingredientSpace.scope);

		ProgramVariant pvar = cardumen.getVariants().get(0);

		ModificationPoint mp15 = pvar.getModificationPoints().stream()
				.filter(e -> e.getCodeElement().toString().equals("(a + b) * 0.5")).findFirst().get();

		assertNotNull(mp15);
		System.out.println("-->" + mp15.getCodeElement());
		List<CtCodeElement> ingredients = ingredientSpace.getIngredients(mp15.getCodeElement());

		String code = "solve(_UnivariateRealFunction_0, _double_1, _double_2)";

		CtCodeElement template = ingredients.stream().filter(e -> e.toString().equals(code)).findFirst().get();
		Ingredient ingredientToModify = new Ingredient(template);
		assertNotNull(ingredientToModify);
		assertEquals(code, template.toString());

		List<Ingredient> ingredientsTransformed = cardumen.getIngredientTransformationStrategy().transform(mp15,
				ingredientToModify);
		System.out.println("MPoint: " + mp15);

		assertTrue(ingredientsTransformed.isEmpty());

		///
		System.out.println("Checking ");
		ModificationPoint mp0 = pvar.getModificationPoints().stream()
				.filter(e -> e.getCodeElement().toString().equals("solve(min, max)")).findFirst().get();
		assertEquals("solve(min, max)", mp0.getCodeElement().toString());

		List<CtCodeElement> ingredients0 = ingredientSpace.getIngredients(mp0.getCodeElement());

		CtCodeElement element = ingredients0.stream().filter(e -> code.equals(e.toString())).findFirst().get();
		Ingredient ingredientToModify0 = new Ingredient(element);
		assertEquals(code, element.toString());

		List<Ingredient> ingredientsTransformed0 = cardumen.getIngredientTransformationStrategy().transform(mp0,
				ingredientToModify0);
		System.out.println("MPoint: " + mp15);

		boolean found = false;
		assertTrue(!ingredientsTransformed0.isEmpty());
		for (Ingredient ingredient : ingredientsTransformed0) {
			System.out.println(ingredient.getCode());
			if (ingredient.getCode().toString().equals("solve(f, min, max)")) {
				found = true;
			}
		}
		assertTrue(found);
	}

	@Test
	@Ignore
	@Deprecated // since we dont manage the VarAccess
	public void testCardumentM70TransformationReference() throws Exception {
		CommandSummary command = MathTests.getMath70Command();

		IngredientSpaceScope scope = IngredientSpaceScope.PACKAGE;

		command.command.put("-mode", ExecutionMode.CARDUMEN.name());
		command.command.put("-flthreshold", "0.01");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");
		command.command.put("-scope", scope.toString().toLowerCase());

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		CardumenApproach cardumen = (CardumenApproach) main1.getEngine();

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen.getIngredientStrategy()
				.getIngredientSpace();
		assertNotNull(ingredientSpace);

		assertEquals(scope, ingredientSpace.scope);

		ProgramVariant pvar = cardumen.getVariants().get(0);

		System.out.println("Checking ");
		ModificationPoint mp15 = pvar.getModificationPoints().get(15);

		assertEquals("lower >= upper", mp15.getCodeElement().toString());

		List<CtCodeElement> ingredients = ingredientSpace.getIngredients(mp15.getCodeElement());
		System.out.println(ingredients);
		assertEquals("_double_0", ingredients.get(1).toString());

		Ingredient ingredientToModify = new Ingredient(ingredients.get(1));

		List<Ingredient> ingredientsTransformed = cardumen.getIngredientTransformationStrategy().transform(mp15,
				ingredientToModify);

		for (Ingredient ingInstance : ingredientsTransformed) {
			assertTrue(DynamicIngredient.class.isInstance(ingInstance));
			System.out.println(ingInstance.getCode());
			String c = ingInstance.getCode().toString();
			assertFalse("_double_0".equals(c));
		}

	}

	@Test
	public void testCardumentM70Evolve() throws Exception {
		CommandSummary command = MathTests.getMath70Command();

		IngredientSpaceScope scope = IngredientSpaceScope.PACKAGE;

		command.command.put("-mode", ExecutionMode.CARDUMEN.name());
		command.command.put("-flthreshold", "0.01");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "400");
		command.command.put("-maxgen", "200");
		command.command.put("-population", "1");
		command.command.put("-scope", scope.toString().toLowerCase());
		command.command.put("-parameters", "disablelog:true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		CardumenApproach cardumen = (CardumenApproach) main1.getEngine();

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen.getIngredientStrategy()
				.getIngredientSpace();
		assertNotNull(ingredientSpace);

		assertTrue(cardumen.getSolutions().size() > 0);
	}
}
