package fr.inria.astor.test.repair.evaluation.extensionpoints.ingredients;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.validation.TestCaseVariantValidationResult;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.population.PopulationConformation;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import fr.inria.astor.test.repair.core.BaseEvolutionaryTest;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

/**
 * 
 * @author Matias Martinez
 *
 */
public class IngredientScopeTest extends BaseEvolutionaryTest {

	@SuppressWarnings("rawtypes")
	@Test
	public void testMath70LocalSimple() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-flthreshold", "1");
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "INFO");
		cs.command.put("-saveall", "true");
		cs.append("-parameters", ("logtestexecution:true"));

		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());
		ProgramVariant variant = solutions.get(0);

		OperatorInstance mi = variant.getOperations().values().iterator().next().get(0);
		assertNotNull(mi);
		assertEquals(IngredientPoolScope.LOCAL, mi.getIngredientScope());

		assertEquals("return solve(f, min, max)", mi.getModified().toString());

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testMath70LocalSimpleAutocompile() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-location", new File("./examples/math_70_ac").getAbsolutePath());
		cs.command.put("-flthreshold", "1");
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "DEBUG");
		cs.command.put("-saveall", "true");
		cs.append("-parameters", ("logtestexecution:true:autocompile:true"));

		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());
		ProgramVariant variant = solutions.get(0);

		OperatorInstance mi = variant.getOperations().values().iterator().next().get(0);
		assertNotNull(mi);
		assertEquals(IngredientPoolScope.LOCAL, mi.getIngredientScope());

		assertEquals("return solve(f, min, max)", mi.getModified().toString());

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
	public void testMath70LocalSolution() throws Exception {
		AstorMain main1 = new AstorMain();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "DEBUG");
		cs.command.put("-maxgen", "500");
		cs.command.put("-out", out.getAbsolutePath());
		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());
		ProgramVariant variant = solutions.get(0);
		TestCaseVariantValidationResult validationResult = (TestCaseVariantValidationResult) variant
				.getValidationResult();

		assertTrue(validationResult.isRegressionExecuted());

		OperatorInstance mi = variant.getOperations().values().iterator().next().get(0);
		assertNotNull(mi);
		assertEquals(IngredientPoolScope.LOCAL, mi.getIngredientScope());

		// mi.getIngredientScope()
		// Program variant ref to
		Collection<CtType<?>> affected = variant.getAffectedClasses();
		List<CtClass> progVariant = variant.getModifiedClasses();
		assertFalse(progVariant.isEmpty());

		for (CtType aff : affected) {
			CtType ctcProgVariant = returnByName(progVariant, (CtClass) aff);
			assertNotNull(ctcProgVariant);
			assertFalse(ctcProgVariant == aff);

			// Classes from affected set must be not equals to the program
			// variant cloned ctclasses,
			// due to these have include the changes applied for repairing the
			// bug.
			assertNotEquals(ctcProgVariant, aff);

			// Classes from affected set must be equals to the spoon model
			CtType ctspoon = returnByName(MutationSupporter.getFactory().Type().getAll(), (CtClass) aff);
			assertNotNull(ctcProgVariant);
			assertEquals(ctspoon, aff);
		}
	}

	/**
	 * Return the ct type from the collection according tho the class passed as
	 * parameter.
	 * 
	 * @param classes
	 * @param target
	 * @return
	 */
	private CtType returnByName(Collection<?> classes, CtClass target) {

		for (Object ctClass : classes) {
			if (((CtType) ctClass).getSimpleName().equals(target.getSimpleName())) {
				return (CtType) ctClass;
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testMath70PackageSolutions() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(),
				//
				"-scope", "package", "-seed", "10", "-maxgen", "2000", "-stopfirst", "false", "-maxtime", "10",
				"-population", "1", "-reintroduce", PopulationConformation.PARENTS.toString(),
				// Parameters:
				"-parameters", "maxnumbersolutions:2" };// Two ingredients from package
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertEquals(AstorOutputStatus.STOP_BY_PATCH_FOUND, main1.getEngine().getOutputStatus());
		assertEquals(2, solutions.size());
	}
}
