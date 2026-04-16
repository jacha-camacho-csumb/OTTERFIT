package factory;

import db.Database;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.login.LoginView;
import ui.main.MainView;
import ui.main.ViewHistory;
import ui.utility.DatabaseView;

/**
 * SceneFactory Handles creation of all application scenes
 *
 * @author rcwav
 * @author Jose Acha-Camacho
 * @version : 0.3.0
 * @since 3/28/2026
 */
public abstract class SceneFactory {

  /**
   * Default scene creation (no username)
   */
  public static Scene create(SceneType type, Stage stage, Database db) {
    return switch (type) {
      case MAIN -> MainView.createScene(stage, db, "Guest");
      case LOGIN -> LoginView.createScene(stage, db);
      case DATABASE -> DatabaseView.createScene(stage, db, SceneType.MAIN);
      case VIEW_HISTORY -> ViewHistory.createScene(stage, db, "Guest");
    };
  }

  /**
   * Scene creation with username (used after login)
   */
  public static Scene create(SceneType type, Stage stage, Database db, String username) {
    return switch (type) {
      case MAIN -> MainView.createScene(stage, db, username);
      case LOGIN -> LoginView.createScene(stage, db);
      case DATABASE -> DatabaseView.createScene(stage, db, SceneType.MAIN);
      case VIEW_HISTORY -> ViewHistory.createScene(stage, db, username);
    };
  }

  /**
   * Scene creation with return navigation (used by DatabaseView)
   */
  public static Scene create(SceneType type, Stage stage, Database db, SceneType returnTo) {
    return switch (type) {
      case DATABASE -> DatabaseView.createScene(stage, db, returnTo);
      default -> create(type, stage, db);
    };
  }
}