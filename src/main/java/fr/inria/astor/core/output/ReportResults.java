package fr.inria.astor.core.output;

import java.util.List;
import java.util.Map;

import fr.inria.astor.core.solutionsearch.extension.AstorExtensionPoint;
import fr.inria.astor.core.stats.PatchStat;
import fr.inria.astor.core.stats.Stats.GeneralStatEnum;
/**
 * 
 * @author Matias Martinez
 *
 */
public interface ReportResults extends AstorExtensionPoint {

	
	public Object produceOutput(List<PatchStat> statsForPatches, Map<GeneralStatEnum, Object> generalStats, String output);
}
