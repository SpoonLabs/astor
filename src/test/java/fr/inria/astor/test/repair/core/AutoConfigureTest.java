package fr.inria.astor.test.repair.core;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class AutoConfigureTest {

	@Test
	public void testAutoConfigureMath70() throws Exception {

		AstorMain main1 = new AstorMain();

		String[] args = new String[] { "-mode", "jgenprog", "-location", new File("examples/math_70").getAbsolutePath(),
				// We dont say nothing more about the bug
				"-flthreshold", "0.5", "-stopfirst", "true",
				// We put 0 as max generation, so we force to not evolve the
				// population
				"-loglevel", "DEBUG", "-maxgen", "1000", "-scope", "local", "-seed", "10", "-autoconfigure",
				"-parameters",
				// which mvn
				"mvndir:/usr/local/bin/mvn" };

		main1.execute(args);

		List<ProgramVariant> variants = main1.getEngine().getSolutions();
		assertTrue(variants.size() > 0);
	}
}
