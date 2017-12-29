package fr.inria.astor.core.entities;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.inria.astor.core.setup.RandomManager;

/**
 * Element with a propability
 * 
 * @author Matias Martinez
 *
 */
public class WeightElement<T> {

	public T element = null;
	public double weight = 0;
	public double accum = 0;

	public WeightElement(T element, double weight) {
		super();
		this.element = element;
		this.weight = weight;
	}

	@Override
	public String toString() {
		return "WE [element=" + element + ", weight=" + weight + ", accum " + accum + "]";
	}

	private static void sortByWeight(List<WeightElement<?>> we) {
		Collections.sort(we, new Compw());
	}

	public static void feedAccumulative(List<WeightElement<?>> we) {
		WeightElement.sortByWeight(we);
		if (we.isEmpty())
			return;
		// Calculate Accumulative
		we.get(0).accum = we.get(0).weight;
		//
		for (int i = 1; i < we.size(); i++) {
			WeightElement<?> e = we.get(i);
			WeightElement<?> elast = we.get(i - 1);

			e.accum = (elast.accum + e.weight);
		}
	}

	public static WeightElement<?> selectElementWeightBalanced(List<WeightElement<?>> we) {
		WeightElement<?> selected = null;
		double d = RandomManager.nextDouble();

		for (int i = 0; (selected == null && i < we.size()); i++) {
			if (d <= we.get(i).accum) {
				selected = we.get(i);
			}
		}
		if (selected == null) {
			return null;
		}
		return selected;
	}

	static class Compw implements Comparator<WeightElement<?>> {

		@Override
		public int compare(WeightElement<?> o1, WeightElement<?> o2) {
			// Decreasing
			return Double.compare(o2.weight, o1.weight);
		}

	}
}
