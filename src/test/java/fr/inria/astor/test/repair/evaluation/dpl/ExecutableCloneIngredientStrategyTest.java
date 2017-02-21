package fr.inria.astor.test.repair.evaluation.dpl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Queue;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.CloneIngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.CtLocationIngredientSpace;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtType;

/**
 * Test for Clone ingredient strategy
 * 
 * @author Matias Martinez
 *
 */
public class ExecutableCloneIngredientStrategyTest {

	protected Logger log = Logger.getLogger(this.getClass().getName());

	@Test
	public void testInputProperties() throws ClassNotFoundException, IOException {

		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		assertTrue(learningDir.exists());

		assertTrue(learningDir.list().length > 0);

		ConfigurationProperties.properties.setProperty("learningdir", learningDir.getAbsolutePath());
		ConfigurationProperties.properties.setProperty("clonegranularity", CtType.class.getCanonicalName());
		IngredientSpace ingredientSpace = null;
		CloneIngredientSearchStrategy<?> cloneStrategy = new CloneIngredientSearchStrategy<>(ingredientSpace);

	}

	@Test
	public void testExecutableMath70() throws Exception {

		// Now, let's test at Executanble granularity

		Class executableCloneGranularity = CtExecutable.class;

		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		Class typeCloneGranularityClass = CtExecutable.class;
		String[] args = createCommandM70(learningDir, executableCloneGranularity);

		log.debug(Arrays.toString(args));

		AstorMain main1 = new AstorMain();
		main1.execute(args);
		JGenProg engine = (JGenProg) main1.getEngine();

		ProgramVariant pvariant = engine.getVariants().get(0);

		ModificationPoint buggyStatementModPoint = pvariant.getModificationPoints().get(0);

		assertEquals(72, buggyStatementModPoint.getCodeElement().getPosition().getEndLine());

		CloneIngredientSearchStrategy<CtExecutable> excloneStrategy = (CloneIngredientSearchStrategy<CtExecutable>) engine
				.getIngredientStrategy();

		AstorOperator op = new ReplaceOp();
		Ingredient exingredientSelected = excloneStrategy.getFixIngredient(buggyStatementModPoint, op);

		assertNotNull(exingredientSelected);

		CtExecutable<?> executParent = buggyStatementModPoint.getCodeElement().getParent(CtExecutable.class);

		Queue<CtCodeElement> execingredientsToConsider = excloneStrategy.getfixspace(buggyStatementModPoint, op,
				executParent);

		assertNotNull(execingredientsToConsider);

		assertFalse(execingredientsToConsider.isEmpty());

		log.debug(execingredientsToConsider);

	}

	static public String[] createCommandM70(File learningDir, Class cloneGranularityClass) {
		// HERE WE FORCE TO NOT EVOLVE
		return createCommandM70(learningDir, cloneGranularityClass, 0, false);
	}

	static public String[] createCommandM70(File learningDir, Class cloneGranularityClass, int generations,
			boolean transformIngredient) {
		return createCommandM70(learningDir, cloneGranularityClass, generations, transformIngredient,
				CtLocationIngredientSpace.class.getCanonicalName());
	}

	static public String[] createCommandM70(File learningDir, Class cloneGranularityClass, int generations,
			boolean transformIngredient, String scope) {
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		String[] args = new String[] { "-dependencies", dep, "-mode", "statement",
				//
				"-failing", "org.apache.commons.math.analysis.solvers.BisectionSolverTest",
				//
				"-location", new File("./examples/math_70").getAbsolutePath(),
				//
				"-package", "org.apache.commons", "-srcjavafolder",
				//
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes",
				//
				"-javacompliancelevel", "7", "-flthreshold", "0.1", "-out", out.getAbsolutePath(),
				//
				"-scope", scope,
				//
				"-seed", "10",

				"-maxgen", Integer.toString(generations),
				//
				"-population", "1",
				//
				"-stopfirst", "true",
				//
				"-maxtime", "100",
				//
				// Learning Arguments
				"-learningdir", learningDir.getAbsolutePath(),
				//
				"-clonegranularity", cloneGranularityClass.getCanonicalName(),
				//
				"-ingredientstrategy", CloneIngredientSearchStrategy.class.getName(),
				//
				"-transformingredient",
				//
				"-loglevel", Level.DEBUG.toString() };
		return args;
	}

	@Test
	public void testExecutableMath70TransformationStrategy() throws Exception {

		// Now, let's test at Executable granularity

		Class executableCloneGranularity = CtExecutable.class;

		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		Class typeCloneGranularityClass = CtExecutable.class;
		int generations = 100;
		boolean transformIngredient = true;
		String[] args = createCommandM70(learningDir, executableCloneGranularity, generations, transformIngredient);

		log.debug(Arrays.toString(args));

		AstorMain main1 = new AstorMain();
		main1.execute(args);
		JGenProg engine = (JGenProg) main1.getEngine();

		ProgramVariant pvariant = engine.getVariants().get(0);

	}

}
