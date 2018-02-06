package fr.inria.astor.core.stats;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.inria.astor.core.stats.StatSpaceSize.INGREDIENT_STATUS;

/**
 * Stats related to ingredients TODO: TO BE REFACTOR!!
 * 
 * @author Matias Martinez
 *
 */
public class IngredientStats {

	private static Logger log = Logger.getLogger(IngredientStats.class.getName());

	// Ingredients
	// Key: id, value: counter
	public Map<Integer, Integer> temporalIngCounterByPatch = new HashedMap();
	public List<Pair> ingAttemptsSuccessfulPatches = new ArrayList<>();
	public List<Pair> ingAttemptsFailingPatches = new ArrayList<>();
	public List<Pair> successfulTransformedIngredients = new ArrayList<>();
	public Integer temporalIngCounterUntilPatch = 0;
	public List<Integer> patch_attempts = new ArrayList<>();
	public StatCounter<String> sizeSpace = new StatCounter<String>();
	public List<StatSpaceSize> sizeSpaceOfVariant = new ArrayList<StatSpaceSize>();
	public StatCounter<Integer> attemptsPerVariant = new StatCounter<Integer>();
	// key are value, values are abundancy
	public Map<Long, Long> ingredientSpaceSize = new HashedMap();
	public Map<Long, Long> combinationByIngredientSize = new HashedMap();
	public Map<Long, Long> combinationByIngredientCompiledSize = new HashedMap();
	public Map<Double, Long> proportionCompiledSize = new HashedMap();
	public Map<Long, Long> ingMapped = new HashedMap();
	public Map<Long, Long> ingMappedAll = new HashedMap();
	public Map<Long, Long> ingNotMapped = new HashedMap();
	
	public void setAlreadyApplied(int i) {
		setState(i, INGREDIENT_STATUS.alreadyanalyzed);
	}

	public void setCompiles(int i) {
		setState(i, INGREDIENT_STATUS.compiles);
	}

	public void setNotCompiles(int i) {
		setState(i, INGREDIENT_STATUS.notcompiles);
	}

	private void setState(int i, INGREDIENT_STATUS t) {
		if (this.sizeSpaceOfVariant.size() > 0) {
			StatSpaceSize sp = this.sizeSpaceOfVariant.get(sizeSpaceOfVariant.size() - 1);
			if (sp.id == i)
				sp.states = t;
		}
	}

	public void commitStatsOfTrial() {
		if (sizeSpaceOfVariant.isEmpty())
			return;

		for (StatSpaceSize statSpaceSize : sizeSpaceOfVariant) {
			this.sizeSpace.add(statSpaceSize.toString());
		}
		this.attemptsPerVariant.add(sizeSpaceOfVariant.size());
	}

	/**
	 * Increments the counter for variant received as argument. Return the
	 * counter
	 * 
	 * @param idprogvariant
	 * @return
	 */
	public int incrementIngCounter(Integer idprogvariant) {
		Integer counter = 0;
		if (!temporalIngCounterByPatch.containsKey(idprogvariant))
			temporalIngCounterByPatch.put(idprogvariant, new Integer(0));
		else {
			counter = temporalIngCounterByPatch.get(idprogvariant);
		}
		counter++;
		temporalIngCounterByPatch.put(idprogvariant, counter);
		// log.debug("Incrementing ingredient counter for variant
		// "+idprogvariant + " to "+counter);
		return counter;
	}

	public void initializeIngCounter(Integer idprogvariant) {
		temporalIngCounterByPatch.put(idprogvariant, new Integer(0));
	}

	/**
	 * Returns the counter for the program variant passed as argument. It does
	 * not modify the counter.
	 * 
	 * @param idprogvariant
	 * @return
	 */
	public int getIngCounter(Integer idprogvariant) {

		if (!temporalIngCounterByPatch.containsKey(idprogvariant))
			return 0;
		else {
			return temporalIngCounterByPatch.get(idprogvariant);
		}

	}

	/**
	 * Save variant counter and Reset counter for the id received as argument.
	 * 
	 * @param idprogvariant
	 * @return
	 */
	public void storeIngCounterFromSuccessPatch(Integer idprogvariant) {
		storeIngredientCounter(idprogvariant, ingAttemptsSuccessfulPatches);
	}

	/**
	 * Save variant counter and Reset counter for the id received as argument.
	 * 
	 * @param idprogvariant
	 * @return
	 */
	public void storeIngCounterFromFailingPatch(Integer idprogvariant) {
		storeIngredientCounter(idprogvariant, ingAttemptsFailingPatches);
	}

	private Integer storeIngredientCounter(Integer idprogvariant, List<Pair> ingAttempts) {
		Integer counter = temporalIngCounterByPatch.get(idprogvariant);
		if (counter == null) {
			log.debug("Ingredient counter is Zero");
			counter = 0;
		}
		ingAttempts.add(new Pair(idprogvariant, counter));
		temporalIngCounterByPatch.put(idprogvariant, 0);
		// Stores the number of attempts (from a success or failing patch
		// creation until finding a valid patch
		temporalIngCounterUntilPatch += counter;

		return counter;
	}

	public void storeSucessfulTransformedIngredient(int pvid, int transformations) {
		this.successfulTransformedIngredients.add(new Pair(pvid, transformations));
	}

	/**
	 * Save the counter and reset it.
	 * 
	 * @param idprogvariant
	 */
	public void storePatchAttempts(Integer idprogvariant) {

		log.debug("\nAttempts to find patch Id " + idprogvariant + ": " + temporalIngCounterUntilPatch + ", successful "
				+ sum(this.ingAttemptsSuccessfulPatches) + ", failing " + sum(this.ingAttemptsFailingPatches));
		this.patch_attempts.add(temporalIngCounterUntilPatch);
		this.temporalIngCounterUntilPatch = 0;

	}

	/**
	 * Remove all values.
	 */
	public void resetIngCounter() {
		this.temporalIngCounterByPatch.clear();
		this.temporalIngCounterUntilPatch = 0;
		this.ingAttemptsSuccessfulPatches.clear();
		this.ingAttemptsFailingPatches.clear();
		this.patch_attempts.clear();
	}

	public static int sum(List<Pair> el) {
		return el.stream().mapToInt(Pair::getAttempts).sum();
	}

	public class Pair {
		int pvid;
		int attempts;

		public Pair(int pvid, int attempts) {
			super();
			this.pvid = pvid;
			this.attempts = attempts;
		}

		public int getPvid() {
			return pvid;
		}

		public int getAttempts() {
			return attempts;
		}

		public String toString() {
			return "(pv:" + pvid + ",at:" + attempts + ")";
		}
	};

	public void addSize(Map<Long, Long> attempts, Integer sizep) {

		Long size = new Long(sizep);
		Long counter = 0l;
		if (attempts.containsKey(size)) {
			counter = attempts.get((long) size);
		}
		counter++;
		attempts.put(size, counter);

	}

	public void addSize(Map<Long, Long> attempts, long sizep) {

		Long size = new Long(sizep);
		Long counter = 0l;
		if (attempts.containsKey(size)) {
			counter = attempts.get((long) size);
		}
		counter++;
		attempts.put(size, counter);

	}

	public void addProportion(Map<Double, Long> attempts, double sizep) {

		Double size = new Double(sizep);
		Long counter = 0l;
		if (attempts.containsKey(size)) {
			counter = attempts.get(size);
		}
		counter++;
		attempts.put(size, counter);

	}

	public void toJSON(String output, Map<Long, Long> attempts, String filename) {
		JSONObject space = getJsonObject(attempts);
		String absoluteFileName = output + "/" + filename + ".json";
		try (FileWriter file = new FileWriter(absoluteFileName)) {

			file.write(space.toJSONString());
			file.flush();
			log.info("Storing ing JSON at " + absoluteFileName);
			log.info(filename + ":" + space.toJSONString());

		} catch (IOException e) {
			e.printStackTrace();
			log.error("Problem storing ing json file" + e.toString());
		}

	}

	public JSONObject getJsonObject(Map<?, Long> attempts) {
		JSONObject space = new JSONObject();

		JSONArray list = new JSONArray();
		space.put("space", list);
		int total = 0;
		for (Object key : attempts.keySet()) {
			JSONObject keyjson = new JSONObject();
			keyjson.put("a", key);
			long num = attempts.get(key);
			keyjson.put("v", num);
			total += num;
			list.add(keyjson);
		}
		space.put("allAttempts", total);// Times the space was called
		return space;
	}

	public void print() {
		log.info("\nsuccessful_ing_attempts (#trials " + ingAttemptsSuccessfulPatches.size() + ", totalAttps "
				+ sum(ingAttemptsSuccessfulPatches) + "): "
				+ ingAttemptsSuccessfulPatches.stream().map(Pair::getAttempts).collect(Collectors.toList()));
		log.info("\nfailing_ing_attempts (#trials " + ingAttemptsFailingPatches.size() + ", totalAttps "
				+ sum(ingAttemptsFailingPatches) + "): "
				+ ingAttemptsFailingPatches.stream().map(Pair::getAttempts).collect(Collectors.toList()));

		log.info("\ntotal Patch Attempts (" + patch_attempts.size() + "): " + patch_attempts);

		log.info("\nsuccessful_ing_attempts_by_patch: " + ingAttemptsSuccessfulPatches);
		log.info("\nfailing_ing_attempts_by_patch: " + ingAttemptsFailingPatches);

		log.info("\npvariants_with_transformed_ingredients: " + successfulTransformedIngredients);

	}
}
