package fr.inria.astor.core.output;

import java.util.List;

import fr.inria.astor.core.loop.extension.AstorExtensionPoint;
import fr.inria.astor.core.stats.PatchStat;
/**
 * 
 * @author Matias Martinez
 *
 */
public interface OutputResults extends AstorExtensionPoint {

	
	public Object produceOutput(List<PatchStat> statsForPatches,String output);
}
