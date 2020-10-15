package fr.inria.astor.test.repair.evaluation.extensionpoints.faultlocalization;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.faultlocalization.flacoco.FlacocoFaultLocalization;
import fr.inria.astor.core.solutionsearch.navigation.SuspiciousNavigationValues;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class FaultLocalizationExtensionTest {

	@Test
	public void flacocoTest1_Math85() throws Exception {
		CommandSummary csDefault = MathCommandsTests.getMath85Command();
		csDefault.command.put("-maxgen", "0");
		csDefault.command.put("-modificationpointnavigation", SuspiciousNavigationValues.WEIGHT.toString());
		csDefault.command.put("-faultlocalization", FlacocoFaultLocalization.class.getCanonicalName());
		csDefault.command.put("-parameters", "includeTestInSusp:true");

		final Double flthreshold = 0.5;
		csDefault.command.put("-flthreshold", flthreshold.toString());

		AstorMain main1 = new AstorMain();

		System.out.println(Arrays.toString(csDefault.flat()));
		main1.execute(csDefault.flat());

		assertTrue(main1.getEngine().getFaultLocalization() instanceof FlacocoFaultLocalization);
		assertTrue(main1.getEngine().getProjectFacade().getProperties().getFailingTestCases().size() > 0);

		ProgramVariant pv1 = main1.getEngine().getVariants().get(0);

		assertNotNull(pv1);

		assertTrue(pv1.getModificationPoints().size() > 0);

		for (ModificationPoint iMp : pv1.getModificationPoints()) {
			assertTrue(iMp instanceof SuspiciousModificationPoint);
			SuspiciousModificationPoint iSmp = (SuspiciousModificationPoint) iMp;

			assertTrue(iSmp.getSuspicious().getSuspiciousValue() >= 0);

		}

	}

}
