package fr.inria.astor.core.solutionsearch.extension;

import java.util.List;

import fr.inria.astor.core.entities.ProgramVariant;

/**
 * Order patches (program variants) according to a given criterion  
 * @author matias
 *
 */
public interface SolutionVariantSortCriterion extends  AstorExtensionPoint{

	/**
	 * This method prioritizes patches. It receives a list of program variant
	 * that are solution, and it sorts it according to a given criterion.
	 * 
	 * @param patches
	 * @return
	 */
	public List<ProgramVariant> priorize(List<ProgramVariant> patches);
}
