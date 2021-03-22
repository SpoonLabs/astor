package fr.inria.astor.core.entities.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.inria.astor.approaches.tos.core.MetaGenerator;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.core.validation.results.MetaValidationResult;
import spoon.reflect.declaration.CtElement;

/**
 * Meta Program variant
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

		List<MetaOperatorInstance> list = this.getAllOperations().stream()
				.filter(MetaOperatorInstance.class::isInstance).map(MetaOperatorInstance.class::cast)
				.collect(Collectors.toList());
		list.sort((e1, e2) -> Integer.compare(e1.metaIdentifier, e2.metaIdentifier));
		return list;

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
		List<MetaOperatorInstance> metaOps = getMetaOpInstances();
		List<OperatorInstance> noMetaOps = new ArrayList(this.getAllOperations());
		// Remove all meta ops
		noMetaOps.removeAll(metaOps);

		return getPlainProgramVariantFromMetaId(mapMoi2Ingredients, metaOps, noMetaOps);
	}

	private ProgramVariant getPlainProgramVariantFromMetaId(Map<Integer, Integer> mapMoi2Ingredients,
			List<MetaOperatorInstance> metaOps, List<OperatorInstance> noMetaOps) {

		ProgramVariant childPlainVariant = new ProgramVariant(id);

		int generation = 0;

		MetaGenerator.getSourceTarget().clear();
		for (MetaOperatorInstance metaOperatorInstance : metaOps) {

			Integer ingredientId = mapMoi2Ingredients.get(metaOperatorInstance.getIdentifier());

			if (metaOperatorInstance.getAllIngredients().containsKey(ingredientId)) {

				generation++;

				AstorOperator operator = metaOperatorInstance.getOperationApplied();

				if (!(operator instanceof MetaOperator))
					continue;

				CtElement meta_element = (CtElement) metaOperatorInstance.getAllIngredients().get(ingredientId)
						.getMetadata().get("meta_object");

				OperatorInstance normalOpInstance = ((MetaOperator) operator)
						.getConcreteOperatorInstance(metaOperatorInstance, ingredientId);

				MetaGenerator.getSourceTarget().put(meta_element, normalOpInstance.getModified());

				childPlainVariant.getOperations(generation).add(normalOpInstance);

			}
		}
		// Adding the no meta ops
		for (OperatorInstance normalOpInstances : noMetaOps) {
			generation++;
			childPlainVariant.getOperations(generation).add(normalOpInstances);
		}

		if (generation > 0) {
			childPlainVariant.setGenerationSource(this.getParent().getGenerationSource());
			childPlainVariant.setParent(this);
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
