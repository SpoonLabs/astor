package fr.inria.astor.core.stats;

import java.util.ArrayList;
/**
 * Stats of a patch
 * 
 * Matias Martinez
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatchStat {

	

	public enum HunkStat {
		OPERATOR, LOCATION, LINE, SUSPICIOUNESS, ORIGINAL_CODE,BUGGY_CODE_TYPE, PATCH_HUNK_CODE, PATCH_HUNK_TYPE, INGREDIENT_SCOPE, INGREDIENT_PARENT,
	};

	public enum PatchStats {
		VARIANT_ID, TIME, VALIDATION, GENERATION,HUNKS, PATCH_DIFF
	};

	private Map<PatchStats, Object> stats = new HashMap<>();

	public void addPatchHunk(PatchHunkStats hunk) {
		List<PatchHunkStats> hunks = null;
		if (stats.containsKey(PatchStats.HUNKS)) {
			hunks = (List<PatchHunkStats>) stats.get(PatchStats.HUNKS);
		} else {
			hunks = new ArrayList<>();
			stats.put(PatchStats.HUNKS, hunks);
		}
		hunks.add(hunk);

	}

	public void addStat(PatchStats type, Object value) {
		this.stats.put(type, value);
	}

	public Map<PatchStats, Object> getStats() {
		return stats;
	}

	public void setStats(Map<PatchStats, Object> stats) {
		this.stats = stats;
	}

}
