package fr.inria.astor.core.loop.population;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ConfigurationProperties;

/**
 * Controls the population of program by the fitness values.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class TestCaseBasedFitnessPopulationController implements PopulationController {

	private Logger log = Logger.getLogger(Thread.currentThread().getName());

	public FitnessComparator comparator = new FitnessComparator();

	/**
	 * Select the program instances that will pass to the next generation. The
	 * rest are discarted.
	 * 
	 * @param instances2
	 * @param childVariants
	 */
	public List<ProgramVariant> selectProgramVariantsForNextGeneration(List<ProgramVariant> parentVariants,
			List<ProgramVariant> childVariants, int populationSize, ProgramVariantFactory variantFactory, ProgramVariant original, 
			int generation) {
		
		List<ProgramVariant> solutionsFromGeneration = new ArrayList<ProgramVariant>();
		
		List<ProgramVariant> newPopulation = new ArrayList<>(childVariants);
		
		if (ConfigurationProperties.getProperty("reintroduce").contains("parents")) {
			newPopulation.addAll(parentVariants);
		}
		
		int totalInstances = newPopulation.size();

	
		Collections.sort(newPopulation, comparator);

		String variantsIds = "";

		for (ProgramVariant programVariant : newPopulation) {
			variantsIds += programVariant.getId() + "(f=" + programVariant.getFitness() + ")" ;
			if(programVariant.isSolution()){
				solutionsFromGeneration.add(programVariant);
				variantsIds += "[SOL]";
			}
			variantsIds += ", ";
		}
		
		log.debug("Variants to next generation from: " + totalInstances + "-->IDs: (" + variantsIds + ")");

		if(!ConfigurationProperties.getProperty("reintroduce").contains("solutions")){
			newPopulation.removeAll(solutionsFromGeneration);
		}
			
		
		int min = (newPopulation.size() > populationSize) ? populationSize : newPopulation.size();
		newPopulation= newPopulation.subList(0, min);

		variantsIds = "";
		for (ProgramVariant programVariant : newPopulation) {
			variantsIds += programVariant.getId() + "(f=" + programVariant.getFitness() + ")" + ", ";
		}
		log.debug("Selected to next generation: IDs" + totalInstances + "--> (" + variantsIds + ")");
		//
		
		if (ConfigurationProperties.getProperty("reintroduce").contains("original")) {
			if (!newPopulation.isEmpty()) {
				// now replace for the "worse" child
				newPopulation.remove(newPopulation.size() - 1);
				}
		}
		
		
		while(newPopulation.size() < populationSize){
			ProgramVariant originalVariant = variantFactory.createProgramVariantFromAnother(original, generation);
			originalVariant.getOperations().clear();
			originalVariant.setParent(null);
			newPopulation.add(originalVariant);
		}
		
			
		return newPopulation;

	}



	/**
	 * Comparator to sort the variant in ascending mode according to the fitness
	 * values
	 * 
	 * @author Matias Martinez, matias.martinez@inria.fr
	 *
	 */
	public class FitnessComparator implements Comparator<ProgramVariant> {

		@Override
		public int compare(ProgramVariant o1, ProgramVariant o2) {
			int fitness = Double.compare(o1.getFitness(), o2.getFitness());
			if (fitness != 0)
				return fitness;
			//inversed, we prefer have child variant first
			return Integer.compare(o1.getId(), o2.getId());
		}

	}


}
