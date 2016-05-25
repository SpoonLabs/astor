package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.approaches.jgenprog.jGenProgSpace;
import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.AstorCoreEngine;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.EfficientIngredientStrategy;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.test.repair.evaluation.other.FakeIngredientStrategy;
import fr.inria.astor.test.repair.evaluation.other.ShortestIngredientSearchStrategy;
import fr.inria.main.AbstractMain;
import fr.inria.main.evolution.AstorMain;

/**
 * Test of Ingredient Space
 * @author Matias Martinez
 *
 */
public class IngredientSpaceTest extends BaseEvolutionaryTest {

	
	File out = null;

	public IngredientSpaceTest() {
		out = new File(ConfigurationProperties.getProperty("workingDirectory"));
	}

	
	/**
	 * This test asserts the navigation of the ing search space using a modification point and a operation type
	 * The strategy used returns ingredients according to their length (in term of number of chars)
	 * @throws Exception
	 */
	@Test
	public void testShortestIngredientMath85() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				// We put 0 as max generation, so we force to not evolve the
				// population
				"-maxgen", "0", "-scope", "local", "-seed", "10", "-ingredientstrategy",
				ShortestIngredientSearchStrategy.class.getName() };
		
		main1.execute(args);
		JGenProg astor = (JGenProg) main1.getEngine();
		IngredientSearchStrategy ingStrategy = astor.getIngredientStrategy();
		//Let's take a modification point from the first variant. I take the element at 12, it's an assignement.
		ModificationPoint mpoint = astor.getVariants().get(0).getModificationPoints().get(12);
		Ingredient ingLast = ingStrategy.getFixIngredient(mpoint, null);
		Assert.assertNotNull(ingLast);
		boolean respectOrder = true;
		int countAllIng = 1;
		while(respectOrder){
			Ingredient ingN = ingStrategy.getFixIngredient(mpoint, null);
			if(ingN == null)
				break;
			respectOrder= ingLast.getCode().toString().length() <= ingN.getCode().toString().length();
			ingLast = ingN;
			countAllIng++;
		}
		Assert.assertTrue(respectOrder);
	}
	/**
	 * This test asserts the navigation of the ing search space using a modification point and a operation type
	 * For this reason, we assert that the ingredients returned by the strategy are of the same type than the modif point.
	 * @throws Exception
	 */
	@Test
	public void testShortestIngredientReplaceMath85() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				// We put 0 as max generation, so we force to not evolve the
				// population
				"-maxgen", "0", "-scope", "local", "-seed", "10", "-ingredientstrategy",
				ShortestIngredientSearchStrategy.class.getName() };
	
		main1.execute(args);
		JGenProg astor = (JGenProg) main1.getEngine();
		IngredientSearchStrategy ingStrategy = astor.getIngredientStrategy();
	
		
		//
		AstorOperator operator = new ReplaceOp(); 
		//Let's take a modification point from the first variant. I take the element at 12, it's an assignement.
		ModificationPoint mpoint = astor.getVariants().get(0).getModificationPoints().get(12);
		Ingredient ingLast = ingStrategy.getFixIngredient(mpoint, operator);
		Assert.assertNotNull(ingLast);
		boolean respectOrder = true;
		int countRepIngredient = 1;
		while(respectOrder){
			Ingredient ingN = ingStrategy.getFixIngredient(mpoint, operator);
			if(ingN == null)
				break;
			//System.out.println(mpoint.getCodeElement()+" vs "+ ingN.getCode());
			respectOrder= ingLast.getCode().toString().length() <= ingN.getCode().toString().length();
			ingLast = ingN;
			countRepIngredient++;
			Assert.assertEquals(mpoint.getCodeElement().getClass().getName(), ingN.getCode().getClass().getName());
		}
		Assert.assertTrue(respectOrder);
	
	}


	@Test
	public void testMath85_ParticularIngredientStrategy() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				"-maxgen", "100", "-scope", "package", "-seed", "10", "-ingredientstrategy",
				EfficientIngredientStrategy.class.getCanonicalName() };
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		validatePatchExistence(out + File.separator + "AstorMain-math_85/");
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);

	}
	@Test
	public void testMath85_AnyIngredientStrategy() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				"-maxgen", "100", "-scope", "package", "-seed", "10", "-ingredientstrategy",
				"DoneToFailIngredientStrat10" };
		System.out.println(Arrays.toString(args));
		try {
			main1.execute(args);
			fail();
		} catch (Exception e) {// Expected
		}
	}




	@SuppressWarnings("rawtypes")
	@Test
	// @Ignore
	public void testMath70WithFakeIngStrategy() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "package", "-seed", "10", "-maxgen", "50", "-ingredientstrategy",
				FakeIngredientStrategy.class.getCanonicalName() };
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.isEmpty());

	}
	
}
