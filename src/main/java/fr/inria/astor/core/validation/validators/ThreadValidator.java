package fr.inria.astor.core.validation.validators;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.ProgramVariantValidationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.validation.executors.ValidationDualModeThread;

public class ThreadValidator implements IProgramValidator {
	
	protected Logger log = Logger.getLogger(Thread.currentThread().getName());
	
	
	
	/**
	 * Thread-based validation. Advantage: execution time Disadvantage: use of
	 * memory, many classes and instances loaded in the heap/permMem of the
	 * application.
	 * 
	 * @param mutatedVariant
	 * @return
	 */
	
	@Override
	public ProgramVariantValidationResult validate(ProgramVariant mutatedVariant,ProjectRepairFacade projectFacade) {

		try {

			ValidationDualModeThread thread = new ValidationDualModeThread(projectFacade, 
					 mutatedVariant, true);

			// First validation
			synchronized (thread) {
				try {
					log.debug("Waiting for validation...");
					thread.start();
					thread.wait( ConfigurationProperties.getPropertyInt("tmax1"));

				} catch (InterruptedException e) {
					log.debug("stop validation thread");
					e.printStackTrace();
				}
			}
			boolean interrumped1 = !thread.finish;
			thread.stop();
			/*
			 * if (interrumped1)
			 * currentStat.time1Validation.add(Long.MAX_VALUE);
			 */
			log.debug("Thread 1 live" + thread.isAlive() + ", interrumped " + interrumped1);
			// If the first validation is ok, we call the regression
			if (thread.sucessfull) {
				ValidationDualModeThread threadRegression = 
						new ValidationDualModeThread(projectFacade,	mutatedVariant, false);
				synchronized (threadRegression) {
					try {
						log.debug("First validation ok, start with the second one");
						threadRegression.modeFirst = false;
						threadRegression.start();
						log.debug("Thread 2 id " + threadRegression.getId() + " " + threadRegression.getName());
						threadRegression.wait(ConfigurationProperties.getPropertyInt("tmax2"));
					} catch (InterruptedException e) {
						log.debug("stop validation thread");
						e.printStackTrace();
					}
					boolean interrumped2 = !threadRegression.finish;
					if (interrumped2){
						//currentStat.time2Validation.add(Long.MAX_VALUE);
					}
					threadRegression.stop();
					log.debug("Thread 2 " + threadRegression.isAlive() + ", interrumped " + interrumped2);
				}

				return threadRegression.result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
