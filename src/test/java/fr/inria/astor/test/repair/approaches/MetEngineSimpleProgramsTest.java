package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.approaches.tos.core.evalTos.MetaEvalTOSApproach;
import fr.inria.astor.approaches.tos.operator.metaevaltos.ConstReplacementOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.LogicExpOperator;
import fr.inria.astor.approaches.tos.operator.metaevaltos.LogicRedOperator;
import fr.inria.astor.approaches.tos.operator.metaevaltos.MethodXMethodReplacementArgumentRemoveOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.MethodXMethodReplacementDiffArgumentsOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.MethodXMethodReplacementDiffNameOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.MethodXVariableReplacementOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.OperatorReplacementOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.UnwrapfromIfOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.UnwrapfromMethodCallOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.VarReplacementByAnotherVarOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.VarReplacementByMethodCallOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.WrapwithIfNullCheck;
import fr.inria.astor.approaches.tos.operator.metaevaltos.WrapwithIfOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.WrapwithTrySingleStatementOp;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class MetEngineSimpleProgramsTest {

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
		command.command.put("-stopfirst", "false");
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

	@Test
	public void test_doomyIf_1() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testIfSimple1").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "1000000");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-if1");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "INFO");
		command.command.put("-flthreshold", "0.24");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		assertTrue(main1.getEngine().getSolutions().size() > 0);

		List<ProgramVariant> solutionsIfCheck = main1
				.getEngine().getSolutions().stream().filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof WrapwithIfOp).findAny().isPresent())
				.collect(Collectors.toList());

		assertEquals(2, main1.getEngine().getSolutions().size());

		// Solution 1
		ProgramVariant solution1 = solutionsIfCheck.get(0);

		assertTrue(solution1.getAllOperations().stream()
				.filter(e -> e.getModified().toString().startsWith("if (1 == 0")).findFirst().isPresent());

		assertTrue(solution1.getPatchDiff().getOriginalStatementAlignmentDiff().contains("+			if (1 == 0)"));

		// Solution 2
		ProgramVariant solution2 = solutionsIfCheck.get(1);

		assertTrue(solution2.getAllOperations().stream()
				.filter(e -> e.getModified().toString().startsWith("if (i2 == 1)")).findFirst().isPresent());

		assertTrue(solution2.getPatchDiff().getOriginalStatementAlignmentDiff().contains("+			if (i2 == 1)"));

	}

	@Test
	public void test_doomy_if_NullCheck_NE_1() throws Exception {
		test_NullCheck_NE_cases("./examples/testMet/testIfNullCheck1");
	}

	@Test
	public void test_doomy_if_NullCheck_NE_2() throws Exception {
		test_NullCheck_NE_cases("./examples/testMet/testIfNullCheck2");
	}

	@Test
	public void testMethodReplaceByVar1() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testMethodReplaceByVar1").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "0");
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

		main1.getEngine().getOperatorSpace().getOperators().removeIf(e -> !(e instanceof MethodXVariableReplacementOp));

		MetaEvalTOSApproach.MAX_GENERATIONS = 1000;

		main1.getEngine().startEvolution();
		main1.getEngine().atEnd();

		assertTrue(main1.getEngine().getSolutions().size() > 0);

		List<ProgramVariant> solutionsIfCheck = main1.getEngine().getSolutions().stream()
				.filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof MethodXVariableReplacementOp).findAny()
						.isPresent())
				.collect(Collectors.toList());

		assertNotNull(solutionsIfCheck);
		assertFalse(solutionsIfCheck.isEmpty());

		ProgramVariant solution1 = solutionsIfCheck.get(0);

		assertTrue(solution1.getAllOperations().stream().filter(e -> e.getModified().toString().startsWith("i2"))
				.findFirst().isPresent());
		assertTrue(solution1.getAllOperations().stream()
				.filter(e -> e.getOriginal().toString().startsWith("(toNegative(i2))")).findFirst().isPresent());

		assertTrue(solution1.getPatchDiff().getOriginalStatementAlignmentDiff().contains("+			return i1 + i2"));

	}

	private void test_NullCheck_NE_cases(String path) throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File(path).getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
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

		List<ProgramVariant> solutionsIfCheck = main1.getEngine().getSolutions().stream()
				.filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof WrapwithIfNullCheck).findAny().isPresent())
				.collect(Collectors.toList());

		assertNotNull(solutionsIfCheck);
		assertFalse(solutionsIfCheck.isEmpty());

		ProgramVariant solution1 = solutionsIfCheck.get(0);

		assertTrue(solution1.getAllOperations().stream()
				.filter(e -> e.getModified().toString().startsWith("if (type != null)")).findFirst().isPresent());

		assertTrue(solution1.getPatchDiff().getOriginalStatementAlignmentDiff().contains("+		if (type != null)"));

		List<ProgramVariant> solutionTry = main1.getEngine().getSolutions().stream()
				.filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof WrapwithTrySingleStatementOp).findAny()
						.isPresent())
				.collect(Collectors.toList());

		assertNotNull(solutionTry);
		assertFalse(solutionTry.isEmpty());

		List<ProgramVariant> solutionIfPrec = main1
				.getEngine().getSolutions().stream().filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof WrapwithIfOp).findAny().isPresent())
				.collect(Collectors.toList());

		assertNotNull(solutionIfPrec);
		assertFalse(solutionIfPrec.isEmpty());

	}

	@Test
	public void test_doomy_Var_Replace_by_Method_1() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testVarReplaceByMethod1").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "1000000");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-var-by-method1");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "INFO");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		assertTrue(main1.getEngine().getSolutions().size() > 0);

		List<ProgramVariant> solutionVarByVar1 = main1.getEngine().getSolutions().stream()
				.filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof VarReplacementByMethodCallOp).findAny()
						.isPresent())
				.collect(Collectors.toList());

		assertTrue(solutionVarByVar1.size() > 0);

		Optional<ProgramVariant> solution1 = solutionVarByVar1
				.stream().filter(
						soli -> soli.getAllOperations().stream()
								.filter(e -> e.getModified().toString().equals("(getMinusOne())")
										&& e.getOriginal().toString().equals("(ONE)"))
								.findFirst().isPresent())
				.findFirst();
		assertTrue(solution1.isPresent());

	}

	@Test
	public void test_doomy_Var_Replace_by_Var_1() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testVarReplace1").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "1000000");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-var-by-var1");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "INFO");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		assertTrue(main1.getEngine().getSolutions().size() > 0);

		List<ProgramVariant> solutionVarByVar1 = main1.getEngine().getSolutions().stream()
				.filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof VarReplacementByAnotherVarOp).findAny()
						.isPresent())
				.collect(Collectors.toList());
		assertTrue("No solution with the target operator", solutionVarByVar1.size() > 0);

		ProgramVariant solution0 = solutionVarByVar1.get(0);
		assertTrue(solution0.getAllOperations().stream()
				.filter(e -> e.getModified().toString().equals("i2") && e.getOriginal().toString().equals("i1"))
				.findFirst().isPresent());

		assertTrue(solution0.getPatchDiff().getOriginalStatementAlignmentDiff().contains("+			return i2 + i1"));

		ProgramVariant solution1 = solutionVarByVar1.get(1);
		assertTrue(solution1.getAllOperations().stream()
				.filter(e -> e.getModified().toString().equals("i2") && e.getOriginal().toString().equals("i1"))
				.findFirst().isPresent());

		assertTrue(solution1.getPatchDiff().getOriginalStatementAlignmentDiff().contains("+			return i1 + i2"));

	}

	@Test
	public void test_doomy_Expr_Exp_1() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testExprExp1").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "1000000");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-expr_exp1");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "INFO");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		assertTrue(main1.getEngine().getSolutions().size() > 0);

		List<ProgramVariant> solutionVarByVar1 = main1.getEngine().getSolutions().stream()
				.filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof LogicExpOperator).findAny().isPresent())
				.collect(Collectors.toList());

		Optional<ProgramVariant> solution0 = solutionVarByVar1
				.stream().filter(
						soli -> soli.getAllOperations().stream()
								.filter(e -> e.getModified().toString().equals("(i2 > i1) || i2 != 1")
										&& e.getOriginal().toString().equals("i2 > i1"))
								.findFirst().isPresent())
				.findFirst();
		assertTrue(solution0.isPresent());

		assertTrue(solution0.get().getPatchDiff().getOriginalStatementAlignmentDiff()
				.contains("+			if ((i2 > i1) || i2 != 1)"));

		Optional<ProgramVariant> solution1 = solutionVarByVar1.stream().filter(soli -> soli.getAllOperations().stream()
				.filter(e -> e.getModified().toString().equals("(i2 > i1) || i1 == i2"// "(i1 > i2) || i1 == i2"
				) && e.getOriginal().toString().equals("i2 > i1"// "i1 > i2"
				)).findFirst().isPresent()).findFirst();
		assertTrue(solution1.isPresent());

		Optional<ProgramVariant> solution2 = solutionVarByVar1
				.stream().filter(
						soli -> soli.getAllOperations().stream()
								.filter(e -> e.getModified().toString().equals("(i1 > i2) || i1 == i2")
										&& e.getOriginal().toString().equals("i1 > i2"))
								.findFirst().isPresent())
				.findFirst();
		// assertTrue(solution2.isPresent());

		// assertTrue(solution1.get().getPatchDiff().getOriginalStatementAlignmentDiff().contains(//
		// "+ if ((i1 > i2) || i1
		// // == i2) "
		// "+ if ((i2 > i1) || i2 != 1)"));

	}

	@Test
	public void test_doomy_testWMRcase2a() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testWMRcase2a").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "1000000");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-wmr1c2");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "INFO");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		assertTrue(main1.getEngine().getSolutions().size() > 0);

		List<ProgramVariant> solutionVarByVar1 = main1.getEngine().getSolutions().stream()
				.filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof MethodXMethodReplacementDiffNameOp).findAny()
						.isPresent())
				.collect(Collectors.toList());

		assertTrue(solutionVarByVar1.size() > 0);

		assertTrue("No solution with the target operator", solutionVarByVar1.size() > 0);

		Optional<ProgramVariant> solution0 = solutionVarByVar1.stream()
				.filter(soli -> soli.getAllOperations().stream()
						.filter(e -> e.getModified().toString().equals("(toPositive(i2))")
								&& e.getOriginal().toString().equals("(toNegative(i2))"))
						.findFirst().isPresent())
				.findFirst();
		assertTrue(solution0.isPresent());

		assertTrue(solution0.get().getPatchDiff().getOriginalStatementAlignmentDiff()
				.contains("+			return (toPositive(i1)) * (toPositive(i2));"));

	}

	@Test
	public void test_doomy_testWMRcase2b() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testWMRcase2b").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "1000000");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-wmr1c2");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "INFO");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		assertTrue(main1.getEngine().getSolutions().size() > 0);

		List<ProgramVariant> solutionVarByVar1 = main1.getEngine().getSolutions().stream()
				.filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof MethodXMethodReplacementDiffNameOp).findAny()
						.isPresent())
				.collect(Collectors.toList());

		assertTrue(solutionVarByVar1.size() > 0);

		Optional<ProgramVariant> solution0 = solutionVarByVar1.stream()
				.filter(soli -> soli.getAllOperations().stream()
						.filter(e -> e.getModified().toString().equals("(myinst.toPositive(i2))")
								&& e.getOriginal().toString().equals("(myinst.toNegative(i2))"))
						.findFirst().isPresent())
				.findFirst();
		assertTrue(solution0.isPresent());

		assertTrue(solution0.get().getPatchDiff().getOriginalStatementAlignmentDiff()
				.contains("+			return (myinst.toPositive(i1)) * (myinst.toPositive(i2));"));

	}

	@Test
	public void test_doomy_testWMRcase2c() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testWMRcase2c").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "1000000");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-wmr1c2");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "INFO");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		assertTrue(main1.getEngine().getSolutions().size() > 0);

		List<ProgramVariant> solutionVarByVar1 = main1.getEngine().getSolutions().stream()
				.filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof VarReplacementByMethodCallOp).findAny()
						.isPresent())
				.collect(Collectors.toList());

		assertTrue(solutionVarByVar1.size() > 0);

		Optional<ProgramVariant> solution0 = solutionVarByVar1
				.stream().filter(
						soli -> soli.getAllOperations().stream()
								.filter(e -> e.getModified().toString().equals("type.toUpperCase()")
										&& e.getOriginal().toString().equals("type"))
								.findFirst().isPresent())
				.findFirst();
		assertTrue(solution0.isPresent());

		assertTrue(solution0.get().getPatchDiff().getOriginalStatementAlignmentDiff()
				.contains("+		if (\"gr\".toUpperCase().equals(type.toUpperCase())) {"));

	}

	@Test
	public void test_doomy_testWMRcase1() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testWMRcase1").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "1000000");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-wmr1");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "INFO");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		assertTrue(main1.getEngine().getSolutions().size() > 0);

		List<ProgramVariant> solutionVarByVar1 = main1.getEngine().getSolutions().stream()
				.filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof MethodXMethodReplacementArgumentRemoveOp)
						.findAny().isPresent())
				.collect(Collectors.toList());

		assertTrue(solutionVarByVar1.size() > 0);

		Optional<ProgramVariant> solution0 = solutionVarByVar1.stream()
				.filter(soli -> soli.getAllOperations().stream()
						.filter(e -> e.getModified().toString().equals("(myinst.toPositive(i2))")
								&& e.getOriginal().toString().equals("(myinst.toPositive(i2, 0))"))
						.findFirst().isPresent())
				.findFirst();
		assertTrue(solution0.isPresent());

		assertTrue(solution0.get().getPatchDiff().getOriginalStatementAlignmentDiff()
				.contains("+			return (myinst.toPositive(i1)) * (myinst.toPositive(i2));"));

	}

	@Test
	public void test_doomy_testWMRcase3() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testWMRcase3").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "1000000");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-wmr3");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "INFO");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		assertTrue(main1.getEngine().getSolutions().size() > 0);

		List<ProgramVariant> solutionVarByVar1 = main1.getEngine().getSolutions().stream()
				.filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof MethodXMethodReplacementDiffArgumentsOp)
						.findAny().isPresent())
				.collect(Collectors.toList());

		assertTrue(solutionVarByVar1.size() > 0);

		Optional<ProgramVariant> solution0 = solutionVarByVar1.stream()
				.filter(soli -> soli.getAllOperations().stream()
						.filter(e -> e.getModified().toString().equals("(myinst.toPositive(i2))")
								&& e.getOriginal().toString().equals("(myinst.toPositive(\"1\"))"))
						.findFirst().isPresent())
				.findFirst();
		assertTrue(solution0.isPresent());

		assertTrue(solution0.get().getPatchDiff().getOriginalStatementAlignmentDiff()
				.contains("+			return (myinst.toPositive(i1)) * (myinst.toPositive(i2));"));

	}

	@Test
	public void test_doomy_testConstantPerVar1() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testConstantPerVar1").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "1000000");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-wmr1c2");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "INFO");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		assertTrue(main1.getEngine().getSolutions().size() > 0);

		List<ProgramVariant> solutionVarByVar1 = main1.getEngine().getSolutions().stream()
				.filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof ConstReplacementOp).findAny().isPresent())
				.collect(Collectors.toList());

		assertTrue(solutionVarByVar1.size() > 0);

		Optional<ProgramVariant> solution0 = solutionVarByVar1.stream()
				.filter(soli -> soli.getAllOperations().stream()
						.filter(e -> e.getModified().toString().equals("(this.MINUS_ONE)")
								&& e.getOriginal().toString().equals("1"))
						.findFirst().isPresent())
				.findFirst();
		assertTrue(solution0.isPresent());

		assertTrue(solution0.get().getPatchDiff().getOriginalStatementAlignmentDiff()
				.contains("+			return n * (this.MINUS_ONE)"));

	}

	@Test
	public void test_doomy_testOperatorBinary1() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testOperatorBinary1").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "1000000");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-wmr1c2");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "INFO");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		assertTrue(main1.getEngine().getSolutions().size() > 0);

		List<ProgramVariant> solutionVarByVar1 = main1.getEngine().getSolutions().stream()
				.filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof OperatorReplacementOp).findAny().isPresent())
				.collect(Collectors.toList());

		assertTrue(solutionVarByVar1.size() > 0);

		Optional<ProgramVariant> solution0 = solutionVarByVar1
				.stream().filter(
						soli -> soli.getAllOperations().stream()
								.filter(e -> e.getModified().toString().equals("i2 >= i1")
										&& e.getOriginal().toString().equals("i2 > i1"))
								.findFirst().isPresent())
				.findFirst();
		assertTrue(solution0.isPresent());

		assertTrue(solution0.get().getPatchDiff().getOriginalStatementAlignmentDiff()
				.contains("+			if (i2 >= i1)"));

	}

	@Test
	@Ignore // we dont try to repair arithmetics
	public void test_doomy_testOperatorBinary2() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testOperatorBinary2").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "1000000");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-wmr1c2");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "INFO");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		assertTrue(main1.getEngine().getSolutions().size() > 0);

		List<ProgramVariant> solutionVarByVar1 = main1.getEngine().getSolutions().stream()
				.filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof OperatorReplacementOp).findAny().isPresent())
				.collect(Collectors.toList());

		assertTrue(solutionVarByVar1.size() > 0);

		Optional<ProgramVariant> solution0 = solutionVarByVar1
				.stream().filter(
						soli -> soli.getAllOperations().stream()
								.filter(e -> e.getModified().toString().equals("i1 * i2")
										&& e.getOriginal().toString().equals("i1 + i2"))
								.findFirst().isPresent())
				.findFirst();
		assertTrue(solution0.isPresent());

		// assertTrue(solution0.get().getPatchDiff().getOriginalStatementAlignmentDiff()
		// .contains("+ if (i2 >= i1)"));

	}

	@Test
	public void test_doomy_testUnwrapInvocation1_1() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testUnwrapInvocation1").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "1000000");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-unwr-invo1");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "INFO");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		assertTrue(main1.getEngine().getSolutions().size() > 0);

		List<ProgramVariant> solutionVarByVar1 = main1.getEngine().getSolutions().stream()
				.filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof UnwrapfromMethodCallOp).findAny().isPresent())
				.collect(Collectors.toList());

		assertTrue(solutionVarByVar1.size() > 0);

		Optional<ProgramVariant> solution0 = solutionVarByVar1.stream()
				.filter(soli -> soli.getAllOperations().stream()
						.filter(e -> e.getModified().toString().equals("i2")
								&& e.getOriginal().toString().equals("(toPositive(i2))"))
						.findFirst().isPresent())
				.findFirst();
		assertTrue(solution0.isPresent());

		assertTrue(solution0.get().getPatchDiff().getOriginalStatementAlignmentDiff().contains("return i1 * i2;"));

	}

	@Test
	public void test_doomy_testUnwraptry1() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testUnwrapTry1").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "1000000");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-unw-try1");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "INFO");
		command.command.put("-flthreshold", "0.01");
		command.command.put("-saveall", "true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
//TODO: problems with Gzoltar and try
		// assertTrue(main1.getEngine().getSolutions().size() > 0);

//		List<ProgramVariant> solutionVarByVar1 = main1.getEngine().getSolutions().stream()
//				.filter(e -> e.getAllOperations().stream()
//						.filter(o -> o.getOperationApplied() instanceof UnwrapfromMethodCallOp).findAny().isPresent())
//				.collect(Collectors.toList());
//
//		Optional<ProgramVariant> solution0 = solutionVarByVar1.stream()
//				.filter(soli -> soli.getAllOperations().stream()
//						.filter(e -> e.getModified().toString().equals("i2")
//								&& e.getOriginal().toString().equals("(toPositive(i2))"))
//						.findFirst().isPresent())
//				.findFirst();
//		assertTrue(solution0.isPresent());
//
//		assertTrue(solution0.get().getPatchDiff().getOriginalStatementAlignmentDiff().contains("return i1 * i2;"));

	}

	@Test
	public void test_doomy_testUnwrapIf1() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testUnwrapIf1").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "1000000");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-unwif");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "INFO");
		command.command.put("-flthreshold", "0.01");
		command.command.put("-saveall", "true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		assertTrue(main1.getEngine().getSolutions().size() > 0);

		List<ProgramVariant> solutionVarByVar1 = main1.getEngine().getSolutions().stream()
				.filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof UnwrapfromIfOp).findAny().isPresent())
				.collect(Collectors.toList());
		assertTrue(solutionVarByVar1.size() > 0);
		assertTrue("No solution with the target operator", solutionVarByVar1.size() > 0);

		Optional<ProgramVariant> solution0 = solutionVarByVar1.stream()
				.filter(soli -> soli.getAllOperations().stream()
						.filter(e -> e.getModified().toString().equals("result = i1 + i2;")
								&& e.getOriginal().toString().contains("if (type == null)"))
						.findFirst().isPresent())
				.findFirst();
		assertTrue(solution0.isPresent());

		assertTrue(solution0.get().getPatchDiff().getOriginalStatementAlignmentDiff()
				.contains("-		if (type == null)"));

	}

	@Test
	public void test_doomy_testReduce_binary_1() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testBinaryExprReduce1").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "1000000");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-reduce-bin-1");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "INFO");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		assertTrue(main1.getEngine().getSolutions().size() > 0);

		List<ProgramVariant> solutionReductionLogic1 = main1.getEngine().getSolutions().stream()
				.filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof LogicRedOperator).findAny().isPresent())
				.collect(Collectors.toList());

		assertTrue(solutionReductionLogic1.size() > 0);

		Optional<ProgramVariant> solution1 = solutionReductionLogic1.stream()
				.filter(soli -> soli.getAllOperations().stream()
						.filter(e -> e.getModified().toString().equals("i2 >= i1")
								&& e.getOriginal().toString().equals("(i2 >= i1) || (i1 > i2)"))
						.findFirst().isPresent())
				.findFirst();
		assertTrue(solution1.isPresent());

		assertTrue(solution1.get().getPatchDiff().getOriginalStatementAlignmentDiff()
				.contains("+			if (i2 >= i1)"));
	}

	@Test
	public void test_doomy_testReduce_binary_2() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testBinaryExprReduce2").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "1000000");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-reduce-bin-1");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "INFO");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		assertTrue(main1.getEngine().getSolutions().size() > 0);

		List<ProgramVariant> solutionReductionLogic1 = main1.getEngine().getSolutions().stream()
				.filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof LogicRedOperator).findAny().isPresent())
				.collect(Collectors.toList());

		assertTrue(solutionReductionLogic1.size() > 0);

		Optional<ProgramVariant> solution0 = solutionReductionLogic1.stream()
				.filter(soli -> soli.getAllOperations().stream()
						.filter(e -> e.getModified().toString().equals("(i1 == i2)")
								&& e.getOriginal().toString().equals("((i1 == i2) || (i1 > i2))"))
						.findFirst().isPresent())
				.findFirst();
		assertTrue(solution0.isPresent());

		assertTrue(solution0.get().getPatchDiff().getOriginalStatementAlignmentDiff()
				.contains("+			if ((i2 >= i1) || (i1 == i2))"));

		assertTrue(solutionReductionLogic1.size() > 0);

		Optional<ProgramVariant> solution1 = solutionReductionLogic1.stream()
				.filter(soli -> soli.getAllOperations().stream()
						.filter(e -> e.getModified().toString().equals("i2 >= i1")
								&& e.getOriginal().toString().equals("(i2 >= i1) || ((i1 == i2) || (i1 > i2))"))
						.findFirst().isPresent())
				.findFirst();
		assertTrue(solution1.isPresent());

		assertTrue(solution1.get().getPatchDiff().getOriginalStatementAlignmentDiff()
				.contains("+			if (i2 >= i1)"));
	}

}
