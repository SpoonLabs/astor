package fr.inria.astor.test.repair.evaluation.extensionpoints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.faultlocalization.CoCoAstorFaultLocalization;
import fr.inria.astor.core.faultlocalization.cocospoon.CocoSpoonBasedSpectrumBasedFaultLocalizer4Astor;
import fr.inria.astor.core.faultlocalization.cocospoon.SourceLocation;
import fr.inria.astor.core.faultlocalization.cocospoon.StatementSourceLocation;
import fr.inria.astor.core.faultlocalization.cocospoon.TestResult;
import fr.inria.astor.core.faultlocalization.metric.Ochiai;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.bytecode.classloader.BytecodeClassLoader;
import fr.inria.astor.core.manipulation.bytecode.entities.CompilationResult;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.test.repair.evaluation.regression.MathTests;
import fr.inria.astor.util.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author Matias Martinez
 *
 */
public class CocoAstorFaulLocalizationTest {

	@Test
	public void testCocoAstorTest() throws Exception {
		CommandSummary csDefault = MathTests.getMath70Command();
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

		CoCoAstorFaultLocalization cocofl = new CoCoAstorFaultLocalization();
		cocofl.initModel(project);

		////
		cocofl.parse();
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
		CocoSpoonBasedSpectrumBasedFaultLocalizer4Astor coco4Astor = new CocoSpoonBasedSpectrumBasedFaultLocalizer4Astor(
				new Ochiai());
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
			System.out.println("-----");
		}
		//
		/// System.out.println("\n----"+ stc.keySet().size());
	}
}
