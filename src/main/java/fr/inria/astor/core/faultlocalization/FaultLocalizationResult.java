package fr.inria.astor.core.faultlocalization;

import java.util.List;

import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;

/**
 * Stores the result of a fault localization process
 *
 * @author Matias Martinez
 */
public class FaultLocalizationResult {

	List<SuspiciousCode> candidates;
	List<String> failingTestCases;
	List<String> executedTestCases;

	public FaultLocalizationResult(List<SuspiciousCode> candidates) {
		super();
		this.candidates = candidates;

	}

	public FaultLocalizationResult(List<SuspiciousCode> candidates, List<String> failingTestCases) {
		super();
		this.candidates = candidates;
		this.failingTestCases = failingTestCases;
	}

	public FaultLocalizationResult(List<SuspiciousCode> candidates, List<String> failingTestCases,
			List<String> executedTestCases) {
		super();
		this.candidates = candidates;
		this.failingTestCases = failingTestCases;
		this.executedTestCases = executedTestCases;
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

	@Override
	public String toString() {
		return "FaultLocalizationResult{" + "candidates=" + candidates + ", failingTestCases=" + failingTestCases + '}';
	}

	public List<String> getExecutedTestCases() {
		return executedTestCases;
	}

	public void setExecutedTestCases(List<String> executedTestCases) {
		this.executedTestCases = executedTestCases;
	}
}
