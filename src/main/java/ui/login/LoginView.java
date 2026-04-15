package ui.login;

import db.Database;
import factory.SceneFactory;
import factory.SceneType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
/**
 * LoginView
 * <p>
 * Description:
 *
 * @author rcwav
 * @since 4/12/2026
 */

public class LoginView {
    public static Scene createScene(Stage stage, Database db) {

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f2f2f2;");

        // Title
        Label title = new Label("FitnessApp Login");
        title.setFont(new Font("Arial", 26));

        // Username
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(300);

        // Password
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);

        // Message
        Label message = new Label();
        message.setTextFill(Color.RED);

        // Login button
        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(300);
        loginButton.setStyle("""
            -fx-background-color: #4A90E2;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-padding: 10;
            -fx-background-radius: 8;
        """);

        // Create account link
        Button createAccount = new Button("Create Account");
        createAccount.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: #4A90E2;
            -fx-underline: true;
        """);

        // Create a show database link for debugging
        Button showDatabase = new Button("(show database)");
        showDatabase.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: #4A90E2;
            -fx-underline: true;
        """);
        showDatabase.setOnAction(e -> {
            stage.setScene(SceneFactory.create(SceneType.DATABASE, stage, db, SceneType.LOGIN));
        });

        // 🔐 Login logic
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            System.out.printf("username: %s\npassword: %s\n",username, password);

            try {
                if (db.validateUser(username, password)) {
                    message.setTextFill(Color.GREEN);
                    message.setText("Login successful!");

                    // 🚀 Switch to main scene
                    stage.setScene(SceneFactory.create(SceneType.MAIN, stage, db));

                } else {
                    message.setTextFill(Color.RED);
                    message.setText("Invalid username or password");
                }
            } catch (Exception ex) {
                message.setText("Database error");
                ex.printStackTrace();
            }
        });

        root.getChildren().addAll(
                title,
                usernameField,
                passwordField,
                loginButton,
                createAccount,
                showDatabase,
                message
        );

        return new Scene(root, 500, 500);
    }
}