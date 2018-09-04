package fr.inria.astor.core.output;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.stats.PatchHunkStats;
import fr.inria.astor.core.stats.PatchStat;
import fr.inria.astor.core.stats.PatchStat.HunkStatEnum;
import fr.inria.astor.core.stats.PatchStat.PatchStatEnum;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.core.stats.Stats.GeneralStatEnum;
import fr.inria.main.AstorOutputStatus;

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
		JSONParser parser = new JSONParser();
		for (GeneralStatEnum generalStat : GeneralStatEnum.values()) {
			Object vStat = generalStats.get(generalStat);
			if (vStat == null)
				generalStatsjson.put(generalStat.name(), null);
			else {
				try {
					Object value = null;
					if (vStat instanceof AstorOutputStatus || vStat instanceof String)
						value = parser.parse("\"" + vStat + "\"");
					else
						value = parser.parse(vStat.toString());
					generalStatsjson.put(generalStat.name(), value);
				} catch (ParseException e) {
					log.error(e);
				}
			}

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
					try {
						if (stats.containsKey(statKey))
							patchjson.put(statKey.name(), JSONObject.escape(stats.get(statKey).toString()));
					} catch (Exception e) {
						log.error(e);
						log.error("problems with key " + statKey.name());
					}
				}

			}

		}
		String filename = ConfigurationProperties.getProperty("jsonoutputname");
		String absoluteFileName = output + "/" + filename + ".json";
		try (FileWriter file = new FileWriter(absoluteFileName)) {

			file.write(statsjsonRoot.toJSONString());
			file.flush();
			log.info("Storing ing JSON at " + absoluteFileName);
			log.info(filename + ":\n" + statsjsonRoot.toJSONString());

		} catch (IOException e) {
			e.printStackTrace();
			log.error("Problem storing ing json file" + e.toString());
		}

		return statsjsonRoot;
	}

}
