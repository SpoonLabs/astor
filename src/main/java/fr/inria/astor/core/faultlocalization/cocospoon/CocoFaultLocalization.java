package fr.inria.astor.core.faultlocalization.cocospoon;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import fil.iagl.opl.cocospoon.processors.WatcherProcessor;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.faultlocalization.FaultLocalizationResult;
import fr.inria.astor.core.faultlocalization.FaultLocalizationStrategy;
import fr.inria.astor.core.faultlocalization.cocospoon.code.SourceLocation;
import fr.inria.astor.core.faultlocalization.cocospoon.code.StatementSourceLocation;
import fr.inria.astor.core.faultlocalization.cocospoon.metrics.Ochiai;
import fr.inria.astor.core.faultlocalization.cocospoon.testrunner.TestResult;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.faultlocalization.gzoltar.TestCaseResult;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.bytecode.classloader.BytecodeClassLoader;
import fr.inria.astor.core.manipulation.bytecode.compiler.SpoonClassCompiler;
import fr.inria.astor.core.manipulation.bytecode.entities.CompilationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.FinderTestCases;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.util.Converters;
import spoon.processing.ProcessInterruption;
import spoon.processing.ProcessingManager;
import spoon.reflect.declaration.CtType;
import spoon.support.RuntimeProcessingManager;

/**
 * 
 * @author Matias Martinez
 *
 */
public class CocoFaultLocalization implements FaultLocalizationStrategy {

	protected static Logger log = Logger.getLogger(Thread.currentThread().getName());

	@Override
	public FaultLocalizationResult searchSuspicious(ProjectRepairFacade project, List<String> testregression)
			throws Exception {

		MutationSupporter.cleanFactory();

		initModel(project);

		// Parser
		parseModel();

		// Compile

		CompilationResult cresults = compile(project);

		// Create class loader
		BytecodeClassLoader customClassLoader = createClassLoader(cresults, project);

		// call cocoa with tests
		CocoSpoonEngineFaultLocalizer coco4Astor = new CocoSpoonEngineFaultLocalizer(new Ochiai());
		coco4Astor.runTests(testregression.toArray(new String[0]), customClassLoader, project);

		// Collecting failing test cases
		List<String> testsfailing = coco4Astor.getResultsPerNameOfTest().entrySet().stream()
				.filter(e -> e.getValue() == false).collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()))
				.keySet().stream().collect(Collectors.toList());

		Map<SourceLocation, List<TestResult>> stc = coco4Astor.getTestListPerStatement();
		List<? extends StatementSourceLocation> suspstatement = coco4Astor.getStatements();

		MutationSupporter.cleanFactory();

		Double thr = ConfigurationProperties.getPropertyDouble("flthreshold");

		List<SuspiciousCode> candidates = new ArrayList<>();
		for (StatementSourceLocation statementSourceLocation : suspstatement) {
			if ((!ConfigurationProperties.getPropertyBool("limitbysuspicious")
					|| (statementSourceLocation.getSuspiciousness() >= thr))) {
				SuspiciousCode spc = new SuspiciousCode(statementSourceLocation.getLocation().getRootClassName(), null,
						statementSourceLocation.getSuspiciousness());
				spc.setLineNumber(statementSourceLocation.getLocation().getLineNumber());
				candidates.add(spc);
				List<TestResult> testCoverLocation = stc.get(statementSourceLocation.getLocation());

				// Transforming the representation of test
				List<TestCaseResult> testcaseresult = new ArrayList<>();

				for (TestResult testResult : testCoverLocation) {
					testcaseresult
							.add(new TestCaseResult(testResult.getTestCase().toString(), testResult.isSuccessful()));
				}

				spc.setCoveredByTests(testcaseresult);
			}
		}
		int maxSuspCandidates = ConfigurationProperties.getPropertyInt("maxsuspcandidates");

		if (!ConfigurationProperties.getPropertyBool("considerzerovaluesusp")) {
			candidates.removeIf(susp -> (susp.getSuspiciousValue() == 0));
		}
		// We select the best X candidates.
		int max = (candidates.size() < maxSuspCandidates) ? candidates.size() : maxSuspCandidates;
		candidates = candidates.subList(0, max);

		FaultLocalizationResult flresults = new FaultLocalizationResult(candidates, testsfailing);

		return flresults;
	}

	public CompilationResult compile(ProjectRepairFacade projectFacade) throws MalformedURLException {

		SpoonClassCompiler scc = new SpoonClassCompiler(MutationSupporter.getFactory());

		String classpath = projectFacade.getProperties().getDependenciesString();
		classpath += File.pathSeparator + System.getProperty("java.class.path") + // For
																					// dependency
				File.pathSeparator +
				// Workarroud
				projectFacade.getOutDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
		String[] cpArray = classpath.split(File.pathSeparator);

		System.out.println("Classpath " + Arrays.toString(cpArray));
		return scc.compile(MutationSupporter.getFactory().Class().getAll(), Converters.toURLArray(cpArray));
	}

	public BytecodeClassLoader createClassLoader(CompilationResult cresult, ProjectRepairFacade projectFacade)
			throws MalformedURLException {

		String classpath = projectFacade.getProperties().getDependenciesString();
		classpath += File.pathSeparator + System.getProperty("java.class.path") + File.pathSeparator +
		// Workarroud
				projectFacade.getOutDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);

		BytecodeClassLoader bclassloader = new BytecodeClassLoader(
				Converters.toURLArray(classpath.split(File.pathSeparator)));
		bclassloader.setBytecodes(cresult.getByteCodes());
		return bclassloader;
	}

	public void parseModel() {
		WatcherProcessor processor = new WatcherProcessor();
		ProcessingManager manager = new RuntimeProcessingManager(MutationSupporter.getFactory());
		manager.addProcessor(processor);

		for (CtType<?> modelledClass : MutationSupporter.getFactory().Type().getAll()) {

			try {
				manager.process(modelledClass);
			} catch (ProcessInterruption e) {
				continue;
			}
		}
	}

	// Code properties.
	public void initModel(ProjectRepairFacade projectFacade) throws Exception {

		MutationSupporter.currentSupporter.buildSpoonModel(projectFacade);

	}

	@Override
	public List<String> findTestCasesToExecute(ProjectRepairFacade projectFacade) {
		List<String> testCasesToRun = FinderTestCases.findJUnit4XTestCasesForRegression(projectFacade);

		return testCasesToRun;
	}

}
