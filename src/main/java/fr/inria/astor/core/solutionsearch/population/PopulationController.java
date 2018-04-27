package fr.inria.astor.core.solutionsearch.population;

import java.util.List;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.solutionsearch.extension.AstorExtensionPoint;
/**
 * Population Controller: it selects the program variants for the following generations.
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public interface PopulationController extends  AstorExtensionPoint{

	/**
	 * 
	 * @param parentVariants Originals variant 
	 * @param childVariants New Variants
	 * @param maxNumberInstances
	 * @return 
	 */
	public List<ProgramVariant> selectProgramVariantsForNextGeneration(List<ProgramVariant> parentVariants,
			List<ProgramVariant> childVariants, 
			int maxNumberInstances,ProgramVariantFactory variantFactory, ProgramVariant original, 
			int generation
			);

}
