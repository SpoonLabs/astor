package fr.inria.astor.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectConfiguration;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.validation.executors.WorkerThreadHelper;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;

/**
 * 
 * @author Matias Martinez
 *
 */
public class EvoSuiteFacade {

	public static String EVOSUITE_SUFFIX = "_ESTest";
	public static String EVOSUITE_scaffolding_SUFFIX ="_ESTest_scaffolding";
	
	Logger logger = Logger.getLogger(Thread.currentThread().getName());

	public boolean runEvosuite(ProgramVariant variant,
			// String path, String classpath,
			ProjectRepairFacade projectFacade) throws Exception {

		List<URL> originalURL = new ArrayList(
				Arrays.asList(projectFacade.getClassPathURLforProgramVariant(ProgramVariant.DEFAULT_ORIGINAL_VARIANT)));

		String outES = projectFacade.getInDirWithPrefix(ConfigurationProperties.getProperty("evosuiteresultfolder"));
		File fESout = new File(outES);
		fESout.mkdirs();

		URL[] SUTClasspath = null;
		SUTClasspath = originalURL.toArray(new URL[0]);

		List<CtType<?>> types = variant.getAffectedClasses();
		boolean reponse = true;
		for (CtType<?> ctType : types) {
			// generate a process
			String[] command = new String[] { "-class", ctType.getQualifiedName(), "-projectCP",
					urlArrayToString(SUTClasspath), "-base_dir", outES
					// ,"-Djunit_check_on_separate_process=true"
			};

			reponse &= runProcess(null, command);
			System.out.println("Analyzing " + ctType.getQualifiedName() + " " + reponse);
		}
		return reponse;
	}

	// TODO: cloned
	protected String urlArrayToString(URL[] urls) {
		String s = "";
		if (urls == null)
			return s;
		for (int i = 0; i < urls.length; i++) {
			URL url = urls[i];
			s += url.getPath() + File.pathSeparator;
		}
		return s;
	}

	// TODO: cloned
	protected URL[] redefineURL(File foutgen, URL[] originalURL) throws MalformedURLException {
		List<URL> urls = new ArrayList<URL>();
		urls.add(foutgen.toURL());
		for (int i = 0; (originalURL != null) && i < originalURL.length; i++) {
			urls.add(originalURL[i]);
		}

		return (URL[]) urls.toArray(originalURL);
	}

	/**
	 * 
	 * @param urlClasspath
	 * @param argumentsEvo
	 * @return
	 */
	private boolean runProcess(URL[] urlClasspath, String[] argumentsEvo) {
		Process p = null;

		if (!ProjectConfiguration.validJDK())
			throw new IllegalArgumentException(
					"jdk folder not found, please configure property jvm4testexecution in the configuration.properties file");

		String javaPath = ConfigurationProperties.getProperty("jvm4testexecution");
		javaPath += File.separator + "java";

		try {

			List<String> command = new ArrayList<String>();
			command.add(javaPath);
			command.add("-jar");
			command.add(new File(ConfigurationProperties.getProperty("evosuitejar")).getAbsolutePath());

			for (String arg : argumentsEvo) {
				command.add(arg);
			}

			ProcessBuilder pb = new ProcessBuilder(command.toArray(new String[command.size()]));
			pb.redirectOutput();
			pb.redirectErrorStream(true);
			pb.directory(new File((ConfigurationProperties.getProperty("location"))));
			p = pb.start();

			WorkerThreadHelper worker = new WorkerThreadHelper(p);
			worker.start();
			worker.join();
			long t_end = System.currentTimeMillis();

			p.exitValue();

			readOut(p);
			p.destroy();
			return true;
		} catch (IOException | InterruptedException | IllegalThreadStateException ex) {
			if (p != null)
				p.destroy();
		}
		return false;
	}

	public List<CtClass> reificateEvoSuiteTest(String evoTestpath, String[] classpath) {
		System.out.println("Compiling ES code " + evoTestpath + " with CL " + Arrays.toString(classpath));
		MutationSupporter mutatorSupporter = MutationSupporter.currentSupporter;
		Factory factory = MutationSupporter.currentSupporter.getFactory();
		String codeLocation = evoTestpath;
		boolean saveOutput = true;
		try {
			mutatorSupporter.buildModel(codeLocation, classpath, saveOutput);
			//mutatorSupporter.saveClassModel(codeLocation, classpath, saveOutput);//FOR TEST
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			mutatorSupporter.getFactory().getEnvironment()
					.setComplianceLevel(ConfigurationProperties.getPropertyInt("alternativecompliancelevel"));
			mutatorSupporter.buildModel(codeLocation, classpath, saveOutput);
			
		}

		List<CtType<?>> allTypes = mutatorSupporter.getFactory().Type().getAll();
		List<CtClass> ESTestClasses = new ArrayList<>();
		for (CtType<?> ctType : allTypes) {
			if (ctType.getSimpleName().endsWith(EVOSUITE_SUFFIX)
					|| ctType.getSimpleName().endsWith(EVOSUITE_scaffolding_SUFFIX)) {
				ESTestClasses.add((CtClass) ctType);
			}
		}
		return ESTestClasses;
	}

	private String readOut(Process p) {
		boolean success = false;
		String out = "";
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				out += line + "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(out);
		return out;
	}
}
