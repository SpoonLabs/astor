package fr.inria.astor.core.faultlocalization.flacoco;

import fr.inria.astor.core.faultlocalization.FaultLocalizationResult;
import fr.inria.astor.core.faultlocalization.FaultLocalizationStrategy;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.spoonlabs.flacoco.api.Flacoco;
import fr.spoonlabs.flacoco.api.Suspiciousness;
import fr.spoonlabs.flacoco.core.config.FlacocoConfig;
import fr.spoonlabs.flacoco.core.test.TestDetector;
import fr.spoonlabs.flacoco.core.test.TestMethod;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlacocoFaultLocalization implements FaultLocalizationStrategy {

	Logger logger = Logger.getLogger(FlacocoFaultLocalization.class);

	@Override
	public FaultLocalizationResult searchSuspicious(ProjectRepairFacade projectToRepair, List<String> testToRun) throws Exception {
		setupFlacocoConfig(projectToRepair);
		Flacoco flacoco = new Flacoco();

		Map<String, Suspiciousness> susp = flacoco.runDefault();

		List<SuspiciousCode> candidates = new ArrayList<>();

		int i = 0;
		for (String line : susp.keySet()) {
			double suspvalue = susp.get(line).getScore();

			String className = line.split("@-@")[0].replace("/", ".");
			Integer lineNumber = Integer.parseInt(line.split("@-@")[1]);

			logger.info("Suspicious: " + ++i + " line " + className + " l: " + lineNumber + ", susp " + suspvalue);

			SuspiciousCode sc = new SuspiciousCode(className, null, lineNumber, suspvalue, null);
			candidates.add(sc);
		}

		FaultLocalizationResult result = new FaultLocalizationResult(
				candidates,
				susp.values().stream()
						.map(Suspiciousness::getFailingTestCases)
						.flatMap(Collection::stream)
						.map(TestMethod::getFullyQualifiedClassName)
						.distinct()
						.collect(Collectors.toList())
		);

		if (projectToRepair.getProperties().getFailingTestCases().isEmpty()) {
			logger.debug("Failing test cases was not pass as argument: we use failings from FL "
					+ result.getFailingTestCases());
			projectToRepair.getProperties().setFailingTestCases(result.getFailingTestCases());
		}

		// TODO: Add support for other Astor options

		return result;
	}

	@Override
	public List<String> findTestCasesToExecute(ProjectRepairFacade projectFacade) {
		setupFlacocoConfig(projectFacade);
		return new TestDetector().findTests().stream().flatMap(x -> x.getTestMethods().stream())
				.map(TestMethod::getFullyQualifiedClassName).distinct().collect(Collectors.toList());
	}

	private static void setupFlacocoConfig(ProjectRepairFacade projectFacade) {
		FlacocoConfig config = FlacocoConfig.getInstance();
		config.setProjectPath(projectFacade.getProperties().getOriginalProjectRootDir());
		config.setClasspath(projectFacade.getProperties().getDependenciesString());
		config.setSrcJavaDir(projectFacade.getProperties().getOriginalDirSrc());
		config.setSrcTestDir(projectFacade.getProperties().getTestDirSrc());
		config.setBinJavaDir(projectFacade.getProperties().getOriginalAppBinDir());
		config.setBinTestDir(projectFacade.getProperties().getOriginalTestBinDir());
		config.setThreshold(ConfigurationProperties.getPropertyDouble("flthreshold"));
	}
}
