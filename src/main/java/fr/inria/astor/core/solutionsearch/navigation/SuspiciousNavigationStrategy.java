package fr.inria.astor.core.solutionsearch.navigation;

import java.util.List;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.solutionsearch.extension.AstorExtensionPoint;

/**
 * 
 * @author Matias Martinez
 *
 */
public interface SuspiciousNavigationStrategy extends AstorExtensionPoint {

	/**
	 * Returns a list with sorted modification points, with the order to be
	 * navigated.
	 * 
	 * @param list
	 *            of modification point to be navigated
	 * @return list of modification points sorted by a given strategy
	 */
	List<ModificationPoint> getSortedModificationPointsList(List<ModificationPoint> modificationPoints);

}
