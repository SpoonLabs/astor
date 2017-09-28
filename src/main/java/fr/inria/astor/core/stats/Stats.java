package fr.inria.astor.core.stats;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.stats.PatchStat.HunkStat;
import fr.inria.astor.core.stats.PatchStat.PatchStats;
import fr.inria.astor.util.TimeUtil;

/**
 * Stores and manages statistics
 * 
 * @author Matias Martinez
 *
 */
public class Stats {

	public enum TypeStat {
		TOTAL_TIME, NR_GENERATIONS, NR_RIGHT_COMPILATIONS, NR_FAILLING_COMPILATIONS, NR_FAILING_VALIDATION_PROCESS
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

	public List<PatchStat> createStatsForPatches(List<ProgramVariant> variants, int generation,
			Date dateInitEvolution) {
		List<PatchStat> patches = new ArrayList();

		for (ProgramVariant solutionVariant : variants) {

			PatchStat patch_i = new PatchStat();
			patches.add(patch_i);
			patch_i.addStat(PatchStats.TIME,
					TimeUtil.getDateDiff(dateInitEvolution, solutionVariant.getBornDate(), TimeUnit.SECONDS));
			patch_i.addStat(PatchStats.VARIANT_ID, solutionVariant.getId());

			patch_i.addStat(PatchStats.VALIDATION, solutionVariant.getValidationResult().toString());

			patch_i.addStat(PatchStats.PATCH_DIFF, solutionVariant.getPatchDiff());

			List<PatchHunkStats> hunks = new ArrayList<>();
			patch_i.addStat(PatchStats.HUNKS, hunks);

			int lastGeneration = -1;
			for (int i = 1; i <= generation; i++) {
				List<OperatorInstance> genOperationInstances = solutionVariant.getOperations().get(i);
				if (genOperationInstances == null)
					continue;
				lastGeneration = i;

				for (OperatorInstance genOperationInstance : genOperationInstances) {

					PatchHunkStats hunk = new PatchHunkStats();
					hunks.add(hunk);
					hunk.getStats().put(HunkStat.OPERATOR, genOperationInstance.getOperationApplied().toString());
					hunk.getStats().put(HunkStat.LOCATION,
							genOperationInstance.getModificationPoint().getCtClass().getQualifiedName());

					if (genOperationInstance.getModificationPoint() instanceof SuspiciousModificationPoint) {
						SuspiciousModificationPoint gs = (SuspiciousModificationPoint) genOperationInstance
								.getModificationPoint();
						hunk.getStats().put(HunkStat.LINE, gs.getSuspicious().getLineNumber());
						hunk.getStats().put(HunkStat.SUSPICIOUNESS, gs.getSuspicious().getSuspiciousValueString());
					}
					hunk.getStats().put(HunkStat.ORIGINAL_CODE, genOperationInstance.getOriginal().toString());
					hunk.getStats().put(HunkStat.BUGGY_CODE_TYPE,
							genOperationInstance.getOriginal().getClass().getSimpleName() + "|"
									+ genOperationInstance.getOriginal().getParent().getClass().getSimpleName());

					if (genOperationInstance.getModified() != null) {
						// if fix content is the same that original buggy
						// content, we do not write the patch, remaining empty
						// the property fixed statement
						if (genOperationInstance.getModified().toString() != genOperationInstance.getOriginal()
								.toString())

							hunk.getStats().put(HunkStat.PATCH_HUNK_CODE,
									genOperationInstance.getModified().toString());
						else {
							hunk.getStats().put(HunkStat.PATCH_HUNK_CODE,
									genOperationInstance.getOriginal().toString());

						}
						// Information about types Parents

						hunk.getStats().put(HunkStat.PATCH_HUNK_TYPE,
								genOperationInstance.getModified().getClass().getSimpleName() + "|"
										+ genOperationInstance.getModified().getParent().getClass().getSimpleName());

					}

					hunk.getStats().put(HunkStat.INGREDIENT_SCOPE, ((genOperationInstance.getIngredientScope() != null)
							? genOperationInstance.getIngredientScope() : "-"));

					if (genOperationInstance.getIngredient() != null
							&& genOperationInstance.getIngredient().getDerivedFrom() != null)
						hunk.getStats().put(HunkStat.INGREDIENT_PARENT,
								genOperationInstance.getIngredient().getDerivedFrom());

				}
			}
			if (lastGeneration > 0) {
				patch_i.addStat(PatchStats.GENERATION, lastGeneration);

			}

		}
		return patches;
	}

	public String statsToString() {

		List<PatchStat> statsForPatches = this.getStatsOfPatches();
		StringBuffer buff = new StringBuffer();
		buff.append(System.getProperty("line.separator"));

		for (TypeStat generalStat : TypeStat.values()) {
			buff.append(generalStat.name());
			buff.append("=");
			buff.append(this.getStatsValues().get(generalStat));
			buff.append(System.getProperty("line.separator"));
		}

		// Stats of patches
		for (PatchStat patchStat : statsForPatches) {

			Map<PatchStats, Object> stats = patchStat.getStats();
			for (PatchStats statKey : PatchStats.values()) {
				if (statKey.equals(PatchStats.HUNKS)) {
					List<PatchHunkStats> hunks = (List<PatchHunkStats>) stats.get(statKey);
					// Print the properties of a Patch hunk
					int i = 0;
					for (PatchHunkStats patchHunkStats : hunks) {
						Map<HunkStat, Object> statshunk = patchHunkStats.getStats();
						buff.append("--Patch Hunk #" + (++i));
						for (HunkStat hs : HunkStat.values()) {

							buff.append(System.getProperty("line.separator"));
							buff.append(hs.name());
							buff.append("=");
							buff.append(statshunk.get(hs));
							buff.append(System.getProperty("line.separator"));
						}
					}

					buff.append(System.getProperty("line.separator"));
				} else {
					buff.append(statKey.name());
					buff.append("=");
					buff.append(stats.get(statKey));
					buff.append(System.getProperty("line.separator"));
				}

			}

		}
		return buff.toString();
	}

	@SuppressWarnings("unchecked")
	public void statsToJSON(String output) {

		JSONObject statsjsonRoot = new JSONObject();
		JSONArray patchlistJson = new JSONArray();
		statsjsonRoot.put("patches", patchlistJson);

		for (TypeStat generalStat : TypeStat.values()) {
			statsjsonRoot.put(generalStat.name(), this.getStatsValues().get(generalStat));
		}

		List<PatchStat> statsForPatches = this.getStatsOfPatches();

		for (PatchStat patchStat : statsForPatches) {

			JSONObject patchjson = new JSONObject();
			patchlistJson.add(patchjson);

			Map<PatchStats, Object> stats = patchStat.getStats();
			for (PatchStats statKey : PatchStats.values()) {
				if (statKey.equals(PatchStats.HUNKS)) {
					List<PatchHunkStats> hunks = (List<PatchHunkStats>) stats.get(statKey);
					JSONArray hunksListJson = new JSONArray();
					patchjson.put("patchhunks", hunksListJson);

					for (PatchHunkStats patchHunkStats : hunks) {
						Map<HunkStat, Object> statshunk = patchHunkStats.getStats();

						JSONObject hunkjson = new JSONObject();
						hunksListJson.add(hunkjson);
						for (HunkStat hs : HunkStat.values()) {
							if (statshunk.containsKey(hs))
								hunkjson.put(hs.name(), JSONObject.escape(statshunk.get(hs).toString()));
						}
					}

				} else {
					if (stats.containsKey(statKey))
						patchjson.put(statKey.name(), JSONObject.escape(stats.get(statKey).toString()));
				}

			}

		}
		String filename = ConfigurationProperties.getProperty("jsonoutputname");
		String absoluteFileName = output + "/" + filename + ".json";
		try (FileWriter file = new FileWriter(absoluteFileName)) {

			file.write(statsjsonRoot.toJSONString());
			file.flush();
			log.info("Storing ing JSON at " + absoluteFileName);
			log.info(filename + ":" + statsjsonRoot.toJSONString());

		} catch (IOException e) {
			e.printStackTrace();
			log.error("Problem storing ing json file" + e.toString());
		}

	}
}
