package fr.inria.astor.core.loop.evolutionary.spaces;


import java.util.List;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
/**
 * Representation of Fix Location Space.
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public interface FixLocationSpace<K,T extends CtElement> {
	
	public void createFixSpaceFromAClass(K key,CtClass rootCloned);
	public T getElementFromSpace(K rootCloned) ;
	public T getElementFromSpace(K rootCloned, String type) ;
	public List<T> getFixSpace(K rootClass);
	public List<T> getFixSpace(K rootClass,String type);

	
}
