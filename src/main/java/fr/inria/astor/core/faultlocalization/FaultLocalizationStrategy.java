package fr.inria.astor.core.faultlocalization;

import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.extension.AstorExtensionPoint;

/**
 * API for fault localization
 * 
 * @author Matias Martinez
 *
 */
public interface FaultLocalizationStrategy extends AstorExtensionPoint {

	public FaultLocalizationResult searchSuspicious(ProjectRepairFacade projectToRepair) throws Exception;

}
