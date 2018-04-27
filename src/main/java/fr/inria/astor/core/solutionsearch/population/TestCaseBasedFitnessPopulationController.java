package fr.inria.astor.core.solutionsearch.population;

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
			List<ProgramVariant> childVariants, int populationSize, ProgramVariantFactory variantFactory,
			ProgramVariant original, int generation) {

		List<ProgramVariant> solutionsFromGeneration = new ArrayList<ProgramVariant>();

		// It defines the new population (i.e., the population of the next
		// generation) with all new program variants created in the current
		// population.
		List<ProgramVariant> newPopulation = new ArrayList<>(childVariants);

		// If parents can be evolved, we add it to the new population.
		// Otherwise, they are discarded.
		if (ConfigurationProperties.getProperty("reintroduce").contains(PopulationConformation.PARENTS.toString())) {
			newPopulation.addAll(parentVariants);
		}

		int totalInstances = newPopulation.size();

		// The new population is sorted according to the fitness.
		Collections.sort(newPopulation, comparator);

		String variantsIds = "";

		// Only prints the population.
		for (ProgramVariant programVariant : newPopulation) {
			variantsIds += programVariant.getId() + "(f=" + programVariant.getFitness() + ")";
			if (programVariant.isSolution()) {
				solutionsFromGeneration.add(programVariant);
				variantsIds += "[SOL]";
			}
			variantsIds += ", ";
		}

		log.debug("Variants to next generation from: " + totalInstances + "-->IDs: (" + variantsIds + ")");

		// If solution can be evolved, we keep them, otherwise, they are
		// discarded.
		if (!ConfigurationProperties.getProperty("reintroduce").contains(PopulationConformation.SOLUTIONS.toString())) {
			newPopulation.removeAll(solutionsFromGeneration);
		}

		// We take the best X variants, where X is the size of the population
		int min = (newPopulation.size() > populationSize) ? populationSize : newPopulation.size();
		newPopulation = newPopulation.subList(0, min);

		variantsIds = "";
		for (ProgramVariant programVariant : newPopulation) {
			variantsIds += programVariant.getId() + "(f=" + programVariant.getFitness() + ")" + ", ";
		}
		log.debug("Selected to next generation: IDs" + totalInstances + "--> (" + variantsIds + ")");
		//
		// If the original variant has to be reintroduced on each generation, we
		// remove the "worst" variant from those selected in the previous step.
		if (ConfigurationProperties.getProperty("reintroduce").contains(PopulationConformation.ORIGINAL.toString())) {
			if (!newPopulation.isEmpty()) {
				// now replace for the "worse" child
				newPopulation.remove(newPopulation.size() - 1);
			}
		}

		// if the number of variant after L77 and L62 is lower than the
		// population size, we put in the population original variants until
		// arrive to the desired population size
		while (newPopulation.size() < populationSize) {
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
			// inversed, we prefer have child variant first
			return Integer.compare(o1.getId(), o2.getId());
		}

	}

}
