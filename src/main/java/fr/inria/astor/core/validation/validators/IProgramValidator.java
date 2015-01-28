package fr.inria.astor.core.validation.validators;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.ProgramVariantValidationResult;
import fr.inria.astor.core.setup.ProjectRepairFacade;
/**
 * 
 * @author Matias Martinez
 *
 */
public interface IProgramValidator {

		public ProgramVariantValidationResult validate(ProgramVariant variant,  ProjectRepairFacade  projectFacade) ;

}
