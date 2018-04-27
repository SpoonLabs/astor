package fr.inria.astor.core.solutionsearch.spaces.operators;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.core.solutionsearch.extension.AstorExtensionPoint;

/**
 * Includes all operator that an approach uses.
 * @author Matias Martinez
 *
 */
public class OperatorSpace implements AstorExtensionPoint {

	List<AstorOperator> operators = new ArrayList<>();
	
	public void register(AstorOperator op){
		this.operators.add(op);
	}

	public List<AstorOperator> getOperators() {
		return operators;
	}

	public void setOperators(List<AstorOperator> operators) {
		this.operators = operators;
	}
	
	public AstorOperator[] values(){
		AstorOperator[] op_arr = new AstorOperator[this.getOperators().size()]; 
		return this.getOperators().toArray(op_arr);
	}
	
	public int size() {
		return this.operators.size();
	}
}
