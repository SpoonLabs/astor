package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.inria.astor.approaches.tos.core.evalTos.EvalTOSClusterApproach;
import fr.inria.astor.approaches.tos.core.evalTos.navigation.UpdateParentDiffOrderFromJSON;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author matias
 *
 */
public class IntroClassJavaRegressionTest {

	public static Logger log = Logger.getLogger(IntroClassJavaRegressionTest.class.getName());

	String[] subjects = new String[] {
			"1,median/6aaeaf2ffb623b5736c0c0b9e8a1a3b080e8aef14d963d899eb3e4073245ad1171e26fb2a64fb88db6e40aa59e894a55eac832e38d444755cb3b6ad10ba74c62/000,1,70",
			"2,median/fe9d5fb933c758c2cbbd859e3ecbd2d2eaecc2843b57cfd97da99af24de59f189a144d13ce81ec585d7c2f7367f70c0fb2aec8269eed1fd8380def614817ef7c/000,2,73",
			"3,median/fe9d5fb933c758c2cbbd859e3ecbd2d2eaecc2843b57cfd97da99af24de59f189a144d13ce81ec585d7c2f7367f70c0fb2aec8269eed1fd8380def614817ef7c/002,2,73",
			"4,median/1bf73a9ca02e8d64fd54aaf8de9ad5d029ebfff775224c3f5b010db80793ea8698863524436d122a100abc08c3486a795eb1caf39f78fc55d3948124861669ac/003,1,70",
			"5,median/0cdfa335eea3c612e6fa3ad261276b0c3ebbc6ff0ff13c20bdc249bad29a8037ca6dc887dd28558964e1e1a24f47c4cffc05adba525285dc8b93660cdf9b8b7c/003,0,69",
			"6,median/0cdfa335eea3c612e6fa3ad261276b0c3ebbc6ff0ff13c20bdc249bad29a8037ca6dc887dd28558964e1e1a24f47c4cffc05adba525285dc8b93660cdf9b8b7c/003,0,69",
			"7,median/6e464f2b5ba1d5ad3d4fc366e7d7712b424aabd8b41ca36257115a16416d202feb27397a413d04944c9ac76976fa8ff8ae646144855e08791ebf9593d1caaaca/003,1,69",
			"8,median/89b1a701f92f7e190fd4caf2ad32365f2c9261790b9a33967efd0bfb4d047c721db673225a01819900d542401a0b95d29db7ff0d8548087faabd4230f896474f/007,0,69",
			"9,median/89b1a701f92f7e190fd4caf2ad32365f2c9261790b9a33967efd0bfb4d047c721db673225a01819900d542401a0b95d29db7ff0d8548087faabd4230f896474f/010,0,76",
			"10,median/89b1a701f92f7e190fd4caf2ad32365f2c9261790b9a33967efd0bfb4d047c721db673225a01819900d542401a0b95d29db7ff0d8548087faabd4230f896474f/003,0,69",
			"11,median/36d8008b13f6475ca8fa4553fea10042b0a6c623665065672051445c3464d61b29b47cb66321844a0264505a0f5ccf5aa6de072aa266b5a8b0cf13198380a389/000,1,70",
			"12,smallest/88394fc00b7053b386e97564e28ef68421ae09c0baeaf887bba7e254f452419783ed8f774bff3c0c7d23bdc8f6c1443fb47c7af97323d4d3d63cc088d3b6841a/003,14,76",
			"13,smallest/88394fc00b7053b386e97564e28ef68421ae09c0baeaf887bba7e254f452419783ed8f774bff3c0c7d23bdc8f6c1443fb47c7af97323d4d3d63cc088d3b6841a/003,14,76",
			"14,smallest/88394fc00b7053b386e97564e28ef68421ae09c0baeaf887bba7e254f452419783ed8f774bff3c0c7d23bdc8f6c1443fb47c7af97323d4d3d63cc088d3b6841a/003,14,76",
			"15,smallest/88394fc00b7053b386e97564e28ef68421ae09c0baeaf887bba7e254f452419783ed8f774bff3c0c7d23bdc8f6c1443fb47c7af97323d4d3d63cc088d3b6841a/003,14,76",
			"16,smallest/88394fc00b7053b386e97564e28ef68421ae09c0baeaf887bba7e254f452419783ed8f774bff3c0c7d23bdc8f6c1443fb47c7af97323d4d3d63cc088d3b6841a/003,14,76",
			"17,smallest/88394fc00b7053b386e97564e28ef68421ae09c0baeaf887bba7e254f452419783ed8f774bff3c0c7d23bdc8f6c1443fb47c7af97323d4d3d63cc088d3b6841a/002,14,76",
			"18,smallest/88394fc00b7053b386e97564e28ef68421ae09c0baeaf887bba7e254f452419783ed8f774bff3c0c7d23bdc8f6c1443fb47c7af97323d4d3d63cc088d3b6841a/002,14,76",
			"19,smallest/88394fc00b7053b386e97564e28ef68421ae09c0baeaf887bba7e254f452419783ed8f774bff3c0c7d23bdc8f6c1443fb47c7af97323d4d3d63cc088d3b6841a/002,14,76",
			"20,smallest/88394fc00b7053b386e97564e28ef68421ae09c0baeaf887bba7e254f452419783ed8f774bff3c0c7d23bdc8f6c1443fb47c7af97323d4d3d63cc088d3b6841a/002,14,76",
			"21,smallest/88394fc00b7053b386e97564e28ef68421ae09c0baeaf887bba7e254f452419783ed8f774bff3c0c7d23bdc8f6c1443fb47c7af97323d4d3d63cc088d3b6841a/002,14,76",
			"22,smallest/818f8cf4e2e713753d02db9ee70a099b71f2a5a6bdc904191cf9ba68cfa5f64328464dccdd9b02fe0822e14a403dc196fe88b9964969409e60c93a776186a86a/003,2,82",
			"23,smallest/818f8cf4e2e713753d02db9ee70a099b71f2a5a6bdc904191cf9ba68cfa5f64328464dccdd9b02fe0822e14a403dc196fe88b9964969409e60c93a776186a86a/003,3,83",
			"24,smallest/769cd811312cbbb82c87033a78ac9584ad282550bcb9cc3ae8c4e3da44c288c1a5b3954e01998c3c0654ee6774ceab66e9fe5b135750905c917d2b0bb5fab98b/007,14,76",
			"25,smallest/769cd811312cbbb82c87033a78ac9584ad282550bcb9cc3ae8c4e3da44c288c1a5b3954e01998c3c0654ee6774ceab66e9fe5b135750905c917d2b0bb5fab98b/007,14,76",
			"26,smallest/769cd811312cbbb82c87033a78ac9584ad282550bcb9cc3ae8c4e3da44c288c1a5b3954e01998c3c0654ee6774ceab66e9fe5b135750905c917d2b0bb5fab98b/007,14,76",
			"27,smallest/769cd811312cbbb82c87033a78ac9584ad282550bcb9cc3ae8c4e3da44c288c1a5b3954e01998c3c0654ee6774ceab66e9fe5b135750905c917d2b0bb5fab98b/009,14,76",
			"28,smallest/769cd811312cbbb82c87033a78ac9584ad282550bcb9cc3ae8c4e3da44c288c1a5b3954e01998c3c0654ee6774ceab66e9fe5b135750905c917d2b0bb5fab98b/009,14,76",
			"29,smallest/769cd811312cbbb82c87033a78ac9584ad282550bcb9cc3ae8c4e3da44c288c1a5b3954e01998c3c0654ee6774ceab66e9fe5b135750905c917d2b0bb5fab98b/009,14,76",
			"30,smallest/3b2376ab97bb5d1a5dbbf2b45cf062db320757549c761936d19df05e856de894e45695014cd8063cdc22148b13fa1803b3c9e77356931d66f4fbec0efacf7829/007,14,75",
			"31,smallest/3b2376ab97bb5d1a5dbbf2b45cf062db320757549c761936d19df05e856de894e45695014cd8063cdc22148b13fa1803b3c9e77356931d66f4fbec0efacf7829/007,14,75",
			"32,smallest/3b2376ab97bb5d1a5dbbf2b45cf062db320757549c761936d19df05e856de894e45695014cd8063cdc22148b13fa1803b3c9e77356931d66f4fbec0efacf7829/007,14,75",
			"33,smallest/3b2376ab97bb5d1a5dbbf2b45cf062db320757549c761936d19df05e856de894e45695014cd8063cdc22148b13fa1803b3c9e77356931d66f4fbec0efacf7829/007,14,75",
			"34,smallest/3b2376ab97bb5d1a5dbbf2b45cf062db320757549c761936d19df05e856de894e45695014cd8063cdc22148b13fa1803b3c9e77356931d66f4fbec0efacf7829/007,14,75",
			"35,smallest/c868b30a4adebf62b0ed20170a14ee9e5f8bc62d827e9712294ffa4a10ab8423e3d903c29e2392c83963972019a470e667c1987e2547294d1e2d1df1db832912/000,8,65",
			"36,grade/b1924d63a2e25b7c8d9a794093c4ae97fdceec9e0ea46b6a4b02d9a18b9ba9cecf07cb0c42c264a0947aec22b0bacff788a547a8250c2265f601581ab545bf82/001,2,74",
			"37,grade/b1924d63a2e25b7c8d9a794093c4ae97fdceec9e0ea46b6a4b02d9a18b9ba9cecf07cb0c42c264a0947aec22b0bacff788a547a8250c2265f601581ab545bf82/001,2,74",
			"38,grade/b1924d63a2e25b7c8d9a794093c4ae97fdceec9e0ea46b6a4b02d9a18b9ba9cecf07cb0c42c264a0947aec22b0bacff788a547a8250c2265f601581ab545bf82/001,2,74",
			"39,grade/b1924d63a2e25b7c8d9a794093c4ae97fdceec9e0ea46b6a4b02d9a18b9ba9cecf07cb0c42c264a0947aec22b0bacff788a547a8250c2265f601581ab545bf82/003,2,74",
			"40,grade/b1924d63a2e25b7c8d9a794093c4ae97fdceec9e0ea46b6a4b02d9a18b9ba9cecf07cb0c42c264a0947aec22b0bacff788a547a8250c2265f601581ab545bf82/003,2,74",
			"41,grade/b1924d63a2e25b7c8d9a794093c4ae97fdceec9e0ea46b6a4b02d9a18b9ba9cecf07cb0c42c264a0947aec22b0bacff788a547a8250c2265f601581ab545bf82/003,2,74",
			"42,syllables/ca5057661022789c0b40bc1574ab8b0826b3d22f70de1a10b2d2122137774c6aec73fe789a94b2362628481da623120033956bd376b41b825a72dbd8b50aff2f/003,21,77",
			"43,syllables/ca5057661022789c0b40bc1574ab8b0826b3d22f70de1a10b2d2122137774c6aec73fe789a94b2362628481da623120033956bd376b41b825a72dbd8b50aff2f/003,23,79",
			"44,syllables/f8d57deac89e46f99354a70e8f6bc830e0bded0c297d7a0765348de79d6071cb076d4e8f2cd60cff584cb220049d6065827a29904a7e1f9144f510f7773e6d0e/002,0,69",
			"45,digits/d5059e2b1493f91b32bb0c2c846d8461c50356f709a91792b6b625e112675de4edac2a09fa627d58c4651c662bbcf2c477660469b9327ed9427b43c25e4e070c/000,7,63",
			"46,digits/d5059e2b1493f91b32bb0c2c846d8461c50356f709a91792b6b625e112675de4edac2a09fa627d58c4651c662bbcf2c477660469b9327ed9427b43c25e4e070c/000,7,63",
			"47,digits/d5059e2b1493f91b32bb0c2c846d8461c50356f709a91792b6b625e112675de4edac2a09fa627d58c4651c662bbcf2c477660469b9327ed9427b43c25e4e070c/000,7,63",
			"48,digits/d5059e2b1493f91b32bb0c2c846d8461c50356f709a91792b6b625e112675de4edac2a09fa627d58c4651c662bbcf2c477660469b9327ed9427b43c25e4e070c/000,7,63",
			"49,digits/d5059e2b1493f91b32bb0c2c846d8461c50356f709a91792b6b625e112675de4edac2a09fa627d58c4651c662bbcf2c477660469b9327ed9427b43c25e4e070c/000,7,63",
			"50,digits/48b82975576f07f162163145b334648a73321d003a0a8cd4577172e48ce4836e63953dffd4460a9a7aadc511a695ff93de0ce2baf953e4b78b747440caa736a6/000,4,63",
			"51,digits/48b82975576f07f162163145b334648a73321d003a0a8cd4577172e48ce4836e63953dffd4460a9a7aadc511a695ff93de0ce2baf953e4b78b747440caa736a6/000,4,63",
			"52,digits/c5d8f924b86adfeafa7f520559aeb8bd0c3c178efe2500c4054c5ce51bcdbfc2da2e3d9fd5c73f559a7cb6c3b3555b04646111404744496cbcf31caa90e5beb4/003,7,65",
			"53,digits/c5d8f924b86adfeafa7f520559aeb8bd0c3c178efe2500c4054c5ce51bcdbfc2da2e3d9fd5c73f559a7cb6c3b3555b04646111404744496cbcf31caa90e5beb4/003,7,65",
			"54,digits/c5d8f924b86adfeafa7f520559aeb8bd0c3c178efe2500c4054c5ce51bcdbfc2da2e3d9fd5c73f559a7cb6c3b3555b04646111404744496cbcf31caa90e5beb4/003,7,65",
			"55,digits/c5d8f924b86adfeafa7f520559aeb8bd0c3c178efe2500c4054c5ce51bcdbfc2da2e3d9fd5c73f559a7cb6c3b3555b04646111404744496cbcf31caa90e5beb4/003,13,76",
			"56,digits/1b31fa5c50f7725ce468ebf24282f2d080a83aed87e4ee35522ae7710c8e0136bc263cc460b8ec7bf2c3519cb59af4a138e114d36541515b2609ab56ad2b8ee9/000,7,65",
			"57,digits/1b31fa5c50f7725ce468ebf24282f2d080a83aed87e4ee35522ae7710c8e0136bc263cc460b8ec7bf2c3519cb59af4a138e114d36541515b2609ab56ad2b8ee9/000,7,65",
			"58,digits/1b31fa5c50f7725ce468ebf24282f2d080a83aed87e4ee35522ae7710c8e0136bc263cc460b8ec7bf2c3519cb59af4a138e114d36541515b2609ab56ad2b8ee9/002,7,65",
			"59,digits/1b31fa5c50f7725ce468ebf24282f2d080a83aed87e4ee35522ae7710c8e0136bc263cc460b8ec7bf2c3519cb59af4a138e114d36541515b2609ab56ad2b8ee9/002,7,65",
			"60,digits/c9d718f379a877bd04e4544ee830a1c4c256bb4f104f214afd1ccaf81e7b25dea689895678bb1e6f817d8b0939eb175f8e847130f30a9a22e980d38125933516/001,13,81",
			"61,digits/5b504b3547416bfd54f138b3caa529ad72d157744b787e0e6f4745a2ff0fc5cc33bc87904b2d7cda9c7858087b2e04ece46d53fe9edd208f68d30a0ae70f363f/000,13,72",
			"62,digits/5b504b3547416bfd54f138b3caa529ad72d157744b787e0e6f4745a2ff0fc5cc33bc87904b2d7cda9c7858087b2e04ece46d53fe9edd208f68d30a0ae70f363f/000,13,72",
			"63,digits/e79f832ff314d3658c07e164d7866db707eafc8c97f209cf1d5cdb3b0cfc6738e7bd533c426a4887122d853ffa152b8861f30c41642a180152ba50c983de66dc/000,7,64",
			"64,digits/e79f832ff314d3658c07e164d7866db707eafc8c97f209cf1d5cdb3b0cfc6738e7bd533c426a4887122d853ffa152b8861f30c41642a180152ba50c983de66dc/000,7,64",
			"65,digits/e79f832ff314d3658c07e164d7866db707eafc8c97f209cf1d5cdb3b0cfc6738e7bd533c426a4887122d853ffa152b8861f30c41642a180152ba50c983de66dc/002,7,64",
			"66,digits/e79f832ff314d3658c07e164d7866db707eafc8c97f209cf1d5cdb3b0cfc6738e7bd533c426a4887122d853ffa152b8861f30c41642a180152ba50c983de66dc/002,7,64" };

	public void testrun(String path) throws Exception {
		String dependenciespath = new File("./examples/libs/junit-4.11.jar").getAbsolutePath() + File.pathSeparator
				+ new File("./examples/Math-0c1ef/lib/hamcrest-core-1.3.jar").getAbsolutePath();
		String projectId = "TestIntroClass003";
		File exampleLocation = new File("/Users/matias/develop/IntroClassJava/dataset/" + path);
		String location = exampleLocation.getAbsolutePath();
		String packageToInstrument = "";
		File filef = new File("src/test/resources/changes_analisis_frequency.json");
		String[] command = new String[] { "-mode", "jgenprog", "-dependencies", dependenciespath, "-location", location, //
				"-flthreshold", "0.1", "-package", packageToInstrument, "-id", projectId, "-population", "1", //
				"-seed", "10", //
				"-stopfirst", "true", "-maxgen", "1000", "-parameters",
				"autocompile:true" + ":disablelog:false:maxnumbersolutions:5"
						+ ":maxsolutionsperhole:1:sortholes:true:pathjsonfrequency:" + filef.getAbsolutePath()
						+ ":holeorder:" + UpdateParentDiffOrderFromJSON.class.getName() + ":customengine:"
						+ EvalTOSClusterApproach.class.getCanonicalName(), //
				"-loglevel", "ERROR"

		};

		AstorMain main1 = new AstorMain();
		main1.execute(command);
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
	}

	@Test
	public void testAll() throws Exception {
		for (String subject : subjects) {
			testRun(subject);
		}
	}

	@Test
	public void testAllwithlimit() throws Exception {
		int limit = subjects.length;
		limit = 5;
		for (int i = 0; i < limit; i++) {
			System.out.println("CASE " + i);
			String subject = subjects[i];
			testRun(subject);

		}
	}

	public void testRun(String parameters) throws Exception {
		System.out.println("Running " + parameters);
		String[] p = parameters.split(",");

		File exampleLocation = new File("/Users/matias/develop/IntroClassJava/dataset/" + p[1]);
		String location = exampleLocation.getAbsolutePath();
		String dependenciespath = new File("./examples/libs/junit-4.11.jar").getAbsolutePath() + File.pathSeparator
				+ new File("./examples/Math-0c1ef/lib/hamcrest-core-1.3.jar").getAbsolutePath();
		File filef = new File("src/test/resources/changes_analisis_frequency.json");
		assertTrue(filef.exists());
		String packageToInstrument = "";
		String[] command = new String[] { "-mode", "jgenprog", "-dependencies", dependenciespath, "-location", location, //
				"-flthreshold", "0.1", "-package", packageToInstrument, "-id", p[0], "-population", "1", //
				"-seed", "1", //
				"-stopfirst", "true", "-maxgen", "0", "-parameters",
				"autocompile:false" + ":disablelog:false:maxnumbersolutions:5"
						+ ":maxsolutionsperhole:1:sortholes:true:pathjsonfrequency:" + filef.getAbsolutePath()
						+ ":holeorder:" + UpdateParentDiffOrderFromJSON.class.getName() + ":customengine:"
						+ EvalTOSClusterApproach.class.getCanonicalName(), //
				"-loglevel", "ERROR"

		};

		AstorMain main = new AstorMain();
		main.execute(command);

		assertTrue(main.getEngine() instanceof EvalTOSClusterApproach);
		EvalTOSClusterApproach approach = (EvalTOSClusterApproach) main.getEngine();

		ModificationPoint mp42 = approach.getVariants().get(0).getModificationPoints().stream()
				.filter(e -> ((e.getCodeElement().getPosition().getLine() == Integer.valueOf(p[3])))).findFirst().get();

		System.out.println("Mp : \n" + mp42.getCodeElement().toString());

		mp42.getProgramVariant().getModificationPoints().clear();
		mp42.getProgramVariant().getModificationPoints().add(mp42);
		approach.MAX_GENERATIONS = 10000;
		approach.startEvolution();
		assertEquals(1, approach.getSolutions().size());
		assertEquals(AstorOutputStatus.STOP_BY_PATCH_FOUND, approach.getOutputStatus());
		approach.atEnd();
	}

}
