package fr.inria.astor.test.repair.evaluation.extensionpoints.suspiciousnavigation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.solutionsearch.navigation.SuspiciousNavigationValues;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class SuspiciousNavigationStrategyTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testNavigationSuspiciousSpace() throws Exception {

		CommandSummary csDefault = MathCommandsTests.getMath70Command();
		csDefault.command.put("-maxgen", "0");
		csDefault.command.put("-modificationpointnavigation", SuspiciousNavigationValues.WEIGHT.toString());
		AstorMain main1 = new AstorMain();

		System.out.println(Arrays.toString(csDefault.flat()));
		main1.execute(csDefault.flat());

		ProgramVariant pv1 = main1.getEngine().getVariants().get(0);

		List<ModificationPoint> mps = main1.getEngine().getSuspiciousNavigationStrategy()
				.getSortedModificationPointsList(pv1.getModificationPoints());
		assertNotEquals(pv1.getModificationPoints(), mps);

		csDefault = MathCommandsTests.getMath70Command();
		csDefault.command.put("-maxgen", "0");
		csDefault.command.put("-modificationpointnavigation", SuspiciousNavigationValues.RANDOM.toString());
		AstorMain main2 = new AstorMain();

		System.out.println(Arrays.toString(csDefault.flat()));
		main2.execute(csDefault.flat());

		ProgramVariant pv2 = main2.getEngine().getVariants().get(0);

		List<ModificationPoint> mps2 = main2.getEngine().getSuspiciousNavigationStrategy()
				.getSortedModificationPointsList(pv2.getModificationPoints());
		assertNotEquals(pv2.getModificationPoints(), mps2);
		assertNotEquals(mps, mps2);

		List<ModificationPoint> mps2b = main2.getEngine().getSuspiciousNavigationStrategy()
				.getSortedModificationPointsList(pv2.getModificationPoints());
		assertNotEquals(mps2, mps2b);

		csDefault = MathCommandsTests.getMath70Command();
		csDefault.command.put("-maxgen", "0");
		csDefault.command.put("-modificationpointnavigation", SuspiciousNavigationValues.INORDER.toString());
		AstorMain main3 = new AstorMain();

		System.out.println(Arrays.toString(csDefault.flat()));
		main3.execute(csDefault.flat());

		ProgramVariant pv3 = main3.getEngine().getVariants().get(0);

		List<ModificationPoint> mpsInOrder = main3.getEngine().getSuspiciousNavigationStrategy()
				.getSortedModificationPointsList(pv3.getModificationPoints());
		assertEquals(pv3.getModificationPoints(), mpsInOrder);

		Double dmax = Double.MAX_VALUE;
		for (ModificationPoint modificationPoint : mpsInOrder) {
			SuspiciousModificationPoint smp = (SuspiciousModificationPoint) modificationPoint;
			assertTrue(dmax >= smp.getSuspicious().getSuspiciousValue());
			dmax = smp.getSuspicious().getSuspiciousValue();
		}

	}

}
