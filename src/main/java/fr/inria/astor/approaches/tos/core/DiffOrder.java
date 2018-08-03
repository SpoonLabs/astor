package fr.inria.astor.approaches.tos.core;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
public class DiffOrder implements HoleOrder {

	protected Map<String, Integer> frequancies = null;

	public DiffOrder() {

		if (!ConfigurationProperties.hasProperty("pathjsonfrequency")) {
			throw new IllegalArgumentException("Missing pathjsonfrequency");
		}

		String path = ConfigurationProperties.getProperty("pathjsonfrequency");
		this.frequancies = load(path);
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
				return f2.compareTo(f1);
			}

		});

		return holesSorted;

	}

	public String getKey(CtCodeElement element) {

		return element.getClass().getSimpleName();
	}

	public LinkedHashMap load(String path) {
		Map<String, Integer> frq = new LinkedHashMap<>();

		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(path));

			JSONObject jsonObject = (JSONObject) obj;
			System.out.println(jsonObject);

			// loop array
			JSONArray msg = (JSONArray) jsonObject.get("frequency");
			Iterator<JSONObject> iterator = msg.iterator();
			while (iterator.hasNext()) {
				JSONObject io = iterator.next();
				Object type = io.get("c");
				Integer frequency = Integer.valueOf(io.get("f").toString());
				String key = "";
				// example: UPD_CtInvocationImpl_CtBlockImpl
				// String[] comp = type.toString().split("_");
				// key += comp[1];// Element affected
				key += type;
				frq.put(key, frequency);
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

	public Map<String, Integer> getFrequancies() {
		return frequancies;
	}

	public void setFrequancies(Map<String, Integer> frequancies) {
		this.frequancies = frequancies;
	}
}
