package fr.inria.astor.core.loop.evolutionary.spaces;


import java.util.List;

import fr.inria.astor.core.loop.evolutionary.spaces.implementation.IngredientSpaceStrategy;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtSimpleType;
/**
 * Representation of Fix Location Space.
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public interface FixLocationSpace<L extends Object,I extends CtElement, T extends Object > {
	
	/**
	 * 
	 * @param affected cttypes affected by the program variants. That is, types that can me modified during the evolution
	 *@param all corresponds to all types from the program under analysis.
	 */
	public void defineSpace(List<CtSimpleType<?>> affected );
	//public void createFixSpaceFromAClass(L key,CtSimpleType<?> rootCloned);
	public I getElementFromSpace(L rootCloned) ;
	public I getElementFromSpace(L rootCloned, T type) ;
	public List<I> getFixSpace(L rootClass);
	public List<I> getFixSpace(L rootClass, T type);
	public IngredientSpaceStrategy strategy();
	
}
