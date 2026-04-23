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

        // Confirmation question
        Label questionLabel = new Label("Are you sure you want to delete this workout?");
        questionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: normal;");

        // Card that displays workout details (matching the mockup)
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 8; -fx-background-color: white; " +
                "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setMaxWidth(350);

        Label workoutNameLabel = new Label();
        workoutNameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label workoutDateLabel = new Label();
        workoutDateLabel.setStyle("-fx-font-size: 14px;");
        Label workoutDurationLabel = new Label();
        workoutDurationLabel.setStyle("-fx-font-size: 14px;");

        card.getChildren().addAll(workoutNameLabel, workoutDateLabel, workoutDurationLabel);
        // Load workouts into combo box
        ObservableList<WorkoutLog> workouts = FXCollections.observableArrayList();
        try {
            List<WorkoutLog> logs = db.getWorkoutsByUser(userId);
            workouts.setAll(logs);
            workoutCombo.setItems(workouts);
            // Display format: "Upper Body Power - Oct 24, 2023" (example)
            workoutCombo.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(WorkoutLog log, boolean empty) {
                    super.updateItem(log, empty);
                    setText(empty || log == null ? null :
                            log.getExerciseName() + " - " + log.getWorkoutDate());
                }
            });
            workoutCombo.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(WorkoutLog log, boolean empty) {
                    super.updateItem(log, empty);
                    setText(empty || log == null ? null :
                            log.getExerciseName() + " - " + log.getWorkoutDate());
                }
            });
        } catch (SQLException e) {
            NotificationManager.getInstance().showErrorAlert("Database", "Failed to load workouts.");
        }

        // Update the card when a workout is selected
        workoutCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                workoutNameLabel.setText(newVal.getExerciseName());
                workoutDateLabel.setText(newVal.getWorkoutDate());
                workoutDurationLabel.setText(String.format("%.0f Minutes", newVal.getDurationMinutes()));
            } else {
                workoutNameLabel.setText("");
                workoutDateLabel.setText("");
                workoutDurationLabel.setText("");
            }
        });

    }
