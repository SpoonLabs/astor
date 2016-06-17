package fr.inria.astor.core.setup;

import java.util.Random;

/**
 * 
 * @author Claire Le Goues (CLG), clegoues@cs.cmu.edu
 * 
 * RandomManager is a static provider of randomness to all interested classes.
 * This allows the experiment to specify a random seed at configuration time
 * that can then be used to reproduce particular runs.
 * 
 * Note that this feels a bit heavyweight to CLG, as she was trying to just
 * replace the calls to new Random() in place, but that solution appeared a bit
 * scattered.
 * 
 * If accepted as a solution to the reproducible randomness problem, the project
 * moving forward should stringently disallow calls to new java.util.Random, and
 * all randomness requirements should be mediated through this class.
 * 
 */
public class RandomManager {

    private static Random randomNumberGenerator = null;

    public static void initialize() {
        if(ConfigurationProperties.hasProperty("seed")) {
            Integer seed = ConfigurationProperties.getPropertyInt("seed");
            randomNumberGenerator = new Random(seed);
        } else {
        	// by default Astor is deterministic
        	ConfigurationProperties.properties.setProperty("seed","0");
            randomNumberGenerator = new Random(0);
        }
    }

    public static Integer nextInt(int bound) {
        return randomNumberGenerator.nextInt(bound);
    }
    public static Double nextDouble() {
        return randomNumberGenerator.nextDouble();
    }
    
}
