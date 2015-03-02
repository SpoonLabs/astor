package fr.inria.astor.core.setup;

/**
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class TransformationProperties {

	/**
	 * Suspicious threshold for statements.
	 */
	public static double THRESHOLD_SUSPECTNESS = 0.49;

	/**
	 * Initial identifier.
	 */
	public static int firstgenerationIndex = 1;
	/**
	 * Population size
	 */
	public static int populationSize = 1;//4;

	/**
	 * Max number of generation
	 */
	public static int maxGeneration = 40;

	/**
	 * Save all program variant generated (even those that do not compile)
	 */
	public static boolean saveProgramVariant = true;

	/**
	 * Indicates if only one generation is mutated by generation
	 */
	public static boolean mutateOnlyOneGenPerGeneration = true;

	/**
	 * Applies random order in Gen mutation or applies mutation in order
	 * (according to the suspicious value)
	 */
	public static boolean mutateGenInOrder = false;
	
	public static boolean mutateGenRandomlySuspicious = true;

	/**
	 * Analyze whether a statement applies in a context
	 */
	public static boolean analyzeContext = true;

	/**
	 * Stop the algorithm when a solution is found
	 */
	public static boolean stopAtFirstSolutionFound = false;

	/**
	 * Maximun time to validate a program variant
	 */
	public static int validationSingleTimeLimit = 60000;
	public static int validationRegressionTimeLimit = 120000;
	/**
	 * Introduce an unmodified program variant in each generation
	 */
	public static boolean reintroduceOriginalProgram = true;

	/**
	 * Prob to apply a mutation to a given gen.
	 */
	public static double mutation_rate = 1;
	
	/**
	 * Execute all regression test in one process or analyze one by one.
	 */
	public static boolean allTestInOneProcess = true;
	
	public static boolean uniformRandom = true;
	
}
