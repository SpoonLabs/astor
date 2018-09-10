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

/**
 * 
 * @author Matias Martinez
 *
 */
public class PatchStat {

	public enum HunkStatEnum {
		OPERATOR, LOCATION, PATH, MODIFIED_FILE_PATH, LINE, SUSPICIOUNESS, MP_RANKING, ORIGINAL_CODE, BUGGY_CODE_TYPE,
		PATCH_HUNK_CODE, PATCH_HUNK_TYPE, INGREDIENT_SCOPE, INGREDIENT_PARENT,
	};

	public enum PatchStatEnum {
		VARIANT_ID, TIME, VALIDATION, GENERATION, FOLDER_SOLUTION_CODE, HUNKS,
		PATCH_DIFF_ORIG,
	};

	private Map<PatchStatEnum, Object> stats = new HashMap<>();

	public void addPatchHunk(PatchHunkStats hunk) {
		List<PatchHunkStats> hunks = null;
		if (stats.containsKey(PatchStatEnum.HUNKS)) {
			hunks = (List<PatchHunkStats>) stats.get(PatchStatEnum.HUNKS);
		} else {
			hunks = new ArrayList<>();
			stats.put(PatchStatEnum.HUNKS, hunks);
		}
		hunks.add(hunk);

	}

	public void addStat(PatchStatEnum type, Object value) {
		this.stats.put(type, value);
	}

	public Map<PatchStatEnum, Object> getStats() {
		return stats;
	}

	public void setStats(Map<PatchStatEnum, Object> stats) {
		this.stats = stats;
	}

}
