package fr.inria.astor.test.repair.evaluation.extensionpoints.ingredients;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.approaches.cardumen.ExpressionReplaceOperator;
import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.ExpressionBooleanIngredientSpaceProcessor;
import fr.inria.astor.core.manipulation.filters.SpecialStatementFixSpaceProcessor;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.manipulation.sourcecode.VarCombinationForIngredient;
import fr.inria.astor.core.manipulation.sourcecode.VarMapping;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.ingredientSearch.ProbabilisticIngredientStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.DynamicIngredient;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.GramProcessor;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.NGrams;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.ProbabilisticTransformationStrategy;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.astor.util.MapCounter;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import fr.inria.main.evolution.ExtensionPoints;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;

/**
 * 
 * @author Matias Martinez
 *
 */
@Ignore
public class GramBasedRepairTest {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Before
	public void setUp() throws Exception {
	}

	@Test
	@Ignore
	public void testProbabilistic() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();

		command.command.put("-flthreshold", "0.1");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "0");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		// AbstractFixSpaceProcessor<CtExpression> e =
		TargetElementProcessor<?> ingredientProcessor = new SpecialStatementFixSpaceProcessor();// ExpressionBooleanIngredientSpace();

		GramProcessor pt = new GramProcessor(ingredientProcessor);
		List<CtType<?>> all = MutationSupporter.getFactory().Type().getAll();
		NGrams ng = pt.calculateGlobal(all);
		MapCounter mc1 = ng.ngrams[2];

		// System.out.println(mc1);
		mc1.printSort();

		// System.out.println("\n by class");
		Map<String, NGrams> ngByClass = pt.calculateByClass(all);
		mc1.printSort();

		// System.out.println(ngByClass);

		for (String key : ngByClass.keySet()) {
			System.out.println("key " + key + "\n " + ngByClass.get(key));
			// ngByClass.get(key).ngrams[1].printSort();
		}

		CtType type = main1.getEngine().getVariants().get(0).getAffectedClasses().get(0);

		System.out.println("\nclass to analyze : \n" + type.toString());
		MapCounter[] ngrams = ngByClass.get(type.getQualifiedName()).ngrams;

		for (int i = 0; i < ngrams.length; i++) {
			MapCounter mapCounter = ngrams[i];
			if (mapCounter != null) {
				System.out.println("\ngram :" + i + "\n");
				mapCounter.printSort();
			}
		}

	}

	@Test
	@Ignore
	public void testOneClassProbabilistic() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();

		command.command.put("-flthreshold", "0.1");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "0");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		TargetElementProcessor<?> elementProcessor = new SpecialStatementFixSpaceProcessor();

		Boolean mustCloneOriginalValue = ConfigurationProperties.getPropertyBool("duplicateingredientsinspace");
		// Forcing to duplicate
		ConfigurationProperties.setProperty("duplicateingredientsinspace", "true");

		GramProcessor pt = new GramProcessor(elementProcessor);

		// Creating grams for Bisection class
		CtType type = main1.getEngine().getVariants().get(0).getAffectedClasses().get(0);
		assertEquals(type.getSimpleName(), "BisectionSolver");

		// ============Create grams
		Map<String, NGrams> gramByClassBisectionSolver = new HashMap<>();

		NGrams ngramBiClass = pt.calculateGrams4Class(type);

		assertNotNull(ngramBiClass);

		assertNotNull(ngramBiClass.ngrams[1]);

		assertFalse(ngramBiClass.ngrams[1].keySet().isEmpty());

		gramByClassBisectionSolver.put(type.getQualifiedName(), ngramBiClass);

		// reset property clone
		ConfigurationProperties.setProperty("duplicateingredientsinspace", Boolean.toString(mustCloneOriginalValue));

		MapCounter[] ngrams = ngramBiClass.ngrams;

		// Let's check if it has n grams

		int counterAbs = (int) ngrams[3].get("absoluteAccuracy max min");
		assertEquals(1, counterAbs);

		int counterFm1 = (int) ngrams[1].get("fm");
		assertEquals(2, counterFm1);

		int counterFm2 = (int) ngrams[2].get("fm fmin");
		assertEquals(1, counterFm2);

		int counterFm2b = (int) ngrams[2].get("fm m");
		assertEquals(1, counterFm2b);

		JGenProg engine = (JGenProg) main1.getEngine();

		// END grams

		////
		// Now, variable resolver
		searchCtElements(engine);
		List<CtVariable> varContextClearResult = VariableResolver.searchVariablesInScope(clearResult);//

		assertEquals(16, varContextClearResult.size());

		// We try to insert C8 (a + b ...) in the place ofC3 (clearResult)
		// Get Mapping
		VarMapping vmapping = VariableResolver.mapVariablesFromContext(varContextClearResult, returnExpression);

		assertEquals(2, vmapping.getMappedVariables().keySet().size());

		assertEquals(0, vmapping.getNotMappedVariables().size());

		// Obtaining all combinations:
		List<Map<String, CtVariable>> allCombinations = VariableResolver
				.findAllVarMappingCombination(vmapping.getMappedVariables());

		assertTrue(allCombinations.size() > 0);

		MapCounter gramCounterSize1 = ngramBiClass.ngrams[1];

		Map<?, Double> gramProbabilitySize1 = gramCounterSize1.getProbabilies();

		double sum = gramProbabilitySize1.values().stream().mapToDouble(Double::doubleValue).sum();

		assertEquals(1, sum, 0.01);

		ModificationPoint mpointCleanResult = getModpoint(engine, "clearResult()");
		assertEquals("clearResult()", mpointCleanResult.getCodeElement().toString());

		ProbabilisticTransformationStrategy probTransf = new ProbabilisticTransformationStrategy(
				gramByClassBisectionSolver, null);
		probTransf.calculateGramsProbs();
		List<VarCombinationForIngredient> varsComb4Ingredients = probTransf.findAllVarMappingCombinationUsingProbab(
				vmapping.getMappedVariables(), mpointCleanResult, new Ingredient(returnExpression));
		assertTrue(varsComb4Ingredients.size() > 0);

		Double probability = varsComb4Ingredients.get(0).getProbality();
		assertTrue(probability > 0);

		for (VarCombinationForIngredient varCombinationForIngredient : varsComb4Ingredients) {
			logger.debug(varCombinationForIngredient);
			assertTrue(probability >= varCombinationForIngredient.getProbality());
			probability = varCombinationForIngredient.getProbality();
		}

		//
		System.out.println("byclass \n " + gramByClassBisectionSolver);
		//

		List<Ingredient> ingredients = probTransf.transform(mpointCleanResult, new Ingredient(returnExpression));

		assertTrue(ingredients.size() > 1);

		probability = 1d;// Max prob
		System.out.println("\n*****Final Ingredients:");
		for (Ingredient ingredient : ingredients) {
			DynamicIngredient ding = (DynamicIngredient) ingredient;
			CtElement elementTransformed = ingredient.getCode();
			Double probalityIng = ding.getCombination().getProbality();
			assertTrue(probability >= probalityIng);
			probability = probalityIng;
			logger.debug(String.format(" %s : p %.2f ", elementTransformed, probalityIng));
		}

		List<CtType> types = MutationSupporter.getFactory().Type().getAll().stream()
				.filter(e -> "ConvergingAlgorithmImpl".equals(e.getSimpleName())).collect(Collectors.toList());
		assertTrue(types.size() > 0);

	}

	@Test
	@Ignore
	public void testProbabilistiComplete() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-parameters", ExtensionPoints.TARGET_CODE_PROCESSOR.identifier + File.pathSeparator
				+ ExpressionBooleanIngredientSpaceProcessor.class.getCanonicalName() + File.pathSeparator
				//
				+ ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier + File.pathSeparator
				+ ProbabilisticTransformationStrategy.class.getCanonicalName()
				//
				+ File.pathSeparator + ExtensionPoints.INGREDIENT_SEARCH_STRATEGY.identifier + File.pathSeparator
				+ ProbabilisticIngredientStrategy.class.getCanonicalName()
				//
				+ File.pathSeparator + "cleantemplates:false:disablelog:false");
		command.command.put(ExtensionPoints.REPAIR_OPERATORS.argument(), ExpressionReplaceOperator.class.getName());
		command.command.put("-scope", "local");
		command.command.put("-flthreshold", "0.1");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "100");
		command.command.put("-loglevel", "DEBUG");
		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

	}

	@Test
	public void testProbabilistByStep() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();

		command.command.put("-mode", "cardumen");
		command.command.put("-scope", "local"/* ExpressionTypeIngredientSpace.class.getName() */);
		command.command.put("-flthreshold", "0.00");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "0");// Not evolution
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-package", "org.apache.commons.math.analysis.solvers");
		command.command.put("-population", "1");
		command.append("-parameters", "considerzerovaluesusp:true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		JGenProg engine = (JGenProg) main1.getEngine();

		assertTrue(engine.getVariants().get(0).getModificationPoints().size() > 1);

		List mps = new ArrayList<>();
		int i = 0;
		for (ModificationPoint modpoint : engine.getVariants().get(0).getModificationPoints()) {

			// Let's check the modification points
			System.out.println(
					"-mp " + (i++) + " " + modpoint.getCtClass().getQualifiedName() + " " + modpoint.getCodeElement());
			assertTrue(modpoint.getCodeElement() instanceof CtExpression);
			// assertEquals("boolean", ((CtExpression)
			// modpoint.getCodeElement()).getType().getSimpleName());
		}

		ProbabilisticIngredientStrategy ingstr = (ProbabilisticIngredientStrategy) engine.getIngredientSearchStrategy();

		assertNotNull(ingstr);
		// Let's test functionality from getFixIngredient

		// public Ingredient getFixIngredient(ModificationPoint
		// modificationPoint, AstorOperator operationType) {

		Ingredient baseIngredient = null;

		/// -----

		AstorOperator aoperator = engine.getOperatorSpace().getOperators().get(0);

		assertTrue(ExpressionReplaceOperator.class.isInstance(aoperator));

		ModificationPoint mpointBinCondition0 = engine.getVariants().get(0).getModificationPoints().get(4);

		assertEquals("i < (maximalIterationCount)", mpointBinCondition0.getCodeElement().toString());

		// ingstr.getNotUsedTransformedElement(mpointCase, aoperator,
		// baseIngredient);

		/// ------------

		// Grams
		ProbabilisticTransformationStrategy transformationStrategy = (ProbabilisticTransformationStrategy) engine
				.getIngredientTransformationStrategy();
		assertNotNull(transformationStrategy);

		// Calculate
		transformationStrategy.calculateGramsProbs();

		NGrams ngglobals = transformationStrategy.getNgramManager().getNgglobal();

		Map<String, NGrams> ngramsByClass = transformationStrategy.getNgramManager().getNgramsSplitted();

		assertTrue(Arrays.asList(ngglobals.ngrams).stream().filter(e -> e != null).findFirst().isPresent());
		assertTrue(ngramsByClass.keySet().size() > 0);

		// Ngrams of BisectionSolver

		NGrams nGramBisection = ngramsByClass.get(mpointBinCondition0.getCtClass().getQualifiedName());

		assertNotNull(nGramBisection);

		String query1 = "max min";

		MapCounter mapCounterSize2 = nGramBisection.ngrams[2];

		int occurencies = (int) mapCounterSize2.get(query1);

		assertEquals(7, occurencies);

		// Calculate separately:
		TargetElementProcessor<?> elementProcessor = new SpecialStatementFixSpaceProcessor();

		GramProcessor gramProc = new GramProcessor(elementProcessor);

		NGrams nGramBisection2 = gramProc.calculateGrams4Class(mpointBinCondition0.getCtClass());

		// System.out.println("--> "+nGramBisection2);
		assertTrue(Arrays.asList(nGramBisection2.ngrams).stream().filter(e -> e != null).findFirst().isPresent());

		int occurencies2 = (int) nGramBisection2.ngrams[2].get(query1);

		// System.out.println("n-grams 2 a "+mapCounter2);

		// System.out.println("n-grams 2 b"+nGramBisection2.ngrams[2]);
		assertEquals(7, occurencies2);

		// 3 gram from m = UnivariateRealSolverUtils.midpoint(min, max);
		String query3 = "m max min";
		MapCounter mapCounterSize3 = nGramBisection.ngrams[3];
		int ocurrescesQ3 = (int) mapCounterSize3.get(query3);

		assertEquals(2, ocurrescesQ3);

		int occurenciesSingle = (int) nGramBisection2.ngrams[1].get("i");
		assertEquals(2, occurenciesSingle);

		////
		// -mp 9 org.apache.commons.math.analysis.solvers.BrentSolver sign < 0
		ModificationPoint mp9 = engine.getVariants().get(0).getModificationPoints().get(21);
		assertEquals("sign < 0", mp9.getCodeElement().toString());

		// -mp 10 org.apache.commons.math.analysis.solvers.BrentSolver i <
		// (maximalIterationCount)
		ModificationPoint mp10 = engine.getVariants().get(0).getModificationPoints().get(24);
		assertEquals("i < (maximalIterationCount)", mp10.getCodeElement().toString());

		Ingredient baseIngredient9 = new Ingredient(mp9.getCodeElement());
		logger.debug(String.format("In %s putting %s", mpointBinCondition0.getCodeElement(), mp9.getCodeElement()));

		// Try to put an ingredient that match:
		Ingredient selectedIngredientFrom9 = ingstr.getNotUsedTransformedElement(mpointBinCondition0, aoperator,
				baseIngredient9);
		assertNotNull(selectedIngredientFrom9);
		// assertFalse(selectedIngredientFrom9.getDerivedFrom().equals(selectedIngredientFrom9.getCode()));

		// One variable out of scope (n)

		ModificationPoint mp116 = getModpoint(engine, "i < n");
		logger.debug(String.format("In %s putting %s", mpointBinCondition0.getCodeElement(), mp116.getCodeElement()));

		assertEquals("i < n", mp116.getCodeElement().toString());

		Ingredient selectedIngredientFrom116_1 = ingstr.getNotUsedTransformedElement(mpointBinCondition0, aoperator,
				new Ingredient(mp116.getCodeElement()));
		assertNotNull(selectedIngredientFrom116_1);

		// All Vars [protected int maximalIterationCount;, protected int
		// defaultMaximalIterationCount;, protected int iterationCount;, int i =
		// 0]

		String s1 = "i maximalIterationCount";
		String s2 = "defaultMaximalIterationCount i";
		String s3 = "i iterationCount";
		String s4 = "i i";

		int occs1 = (int) nGramBisection2.ngrams[2].get(s1);
		Integer occs2 = (Integer) nGramBisection2.ngrams[2].get(s2);
		Integer occs3 = (Integer) nGramBisection2.ngrams[2].get(s3);
		Integer occs4 = (Integer) nGramBisection2.ngrams[2].get(s4);

		System.out.println("count " + occs1 + " " + occs2 + " " + occs3 + " " + occs4 + " ");
		List<String> combinations = new ArrayList(Arrays.asList("i < (defaultMaximalIterationCount)",
				"i < (iterationCount)", "i < (maximalIterationCount)", "i < i"));

		assertTrue(combinations.remove(selectedIngredientFrom116_1.getCode().toString()));

		Ingredient selectedIngredientFrom116_2 = ingstr.getNotUsedTransformedElement(mpointBinCondition0, aoperator,
				new Ingredient(mp116.getCodeElement()));
		assertNotNull(selectedIngredientFrom116_2);
		logger.debug("--" + selectedIngredientFrom116_2);
		assertTrue(combinations.remove(selectedIngredientFrom116_2.getCode().toString()));

		Ingredient selectedIngredientFrom116_3 = ingstr.getNotUsedTransformedElement(mpointBinCondition0, aoperator,
				new Ingredient(mp116.getCodeElement()));
		assertNotNull(selectedIngredientFrom116_3);

		logger.debug("--" + selectedIngredientFrom116_3);
		assertTrue(combinations.remove(selectedIngredientFrom116_3.getCode().toString()));

		Ingredient selectedIngredientFrom116_4 = ingstr.getNotUsedTransformedElement(mpointBinCondition0, aoperator,
				new Ingredient(mp116.getCodeElement()));

		logger.debug("--" + selectedIngredientFrom116_4);

		assertTrue(selectedIngredientFrom116_4 == null);

		assertTrue(combinations.contains("i < (maximalIterationCount)"));
		assertTrue(combinations.size() == 1);

	}

	private ModificationPoint getModpoint(JGenProg engine, String content) {
		for (ModificationPoint mp : engine.getVariants().get(0).getModificationPoints()) {
			if (mp.getCodeElement().toString().equals(content))
				return mp;
		}
		return null;
	}

	private MapCounter[] print(MapCounter[] ngrams) {

		for (int i = 0; i < ngrams.length; i++) {
			MapCounter mapCounter = ngrams[i];
			if (mapCounter != null) {
				System.out.println("\ngram :" + i + "\n");
				mapCounter.printSort();
			}
		}
		return ngrams;
	}

	CtCodeElement clearResult = null;
	CtElement c3 = null;
	CtElement ingredientCtElementC7 = null;
	CtElement returnExpression = null;

	private void searchCtElements(JGenProg engine) {

		for (ModificationPoint mp : engine.getVariants().get(0).getModificationPoints()) {
			System.out.println("-->" + mp.getCodeElement() + " " + mp.getCtClass().getQualifiedName());
		}

		ProgramVariant pv = engine.getVariants().get(0);
		// C1:.BisectionSolver l: 66,
		clearResult = (CtCodeElement) pv.getModificationPoints().get(2).getCodeElement();

		assertEquals("clearResult()", clearResult.toString());

		returnExpression = pv.getModificationPoints().get(8).getCodeElement();
		assertEquals("return (a + b) * 0.5", returnExpression.toString());

	}

}
