package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.*;

import java.io.File;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.util.CommandSummary;
import fr.inria.main.evolution.AstorMain;

/**
 * failing projects sent by Simon
 * 
 * @author Matias Martinez
 *
 */
public class RepairnatorTest {

	@Test
	public void testjsoup285353482() throws Exception {
		String m2path = System.getenv("HOME") + "/.m2/repository/";
		File fm2 = new File(m2path);
		if (!fm2.exists()) {
			throw new Exception(m2path + "does not exit");
		}

		String[] command = new String[] { "-dependencies",
				m2path + "/junit/junit/4.12/junit-4.12.jar:" + m2path
						+ "/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar:" + m2path
						+ "/com/google/code/gson/gson/2.7/gson-2.7.jar",
				"-mode", "jgenprog", "-location",
				new File("./examples/librepair-experiments-jhy-jsoup-285353482-20171009-062400_bugonly")
						.getAbsolutePath(),
				"-srcjavafolder", "/src/main/java", // Modified by M.M, original
													// path was absolute.
				"-stopfirst", "true", "-population", "1", "-maxgen", "50", // Added
																			// by
																			// M.M.,
																			// not
																			// present
																			// in
																			// command
																			// sent
																			// by
																			// S.U.
				"-parameters", "timezone:Europe/Paris:maxnumbersolutions:3:limitbysuspicious:false:" + "loglevel:DEBUG",
				"-maxtime", "100", "-seed", "1" };
		CommandSummary cs = new CommandSummary(command);
		AstorMain main1 = new AstorMain();
		main1.execute(cs.flat());

		assertTrue(main1.getEngine().getVariants().size() > 0);
		assertTrue(main1.getEngine().getVariants().get(0).getModificationPoints().size() > 0);

	}

	@Test
	@Ignore
	public void testAsc() throws Exception {
		// TODO.
		String[] command = new String[] { "-dependencies", "X", "-mode", "jgenprog", "-location",
				"/root/workspace/AsyncHttpClient/async-http-client/285409364/client", "-srcjavafolder",
				"/root/workspace/AsyncHttpClient/async-http-client/285409364/client/src/main/java", "-stopfirst",
				"true", "-population", "1", "-parameters",
				"timezone:Europe/Paris:maxnumbersolutions:3:limitbysuspicious:false:maxmodificationpoints:1000:javacompliancelevel:8:logfilepath:./workspace/AsyncHttpClient/async-http-client/285409364/repairnator.astor.log",
				"-maxtime", "100", "-seed", "1" };
		CommandSummary cs = new CommandSummary(command);
		AstorMain main1 = new AstorMain();
		main1.execute(cs.flat());

	}

}
