package fr.inria.main;

/**
 * Output values of Astor
 * 
 * @author Matias Martinez
 *
 */
public enum AstorOutputStatus {
	// max time reached
	TIME_OUT,
	// max number of generation reached
	MAX_GENERATION,
	// max number of solution reached.
	STOP_BY_PATCH_FOUND,
	// An exception was thrown
	ERROR,
	// stops due to a convergence on the search
	CONVERGED,
	// stops due the navigation is finished, i.e., space exhaustively navigated
	EXHAUSTIVE_NAVIGATED;
}
