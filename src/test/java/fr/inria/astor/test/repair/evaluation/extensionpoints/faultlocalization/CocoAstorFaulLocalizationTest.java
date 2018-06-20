package fr.inria.astor.test.repair.evaluation.extensionpoints.faultlocalization;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.faultlocalization.cocospoon.CocoFaultLocalization;
import fr.inria.astor.core.faultlocalization.cocospoon.CocoSpoonEngineFaultLocalizer;
import fr.inria.astor.core.faultlocalization.cocospoon.code.SourceLocation;
import fr.inria.astor.core.faultlocalization.cocospoon.code.StatementSourceLocation;
import fr.inria.astor.core.faultlocalization.cocospoon.metrics.Ochiai;
import fr.inria.astor.core.faultlocalization.cocospoon.testrunner.TestResult;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.bytecode.classloader.BytecodeClassLoader;
import fr.inria.astor.core.manipulation.bytecode.entities.CompilationResult;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.declaration.CtType;

/**
 * 
 * @author Matias Martinez
 *
 */
@Ignore
public class CocoAstorFaulLocalizationTest {

	@Test
	public void testStepByStepCocoAstorTest() throws Exception {
		CommandSummary csDefault = MathCommandsTests.getMath70Command();
		csDefault.command.put("-maxgen", "0");
		csDefault.command.put("-loglevel", "DEBUG");
		AstorMain main1 = new AstorMain();

		System.out.println(Arrays.toString(csDefault.flat()));
		main1.execute(csDefault.flat());

		ProgramVariant pv1 = main1.getEngine().getVariants().get(0);

		ProjectRepairFacade project = main1.getEngine().getProjectFacade();
		Assert.assertNotNull(project);

		CtType typeBisectionSolver1 = MutationSupporter.getFactory().Type().getAll().stream()
				.filter(e -> e.getSimpleName().equals("BisectionSolver")).findFirst().get();
		Assert.assertNotNull(typeBisectionSolver1);
		String s1 = typeBisectionSolver1.toString();

		MutationSupporter.cleanFactory();

		CocoFaultLocalization cocofl = new CocoFaultLocalization();
		cocofl.initModel(project);

		////
		cocofl.parseModel();
		CtType typeBisectionSolver2 = MutationSupporter.getFactory().Type().getAll().stream()
				.filter(e -> e.getSimpleName().equals("BisectionSolver")).findFirst().get();
		Assert.assertNotNull(typeBisectionSolver2);
		String s2 = typeBisectionSolver2.toString();

		// System.out.println("--> line "+ cs.getBody().getStatements().get(0));
		Assert.assertNotEquals(s1, s2);
		System.out.println(s2);

		////
		CompilationResult cresults = cocofl.compile(project);
		Assert.assertNotNull(cresults.getByteCodes());
		System.out.println(cresults.getErrorList());
		Assert.assertTrue(cresults.compiles());

		//

		BytecodeClassLoader customClassLoader = cocofl.createClassLoader(cresults, project);
		Assert.assertNotNull(customClassLoader);
		//
		//
		CocoSpoonEngineFaultLocalizer coco4Astor = new CocoSpoonEngineFaultLocalizer(new Ochiai());
		List<String> testregression = project.getProperties().getRegressionTestCases();
		testregression.toArray(new String[0]);
		coco4Astor.runTests(testregression.toArray(new String[0]), customClassLoader, project);
		Map<SourceLocation, List<TestResult>> stc = coco4Astor.getTestListPerStatement();
		Assert.assertNotNull(stc);
		List<? extends StatementSourceLocation> suspstatement = coco4Astor.getStatements();
		Assert.assertNotNull(suspstatement);

		// System.out.println("\n susp \n" + suspstatement);
		for (int i = 0; i < 8; i++) {

			StatementSourceLocation sic = suspstatement.get(i);
			System.out.println(sic);

			ModificationPoint mpi = main1.getEngine().getVariants().get(0).getModificationPoints().get(i);
			System.out.println(mpi);
		}
		//
		/// System.out.println("\n----"+ stc.keySet().size());
	}

	@Test
	public void cocoTest() throws Exception {
		CommandSummary csDefault = MathCommandsTests.getMath70Command();
		csDefault.command.put("-maxgen", "0");
		csDefault.command.put("-loglevel", "DEBUG");
		csDefault.command.put("-faultlocalization", "cocospoon");
		csDefault.command.put("-flthreshold", "0");
		AstorMain main1 = new AstorMain();

		System.out.println(Arrays.toString(csDefault.flat()));
		main1.execute(csDefault.flat());

		ProgramVariant pv1 = main1.getEngine().getVariants().get(0);

		for (ModificationPoint mp : pv1.getModificationPoints()) {
			System.out.println(
					"mp " + mp + " " + ((SuspiciousModificationPoint) mp).getSuspicious().getSuspiciousValue());

		}
		SuspiciousModificationPoint smp0 = (SuspiciousModificationPoint) pv1.getModificationPoints().get(0);
		Assert.assertEquals(72, smp0.getSuspicious().getLineNumber());
		Assert.assertTrue(smp0.getSuspicious().getSuspiciousValue() < 1);
		Assert.assertEquals(23, pv1.getModificationPoints().size());

	}
}
