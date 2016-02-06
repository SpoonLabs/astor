package fr.inria.astor.core.faultlocalization;

import java.util.List;
import java.util.Set;

import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
/**
 * API for fault localization
 * @author Matias Martinez
 *
 */
public interface IFaultLocalization {

	
	public List<SuspiciousCode> searchSuspicious(
			String location, 
			List<String> testsToExecute, 
			List<String> toInstrument,
			Set<String> cp, 
			String srcFolder) throws Exception ;
	
}
