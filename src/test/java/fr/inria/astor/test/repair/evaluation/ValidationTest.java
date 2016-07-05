package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ValidationTest {

	@Before
	public void setup() {
		LogManager.getRootLogger().setLevel(Level.DEBUG);

	}

	@Test
	public void testLang63ValidationStepbyStep() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-3.8.1.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.lang.time.DurationFormatUtilsTest"
				// + ":org.apache.commons.lang.builder.ToStringBuilder",
				, "-location", new File("./examples/lang_63/").getAbsolutePath(),
				//
				"-package", "org.apache.commons", "-srcjavafolder", "/src/main/java/", "-srctestfolder",
				"/src/test/java", "-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes",
				"-javacompliancelevel", "7", "-flthreshold", "0.5", "-out", out.getAbsolutePath(), "-scope", "local",
				"-seed", "6010", "-maxgen", "50", "-stopfirst", "true", "-maxtime", "30", "-testbystep",
				//
				"ignoredtestcases", "org.apache.commons.lang.LocaleUtilsTest",

		};
		try {
			System.out.println(Arrays.toString(args));
			LogManager.getRootLogger().setLevel(Level.DEBUG);
			main1.execute(args);
			System.out.println("Solution SetbyStep " + main1.getEngine().getSolutions().size());
			// assertTrue(main1.getEngine().getSolutions().size() > 0);
		} catch (Exception e) {
			System.out.println("StepByStep fails " + e.getMessage());
			e.printStackTrace();
		}
	}

	@After
	public void teardown() {
		LogManager.getRootLogger().setLevel(Level.ERROR);

	}
}
