package fr.inria.astor.core.entities.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.core.validation.results.MetaValidationResult;

/**
 * Meta Progam variant
 * 
 * @author Matias Martinez
 *
 */
public class MetaProgramVariant extends ProgramVariant {

	public MetaProgramVariant(int id) {
		super(id);
	}

	/**
	 * Get all the meta operators
	 * 
	 * @return
	 */
	public List<MetaOperatorInstance> getMetaOpInstances() {

		return this.getAllOperations().stream().filter(MetaOperatorInstance.class::isInstance)
				.map(MetaOperatorInstance.class::cast).collect(Collectors.toList());

	}

	public List<ProgramVariant> getAllPlainProgramVariant() {

		List<ProgramVariant> variants = new ArrayList<>();
		MetaValidationResult metaVal = (MetaValidationResult) this.getValidationResult();

		for (Integer id : metaVal.getSucessfulValidations().keySet()) {
			variants.add(this.getPlainProgramVariantFromMetaId(id));
		}
		return variants;

	}

	public ProgramVariant getPlainProgramVariantFromMetaId(int metaId) {

		List<MetaOperatorInstance> metas = getMetaOpInstances();
		for (MetaOperatorInstance metaOperatorInstance : metas) {
			if (metaOperatorInstance.getAllIngredients().containsKey(metaId)) {

				AstorOperator operator = metaOperatorInstance.getOperationApplied();

				if (!(operator instanceof MetaOperator))
					continue;

				OperatorInstance normalOpInstance = ((MetaOperator) operator)
						.getConcreteOperatorInstance(metaOperatorInstance, metaId);

				ProgramVariant childPlainVariant = new ProgramVariant(id);
				childPlainVariant.setGenerationSource(this.getParent().getGenerationSource());
				childPlainVariant.setParent(this.getParent());
				childPlainVariant.addModificationPoints(this.getParent().getModificationPoints());
				childPlainVariant.getBuiltClasses().putAll(this.getParent().getBuiltClasses());

				int generation = 1;
				childPlainVariant.getOperations(generation).add(normalOpInstance);

				// Get the meta validation
				MetaValidationResult metaVal = (MetaValidationResult) this.getValidationResult();
				childPlainVariant.setFitness(this.getFitness());
				childPlainVariant.setValidationResult(metaVal.getValidation(id));

				return childPlainVariant;
			}
		}

		return null;
	}

}
