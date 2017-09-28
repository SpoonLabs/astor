package fr.inria.astor.core.stats;

import java.util.HashMap;
import java.util.Map;

import fr.inria.astor.core.stats.PatchStat.HunkStat;

/**
 * 
 * @author Matias Martinez
 *
 */
public class PatchHunkStats {

	private Map<HunkStat, Object> stats = new HashMap<>();

	public Map<HunkStat, Object> getStats() {
		return stats;
	}

	public void setStats(Map<HunkStat, Object> stats) {
		this.stats = stats;
	}

}