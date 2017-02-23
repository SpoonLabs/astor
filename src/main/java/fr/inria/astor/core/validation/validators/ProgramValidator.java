package fr.inria.astor.core.validation.validators;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.VariantValidationResult;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.stats.Stats;

/**
 * 
 * @author Matias Martinez
 *
 */
public abstract class ProgramValidator {

	Stats currentStats = new Stats();

	public void setStats(Stats stats) {
		currentStats = stats;
	};

	public abstract VariantValidationResult validate(ProgramVariant variant, ProjectRepairFacade projectFacade);

}
