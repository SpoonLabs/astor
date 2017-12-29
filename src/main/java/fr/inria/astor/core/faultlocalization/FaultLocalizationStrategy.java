package fr.inria.astor.core.faultlocalization;

import fr.inria.astor.core.loop.extension.AstorExtensionPoint;
import fr.inria.astor.core.setup.ProjectRepairFacade;

/**
 * API for fault localization
 * 
 * @author Matias Martinez
 *
 */
public interface FaultLocalizationStrategy extends AstorExtensionPoint {

	public FaultLocalizationResult searchSuspicious(ProjectRepairFacade searchSuspicious) throws Exception;

}
