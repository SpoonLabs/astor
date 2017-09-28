package fr.inria.astor.core.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Stores and manages statistics
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class Stats {

	public enum TypeStat {
		VARIANT_ID, TIME, OPERATOR, LOCATION, LINE, SUSPICIOUNESS, ORIGINAL_CODE, FIXED_CODE, PATCH_TYPE, INGREDIENT_SCOPE, INGREDIENT_PARENT, VALIDATION, PATCH_DIFF, NR_GENERATIONS, NR_RIGHT_COMPILATIONS, NR_FAILLING_COMPILATIONS, TOTAL_TIME, NR_FAILING_VALIDATION_PROCESS

	};

	public class Counter {

		int counter = 0;

		public void increment() {
			counter++;
		}

		public int getCounter() {
			return counter;
		}

		@Override
		public String toString() {
			return Integer.toString(counter);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (obj instanceof Number) {
				return obj.equals(this.counter);
			}

			if (getClass() != obj.getClass())
				return false;
			Counter other = (Counter) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (counter != other.counter)
				return false;
			return true;
		}

		private Stats getOuterType() {
			return Stats.this;
		}

	}

	private static Logger log = Logger.getLogger(Stats.class.getName());

	public static Stats currentStat = null;

	public IngredientStats ingredientsStats = new IngredientStats();

	List<PatchStat> statsOfPatches = new ArrayList<>();

	private Map<TypeStat, Object> statsValues = new HashMap<>();

	public static Stats getCurrentStat() {
		return currentStat;
	}

	public static void setCurrentStat(Stats currentStat) {
		Stats.currentStat = currentStat;
	}

	public IngredientStats getIngredientsStats() {
		return ingredientsStats;
	}

	public void setIngredientsStats(IngredientStats ingredientsStats) {
		this.ingredientsStats = ingredientsStats;
	}

	public List<PatchStat> getStatsOfPatches() {
		return statsOfPatches;
	}

	public void setStatsOfPatches(List<PatchStat> statsOfPatches) {
		this.statsOfPatches = statsOfPatches;
	}

	public void increment(TypeStat type) {
		Object v = this.statsValues.get(type);
		if (v == null) {
			Counter c = new Counter();
			this.statsValues.put(type, c);
			c.increment();
		} else if (v instanceof Counter) {
			((Counter) v).increment();
		}
	}

	public Map<TypeStat, Object> getStatsValues() {
		return statsValues;
	}

	public void setStatsValues(Map<TypeStat, Object> statsValues) {
		this.statsValues = statsValues;
	}

	public static Stats createStat() {

		if (currentStat == null) {
			currentStat = new Stats();
		}
		return currentStat;
	}
}
