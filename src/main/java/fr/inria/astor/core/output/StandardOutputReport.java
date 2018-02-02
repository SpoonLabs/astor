package fr.inria.astor.core.output;

import java.util.List;
import java.util.Map;

import fr.inria.astor.core.stats.PatchHunkStats;
import fr.inria.astor.core.stats.PatchStat;
import fr.inria.astor.core.stats.PatchStat.HunkStatEnum;
import fr.inria.astor.core.stats.PatchStat.PatchStatEnum;
import fr.inria.astor.core.stats.Stats.GeneralStatEnum;

/**
 * 
 * @author Matias Martinez
 *
 */
public class StandardOutputReport implements ReportResults {

	@Override
	public Object produceOutput(List<PatchStat> statsForPatches, Map<GeneralStatEnum, Object> generalStats,
			String output) {

		StringBuffer buff = new StringBuffer();
		buff.append(System.getProperty("line.separator"));
		buff.append("General stats:");
		buff.append(System.getProperty("line.separator"));

		for (GeneralStatEnum generalStat : GeneralStatEnum.values()) {
			buff.append(generalStat.name());
			buff.append("=");
			buff.append(generalStats.get(generalStat));
			buff.append(System.getProperty("line.separator"));
		}

		buff.append(System.getProperty("line.separator"));
		buff.append("Patch stats:");
		buff.append(System.getProperty("line.separator"));
		int ips = 0;
		// Stats of patches
		for (PatchStat patchStat : statsForPatches) {
			
			buff.append(System.getProperty("line.separator"));
			buff.append("Patch "+ (++ips));
			buff.append(System.getProperty("line.separator"));
			Map<PatchStatEnum, Object> stats = patchStat.getStats();
			for (PatchStatEnum statKey : PatchStatEnum.values()) {
				if (statKey.equals(PatchStatEnum.HUNKS)) {
					List<PatchHunkStats> hunks = (List<PatchHunkStats>) stats.get(statKey);
					// Print the properties of a Patch hunk
					int i = 0;
					for (PatchHunkStats patchHunkStats : hunks) {
						Map<HunkStatEnum, Object> statshunk = patchHunkStats.getStats();
						buff.append("--Patch Hunk #" + (++i));
						for (HunkStatEnum hs : HunkStatEnum.values()) {

							buff.append(System.getProperty("line.separator"));
							buff.append(hs.name());
							buff.append("=");
							buff.append(statshunk.get(hs));
							buff.append(System.getProperty("line.separator"));
						}
					}

					buff.append(System.getProperty("line.separator"));
				} else {
					buff.append(statKey.name());
					buff.append("=");
					buff.append(stats.get(statKey));
					buff.append(System.getProperty("line.separator"));
				}

			}

		}
		String outString = buff.toString();
		System.out.println("Astor Output:\n" + outString);
		return output;
	}
}
