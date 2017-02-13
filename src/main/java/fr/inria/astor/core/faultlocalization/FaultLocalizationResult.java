package fr.inria.astor.core.faultlocalization;

import java.util.List;

import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;

/**
 * Stores the result of a fault localization process
 * @author Matias Martinez
 *
 */
public class FaultLocalizationResult {

	
	List<SuspiciousCode> candidates;
	List<String> failingTestCases;
	
	public FaultLocalizationResult(List<SuspiciousCode> candidates, List<String> failingTestCases) {
		super();
		this.candidates = candidates;
		this.failingTestCases = failingTestCases;
	}

	public List<SuspiciousCode> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<SuspiciousCode> candidates) {
		this.candidates = candidates;
	}

	public List<String> getFailingTestCases() {
		return failingTestCases;
	}

	public void setFailingTestCases(List<String> failingTestCases) {
		this.failingTestCases = failingTestCases;
	}

}
