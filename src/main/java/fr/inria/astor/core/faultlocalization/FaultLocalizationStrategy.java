package fr.inria.astor.core.faultlocalization;

import java.util.List;

import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.extension.AstorExtensionPoint;

/**
 * API for fault localization
 * 
 * @author Matias Martinez
 *
 */
public interface FaultLocalizationStrategy extends AstorExtensionPoint {

	public FaultLocalizationResult searchSuspicious(ProjectRepairFacade projectToRepair, List<String> testToRun)
			throws Exception;
	
	public abstract List<String> findTestCasesToExecute(ProjectRepairFacade projectFacade);

}
