package ui.workout;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

/**
 * Handles user actions for logging and deleting workouts by displaying alerts.
 *
 * @author Nanorta Amwar
 * @version 0.1.0
 * @since 4/14/2026
 */

public class WorkoutController {
    /**
     * Handles the log workout action by showing a success alert
     * This method is triggered when the user logs a workout
     *
     * @return void
     */
    @FXML
    private void handleWorkout()() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Workout Logged");
        alert.setHeaderText(null);
        alert.setContentText("Workout logged successfully");
        alert.showAndWait();
    }

    /**
     * Handles the delete workout action by confirming deletion and showing a
     * sucess alert if the user proceeds.
     *
     * @return void
     */
    @FXML
    private void handleDeleteWorkout(){

        // Create confirmation alert before deleting workout
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setContentText("Are you sure you want to delete this workout?");

        // If user confirms, show success alert
        if (confirm.showAndWait().isPresent()) {
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Workout Deleted");
            success.setHeaderText(null);
            success.setContentText("Workout deleted successfully");
            success.showAndWait();
        }
    }
}
