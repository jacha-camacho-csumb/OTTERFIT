/**
 * Provides a reusable dropdown component for selecting an exercise.
 * Uses data binding with ObservableList
 *
 * @author Nanorta Amwar
 * @version 0.1.0
 * @since 4/21/2026
 */

package ui.workout.components;

import db.Database.Exercise;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

// initialize layout and UI components
public class ExerciseSelector extends VBox {
    // declare combo box and list
    private final ComboBox<Exercise> comboBox;
    private final ObservableList<Exercise> exercises;

    public ExerciseSelector(){
        this.setSpacing(8);
        this.setStyle("-fx-padding: 10; -fx-border-color: #cc; -fx-border-radius: 5;");

        Label label = new Label("Select Exercise:");
        label.setStyle("-fx-font-weight: bold;");

        // Convert Exercise objects to display their names on the dropdown
        comboBox = new ComboBox<>();
        comboBox.setPromptText("Choose an exercise");
        comboBox.setMaxWidth(Double.MAX_VALUE);

        exercises = FXCollections.observableArrayList();
        comboBox.setItems(exercises);
        comboBox.setConverter(new javafx.util.StringConverter<>
                () {
            @Override public String toString(Exercise ex)
            { return ex == null ? "" : ex.getName(); }
            @Override public Exercise fromString(String s)
            { return null; }
        });
        this.getChildren().addAll(label, comboBox);
    }
    // updates the list of exercises in the dropdown
    public void setExercises(ObservableList<Exercise> list) {
        exercises.setAll(list);
    }
    // returns the currently selected exercise
    public Exercise getSelectedExercise() {
        return comboBox.getValue();
    }
    // clears the selected exercise
    public void clearSelection(){
        comboBox.setValue(null);
    }
}
