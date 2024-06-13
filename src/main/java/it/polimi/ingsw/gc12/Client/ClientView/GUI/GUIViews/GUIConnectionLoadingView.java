package it.polimi.ingsw.gc12.Client.ClientView.GUI.GUIViews;

import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.ViewState;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class GUIConnectionLoadingView extends GUIView {

    private static GUIConnectionLoadingView connectionLoadingScreenController = null;

    //static VBox connectionSetupBox = (VBox) sceneRoot.lookup("#connectionSetupBox");
    private final Parent SCENE_ROOT;

    private GUIConnectionLoadingView() {
        try {
            SCENE_ROOT = new FXMLLoader(GUIView.class.getResource("/fxml/connection_loading.fxml")).load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static GUIConnectionLoadingView getInstance() {
        if (connectionLoadingScreenController == null) {
            connectionLoadingScreenController = new GUIConnectionLoadingView();
        }
        return connectionLoadingScreenController;
    }

    @Override
    protected void connectionLoadingScreen() {
        AnchorPane titleScreenPane = (AnchorPane) stage.getScene().getRoot().lookup("#connectionPane");
        VBox connectionSetupBox = (VBox) titleScreenPane.lookup("#connectionSetupBox");

        TextField nicknameField = (TextField) connectionSetupBox.lookup("#nicknameField");
        //Label error = ((Label) stage.getScene().getRoot().lookup("#error"));
        TextField addressField = (TextField) connectionSetupBox.lookup("#addressField");

        if (nicknameField.getText().isEmpty()) {
            //error.setText("Inserire un nickname prima di proseguire");
            return;
        }

        if (addressField.getText().isEmpty()) {
            addressField.setText("localhost");
        }

        stage.getScene().setRoot(SCENE_ROOT);

        Label downloadLabel = (Label) SCENE_ROOT.lookup("#download");
        ProgressIndicator progressIndicator = (ProgressIndicator) SCENE_ROOT.lookup("#progress");
        downloadLabel.setStyle("-fx-font-size: 30");

        downloadLabel.relocate((screenSizes.getX() - downloadLabel.getPrefWidth()) / 2, screenSizes.getY() * 0.45);
        progressIndicator.relocate((screenSizes.getX() - progressIndicator.getPrefWidth()) / 2, screenSizes.getY() * 0.55);

        // Before changing scene, we notify the chosen comm technology to the controller so that it initializes it
        new Thread(() -> ViewState.getCurrentState().connect(
                addressField.getText(),
                ((RadioButton) GUIConnectionSetupView.getInstance().getConnectionChoice().getSelectedToggle()).getText(),
                nicknameField.getText()
        )).start();
    }
}
