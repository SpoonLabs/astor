package fr.inria.astor.core.validation.validators;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.ProgramVariantValidationResult;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.setup.TransformationProperties;
import fr.inria.astor.core.validation.entity.TestResult;
import fr.inria.astor.core.validation.executors.ValidatorProcess;

public class ProcessValidator implements IProgramValidator {
	
	protected Logger log = Logger.getLogger(Thread.currentThread().getName());

	/**
	 * Process-based validation Advantage: stability, memory consumption, CG
	 * activity Disadvantage: time.
	 * 
	 * @param mutatedVariant
	 * @return
	 */
	@Override
	public ProgramVariantValidationResult validate(ProgramVariant mutatedVariant,ProjectRepairFacade projectFacade) {
		try {
			String bytecodeOutput = projectFacade.getOutDirWithPrefix(mutatedVariant.currentMutatorIdentifier());
			File variantOutputFile = new File(bytecodeOutput);
			URL[] bc = null;
			URL[] originalURL = projectFacade.getURLforMutation(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);

			if (mutatedVariant.getCompilation() != null) {
				MutationSupporter.currentSupporter.getSpoonClassCompiler().saveByteCode(
						mutatedVariant.getCompilation(), variantOutputFile);

				bc = redefineURL(variantOutputFile, originalURL);
			} else {
				bc = originalURL;
			}
			ValidatorProcess p = new ValidatorProcess();
			// First validation: failing test case
			String localPrefix = projectFacade.getProperties().getExperimentName() + File.separator
					+ projectFacade.getProperties().getFixid();

		
			
			TestResult trfailing = p.execute(bc, projectFacade.getProperties().getFailingTestCases(),
					TransformationProperties.validationSingleTimeLimit * 5);
		//	currentStat.passFailingval1++;
			if (trfailing == null) {
				log.debug("The validation 1 have not finished well");
			//	mutatedVariant.setFitness(Double.MAX_VALUE);
				return null;
			} else {
				// If it is successful, execute regression
				log.debug(trfailing);
				if (trfailing.wasSuccessful()) {
					//currentStat.passFailingval2++;
					if (TransformationProperties.allTestInOneProcess)
						return executeRegressionTesting(mutatedVariant, bc, p, localPrefix);
					else
						return executeRegressionTestingOneByOne(mutatedVariant, bc, p, localPrefix);

				} else {
				//	mutatedVariant.setFitness(trfailing.getFailures().size());
					//return trfailing.wasSuccessful();// false
					ProgramVariantValidationResult r = new ProgramVariantValidationResult(trfailing,false,false);
					return r;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	
	
	protected URL[] redefineURL(File foutgen, URL[] originalURL) throws MalformedURLException {
		List<URL> urls = new ArrayList<URL>();
		urls.add(foutgen.toURL());
		for (int i = 0; (originalURL != null) && i < originalURL.length; i++) {
			urls.add(originalURL[i]);
		}

		return (URL[]) urls.toArray(originalURL);
	}

	protected ProgramVariantValidationResult executeRegressionTesting(ProgramVariant mutatedVariant, URL[] bc, ValidatorProcess p,
			String localPrefix) {
		log.debug("Test Failing is passing, Executing regression");
		ProgramVariantValidator validator = new ProgramVariantValidator();
		TestResult trregression = p.execute(bc, validator.retrieveRegressionTestCases(),
				TransformationProperties.validationRegressionTimeLimit * 2);
		if (trregression == null) {
			return null;
		} else {
			log.debug(trregression);
			return new ProgramVariantValidationResult(trregression);
		}
	}

	protected ProgramVariantValidationResult executeRegressionTestingOneByOne(ProgramVariant mutatedVariant, URL[] bc,
			ValidatorProcess p, String localPrefix) {
		log.debug("Test Failing is passing, Executing regression");
		TestResult trregressionall = new TestResult();
		long t1 = System.currentTimeMillis();
		ProgramVariantValidator validator = new ProgramVariantValidator();
		for (String tc : validator.retrieveRegressionTestCases()) {
									
			List<String> parcial = new ArrayList<String>();
			parcial.add(tc);
			TestResult trregression = p.execute(bc, parcial, TransformationProperties.validationRegressionTimeLimit * 2);
			if (trregression == null) {
				log.debug("The validation 2 have not finished well");
				return null;
			} else {
				// return trregression.wasSuccessful();
				trregressionall.getFailures().addAll(trregression.getFailures());
				trregressionall.getSuccessTest().addAll(trregression.getSuccessTest());
				trregressionall.failures+=trregression.getFailures().size();
				trregressionall.casesExecuted+=trregression.getFailures().size()+trregression.getSuccessTest().size() ;
			}
		}
		long t2 = System.currentTimeMillis();
		//currentStat.time2Validation.add((t2 - t1));
		log.debug(trregressionall);
		return new ProgramVariantValidationResult(trregressionall, true, trregressionall.wasSuccessful());

	}

	List<String> regressionCases = null;

/*	*//**
	 * Feed the list of test cases according to the definition POM/build.xml
	 *//*
	public List<String> retrieveRegressionTestCases() {
		if (regressionCases == null) {
			regressionCases = new ArrayList<String>();
			for (CtSimpleType<?> type : FactoryImpl.getLauchingFactory().Type().getAll()) {
				String name = type.getQualifiedName();
				if ((name.endsWith("Test") || name.endsWith("TestBinary") || name.endsWith("TestPermutations"))
						&& (!name.endsWith("AbstractTest")) && !(type instanceof CtInterface)
						&& !(name.equals("junit.framework.TestSuite"))
						&& !(name.startsWith("org.apache.commons.math.ode.nonstiff"))
						) {
					regressionCases.add(type.getQualifiedName());
				}

			}
		}
		return regressionCases;
	}*/
	
}
