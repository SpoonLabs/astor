package fr.inria.astor.approaches.mutRepair.operators.ctmutators;

import java.util.List;

import spoon.reflect.declaration.CtElement;
/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 * @param <T>
 */
public interface IMutator<T extends CtElement,M> {
	
	
	public List<M> execute(T toMutate);
	
	public String key();
	
	public void setup();

}
