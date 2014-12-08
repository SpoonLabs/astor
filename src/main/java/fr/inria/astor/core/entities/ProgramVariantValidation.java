package fr.inria.astor.core.entities;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Result;
/**
 * Result of the program validation (i.e. execution of the junit test)
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class ProgramVariantValidation {
	/**
	 * Result of the regression Test
	 */
	private Result regressionResult = null;
	
	/**
	 * Result of the original failing test cases
	 */
	private List<Result> failingResults = new ArrayList<Result>();
	
	private boolean resultSuccess = false;
	
	public Result getRegressionResult() {
		return regressionResult;
	}
	public void setRegressionResult(Result regressionResult) {
		this.regressionResult = regressionResult;
	}
	public List<Result> getFailingResults() {
		return failingResults;
	}
	public void setFailingResults(List<Result> failingResults) {
		this.failingResults = failingResults;
	}
	public boolean isResult() {
		return resultSuccess;
	}
	public void setResult(boolean result) {
		this.resultSuccess = result;
	}
	
	public boolean wasSuccessful(){
		for(Result r : failingResults){
			if(!r.wasSuccessful()){
				return false;
			}
		}
		if(regressionResult != null){
			return regressionResult.wasSuccessful();
		}
		return true;
	}
	public int getRunCount(){
		int runs = 0;
		for(Result r : failingResults){
			runs+= r.getRunCount();
		}
		if(regressionResult != null){
			runs+= regressionResult.getRunCount();
		}
		return runs;
		
	}
	
	public int getFailureCount(){
		int runs = 0;
		for(Result r : failingResults){
			runs+= r.getFailureCount();
		}
		if(regressionResult != null){
			runs+= regressionResult.getFailureCount();
		}
		return runs;
		
	}
	
}
