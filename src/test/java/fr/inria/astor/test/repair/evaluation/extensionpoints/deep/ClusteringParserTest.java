package fr.inria.astor.test.repair.evaluation.extensionpoints.deep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.core.manipulation.sourcecode.ClusteringParser;
/**
 * 
 * @author Matias Martinez
 *
 */
@Ignore
public class ClusteringParserTest {

	@Test
	public void testClusterRepresentation() {
		
		ClassLoader classLoader = getClass().getClassLoader();
		File clusteringFile = new File(classLoader.getResource("learningm70"+File.separator+"clustering_test.csv").getFile());
		assertTrue(clusteringFile.exists());
		
		ClusteringParser clusteringParser = new ClusteringParser();
		Map<String, List<String>> clusters = clusteringParser.readClusterFile(clusteringFile.toPath());
		assertNotNull(clusters);
		
		assertTrue(clusters.get("N").isEmpty());
		assertEquals(2, clusters.get("alpha").size());
		assertTrue(clusters.get("alpha").contains("beta"));
		assertTrue(clusters.get("alpha").contains("gamma"));
		assertFalse(clusters.get("N").contains("gamma"));
		assertNull(clusters.get("claveinexistente"));
		
		
		Map<String, List<String>> clusters2 = clusteringParser.readClusterFile(clusteringFile.toPath());
		System.out.println(clusters2.get("a"));

		
	}

}
