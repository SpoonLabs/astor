package fr.inria.astor.test.repair.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.astor.util.PatchDiffCalculator;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class PatchDiffTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testMath70Diff() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-stopfirst", "true");
		cs.command.put("-parameters", "savesolution:false:" + "preservelinenumbers:false");

		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());
		ProgramVariant variant = solutions.get(0);
		assertFalse(variant.getPatchDiff().getFormattedDiff().isEmpty());
		assertEquals(AstorOutputStatus.STOP_BY_PATCH_FOUND, main1.getEngine().getOutputStatus());

		String diff = variant.getPatchDiff().getFormattedDiff();
		System.out.println("Patch formatted: \n" + diff);
		assertTrue(diff.contains("@@ -22,7 +22,7"));
		assertTrue(diff.contains("return solve(f, min, max)"));

		String diff2 = variant.getPatchDiff().getOriginalStatementAlignmentDiff();
		System.out.println("Patch original alignment: \n " + diff2);
		assertTrue(diff2.contains("@@ -68,8 +68,8"));
		assertTrue(diff2.contains("return solve(f, min, max)"));

		String path_o = main1.getEngine().getProjectFacade().getInDirWithPrefix(variant.currentMutatorIdentifier());
		assertTrue(new File(path_o + File.separator + PatchDiffCalculator.PATCH_DIFF_FILE_NAME).exists());

		String path_f = main1.getEngine().getProjectFacade()
				.getInDirWithPrefix(variant.currentMutatorIdentifier() + PatchDiffCalculator.DIFF_SUFFIX);
		assertTrue(new File(path_f + File.separator + PatchDiffCalculator.PATCH_DIFF_FILE_NAME).exists());
	}

}
