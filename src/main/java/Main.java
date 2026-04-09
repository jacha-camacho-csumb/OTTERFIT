import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * PROJECT #2 - OTTERFit
 * Description: Fitness tracking program built with JavaFX
 *
 * @author TEAM-OTTERWISE
 * @since 4/1/2026
 */


public class Main extends Application {
  @Override
  public void start(Stage stage) {
    Label label = new Label("Hello JavaFX ");
    Scene scene = new Scene(label, 300, 200);

    stage.setTitle("My First App");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}