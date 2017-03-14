package fr.inria.astor.util;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
/**
 * 
 * @author Matias Martinez
 *
 */
public class CommandSummaryTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCommand1() {
		String[] args = new String[] { "-dependencies","dd", "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				"dd",
				//
				"-scope", "package", 
				"-seed", "10", 
				"-maxgen", "500", 
				"-stopfirst", "false",//two solutions
				"-maxtime", "10",
				"-population","1",
				"-transformingredient"

		};
		
		CommandSummary c = new CommandSummary();
		c.read(args);
		assertNotNull(c.command);
		assertEquals(c.command.get("-seed"), "10");
		assertTrue(c.command.containsKey("-transformingredient"));
		assertNull(c.command.get("-transformingredient") );
		
		assertEquals(args.length, c.flat().length);
		
		for (String a : args) {
			if(a.startsWith("-"))
				assertTrue(c.command.keySet().contains(a));
			else
				assertTrue(c.command.values().contains(a));
		}
	}

}
