package ui.main;

import db.Database;
import factory.SceneFactory;
import factory.SceneType;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * MainView Description: Main application view, dashboard/homescreen?
 *
 * @author rcwav
 * @author Jose Acha-Camacho
 * @version : 0.4.0
 * @since 4/19/2026
 */
public class MainView {

  public static Scene createScene(Stage stage, Database db, String username) {

    BorderPane root = new BorderPane();
    root.setStyle("-fx-background-color: #f2f2f2;");

    // Title w/ dynamic username
    Label welcomeLabel = new Label("Welcome, " + username);
    welcomeLabel.setFont(new Font("Arial", 26));

    // Shared button style
    String buttonStyle = """
            -fx-background-color: #4A90E2;
            -fx-text-fill: white;
            -fx-font-size: 16px;
            -fx-padding: 12;
            -fx-background-radius: 8;
        """;

    // Alternate style for sign out
    String signOutStyle = """
            -fx-background-color: #d9534f;
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
    viewExercisesBtn.setOnAction(e ->{
        System.out.println(username + " clicked View Exercises");
        stage.setScene(SceneFactory.create(SceneType.VIEW_EXERCISES, stage, db, username));
    });

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

    Button signOutBtn = new Button("Sign Out");
    signOutBtn.setPrefSize(buttonWidth, buttonHeight);
    signOutBtn.setStyle(signOutStyle);
    signOutBtn.setOnAction(e -> {
      System.out.println(username + " signed out");
      stage.setScene(SceneFactory.create(SceneType.LOGIN, stage, db, SceneType.MAIN));
    });

    /**
     * Make space for the weather widget
     */
    Label weatherLabel = new Label("⛅ 65°F");
    weatherLabel.setStyle("""
            -fx-background-color: white;
            -fx-padding: 4 8 4 8;
            -fx-background-radius: 8;
            """);

    HBox weatherBox = new HBox(weatherLabel);
    weatherBox.setAlignment(Pos.TOP_RIGHT);
    weatherBox.setPadding(new Insets(10));
    VBox content = new VBox(20);
    content.setAlignment(Pos.CENTER);
    content.setPadding(new Insets(40));

    content.getChildren().addAll(
        welcomeLabel,
        logWorkoutBtn,
        viewExercisesBtn,
        addExerciseBtn,
        viewHistoryBtn,
        signOutBtn
    );

    root.setCenter(content);
    root.setTop(weatherBox);

    /**
     * Async task to load the weather label
     */
    Task<String> weatherTask = new Task<>() {
      @Override
      protected String call() {
        return model.Weather.getCurrentWeather();
      }
    };
    weatherTask.setOnSucceeded(e-> {
      weatherLabel.setText(weatherTask.getValue());
    });
    weatherTask.setOnFailed(e-> {
      weatherLabel.setText("NA");
    });
    new Thread(weatherTask).start();

    /**
     * Seperate Asyn task to get weather comments
     * to display on hover of weather
     */
    Task<String> weatherCommentTask = new Task<>() {
      @Override
      protected String call() {
        return model.Weather.getWeatherComments();
      }
    };
    weatherCommentTask.setOnSucceeded(e-> {
      String comments = weatherCommentTask.getValue();

      // Tooltip
      Tooltip tooltip = new Tooltip(comments);
      tooltip.setWrapText(true);
      tooltip.setMaxWidth(320);

      // timing tweaks
      tooltip.setShowDelay(javafx.util.Duration.millis(250));
      tooltip.setHideDelay(javafx.util.Duration.seconds(6)); // stays longer

      // styling
      tooltip.setStyle(
              "-fx-background-color: #2b2b2b;" +
                      "-fx-text-fill: white;" +
                      "-fx-padding: 12;" +
                      "-fx-background-radius: 10;" +
                      "-fx-font-size: 13px;"
      );

      weatherLabel.setTooltip(tooltip);

      // apply highlight + ✨ fade if valid
      if (comments != null &&
              !comments.isBlank() &&
              !comments.equals("Weather unavailable")) {

        weatherLabel.setStyle(
                "-fx-background-color: #e6f4ea;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 4 8 4 8;"
        );

        // fade-in effect
        weatherLabel.setOpacity(0.0);

        javafx.animation.FadeTransition ft =
                new javafx.animation.FadeTransition(
                        javafx.util.Duration.millis(400),
                        weatherLabel
                );

        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
      }
    });
    weatherCommentTask.setOnFailed(e-> {
      weatherLabel.setTooltip(new Tooltip("Have a nice day!"));
    });
    new Thread(weatherCommentTask).start();

    return new Scene(root, 500, 700);
  }
}