package fr.inria.astor.core.solutionsearch;

import java.util.List;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.main.ExecutionResult;

/**
 * 
 * @author Matias Martinez
 *
 */
public class RepairResult extends ExecutionResult {

	private List<ProgramVariant> solutions;

	public List<ProgramVariant> getSolutions() {
		return solutions;
	}

	public void setSolutions(List<ProgramVariant> solutions) {
		this.solutions = solutions;
	}

}
