package fr.inria.astor.test.repair.evaluation.extensionpoints.deep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import fr.inria.astor.approaches.deeprepair.DeepRepairEngine;
import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.approaches.jgenprog.operators.InsertAfterOp;
import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPool;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.ingredientSearch.CloneIngredientSearchStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.CtLocationIngredientSpace;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.ctscopes.CtClassIngredientSpace;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.ctscopes.CtGlobalIngredientScope;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.ctscopes.CtPackageIngredientScope;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import fr.inria.main.evolution.ExtensionPoints;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtType;

/**
 * Test for Clone ingredient strategy
 * 
 * @author Matias Martinez
 *
 */
// @Ignore
public class DeepRepairTest {

	protected Logger log = Logger.getLogger(this.getClass().getName());

	static public String[] createCommandM70ForNotEvolve(File learningDir, Class cloneGranularityClass) {
		// HERE WE FORCE TO NOT EVOLVE
		return createDeepRepairCommandM70(learningDir, cloneGranularityClass, 0, false);
	}

	static public String[] createDeepRepairCommandM70(File learningDir, Class cloneGranularityClass, int generations,
			boolean transformIngredient) {
		Double faultLocalizationThreshold = 0.5;
		return createDeepRepairCommandM70(learningDir, cloneGranularityClass, generations, transformIngredient,
				CtLocationIngredientSpace.class.getCanonicalName(), faultLocalizationThreshold);
	}

	static public String[] createDeepRepairCommandM70(File learningDir, Class cloneGranularityClass, int generations,
			boolean transformIngredient, String scope) {
		Double faultLocalizationThreshold = 0.5;
		return createDeepRepairCommandM70(learningDir, cloneGranularityClass, generations, transformIngredient, scope,
				faultLocalizationThreshold);
	}

	static public String[] createDeepRepairCommandM70(File learningDir, Class cloneGranularityClass, int generations,
			boolean transformIngredient, String scope, Double faultlocalizationThreshold) {
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog",
				//
				"-failing", "org.apache.commons.math.analysis.solvers.BisectionSolverTest",
				//
				"-location", new File("./examples/math_70").getAbsolutePath(),
				//
				"-package", "org.apache.commons", "-srcjavafolder",
				//
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes",
				//
				"-javacompliancelevel", "7", "-flthreshold", faultlocalizationThreshold.toString(), "-out",
				out.getAbsolutePath(),
				//
				"-scope", scope,
				//
				"-seed", "10",

				"-maxgen", Integer.toString(generations),
				//
				"-population", "1",
				//
				"-stopfirst", "true",
				//
				"-maxtime", "100",
				//
				// Learning Arguments
				"-learningdir", learningDir.getAbsolutePath(),
				//
				"-clonegranularity", cloneGranularityClass.getCanonicalName(),
				//
				"-ingredientstrategy", CloneIngredientSearchStrategy.class.getName(),
				//
				"-transformingredient",
				//
				"-loglevel", Level.DEBUG.toString() };
		return args;
	}

	static public CommandSummary createDeepRepairModeCommandM70(File learningDir, int generations,
			boolean transformIngredient, String scope, Double faultlocalizationThreshold) {
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		String[] args = new String[] { "-dependencies", dep, "-mode", "deeprepair",
				//
				"-failing", "org.apache.commons.math.analysis.solvers.BisectionSolverTest",
				//
				"-location", new File("./examples/math_70").getAbsolutePath(),
				//
				"-package", "org.apache.commons", "-srcjavafolder",
				//
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes",
				//
				"-javacompliancelevel", "7", "-flthreshold", faultlocalizationThreshold.toString(), "-out",
				out.getAbsolutePath(),
				//
				"-scope", scope,
				//
				"-seed", "10",

				"-maxgen", Integer.toString(generations),
				//
				"-population", "1",
				//
				"-stopfirst", "true",
				//
				"-maxtime", "100",
				//
				// Learning Arguments
				"-learningdir", learningDir.getAbsolutePath(),
				//
				"-loglevel", Level.DEBUG.toString() };

		CommandSummary cs = new CommandSummary(args);
		return cs;
	}

	protected String[] commandLang1Clone(File out, boolean step) {
		String libsdir = new File("./examples/libs/lang_common_lib").getAbsolutePath();
		String dep = libsdir + File.separator + "cglib.jar" + File.pathSeparator //
				+ libsdir + File.separator + "commons-io.jar" + File.pathSeparator //
				+ File.separator + libsdir + File.separator + "asm.jar" + File.pathSeparator //
				+ File.separator + libsdir + File.separator + "easymock.jar";//

		// target/classes/ -bintestfolder target/tests/ -srcjavafolder
		// src/main/java/ -srctestfolder src/test/java/

		// -learningdir ../../out/learningdir/Lang/1/b/ -location
		// ../../dat/defects4j/Lang/1/b/

		// -dependencies
		// ../../dat/libs/Lang/cglib.jar:../../dat/libs/Lang/commons-io.jar:../../dat/libs/Lang/asm.jar:../../dat/libs/Lang/easymock.jar

		String[] args = new String[] {
				///
				"-dependencies", dep, "-mode", "jgenprog",
				// "-failing", "org.apache.commons.lang3.math.NumberUtilsTest",
				// //
				"-location", new File("./examples/lang_1/").getAbsolutePath(),
				//
				"-package", "org.apache.commons",
				//
				"-srcjavafolder", "/src/main/java/",
				//
				"-srctestfolder", "/src/main/test/",
				//
				"-binjavafolder", "/target/classes/",
				//
				"-bintestfolder", "/target/test-classes/",
				//
				"-javacompliancelevel", "6",
				//
				"-flthreshold", "0.1",
				//
				"-population", "1",
				//
				"-out", out.getAbsolutePath(), "-scope", "package", "-seed", "1", "-maxgen", "50",
				//
				//
				"-ingredientstrategy",
				"fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.EfficientIngredientStrategy", "-scope",
				"fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtGlobalIngredientScope", "-learningdir",
				"", "-timezone", "America/New_York", "-stopfirst", "true", "-maxtime", "5", (step) ? "-testbystep" : "",
				// Operators to consider (we discard REMOVE)
				ExtensionPoints.REPAIR_OPERATORS.argument(),
				"fr.inria.astor.approaches.jgenprog.operators.InsertAfterOp:fr.inria.astor.approaches.jgenprog.operators.InsertBeforeOp:fr.inria.astor.approaches.jgenprog.operators.ReplaceOp",
				"-loglevel", "DEBUG", };
		return args;
	}

	@Test
	public void testExecutableMath70() throws Exception {

		// let's test at Executanble granularity

		Class executableCloneGranularity = CtExecutable.class;

		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		Class typeCloneGranularityClass = CtExecutable.class;
		String[] args = createCommandM70ForNotEvolve(learningDir, executableCloneGranularity);

		log.debug(Arrays.toString(args));

		AstorMain main1 = new AstorMain();
		main1.execute(args);
		JGenProg engine = (JGenProg) main1.getEngine();

		ProgramVariant pvariant = engine.getVariants().get(0);

		ModificationPoint buggyStatementModPoint = pvariant.getModificationPoints().get(0);

		assertEquals(72, buggyStatementModPoint.getCodeElement().getPosition().getEndLine());

		// We check that the strategy is that one from the DeepRepair
		assertTrue(engine.getIngredientSearchStrategy() instanceof CloneIngredientSearchStrategy);

		CloneIngredientSearchStrategy<CtExecutable> excloneStrategy = (CloneIngredientSearchStrategy<CtExecutable>) engine
				.getIngredientSearchStrategy();

		AstorOperator op = new ReplaceOp();

		// Now, we query only *One* Ingredient
		Ingredient exingredientSelected = excloneStrategy.getFixIngredient(buggyStatementModPoint, op);
		assertNotNull(exingredientSelected);

		// Note that it must be invoked after FixIngredients (it initialize the
		// space)
		CtExecutable<?> executParent = buggyStatementModPoint.getCodeElement().getParent(CtExecutable.class);
		// We retrieve *ALL* the ingredients for the modification point that has
		// the bug
		Queue<CtCodeElement> execingredientsToConsider = excloneStrategy.getfixspace(buggyStatementModPoint, op,
				executParent);

		assertNotNull(execingredientsToConsider);

		assertFalse(execingredientsToConsider.isEmpty());

	}

	@Test
	public void testExecutableGranularityMath70TransformationStrategy() throws Exception {

		// Now, let's test at Executable granularity

		Class executableCloneGranularity = CtExecutable.class;

		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		Class typeCloneGranularityClass = CtExecutable.class;
		int generations = 100;
		boolean transformIngredient = true;
		String[] args = createDeepRepairCommandM70(learningDir, executableCloneGranularity, generations,
				transformIngredient);

		log.debug(Arrays.toString(args));

		AstorMain main1 = new AstorMain();
		main1.execute(args);
		JGenProg engine = (JGenProg) main1.getEngine();

		assertTrue(engine.getIngredientSearchStrategy() instanceof CloneIngredientSearchStrategy);

		log.debug("size solutions: " + engine.getSolutions().size());

		assertFalse(engine.getSolutions().isEmpty());
	}

	@Test
	public void testCloneLang1RegressionFailing() throws Exception {
		AstorMain main1 = new AstorMain();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		boolean stepbystep = false;
		String[] args = commandLang1Clone(out, stepbystep);
		log.debug(Arrays.toString(args));
		main1.execute(args);
		Assert.assertFalse(main1.getEngine().getMutatorSupporter().getFactory().Type().getAll().isEmpty());

	}

	///////////

	@Test
	public void testTypeCloneStrategyIsolatedMath70OneSuspicious() throws Exception {
		AstorMain main1 = new AstorMain();
		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		Class typeCloneGranularityClass = CtType.class;

		String scope = CtClassIngredientSpace.class.getCanonicalName();

		String[] args = createDeepRepairCommandM70(learningDir, typeCloneGranularityClass, 0, false, scope, 0.5);

		log.debug(Arrays.toString(args));

		main1.execute(args);
		JGenProg engine = (JGenProg) main1.getEngine();

		IngredientPool ingredientSpace = engine.getIngredientSearchStrategy().getIngredientSpace();

		assertTrue(ingredientSpace.getLocations().size() > 0);

		assertEquals(1, ingredientSpace.getLocations().size());

		IngredientSearchStrategy ingStrategy = engine.getIngredientSearchStrategy();

		assertNotNull(ingStrategy);

		assertTrue(ingStrategy instanceof CloneIngredientSearchStrategy<?>);

		CloneIngredientSearchStrategy<CtType> cloneStrategy = (CloneIngredientSearchStrategy<CtType>) ingStrategy;

		Optional<?> elements = cloneStrategy.queryelements();

		assertNotNull(elements);

		Map<String, ?> mapKeys = (Map<String, ?>) elements.get();

		// Using 0.1 of thr we have two locations. Using 0.5 we have only 1.
		// assertEquals(Double.valueOf(0.1),
		// ConfigurationProperties.getPropertyDouble("flthreshold"));

		mapKeys.keySet().forEach(e -> System.out.println("key :" + e));

		assertEquals(1, mapKeys.keySet().size());

		// Let's take the Program Variant
		ProgramVariant pvariant = engine.getVariants().get(0);

		ModificationPoint buggyStatementModPoint = pvariant.getModificationPoints().get(0);

		// We find the buggy modification point
		assertEquals(72, buggyStatementModPoint.getCodeElement().getPosition().getEndLine());

		AstorOperator testOperator = new ReplaceOp(); // We dont care about the
														// operator here

		CtType buggyClass = buggyStatementModPoint.getCtClass();

		// Important:I had to invoke this method to execute computesimlist
		// (it's private) before calling getfixspace()
		Ingredient ingredientSelected = cloneStrategy.getFixIngredient(buggyStatementModPoint, testOperator);

		assertNotNull(ingredientSelected);

		Queue<CtCodeElement> ingredientsToConsider = cloneStrategy.getfixspace(buggyStatementModPoint, testOperator,
				buggyClass);

		assertNotNull(ingredientsToConsider);
		// At least one ingredient
		assertFalse(ingredientsToConsider.isEmpty());

		// The suspicious class has 4 return statements.
		// They are: [return solve(f, min, max), return solve(f, min, max),
		// return solve(min, max), return m]
		assertEquals("Wrong number of ingredients collected", 3, ingredientsToConsider.size());
		List<CtCodeElement> ingredientsList = (List) ingredientsToConsider;

		List<String> solutions = new ArrayList(
				Arrays.asList("return solve(f, min, max)", "return solve(min, max)", "return m"));
		for (CtCodeElement ctCodeElement : ingredientsList) {
			assertTrue(solutions.remove(ctCodeElement.toString()));
		}
		// All ingredients returned
		assertTrue(solutions.isEmpty());

		log.debug("Ingredients to considers: " + ingredientsToConsider);
		// Now, from all ingredients
		Queue<CtCodeElement> ingredientsToConsiderInser = cloneStrategy.getfixspace(buggyStatementModPoint,
				new InsertAfterOp(), buggyClass);
		// the suspicious class has 22 statements (it does not include super
		// calls)
		assertEquals("Wrong number of ingredients collected", 20, ingredientsToConsiderInser.size());
		log.debug("All ingredients from space\n" + ingredientsToConsiderInser);
	}

	@Test
	public void testTypeCloneStrategyIsolatedMath70TwoSuspicious() throws Exception {
		AstorMain main1 = new AstorMain();
		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		Class typeCloneGranularityClass = CtType.class;

		String scope = CtClassIngredientSpace.class.getCanonicalName();
		String[] args = createDeepRepairCommandM70(learningDir, typeCloneGranularityClass, 0, false, scope, 0.1);

		log.debug(Arrays.toString(args));

		main1.execute(args);
		JGenProg engine = (JGenProg) main1.getEngine();

		IngredientPool ingredientSpace = engine.getIngredientSearchStrategy().getIngredientSpace();

		assertTrue(ingredientSpace.getLocations().size() > 0);

		IngredientSearchStrategy ingStrategy = engine.getIngredientSearchStrategy();

		assertNotNull(ingStrategy);

		assertTrue(ingStrategy instanceof CloneIngredientSearchStrategy<?>);

		CloneIngredientSearchStrategy<CtType> cloneStrategy = (CloneIngredientSearchStrategy<CtType>) ingStrategy;

		Optional<?> elements = cloneStrategy.queryelements();

		assertNotNull(elements);

		Map<String, ?> mapKeys = (Map<String, ?>) elements.get();

		// Using 0.1 of thr we have two locations. Using 0.5 we have only 1.
		assertEquals(Double.valueOf(0.1), ConfigurationProperties.getPropertyDouble("flthreshold"));

		mapKeys.keySet().forEach(e -> System.out.println("key :" + e));

		assertEquals(2, mapKeys.keySet().size());

		// Let's take the Program Variant
		ProgramVariant pvariant = engine.getVariants().get(0);

		ModificationPoint buggyStatementModPoint = pvariant.getModificationPoints().get(0);

		assertEquals(72, buggyStatementModPoint.getCodeElement().getPosition().getEndLine());

		AstorOperator testOperator = new ReplaceOp(); // We dont care about the
														// operator here

		CtType buggyClass = buggyStatementModPoint.getCtClass();

		// Important:I had to invoke this method to execute computesimlist
		// (it's private) before calling getfixspace()
		Ingredient ingredientSelected = cloneStrategy.getFixIngredient(buggyStatementModPoint, testOperator);

		log.debug(ingredientSelected);

		Queue<CtCodeElement> ingredientsToConsider = cloneStrategy.getfixspace(buggyStatementModPoint, testOperator,
				buggyClass);

		log.debug(ingredientsToConsider);

		assertFalse(ingredientsToConsider.isEmpty());

		log.debug("Buggy mp " + buggyStatementModPoint);
		log.debug("ingredientsToConsider: " + ingredientsToConsider);

		// The suspicious class has 9 return statements.
		// 4 returns from Bisection and 5 from Univariate
		assertEquals("Wrong number of ingredients collected", 8, ingredientsToConsider.size());

		log.debug("Ingredients to considers: " + ingredientsToConsider);

		// Now, Insert operator

		Queue<CtCodeElement> ingredientsToConsiderInser = cloneStrategy.getfixspace(buggyStatementModPoint,
				new InsertAfterOp(), buggyClass);
		log.debug(ingredientsToConsiderInser);
		// the suspicious classes has 52 statements (it does not include super
		// calls)
		assertEquals("Wrong number of ingredients collected", 48, ingredientsToConsiderInser.size());

	}

	@Test
	public void testDeepRepairParametersScope() throws Exception {
		AstorMain main1 = new AstorMain();
		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		Class typeCloneGranularityClass = CtType.class;

		String scope = CtClassIngredientSpace.class.getCanonicalName();

		CommandSummary args = createDeepRepairModeCommandM70(learningDir, 10, false, scope, 0.5);

		args.command.put("-scope", "local");
		main1.execute(args.flat());
		DeepRepairEngine engine = (DeepRepairEngine) main1.getEngine();

		assertEquals(CtClassIngredientSpace.class,
				engine.getIngredientSearchStrategy().getIngredientSpace().getClass());

		args.command.put("-scope", "package");
		main1.execute(args.flat());
		engine = (DeepRepairEngine) main1.getEngine();

		assertEquals(CtPackageIngredientScope.class,
				engine.getIngredientSearchStrategy().getIngredientSpace().getClass());

		args = createDeepRepairModeCommandM70(learningDir, 0, false, scope, 0.5);

		args.command.put("-scope", "global");
		main1.execute(args.flat());
		engine = (DeepRepairEngine) main1.getEngine();

		assertEquals(CtGlobalIngredientScope.class,
				engine.getIngredientSearchStrategy().getIngredientSpace().getClass());
	}

	@Test
	public void testDeepRepairParametersTypeGranularity() throws Exception {
		AstorMain main1 = new AstorMain();
		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		String scope = CtClassIngredientSpace.class.getCanonicalName();

		CommandSummary args = createDeepRepairModeCommandM70(learningDir, 10, false, scope, 0.5);
		args.command.put("-scope", "local");
		main1.execute(args.flat());
		DeepRepairEngine engine = (DeepRepairEngine) main1.getEngine();

		assertTrue(CloneIngredientSearchStrategy.class.isInstance(engine.getIngredientSearchStrategy()));
		assertEquals(CtType.class, ((CloneIngredientSearchStrategy) engine.getIngredientSearchStrategy()).getCls());

		// As argument type
		args.command.put("-clonegranularity", CtType.class.getName());
		main1.execute(args.flat());
		engine = (DeepRepairEngine) main1.getEngine();

		assertTrue(CloneIngredientSearchStrategy.class.isInstance(engine.getIngredientSearchStrategy()));
		assertEquals(CtType.class, ((CloneIngredientSearchStrategy) engine.getIngredientSearchStrategy()).getCls());

		args.command.put("-clonegranularity", CtExecutable.class.getName());
		main1.execute(args.flat());
		engine = (DeepRepairEngine) main1.getEngine();

		assertTrue(CloneIngredientSearchStrategy.class.isInstance(engine.getIngredientSearchStrategy()));
		assertEquals(CtExecutable.class,
				((CloneIngredientSearchStrategy) engine.getIngredientSearchStrategy()).getCls());

	}

	@Test
	public void testDeepRepairParametersExecutableGranularity() throws Exception {
		AstorMain main1 = new AstorMain();
		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		String scope = CtClassIngredientSpace.class.getCanonicalName();

		CommandSummary args = createDeepRepairModeCommandM70(learningDir, 10, false, scope, 0.5);

		args.command.put("-clonegranularity", CtExecutable.class.getName());
		main1.execute(args.flat());
		DeepRepairEngine engine = (DeepRepairEngine) main1.getEngine();

		assertTrue(CloneIngredientSearchStrategy.class.isInstance(engine.getIngredientSearchStrategy()));
		assertEquals(CtExecutable.class,
				((CloneIngredientSearchStrategy) engine.getIngredientSearchStrategy()).getCls());

	}

	@Test
	public void testDeepRepairModeMath70OneSuspicious() throws Exception {
		AstorMain main1 = new AstorMain();
		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		Class typeCloneGranularityClass = CtType.class;

		String scope = CtClassIngredientSpace.class.getCanonicalName();

		String[] args = createDeepRepairModeCommandM70(learningDir, 0, false, scope, 0.5).flat();

		log.debug(Arrays.toString(args));

		main1.execute(args);
		DeepRepairEngine engine = (DeepRepairEngine) main1.getEngine();

		IngredientPool ingredientSpace = engine.getIngredientSearchStrategy().getIngredientSpace();

		assertTrue(ingredientSpace.getLocations().size() > 0);

		assertEquals(1, ingredientSpace.getLocations().size());

		IngredientSearchStrategy ingStrategy = engine.getIngredientSearchStrategy();

		assertNotNull(ingStrategy);

		assertTrue(ingStrategy instanceof CloneIngredientSearchStrategy<?>);

		CloneIngredientSearchStrategy<CtType> cloneStrategy = (CloneIngredientSearchStrategy<CtType>) ingStrategy;

		Optional<?> elements = cloneStrategy.queryelements();

		assertNotNull(elements);

		Map<String, ?> mapKeys = (Map<String, ?>) elements.get();

		// Using 0.1 of thr we have two locations. Using 0.5 we have only 1.
		// assertEquals(Double.valueOf(0.1),
		// ConfigurationProperties.getPropertyDouble("flthreshold"));

		mapKeys.keySet().forEach(e -> System.out.println("key :" + e));

		assertEquals(1, mapKeys.keySet().size());

		// Let's take the Program Variant
		ProgramVariant pvariant = engine.getVariants().get(0);

		ModificationPoint buggyStatementModPoint = pvariant.getModificationPoints().get(0);

		// We find the buggy modification point
		assertEquals(72, buggyStatementModPoint.getCodeElement().getPosition().getEndLine());

		AstorOperator testOperator = new ReplaceOp(); // We dont care about the
														// operator here

		CtType buggyClass = buggyStatementModPoint.getCtClass();

		// Important:I had to invoke this method to execute computesimlist
		// (it's private) before calling getfixspace()
		Ingredient ingredientSelected = cloneStrategy.getFixIngredient(buggyStatementModPoint, testOperator);

		assertNotNull(ingredientSelected);

		Queue<CtCodeElement> ingredientsToConsider = cloneStrategy.getfixspace(buggyStatementModPoint, testOperator,
				buggyClass);

		assertNotNull(ingredientsToConsider);
		// At least one ingredient
		assertFalse(ingredientsToConsider.isEmpty());

		// The suspicious class has 4 return statements.
		// They are: [return solve(f, min, max), return solve(f, min, max),
		// return solve(min, max), return m]
		assertEquals("Wrong number of ingredients collected", 3, ingredientsToConsider.size());
		List<CtCodeElement> ingredientsList = (List) ingredientsToConsider;

		List<String> solutions = new ArrayList(
				Arrays.asList("return solve(f, min, max)", "return solve(min, max)", "return m"));
		for (CtCodeElement ctCodeElement : ingredientsList) {
			assertTrue(solutions.remove(ctCodeElement.toString()));
		}
		// All ingredients returned
		assertTrue(solutions.isEmpty());

		log.debug("Ingredients to considers: " + ingredientsToConsider);
		// Now, from all ingredients
		Queue<CtCodeElement> ingredientsToConsiderInser = cloneStrategy.getfixspace(buggyStatementModPoint,
				new InsertAfterOp(), buggyClass);
		// the suspicious class has 22 statements (it does not include super
		// calls)
		assertEquals("Wrong number of ingredients collected", 20, ingredientsToConsiderInser.size());
		log.debug("All ingredients from space\n" + ingredientsToConsiderInser);
	}

}
