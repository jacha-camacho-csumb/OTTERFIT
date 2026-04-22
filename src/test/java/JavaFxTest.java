import db.Database;
import factory.SceneFactory;
import factory.SceneType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import ui.login.CreateUserView;
import ui.login.LoginView;
import ui.main.ViewExercises;

import static org.junit.jupiter.api.Assertions.*;

/**
 * [Testing for the JavaFX UI Scenes and Transitions.]
 * Fixed Git Files for viewing making new branch to push changes.
 * @author Christian Hoefer
 * @version 0.1.0
 * @since 4/13/2026
 * @version 0.2.0 : RC : Updated after changing the Login view
 * @version 0.3.0 : JA : Updated after altering MainView
 * @version 0.4.0 : CH : Updated for new MainView, ViewHistory scene, and CreateUserView email/cancel fields
 * @version 0.5.0 : CH : Updated for adding ViewExercises and AddExercise
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
        robot.sleep(1000);
        //Find and populate username and password
        robot.clickOn("#" + LoginView.usernameField).write("oops");
        robot.clickOn("#" + LoginView.passwordField).write("oops");
        //Find the login button and click the button to close
        Button loginButton = robot.lookup("#" + LoginView.loginButton).queryAs(Button.class);
        robot.clickOn(loginButton);
        //Pause to show the testing process
        robot.sleep(1000);
        //Invalid login should show here
        Label message = robot.lookup("#" + LoginView.messageField).queryAs(Label.class);
        assertNotNull(message);
        assertTrue(message.isVisible());
        assertFalse(message.getText().isBlank(), "Error message should not be blank on bad login");
    }

    @Test
    void loginButtonLogsIn(FxRobot robot){
        // Pause to show testing process
        robot.sleep(1000);
        // Enter login credentials
        robot.clickOn("#" + LoginView.usernameField).write("otter");
        robot.clickOn("#" + LoginView.passwordField).write("otter");
        Button loginButton = robot.lookup("#" + LoginView.loginButton).queryAs(Button.class);
        robot.clickOn(loginButton);
        robot.sleep(1000);
        assertTrue(testStage.isShowing(), "Main stage should still be showing after login");
        // Check for welcome label
        Label welcomeLabel = robot.lookup("Welcome, otter").queryAs(Label.class);
        assertNotNull(welcomeLabel, "Welcome label should exist after login");
        assertTrue(welcomeLabel.isVisible(), "Welcome label should be visible after login");
    }

    private void loginAsOtter(FxRobot robot){
        robot.clickOn("#" + LoginView.usernameField).write("otter");
        robot.clickOn("#" + LoginView.passwordField).write("otter");
        robot.clickOn("#" + LoginView.loginButton);
        robot.sleep(1000);
    }

    @Test
    void mainViewButtonsExist(FxRobot robot){
        //Login
        loginAsOtter(robot);
        //check buttons
        assertNotNull(robot.lookup("Log Workout").queryAs(Button.class), "Log Workout button should exist");
        assertNotNull(robot.lookup("View Exercises").queryAs(Button.class), "View Exercises button should exist");
        assertNotNull(robot.lookup("Add Exercise").queryAs(Button.class), "Add Exercise button should exist");
        assertNotNull(robot.lookup("View History").queryAs(Button.class), "View History button should exist");
        assertNotNull(robot.lookup("Sign Out").queryAs(Button.class), "Sign Out button should exist");
    }

    @Test
    void signOutReturnToLogin(FxRobot robot){
        //Login
        loginAsOtter(robot);
        //Sign out
        robot.clickOn("Sign Out");
        robot.sleep(1000);
        //Check for Login Scene after Sign Out
        Label loginLabel = robot.lookup("#" + LoginView.loginTitle).queryAs(Label.class);
        assertNotNull(loginLabel, "Login title should reappear after sign out");
        assertTrue(loginLabel.isVisible());
    }

    @Test
    void viewHistoryShowsWorkouts(FxRobot robot){
        //Login
        loginAsOtter(robot);
        //View History button press
        robot.clickOn("View History");
        robot.sleep(1000);
        //Check Title
        Label title = robot.lookup("Workout History").queryAs(Label.class);
        assertNotNull(title, "Workout History title label should exist");
        assertTrue(title.isVisible());
        //Check the list of workouts, three entries should be present for testing
        ListView<?> list = robot.lookup(".list-view").queryAs(ListView.class);
        assertNotNull(list, "History ListView should exist");
        assertFalse(list.getItems().isEmpty(), "Workout history should have at least one entry");
    }

    @Test
    void viewHistoryBackButtonCheck(FxRobot robot){
        //Login
        loginAsOtter(robot);
        //View History button press
        robot.clickOn("View History");
        robot.sleep(1000);
        //Back button click
        robot.clickOn("Back");
        robot.sleep(1000);
        //Check to be back on MainView
        Label mainLabel = robot.lookup("Welcome, otter").queryAs(Label.class);
        assertNotNull(mainLabel, "Should be back to MainView after clicking Back");
        assertTrue(mainLabel.isVisible());
    }

    @Test
    void createUserSceneEmail(FxRobot robot){
        //Start clicking Create Account
        robot.clickOn("Create Account");
        robot.sleep(500);
        //Checking Email Field
        TextField emailField = robot.lookup("#" + CreateUserView.emailField).queryAs(TextField.class);
        assertNotNull(emailField, "Email field should exist on CreateUser scene");
        assertTrue(emailField.isVisible());
    }

    @Test
    void createUserCancelReturns(FxRobot robot){
        //Click on Create Account
        robot.clickOn("Create Account");
        robot.sleep(1000);
        //Click on Cancel
        robot.clickOn("Cancel");
        robot.sleep(1000);
        //Check for Login Scene
        Label loginLabel = robot.lookup("#" + LoginView.loginTitle).queryAs(Label.class);
        assertNotNull(loginLabel, "Cancel should return to the Login Scene");
        assertTrue(loginLabel.isVisible());
    }

    @Test
    void createUserInvalidInput(FxRobot robot){
        //Click on Create Account
        robot.clickOn("Create Account");
        robot.sleep(1000);
        //Fields Blank and try to save for the invalid input
        robot.clickOn("Save");
        robot.sleep(1000);
        //Check for error message
        Label message = robot.lookup("#" + CreateUserView.messageField).queryAs(Label.class);
        assertNotNull(message);
        assertFalse(message.getText().isBlank(), "Error message should appear for an invalid input");
    }

    @Test
    void createUserSuccess(FxRobot robot){
        //Click on Create Account
        robot.clickOn("Create Account");
        robot.sleep(500);
        //Input new test user
        robot.clickOn("#" + CreateUserView.usernameField).write("newUser");
        robot.clickOn("#" + CreateUserView.emailField).write("newUser@test.com");
        robot.clickOn("#" + CreateUserView.passwordField).write("password123");
        //Click on save
        robot.clickOn("Save");
        robot.sleep(2000);
        //Check for MainView after Login
        Label mainLabel = robot.lookup("Welcome, newUser").queryAs(Label.class);
        assertNotNull(mainLabel, "MainView should be showing after successful Login");
        assertTrue(mainLabel.isVisible());
    }

    @Test
    void viewExercisesOpens(FxRobot robot){
        //Login
        loginAsOtter(robot);
        //Click View Exercises button
        robot.clickOn("View Exercises");
        robot.sleep(1000);
        //Check for Title after Scene change
        Label title = robot.lookup("Exercises").queryAs(Label.class);
        assertNotNull(title, "Exercises title label should exist");
        assertTrue(title.isVisible(), "Exercises title should be visible");
    }

    @Test
    void viewExercisesListShows(FxRobot robot){
        //Login
        loginAsOtter(robot);
        //Click on View Exercises button
        robot.clickOn("View Exercises");
        robot.sleep(1000);
        //Check the list exists and has the test data
        ListView<?> list = robot.lookup(".list-view").queryAs(ListView.class);
        assertNotNull(list, "Exercise ListView should exist");
        assertFalse(list.getItems().isEmpty(), "Exercise list should contain at least one entry from test data");
    }

    @Test
    void viewExercisesBackButton(FxRobot robot){
        //Login
        loginAsOtter(robot);
        //Click on View Exercises button
        robot.clickOn("View Exercises");
        robot.sleep(1000);
        //Click on Back button
        robot.clickOn("Back");
        robot.sleep(1000);
        //Should be back on MainView
        Label mainLabel = robot.lookup("Welcome, otter").queryAs(Label.class);
        assertNotNull(mainLabel, "Should return to MainView after clicking Back button in ViewExercises");
        assertTrue(mainLabel.isVisible());
    }
}