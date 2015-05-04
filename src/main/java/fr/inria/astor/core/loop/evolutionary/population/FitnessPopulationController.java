package fr.inria.astor.core.loop.evolutionary.population;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.ProgramVariantValidationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;

/**
 * Controls the population of program by the fitness values.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class FitnessPopulationController implements PopulationController {

	private Logger log = Logger.getLogger(Thread.currentThread().getName());

	protected FitnessComparator comparator = new FitnessComparator();

	/**
	 * Select the program instances that will pass to the next generation. The
	 * rest are discarded.
	 * 
	 * @param instances2
	 * @param childVariants
	 */
	public List<ProgramVariant> selectProgramVariantsForNextGeneration(List<ProgramVariant> parentVariants,
			List<ProgramVariant> childVariants, List<ProgramVariant> allSolutions, int maxNumberInstances) {
		
		if (ConfigurationProperties.getProperty("reintroduce").contains("parents")) {
			childVariants.addAll(parentVariants);
		}
		int totalInstances = childVariants.size();

		List<ProgramVariant> newPop = new ArrayList<ProgramVariant>();

		List<ProgramVariant> genSolutions = new ArrayList<ProgramVariant>();

		Collections.sort(childVariants, comparator);

		String variantsIds = "", solutionId = "";

		for (ProgramVariant programVariant : childVariants) {
			variantsIds += programVariant.getId() + "(f=" + programVariant.getFitness() + ")" + ", ";
			if (programVariant.isSolution()) {
				genSolutions.add(programVariant);
				solutionId += programVariant.getId() + "(SOLUTION)(f=" + programVariant.getFitness() + ")" + ", ";
			}
		}

		log.debug("\nEnd analysis generation - \nSolutions found:" + "--> (" + solutionId + ")");

		boolean removed = childVariants.removeAll(genSolutions);

		log.debug("Variants to next generation from: " + totalInstances + "-->IDs: (" + variantsIds + ")");

		int min = (childVariants.size() > maxNumberInstances) ? maxNumberInstances : childVariants.size();
		newPop.addAll(childVariants.subList(0, min));

		variantsIds = "";
		for (ProgramVariant programVariant : newPop) {
			variantsIds += programVariant.getId() + "(f=" + programVariant.getFitness() + ")" + ", ";
		}
		log.debug("Selected to next generation: IDs" + totalInstances + "--> (" + variantsIds + ")");

		allSolutions.addAll(genSolutions);
		return newPop;

	}

	/**
	 * In this case the fitness value is associate to the failures: LESS FITNESS
	 * is better.
	 */
	@Override
	public double getFitnessValue(ProgramVariant variant, ProgramVariantValidationResult result) {

		return result.getFailureCount();
	}
	
	@Override
	public double getMaxFitnessValue() {

		return Double.MAX_VALUE;
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
			return Integer.compare(o2.getId(), o1.getId());
		}

	}


}
