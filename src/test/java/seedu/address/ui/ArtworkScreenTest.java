package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

public class ArtworkScreenTest {

    @Test
    public void runOnce_calledSeveralTimes_runsActionOnlyOnce() {
        AtomicInteger runCount = new AtomicInteger(0);
        Runnable runOnce = ArtworkScreen.runOnce(runCount::incrementAndGet);

        runOnce.run();
        runOnce.run();
        runOnce.run();

        assertEquals(1, runCount.get());
    }

    @Test
    public void runOnce_neverCalled_doesNotRunAction() {
        AtomicInteger runCount = new AtomicInteger(0);
        ArtworkScreen.runOnce(runCount::incrementAndGet);

        assertEquals(0, runCount.get());
    }
}
