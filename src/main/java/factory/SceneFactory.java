package factory;

import db.Database;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * SceneFactory
 * <p>
 * Description:
 *
 * @author rcwav
 * @since 3/28/2026
 */
public abstract class SceneFactory {

    // Window dimensions in pixels
    private static final int SCENE_WIDTH = 400;
    private static final int SCENE_HEIGHT = 300;

    public static Scene create(SceneType type, Stage stage, Database db) {
        return switch(type) {
            case MAIN -> ui.main.MainView.createScene(stage, db);
            case LOGIN -> ui.login.LoginView.createScene(stage, db);
        };
    }

    // For popups (like login, returns Stage instead of Scene)
    public static Stage createLoginPopup(Stage owner) {
        return ui.login.LoginView.createPopup(owner);
    }
}
