package fr.inria.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Matias Martinez
 *
 */
public class CommandSummary {

	public Map<String, String> command = new HashMap<>();

	public CommandSummary() {

	}

	public CommandSummary(String[] pCommand) {
		read(pCommand);
	}

	public void read(String[] pCommand) {
		for (int i = 0; i < pCommand.length; i++) {

			String key = pCommand[i];
			if (key.startsWith("-")) {

				if (i < pCommand.length - 1 && !pCommand[i + 1].startsWith("-")) {
					command.put(key, pCommand[i + 1]);
					i++;
				} else
					command.put(key, null);

			}

		}
	}

	public String[] flat() {
		List<String> values = new ArrayList<>();
		for (String key : this.command.keySet()) {
			values.add(key);
			String v = command.get(key);
			if (v != null)
				values.add(v);
		}
		String[] re = new String[values.size()];
		return values.toArray(re);
	}

	public void append(String k, String v) {
		if (this.command.containsKey(k)) {
			String vold = this.command.get(k);
			this.command.put(k, vold + File.pathSeparator + v);
		} else {
			this.command.put(k, v);
		}

	}

}
