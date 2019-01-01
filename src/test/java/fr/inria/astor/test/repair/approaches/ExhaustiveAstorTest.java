package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.approaches.deeprepair.DeepRepairExhausitiveCloneEngine;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.ctscopes.CtPackageIngredientScope;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.RandomTransformationStrategy;
import fr.inria.astor.core.stats.PatchHunkStats;
import fr.inria.astor.core.stats.PatchStat;
import fr.inria.astor.core.validation.results.TestCasesProgramValidationResult;
import fr.inria.astor.test.repair.core.BaseEvolutionaryTest;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.CommandSummary;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;
import fr.inria.main.evolution.ExtensionPoints;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

/**
 * 
 * @author matias
 *
 */
public class ExhaustiveAstorTest extends BaseEvolutionaryTest {

	/**
	 * Math 70 bug can be fixed by replacing a method invocation inside a return
	 * statement. + return solve(f, min, max); - return solve(min, max); One
	 * solution with local scope, another with package
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testExhaustiveMath70LocalSolutionAssertPatch() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", ExecutionMode.EXASTOR.toString().toLowerCase(),
				"-failing", "org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.9", "-out",
				out.getAbsolutePath(),
				//
				"-scope", "package", "-seed", "10", "-maxgen", "10000", "-stopfirst", "false", //
				"-maxtime", "100",
				// For excluding regression
				"-excludeRegression"

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();

		AstorOutputStatus status = main1.getEngine().getOutputStatus();

		assertEquals(AstorOutputStatus.EXHAUSTIVE_NAVIGATED, status);

		assertTrue(solutions.size() > 0);

		List<PatchStat> patches = main1.getEngine().getPatchInfo();

		Assert.assertTrue(patches.size() > 0);

		Assert.assertEquals(2, patches.size());

		// Patch only found by jgp
		PatchHunkStats hunkSolution = getHunkSolution(patches, "return solve(f, min, max)", "CtReturnImpl|CtBlockImpl");
		Assert.assertNotNull(hunkSolution);

		PatchHunkStats hunkSolution2 = getHunkSolution(patches, "return solve(f, initial, max)",
				"CtReturnImpl|CtBlockImpl");
		Assert.assertNotNull(hunkSolution2);

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testExhaustiveTibraMath70LocalSolutionAssertPatch() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", ExecutionMode.EXASTOR.toString().toLowerCase(),
				"-failing", "org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.9", "-out",
				out.getAbsolutePath(),
				//
				"-scope", "package", "-seed", "10", "-maxgen", "10000", "-stopfirst", "false", //
				"-maxtime", "100", "-loglevel", "INFO",
				// For excluding regression
				"-excludeRegression", "-parameters", "maxCombinationVariableLimit:true"
						// reduced search space
						+ ":maxVarCombination:1" + ":ingredienttransformstrategy:"
						+ RandomTransformationStrategy.class.getCanonicalName()

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();

		AstorOutputStatus status = main1.getEngine().getOutputStatus();

		assertEquals(AstorOutputStatus.EXHAUSTIVE_NAVIGATED, status);

		assertTrue(solutions.size() > 0);

		List<PatchStat> patches = main1.getEngine().getPatchInfo();

		Assert.assertTrue(patches.size() >= 2);

		PatchHunkStats hunkSolution = getHunkSolution(patches, "this.f = f", "CtAssignmentImpl|CtBlockImpl");
		Assert.assertNotNull(hunkSolution);

		PatchHunkStats hunkSolution2 = getHunkSolution(patches, "return solve(f, initial, max)",
				"CtReturnImpl|CtBlockImpl");
		Assert.assertNotNull(hunkSolution2);

	}

	/**
	 * Math 70 bug can be fixed by replacing a method invocation inside a return
	 * statement. + return solve(f, min, max); - return solve(min, max); One
	 * solution with local scope, another with package
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testExhaustiveMath70LocalSolution() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", ExecutionMode.EXASTOR.toString().toLowerCase(),
				"-failing", "org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(),
				//
				"-scope", "package", "-seed", "10", "-maxgen", "10000", "-stopfirst", "false", //
				"-maxtime", "100",
				// For excluding regression
				"-excludeRegression"

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();

		AstorOutputStatus status = main1.getEngine().getOutputStatus();

		assertEquals(AstorOutputStatus.EXHAUSTIVE_NAVIGATED, status);

		assertTrue(solutions.size() > 0);
		// assertEquals(1, solutions.size());
		ProgramVariant variant = solutions.get(0);
		TestCasesProgramValidationResult validationResult = (TestCasesProgramValidationResult) variant
				.getValidationResult();
		assertTrue(validationResult.isRegressionExecuted());

		assertFalse(solutions.isEmpty());

		OperatorInstance mi = variant.getOperations().values().iterator().next().get(0);
		assertNotNull(mi);
		// assertEquals(IngredientSpaceScope.LOCAL, mi.getIngredientScope());

		// mi.getIngredientScope()
		// Program variant ref to
		Collection<CtType<?>> affected = variant.getAffectedClasses();
		List<CtClass> progVariant = variant.getModifiedClasses();
		assertFalse(progVariant.isEmpty());

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testExhaustiveMath70Status() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", ExecutionMode.EXASTOR.toString().toLowerCase(),
				"-failing", "org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "1", "-out", out.getAbsolutePath(),
				//
				"-scope", "package", "-seed", "10", "-maxgen", "100000", "-stopfirst", "false", //
				"-maxtime", "100",
				// For excluding regression
				"-excludeRegression"

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();

		AstorOutputStatus status = main1.getEngine().getOutputStatus();

		assertEquals(AstorOutputStatus.EXHAUSTIVE_NAVIGATED, status);

		assertTrue(solutions.size() > 0);

		CommandSummary cs = new CommandSummary(args);
		cs.command.put("-maxgen", "10");
		main1.execute(cs.flat());
		assertEquals(AstorOutputStatus.MAX_GENERATION, main1.getEngine().getOutputStatus());

	}

	/**
	 * Math 70 bug can be fixed by replacing a method invocation inside a return
	 * statement. + return solve(f, min, max); - return solve(min, max); One
	 * solution with local scope, another with package
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@Test
	@Ignore
	public void testExhaustiveCloneMath70LocalSolution() throws Exception {

		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "custom", "-customengine",
				DeepRepairExhausitiveCloneEngine.class.getCanonicalName(), ExtensionPoints.REPAIR_OPERATORS.argument(),
				"fr.inria.astor.approaches.jgenprog.operators.InsertAfterOp:fr.inria.astor.approaches.jgenprog.operators.InsertBeforeOp:fr.inria.astor.approaches.jgenprog.operators.ReplaceOp",

				"-failing", "org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.1", "-out",
				out.getAbsolutePath(),
				//
				// "-scope", "package",
				"-seed", "10", "-maxgen", "100000", "-stopfirst", "false", //
				"-maxtime", "100", "-scope", //

				// CtClassIngredientSpace.class.getCanonicalName(),//,
				CtPackageIngredientScope.class.getCanonicalName(), //
				// "fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtGlobalIngredientScope",
				"-clonegranularity", "spoon.reflect.declaration.CtExecutable"//
				, "-transformingredient", "-population", "1", "-learningdir", learningDir.getAbsolutePath()

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		// assertEquals(1, solutions.size());
		ProgramVariant variant = solutions.get(0);
		TestCasesProgramValidationResult validationResult = (TestCasesProgramValidationResult) variant
				.getValidationResult();
		assertTrue(validationResult.isRegressionExecuted());

		assertFalse(solutions.isEmpty());

		OperatorInstance mi = variant.getOperations().values().iterator().next().get(0);
		assertNotNull(mi);
		// assertEquals(IngredientSpaceScope.LOCAL, mi.getIngredientScope());

		// mi.getIngredientScope()
		// Program variant ref to
		Collection<CtType<?>> affected = variant.getAffectedClasses();
		List<CtClass> progVariant = variant.getModifiedClasses();
		assertFalse(progVariant.isEmpty());

	}

}
