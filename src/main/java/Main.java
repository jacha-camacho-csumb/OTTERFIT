import db.Database;
import javafx.application.Application;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import factory.*;

import java.sql.SQLException;

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

    // Create DatabaseManager here, then pass to scene manager
    // so all scenes can use it, a single instance
    try {
      Database db = new Database();

      stage.setScene(SceneFactory.create(SceneType.LOGIN, stage, db));
      stage.show();
    } catch (SQLException e) {
      System.out.println("Error opening db: " + e);
    }

  }

  public static void main(String[] args) {
    launch();
  }
}