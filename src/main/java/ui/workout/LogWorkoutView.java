package ui.workout;

import db.Database;
import db.Database.Exercise;
import factory.SceneFactory;
import factory.SceneType;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import ui.workout.components.ExerciseSelector;
import ui.workout.notifications.NotificationManager;

import java.sql.SQLException;

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

        // Load exercises from DB
        try {
            var exercises = db.getAllExercises();

            exerciseSelector.setExercises(FXCollections.observableArrayList(exercises));
        } catch (SQLException e) {
            NotificationManager.getInstance().showErrorAlert("Database", "Could not load exercises.");
        }

        // save workout action
        Button saveBtn = new Button("Save");
        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 8;");
        saveBtn.setOnAction(e -> {
            Exercise selected = exerciseSelector.getSelectedExercise();
            if (selected == null) {
                NotificationManager.getInstance().showWarningAlert("Missing", "Please select an exercise.");
                return;
            }
            if (datePicker.getValue() == null) {
                NotificationManager.getInstance().showWarningAlert("Missing", "Please select a workout date.");
                return;
            }
            double dur;
            try {
                dur = Double.parseDouble(durationField.getText());
            } catch (NumberFormatException ex) {
                NotificationManager.getInstance().showWarningAlert("Invalid", "Duration must be a number.");
                return;
            }
            try {
                db.createWorkout(
                        userId,
                        selected.getId(),
                        datePicker.getValue().toString(),
                        notes.get(),
                        dur
                );
                NotificationManager.getInstance().showDesktopNotification(
                        "Workout Logged",
                        String.format("%s - %s, %.0f min",
                                datePicker.getValue(), selected.getName(), dur)
                );
                // clear form for the next entry
                exerciseSelector.clearSelection();
                datePicker.setValue(null);
                durationField.clear();
                notesArea.clear();
            } catch (SQLException ex) {
                NotificationManager.getInstance().showErrorAlert("Save Failed", ex.getMessage());
            }
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: #d3d3d3; -fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 8;");
        cancelBtn.setOnAction(e ->
                stage.setScene(SceneFactory.create(SceneType.MAIN, stage, db, username))
        );

        root.getChildren().addAll(title, exerciseSelector, datePicker, durationField, notesArea, saveBtn, cancelBtn);
        return new Scene(root, 500, 550);
    }
}