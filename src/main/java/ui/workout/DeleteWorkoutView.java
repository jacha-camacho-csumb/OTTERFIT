package ui.workout;


/**
 * Deletes workout scene redesigned to match the original mockup
 * Shows a confirmation messagem a card with workout details, and Confurm/Cancel Buttons
 *
 * @author Nanorta Amwar
 * @version 0.1.0
 * @since 4/22/2026
 */

public class DeleteWorkoutView {
    public static Scene createScene(Stage stage, Database db, int userId, String username) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f2f2f2;");

        //Title
        Label title = new Label("Delete Workout");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Dropdown to select a workout
        ComboBox<WorkoutLog> workoutCombo = new ComboBox<>();
        workoutCombo.setPromptText("Select a workout");
        workoutCombo.setMaxWidth(300);
        workoutCombo.setStyle("-fx-font-size: 14px;");

    }
