package fr.inria.astor.approaches.mutRepair.operators.ctmutators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.inria.astor.core.entities.MutantCtElement;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;

/**
 *Comparison binary operation
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class RelationalBinaryOperatorMutator extends BinaryOperatorMutator {
	
	List<BinaryOperatorKind> operators2 = null;
	 
	public RelationalBinaryOperatorMutator(Factory factory) {
		super(factory);
		operators2 =
		          Arrays.asList(
		              BinaryOperatorKind.EQ,
		              BinaryOperatorKind.NE,
		              BinaryOperatorKind.GE, 
		              BinaryOperatorKind.GT,
		              BinaryOperatorKind.LE,
		              BinaryOperatorKind.LT
		            
		        		  );

	}
	double low = 0.1;
	double medium = 0.3;
	double high = 0.5;
	double ehigh = 0.9;
	
	double[][] probs = {
			//EQ
			{0, ehigh,medium,medium,medium,medium}
			,//NE
			{ehigh, 0,medium,medium,medium,medium},
			//GE
			{medium,medium,0,ehigh,low,low},
			//GT
			{medium,medium,ehigh,0,low,low},
			//LE
			{medium,medium,low,low,0,ehigh},
			//LT
			{medium,medium,low,low,ehigh,0}
	};
			
			
	public List<MutantCtElement> execute(CtElement toMutate) {

		List<MutantCtElement> result = new ArrayList<MutantCtElement>();

		 if (toMutate instanceof CtBinaryOperator<?>) {
		      CtBinaryOperator<?> op = (CtBinaryOperator<?>)toMutate;
		     
		      addRemainingsAndFoward(result, op,  operators2);
		    }
		
		return result;
		
	}
	@Override
	public double getProbabilityChange(BinaryOperatorKind oldKind, BinaryOperatorKind modifiedKind) {
		int oldI= operators2.indexOf(oldKind);
		int newI= operators2.indexOf(modifiedKind);
		return probs[oldI][newI];
	}


}
