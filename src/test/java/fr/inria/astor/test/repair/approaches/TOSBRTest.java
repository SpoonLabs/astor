package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import fr.inria.astor.approaches.tos.core.TOSBRApproach;
import fr.inria.astor.approaches.tos.entity.TOSCounter;
import fr.inria.astor.approaches.tos.entity.TOSEntity;
import fr.inria.astor.approaches.tos.entity.TOSInstance;
import fr.inria.astor.approaches.tos.entity.placeholders.InvocationPlaceholder;
import fr.inria.astor.approaches.tos.entity.placeholders.VariablePlaceholder;
import fr.inria.astor.approaches.tos.ingredients.TOSBStatementIngredientSpace;
import fr.inria.astor.approaches.tos.ingredients.TOSIngredientSearchStrategy;
import fr.inria.astor.approaches.tos.ingredients.processors.TOSInvocationGenerator;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.manipulation.sourcecode.InvocationResolver;
import fr.inria.astor.core.manipulation.sourcecode.InvocationResolver.InvocationMatching;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.astor.util.CommandSummary;
import fr.inria.astor.util.MapCounter;
import fr.inria.astor.util.Probability;
import fr.inria.astor.util.StringUtil;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtAbstractInvocation;
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
						+ File.pathSeparator + "true" + File.pathSeparator + "excludeinvocationplaceholder"
						+ File.pathSeparator + "true");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);
		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientSpace ingredientPool = approach.getIngredientPool();
		TOSBStatementIngredientSpace tosIngredientPool = (TOSBStatementIngredientSpace) ingredientPool;

		TOSIngredientSearchStrategy strategy = new TOSIngredientSearchStrategy(tosIngredientPool);

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
		command.command.put("-maxgen", "100");
		command.command.put("-loglevel", LOG_LEVEL);
		command.command.put("-scope", "package");
		command.command.put("-stopfirst", "false");

		command.command.put("-parameters",
				"nrPlaceholders:" + nrPlaceholders + File.pathSeparator + "duplicateingredientsinspace"
						+ File.pathSeparator + "true" + File.pathSeparator + "excludeinvocationplaceholder"
						+ File.pathSeparator + "true");

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
		TOSInstance ing = (TOSInstance) opI0.getIngredient();
		assertNotNull(ing);

		CtElement fix = opI0.getModified();
		String patchCode = "return solve(f, min, max)";
		assertEquals(patchCode, fix.toString());

		assertEquals("return solve(_UnivariateRealFunction_0, min, max)", ing.getTosDerived().getCode().toString());

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
						+ File.pathSeparator + "true" + File.pathSeparator + "excludeinvocationplaceholder"
						+ File.pathSeparator + "true");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);
		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientSpace ingredientPool = approach.getIngredientPool();
		TOSBStatementIngredientSpace tosIngredientPool = (TOSBStatementIngredientSpace) ingredientPool;

		TOSIngredientSearchStrategy iss = new TOSIngredientSearchStrategy(tosIngredientPool);

		// assertNotEquals(AstorOutputStatus.ERROR, approach.getOutputStatus());

		// assertTrue(approach.getSolutions().size() > 0);

		List<Ingredient> allin = tosIngredientPool.getAllIngredients();

		// First patch
		ProgramVariant solution0 = approach.getVariants().get(0);

		for (ModificationPoint mp : solution0.getModificationPoints()) {

			System.out.println(":MP " + mp);
			for (Ingredient ingredient : allin) {
				System.out.println("-->Pattern: " + ingredient.getCode());
				List<TOSInstance> ingx = iss.getInstances(mp, ingredient);
				String last = null;
				for (TOSInstance tosIngredient : ingx) {
					tosIngredient.generatePatch();
					System.out.println("--> " + tosIngredient.getCode());
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
		command.command.put("-maxgen", "200");
		command.command.put("-loglevel", LOG_LEVEL);
		command.command.put("-scope", "package");
		command.command.put("-stopfirst", "false");

		command.command.put("-parameters",
				"nrPlaceholders:" + nrPlaceholders + File.pathSeparator + "duplicateingredientsinspace"
						+ File.pathSeparator + "true" + File.pathSeparator + "excludeinvocationplaceholder"
						+ File.pathSeparator + "true");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof TOSBRApproach);
		TOSBRApproach approach = (TOSBRApproach) main.getEngine();
		IngredientSpace ingredientPool = approach.getIngredientPool();
		TOSBStatementIngredientSpace tosIngredientPool = (TOSBStatementIngredientSpace) ingredientPool;

		assertNotEquals(AstorOutputStatus.ERROR, approach.getOutputStatus());

		List<ProgramVariant> solutions = approach.getSolutions();
		assertTrue(solutions.size() > 0);

		String patch0 = "return solve(f, absoluteAccuracy, max)";
		String baseIng0 = "return solve(_UnivariateRealFunction_0, _double_1, max)";
		assertPatch(solutions, patch0, baseIng0);

		String patch1 = "return solve(f, initial, max)";
		String baseIng1 = "return solve(_UnivariateRealFunction_0, _double_1, max)";
		assertPatch(solutions, patch1, baseIng1);

		String patch2 = "return solve(f, defaultRelativeAccuracy, max)";
		String baseIng2 = "return solve(_UnivariateRealFunction_0, _double_1, max)";
		assertPatch(solutions, patch2, baseIng2);

		String patch3 = "return solve(f, min, max)";
		String baseIng3 = "return solve(_UnivariateRealFunction_0, min, _double_1)";
		assertPatch(solutions, patch3, baseIng3);

		String patch4 = "return solve(f, functionValueAccuracy, max)";
		String baseIng4 = "return solve(_UnivariateRealFunction_0, _double_1, max)";
		assertPatch(solutions, patch4, baseIng4);

		String patch5 = "return solve(f, relativeAccuracy, max)";
		String baseIng5 = "return solve(_UnivariateRealFunction_0, _double_1, max)";
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
					assertEquals(baseIng, ing.getDerivedFrom().toString());
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

		command.command.put("-parameters",
				"nrPlaceholders:" + nrPlaceholders + File.pathSeparator + "duplicateingredientsinspace"
						+ File.pathSeparator + "true" + File.pathSeparator + "excludeinvocationplaceholder"
						+ File.pathSeparator + "true");

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

			TOSEntity TOSingredient = (TOSEntity) ingredient;
			// Forcing generation
			System.out.println("--> " + TOSingredient.generateCodeofTOS());

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
			List<CtVariableAccess> varAccessCollected = VariableResolver.collectVariableAccess(TOSingredient.getCode(),
					true);

			// Each variable not transformed must appears in the list of
			// collected variables
			for (CtVariableAccess notModified : vp.getVariablesNotModified()) {
				assertTrue(varAccessCollected.contains(notModified));
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

		command.command.put("-parameters",
				"nrPlaceholders:" + nrPlaceholders + File.pathSeparator + "duplicateingredientsinspace"
						+ File.pathSeparator + "true" + File.pathSeparator + "excludeinvocationplaceholder"
						+ File.pathSeparator + "true");

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
						+ File.pathSeparator + "true" + File.pathSeparator + "excludeinvocationplaceholder"
						+ File.pathSeparator + "false" + File.pathSeparator + "excludevariableplaceholder"
						+ File.pathSeparator + "true");

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
		IngredientSpace ingredientPool = approach.getIngredientPool();
		// one location i.e., the package
		assertEquals(1, ingredientPool.getLocations().size());
		assertEquals("org.apache.commons.math.analysis.solvers", ingredientPool.getLocations().get(0).toString());

		TOSBStatementIngredientSpace tosIngredientPool = (TOSBStatementIngredientSpace) ingredientPool;

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

		TOSIngredientSearchStrategy strategy = new TOSIngredientSearchStrategy(tosIngredientPool);

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

}
