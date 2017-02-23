package fr.inria.astor.core.validation.validators;

import java.util.HashMap;
import java.util.Map;

import fr.inria.astor.core.entities.TestCaseVariantValidationResult;
/**
 * Contains a set of Validation Results
 * @author Matias Martinez
 *
 */
public class CompoundValidationResult implements  TestCaseVariantValidationResult {

	protected Map<String,TestCaseVariantValidationResult> validations = new HashMap<>();
	
	public CompoundValidationResult() {
		
	}
	

	public void addValidation(String mode, TestCaseVariantValidationResult p){
		this.validations.put(mode, p);
	}
	
	
	public  TestCaseVariantValidationResult getValidation(String mode){
		return this.validations.get(mode);
	}

	@Override
	public boolean isSuccessful() {
		for (TestCaseVariantValidationResult  pv : this.validations.values()) {
			if(!pv.isSuccessful()){
				return false;
			}
		}
		return true;
	};
	
	
	public String toString(){
		String r = "";
		for(String mode: this.validations.keySet()){
			
				r+= "\n"+mode+": "+ this.getValidation(mode).toString();
			}
		return r;
	}


	@Override
	public int getFailureCount() {
		if( this.validations.isEmpty())
			return 0;
		
		int count = 0;
		for (TestCaseVariantValidationResult  pv : this.validations.values()) {
			if(pv != null)
				count+= pv.getFailureCount();
		}
		
		return count;
	}


	@Override
	public boolean isRegressionExecuted() {
		
		return false;
	}


	@Override
	public void setRegressionExecuted(boolean regressionExecuted) {
	
		
	}


	@Override
	public int getPassingTestCases() {
		int count = 0;
		for (TestCaseVariantValidationResult  pv : this.validations.values()) {
			count+= pv.getPassingTestCases();
		}
		
		return count;
	}
	
	@Override
	public int getCasesExecuted() {
		
		return getPassingTestCases() + getFailureCount();
	}
	

}
