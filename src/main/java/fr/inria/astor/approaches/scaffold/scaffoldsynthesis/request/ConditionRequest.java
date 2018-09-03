package fr.inria.astor.approaches.scaffold.scaffoldsynthesis.request;

import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.generator.ScaffoldConditionGenerator;

@SuppressWarnings("rawtypes")
public class ConditionRequest extends AbstractRequest {

	public ConditionRequest(Object[] inputValues, String[] inputNames, Class<?> targetType) {
		super(inputValues, inputNames, targetType);
	}
	
	@Override
	public Object invoke() {

		if (hasInit) {
			return generators.next(candidates);
		}
		
		initCandidates(targetType, inputNames.toArray(new String[inputNames.size()]), inputValues.toArray(new Object[inputValues.size()]), true);
		generators = new ScaffoldConditionGenerator();
		generators.initScaffold();
		hasInit = true;
		return generators.next(candidates);
	}
}
