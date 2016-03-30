package fr.inria.astor.core.loop.spaces.ingredients;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.stats.StatSpaceSize.INGREDIENT_STATUS;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
/**
 * This class represents a basic strategy to navigate one ingredient space (passed as parameter in the constructor). 
 * Specifically, it randomly takes ingredients and verifies that it was not taken before for the same modification point. 
 * @author Matias Martinez
 *
 */
public class BasicIngredientStrategy extends IngredientStrategy{

	protected Logger log = Logger.getLogger(this.getClass().getName());

	protected Map<String, List<String>> appliedCache = new HashMap<String, List<String>>();

	protected FixIngredientSpace<CtElement, CtCodeElement, String> fixspace = null;


	
	@Override
	public void refineSpaceForProgramVariant(ProgramVariant variant) {
		List<CtType<?>> typesToProcess = null;
		if (getFixspace().spaceScope().equals(IngredientSpaceScope.LOCAL)
				|| getFixspace().spaceScope().equals(IngredientSpaceScope.PACKAGE))
			typesToProcess = variant.getAffectedClasses();

		if (getFixspace().spaceScope().equals(IngredientSpaceScope.GLOBAL))
			typesToProcess = MutationSupporter.getFactory().Type().getAll();
	
		this.fixspace.defineSpace(typesToProcess);
		
	}
	

	
	/**
	 * Return fix ingredient considering cache.
	 * 
	 * @param modificationPoint
	 * @param targetStmt
	 * @param operationType
	 * @param elementsFromFixSpace
	 * @return
	 */
	public Ingredient getFixIngredient(ModificationPoint modificationPoint, 
			AstorOperator operationType) {

		int attempts = 0;
		
		boolean continueSearching = true;
		String type = null;
		if(operationType instanceof ReplaceOp){
			 type = modificationPoint.getCodeElement().getClass().getSimpleName();
		}
		
		int elementsFromFixSpace = 0;
		// Here, search in the space an element without type preference
		List<?> ingredients = null;
		if (type == null) {
			ingredients = this.fixspace.getFixSpace(modificationPoint.getCodeElement());
		} else {// We search for ingredients of one particular type
			ingredients = this.fixspace.getFixSpace(modificationPoint.getCodeElement(), type);
		}
		elementsFromFixSpace = (ingredients == null) ? 0 : ingredients.size();

		while (continueSearching && attempts < elementsFromFixSpace) {
			CtElement fix = null;
			if (type == null) {
				fix = this.fixspace.getElementFromSpace(modificationPoint.getCodeElement());
			} else {
				fix = this.fixspace.getElementFromSpace(modificationPoint.getCodeElement(), type);
			}
			
			if (fix == null) {
				return null;
			}

			attempts++;
			INGREDIENT_STATUS fixStat = null;

			boolean alreadyApplied = alreadyApplied(modificationPoint, fix, operationType), ccompatibleNameTypes = false;

			if (!alreadyApplied && !fix.getSignature().equals(modificationPoint.getCodeElement().getSignature())) {

				ccompatibleNameTypes = VariableResolver.fitInPlace(modificationPoint.getContextOfModificationPoint(), fix);
				continueSearching = !ccompatibleNameTypes;
				fixStat = (ccompatibleNameTypes) ? INGREDIENT_STATUS.compiles : INGREDIENT_STATUS.notcompiles;
			} else {
				fixStat = INGREDIENT_STATUS.alreadyanalyzed;
			}

			IngredientSpaceScope scope = (fix != null)
					? determineIngredientScope(modificationPoint.getCodeElement(), fix, ingredients) : IngredientSpaceScope.GLOBAL;

			if (!continueSearching) {
				return new Ingredient(fix, scope);
			}

		}

		log.debug("--- no mutation left to apply in element " + modificationPoint.getCodeElement().getSignature());
		return null;

	}
	protected IngredientSpaceScope determineIngredientScope(CtElement ingredient, CtElement fix,
			List<?> ingredients) {

		IngredientSpaceScope orig = determineIngredientScope(ingredient, fix);

		String fixStr = fix.toString();
		for (Object ing : ingredients) {
			try {
				ing.toString();
			} catch (Exception e) {
				log.error(e.toString());
				continue;
			}
			if (ing.toString().equals(fixStr)) {
				IngredientSpaceScope n = determineIngredientScope(ingredient, (CtElement) ing);
				if (n.ordinal() < orig.ordinal()) {
					orig = n;
					if (IngredientSpaceScope.values()[0].equals(orig))
						return orig;
				}

			}
		}
		return orig;
	}

	protected IngredientSpaceScope determineIngredientScope(CtElement ingredient, CtElement fix) {

		File ingp = ingredient.getPosition().getFile();
		File fixp = fix.getPosition().getFile();

		if (ingp.getAbsolutePath().equals(fixp.getAbsolutePath())) {
			return IngredientSpaceScope.LOCAL;
		}
		if (ingp.getParentFile().getAbsolutePath().equals(fixp.getParentFile().getAbsolutePath())) {
			return IngredientSpaceScope.PACKAGE;
		}
		return IngredientSpaceScope.GLOBAL;
	}
	

	/**
	 * Check if the fix were applied in the location for a program instance
	 * 
	 * @param id
	 *            program instance id.
	 * @param fix
	 * @param location
	 * @return
	 */
	protected boolean alreadyApplied(ModificationPoint gen, CtElement fixElement, AstorOperator operator) {
		// we add the instance identifier to the patch.
		String lockey = gen.getCodeElement() + "-" + operator.toString();
		String fix = "";
		try {
			fix = fixElement.toString();
		} catch (Exception e) {
			log.error("to string fails");
		}
		List<String> prev = appliedCache.get(lockey);
		// The element does not have any mutation applied
		if (prev == null) {
			prev = new ArrayList<String>();
			prev.add(fix);
			appliedCache.put(lockey, prev);
			return false;
		} else {
			// The element has mutation applied
			if (prev.contains(fix))
				return true;
			else {
				prev.add(fix);
				return false;
			}
		}
	}
	public FixIngredientSpace<CtElement, CtCodeElement, String> getFixspace() {
		return fixspace;
	}


	@Override
	public void setIngredientSpace(FixIngredientSpace<CtElement, CtCodeElement, String> space) {
		this.fixspace = space;
	}
	

	
}
