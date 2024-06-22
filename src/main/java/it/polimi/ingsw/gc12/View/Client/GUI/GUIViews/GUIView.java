package it.polimi.ingsw.gc12.View.Client.GUI.GUIViews;

import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import it.polimi.ingsw.gc12.View.Client.GUI.GUIApplication;
import it.polimi.ingsw.gc12.View.Client.GUI.OverlayPopup;
import it.polimi.ingsw.gc12.View.Client.View;
import it.polimi.ingsw.gc12.View.Client.ViewStates.ViewState;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

//FIXME: consider removing some Platform.runLater() and restricting some of them to necessary actions only
public class GUIView extends View {

    private static GUIView SINGLETON_GUI_INSTANCE = null;

    public static Stage stage;

    public static GenericPair<Double, Double> screenSizes;

    protected static GenericPair<Double, Double> cardSizes = new GenericPair<>(100.0, 66.0);
    protected static GenericPair<Double, Double> cornerScaleFactor = new GenericPair<>(2.0 / 9, 2.0 / 5);

    public GUIView() {
    }

    public static GUIView getInstance() {
        if (SINGLETON_GUI_INSTANCE == null) {
            new Thread(() -> Application.launch(GUIApplication.class)).start(); //starting the GUI thread

            GUITitleView.getInstance();
            GUIConnectionSetupView.getInstance();
            GUIConnectionLoadingView.getInstance();
            GUILobbiesView.getInstance();
            GUIGameView.getInstance();

            SINGLETON_GUI_INSTANCE = new GUIView();
        }
        return SINGLETON_GUI_INSTANCE;
    }

    public static void setScreenSizes() {
        screenSizes = new GenericPair<>(stage.getWidth(), stage.getHeight());
    }

    @Override
    public void printError(Throwable t) {
        Platform.runLater(() -> {
            Popup errorPopup = new Popup();

            VBox errorPopupContent = new VBox();

            Label error = new Label("ERROR");

            Label errorLabel = new Label(t.getMessage());
            errorLabel.setPrefWidth(300);
            errorLabel.setWrapText(true);

            errorPopupContent.getChildren().addAll(error, errorLabel);
            errorPopup.getContent().add(errorPopupContent);
            VBox.setMargin(error, new Insets(20, 0, 0, 0));
            VBox.setMargin(errorLabel, new Insets(0, 30, 30, 30));

            errorPopupContent.getStylesheets().add(Objects.requireNonNull(GUIView.class.getResource("/Client/style.css")).toExternalForm());
            errorPopupContent.getStyleClass().add("decoratedPopup");
            error.getStyleClass().add("popupText");
            errorLabel.getStyleClass().add("popupText");

            error.setStyle("-fx-font-size: 18px");
            errorLabel.setStyle("-fx-font-size: 12px");

            errorPopup.setAutoFix(true);
            errorPopup.setAutoHide(true);
            errorPopup.setHideOnEscape(true);
            errorPopup.centerOnScreen();

            errorPopup.show(stage);
        });
    }

    public static OverlayPopup drawOverlayPopup(Pane popupContent, boolean isCloseable) {
        OverlayPopup overlayPopup = new OverlayPopup();

        AnchorPane content = new AnchorPane();
        content.setVisible(false);
        content.setPrefSize(popupContent.getPrefWidth(), popupContent.getPrefHeight());
        content.getChildren().add(popupContent);
        popupContent.setVisible(true);

        if (isCloseable) {
            ImageView closeImage = new ImageView(String.valueOf(GUIGameView.class.getResource("/Client/images/icons/close.png")));
            closeImage.setFitHeight(20);
            closeImage.setPreserveRatio(true);

            Button XButton = new Button();
            XButton.setGraphic(closeImage);
            XButton.setPrefSize(25, 25);
            XButton.setStyle("-fx-border-radius: 5; -fx-border-width: 1px; -fx-border-color: black; -fx-background-color: white");
            XButton.setOnMouseClicked((event) -> overlayPopup.hide());

            content.getChildren().add(XButton);
            XButton.relocate(content.getPrefWidth() - 50, 20);
        }

        overlayPopup.getContent().add(content);
        content.getStylesheets().add(Objects.requireNonNull(GUIView.class.getResource("/Client/style.css")).toExternalForm());

        overlayPopup.setAutoFix(true);
        overlayPopup.centerOnScreen();
        overlayPopup.setHideOnEscape(false);

        overlayPopup.setX((screenSizes.getX() - popupContent.getPrefWidth()) / 2);
        overlayPopup.setY((screenSizes.getY() - popupContent.getPrefHeight()) / 2);

        return overlayPopup;
    }

    protected void connectionLoadingScreen() {
        GUIConnectionLoadingView.getInstance().connectionLoadingScreen();
    }

    @Override
    public void titleScreen() {
        GUITitleView.getInstance().titleScreen();
    }

    @Override
    public void connectionSetupScreen() {
        GUIConnectionSetupView.getInstance().connectionSetupScreen();
    }

    @Override
    public void connectedConfirmation() {
    }

    @Override
    public boolean retryConnectionPrompt(boolean causedByNetworkError) {
        return GUIConnectionLoadingView.getInstance().retryConnectionPrompt(causedByNetworkError);
    }

    @Override
    public void awaitingScreen() {
        GUIGameView.getInstance().awaitingScreen();
    }

    @Override
    public void disconnectedScreen() {
        OverlayPopup.closeLingeringOpenedPopup();

        AtomicReference<OverlayPopup> reconnectingPopup = new AtomicReference<>();

        Platform.runLater(() -> {
            VBox reconnectingPopupContent = new VBox();
            reconnectingPopupContent.getStyleClass().add("decoratedPopup");
            reconnectingPopupContent.setPrefSize(600, 350);

            Label reconnectingLabel = new Label("Connection to server lost: trying to reconnect...");
            reconnectingLabel.getStyleClass().add("popupText");

            Button exitButton = new Button("BACK TO TITLE SCREEN");
            exitButton.getStyleClass().add("rectangularButton");
            exitButton.setMaxWidth(350);

            reconnectingPopupContent.getChildren().addAll(reconnectingLabel, exitButton);

            VBox.setMargin(reconnectingLabel, new Insets(30, 30, 0, 30));
            VBox.setMargin(exitButton, new Insets(0, 0, 30, 0));

            reconnectingPopup.set(drawOverlayPopup(reconnectingPopupContent, false));

            exitButton.setOnMouseClicked((event) -> ViewState.getCurrentState().quit());

            reconnectingPopup.get().centerOnScreen();
            reconnectingPopup.get().show(stage);
            GUIGameView.getInstance().shouldReset = true;
        });

        new Thread(() -> {
            synchronized (CLIENT_CONTROLLER.CLIENT.DISCONNECTED_LOCK) {
                while (CLIENT_CONTROLLER.CLIENT.disconnected)
                    try {
                        CLIENT_CONTROLLER.CLIENT.DISCONNECTED_LOCK.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
            }

            Platform.runLater(() -> reconnectingPopup.get().hide());
        }).start();
    }

    @Override
    public void quittingScreen() {
        OverlayPopup.closeLingeringOpenedPopup();

        Platform.runLater(() -> {
            VBox quittingPopupContent = new VBox();
            quittingPopupContent.getStyleClass().add("decoratedPopup");
            quittingPopupContent.setPrefSize(400, 250);

            Label quittingLabel = new Label("Returning to title screen...");
            quittingLabel.getStyleClass().add("popupText");

            quittingPopupContent.getChildren().add(quittingLabel);

            VBox.setMargin(quittingLabel, new Insets(30, 30, 30, 30));

            OverlayPopup quittingPopup = drawOverlayPopup(quittingPopupContent, false);

            quittingPopup.centerOnScreen();
            quittingPopup.show(stage);
        });
    }

    @Override
    public void lobbiesScreen() {
        GUILobbiesView.getInstance().lobbiesScreen();
    }

    @Override
    public void showNickname() {
        GUILobbiesView.getInstance().showNickname();
    }

    @Override
    public void gameScreen() {
        GUIGameView.getInstance().gameScreen();
    }

    @Override
    public void updateChat() {
        GUIGameView.getInstance().updateChat();
    }

    @Override
    public void showInitialCardsChoice() {
        GUIGameView.getInstance().showInitialCardsChoice();
    }

    @Override
    public void showObjectiveCardsChoice(ArrayList<ClientCard> objectivesSelection) {
        GUIGameView.getInstance().showObjectiveCardsChoice(objectivesSelection);
    }

    @Override
    public void showHand() {
        GUIGameView.getInstance().showHand();
    }

    @Override
    public void showCommonPlacedCards() {
        GUIGameView.getInstance().showCommonPlacedCards();
    }

    @Override
    public void showField(ClientPlayer player) {
        GUIGameView.getInstance().showField(player);
    }

    @Override
    public void moveField(GenericPair<Integer, Integer> centerOffset) {
    }

    @Override
    public void leaderboardScreen(List<Triplet<String, Integer, Integer>> leaderboard, boolean gameEndedDueToDisconnections) {
        GUIGameView.getInstance().leaderboardScreen(leaderboard, gameEndedDueToDisconnections);
    }

}