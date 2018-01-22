package fr.inria.astor.test.repair.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.test.repair.approaches.JGenProgTest;
import fr.inria.astor.util.PatchDiffCalculator;
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
	public void testDiffMath70() throws Exception {
		
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		int generations = 50;
		String[] args = JGenProgTest.commandMath70(dep, out,generations);
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		
		

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());
		ProgramVariant variant = solutions.get(0);
		PatchDiffCalculator cdiff = new PatchDiffCalculator();
		String diff = cdiff.getDiff(main1.getEngine().getProjectFacade(), variant);
		System.out.println(diff);
		assertNotNull(diff);
		assertTrue(diff.contains("return solve(f, min, max)"));
		
	}

}
