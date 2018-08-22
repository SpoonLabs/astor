package fr.inria.astor.approaches.tos.core;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import fr.inria.astor.core.setup.ConfigurationProperties;
import spoon.reflect.code.CtCodeElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class SimpleDiffOrderFromJSON implements HoleOrder {
	protected static Logger log = Logger.getLogger(Thread.currentThread().getName());

	protected Map<String, Integer> frequancies = null;

	public SimpleDiffOrderFromJSON() {

		if (!ConfigurationProperties.hasProperty("pathjsonfrequency")) {
			throw new IllegalArgumentException("Missing pathjsonfrequency");
		}
		log.debug("Loading Json file");
		String path = ConfigurationProperties.getProperty("pathjsonfrequency");
		this.frequancies = loadFile(path);
	}

	@Override
	public List<CtCodeElement> orderHoleElements(List<CtCodeElement> holes) {

		List<CtCodeElement> holesSorted = new ArrayList<>(holes);

		holesSorted.sort(new Comparator<CtCodeElement>() {

			@Override
			public int compare(CtCodeElement o1, CtCodeElement o2) {
				String k1 = getKey(o1);
				String k2 = getKey(o2);
				Integer f1 = frequancies.get(k1);
				Integer f2 = frequancies.get(k2);
				if (f1 == null && f2 == null) {
					log.debug("Comparison error: elements are null " + k1 + " and " + k2);
					return 0;
				}
				if (f1 == null) {
					log.debug("Comparison error: k1 is null " + k1);
					return 1;
				}
				if (f2 == null) {
					log.debug("Comparison error: k2 is null " + k2);
					return -1;
				}
				return f2.compareTo(f1);
			}

		});

		return holesSorted;

	}

	public LinkedHashMap loadFile(String path) {
		Map<String, Integer> frq = new LinkedHashMap<>();

		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(path));

			JSONObject jsonObject = (JSONObject) obj;

			// loop array
			JSONArray msg = (JSONArray) jsonObject.get(tagName());
			Iterator<JSONObject> iterator = msg.iterator();
			while (iterator.hasNext()) {
				JSONObject io = iterator.next();
				Object type = io.get("c");
				if (accept(type)) {
					Integer frequency = Integer.valueOf(io.get("f").toString());
					String key = getKeyFromJSON(type);
					frq.put(key, frequency);
				}
			}

			LinkedHashMap sorted = frq.entrySet().stream()
					.sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
							LinkedHashMap::new));

			return sorted;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public boolean accept(Object type) {
		// accept all changes
		return true;
	}

	public String getKey(CtCodeElement element) {

		return element.getClass().getSimpleName();
	}

	public String getKeyFromJSON(Object type) {
		return type.toString();
	}

	public String tagName() {
		return "frequency";
	}

	public Map<String, Integer> getFrequancies() {
		return frequancies;
	}

	public void setFrequancies(Map<String, Integer> frequancies) {
		this.frequancies = frequancies;
	}
}
