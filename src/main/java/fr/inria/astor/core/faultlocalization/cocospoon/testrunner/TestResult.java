package fr.inria.astor.core.faultlocalization.cocospoon.testrunner;


/**
 * Created by bdanglot on 10/3/16.
 */
public interface TestResult {

	TestCase getTestCase();

	boolean isSuccessful();

}
