package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.faultlocalization.bridgeFLSpoon.SpoonLocationPointerLauncher;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;

/**
 * 
 * @author Matias Martinez
 *
 */
public class VariableResolverTest {

	@SuppressWarnings("rawtypes")
	@Test
	public void test1VariablesInScope() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "10",
				// Force not evolution
				"-maxgen", "0",
				//
				"-stopfirst", "true", "-maxtime", "100"

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> variants = main1.getEngine().getVariants();

		ProgramVariant pv = variants.get(0);
		ModificationPoint mp = pv.getModificationPoints().get(0);
		System.out.println(mp);
		List<CtVariable> vars = VariableResolver.searchVariablesInScope(mp.getCodeElement());
		System.out.println(vars);

		// 0 line 72, file BisectionSolver.java
		// final UnivariateRealFunction f, double min, double max, double
		// initial
		// 7 from class + 6 parent + 4 param
		assertEquals((7 + 6 + 4), vars.size());

		ModificationPoint mp1 = pv.getModificationPoints().get(1);

		List<CtVariable> vars1 = VariableResolver.searchVariablesInScope(mp1.getCodeElement());
		// Now, two locals
		assertEquals((7 + 6 + 2), vars1.size());


	}

	@Test
	public void test2VariablesInScope() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
			//Force not running
				"-maxgen", "0", "-scope", "package", "-seed", "10" };
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> variants = main1.getEngine().getVariants();

		ProgramVariant pv = variants.get(0);
		ModificationPoint mp = pv.getModificationPoints().get(0);
		assertEquals(97, ((SuspiciousModificationPoint)mp).getSuspicious().getLineNumber() );
		List<CtVariable> vars = VariableResolver.searchVariablesInScope(mp.getCodeElement());
		//remember that we exclude serialId fields
		assertEquals((0 +4 + 1), vars.size());

		ModificationPoint mp4 = pv.getModificationPoints().get(4);
		assertEquals(181, ((SuspiciousModificationPoint)mp4).getSuspicious().getLineNumber() );
	
		List<CtVariable> vars4 = VariableResolver.searchVariablesInScope(mp4.getCodeElement());
		//method local + block local + par + fields
		assertEquals((1 + 3 +4 + 6), vars4.size());
	}
	
	@Test
	public void testVarMapping() throws Exception{
		

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
			//Force not running
				"-maxgen", "0", "-scope", "package", "-seed", "10" };
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> variants = main1.getEngine().getVariants();

		ProgramVariant pv = variants.get(0);
		ModificationPoint mp = pv.getModificationPoints().get(0);
		assertEquals(97, ((SuspiciousModificationPoint)mp).getSuspicious().getLineNumber() );
		List<CtVariable> varsInScope = VariableResolver.searchVariablesInScope(mp.getCodeElement());
		List<CtVariableAccess> varacc = VariableResolver.collectVariableAccess(mp.getCodeElement());
		
		
		//Let's imagine that we take an ingredient. I created two statement. 
		CtStatement statementsToTest = MutationSupporter.factory.Code().createCodeSnippetStatement("double i = 0; i= i+1;").compile();
		//let's take the statement i= i+1 
		CtStatement varToQuery = ((CtBlock)statementsToTest.getParent()).getLastStatement();
		//we collect the variable access.
		List<CtVariableAccess> varsAccessCollectedToQuery = VariableResolver.collectVariableAccess(varToQuery);
		assertTrue(varsAccessCollectedToQuery.size() > 0);
		
		//We do not match the names, only the types so double vars are mapped
		Map<CtVariableAccess, List<CtVariable>>  mappings1= VariableResolver.matchVars(varsInScope,varsAccessCollectedToQuery, false);
		assertTrue(!mappings1.isEmpty());
		
		assertTrue(mappings1.get(varsAccessCollectedToQuery.get(0)).size() > 0);
		boolean match1 = VariableResolver.fitInContext(varsInScope , varToQuery, false);
		assertTrue(match1);
		
		//Using var names, it must not be match (we have var i as ingredient)
		Map<CtVariableAccess, List<CtVariable>>  mappings1b= VariableResolver.matchVars(varsInScope,varsAccessCollectedToQuery, true);
		assertTrue(mappings1b.get(varsAccessCollectedToQuery.get(0)).isEmpty());
			
		boolean match1b = VariableResolver.fitInContext(varsInScope , varToQuery, true);
		assertFalse(match1b);
		
		//Now, case with same name and type
		
		//Let's imagine that we take an ingredient. I created two statement. 
		CtStatement statementsToTest2 = MutationSupporter.factory.Code().createCodeSnippetStatement("double p = 0; p= p+1;").compile();
		CtStatement varToQuery2 = ((CtBlock)statementsToTest2.getParent()).getLastStatement();
		assertTrue(VariableResolver.fitInContext(varsInScope , varToQuery2, false));
		assertTrue(VariableResolver.fitInContext(varsInScope , varToQuery2, true));
		
		
		//Now, case same name incomp type, no matching
		CtStatement statementsToTest3 = MutationSupporter.factory.Code().createCodeSnippetStatement("int p = 0; p= p+1;").compile();
		CtStatement varToQuery3 = ((CtBlock)statementsToTest3.getParent()).getLastStatement();
		assertFalse(VariableResolver.fitInContext(varsInScope , varToQuery3, false));
		assertFalse(VariableResolver.fitInContext(varsInScope , varToQuery3, true));
		
		
		
		//Now, case two compatible variables
		CtStatement statementsToTest4 = MutationSupporter.factory.Code().createCodeSnippetStatement("double p = 0;double lowerBound = 0; p= lowerBound+1;").compile();
		CtStatement varToQuery4 = ((CtBlock)statementsToTest4.getParent()).getLastStatement();
		assertTrue(VariableResolver.fitInContext(varsInScope , varToQuery4, false));
		assertTrue(VariableResolver.fitInContext(varsInScope , varToQuery4, true));
		
		
		//Now, case two variables, one compatible by name, the other not
		CtStatement statementsToTest5 = MutationSupporter.factory.Code().createCodeSnippetStatement("double p = 0;double lowerBound1 = 0; p = lowerBound1 + 1;").compile();
		CtStatement varToQuery5 = ((CtBlock)statementsToTest5.getParent()).getLastStatement();
		assertTrue(VariableResolver.fitInContext(varsInScope , varToQuery5, false));
		//One var name does not match
		assertFalse(VariableResolver.fitInContext(varsInScope , varToQuery5, true));
	}
	
	@Test
	public void testJSoupParser31be24Version2() throws Exception {
		String dep = new File("./examples/libs/junit-4.5.jar").getAbsolutePath();
		AstorMain main1 = new AstorMain();
		
		String[] args = new String[] { "-mode", "statement", "-location",
				new File("./examples/jsoup31be24").getAbsolutePath(), "-dependencies", dep, 
				//The injected bug produces 4 failing cases in two files
				"-failing",
				"org.jsoup.parser.CharacterReaderTest"+File.pathSeparator
				+"org.jsoup.parser.HtmlParserTest"
				, 
				//
				"-package", "org.jsoup", "-javacompliancelevel", "7", 
				"-stopfirst", "true", 
				//
				"-flthreshold", "0.8", "-srcjavafolder", "/src/main/java/", "-srctestfolder", "/src/test/java/",
				"-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes", 
				//
				"-scope", "local",
				"-seed", "10", 
				//Not Evolution
				"-maxtime", "0",
				"-maxgen","0",
				"-population","1"};
		main1.execute(args);

		ProgramVariant pv = main1.getEngine().getVariants().get(0);
		SuspiciousModificationPoint smp = (SuspiciousModificationPoint) pv.getModificationPoints().get(1);
		assertEquals(118, smp.getSuspicious().getLineNumber());
		
		SpoonLocationPointerLauncher muSpoonLaucher = new SpoonLocationPointerLauncher(MutationSupporter.getFactory());
		//Ingredient position
		List<CtElement> ingredients = muSpoonLaucher.run(smp.getCtClass(), 107);
		System.out.println(ingredients);
		assertEquals(3, ingredients.size());
		CtStatement ingredient = (CtStatement) ingredients.get(2);
		assertNotNull(ingredient);
		assertEquals("pos += offset",ingredient.toString());
		assertTrue(smp.getContextOfModificationPoint().size() > 0);
		//The ingredient must fit in the modpoint'context.
		boolean fits = VariableResolver.fitInPlace(smp.getContextOfModificationPoint(), ingredient);
		assertTrue(fits);
		
		List<CtElement> ingredients2 = muSpoonLaucher.run(smp.getCtClass(), 104);
		CtStatement ingredient2 = (CtStatement) ingredients2.get(3);
		assertNotNull(ingredient2);
		assertEquals("int offset = nextIndexOf(c)",ingredient2.toString());
		
		boolean fits2 = VariableResolver.fitInPlace(smp.getContextOfModificationPoint(), ingredient2);
		assertFalse(fits2);
		
	}
	@Test
	public void testExample288VariablesLocalAccess() throws Exception{
		//then to remove induction variables from varAccessCollected, 
		//we would look for the induction variables' names in varAccessCollected 
		//and remove the local accesses but leave static references to those names in the collection
		
			AstorMain main1 = new AstorMain();
			
			String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
			File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
			String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
					"org.apache.commons.math.optimization.linear.SimplexSolverTest", "-location",
					new File("./examples/Math-issue-288").getAbsolutePath(), 
					"-package", "org.apache.commons", "-srcjavafolder",
					"/src/main/java/", 
					"-srctestfolder", "/src/main/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
					"/target/test-classes", "-javacompliancelevel", "7", //
					"-flthreshold", "0.2", "-out",//
					out.getAbsolutePath(), "-scope", "package", "-seed", "10",
					// Force not evolution
					"-maxgen", "0",
					//
					"-stopfirst", "true", "-maxtime", "100"

			};
			
			
			main1.execute(args);
			
			List<ProgramVariant> variants = main1.getEngine().getVariants();
			JGenProg jgp = (JGenProg) main1.getEngine();
			ModificationPoint mp = variants.get(0).getModificationPoints().get(0);
			System.out.println("Mpoint \n"+mp);
			
			System.out.println("Mpoint Context \n"+mp.getContextOfModificationPoint());
			
			
			IngredientSpace ispace = jgp.getIngredientStrategy().getIngredientSpace();
			List<CtElement> ingredients = ispace.getIngredients(mp.getCodeElement()); 
			for (int i = 0; i < ingredients.size(); i++) {
				//System.out.println(i+ " "+ ingredients.get(i));
			}
			
			
			//For with a induction variable
			CtElement ifor = ingredients.get(71); //for (int i = tableau.getNumObjectiveFunctions() 
			assertTrue(ifor.toString().startsWith("for (int i = tableau.getNumObjectiveFunctions"));
			assertTrue(ifor instanceof CtFor);
			boolean matchFor = VariableResolver.fitInContext(mp.getContextOfModificationPoint(), ifor, true);
			
			//the variable 'i' is declared inside the ingredient, and event it does not exist  
			assertTrue(matchFor);
			
			CtElement iif = ingredients.get(70); //if ((org.apache.commons.math.util.MathUtils.compareTo(tableau.getEntry(0, i),
			assertTrue(iif.toString().startsWith("if ((org.apache.commons.math.util.MathUtils.compareTo(tableau.getEntry(0, i)"));
			assertTrue(iif instanceof CtIf);
			boolean matchIf = VariableResolver.fitInContext(mp.getContextOfModificationPoint(), iif, true);
			
			//the variable 'i' does not exist in the context
			assertFalse(matchIf);
		
			
			CtElement iStaticSame = ingredients.get(0);//static setMaxIterations(org.apache.commons.math.optimization.linear.AbstractLinearOptimizer.DEFAULT_MAX_ITERATIONS)
			assertTrue(iStaticSame instanceof CtInvocation);
			assertTrue(iStaticSame.toString().startsWith("setMaxIterations("));
			boolean matchStSame = VariableResolver.fitInContext(mp.getContextOfModificationPoint(), iStaticSame, true);
			assertTrue(matchStSame);
			
			
			CtElement iStaticDouble = ingredients.get(80);//static 
			assertTrue(iStaticDouble instanceof CtLocalVariable);
			assertTrue(iStaticDouble.toString().startsWith("double minRatio = java.lang.Double.MAX_VALUE"));
			boolean matchSt = VariableResolver.fitInContext(mp.getContextOfModificationPoint(), iStaticDouble, true);
			assertTrue(matchSt);
			
			
		}
	
	@Test
	public void testLang39VariableIndutionBugFix() throws Exception{
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.7.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.lang3.StringUtilsTest"
				,"-location",
				new File("./examples/lang_39/").getAbsolutePath(),
				//
				"-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", 
				"-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "5", "-flthreshold", "0.5", 
				"-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "6130",//"6010", 
				"-maxgen", "50", 
				"-stopfirst", "true",
				"-maxtime", "30",
				"-testbystep",
				//
				"ignoredtestcases","org.apache.commons.lang.LocaleUtilsTest",
					
		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		
		assertTrue(main1.getEngine().getSolutions().size() > 0);
	}
	
	
}
