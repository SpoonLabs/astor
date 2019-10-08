package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Test;

import fr.inria.astor.test.repair.core.BaseEvolutionaryTest;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class UnitFrameworkTest extends BaseEvolutionaryTest {

	@Test
	public void testNoSuspicious() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary cs = new CommandSummary();
		cs.command.put("-mode", "jGenProg");
		cs.command.put("-srcjavafolder", "/src/main/java/");
		cs.command.put("-srctestfolder", "/src/test/java/");
		cs.command.put("-binjavafolder", "/target/classes");
		cs.command.put("-bintestfolder", "/target/test-classes/");
		cs.command.put("-location", new File("./examples/java-maven-junit-helloworld/").getAbsolutePath());
		// cs.command.put("-dependencies", new

		cs.command.put("-maxgen", "100");
		cs.command.put("-loglevel", "DEBUG");

		try {
			// Fails due to Gzoltar 0.1.1
			main1.execute(cs.flat());

			fail("it found a test");
		} catch (Exception e) {
			// Let's try directly:
			e.printStackTrace();
			try {
				List<String> test = main1.getEngine().resolveTestsToRun();

				assertTrue(test.size() > 0);
			} catch (Exception e1) {
				fail("sss");
			}

		}
	}

}
