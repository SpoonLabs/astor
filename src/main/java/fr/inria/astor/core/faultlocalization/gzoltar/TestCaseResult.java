package fr.inria.astor.core.faultlocalization.gzoltar;

/**
 * Representation of a test case.
 * 
 * @author Matias Martinez
 *
 */
public class TestCaseResult {
	String testCaseCompleteName;
	String testCaseName;
	String testCaseClass;
	String trance;
	Boolean correct;

	public TestCaseResult(String testCaseCompleteName, boolean correct) {
		super();
		this.testCaseCompleteName = testCaseCompleteName;
		this.correct = correct;
	}

	public String getTestCaseCompleteName() {
		return testCaseCompleteName;
	}

	public void setTestCaseCompleteName(String testCaseCompleteName) {
		this.testCaseCompleteName = testCaseCompleteName;
	}

	public String getTestCaseName() {
		return testCaseName;
	}

	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	public String getTestCaseClass() {
		return testCaseClass;
	}

	public void setTestCaseClass(String testCaseClass) {
		this.testCaseClass = testCaseClass;
	}

	public String getTrance() {
		return trance;
	}

	public void setTrance(String trance) {
		this.trance = trance;
	}

	public boolean isCorrect() {

		return this.correct;
	}

}
