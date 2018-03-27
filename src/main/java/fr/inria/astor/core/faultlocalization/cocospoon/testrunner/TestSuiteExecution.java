package fr.inria.astor.core.faultlocalization.cocospoon.testrunner;


import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import fr.inria.astor.core.faultlocalization.entity.CustomClassLoaderThreadFactory;
import fr.inria.astor.core.setup.ConfigurationProperties;


public class TestSuiteExecution {

    public static Result runCasesIn(String[] testClasses, ClassLoader classLoaderForTestThread, RunListener listener) {
        return executionResult(new JUnitRunner(testClasses, listener), classLoaderForTestThread);
    }


    public static Result runTestCase(TestCase testCase, ClassLoader classLoaderForTestThread, RunListener listener) {
        return executionResult(new JUnitSingleTestRunner(testCase, listener), classLoaderForTestThread);
    }

    public static Result runTest(String test, ClassLoader classLoaderForTestThread, RunListener listener) {
        return executionResult(new JUnitSingleTestResultRunner(test, listener), classLoaderForTestThread);
    }

    public static Result runTestCase(TestResult testCase, ClassLoader classLoaderForTestThread, RunListener listener) {
        return executionResult(new JUnitSingleTestResultRunner(testCase.getTestCase().toString(), listener), classLoaderForTestThread);
    }

  

    private static Result executionResult(Callable<Result> callable, ClassLoader classLoaderForTestThread) {
        ExecutorService executor = Executors.newSingleThreadExecutor(new CustomClassLoaderThreadFactory(classLoaderForTestThread));
        Result result = null;

        Future<Result> future = executor.submit(callable);
        try {
            executor.shutdown();
            int maxregressiontimeseconds = ConfigurationProperties.getPropertyInt("tmax2");
            result = future.get(maxregressiontimeseconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
           // logDebug(logger(), String.format("Timeout after %d seconds. Infinite loop?", nopolContext.getTimeoutTestExecution()));
            throw new RuntimeException(e);
        } finally {
            executor.shutdownNow();
        }
        return result;
    }

    public static List<Description> collectDescription(List<Failure> failures) {
        List<Description> descriptions = new LinkedList<>();
        for (Failure failure : failures) {
            descriptions.add(failure.getDescription());
        }
        return descriptions;
    }


}