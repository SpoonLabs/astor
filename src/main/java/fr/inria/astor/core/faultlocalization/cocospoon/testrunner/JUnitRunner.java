package fr.inria.astor.core.faultlocalization.cocospoon.testrunner;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import fr.inria.astor.core.faultlocalization.cocospoon.code.ClassLibrary;

public final class JUnitRunner implements Callable<Result> {

    public JUnitRunner(final String[] classes, RunListener listener) {
        this.testClasses = checkNotNull(classes);
        this.listener = listener;
    }

    @Override
    public Result call() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
        JUnitCore runner = new JUnitCore();
        runner.addListener(listener);
        Class<?>[] testClasses = testClassesFromCustomClassLoader();
        try {
            return runner.run(testClasses);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?>[] testClassesFromCustomClassLoader() {
        Collection<Class<?>> classes = new LinkedList<>();
        for (String className : testClasses) {
            try {
                Class<?> testClass = Thread.currentThread().getContextClassLoader().loadClass(className);
                if (!ClassLibrary.isAbstract(testClass)) {
                    classes.add(testClass);
                }
            } catch (ClassNotFoundException cnfe) {
                throw new RuntimeException(cnfe);
            }
        }
        return classes.toArray(new Class<?>[classes.size()]);
    }

    private final String[] testClasses;
    private final RunListener listener;
}
