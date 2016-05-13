package fr.inria.astor.core.stats;

import fr.inria.astor.core.loop.spaces.ingredients.scopes.IngredientSpaceScope;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;

/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class StatSpaceSize {

	int id = -1;
	int operations = 0;
	int ingredients = 0;
	String genType = "";
	String ingredientType = "";
	public INGREDIENT_STATUS states; 
	IngredientSpaceScope ingredientSpaceStrategy;
	AstorOperator operationType;
	
	public enum INGREDIENT_STATUS { compiles, notcompiles, alreadyanalyzed }
	

	public StatSpaceSize(
			int id,String type, int ingredients, String ingType,INGREDIENT_STATUS states, IngredientSpaceScope ingredientSpaceStrategy, AstorOperator operationType) {
		super();
		this.id = id;
		this.genType = type;
		this.ingredients = ingredients;
		this.ingredientType = ingType;
		this.states = states;
		this.ingredientSpaceStrategy = ingredientSpaceStrategy;
		this.operationType = operationType;
	}
	
	public StatSpaceSize(int operations, int ingredients) {
		super();
		this.operations = operations;
		this.ingredients = ingredients;
	}
	
	public int size(){
		
		if(operations == 0)return ingredients;
		
		if(ingredients == 0) return operations;
		
		return operations * ingredients;
	}

	@Override
	public String toString() {
		return "("
				//+ "id:"+id+"| "
				+genType+"| size: "+Integer.toString(this.size())/*+" -op "+operations+" ing "+ingredients+*/
				+"| "+states+"| "+ingredientSpaceStrategy.toString()
				+ "| "+operationType
				+") " ;
	}
}
