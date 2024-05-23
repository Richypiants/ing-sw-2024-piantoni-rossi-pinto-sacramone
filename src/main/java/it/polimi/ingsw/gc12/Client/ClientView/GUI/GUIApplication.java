package it.polimi.ingsw.gc12.Client.ClientView.GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class GUIApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        stage.setFullScreen(true);
        stage.setResizable(false);
        GUIView.stage = stage;

        Screen screen = Screen.getPrimary();
        stage.setWidth(screen.getVisualBounds().getWidth());
        stage.setHeight(screen.getVisualBounds().getHeight());

        stage.setScene(new Scene(new Pane(), stage.getWidth(), stage.getHeight()));

        stage.setTitle("Codex Naturalis");
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
                stage.setResizable(false);
            }
        });

        stage.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            if (event.getCode().equals(KeyCode.F11)) {
                stage.setFullScreen(true);
            }
            //TODO: + show hint per rientrare in fullscreen
        });

        //stage.addEventHandler(MouseDragEvent.MOUSE_DRAGGED, (event) -> stage.setMaximized(true));
    }
}