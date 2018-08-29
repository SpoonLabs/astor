package fr.inria.astor.test.repair.evaluation.extensionpoints.ingredients;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.ingredientbased.IngredientBasedApproach;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.extension.IdentifierPriorityCriterion;
import fr.inria.astor.core.solutionsearch.population.PopulationConformation;
import fr.inria.astor.core.stats.PatchHunkStats;
import fr.inria.astor.core.stats.PatchStat;
import fr.inria.astor.core.stats.PatchStat.HunkStatEnum;
import fr.inria.astor.core.stats.PatchStat.PatchStatEnum;
import fr.inria.astor.core.validation.results.TestCasesProgramValidationResult;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class MultiPatchTest {

	@SuppressWarnings("rawtypes")
	@Test
	public void testMath70Steps() throws Exception {
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
				"-loglevel", "INFO", "-scope", "package", "-seed", "10", "-maxgen", "0", "-stopfirst", "false",
				"-maxtime", "10", "-population", "1", "-reintroduce", PopulationConformation.PARENTS.toString(),
				"-parameters", "logtestexecution:true:saveall:true" };

		System.out.println(Arrays.toString(args));
		main1.execute(args);

		ProgramVariant programVariant = main1.getEngine().getVariants().get(0);
		programVariant.setId(44);
		ModificationPoint mp0 = programVariant.getModificationPoints().get(0);
		assertEquals("return solve(min, max)", mp0.getCodeElement().toString());

		int currentGeneration = 10;
		IngredientBasedApproach ibra = (IngredientBasedApproach) main1.getEngine();
		List<Ingredient> ingredients = ibra.getIngredientPool().getIngredients(mp0.getCodeElement(),
				mp0.getCodeElement().getClass().getSimpleName());
		assertNotNull(ingredients);
		assertTrue(ingredients.size() > 0);

		Ingredient ingp = ingredients.stream().filter(e -> e.getCode().toString().equals("return solve(f, min, max)"))
				.findFirst().get();

		ProgramVariant nVariant = main1.getEngine().getVariantFactory().createProgramVariantFromAnother(programVariant,
				currentGeneration);

		boolean changeShadow = VariableResolver.changeShadowedVars(mp0.getCodeElement(), ingp.getCode());
		assertTrue(changeShadow);

		OperatorInstance op1 = createSecondPatch(nVariant, mp0, ingp.getCode(), currentGeneration);
		assertTrue("Wrong engine created", main1.getEngine() instanceof JGenProg);

		JGenProg jgp = (JGenProg) main1.getEngine();

		jgp.applyNewOperationsToVariantModel(nVariant, currentGeneration);

		// assertEquals("return solve(f, initial, max)",
		// mp0.getCodeElement().toString());
		boolean result = main1.getEngine().processCreatedVariant(nVariant, 10);
		assertTrue(result);
		main1.getEngine().reverseOperationInModel(nVariant, currentGeneration);
		///
		/////
		currentGeneration++;
		Ingredient ingp2 = ingredients.stream()
				.filter(e -> e.getCode().toString().equals("return solve(f, initial, max)")).findFirst().get();

		ProgramVariant nVariant2 = main1.getEngine().getVariantFactory().createProgramVariantFromAnother(programVariant,
				currentGeneration);

		ModificationPoint mp0_2 = nVariant2.getModificationPoints().get(0);
		boolean changeShadow2 = VariableResolver.changeShadowedVars(mp0_2.getCodeElement(), ingp2.getCode());
		assertFalse(changeShadow2);

		OperatorInstance op2 = createSecondPatch(nVariant2, mp0_2, ingp2.getCode(), currentGeneration);

		jgp.applyNewOperationsToVariantModel(nVariant2, currentGeneration);

		boolean result2 = main1.getEngine().processCreatedVariant(nVariant2, 10);
		assertTrue(result2);
		main1.getEngine().reverseOperationInModel(nVariant2, currentGeneration);

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testMath70StepsInverted1() throws Exception {
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
				"-loglevel", "INFO", "-scope", "package", "-seed", "10", "-maxgen", "0", "-stopfirst", "false",
				"-maxtime", "10", "-population", "1", "-reintroduce", PopulationConformation.PARENTS.toString(),
				"-parameters", "logtestexecution:true:saveall:true" };

		System.out.println(Arrays.toString(args));
		main1.execute(args);

		ProgramVariant programVariant = main1.getEngine().getVariants().get(0);
		programVariant.setId(44);
		ModificationPoint mp0 = programVariant.getModificationPoints().get(0);
		assertEquals("return solve(min, max)", mp0.getCodeElement().toString());

		int currentGeneration = 10;
		IngredientBasedApproach ibra = (IngredientBasedApproach) main1.getEngine();
		List<Ingredient> ingredients = ibra.getIngredientPool().getIngredients(mp0.getCodeElement(),
				mp0.getCodeElement().getClass().getSimpleName());
		assertNotNull(ingredients);
		assertTrue(ingredients.size() > 0);

		/////
		currentGeneration++;
		Ingredient ingp2 = ingredients.stream()
				.filter(e -> e.getCode().toString().equals("return solve(f, initial, max)")).findFirst().get();

		ProgramVariant nVariant2 = main1.getEngine().getVariantFactory().createProgramVariantFromAnother(programVariant,
				currentGeneration);

		ModificationPoint mp0_2 = nVariant2.getModificationPoints().get(0);
		boolean changeShadow2 = VariableResolver.changeShadowedVars(mp0_2.getCodeElement(), ingp2.getCode());
		assertFalse(changeShadow2);

		OperatorInstance op2 = createSecondPatch(nVariant2, mp0_2, ingp2.getCode(), currentGeneration);
		JGenProg jgp = (JGenProg) main1.getEngine();
		jgp.applyNewOperationsToVariantModel(nVariant2, currentGeneration);

		boolean result2 = main1.getEngine().processCreatedVariant(nVariant2, 10);
		assertTrue(result2);
		main1.getEngine().reverseOperationInModel(nVariant2, currentGeneration);
		/////
		Ingredient ingp = ingredients.stream().filter(e -> e.getCode().toString().equals("return solve(f, min, max)"))
				.findFirst().get();

		ProgramVariant nVariant = main1.getEngine().getVariantFactory().createProgramVariantFromAnother(programVariant,
				currentGeneration);

		boolean changeShadow = VariableResolver.changeShadowedVars(mp0.getCodeElement(), ingp.getCode());
		assertTrue(changeShadow);

		OperatorInstance op1 = createSecondPatch(nVariant, mp0, ingp.getCode(), currentGeneration);

		jgp.applyNewOperationsToVariantModel(nVariant, currentGeneration);

		// assertEquals("return solve(f, initial, max)",
		// mp0.getCodeElement().toString());
		boolean result = main1.getEngine().processCreatedVariant(nVariant, 10);
		assertTrue(result);
		main1.getEngine().reverseOperationInModel(nVariant, currentGeneration);
		///
	}

	@Test
	public void testMath70PriorityLocalSolution() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "1", "-out", out.getAbsolutePath(),
				"-scope", "package", "-seed", "10", "-maxgen", "10000", "-stopfirst", "false", "-maxtime", "15", //
				"-patchprioritization", IdentifierPriorityCriterion.class.getName(), "-parameters",
				"maxnumbersolutions:2"

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(2, solutions.size());
		ProgramVariant variant = solutions.get(0);
		assertTrue(((TestCasesProgramValidationResult) variant.getValidationResult()).isRegressionExecuted());

		assertEquals(AstorOutputStatus.STOP_BY_PATCH_FOUND, main1.getEngine().getOutputStatus());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testMath70Multi() throws Exception {
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
				"-population", "1", "-reintroduce", PopulationConformation.PARENTS.toString(), "-parameters",
				"maxnumbersolutions:2" };

		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();

		assertEquals(AstorOutputStatus.STOP_BY_PATCH_FOUND, main1.getEngine().getOutputStatus());
		assertEquals(2, solutions.size());

		System.out.println("Patches: ");
		for (PatchStat ps : main1.getEngine().getPatchInfo()) {

			// System.out.println("--> "+ ps.getStats().);
			List<PatchHunkStats> hunks = (List<PatchHunkStats>) ps.getStats().get(PatchStatEnum.HUNKS);
			System.out.println("hunks " + hunks.size());

			for (PatchHunkStats patchHunkStats : hunks) {
				System.out.println("--Original: " + patchHunkStats.getStats().get(HunkStatEnum.ORIGINAL_CODE));
				System.out.println("--Patch: " + patchHunkStats.getStats().get(HunkStatEnum.PATCH_HUNK_CODE));
			}

		}
		;

		ModificationPoint mp0 = main1.getEngine().getVariants().get(0).getModificationPoints().get(0);
		assertEquals("return solve(min, max)", mp0.getCodeElement().toString());

	}

	private OperatorInstance createSecondPatch(ProgramVariant variant, ModificationPoint modificationPoint,
			CtElement fix, int currentGeneration) {

		CtElement targetStmt = modificationPoint.getCodeElement();

		StatementOperatorInstance operation = new StatementOperatorInstance();

		operation.setOperationApplied(new ReplaceOp());
		operation.setModificationPoint(modificationPoint);
		operation.setParentBlock((CtBlock) targetStmt.getParent());
		operation.setOriginal(targetStmt);
		operation.setModified(fix);
		operation.defineParentInformation(modificationPoint);

		variant.putModificationInstance(currentGeneration, operation);
		operation.setModificationPoint(modificationPoint);

		return operation;
	}
}
