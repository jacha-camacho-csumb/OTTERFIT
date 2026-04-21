package factory;

import db.Database;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.login.CreateUserView;
import ui.login.LoginView;
import ui.main.AddExercise;
import ui.main.MainView;
import ui.main.ViewExercises;
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
    return create(type, stage, db, null, null);
  }

  /**
   * Scene creation with username (used after login)
   */
  public static Scene create(SceneType type, Stage stage, Database db, String username) {
    return create(type, stage, db, null, username);
  }

  /**
   * Scene creation with return navigation (used by DatabaseView)
   */
  public static Scene create(SceneType type, Stage stage, Database db, SceneType returnTo) {
    return create(type, stage, db, returnTo,null);
  }

  /**
   * One Scene creation to rule them all...
   */
  public static Scene create(SceneType type, Stage stage, Database db, SceneType returnTo, String username) {
    String sceneUsername = username != null ? username : "Guest";
    SceneType sceneReturnTo = returnTo != null ? returnTo : SceneType.MAIN;
    return switch (type) {
      case MAIN -> MainView.createScene(stage, db, sceneUsername);
      case LOGIN -> LoginView.createScene(stage, db);
      case DATABASE -> DatabaseView.createScene(stage, db, sceneReturnTo);
      case VIEW_HISTORY -> ViewHistory.createScene(stage, db, sceneUsername);
      case CREATE_USER -> CreateUserView.createScene(stage, db);
      case VIEW_EXERCISES -> ViewExercises.createScene(stage, db, sceneUsername);
      case ADD_EXERCISE -> AddExercise.createScene(stage, db, sceneUsername);
    };
  }
}