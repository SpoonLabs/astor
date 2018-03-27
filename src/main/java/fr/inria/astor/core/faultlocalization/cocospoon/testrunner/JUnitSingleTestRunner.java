package fr.inria.astor.core.faultlocalization.cocospoon.testrunner;

import java.util.concurrent.Callable;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

public class JUnitSingleTestRunner implements Callable<Result> {

    public JUnitSingleTestRunner(TestCase testCase, RunListener listener) {
        this.testCase = testCase;
        this.listener = listener;
    }

    @Override
    public Result call() throws Exception {
        JUnitCore runner = new JUnitCore();
        runner.addListener(listener);
        Request request = Request.method(testClassFromCustomClassLoader(), testCaseName());
        try {
            return runner.run(request);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?> testClassFromCustomClassLoader() {
        Class<?> compiledClass;
        try {
            compiledClass = Thread.currentThread().getContextClassLoader().loadClass(testClassName());
        } catch (ClassNotFoundException cnfe) {
            throw new RuntimeException(cnfe);
        }
        return compiledClass;
    }

    public String testCaseName() {
        return testCase.testName();
    }

    public String testClassName() {
        return testCase.className();
    }

    private TestCase testCase;
    private RunListener listener;
}
