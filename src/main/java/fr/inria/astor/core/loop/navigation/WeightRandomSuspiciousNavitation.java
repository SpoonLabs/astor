package fr.inria.astor.core.loop.navigation;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.entities.WeightCtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class WeightRandomSuspiciousNavitation implements SuspiciousNavigationStrategy {

	@Override
	public List<ModificationPoint> getSortedModificationPointsList(ProgramVariant variant) {

		List<ModificationPoint> modificationPoints = variant.getModificationPoints();
		List<ModificationPoint> remaining = new ArrayList<ModificationPoint>(modificationPoints);
		List<ModificationPoint> solution = new ArrayList<ModificationPoint>();

		for (int i = 0; i < modificationPoints.size(); i++) {
			List<WeightCtElement> we = new ArrayList<WeightCtElement>();
			double sum = 0;
			for (ModificationPoint gen : remaining) {
				double susp = ((SuspiciousModificationPoint) gen).getSuspicious().getSuspiciousValue();
				sum += susp;
				WeightCtElement w = new WeightCtElement(gen, 0);
				w.weight = susp;
				we.add(w);
			}

			if (sum != 0) {

				for (WeightCtElement weightCtElement : we) {
					weightCtElement.weight = weightCtElement.weight / sum;
				}

				WeightCtElement.feedAccumulative(we);
				WeightCtElement selected = WeightCtElement.selectElementWeightBalanced(we);

				ModificationPoint selectedg = (ModificationPoint) selected.element;
				remaining.remove(selectedg);
				solution.add(selectedg);
			} else {
				solution.addAll(remaining);
				break;
			}
		}
		return solution;

	}

}
