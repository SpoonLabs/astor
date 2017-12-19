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
import fr.inria.astor.core.loop.spaces.ingredients.transformations.DynamicIngredient;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.stats.PatchStat.HunkStatEnum;
import fr.inria.astor.core.stats.PatchStat.PatchStatEnum;
import fr.inria.astor.util.TimeUtil;

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

	public List<PatchStat> createStatsForPatches(List<ProgramVariant> variants, int generation,
			Date dateInitEvolution) {
		List<PatchStat> patches = new ArrayList();

		for (ProgramVariant solutionVariant : variants) {

			PatchStat patch_i = new PatchStat();
			patches.add(patch_i);
			patch_i.addStat(PatchStatEnum.TIME,
					TimeUtil.getDateDiff(dateInitEvolution, solutionVariant.getBornDate(), TimeUnit.SECONDS));
			patch_i.addStat(PatchStatEnum.VARIANT_ID, solutionVariant.getId());

			patch_i.addStat(PatchStatEnum.VALIDATION, solutionVariant.getValidationResult().toString());

			patch_i.addStat(PatchStatEnum.PATCH_DIFF, solutionVariant.getPatchDiff());

			List<PatchHunkStats> hunks = new ArrayList<>();
			patch_i.addStat(PatchStatEnum.HUNKS, hunks);

			int lastGeneration = -1;
			for (int i = 1; i <= generation; i++) {
				List<OperatorInstance> genOperationInstances = solutionVariant.getOperations().get(i);
				if (genOperationInstances == null)
					continue;
				lastGeneration = i;

				for (OperatorInstance genOperationInstance : genOperationInstances) {

					PatchHunkStats hunk = new PatchHunkStats();
					hunks.add(hunk);
					hunk.getStats().put(HunkStatEnum.OPERATOR, genOperationInstance.getOperationApplied().toString());
					hunk.getStats().put(HunkStatEnum.LOCATION,
							genOperationInstance.getModificationPoint().getCtClass().getQualifiedName());

					hunk.getStats().put(HunkStatEnum.MP_RANKING,
							genOperationInstance.getModificationPoint().identified);

					if (genOperationInstance.getModificationPoint() instanceof SuspiciousModificationPoint) {
						SuspiciousModificationPoint gs = (SuspiciousModificationPoint) genOperationInstance
								.getModificationPoint();
						hunk.getStats().put(HunkStatEnum.LINE, gs.getSuspicious().getLineNumber());
						hunk.getStats().put(HunkStatEnum.SUSPICIOUNESS, gs.getSuspicious().getSuspiciousValueString());
					}
					hunk.getStats().put(HunkStatEnum.ORIGINAL_CODE, genOperationInstance.getOriginal().toString());
					hunk.getStats().put(HunkStatEnum.BUGGY_CODE_TYPE,
							genOperationInstance.getOriginal().getClass().getSimpleName() + "|"
									+ genOperationInstance.getOriginal().getParent().getClass().getSimpleName());

					if (genOperationInstance.getModified() != null) {
						// if fix content is the same that original buggy
						// content, we do not write the patch, remaining empty
						// the property fixed statement
						if (genOperationInstance.getModified().toString() != genOperationInstance.getOriginal()
								.toString())

							hunk.getStats().put(HunkStatEnum.PATCH_HUNK_CODE,
									genOperationInstance.getModified().toString());
						else {
							hunk.getStats().put(HunkStatEnum.PATCH_HUNK_CODE,
									genOperationInstance.getOriginal().toString());

						}
						// Information about types Parents

						hunk.getStats().put(HunkStatEnum.PATCH_HUNK_TYPE,
								genOperationInstance.getModified().getClass().getSimpleName() + "|"
										+ genOperationInstance.getModified().getParent().getClass().getSimpleName());

						if (genOperationInstance.getIngredient() != null
								&& genOperationInstance.getIngredient() instanceof DynamicIngredient) {
							DynamicIngredient ding = (DynamicIngredient) genOperationInstance.getIngredient();
							hunk.getStats().put(HunkStatEnum.INGREDIENT, ding.getBaseIngredient().toString());
						}
					}

					hunk.getStats().put(HunkStatEnum.INGREDIENT_SCOPE,
							((genOperationInstance.getIngredientScope() != null)
									? genOperationInstance.getIngredientScope() : "-"));

					if (genOperationInstance.getIngredient() != null
							&& genOperationInstance.getIngredient().getDerivedFrom() != null)
						hunk.getStats().put(HunkStatEnum.INGREDIENT_PARENT,
								genOperationInstance.getIngredient().getDerivedFrom());

				}
			}
			if (lastGeneration > 0) {
				patch_i.addStat(PatchStatEnum.GENERATION, lastGeneration);

			}

		}
		return patches;
	}

	public String statsToString(List<PatchStat> statsForPatches) {

		//List<PatchStat> statsForPatches = this.getStatsOfPatches();
		StringBuffer buff = new StringBuffer();
		buff.append(System.getProperty("line.separator"));

		for (GeneralStatEnum generalStat : GeneralStatEnum.values()) {
			buff.append(generalStat.name());
			buff.append("=");
			buff.append(this.getGeneralStats().get(generalStat));
			buff.append(System.getProperty("line.separator"));
		}

		// Stats of patches
		for (PatchStat patchStat : statsForPatches) {

			Map<PatchStatEnum, Object> stats = patchStat.getStats();
			for (PatchStatEnum statKey : PatchStatEnum.values()) {
				if (statKey.equals(PatchStatEnum.HUNKS)) {
					List<PatchHunkStats> hunks = (List<PatchHunkStats>) stats.get(statKey);
					// Print the properties of a Patch hunk
					int i = 0;
					for (PatchHunkStats patchHunkStats : hunks) {
						Map<HunkStatEnum, Object> statshunk = patchHunkStats.getStats();
						buff.append("--Patch Hunk #" + (++i));
						for (HunkStatEnum hs : HunkStatEnum.values()) {

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

}
