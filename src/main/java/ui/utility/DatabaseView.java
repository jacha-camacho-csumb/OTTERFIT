package ui.utility;
import db.Database;
import factory.SceneFactory;
import factory.SceneType;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
/**
 * DatabaseView
 * <p>
 * Description:
 *
 * @author rcwav
 * @since 4/14/2026
 */
public class DatabaseView {

    public static Scene createScene(Stage stage, Database db, SceneType returnTo) {

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label title = new Label("Database Contents");

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(false);

        // Load data
        textArea.setText(db.dumpAllTables());

        Button backButton = new Button("Back");
        backButton.setOnAction(e ->
                stage.setScene(SceneFactory.create(returnTo, stage, db))
        );

        root.getChildren().addAll(title, textArea, backButton);

        return new Scene(root, 600, 500);
    }
}
