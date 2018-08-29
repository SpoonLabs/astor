package fr.inria.astor.test.repair.approaches.cardumen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.approaches.cardumen.ExpressionReplaceOperator;
import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.filters.ExpressionBooleanIngredientSpaceProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.CodeParserLauncher;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPoolLocationType;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.ExpressionTypeIngredientSpace;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.InScopeVarsTransformation;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import fr.inria.main.evolution.ExtensionPoints;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
@Ignore
public class ExpressionBooleanOperatorTest {

	protected Logger log = Logger.getLogger(ExpressionBooleanOperatorTest.class.getName());

	// @Before
	public void setup() {
		Logger.getRootLogger().getLoggerRepository().resetConfiguration();
		ConsoleAppender console = new ConsoleAppender(); // create appender
		// configure the appender
		String PATTERN = "%m%n";
		console.setLayout(new PatternLayout(PATTERN));
		console.activateOptions();
		// add appender to any Logger (here is root)
		Logger.getRootLogger().addAppender(console);
	}

	@Test
	public void testM70BooleanExpressionAsModificationPoint() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-parameters",
				ExtensionPoints.TARGET_CODE_PROCESSOR.identifier + File.pathSeparator
						+ ExpressionBooleanIngredientSpaceProcessor.class.getCanonicalName() + File.pathSeparator
						+ "cleantemplates:false");
		command.command.put("-maxgen", "0");// Avoid evolution
		command.command.put("-flthreshold", "0.00");
		command.command.put("-package", "org.apache.commons.math.analysis.solvers");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		List<ProgramVariant> variantss = main1.getEngine().getVariants();
		assertTrue(variantss.size() > 0);

		JGenProg jgp = (JGenProg) main1.getEngine();
		IngredientPoolLocationType ingSpace = (IngredientPoolLocationType) jgp.getIngredientSearchStrategy()
				.getIngredientSpace();

		// Let's check the ingredient space
		List allingredients = ingSpace.getAllIngredients();
		for (Object ingredient : allingredients) {
			assertTrue(ingredient instanceof CtExpression);
			CtExpression expr = (CtExpression) ingredient;
			assertEquals(Boolean.class.getSimpleName(), expr.getType().box().getSimpleName());

		}

		ExpressionBooleanIngredientSpaceProcessor bexp = new ExpressionBooleanIngredientSpaceProcessor();

		CodeParserLauncher ip = new CodeParserLauncher<>(bexp);

		assertEquals("BisectionSolver", variantss.get(0).getAffectedClasses().get(0).getSimpleName());
		List ingredientRetrieved = ip.createFixSpace(variantss.get(0).getAffectedClasses().get(0));

		for (Object object : ingredientRetrieved) {
			log.debug("ing: " + object);
		}
		assertEquals(3, ingredientRetrieved.size());
		assertTrue(ingredientRetrieved.stream().filter(e -> e.toString().equals("i < (maximalIterationCount)"))
				.findFirst().isPresent());
		assertTrue(ingredientRetrieved.stream().filter(e -> e.toString().equals("(fm * fmin) > 0.0")).findFirst()
				.isPresent());
		assertTrue(ingredientRetrieved.stream()
				.filter(e -> e.toString().equals("(java.lang.Math.abs((max - min))) <= (absoluteAccuracy)")).findFirst()
				.isPresent());

		// Let's check the modification points

		assertTrue(variantss.get(0).getModificationPoints().size() > 1);

		int i = 0;
		for (ModificationPoint modpoint : variantss.get(0).getModificationPoints()) {
			log.debug("-mod-point-> " + (i++) + " " + modpoint.getCodeElement());
			assertTrue(modpoint.getCodeElement() instanceof CtExpression);
			assertEquals("boolean", ((CtExpression) modpoint.getCodeElement()).getType().getSimpleName());
		}

		Optional s1 = variantss.get(0).getModificationPoints().stream()
				.filter(e -> e.getCodeElement().toString().equals("i < (maximalIterationCount)")).findFirst();
		assertTrue(s1.isPresent());

		Optional s2 = variantss.get(0).getModificationPoints().stream()
				.filter(e -> e.getCodeElement().toString().equals("(fm * fmin) > 0.0")).findFirst();
		assertTrue(s2.isPresent());

		Optional s3 = variantss.get(0).getModificationPoints().stream().filter(
				e -> e.getCodeElement().toString().equals("(java.lang.Math.abs((max - min))) <= (absoluteAccuracy)"))
				.findFirst();
		assertTrue(s3.isPresent());

	}

	@Test
	// @Ignore
	public void testM70ExpressionLongExecution() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-parameters",
				ExtensionPoints.TARGET_CODE_PROCESSOR.identifier + File.pathSeparator
						+ ExpressionBooleanIngredientSpaceProcessor.class.getCanonicalName() + File.pathSeparator
						+ ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier + File.pathSeparator
						+ InScopeVarsTransformation.class.getCanonicalName() + File.pathSeparator
						+ "cleantemplates:false:disablelog:true");
		command.command.put(ExtensionPoints.REPAIR_OPERATORS.argument(), ExpressionReplaceOperator.class.getName());
		command.command.put("-scope", ExpressionTypeIngredientSpace.class.getName());
		command.command.put("-flthreshold", "0.1");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "10");
		command.command.put("-loglevel", "INFO");
		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

	}

	@Test
	public void testM70ExpressionNPEClone() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-parameters",
				ExtensionPoints.TARGET_CODE_PROCESSOR.identifier + File.pathSeparator
						+ ExpressionBooleanIngredientSpaceProcessor.class.getCanonicalName() + File.pathSeparator
						+ ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier + File.pathSeparator
						+ InScopeVarsTransformation.class.getCanonicalName() + File.pathSeparator + "cleantemplates"
						+ File.pathSeparator + "false");
		command.command.put(ExtensionPoints.REPAIR_OPERATORS.argument(), ExpressionReplaceOperator.class.getName());
		command.command.put("-scope", ExpressionTypeIngredientSpace.class.getName());
		command.command.put("-flthreshold", "0.1");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "100");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		JGenProg jgp = (JGenProg) main1.getEngine();

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) jgp
				.getIngredientSearchStrategy().getIngredientSpace();
		assertNotNull(ingredientSpace);
		int i = 0;
		List allIng = ingredientSpace.getAllIngredients();
		for (Object object : allIng) {
			// log.debug((i++) + object.toString());
		}
		CtElement cd = ingredientSpace.getAllIngredients().get(3027).getCode();
		CtElement mi = ingredientSpace.getAllIngredients().get(2962).getCode();

		CtElement cd2 = ingredientSpace.getAllIngredients().get(3033).getCode();
		System.out.println("before " + cd.getParent(CtBlock.class));
		System.out.println("before " + mi);
		cd.replace(cd2);

		System.out.println("after " + cd.getParent(CtBlock.class));

	}

	@Test
	public void testM70ExpressionFormat() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-parameters",
				ExtensionPoints.TARGET_CODE_PROCESSOR.identifier + File.pathSeparator
						+ ExpressionBooleanIngredientSpaceProcessor.class.getCanonicalName() + File.pathSeparator
						+ ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier + File.pathSeparator
						+ InScopeVarsTransformation.class.getCanonicalName() + File.pathSeparator + "cleantemplates"
						+ File.pathSeparator + "true");
		command.command.put(ExtensionPoints.REPAIR_OPERATORS.argument(), ExpressionReplaceOperator.class.getName());
		command.command.put("-scope", ExpressionTypeIngredientSpace.class.getName());
		command.command.put("-flthreshold", "0.1");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "0");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		JGenProg jgp = (JGenProg) main1.getEngine();

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) jgp
				.getIngredientSearchStrategy().getIngredientSpace();
		assertNotNull(ingredientSpace);

		List allIng = ingredientSpace.getAllIngredients();
		for (Object object : allIng) {
			log.debug(object);
		}
		assertEquals(14, allIng.size());

		for (ModificationPoint mp : jgp.getVariants().get(0).getModificationPoints()) {
			log.debug(mp.getCodeElement());
		}
		;

		assertEquals(allIng.size(), ingredientSpace.allElementsFromSpace.size());

		log.debug("\nLinked templates");
		int nrIng = 0;
		for (String keyTemplate : ingredientSpace.linkTemplateElements.keySet()) {
			log.debug("\nkey " + keyTemplate);
			List linkedIngredients = ingredientSpace.linkTemplateElements.get(keyTemplate);
			nrIng += linkedIngredients.size();
			log.debug("values: (" + linkedIngredients.size() + ") " + linkedIngredients);
		}

		assertEquals(19, nrIng);
	}

	@Test
	public void testM70CheckTypesOfKeys() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-parameters", ExtensionPoints.TARGET_CODE_PROCESSOR.identifier + File.pathSeparator
				+ ExpressionBooleanIngredientSpaceProcessor.class.getCanonicalName() + File.pathSeparator
				+ ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier + File.pathSeparator
				+ InScopeVarsTransformation.class.getCanonicalName() + File.pathSeparator + "cleantemplates:false");
		command.command.put(ExtensionPoints.REPAIR_OPERATORS.argument(), ExpressionReplaceOperator.class.getName());
		command.command.put("-scope", ExpressionTypeIngredientSpace.class.getName());
		command.command.put("-flthreshold", "0.01");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		JGenProg jgp = (JGenProg) main1.getEngine();

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) jgp
				.getIngredientSearchStrategy().getIngredientSpace();
		assertNotNull(ingredientSpace);

		for (ModificationPoint modificationPoint : jgp.getVariants().get(0).getModificationPoints()) {
			List<Ingredient> allIng = ingredientSpace.getIngredients(modificationPoint.getCodeElement());

			System.out.println("--> Mp " + modificationPoint);

			System.out.println(allIng.size());
			for (Ingredient code : allIng) {

				assertEquals(((CtExpression) modificationPoint.getCodeElement()).getType().box().getQualifiedName(),
						((CtExpression) code.getCode()).getType().box().getQualifiedName());

			}
		}

	}

	@Test
	public void testM70ExpressionProcessingTransform() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-parameters", ExtensionPoints.TARGET_CODE_PROCESSOR.identifier + File.pathSeparator
				+ ExpressionBooleanIngredientSpaceProcessor.class.getCanonicalName() + File.pathSeparator
				+ ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier + File.pathSeparator
				+ InScopeVarsTransformation.class.getCanonicalName() + File.pathSeparator + "cleantemplates:false");
		command.command.put(ExtensionPoints.REPAIR_OPERATORS.argument(), ExpressionReplaceOperator.class.getName());
		command.command.put("-scope", ExpressionTypeIngredientSpace.class.getName());
		command.command.put("-flthreshold", "0.1");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		JGenProg jgp = (JGenProg) main1.getEngine();

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) jgp
				.getIngredientSearchStrategy().getIngredientSpace();
		assertNotNull(ingredientSpace);

		List allIng = ingredientSpace.getAllIngredients();
		assertEquals(18, allIng.size());

		List clonedIng = new ArrayList(ingredientSpace.getAllIngredients());

		assertTrue(clonedIng.remove(jgp.getVariants().get(0).getModificationPoints().get(0).getCodeElement()));
		int sizeIngSpace = clonedIng.size();

		for (int generation = 1; generation <= sizeIngSpace; generation++) {
			log.debug("\n------generation " + generation);
			OperatorInstance newOperation = jgp
					.createOperatorInstanceForPoint(jgp.getVariants().get(0).getModificationPoints().get(0));
			assertNotNull("At i:" + generation, newOperation);

		}
		OperatorInstance noOperation = jgp
				.createOperatorInstanceForPoint(jgp.getVariants().get(0).getModificationPoints().get(0));
		assertNotNull(noOperation);

		for (Object object : allIng) {
			log.debug(object);
		}
		CtElement cte7 = (CtElement) allIng.get(7);
		assertEquals("((initial < lowerBound) || (initial > upperBound))", cte7.toString());

		CtElement cloned7 = cte7.clone();
		ingredientSpace.formatIngredient(cloned7);
		assertEquals("(_double_0 < _double_1) || (_double_0 > _double_2)", cloned7.toString());

		CtElement cte9 = (CtElement) allIng.get(9);
		assertEquals("((initial < lowerBound) || (initial > upperBound)) || (lowerBound >= upperBound)",
				cte9.toString());

		CtElement cloned9 = cte9.clone();
		ingredientSpace.formatIngredient(cloned9);
		assertEquals("((_double_0 < _double_1) || (_double_0 > _double_2)) || (_double_1 >= _double_2)",
				cloned9.toString());

	}

	@Test
	public void testM70ExpressionNotTransformedIngredients() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-parameters",
				ExtensionPoints.TARGET_CODE_PROCESSOR.identifier + File.pathSeparator
						+ ExpressionBooleanIngredientSpaceProcessor.class.getCanonicalName() + File.pathSeparator
						+ "cleantemplates:false");
		command.command.put(ExtensionPoints.REPAIR_OPERATORS.argument(), ExpressionReplaceOperator.class.getName());
		command.command.put("-scope", ExpressionTypeIngredientSpace.class.getName());
		command.command.put("-flthreshold", "0.1");
		command.command.put("-maxtime", "60");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		JGenProg jgp = (JGenProg) main1.getEngine();

		ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) jgp
				.getIngredientSearchStrategy().getIngredientSpace();
		assertNotNull(ingredientSpace);

		List allIng = ingredientSpace.getAllIngredients();
		assertEquals(18, allIng.size());

		List clonedIng = new ArrayList(ingredientSpace.getAllIngredients());

		assertEquals("i < (maximalIterationCount)",
				(jgp.getVariants().get(0).getModificationPoints().get(0).getCodeElement().toString()));

		assertTrue(clonedIng.remove(jgp.getVariants().get(0).getModificationPoints().get(0).getCodeElement()));
		int sizeIngSpace = clonedIng.size();

		String[] ingInScope = new String[] { "f == null", "(java.lang.Math.abs((max - min))) <= (absoluteAccuracy)",
				"(fm * fmin) > 0.0" };
		List ingredientScopeList = new ArrayList(Arrays.asList(ingInScope));

		for (int generation = 1; generation <= ingInScope.length; generation++) {
			log.debug("\n------generation " + generation);
			OperatorInstance newOperation = jgp
					.createOperatorInstanceForPoint(jgp.getVariants().get(0).getModificationPoints().get(0));
			assertNotNull("At i:" + generation, newOperation);
			assertTrue(ingredientScopeList.remove(newOperation.getModified().toString()));
			log.debug("\n------End generation " + generation);

		}

		assertTrue(ingredientScopeList.isEmpty());
		// Force asking to a new operation
		OperatorInstance noOperation = jgp
				.createOperatorInstanceForPoint(jgp.getVariants().get(0).getModificationPoints().get(0));
		assertTrue(noOperation == null);

	}

	@Test
	public void testMath20() throws Exception {

		String dep = new File("./examples/libs/commons-discovery-0.5.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		String[] args = new String[] { "-mode", "jgenprog", "-location",
				new File("./examples/math_20").getAbsolutePath(), "-dependencies", dep, "-out", out.getAbsolutePath(),
				"-failing", "org.apache.commons.math3.optimization.direct.CMAESOptimizerTest"//
				, "-package", "org.apache.commons", //
				"-javacompliancelevel", "5"//
				, "-maxgen", "100", "-seed", "10", "-maxtime", "120", "-stopfirst", "false", "-population", "1",
				"-srcjavafolder", "src/main/java/", "-srctestfolder", "src/test/java/", "-binjavafolder",
				"target/classes/", "-bintestfolder", "target/test-classes/", "-flthreshold", "0.1", "-loglevel",
				"DEBUG", "-scope", ExpressionTypeIngredientSpace.class.getCanonicalName(),
				ExtensionPoints.REPAIR_OPERATORS.argument(),
				"fr.inria.astor.approaches.jgenprog.operators.ExpressionReplaceOperator", "-parameters",
				ExtensionPoints.TARGET_CODE_PROCESSOR.identifier
						+ ":fr.inria.astor.core.manipulation.filters.ExpressionBooleanIngredientSpace:"
						+ "ingredienttransformstrategy:fr.inria.astor.core.loop.spaces.ingredients.transformations.InScopeVarsTransformation:"
						+ "cleantemplates:true", };
		AstorMain main1 = new AstorMain();
		main1.execute(args);

		JGenProg jgp = (JGenProg) main1.getEngine();
		assertTrue(jgp.getVariants().size() > 0);
		assertTrue(jgp.getVariants().get(0).getModificationPoints().size() > 0);

		// at solution in 54

	}

}
