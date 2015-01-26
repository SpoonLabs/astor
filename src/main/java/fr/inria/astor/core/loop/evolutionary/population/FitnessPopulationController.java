package fr.inria.astor.core.loop.evolutionary.population;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.ProgramVariantValidation;
/**
 * Controls the population of program by the fitness values.
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class FitnessPopulationController implements PopulationController{
	
	private Logger log = Logger.getLogger(Thread.currentThread().getName());
	
	protected FitnessComparator comparator = new FitnessComparator();
		
	/**
	 * Select the program instances that will pass to the next generation. The
	 * rest are discarded.
	 * 
	 * @param instances2
	 * @param childVariants
	 */
	public List<ProgramVariant>  selectProgramVariantsForNextGeneration(List<ProgramVariant> parentVariants,
			List<ProgramVariant> childVariants,List<ProgramVariant> allSolutions,int maxNumberInstances) {
		childVariants.addAll(parentVariants);
		int totalInstances = childVariants.size();
		
		List<ProgramVariant> newPop = new ArrayList<ProgramVariant>();
		
		List<ProgramVariant> genSolutions = new ArrayList<ProgramVariant>();
		
		//Sort the population according to fitness		
		Collections.sort(childVariants, comparator);
		
		String variantsIds = "",solutionId="";
		for (ProgramVariant programVariant : childVariants) {
			variantsIds += programVariant.getId()+"(f="+programVariant.getFitness()+")" + ", ";
			if(programVariant.getFitness() == 0){
				genSolutions.add(programVariant);
				solutionId += programVariant.getId()+"(f="+programVariant.getFitness()+")" + ", ";
			}
		}
		
		log.info("Solutions: " +  "--> (" + solutionId + ")");

		childVariants.removeAll(genSolutions);
				
		log.info("Variants to next generation from: " + totalInstances + "--> (" + variantsIds + ")");

		
		int min = (childVariants.size() > maxNumberInstances) ? maxNumberInstances : childVariants.size();
		newPop.addAll(childVariants.subList(0, min));
		
		variantsIds = "";
		for (ProgramVariant programVariant : newPop) {
			variantsIds += programVariant.getId()+"(f="+programVariant.getFitness()+")" + ", ";
		}
		log.info("Selected to next generation: " + totalInstances + "--> (" + variantsIds + ")");

		allSolutions.addAll(genSolutions);
		return newPop;

	}

/**
 *In this case the fitness value is associate to the failures: LESS FITNESS is better. 
 */
	@Override
	public double getFitnessValue(ProgramVariant variant, ProgramVariantValidation result) {
		
		return /*result.getRunCount() -*/ result.getFailureCount();
	}
	
	/**
	 * Comparator to sort the variant in ascending mode according to the fitness values
	 * @author Matias Martinez,  matias.martinez@inria.fr
	 *
	 */
	public class FitnessComparator implements Comparator<ProgramVariant>{

		@Override
		public int compare(ProgramVariant o1, ProgramVariant o2) {
			
			return Double.compare(o1.getFitness(),o2.getFitness());
		}
		
	}
}
