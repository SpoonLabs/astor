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
package fr.inria.astor.core.loop.evolutionary;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ComputationException;
/*import javax.annotation.Nonnull;
import javax.annotation.Nullable;*/

/**
 * @author Favio D. DeMarco
 * 
 */
public final class JUnitRunner implements Callable<Result> {

	private enum StringToClass implements Function<String, Class<?>> {
		INSTANCE;

		@Override
		//@Nullable
		public Class<?> apply(/*@Nullable*/ final String input) {
			try {
				return Thread.currentThread().getContextClassLoader().loadClass(input);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				throw new ComputationException(e);
			}
		}
	}

	private final String[] classes;

	private final List<RunListener> listeners = new ArrayList<>();

	/**
	 * @param classes
	 */
	public JUnitRunner(/*@Nonnull*/ final String[] classes) {
		this.classes = checkNotNull(classes);
	}

	/**
	 * @param classes
	 * @param listener
	 */
	public JUnitRunner(/*@Nonnull*/ final String[] classes, /*@Nonnull */final RunListener listener) {
		this.listeners.add(checkNotNull(listener));
		this.classes = checkNotNull(classes);
	}

	/**
	 * @param classes
	 * @param listeners
	 */
	public JUnitRunner(/*@Nonnull*/ final String[] classes, final RunListener... listeners) {
		this.classes = checkNotNull(classes);
		this.listeners.addAll(asList(listeners));
	}

	@Override
	public Result call() throws Exception {
		JUnitCore runner = new JUnitCore();
		for (RunListener listener : this.listeners) {
			runner.addListener(listener);
		}
		System.err.println("Thread id "+Thread.currentThread().getId()+ " "+Thread.currentThread().getName());
		Class<?>[] testClasses = Collections2.transform(asList(this.classes), StringToClass.INSTANCE).toArray(
				new Class<?>[this.classes.length]);

		return runner.run(testClasses);
	}
}
