/*
 * Copyright (C) 2013 INRIA
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.inria.astor.core.faultlocalization.entity;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import fr.inria.astor.core.faultlocalization.entity.runtestsuite.ClassloaderFinder;
import fr.inria.astor.core.faultlocalization.entity.runtestsuite.Processor;
import fr.inria.astor.core.faultlocalization.entity.runtestsuite.TestFilter;



/**
 * @author Favio D. DeMarco
 * 
 */
public final class TestClassesFinder implements Callable<Collection<Class<?>>> {

	private final Logger logger = Logger.getLogger(this.getClass());

	public Collection<Class<?>> call() throws Exception {

		Class<?>[] classes = new Processor(
				new ClassloaderFinder((URLClassLoader) Thread.currentThread()
						.getContextClassLoader()), new TestFilter()).process();

		return java.util.Arrays.asList(classes);
	}

	protected String[] namesFrom(Collection<Class<?>> classes) {
		String[] names = new String[classes.size()];
		int index = 0;
		for (Class<?> aClass : classes) {
			names[index] = aClass.getName();
			index += 1;
		}
		return names;
	}

	public String[] findIn(ClassLoader dumpedToClassLoader,
			boolean acceptTestSuite) {
		ExecutorService executor = Executors
				.newSingleThreadExecutor(new CustomClassLoaderThreadFactory(
						dumpedToClassLoader));
		String[] testClasses;
		try {
			testClasses = namesFrom(executor.submit(new TestClassesFinder())
					.get());
		} catch (InterruptedException ie) {
			throw new RuntimeException(ie);
		} catch (ExecutionException ee) {
			throw new RuntimeException(ee);
		} finally {
			executor.shutdown();
		}

		if (!acceptTestSuite) {
			testClasses = removeTestSuite(testClasses);
		}

		if (this.logger.isDebugEnabled()) {
			//this.logger.debug("Test clases:");
			for (String testClass : testClasses) {
				//this.logger.debug(testClass);
			}
		}
		return testClasses;
	}

	public String[] findIn(final URL[] classpath, boolean acceptTestSuite) {
		return findIn(new URLClassLoader(classpath, Thread.currentThread().getContextClassLoader()), acceptTestSuite);
	}

	public String[] removeTestSuite(String[] totalTest) {
		List<String> tests = new ArrayList<String>();
		for (int i = 0; i < totalTest.length; i++) {
			if (!totalTest[i].endsWith("Suite")) {
				tests.add(totalTest[i]);
			}
		}
		return tests.toArray(new String[tests.size()]);
	}

}
