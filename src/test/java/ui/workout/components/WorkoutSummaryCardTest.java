package ui.workout.components;

import db.Database;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that WorkoutSummaryCard correctly stores and returns a workout object.
 *
 * @author Nanorta Amwar
 * @version 0.1.0
 * @since 4/22/2026
 */

public class WorkoutSummaryCardTest {

    // Starts JavaFX once before running tests
    @BeforeAll
    static void startJavaFx() throws Exception {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // JavaFX already started
        }
    }

    // Verifies that setWorkout() properly stores the workout in the card
    @Test
    void setWorkoutStoresWorkoutCorrectly() throws Exception {
        Database.WorkoutLog workout = new Database.WorkoutLog();
        workout.setWorkoutId(1);
        workout.setExerciseName("Push-Up");

        AtomicReference<WorkoutSummaryCard> cardRef = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            WorkoutSummaryCard card = new WorkoutSummaryCard();
            card.setWorkout(workout);
            cardRef.set(card);
            latch.countDown();
        });

        latch.await();

        assertSame(workout, cardRef.get().getWorkout());
    }
}
