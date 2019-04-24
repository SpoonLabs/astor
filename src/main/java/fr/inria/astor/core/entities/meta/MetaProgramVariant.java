package fr.inria.astor.core.entities.meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

			Map<Integer, Integer> map2Ingredients = metaVal.getAllCandidates().get(id - 1);
			variants.add(this.getPlainProgramVariantFromMetaId(map2Ingredients));
		}
		return variants;

	}

	public ProgramVariant getPlainProgramVariantFromMetaId(Map<Integer, Integer> mapMoi2Ingredients) {

		ProgramVariant childPlainVariant = new ProgramVariant(id);

		List<MetaOperatorInstance> metas = getMetaOpInstances();

		Collections.reverse(metas);

		int generation = 0;

		for (MetaOperatorInstance metaOperatorInstance : metas) {

			Integer ingredientId = mapMoi2Ingredients.get(metaOperatorInstance.getIdentifier());

			if (metaOperatorInstance.getAllIngredients().containsKey(ingredientId)) {

				generation++;

				AstorOperator operator = metaOperatorInstance.getOperationApplied();

				if (!(operator instanceof MetaOperator))
					continue;

				OperatorInstance normalOpInstance = ((MetaOperator) operator)
						.getConcreteOperatorInstance(metaOperatorInstance, ingredientId);

				childPlainVariant.getOperations(generation).add(normalOpInstance);

			}
		}
		if (generation > 0) {
			childPlainVariant.setGenerationSource(this.getParent().getGenerationSource());
			childPlainVariant.setParent(this.getParent());
			childPlainVariant.addModificationPoints(this.getParent().getModificationPoints());
			childPlainVariant.getBuiltClasses().putAll(this.getParent().getBuiltClasses());

			// Get the meta validation
			MetaValidationResult metaVal = (MetaValidationResult) this.getValidationResult();
			childPlainVariant.setFitness(this.getFitness());
			childPlainVariant.setValidationResult(metaVal.getValidation(id));
			return childPlainVariant;
		}
		return null;

	}

	public ProgramVariant getPlainProgramVariantFromMetaIdOLD(int metaId) {

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
