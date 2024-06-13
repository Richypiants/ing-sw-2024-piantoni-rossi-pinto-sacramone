package it.polimi.ingsw.gc12.Client.ClientView.GUI.GUIViews;

import it.polimi.ingsw.gc12.Client.ClientView.GUI.GUIApplication;
import it.polimi.ingsw.gc12.Client.ClientView.GUI.OverlayPopup;
import it.polimi.ingsw.gc12.Client.ClientView.View;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

//FIXME: consider removing some Platform.runLater() and restricting some of them to necessary actions only
public class GUIView extends View {

    private static GUIView SINGLETON_GUI_INSTANCE = null;

    public static Stage stage;

    static GenericPair<Double, Double> screenSizes;

    static GenericPair<Double, Double> cardSizes = new GenericPair<>(100.0, 66.0);
    static GenericPair<Double, Double> clippedPaneCenter = null;
    static GenericPair<Double, Double> cornerScaleFactor = new GenericPair<>(2.0 / 9, 2.0 / 5);

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
        //TODO: popup con l'exception
        Platform.runLater(() -> {
            String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1; -fx-padding: 10;";

            //Popup error
            Popup errorPopup = new Popup();

            VBox popupErrorBox = new VBox(10);
            popupErrorBox.setAlignment(Pos.CENTER);

            Label error = new Label();
            error.setAlignment(Pos.CENTER);
            error.setTextAlignment(TextAlignment.CENTER);
            // Button okError = new Button("Ok"); // se si ri-aggiunge bottone allora metterlo anche in addAll

            popupErrorBox.getChildren().add(error);
            errorPopup.getContent().addAll(popupErrorBox);

            errorPopup.setHeight(500);
            errorPopup.setWidth(700);
            popupErrorBox.setStyle(style);

            errorPopup.setAutoFix(true);
            errorPopup.setAutoHide(true);
            errorPopup.setHideOnEscape(true);

            error.setText(t.getMessage());
            errorPopup.show(stage);
        });
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
        //TODO: maybe consider deleting this for TUI also?
    }

    @Override
    public void lobbiesScreen() {
        GUILobbiesView.getInstance().lobbiesScreen();
    }

    @Override
    public void quittingScreen() {
        //TODO: implement quitting screen for GUI
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
    public void awaitingScreen() {
        GUIGameView.getInstance().awaitingScreen();
    }

    public static OverlayPopup drawOverlayPopup(Pane popupContent, boolean isCloseable) {
        OverlayPopup overlayPopup = new OverlayPopup();
        //TODO: aggiungere per quanto possibile gli elementi dei popup all'fxml?

        AnchorPane content = new AnchorPane();
        content.setPrefSize(popupContent.getPrefWidth(), popupContent.getPrefHeight());
        content.getChildren().add(popupContent);

        if (isCloseable) {
            Button XButton = new Button("X");
            XButton.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #ff0000; -fx-background-radius: 5px;"); // -fx-padding: 10px 20px;");
            XButton.setOnMouseClicked((event) -> overlayPopup.hide());

            content.getChildren().add(XButton);
            XButton.relocate(content.getPrefWidth() - 50, 20);
        }

        overlayPopup.getContent().add(content);
        overlayPopup.setWidth(7);
        return overlayPopup;
    }

    @Override
    public void showChat() {
        GUIGameView.getInstance().showChat();
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
    public void showLeaderboard(List<Triplet<String, Integer, Integer>> POINTS_STATS, boolean gameEndedDueToDisconnections) {
        GUIGameView.getInstance().showLeaderboard(POINTS_STATS, gameEndedDueToDisconnections);
    }

}