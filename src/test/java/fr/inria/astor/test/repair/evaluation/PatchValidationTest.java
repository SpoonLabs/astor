package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.loop.AstorCoreEngine;
import fr.inria.astor.util.ProcessUtil;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
/**
 * This test cases aims at validating the mechanism of patch validation 
 * (We manually generate the candidate patches)
 * @author Matias Martinez
 *
 */
public class PatchValidationTest {

	protected Logger log = Logger.getLogger(PatchValidationTest.class.getName());
	
	@Test
	@Ignore
	public void testPatchMath0C1() throws Exception {
		
		log.debug("\nInit test with one failing TC");
		
		String dependenciespath = new File("./examples/Math-0c1ef/lib/junit-4.11.jar").getAbsolutePath() 
				+ File.pathSeparator
				+ new File("./examples/Math-0c1ef/lib/hamcrest-core-1.3.jar").getAbsolutePath();
		String projectId = "Math-0c1ef";
		String failing = "org.apache.commons.math3.primes.PrimesTest";
		File exampleLocation = new File("./examples/Math-0c1ef/");
		String location = exampleLocation.getAbsolutePath();
		String packageToInstrument = "org.apache.commons";
		double thfl = 0.5;
		
		String[] command = new String[]{"-dependencies",dependenciespath,"-location",location,
				"-flthreshold",Double.toString(thfl),
				"-package",packageToInstrument,
				"-failing", failing,
				"-id",projectId,
				"-population","1"};
		

		int processBeforeAll = ProcessUtil.currentNumberProcess();

		AstorMain main = new AstorMain();
		
		boolean correctArguments = main.processArguments(command);
		assertTrue(correctArguments);
		
		main.initProject(location, projectId, dependenciespath, packageToInstrument, thfl, failing);

		AstorCoreEngine astor = main.createEngine(ExecutionMode.jGenProg);
		
		assertTrue("Wrong engine created",astor instanceof JGenProg);
		
		JGenProg jgp = (JGenProg) astor;
		
		Assert.assertEquals(1, astor.getVariants().size());

		ProgramVariant variant = astor.getVariants().get(0);
	
		int currentGeneration = 1;
		OperatorInstance operation1 = createDummyOperation1(variant, currentGeneration);
		System.out.println("operation " + operation1);
		assertNotNull(operation1);

		boolean isSolution = false;
		isSolution = astor.processCreatedVariant(variant, currentGeneration);
		// The model has not been changed.
		assertFalse("Any solution was expected here", isSolution);

		int afterFirstValidation = ProcessUtil.currentNumberProcess();
		
		astor.applyNewOperationsToVariantModel(variant, currentGeneration);

		
		isSolution = astor.processCreatedVariant(variant, currentGeneration);

		int afterPatchValidation = ProcessUtil.currentNumberProcess();

		assertTrue("The variant must be a solution",isSolution);

		System.out.println("\nSolutions:\n" + astor.getSolutionData(astor.getVariants(), 1));

		astor.prepareNextGeneration(astor.getVariants(), 1);

		assertNotNull("Any solution found",astor.getSolutions());

		assertFalse("Solution set must be not empty",astor.getSolutions().isEmpty());

		assertEquals("Problems with number of process", processBeforeAll, afterFirstValidation);

		assertEquals("Problems with number of  process", processBeforeAll, afterPatchValidation);

		String printed = jgp.getIngredientStrategy().toString();
		Assert.assertNotNull(printed);
	}

	@Test
	@Ignore
	public void testPatchMath0C1TwoFailing() throws Exception {
		log.debug("\nInit test with two failing TC");
		String dependenciespath = new File("./examples/Math-0c1ef/lib/junit-4.11.jar").getAbsolutePath() 
				+ File.pathSeparator
				+ new File("./examples/Math-0c1ef/lib/hamcrest-core-1.3.jar").getAbsolutePath();
		String folder = "Math-0c1ef";
		// Only the first one fails
		String failing = "org.apache.commons.math3.primes.PrimesTest" + File.pathSeparator
				+ "org.apache.commons.math3.random.BitsStreamGeneratorTest";

		File projectId = new File("./examples/Math-0c1ef/");
		String location = projectId.getAbsolutePath();
		String packageToInstrument = "org.apache.commons";
		double thfl = 0.5;
		
		String[] command = new String[]{"-dependencies",dependenciespath,"-location",location,
				"-flthreshold",Double.toString(thfl),
				"-package",packageToInstrument,
				"-failing", failing,
				"-id",projectId.getName(),
				"-population","1"};
		

		AstorMain main = new AstorMain();
		
		boolean correctArguments = main.processArguments(command);
		assertTrue("Problems with arguments",correctArguments);
		
		main.initProject(location, folder, dependenciespath, packageToInstrument, thfl, failing);

		AstorCoreEngine astor = main.createEngine(ExecutionMode.jGenProg);
		
		Assert.assertEquals(1, astor.getVariants().size());

		ProgramVariant variant = astor.getVariants().get(0);
		
		int currentGeneration = 1;
		OperatorInstance operation1 = createDummyOperation1(variant, currentGeneration);
		assertNotNull(operation1);

		boolean isSolution = false;
	
		astor.applyNewOperationsToVariantModel(variant, currentGeneration);
		
		isSolution = astor.processCreatedVariant(variant, currentGeneration);
		assertTrue("A solution is attended",isSolution);

	}
	private OperatorInstance createDummyOperation1(ProgramVariant variant, int currentGeneration) {

		SuspiciousModificationPoint genSusp = searchSuspiciousElement(variant, "n += 3", " ", 93);
		assertNotNull(genSusp);

		CtElement targetStmt = genSusp.getCodeElement();
		CtElement fix = createFix1();
		assertEquals(fix.toString(), "n += 2");

		OperatorInstance operation = new OperatorInstance();

		operation.setOperationApplied(new ReplaceOp());
		operation.setModificationPoint(genSusp);
		operation.setParentBlock((CtBlock) targetStmt.getParent());
		operation.setOriginal(targetStmt);
		operation.setModified(fix);

		variant.putModificationInstance(currentGeneration, operation);
		operation.setModificationPoint(genSusp);

		return operation;
	}

	@Test
	public void testCreateFix1() {
		assertEquals(createFix1().toString(), "n += 2");
	}

	public CtElement createFix1() {
		CtElement gen = createPatchStatementCode("int n=0; n += 2");
		CtElement fix = ((CtBlock) gen.getParent()).getStatement(1);
		return fix;
	}
	

	public CtStatement createPatchStatementCode(String snippet) {

		Factory factory = new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());
		CtStatement st = (factory).Code().createCodeSnippetStatement(snippet).compile();
		return st;
	}

	public SuspiciousModificationPoint searchSuspiciousElement(ProgramVariant variant, String snippet, String fileName, int line) {

		for (ModificationPoint gen : variant.getModificationPoints()) {

			if (gen.getCodeElement().toString().equals(snippet) && gen.getCodeElement().getPosition().getLine() == line)
				return (SuspiciousModificationPoint) gen;
		}

		return null;
	}	
	
	
	
}
