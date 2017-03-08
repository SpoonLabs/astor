package fr.inria.astor.core.faultlocalization;

import java.util.List;
import java.util.Set;

import fr.inria.astor.core.loop.extension.AstorExtensionPoint;

/**
 * API for fault localization
 * @author Matias Martinez
 *
 */
public interface FaultLocalizationStrategy extends AstorExtensionPoint {

	
	public FaultLocalizationResult searchSuspicious(
			String location, 
			List<String> testsToExecute, 
			List<String> toInstrument,
			Set<String> cp, 
			String srcFolder) throws Exception ;
	
}
