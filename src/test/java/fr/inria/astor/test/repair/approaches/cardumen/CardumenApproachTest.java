package fr.inria.astor.test.repair.approaches.cardumen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.approaches.cardumen.CardumenApproach;
import fr.inria.astor.approaches.cardumen.CardumenExhaustiveEngine4Stats;
import fr.inria.astor.approaches.cardumen.ExpressionReplaceOperator;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.manipulation.sourcecode.VarCombinationForIngredient;
import fr.inria.astor.core.manipulation.sourcecode.VarMapping;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.AstorCoreEngine;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.ingredientSearch.ProbabilisticIngredientStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.ingredientSearch.RandomSelectionTransformedIngredientStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.ExpressionTypeIngredientSpace;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.DynamicIngredient;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.ProbabilisticTransformationStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.RandomTransformationStrategy;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.test.repair.approaches.TestHelper;
import fr.inria.astor.test.repair.core.BaseEvolutionaryTest;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.CommandSummary;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;

/**
 * 
 * @author Matias Martinez
 *
 */
public class CardumenApproachTest extends BaseEvolutionaryTest {

	protected Logger log = Logger.getLogger(this.getClass().getName());

	@Test
	public void testCardumentChart11() throws Exception {

		String depJunit411 = new File("./lib/junit-4.11.jar").getAbsolutePath();

		String bugLocation = new File("./examples/chart_11/").getAbsolutePath();

		CommandSummary command = new CommandSummary();

		command.command.put("-mode", ExecutionMode.CARDUMEN.name());
		command.command.put("-id", "Chart-11");
		command.command.put("-location", bugLocation);
		command.command.put("-srcjavafolder", "source");
		command.command.put("-srctestfolder", "tests");
		command.command.put("-binjavafolder", "build");
		command.command.put("-bintestfolder", "build-tests");

		command.command.put("-dependencies", bugLocation + File.separator + "/lib/servlet.jar" + File.pathSeparator
				+ bugLocation + File.pathSeparator + "/lib/itext-2.0.6.jar" + File.pathSeparator + depJunit411);

		command.command.put("-seed", "0");
		command.command.put("-scope", "local");
		command.command.put("-population", "1");
		command.command.put("-javacompliancelevel", "4");

		command.command.put("-flthreshold", "0.1");
		command.command.put("-maxtime", "60");
		command.command.put("-stopfirst", "true");
		command.command.put("-maxgen", "1000000");
		command.command.put("-population", "1");
		command.command.put("-loglevel", "INFO");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		AstorCoreEngine engine = main1.getEngine();

		assertEquals(1, engine.getSolutions().size());

		// Assert the patch
		assertTrue(super.existPatchWithCode(engine.getSolutions(), "p2.getPathIterator(null)"));
		// Assert patch is not the buggy code
		assertFalse(super.existPatchWithCode(engine.getSolutions(), "p1.getPathIterator(null)"));

	}

	@Test
	public void testCardumentM70() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", ExecutionMode.CARDUMEN.name());
		command.command.put("-flthreshold", "0.01");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		CardumenApproach cardumen = (CardumenApproach) main1.getEngine();

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen
				.getIngredientSearchStrategy().getIngredientSpace();
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
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientPoolScope scope = IngredientPoolScope.LOCAL;

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

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen
				.getIngredientSearchStrategy().getIngredientSpace();
		assertNotNull(ingredientSpace);

		assertEquals(scope, ingredientSpace.scope);

		ProgramVariant pvar = cardumen.getVariants().get(0);

		CtElement codeElement0 = pvar.getModificationPoints().stream()
				.filter(e -> e.getCodeElement().toString().equals("solve(min, max)")).findFirst().get()
				.getCodeElement();
		List<Ingredient> ingredients = ingredientSpace.getIngredients(codeElement0);

		List<String> locations = ingredientSpace.getLocations();

		log.debug("Locations " + locations);

		for (Ingredient ingredient : ingredients) {
			CtElement ingredientElement = ingredient.getCode();

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
		List<Ingredient> ingredients40 = ingredientSpace.getIngredients(codeElement40);

		List<?> locations40 = ingredientSpace.getLocations();

		boolean withName = false;
		for (Object location : locations40) {
			if (((MultiKey) location).getKey(0).toString()
					.contains("org.apache.commons.math.analysis.solvers.UnivariateRealSolverImp"))
				withName = true;
		}
		assertTrue(withName);

		log.debug("Locations " + locations40);

		for (Ingredient ingredient : ingredients40) {
			CtElement ingredientElement = ingredient.getCode();

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
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientPoolScope scope = IngredientPoolScope.PACKAGE;

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

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen
				.getIngredientSearchStrategy().getIngredientSpace();
		assertNotNull(ingredientSpace);

		assertEquals(scope, ingredientSpace.scope);

		ProgramVariant pvar = cardumen.getVariants().get(0);

		CtElement codeElement0 = pvar.getModificationPoints().stream()
				.filter(e -> e.getCodeElement().toString().equals("solve(min, max)")).findFirst().get()
				.getCodeElement();

		List<Ingredient> ingredients = ingredientSpace.getIngredients(codeElement0);

		List<String> locations = ingredientSpace.getLocations();

		log.debug("Locations " + locations);

		for (Ingredient ingredient : ingredients) {

			CtElement ingredientElement = ingredient.getCode();
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

		List<Ingredient> ingredients40 = ingredientSpace.getIngredients(codeElement40);

		List<?> locations40 = ingredientSpace.getLocations();

		boolean withName = false;
		for (Object location : locations40) {
			if (((MultiKey) location).getKey(0).toString().equals("org.apache.commons.math.analysis.solvers"))
				withName = true;
		}
		assertTrue(withName);

		log.debug("Locations " + locations40);

		for (Ingredient ingredient : ingredients40) {
			CtElement ingredientElement = ingredient.getCode();

			assertEquals(codeElement40.getParent(CtPackage.class).getQualifiedName(),
					ingredientElement.getParent(CtPackage.class).getQualifiedName());
			assertEquals(((CtExpression) codeElement40).getType().getQualifiedName(),
					((CtExpression) ingredientElement).getType().getQualifiedName());
			assertEquals("org.apache.commons.math.analysis.solvers",
					ingredientElement.getParent(CtPackage.class).getQualifiedName());
		}

	}

	@Test
	@Ignore // same failure as testCardumentM70EvolveNotUniformreplacement
	public void testCardumentM70TransformationBugSameCombination() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientPoolScope scope = IngredientPoolScope.PACKAGE;

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

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen
				.getIngredientSearchStrategy().getIngredientSpace();
		assertNotNull(ingredientSpace);

		assertEquals(scope, ingredientSpace.scope);

		ProgramVariant pvar = cardumen.getVariants().get(0);
		int mpi = 0;
		for (ModificationPoint mp : pvar.getModificationPoints()) {

			log.debug("--mp " + (mpi) + " [" + mp.getCodeElement().getClass().getCanonicalName() + "] " + mp);
			CtElement codeElement0 = mp.getCodeElement();
			List<Ingredient> ingredients = ingredientSpace.getIngredients(codeElement0);
			int t = 0;
			for (Ingredient ingredientToModify : ingredients) {

				List<Ingredient> ingredientsTransformed = cardumen.getIngredientTransformationStrategy().transform(mp,
						ingredientToModify);

				Set<String> ing = new HashSet<>();
				int c = 0;
				for (Ingredient ingredientInstantiated : ingredientsTransformed) {
					c++;

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
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientPoolScope scope = IngredientPoolScope.PACKAGE;

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

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen
				.getIngredientSearchStrategy().getIngredientSpace();
		assertNotNull(ingredientSpace);

		assertEquals(scope, ingredientSpace.scope);

		ProgramVariant pvar = cardumen.getVariants().get(0);
		int mpi = 0;
		ModificationPoint mp = pvar.getModificationPoints().get(0);

		log.debug("--mp " + (mpi) + " [" + mp.getCodeElement().getClass().getCanonicalName() + "] " + mp);
		CtElement codeElement0 = mp.getCodeElement();
		List<Ingredient> ingredients = ingredientSpace.getIngredients(codeElement0);
		int t = 0;
		Ingredient ingredientToModify = ingredients.stream()
				.filter(e -> e.getCode().toString().equals("solve(_UnivariateRealFunction_0, _double_1, _double_2)"))
				.findFirst().get();

		List<Ingredient> ingredientsTransformed = cardumen.getIngredientTransformationStrategy().transform(mp,
				ingredientToModify);
		log.debug("--mp " + (mpi) + " [" + mp.getCodeElement().getClass().getCanonicalName() + "] " + mp);

		Set<String> ing = new HashSet<>();

		for (Ingredient ingredientInstantiated : ingredientsTransformed) {

			// log.debug("------Instance-->" +
			// ingredientInstantiated.getCode());

			// TODO: Should not be duplicates
			// assertFalse(ing.contains(ingredientInstantiated.getCode().toString()));
			ing.add(ingredientInstantiated.getCode().toString());

		}

	}

	@Test
	@Ignore
	public void testCardumentLang7NPE() throws Exception {
		String dep = new File("./examples/libs/junit-4.11.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-mode", ExecutionMode.CARDUMEN.name());
		command.command.put("-javacompliancelevel", "6");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "true");
		command.command.put("-maxgen", "0");// Update here
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/tests/");
		command.command.put("-failing", "org.apache.commons.lang3.math.NumberUtilsTest");
		command.command.put("-id", "Lang-7");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "INFO");
		command.command.put("-location", new File("./examples/lang_7").getAbsolutePath());
		command.command.put("-parameters", "maxCombinationVariableLimit:true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		CardumenApproach cardumen = (CardumenApproach) main1.getEngine();

	}

	@Test
	public void testCardumentM70bugHEAP() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientPoolScope scope = IngredientPoolScope.PACKAGE;

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

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen
				.getIngredientSearchStrategy().getIngredientSpace();
		assertNotNull(ingredientSpace);

		assertEquals(scope, ingredientSpace.scope);

		ProgramVariant pvar = cardumen.getVariants().get(0);

		ModificationPoint mp = pvar.getModificationPoints().stream()
				.filter(e -> e.getCodeElement().toString().equals("i < maximalIterationCount")).findFirst().get();
		CtElement codeElement0 = mp.getCodeElement();
		List<Ingredient> ingredients = ingredientSpace.getIngredients(codeElement0);

		String template = "_double_0 < _double_1 || _double_0 > _double_2";

		Ingredient ingredientToModify = ingredients.stream()
				.filter(e -> TestHelper.equalsNoParentesis(e.toString(), template)).findFirst().get();

		List<Ingredient> ingredientsTransformed = cardumen.getIngredientTransformationStrategy().transform(mp,
				ingredientToModify);

		// 3_13 = 2197
		assertEquals(2197, ingredientsTransformed.size());
		assertTrue(ingredientsTransformed.size() + "vs " + maxCombinations,
				ingredientsTransformed.size() <= maxCombinations);

		Set<String> ing = new HashSet<>();

		int i = 0;
		for (Ingredient ingredient : ingredientsTransformed) {
			// log.debug("--Ing-->" + (i++) + " " + ingredient);
			String transformation = ingredient.getCode().toString();
			assertFalse(ing.contains(transformation));
			ing.add(transformation);

			assertFalse(TestHelper.equalsNoParentesis("((max < min) || (min > max))", (transformation)));
		}

	}

	@Test
	@Ignore
	public void testCardumentM70InspectIngredients() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientPoolScope scope = IngredientPoolScope.PACKAGE;

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

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen
				.getIngredientSearchStrategy().getIngredientSpace();
		assertNotNull(ingredientSpace);

		assertEquals(scope, ingredientSpace.scope);

		ProgramVariant pvar = cardumen.getVariants().get(0);
		int mpi = 0;
		ModificationPoint mp = pvar.getModificationPoints().get(245);

		log.debug("--mp " + (mpi) + " [" + mp.getCodeElement().getClass().getCanonicalName() + "] " + mp);
		CtElement codeElement0 = mp.getCodeElement();
		List<Ingredient> ingredients = ingredientSpace.getIngredients(codeElement0);

		Ingredient ingredientToModify = ingredients.get(23);

		List<Ingredient> ingredientsTransformed = cardumen.getIngredientTransformationStrategy().transform(mp,
				ingredientToModify);
		log.debug("--mp " + (mpi) + " [" + mp.getCodeElement().getClass().getCanonicalName() + "] " + mp);

	}

	@Test
	public void testCardumentM70IngredientPartialMapped() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientPoolScope scope = IngredientPoolScope.PACKAGE;

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

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen
				.getIngredientSearchStrategy().getIngredientSpace();
		assertNotNull(ingredientSpace);

		assertEquals(scope, ingredientSpace.scope);

		ProgramVariant pvar = cardumen.getVariants().get(0);

		ModificationPoint mp15 = pvar.getModificationPoints().stream()
				.filter(e -> e.getCodeElement().toString().equals("(a + b) * 0.5")).findFirst().get();

		assertNotNull(mp15);
		log.debug("-->" + mp15.getCodeElement());
		List<Ingredient> ingredients = ingredientSpace.getIngredients(mp15.getCodeElement());

		String code = "solve(_UnivariateRealFunction_0, _double_1, _double_2)";

		Ingredient ingredientToModify = ingredients.stream().filter(e -> e.toString().equals(code)).findFirst().get();

		assertNotNull(ingredientToModify);
		assertEquals(code, ingredientToModify.getCode().toString());

		List<Ingredient> ingredientsTransformed = cardumen.getIngredientTransformationStrategy().transform(mp15,
				ingredientToModify);
		log.debug("MPoint: " + mp15);

		assertTrue(ingredientsTransformed.isEmpty());

		///
		log.debug("Checking ");
		ModificationPoint mp0 = pvar.getModificationPoints().stream()
				.filter(e -> e.getCodeElement().toString().equals("solve(min, max)")).findFirst().get();
		assertEquals("solve(min, max)", mp0.getCodeElement().toString());

		List<Ingredient> ingredients0 = ingredientSpace.getIngredients(mp0.getCodeElement());

		Ingredient ingredientToModify0 = ingredients0.stream().filter(e -> code.equals(e.getCode().toString()))
				.findFirst().get();
		assertEquals(code, ingredientToModify0.toString());

		List<Ingredient> ingredientsTransformed0 = cardumen.getIngredientTransformationStrategy().transform(mp0,
				ingredientToModify0);
		log.debug("MPoint: " + mp15);

		boolean found = false;
		assertTrue(!ingredientsTransformed0.isEmpty());
		for (Ingredient ingredient : ingredientsTransformed0) {
			// log.debug(ingredient.getCode());
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
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientPoolScope scope = IngredientPoolScope.PACKAGE;

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

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen
				.getIngredientSearchStrategy().getIngredientSpace();
		assertNotNull(ingredientSpace);

		assertEquals(scope, ingredientSpace.scope);

		ProgramVariant pvar = cardumen.getVariants().get(0);

		log.debug("Checking ");
		ModificationPoint mp15 = pvar.getModificationPoints().get(15);

		assertEquals("lower >= upper", mp15.getCodeElement().toString());

		List<Ingredient> ingredients = ingredientSpace.getIngredients(mp15.getCodeElement());
		log.debug(ingredients);
		assertEquals("_double_0", ingredients.get(1).toString());

		Ingredient ingredientToModify = (ingredients.get(1));

		List<Ingredient> ingredientsTransformed = cardumen.getIngredientTransformationStrategy().transform(mp15,
				ingredientToModify);

		for (Ingredient ingInstance : ingredientsTransformed) {
			assertTrue(DynamicIngredient.class.isInstance(ingInstance));
			// log.debug(ingInstance.getCode());
			String c = ingInstance.getCode().toString();
			assertFalse("_double_0".equals(c));
		}

	}

	@Test
	public void testCardumentM70Evolve() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientPoolScope scope = IngredientPoolScope.PACKAGE;

		command.command.put("-mode", ExecutionMode.CARDUMEN.name());
		command.command.put("-flthreshold", "0.01");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "400");
		command.command.put("-maxgen", "200");
		command.command.put("-population", "1");
		command.command.put("-scope", scope.toString().toLowerCase());
		command.command.put("-parameters", "disablelog:false");
		command.command.put("-maxVarCombination", "100");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		CardumenApproach cardumen = (CardumenApproach) main1.getEngine();

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen
				.getIngredientSearchStrategy().getIngredientSpace();
		assertNotNull(ingredientSpace);

		assertTrue(cardumen.getSolutions().size() > 0);
		// In this test case we find the solution at generation 74 (flacoco changes this)
		//assertTrue(cardumen.getCurrentStat().getGeneralStats().get(Stats.GeneralStatEnum.NR_GENERATIONS).equals(74));
	}

	@Test
	@Ignore // fails on Travis, seems to be a behavioral change in Spoon, and seems to be deterministic
	// --Martin Nov 2020
	/*
	java.lang.AssertionError: null
	at spoon.support.compiler.jdt.ReferenceBuilder.setPackageOrDeclaringType(ReferenceBuilder.java:1100)
	at spoon.support.compiler.jdt.ReferenceBuilder.getTypeReference(ReferenceBuilder.java:973)
	at spoon.support.compiler.jdt.ReferenceBuilder.getTypeReference(ReferenceBuilder.java:745)
	at spoon.support.compiler.jdt.ReferenceBuilder.getTypeReference(ReferenceBuilder.java:515)
	at spoon.support.compiler.jdt.ReferenceBuilder.buildTypeReference(ReferenceBuilder.java:136)
	at spoon.support.compiler.jdt.ReferenceBuilder.buildTypeReference(ReferenceBuilder.java:130)
	at spoon.support.compiler.jdt.JDTTreeBuilder.visit(JDTTreeBuilder.java:1622)
	at org.eclipse.jdt.internal.compiler.ast.SingleTypeReference.traverse(SingleTypeReference.java:137)
	at org.eclipse.jdt.internal.compiler.ast.Argument.traverse(Argument.java:276)
	at org.eclipse.jdt.internal.compiler.ast.MethodDeclaration.traverse(MethodDeclaration.java:400)
	at org.eclipse.jdt.internal.compiler.ast.TypeDeclaration.traverse(TypeDeclaration.java:1681)
	at org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration.traverse(CompilationUnitDeclaration.java:822)
	at org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration.traverse(CompilationUnitDeclaration.java:783)
	at spoon.support.compiler.jdt.JDTBasedSpoonCompiler.lambda$buildModel$0(JDTBasedSpoonCompiler.java:435)
	at spoon.support.compiler.jdt.JDTBasedSpoonCompiler.forEachCompilationUnit(JDTBasedSpoonCompiler.java:462)
	at spoon.support.compiler.jdt.JDTBasedSpoonCompiler.buildModel(JDTBasedSpoonCompiler.java:433)
	at spoon.support.compiler.jdt.JDTBasedSpoonCompiler.buildUnitsAndModel(JDTBasedSpoonCompiler.java:370)
	at spoon.support.compiler.jdt.JDTBasedSpoonCompiler.buildSources(JDTBasedSpoonCompiler.java:333)
	at spoon.support.compiler.jdt.JDTBasedSpoonCompiler.build(JDTBasedSpoonCompiler.java:114)
	at spoon.support.compiler.jdt.JDTBasedSpoonCompiler.build(JDTBasedSpoonCompiler.java:97)
    at fr.inria.astor.core.manipulation.MutationSupporter.buildModel(MutationSupporter.java:85)
	at fr.inria.astor.core.manipulation.MutationSupporter.buildSpoonModel(MutationSupporter.java:240)
	at fr.inria.astor.core.solutionsearch.AstorCoreEngine.initModel(AstorCoreEngine.java:777)
	at fr.inria.main.evolution.AstorMain.createEngine(AstorMain.java:113)
	at fr.inria.main.evolution.AstorMain.run(AstorMain.java:173)
	at fr.inria.main.evolution.AstorMain.execute(AstorMain.java:233)
	at fr.inria.astor.test.repair.approaches.cardumen.CardumenApproachTest.testCardumentM70EvolveNotUniformreplacement(CardumenApproachTest.java:739)
	 */
	public void testCardumentM70EvolveNotUniformreplacement() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
		// Configuration for paper experiment
		command.command.put("-mode", ExecutionMode.CARDUMEN.name());
		command.command.put("-flthreshold", "0.1");
		command.command.put("-maxtime", "60");
		command.command.put("-population", "1");
		command.command.put("-maxgen", "100");
		command.command.put("-scope", scope.toString().toLowerCase());
		command.command.put("-parameters",
				"limitbysuspicious:false:" + "disablelog:true:uniformreplacement:false:frequenttemplate:true");
		command.command.put("-loglevel", Level.INFO.toString());
		command.command.put("-maxVarCombination", "1000");
		command.command.put("-stopfirst", "false");
		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		CardumenApproach cardumen = (CardumenApproach) main1.getEngine();

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen
				.getIngredientSearchStrategy().getIngredientSpace();
		assertNotNull(ingredientSpace);

	}

	@Test
	@Ignore // takes too much time
	public void testCardumentM70EvolveUniformreplacement() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientPoolScope scope = IngredientPoolScope.LOCAL;

		command.command.put("-mode", ExecutionMode.CARDUMEN.name());
		command.command.put("-flthreshold", "0.01");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "400");
		command.command.put("-maxgen", "200");
		command.command.put("-population", "1");
		command.command.put("-scope", scope.toString().toLowerCase());
		command.command.put("-parameters", "disablelog:true:uniformreplacement:true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		CardumenApproach cardumen = (CardumenApproach) main1.getEngine();

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen
				.getIngredientSearchStrategy().getIngredientSpace();
		assertNotNull(ingredientSpace);

		for (Object keys : ingredientSpace.mkp.keySet()) {
			log.debug(keys + ": ");
			log.debug(ingredientSpace.mkp.get(keys));
		}

		assertTrue(cardumen.getSolutions().size() > 0);

	}

	@Test
	public void testCardumentM70EvolveUniformreplacementMostUsedTemplate() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientPoolScope scope = IngredientPoolScope.LOCAL;

		command.command.put("-mode", ExecutionMode.CARDUMEN.name());
		command.command.put("-flthreshold", "0.01");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "400");
		command.command.put("-maxgen", "200");
		command.command.put("-population", "1");
		command.command.put("-scope", scope.toString().toLowerCase());
		command.command.put("-stopfirst", "false");
		command.command.put("-parameters", "disablelog:true:uniformreplacement:true:frequenttemplate:true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		CardumenApproach cardumen = (CardumenApproach) main1.getEngine();

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen
				.getIngredientSearchStrategy().getIngredientSpace();
		assertNotNull(ingredientSpace);

		assertTrue(cardumen.getSolutions().size() > 0);

	}

	@Test
	public void testCardumentM70Suspicious() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientPoolScope scope = IngredientPoolScope.LOCAL;

		command.command.put("-mode", ExecutionMode.CARDUMEN.name());
		Integer maxsusp = 2;
		command.command.put("-maxsuspcandidates", maxsusp.toString());

		command.command.put("-flthreshold", "0.1");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "400");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");
		command.command.put("-scope", scope.toString().toLowerCase());
		command.command.put("-stopfirst", "false");
		command.command.put("-parameters", "disablelog:true:uniformreplacement:true:frequenttemplate:true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		CardumenApproach cardumen = (CardumenApproach) main1.getEngine();

		assertEquals((int) maxsusp, cardumen.getVariants().get(0).getModificationPoints().size());

		main1 = new AstorMain();
		maxsusp = 100;
		command.command.put("-maxsuspcandidates", maxsusp.toString());
		command.command.put("-flthreshold", "0.9");

		main1.execute(command.flat());

		cardumen = (CardumenApproach) main1.getEngine();

		assertEquals(1, cardumen.getVariants().get(0).getModificationPoints().size());

		main1 = new AstorMain();
		maxsusp = 100;
		command.command.put("-maxsuspcandidates", maxsusp.toString());
		command.command.put("-flthreshold", "0.000000001");
		command.command.put("-parameters", "limitbysuspicious:false");

		main1.execute(command.flat());

		cardumen = (CardumenApproach) main1.getEngine();

		assertEquals(23, cardumen.getVariants().get(0).getModificationPoints().size());

	}

	@Test
	public void testCardumentM70AssertStats() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientPoolScope scope = IngredientPoolScope.LOCAL;

		command.command.put("-mode", ExecutionMode.CARDUMEN.name());
		command.command.put("-flthreshold", "0.1");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "2000");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");
		command.command.put("-scope", scope.toString().toLowerCase());
		command.command.put("-stopfirst", "false");
		command.command.put("-loglevel", "INFO");
		command.command.put("-parameters", "disablelog:true:uniformreplacement:true:frequenttemplate:true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		Stats.currentStat = null;
		Stats.createStat();
		CardumenApproach cardumen = (CardumenApproach) main1.getEngine();

		RandomSelectionTransformedIngredientStrategy estrategy = (RandomSelectionTransformedIngredientStrategy) cardumen
				.getIngredientSearchStrategy();
		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) cardumen
				.getIngredientSearchStrategy().getIngredientSpace();
		assertNotNull(ingredientSpace);

		assertEquals(0, Stats.createStat().getIngredientsStats().combinationByIngredientSize.size());
		assertEquals(0, Stats.createStat().getIngredientsStats().ingredientSpaceSize.size());

		ModificationPoint mp1 = cardumen.getVariants().get(0).getModificationPoints().get(0);

		AstorOperator op1 = cardumen.getOperatorSpace().getOperators().get(0);
		List elements = estrategy.getNotExhaustedBaseElements(mp1, op1);
		int initialIngredients = elements.size();
		assertEquals(elements.size(), ingredientSpace.getIngredients(mp1.getCodeElement()).size());

		assertTrue(elements.size() > 0);

		// assertEquals(0, Stats.createStat().ingredientSpaceSize.get());

		List<Ingredient> bases1 = estrategy.getNotExhaustedBaseElements(mp1, op1);

		assertNotNull(bases1);

		CtElement base1 = bases1.get(0).getCode();
		assertNotNull(base1);
		log.debug("base 1:" + base1);

		// assertEquals(1, (int)
		// Stats.getCurrentStats().ingredientSpaceSize.get(bases.size()));

		List<Ingredient> ingredientsAfterTransformation = estrategy.getInstancesFromBase(mp1, op1,
				new Ingredient(base1));
		int conmbination1Size = ingredientsAfterTransformation.size();
		assertTrue(conmbination1Size > 0);

		long nrcomb = ingredientsAfterTransformation.size();
		Ingredient ins1 = estrategy.getNotUsedTransformedElement(mp1, op1, new Ingredient(base1));

		assertNotNull(ins1);
		log.debug(Stats.currentStat.getIngredientsStats().combinationByIngredientSize);

		assertTrue(Stats.currentStat.getIngredientsStats().combinationByIngredientSize.containsKey(nrcomb));
		// Two attempts done before
		// assertEquals(2, (long)
		// Stats.currentStat.getIngredientsStats().combinationByIngredientSize.get(nrcomb));

		for (int i = 1; i < nrcomb; i++) {
			// log.debug("-->" + i);
			Ingredient ins1i = estrategy.getNotUsedTransformedElement(mp1, op1, new Ingredient(base1));
			assertNotNull(ins1i);

			assertEquals(i + 1, Stats.currentStat.getIngredientsStats().combinationByIngredientSize.keySet().size());
			log.debug(Stats.currentStat.getIngredientsStats().combinationByIngredientSize);
			assertTrue(Stats.currentStat.getIngredientsStats().combinationByIngredientSize.containsKey(nrcomb - i));
			assertEquals(1, (long) Stats.currentStat.getIngredientsStats().combinationByIngredientSize.get(nrcomb - i));
			if (Stats.currentStat.getIngredientsStats().combinationByIngredientSize.containsKey((long) 0)) {
				log.debug("Putting zero for " + i);
			}
			if (i != 120)
				assertNull(estrategy.exhaustTemplates.get(estrategy.getKey(mp1, op1)));
		}
		for (Long i : Stats.currentStat.getIngredientsStats().combinationByIngredientSize.keySet()) {
			assertTrue("--" + i, i <= 121 && i > 0);
		}
		assertEquals(nrcomb, Stats.currentStat.getIngredientsStats().combinationByIngredientSize.keySet().size());
		assertFalse(Stats.currentStat.getIngredientsStats().combinationByIngredientSize.containsKey((long) 0));

		// Cloning stats to be sure that is not modified when there is no more
		// ingredients to select
		Map clonedCombStats = new HashMap<>(Stats.currentStat.getIngredientsStats().combinationByIngredientSize);
		// Any ingredients to add....
		Ingredient ins1i = estrategy.getNotUsedTransformedElement(mp1, op1, new Ingredient(base1));
		assertNull(ins1i);
		// Now, after the space is completely navigated, a zero must Not be
		// written
		assertFalse(Stats.currentStat.getIngredientsStats().combinationByIngredientSize.containsKey((long) 0));

		// The same that before, the cache of ingredients would return zero
		// ingredients
		Ingredient ins1i2 = estrategy.getNotUsedTransformedElement(mp1, op1, new Ingredient(base1));
		assertNull(ins1i2);

		// No change in the stats
		assertEquals(clonedCombStats.keySet().size(),
				Stats.currentStat.getIngredientsStats().combinationByIngredientSize.keySet().size());

		for (Long i : Stats.currentStat.getIngredientsStats().combinationByIngredientSize.keySet()) {
			assertTrue("--" + i, i <= 121 && i > 0);
			assertEquals(clonedCombStats.get(i),
					Stats.currentStat.getIngredientsStats().combinationByIngredientSize.get(i));
		}

		for (int i = 1; i < conmbination1Size; i++) {
			assertEquals("-->" + i, 1,
					Stats.currentStat.getIngredientsStats().combinationByIngredientSize.get((long) i), 0);

		}

		assertEquals(nrcomb, Stats.currentStat.getIngredientsStats().combinationByIngredientSize.keySet().size());

		assertEquals(1, estrategy.exhaustTemplates.keySet().size());
		assertTrue(!estrategy.exhaustTemplates.get(estrategy.getKey(mp1, op1)).isEmpty());
		assertTrue(estrategy.exhaustTemplates.get(estrategy.getKey(mp1, op1)).contains(new Ingredient(base1)));

		log.debug("-------Base 2-------------");

		CtElement base2 = bases1.get(1).getCode();
		assertNotNull(base2);
		log.debug("base 2" + base2);
		assertEquals(1, estrategy.exhaustTemplates.keySet().size());

		List<Ingredient> bases2 = estrategy.getNotExhaustedBaseElements(mp1, op1);

		assertNotNull(bases2);

		List<Ingredient> ingredientsAfterTransformationNull = estrategy.getInstancesFromBase(mp1, op1,
				new Ingredient(base1));
		assertNull(ingredientsAfterTransformationNull);

		List<Ingredient> ingredientsAfterTransformation2 = estrategy.getInstancesFromBase(mp1, op1,
				new Ingredient(base2));

		int conmbination2 = ingredientsAfterTransformation2.size();
		assertTrue(conmbination2 > 0);
		int nrcomb2 = ingredientsAfterTransformation2.size();
		assertEquals(bases1.size(), bases2.size() + 1);

		assertFalse(bases2.contains(new Ingredient(base1)));

		log.debug("-------Base 3-----------");
		List<Ingredient> bases3 = estrategy.getNotExhaustedBaseElements(mp1, op1);

		log.debug("\nbase 3 " + bases3);
		CtElement base3 = bases3.get(2).getCode();
		log.debug("before 3: " + Stats.currentStat.getIngredientsStats().combinationByIngredientSize);

		List<Ingredient> ingredientsAfterTransformation3 = estrategy.getInstancesFromBase(mp1, op1,
				new Ingredient(base3));
		log.debug("Ingredients for base 3\n" + ingredientsAfterTransformation3.size());
		assertTrue(ingredientsAfterTransformation3.size() > 0);

		int ingredientsAfterTransformation3Size = ingredientsAfterTransformation3.size();
		for (int i = 1; i <= ingredientsAfterTransformation3Size; i++) {
			// log.debug("-->" + i);
			Ingredient ins3i = estrategy.getNotUsedTransformedElement(mp1, op1, new Ingredient(base3));
			assertNotNull(ins3i);
			log.debug("After: " + Stats.currentStat.getIngredientsStats().combinationByIngredientSize);
			int remainingSize = ingredientsAfterTransformation3Size - i + 1;// +1
																			// due
																			// i
																			// starts
																			// in
																			// 1
			assertTrue(" " + i, (long) Stats.currentStat.getIngredientsStats().combinationByIngredientSize
					.get((long) (remainingSize)) >= 2);
			if (Stats.currentStat.getIngredientsStats().combinationByIngredientSize.containsKey((long) 0)) {
				log.debug("Putting zero for " + i + ", ing " + ins3i + ", total attepts with zero "
						+ Stats.currentStat.getIngredientsStats().combinationByIngredientSize.get((long) 0));
			}
		}

		Ingredient ins3n = estrategy.getNotUsedTransformedElement(mp1, op1, new Ingredient(base3));
		assertNull(ins3n);

		log.debug("Putting zero for " + base3 + " total attepts with zero "
				+ Stats.currentStat.getIngredientsStats().combinationByIngredientSize.get((long) 0));

		assertTrue(estrategy.exhaustTemplates.get(estrategy.getKey(mp1, op1)).contains(new Ingredient(base3)));

		log.debug("--------BASE 4--no trasformation--------");

		List<Ingredient> bases4 = estrategy.getNotExhaustedBaseElements(mp1, op1);
		assertFalse(bases4.contains(new Ingredient(base3)));

		///

		assertTrue(!estrategy.exhaustTemplates.get(estrategy.getKey(mp1, op1)).isEmpty());

		List<Ingredient> l = (List) ingredientSpace.mkp.values().toArray()[0];
		CtCodeElement base4 = (CtCodeElement) l.get(0).getCode();
		assertEquals("clearResult()", base4.toString());

		long before = Stats.currentStat.getIngredientsStats().combinationByIngredientSize.get((long) 1);
		ModificationPoint mp5 = cardumen.getVariants().get(0).getModificationPoints().get(5);
		List<Ingredient> ingredientsAfterTransformation4 = estrategy.getInstancesFromBase(mp5, op1,
				new Ingredient(base4));
		log.debug("mp5 " + Stats.currentStat.getIngredientsStats().combinationByIngredientSize);
		assertTrue(Stats.currentStat.getIngredientsStats().combinationByIngredientSize.containsKey((long) 1));

		// assertEquals(before + 1,
		// Stats.currentStat.getIngredientsStats().combinationByIngredientSize.get((long)
		// 1), 0);

		assertTrue(ingredientsAfterTransformation4.size() > 0);

		log.debug("--------BASE 5----------");

		SuspiciousModificationPoint mp7 = (SuspiciousModificationPoint) cardumen.getVariants().get(0)
				.getModificationPoints().get(7);
		assertTrue(mp7.getSuspicious().getClassName().contains("UnivariateRealSolverUtils"));
		assertEquals("solve(_UnivariateRealFunction_0, _double_1, _double_2)", base1.toString());

		assertFalse(Stats.currentStat.getIngredientsStats().combinationByIngredientSize.containsKey((long) 0));

		List<Ingredient> ingredientsAfterTransformation5 = estrategy.getInstancesFromBase(mp7, op1,
				new Ingredient(base1));

		assertTrue(ingredientsAfterTransformation5.isEmpty());

		assertTrue(Stats.currentStat.getIngredientsStats().combinationByIngredientSize.containsKey((long) 0));

		// assertEquals(1,
		// Stats.currentStat.getIngredientsStats().combinationByIngredientSize.get((long)
		// 0), 0);

	}

	@Test
	public void testCardumentM70MaxModPoints() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientPoolScope scope = IngredientPoolScope.PACKAGE;

		int maxModPoints = 7;// Let's say 7, the number of MP over this
								// configuration is 12.

		command.command.put("-mode", ExecutionMode.custom.name());
		command.command.put("-flthreshold", "0.1");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");
		command.command.put("-customengine", CardumenExhaustiveEngine4Stats.class.getCanonicalName());
		command.command.put("-scope", scope.toString().toLowerCase());
		command.command.put("-parameters",
				"maxmodificationpoints:" + maxModPoints + ":skipfitnessinitialpopulation:true:limitbysuspicious:false");
		command.command.put("-loglevel", Level.INFO.toString());

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		Stats.createStat();

		assertEquals(maxModPoints, main1.getEngine().getVariants().get(0).getModificationPoints().size());
	}

	@Test
	@Ignore
	public void testCardumentM42() throws Exception {
		CommandSummary command = MathCommandsTests.getMath42Command();

		IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
		boolean uniformreplacement = false;
		command.command.put("-mode", ExecutionMode.custom.name());
		command.command.put("-flthreshold", "0.1");
		command.command.put("-maxtime", "60");
		command.command.put("-population", "1");
		command.command.put("-customengine", CardumenExhaustiveEngine4Stats.class.getCanonicalName());
		command.command.put("-scope", scope.toString().toLowerCase());
		command.command.put("-parameters", "skipfitnessinitialpopulation:true:limitbysuspicious:false:"
				+ "disablelog:false:uniformreplacement:" + Boolean.toString(uniformreplacement));
		command.command.put("-loglevel", Level.INFO.toString());

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		Stats.createStat();

	}

	@Test
	public void testCardumentM70ScopeLocalProbTransformation() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientPoolScope scope = IngredientPoolScope.LOCAL;

		command.command.put("-mode", ExecutionMode.CARDUMEN.name());
		command.command.put("-flthreshold", "0.00");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");
		command.command.put("-scope", scope.toString().toLowerCase());
		command.command.put("-parameters", "probabilistictransformation:true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		CardumenApproach cardumen = (CardumenApproach) main1.getEngine();

		ProbabilisticTransformationStrategy probTransf = (ProbabilisticTransformationStrategy) cardumen
				.getIngredientTransformationStrategy();

		probTransf.calculateGramsProbs();

		ProgramVariant pv = cardumen.getVariants().get(0);
		// C1:.BisectionSolver l: 66,
		CtElement clearResult = (CtCodeElement) pv.getModificationPoints().get(2).getCodeElement();

		List<CtVariable> varContextClearResult = VariableResolver.searchVariablesInScope(clearResult);//
		CtElement returnExpression = pv.getModificationPoints().get(8).getCodeElement();
		VarMapping vmapping = VariableResolver.mapVariablesFromContext(varContextClearResult, returnExpression);

		ModificationPoint mpointCleanResult = pv.getModificationPoints().get(8);

		assertEquals("clearResult()", clearResult.toString());

		assertEquals("(a + b) * 0.5", returnExpression.toString());

		List<VarCombinationForIngredient> varsComb4Ingredients = probTransf.findAllVarMappingCombinationUsingProbab(
				vmapping.getMappedVariables(), mpointCleanResult, new Ingredient(returnExpression));
		assertTrue(varsComb4Ingredients.size() > 0);

		Double probability = varsComb4Ingredients.get(0).getProbality();
		assertTrue(probability > 0);

		for (VarCombinationForIngredient varCombinationForIngredient : varsComb4Ingredients) {
			log.debug(varCombinationForIngredient);
			assertTrue(probability >= varCombinationForIngredient.getProbality());
			probability = varCombinationForIngredient.getProbality();
		}

	}

	@Test
	public void testCardumentM70ScopeLocalRandomTransformation() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientPoolScope scope = IngredientPoolScope.LOCAL;

		command.command.put("-mode", ExecutionMode.CARDUMEN.name());
		command.command.put("-flthreshold", "0.00");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");
		command.command.put("-scope", scope.toString().toLowerCase());
		// RANDOM
		command.command.put("-parameters", "probabilistictransformation:false");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		CardumenApproach cardumen = (CardumenApproach) main1.getEngine();

		RandomTransformationStrategy probTransf = (RandomTransformationStrategy) cardumen
				.getIngredientTransformationStrategy();

		ProgramVariant pv = cardumen.getVariants().get(0);
		// C1:.BisectionSolver l: 66,
		CtElement clearResult = (CtCodeElement) pv.getModificationPoints().get(2).getCodeElement();

		List<CtVariable> varContextClearResult = VariableResolver.searchVariablesInScope(clearResult);//
		CtElement returnExpression = pv.getModificationPoints().get(8).getCodeElement();
		VarMapping vmapping = VariableResolver.mapVariablesFromContext(varContextClearResult, returnExpression);

		ModificationPoint mpointCleanResult = pv.getModificationPoints().get(8);

		assertEquals("clearResult()", clearResult.toString());

		assertEquals("(a + b) * 0.5", returnExpression.toString());

		List<VarCombinationForIngredient> varsComb4Ingredients = probTransf
				.findAllVarMappingCombinationUsingRandom(vmapping.getMappedVariables(), mpointCleanResult);
		assertTrue(varsComb4Ingredients.size() > 0);

		Double probability = varsComb4Ingredients.get(0).getProbality();
		assertTrue(probability > 0);

		for (VarCombinationForIngredient varCombinationForIngredient : varsComb4Ingredients) {
			log.debug(varCombinationForIngredient);
			assertEquals(probability, varCombinationForIngredient.getProbality());
			probability = varCombinationForIngredient.getProbality();
		}

	}

	@Test
	public void testCardumentM57() throws Exception {
		CommandSummary command = MathCommandsTests.getMath57Command();

		IngredientPoolScope scope = IngredientPoolScope.LOCAL;

		command.command.put("-mode", ExecutionMode.CARDUMEN.name());
		command.command.put("-flthreshold", "0.1");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "20007");
		command.command.put("-maxgen", "10");
		command.command.put("-population", "1");
		command.command.put("-scope", scope.toString().toLowerCase());
		command.command.put("-loglevel", "INFO");
		// RANDOM
		// command.command.put("-parameters",
		// "probabilistictransformation:false");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		CardumenApproach cardumen = (CardumenApproach) main1.getEngine();
		assertEquals(AstorOutputStatus.MAX_GENERATION, cardumen.getOutputStatus());

		ExpressionTypeIngredientSpace space = (ExpressionTypeIngredientSpace) cardumen.getIngredientSearchStrategy()
				.getIngredientSpace();

	}

}
