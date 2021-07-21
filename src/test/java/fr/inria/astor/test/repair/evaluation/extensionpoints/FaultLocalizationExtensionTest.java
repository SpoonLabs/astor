package fr.inria.astor.test.repair.evaluation.extensionpoints;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.faultlocalization.flacoco.FlacocoFaultLocalization;
import fr.inria.astor.core.faultlocalization.gzoltar.GZoltarFaultLocalization;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.navigation.SuspiciousNavigationValues;
import fr.inria.astor.core.stats.PatchStat;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;
import org.junit.Assert;
import org.junit.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static fr.inria.astor.test.repair.QuixBugsRepairTest.getQuixBugsCommand;
import static org.junit.Assert.*;

public class FaultLocalizationExtensionTest {

	@Test
	public void testFlacocoExtensionjKaliFindingMoreThanOneSolution() throws Exception {
		AstorMain gzoltarMain =
				runjKaliFindingMoreThanOneSolution(GZoltarFaultLocalization.class.getCanonicalName());
		AstorMain flacocoMain =
				runjKaliFindingMoreThanOneSolution(FlacocoFaultLocalization.class.getCanonicalName());

		checkEqualFaultLocalization(flacocoMain, gzoltarMain);
	}

	@Test
	public void testFlacocoExtensionShortestPathLenghtsRepairStatement() throws Exception {
		AstorMain gzoltarMain =
				run_shortest_path_lengthsRepair_statement(GZoltarFaultLocalization.class.getCanonicalName());
		AstorMain flacocoMain =
				run_shortest_path_lengthsRepair_statement(FlacocoFaultLocalization.class.getCanonicalName());

		checkEqualFaultLocalization(flacocoMain, gzoltarMain);
	}

	@Test
	public void testFlacocoExtensionMath85() throws Exception {
		AstorMain gzoltarMain =
				runMath85(GZoltarFaultLocalization.class.getCanonicalName());
		AstorMain flacocoMain =
				runMath85(FlacocoFaultLocalization.class.getCanonicalName());

		checkEqualFaultLocalization(flacocoMain, gzoltarMain);
	}

	private void checkEqualFaultLocalization(AstorMain flacocoMain, AstorMain gzoltarMain) {
		// Get suspicious points for gzoltar
		ProgramVariant pv1 = gzoltarMain.getEngine().getVariants().get(0);
		assertNotNull(pv1);
		assertTrue(pv1.getModificationPoints().size() > 0);
		List<SuspiciousModificationPoint> gzoltarSuspicious = new ArrayList<>();
		for (ModificationPoint iMp : pv1.getModificationPoints()) {
			assertTrue(iMp instanceof SuspiciousModificationPoint);
			SuspiciousModificationPoint iSmp = (SuspiciousModificationPoint) iMp;
			gzoltarSuspicious.add(iSmp);
			assertTrue(iSmp.getSuspicious().getSuspiciousValue() > 0.5);
		}

		// Get suspicous points for flacoco
		pv1 = flacocoMain.getEngine().getVariants().get(0);
		assertNotNull(pv1);
		assertTrue(pv1.getModificationPoints().size() > 0);
		List<SuspiciousModificationPoint> flacocoSuspicious = new ArrayList<>();
		for (ModificationPoint iMp : pv1.getModificationPoints()) {
			assertTrue(iMp instanceof SuspiciousModificationPoint);
			SuspiciousModificationPoint iSmp = (SuspiciousModificationPoint) iMp;
			flacocoSuspicious.add(iSmp);
			assertTrue(iSmp.getSuspicious().getSuspiciousValue() > 0.5);
		}

		// Assert they are the same
		assertEquals(gzoltarMain.getEngine().getProjectFacade().getProperties().getFailingTestCases(),
				flacocoMain.getEngine().getProjectFacade().getProperties().getFailingTestCases());

		Iterator<SuspiciousModificationPoint> flacocoIterator = flacocoSuspicious
				.stream().sorted(Comparator.comparingInt(x -> x.getSuspicious().getLineNumber()))
				.iterator();
		assertNotNull(flacocoIterator);
		System.out.println("flacoco : " + flacocoSuspicious);

		Iterator<SuspiciousModificationPoint> gzoltarIterator = gzoltarSuspicious
				.stream().sorted(Comparator.comparingInt(x -> x.getSuspicious().getLineNumber()))
				.iterator();
		assertNotNull(gzoltarSuspicious);
		System.out.println("GZoltar : " + gzoltarSuspicious);

		while (flacocoIterator.hasNext() || gzoltarIterator.hasNext()) {
			assertTrue(flacocoIterator.hasNext());
			assertTrue(gzoltarIterator.hasNext());

			SuspiciousModificationPoint fsp = flacocoIterator.next();
			assertNotNull(fsp);
			SuspiciousModificationPoint gsp = gzoltarIterator.next();
			assertNotNull(gsp);

			System.out.println(fsp.getSuspicious());
			System.out.println(gsp.getSuspicious());

			assertEquals(gsp.getSuspicious().getSuspiciousValue(), fsp.getSuspicious().getSuspiciousValue(), 0.00);
			assertEquals(gsp.getSuspicious().getFileName(), fsp.getSuspicious().getFileName());
			assertEquals(gsp.getSuspicious().getClassName(), fsp.getSuspicious().getClassName());
			// flacoco doesn't return the method info like gzoltar
			// this information isn't used in astor currently
			// assertEquals(gsp.getSuspicious().getMethodName(), fsp.getSuspicious().getMethodName());
			assertEquals(gsp.getSuspicious().getLineNumber(), fsp.getSuspicious().getLineNumber());
		}
	}

	private AstorMain runjKaliFindingMoreThanOneSolution(String fltorun) throws Exception {
		File targetolder = new File("./examples/test-jkali-multiple-solutions/target/");
		if (!targetolder.exists()) {
			targetolder.mkdirs();
		}

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		int compilerResult = compiler.run(null, null, null,
				"./examples/test-jkali-multiple-solutions/src/main/java/com/example/jkali_multiple_solutions/App.java",
				"./examples/test-jkali-multiple-solutions/src/test/java/com/example/jkali_multiple_solutions/AppTest.java",
				"-d", "./examples/test-jkali-multiple-solutions/target/");

		assertEquals(0, compilerResult);

		File binJavaFolder = new File("./examples/test-jkali-multiple-solutions/target/classes/com/example/jkali_multiple_solutions");
		if (!binJavaFolder.exists()) {
			binJavaFolder.mkdirs();
		}

		File compiledSource = new File("./examples/test-jkali-multiple-solutions/target/com/example/jkali_multiple_solutions/App.class");
		compiledSource.renameTo(new File(binJavaFolder, compiledSource.getName()));

		File binTestFolder = new File("./examples/test-jkali-multiple-solutions/target/test-classes/com/example/jkali_multiple_solutions");
		if (!binTestFolder.exists()) {
			binTestFolder.mkdirs();
		}

		File compiledTest = new File("./examples/test-jkali-multiple-solutions/target/com/example/jkali_multiple_solutions/AppTest.class");
		compiledTest.renameTo(new File(binTestFolder, compiledTest.getName()));

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[]{"-dependencies", dep, "-mode", ExecutionMode.jKali.toString().toLowerCase(),
				"-failing", "com.example.jkali_multiple_solutions.AppTest", "-location",
				new File("./examples/test-jkali-multiple-solutions").getAbsolutePath(), "-package", "com.example.jkali_multiple_solutions", "-srcjavafolder",
				"/src/main/java/", "-srctestfolder", "/src/test/java", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-out",
				out.getAbsolutePath(),
				"-scope", "package", "-seed", "10", "-maxgen", "10000", "-stopfirst", "false",
				"-faultlocalization", fltorun
		};
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();

		assertTrue(solutions.size() > 1);

		List<PatchStat> patches = main1.getEngine().getPatchInfo();

		Assert.assertTrue(patches.size() > 1);

		Assert.assertEquals(3, patches.size());

		return main1;
	}

	private AstorMain run_shortest_path_lengthsRepair_statement(String fltorun) throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("shortest_path_lengths"));
		command.command.put("-maxgen", "500");
		command.command.put("-faultlocalization", fltorun);
		main1.execute(command.flat());

		assertTrue("No solution", main1.getEngine().getSolutions().size() > 0);

		return main1;
	}

	private AstorMain runMath85(String fltorun) throws Exception {
		CommandSummary csDefault = MathCommandsTests.getMath85Command();
		csDefault.command.put("-maxgen", "0");
		csDefault.command.put("-modificationpointnavigation", SuspiciousNavigationValues.WEIGHT.toString());
		csDefault.command.put("-faultlocalization", fltorun);
		csDefault.command.put("-parameters", "includeTestInSusp:true:loglevel:INFO");

		final Double flthreshold = 0.5;
		csDefault.command.put("-flthreshold", flthreshold.toString());

		AstorMain main1 = new AstorMain();

		main1.execute(csDefault.flat());

		return main1;
	}
}
