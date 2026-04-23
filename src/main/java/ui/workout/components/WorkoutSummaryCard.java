package ui.workout.components;

import db.Database.WorkoutLog;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;


/**
 * Reusable component that displays a workout summary.
 * Uses data binding via a property.
 *
 * @author Nanorta Amwar
 * @version 0.1.0
 * @since 4/22/2026
 */

public class WorkoutSummaryCard {
    private final Label exerciseLabel;
    private final Label dateLabel;
    private final Label durationLabel;
    private final Label notesLabel;
    private final ObjectProperty<WorkoutLog> workout = new SimpleObjectProperty<>();

    public WorkoutSummaryCard() {
        this.setSpacing(5);
        this.setStyle("-fx-border-color: #2196F3; -fx-border-radius: 8; -fx-padding: 10; -fx-background-color: #f5f5f5;");

        Label header = new Label("Workout Details");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        exerciseLabel = new Label("Exercise: -");
        dateLabel = new Label("Date: -");
        durationLabel = new Label("Duration: -");
        notesLabel = new Label("Notes: -");
        notesLabel.setWrapText(true);

        this.getChildren().addAll(header, exerciseLabel, dateLabel, durationLabel, notesLabel);

        // Bind the labels to the workout property
        workout.addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                exerciseLabel.setText("Exercise: " + newVal.getExerciseName());
                dateLabel.setText("Date: " + newVal.getWorkoutDate());
                durationLabel.setText("Duration: " + newVal.getDurationMinutes() + " min");
                notesLabel.setText("Notes: " + (newVal.getNotes() == null ? "â€”" : newVal.getNotes()));
            } else {
                exerciseLabel.setText("Exercise: -");
                dateLabel.setText("Date: -");
                durationLabel.setText("Duration: -");
                notesLabel.setText("Notes: -");
            }
        });
    }

    public void setWorkout(WorkoutLog workout) {
        this.workout.set(workout);
    }

    public WorkoutLog getWorkout() {
        return workout.get();
    }

    public ObjectProperty<WorkoutLog> workoutProperty() {
        return workout;
    }

