package fr.inria.astor.core.loop.navigation;

import java.util.List;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;

/**
 * 
 * @author Matias Martinez
 *
 */
public class InOrderSuspiciousNavigation implements SuspiciousNavigationStrategy {

	/**
	 * same order the MP are referenced by the variant.
	 */
	@Override
	public List<ModificationPoint> getSortedModificationPointsList(ProgramVariant variant) {
		return variant.getModificationPoints();
	}

}
