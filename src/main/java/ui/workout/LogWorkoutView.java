package ui.workout;

import db.Database;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import ui.workout.components.ExerciseSelector;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;

/**
 * Log Workout scene.
 * Allows user to select an exercise, enter date, duration, notes, and save
 * After saving, the form is cleared so another workout can be logged.
 *
 * @author Nanorta Amwar
 * @version 0.1.0
 * @since 4/21/2026
 */

public class LogWorkoutView {
    public static Scene createScene(Stage stage, Database db, int userId, String username) {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f2f2f2;");

        Label title = new Label("Log Workout");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Custom component used
        ExerciseSelector exerciseSelector = new ExerciseSelector();

        // Form fields with data binding
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Workout Date");
        TextField durationField = new TextField();
        durationField.setPromptText("Duration (minutes)");
        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Notes (optional)");
        notesArea.setPrefHeight(80);

        // Bindings
        SimpleStringProperty workoutDate = new SimpleStringProperty();
        SimpleDoubleProperty duration = new SimpleDoubleProperty();
        SimpleStringProperty notes = new SimpleStringProperty();

        datePicker.valueProperty().addListener((obs, old, newVal) ->
                workoutDate.set(newVal == null ? "" : newVal.toString()));

        durationField.textProperty().addListener((obs, old, newVal) -> {
            try {
                duration.set(Double.parseDouble(newVal));
            } catch (NumberFormatException e) {
                duration.set(0.0);
            }
        });
        notesArea.textProperty().bindBidirectional(notes);
    }
}
