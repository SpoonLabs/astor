package fr.inria.astor.test.repair.core;

import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.stats.PatchHunkStats;
import fr.inria.astor.core.stats.PatchStat;
import fr.inria.astor.core.stats.PatchStat.HunkStatEnum;
import fr.inria.astor.core.stats.PatchStat.PatchStatEnum;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import spoon.support.StandardEnvironment;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public abstract class BaseEvolutionaryTest {

	public static Logger log = Logger.getLogger(Thread.currentThread().getName());

	@After
	public void tearDown() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		MutationSupporter.cleanFactory();

		Logger.getLogger(StandardEnvironment.class).setLevel(Level.ERROR);

	}

	public static void validatePatchExistence(String dir) {
		int cantSol = numberSolutions(dir);
		assertTrue(cantSol > 0);
	}

	protected static int numberSolutions(String dir) {
		File out = new File(dir + File.separator + "src");
		log.info("Searching for stored variants at " + out.getParent());
		assertTrue("The directory that store results does not exist: " + out.getAbsolutePath(), out.exists());
		// src folder has a folder with the buggy variant,
		// and zero or more of solution variants
		// assertTrue(out.listFiles().length > 1);
		log.info("Stored variants: " + Arrays.toString(out.listFiles()));
		int cantSol = 0;
		for (File sol : out.listFiles()) {
			cantSol += (sol.getName().startsWith(ConfigurationProperties.getProperty("pvariantfoldername"))) ? 1 : 0;
		}
		return cantSol;
	}

	protected static boolean comparePatch(ProgramVariant variant, String patch) {
		boolean found = false;

		for (List<OperatorInstance> modif : variant.getOperations().values()) {
			for (OperatorInstance modificationInstance : modif) {
				if (patch.equals(modificationInstance.getModified())) {
					found = true;
				}
			}
		}

		return found;

	}

	public PatchHunkStats getHunkSolution(List<PatchStat> solutions, String fix, String type) {

		for (PatchStat patchStat : solutions) {
			PatchHunkStats hunk = (PatchHunkStats) ((List) patchStat.getStats().get(PatchStatEnum.HUNKS)).get(0);
			String fixhunk = hunk.getStats().get(HunkStatEnum.PATCH_HUNK_CODE).toString();
			String fixtype = hunk.getStats().get(HunkStatEnum.PATCH_HUNK_TYPE).toString();

			if (fixhunk.equals(fix) && type.equals(fixtype))
				return hunk;
		}
		return null;
	}

	protected boolean existPatchWithCode(List<ProgramVariant> solutions, String targetCode) {

		for (ProgramVariant programVariant : solutions) {

			if (existPatchWithCode(programVariant, targetCode)) {
				return true;
			}
		}
		return false;
	}

	public boolean existPatchWithCode(ProgramVariant programVariant, String targetCode) {
		List<OperatorInstance> allOps = programVariant.getAllOperations();

		for (OperatorInstance op : allOps) {
			if (op.getModified() != null) {
				String content = op.getModified().toString();

				if (content.contains(targetCode)) {
					return true;
				}
			}
		}
		return false;
	}

}
