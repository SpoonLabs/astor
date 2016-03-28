package fr.inria.astor.core.manipulation.filters;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */

public class IFCollectorProcessor  extends AbstractFixSpaceProcessor<CtIf>  {

	private Logger logger = Logger.getLogger(IFExpressionFixSpaceProcessor.class.getName());


	@Override
	public void process(CtIf element) {
		
		CtType simpleType = getSimpleType(element);
		/*ifs.add(element);
		ifClasses.put(element, simpleType);*/
		super.add(element);
		
	}
	public boolean mustClone(){
		return false;
	}
	
	public CtType getSimpleType(CtElement el){
		if(el instanceof CtType){
			return (CtType) el;
		}
		else{
			return getSimpleType(el.getParent());
		}
	}
}
