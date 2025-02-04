package it.polimi.ingsw.gc12.View.Client.GUI;

import it.polimi.ingsw.gc12.View.Client.GUI.GUIViews.GUIView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Represents the main JavaFX application for the Graphical User Interface implementation.
 * <p>
 * This class extends the {@link Application} class from JavaFX, enabling the GUI to be launched with
 * all the routine methods needed by JavaFX and setting the initial stage with the desired characteristics.
 * </p>
 */
public class GUIApplication extends Application {

    /**
     * The main entry point for this JavaFX application. The start method is called after the init method
     * has returned, and after the system is ready for the application to begin running.
     * The stage is set up to reflect the wanted appearance and implement the needed functionalities.
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     *
     * @param stage the primary stage for this application, onto which the application scene can be set.
     */
    @Override
    public void start(Stage stage) {
        Font.loadFont(GUIApplication.class.getResourceAsStream("/Client/fonts/MedievalSharp-Regular.ttf"), 16);
        Font.loadFont(GUIApplication.class.getResourceAsStream("/Client/fonts/MedievalSharp-Regular.ttf"), 18);
        Font.loadFont(GUIApplication.class.getResourceAsStream("/Client/fonts/MedievalSharp-Regular.ttf"), 20);
        Font.loadFont(GUIApplication.class.getResourceAsStream("/Client/fonts/MedievalSharp-Regular.ttf"), 22);
        Font.loadFont(GUIApplication.class.getResourceAsStream("/Client/fonts/MedievalSharp-Regular.ttf"), 24);

        stage.setFullScreen(true);
        stage.setResizable(false);
        GUIView.stage = stage;

        Screen screen = Screen.getPrimary();
        stage.setWidth(screen.getVisualBounds().getWidth());
        stage.setHeight(screen.getVisualBounds().getHeight());

        Scene stageScene = new Scene(new Pane(), stage.getWidth(), stage.getHeight());
        AnchorPane initializationPane = new AnchorPane();
        initializationPane.setStyle("-fx-background-color: black;");
        stageScene.setRoot(initializationPane);
        stage.setScene(stageScene);

        stage.setTitle("Codex Naturalis");
        stage.getIcons().clear();
        stage.getIcons().add(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("/Client/images/only_center_logo_no_bg.png"))));

        stage.setFullScreenExitHint("Press ESC to exit fullscreen. If you want to re-enter fullscreen afterwards, press F11.");

        stage.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                stage.setResizable(true);
                stage.setFullScreen(false);
                stage.setMaximized(true);
                stage.setResizable(false);
            }
        });

        stage.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            if (event.getCode().equals(KeyCode.F11)) {
                stage.setFullScreen(true);
            }
        });

        stage.setOnCloseRequest((event) -> System.exit(1));

        GUIView.setWindowSize();
        stage.show();
    }
}