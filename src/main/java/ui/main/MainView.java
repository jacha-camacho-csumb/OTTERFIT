package ui.main;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import factory.SceneFactory;

/**
 * MainView
 * <p>
 * Description:
 *
 * @author rcwav
 * @since 4/12/2026
 */

public class MainView {

    public static Scene createScene(Stage stage) {
        Label label = new Label("Main Screen");
        StackPane layout = new StackPane(label);

        // Show login popup after main loads
        stage.setOnShown(e -> {
            SceneFactory.createLoginPopup(stage).show();
        });

        return new Scene(layout, 400, 300);
    }
}