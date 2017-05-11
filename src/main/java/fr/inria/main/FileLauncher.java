package fr.inria.main;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * 
 * @author Matias Martinez
 *
 */
public class FileLauncher {

	public static Logger logger = Logger.getLogger(Thread.currentThread().getName());

	public Map<String, String> readJSOn(File f, int bugId, String pathDependencies, String projectLocation,
			List<String> otherDep) {

		Map<String, String> args = new HashMap<String, String>();

		JSONParser parser = new JSONParser();

		if (!f.exists()) {
			logger.debug("Any result file for bug " + bugId);
		}
		try {

			Object obj = parser.parse(new FileReader(f));

			JSONObject jsonObject = (JSONObject) obj;

			JSONObject compliance = (JSONObject) jsonObject.get("complianceLevel");

			// System.out.println(compliance.keySet());

			JSONObject comp = (JSONObject) compliance.get(new Integer(bugId).toString());

			args.put("-javacompliancelevel", comp.get("source").toString());
			String libsS = "";
			//

			// CP
			JSONObject srs;
			int max;
			srs = (JSONObject) jsonObject.get("classpath");

			max = 1000;
			for (Object key : srs.keySet()) {

				Integer k = new Integer(key.toString());
				// System.out.println(k);
				// System.out.println(srs.get(key));
				if (bugId <= k && k < max) {
					// System.out.println("xx");
					max = k;
				}

			}

			for (String odep : otherDep) {
				libsS += odep + File.pathSeparator;
			}
			if (projectLocation != null) {
				String[] cps = srs.get(new Integer(max).toString()).toString().split(File.pathSeparator);
				for (String cp : cps) {
					libsS += (projectLocation) + File.separator + cp + File.pathSeparator;
				}
			}

			String pack = (String) jsonObject.get("package");

			args.put("-package", pack);

			JSONArray libs = (JSONArray) jsonObject.get("libs");

			// System.out.println(libs);

			//
			Iterator<JSONObject> iterator = libs.iterator();

			while (iterator.hasNext()) {
				Object jsonLib = iterator.next();
				// System.out.println(jsonLib.toString());
				libsS += pathDependencies + File.separator + jsonLib.toString() + File.pathSeparator;
			}
			//

			///
			args.put("-dependencies", libsS);

			//
			srs = (JSONObject) jsonObject.get("src");
			// System.out.println(srs);
			max = 1000;
			for (Object key : srs.keySet()) {

				Integer k = new Integer(key.toString());
				// System.out.println(k);
				// System.out.println(srs.get(key));
				if (bugId <= k && k < max) {
					max = k;
				}

			}
			// System.out.println("max "+ max);
			JSONObject srsk = (JSONObject) srs.get(new Integer(max).toString());
			// System.out.println(srsk);
			String s = srsk.get("binjava").toString();
			// System.out.println(s);
			args.put("-srctestfolder", srsk.get("srctest").toString());
			args.put("-srcjavafolder", srsk.get("srcjava").toString());
			args.put("-binjavafolder", srsk.get("binjava").toString());
			args.put("-bintestfolder", srsk.get("bintest").toString());
			// System.out.println("-->\n "+args);
			return args;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String[] getCommand(String[] baseArg, File f, int bugId, String pathDependencies) {
		return getCommand(baseArg, f, bugId, pathDependencies, null, new ArrayList<>());
	}

	public String[] getCommand(String[] baseArg, File f, int bugId, String pathDependencies, String projectLocation,
			List<String> otherDep) {

		ArrayList<String> newObj = new ArrayList<String>(Arrays.asList(baseArg));

		newObj.addAll(this.transform(this.readJSOn(f, bugId, pathDependencies, projectLocation, otherDep)));

		String[] completeCommand = new String[newObj.size()];
		completeCommand = newObj.toArray(completeCommand);
		return completeCommand;
	}

	public List<String> transform(Map<String, String> map) {
		List<String> plain = new ArrayList<String>();
		for (String k : map.keySet()) {
			plain.add(k);
			plain.add(map.get(k));
		}
		return plain;
	}

}
