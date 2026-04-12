import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    private static final String F_TO_C_LABEL = "Farenheit -> Celcius";
    private static final String C_TO_F_LABEL = "Celcius -> Farenheit";
    private static final String S1_PROMPT = "Enter °F";
    private static final String S2_PROMPT = "Enter °C";
    private static final String CONVERT = "Convert!";
    private static final String ERROR_MSG = "Not a valid input: ";

    // declare a PauseTransition timer to implement "long press" behavior
    private static final PauseTransition holdTimer = new PauseTransition(Duration.millis(800));

    public static Scene create(SceneType type, Stage stage) {
        return switch(type) {
            case F_TO_C -> buildFtoCStage(stage);
            case C_TO_F -> buildCtoFStage(stage);
        };
    }

    private static Scene buildFtoCStage(Stage stage) {
        Label s1Label = new Label(F_TO_C_LABEL);
        TextField s1Input = new TextField();
        s1Input.setPromptText(S1_PROMPT);
        s1Input.setPrefWidth(200);

        Button s1Convert = new Button(CONVERT);

        Label s1Result = new Label();

        s1Convert.setOnAction(e -> {
            String input = s1Input.getText();
            try {
                double value = Double.parseDouble(input);
                s1Result.setText(String.format("%.2f", TemperatureConverters.FtoC(value)));
            } catch (NumberFormatException ex) {
                s1Result.setText(ERROR_MSG + input);
            }

        });

        // implement a "long press" behavior to switch scenes
        s1Convert.setOnMousePressed(e -> {
            holdTimer.setOnFinished(ev -> {
                stage.setScene(SceneFactory.create(SceneType.C_TO_F, stage));
                stage.show();
            });
            holdTimer.playFromStart();
        });

        s1Convert.setOnMouseReleased(e -> {
            holdTimer.stop();
        });

        VBox root1 = new VBox(12, s1Label, s1Input, s1Convert, s1Result);

        root1.setPadding(new Insets(30));
        root1.setAlignment(Pos.CENTER);

        // Scene holds the layout and defines the window size
        return new Scene(root1, SCENE_WIDTH, SCENE_HEIGHT);
    }

    private static Scene buildCtoFStage(Stage stage) {
        Label s1Label = new Label(C_TO_F_LABEL);
        TextField s1Input = new TextField();
        s1Input.setPromptText(S2_PROMPT);
        s1Input.setPrefWidth(200);

        Button s1Convert = new Button(CONVERT);

        Label s1Result = new Label();

        s1Convert.setOnAction(e -> {
            String input = s1Input.getText();
            try {
                double value = Double.parseDouble(input);
                s1Result.setText(String.format("%.2f", TemperatureConverters.CtoF(value)));
            } catch (NumberFormatException ex) {
                s1Result.setText(ERROR_MSG + input);
            }

        });

        // implement a "long press" behavior to switch scenes
        s1Convert.setOnMousePressed(e -> {
            holdTimer.setOnFinished(ev -> {
                stage.setScene(SceneFactory.create(SceneType.F_TO_C, stage));
                stage.show();
            });
            holdTimer.playFromStart();
        });

        s1Convert.setOnMouseReleased(e -> {
            holdTimer.stop();
        });

        VBox root1 = new VBox(12, s1Label, s1Input, s1Convert, s1Result);

        root1.setPadding(new Insets(30));
        root1.setAlignment(Pos.CENTER);

        // Scene holds the layout and defines the window size
        return new Scene(root1, SCENE_WIDTH, SCENE_HEIGHT);
    }
}
