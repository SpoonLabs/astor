package fr.inria.astor.core.stats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;

import fr.inria.astor.core.stats.StatSpaceSize.INGREDIENT_STATUS;

/**
 * Stores and manages statistics
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class Stats {

	private static Logger log = Logger.getLogger(Stats.class.getName());

	public static Stats currentStat = null;

	public int id = 0;

	// Ingredients
	// Key: id, value: counter
	public Map<Integer, Integer> temporalIngCounter = new HashedMap();
	public List<Integer> ingAttemptsSuccessfulPatches = new ArrayList<>();
	public List<Integer> ingAttemptsFailingPatches = new ArrayList<>();
	//

	public int numberOfElementsToMutate = 0;
	public StatCounter<String> sizeSpace = new StatCounter<String>();
	public List<StatSpaceSize> sizeSpaceOfVariant = new ArrayList<StatSpaceSize>();
	public StatCounter<Integer> attemptsPerVariant = new StatCounter<Integer>();

	public StatCounter<String> typeOfElementsSelectedForModifying = new StatCounter<String>();
	public List<StatPatch> genPatches = new ArrayList<StatPatch>();

	public int numberOfRightCompilation = 0;
	public int numberOfFailingCompilation = 0;
	public int numberOfFailingTestCaseExecution = 0;
	public int numberOfRegressionTestExecution = 0;
	public int numberOfFailingTestCase = 0;
	public int numberOfRegressionTestCases = 0;

	public int numberGenerations = 0;

	public int patches = 0;

	public int numberOfAppliedOp = 0;
	public int numberOfNotAppliedOp = 0;
	// this property contains the number of modification points that the
	// approach try to mutate
	// but it could. For instance, it takes as same ingredient one element that
	// gen already containe
	public int numberOfGenInmutated = 0;
	public List<Long> time1Validation = new ArrayList<Long>();
	public List<Long> time2Validation = new ArrayList<Long>();
	public long timeIteraction;
	//
	public double fl_threshold;
	public int fl_size;
	public int fl_gens_size;

	public int passFailingval1 = 0;
	public int passFailingval2 = 0;

	public int numberOfTestcasesExecutedval1 = 0;
	public int numberOfTestcasesExecutedval2 = 0;

	public int unfinishValidation = 0;

	public void printStats() {
		log.info(toString());
	}

	public String toString() {
		String s = "";
		s += ("\nspaces navigation: [" + this.sizeSpace.getStructure().size() + "]: " + this.sizeSpace);
		// s += ("\ntime val1 [" + this.time1Validation.size() + "]: " +
		// this.time1Validation);
		// s += ("\ntime val2 [" + this.time2Validation.size() + "]: " +
		// this.time2Validation);
		s += ("\n#gen: " + this.numberGenerations);
		s += ("\n#patches: " + this.patches);
		s += ("\n#RightCompilation: " + this.numberOfRightCompilation);
		s += ("\n#WrongCompilation: " + this.numberOfFailingCompilation);
		s += ("\n#FailingTestCaseExecution: " + this.numberOfFailingTestCaseExecution);
		s += ("\n#RegressionTestExecution: " + this.numberOfRegressionTestExecution);
		s += ("\n#TestcasesExecutedval1: " + this.numberOfTestcasesExecutedval1);
		s += ("\n#TestcasesExecutedval2: " + this.numberOfTestcasesExecutedval2);

		s += ("\n#FailingTestCase: " + this.numberOfFailingTestCase);
		s += ("\n#RegressionTestCases: " + this.numberOfRegressionTestCases);

		s += ("\n#OfAppliedOp: " + this.numberOfAppliedOp);
		s += ("\n#NotAppliedOp: " + this.numberOfNotAppliedOp);
		s += ("\n#InmutatedGen: " + this.numberOfGenInmutated);
		s += ("\n#unfinishValidation: " + this.unfinishValidation);

		s += ("\n#ing " + this.typeOfElementsSelectedForModifying);
		s += ("\n#untilcompile " + this.attemptsPerVariant);
		return s;
	}

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

	public static Stats createStat() {

		if (currentStat == null) {
			currentStat = new Stats();
		}
		return currentStat;
	}

	public static Stats getCurrentStats() {

		if (currentStat == null) {
			return createStat();
		}
		return currentStat;
	}

	public void saveStats() {
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
		if (!temporalIngCounter.containsKey(idprogvariant))
			temporalIngCounter.put(idprogvariant, new Integer(0));
		else {
			counter = temporalIngCounter.get(idprogvariant);
		}
		counter++;
		temporalIngCounter.put(idprogvariant, counter);
		log.debug("Incrementing ingredient counter for variant "+idprogvariant + " to "+counter);
		return counter;
	}

	public void initializeIngCounter(Integer idprogvariant) {
		temporalIngCounter.put(idprogvariant, new Integer(0));
	}

	/**
	 * Returns the counter for the program variant passed as argument. It does
	 * not modify the counter.
	 * 
	 * @param idprogvariant
	 * @return
	 */
	public int getIngCounter(Integer idprogvariant) {

		if (!temporalIngCounter.containsKey(idprogvariant))
			return 0;
		else {
			return temporalIngCounter.get(idprogvariant);
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

	private Integer storeIngredientCounter(Integer idprogvariant, List<Integer> ingAttempts) {
		Integer counter = temporalIngCounter.get(idprogvariant);
		if (counter == null) {
			log.debug("Ingredient counter is Zero");
			ingAttempts.add(0);
		} else {
			ingAttempts.add(counter);
		}
		temporalIngCounter.put(idprogvariant, 0);
		return counter;
	}

	/**
	 * Remove all values.
	 */
	public void resetIngCounter() {
		temporalIngCounter.clear();
		this.ingAttemptsSuccessfulPatches.clear();
		this.ingAttemptsFailingPatches.clear();
	}

}
