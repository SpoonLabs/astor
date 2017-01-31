package fr.inria.astor.core.manipulation.sourcecode;

import java.io.BufferedReader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ClusteringParser {

	private static Logger logger = Logger.getLogger(ClusteringParser.class.getName());

	private Map<String, List<String>> clusters = new HashMap<>();

	public boolean readClusterFile(Path file) {

		clusters.clear();

		Charset charset = Charset.forName("US-ASCII");
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] metad = line.split(",");
				String lexicalElement = metad[0];
				String idcluster = metad[1];
				List<String> clusterElements = new ArrayList<String>();

				if (metad.length == 3) {
					String cluster = metad[2];
					for (String elementCluster : cluster.split(";")) {
						clusterElements.add(elementCluster);
					}
				}
				clusters.put(lexicalElement, clusterElements);

			}
		} catch (IOException x) {
			logger.error("IOException: %s%n", x);
		}
		return !clusters.isEmpty();
	}

	public Map<String, List<String>> getClusters() {
		return clusters;
	}

	public void setClusters(Map<String, List<String>> clusters) {
		this.clusters = clusters;
	}
}
