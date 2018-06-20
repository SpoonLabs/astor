package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import fr.inria.astor.approaches.tos.core.PatchGenerator;
import fr.inria.astor.approaches.tos.core.TOSBRApproach;
import fr.inria.astor.approaches.tos.entity.TOSCounter;
import fr.inria.astor.approaches.tos.entity.TOSEntity;
import fr.inria.astor.approaches.tos.entity.TOSInstance;
import fr.inria.astor.approaches.tos.entity.placeholders.InvocationPlaceholder;
import fr.inria.astor.approaches.tos.entity.placeholders.LiteralPlaceholder;
import fr.inria.astor.approaches.tos.entity.placeholders.VarLiPlaceholder;
import fr.inria.astor.approaches.tos.entity.placeholders.VariablePlaceholder;
import fr.inria.astor.approaches.tos.entity.transf.Transformation;
import fr.inria.astor.approaches.tos.ingredients.LiteralsSpace;
import fr.inria.astor.approaches.tos.ingredients.TOSIngredientRandomSearchStrategy;
import fr.inria.astor.approaches.tos.ingredients.TOSIngredientPool;
import fr.inria.astor.approaches.tos.ingredients.processors.LiteralPlaceholderGenerator;
import fr.inria.astor.approaches.tos.ingredients.processors.TOSInvocationGenerator;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.manipulation.sourcecode.InvocationResolver;
import fr.inria.astor.core.manipulation.sourcecode.InvocationResolver.InvocationMatching;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPool;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.astor.util.MapCounter;
import fr.inria.astor.util.Probability;
import fr.inria.astor.util.StringUtil;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class TOSBRTest {

	protected static final String LOG_LEVEL = "INFO";
	protected Logger log = Logger.getLogger(this.getClass().getName());

	private String desactivateall() {
		return "excludevarliteralplaceholder" + File.pathSeparator + "true" + File.pathSeparator
				+ "excludevariableplaceholder" + File.pathSeparator + "true" + File.pathSeparator
				+ "excludeinvocationplaceholder" + File.pathSeparator + "true" + File.pathSeparator
				+ "excludeliteralplaceholder" + File.pathSeparator + "true";
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

		command.command.put("-parameters",
				"nrPlaceholders:" + nrPlaceholders + File.pathSeparator + "duplicateingredientsinspace"
						+ File.pathSeparator + "true" + File.pathSeparator + desactivateall() + File.pathSeparator
						+ "excludevariableplaceholder" + File.pathSeparator + "false");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);
		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientPool ingredientPool = approach.getIngredientPool();
		TOSIngredientPool tosIngredientPool = (TOSIngredientPool) ingredientPool;

		TOSIngredientRandomSearchStrategy strategy = new TOSIngredientRandomSearchStrategy(tosIngredientPool);

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
		strategy.getCacheInstances().clear();
		List<TOSInstance> i11Trans = strategy.getInstances(mpL72, i11);
		assertTrue(i11Trans.isEmpty());

		// ******* case all mapped
		strategy.getCacheInstances().clear();
		List<TOSInstance> i5Trans = strategy.getInstances(mpL72, i5);
		assertFalse(i5Trans.isEmpty());

		// i 1 return solve(f, _double_0, max)
		Ingredient i1 = ingredientsPackage.get(1);
		assertEquals("return solve(f, _double_0, max)", i1.getCode().toString());

		SuspiciousModificationPoint mpL89 = (SuspiciousModificationPoint) mps.get(7);
		assertEquals(89, mpL89.getSuspicious().getLineNumber());

		// ************* case a placeholder not mapped

		// We start with a ingredient with a placeholder and a mapped var.
		// i 25 setResult(m, _int_0)
		Ingredient i26 = ingredientsPackage.get(25);
		assertEquals("setResult(m, _int_0)", i26.getCode().toString());

		// At line 89, var *m* and placeholder *_int_0* should be mapped.
		List<TOSInstance> i25Trans = strategy.getInstances(mpL89, i26);
		assertFalse(i25Trans.isEmpty());

		// Now, another experiment.
		// On line 89, we remove from the list of variables in scope/context,
		// all *int* vars
		// So, the placeholder must not be mapped
		boolean removedInt = mpL89.getContextOfModificationPoint()
				.removeIf(e -> e.getType().getSimpleName().equals("int"));
		assertTrue(removedInt);
		strategy.getCacheInstances().clear();
		// Now, let's check that the ingredient has not transformation
		i25Trans = strategy.getInstances(mpL89, i26);
		assertTrue(i25Trans.toString(), i25Trans.isEmpty());

		// One case nothing mapped

		boolean removedAll = mpL89.getContextOfModificationPoint().removeIf(e -> true);
		assertTrue(removedAll);
		strategy.getCacheInstances().clear();
		// No vars in context of L89
		i25Trans = strategy.getInstances(mpL89, i26);
		assertTrue(i25Trans.isEmpty());

	}

	@Test
	public void testFindRepairFromTOS1() throws Exception {
		int nrPlaceholders = 1;

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", TOSBRApproach.class.getCanonicalName());
		command.command.put("-maxgen", "400");
		command.command.put("-loglevel", LOG_LEVEL);
		command.command.put("-scope", "package");
		command.command.put("-stopfirst", "false");

		command.command.put("-parameters",
				"nrPlaceholders:" + nrPlaceholders + File.pathSeparator + "duplicateingredientsinspace"
						+ File.pathSeparator + "true" + File.pathSeparator + desactivateall() + File.pathSeparator
						+ "excludevariableplaceholder" + File.pathSeparator + "false");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);
		TOSBRApproach approach = (TOSBRApproach) main.getEngine();

		// assertNotEquals(AstorOutputStatus.ERROR, approach.getOutputStatus());

		assertTrue(approach.getSolutions().size() > 0);

		// First patch
		ProgramVariant solution0 = approach.getSolutions().get(0);

		OperatorInstance opI0 = solution0.getOperations().values().stream().filter(e -> e.size() > 0).findFirst().get()
				.get(0);
		assertNotNull(opI0);
		TOSInstance ing = (TOSInstance) opI0.getIngredient();
		assertNotNull(ing);

		// assertPatch(approach.getSolutions(), "return solve(f, min, max)",
		// "return solve(f, min, _double_0)");
		assertPatch(approach.getSolutions(), "return solve(f, min, max)",
				"return solve(_UnivariateRealFunction_0, min, max)");
	}

	@Test
	public void testCheckSpace1() throws Exception {
		int nrPlaceholders = 1;

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", TOSBRApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", LOG_LEVEL);
		command.command.put("-scope", "package");
		command.command.put("-stopfirst", "false");

		command.command.put("-parameters",
				"nrPlaceholders:" + nrPlaceholders + File.pathSeparator + "duplicateingredientsinspace"
						+ File.pathSeparator + "true" + File.pathSeparator + desactivateall() + File.pathSeparator
						+ "excludevariableplaceholder" + File.pathSeparator + "false");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);
		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientPool ingredientPool = approach.getIngredientPool();
		TOSIngredientPool tosIngredientPool = (TOSIngredientPool) ingredientPool;

		TOSIngredientRandomSearchStrategy iss = new TOSIngredientRandomSearchStrategy(tosIngredientPool);

		// assertNotEquals(AstorOutputStatus.ERROR, approach.getOutputStatus());

		// assertTrue(approach.getSolutions().size() > 0);

		List<Ingredient> allin = tosIngredientPool.getAllIngredients();

		// First patch
		ProgramVariant solution0 = approach.getVariants().get(0);

		for (ModificationPoint mp : solution0.getModificationPoints()) {

			System.out.println(":MP " + mp);
			for (Ingredient ingredient : allin) {
				// System.out.println("-->Pattern: " + ingredient.getCode());
				List<TOSInstance> ingx = iss.getInstances(mp, ingredient);
				String last = null;
				for (TOSInstance tosIngredient : ingx) {
					tosIngredient.generatePatch();
					// System.out.println("--> " + tosIngredient.getCode());
					assertNotEquals(last, tosIngredient.getChacheCodeString());
					last = tosIngredient.getChacheCodeString();
				}
			}
		}

	}

	@Test
	public void testFindRepairFromTOS2() throws Exception {
		int nrPlaceholders = 2;

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", TOSBRApproach.class.getCanonicalName());
		command.command.put("-maxgen", "2000");
		command.command.put("-loglevel", LOG_LEVEL);
		command.command.put("-scope", "package");
		command.command.put("-stopfirst", "false");

		command.command.put("-parameters",
				"nrPlaceholders:" + nrPlaceholders + File.pathSeparator + "duplicateingredientsinspace"
						+ File.pathSeparator + "true" + File.pathSeparator + desactivateall() + File.pathSeparator
						+ "excludevariableplaceholder" + File.pathSeparator + "false");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);
		TOSBRApproach approach = (TOSBRApproach) main.getEngine();

		assertNotEquals(AstorOutputStatus.ERROR, approach.getOutputStatus());

		List<ProgramVariant> solutions = approach.getSolutions();
		assertTrue(solutions.size() > 0);

		String patch0 = "return solve(f, absoluteAccuracy, max)";
		String baseIng0 = "return solve(_UnivariateRealFunction_0, _double_1, max)";
		assertPatch(solutions, patch0, baseIng0);

		String patch1 = "return solve(f, initial, max)";
		String baseIng1 = "return solve(_UnivariateRealFunction_0, initial, _double_1)";
		assertPatch(solutions, patch1, baseIng1);

		String patch2 = "return solve(f, defaultRelativeAccuracy, max)";
		String baseIng2 = "return solve(_UnivariateRealFunction_0, _double_1, max)";
		assertPatch(solutions, patch2, baseIng2);

		String patch3 = "return solve(f, min, max)";
		String baseIng3 = "return solve(_UnivariateRealFunction_0, _double_1, max)";
		assertPatch(solutions, patch3, baseIng3);

		String patch4 = "return solve(f, functionValueAccuracy, max)";
		String baseIng4 = "return solve(_UnivariateRealFunction_0, _double_1, max)";
		assertPatch(solutions, patch4, baseIng4);

		String patch5 = "return solve(f, relativeAccuracy, max)";
		String baseIng5 = "return solve(f, _double_0, _double_1)";
		assertPatch(solutions, patch5, baseIng5);
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
		TOSInstance ing = (TOSInstance) opI0.getIngredient();
		assertNotNull(ing);

		CtElement fix = opI0.getModified();
		assertEquals(patch, fix.toString());

		if (baseIng != null) {
			assertEquals(baseIng, ing.getDerivedFrom().toString());
		}
	}

	private void assertPatch(List<ProgramVariant> solutions, String patch, String baseIng) {

		for (ProgramVariant programVariant : solutions) {

			OperatorInstance opI0 = programVariant.getOperations().values().stream().filter(e -> e.size() > 0)
					.findFirst().get().get(0);

			assertNotNull(opI0);
			CtElement fix = opI0.getModified();
			if (patch.equals(fix.toString())) {
				TOSInstance ing = (TOSInstance) opI0.getIngredient();
				assertNotNull(ing);

				assertEquals(patch, fix.toString());

				if (baseIng != null) {
					// assertEquals(baseIng, ing.getDerivedFrom().toString());
				}
				return;
			}
		}
		Assert.fail("No program variant with that patch");
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

		IngredientPool ingredientPool = approach.getIngredientPool();
		TOSIngredientPool tosIngredientPool = (TOSIngredientPool) ingredientPool;

		TOSCounter tosCounter = tosIngredientPool.getTosCounter();
		assertNotNull(tosCounter);
		MapCounter<String> ocurrenceTOS = tosCounter.getTosOcurrenceCounter();
		assertEquals(2, (int) ocurrenceTOS.get("_double_0 = m"));
		assertEquals(1, (int) ocurrenceTOS.get("min = _double_0"));
		assertEquals(1, (int) ocurrenceTOS.get("max = _double_0"));
		// TODO: attention: this keyword not written any more
		// assertEquals(2, (int) ocurrenceTOS.get("return solve(this.f,
		// _double_0, max)"));
		assertEquals(11, (int) ocurrenceTOS.get("return solve(f, _double_0, max)"));

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

		IngredientPool ingredientPool = approach.getIngredientPool();
		TOSIngredientPool tosIngredientPool = (TOSIngredientPool) ingredientPool;

		TOSCounter tosCounter = tosIngredientPool.getTosCounter();
		assertNotNull(tosCounter);
		MapCounter<String> ocurrenceTOS = tosCounter.getTosOcurrenceCounter();

		assertEquals(60, (int) ocurrenceTOS.get("_double_0 = _double_1"));
		assertEquals(5, (int) ocurrenceTOS.get("_double_0 = f.value(_double_1)"));

	}

	private TOSBRApproach runTestForPlaceholder(int nrPlaceholders) throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", TOSBRApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", LOG_LEVEL);
		command.command.put("-scope", "package");

		command.command.put("-parameters",
				"nrPlaceholders:" + nrPlaceholders + File.pathSeparator + "duplicateingredientsinspace"
						+ File.pathSeparator + "true" + File.pathSeparator + desactivateall() + File.pathSeparator
						+ "excludevariableplaceholder" + File.pathSeparator + "false");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);
		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientPool ingredientPool = approach.getIngredientPool();
		// one location i.e., the package
		assertEquals(1, ingredientPool.getLocations().size());
		assertEquals("org.apache.commons.math.analysis.solvers", ingredientPool.getLocations().get(0).toString());

		TOSIngredientPool tosIngredientPool = (TOSIngredientPool) ingredientPool;

		// We should have at least one ingredient
		assertTrue(tosIngredientPool.getAllIngredients().size() > 0);

		// For each ingredient
		for (Object ingredient : tosIngredientPool.getAllIngredients()) {

			TOSEntity TOSingredient = (TOSEntity) ingredient;
			// Forcing generation
			log.debug("\n-tos-> " + TOSingredient.generateCodeofTOS() + " "
					+ TOSingredient.generateCodeofTOS().getClass().getCanonicalName());

			VariablePlaceholder vp = (VariablePlaceholder) TOSingredient.getPlaceholders().get(0);

			// Nr of placeholders in the ingrfdient idem to the configuration
			assertEquals(nrPlaceholders, vp.getPlaceholderVarNamesMappings().keySet().size());
			assertEquals(nrPlaceholders, vp.getPalceholders().keySet().size());
			// The ingredient is different to the element from it was mined
			assertNotEquals(TOSingredient.getDerivedFrom(), TOSingredient.getCode());
			// Each placeholder must exist on the ingredient
			for (String placeholder : vp.getPalceholders().keySet()) {
				assertTrue(TOSingredient.getCode().toString().contains(placeholder));
			}
			// Retrieve all variables from the ingredient
			List<CtVariableAccess> varAccessCollected = VariableResolver.collectVariableAccess(
					TOSingredient.getDerivedFrom(), // TOSingredient.getCode(),
					true);

			// Each variable not transformed must appears in the list of
			// collected variables
			for (CtVariableAccess notModified : vp.getVariablesNotModified()) {

				assertTrue("notModified : " + notModified + " not in list " + varAccessCollected,
						varAccessCollected.contains(notModified));
			}

			// Each placeholder variable not transformed must appears in the
			// list of collected variables
			for (String placeholder : vp.getPalceholders().keySet()) {

				for (CtVariableAccess varModified : vp.getPalceholders().get(placeholder)) {
					// assertTrue(varAccessCollected.contains(varModified));
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
			log.debug("\n**\nTOS nr " + i + ":\n " + (ingi.getCode().toString()));
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

		command.command.put("-parameters",
				"nrPlaceholders:" + nrPlaceholders + File.pathSeparator + "duplicateingredientsinspace"
						+ File.pathSeparator + "true" + File.pathSeparator + desactivateall() + File.pathSeparator
						+ "excludevariableplaceholder" + File.pathSeparator + "false");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);
		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientPool ingredientPool = approach.getIngredientPool();
		TOSIngredientPool tosIngredientPool = (TOSIngredientPool) ingredientPool;

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

	@SuppressWarnings("rawtypes")
	@Test
	public void testInvocationsG1() throws Exception {
		int nrPlaceholders = 1;

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", TOSBRApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", "DEBUG" /* LOG_LEVEL */);
		command.command.put("-scope", "package");
		command.command.put("-stopfirst", "false");

		command.command.put("-parameters",
				"nrPlaceholders:" + nrPlaceholders + File.pathSeparator + "duplicateingredientsinspace"
						+ File.pathSeparator + "true" + File.pathSeparator + desactivateall() + File.pathSeparator
						+ "excludeinvocationplaceholder" + File.pathSeparator + "false");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);

		List<ModificationPoint> mps = main.getEngine().getVariants().get(0).getModificationPoints();

		ModificationPoint mp0 = mps.get(0);

		assertTrue(mp0.getCodeElement() instanceof CtStatement);

		TOSInvocationGenerator invgenerator = new TOSInvocationGenerator();

		List<InvocationPlaceholder> invphd = (List<InvocationPlaceholder>) invgenerator
				.createTOS((CtStatement) mp0.getCodeElement());

		assertTrue(invphd.size() > 0);
		InvocationPlaceholder iv0 = invphd.get(0);
		System.out.println(iv0);

		TOSEntity tos = new TOSEntity();
		tos.setDerivedFrom(mp0.getCodeElement());
		tos.getPlaceholders().add(iv0);
		tos.generateCodeofTOS();
		System.out.println(tos.getCode());

		assertEquals("return _org.apache.commons.math.analysis.solvers.BisectionSolver_double_0_(min, max)",
				tos.getCode().toString());

		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientPool ingredientPool = approach.getIngredientPool();
		// one location i.e., the package
		assertEquals(1, ingredientPool.getLocations().size());
		assertEquals("org.apache.commons.math.analysis.solvers", ingredientPool.getLocations().get(0).toString());

		TOSIngredientPool tosIngredientPool = (TOSIngredientPool) ingredientPool;

		List<Ingredient> allIngredients = tosIngredientPool.getAllIngredients();
		int i = 0;
		for (Ingredient ingredient : allIngredients) {
			TOSEntity tosIngredient = (TOSEntity) ingredient;
			CtElement codeTOS = tosIngredient.generateCodeofTOS();
			System.out.println("-ic->" + (i++) + " " + codeTOS);
		}

		TOSEntity ing0 = (TOSEntity) allIngredients.get(0);
		assertEquals("return _org.apache.commons.math.analysis.solvers.BisectionSolver_double_0_(f, min, max)",
				ing0.getCode().toString());
		assertEquals("return solve(f, min, max)", ing0.getDerivedFrom().toString());
		assertEquals(1, ing0.getPlaceholders().size());
		assertTrue((ing0.getPlaceholders().get(0)) instanceof InvocationPlaceholder);
		CtAbstractInvocation invocation0 = ((InvocationPlaceholder) (ing0.getPlaceholders().get(0))).getInvocation();

		TOSIngredientRandomSearchStrategy strategy = new TOSIngredientRandomSearchStrategy(tosIngredientPool);

		InvocationMatching matchingVar0 = InvocationResolver.mapImplicitInvocation(mp0.getCtClass(), invocation0);
		assertEquals(InvocationMatching.TARGET_SAME_TYPE.toString(), matchingVar0.toString());

		List<TOSInstance> instances0 = strategy.getInstances(mp0, ing0);
		assertEquals(1, instances0.size());
		CtElement code0 = instances0.get(0).generatePatch();
		assertEquals("return solve(f, min, max)", code0.toString());

		///
		TOSEntity ing5 = (TOSEntity) allIngredients.get(5);
		assertEquals("fmin = f._org.apache.commons.math.analysis.UnivariateRealFunction_double_0_(min)",
				ing5.getCode().toString());

		CtAbstractInvocation invocation5 = ((InvocationPlaceholder) (ing5.getPlaceholders().get(0))).getInvocation();

		InvocationMatching matchingVar5 = InvocationResolver.mapImplicitInvocation(mp0.getCtClass(), invocation5);
		assertEquals(InvocationMatching.TARGET_IS_VARIABLE.toString(), matchingVar5.toString());

		ModificationPoint mp6 = mps.get(6);

		List<TOSInstance> instances5 = strategy.getInstances(mp6, ing5);
		assertEquals(1, instances5.size());
		CtElement code5 = instances5.get(0).generatePatch();
		assertEquals("fmin = f.value(min)", code5.toString());

		ModificationPoint mp7 = mps.get(7);
		List<TOSInstance> instances7 = strategy.getInstances(mp7, ing5);
		assertEquals(1, instances7.size());
		CtElement code7 = instances5.get(0).generatePatch();
		assertEquals("fmin = f.value(min)", code7.toString());

		List<TOSInstance> instances0mp = strategy.getInstances(mp0, ing5);
		assertEquals(0, instances0mp.size());

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testCheckPackageInstanceSize1() throws Exception {
		int nrPlaceholders = 1;

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", TOSBRApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", "DEBUG" /* LOG_LEVEL */);
		command.command.put("-scope", "package");
		command.command.put("-stopfirst", "false");
		command.command.put("-flthreshold", "0.00");

		int nrmp = 30;
		command.command.put("-parameters",
				"nrPlaceholders:" + nrPlaceholders + File.pathSeparator + "duplicateingredientsinspace"
						+ File.pathSeparator + "true" + File.pathSeparator + desactivateall() + File.pathSeparator
						+ "excludevariableplaceholder" + File.pathSeparator + "false" //
						+ File.pathSeparator + "maxmodificationpoints" + File.pathSeparator + nrmp + File.pathSeparator
						+ "considerzerovaluesusp" + File.pathSeparator + "true" + File.pathSeparator
						+ "excludeliteralplaceholder" + File.pathSeparator + "true");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);

		List<ModificationPoint> mps = main.getEngine().getVariants().get(0).getModificationPoints();

		assertEquals(nrmp, mps.size());

		ModificationPoint mp0 = mps.get(0);

		assertTrue(mp0.getCodeElement() instanceof CtStatement);

		TOSInvocationGenerator invgenerator = new TOSInvocationGenerator();

		List<InvocationPlaceholder> invphd = (List<InvocationPlaceholder>) invgenerator
				.createTOS((CtStatement) mp0.getCodeElement());

		assertTrue(invphd.size() > 0);
		InvocationPlaceholder iv0 = invphd.get(0);
		System.out.println(iv0);

		TOSEntity tos = new TOSEntity();
		tos.setDerivedFrom(mp0.getCodeElement());
		tos.getPlaceholders().add(iv0);
		tos.generateCodeofTOS();
		System.out.println(tos.getCode());

		assertEquals("return _org.apache.commons.math.analysis.solvers.BisectionSolver_double_0_(min, max)",
				tos.getCode().toString());

		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientPool ingredientPool = approach.getIngredientPool();

		TOSIngredientPool tosIngredientPool = (TOSIngredientPool) ingredientPool;

		List<String> locations = tosIngredientPool.getLocations();
		assertTrue(locations.contains("org.apache.commons.math.analysis.solvers"));
		assertTrue(locations.contains("org.apache.commons.math.stat.descriptive.rank"));

		for (String loc : locations) {
			System.out.println("loc: " + loc);
		}
		int i = 0;
		List<Ingredient> allIngredientsRank = tosIngredientPool
				.retrieveIngredients("org.apache.commons.math.stat.descriptive.rank");
		for (Ingredient ingredient : allIngredientsRank) {
			TOSEntity tosIngredient = (TOSEntity) ingredient;
			CtElement codeTOS = tosIngredient.generateCodeofTOS();
			System.out.println(
					"\n-ic->" + (i++) + "\ntos:\n" + codeTOS + "\nderived from:\n" + tosIngredient.getDerivedFrom());
		}
		assertEquals(122, allIngredientsRank.size());

		String patch0 = "_long_0 = 0";
		String derived0 = "n = 0";

		TOSIngredientRandomSearchStrategy strategy = new TOSIngredientRandomSearchStrategy(tosIngredientPool);

		assertEquals(patch0, allIngredientsRank.get(0).getChacheCodeString());
		assertEquals(derived0, allIngredientsRank.get(0).getDerivedFrom().toString());

		List<TOSInstance> instances0 = strategy.getInstances(mp0, allIngredientsRank.get(0));
		assertTrue(instances0.isEmpty());

		String patch2 = "_double_0 = java.lang.Double.NaN";
		String derived2 = "value = java.lang.Double.NaN";

		Ingredient ing2 = findIngredient(allIngredientsRank, patch2);
		assertEquals(patch2, ing2.getChacheCodeString());
		assertEquals(derived2, ing2.getDerivedFrom().toString());

		List<TOSInstance> instances2 = strategy.getInstances(mp0, ing2);
		assertFalse(instances2.isEmpty());

		String patch121 = "return _Max_0";
		String derived121 = "return result";

		Ingredient ingredient121 = findIngredient(allIngredientsRank, patch121);
		assertEquals(patch121, ingredient121.getChacheCodeString());
		assertEquals(derived121, ingredient121.getDerivedFrom().toString());

		List<TOSInstance> instances121 = strategy.getInstances(mp0, ingredient121);
		assertTrue(instances121.isEmpty());

		String patch74 = "java.util.Arrays.sort(_double[]_0)";
		String derived74 = "java.util.Arrays.sort(sorted)";

		Ingredient ingredient74 = findIngredient(allIngredientsRank, patch74);
		assertEquals(patch74, ingredient74.getChacheCodeString());
		assertEquals(derived74, ingredient74.getDerivedFrom().toString());

		List<TOSInstance> instances74 = strategy.getInstances(mp0, ingredient74);
		assertTrue(instances74.isEmpty());

		// -ic->55
		// tos:
		// if (_int_0 == 0) {
		// return java.lang.Double.NaN;
		// }

		// Ingredient ingredient55 =findIngredient(allIngredientsRank, patch55);
		// List<TOSInstance> instances55 = strategy.getInstances(mp0,
		// ingredient55);
		// assertFalse(instances55.isEmpty());

		// -ic->60
		// tos:
		// if (_int_0 == 1) {
		// return values[begin];

		Ingredient ingredient60 = allIngredientsRank.get(60);
		List<TOSInstance> instances60 = strategy.getInstances(mp0, ingredient60);
		// assertTrue(instances60.isEmpty());

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testCheckPackageInstanceSize2() throws Exception {
		int nrPlaceholders = 2;

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", TOSBRApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", "INFO" /* LOG_LEVEL */);
		command.command.put("-scope", "package");
		command.command.put("-stopfirst", "false");
		command.command.put("-flthreshold", "0.00");

		int nrmp = 30;
		command.command.put("-parameters",
				"nrPlaceholders:" + nrPlaceholders + File.pathSeparator + "duplicateingredientsinspace"
						+ File.pathSeparator + "true" + File.pathSeparator + desactivateall() + File.pathSeparator
						+ "excludevariableplaceholder" + File.pathSeparator + "false" //
						+ File.pathSeparator + "maxmodificationpoints" + File.pathSeparator + nrmp + File.pathSeparator
						+ "considerzerovaluesusp" + File.pathSeparator + "true");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);

		List<ModificationPoint> mps = main.getEngine().getVariants().get(0).getModificationPoints();

		assertEquals(nrmp, mps.size());

		ModificationPoint mp0 = mps.get(0);

		assertTrue(mp0.getCodeElement() instanceof CtStatement);

		TOSInvocationGenerator invgenerator = new TOSInvocationGenerator();

		List<InvocationPlaceholder> invphd = (List<InvocationPlaceholder>) invgenerator
				.createTOS((CtStatement) mp0.getCodeElement());

		assertTrue(invphd.size() > 0);
		InvocationPlaceholder iv0 = invphd.get(0);
		System.out.println(iv0);

		TOSEntity tos = new TOSEntity();
		tos.setDerivedFrom(mp0.getCodeElement());
		tos.getPlaceholders().add(iv0);
		tos.generateCodeofTOS();
		System.out.println(tos.getCode());

		assertEquals("return _org.apache.commons.math.analysis.solvers.BisectionSolver_double_0_(min, max)",
				tos.getCode().toString());

		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientPool ingredientPool = approach.getIngredientPool();

		TOSIngredientPool tosIngredientPool = (TOSIngredientPool) ingredientPool;

		List<String> locations = tosIngredientPool.getLocations();
		assertTrue(locations.contains("org.apache.commons.math.analysis.solvers"));
		assertTrue(locations.contains("org.apache.commons.math.stat.descriptive.rank"));

		for (String loc : locations) {
			System.out.println("loc: " + loc);
		}

		int i = 0;

		List<Ingredient> allIngredientsSolver = tosIngredientPool
				.retrieveIngredients("org.apache.commons.math.analysis.solvers");
		for (Ingredient ingredient : allIngredientsSolver) {
			TOSEntity tosIngredient = (TOSEntity) ingredient;
			CtElement codeTOS = tosIngredient.generateCodeofTOS();
			System.out.println("\n-ic-solver-package->" + (i++) + "\ntos:\n" + codeTOS + "\nderived from:\n"
					+ tosIngredient.getDerivedFrom());
		}
		i = 0;
		List<Ingredient> allIngredientsRank = tosIngredientPool
				.retrieveIngredients("org.apache.commons.math.stat.descriptive.rank");
		for (Ingredient ingredient : allIngredientsRank) {
			TOSEntity tosIngredient = (TOSEntity) ingredient;
			CtElement codeTOS = tosIngredient.generateCodeofTOS();
			System.out.println("\n-ic-rank->" + (i++) + "\ntos:\n" + codeTOS + "\nderived from:\n"
					+ tosIngredient.getDerivedFrom());
		}

		TOSIngredientRandomSearchStrategy strategy = new TOSIngredientRandomSearchStrategy(tosIngredientPool);

		// -ic->1
		// tos:
		// _double_0 = _double_1
		// derived from:
		// value = d

		List<TOSInstance> instances1 = strategy.getInstances(mp0, allIngredientsRank.get(1));
		assertFalse(instances1.isEmpty());

		assertEquals("_double_0 = _double_1", allIngredientsRank.get(1).getChacheCodeString());

		// -ic->4
		// tos:
		// _double_0 = values[_int_1]
		// derived from:
		// min = values[begin]

		assertEquals("_double_0 = values[_int_1]", allIngredientsRank.get(4).getChacheCodeString());
		List<TOSInstance> instances4 = strategy.getInstances(mp0, allIngredientsRank.get(4));
		assertTrue(instances4.isEmpty());

		// -ic->5
		// tos:
		// min = _double[]_0[_int_1]
		// derived from:
		// min = values[begin]

		assertEquals("min = _double[]_0[_int_1]", allIngredientsRank.get(5).getChacheCodeString());

		List<TOSInstance> instances5 = strategy.getInstances(mp0, allIngredientsRank.get(5));
		assertTrue(instances5.isEmpty());

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testCheckPackageBug() throws Exception {
		int nrPlaceholders = 1;

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", TOSBRApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", LOG_LEVEL);
		command.command.put("-scope", "package");
		command.command.put("-stopfirst", "false");
		command.command.put("-flthreshold", "0.00");

		int nrmp = 30;
		command.command.put("-parameters",
				"nrPlaceholders:" + nrPlaceholders + File.pathSeparator + "duplicateingredientsinspace"
						+ File.pathSeparator + "true" + File.pathSeparator + desactivateall() + File.pathSeparator
						+ "excludevariableplaceholder" + File.pathSeparator + "false" //
						+ File.pathSeparator + "maxmodificationpoints" + File.pathSeparator + nrmp + File.pathSeparator
						+ "considerzerovaluesusp" + File.pathSeparator + "true");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);

		List<ModificationPoint> mps = main.getEngine().getVariants().get(0).getModificationPoints();

		assertEquals(nrmp, mps.size());

		ModificationPoint mp0 = mps.get(0);

		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientPool ingredientPool = approach.getIngredientPool();

		TOSIngredientPool tosIngredientPool = (TOSIngredientPool) ingredientPool;

		List<String> locations = tosIngredientPool.getLocations();

		List<Ingredient> allIng = tosIngredientPool.getAllIngredients();

		assertTrue(allIng.size() > 10000);

	}

	@Test
	public void testTOSsupport() throws Exception {
		int nrPlaceholders = 1;

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", TOSBRApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", LOG_LEVEL);
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "true");

		command.command.put("-parameters",
				"nrPlaceholders:" + nrPlaceholders + File.pathSeparator + desactivateall() + File.pathSeparator
						+ "duplicateingredientsinspace" + File.pathSeparator + "true" + File.pathSeparator
						+ "excludevariableplaceholder" + File.pathSeparator + "false");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);
		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientPool ingredientPool = approach.getIngredientPool();
		TOSIngredientPool tosIngredientPool = (TOSIngredientPool) ingredientPool;

		TOSIngredientRandomSearchStrategy strategy = new TOSIngredientRandomSearchStrategy(tosIngredientPool);

		assertNotEquals(AstorOutputStatus.ERROR, approach.getOutputStatus());

		List<ModificationPoint> mps = approach.getVariants().get(0).getModificationPoints();
		String packageName = "org.apache.commons.math.analysis.solvers.BisectionSolver";

		List<Ingredient> ingredientsLocalBi = tosIngredientPool.retrieveIngredients(packageName);

		assertTrue(ingredientsLocalBi.size() > 0);
		assertEquals(tosIngredientPool.getTosCounter().getTosOcurrenceCounter().keySet().size(),
				ingredientsLocalBi.size());
		for (String k : tosIngredientPool.getTosCounter().getTosOcurrenceCounter().keySet()) {
			System.out.println(k + "\nocurrences: " + tosIngredientPool.getTosCounter().getTosOcurrenceCounter().get(k)
					+ "\n------ ");

		}

		// ***We list the ingrediets of package
		// "org.apache.commons.math.analysis.solvers"
		for (int i = 0; i < ingredientsLocalBi.size(); i++) {
			Ingredient ingi = ingredientsLocalBi.get(i);
			String cacheCodeString = ingi.getChacheCodeString();
			System.out.println("i " + i + " " + cacheCodeString);

			Integer ocurrences = tosIngredientPool.getTosCounter().getTosOcurrenceCounter().get(cacheCodeString);
			assertNotNull(ocurrences);
			assertTrue(ocurrences > 0);
			System.out.println("ocurrences " + ocurrences);
			// tosIngredientPool.support(tos, minsupport)
		}

		Ingredient in1 = findIngredient(ingredientsLocalBi, "return solve(f, _double_0, max)");
		Integer ocurrences = tosIngredientPool.getTosCounter().getTosOcurrenceCounter().get(in1.getChacheCodeString());
		assertEquals(2, ocurrences.intValue());
		assertTrue(tosIngredientPool.support((TOSEntity) in1, 2));

		Ingredient in2 = findIngredient(ingredientsLocalBi, "return _double_0");
		Integer ocurrences2 = tosIngredientPool.getTosCounter().getTosOcurrenceCounter().get(in2.getChacheCodeString());
		assertEquals(1, ocurrences2.intValue());
		assertFalse(tosIngredientPool.support((TOSEntity) in2, 2));

		Ingredient in3 = findIngredient(ingredientsLocalBi, "return solve(_UnivariateRealFunction_0, min, max)");
		Integer ocurrences3 = tosIngredientPool.getTosCounter().getTosOcurrenceCounter().get(in3.getChacheCodeString());
		assertEquals(2, ocurrences3.intValue());
		assertTrue(tosIngredientPool.support((TOSEntity) in3, 2));

	}

	public Ingredient findIngredient(List<? extends Ingredient> ingredients, String code) {
		Optional<? extends Ingredient> findFirst = ingredients.stream()
				.filter(e -> e.getChacheCodeString().equals(code)).findFirst();
		if (!findFirst.isPresent())
			return null;
		return findFirst.get();
	}

	public Ingredient findInstance(List<TOSInstance> ingredients, String code) {
		String codese = code.replace(" ", "").replace("\n","");
		Optional<TOSInstance> findFirst = ingredients.stream().filter(e -> e.getChacheCodeString().replace(" ", "").replace("\n","").equals(codese))
				.findFirst();
		if (!findFirst.isPresent())
			return null;
		return findFirst.get();
	}

	@Test
	public void testLiterals1() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", TOSBRApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", LOG_LEVEL);
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "true");

		command.command.put("-parameters",
				"nrPlaceholders:" + 1 + File.pathSeparator + "duplicateingredientsinspace" + File.pathSeparator + "true"
						+ File.pathSeparator + desactivateall() + File.pathSeparator + "excludeliteralplaceholder"
						+ File.pathSeparator + "false");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);
		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientPool ingredientPool = approach.getIngredientPool();
		TOSIngredientPool tosIngredientPool = (TOSIngredientPool) ingredientPool;
		// Local
		LiteralsSpace lspace = new LiteralsSpace(IngredientPoolScope.LOCAL);
		lspace.defineSpace(main.getEngine().getVariants().get(0));
		List<String> locations = lspace.getLocations();
		System.out.println("locations " + locations);
		assertEquals(1, locations.size());
		String loc1 = "org.apache.commons.math.analysis.solvers.BisectionSolver";
		List<Ingredient> ingredients = lspace.retrieveIngredients(loc1);
		System.out.println("ingredient " + ingredients);
		assertTrue(ingredients.size() > 0);
		assertEquals(4, ingredients.size());

		//
		LiteralsSpace lspaceP = new LiteralsSpace(IngredientPoolScope.PACKAGE);
		lspaceP.defineSpace(main.getEngine().getVariants().get(0));
		List<String> locationsP = lspaceP.getLocations();
		System.out.println("locationsP " + locationsP);
		assertEquals(1, locationsP.size());
		String locP1 = "org.apache.commons.math.analysis.solvers";
		List<Ingredient> ingredientsPackage = lspaceP.retrieveIngredients(locP1);
		System.out.println("ingredientP " + ingredientsPackage);
		assertTrue(ingredientsPackage.size() > 0);
		assertEquals(34, ingredientsPackage.size());
		for (Ingredient literalPackage : ingredientsPackage) {
			System.out.println("--> " + literalPackage + " " + ((CtLiteral) literalPackage.getCode()).getType());
		}

	}

	@Test
	public void testTOStransformationLiteral() throws Exception {
		int nrPlaceholders = 1;

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", TOSBRApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", "debug"/* LOG_LEVEL */);
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "true");

		command.command.put("-parameters", "nrPlaceholders:" + nrPlaceholders + File.pathSeparator + desactivateall()
				+ File.pathSeparator + "excludeliteralplaceholder" + File.pathSeparator + "false");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);
		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientPool ingredientPool = approach.getIngredientPool();
		TOSIngredientPool tosIngredientPool = (TOSIngredientPool) ingredientPool;

		assertNotEquals(AstorOutputStatus.ERROR, approach.getOutputStatus());

		List<ModificationPoint> mps = approach.getVariants().get(0).getModificationPoints();

		ModificationPoint mp0 = mps.get(0);
		assertEquals("return solve(min, max)", mp0.getCodeElement().toString());

		ModificationPoint mp4 = mps.get(4);
		assertEquals("int i = 0", mp4.getCodeElement().toString());

		LiteralPlaceholderGenerator lg = new LiteralPlaceholderGenerator();
		List<LiteralPlaceholder> literals = lg.createTOS((CtStatement) mp4.getCodeElement());
		assertNotNull(literals);
		assertEquals(1, literals.size());
		for (LiteralPlaceholder literalPlaceholder : literals) {
			System.out.println("-lit->" + literalPlaceholder.getAffected());
			// System.out.println("-lit->"+literalPlaceholder.getAffected());
		}

		String className = "org.apache.commons.math.analysis.solvers.BisectionSolver";

		List<Ingredient> ingredientsLocalBi = tosIngredientPool.retrieveIngredients(className);

		assertTrue(ingredientsLocalBi.size() > 0);
		for (Ingredient ingredient : ingredientsLocalBi) {
			TOSEntity te = (TOSEntity) ingredient;
			assertTrue(te.getPlaceholders().size() > 0);
			LiteralPlaceholder placeholder = (LiteralPlaceholder) te.getPlaceholders().stream()
					.filter(e -> e instanceof LiteralPlaceholder).findFirst().get();
			assertNotNull(placeholder);
			CtElement e = te.generateCodeofTOS();
			System.out.println(placeholder.getAffected());
			System.out.println("-->ing: " + te.getCode());
		}
		TOSEntity ingredientSuper = (TOSEntity) ingredientsLocalBi.get(0);
		assertEquals("super(f, \"_l_int_0\", 1.0E-6)", ingredientSuper.getChacheCodeString());
		assertEquals("int i = \"_l_int_0\"", ingredientsLocalBi.get(4).getChacheCodeString());

		PatchGenerator pv = new PatchGenerator();

		List<Transformation> transformations = pv.process(mp4,
				(LiteralPlaceholder) ingredientSuper.getPlaceholders().get(0));

		// two ints
		assertEquals(2, transformations.size());

		TOSIngredientRandomSearchStrategy strategy = new TOSIngredientRandomSearchStrategy(tosIngredientPool);

		List<TOSInstance> instances4super = strategy.getInstances(mp4, ingredientSuper);
		assertFalse(instances4super.isEmpty());
		for (TOSInstance tosInstance : instances4super) {
			tosInstance.generatePatch();
			System.out.println(tosInstance.getChacheCodeString());
		}

		assertEquals("super(f, 100, 1.0E-6)", instances4super.get(0).getChacheCodeString());
		assertEquals("super(f, 0, 1.0E-6)", instances4super.get(1).getChacheCodeString());

		List<TOSInstance> instances0 = strategy.getInstances(mp0, ingredientSuper);
		assertTrue(instances0.size() > 0);

		Ingredient ingredientWhile = ingredientsLocalBi.get(6);
		assertTrue(ingredientWhile.getChacheCodeString().startsWith("while (i <"));
		List<TOSInstance> instances5 = strategy.getInstances(mp0, ingredientWhile);
		assertTrue(instances5.isEmpty());
	}

	@Test
	public void testTOStransformationVarLit() throws Exception {
		int nrPlaceholders = 1;

		CommandSummary command = commandVarLit(nrPlaceholders);

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);
		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientPool ingredientPool = approach.getIngredientPool();
		TOSIngredientPool tosIngredientPool = (TOSIngredientPool) ingredientPool;

		assertNotEquals(AstorOutputStatus.ERROR, approach.getOutputStatus());

		List<ModificationPoint> mps = approach.getVariants().get(0).getModificationPoints();

		ModificationPoint mp0 = mps.get(0);
		assertEquals("return solve(min, max)", mp0.getCodeElement().toString());

		ModificationPoint mp4 = mps.get(4);
		assertEquals("int i = 0", mp4.getCodeElement().toString());

		LiteralPlaceholderGenerator lg = new LiteralPlaceholderGenerator();

		String className = "org.apache.commons.math.analysis.solvers.BisectionSolver";

		List<Ingredient> ingredientsLocalBi = tosIngredientPool.retrieveIngredients(className);

		assertTrue(ingredientsLocalBi.size() > 0);
		for (Ingredient ingredient : ingredientsLocalBi) {
			TOSEntity te = (TOSEntity) ingredient;
			assertTrue(te.getPlaceholders().size() > 0);
			VarLiPlaceholder placeholder = (VarLiPlaceholder) te.getPlaceholders().stream()
					.filter(e -> e instanceof VarLiPlaceholder).findFirst().get();
			assertNotNull(placeholder);
			CtElement e = te.generateCodeofTOS();
			System.out.println(placeholder.getAffectedVariable());
			System.out.println("-->ing: " + te.getCode());
		}

		PatchGenerator pv = new PatchGenerator();
		assertNotNull(findIngredient(ingredientsLocalBi, "super(_lit_UnivariateRealFunction_0, 100, 1.0E-6)"));
		assertNotNull(findIngredient(ingredientsLocalBi, "return solve(_lit_double_0, max)"));
		assertNotNull(findIngredient(ingredientsLocalBi, "return solve(min, _lit_double_0)"));
		Ingredient ingrAssing = findIngredient(ingredientsLocalBi, "min = _lit_double_0");
		assertNotNull(ingrAssing);
		assertNull(findIngredient(ingredientsLocalBi, "_lit_double_0 = m"));
		assertNull(findIngredient(ingredientsLocalBi, "int _lit_int_0 = 0"));
		assertNotNull(findIngredient(ingredientsLocalBi, "verifyInterval(_lit_double_0, max)"));
		TOSIngredientRandomSearchStrategy strategy = new TOSIngredientRandomSearchStrategy(tosIngredientPool);

		List<TOSInstance> instances4super = strategy.getInstances(mp4, ingrAssing);
		assertFalse(instances4super.isEmpty());
		for (TOSInstance tosInstance : instances4super) {
			tosInstance.generatePatch();
			System.out.println(tosInstance.getChacheCodeString());
		}

		assertNotNull(findInstance(instances4super, "min = 1.0E-6"));
		assertNotNull(findInstance(instances4super, "min = 0.0"));

	}

	private CommandSummary commandVarLit(int nrPlaceholders) {
		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", TOSBRApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", "debug"/* LOG_LEVEL */);
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "true");

		command.command.put("-parameters",
				"nrPlaceholders:" + nrPlaceholders + File.pathSeparator + "duplicateingredientsinspace"
						+ File.pathSeparator + "false" + File.pathSeparator + desactivateall() + File.pathSeparator
						+ "excludevarliteralplaceholder" + File.pathSeparator + "false");
		return command;
	}

	@Test
	public void testTOStransformationVarLitPackage() throws Exception {
		int nrPlaceholders = 1;

		CommandSummary command = commandVarLit(nrPlaceholders);
		command.command.put("-scope", "package");
		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);
		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientPool ingredientPool = approach.getIngredientPool();
		TOSIngredientPool tosIngredientPool = (TOSIngredientPool) ingredientPool;

		assertNotEquals(AstorOutputStatus.ERROR, approach.getOutputStatus());

		String packageName = "org.apache.commons.math.analysis.solvers";

		List<Ingredient> ingredientsLocalBi = tosIngredientPool.retrieveIngredients(packageName);

		assertTrue(ingredientsLocalBi.size() > 0);
		int i = 0;
		for (Ingredient ingredient : ingredientsLocalBi) {
			TOSEntity te = (TOSEntity) ingredient;
			assertTrue(te.getPlaceholders().size() > 0);
			VarLiPlaceholder placeholder = (VarLiPlaceholder) te.getPlaceholders().stream()
					.filter(e -> e instanceof VarLiPlaceholder).findFirst().get();
			assertNotNull(placeholder);
			CtElement e = te.generateCodeofTOS();
			System.out.println(placeholder.getAffectedVariable());
			System.out.println("-->ing " + (i++) + ": " + StringUtil.trunc(te.getCode()));
		}
		Ingredient ingSolve3arg = findIngredient(ingredientsLocalBi, "return solve(f, min, max, _lit_double_0)");
		assertNotNull(ingSolve3arg);

		Ingredient ingDecl1 = findIngredient(ingredientsLocalBi, "double yMin = f.value(_lit_double_0)");
		assertNotNull(ingDecl1);

		Ingredient ingSolve5args = findIngredient(ingredientsLocalBi,
				"return solve(f, min, _lit_double_0, initial, yInitial, min, yMin)");
		assertNotNull(ingSolve5args);

		List<ModificationPoint> mps = approach.getVariants().get(0).getModificationPoints();

		ModificationPoint mp0 = mps.get(0);
		assertEquals("return solve(min, max)", mp0.getCodeElement().toString());

		ModificationPoint mp4 = mps.get(4);
		assertEquals("int i = 0", mp4.getCodeElement().toString());

		TOSIngredientRandomSearchStrategy strategy = new TOSIngredientRandomSearchStrategy(tosIngredientPool);

		List<TOSInstance> instancesSolve3args = strategy.getInstances(mp0, ingSolve3arg);
		assertFalse(instancesSolve3args.isEmpty());
		for (TOSInstance tosInstance : instancesSolve3args) {
			tosInstance.generatePatch();
			System.out.println(tosInstance.getChacheCodeString());
		}

		assertNotNull(findInstance(instancesSolve3args, "return solve(f, min, max, 0.0)"));
		assertNotNull(findInstance(instancesSolve3args, "return solve(f, min, max, 1.0E-15)"));
		// assertNotNull(findInstance(instancesSolve3args, "return solve(f, min,
		// max, 0.95)"));//bug of spaces

		//
		List<TOSInstance> instancesSolve5args = strategy.getInstances(mp0, ingSolve5args);
		assertTrue(instancesSolve5args.isEmpty());

		List<TOSInstance> instancesDecl1 = strategy.getInstances(mp0, ingDecl1);
		assertFalse(instancesDecl1.isEmpty());

		for (TOSInstance tosInstance : instancesDecl1) {
			tosInstance.generatePatch();
			System.out.println(tosInstance.getChacheCodeString());
		}

		assertNotNull(findInstance(instancesDecl1, "double yMin = f.value(1.0E-6)"));
		// assertNotNull(findInstance(instancesDecl1, "double yMin =
		// f.value(2.0)"));//bug of spaces
	}

	@Test
	public void testNotCm() throws Exception {
		int nrPlaceholders = 1;
		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", TOSBRApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", "debug"/* LOG_LEVEL */);
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "true");

		command.command.put("-parameters", "nrPlaceholders:" + nrPlaceholders
		// + File.pathSeparator + desactivateall()
		// + File.pathSeparator + "excludeliteralplaceholder" +
		// File.pathSeparator + "false"
				+ File.pathSeparator + "toscombinated" + File.pathSeparator + "false");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);
		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientPool ingredientPool = approach.getIngredientPool();
		TOSIngredientPool tosIngredientPool = (TOSIngredientPool) ingredientPool;

		// assertNotEquals(AstorOutputStatus.ERROR, approach.getOutputStatus());

		List<ModificationPoint> mps = approach.getVariants().get(0).getModificationPoints();

		ModificationPoint mp0 = mps.get(0);
		assertEquals("return solve(min, max)", mp0.getCodeElement().toString());

		ModificationPoint mp4 = mps.get(4);
		assertEquals("int i = 0", mp4.getCodeElement().toString());

		TOSIngredientRandomSearchStrategy strategy = new TOSIngredientRandomSearchStrategy(tosIngredientPool);

		String packageName = "org.apache.commons.math.analysis.solvers.BisectionSolver";

		List<Ingredient> ingredientsLocalBi = tosIngredientPool.retrieveIngredients(packageName);

		assertTrue(ingredientsLocalBi.size() > 0);
		int i = 0;
		for (Ingredient ingredient : ingredientsLocalBi) {
			TOSEntity te = (TOSEntity) ingredient;
			assertTrue(te.getPlaceholders().size() > 0);
			assertEquals(1, te.getNrPlaceholder());
			CtElement e = te.generateCodeofTOS();
			// System.out.println(placeholder.getAffectedVariable());
			System.out.println("-->ing " + (i++) + ": " + StringUtil.trunc(te.getCode()) + te.getPlaceholders());
		}
		assertNotNull(findIngredient(ingredientsLocalBi, "verifyInterval(min, _lit_double_0)"));
		assertNotNull(findIngredient(ingredientsLocalBi, "verifyInterval(min, _double_0)"));
		assertNotNull(findIngredient(ingredientsLocalBi, "verifyInterval(_double_0, max)"));
		assertNotNull(findIngredient(ingredientsLocalBi, "verifyInterval(_lit_double_0, max)"));

		assertNotNull(findIngredient(ingredientsLocalBi, "return solve(_double_0, max)"));
		assertNotNull(findIngredient(ingredientsLocalBi, "return solve(min, _double_0)"));
		assertNotNull(findIngredient(ingredientsLocalBi,
				"return _org.apache.commons.math.analysis.solvers.BisectionSolver_double_0_(min, max)"));
		assertNotNull(findIngredient(ingredientsLocalBi, "return solve(_lit_double_0, max)"));
		assertNotNull(findIngredient(ingredientsLocalBi, "return solve(min, _lit_double_0)"));
		assertNotNull(findIngredient(ingredientsLocalBi, "super(f, \"_l_int_0\", 1.0E-6)"));
		assertNotNull(findIngredient(ingredientsLocalBi, "return solve(min, _lit_double_0)"));

	}

	@Test
	public void testCm() throws Exception {
		int nrPlaceholders = 1;
		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", TOSBRApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", "info"/* LOG_LEVEL */);
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "true");
		command.command.put("-flthreshold", "0.001");
		command.command.put("-parameters", "nrPlaceholders:" + nrPlaceholders

				+ File.pathSeparator + "toscombinated" + File.pathSeparator + "true");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);
		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientPool ingredientPool = approach.getIngredientPool();
		TOSIngredientPool tosIngredientPool = (TOSIngredientPool) ingredientPool;

		List<ModificationPoint> mps = approach.getVariants().get(0).getModificationPoints();

		ModificationPoint mp0 = mps.get(0);
		assertEquals("return solve(min, max)", mp0.getCodeElement().toString());

		ModificationPoint mp4 = mps.get(4);
		assertEquals("int i = 0", mp4.getCodeElement().toString());

		TOSIngredientRandomSearchStrategy strategy = new TOSIngredientRandomSearchStrategy(tosIngredientPool);

		String packageName = "org.apache.commons.math.analysis.solvers.BisectionSolver";

		List<Ingredient> ingredientsLocalBi = tosIngredientPool.retrieveIngredients(packageName);
		assertTrue(ingredientsLocalBi.size() > 0);

		int i = 0;
		for (Ingredient ingredient : ingredientsLocalBi) {
			TOSEntity te = (TOSEntity) ingredient;
			assertTrue(te.getPlaceholders().size() > 0);
			CtElement e = te.generateCodeofTOS();
		}

		i = 0;
		List<TOSEntity> elsolve = tosIngredientPool.createAllTOSCombined((CtStatement) mp0.getCodeElement());

		assertNotNull(findIngredient(elsolve, "return solve(_double_0, max)"));
		assertNotNull(findIngredient(elsolve, "return solve(min, _double_0)"));
		assertNotNull(findIngredient(elsolve,
				"return _org.apache.commons.math.analysis.solvers.BisectionSolver_double_0_(min, max)"));
		assertNotNull(findIngredient(elsolve,
				"return _org.apache.commons.math.analysis.solvers.BisectionSolver_double_0_(_double_0, max)"));
		Ingredient inginvparam = findIngredient(elsolve,
				"return _org.apache.commons.math.analysis.solvers.BisectionSolver_double_0_(min, _double_0)");
		assertNotNull(inginvparam);
		assertNotNull(findIngredient(elsolve, "return solve(_lit_double_0, max)"));
		assertNotNull(findIngredient(elsolve, "return solve(_lit_double_0, _double_0)"));
		assertNotNull(findIngredient(elsolve,
				"return _org.apache.commons.math.analysis.solvers.BisectionSolver_double_0_(_lit_double_0, max)"));
		assertNotNull(findIngredient(elsolve,
				"return _org.apache.commons.math.analysis.solvers.BisectionSolver_double_0_(_lit_double_0, _double_0)"));
		assertNotNull(findIngredient(elsolve, "return solve(min, _lit_double_0)"));
		assertNotNull(findIngredient(elsolve, "return solve(_double_0, _lit_double_0)"));
		Ingredient ing2phvar = findIngredient(elsolve,
				"return _org.apache.commons.math.analysis.solvers.BisectionSolver_double_0_(min, _lit_double_0)");
		assertNotNull(ing2phvar);
		Ingredient allph1 = findIngredient(elsolve,
				"return _org.apache.commons.math.analysis.solvers.BisectionSolver_double_0_(_double_0, _lit_double_0)");
		assertNotNull(allph1);
		////

		System.out.println("all ing2phvar");
		List<TOSInstance> instancesph2 = strategy.getInstances(mp4, ing2phvar);
		assertEquals(2, instancesph2.size());

		for (TOSInstance tosInstance : instancesph2) {
			tosInstance.generatePatch();
			// System.out.println("-ph2-> " +
			// tosInstance.getChacheCodeString());
		}
		assertTrue(instancesph2.size() > 0);
		assertNotNull(findInstance(instancesph2, "return solve(min, 1.0E-6)"));
		assertNotNull(findInstance(instancesph2, "return solve(min, 0.0)"));

		////////
		i = 0;
		List<TOSEntity> elassing = tosIngredientPool.createAllTOSCombined((CtStatement) mp4.getCodeElement());

		assertEquals(1, elassing.size());
		assertNotNull(findIngredient(elassing, "int i = \"_l_int_0\""));

		/// Let's focus on modif point return

		ModificationPoint mp8 = mps.get(8);
		assertEquals("return (a + b) * 0.5", mp8.getCodeElement().toString());
		i = 0;
		List<TOSEntity> elret = tosIngredientPool.createAllTOSCombined((CtStatement) mp8.getCodeElement());
		for (TOSEntity tosEntity : elret) {
			System.out.println("--> tosret " + i++ + ": " + StringUtil.trunc(tosEntity.getCode()) + "   | "
					+ tosEntity.getPlaceholders());
		}

		// We assert the presence of tos in the pool.
		Ingredient ingRetvar = findIngredient(elret, "return (_double_0 + b) * 0.5");
		assertNotNull(ingRetvar);
		assertNotNull(findIngredient(elret, "return (a + _double_0) * 0.5"));
		Ingredient ingLit1 = findIngredient(elret, "return (a + b) * \"_l_double_0\"");
		assertNotNull(ingLit1);
		assertNotNull(findIngredient(elret, "return (_double_0 + b) * \"_l_double_0\""));
		assertNotNull(findIngredient(elret, "return (a + _double_0) * \"_l_double_0\""));
		assertNotNull(findIngredient(elret, "return (_lit_double_0 + b) * 0.5"));
		assertNotNull(findIngredient(elret, "return (_lit_double_0 + _double_0) * 0.5"));
		assertNotNull(findIngredient(elret, "return (_lit_double_0 + b) * \"_l_double_0\""));
		assertNotNull(findIngredient(elret, "return (_lit_double_0 + _double_0) * \"_l_double_0\""));
		Ingredient ingLitVar1 = findIngredient(elret, "return (a + _lit_double_0) * 0.5");
		assertNotNull(ingLitVar1);
		assertNotNull(findIngredient(elret, "return (_double_0 + _lit_double_0) * 0.5"));
		Ingredient inglitvar2 = findIngredient(elret, "return (a + _lit_double_0) * \"_l_double_0\"");
		assertNotNull(inglitvar2);
		assertNotNull(findIngredient(elret, "return (_double_0 + _lit_double_0) * \"_l_double_0\""));

		///// Assertion of different cases.
		assertVarChange1(strategy, mp8, ingRetvar);

		assertChangeLit2(strategy, mp8, ingLit1);

		assertVarToLi1(strategy, mp8, ingLitVar1);

		assertVarToLi2(strategy, mp8, inglitvar2);
	}

	private void assertVarChange1(TOSIngredientRandomSearchStrategy strategy, ModificationPoint mp8,
			Ingredient ingRetvar) {
		List<TOSInstance> instancesReturn1 = strategy.getInstances(mp8, ingRetvar);
		for (TOSInstance tosInstancer : instancesReturn1) {
			tosInstancer.generatePatch();
		}
		assertTrue(instancesReturn1.size() > 0);
		assertNotNull(findInstance(instancesReturn1, "return (b + b) * 0.5"));
		assertNotNull(findInstance(instancesReturn1, "return (a + b) * 0.5"));
	}

	private void assertChangeLit2(TOSIngredientRandomSearchStrategy strategy, ModificationPoint mp8,
			Ingredient ingLit1) {
		List<TOSInstance> instancesLit1 = strategy.getInstances(mp8, ingLit1);
		for (TOSInstance tosInstancel : instancesLit1) {
			System.out.println(tosInstancel.generatePatch());
		}
		assertNotNull(findInstance(instancesLit1, "return (a + b) * 0.0"));
		assertNotNull(findInstance(instancesLit1, "return (a + b) * 1.0"));
		assertNotNull(findInstance(instancesLit1, "return (a + b) * 0.5"));
		assertNull(findInstance(instancesLit1, "return (a + b) * 1E-6"));

		assertTrue(instancesLit1.size() > 0);
	}

	private void assertVarToLi1(TOSIngredientRandomSearchStrategy strategy, ModificationPoint mp8,
			Ingredient ingLitVar1) {
		List<TOSInstance> instancesVarLit1 = strategy.getInstances(mp8, ingLitVar1);
		for (TOSInstance tosInstancev : instancesVarLit1) {
			System.out.println(tosInstancev.generatePatch());
		}
		assertNotNull(findInstance(instancesVarLit1, "return (a + 0.0) * 0.5"));
		assertNotNull(findInstance(instancesVarLit1, "return (a + 1.0) * 0.5"));
		assertNotNull(findInstance(instancesVarLit1, "return (a + 0.5) * 0.5"));
	}

	private void assertVarToLi2(TOSIngredientRandomSearchStrategy strategy, ModificationPoint mp8,
			Ingredient inglitvar2) {
		List<TOSInstance> instancesVarLit2 = strategy.getInstances(mp8, inglitvar2);
		for (TOSInstance tosInstance2 : instancesVarLit2) {
			System.out.println(tosInstance2.generatePatch());
		}
		assertNotNull(findInstance(instancesVarLit2, "return (a + 0.0) * 0.0"));
		assertNotNull(findInstance(instancesVarLit2, "return (a + 0.0) * 1.0"));
		assertNotNull(findInstance(instancesVarLit2, "return (a + 0.0) * 0.5"));
		assertNotNull(findInstance(instancesVarLit2, "return (a + 1.0) * 0.0"));
		assertNotNull(findInstance(instancesVarLit2, "return (a + 1.0) * 1.0"));
		assertNotNull(findInstance(instancesVarLit2, "return (a + 1.0) * 0.5"));
		assertNotNull(findInstance(instancesVarLit2, "return (a + 0.5) * 0.0"));
		assertNotNull(findInstance(instancesVarLit2, "return (a + 0.5) * 1.0"));
		assertNotNull(findInstance(instancesVarLit2, "return (a + 0.5) * 0.5"));
	}

}
