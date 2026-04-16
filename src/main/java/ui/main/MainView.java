package ui.main;

import db.Database;
import factory.SceneFactory;
import factory.SceneType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * MainView Description: Main application view, dashboard/homescreen?
 *
 * @author rcwav
 * @author Jose Acha-Camacho
 * @version : 0.2.0
 * @since 4/12/2026
 */

public class MainView {

  public static Scene createScene(Stage stage, Database db, String username) {

    VBox root = new VBox(20);
    root.setAlignment(Pos.CENTER);
    root.setPadding(new Insets(40));
    root.setStyle("-fx-background-color: #f2f2f2;");

    // Title w/ dynamic username
    Label welcomeLabel = new Label("Welcome, " + username);
    welcomeLabel.setFont(new Font("Arial", 26));

    // Shared button style ( to match login theme?)
    String buttonStyle = """
            -fx-background-color: #4A90E2;
            -fx-text-fill: white;
            -fx-font-size: 16px;
            -fx-padding: 12;
            -fx-background-radius: 8;
        """;

    int buttonWidth = 300;
    int buttonHeight = 50;

    // Buttons
    Button logWorkoutBtn = new Button("Log Workout");
    logWorkoutBtn.setPrefSize(buttonWidth, buttonHeight);
    logWorkoutBtn.setStyle(buttonStyle);
    logWorkoutBtn.setOnAction(e ->
        System.out.println(username + " clicked Log Workout"));

    Button viewExercisesBtn = new Button("View Exercises");
    viewExercisesBtn.setPrefSize(buttonWidth, buttonHeight);
    viewExercisesBtn.setStyle(buttonStyle);
    viewExercisesBtn.setOnAction(e ->
        System.out.println(username + " clicked View Exercises"));

    Button addExerciseBtn = new Button("Add Exercise");
    addExerciseBtn.setPrefSize(buttonWidth, buttonHeight);
    addExerciseBtn.setStyle(buttonStyle);
    addExerciseBtn.setOnAction(e ->
        System.out.println(username + " clicked Add Exercise"));

    Button viewHistoryBtn = new Button("View History");
    viewHistoryBtn.setPrefSize(buttonWidth, buttonHeight);
    viewHistoryBtn.setStyle(buttonStyle);
    viewHistoryBtn.setOnAction(e -> {
      System.out.println(username + " clicked View History");
      stage.setScene(SceneFactory.create(SceneType.VIEW_HISTORY, stage, db, username));
    });

    root.getChildren().addAll(
        welcomeLabel,
        logWorkoutBtn,
        viewExercisesBtn,
        addExerciseBtn,
        viewHistoryBtn
    );

    return new Scene(root, 500, 700);
  }
}