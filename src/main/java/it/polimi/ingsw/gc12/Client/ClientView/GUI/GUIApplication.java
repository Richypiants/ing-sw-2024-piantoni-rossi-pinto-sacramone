package it.polimi.ingsw.gc12.Client.ClientView.GUI;

import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.TitleScreenState;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class GUIApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        ((GUIView) ClientController.getInstance().view).stage = stage;
        ClientController.getInstance().viewState = new TitleScreenState();
        ClientController.getInstance().viewState.executeState();
    }
}