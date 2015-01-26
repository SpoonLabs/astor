package fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.Gen;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
import fr.inria.astor.core.manipulation.MutationSupporter;

/**
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 * @param <T>
 */
@Deprecated
public class LocationFixSpace<T extends CtCodeElement> extends UniformRandomFixSpace {

	private Logger logger = Logger.getLogger(UniformRandomFixSpace.class.getName());

	
	public LocationFixSpace(AbstractFixSpaceProcessor processor) throws JSAPException {
		super(processor);
	}

	public LocationFixSpace(List processor) throws JSAPException {
		super(processor);
	}

	Map<Gen, List<WeightCtElement>> cache = new HashMap<Gen, List<WeightCtElement>>();

	//@Override
	public CtElement getElementFromSpace(Gen gen) {
		
		List<WeightCtElement> we = null;
		//Check if the gen was calculated before.
		if (cache.containsKey(gen)) {
			logger.info("Location fix cache");
			we = cache.get(gen);
		} else {
			we = createWeightLocationSpace(gen);
			if(we != null && we.size()>0){
			//Saving in cache
			cache.put(gen, we);
			}
			else{
				logger.info("Empty fix ingredient space");
				return null;
			}
		}
		logger.info("gen: "+gen+": "+we);
		
		// Now, we  choose
		WeightCtElement selected = WeightCtElement.selectElementWeightBalanced(we);
		if(selected == null)
			return null;
		CtElement cloned = 	MutationSupporter.clone((CtCodeElement)selected.element);
		
		return cloned;
	}


	private List<WeightCtElement> createWeightLocationSpace(Gen gen) {
		List<WeightCtElement> we = new ArrayList<WeightCtElement>();
		int lineGen = gen.getRootElement().getPosition().getLine();
		// We got the space for the fixspace
		List space = this.getFixSpace(gen.getCtClass().getQualifiedName());
		List order = new ArrayList(space);
		//Empty space
		if(order == null || order.size() == 0){
			return null;
		}
		
		int sumdist = 0;
		
		for (Object object : order) {
			CtCodeElement ce = (CtCodeElement) object;
					
			int elline = ce.getPosition().getLine();
			int distance = Math.abs(elline - lineGen);
			if(distance == 0){
				continue;
			}
			sumdist += distance;
			we.add(new WeightCtElement(ce, distance));
		}
		int sum2 = 0;
		for (WeightCtElement weightCtElement : we) {
			weightCtElement.distance = sumdist - weightCtElement.line;
			sum2 += weightCtElement.distance;
		}

		for (WeightCtElement weightCtElement : we) {
			weightCtElement.weight = (double) weightCtElement.distance / (double) sum2;
		}

		
		//
		if(we.size() == 0){
			//logger.info("fix ingredient space size == 0");
			return null;
		}
		// Calculate Accumulative
		WeightCtElement.feedAccumulative(we);
	
		return we;
	}



}
