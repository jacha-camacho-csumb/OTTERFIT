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
 * ViewExercises Displays Exercises available for the logged-in user
 *
 * @author Christian Hoefer
 * @version 0.1.0
 * @since 4/20/2026
 */
public class ViewExercises {

    /**
     * Creates and return the ViewExercises Scene
     * @param stage Used to swap scenes after back button
     * @param db the database used to get the exercise data
     * @param username Currently logged-in user
     * @return return's the fully constructed ViewExercises Scene
     */
    public static Scene createScene(Stage stage, Database db, String username){

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f2f2f2;");

        //Title
        Label title = new Label("Exercises");
        title.setFont(new Font("Arial", 24));

        //ListView to display
        ListView<String> exerciseList = new ListView<>();
        exerciseList.setPrefSize(400, 400);

        //Pull from database
        try{
            List<String> exercises = db.getExercises(username);
            exerciseList.setItems(FXCollections.observableArrayList(exercises));
        } catch (Exception e) {
            exerciseList.setItems(FXCollections.observableArrayList("Error loading exercises"));
            e.printStackTrace();
        }

        //Back Button
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

        root.getChildren().addAll(title, exerciseList, backButton);

        return new Scene(root, 500, 600);
    }
}
