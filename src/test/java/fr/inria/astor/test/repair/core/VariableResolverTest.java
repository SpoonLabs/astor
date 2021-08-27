package fr.inria.astor.test.repair.core;

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

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.faultlocalization.bridgeFLSpoon.SpoonLocationPointerLauncher;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.VarAccessWrapper;
import fr.inria.astor.core.manipulation.sourcecode.VarMapping;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPool;
import fr.inria.astor.test.repair.approaches.TestHelper;
//import fr.inria.astor.test.repair.evaluation.extensionpoints.deep.DeepRepairTest;
import fr.inria.main.evolution.AstorMain;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Filter;

/**
 * 
 * @author Matias Martinez
 *
 */
public class VariableResolverTest {

	protected Logger log = Logger.getLogger(this.getClass().getName());

	@SuppressWarnings("rawtypes")
	@Test
	public void test1VariablesInScope() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
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
		log.debug(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> variants = main1.getEngine().getVariants();

		ProgramVariant pv = variants.get(0);
		ModificationPoint mp = pv.getModificationPoints().get(0);
		log.debug(mp);
		List<CtVariable> vars = VariableResolver.searchVariablesInScope(mp.getCodeElement());
		log.debug(vars);

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
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				// Force not running
				"-maxgen", "0", "-scope", "package", "-seed", "10" };
		log.debug(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> variants = main1.getEngine().getVariants();

		ProgramVariant pv = variants.get(0);
		ModificationPoint mp = pv.getModificationPoints().get(0);
		assertEquals(97, ((SuspiciousModificationPoint) mp).getSuspicious().getLineNumber());
		List<CtVariable> vars = VariableResolver.searchVariablesInScope(mp.getCodeElement());
		// remember that we exclude serialId fields
		assertEquals((0 + 4 + 1), vars.size());

		ModificationPoint mp10 = pv.getModificationPoints().get(10);
		System.out.println(mp10.toString());
		assertEquals(181, ((SuspiciousModificationPoint) mp10).getSuspicious().getLineNumber());

		List<CtVariable> vars10 = VariableResolver.searchVariablesInScope(mp10.getCodeElement());
		// method local + block local + par + fields
		assertEquals((1 + 3 + 4 + 6), vars10.size());
	}

	@Test
	public void testVarMapping() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				// Force not running
				"-maxgen", "0", "-scope", "package", "-seed", "10" };
		log.debug(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> variants = main1.getEngine().getVariants();

		ProgramVariant pv = variants.get(0);
		ModificationPoint mp = pv.getModificationPoints().get(0);
		assertEquals(97, ((SuspiciousModificationPoint) mp).getSuspicious().getLineNumber());
		List<CtVariable> varsInScope = VariableResolver.searchVariablesInScope(mp.getCodeElement());
		List<CtVariableAccess> varacc = VariableResolver.collectVariableAccess(mp.getCodeElement());

		// Let's imagine that we take an ingredient. I created two statement.
		// let's take the statement i= i+1
		CtStatement statementsToTest = MutationSupporter.factory.Code()
				.createCodeSnippetStatement("double i = 0; i= i+1;").compile();

		// we collect the variable access.
		List<CtVariableAccess> varsAccessCollectedToQuery = VariableResolver.collectVariableAccess(statementsToTest);
		assertTrue(varsAccessCollectedToQuery.size() > 0);

		// We do not match the names, only the types so double vars are mapped
		Map<CtVariableAccess, List<CtVariable>> mappings1 = VariableResolver.matchVars(varsInScope,
				varsAccessCollectedToQuery, false);
		assertTrue(!mappings1.isEmpty());

		assertTrue(mappings1.get(varsAccessCollectedToQuery.get(0)).size() > 0);
		boolean match1 = VariableResolver.fitInContext(varsInScope, statementsToTest, false);
		assertTrue(match1);

		// Using var names, it must not be match (we have var i as ingredient)
		Map<CtVariableAccess, List<CtVariable>> mappings1b = VariableResolver.matchVars(varsInScope,
				varsAccessCollectedToQuery, true);
		assertTrue(mappings1b.get(varsAccessCollectedToQuery.get(0)).isEmpty());

		boolean match1b = VariableResolver.fitInContext(varsInScope, statementsToTest, true);
		assertFalse(match1b);

		// Now, case with same name and type

		// Let's imagine that we take an ingredient. I created two statement.
		CtStatement statementsToTest2 = MutationSupporter.factory.Code()
				.createCodeSnippetStatement("double p = 0; p= p+1;").compile();

		assertTrue(VariableResolver.fitInContext(varsInScope, statementsToTest2, false));
		assertTrue(VariableResolver.fitInContext(varsInScope, statementsToTest2, true));

		// Now, case same name incomp type, no matching
		CtStatement statementsToTest3 = MutationSupporter.factory.Code()
				.createCodeSnippetStatement("int p = 0; p= p+1;").compile();

		assertFalse(VariableResolver.fitInContext(varsInScope, statementsToTest3, false));
		assertFalse(VariableResolver.fitInContext(varsInScope, statementsToTest3, true));

		// Now, case two compatible variables
		CtStatement statementsToTest4 = MutationSupporter.factory.Code()
				.createCodeSnippetStatement("double p = 0;double lowerBound = 0; p= lowerBound+1;").compile();

		assertTrue(VariableResolver.fitInContext(varsInScope, statementsToTest4, false));
		assertTrue(VariableResolver.fitInContext(varsInScope, statementsToTest4, true));

		// Now, case two variables, one compatible by name, the other not
		CtStatement statementsToTest5 = MutationSupporter.factory.Code()
				.createCodeSnippetStatement("double p = 0;double lowerBound1 = 0; p = lowerBound1 + 1;").compile();
		assertTrue(VariableResolver.fitInContext(varsInScope, statementsToTest5, false));
		// One var name does not match
		assertFalse(VariableResolver.fitInContext(varsInScope, statementsToTest5, true));
	}

	@Test
	public void testJSoupParser31be24Version2() throws Exception {
		String dep = new File("./examples/libs/junit-4.5.jar").getAbsolutePath();
		AstorMain main1 = new AstorMain();

		String[] args = new String[] { "-mode", "jgenprog", "-location",
				new File("./examples/jsoup31be24").getAbsolutePath(), "-dependencies", dep,
				// The injected bug produces 4 failing cases in two files
				"-failing",
				"org.jsoup.parser.CharacterReaderTest" + File.pathSeparator + "org.jsoup.parser.HtmlParserTest",
				//
				"-package", "org.jsoup", "-javacompliancelevel", "7", "-stopfirst", "true",
				//
				"-flthreshold", "0.8", "-srcjavafolder", "/src/main/java/", "-srctestfolder", "/src/test/java/",
				"-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes",
				//
				"-scope", "local", "-seed", "10",
				// Not Evolution
				"-maxtime", "0", "-maxgen", "0", "-population", "1" };
		main1.execute(args);

		ProgramVariant pv = main1.getEngine().getVariants().get(0);
		SuspiciousModificationPoint smp = (SuspiciousModificationPoint) pv.getModificationPoints().get(1);
		assertEquals(118, smp.getSuspicious().getLineNumber());

		SpoonLocationPointerLauncher muSpoonLaucher = new SpoonLocationPointerLauncher(MutationSupporter.getFactory());
		// Ingredient position
		List<CtElement> ingredients = muSpoonLaucher.run(smp.getCtClass(), 107);
		log.debug(ingredients);
		assertEquals(3, ingredients.size());
		CtStatement ingredient = (CtStatement) ingredients.get(2);
		assertNotNull(ingredient);
		assertEquals("pos += offset", ingredient.toString());
		assertTrue(smp.getContextOfModificationPoint().size() > 0);
		// The ingredient must fit in the modpoint'context.
		boolean fits = VariableResolver.fitInPlace(smp.getContextOfModificationPoint(), ingredient);
		assertTrue(fits);

		List<CtElement> ingredients2 = muSpoonLaucher.run(smp.getCtClass(), 104);
		CtStatement ingredient2 = (CtStatement) ingredients2.get(3);
		assertNotNull(ingredient2);
		assertEquals("int offset = nextIndexOf(c)", ingredient2.toString());

		boolean fits2 = VariableResolver.fitInPlace(smp.getContextOfModificationPoint(), ingredient2);
		assertFalse(fits2);

	}

	@Test
	public void testExample288VariablesLocalAccess() throws Exception {
		// then to remove induction variables from varAccessCollected,
		// we would look for the induction variables' names in
		// varAccessCollected
		// and remove the local accesses but leave static references to those
		// names in the collection

		AstorMain main1 = new AstorMain();

		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.optimization.linear.SimplexSolverTest", "-location",
				new File("./examples/Math-issue-288").getAbsolutePath(), "-package", "org.apache.commons",
				"-srcjavafolder", "/src/main/java/", "-srctestfolder", "/src/main/test/", "-binjavafolder",
				"/target/classes", "-bintestfolder", "/target/test-classes", "-javacompliancelevel", "7", //
				"-flthreshold", "0.2", "-out", //
				out.getAbsolutePath(), "-scope", "package", "-seed", "10",
				// Force not evolution
				"-maxgen", "0", "-population", "1", //
				"-stopfirst", "true", "-maxtime", "100"

		};

		main1.execute(args);

		List<ProgramVariant> variants = main1.getEngine().getVariants();
		JGenProg jgp = (JGenProg) main1.getEngine();
		ModificationPoint mp = findModPoint(variants.get(0).getModificationPoints(), "return minPos");// variants.get(0).getModificationPoints().get(0);
		log.debug("Mpoint \n" + mp);
		log.debug(mp.getCtClass());

		log.debug("Mpoint Context \n" + mp.getContextOfModificationPoint());

		IngredientPool ispace = jgp.getIngredientSearchStrategy().getIngredientSpace();
		List<Ingredient> ingredients = ispace.getIngredients(mp.getCodeElement());

		// For with a induction variable
		CtElement ifor = findElement(ingredients, "for (int i = tableau.getNumObjectiveFunctions").getCode();// ingredients.get(46);
		// //for
		// (int
		// i
		// =
		// tableau.getNumObjectiveFunctions()
		assertTrue(ifor.toString().startsWith("for (int i = tableau.getNumObjectiveFunctions"));
		assertTrue(ifor instanceof CtFor);
		log.debug("fit? " + ifor + " in context: " + mp.getContextOfModificationPoint());
		boolean matchFor = VariableResolver.fitInContext(mp.getContextOfModificationPoint(), ifor, true);

		// the variable 'i' is declared inside the ingredient, and event it does
		// not exist
		assertTrue(matchFor);

		CtElement iif = findElement(ingredients,
				"if ((org.apache.commons.math.util.MathUtils.compareTo(tableau.getEntry(0, i)").getCode();// ingredients.get(45);
		// //if
		// ((org.apache.commons.math.util.MathUtils.compareTo(tableau.getEntry(0,
		// i),
		assertTrue(TestHelper.getWithoutParentheses(iif.toString()).startsWith(TestHelper.getWithoutParentheses(
				"if ((org.apache.commons.math.util.MathUtils.compareTo(tableau.getEntry(0, i)")));
		assertTrue(iif instanceof CtIf);
		boolean matchIf = VariableResolver.fitInContext(mp.getContextOfModificationPoint(), iif, true);

		// the variable 'i' does not exist in the context
		assertFalse(matchIf);

		CtElement iStaticSame = findElement(ingredients, "setMaxIterations(").getCode();// ingredients.get(0);//static
		// setMaxIterations(org.apache.commons.math.optimization.linear.AbstractLinearOptimizer.DEFAULT_MAX_ITERATIONS)
		assertTrue(iStaticSame instanceof CtInvocation);
		assertTrue(iStaticSame.toString().startsWith("setMaxIterations("));
		boolean matchStSame = VariableResolver.fitInContext(mp.getContextOfModificationPoint(), iStaticSame, true);
		assertTrue(matchStSame);

		CtElement iStaticDouble = findElement(ingredients, "double minRatio = java.lang.Double.MAX_VALUE").getCode();// ingredients.get(55);//static
		assertTrue(iStaticDouble instanceof CtLocalVariable);
		assertTrue(iStaticDouble.toString().startsWith("double minRatio = java.lang.Double.MAX_VALUE"));
		boolean matchSt = VariableResolver.fitInContext(mp.getContextOfModificationPoint(), iStaticDouble, true);
		assertTrue(matchSt);

	}

	private ModificationPoint findModPoint(List<ModificationPoint> modificationPoints, String content) {
		for (ModificationPoint modificationPoint : modificationPoints) {
			if (modificationPoint.getCodeElement().toString().startsWith(content))
				return modificationPoint;
		}
		return null;
	}

	private Ingredient findElement(List<Ingredient> ingredients, String tofind) {
		for (Ingredient ingredient : ingredients) {
			if (TestHelper.getWithoutParentheses(ingredient.getCode().toString())
					.startsWith(TestHelper.getWithoutParentheses(tofind)))
				return ingredient;
		}
		return null;
	}

	@Test
	@Ignore
	public void testLang39VariableIndutionBugFix() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.7.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.lang3.StringUtilsTest", "-location",
				new File("./examples/lang_39/").getAbsolutePath(),
				//
				"-package", "org.apache.commons", "-srcjavafolder", "/src/java/", "-srctestfolder", "/src/test/",
				"-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes", "-javacompliancelevel",
				"5", "-flthreshold", "0.5", "-out", out.getAbsolutePath(), "-scope", "local", "-seed", "6130", // "6010",
				"-maxgen", "50", "-stopfirst", "true", "-maxtime", "30", "-testbystep",
				//
				"ignoredtestcases", "org.apache.commons.lang.LocaleUtilsTest",

		};
		log.debug(Arrays.toString(args));
		main1.execute(args);

		assertTrue(main1.getEngine().getSolutions().size() > 0);
	}

	private String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
	private File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

	@Test
	public void testBugVariableResolver2() {

		File projectLocation = new File("./examples/exampleVarResolver/");
		AstorMain main1 = new AstorMain();
		Launcher launcher = new Launcher();
		launcher.addInputResource("");

		Factory factory = launcher.createFactory();
		factory.getEnvironment().setComplianceLevel(6);
		SpoonModelBuilder compiler = launcher.createCompiler(factory);
		compiler.setSourceClasspath(dep.split(File.pathSeparator));
		compiler.addInputSource(new File(projectLocation.getAbsolutePath()));
		compiler.build();

		List<CtType<?>> types = factory.Type().getAll();
		assertTrue(types.size() > 0);
		log.info(types.get(0).toString());

		List<CtMethod> mts = new ArrayList<>(types.get(0).getAllMethods());
		CtMethod mt = types.get(0).getAllMethods().stream().filter(x -> x.getSimpleName().equals("test2")).findFirst()
				.get();

		log.debug("Mthd : " + mt);

		CtStatement l1 = mt.getBody().getStatement(1);
		log.debug(l1);
		CtStatement l2 = mt.getBody().getStatement(2);
		log.debug(l2);

		List<CtVariableAccess> vars2 = l2.getElements(new VAFilter());
		log.debug("vars access l2: " + vars2);

		List<CtVariable> fields = l2.getParent(CtClass.class).getFields();

		Map<VarAccessWrapper, List<CtVariable>> mapsVariables = new HashMap<>();

		// let's take the second one (l2) and maps with fields
		mapsVariables.put(new VarAccessWrapper(vars2.get(1)), fields);

		assertTrue(l2.toString().startsWith("l1 = l2"));

		VarMapping vm = new VarMapping(mapsVariables, new ArrayList<>());

		assertTrue(vm.getMappedVariables().size() > 0);

		log.debug(vm.getMappedVariables());

		List<Map<String, CtVariable>> allCombinations = VariableResolver
				.findAllVarMappingCombination(vm.getMappedVariables());

		assertTrue(allCombinations.size() > 0);

		log.debug("Combinations: " + allCombinations);
		Map<VarAccessWrapper, CtVariableAccess> result = VariableResolver.convertIngredient(vm, allCombinations.get(0));

		try {
			// We force to print the line 2 (transformed)
			log.debug(l2);

			assertTrue(l2.toString().startsWith("l1 = f1"));

		} catch (ClassCastException e) {

			e.printStackTrace();
			Assert.fail();
		}
		//
		log.debug("before reset " + l2.toString());

		CtAssignment assingL2 = (CtAssignment) l2;

		CtVariableAccess assignment = (CtVariableAccess) assingL2.getAssignment();

		assertTrue(assignment.getType().toString().startsWith("java.lang.String"));

		// Revert

		VariableResolver.resetIngredient(result);

		assertTrue(l2.toString().startsWith("l1 = l2"));

		log.debug("After reset " + l2.toString());

	}

	@Test
	public void testBugVariableResolver1() {

		File projectLocation = new File("./examples/exampleVarResolver/");
		AstorMain main1 = new AstorMain();
		Launcher launcher = new Launcher();
		launcher.addInputResource("");

		Factory factory = launcher.createFactory();
		factory.getEnvironment().setComplianceLevel(6);
		SpoonModelBuilder compiler = launcher.createCompiler(factory);
		compiler.setSourceClasspath(dep.split(File.pathSeparator));
		compiler.addInputSource(new File(projectLocation.getAbsolutePath()));
		compiler.build();

		List<CtType<?>> types = factory.Type().getAll();
		assertTrue(types.size() > 0);
		log.info(types.get(0).toString());

		List<CtMethod> mts = new ArrayList<>(types.get(0).getAllMethods());
		CtMethod mt = types.get(0).getAllMethods().stream().filter(x -> x.getSimpleName().equals("test1")).findFirst()
				.get();

		log.debug("Mthd : " + mt);

		CtStatement l1 = mt.getBody().getStatement(0);
		log.debug(l1);
		CtStatement l2 = mt.getBody().getStatement(1);
		log.debug(l2);

		List<CtVariable> vars1 = l1.getElements(new VarFilter());

		log.debug("vars: " + vars1);

		List<CtVariableAccess> vars2 = l2.getElements(new VAFilter());
		log.debug("vars access: " + vars2);

		Map<VarAccessWrapper, List<CtVariable>> mapsVariables = new HashMap<>();

		mapsVariables.put(new VarAccessWrapper(vars2.get(0)), vars1);

		assertTrue(l2.toString().startsWith("java.lang.String l2 = f1"));

		VarMapping vm = new VarMapping(mapsVariables, new ArrayList<>());

		assertTrue(vm.getMappedVariables().size() > 0);

		log.debug(vm.getMappedVariables());

		List<Map<String, CtVariable>> allCombinations = VariableResolver
				.findAllVarMappingCombination(vm.getMappedVariables());

		assertTrue(allCombinations.size() > 0);

		log.debug("To convert " + allCombinations.get(0));
		Map<VarAccessWrapper, CtVariableAccess> result = VariableResolver.convertIngredient(vm, allCombinations.get(0));

		try {
			// We force to print the line 2 (transformed)
			log.debug(l2);

			assertTrue(l2.toString().startsWith("java.lang.String l2 = l1"));

		} catch (ClassCastException e) {

			e.printStackTrace();
			Assert.fail();
		}
		//
		log.debug("before reset " + l2.toString());

		CtLocalVariable assingL2 = (CtLocalVariable) l2;

		CtVariableAccess assignment = (CtVariableAccess) assingL2.getAssignment();

		assertEquals("java.lang.String", assignment.getType().toString());

		VariableResolver.resetIngredient(result);

		assertTrue(l2.toString().startsWith("java.lang.String l2 = f1"));

		log.debug("After reset " + l2.toString());
	}

	@Test
	@Ignore
	public void testBugNPE() {

		File projectLocation = new File("./examples/exampleVRClassNotFould/");
		Launcher launcher = new Launcher();
		launcher.addInputResource("");

		Factory factory = launcher.createFactory();
		factory.getEnvironment().setComplianceLevel(6);
		SpoonModelBuilder compiler = launcher.createCompiler(factory);
		compiler.setSourceClasspath(dep.split(File.pathSeparator));
		compiler.addInputSource(new File(projectLocation.getAbsolutePath()));
		compiler.build();

		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm1").getFile());

		ConfigurationProperties.setProperty("learningdir", learningDir.getAbsolutePath());

		List<CtType<?>> types = factory.Type().getAll();
		assertTrue(types.size() > 0);

		CtType<?> type1 = types.stream().filter(x -> x.getSimpleName().equals("Class1")).findFirst().get();

		CtType<?> type2 = types.stream().filter(x -> x.getSimpleName().equals("Class2")).findFirst().get();

		CtMethod mt1 = type2.getAllMethods().stream().filter(x -> x.getSimpleName().equals("test1")).findFirst().get();
		CtStatement st = mt1.getBody().getStatement(0);

		List<CtVariable> varsContext = st.getElements(new VarFilter());

		//
		CtMethod mt2 = type2.getAllMethods().stream().filter(x -> x.getSimpleName().equals("test2")).findFirst().get();
		CtStatement st2 = mt2.getBody().getStatement(1);

		log.debug("Comparing: " + varsContext + " " + st2);
		VarMapping vm = VariableResolver.mapVariablesUsingCluster(varsContext, st2);
		assertTrue(vm.getMappedVariables().isEmpty());
		log.debug("map " + vm.getMappedVariables());
		log.debug("-----");

		//
		CtMethod mt3 = type2.getAllMethods().stream().filter(x -> x.getSimpleName().equals("test3")).findFirst().get();
		log.debug(mt3);
		CtStatement ingredient = mt3.getBody().getStatement(0);
		log.debug("Comparing: " + varsContext + " " + ingredient);
		VarMapping vm3 = VariableResolver.mapVariablesUsingCluster(varsContext, ingredient);
		log.debug("map " + vm3.getMappedVariables());
		assertTrue(vm3.getMappedVariables().isEmpty());
		log.debug("-----");
		//

		//
		log.debug("Comparing: " + varsContext + " " + mt3.getBody().getStatement(0));
		ingredient = mt1.getBody().getStatement(0);
		VarMapping vm4 = VariableResolver.mapVariablesUsingCluster(varsContext, ingredient);
		log.debug("map " + vm4.getMappedVariables());
		assertTrue(vm4.getMappedVariables().isEmpty());
		log.debug("-----");
		//

		List<CtVariable> varsContext2 = mt3.getBody().getStatement(1).getElements(new VarFilter());
		log.debug(varsContext2);
		ingredient = mt1.getBody().getStatement(1);
		log.debug("Comparing: " + varsContext2 + " and " + ingredient);

		VarMapping vm5 = VariableResolver.mapVariablesUsingCluster(varsContext2, ingredient);

		log.debug("map 5 " + vm5.getMappedVariables());

		assertTrue(vm5.getMappedVariables().size() > 0);

	}

	@Test
	public void testBugVariableResolver3() {

		File projectLocation = new File("./examples/exampleVarResolver/");
		AstorMain main1 = new AstorMain();
		Launcher launcher = new Launcher();
		launcher.addInputResource("");

		Factory factory = launcher.createFactory();
		factory.getEnvironment().setComplianceLevel(6);
		SpoonModelBuilder compiler = launcher.createCompiler(factory);
		compiler.setSourceClasspath(dep.split(File.pathSeparator));
		compiler.addInputSource(new File(projectLocation.getAbsolutePath()));
		compiler.build();

		List<CtType<?>> types = factory.Type().getAll();
		assertTrue(types.size() > 0);
		log.info(types.get(0).toString());

		List<CtMethod> mts = new ArrayList<>(types.get(0).getAllMethods());
		CtMethod mt = types.get(0).getAllMethods().stream().filter(x -> x.getSimpleName().equals("test3")).findFirst()
				.get();

		log.debug("Mthd : " + mt);

		CtStatement l2 = mt.getBody().getStatement(1);
		log.debug(l2);

		List<CtVariable> pars1 = l2.getParent(CtMethod.class).getParameters();

		log.debug("param: " + pars1);

		List<CtVariableAccess> vars2 = l2.getElements(new VAFilter());
		log.debug("vars access: " + vars2);

		Map<VarAccessWrapper, List<CtVariable>> mapsVariables = new HashMap<>();

		mapsVariables.put(new VarAccessWrapper(vars2.get(0)), pars1);

		assertTrue(l2.toString().startsWith("java.lang.String l2 = f1"));

		VarMapping vm = new VarMapping(mapsVariables, new ArrayList<>());

		assertTrue(vm.getMappedVariables().size() > 0);

		log.debug(vm.getMappedVariables());

		List<Map<String, CtVariable>> allCombinations = VariableResolver
				.findAllVarMappingCombination(vm.getMappedVariables());

		assertTrue(allCombinations.size() > 0);

		log.debug("To convert " + allCombinations.get(0));
		Map<VarAccessWrapper, CtVariableAccess> result = VariableResolver.convertIngredient(vm, allCombinations.get(0));

		try {
			// We force to print the line 2 (transformed)
			log.debug(l2);

			assertTrue(l2.toString().startsWith("java.lang.String l2 = p1"));

		} catch (ClassCastException e) {

			e.printStackTrace();
			Assert.fail();
		}
		//
		log.debug("before reset " + l2.toString());

		CtLocalVariable assingL2 = (CtLocalVariable) l2;

		CtVariableAccess assignment = (CtVariableAccess) assingL2.getAssignment();

		assertTrue(assignment.getType().toString().startsWith("java.lang.String"));

		// Revert

		VariableResolver.resetIngredient(result);

		assertTrue(l2.toString().startsWith("java.lang.String l2 = f1"));

		log.debug("After reset " + l2.toString());
	}

	@Test
	public void testBugVariableResolver4() {

		File projectLocation = new File("./examples/exampleVarResolver/");
		AstorMain main1 = new AstorMain();
		Launcher launcher = new Launcher();
		launcher.addInputResource("");

		Factory factory = launcher.createFactory();
		factory.getEnvironment().setComplianceLevel(6);
		SpoonModelBuilder compiler = launcher.createCompiler(factory);
		compiler.setSourceClasspath(dep.split(File.pathSeparator));
		compiler.addInputSource(new File(projectLocation.getAbsolutePath()));
		compiler.build();

		List<CtType<?>> types = factory.Type().getAll();
		assertTrue(types.size() > 0);
		log.info(types.get(0).toString());

		List<CtMethod> mts = new ArrayList<>(types.get(0).getAllMethods());
		CtMethod mt = types.get(0).getAllMethods().stream().filter(x -> x.getSimpleName().equals("test2")).findFirst()
				.get();

		log.debug("Mthd : " + mt);

		CtStatement l2 = mt.getBody().getStatement(2);
		log.debug(l2);

		List<CtVariableAccess> vars2 = l2.getElements(new VAFilter());
		log.debug("vars access l2: " + vars2);

		List<CtVariable> fields = l2.getParent(CtClass.class).getFields();

		Map<VarAccessWrapper, List<CtVariable>> mapsVariables = new HashMap<>();

		// let's take the second one (l1) and maps with fields
		// L1 is a WRITE
		mapsVariables.put(new VarAccessWrapper(vars2.get(0)), fields);

		assertTrue(l2.toString().startsWith("l1 = l2"));

		VarMapping vm = new VarMapping(mapsVariables, new ArrayList<>());

		assertTrue(vm.getMappedVariables().size() > 0);

		log.debug(vm.getMappedVariables());

		List<Map<String, CtVariable>> allCombinations = VariableResolver
				.findAllVarMappingCombination(vm.getMappedVariables());

		assertTrue(allCombinations.size() > 0);

		log.debug("Combinations: " + allCombinations);
		Map<VarAccessWrapper, CtVariableAccess> result = VariableResolver.convertIngredient(vm, allCombinations.get(0));

		try {
			// We force to print the line 2 (transformed)
			log.debug(l2);

			assertTrue(l2.toString().startsWith("f1 = l2"));

		} catch (ClassCastException e) {

			e.printStackTrace();
			Assert.fail();
		}
		//
		// log.debug("before reset " + l2.toString());

		CtAssignment assingL2 = (CtAssignment) l2;

		CtVariableAccess assignment = (CtVariableAccess) assingL2.getAssignment();

		assertTrue(assignment.getType().toString().startsWith("java.lang.String"));

		// Revert

		VariableResolver.resetIngredient(result);

		assertTrue(l2.toString().startsWith("l1 = l2"));

		log.debug("After reset " + l2.toString());

	}

	public class VAFilter implements Filter<CtVariableAccess> {

		@Override
		public boolean matches(CtVariableAccess element) {
			return true;
		}
	};

	public class VarFilter implements Filter<CtVariable> {

		@Override
		public boolean matches(CtVariable element) {
			return true;
		}
	};

	public class FieldFilter implements Filter<CtField> {

		@Override
		public boolean matches(CtField element) {
			return true;
		}
	};

}
