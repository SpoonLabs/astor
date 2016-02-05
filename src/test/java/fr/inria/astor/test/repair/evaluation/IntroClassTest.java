package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author matias
 *
 */
public class IntroClassTest {

	@Test
	public void test_3b2376_003() throws Exception {
		String dep = new File("./examples/libs/junit-4.11.jar").getAbsolutePath();
		String[] command = new String[]{
		"-location", 
		new File("./examples/introclass/3b2376ab97bb5d1a5dbbf2b45cf062db320757549c761936d19df05e856de894e45695014cd8063cdc22148b13fa1803b3c9e77356931d66f4fbec0efacf7829/003/").getAbsolutePath(),
		"-failing","introclassJava.median_3b2376ab_003BlackboxTest:introclassJava.median_3b2376ab_003WhiteboxTest", 
		"-dependencies", dep,
		"-seed","10",
		"-skipfaultlocalization",
		};
		AstorMain.main(command);
	}

}
