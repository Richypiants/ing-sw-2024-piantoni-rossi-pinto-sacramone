package it.polimi.ingsw.gc12.Client.ClientView.GUI.GUIControllers;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class GUIConnectionLoadingScreen extends GUIView {

    static Parent sceneRoot = sceneRoots.get("waiting_for_connection");

    //static VBox connectionSetupBox = (VBox) sceneRoot.lookup("#connectionSetupBox");

    public static void connectionLoadingScreen() {
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

        stage.getScene().setRoot(sceneRoot);

        Label downloadLabel = (Label) sceneRoot.lookup("#download");
        ProgressIndicator progressIndicator = (ProgressIndicator) sceneRoot.lookup("#progress");
        downloadLabel.setStyle("-fx-font-size: 30");

        downloadLabel.relocate((screenSizes.getX() - downloadLabel.getPrefWidth()) / 2, screenSizes.getY() * 0.45);
        progressIndicator.relocate((screenSizes.getX() - progressIndicator.getPrefWidth()) / 2, screenSizes.getY() * 0.55);

        // Before changing scene, we notify the chosen comm technology to the controller so that it initializes it
        new Thread(() -> ClientController.getInstance().viewState.connect(
                addressField.getText(), ((RadioButton) GUIConnectionSetupController.connection.getSelectedToggle()).getText(), nicknameField.getText())
        ).start();
    }
}
