package fr.inria.astor.test.repair.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.ingredientbased.IngredientBasedApproach;
import fr.inria.astor.core.manipulation.synthesis.IngredientSynthesizer;
import fr.inria.astor.core.manipulation.synthesis.SynthesisBasedTransformationStrategy;
import fr.inria.astor.core.manipulation.synthesis.dynamoth.DynamothCollector;
import fr.inria.astor.core.manipulation.synthesis.dynamoth.DynamothCollectorFacade;
import fr.inria.astor.core.manipulation.synthesis.dynamoth.DynamothIngredientSynthesizer;
import fr.inria.astor.core.manipulation.synthesis.dynamoth.DynamothSynthesisContext;
import fr.inria.astor.core.manipulation.synthesis.dynamoth.DynamothSynthesizer;
import fr.inria.astor.core.manipulation.synthesis.dynamoth.DynamothSynthesizerWOracle;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.IngredientTransformationStrategy;
import fr.inria.astor.test.repair.DummySynthesizer4TestImpl;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.astor.util.MapList;
import fr.inria.lille.repair.common.Candidates;
import fr.inria.lille.repair.expression.Expression;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import fr.inria.main.evolution.ExtensionPoints;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class SynthesisComponentTest {
	protected static Logger log = Logger.getLogger(Thread.currentThread().getName());

	@Test
	public void testValueCollection1() throws Exception {
		AstorMain main1 = new AstorMain();
		CommandSummary cs = MathCommandsTests.getMath70Command();

		cs.command.put("-flthreshold", "0.1");
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "DEBUG");
		cs.command.put("-saveall", "true");
		cs.command.put("-maxgen", "0");
		cs.append("-parameters", ("logtestexecution:true:disablelog:true"));

		log.info(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		assertEquals(1, main1.getEngine().getVariants().size());
		ProgramVariant variant = main1.getEngine().getVariants().get(0);

		DynamothCollectorFacade sc = new DynamothCollectorFacade();

		log.info("***First mp to test: ");
		SuspiciousModificationPoint mp0 = (SuspiciousModificationPoint) variant.getModificationPoints().get(0);
		assertEquals("org.apache.commons.math.analysis.solvers.BisectionSolver", mp0.getCtClass().getQualifiedName());
		assertEquals(72, mp0.getSuspicious().getLineNumber());

		valuesOfModificationPoint(main1, sc, mp0);

		log.info("***Second mp to test: ");

		SuspiciousModificationPoint mp8 = (SuspiciousModificationPoint) variant.getModificationPoints().get(8);
		assertEquals("org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils",
				mp8.getCtClass().getQualifiedName());
		assertEquals(223, mp8.getSuspicious().getLineNumber());
		valuesOfModificationPoint(main1, sc, mp8);

	}

	@Test
	public void testSynthesis1_boolean() throws Exception {

		AstorMain main1 = new AstorMain();
		CommandSummary cs = MathCommandsTests.getMath70Command();

		cs.command.put("-flthreshold", "0.0");
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "DEBUG");
		cs.command.put("-saveall", "true");
		cs.command.put("-maxgen", "0");
		cs.append("-parameters", ("logtestexecution:true:disablelog:true"));

		log.info(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		assertEquals(1, main1.getEngine().getVariants().size());
		ProgramVariant variant = main1.getEngine().getVariants().get(0);

		DynamothCollectorFacade sc = new DynamothCollectorFacade();

		SuspiciousModificationPoint mp8 = (SuspiciousModificationPoint) variant.getModificationPoints().get(0);

		DynamothCollector dynamothCodeGenesis = sc.createCollector(main1.getEngine().getProjectFacade(), mp8);
		DynamothSynthesizer synthesis = new DynamothSynthesizer(dynamothCodeGenesis.getValues(),
				dynamothCodeGenesis.getNopolContext(), dynamothCodeGenesis.getOracle());

		Candidates candidates = synthesis.combineValues();
		printValuesCollected(dynamothCodeGenesis);

		// TODO: BinaryExpressionImpl modified at line 91
		System.out.println("Candidates: " + candidates.size() + " " + candidates);

		assertTrue(candidates.size() > 0);
		for (int i = 0; i < candidates.size(); i++) {
			Expression expr = candidates.get(i);
			System.out.println("i " + i + ": " + expr + " value: " + expr.getValue());
			// By default, the angelic value is a boolean True
			assertTrue("Wrong value: " + expr.getValue().getRealValue(),
					Boolean.TRUE.equals(expr.getValue().getRealValue()));
		}
		assertTrue(candidates.stream().filter(e -> e.toString().equals("!this.resultComputed")).findAny().isPresent());
		assertTrue(candidates.stream().filter(e -> e.toString().equals("this.f == null")).findAny().isPresent());
		assertTrue(candidates.stream().filter(e -> e.toString().equals("min != max")).findAny().isPresent());
		assertTrue(candidates.stream().filter(e -> e.toString().equals("!(1 < 0)")).findAny().isPresent());
		assertTrue(candidates.stream().filter(e -> e.toString().equals("!(this.absoluteAccuracy < this.functionValue)"))
				.findAny().isPresent());
		assertTrue(candidates.stream().filter(e -> e.toString().equals("!(this.result < 0)")).findAny().isPresent());
	}

	@Test
	public void testSynthesis2_Int() throws Exception {

		AstorMain main1 = new AstorMain();
		CommandSummary cs = MathCommandsTests.getMath70Command();

		cs.command.put("-flthreshold", "0.0");
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "DEBUG");
		cs.command.put("-saveall", "true");
		cs.command.put("-maxgen", "0");
		cs.append("-parameters", ("logtestexecution:true:disablelog:true"));

		log.info(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		assertEquals(1, main1.getEngine().getVariants().size());
		ProgramVariant variant = main1.getEngine().getVariants().get(0);

		DynamothCollectorFacade sc = new DynamothCollectorFacade();

		SuspiciousModificationPoint mp8 = (SuspiciousModificationPoint) variant.getModificationPoints().get(0);

		String[] tests = sc.getCoverTest(mp8);
		Map<String, Object[]> oracle = new HashMap<>();

		for (String testCase : tests) {
			oracle.put(testCase, new Integer[] { 0 });
		}
		DynamothCollector dynamothCodeGenesis = sc.createCollector(main1.getEngine().getProjectFacade(), mp8, oracle,
				tests);
		DynamothSynthesizer synthesis = new DynamothSynthesizer(dynamothCodeGenesis.getValues(),
				dynamothCodeGenesis.getNopolContext(), dynamothCodeGenesis.getOracle());

		Candidates candidates = synthesis.combineValues();
		printValuesCollected(dynamothCodeGenesis);

		// TODO: BinaryExpressionImpl modified at line 91
		System.out.println("Candidates: " + candidates.size() + " " + candidates);

		assertTrue(candidates.size() > 0);
		for (int i = 0; i < candidates.size(); i++) {
			System.out.println("i " + i + ": " + candidates.get(i));
		}
		assertTrue(candidates.stream().filter(e -> e.toString().equals("this.iterationCount")).findAny().isPresent());
		assertTrue(candidates.stream().filter(e -> e.toString().equals("0")).findAny().isPresent());
		assertTrue(candidates.stream()
				.filter(e -> e.toString()
						.equals("(1 - this.defaultMaximalIterationCount) + (this.defaultMaximalIterationCount - 1)"))
				.findAny().isPresent());
		assertTrue(candidates.stream()
				.filter(e -> e.toString().equals("this.defaultMaximalIterationCount - this.maximalIterationCount"))
				.findAny().isPresent());
		assertTrue(candidates.stream()
				.filter(e -> e.toString().equals("this.maximalIterationCount - this.defaultMaximalIterationCount"))
				.findAny().isPresent());

	}

	@Test
	public void testSynthesis2_double_1() throws Exception {

		AstorMain main1 = new AstorMain();
		CommandSummary cs = MathCommandsTests.getMath70Command();

		cs.command.put("-flthreshold", "0.0");
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "DEBUG");
		cs.command.put("-saveall", "true");
		cs.command.put("-maxgen", "0");
		cs.append("-parameters", ("logtestexecution:true:disablelog:true"));

		log.info(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		assertEquals(1, main1.getEngine().getVariants().size());
		ProgramVariant variant = main1.getEngine().getVariants().get(0);

		DynamothCollectorFacade sc = new DynamothCollectorFacade();

		SuspiciousModificationPoint mp8 = (SuspiciousModificationPoint) variant.getModificationPoints().get(0);

		String[] tests = sc.getCoverTest(mp8);
		Map<String, Object[]> oracle = new HashMap<>();

		Double angelicValue = 0.0;
		for (String testCase : tests) {
			oracle.put(testCase, new Double[] { angelicValue });
		}
		DynamothCollector dynamothCodeGenesis = sc.createCollector(main1.getEngine().getProjectFacade(), mp8, oracle,
				tests);
		DynamothSynthesizer synthesis = new DynamothSynthesizer(dynamothCodeGenesis.getValues(),
				dynamothCodeGenesis.getNopolContext(), dynamothCodeGenesis.getOracle());

		Candidates candidates = synthesis.combineValues();
		printValuesCollected(dynamothCodeGenesis);

		// TODO: BinaryExpressionImpl modified at line 91
		System.out.println("Candidates: " + candidates.size() + " " + candidates);

		assertTrue(candidates.size() > 0);
		for (int i = 0; i < candidates.size(); i++) {
			Expression expr = candidates.get(i);
			System.out.println("i " + i + ": " + expr + " eval: " + expr.getValue());
			assertTrue(angelicValue.equals(expr.getValue().getRealValue()));
		}
		assertTrue(candidates.stream().filter(e -> e.toString().equals("this.result")).findAny().isPresent());
		assertTrue(candidates.stream().filter(e -> e.toString().equals("this.functionValue")).findAny().isPresent());

	}

	@Test
	public void testSynthesis2_double_3() throws Exception {

		AstorMain main1 = new AstorMain();
		CommandSummary cs = MathCommandsTests.getMath70Command();

		cs.command.put("-flthreshold", "0.0");
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "DEBUG");
		cs.command.put("-saveall", "true");
		cs.command.put("-maxgen", "0");
		cs.append("-parameters", ("logtestexecution:true:disablelog:true"));

		log.info(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		assertEquals(1, main1.getEngine().getVariants().size());
		ProgramVariant variant = main1.getEngine().getVariants().get(0);

		DynamothCollectorFacade sc = new DynamothCollectorFacade();

		SuspiciousModificationPoint mp8 = (SuspiciousModificationPoint) variant.getModificationPoints().get(0);

		String[] tests = sc.getCoverTest(mp8);
		Map<String, Object[]> oracle = new HashMap<>();

		Double angelicValue = 4.0;
		for (String testCase : tests) {
			oracle.put(testCase, new Double[] { angelicValue });
		}
		DynamothCollector dynamothCodeGenesis = sc.createCollector(main1.getEngine().getProjectFacade(), mp8, oracle,
				tests);
		DynamothSynthesizer synthesis = new DynamothSynthesizer(dynamothCodeGenesis.getValues(),
				dynamothCodeGenesis.getNopolContext(), dynamothCodeGenesis.getOracle());

		Candidates candidates = synthesis.combineValues();
		printValuesCollected(dynamothCodeGenesis);

		// TODO: BinaryExpressionImpl modified at line 91
		System.out.println("Candidates: " + candidates.size() + " " + candidates);

		assertEquals(16, candidates.size());
		for (int i = 0; i < candidates.size(); i++) {
			Expression expr = candidates.get(i);
			System.out.println("i " + i + ": " + expr + " eval: " + expr.getValue());
			assertTrue(angelicValue.equals(expr.getValue().getRealValue()));
		}
		assertTrue(candidates.stream().filter(e -> e.toString().equals("min + 1")).findAny().isPresent());
		assertTrue(candidates.stream().filter(e -> e.toString().equals("(min + this.functionValue) + 1")).findAny()
				.isPresent());
		assertTrue(candidates.stream().filter(e -> e.toString().equals("min + (1 - 0)")).findAny().isPresent());
	}

	@Test
	@Ignore
	public void testSynthesisCollectWithEmptyOracle() throws Exception {

		AstorMain main1 = new AstorMain();
		CommandSummary cs = MathCommandsTests.getMath70Command();

		cs.command.put("-flthreshold", "0.0");
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "DEBUG");
		cs.command.put("-saveall", "true");
		cs.command.put("-maxgen", "0");
		cs.append("-parameters", ("logtestexecution:true:disablelog:true"));

		log.info(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		assertEquals(1, main1.getEngine().getVariants().size());
		ProgramVariant variant = main1.getEngine().getVariants().get(0);

		DynamothCollectorFacade sc = new DynamothCollectorFacade();

		SuspiciousModificationPoint mp8 = (SuspiciousModificationPoint) variant.getModificationPoints().get(0);

		String[] tests = sc.getCoverTest(mp8);
		Map<String, Object[]> oracle = new HashMap<>();

		DynamothCollector dynamothCodeGenesis = sc.createCollector(main1.getEngine().getProjectFacade(), mp8, oracle,
				tests);

		Double angelicValue62 = 6.2;
		oracle.clear();
		for (String testCase : tests) {
			oracle.put(testCase, new Double[] { angelicValue62 });
		}
	}

	@Test
	public void testSynthesis2_double_2() throws Exception {

		AstorMain main1 = new AstorMain();
		CommandSummary cs = MathCommandsTests.getMath70Command();

		cs.command.put("-flthreshold", "0.0");
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "DEBUG");
		cs.command.put("-saveall", "true");
		cs.command.put("-maxgen", "0");
		cs.append("-parameters", ("logtestexecution:true:disablelog:true"));

		log.info(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		assertEquals(1, main1.getEngine().getVariants().size());
		ProgramVariant variant = main1.getEngine().getVariants().get(0);

		DynamothCollectorFacade sc = new DynamothCollectorFacade();

		SuspiciousModificationPoint mp8 = (SuspiciousModificationPoint) variant.getModificationPoints().get(0);

		String[] tests = sc.getCoverTest(mp8);
		Map<String, Object[]> oracle = new HashMap<>();

		Double angelicValue62 = 6.2;
		oracle.clear();
		for (String testCase : tests) {
			oracle.put(testCase, new Double[] { angelicValue62 });
		}

		DynamothCollector dynamothCodeGenesis = sc.createCollector(main1.getEngine().getProjectFacade(), mp8, oracle,
				tests);

		DynamothSynthesizer synthesis2 = new DynamothSynthesizer(dynamothCodeGenesis.getValues(),
				dynamothCodeGenesis.getNopolContext(), oracle);
		Candidates candidates62 = synthesis2.combineValues();
		assertTrue(candidates62.size() > 0);
		for (int i = 0; i < candidates62.size(); i++) {
			Expression expr = candidates62.get(i);
			System.out.println("i " + i + ": " + expr + " eval: " + expr.getValue());
			// assertTrue(angelicValue62.equals(expr.getValue().getRealValue()));
		}
		for (int i = 0; i < candidates62.size(); i++) {
			Expression expr = candidates62.get(i);
			// System.out.println("i " + i + ": " + expr + " eval: " +
			// expr.getValue());
			assertTrue(angelicValue62.equals(expr.getValue().getRealValue()));
		}
		assertTrue(candidates62.stream().filter(e -> e.toString().equals("min + max")).findAny().isPresent());
		// assertTrue(candidates62.stream().filter(e ->
		// e.toString().equals("this.functionValue + (min + max)")).findAny()
		// .isPresent());
		assertTrue(candidates62.stream().filter(e -> e.toString().equals("initial + initial")).findAny().isPresent());

	}

	private DynamothSynthesisContext valuesOfModificationPoint(AstorMain main1, DynamothCollectorFacade sc,
			SuspiciousModificationPoint mp0) {

		log.info("-mp-> " + mp0.getCodeElement());

		DynamothCollector dynamothCodeGenesis = sc.createCollector(main1.getEngine().getProjectFacade(), mp0);

		Map<String, List<Candidates>> values = printValuesCollected(dynamothCodeGenesis);
		return new DynamothSynthesisContext(values);
	}

	private Map<String, List<Candidates>> printValuesCollected(DynamothCollector dynamothCodeGenesis) {
		Map<String, List<Candidates>> values = dynamothCodeGenesis.getValues();
		assertTrue(!values.isEmpty());
		int nrtest = 0;
		for (String key : values.keySet()) {
			log.info("test " + nrtest++ + " :" + key);
			List<Candidates> candidates1 = values.get(key);
			log.info("nr candidates 1: " + candidates1.size());
			int i = 0;
			for (Candidates candidates2 : candidates1) {
				log.info("--Nr of candidates of " + (i++) + ": " + candidates2.size());
				int j = 0;
				for (fr.inria.lille.repair.expression.Expression expression : candidates2) {

					log.info("--*-->" + i + " " + (j++) + " " + expression.asPatch() + " " + expression.getValue());

				}
			}

		}
		return values;
	}

	@Test
	public void testExtensionPointSynthesis() throws Exception {
		AstorMain main1 = new AstorMain();
		CommandSummary cs = MathCommandsTests.getMath70Command();

		cs.command.put("-flthreshold", "0.1");
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "DEBUG");
		cs.command.put("-saveall", "true");
		cs.command.put("-maxgen", "0");
		cs.append("-parameters",
				"logtestexecution:true:disablelog:true:"//
						+ ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier + File.pathSeparator
						+ SynthesisBasedTransformationStrategy.class.getCanonicalName() + File.pathSeparator + //
						ExtensionPoints.CODE_SYNTHESIS.identifier + File.pathSeparator
						+ DummySynthesizer4TestImpl.class.getCanonicalName() + File.pathSeparator
						+ ExtensionPoints.CONTEXT_COLLECTOR.identifier + File.pathSeparator
						+ DynamothCollectorFacade.class.getCanonicalName());

		log.info(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		assertEquals(1, main1.getEngine().getVariants().size());
		ProgramVariant variant = main1.getEngine().getVariants().get(0);

		IngredientTransformationStrategy transfst = ((IngredientBasedApproach) main1.getEngine())
				.getIngredientTransformationStrategy();

		assertNotNull(transfst);
		assertTrue(transfst instanceof SynthesisBasedTransformationStrategy);
		SynthesisBasedTransformationStrategy synthesizerStrategy = (SynthesisBasedTransformationStrategy) transfst;
		IngredientSynthesizer synthesizer = synthesizerStrategy.getSynthesizer();
		assertNotNull(synthesizer);
		assertTrue(synthesizer instanceof DummySynthesizer4TestImpl);

		SuspiciousModificationPoint mp0 = (SuspiciousModificationPoint) variant.getModificationPoints().get(0);

		List<Ingredient> ingredients = synthesizerStrategy.transform(mp0, new Ingredient(mp0.getCodeElement()));
		assertTrue(ingredients.isEmpty());
	}

	@Test
	public void testExtensionPoint_real() throws Exception {
		AstorMain main1 = new AstorMain();
		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-mode", "cardumen");
		cs.command.put("-flthreshold", "0.1");
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "DEBUG");
		cs.command.put("-saveall", "true");
		cs.command.put("-maxgen", "0");
		cs.append("-parameters",
				"logtestexecution:true:disablelog:true:probabilistictransformation:false:"//
						+ ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier + File.pathSeparator
						+ SynthesisBasedTransformationStrategy.class.getCanonicalName() + File.pathSeparator + //
						ExtensionPoints.CODE_SYNTHESIS.identifier + File.pathSeparator
						+ DynamothIngredientSynthesizer.class.getCanonicalName()//
						+ File.pathSeparator + ExtensionPoints.CONTEXT_COLLECTOR.identifier + File.pathSeparator
						+ DynamothCollectorFacade.class.getCanonicalName());

		log.info(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		assertEquals(1, main1.getEngine().getVariants().size());
		ProgramVariant variant = main1.getEngine().getVariants().get(0);

		IngredientTransformationStrategy transfst = ((IngredientBasedApproach) main1.getEngine())
				.getIngredientTransformationStrategy();

		assertNotNull(transfst);
		assertTrue(transfst instanceof SynthesisBasedTransformationStrategy);
		SynthesisBasedTransformationStrategy synthesizerStrategy = (SynthesisBasedTransformationStrategy) transfst;
		IngredientSynthesizer synthesizer = synthesizerStrategy.getSynthesizer();
		assertNotNull(synthesizer);
		assertTrue(synthesizer instanceof DynamothIngredientSynthesizer);

		SuspiciousModificationPoint mp0 = (SuspiciousModificationPoint) variant.getModificationPoints().get(0);

		List<Ingredient> ingredients = synthesizerStrategy.transform(mp0, new Ingredient(mp0.getCodeElement()));
		assertTrue(ingredients.size() > 0);
		log.info("Ingredients retrieved: " + ingredients);
		for (Ingredient ingredient : ingredients) {
			CtElement elementIng = ingredient.getCode();
			assertNotNull(elementIng);
			assertFalse(elementIng.toString().isEmpty());
			log.info("--> " + elementIng.toString());
		}
	}

	@Test
	public void testSynthesisWOOrale() throws Exception {
		AstorMain main1 = new AstorMain();
		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-mode", "cardumen");
		cs.command.put("-flthreshold", "0.1");
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "DEBUG");
		cs.command.put("-saveall", "true");
		cs.command.put("-maxgen", "0");
		cs.append("-parameters",
				"logtestexecution:true:disablelog:true:probabilistictransformation:false:max_synthesis_step:12000:"//
						+ ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier + File.pathSeparator
						+ SynthesisBasedTransformationStrategy.class.getCanonicalName() + File.pathSeparator + //
						ExtensionPoints.CODE_SYNTHESIS.identifier + File.pathSeparator
						+ DynamothIngredientSynthesizer.class.getCanonicalName()//
						+ File.pathSeparator + ExtensionPoints.CONTEXT_COLLECTOR.identifier + File.pathSeparator
						+ DynamothCollectorFacade.class.getCanonicalName());

		log.info(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		assertEquals(1, main1.getEngine().getVariants().size());
		ProgramVariant variant = main1.getEngine().getVariants().get(0);

		DynamothCollectorFacade sc = new DynamothCollectorFacade();

		SuspiciousModificationPoint mp8 = (SuspiciousModificationPoint) variant.getModificationPoints().get(0);

		String[] tests = sc.getCoverTest(mp8);
		Map<String, Object[]> oracle = new HashMap<>();

		for (String testCase : tests) {
			oracle.put(testCase, new Integer[] { 0 });
		}

		DynamothCollector dynamothCodeGenesis = sc.createCollector(main1.getEngine().getProjectFacade(), mp8, oracle,
				tests);
		dynamothCodeGenesis.getNopolContext().setSynthesisDepth(3);
		assertTrue(dynamothCodeGenesis.getValues().size() > 0);
		DynamothSynthesisContext data = new DynamothSynthesisContext(dynamothCodeGenesis.getValues());
		data.setNopolContext(dynamothCodeGenesis.getNopolContext());
		DynamothSynthesizerWOracle soo = new DynamothSynthesizerWOracle(data);
		Candidates candidates = soo.combineValues();
		assertTrue(candidates.size() > 0);
		Set<Object> differentValues = new HashSet<>();
		MapList<Object, Expression> clusterValues = new MapList<>();
		for (int i = 0; i < candidates.size(); i++) {
			Expression expr = candidates.get(i);
			System.out.println("i " + i + ": " + expr + ", evaluation: " + expr.getValue());
			differentValues.add(expr.getValue().getRealValue());
			clusterValues.add(expr.getValue().getRealValue(), expr);
		}
		System.out.println("Total candidates: " + candidates.size());
		System.out.println("Values retrieved (size " + differentValues.size() + "): \n" + differentValues);
		System.out.println("Cluster (size " + clusterValues.keySet().size() + ") :\n " + clusterValues);

		assertTrue(candidates.stream().filter(e -> e.toString().equals("min + max")).findAny().isPresent());
		assertTrue(differentValues.size() > 170);
		assertTrue(candidates.stream().filter(e -> e.toString().equals("min == max")).findAny().isPresent());
		assertTrue(candidates.stream().filter(e -> e.toString().equals("1 == 0")).findAny().isPresent());
		assertTrue(
				candidates.stream().filter(e -> e.toString().equals("this.iterationCount == 0")).findAny().isPresent());
		assertTrue(candidates.stream().filter(e -> e.toString().equals("this.defaultFunctionValueAccuracy")).findAny()
				.isPresent());
		assertTrue(candidates.stream().filter(e -> e.toString().equals("max + this.defaultMaximalIterationCount"))
				.findAny().isPresent());
		assertTrue(candidates.stream().filter(e -> e.toString().equals("this.functionValue + this.result")).findAny()
				.isPresent());
		assertTrue(candidates.stream().filter(e -> e.toString().equals("this.defaultMaximalIterationCount + min"))
				.findAny().isPresent());
		assertTrue(candidates.stream().filter(e -> e.toString().equals("max * min")).findAny().isPresent());
		assertTrue(candidates.stream()
				.filter(e -> e.toString().equals("this.resultComputed && (this.defaultMaximalIterationCount != 1)"))
				.findAny().isPresent());

	}

}
