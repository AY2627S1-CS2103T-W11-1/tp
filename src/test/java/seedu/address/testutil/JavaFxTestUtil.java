package seedu.address.testutil;

/**
 * Shares a JavaFX toolkit between UI test classes and forwards failures from its application thread.
 */
public final class JavaFxTestUtil {
    private JavaFxTestUtil() {}

    /**
     * Starts the toolkit once per test JVM. It remains available until the test process exits.
     */
    public static void startJavaFxToolkit() throws InterruptedException {
        JavaFxToolkit.start();
    }

    /**
     * Runs assertions on the JavaFX application thread, propagating errors and bounding the wait.
     */
    public static void runOnJavaFxThread(Runnable action) throws Exception {
        startJavaFxToolkit();
        JavaFxToolkit.call(() -> {
            action.run();
            return null;
        });
    }
}
