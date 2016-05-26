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
import fr.inria.astor.core.entities.ProgramVariantValidationResult;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.bytecode.entities.CompilationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectConfiguration;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.validation.executors.WorkerThreadHelper;
import fr.inria.astor.core.validation.validators.ProcessEvoSuiteValidator;
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
	public static String EVOSUITE_scaffolding_SUFFIX = "_ESTest_scaffolding";

	Logger logger = Logger.getLogger(Thread.currentThread().getName());

	/**
	 * Executing evosuite.
	 * For each Affected class from the program variant, 
	 * we create the related TestUnit using evosuite
	 * @param variant
	 * @param projectFacade
	 * @return
	 * @throws Exception
	 */
	public boolean runEvosuite(ProgramVariant variant,
			ProjectRepairFacade projectFacade) throws Exception {

		List<URL> originalURL = new ArrayList(
				Arrays.asList(projectFacade.getClassPathURLforProgramVariant(ProgramVariant.DEFAULT_ORIGINAL_VARIANT)));

		String outES = projectFacade.getInDirWithPrefix(ConfigurationProperties.getProperty("evosuiteresultfolder"));
		File fESout = new File(outES);
		fESout.mkdirs();

		URL[] SUTClasspath = originalURL.toArray(new URL[0]);

		logger.debug("Creating test cases using evosuite ");
		
		List<CtType<?>> types = variant.getAffectedClasses();
		boolean reponse = true;
		for (CtType<?> ctType : types) {
			// generate a process for running evosuite
			String[] command = new String[] { "-class", ctType.getQualifiedName(), 
					"-projectCP", urlArrayToString(SUTClasspath),//
					"-base_dir", outES//
					// ,"-Djunit_check_on_separate_process=true"
			};
			logger.debug("Creating test for "+ctType.getQualifiedName());
			reponse &= runProcess(null, command);
			//logger.debug("reponse from " + ctType.getQualifiedName() + " " + reponse);
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
		logger.debug("Compiling ES code " + evoTestpath + " with CL " + Arrays.toString(classpath));
		MutationSupporter mutatorSupporter = MutationSupporter.currentSupporter;
		Factory factory = MutationSupporter.currentSupporter.getFactory();
		String codeLocation = evoTestpath;
		boolean saveOutput = true;
		try {
			mutatorSupporter.buildModel(codeLocation, classpath, saveOutput);
			// mutatorSupporter.saveClassModel(codeLocation, classpath,
			// saveOutput);//FOR TEST
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
	/**
	 * Given a program variant, we create using a program variant a) the Evosuite test
	 * b) the spoon model of the Evosuite test classes. 
	 * @param projectFacade
	 * @param variant
	 * @return
	 * @throws Exception
	 */
	public List<CtClass> createEvoTestModel(ProjectRepairFacade projectFacade, ProgramVariant variant)
			throws Exception {

		logger.info("Executing evosuite");
		//Generating Evosuite test class from the variant
		boolean executed = this.runEvosuite(variant, projectFacade);
		

		// CHECKING EVO OUTPUT
		String testEScodepath = projectFacade
				.getInDirWithPrefix(ConfigurationProperties.getProperty("evosuiteresultfolder"));
		File esPath = new File(testEScodepath);

		logger.info("Evo output: " + esPath);

		logger.info("Loading EvoTest model");
		// CREATING CLASSPATH FOR EXECUTING EVO TESTS
		String bytecodeSUTLocation = projectFacade.getOutDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);

		String classpathForModelizeEvoSuite = "";

		classpathForModelizeEvoSuite += (new File(ConfigurationProperties.getProperty("evosuitejar"))
				.getAbsolutePath());

		classpathForModelizeEvoSuite += (File.pathSeparator + projectFacade.getProperties().getDependenciesString());
		classpathForModelizeEvoSuite += (File.pathSeparator + bytecodeSUTLocation);

		String[] classpathForModelizeEvoSuiteTest = classpathForModelizeEvoSuite.split(File.pathSeparator);

		// We create the Spoon model for the evosuite test generated
		List<CtClass> classes = reificateEvoSuiteTest(testEScodepath, classpathForModelizeEvoSuiteTest);

		return classes;

	}

	public ProgramVariantValidationResult saveAndExecuteEvoSuite(ProjectRepairFacade projectFacade, ProgramVariant variant,
			List<CtClass> ctclasses) throws Exception{
			
		
				String classpathForCompileSpoon = "";
				classpathForCompileSpoon =
						projectFacade.getProperties().getDependenciesString()
						+ File.pathSeparator + 
						projectFacade.getOutDirWithPrefix(variant.currentMutatorIdentifier())
						+File.pathSeparator//
						+ new File("./lib/evosuite-1.0.3.jar").getAbsolutePath()
						+ File.pathSeparator + 
						projectFacade.getOutDirWithPrefix(variant.DEFAULT_ORIGINAL_VARIANT)
					;

				logger.info("Classpath "+classpathForCompileSpoon);
				String[] classpathForCreateModel = classpathForCompileSpoon.split(File.pathSeparator);

				// Compile evo classes from spoon model
				CompilationResult compilation = MutationSupporter.currentSupporter.getSpoonClassCompiler()
						.compileOnMemory(ctclasses, Converters.toURLArray(classpathForCreateModel));
				//assertFalse("Any bytecode", compilation.getByteCodes().values().isEmpty());

				if(!compilation.compiles() || compilation.getByteCodes().values().isEmpty()){
					logger.error("Error at compiling evotest classes");
					return null;
				}
				logger.debug("EvoSuite compiled ok "+compilation.compiles());
					
					
				//// Save compiled
				String outPutTest = projectFacade
						.getOutDirWithPrefix("/evosuite/evosuite-tests_" + variant.currentMutatorIdentifier());
				File fbyteEvo = new File(outPutTest);
				logger.info("Saving evotest bytecode at " + fbyteEvo);
				MutationSupporter.currentSupporter.getSpoonClassCompiler().saveByteCode(compilation, fbyteEvo);
			

				List<String> testToExecute = new ArrayList<>();
				for (CtClass evoTest : ctclasses) {
					if (!evoTest.getQualifiedName().endsWith("ESTest_scaffolding"))
						testToExecute.add(evoTest.getQualifiedName());
				}

				String classpathForRunTest = classpathForCompileSpoon + (File.pathSeparator) + outPutTest;
				logger.info("Process classpath " + classpathForRunTest);

				ProcessEvoSuiteValidator evoProcess = new ProcessEvoSuiteValidator();
				ProgramVariantValidationResult evoResult = evoProcess
						.executeRegressionTesting(Converters.toURLArray(classpathForRunTest.split(File.pathSeparator)), testToExecute);

				return evoResult;
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
		return out;
	}
}
