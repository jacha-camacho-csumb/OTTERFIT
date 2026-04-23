import db.Database;
import factory.SceneFactory;
import factory.SceneType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import ui.login.CreateUserView;
import ui.login.LoginView;
import ui.main.AddExercise;
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
 * @version 0.6.0 : CH : Updated for adding LogWorkoutView and DeleteWorkoutView
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
        //Find and populate username and password
        robot.clickOn("#" + LoginView.usernameField).write("oops");
        robot.clickOn("#" + LoginView.passwordField).write("oops");
        //Find the login button and click the button to close
        Button loginButton = robot.lookup("#" + LoginView.loginButton).queryAs(Button.class);
        robot.clickOn(loginButton);
        //Pause to show the testing process
        robot.sleep(400);
        //Invalid login should show here
        Label message = robot.lookup("#" + LoginView.messageField).queryAs(Label.class);
        assertNotNull(message);
        assertTrue(message.isVisible());
        assertFalse(message.getText().isBlank(), "Error message should not be blank on bad login");
    }

    @Test
    void loginButtonLogsIn(FxRobot robot){
        // Enter login credentials
        robot.clickOn("#" + LoginView.usernameField).write("otter");
        robot.clickOn("#" + LoginView.passwordField).write("otter");
        Button loginButton = robot.lookup("#" + LoginView.loginButton).queryAs(Button.class);
        robot.clickOn(loginButton);
        robot.sleep(400);
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
        robot.sleep(400);
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
        assertNotNull(robot.lookup("Delete Workout").queryAs(Button.class), "Delete Workout button should exist");
        assertNotNull(robot.lookup("Sign Out").queryAs(Button.class), "Sign Out button should exist");
    }

    @Test
    void signOutReturnToLogin(FxRobot robot){
        //Login
        loginAsOtter(robot);
        //Sign out
        robot.clickOn("Sign Out");
        robot.sleep(400);
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
        robot.sleep(400);
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
        robot.sleep(400);
        //Back button click
        robot.clickOn("Back");
        robot.sleep(400);
        //Check to be back on MainView
        Label mainLabel = robot.lookup("Welcome, otter").queryAs(Label.class);
        assertNotNull(mainLabel, "Should be back to MainView after clicking Back");
        assertTrue(mainLabel.isVisible());
    }

    @Test
    void createUserSceneEmail(FxRobot robot){
        //Start clicking Create Account
        robot.clickOn("Create Account");
        robot.sleep(400);
        //Checking Email Field
        TextField emailField = robot.lookup("#" + CreateUserView.emailField).queryAs(TextField.class);
        assertNotNull(emailField, "Email field should exist on CreateUser scene");
        assertTrue(emailField.isVisible());
    }

    @Test
    void createUserCancelReturns(FxRobot robot){
        //Click on Create Account
        robot.clickOn("Create Account");
        robot.sleep(400);
        //Click on Cancel
        robot.clickOn("Cancel");
        robot.sleep(400);
        //Check for Login Scene
        Label loginLabel = robot.lookup("#" + LoginView.loginTitle).queryAs(Label.class);
        assertNotNull(loginLabel, "Cancel should return to the Login Scene");
        assertTrue(loginLabel.isVisible());
    }

    @Test
    void createUserInvalidInput(FxRobot robot){
        //Click on Create Account
        robot.clickOn("Create Account");
        robot.sleep(400);
        //Fields Blank and try to save for the invalid input
        robot.clickOn("Save");
        robot.sleep(400);
        //Check for error message
        Label message = robot.lookup("#" + CreateUserView.messageField).queryAs(Label.class);
        assertNotNull(message);
        assertFalse(message.getText().isBlank(), "Error message should appear for an invalid input");
    }

    @Test
    void createUserSuccess(FxRobot robot){
        //Click on Create Account
        robot.clickOn("Create Account");
        robot.sleep(400);
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
        robot.sleep(400);
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
        robot.sleep(400);
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
        robot.sleep(400);
        //Click on Back button
        robot.clickOn("Back");
        robot.sleep(400);
        //Should be back on MainView
        Label mainLabel = robot.lookup("Welcome, otter").queryAs(Label.class);
        assertNotNull(mainLabel, "Should return to MainView after clicking Back button in ViewExercises");
        assertTrue(mainLabel.isVisible());
    }

    @Test
    void addExerciseOpens(FxRobot robot){
        //Login
        loginAsOtter(robot);
        //Click on Add Exercise
        robot.clickOn("Add Exercise");
        robot.sleep(400);
        //Check for Title after Scene change
        Label title = robot.lookup("Add Exercise").queryAs(Label.class);
        assertNotNull(title, "Add Exercise title label should exist");
        assertTrue(title.isVisible(), "Add Exercise title should be visible");
    }

    @Test
    void addExerciseFieldsShows(FxRobot robot){
        //Login
        loginAsOtter(robot);
        //Click on Add Exercise
        robot.clickOn("Add Exercise");
        robot.sleep(400);
        //Check that name, category, and description fields show on screen
        TextField nameField = robot.lookup("#" + AddExercise.nameField).queryAs(TextField.class);
        assertNotNull(nameField, "Exercise name field should exist");
        assertTrue(nameField.isVisible());
        TextField categoryField = robot.lookup("#" + AddExercise.categoryField).queryAs(TextField.class);
        assertNotNull(categoryField, "Exercise category field should exist");
        assertTrue(categoryField.isVisible());
        TextArea descriptionArea = robot.lookup("#" + AddExercise.descriptionArea).queryAs(TextArea.class);
        assertNotNull(descriptionArea, "Exercise description area should exist");
        assertTrue(descriptionArea.isVisible());
    }

    @Test
    void addExerciseCancelButton(FxRobot robot){
        //Login
        loginAsOtter(robot);
        //Click on Add Exercise
        robot.clickOn("Add Exercise");
        robot.sleep(400);
        //Click on cancel button
        robot.clickOn("Cancel");
        robot.sleep(400);
        //Should be back on MainView
        Label mainLabel = robot.lookup("Welcome, otter").queryAs(Label.class);
        assertNotNull(mainLabel, "Should return to MainView after clicking Cancel button in AddExercise");
        assertTrue(mainLabel.isVisible());
    }

    @Test
    void addExerciseRequiresName(FxRobot robot){
        //Login
        loginAsOtter(robot);
        //Click on Add Exercise
        robot.clickOn("Add Exercise");
        robot.sleep(400);
        //Click on Save without anything placed in name
        robot.clickOn("Save");
        robot.sleep(400);
        //Error should happen and show
        Label error = robot.lookup("#" + AddExercise.messageField).queryAs(Label.class);
        assertNotNull(error, "Error should appear");
        assertFalse(error.getText().isBlank(), "Error should appear when name is blank");
    }

    @Test
    void addExerciseDuplicate(FxRobot robot){
        //Login
        loginAsOtter(robot);
        //Click on Add Exercise
        robot.clickOn("Add Exercise");
        robot.sleep(400);
        //Try an already used name for an exercise
        robot.clickOn("#" + AddExercise.nameField).write("Push-Up");
        robot.clickOn("Save");
        robot.sleep(400);
        //Error for duplicate should appear
        Label message = robot.lookup("#" + AddExercise.messageField).queryAs(Label.class);
        assertNotNull(message, "Error message should exist");
        assertFalse(message.getText().isBlank(), "Error message should appear for the duplicate");
    }

    @Test
    void addExerciseSuccess(FxRobot robot){
        //Login
        loginAsOtter(robot);
        //Click on Add Exercise
        robot.clickOn("Add Exercise");
        robot.sleep(400);
        //Fill in New Exercise
        robot.clickOn("#" + AddExercise.nameField).write("Plank");
        robot.clickOn("#" + AddExercise.categoryField).write("Core");
        robot.clickOn("#" + AddExercise.descriptionArea).write("Hold a horizontal body position");
        //Click on Save
        robot.clickOn("Save");
        robot.sleep(400);
        //Should be back on MainView after saving new exercise
        Label mainLabel = robot.lookup("Welcome, otter").queryAs(Label.class);
        assertNotNull(mainLabel, "Should return to MainView after clicking Save button in AddExercise");
        assertTrue(mainLabel.isVisible(), "Welcome label should be visible after cancel");
    }

    @Test
    void logWorkoutOpens(FxRobot robot){
        //Login
        loginAsOtter(robot);
        //Click on Log Workout
        robot.clickOn("Log Workout");
        robot.sleep(400);
        //Log workout title should be visible on the after clicking
        Label title = robot.lookup("Log Workout").queryAs(Label.class);
        assertNotNull(title, "Log Workout title label should exist after clicking Log Workout");
        assertTrue(title.isVisible(), "Log Workout title should be visible");
    }

    @Test
    void logWorkoutCancelButton(FxRobot robot){
        //Login
        loginAsOtter(robot);
        //Click on Log Workout
        robot.clickOn("Log Workout");
        robot.sleep(400);
        //Click on Cancel Button
        robot.clickOn("Cancel");
        robot.sleep(400);
        //Should be back on MainView after clicking cancel
        Label mainLabel = robot.lookup("Welcome, otter").queryAs(Label.class);
        assertNotNull(mainLabel, "Should return to MainView after clicking Cancel Button");
        assertTrue(mainLabel.isVisible(), "Welcome label should be visible after cancel");
    }

    @Test
    void deleteWorkoutOpens(FxRobot robot){
        //Login
        loginAsOtter(robot);
        //Click on Delete Workout
        robot.clickOn("Delete Workout");
        robot.sleep(400);
        //Delete Workout title should be visible
        Label title = robot.lookup("Delete Workout").queryAs(Label.class);
        assertNotNull(title, "Delete Workout title label should exist after clicking Delete Workout");
        assertTrue(title.isVisible(), "Delete Workout title should be visible");
    }

    @Test
    void deleteWorkoutComboBoxLoads(FxRobot robot){
        //Login
        loginAsOtter(robot);
        //Click on Delete Workout
        robot.clickOn("Delete Workout");
        robot.sleep(400);
        //Combo Box should be loaded with Test Workouts
        ComboBox<?> workoutCombo = robot.lookup(".combo-box").queryAs(ComboBox.class);
        assertNotNull(workoutCombo, "Workout ComboBox should exist on Delete Workout");
        assertFalse(workoutCombo.getItems().isEmpty(), "Workout ComboBox should contain at least one entry");
    }

    @Test
    void deleteWorkoutCancelButton(FxRobot robot){
        //Login
        loginAsOtter(robot);
        //Click on Delete Workout
        robot.clickOn("Delete Workout");
        robot.sleep(400);
        //Click on the Cancel Button
        robot.clickOn("Cancel");
        robot.sleep(400);
        //Should be back on MainView after clicking cancel
        Label mainLabel = robot.lookup("Welcome, otter").queryAs(Label.class);
        assertNotNull(mainLabel, "Should return to MainView after clicking Cancel Button");
        assertTrue(mainLabel.isVisible(), "Welcome label should be visible after cancel");
    }
}