import javafx.application.Application;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import factory.*;

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

    stage.setTitle("My First App");
    stage.setScene(SceneFactory.create(SceneType.MAIN, stage));
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}