package fr.inria.astor.core.entities;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.AstorOutputStatus;

public class Cntx<I> {

	public final static String PREFIX = "CNTX";

	private Object identifier = null;

	private Map<CNTX_Property, I> information = new HashMap<>();

	public Cntx() {
		super();
	}

	public Cntx(Object identifier, Map<CNTX_Property, I> information) {
		super();
		this.identifier = identifier;
		this.information = information;
	}

	public Cntx(Object identifier) {
		super();
		this.identifier = identifier;
	}

	protected static Logger log = Logger.getLogger(Thread.currentThread().getName());

	public Map<CNTX_Property, I> getInformation() {
		return information;
	}

	public void setInformation(Map<CNTX_Property, I> information) {
		this.information = information;
	}

	@Override
	public String toString() {
		return "Cntx [" + information + "]";
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {

		JSONObject statsjsonRoot = new JSONObject();
		JSONObject generalStatsjson = new JSONObject();
		statsjsonRoot.put("context", generalStatsjson);
		JSONParser parser = new JSONParser();
		for (CNTX_Property generalStat : information.keySet()) {
			Object vStat = information.get(generalStat);

			try {
				Object value = calculateValue(parser, vStat);
				generalStatsjson.put(generalStat.name(), value);
			} catch (ParseException e) {
				System.out.println("Error property: " + generalStat);
				log.error(e);
				e.printStackTrace();
			}

		}

		return statsjsonRoot;
	}

	@SuppressWarnings("unchecked")
	public Object calculateValue(JSONParser parser, Object vStat) throws ParseException {
		Object value = null;
		if (vStat instanceof Cntx) {
			Cntx<Object> cntx = (Cntx) vStat;
			JSONObject composed = new JSONObject();
			for (CNTX_Property property : cntx.getInformation().keySet()) {
				Object v = calculateValue(parser, cntx.getInformation().get(property));
				composed.put(property.name(), v);
			}
			return composed;
		} else if (vStat instanceof AstorOutputStatus || vStat instanceof String)
			// value = parser.parse("\"" + vStat + "\"");
			value = JSONObject.escape(vStat.toString());
		else if (vStat instanceof Collection<?>) {
			JSONArray sublistJSon = new JSONArray();
			Collection acollec = (Collection) vStat;
			for (Iterator iterator = acollec.iterator(); iterator.hasNext();) {
				Object anItemList = (Object) iterator.next();
				sublistJSon.add(calculateValue(parser, anItemList));
			}
			value = sublistJSon;
		} else {
			try {
				value = JSONObject.escape(vStat.toString());
			} catch (Exception e) {
				// System.out.println("Error");
			}
		}
		return value;
	}

	public void save() {
		this.save(this.toJSON());
	}

	public void save(JSONObject statsjsonRoot) {
		String output = ConfigurationProperties.getProperty("workingDirectory");
		String filename = "CNTX_" + ((this.identifier != null) ? this.identifier.toString() : "0");
		String absoluteFileName = output + "/" + filename + ".json";
		try (FileWriter file = new FileWriter(absoluteFileName)) {

			/*
			 * Gson gson = new GsonBuilder().setPrettyPrinting().create(); JsonParser p =
			 * new JsonParser(); JsonElement gsonelement =
			 * p.parse(statsjsonRoot.toJSONString()); file.write(gson.toJson(gsonelement));
			 */

			file.write(statsjsonRoot.toJSONString());
			file.flush();
			log.info("Storing ing JSON at " + absoluteFileName);
			log.info(filename + ":\n" + statsjsonRoot.toJSONString());

		} catch (IOException e) {
			e.printStackTrace();
			log.error("Problem storing ing json file" + e.toString());
		}
	}

	public Object getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Object identifier) {
		this.identifier = identifier;
	}

}
