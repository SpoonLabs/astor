package fr.inria.astor.test.repair.core;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectConfiguration;
import fr.inria.astor.test.repair.evaluation.extensionpoints.ingredients.ShortestIngredientSearchStrategy;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ConfigurationTest extends BaseEvolutionaryTest {

	@Test
	public void testJDKVersion() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				// We put 0 as max generation, so we force to not evolve the
				// population
				"-maxgen", "0", "-scope", "local", "-seed", "10", "-ingredientstrategy",
				ShortestIngredientSearchStrategy.class.getName() };

		main1.execute(args);
		String jdk = ConfigurationProperties.getProperty("jvm4testexecution");
		String jdkVersion = ProjectConfiguration.getVersionJDK(jdk);
		System.out.println(jdkVersion);
		Assert.assertTrue(jdkVersion.startsWith("1.8") || jdkVersion.startsWith("8"));

	}

	@Test
	public void testJDKVersionAlternative() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				// We put 0 as max generation, so we force to not evolve the
				// population
				"-maxgen", "0", "-scope", "local", "-seed", "10", "-ingredientstrategy",
				ShortestIngredientSearchStrategy.class.getName() };

		main1.execute(args);
		JGenProg astor = (JGenProg) main1.getEngine();
		String jdk = ConfigurationProperties.getProperty("jvm4testexecution");
		String jdkaltern = ConfigurationProperties.getProperty("jvm4evosuitetestexecution");
		Assert.assertEquals(jdk, jdkaltern);
	}

	@Test
	public void testJDKVersionLower8() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				// We put 0 as max generation, so we force to not evolve the
				// population
				"-maxgen", "0", "-scope", "local", "-seed", "10", "-ingredientstrategy",
				ShortestIngredientSearchStrategy.class.getName() };

		main1.execute(args);
		ConfigurationProperties.setProperty("jvmversion", "1.8.0_101");
		Assert.assertFalse(ProjectConfiguration.isJDKLowerThan8());
		ConfigurationProperties.setProperty("jvmversion", "1.5.0_101");
		Assert.assertTrue(ProjectConfiguration.isJDKLowerThan8());
		ConfigurationProperties.setProperty("jvmversion", "1.7.0_101");
		Assert.assertTrue(ProjectConfiguration.isJDKLowerThan8());
		ConfigurationProperties.setProperty("jvmversion", "1.7.51");
		Assert.assertTrue(ProjectConfiguration.isJDKLowerThan8());
		ConfigurationProperties.setProperty("jvmversion", "1");
		Assert.assertFalse(ProjectConfiguration.isJDKLowerThan8());
		ConfigurationProperties.setProperty("jvmversion", "1.9");
		Assert.assertFalse(ProjectConfiguration.isJDKLowerThan8());
		ConfigurationProperties.setProperty("jvmversion", "1.1.1.1");
		Assert.assertTrue(ProjectConfiguration.isJDKLowerThan8());
		ConfigurationProperties.setProperty("jvmversion", "1.8.1.1");
		Assert.assertFalse(ProjectConfiguration.isJDKLowerThan8());
		ConfigurationProperties.setProperty("jvmversion", "1.7.1.1");
		Assert.assertTrue(ProjectConfiguration.isJDKLowerThan8());
	}

	@Test
	public void testMultiplesBin() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/",
				///
				"-binjavafolder", "/target/classes" + File.pathSeparator + "/target/classes",
				//
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5",
				"-stopfirst", "false", "-maxgen", "0", "-scope", "local", "-seed", "10", "-ingredientstrategy",
				ShortestIngredientSearchStrategy.class.getName() };

		main1.execute(args);

	}

	@Test
	public void testNoDependency() throws Exception {

		AstorMain main1 = new AstorMain();
		// String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { // "-dependencies", dep,
				"-mode", "jgenprog", "-failing", "org.apache.commons.math.distribution.NormalDistributionTest",
				"-location", new File("./examples/math_85/").getAbsolutePath(), "-package", "org.apache.commons",
				"-srcjavafolder", "/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5",
				"-stopfirst", "false",
				// We put 0 as max generation, so we force to not evolve the
				// population
				"-maxgen", "0", "-scope", "local", "-seed", "10", "-ingredientstrategy",
				ShortestIngredientSearchStrategy.class.getName() };

		main1.execute(args);
		String jdk = ConfigurationProperties.getProperty("jvm4testexecution");
		String jdkaltern = ConfigurationProperties.getProperty("jvm4evosuitetestexecution");
		Assert.assertEquals(jdk, jdkaltern);
	}

}
