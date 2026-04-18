package ui.login;

import db.Database;
import factory.SceneFactory;
import factory.SceneType;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * CreateUser
 * <p>
 * Description:
 * Create a new user, new user registration
 *
 * @author rcwav
 * @since 4/18/2026
 */
public class CreateUserView {

    public static final String loginTitle = "loginTitle";
    public static final String usernameField = "usernameField";
    public static final String emailField = "emailField";
    public static final String passwordField = "passwordField";
    public static final String messageField = "messageField";

    public static Scene createScene(Stage stage, Database db) {

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f2f2f2;");

        // Title
        Label title = new Label("Create New User");
        title.setFont(new Font("Arial", 26));
        title.setId(CreateUserView.loginTitle);

        // Username
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(300);
        usernameField.setId(CreateUserView.usernameField);

        // Email
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(300);
        emailField.setId(CreateUserView.emailField);

        // Password
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);
        passwordField.setId(CreateUserView.passwordField);

        // Message
        Label message = new Label();
        message.setTextFill(Color.RED);
        message.setId(CreateUserView.messageField);

        // Create account link
        Button createAccount = new Button("Save");
        createAccount.setStyle("""
            -fx-background-color: #4A90E2;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-padding: 10;
            -fx-background-radius: 8;
        """);
        createAccount.setOnAction(e -> {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();

            // Basic validation (matches your User.isValid style)
            if (!model.User.isValid(username, email, password)) {
                message.setTextFill(Color.RED);
                message.setText("Invalid input");
                return;
            }

            try {
                boolean created = db.createUser(username, email, password);

                if (created) {
                    message.setTextFill(Color.GREEN);
                    message.setText("User created successfully!");

                    // small pause then navigate back to login
                    PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                    pause.setOnFinished(event -> {
                        stage.setScene(SceneFactory.create(SceneType.MAIN, stage, db, username));
                    });
                    pause.play();

                    stage.setScene(SceneFactory.create(SceneType.LOGIN, stage, db));

                } else {
                    message.setTextFill(Color.RED);
                    message.setText("Username or email already exists");
                }

            } catch (Exception ex) {
                message.setTextFill(Color.RED);
                message.setText("Database error");
                ex.printStackTrace();
            }
        });

        // Create account link
        Button cancel = new Button("Cancel");
        cancel.setStyle("""
            -fx-background-color: #d3d3d3;
            -fx-text-fill: black;
            -fx-font-size: 14px;
            -fx-padding: 10;
            -fx-background-radius: 8;
        """);
        cancel.setOnAction(e -> {
            stage.setScene(SceneFactory.create(SceneType.LOGIN, stage, db));
        });

        // Create a show database link for debugging
        Button showDatabase = new Button("(show database)");
        showDatabase.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: #4A90E2;
            -fx-underline: true;
        """);
        showDatabase.setOnAction(e -> {
            stage.setScene(SceneFactory.create(SceneType.DATABASE, stage, db, SceneType.CREATE_USER));
        });

        HBox buttonRow = new HBox(15);
        buttonRow.setAlignment(Pos.CENTER);
        buttonRow.getChildren().addAll(createAccount, cancel);

        root.getChildren().addAll(
                title,
                usernameField,
                emailField,
                passwordField,
                buttonRow,
                showDatabase,
                message
        );

        return new Scene(root, 500, 500);
    }
}
