package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import fr.inria.astor.approaches.tos.core.evalTos.MetaEvalTOSApproach;
import fr.inria.astor.approaches.tos.operator.metaevaltos.LogicExpOperator;
import fr.inria.astor.approaches.tos.operator.metaevaltos.OperatorReplacementOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.VarReplacementByAnotherVarOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.WrapwithTrySingleStatementOp;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtIf;

/**
 * 
 * @author Matias Martinez
 *
 */
public class MetEngineDebuggingTest {

	@Test
	public void testBT_Math85_1_Met_all() throws Exception {
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = MathCommandsTests.getMath85Command();
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "true");
		command.command.put("-maxgen", "1000000");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");

		command.command.put("-id", "test-math85");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-flthreshold", "0.24");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		assertTrue(main1.getEngine().getSolutions().size() > 0);

		List<ProgramVariant> solutionTry = main1.getEngine().getSolutions().stream()
				.filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof WrapwithTrySingleStatementOp).findAny()
						.isPresent())
				.collect(Collectors.toList());

		assertTrue(solutionTry.size() > 0);

		// Retrieve the buggy if condition.
		ModificationPoint mp198 = main1.getEngine().getVariants().get(0).getModificationPoints().stream()
				.filter(e -> (e.getCodeElement().getPosition().getLine() == 198 && e.getCodeElement().getPosition()
						.getFile().getName().equals("UnivariateRealSolverUtils.java")))
				.findAny().get();
		assertNotNull(mp198);

		assertTrue(mp198.getCodeElement() instanceof CtIf);

		main1.getEngine().getVariants().get(0).getModificationPoints().clear();
		main1.getEngine().getVariants().get(0).getModificationPoints().add(mp198);

		main1.getEngine().getSolutions().clear();

		command.command.put("-stopfirst", "false");
		command.command.put("-loglevel", "DEBUG");
		main1.getEngine().startEvolution();
		assertTrue(main1.getEngine().getSolutions().size() > 0);
		main1.getEngine().atEnd();

		List<ProgramVariant> solutionOperatorReplacement = main1.getEngine().getSolutions().stream()
				.filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof OperatorReplacementOp).findAny().isPresent())
				.collect(Collectors.toList());

		assertTrue(solutionOperatorReplacement.size() > 0);

	}

	/**
	 * Tests the bug -The child does NOT compile: 5, errors:
	 * [UnivariateRealSolverUtils.java:74: error: cannot find symbol return ((fa *
	 * fb) >= 0.0) || function.derivative() == null;
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBugIngredientMethodInv_Math85_Dynamoth() throws Exception {
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = MathCommandsTests.getMath85Command();
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "true");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");

		command.command.put("-id", "test-math85");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-flthreshold", "0.24");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		// Retrieve the buggy if condition.
		ModificationPoint mp198 = main1.getEngine().getVariants().get(0).getModificationPoints().stream()
				.filter(e -> (e.getCodeElement().getPosition().getLine() == 198 && e.getCodeElement().getPosition()
						.getFile().getName().equals("UnivariateRealSolverUtils.java")))
				.findAny().get();
		assertNotNull(mp198);

		assertTrue(mp198.getCodeElement() instanceof CtIf);

		main1.getEngine().getVariants().get(0).getModificationPoints().clear();
		main1.getEngine().getVariants().get(0).getModificationPoints().add(mp198);

		AstorOperator opWithDynamothIngredient = main1.getEngine().getOperatorSpace().getOperators().stream()
				.filter(e -> e instanceof LogicExpOperator).findFirst().get();

		assertNotNull(opWithDynamothIngredient);
		main1.getEngine().getOperatorSpace().getOperators().add(opWithDynamothIngredient);

		main1.getEngine().getSolutions().clear();

		MetaEvalTOSApproach.MAX_GENERATIONS = 1000;
		main1.getEngine().startEvolution();
		main1.getEngine().atEnd();

	}

	/**
	 * Line 69885 [DEBUG]
	 * fr.inria.astor.approaches.tos.operator.metaevaltos.MetaGenerator.createMetaForSingleElement(MetaGenerator.java:108)
	 * - invocation:
	 * org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils._meta_9(function,
	 * upperBound, b, initial, lowerBound, fb, a, fa) [DEBUG]
	 * fr.inria.astor.core.solutionsearch.AstorCoreEngine.processCreatedVariant(AstorCoreEngine.java:443)
	 * - -The child does NOT compile: 7, errors:
	 * [UnivariateRealSolverUtils.java:1684: error: non-static method hashCode()
	 * cannot be referenced from a static context return hashCode(); ^,
	 * UnivariateRealSolverUtils.java:1694: error: non-static method hashCode()
	 * cannot be referenced from a static context return hashCode(); ^]
	 * 
	 * @throws Exception
	 */

	@Test
	public void tesM85hasCode() throws Exception {
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = MathCommandsTests.getMath85Command();
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "true");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");

		command.command.put("-id", "test-math85");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-flthreshold", "0.24");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		// Retrieve the buggy if condition.
		ModificationPoint mp198 = main1.getEngine().getVariants().get(0).getModificationPoints().stream()
				.filter(e -> (e.getCodeElement().getPosition().getLine() == 198 && e.getCodeElement().getPosition()
						.getFile().getName().equals("UnivariateRealSolverUtils.java")))
				.findAny().get();
		assertNotNull(mp198);

		assertTrue(mp198.getCodeElement() instanceof CtIf);

		main1.getEngine().getVariants().get(0).getModificationPoints().clear();
		main1.getEngine().getVariants().get(0).getModificationPoints().add(mp198);

		AstorOperator opWithDynamothIngredient = main1.getEngine().getOperatorSpace().getOperators().stream()
				.filter(e -> e instanceof LogicExpOperator).findFirst().get();

		assertNotNull(opWithDynamothIngredient);
		main1.getEngine().getOperatorSpace().getOperators().add(opWithDynamothIngredient);

		main1.getEngine().getSolutions().clear();

		MetaEvalTOSApproach.MAX_GENERATIONS = 1000;
		main1.getEngine().startEvolution();
		main1.getEngine().atEnd();

	}

	@Test
	public void testBT_Math85_StaticToNoStaticMethod() throws Exception {
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = MathCommandsTests.getMath85Command();
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "true");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");

		command.command.put("-id", "test-math85");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-flthreshold", "0.24");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		// Retrieve the buggy if condition.
		ModificationPoint mp198 = main1.getEngine().getVariants().get(0).getModificationPoints().stream()
				.filter(e -> (e.getCodeElement().getPosition().getLine() == 198 && e.getCodeElement().getPosition()
						.getFile().getName().equals("UnivariateRealSolverUtils.java")))
				.findAny().get();
		assertNotNull(mp198);

		assertTrue(mp198.getCodeElement() instanceof CtIf);

		main1.getEngine().getVariants().get(0).getModificationPoints().clear();
		main1.getEngine().getVariants().get(0).getModificationPoints().add(mp198);

		main1.getEngine().getSolutions().clear();

		MetaEvalTOSApproach.MAX_GENERATIONS = 1000;
		main1.getEngine().startEvolution();
		main1.getEngine().atEnd();

	}

	@Test
	public void testBT_Math85_CheckVarReplacement() throws Exception {
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = MathCommandsTests.getMath85Command();
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "1000");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");

		command.command.put("-id", "test-math85");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-flthreshold", "0.24");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		// Retrieve the buggy if condition.
		ModificationPoint mp198 = main1.getEngine().getVariants().get(0).getModificationPoints().stream()
				.filter(e -> (e.getCodeElement().getPosition().getLine() == 198 && e.getCodeElement().getPosition()
						.getFile().getName().equals("UnivariateRealSolverUtils.java")))
				.findAny().get();
		assertNotNull(mp198);

		assertTrue(mp198.getCodeElement() instanceof CtIf);

		AstorOperator op = main1.getEngine().getOperatorSpace().getOperators().stream()
				.filter(e -> e instanceof VarReplacementByAnotherVarOp).findFirst().get();

		main1.getEngine().getOperatorSpace().getOperators().clear();
		main1.getEngine().getOperatorSpace().getOperators().add(op);
		main1.getEngine().getVariants().get(0).getModificationPoints().clear();
		main1.getEngine().getVariants().get(0).getModificationPoints().add(mp198);

		main1.getEngine().getSolutions().clear();

		command.command.put("-stopfirst", "false");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-maxgen", "1000");
		MetaEvalTOSApproach.MAX_GENERATIONS = 1000;
		main1.getEngine().startEvolution();
		// assertTrue(main1.getEngine().getSolutions().size() > 0);
		main1.getEngine().atEnd();

	}

	@Test
	public void test_doomyTry_1() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testTrySimple1").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "true");
		command.command.put("-maxgen", "1000000");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-try");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "INFO");
		command.command.put("-flthreshold", "0.24");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		assertTrue(main1.getEngine().getSolutions().size() > 0);

		List<ProgramVariant> solutionTry = main1.getEngine().getSolutions().stream()
				.filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof WrapwithTrySingleStatementOp).findAny()
						.isPresent())
				.collect(Collectors.toList());

		ProgramVariant solution1 = solutionTry.get(0);

		assertTrue(solution1.getAllOperations().stream().filter(e -> e.getModified().toString().startsWith("try {"))
				.findFirst().isPresent());

		assertTrue(solution1.getPatchDiff().getOriginalStatementAlignmentDiff().contains("+			try {"));
		assertTrue(solution1.getPatchDiff().getOriginalStatementAlignmentDiff()
				.contains("catch (java.lang.Exception e) {}"));

	}

	// Math_77, Math_104, Math_75, Math_22, MATH_62.

	@Test
	public void testBT_Math22() throws Exception {
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math3.distribution.FDistributionTest:org.apache.commons.math3.distribution.UniformRealDistributionTest",
				"-location", new File("./examples/math_22").getAbsolutePath(), "-package", "org.apache.commons",
				"-srcjavafolder", "src/main/java/", "-srctestfolder", "src/test/java/", "-binjavafolder",
				"target/classes/", "-bintestfolder", "/target/test-classes", "-stopfirst", "true", "-maxgen", "200",
				"-scope", "package", };

		CommandSummary command = new CommandSummary(args);

		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "true");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");

		command.command.put("-id", "test-math85");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-flthreshold", "0.24");

		// [-mode custom -location . -id Math -dependencies
		// /tmp/math_22_custom_package_s1/target/classes/:/tmp/math_22_custom_package_s1/target/test-classes/:/tmp/math_22_custom_package_s1/lib/commons-discovery-0.5.jar:/home/mmartinez/scriptsAstorOnG5k/src/python/core/../../..//libs/astor.jar
		// -failing
		// org.apache.commons.math3.distribution.FDistributionTest:org.apache.commons.math3.distribution.UniformRealDistributionTest:
		// -package org.apache.commons -jvm4testexecution
		// /home/mmartinez/jdk1.7.0_80/bin/ -jvm4evosuitetestexecution
		// /home/mmartinez/jdk1.8.0_45/bin/ -javacompliancelevel 5 -maxgen 1000000 -seed
		// 1 -scope package -population 1 -srcjavafolder src/main/java/ -srctestfolder
		// src/test/java/ -binjavafolder target/classes/ -bintestfolder
		// target/test-classes/ -parameters
		// limitbysuspicious:false:maxtime:180:maxGeneration:100000000:disablelog:true:maxnumbersolutions:100:stopfirst:false:loglevel:info:runjava7code:true:executorjar:/home/mmartinez/scriptsAstorOnG5k/src/python/core/../../../libs/jtestex7.jar:customengine:fr.inria.astor.approaches.tos.core.evalTos.MetaEvalTOSApproach]

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		// assertTrue(main1.getEngine().getSolutions().size() > 0);

	}

	// [-mode custom -location . -id Math -dependencies
	// /tmp/math_75_custom_package_s1/target/classes/:/tmp/math_75_custom_package_s1/target/test-classes/:/tmp/math_75_custom_package_s1/lib/commons-discovery-0.5.jar:/home/mmartinez/scriptsAstorOnG5k/src/python/core/../../..//libs/astor.jar
	// -failing org.apache.commons.math.stat.FrequencyTest: -package
	// org.apache.commons -jvm4testexecution /home/mmartinez/jdk1.7.0_80/bin/
	// -jvm4evosuitetestexecution /home/mmartinez/jdk1.8.0_45/bin/
	// -javacompliancelevel 5 -maxgen 1000000 -seed 1 -scope package -population 1
	// -srcjavafolder src/main/java/ -srctestfolder src/test/java/ -binjavafolder
	// target/classes/ -bintestfolder target/test-classes/ -parameters
	// limitbysuspicious:false:maxtime:180:maxGeneration:100000000:disablelog:true:maxnumbersolutions:100:stopfirst:false:loglevel:info:runjava7code:true:executorjar:/home/mmartinez/scriptsAstorOnG5k/src/python/core/../../../libs/jtestex7.jar:customengine:fr.inria.astor.approaches.tos.core.evalTos.MetaEvalTOSApproach]

	@Test
	public void testBT_Math75() throws Exception {
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath() + File.pathSeparator
				+ new File("./examples/libs/commons-discovery-0.5.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.stat.FrequencyTest", "-location",
				new File("./examples/math_75").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"src/main/java/", "-srctestfolder", "src/test/java/", "-binjavafolder", "target/classes/",
				"-bintestfolder", "/target/test-classes", "-stopfirst", "true", "-maxgen", "200", "-scope",
				"package", };

		CommandSummary command = new CommandSummary(args);

		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "true");
		command.command.put("-maxgen", "10000");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");

		command.command.put("-id", "test-math85");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-flthreshold", "1");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

	}

}
