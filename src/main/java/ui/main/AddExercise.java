package ui.main;

import db.Database;
import factory.SceneFactory;
import factory.SceneType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * [Brief one sentence description of what this class does.]
 *
 * @author Christian Hoefer
 * @version 0.1.0
 * @since 4/20/2026
 */
public class AddExercise {

    public static final String nameField = "nameField";
    public static final String categoryField = "categoryField";
    public static final String descriptionArea = "descriptionArea";
    public static final String messageField = "messageField";

    public static Scene createScene(Stage stage, Database db, String username){

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f2f2f2;");

        //Title
        Label title = new Label("Add Exercise");
        title.setFont(new Font("Arial", 26));

        //Name
        TextField nameField = new TextField();
        nameField.setPromptText("Exercise name (required)");
        nameField.setMaxWidth(300);
        nameField.setId(AddExercise.nameField);

        //Category
        TextField categoryField = new TextField();
        categoryField.setPromptText("Category e.g. Strength, Cardio");
        categoryField.setMaxWidth(300);
        categoryField.setId(AddExercise.categoryField);

        //Description
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description (optional)");
        descriptionArea.setMaxWidth(300);
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setWrapText(true);
        descriptionArea.setId(AddExercise.descriptionArea);

        Label message = new Label();
        message.setId(AddExercise.messageField);

        //Save Button
        Button saveButton = new Button("Save");
        saveButton.setStyle("""
            -fx-background-color: #4A90E2;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-padding: 10;
            -fx-background-radius: 8;
        """);
        saveButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            String description = descriptionArea.getText().trim();

            if(name.isEmpty()){
                message.setTextFill(Color.RED);
                message.setText("Exercise name is required.");
                return;
            }

            try{
                boolean created = db.addExercise(username, name, category, description);

                if (created) {
                    stage.setScene(SceneFactory.create(SceneType.MAIN, stage, db, username));
                }
                else {
                    message.setTextFill(Color.RED);
                    message.setText("An exercise with that name already exists.");
                }
            }
            catch (Exception ex){
                message.setTextFill(Color.RED);
                message.setText("Database error.");
                ex.printStackTrace();
            }
        });

        //Cancel Button
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("""
            -fx-background-color: #d3d3d3;
            -fx-text-fill: black;
            -fx-font-size: 14px;
            -fx-padding: 10;
            -fx-background-radius: 8;
        """);
        cancelButton.setOnAction(e ->
            stage.setScene(SceneFactory.create(SceneType.MAIN, stage, db, username)));

        javafx.scene.layout.HBox buttonRow = new javafx.scene.layout.HBox(15);
        buttonRow.setAlignment(Pos.CENTER);
        buttonRow.getChildren().addAll(saveButton, cancelButton);

        root.getChildren().addAll(
                title,
                nameField,
                categoryField,
                descriptionArea,
                buttonRow,
                message
        );

        return new Scene(root, 500, 500);
    }
}
