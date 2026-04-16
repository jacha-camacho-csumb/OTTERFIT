package ui.main;

import db.Database;
import factory.SceneFactory;
import factory.SceneType;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * CSUMB CS ONLINE PROGRAM - ViewHistory Displays workout history for the logged-in user
 *
 * @author Jose Acha-Camacho
 * @version 0.1.0
 * @since 4/15/26
 */
public class ViewHistory {

  public static Scene createScene(Stage stage, Database db, String username) {

    VBox root = new VBox(15);
    root.setAlignment(Pos.CENTER);
    root.setPadding(new Insets(30));
    root.setStyle("-fx-background-color: #f2f2f2;");

    // Title
    Label title = new Label("Workout History");
    title.setFont(new Font("Arial", 24));

    // ListView to display workouts
    ListView<String> historyList = new ListView<>();
    historyList.setPrefSize(400, 400);

    //Pull from database
    try {
      List<String> workouts = db.getWorkoutHistory(username); // YOU must implement this in Database
      historyList.setItems(FXCollections.observableArrayList(workouts));
    } catch (Exception e) {
      historyList.setItems(FXCollections.observableArrayList("Error loading workout history"));
      e.printStackTrace();
    }

    Button backButton = new Button("Back");
    backButton.setStyle("""
            -fx-background-color: #4A90E2;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-padding: 10;
            -fx-background-radius: 8;
        """);

    backButton.setOnAction(e ->
        stage.setScene(SceneFactory.create(SceneType.MAIN, stage, db, username))
    );

    root.getChildren().addAll(title, historyList, backButton);

    return new Scene(root, 500, 600);
  }
}