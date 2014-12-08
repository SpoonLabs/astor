package fr.inria.astor.core.validation;

import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.ProgramVariantValidation;
import fr.inria.astor.core.loop.evolutionary.population.PopulationController;
import fr.inria.astor.core.loop.evolutionary.population.ProgramValidator;
import fr.inria.astor.core.setup.ProjectRepairFacade;
/**
 * This thread executes the test and set the fitness value.
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class ValidationThread extends Thread {

	private Logger log = Logger.getLogger(ValidationThread.class.getName());

	boolean sucessfull = false;
	
	ProjectRepairFacade projectFacade;
	ProgramValidator programVariantValidator;
	PopulationController populationControler; 
	ProgramVariant mutatedVariant;
	/**
	 * 
	 * @param projectFacade
	 * @param programVariantValidator
	 * @param populationControler
	 * @param mutatedVariant
	 */
	public ValidationThread(ProjectRepairFacade projectFacade, ProgramValidator programVariantValidator, PopulationController populationControler, ProgramVariant mutatedVariant) {
		this.projectFacade = projectFacade;
		this.programVariantValidator = programVariantValidator;
		this.populationControler = populationControler;
		this.mutatedVariant = mutatedVariant;
	}

	@Override
	    public void run(){
	       	//Get test cases to execute.
				List<String> failingCases = projectFacade.getProperties().getFailingTestCases();
				String testSuiteClassName = projectFacade.getProperties().getTestSuiteClassName();
				
				ProgramVariantValidation result;
				try {
					result = this.programVariantValidator.validateVariantTwoPhases(failingCases,testSuiteClassName);
			

				// putting fitness into program variant
				double fitness = populationControler.getFitnessValue(mutatedVariant, result);
				mutatedVariant.setFitness(fitness);

				// TODO: result has ignore count
				log.info("Fitness of instance #" + mutatedVariant.getId() + ": " + fitness + " (Totals: "
						+ result.getRunCount() + ", failed: " + result.getFailureCount() + ")");

				sucessfull =  result.wasSuccessful();
				
				}// catch (FileNotFoundException | ClassNotFoundException | InitializationError e) {
				catch (Throwable e) {
					
					e.printStackTrace();
				}
	            //
				 synchronized(this){
					 notify(); 
				 }
	           
	        }
	   
}
