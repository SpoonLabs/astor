package fr.inria.astor.core.stats;
/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class StatSpaceSize {

	int id = -1;
	int operations = 0;
	int ingredients = 0;
	
	public StatSpaceSize(int id, int operations, int ingredients) {
		super();
		this.id = id;
		this.operations = operations;
		this.ingredients = ingredients;
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
		return "("+Integer.toString(this.size())+" -op "+operations+" ing "+ingredients+"-)" ;
	}
}
