package fr.inria.astor.core.output;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.stats.PatchHunkStats;
import fr.inria.astor.core.stats.PatchStat;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.core.stats.PatchStat.HunkStatEnum;
import fr.inria.astor.core.stats.PatchStat.PatchStatEnum;
import fr.inria.astor.core.stats.Stats.GeneralStatEnum;

/**
 * 
 * @author Matias Martinez
 *
 */
public class PatchJSONStandarOutput implements ReportResults {

	private static Logger log = Logger.getLogger(Stats.class.getName());

	@SuppressWarnings("unchecked")
	public Object produceOutput(List<PatchStat> statsForPatches, Map<GeneralStatEnum, Object> generalStats,
			String output) {

		JSONObject statsjsonRoot = new JSONObject();
		JSONArray patchlistJson = new JSONArray();
		statsjsonRoot.put("patches", patchlistJson);
		JSONObject generalStatsjson = new JSONObject();
		statsjsonRoot.put("general", generalStatsjson);
		
		 for (GeneralStatEnum generalStat : GeneralStatEnum.values()) {
			 generalStatsjson.put(generalStat.name(), generalStats.get(generalStat));
		 }

		for (PatchStat patchStat : statsForPatches) {

			JSONObject patchjson = new JSONObject();
			patchlistJson.add(patchjson);

			Map<PatchStatEnum, Object> stats = patchStat.getStats();
			for (PatchStatEnum statKey : PatchStatEnum.values()) {
				if (statKey.equals(PatchStatEnum.HUNKS)) {
					List<PatchHunkStats> hunks = (List<PatchHunkStats>) stats.get(statKey);
					JSONArray hunksListJson = new JSONArray();
					patchjson.put("patchhunks", hunksListJson);

					for (PatchHunkStats patchHunkStats : hunks) {
						Map<HunkStatEnum, Object> statshunk = patchHunkStats.getStats();

						JSONObject hunkjson = new JSONObject();
						hunksListJson.add(hunkjson);
						for (HunkStatEnum hs : HunkStatEnum.values()) {
							if (statshunk.containsKey(hs))
								hunkjson.put(hs.name(), JSONObject.escape(statshunk.get(hs).toString()));
						}
					}

				} else {
					if (stats.containsKey(statKey))
						patchjson.put(statKey.name(), JSONObject.escape(stats.get(statKey).toString()));
				}

			}

		}
		String filename = ConfigurationProperties.getProperty("jsonoutputname");
		String absoluteFileName = output + "/" + filename + ".json";
		try (FileWriter file = new FileWriter(absoluteFileName)) {

			file.write(statsjsonRoot.toJSONString());
			file.flush();
			log.info("Storing ing JSON at " + absoluteFileName);
			log.info(filename + ":" + statsjsonRoot.toJSONString());

		} catch (IOException e) {
			e.printStackTrace();
			log.error("Problem storing ing json file" + e.toString());
		}

		return null;
	}

}
