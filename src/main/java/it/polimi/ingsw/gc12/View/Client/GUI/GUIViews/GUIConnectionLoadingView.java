package it.polimi.ingsw.gc12.View.Client.GUI.GUIViews;

import it.polimi.ingsw.gc12.View.Client.ViewStates.ViewState;
import it.polimi.ingsw.gc12.View.GUI.OverlayPopup;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class GUIConnectionLoadingView extends GUIView {

    private static GUIConnectionLoadingView connectionLoadingScreenController = null;

    private final Parent SCENE_ROOT;
    private final Label CONNECTION_LOADING_LABEL;
    private final ProgressIndicator LOADING_PROGRESS_INDICATOR;
    private final VBox RETRY_CONNECTION_PANE;
    private final Label RETRY_CONNECTION_PROMPT_LABEL;
    private final Button YES_BUTTON;
    private final Button NO_BUTTON;

    private GUIConnectionLoadingView() {
        try {
            SCENE_ROOT = new FXMLLoader(GUIView.class.getResource("/Client/fxml/connection_loading.fxml")).load();
        } catch (IOException e) {
            throw new RuntimeException(e); //Should never happen
        }

        CONNECTION_LOADING_LABEL = (Label) SCENE_ROOT.lookup("#connectionLoadingLabel");
        LOADING_PROGRESS_INDICATOR = (ProgressIndicator) SCENE_ROOT.lookup("#loadingProgressIndicator");
        RETRY_CONNECTION_PANE = (VBox) SCENE_ROOT.lookup("#retryConnectionPane");
        RETRY_CONNECTION_PROMPT_LABEL = (Label) SCENE_ROOT.lookup("#retryConnectionPromptLabel");
        YES_BUTTON = (Button) SCENE_ROOT.lookup("#yesButton");
        NO_BUTTON = (Button) SCENE_ROOT.lookup("#noButton");
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

        if (nicknameField.getText().isEmpty() || nicknameField.getText().length() > 10) {
            printError(new IllegalArgumentException("The entered nickname is longer than 10 characters or is empty! Retry..."));
            return;
        }

        if (addressField.getText().isEmpty()) {
            addressField.setText("localhost");
        }

        CONNECTION_LOADING_LABEL.relocate((screenSizes.getX() - CONNECTION_LOADING_LABEL.getPrefWidth()) / 2, screenSizes.getY() * 0.45);
        LOADING_PROGRESS_INDICATOR.relocate((screenSizes.getX() - LOADING_PROGRESS_INDICATOR.getPrefWidth()) / 2, screenSizes.getY() * 0.55);

        stage.getScene().setRoot(SCENE_ROOT);

        // Before changing scene, we notify the chosen comm technology to the controller so that it initializes it
        new Thread(() -> ViewState.getCurrentState().connect(
                addressField.getText(),
                ((RadioButton) GUIConnectionSetupView.getInstance().getConnectionChoice().getSelectedToggle()).getText(),
                nicknameField.getText()
        )).start();
    }

    @Override
    public boolean retryConnectionPrompt(boolean causedByNetworkError) {
        //TODO: restyle popup
        AtomicBoolean wantsToRetry = new AtomicBoolean(true);

        Platform.runLater(() -> {
            RETRY_CONNECTION_PANE.setPrefSize(screenSizes.getX() * 50 / 100, screenSizes.getY() * 50 / 100);

            String promptText = "It seems " + (
                    causedByNetworkError ?
                            "a network error occurred" :
                            "your chosen nickname is already in use"
            ) + ": would you like to retry? (Yes-No):";

            RETRY_CONNECTION_PROMPT_LABEL.setMaxWidth(600);
            RETRY_CONNECTION_PROMPT_LABEL.setMaxWidth(600);
            RETRY_CONNECTION_PROMPT_LABEL.setText(promptText);

            OverlayPopup retryConnectionPopup = drawOverlayPopup(RETRY_CONNECTION_PANE, false);

            YES_BUTTON.setMinWidth(100);
            YES_BUTTON.setOnAction(event -> {
                synchronized (this) {
                    wantsToRetry.set(true);
                    this.notifyAll();
                }
                retryConnectionPopup.hide();
            });

            NO_BUTTON.setMinWidth(100);
            NO_BUTTON.setOnAction(event -> {
                synchronized (this) {
                    wantsToRetry.set(false);
                    this.notifyAll();
                }
                retryConnectionPopup.hide();
            });

            retryConnectionPopup.centerOnScreen();
            retryConnectionPopup.show(stage);
        });

        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e); //Should never happen
            }
        }

        return wantsToRetry.get();
    }
}
