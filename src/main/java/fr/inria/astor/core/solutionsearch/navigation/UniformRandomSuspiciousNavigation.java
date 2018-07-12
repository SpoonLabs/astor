package fr.inria.astor.core.solutionsearch.navigation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.inria.astor.core.entities.ModificationPoint;

/**
 * 
 * @author Matias Martinez
 *
 */
public class UniformRandomSuspiciousNavigation implements SuspiciousNavigationStrategy {

	@Override
	public List<ModificationPoint> getSortedModificationPointsList(List<ModificationPoint> modificationPoints) {
		List<ModificationPoint> shuffList = new ArrayList<ModificationPoint>(modificationPoints);
		Collections.shuffle(shuffList);
		return shuffList;
	}

}
