package fr.inria.astor.test.repair.evaluation.dpl;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import fr.inria.astor.core.manipulation.sourcecode.ClusteringParser;
/**
 * 
 * @author Matias Martinez
 *
 */
public class ClusteringParserTest {

	@Test
	public void testClusterRepresentation() {
		
		ClassLoader classLoader = getClass().getClassLoader();
		File clusteringFile = new File(classLoader.getResource("learningm70"+File.separator+"clustering.csv").getFile());
		assertTrue(clusteringFile.exists());
		
		ClusteringParser clusteringParser = new ClusteringParser();
		boolean loaded = clusteringParser.readClusterFile(clusteringFile.toPath());
		assertTrue(loaded);
		
		assertTrue(clusteringParser.getClusters().get("N").isEmpty());
		assertEquals(2, clusteringParser.getClusters().get("alpha").size());
		assertTrue(clusteringParser.getClusters().get("alpha").contains("beta"));
		assertTrue(clusteringParser.getClusters().get("alpha").contains("gamma"));
		assertFalse(clusteringParser.getClusters().get("N").contains("gamma"));
		assertNull(clusteringParser.getClusters().get("claveinexistente"));

		
	}

}
