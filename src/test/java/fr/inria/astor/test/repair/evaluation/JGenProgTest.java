package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import fr.inria.astor.core.entities.Gen;
import fr.inria.astor.core.entities.GenOperationInstance;
import fr.inria.astor.core.entities.GenSuspicious;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.taxonomy.GenProgMutationOperation;
import fr.inria.astor.core.loop.evolutionary.JGenProg;
import fr.inria.astor.core.util.ProcessUtil;
import fr.inria.astor.core.util.TimeUtil;
import fr.inria.main.AbstractMain;
import fr.inria.main.evolution.MainjGenProg;

/**
 * This class executes the experiment from our paper
 * if-conditional-dataset-2014.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class JGenProgTest extends BaseEvolutionaryTest {

	@Override
	public void generic(String location, String folder, String regression, String failing, String dependenciespath,
			String packageToInstrument, double thfl) throws Exception {

		getMain().run(location, folder, dependenciespath, packageToInstrument, thfl, failing);

	}

	@Override
	public AbstractMain createMain() {
		if (main == null) {
			return new MainjGenProg();
		}
		return main;
	}

	@Test
	public void testExampleMath280() throws Exception {

		String dependenciespath = "examples/Math-issue-280/lib/junit-4.4.jar";
		String folder = "Math-issue-280";
		String failing = "org.apache.commons.math.distribution.NormalDistributionTest";
		File f = new File("examples/Math-issue-280/");
		String location = f.getParent();
		String regression = "org.apache.commons.math.distribution.NormalDistributionTest";
		String packageToInstrument = "org.apache.commons";
		double thfl = 0.5;

		this.generic(location, folder, regression, failing, dependenciespath, packageToInstrument, thfl);

	}

	@Test
	public void testExampleMath0C1() throws Exception {
		//Recompile the example project before executing it.
		String dependenciespath = "examples/Math-0c1ef/lib/junit-4.11.jar" + File.pathSeparator
				+ "examples/Math-0c1ef/lib/hamcrest-core-1.3.jar";
		String folder = "Math-0c1ef";
		String failing = "org.apache.commons.math3.primes.PrimesTest";
		File f = new File("examples/Math-0c1ef/");
		String location = f.getParent();
		String packageToInstrument = "org.apache.commons";
		double thfl = 0.5;

		this.generic(location, folder, "", failing, dependenciespath, packageToInstrument, thfl);

	}

	@Test
	public void testPatchMath0C1() throws Exception {
		//Recompile the example project before executing it.
		String dependenciespath = "examples/Math-0c1ef/lib/junit-4.11.jar" + File.pathSeparator
				+ "examples/Math-0c1ef/lib/hamcrest-core-1.3.jar";
		String folder = "Math-0c1ef";
		String failing = "org.apache.commons.math3.primes.PrimesTest";
		File f = new File("examples/Math-0c1ef/");
		String location = f.getAbsolutePath();// f.getParent();
		String packageToInstrument = "org.apache.commons";
		double thfl = 0.5;

		int processBeforeAll = ProcessUtil.currentNumberProcess();

		MainjGenProg main = new MainjGenProg();

		main.initProject(location, folder, dependenciespath, packageToInstrument, thfl, failing);

		JGenProg jgp = main.statementMode();

		Assert.assertEquals(1, jgp.getVariants().size());

		ProgramVariant variant = jgp.getVariants().get(0);
		//
		int currentGeneration = 1;
		GenOperationInstance operation1 = createDummyOperation1(variant, currentGeneration);
		System.out.println("operation " + operation1);
		assertNotNull(operation1);

		boolean isSolution = false;
		isSolution = jgp.processCreatedVariant(variant, currentGeneration);
		// The model has not been changed.
		assertFalse(isSolution);

		int afterFirstValidation = ProcessUtil.currentNumberProcess();
		//
		jgp.applyNewOperationsToVariantModel(variant, currentGeneration);

		//
		isSolution = jgp.processCreatedVariant(variant, currentGeneration);

		int afterPatchValidation = ProcessUtil.currentNumberProcess();
		
		assertTrue(isSolution);
		
		System.out.println("\nSolutions:\n" + jgp.getMutatorSupporter().getSolutionData(jgp.getVariants(), 1));
		

		jgp.prepareNextGeneration(jgp.getVariants(), 1);
		
		assertNotNull(jgp.getSolutions());
		
		assertFalse(jgp.getSolutions().isEmpty());
		
		
		assertEquals("Problems with process", processBeforeAll, afterFirstValidation);
		
		assertEquals("Problems with process", processBeforeAll, afterPatchValidation);



	}
	
	@Test
	public void testPatchMath0C1TwoFailing() throws Exception {
		//Recompile the example project before executing it.
		String dependenciespath = "examples/Math-0c1ef/lib/junit-4.11.jar" + File.pathSeparator
				+ "examples/Math-0c1ef/lib/hamcrest-core-1.3.jar";
		String folder = "Math-0c1ef";
		//Only the first one fails
		String failing = "org.apache.commons.math3.primes.PrimesTest"+
				File.pathSeparator+"org.apache.commons.math3.random.BitsStreamGeneratorTest";
		
		File f = new File("examples/Math-0c1ef/");
		String location = f.getAbsolutePath();
		String packageToInstrument = "org.apache.commons";
		double thfl = 0.5;

		int processBeforeAll = ProcessUtil.currentNumberProcess();

		MainjGenProg main = new MainjGenProg();

		main.initProject(location, folder, dependenciespath, packageToInstrument, thfl, failing);

		JGenProg jgp = main.statementMode();

		Assert.assertEquals(1, jgp.getVariants().size());

		ProgramVariant variant = jgp.getVariants().get(0);
		//
		int currentGeneration = 1;
		GenOperationInstance operation1 = createDummyOperation1(variant, currentGeneration);
		System.out.println("operation " + operation1);
		assertNotNull(operation1);

		boolean isSolution = false;
		//
		jgp.applyNewOperationsToVariantModel(variant, currentGeneration);
		//
		isSolution = jgp.processCreatedVariant(variant, currentGeneration);
		assertTrue(isSolution);

	}

	

	public GenSuspicious searchSuspiciousElement(ProgramVariant variant, String snippet, String fileName, int line) {

		for (Gen gen : variant.getGenList()) {

			if (gen.getRootElement().toString().equals(snippet) && gen.getRootElement().getPosition().getLine() == line)
				return (GenSuspicious) gen;
		}

		return null;
	}

	public CtStatement createPatchStatementCode(String snippet) {

		Factory factory = new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());
		CtStatement st = (factory).Code().createCodeSnippetStatement(snippet).compile();
		return st;
	}

	private GenOperationInstance createDummyOperation1(ProgramVariant variant, int currentGeneration) {

		GenSuspicious genSusp = searchSuspiciousElement(variant, "n += 3", " ", 93);//TODO: is 93 or 95
		assertNotNull(genSusp);

		CtElement targetStmt = genSusp.getRootElement();
		CtElement fix = createFix1();
		assertEquals(fix.toString(), "n += 2");

		GenOperationInstance operation = new GenOperationInstance();

		operation.setOperationApplied(GenProgMutationOperation.REPLACE);
		operation.setGen(genSusp);
		operation.setParentBlock((CtBlock) targetStmt.getParent());
		operation.setOriginal(targetStmt);
		operation.setModified(fix);

		variant.putGenOperation(currentGeneration, operation);
		operation.setGen(genSusp);

		return operation;
	}

	@Test
	public void testCreateFix1() {
		assertEquals(createFix1().toString(), "n += 2");
		// return fix;
	}

	public CtElement createFix1() {
		CtElement gen = createPatchStatementCode("int n=0; n += 2");
		CtElement fix = ((CtBlock) gen.getParent()).getStatement(1);
		// assertEquals(fix,"n += 2" );
		return fix;
	}

	@Test
	public void testHelpMain() throws Exception {
		MainjGenProg main1 = new MainjGenProg();
		main1.main(new String[] { "help" });
	}

	@SuppressWarnings("static-access")
	@Test
	public void testRunMain() throws Exception {
		MainjGenProg main1 = new MainjGenProg();
		main1.main(new String[] {
				"-dependencies",
				"examples/Math-0c1ef/lib/junit-4.11.jar" + File.pathSeparator
						+ "/home/matias/develop/code/astor/examples/Math-0c1ef/lib/hamcrest-core-1.3.jar",
				// "-id","tttMath-0c1ef",
				"-failing", "org.apache.commons.math3.primes.PrimesTest", "-location",
				"/home/matias/develop/code/astor/examples/Math-0c1ef", "-package", "org.apache.commons",
				"-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin" });

	}

	@SuppressWarnings("static-access")
	@Test
	public void testRunMain2() throws Exception {
		MainjGenProg main1 = new MainjGenProg();
		main1.main(new String[] {
				"-dependencies",
				"examples/Math-0c1ef/lib/junit-4.11.jar" + File.pathSeparator
						+ "/home/matias/develop/code/astor/examples/Math-0c1ef/lib/hamcrest-core-1.3.jar", "-id",
				"tttMath-0c1ef", "-failing", "org.apache.commons.math3.primes.PrimesTest", "-location",
				"/home/matias/develop/code/astor/examples/Math-0c1ef", "-package", "org.apache.commons", "-maxgen",
				"400", "-population", "2", "-saveall",

		// "-flthreshold","0.9"
		});

	}

	@SuppressWarnings("static-access")
	@Test
	public void testRunMainRemove() throws Exception {
		MainjGenProg main1 = new MainjGenProg();
		main1.main(new String[] {
				"-dependencies",
				"examples/Math-0c1ef/lib/junit-4.11.jar" + File.pathSeparator
						+ "/home/matias/develop/code/astor/examples/Math-0c1ef/lib/hamcrest-core-1.3.jar", "-id",
				"tttMath-0c1ef", "-failing", "org.apache.commons.math3.primes.PrimesTest", "-location",
				"/home/matias/develop/code/astor/examples/Math-0c1ef", "-package", "org.apache.commons", "-maxgen",
				"400", "-population", "2", "-saveall", "-mode", "statement-remove"
		// "-flthreshold","0.9"
		});

	}

	@SuppressWarnings("static-access")
	@Test
	public void testRunMainTime() throws Exception {

		Date init = new Date();
		MainjGenProg main1 = new MainjGenProg();
		main1.main(new String[] {
				"-dependencies",
				"examples/Math-0c1ef/lib/junit-4.11.jar" + File.pathSeparator
						+ "/home/matias/develop/code/astor/examples/Math-0c1ef/lib/hamcrest-core-1.3.jar", "-id",
				"tttMath-0c1ef", "-failing", "org.apache.commons.math3.primes.PrimesTest", "-location",
				"/home/matias/develop/code/astor/examples/Math-0c1ef", "-package", "org.apache.commons", "-maxgen",
				"400", "-population", "2", "-saveall", "-maxtime", "1"
		// "-flthreshold","0.9"
		});
		long t = TimeUtil.delta(init);
		assertTrue(t > 1);// more than one minute
		assertFalse(t < 2);// less than two minutes
	}

	@SuppressWarnings("static-access")
	@Test
	public void testRunMainSrcTestFolder() throws Exception {
		MainjGenProg main1 = new MainjGenProg();
		main1.main(new String[] {
				"-dependencies",
				"examples/Math-0c1ef/lib/junit-4.11.jar" + File.pathSeparator
						+ "/home/matias/develop/code/astor/examples/Math-0c1ef/lib/hamcrest-core-1.3.jar",
				// "-id","tttMath-0c1ef",
				"-failing", "org.apache.commons.math3.primes.PrimesTest", "-location",
				"/home/matias/develop/code/astor/examples/Math-0c1ef", "-package", "org.apache.commons",
				"-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder", "/src/main/java/",
				"-srctestfolder", "/src/test/java/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes" });

	}

	@SuppressWarnings("static-access")
	@Test
	public void testMath2Example() throws Exception {
		MainjGenProg main1 = new MainjGenProg();
		// java -cp astor-0.0.2-SNAPSHOT-jar-with-dependencies.jar
		// fr.inria.main.evolution.MainjGenProg -mode statement-remove -location
		// . -dependencies lib/ -failing $failingTest -package
		// org.apache.commons -jvm4testexecution
		// /usr/lib/jvm/java-1.7.0-openjdk-amd64/bin/ -maxgen 1000000
		// -population 2 -srcjavafolder src/main/java/ -srctestfolder
		// src/test/java/ -binjavafolder target/classes/ -bintestfolder
		// target/test-classes/
		main1.main(new String[] {
				"-dependencies","examples/math_2/lib/" , //File.pathSeparator +
				// "-id","tttMath-0c1ef",
				"-mode","statement-remove",
				"-failing", "org.apache.commons.math3.distribution.HypergeometricDistributionTest", 
				"-location",	"examples/math_2/", 
				"-package", "org.apache.commons",
				"-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin", 
				"-srcjavafolder", "/src/main/java/",
				"-srctestfolder", "/src/test/java/", 
				"-binjavafolder", "/target/classes", 
				"-bintestfolder","/target/test-classes" });

	}

}
