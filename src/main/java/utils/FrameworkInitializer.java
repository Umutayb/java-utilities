package utils;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.util.List;

public class FrameworkInitializer extends ParentRunner<ParentRunner<?>> {

    protected FrameworkInitializer(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected List<ParentRunner<?>> getChildren() {
        return null;
    }

    @Override
    protected Description describeChild(ParentRunner<?> parentRunner) {
        return null;
    }

    @Override
    protected void runChild(ParentRunner<?> parentRunner, RunNotifier runNotifier) {

    }
}
