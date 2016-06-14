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
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.ProgramVariantValidationResult;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.bytecode.entities.CompilationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.validation.executors.WorkerThreadHelper;
import fr.inria.astor.core.validation.validators.ProcessEvoSuiteValidator;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

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
	 * Executing evosuite. For each Affected class from the program variant, we
	 * create the related TestUnit using evosuite
	 * 
	 * @param variant
	 * @param projectFacade
	 * @return
	 * @throws Exception
	 */
	public boolean runEvosuite(ProgramVariant variant, ProjectRepairFacade projectFacade) throws Exception {

		int nrGenerated = 0;
		List<URL> originalURL = new ArrayList<>(
				Arrays.asList(projectFacade.getClassPathURLforProgramVariant(ProgramVariant.DEFAULT_ORIGINAL_VARIANT)));

		logger.debug("---> creating evosuite tests");

		String outES = projectFacade.getInDirWithPrefix(ConfigurationProperties.getProperty("evosuiteresultfolder"));
		File fESout = new File(outES);
		fESout.mkdirs();

		URL[] SUTClasspath = originalURL.toArray(new URL[0]);

		List<CtType<?>> typesToProcess = null;
		if (ConfigurationProperties.getPropertyBool("evo_buggy_class")) {
			logger.info("Buggy classes");
			typesToProcess = variant.getAffectedClasses();
		} else {
			typesToProcess = new ArrayList<>(variant.getModifiedClasses());
		}

		logger.debug("Creating test cases using evosuite for: " + typesToProcess.size() + " classes");

		boolean reponse = true;
		int counter = 0;
		for (CtType<?> ctType : typesToProcess) {
			// generate a process for running evosuite
			String[] command = new String[] { "-class", ctType.getQualifiedName(), "-projectCP",
					urlArrayToString(SUTClasspath), //
					"-base_dir", outES, //
					"-Dglobal_timeout", ConfigurationProperties.getProperty("evosuitetimeout")
					// ,"-Djunit_check_on_separate_process=true"
			};
			logger.debug("Creating test for " + ctType.getQualifiedName() +" "+(++counter) + "/"+typesToProcess.size()) ;
			boolean sucess = runProcess(null, command);
			logger.debug("---> Evo OK? " + sucess + " ");
			reponse &= sucess;
			nrGenerated += (sucess)?1:0;
	
		}
		logger.debug("Evo end: generated "+nrGenerated+" over "+typesToProcess.size() );
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

		String javaPath = ConfigurationProperties.getProperty("jvm4evosuitetestexecution");
		javaPath += File.separator + "java";

		try {

			List<String> command = new ArrayList<String>();
			command.add(javaPath);
			command.add("-jar");
			command.add(new File(ConfigurationProperties.getProperty("evosuitejar")).getAbsolutePath());

			for (String arg : argumentsEvo) {
				command.add(arg);
			}
			logger.debug("EvoGenerate "+(command));
			ProcessBuilder pb = new ProcessBuilder(command.toArray(new String[command.size()]));
			pb.redirectOutput();
			pb.redirectErrorStream(true);
			pb.directory(new File((ConfigurationProperties.getProperty("location"))));
			p = pb.start();

			p.waitFor( (ConfigurationProperties.getPropertyInt("evosuitetimeout") * 2 * 1000), TimeUnit.MILLISECONDS);

		//	p.exitValue();

			String out = readOut(p);
			logger.debug(out);
			p.destroy();
			return true;
		} catch (IOException | InterruptedException | IllegalThreadStateException ex) {
			logger.error(ex.getMessage(), ex);
			if (p != null)
				p.destroy();
		}
		return false;
	}

	public List<CtClass> reificateEvoSuiteTest(String evoTestpath, String[] classpath) {
		logger.debug("Compiling ES code " + evoTestpath + " with CL " + Arrays.toString(classpath));
		logger.debug("Es dir content: " + Arrays.toString(new File(evoTestpath).listFiles()));
		MutationSupporter mutatorSupporter = MutationSupporter.currentSupporter;
		String codeLocation = evoTestpath;
		boolean saveOutput = false;
		try {
			mutatorSupporter.buildModel(codeLocation, classpath, saveOutput);
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
		logger.debug("CtClass from evosuite: #" + ESTestClasses.size());
		return ESTestClasses;
	}

	/**
	 * Given a program variant, we create using a program variant a) the
	 * Evosuite test b) the spoon model of the Evosuite test classes.
	 * 
	 * @param projectFacade
	 * @param variant
	 * @return
	 * @throws Exception
	 */
	public List<CtClass> createEvoTestModel(ProjectRepairFacade projectFacade, ProgramVariant variant)
			throws Exception {

		logger.info("Executing evosuite");
		// Generating Evosuite test class from the variant
		boolean executed = this.runEvosuite(variant, projectFacade);
		logger.debug("Evo result: " + executed);

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

	public ProgramVariantValidationResult saveAndExecuteEvoSuite(ProjectRepairFacade projectFacade,
			ProgramVariant variant, List<CtClass> ctclasses) throws Exception {

		String classpathForCompileSpoon = "";
		classpathForCompileSpoon = projectFacade.getProperties().getDependenciesString() + File.pathSeparator
				+ projectFacade.getOutDirWithPrefix(variant.currentMutatorIdentifier()) + File.pathSeparator//
				+ new File("./lib/evosuite-1.0.3.jar").getAbsolutePath() + File.pathSeparator
				+ projectFacade.getOutDirWithPrefix(variant.DEFAULT_ORIGINAL_VARIANT);

		logger.info("Classpath " + classpathForCompileSpoon);
		String[] classpathForCreateModel = classpathForCompileSpoon.split(File.pathSeparator);

		// Compile evo classes from spoon model
		CompilationResult compilation = MutationSupporter.currentSupporter.getSpoonClassCompiler()
				.compileOnMemory(ctclasses, Converters.toURLArray(classpathForCreateModel));
		// assertFalse("Any bytecode",
		// compilation.getByteCodes().values().isEmpty());

		if (!compilation.compiles() || compilation.getByteCodes().values().isEmpty()) {
			logger.error("Error at compiling evotest classes");
			return null;
		}
		logger.debug("EvoSuite compiled ok " + compilation.compiles());

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
		ProgramVariantValidationResult evoResult = evoProcess.executeRegressionTesting(
				Converters.toURLArray(classpathForRunTest.split(File.pathSeparator)), testToExecute);

		return evoResult;
	}

	private String readOut(Process p) {
		String out = "";
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				if (line.startsWith("Writing JUnit test case")) {
					out += line + "\n";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}

	private List<CtType> obtainBuggyButLatterModif(ProgramVariant variant){
		List<CtType> types = new ArrayList<>();
		for(CtType affected : variant.getAffectedClasses()){
			
			boolean add = false;
			for(CtType modif : variant.getModifiedClasses()){
				if(modif.getQualifiedName().equals(affected.getQualifiedName())){
					add = true;
					break;
				}
				if(add){
					types.add(affected);
				}
		}
		}
		return types;
	}
}
