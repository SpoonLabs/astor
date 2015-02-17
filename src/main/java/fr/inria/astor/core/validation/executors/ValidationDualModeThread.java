package fr.inria.astor.core.validation.executors;

import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.ProgramVariantValidationResult;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.validation.validators.ProgramVariantValidator;
/**
 * This thread executes the test and set the fitness value.
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class ValidationDualModeThread extends Thread {

	private Logger log = Logger.getLogger(ValidationDualModeThread.class.getName());

	public boolean sucessfull = false;
	
	ProjectRepairFacade projectFacade;
	
	ProgramVariantValidator programVariantValidator = new ProgramVariantValidator();
	
	ProgramVariant mutatedVariant;
	
	public boolean modeFirst;
	
	public boolean finish = false;
	
	public ProgramVariantValidationResult result = null;
	
	/**
	 * 
	 * @param projectFacade
	 * @param programVariantValidator
	 * @param populationControler
	 * @param mutatedVariant
	 */
	public ValidationDualModeThread(ProjectRepairFacade projectFacade, ProgramVariant mutatedVariant, boolean modeFirst) {
		this.projectFacade = projectFacade;
		this.mutatedVariant = mutatedVariant;
		this.modeFirst = modeFirst;
	}

	@Override
	    public void run(){
		
				sucessfull = false;
				//fitness in advance
			//	mutatedVariant.setFitness(Double.MAX_VALUE);
				
				//Get test cases to execute.
				List<String> failingCases = projectFacade.getProperties().getFailingTestCases();
				String testSuiteClassName = projectFacade.getProperties().getTestSuiteClassName();
			
				result = new ProgramVariantValidationResult();
				try {
				if(modeFirst){
					result = this.programVariantValidator.validateVariantFirstPhases(failingCases,testSuiteClassName);
				}
				else{
					//Use this one
					result = this.programVariantValidator.validateVariantTwoPhases(failingCases,testSuiteClassName);
					
				}
	
			
			/*	log.info("Fitness of instance #" + mutatedVariant.getId() + ": " + fitness + " (Totals: "
						+ result.getRunCount() + ", failed: " + result.getFailureCount() + ")");
*/
				sucessfull =  result.wasSuccessful();
				
				}
				catch (Throwable e) {
					
					e.printStackTrace();
				}
	            finish = true;
				synchronized(this){
					 notify();
				 }
         
	        }

	
}
