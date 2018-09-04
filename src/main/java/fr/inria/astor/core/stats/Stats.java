package fr.inria.astor.core.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Stores and manages statistics
 * 
 * @author Matias Martinez
 *
 */
public class Stats {
	/**
	 * Stats related to Astor execution.
	 * 
	 * @author Matias Martinez
	 *
	 */
	public enum GeneralStatEnum {
	EXECUTION_IDENTIFIER, TOTAL_TIME, NR_GENERATIONS, NR_RIGHT_COMPILATIONS, NR_FAILLING_COMPILATIONS,
	NR_ERRONEOUS_VARIANCES, NR_FAILING_VALIDATION_PROCESS, OUTPUT_STATUS
	};

	/**
	 * Wrapper of an integer that allows increment
	 * 
	 * @author Matias Martinez
	 *
	 */
	public class Counter {

		private int counter = 0;

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

	/**
	 * Stats related to ingredients
	 */
	public IngredientStats ingredientsStats = new IngredientStats();

	/**
	 * Stats of each patch
	 */
	List<PatchStat> statsOfPatches = new ArrayList<>();

	/**
	 * General stats (Not related to any patch)
	 */
	private Map<GeneralStatEnum, Object> generalStats = new HashMap<>();

	public static Stats getCurrentStat() {
		return currentStat;
	}

	public static void setCurrentStat(Stats currentStat) {
		Stats.currentStat = currentStat;
	}

	private Stats() {

	}

	public IngredientStats getIngredientsStats() {
		return ingredientsStats;
	}

	public void setIngredientsStats(IngredientStats ingredientsStats) {
		this.ingredientsStats = ingredientsStats;
	}

	public void increment(GeneralStatEnum type) {
		Object v = this.generalStats.get(type);
		if (v == null) {
			Counter c = new Counter();
			this.generalStats.put(type, c);
			c.increment();
		} else if (v instanceof Counter) {
			((Counter) v).increment();
		}
	}

	public Map<GeneralStatEnum, Object> getGeneralStats() {
		return generalStats;
	}

	public void setGeneralStats(Map<GeneralStatEnum, Object> statsValues) {
		this.generalStats = statsValues;
	}

	public static Stats createStat() {
		currentStat = new Stats();
		return currentStat;
	}

}
