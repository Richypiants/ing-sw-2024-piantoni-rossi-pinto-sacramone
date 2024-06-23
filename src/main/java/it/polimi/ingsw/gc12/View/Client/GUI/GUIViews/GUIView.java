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

/**
 * Singleton class representing the Graphical User Interface (GUI) view.
 * It extends the abstract View class and implements various methods for displaying
 * different screens and handling user input in the terminal.
 */
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
     * @param isCloseable  True if the popup should contain a standard close button (allowing it to be
     *                     closed), False otherwise.
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

    /**
     * Shows an error popup containing the message of the given Throwable on the screen.
     *
     * @param error The throwable containing the error message.
     */
    @Override
    public void printError(Throwable error) {
        Platform.runLater(() -> {
            Popup errorPopup = new Popup();

            VBox errorPopupContent = new VBox();

            Label errorLabel = new Label("ERROR");

            Label errorMessageLabel = new Label(error.getMessage());
            errorMessageLabel.setPrefWidth(300);
            errorMessageLabel.setWrapText(true);

            errorPopupContent.getChildren().addAll(errorLabel, errorMessageLabel);
            errorPopup.getContent().add(errorPopupContent);
            VBox.setMargin(errorLabel, new Insets(20, 0, 0, 0));
            VBox.setMargin(errorMessageLabel, new Insets(0, 30, 30, 30));

            errorPopupContent.getStylesheets().add(Objects.requireNonNull(GUIView.class.getResource("/Client/style.css")).toExternalForm());
            errorPopupContent.getStyleClass().add("decoratedPopup");
            errorLabel.getStyleClass().add("popupText");
            errorMessageLabel.getStyleClass().add("popupText");

            errorLabel.setStyle("-fx-font-size: 18px");
            errorMessageLabel.setStyle("-fx-font-size: 12px");

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

    /**
     * Displays the title screen.
     */
    @Override
    public void titleScreen() {
        GUITitleView.getInstance().titleScreen();
    }

    /**
     * Displays the connection setup screen.
     */
    @Override
    public void connectionSetupScreen() {
        GUIConnectionSetupView.getInstance().connectionSetupScreen();
    }


    /**
     * Prompts the user to retry the connection if it failed.
     *
     * @param causedByNetworkError True if the connection failed due to a network error.
     * @return True if the user decides to retry the connection.
     */
    @Override
    public boolean retryConnectionPrompt(boolean causedByNetworkError) {
        return GUIConnectionLoadingView.getInstance().retryConnectionPrompt(causedByNetworkError);
    }

    /**
     * Displays a confirmation message for a successful connection (not needed/implemented for the GUI).
     */
    @Override
    public void connectedConfirmation() {
    }

    /**
     * Displays the disconnected screen and attempts to reconnect.
     */
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

            exitButton.setOnMouseClicked((event) -> {
                ViewState.getCurrentState().quit();
            });

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
                        throw new RuntimeException(e); //Should never happen
                    }
            }

            Platform.runLater(() -> reconnectingPopup.get().hide());
        }).start();
    }

    /**
     * Displays the quitting screen.
     */
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

    /**
     * Displays the lobbies screen.
     */
    @Override
    public void lobbiesScreen() {
        GUILobbiesView.getInstance().lobbiesScreen();
    }

    /**
     * Displays the user's nickname.
     */
    @Override
    public void showNickname() {
        GUILobbiesView.getInstance().showNickname();
    }

    /**
     * Displays the game screen.
     */
    @Override
    public void gameScreen() {
        GUIGameView.getInstance().gameScreen();
    }

    /**
     * Displays the awaiting screen while waiting for other players.
     */
    @Override
    public void awaitingScreen() {
        GUIGameView.getInstance().awaitingScreen();
    }

    /**
     * Updates the chat screen.
     */
    @Override
    public void updateChat() {
        GUIGameView.getInstance().updateChat();
    }

    /**
     * Displays the initial card choice screen.
     */
    @Override
    public void showInitialCardsChoice() {
        GUIGameView.getInstance().showInitialCardsChoice();
    }

    /**
     * Displays the objective card choice screen.
     *
     * @param objectivesSelection List of objective cards to choose from.
     */
    @Override
    public void showObjectiveCardsChoice(ArrayList<ClientCard> objectivesSelection) {
        GUIGameView.getInstance().showObjectiveCardsChoice(objectivesSelection);
    }

    /**
     * Displays the user's hand of cards.
     */
    @Override
    public void showHand() {
        GUIGameView.getInstance().showHand();
    }

    /**
     * Displays the common placed cards on the game board.
     */
    @Override
    public void showCommonPlacedCards() {
        GUIGameView.getInstance().showCommonPlacedCards();
    }

    /**
     * Displays the field of the specified player.
     *
     * @param player The player whose field is to be displayed.
     */
    @Override
    public void showField(ClientPlayer player) {
        GUIGameView.getInstance().showField(player);
    }

    /**
     * Moves the field currently displayed by x cards left and y cards down (not needed/implemented
     * for the GUI, as this is already managed in gameScreen).
     *
     * @param centerOffset The offset by which to move the field view.
     */
    @Override
    public void moveField(GenericPair<Integer, Integer> centerOffset) {
    }

    /**
     * Displays the leaderboard screen with the given points statistics.
     *
     * @param leaderboard                  List of triplets containing player names, scores, and ranks.
     * @param gameEndedDueToDisconnections True if the game ended due to disconnections.
     */
    @Override
    public void leaderboardScreen(List<Triplet<String, Integer, Integer>> leaderboard, boolean gameEndedDueToDisconnections) {
        GUIGameView.getInstance().leaderboardScreen(leaderboard, gameEndedDueToDisconnections);
    }

}