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
	List<String> failingTestCasesClasses;
	List<String> failingTestCasesMethods;
	List<String> executedTestCasesMethods;

	public FaultLocalizationResult(List<SuspiciousCode> candidates) {
		super();
		this.candidates = candidates;

	}

	public FaultLocalizationResult(List<SuspiciousCode> candidates, List<String> failingTestCases) {
		super();
		this.candidates = candidates;
		this.failingTestCasesClasses = failingTestCases;
	}

	public FaultLocalizationResult(List<SuspiciousCode> candidates, List<String> failingTestCases,
			List<String> executedTestCases) {
		super();
		this.candidates = candidates;
		this.failingTestCasesClasses = failingTestCases;
		this.executedTestCasesMethods = executedTestCases;
	}

	public List<SuspiciousCode> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<SuspiciousCode> candidates) {
		this.candidates = candidates;
	}

	public List<String> getFailingTestCasesClasses() {
		return failingTestCasesClasses;
	}

	public void setFailingTestCasesClasses(List<String> failingTestCases) {
		this.failingTestCasesClasses = failingTestCases;
	}

	public List<String> getExecutedTestCasesMethods() {
		return executedTestCasesMethods;
	}

	public void setExecutedTestCasesMethods(List<String> executedTestCases) {
		this.executedTestCasesMethods = executedTestCases;
	}

	public List<String> getFailingTestCasesMethods() {
		return failingTestCasesMethods;
	}

	public void setFailingTestCasesMethods(List<String> failingTestCasesMethods) {
		this.failingTestCasesMethods = failingTestCasesMethods;
	}

	@Override
	public String toString() {
		return "FaultLocalizationResult{" + "candidates=" + candidates + ", failingTestCases=" + failingTestCasesClasses
				+ '}';
	}

}
