package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;

import fr.inria.astor.approaches.tos.core.evalTos.IPredictor;
import fr.inria.astor.approaches.tos.core.evalTos.MultiMetaEvalTOSApproach;
import fr.inria.astor.approaches.tos.core.evalTos.Prediction;
import fr.inria.astor.approaches.tos.core.evalTos.PredictionElement;
import fr.inria.astor.approaches.tos.core.evalTos.PredictionResult;
import fr.inria.astor.approaches.tos.operator.metaevaltos.ConstReplacementOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.LogicExpOperator;
import fr.inria.astor.approaches.tos.operator.metaevaltos.LogicRedOperator;
import fr.inria.astor.approaches.tos.operator.metaevaltos.MethodXMethodReplacementDiffNameOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.OperatorReplacementOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.VarReplacementByAnotherVarOp;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class MultiMetEngineSimpleProgramsTest {

	@Test
	public void test_doomy_Multi_1_hierarchy_with_target() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMultiMet/testMulti1").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MultiMetaEvalTOSApproach.class.getName());
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
		command.command.put("-id", "test-mr1a");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-flthreshold", "0.24");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		MultiMetaEvalTOSApproach.MAX_GENERATIONS = 1000;

		MultiMetaEvalTOSApproach approach = (MultiMetaEvalTOSApproach) main1.getEngine();

		ModificationPoint mp24 = approach.getVariants().get(0).getModificationPoints().stream()
				.filter(e -> (e.getCodeElement().getPosition().getLine() == 24
						&& e.getCodeElement().getPosition().getFile().getName().equals("MyBuggy.java")))
				.findAny().get();
		assertNotNull(mp24);

		Prediction prediction = new Prediction();

		approach.setPredictor(new IPredictor() {

			@Override
			public PredictionResult computePredictionsForModificationPoint(ModificationPoint iModifPoint) {
				// No prediction
				return null;
			}
		});

		CtElement binary = mp24.getCodeElement().getElements(e -> e.toString().equals("i2 > i1")).get(0);
		assertNotNull(binary);

		CtReturn rnt = (CtReturn) mp24.getCodeElement().getElements(e -> e.toString().equals("return i1")).get(0);
		assertNotNull(rnt);

		prediction.add(new PredictionElement(1, binary), new OperatorReplacementOp());
		prediction.add(new PredictionElement(2, rnt.getReturnedExpression()), new VarReplacementByAnotherVarOp());

		boolean isSolution = approach.analyzePrediction(approach.getVariants().get(0), mp24, 0, prediction);

		assertTrue(isSolution);

		approach.atEnd();

		List<ProgramVariant> solutions = approach.getSolutions();

		assertTrue(solutions.size() > 0);
		ProgramVariant solutions0 = solutions.get(0);
		assertEquals(2, solutions0.getAllOperations().size());

	}

	/**
	 * Two changes, one in a hierarchy of the other (but not inside). We do not
	 * specify the target.
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_doomy_Multi_1_hierarchy_without_target() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMultiMet/testMulti1").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MultiMetaEvalTOSApproach.class.getName());
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
		command.command.put("-id", "test-mr1b");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		MultiMetaEvalTOSApproach.MAX_GENERATIONS = 1000;

		MultiMetaEvalTOSApproach approach = (MultiMetaEvalTOSApproach) main1.getEngine();

		ModificationPoint mp24 = approach.getVariants().get(0).getModificationPoints().stream()
				.filter(e -> (e.getCodeElement().getPosition().getLine() == 24
						&& e.getCodeElement().getPosition().getFile().getName().equals("MyBuggy.java")))
				.findAny().get();
		assertNotNull(mp24);

		Prediction prediction = new Prediction();

		CtElement binary = mp24.getCodeElement().getElements(e -> e.toString().equals("i2 > i1")).get(0);
		assertNotNull(binary);

		CtReturn rnt = (CtReturn) mp24.getCodeElement().getElements(e -> e.toString().equals("return i1")).get(0);
		assertNotNull(rnt);

		prediction.add(new PredictionElement(1, binary), new OperatorReplacementOp());
		prediction.add(new PredictionElement(2, rnt.getReturnedExpression()), new VarReplacementByAnotherVarOp());

		boolean isSolution = approach.analyzePrediction(approach.getVariants().get(0), mp24, 0, prediction);

		assertTrue(isSolution);

		approach.atEnd();
	}

	/**
	 * Two changes, one in a hierarchy of the other (but not inside). We specify the
	 * target.
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_doomy_Multi_2_hierarchy_2_target() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMultiMet/testMulti2").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MultiMetaEvalTOSApproach.class.getName());
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
		command.command.put("-id", "test-mr2a");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		MultiMetaEvalTOSApproach.MAX_GENERATIONS = 1000;

		MultiMetaEvalTOSApproach approach = (MultiMetaEvalTOSApproach) main1.getEngine();

		ModificationPoint mp24 = approach.getVariants().get(0).getModificationPoints().stream()
				.filter(e -> (e.getCodeElement().getPosition().getLine() == 36
						&& e.getCodeElement().getPosition().getFile().getName().equals("MyBuggy.java")))
				.findAny().get();
		assertNotNull(mp24);
		assertTrue(mp24.getCodeElement() instanceof CtReturn);

		Prediction prediction = new Prediction();

		approach.setPredictor(new IPredictor() {

			@Override
			public PredictionResult computePredictionsForModificationPoint(ModificationPoint iModifPoint) {
				// No prediction
				return null;
			}
		});

		CtElement invocation = mp24.getCodeElement().getElements(e -> e.toString().equals("(myinst.toNegative(i1))"))
				.get(0);
		assertNotNull(invocation);
		assertTrue(invocation instanceof CtInvocation);
		// The var inside invocation
		CtElement vacc = invocation.getElements(e -> e.toString().equals("i1")).get(0);
		assertNotNull(vacc);

		prediction.add(new PredictionElement(1, invocation), new MethodXMethodReplacementDiffNameOp());
		prediction.add(new PredictionElement(2, vacc), new VarReplacementByAnotherVarOp());

		boolean isSolution = approach.analyzePrediction(approach.getVariants().get(0), mp24, 0, prediction);

		assertTrue(isSolution);

		approach.atEnd();

		List<ProgramVariant> solutions = approach.getSolutions();

		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());
		ProgramVariant solutions0 = solutions.get(0);
		assertEquals(2, solutions0.getAllOperations().size());

		assertTrue(solutions0.getAllOperations().stream()
				.filter(e -> e.getModified().toString().startsWith("(myinst.toPositive(i2))")).findFirst().isPresent());

		assertTrue(solutions0.getPatchDiff().getOriginalStatementAlignmentDiff()
				.contains("-			return (myinst.toPositive(i1)) * (myinst.toNegative(i1))"));

		assertTrue(solutions0.getPatchDiff().getOriginalStatementAlignmentDiff()
				.contains("+			return (myinst.toPositive(i1)) * (myinst.toPositive(i2))"));

	}

	/**
	 * Two changes, one inside the other.
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_doomy_2_hiera_changes() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMultiMet/testMulti3").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MultiMetaEvalTOSApproach.class.getName());
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
		command.command.put("-id", "test-mr3a");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "");
		command.command.put("-parameters", "metamustclone:false");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		MultiMetaEvalTOSApproach.MAX_GENERATIONS = 1000;

		MultiMetaEvalTOSApproach approach = (MultiMetaEvalTOSApproach) main1.getEngine();

		ModificationPoint mp30 = approach.getVariants().get(0).getModificationPoints().stream()
				.filter(e -> (e.getCodeElement().getPosition().getLine() == 30
						&& e.getCodeElement().getPosition().getFile().getName().equals("MyBuggy.java")))
				.findAny().get();
		assertNotNull(mp30);
		assertTrue(mp30.getCodeElement() instanceof CtIf);

		Prediction prediction = new Prediction();

		approach.setPredictor(new IPredictor() {

			@Override
			public PredictionResult computePredictionsForModificationPoint(ModificationPoint iModifPoint) {
				// No prediction
				return null;
			}
		});

		CtIf ifexp = (CtIf) mp30.getCodeElement();

		CtBinaryOperator binif = (CtBinaryOperator) ifexp.getCondition();

		assertEquals("i1 > i1", binif.toString());

		// the second one is buggy
		List<CtElement> vars = mp30.getCodeElement()
				.getElements(e -> e instanceof CtVariableAccess && e.toString().equals("i1"));
		// var in 1 is that one in the right part of the assignment.
		CtElement vacc = vars.get(1);
		assertNotNull(vacc);

		prediction.add(new PredictionElement(1, vacc), new VarReplacementByAnotherVarOp());

		prediction.add(new PredictionElement(2, binif), new LogicExpOperator());

		boolean isSolution = approach.analyzePrediction(approach.getVariants().get(0), mp30, 0, prediction);

		assertTrue(isSolution);

		approach.atEnd();

		List<ProgramVariant> solutions = approach.getSolutions();

		assertTrue(solutions.size() > 0);
		ProgramVariant solutions0 = solutions.get(0);
		assertEquals(2, solutions0.getAllOperations().size());

		boolean exists = true;

		for (ProgramVariant programVariant : solutions) {
			exists = true;
			exists &= programVariant.getAllOperations().stream()
					.filter(e -> e.getModified().toString().startsWith("(i1 > i2) || i1 == i2")
							&& e.getOriginal().toString().equals("i1 > i1"))
					.findFirst().isPresent();

			exists &= programVariant.getPatchDiff().getOriginalStatementAlignmentDiff()
					.contains("+			if ((i1 > i2) || i1 == i2)");

			exists &= programVariant.getPatchDiff().getOriginalStatementAlignmentDiff()
					.contains("-			if (i1 > i1)");

			if (exists)
				break;
		}
		assertTrue(exists);

	}

	/**
	 * The analyze the subject from original Meta
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_Expr_Exp_1_VAR_REP_multimeta() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testExprExp1").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MultiMetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "1110");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-expr_exp1");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		MultiMetaEvalTOSApproach.MAX_GENERATIONS = 1000;

		MultiMetaEvalTOSApproach approach = (MultiMetaEvalTOSApproach) main1.getEngine();
		approach.getOperatorSpace().getOperators().removeIf(e -> !(e instanceof LogicExpOperator));

		approach.getVariants().get(0).getModificationPoints()
				.removeIf(e -> !((e.getCodeElement().getPosition().getLine() == 24 // or 27
						&& e.getCodeElement().getPosition().getFile().getName().equals("MyBuggy.java"))));

		approach.startEvolution();

		approach.atEnd();

		assertTrue(main1.getEngine().getSolutions().size() > 0);

		List<ProgramVariant> solutionVarByVar1 = main1.getEngine().getSolutions();

		boolean exists = true;
		for (ProgramVariant programVariant : solutionVarByVar1) {
			exists = true;

			exists &= programVariant.getAllOperations().stream()
					.filter(e -> e.getModified().toString().equals("(i2 > i1) || i1 == i2")
							&& e.getOriginal().toString().equals("i2 > i1"))
					.findFirst().isPresent();

			exists &= programVariant.getPatchDiff().getOriginalStatementAlignmentDiff()
					.contains("+			if ((i2 > i1) || i1 == i2)");

			if (exists)
				break;
		}
		assertTrue(exists);

	}

	@Test
	public void test_Reduce_single_change_binary_1() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testBinaryExprReduce1").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MultiMetaEvalTOSApproach.class.getName());
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
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		MultiMetaEvalTOSApproach.MAX_GENERATIONS = 1000;

		MultiMetaEvalTOSApproach approach = (MultiMetaEvalTOSApproach) main1.getEngine();
		approach.getOperatorSpace().getOperators().removeIf(e -> !(e instanceof LogicRedOperator));

		approach.startEvolution();

		approach.atEnd();

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
		// Check undo
		assertTrue(solution1.get().getAllOperations().get(0).getModificationPoint().getCodeElement().toString()
				.contains("if ((i2 >= i1) || (i1 > i2)) "));
	}

	@Test
	public void test_Reduce_single_change_binary_2() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testBinaryExprReduce2").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MultiMetaEvalTOSApproach.class.getName());
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
		command.command.put("-id", "test-reduce-bin-1");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		MultiMetaEvalTOSApproach.MAX_GENERATIONS = 1000;

		MultiMetaEvalTOSApproach approach = (MultiMetaEvalTOSApproach) main1.getEngine();
		approach.getOperatorSpace().getOperators().removeIf(e -> !(e instanceof LogicRedOperator));

		approach.startEvolution();

		approach.atEnd();

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

		// Check undo
		assertTrue(solution1.get().getAllOperations().get(0).getModificationPoint().getCodeElement().toString()
				.contains("(i2 >= i1) || ((i1 == i2) || (i1 > i2))"));
	}

	/**
	 * Two changes in the hierarchy but not inside the other.
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_Reduce_VM_change_binary_2() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMultiMet/testBinaryExprReduceVM2").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MultiMetaEvalTOSApproach.class.getName());
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
		command.command.put("-id", "test-reduce-bin-1");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		MultiMetaEvalTOSApproach.MAX_GENERATIONS = 1000;

		MultiMetaEvalTOSApproach approach = (MultiMetaEvalTOSApproach) main1.getEngine();

		approach.getVariants().get(0).getModificationPoints()
				.removeIf(e -> !((e.getCodeElement().getPosition().getLine() == 24
						&& e.getCodeElement().getPosition().getFile().getName().equals("MyBuggy.java"))));

		ModificationPoint mp24 = approach.getVariants().get(0).getModificationPoints().get(0);

		// approach.startEvolution();

		Prediction prediction = new Prediction();

		approach.setPredictor(new IPredictor() {

			@Override
			public PredictionResult computePredictionsForModificationPoint(ModificationPoint iModifPoint) {
				// No prediction
				return null;
			}
		});

		CtElement binary = mp24.getCodeElement()
				.getElements(e -> (e.toString().equals("((i1 == i2) || (i1 > i2))") && e instanceof CtBinaryOperator))
				.get(0);
		assertNotNull(binary);
		assertTrue(binary instanceof CtBinaryOperator);
		// The var inside invocation
		CtElement vacc = mp24.getCodeElement()
				.getElements(e -> (e.toString().equals("i1") && e instanceof CtVariableAccess)
						&& e.getParent().toString().equals("(i1 >= i1)"))
				.get(0);
		assertNotNull(vacc);

		assertTrue(vacc.getParent().toString().equals("(i1 >= i1)"));

		prediction.add(new PredictionElement(1, binary), new LogicRedOperator());
		prediction.add(new PredictionElement(2, vacc), new VarReplacementByAnotherVarOp());

		boolean isSolution = approach.analyzePrediction(approach.getVariants().get(0), mp24, 0, prediction);

		assertTrue(isSolution);

		approach.atEnd();

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
						.filter(e -> e.getModified().toString().equals("(i1 == i2)")
								&& e.getOriginal().toString().equals("((i1 == i2) || (i1 > i2))"))
						.findFirst().isPresent())
				.findFirst();
		assertTrue(solution1.isPresent());

		assertTrue(solution1.get().getPatchDiff().getOriginalStatementAlignmentDiff()
				.contains("+			if ((i2 >= i1) || (i1 == i2))"));

		// Check undo
		assertTrue(solution1.get().getAllOperations().get(0).getModificationPoint().getCodeElement().toString()
				.contains("(i1 >= i1) || ((i1 == i2) || (i1 > i2))"));
	}

	/**
	 * Two changes in the hierarchy, one inside the other
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_Reduce_VM_change_binary_2b() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location",
				new File("./examples/testMultiMet/testBinaryExprReduceVM2b").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MultiMetaEvalTOSApproach.class.getName());
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
		command.command.put("-id", "test-reduce-bin-1");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "true");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		MultiMetaEvalTOSApproach.MAX_GENERATIONS = 1000;

		MultiMetaEvalTOSApproach approach = (MultiMetaEvalTOSApproach) main1.getEngine();

		approach.getVariants().get(0).getModificationPoints()
				.removeIf(e -> !((e.getCodeElement().getPosition().getLine() == 24
						&& e.getCodeElement().getPosition().getFile().getName().equals("MyBuggy.java"))));

		ModificationPoint mp24 = approach.getVariants().get(0).getModificationPoints().get(0);

		// approach.startEvolution();

		Prediction prediction = new Prediction();

		approach.setPredictor(new IPredictor() {

			@Override
			public PredictionResult computePredictionsForModificationPoint(ModificationPoint iModifPoint) {
				// No prediction
				return null;
			}
		});

		CtElement binaryToReduce = mp24.getCodeElement()
				.getElements(e -> (e.toString().equals("((100 == i2) || (i1 > i2))") && e instanceof CtBinaryOperator))
				.get(0);
		assertNotNull(binaryToReduce);
		assertTrue(binaryToReduce instanceof CtBinaryOperator);
		// The var inside invocation
		CtElement constant = binaryToReduce.getElements(e -> (e.toString().contains("100") && e instanceof CtLiteral)
				&& e.getParent().toString().equals("(100 == i2)")).get(0);
		assertNotNull(constant);

		assertTrue(constant.getParent().toString().equals("(100 == i2)"));

		prediction.add(new PredictionElement(1, constant), new ConstReplacementOp());
		prediction.add(new PredictionElement(2, binaryToReduce), new LogicRedOperator());

		boolean isSolution = approach.analyzePrediction(approach.getVariants().get(0), mp24, 0, prediction);

		assertTrue(isSolution);

		approach.atEnd();

		assertTrue(main1.getEngine().getSolutions().size() > 0);

		List<ProgramVariant> solutionReductionLogic1 = main1.getEngine().getSolutions().stream()
				.filter(e -> e.getAllOperations().stream()
						.filter(o -> o.getOperationApplied() instanceof LogicRedOperator).findAny().isPresent())
				.collect(Collectors.toList());

		assertTrue(solutionReductionLogic1.size() > 0);

		Optional<ProgramVariant> solution0 = solutionReductionLogic1.stream()
				.filter(soli -> soli.getAllOperations().stream()
						.filter(e -> e.getModified().toString().equals("(100 == i2)")
								&& e.getOriginal().toString().equals("((100 == i2) || (i1 > i2))"))
						.findFirst().isPresent())
				.findFirst();
		assertTrue(solution0.isPresent());

		assertTrue(solution0.get().getPatchDiff().getOriginalStatementAlignmentDiff()
				.contains("+			if ((i2 >= i1) || (i1 == i2))"));

		assertTrue(solutionReductionLogic1.size() > 0);

		// Check undo
		assertTrue(solution0.get().getAllOperations().get(0).getModificationPoint().getCodeElement().toString()
				.contains("(i2 >= i1) || ((100 == i2) || (i1 > i2))"));
	}

}
