package seedu.address.testutil;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

/**
 * Shares a JavaFX toolkit between UI test classes and forwards failures from its application thread.
 */
public final class JavaFxTestUtil {
    private static final int TIMEOUT_SECONDS = 5;
    private static boolean isStarted;

    private JavaFxTestUtil() {}

    /**
     * Starts the toolkit once per test JVM. It remains available until the test process exits.
     */
    public static synchronized void startJavaFxToolkit() throws InterruptedException {
        if (isStarted) {
            return;
        }
        CountDownLatch startupLatch = new CountDownLatch(1);
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            startupLatch.countDown();
        });
        assertTrue(startupLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), "JavaFX toolkit did not start");
        isStarted = true;
    }

    /**
     * Runs assertions on the JavaFX application thread, propagating errors and bounding the wait.
     */
    public static void runOnJavaFxThread(Runnable action) throws Exception {
        startJavaFxToolkit();
        FutureTask<Void> task = new FutureTask<>(() -> {
            action.run();
            return null;
        });
        Platform.runLater(task);
        try {
            task.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (ExecutionException exception) {
            if (exception.getCause() instanceof AssertionError assertionError) {
                throw assertionError;
            }
            if (exception.getCause() instanceof Exception cause) {
                throw cause;
            }
            throw new RuntimeException(exception.getCause());
        }
    }
}
