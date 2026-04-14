package ui.main;

import db.Database;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import factory.SceneFactory;

/**
 * MainView
 * <p>
 * Description:
 * Main application view, dashboard
 *
 * @author rcwav
 * @since 4/12/2026
 */

public class MainView {

    public static Scene createScene(Stage stage, Database db) {
        Label label = new Label("Main Screen");
        StackPane layout = new StackPane(label);

        // Show login popup after main loads
        stage.setOnShown(e -> {
            SceneFactory.createLoginPopup(stage).show();
        });

        return new Scene(layout, 400, 300);
    }
}