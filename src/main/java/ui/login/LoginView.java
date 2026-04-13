package ui.login;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * LoginView
 * <p>
 * Description:
 *
 * @author rcwav
 * @since 4/12/2026
 */

public class LoginView {

    public static Scene createScene(Stage stage) {
        // Not used right now, but keeps API consistent
        VBox layout = new VBox(new Label("Login Scene"));
        return new Scene(layout, 200, 150);
    }

    public static Stage createPopup(Stage owner) {
        Stage stage = new Stage();

        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);

        Label label = new Label("Login");
        Button button = new Button("Login");

        button.setOnAction(e -> stage.close());

        VBox layout = new VBox(10, label, button);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        stage.setScene(new Scene(layout, 200, 150));
        stage.setTitle("Login");

        return stage;
    }
}