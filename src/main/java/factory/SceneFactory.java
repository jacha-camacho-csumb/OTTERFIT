package factory;

import db.Database;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.utility.DatabaseView;

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
            case DATABASE -> ui.utility.DatabaseView.createScene(stage, db, SceneType.MAIN);
        };
    }
    /*
    Overload create with a method that includes a returnTo
     */
    public static Scene create(SceneType type, Stage stage, Database db, SceneType returnTo) {
        switch(type) {
            case DATABASE:
                return DatabaseView.createScene(stage, db, returnTo);
            default:
                return create(type, stage, db);
        }
    }
}
