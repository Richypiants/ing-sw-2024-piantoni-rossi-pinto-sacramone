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

public class GUIView extends View {

    /**
     * The main stage of the GUI, shared among all subclasses of this class to simplify managing
     * the scene and switching the root node at will.
     */
    public static Stage stage;
    /**
     * The size (width and height) of the window in which the stage is rendered.
     * These values are set to the screen's width and height only once when the application is launched,
     * but they may be changed at will to reflect adjustments to the window size if needed.
     */
    public static GenericPair<Double, Double> windowSize;
    /**
     * The singleton instance of the {@code GUIView}.
     */
    private static GUIView SINGLETON_GUI_INSTANCE = null;

    /**
     * Constructs an instance of a GUIView. This method is not public in concordance with the
     * Singleton pattern, but also cannot be private because subclasses need to call it.
     */
    protected GUIView() {
    }

    /**
     * Returns the singleton instance of the {@code GUIView}, also initializing it if it had never been
     * instantiated, as per the Singleton pattern.
     * In the latter case, this method also launches the {@code GUIApplication}.
     *
     * @return The singleton instance
     */
    public static GUIView getInstance() {
        if (SINGLETON_GUI_INSTANCE == null) {
            //Starting the GUI on another thread so that it doesn't block the flow of the current thread.
            new Thread(() -> Application.launch(GUIApplication.class)).start();

            GUITitleView.getInstance();
            GUIConnectionSetupView.getInstance();
            GUIConnectionLoadingView.getInstance();
            GUILobbiesView.getInstance();
            GUIGameView.getInstance();

            SINGLETON_GUI_INSTANCE = new GUIView();
        }
        return SINGLETON_GUI_INSTANCE;
    }

    /**
     * Stores new width and height values for the stage window.
     */
    public static void setWindowSize() {
        windowSize = new GenericPair<>(stage.getWidth(), stage.getHeight());
    }

    /**
     * Generates a new OverlayPopup having the desired graphic layout and the given content.
     *
     * @param popupContent The content to be added to the new popup.
     * @param isCloseable  The boolean specifying if the popup should contain a standard close button or not.
     * @return The OverlayPopup newly created.
     */
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

        overlayPopup.setX((windowSize.getX() - popupContent.getPrefWidth()) / 2);
        overlayPopup.setY((windowSize.getY() - popupContent.getPrefHeight()) / 2);

        return overlayPopup;
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

    /**
     * Shows the connection loading screen on the GUI.
     */
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