package fr.inria.astor.core.stats;

import java.util.HashMap;
import java.util.Map;

import fr.inria.astor.core.stats.PatchStat.HunkStatEnum;

/**
 * 
 * @author Matias Martinez
 *
 */
public class PatchHunkStats {

	private Map<HunkStatEnum, Object> stats = new HashMap<>();

	public Map<HunkStatEnum, Object> getStats() {
		return stats;
	}

	public void setStats(Map<HunkStatEnum, Object> stats) {
		this.stats = stats;
	}

}