package fr.inria.astor.core.stats;

/**
 * Stats of a patch
 * 
 * Matias Martinez
 */
import java.util.HashMap;
import java.util.Map;

import fr.inria.astor.core.stats.Stats.TypeStat;

public class PatchStat {


	private Map<TypeStat, Object> stats = new HashMap<>();

	public void addStat(TypeStat type, Object value) {
		this.stats.put(type, value);
	}

	public Map<TypeStat, Object> getStats() {
		return stats;
	}

	public void setStats(Map<TypeStat, Object> stats) {
		this.stats = stats;
	}

}
