package fr.inria.astor.core.faultlocalization;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FaultLocalizationUtils {

    private static final Logger logger = Logger.getLogger(FaultLocalizationUtils.class.getName());

    /**
     * It adds to the ignore list all failing TC that were not passed as argument. \
     * They are probably flaky test.
     *
     * @param failingTestCases
     */
    public static void addFlakyFailingTestToIgnoredList(List<String> failingTestCases, ProjectRepairFacade project) {
        //
        if (project.getProperties().getFailingTestCases() == null)
            return;
        List<String> originalFailing = project.getProperties().getFailingTestCases();
        List<String> onlyFailingInFL = new ArrayList<>(failingTestCases);
        // we remove those that we already know that fail
        onlyFailingInFL.removeAll(originalFailing);
        logger.debug("failing before " + onlyFailingInFL + ", added to the ignored list");
        String ignoredTestCases = ConfigurationProperties.getProperty("ignoredTestCases");
        for (String failingFL : onlyFailingInFL) {
            ignoredTestCases += File.pathSeparator + failingFL;
        }
        ConfigurationProperties.properties.setProperty("ignoredTestCases", ignoredTestCases);
    }
}
