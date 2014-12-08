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
package fr.inria.astor.core.loop.mutation.muttest;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sacha.finder.classes.impl.ClassloaderFinder;
import sacha.finder.filters.impl.TestFilter;
import sacha.finder.processor.Processor;

import com.google.common.collect.Collections2;



/**
 * @author Favio D. DeMarco
 * 
 */
public final class TestClassesFinder implements Callable<String[]> {

	//private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public String[] call() throws Exception {
	
		Class<?>[] classes = new Processor(new ClassloaderFinder((URLClassLoader) Thread.currentThread()	.getContextClassLoader())
				, new TestFilter()).process();

		return Collections2.transform(Arrays.asList(classes), ClassName.INSTANCE).toArray(new String[classes.length]);
	}

	public String[] findIn(final URL[] classpath, boolean acceptTestSuite) {

	
		
	//	urlcl = new URLClassLoader(classpath);
		ExecutorService executor = Executors.newSingleThreadExecutor(new ProvidedClassLoaderThreadFactory(
		/*	urlcl	*/new URLClassLoader(classpath)));

		String[] testClasses;
		try {
			testClasses = executor.submit(new TestClassesFinder()).get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} finally {
			executor.shutdown();
		}
		
		if ( !acceptTestSuite ){
			testClasses = removeTestSuite(testClasses);
		}
		

		return testClasses;
	}
	
	public String[] removeTestSuite(String[] totalTest){
		List<String> tests = new ArrayList<>();
		for ( int i = 0 ; i < totalTest.length ; i++ ){
			if ( !totalTest[i].endsWith("Suite") ){
				tests.add(totalTest[i]);
			}
		}
		return tests.toArray(new String[tests.size()]);	
	}
}
