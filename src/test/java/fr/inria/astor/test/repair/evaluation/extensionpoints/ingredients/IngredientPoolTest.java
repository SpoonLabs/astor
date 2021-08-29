package fr.inria.astor.test.repair.evaluation.extensionpoints.ingredients;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPool;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.ingredientSearch.RandomSelectionTransformedIngredientStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.CtLocationIngredientSpace;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.MethodBasicIngredientScope;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.core.stats.Stats.GeneralStatEnum;
import fr.inria.astor.test.repair.core.BaseEvolutionaryTest;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test of Ingredient Space
 *
 * @author Matias Martinez
 *
 */
public class IngredientPoolTest extends BaseEvolutionaryTest {

	File out = null;

	public IngredientPoolTest() {
		out = new File(ConfigurationProperties.getProperty("workingDirectory"));
	}

	/**
	 * This test asserts the navigation of the ing search space using a modification
	 * point and a operation type The strategy used returns ingredients according to
	 * LCS distance compared with modificationPoint.
	 *
	 * @throws Exception
	 */
	@Test
	public void testMaxLcsSimSearchStrategyMath85() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				// We put 0 as max generation, so we force to not evolve the
				// population
				"-maxgen", "0", "-scope", "local", "-seed", "10", "-ingredientstrategy",
				MaxLcsSimSearchStrategy.class.getName() };

		main1.execute(args);
		JGenProg astor = (JGenProg) main1.getEngine();
		IngredientSearchStrategy ingStrategy = astor.getIngredientSearchStrategy();

		assertTrue(ingStrategy instanceof MaxLcsSimSearchStrategy);

		MaxLcsSimSearchStrategy maxLcsIngredientStrategy = (MaxLcsSimSearchStrategy) ingStrategy;

		// Let's take a modification point from the first variant. I take the
		// element at 12, it's an assignement.
		ModificationPoint mpoint = astor.getVariants().get(0).getModificationPoints().get(12);
		String modificationPoint_toString = mpoint.getCodeElement().toString().trim();
		Ingredient ingLast = ingStrategy.getFixIngredient(mpoint, null);
		Assert.assertNotNull(ingLast);
		boolean respectOrder = true;
		while (respectOrder) {
			Ingredient ingN = ingStrategy.getFixIngredient(mpoint, null);
			if (ingN == null)
				break;
			respectOrder = maxLcsIngredientStrategy.getLcsSimilarity(ingLast.getCode().toString().trim(),
					modificationPoint_toString) >= maxLcsIngredientStrategy
							.getLcsSimilarity(ingN.getCode().toString().trim(), modificationPoint_toString);
			ingLast = ingN;
		}
		Assert.assertTrue(respectOrder);
	}

	/**
	 * This test asserts the navigation of the ing search space using a modification
	 * point and a operation type The strategy used returns ingredients according to
	 * LCS distance compared with modificationPoint.
	 *
	 * @throws Exception
	 */
	@Test
	public void testMaxLcsSimSearchStrategyReplaceMath85() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				// We put 0 as max generation, so we force to not evolve the
				// population
				"-maxgen", "0", "-scope", "local", "-seed", "10", "-ingredientstrategy",
				MaxLcsSimSearchStrategy.class.getName() };

		main1.execute(args);
		JGenProg astor = (JGenProg) main1.getEngine();
		IngredientSearchStrategy ingStrategy = astor.getIngredientSearchStrategy();

		assertTrue(ingStrategy instanceof MaxLcsSimSearchStrategy);

		MaxLcsSimSearchStrategy maxLcsIngredientStrategy = (MaxLcsSimSearchStrategy) ingStrategy;
		//
		AstorOperator operator = new ReplaceOp();
		// Let's take a modification point from the first variant. I take the
		// element at 12, it's an assignement.
		ModificationPoint mpoint = astor.getVariants().get(0).getModificationPoints().get(12);
		String modificationPoint_toString = mpoint.getCodeElement().toString().trim();

		Ingredient ingLast = ingStrategy.getFixIngredient(mpoint, operator);
		Assert.assertNotNull(ingLast);
		boolean respectOrder = true;
		while (respectOrder) {
			Ingredient ingN = ingStrategy.getFixIngredient(mpoint, operator);
			if (ingN == null)
				break;
			respectOrder = maxLcsIngredientStrategy.getLcsSimilarity(ingLast.getCode().toString().trim(),
					modificationPoint_toString) >= maxLcsIngredientStrategy
							.getLcsSimilarity(ingN.getCode().toString().trim(), modificationPoint_toString);
			ingLast = ingN;
		}
		Assert.assertTrue(respectOrder);
	}

	/**
	 * This test asserts the navigation of the ing search space using a modification
	 * point and a operation type The strategy used returns ingredients according to
	 * their length (in term of number of chars)
	 *
	 * @throws Exception
	 */
	@Test
	public void testShortestIngredientMath85() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				// We put 0 as max generation, so we force to not evolve the
				// population
				"-maxgen", "0", "-scope", "local", "-seed", "10", "-ingredientstrategy",
				ShortestIngredientSearchStrategy.class.getName() };

		main1.execute(args);
		JGenProg astor = (JGenProg) main1.getEngine();
		IngredientSearchStrategy ingStrategy = astor.getIngredientSearchStrategy();
		// Let's take a modification point from the first variant. I take the
		// element at 12, it's an assignement.
		ModificationPoint mpoint = astor.getVariants().get(0).getModificationPoints().get(12);
		Ingredient ingLast = ingStrategy.getFixIngredient(mpoint, null);
		Assert.assertNotNull(ingLast);
		boolean respectOrder = true;
		while (respectOrder) {
			Ingredient ingN = ingStrategy.getFixIngredient(mpoint, null);
			if (ingN == null)
				break;
			respectOrder = ingLast.getCode().toString().length() <= ingN.getCode().toString().length();
			ingLast = ingN;
		}
		Assert.assertTrue(respectOrder);
	}

	/**
	 * This test asserts the navigation of the ing search space using a modification
	 * point and a operation type For this reason, we assert that the ingredients
	 * returned by the strategy are of the same type than the modif point.
	 *
	 * @throws Exception
	 */
	@Test
	public void testShortestIngredientReplaceMath85() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				// We put 0 as max generation, so we force to not evolve the
				// population
				"-maxgen", "0", "-scope", "local", "-seed", "10", "-ingredientstrategy",
				ShortestIngredientSearchStrategy.class.getName() };

		main1.execute(args);
		JGenProg astor = (JGenProg) main1.getEngine();
		IngredientSearchStrategy ingStrategy = astor.getIngredientSearchStrategy();

		//
		AstorOperator operator = new ReplaceOp();
		// Let's take a modification point from the first variant. I take the
		// element at 12, it's an assignement.
		ModificationPoint mpoint = astor.getVariants().get(0).getModificationPoints().get(12);
		Ingredient ingLast = ingStrategy.getFixIngredient(mpoint, operator);
		Assert.assertNotNull(ingLast);
		boolean respectOrder = true;
		while (respectOrder) {
			Ingredient ingN = ingStrategy.getFixIngredient(mpoint, operator);
			if (ingN == null)
				break;
			// System.out.println(mpoint.getCodeElement()+" vs "+
			// ingN.getCode());
			respectOrder = ingLast.getCode().toString().length() <= ingN.getCode().toString().length();
			ingLast = ingN;
			Assert.assertEquals(mpoint.getCodeElement().getClass().getName(), ingN.getCode().getClass().getName());
		}
		Assert.assertTrue(respectOrder);

	}

	@Test
	public void testMath85_ParticularIngredientStrategy() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "true",
				"-maxgen", "200", "-scope", "package", "-seed", "10", "-ingredientstrategy",
				RandomSelectionTransformedIngredientStrategy.class.getCanonicalName() };
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		validatePatchExistence(out + File.separator + "AstorMain-math_85/");
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);

	}

	@Test
	public void testMath85_AnyIngredientStrategy() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				"-maxgen", "100", "-scope", "package", "-seed", "10", "-ingredientstrategy",
				"DoneToFailIngredientStrat10" };
		System.out.println(Arrays.toString(args));
		try {
			main1.execute(args);
			fail();
		} catch (Exception e) {// Expected
		}
	}

	@SuppressWarnings("rawtypes")
	@Test
	// @Ignore
	public void testMath70WithFakeIngStrategy() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "package", "-seed", "10", "-maxgen", "50", "-ingredientstrategy",
				FakeIngredientStrategy.class.getCanonicalName() };
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.isEmpty());

	}

	@SuppressWarnings("rawtypes")

	@Test
	public void testMath85ScopeLocalSpace() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				// We put 0 as max generation, so we force to not evolve the
				// population
				"-maxgen", "0", "-scope", "local", "-seed", "10", "-ingredientstrategy",
				ShortestIngredientSearchStrategy.class.getName() };

		main1.execute(args);
		JGenProg astor = (JGenProg) main1.getEngine();
		IngredientSearchStrategy ingStrategy = astor.getIngredientSearchStrategy();

		//
		AstorOperator operator = new ReplaceOp();
		// Let's take a modification point from the first variant. I take the
		// element at 12, it's an assignement.
		ModificationPoint mpoint = astor.getVariants().get(0).getModificationPoints().get(12);
		Ingredient ingLast = ingStrategy.getFixIngredient(mpoint, operator);
		Assert.assertNotNull(ingLast);

		List<String> files = ingStrategy.getIngredientSpace().getLocations();
		Assert.assertTrue(files.size() > 0);
		Assert.assertTrue(files.contains(mpoint.getProgramVariant().getAffectedClasses().get(0).getQualifiedName()));

		List<Ingredient> ingredients = ingStrategy.getIngredientSpace().getIngredients(mpoint.getCodeElement());
		Assert.assertTrue(ingredients.size() > 0);
		Assert.assertTrue(hasIngredient(ingredients, ingLast));

		// Now, we check if all ingredients retrieved belongs affected classes
		for (Ingredient ctCodeElement : ingredients) {
			assertTrue(mpoint.getProgramVariant().getAffectedClasses()
					.contains(ctCodeElement.getCode().getParent(CtType.class)));
		}

	}

	@Test
	public void testMath280IngredientNull() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String maxgenerations = "10";
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog",
				// "-failing",
				// "org.apache.commons.math.distribution.NormalDistributionTest",
				"-location", new File("./examples/Math-issue-280").getAbsolutePath(), "-package", "org.apache.commons",
				"-srcjavafolder", "/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5",
				"-stopfirst", "false",
				// We put 0 as max generation, so we force to not evolve the
				// population
				"-maxgen", maxgenerations, "-scope", "package", "-seed", maxgenerations, "-ingredientstrategy",
				ShortestIngredientSearchStrategy.class.getName() };

		main1.execute(args);
		JGenProg astor = (JGenProg) main1.getEngine();
		Assert.assertEquals(maxgenerations,
				astor.getCurrentStat().getGeneralStats().get(GeneralStatEnum.NR_GENERATIONS).toString());

	}

	@Test
	public void testMath85ScopePackageSpace() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				// We put 0 as max generation, so we force to not evolve the
				// population
				"-maxgen", "0", "-scope", "package", "-seed", "10", "-ingredientstrategy",
				ShortestIngredientSearchStrategy.class.getName() };

		main1.execute(args);
		JGenProg astor = (JGenProg) main1.getEngine();
		IngredientSearchStrategy ingStrategy = astor.getIngredientSearchStrategy();

		//
		AstorOperator operator = new ReplaceOp();
		// Let's take a modification point from the first variant. I take the
		// element at 12, it's an assignement.
		ModificationPoint mpoint = astor.getVariants().get(0).getModificationPoints().get(12);
		Ingredient ingLast = ingStrategy.getFixIngredient(mpoint, operator);
		Assert.assertNotNull(ingLast);

		List<String> packages = ingStrategy.getIngredientSpace().getLocations();
		Assert.assertTrue(packages.size() > 0);
		Assert.assertTrue(packages.contains(
				mpoint.getProgramVariant().getAffectedClasses().get(0).getParent(CtPackage.class).getQualifiedName()));

		List<Ingredient> ingredients = ingStrategy.getIngredientSpace().getIngredients(mpoint.getCodeElement());
		Assert.assertTrue(ingredients.size() > 0);
		Assert.assertTrue(hasIngredient(ingredients, ingLast));

		boolean ingrePackageCorrect = false;
		// Now, we check if all ingredients retrieved belongs affected classes
		for (Ingredient ctCodeElement : ingredients) {
			for (CtType aff : mpoint.getProgramVariant().getAffectedClasses()) {
				if (aff.getPackage().equals(ctCodeElement.getCode().getParent(CtPackage.class)))
					ingrePackageCorrect = true;
			}
			;

		}
		assertTrue(ingrePackageCorrect);

	}

	/**
	 * FIXME: With flacoco, the first line identified is "org.apache.commons.math.distribution.AbstractContinuousDistribution l: 88, susp 1.0",
	 * where in GZoltar this one is not considered and line 97 is returned. Line 88 is the beggining of a catch block.
	 *
	 * This test might be too restrictive?
	 */
	@Test
	@Ignore
	public void testMath85ScopeMethodSpace() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				// We put 0 as max generation, so we force to not evolve the
				// population
				"-maxgen", "0", "-scope", MethodBasicIngredientScope.class.getCanonicalName(), //
				"-seed", "10", "-ingredientstrategy", ShortestIngredientSearchStrategy.class.getName() };

		main1.execute(args);
		JGenProg astor = (JGenProg) main1.getEngine();
		IngredientSearchStrategy ingStrategy = astor.getIngredientSearchStrategy();

		//
		AstorOperator operator = new ReplaceOp();
		// Let's take a modification point from the first variant. I take the
		// element at 12, it's an assignement.
		ModificationPoint mpoint = astor.getVariants().get(0).getModificationPoints().get(12);
		Ingredient ingLast = ingStrategy.getFixIngredient(mpoint, operator);
		Assert.assertNotNull(ingLast);

		List<String> methods = ingStrategy.getIngredientSpace().getLocations();
		Assert.assertTrue(methods.size() > 0);

		List<Ingredient> ingredients = ingStrategy.getIngredientSpace().getIngredients(mpoint.getCodeElement());
		Assert.assertTrue(ingredients.size() > 0);
		Assert.assertTrue(hasIngredient(ingredients, ingLast));

		CtExecutable exec = (mpoint.getCodeElement().getParent(CtExecutable.class));
		for (Ingredient ctCodeElement : ingredients) {
			assertEquals(exec, ctCodeElement.getCode().getParent(CtExecutable.class));
		}

	}

	private boolean hasIngredient(List<Ingredient> ingredients, Ingredient ing) {
		for (Ingredient codei : ingredients) {
			if (codei.toString().equals(ing.getCode().toString()))
				return true;
		}
		return false;
	}

	@Test
	public void testMath70WithCtLocation() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-seed", "10", "-maxgen", "100", "-scope",
				CtLocationIngredientSpace.class.getCanonicalName() };
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		// At least one solution in local scope mode.
		assertTrue(solutions.size() > 0);

		// We retrieve the engine
		JGenProg jgp = (JGenProg) main1.getEngine();
		IngredientPool ispace = jgp.getIngredientSearchStrategy().getIngredientSpace();
		// List of locations considered by the space
		List<CtElement> ctLocations = ispace.getLocations();
		// Only one class has suspicious:
		// org.apache.commons.math.analysis.solvers.BisectionSolver
		assertEquals(1, ctLocations.size());
		// Now, we location is a CtClass (see
		// CTLocationIngredientScope#calculateLocation)
		assertTrue(ctLocations.get(0) instanceof spoon.reflect.declaration.CtClass);
	}

	@Test
	public void testMath70WithCtLocationAvoidDuplicatesInSpace() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-stopfirst", "true", "-seed", "10", "-maxgen", "100", "-scope",
				CtLocationIngredientSpace.class.getCanonicalName(), "-parameters",
				"duplicateingredientsinspace:false" };
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		// At least one solution in local scope mode.
		assertTrue(solutions.size() > 0);

		// We retrieve the engine
		JGenProg jgp = (JGenProg) main1.getEngine();
		IngredientPool ispace = jgp.getIngredientSearchStrategy().getIngredientSpace();
		// List of locations considered by the space
		List<CtElement> ctLocations = ispace.getLocations();
		// Only one class has suspicious:
		// org.apache.commons.math.analysis.solvers.BisectionSolver
		assertEquals(1, ctLocations.size());
		// Now, we location is a CtClass (see
		// CTLocationIngredientScope#calculateLocation)
		assertTrue(ctLocations.get(0) instanceof spoon.reflect.declaration.CtClass);

		ProgramVariant pv = solutions.get(0);
		ModificationPoint mp = pv.getModificationPoints().get(7);
		assertEquals("return solve(f, min, max)", mp.getCodeElement().toString());
		assertEquals(72, ((SuspiciousModificationPoint) mp).getSuspicious().getLineNumber());

		List ingredientsWithoutDuplicates = jgp.getIngredientSearchStrategy().getIngredientSpace()
				.getIngredients(mp.getCodeElement(), mp.getCodeElement().getClass().getSimpleName());
		assertEquals(3, ingredientsWithoutDuplicates.size());

		// Now, with duplicates:

		CommandSummary cs = new CommandSummary(args);
		cs.command.put("-parameters", "duplicateingredientsinspace:true");
		main1.execute(cs.flat());
		jgp = (JGenProg) main1.getEngine();
		pv = solutions.get(0);
		mp = pv.getModificationPoints().get(7);
		assertEquals("return solve(f, min, max)", mp.getCodeElement().toString());
		assertEquals(72, ((SuspiciousModificationPoint) mp).getSuspicious().getLineNumber());

		List ingredientsWithDuplicates = jgp.getIngredientSearchStrategy().getIngredientSpace()
				.getIngredients(mp.getCodeElement(), mp.getCodeElement().getClass().getSimpleName());
		assertEquals(4, ingredientsWithDuplicates.size());

	}

}
