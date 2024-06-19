package it.polimi.ingsw.gc12.View.Client.GUI;

import it.polimi.ingsw.gc12.View.Client.GUI.GUIViews.GUIView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class GUIApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
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
        //stage.initStyle(StageStyle.UNDECORATED);
        //TODO: add custom toolbar on top of the stage and make it undecorated, and also make that draggable to the top
        // so that one can maximize the window by dragging toolbar to the top (along with decommenting setSizes below)

        stage.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                stage.setResizable(true);
                stage.setFullScreen(false);
                stage.setMaximized(true);
                //stage.setWidth(screen.getVisualBounds().getWidth());
                //stage.setHeight(screen.getVisualBounds().getHeight());
                //GUIView.setScreenSizes();
                stage.setResizable(false);
            }
        });

        stage.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            if (event.getCode().equals(KeyCode.F11)) {
                stage.setFullScreen(true);
            }
            //TODO: + show hint per rientrare in fullscreen
        });

        stage.setOnCloseRequest((event) -> System.exit(1));

        //stage.addEventHandler(MouseDragEvent.MOUSE_DRAGGED, (event) -> stage.setMaximized(true));

        GUIView.setScreenSizes();
        stage.show();

        //FIXME: non funziona il requestFocus()...
        //stage.requestFocus();
    }
}