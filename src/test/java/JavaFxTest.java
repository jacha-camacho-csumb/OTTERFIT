import db.Database;
import factory.SceneFactory;
import factory.SceneType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import ui.login.LoginView;

import static org.junit.jupiter.api.Assertions.*;

/**
 * [Testing for the JavaFX UI Scenes and Transitions.]
 * Fixed Git Files for viewing making new branch to push changes.
 * @author Christian Hoefer
 * @version 0.1.0
 * @since 4/13/2026
 * @version 0.2.0 : RC : Updated after changing the Login view
 */
@ExtendWith(ApplicationExtension.class)
public class JavaFxTest {

    private Stage testStage;
    private Database db;
    private static final String TEMP_DB = "jdbc:sqlite::memory:";

    @Start
    public void start(Stage stage) throws SQLException {
        this.testStage = stage;

        // important - establish new in memory database, then initialize()
        Connection conn = DriverManager.getConnection(TEMP_DB);
        this.db = new Database(conn);
        db.initialize();

        // Set the window title
        stage.setTitle("OtterFit");
        // Load the main scene
        stage.setScene(SceneFactory.create(SceneType.LOGIN, stage, db));
        // Show the window
        stage.show();
    }

    @Test
    void loginScene(FxRobot robot){
        //Find the Login label in the popup
        Label loginLabel = robot.lookup("#" + LoginView.loginTitle).queryAs(Label.class);
        assertNotNull(loginLabel, "Login label should exist in the popup");
        assertTrue(loginLabel.isVisible(), "Login label should be visible");
        //Find the button by its class
        Button loginButton = robot.lookup("#" + LoginView.loginButton).queryAs(Button.class);
        assertNotNull(loginButton, "Login button should exist.");
        assertEquals("Login", loginButton.getText(), "Button text should say Login");
    }

    @Test
    void loginButtonFails(FxRobot robot){
        //Pause to show testing process
        robot.sleep(2000);
        //Find and populate username and password
        robot.clickOn("#" + LoginView.usernameField).write("oops");
        robot.clickOn("#" + LoginView.passwordField).write("oops");
        //Find the login button and click the button to close
        Button loginButton = robot.lookup("#" + LoginView.loginButton).queryAs(Button.class);
        robot.clickOn(loginButton);
        //Pause to show the testing process
        robot.sleep(2000);
        //Invalid login should show here
        Label message = robot.lookup("#" + LoginView.messageField).queryAs(Label.class);
        assertNotNull(message);
        assertTrue(message.isVisible());
    }

    @Test
    void loginButtonLogsIn(FxRobot robot){
        //Pause to show testing process
        robot.sleep(2000);
        //Find and populate username and password
        robot.clickOn("#" + LoginView.usernameField).write("otter");
        robot.clickOn("#" + LoginView.passwordField).write("otter");
        //Find the login button and click the button to close
        Button loginButton = robot.lookup("#" + LoginView.loginButton).queryAs(Button.class);
        robot.clickOn(loginButton);
        //Pause to show the testing process
        robot.sleep(2000);
        //Main stage should now be open
        assertTrue(testStage.isShowing(), "Main stage should still be showing after login");
        //Main screen label should still be visible
        Label mainLabel = robot.lookup("Main Screen").queryAs(Label.class);
        assertNotNull(mainLabel, "Main Screen label should still exist after popup closes");
        assertTrue(mainLabel.isVisible(), "Main Screen label should be visible after popup closes");
    }
}