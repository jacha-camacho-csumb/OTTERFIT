import factory.SceneFactory;
import factory.SceneType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import static org.junit.jupiter.api.Assertions.*;

/**
 * [Testing for the JavaFX UI Scenes and Transitions.]
 * Fixed Git Files for viewing making new branch to push changes.
 * @author Christian Hoefer
 * @version 0.1.0
 * @since 4/13/2026
 */
@ExtendWith(ApplicationExtension.class)
public class JavaFxTest {

    private Stage testStage;

    @Start
    public void start(Stage stage){
        this.testStage = stage;
        //Set the window title
        stage.setTitle("OtterFit");
        //Load the main scene
        stage.setScene(SceneFactory.create(SceneType.MAIN, stage));
        //Show the window
        stage.show();
    }

    @Test
    void mainScene(FxRobot robot){
        //Find the label by its text
        Label label = robot.lookup("Main Screen").queryAs(Label.class);
        assertNotNull(label, "Main Screen label should exist");
        assertTrue(label.isVisible(), "Main Screen label should be visible");
    }

    @Test
    void loginPopup(FxRobot robot){
        //Find the Login label in the popup
        Label loginLabel = robot.lookup("Login").queryAs(Label.class);
        assertNotNull(loginLabel, "Login label should exist in the popup");
        assertTrue(loginLabel.isVisible(), "Login label should be visible");
        //Find the button by its class
        Button loginButton = robot.lookup(".button").queryAs(Button.class);
        assertNotNull(loginButton, "Login button should exist.");
        assertEquals("Login", loginButton.getText(), "Button text should say Login");
    }

    @Test
    void loginButtonCloses(FxRobot robot){
        //Pause to show testing process
        robot.sleep(2000);
        //Find the login button and click the button to close
        Button loginButton = robot.lookup(".button").queryAs(Button.class);
        robot.clickOn(loginButton);
        //Pause to show the testing process
        robot.sleep(2000);
        //Main stage should still be open
        assertTrue(testStage.isShowing(), "Main stage should still be showing after login");
        //Main screen label should still be visible
        Label mainLabel = robot.lookup("Main Screen").queryAs(Label.class);
        assertNotNull(mainLabel, "Main Screen label should still exist after popup closes");
        assertTrue(mainLabel.isVisible(), "Main Screen label should be visible after popup closes");
    }
}
