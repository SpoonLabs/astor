package fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtSimpleType;
/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */

public class IFCollectorProcessor  extends AbstractFixSpaceProcessor<CtIf>  {

	private Logger logger = Logger.getLogger(IFExpressionFixSpaceProcessor.class.getName());


	@Override
	public void process(CtIf element) {
		
		CtSimpleType simpleType = getSimpleType(element);
		/*ifs.add(element);
		ifClasses.put(element, simpleType);*/
		super.add(element);
		
	}
	public boolean mustClone(){
		return false;
	}
	
	public CtSimpleType getSimpleType(CtElement el){
		if(el instanceof CtSimpleType){
			return (CtSimpleType) el;
		}
		else{
			return getSimpleType(el.getParent());
		}
	}
}
