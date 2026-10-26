package seedu.address.testutil;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

/**
 * Starts the JavaFX toolkit once for the whole test run, so that every UI test class can use it.
 * The toolkit can only be started once per JVM and cannot be restarted after {@code Platform.exit()},
 * so test classes must share it and never shut it down.
 */
public class JavaFxToolkit {

    private static final int TIMEOUT_SECONDS = 5;

    private static boolean started;

    private JavaFxToolkit() {}

    /** Starts the toolkit if no earlier test class has done so. */
    public static synchronized void start() throws InterruptedException {
        if (started) {
            return;
        }
        if (System.getProperty("os.name").startsWith("Linux")) {
            String display = System.getenv("DISPLAY");
            assertNotNull(display, "JavaFX tests on Linux require an X display");
            assertFalse(display.isBlank(), "JavaFX tests on Linux require an X display");
        }

        CountDownLatch startupLatch = new CountDownLatch(1);
        Platform.startup(startupLatch::countDown);
        // Otherwise JavaFX shuts down when a test closes the last window it showed, and later tests time out.
        Platform.setImplicitExit(false);
        assertTrue(startupLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), "JavaFX toolkit did not start");
        started = true;
    }

    /** Runs {@code action} on the JavaFX application thread and returns its result. */
    public static <T> T call(Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        try {
            return task.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
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
