package fr.inria.astor.approaches.jgenprog;

import fr.inria.astor.core.ingredientbased.IngredientBasedPlugInLoader;
import fr.inria.astor.core.solutionsearch.AstorCoreEngine;

/**
 * 
 * @author Matias Martinez
 *
 */
public class jGenProgPlugInLoader extends IngredientBasedPlugInLoader {

	@Override
	public void loadOperatorSpaceDefinition(AstorCoreEngine approach) throws Exception {

		super.loadOperatorSpaceDefinition(approach);

		if (approach.getOperatorSpace() == null) {

			approach.setOperatorSpace(new jGenProgSpace());
		}

	}

}
