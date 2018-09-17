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
		for (CNTX_Property generalStat : CNTX_Property.values()) {
			Object vStat = information.get(generalStat);
			if (vStat == null)
				generalStatsjson.put(generalStat.name(), null);
			else {
				try {
					Object value = null;
					if (vStat instanceof AstorOutputStatus || vStat instanceof String)
						value = parser.parse("\"" + vStat + "\"");
					else if (vStat instanceof Collection<?>) {
						JSONArray sublistJSon = new JSONArray();
						Collection acollec = (Collection) vStat;
						for (Iterator iterator = acollec.iterator(); iterator.hasNext();) {
							Object anItemList = (Object) iterator.next();
							sublistJSon.add((JSONObject.escape(anItemList.toString())));

						}
						value = sublistJSon;
					} else {

						value = parser.parse(vStat.toString());
					}
					generalStatsjson.put(generalStat.name(), value);
				} catch (ParseException e) {
					log.error(e);
					e.printStackTrace();
				}
			}

		}

		return statsjsonRoot;
	}

	public void save() {
		this.save(this.toJSON());
	}

	public void save(JSONObject statsjsonRoot) {
		String output = ConfigurationProperties.getProperty("workingDirectory");
		String filename = "CNTX_" + ((this.identifier != null) ? this.identifier.toString() : "0");
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
	}

	public Object getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Object identifier) {
		this.identifier = identifier;
	}

}
