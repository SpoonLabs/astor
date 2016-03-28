package fr.inria.astor.core.entities;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.setup.RandomManager;
/**
 * 
 * @author Matias Martinez
 *
 */
public class WeightCtElement {

	private Logger logger = Logger.getLogger(WeightCtElement.class.getName());

	public Object element = null;
	public int line = 0;
	public int distance = 0;
	public double weight = 0;
	public double accum = 0;

	public WeightCtElement(Object element, int l) {
		super();
		this.element = element;
		this.line = l;
	}

	@Override
	public String toString() {
		return "WE [element=" + element + ", weight=" + weight + ", accum " + accum + ", line=" + line + ", dist= "
				+ distance + "]";
	}

	private static void sortByWeight(List<WeightCtElement> we) {
		Collections.sort(we, new Compw());
	}

	public static void feedAccumulative(List<WeightCtElement> we) {
		WeightCtElement.sortByWeight(we);
		if(we.isEmpty())
			return;
		// Calculate Accumulative
		we.get(0).accum = we.get(0).weight;
		//
		for (int i = 1; i < we.size(); i++) {
			WeightCtElement e = we.get(i);
			WeightCtElement elast = we.get(i - 1);

			e.accum = (elast.accum + e.weight);
		}
	}

	public static WeightCtElement selectElementWeightBalanced(List<WeightCtElement> we) {
		WeightCtElement selected = null;
		double d = RandomManager.nextDouble();
		
		for (int i = 0; (selected == null && i < we.size()); i++) {
			if (d <= we.get(i).accum) {
				selected = we.get(i);
			//	logger.info("Rand: "+d+" select: i: "+i+", "+selected);
			}
		}
		if (selected == null) {
		//	logger.info("No element selected with d = " + d);
			return null;
		}
		return selected;
	}
	
	static class Compw implements Comparator<WeightCtElement> {

		@Override
		public int compare(WeightCtElement o1, WeightCtElement o2) {
			// Decreasing
			return Double.compare(o2.weight, o1.weight);
		}

	}
}
