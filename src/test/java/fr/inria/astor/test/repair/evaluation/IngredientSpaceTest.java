package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.EfficientIngredientStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.CtLocationIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.MethodBasicIngredientScope;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.test.repair.evaluation.other.FakeIngredientStrategy;
import fr.inria.astor.test.repair.evaluation.other.ShortestIngredientSearchStrategy;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

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
		while(respectOrder){
			Ingredient ingN = ingStrategy.getFixIngredient(mpoint, null);
			if(ingN == null)
				break;
			respectOrder= ingLast.getCode().toString().length() <= ingN.getCode().toString().length();
			ingLast = ingN;
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
		while(respectOrder){
			Ingredient ingN = ingStrategy.getFixIngredient(mpoint, operator);
			if(ingN == null)
				break;
			//System.out.println(mpoint.getCodeElement()+" vs "+ ingN.getCode());
			respectOrder= ingLast.getCode().toString().length() <= ingN.getCode().toString().length();
			ingLast = ingN;
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
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "true",
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
	
	@SuppressWarnings("rawtypes")
		
	
	@Test
	public void testMath85ScopeLocalSpace() throws Exception {

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
		
		List<String> files =ingStrategy.getIngredientSpace().getLocations();
		Assert.assertTrue(files.size() > 0);
		Assert.assertTrue(files.contains(mpoint.getProgramVariant().getAffectedClasses().get(0).getQualifiedName()));
	
		List<CtCodeElement> ingredients =  ingStrategy.getIngredientSpace().getIngredients(mpoint.getCodeElement());
		Assert.assertTrue(ingredients.size() > 0);
		Assert.assertTrue(hasIngredient(ingredients,ingLast));
		
		//Now, we check if all ingredients retrieved belongs affected classes
		for (CtCodeElement ctCodeElement : ingredients) {
			assertTrue(mpoint.getProgramVariant().getAffectedClasses().contains(ctCodeElement.getParent(CtType.class)));
		}
		
	}
	
	@Test
	public void testMath85ScopePackageSpace() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				// We put 0 as max generation, so we force to not evolve the
				// population
				"-maxgen", "0", "-scope", "package", "-seed", "10", "-ingredientstrategy",
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
		
		List<String> packages =ingStrategy.getIngredientSpace().getLocations();
		Assert.assertTrue(packages.size() > 0);
		Assert.assertTrue(packages.contains(mpoint.getProgramVariant().getAffectedClasses().get(0).getParent(CtPackage.class).getQualifiedName()));
	
		List<CtCodeElement> ingredients =  ingStrategy.getIngredientSpace().getIngredients(mpoint.getCodeElement());
		Assert.assertTrue(ingredients.size() > 0);
		Assert.assertTrue(hasIngredient(ingredients,ingLast));
		
		boolean ingrePackageCorrect = false;
		//Now, we check if all ingredients retrieved belongs affected classes
		for (CtCodeElement ctCodeElement : ingredients) {
			for(CtType aff : mpoint.getProgramVariant().getAffectedClasses()){
				if(aff.getPackage().equals(ctCodeElement.getParent(CtPackage.class)))
					ingrePackageCorrect = true;
			};
			
		}
		assertTrue(ingrePackageCorrect);
		
	}
	
	@Test
	public void testMath85ScopeMethodSpace() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				// We put 0 as max generation, so we force to not evolve the
				// population
				"-maxgen", "0", 
				"-scope", MethodBasicIngredientScope.class.getCanonicalName(), //
				"-seed", "10", "-ingredientstrategy",
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
		
		List<String> methods =ingStrategy.getIngredientSpace().getLocations();
		Assert.assertTrue(methods.size() > 0);
		
		List<CtCodeElement> ingredients =  ingStrategy.getIngredientSpace().getIngredients(mpoint.getCodeElement());
		Assert.assertTrue(ingredients.size() > 0);
		Assert.assertTrue(hasIngredient(ingredients,ingLast));
		
		CtExecutable exec = (mpoint.getCodeElement().getParent(CtExecutable.class));
		for (CtCodeElement ctCodeElement : ingredients) {
			assertEquals(exec,ctCodeElement.getParent(CtExecutable.class));
		}
		
	}
	
	
	
	private boolean hasIngredient(List<CtCodeElement> ingredients, Ingredient ing){
		for (CtCodeElement ctCodeElement : ingredients) {
			if(ctCodeElement.toString().equals(ing.getCode().toString()))
				return true;
		}
		return false;
	}
	
	
	@Test
	public void testMath70WithCtLocation() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), 
				"-seed", "10", "-maxgen", "100", 
				"-scope",	CtLocationIngredientSpace.class.getCanonicalName() 
				};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		//At least one solution in local scope mode.
		assertTrue(solutions.size() > 0);

		//We retrieve the engine
		JGenProg jgp = (JGenProg) main1.getEngine();
		IngredientSpace ispace = jgp.getIngredientStrategy().getIngredientSpace();
		//List of locations considered by the space
		List<CtElement> ctLocations = ispace.getLocations();
		//Only one class has suspicious: org.apache.commons.math.analysis.solvers.BisectionSolver
		assertEquals(1, ctLocations.size());
		//Now, we location is a CtClass (see CTLocationIngredientScope#calculateLocation)
		assertTrue(ctLocations.get(0) instanceof spoon.reflect.declaration.CtClass);
	}

	
	
}
