package ui.workout;

import db.Database;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import ui.workout.components.ExerciseSelector;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField
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
        datePicker.SetPromptText("Workout Date");
        TextField durationField = new TextField();
        durationField.setPromptText("Duration (minutes)");
        TextField notesField = new TextField();
        notesField.setPromptText("Notes (optional)");
        notesArea.setPrefHeight(80);
    }
}
