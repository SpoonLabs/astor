package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import fr.inria.astor.approaches.tos.core.TOSBRApproach;
import fr.inria.astor.approaches.tos.entity.TOSCounter;
import fr.inria.astor.approaches.tos.entity.TOSDynamicIngredient;
import fr.inria.astor.approaches.tos.entity.TOSVariablePlaceholder;
import fr.inria.astor.approaches.tos.ingredients.TOSBStatementIngredientSpace;
import fr.inria.astor.approaches.tos.ingredients.TOSRandomTransformationStrategy;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.astor.util.CommandSummary;
import fr.inria.astor.util.MapCounter;
import fr.inria.astor.util.Probability;
import fr.inria.astor.util.StringUtil;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class TOSBRTest {

	protected static final String LOG_LEVEL = "INFO";

	@Test
	public void testFindRepairFromTOS1() throws Exception {
		int nrPlaceholders = 1;

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", TOSBRApproach.class.getCanonicalName());
		command.command.put("-maxgen", "100");
		command.command.put("-loglevel", LOG_LEVEL);
		command.command.put("-scope", "package");
		command.command.put("-stopfirst", "true");

		command.command.put("-parameters", "nrPlaceholders:" + nrPlaceholders + File.pathSeparator
				+ "duplicateingredientsinspace" + File.pathSeparator + "true");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);
		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientSpace ingredientPool = approach.getIngredientPool();
		TOSBStatementIngredientSpace tosIngredientPool = (TOSBStatementIngredientSpace) ingredientPool;

		assertNotEquals(AstorOutputStatus.ERROR, approach.getOutputStatus());

		assertTrue(approach.getSolutions().size() > 0);

		// First patch
		ProgramVariant solution0 = approach.getSolutions().get(0);

		OperatorInstance opI0 = solution0.getOperations().values().stream().filter(e -> e.size() > 0).findFirst().get()
				.get(0);
		assertNotNull(opI0);
		TOSDynamicIngredient ing = (TOSDynamicIngredient) opI0.getIngredient();
		assertNotNull(ing);

		CtElement fix = opI0.getModified();
		assertEquals("return solve(f, min, max)", fix.toString());

		assertEquals("return solve(_UnivariateRealFunction_0, min, max)", ing.getDerivedFrom().toString());

	}

	@Test
	public void testFindRepairFromTOS2() throws Exception {
		int nrPlaceholders = 2;

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", TOSBRApproach.class.getCanonicalName());
		command.command.put("-maxgen", "100");
		command.command.put("-loglevel", LOG_LEVEL);
		command.command.put("-scope", "package");
		command.command.put("-stopfirst", "false");

		command.command.put("-parameters", "nrPlaceholders:" + nrPlaceholders + File.pathSeparator
				+ "duplicateingredientsinspace" + File.pathSeparator + "true");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);
		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientSpace ingredientPool = approach.getIngredientPool();
		TOSBStatementIngredientSpace tosIngredientPool = (TOSBStatementIngredientSpace) ingredientPool;

		assertNotEquals(AstorOutputStatus.ERROR, approach.getOutputStatus());

		assertTrue(approach.getSolutions().size() > 0);

		// First patch
		ProgramVariant solution0 = approach.getSolutions().get(0);
		String patch0 = "return solve(f, absoluteAccuracy, max)";
		String baseIng0 = "return solve(_UnivariateRealFunction_0, _double_1, max)";
		assertPatch(solution0, patch0, baseIng0);

		ProgramVariant solution1 = approach.getSolutions().get(1);
		String patch1 = "return solve(f, initial, max)";
		String baseIng1 = "return solve(_UnivariateRealFunction_0, _double_1, max)";
		assertPatch(solution1, patch1, baseIng1);

		ProgramVariant solution2 = approach.getSolutions().get(2);
		String patch2 = "return solve(f, defaultRelativeAccuracy, max)";
		String baseIng2 = "return solve(_UnivariateRealFunction_0, _double_1, max)";
		assertPatch(solution2, patch2, baseIng2);

		ProgramVariant solution3 = approach.getSolutions().get(3);
		String patch3 = "return solve(f, min, max)";
		String baseIng3 = "return solve(_UnivariateRealFunction_0, _double_1, max)";
		assertPatch(solution3, patch3, baseIng3);

		ProgramVariant solution4 = approach.getSolutions().get(4);
		String patch4 = "return solve(f, functionValueAccuracy, max)";
		String baseIng4 = "return solve(_UnivariateRealFunction_0, _double_1, max)";
		assertPatch(solution4, patch4, baseIng4);

		ProgramVariant solution5 = approach.getSolutions().get(6);
		String patch5 = "return solve(f, relativeAccuracy, max)";
		String baseIng5 = "return solve(_UnivariateRealFunction_0, _double_1, max)";
		assertPatch(solution5, patch5, baseIng5);
	}

	/**
	 * Assert the presence of a patch
	 * 
	 * @param solution
	 *            solution to validate
	 * @param patch
	 *            the patch we find
	 * @param baseIng
	 *            the parent of the patch
	 */
	private void assertPatch(ProgramVariant solution, String patch, String baseIng) {
		OperatorInstance opI0 = solution.getOperations().values().stream().filter(e -> e.size() > 0).findFirst().get()
				.get(0);
		assertNotNull(opI0);
		TOSDynamicIngredient ing = (TOSDynamicIngredient) opI0.getIngredient();
		assertNotNull(ing);

		CtElement fix = opI0.getModified();
		assertEquals(patch, fix.toString());

		if (baseIng != null) {
			assertEquals(baseIng, ing.getDerivedFrom().toString());
		}
	}

	/**
	 * This test focuses on the instantiation of TOS: in particular, 3 cases.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTOStransformation() throws Exception {
		int nrPlaceholders = 1;

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", TOSBRApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", LOG_LEVEL);
		command.command.put("-scope", "package");
		command.command.put("-stopfirst", "true");

		command.command.put("-parameters", "nrPlaceholders:" + nrPlaceholders + File.pathSeparator
				+ "duplicateingredientsinspace" + File.pathSeparator + "true");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);
		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientSpace ingredientPool = approach.getIngredientPool();
		TOSBStatementIngredientSpace tosIngredientPool = (TOSBStatementIngredientSpace) ingredientPool;

		assertNotEquals(AstorOutputStatus.ERROR, approach.getOutputStatus());

		List<ModificationPoint> mps = approach.getVariants().get(0).getModificationPoints();

		String packageName = "org.apache.commons.math.analysis.solvers";

		List<Ingredient> ingredientsPackage = tosIngredientPool.retrieveIngredients(packageName);

		assertTrue(ingredientsPackage.size() > 0);

		// ***We list the ingrediets of package
		// "org.apache.commons.math.analysis.solvers"
		for (int i = 0; i < ingredientsPackage.size(); i++) {
			Ingredient ingi = ingredientsPackage.get(i);
			System.out.println("i " + i + " " + StringUtil.trunc(ingi.getCode()));
		}

		// We get the suspicious Line 72 of class BisectionSolver.
		SuspiciousModificationPoint mpL72 = (SuspiciousModificationPoint) mps.get(0);

		assertEquals(72, mpL72.getSuspicious().getLineNumber());

		// ******* case where variable is not mapped

		Ingredient i5 = ingredientsPackage.get(5);
		assertEquals("return solve(min, _double_0)", i5.getCode().toString());

		Ingredient i11 = ingredientsPackage.get(11);
		assertEquals("fmin = f.value(_double_0)", i11.getCode().toString());

		TOSRandomTransformationStrategy ts = new TOSRandomTransformationStrategy();

		List<Ingredient> i11Trans = ts.transform(mpL72, i11);
		assertTrue(i11Trans.isEmpty());

		// ******* case all mapped

		List<Ingredient> i5Trans = ts.transform(mpL72, i5);
		assertFalse(i5Trans.isEmpty());

		// i 1 return solve(f, _double_0, max)
		Ingredient i1 = ingredientsPackage.get(1);
		assertEquals("return solve(f, _double_0, max)", i1.getCode().toString());

		SuspiciousModificationPoint mpL89 = (SuspiciousModificationPoint) mps.get(7);
		assertEquals(89, mpL89.getSuspicious().getLineNumber());

		// ************* case a placeholder not mapped

		// We start with a ingredient with a placeholder and a mapped var.
		// i 25 setResult(m, _int_0)
		Ingredient i26 = ingredientsPackage.get(26);
		assertEquals("setResult(m, _int_0)", i26.getCode().toString());

		// At line 89, var *m* and placeholder *_int_0* should be mapped.
		List<Ingredient> i25Trans = ts.transform(mpL89, i26);
		assertFalse(i25Trans.isEmpty());

		// Now, another experiment.
		// On line 89, we remove from the list of variables in scope/context,
		// all *int* vars
		// So, the placeholder must not be mapped
		boolean removedInt = mpL89.getContextOfModificationPoint()
				.removeIf(e -> e.getType().getSimpleName().equals("int"));
		assertTrue(removedInt);
		// Now, let's check that the ingredient has not transformation
		i25Trans = ts.transform(mpL89, i26);
		assertTrue(i25Trans.isEmpty());

		// One case nothing mapped

		boolean removedAll = mpL89.getContextOfModificationPoint().removeIf(e -> true);
		assertTrue(removedAll);
		// No vars in context of L89
		i25Trans = ts.transform(mpL89, i26);
		assertTrue(i25Trans.isEmpty());

	}

	/**
	 * Tests the occurrence of TOS with 1 placeholder.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTOSOcurrencesSize1() throws Exception {
		int nrPlaceholders = 1;
		TOSBRApproach approach = runTestForPlaceholder(nrPlaceholders);

		IngredientSpace ingredientPool = approach.getIngredientPool();
		TOSBStatementIngredientSpace tosIngredientPool = (TOSBStatementIngredientSpace) ingredientPool;

		TOSCounter tosCounter = tosIngredientPool.getTosCounter();
		assertNotNull(tosCounter);
		MapCounter<String> ocurrenceTOS = tosCounter.getTosOcurrenceCounter();
		assertEquals(2, (int) ocurrenceTOS.get("_double_0 = m"));
		assertEquals(1, (int) ocurrenceTOS.get("min = _double_0"));
		assertEquals(1, (int) ocurrenceTOS.get("max = _double_0"));
		// TODO: attention: this keyword not written any more
		// assertEquals(2, (int) ocurrenceTOS.get("return solve(this.f,
		// _double_0, max)"));
		assertEquals(2, (int) ocurrenceTOS.get("return solve(f, _double_0, max)"));

	}

	/**
	 * Tests the occurrence of TOS with 2 placeholders.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTOSOcurrencesSize2() throws Exception {
		int nrPlaceholders = 2;
		TOSBRApproach approach = runTestForPlaceholder(nrPlaceholders);

		IngredientSpace ingredientPool = approach.getIngredientPool();
		TOSBStatementIngredientSpace tosIngredientPool = (TOSBStatementIngredientSpace) ingredientPool;

		TOSCounter tosCounter = tosIngredientPool.getTosCounter();
		assertNotNull(tosCounter);
		MapCounter<String> ocurrenceTOS = tosCounter.getTosOcurrenceCounter();

		assertEquals(2, (int) ocurrenceTOS.get("_double_0 = _double_1"));
		assertEquals(2, (int) ocurrenceTOS.get("_double_0 = f.value(_double_1)"));

	}

	private TOSBRApproach runTestForPlaceholder(int nrPlaceholders) throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", TOSBRApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", LOG_LEVEL);
		command.command.put("-scope", "package");

		command.command.put("-parameters", "nrPlaceholders:" + nrPlaceholders + File.pathSeparator
				+ "duplicateingredientsinspace" + File.pathSeparator + "true");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);
		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientSpace ingredientPool = approach.getIngredientPool();
		// one location i.e., the package
		assertEquals(1, ingredientPool.getLocations().size());
		assertEquals("org.apache.commons.math.analysis.solvers", ingredientPool.getLocations().get(0).toString());

		TOSBStatementIngredientSpace tosIngredientPool = (TOSBStatementIngredientSpace) ingredientPool;

		// We should have at least one ingredient
		assertTrue(tosIngredientPool.getAllIngredients().size() > 0);

		// For each ingredient
		for (Object ingredient : tosIngredientPool.getAllIngredients()) {

			TOSVariablePlaceholder TOSingredient = (TOSVariablePlaceholder) ingredient;
			// Nr of placeholders in the ingrfdient idem to the configuration
			assertEquals(nrPlaceholders, TOSingredient.getPlaceholderVarNamesMappings().keySet().size());
			assertEquals(nrPlaceholders, TOSingredient.getPalceholders().keySet().size());
			// The ingredient is different to the element from it was mined
			assertNotEquals(TOSingredient.getDerivedFrom(), TOSingredient.getCode());
			// Each placeholder must exist on the ingredient
			for (String placeholder : TOSingredient.getPalceholders().keySet()) {
				assertTrue(TOSingredient.getCode().toString().contains(placeholder));
			}
			// Retrieve all variables from the ingredient
			List<CtVariableAccess> varAccessCollected = VariableResolver.collectVariableAccess(TOSingredient.getCode(),
					true);

			// Each variable not transformed must appears in the list of
			// collected variables
			for (CtVariableAccess notModified : TOSingredient.getVariablesNotModified()) {
				assertTrue(varAccessCollected.contains(notModified));
			}

			// Each placeholder variable not transformed must appears in the
			// list of collected variables
			for (String placeholder : TOSingredient.getPalceholders().keySet()) {

				for (CtVariableAccess varModified : TOSingredient.getPalceholders().get(placeholder)) {
					assertTrue(varAccessCollected.contains(varModified));
				}
			}

		}
		// Show Ingredients
		String packageName = "org.apache.commons.math.analysis.solvers";

		List<Ingredient> ingredientsPackage = tosIngredientPool.retrieveIngredients(packageName);

		assertTrue(ingredientsPackage.size() > 0);

		// ***We list the ingrediets of package
		// "org.apache.commons.math.analysis.solvers"
		for (int i = 0; i < ingredientsPackage.size(); i++) {
			Ingredient ingi = ingredientsPackage.get(i);
			System.out.println("**\nTOS nr " + i + ":\n " + (ingi.getCode().toString()));
		}

		return approach;
	}

	/**
	 * Test the probabilities of ingredients
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIngredientCounters() throws Exception {
		int nrPlaceholders = 1;

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", TOSBRApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", LOG_LEVEL);
		command.command.put("-scope", "package");
		command.command.put("-stopfirst", "true");

		command.command.put("-parameters", "nrPlaceholders:" + nrPlaceholders + File.pathSeparator
				+ "duplicateingredientsinspace" + File.pathSeparator + "true");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);
		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientSpace ingredientPool = approach.getIngredientPool();
		TOSBStatementIngredientSpace tosIngredientPool = (TOSBStatementIngredientSpace) ingredientPool;

		TOSCounter tosCounter = tosIngredientPool.getTosCounter();
		assertNotNull(tosCounter);

		assertTrue(tosCounter.getTosOcurrenceCounter().size() > 0);
		assertTrue(tosCounter.getLinkTemplateElements().size() > 0);

		Map<String, Double> probDist = tosCounter.getProbabilitiesTOS().getProbDist();
		assertNotNull(probDist);

		Probability<String> prob = tosCounter.getProbabilitiesTOS();
		Map<String, Double> pProb = prob.getProbDist();
		double previous = 1d;
		double sum = 0d;
		for (String key : pProb.keySet()) {
			Double prob_key = pProb.get(key);
			assertTrue(prob_key <= previous);
			sum += prob_key;
		}
		assertEquals(1d, (double) sum, 0.001d);

		Map<String, Double> pProbAcum = prob.getProbAccumulative();
		System.out.println(pProbAcum);
		double prob_key = 0d;
		for (String key : pProbAcum.keySet()) {
			prob_key = pProbAcum.get(key);
		}
		assertEquals(1d, (double) prob_key, 0.0001d);

	}
}
