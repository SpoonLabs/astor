package fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.SpecialStatementFixSpaceProcessor;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import spoon.reflect.declaration.CtType;

/**
 * 
 * @author Matias Martinez
 *
 */
public class NGramManager {

	protected Map<String, NGrams> ngramsSplitted = null;
	protected NGrams ngglobal = null;

	protected Logger logger = Logger.getLogger(ProbabilisticTransformationStrategy.class.getName());

	public NGramManager(Map<String, NGrams> ngramsSplitted, NGrams ngglobal) {
		super();
		this.ngramsSplitted = ngramsSplitted;
		this.ngglobal = ngglobal;
	}

	public void init() throws JSAPException {
		this.ngglobal = new NGrams();
		this.ngramsSplitted = new HashMap<>();
		logger.debug("Calculating N-grams");

		TargetElementProcessor<?> elementProcessor = new SpecialStatementFixSpaceProcessor();
		Boolean mustCloneOriginalValue = ConfigurationProperties.getPropertyBool("duplicateingredientsinspace");
		// Forcing to duplicate
		ConfigurationProperties.setProperty("duplicateingredientsinspace", "true");

		List<CtType<?>> all = MutationSupporter.getFactory().Type().getAll();

		GramProcessor pt = new GramProcessor(elementProcessor);
		for (CtType<?> ctType : all) {
			NGrams ng = pt.calculateGrams4Class(ctType);
			ngramsSplitted.put(ctType.getQualifiedName(), ng);

		}
		ngglobal = pt.calculateGlobal(all);

		// reset property clone
		ConfigurationProperties.setProperty("duplicateingredientsinspace", Boolean.toString(mustCloneOriginalValue));

	}
	public boolean initialized(){
		return (ngramsSplitted != null && ngglobal != null);
	}
	public Map<String, NGrams> getNgramsSplitted() {
		return ngramsSplitted;
	}

	public void setNgramsSplitted(Map<String, NGrams> ngramsSplitted) {
		this.ngramsSplitted = ngramsSplitted;
	}

	public NGrams getNgglobal() {
		return ngglobal;
	}

	public void setNgglobal(NGrams ngglobal) {
		this.ngglobal = ngglobal;
	}

}
