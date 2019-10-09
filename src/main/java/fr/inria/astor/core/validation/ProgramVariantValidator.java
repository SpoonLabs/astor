package fr.inria.astor.core.validation;

import java.util.List;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.validation.VariantValidationResult;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.extension.AstorExtensionPoint;
import fr.inria.astor.core.stats.Stats;

/**
 * 
 * @author Matias Martinez
 *
 */
public abstract class ProgramVariantValidator implements AstorExtensionPoint {

	protected Stats currentStats = Stats.getCurrentStat();

	public void setStats(Stats stats) {
		currentStats = stats;
	};

	/**
	 * Validates a program variant
	 * 
	 * @param variant
	 * @param projectFacade
	 * @return
	 */
	public abstract VariantValidationResult validate(ProgramVariant variant, ProjectRepairFacade projectFacade);

	/**
	 * Determines the test cases from a project
	 * 
	 * @param projectFacade
	 * @return
	 */
	public abstract List<String> findTestCasesToExecute(ProjectRepairFacade projectFacade);

}
