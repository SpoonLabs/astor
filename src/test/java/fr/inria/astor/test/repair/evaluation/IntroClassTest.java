package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.inria.main.evolution.AstorMain;


public class IntroClassTest {

	@Test
	public void hola() throws Exception {
		String[] command = new String[]{
		"-location", "/Users/matias/develop/IntroClassJava/dataset/median/3b2376ab97bb5d1a5dbbf2b45cf062db320757549c761936d19df05e856de894e45695014cd8063cdc22148b13fa1803b3c9e77356931d66f4fbec0efacf7829/003/",
		"-failing","introclassJava.median_3b2376ab_003BlackboxTest:introclassJava.median_3b2376ab_003WhiteboxTest", 
		"-dependencies", "/Users/matias/develop/code/astor/examples/libs/junit-4.11.jar",
		"-seed","10",
		"-skipfaultlocalization",
		};
		AstorMain.main(command);
	}

}
