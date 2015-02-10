package fr.inria.astor.core.loop.evolutionary.spaces;


import java.util.List;


import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtSimpleType;
/**
 * Representation of Fix Location Space.
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public interface FixLocationSpace<K,T extends CtElement> {
	
	/**
	 * 
	 * @param affected cttypes affected by the program variants. That is, types that can me modified during the evolution
	 * @param all corresponds to all types from the program under analysis.
	 */
	public void defineSpace(List<CtSimpleType<?>> affected, List<CtSimpleType<?>> all );
	
	public void createFixSpaceFromAClass(K key,CtSimpleType<?> rootCloned);
	public T getElementFromSpace(K rootCloned) ;
	public T getElementFromSpace(K rootCloned, String type) ;
	public List<T> getFixSpace(K rootClass);
	public List<T> getFixSpace(K rootClass,String type);

	
}
