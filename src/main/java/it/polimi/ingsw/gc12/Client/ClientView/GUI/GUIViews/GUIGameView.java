package it.polimi.ingsw.gc12.Client.ClientView.GUI.GUIViews;

import it.polimi.ingsw.gc12.Client.ClientView.GUI.OverlayPopup;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.ViewState;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class GUIGameView extends GUIView {

    //FIXME: this is probably not the correct MIME type syntax for the data we want to pass...
    private static final DataFormat PLACE_CARD_DATA_FORMAT = new DataFormat("text/genericpair<integer,side>");
    private static GUIGameView gameScreenController = null;
    private final Parent SCENE_ROOT;
    private final AnchorPane OWN_FIELD_PANE;
    private final VBox STATS_BOX;
    private final ScrollPane OWN_FIELD_SCROLL_PANE;
    private final Button ZOOMED_OWN_FIELD_BUTTON;
    private final Button CENTER_OWN_FIELD_BUTTON;
    private final Button SHOW_SCOREBOARD_BUTTON;
    private final Button LEAVE_BUTTON;
    private final Button CHAT_BUTTON;
    private final AnchorPane SCOREBOARD_PANE;
    private final Button HIDE_SCOREBOARD_BUTTON;
    private final AnchorPane CHAT_PANE;
    private final ScrollPane CHAT_SCROLL_PANE;
    private final VBox MESSAGES_BOX;
    private final ComboBox<String> RECEIVER_NICKNAME_SELECTOR;
    private final TextField MESSAGE_TEXTFIELD;
    private final Button HIDE_CHAT_BUTTON;
    private final Button SEND_MESSAGE_BUTTON;
    private final HBox OPPONENTS_FIELDS_PANE;
    private final HBox OWN_HAND_PANE;
    private final AnchorPane DECK_AND_VISIBLE_CARDS_PANE;
    private final HBox RESOURCE_HBOX;
    private final HBox GOLD_HBOX;
    private final HBox OBJECTIVE_HBOX;
    private final VBox AWAITING_STATE_MESSAGE_BOX;
    private final VBox LEADERBOARD_VBOX;
    private final Label LEADERBOARD_LABEL;
    private final Label WINNING_PLAYER_LABEL;

    private GUIGameView() {
        try {
            SCENE_ROOT = new FXMLLoader(GUIView.class.getResource("/fxml/game_screen.fxml")).load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        OWN_FIELD_PANE = (AnchorPane) SCENE_ROOT.lookup("#ownFieldPane");
        STATS_BOX = (VBox) SCENE_ROOT.lookup("#statsBox");
        OWN_FIELD_SCROLL_PANE = (ScrollPane) OWN_FIELD_PANE.lookup("#ownFieldScrollPane");
        ZOOMED_OWN_FIELD_BUTTON = (Button) OWN_FIELD_PANE.lookup("#zoomedOwnFieldButton");
        CENTER_OWN_FIELD_BUTTON = (Button) OWN_FIELD_PANE.lookup("#centerOwnFieldButton");
        SHOW_SCOREBOARD_BUTTON = (Button) SCENE_ROOT.lookup("#scoreboardButton");
        LEAVE_BUTTON = (Button) SCENE_ROOT.lookup("#leaveButton");
        CHAT_BUTTON = (Button) SCENE_ROOT.lookup("#chatButton");
        SCOREBOARD_PANE = (AnchorPane) SCENE_ROOT.lookup("#scoreboardPane");
        HIDE_SCOREBOARD_BUTTON = (Button) SCOREBOARD_PANE.lookup("#hideScoreboardButton");
        CHAT_PANE = (AnchorPane) SCENE_ROOT.lookup("#chatPane");
        CHAT_SCROLL_PANE = (ScrollPane) SCENE_ROOT.lookup("#chatScrollPane");
        MESSAGES_BOX = (VBox) CHAT_SCROLL_PANE.lookup("#messagesBox");
        RECEIVER_NICKNAME_SELECTOR = (ComboBox<String>) CHAT_PANE.lookup("#receiverSelector");
        MESSAGE_TEXTFIELD = (TextField) CHAT_PANE.lookup("#messageText");
        HIDE_CHAT_BUTTON = (Button) CHAT_PANE.lookup("#hideChatButton");
        SEND_MESSAGE_BUTTON = (Button) CHAT_PANE.lookup("#sendButton");
        OPPONENTS_FIELDS_PANE = (HBox) SCENE_ROOT.lookup("#opponentsFieldsPane");
        OWN_HAND_PANE = (HBox) SCENE_ROOT.lookup("#handPane");
        DECK_AND_VISIBLE_CARDS_PANE = (AnchorPane) SCENE_ROOT.lookup("#deckAndVisiblePane");
        RESOURCE_HBOX = (HBox) DECK_AND_VISIBLE_CARDS_PANE.lookup("#resourceHBox");
        GOLD_HBOX = (HBox) DECK_AND_VISIBLE_CARDS_PANE.lookup("#goldHBox");
        OBJECTIVE_HBOX = (HBox) DECK_AND_VISIBLE_CARDS_PANE.lookup("#objectiveHBox");
        AWAITING_STATE_MESSAGE_BOX = (VBox) SCENE_ROOT.lookup("#awaitingStateMessageBox");
        LEADERBOARD_VBOX = (VBox) SCENE_ROOT.lookup("#leaderboardVBox");
        LEADERBOARD_LABEL = (Label) SCENE_ROOT.lookup("#leaderboardLabel");
        WINNING_PLAYER_LABEL = (Label) SCENE_ROOT.lookup("#winningPlayerLabel");
    }

    public static GUIGameView getInstance() {
        if (gameScreenController == null) {
            gameScreenController = new GUIGameView();
        }
        return gameScreenController;
    }

    @Override
    public void gameScreen() {
        Platform.runLater(() -> {
            stage.getScene().setRoot(SCENE_ROOT);

            ClientPlayer thisPlayer = ClientController.getInstance().VIEWMODEL.getCurrentGame().getThisPlayer();

            GUIView.getInstance().showHand();
            GUIView.getInstance().showCommonPlacedCards();

            //TODO: estrarre in una funzione
            OWN_FIELD_PANE.setPrefSize(screenSizes.getX() * 75 / 100, screenSizes.getY() * 50 / 100);
            OWN_FIELD_PANE.relocate(screenSizes.getX() * 25 / 100, screenSizes.getY() * 35 / 100);

            STATS_BOX.getChildren().clear();

            STATS_BOX.setPrefSize(OWN_FIELD_PANE.getPrefWidth() / 10, OWN_FIELD_PANE.getPrefHeight());
            for (var resourceEntry : thisPlayer.getOwnedResources().entrySet()) {
                //TODO: dopo aggiungeremo le immaginette o le icone o le emoji che al momento non abbiamo
                HBox oneData = new HBox(5);
                ImageView image = new ImageView(String.valueOf(GUIView.class.getResource("/images/icons/res/" + resourceEntry.getKey().SYMBOL.toLowerCase() + ".png")));
                image.setFitWidth(50);
                image.setPreserveRatio(true);
                Label resourceInfo = new Label("" + resourceEntry.getValue());
                resourceInfo.setPrefSize(STATS_BOX.getPrefWidth(), STATS_BOX.getPrefHeight());
                resourceInfo.setStyle("-fx-font-size: 15px; -fx-font-family: 'Bell MT'; -fx-background-color: #f0f0f0; -fx-border-color: #D50A0AFF; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
                oneData.getChildren().addAll(image, resourceInfo);
                STATS_BOX.getChildren().add(oneData);
//                Label resourceInfo = new Label(resourceEntry.getKey().SYMBOL + " x " + resourceEntry.getValue());
//                resourceInfo.setAlignment(Pos.CENTER);
//                resourceInfo.setPrefSize(STATS_BOX.getPrefWidth(), STATS_BOX.getPrefHeight());
//                resourceInfo.setStyle("-fx-font-size: 15px; -fx-font-family: 'Bell MT'; -fx-background-color: #f0f0f0; -fx-border-color: #D50A0AFF; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
//                STATS_BOX.getChildren().add(resourceInfo);
            }

            OWN_FIELD_SCROLL_PANE.setPannable(true);
            OWN_FIELD_SCROLL_PANE.setPrefSize(OWN_FIELD_PANE.getPrefWidth() * 9 / 10, OWN_FIELD_PANE.getPrefHeight());
            drawField(OWN_FIELD_SCROLL_PANE, thisPlayer, true);

            ZOOMED_OWN_FIELD_BUTTON.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #ff0000; -fx-background-radius: 5px;"); // -fx-padding: 10px 20px;");
            ZOOMED_OWN_FIELD_BUTTON.setOnMouseClicked((event) -> GUIView.getInstance().showField(thisPlayer));

            CENTER_OWN_FIELD_BUTTON.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #ff0000; -fx-background-radius: 5px;"); // -fx-padding: 10px 20px;");
            CENTER_OWN_FIELD_BUTTON.setOnMouseClicked((event) -> {
                OWN_FIELD_SCROLL_PANE.setHvalue((OWN_FIELD_SCROLL_PANE.getHmax() + OWN_FIELD_SCROLL_PANE.getHmin()) / 2);
                OWN_FIELD_SCROLL_PANE.setVvalue((OWN_FIELD_SCROLL_PANE.getVmax() + OWN_FIELD_SCROLL_PANE.getVmin()) / 2);
            });

            OWN_FIELD_SCROLL_PANE.relocate(OWN_FIELD_PANE.getPrefWidth() / 10, 0);
            ZOOMED_OWN_FIELD_BUTTON.relocate(OWN_FIELD_PANE.getPrefWidth() - 50, 20);
            CENTER_OWN_FIELD_BUTTON.relocate(OWN_FIELD_PANE.getPrefWidth() - 50, 60);
            showOpponentsFieldsMiniaturized();

            ImageView scoreboardImage = new ImageView(String.valueOf(GUIGameView.class.getResource("/images/icons/scoreboard.png")));
            scoreboardImage.setFitHeight(30);
            scoreboardImage.setPreserveRatio(true);
            SHOW_SCOREBOARD_BUTTON.setGraphic(scoreboardImage);
            SHOW_SCOREBOARD_BUTTON.setOnMouseClicked((event) -> showScoreboard());
            SHOW_SCOREBOARD_BUTTON.relocate(screenSizes.getX() * 2 / 100, screenSizes.getY() * 90 / 100);
            SHOW_SCOREBOARD_BUTTON.toFront();

            ImageView leaveImage = new ImageView(String.valueOf(GUIGameView.class.getResource("/images/icons/leaveGame.png")));
            leaveImage.setFitHeight(30);
            leaveImage.setPreserveRatio(true);
            LEAVE_BUTTON.setGraphic(leaveImage);
            LEAVE_BUTTON.setOnMouseClicked((event) -> ViewState.getCurrentState().quit());
            LEAVE_BUTTON.relocate(screenSizes.getX() * 95 / 100, screenSizes.getY() * 90 / 100);
            LEAVE_BUTTON.toFront();

            ImageView chatImage = new ImageView(String.valueOf(GUIGameView.class.getResource("/images/icons/chat.png")));
            chatImage.setFitHeight(30);
            chatImage.setPreserveRatio(true);
            CHAT_BUTTON.setGraphic(chatImage);
            CHAT_BUTTON.setOnMouseClicked((event) -> GUIView.getInstance().showChat()); //FIXME: make toggleChat()?
            CHAT_BUTTON.relocate(screenSizes.getX() * 90 / 100, screenSizes.getY() * 90 / 100);
            CHAT_BUTTON.toFront();
        });
    }

    @Override
    public void awaitingScreen() {
        Platform.runLater(() -> {
            OverlayPopup awaitingStatePopup = drawOverlayPopup(AWAITING_STATE_MESSAGE_BOX, false);
            awaitingStatePopup.setAutoFix(true);

            awaitingStatePopup.show(stage);
            AWAITING_STATE_MESSAGE_BOX.setVisible(true);
            awaitingStatePopup.centerOnScreen();
        });
    }

    private void showScoreboard() {
        Platform.runLater(() -> {
            //TODO: CAMBIARE LE DIMENSIONI SOLO PREVIA COMUNICAZIONE A SACRAMONE
            SCOREBOARD_PANE.setPrefSize(screenSizes.getX() * 0.1328125, screenSizes.getY() * 0.5);
            // System.out.println("x: " + screenSizes.getX() + " y: " + screenSizes.getY());
            // System.out.println("\n" + "x: " + scoreboardPane.getPrefWidth() + " y: " + scoreboardPane.getPrefHeight());
            SCOREBOARD_PANE.setStyle("-fx-background-image: url('/images/scoreboard.png'); -fx-background-size: stretch;");

            HIDE_SCOREBOARD_BUTTON.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #ff0000; -fx-background-radius: 5px;"); // -fx-padding: 10px 20px;");
            HIDE_SCOREBOARD_BUTTON.setOnMouseClicked((event) -> SCOREBOARD_PANE.setVisible(false));

            HIDE_SCOREBOARD_BUTTON.relocate(SCOREBOARD_PANE.getPrefWidth(), 0);

            //TODO: ???????
            AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
            AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);

            SCOREBOARD_PANE.setOnMousePressed((event) -> {
                xOffset.set(SCOREBOARD_PANE.getLayoutX() - event.getScreenX());
                yOffset.set(SCOREBOARD_PANE.getLayoutY() - event.getScreenY());
                SCOREBOARD_PANE.toFront();
            });

            SCOREBOARD_PANE.setOnMouseDragged((event) -> {
                SCOREBOARD_PANE.relocate(event.getScreenX() + xOffset.get(), event.getScreenY() + yOffset.get());
            });

            //TODO: Find correct coordinates of each point cell
            ArrayList<GenericPair<Double, Double>> relativeOffsetScaleFactors = new ArrayList<>();

            //TODO: CAMBIARE LE COORDINATE SOLO PREVIA COMUNICAZIONE A SACRAMONE
            relativeOffsetScaleFactors.add(new GenericPair<>(0.187, 0.8918)); // 0
            relativeOffsetScaleFactors.add(new GenericPair<>(0.422, 0.8915)); // 1
            relativeOffsetScaleFactors.add(new GenericPair<>(0.658, 0.8915)); // 2
            relativeOffsetScaleFactors.add(new GenericPair<>(0.775, 0.783)); // 3
            relativeOffsetScaleFactors.add(new GenericPair<>(0.54, 0.783)); // 4
            relativeOffsetScaleFactors.add(new GenericPair<>(0.305, 0.783)); // 5
            relativeOffsetScaleFactors.add(new GenericPair<>(0.069, 0.783)); // 6
            relativeOffsetScaleFactors.add(new GenericPair<>(0.069, 0.6755)); // 7
            relativeOffsetScaleFactors.add(new GenericPair<>(0.303, 0.6755)); // 8
            relativeOffsetScaleFactors.add(new GenericPair<>(0.54, 0.6755)); // 9
            relativeOffsetScaleFactors.add(new GenericPair<>(0.775, 0.6755)); // 10
            relativeOffsetScaleFactors.add(new GenericPair<>(0.775, 0.568)); // 11
            relativeOffsetScaleFactors.add(new GenericPair<>(0.54, 0.568)); // 12
            relativeOffsetScaleFactors.add(new GenericPair<>(0.303, 0.568)); // 13
            relativeOffsetScaleFactors.add(new GenericPair<>(0.069, 0.568)); // 14
            relativeOffsetScaleFactors.add(new GenericPair<>(0.069, 0.4605)); // 15
            relativeOffsetScaleFactors.add(new GenericPair<>(0.303, 0.4605)); // 16
            relativeOffsetScaleFactors.add(new GenericPair<>(0.54, 0.4605)); // 17
            relativeOffsetScaleFactors.add(new GenericPair<>(0.775, 0.4605)); // 18
            relativeOffsetScaleFactors.add(new GenericPair<>(0.775, 0.353)); // 19
            relativeOffsetScaleFactors.add(new GenericPair<>(0.422, 0.30)); // 20
            relativeOffsetScaleFactors.add(new GenericPair<>(0.069, 0.353)); // 21
            relativeOffsetScaleFactors.add(new GenericPair<>(0.069, 0.245)); // 22
            relativeOffsetScaleFactors.add(new GenericPair<>(0.069, 0.1375)); // 23
            relativeOffsetScaleFactors.add(new GenericPair<>(0.205, 0.05)); // 24
            relativeOffsetScaleFactors.add(new GenericPair<>(0.422, 0.03)); // 25
            relativeOffsetScaleFactors.add(new GenericPair<>(0.639, 0.05)); // 26
            relativeOffsetScaleFactors.add(new GenericPair<>(0.775, 0.1375)); // 27
            relativeOffsetScaleFactors.add(new GenericPair<>(0.775, 0.245)); // 28
            relativeOffsetScaleFactors.add(new GenericPair<>(0.422, 0.1623)); // 29


            ClientGame thisGame = ClientController.getInstance().VIEWMODEL.getCurrentGame();

            SCOREBOARD_PANE.getChildren().clear();

            for (var player : thisGame.getPlayers().stream().sorted(Comparator.comparingInt(ClientPlayer::getPoints)).toList().reversed()) {
                GenericPair<Double, Double> scaleFactor = relativeOffsetScaleFactors.get(player.getPoints());
                ImageView token = new ImageView(String.valueOf(GUIView.class.getResource("/images/misc/" + player.getColor().name().toLowerCase() + ".png")));
                token.setFitHeight(SCOREBOARD_PANE.getPrefWidth() * 0.16);
                token.setPreserveRatio(true);

                SCOREBOARD_PANE.getChildren().add(token);
                token.relocate(SCOREBOARD_PANE.getPrefWidth() * scaleFactor.getX(),
                        SCOREBOARD_PANE.getPrefHeight() * scaleFactor.getY());

                //TODO: Sovrapporre pedine se punteggi uguali
            }

            /*
            GenericPair<Double, Double> scaleFactorCircle = relativeOffsetScaleFactors.get(0);
            Circle circle = new Circle();
            circle.setRadius((230 * scoreboardPane.getPrefWidth() / 1575) / 2);
            circle.setFill(Color.RED);

            scoreboardPane.getChildren().add(circle);
            circle.relocate(scoreboardPane.getPrefWidth() * scaleFactorCircle.getX(),
                    scoreboardPane.getPrefHeight() * scaleFactorCircle.getY());
             */

            //FIXME: we need to clear all the previous token, but this also clears the hideScoreboardButton...
            //scoreboardPane.getChildren().clear();

            /* for (int i = 0; i < 30; i++) {
                GenericPair<Double, Double> scaleFactor = relativeOffsetScaleFactors.get(i);
                ImageView token = new ImageView(String.valueOf(GUIView.class.getResource("/images/misc/red.png")));
                token.setFitWidth(SCOREBOARD_PANE.getPrefWidth() * 0.16);
                token.setPreserveRatio(true);

                SCOREBOARD_PANE.getChildren().add(token);
                token.relocate(SCOREBOARD_PANE.getPrefWidth() * scaleFactor.getX(),
                        SCOREBOARD_PANE.getPrefHeight() * scaleFactor.getY());
            }
            */


            SCOREBOARD_PANE.setVisible(true);
            SCOREBOARD_PANE.toFront();
        });
    }


    //TODO: eventually remove boolean from signature?
    private void drawField(ScrollPane fieldPane, ClientPlayer player, boolean isInteractive) {
        fieldPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        fieldPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        //TODO: add background color or texture or image?
        //TODO: resize this pane based on how many cards have been played and center the field on it? or not?
        double clippedPaneWidth = 4000;
        double clippedPaneHeight = 4000 * cardSizes.getY() / cardSizes.getX();
        AnchorPane clippedPane = new AnchorPane();
        clippedPane.setPrefSize(clippedPaneWidth, clippedPaneHeight);
        clippedPane.setCenterShape(true);

        clippedPaneCenter = new GenericPair<>(clippedPaneWidth / 2, clippedPaneHeight / 2);
        for (var cardEntry : player.getPlacedCards().sequencedEntrySet()) {
            //TODO: maybe zoom cards here in this popup?
            ImageView cardImage = new ImageView(String.valueOf(GUIView.class.getResource(cardEntry.getValue().getX().GUI_SPRITES.get(cardEntry.getValue().getY()))));

            //FIXME: correct this: it is needed to get this later, but which size?
            // or maybe later when needed use cardSizes like this, after having decided if values are correct
            cardImage.setFitHeight(cardSizes.getY());
            cardImage.setFitWidth(cardSizes.getX());
            cardImage.setPreserveRatio(true);

            clippedPane.getChildren().add(cardImage);

            cardImage.relocate(
                    clippedPaneCenter.getX() - cardImage.getFitWidth() / 2 + cardImage.getFitWidth() * (1 - cornerScaleFactor.getX()) * cardEntry.getKey().getX(),
                    clippedPaneCenter.getY() - cardImage.getFitHeight() / 2 - cardImage.getFitHeight() * (1 - cornerScaleFactor.getY()) * cardEntry.getKey().getY()
            );
        }

        for (var openCorner : player.getOpenCorners()) {
            var openCornerShape = new Rectangle(cardSizes.getX(), cardSizes.getY()) {
                public final GenericPair<Integer, Integer> COORDINATES = openCorner;
            };
            openCornerShape.setFill(Color.TRANSPARENT);
            openCornerShape.setStyle("-fx-stroke: gray; -fx-stroke-width: 1; -fx-stroke-dash-array: 4 8;");
            openCornerShape.setArcWidth(10);
            openCornerShape.setArcHeight(10);

            if (isInteractive) {
                openCornerShape.setOnDragOver((event) -> {
                    //TODO: implement visual effects
                    if (event.getGestureSource() != openCornerShape && event.getDragboard().hasContent(PLACE_CARD_DATA_FORMAT)) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                });

                openCornerShape.setOnDragDropped((event) -> {
                    if (event.getTransferMode() == TransferMode.MOVE) {
                        GenericPair<Integer, Side> placeCardData = (GenericPair<Integer, Side>) event.getDragboard().getContent(PLACE_CARD_DATA_FORMAT);
                        ViewState.getCurrentState().placeCard(
                                openCornerShape.COORDINATES,
                                placeCardData.getX(),
                                placeCardData.getY()
                        );
                        event.setDropCompleted(event.getDragboard().hasContent(PLACE_CARD_DATA_FORMAT));
                    }
                });
            }

            clippedPane.getChildren().add(openCornerShape);

            openCornerShape.relocate(
                    clippedPaneCenter.getX() - cardSizes.getX() / 2 + cardSizes.getX() * (1 - cornerScaleFactor.getX()) * openCorner.getX(),
                    clippedPaneCenter.getY() - cardSizes.getY() / 2 - cardSizes.getY() * (1 - cornerScaleFactor.getY()) * openCorner.getY()
            );
        }

        fieldPane.setContent(clippedPane);
        fieldPane.setHvalue((fieldPane.getHmax() + fieldPane.getHmin()) / 2);
        fieldPane.setVvalue((fieldPane.getVmax() + fieldPane.getVmin()) / 2);
    }

    @Override
    public void showChat() {
        Platform.runLater(() -> {
            //TODO: maybe perform chatPane initialization only at the start of a game instead of everytime?
            CHAT_PANE.setPrefSize(screenSizes.getX() * 30 / 100, screenSizes.getY() * 70 / 100);

            //TODO: ???????
            AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
            AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);

            CHAT_PANE.setOnMousePressed((event) -> {
                xOffset.set(CHAT_PANE.getLayoutX() - event.getScreenX());
                yOffset.set(CHAT_PANE.getLayoutY() - event.getScreenY());
                CHAT_PANE.toFront();
            });

            CHAT_PANE.setOnMouseDragged((event) -> {
                CHAT_PANE.relocate(event.getScreenX() + xOffset.get(), event.getScreenY() + yOffset.get());
            });

            ClientGame thisGame = ClientController.getInstance().VIEWMODEL.getCurrentGame();

            MESSAGES_BOX.getChildren().clear();

            //TODO: invece di ricrearlo ogni volta, salvarlo e updatarlo?
            for (var message : thisGame.getChatLog()) {
                MESSAGES_BOX.getChildren().add(createMessageElement(message));
            }

            CHAT_SCROLL_PANE.setVvalue(CHAT_SCROLL_PANE.getVmax());

            List<String> nicknames = thisGame.getPlayers().stream().map(Player::getNickname).collect(Collectors.toCollection(ArrayList::new));
            nicknames.addFirst("everyone");
            nicknames.remove(ClientController.getInstance().VIEWMODEL.getOwnNickname());
            ObservableList<String> receiverNicknames = FXCollections.observableList(nicknames);
            RECEIVER_NICKNAME_SELECTOR.setItems(receiverNicknames);
            RECEIVER_NICKNAME_SELECTOR.getSelectionModel().selectFirst();

            HIDE_CHAT_BUTTON.setOnMouseClicked((event) -> CHAT_PANE.setVisible(false));

            SEND_MESSAGE_BUTTON.setOnMouseClicked((event) -> {
                String receiver = RECEIVER_NICKNAME_SELECTOR.getValue();
                String message = MESSAGE_TEXTFIELD.getText().trim();

                //FIXME: aggiungere lunghezza massima e substring anche qui? Magari aggiungerle ma più di 150 chars?
                if (receiver.equals("everyone"))
                    ViewState.getCurrentState().broadcastMessage(message);
                else
                    ViewState.getCurrentState().directMessage(receiver, message);
            });

            CHAT_PANE.setVisible(true);
            CHAT_PANE.toFront();
        });
    }

    //TODO: centrare l'opponentField sulla carta nuova appena piazzata?
    private void showOpponentsFieldsMiniaturized() {
        OPPONENTS_FIELDS_PANE.setPrefSize(screenSizes.getX(), screenSizes.getY() * 35 / 100);
        OPPONENTS_FIELDS_PANE.relocate(0, 0);

        ClientGame thisGame = ClientController.getInstance().VIEWMODEL.getCurrentGame();

        String style = "-fx-font-size: 15px; -fx-font-family: 'Bell MT'; -fx-background-color: #f0f0f0; -fx-border-color: #D50A0AFF; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px;";

        OPPONENTS_FIELDS_PANE.getChildren().clear();

        for (var player : thisGame.getPlayers().stream()
                .filter((player) -> !(player.getNickname().equals(ClientController.getInstance().VIEWMODEL.getOwnNickname()))).toList()) {
            VBox opponentInfo = new VBox(5);
            opponentInfo.setAlignment(Pos.CENTER);
            opponentInfo.setPrefSize(OPPONENTS_FIELDS_PANE.getPrefWidth() / (thisGame.getPlayersNumber() - 1), OPPONENTS_FIELDS_PANE.getPrefHeight());

            Label opponentName = new Label(player.getNickname());
            opponentName.setAlignment(Pos.CENTER);
            opponentName.setPrefSize(opponentInfo.getPrefWidth(), opponentInfo.getPrefHeight() / 10);
            opponentName.setStyle(style);

            HBox opponentData = new HBox(0);
            opponentData.setAlignment(Pos.CENTER);
            opponentData.setPrefSize(opponentInfo.getPrefWidth(), opponentInfo.getPrefHeight() * 9 / 10);

            VBox opponentStats = new VBox(0);
            opponentStats.setAlignment(Pos.CENTER);
            opponentStats.setPrefSize(opponentData.getPrefWidth() / 10, opponentData.getPrefHeight());
            for (var resourceEntry : player.getOwnedResources().entrySet()) {
                HBox oneData = new HBox(5);
                ImageView image = new ImageView(String.valueOf(GUIView.class.getResource("/images/icons/res/" + resourceEntry.getKey().SYMBOL.toLowerCase() + ".png")));
                image.setFitWidth(30);
                image.setPreserveRatio(true);
                Label resourceInfo = new Label("" + resourceEntry.getValue());
                resourceInfo.setPrefSize(opponentStats.getPrefWidth(), opponentStats.getPrefHeight());
                resourceInfo.setStyle(style);
                oneData.getChildren().addAll(image, resourceInfo);
                opponentStats.getChildren().add(oneData);
            }

            AnchorPane opponentField = new AnchorPane();
            opponentField.setPrefSize(opponentData.getPrefWidth() * 9 / 10, opponentData.getPrefHeight());

            ScrollPane opponentScrollField = new ScrollPane();
            opponentScrollField.setPrefSize(opponentField.getPrefWidth(), opponentField.getPrefHeight());
            opponentScrollField.setPannable(true);
            drawField(opponentScrollField, player, false);

            Button zoomedOwnFieldButton = new Button("[]");
            zoomedOwnFieldButton.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #ff0000; -fx-background-radius: 5px;"); // -fx-padding: 10px 20px;");
            zoomedOwnFieldButton.setOnMouseClicked((event) -> showField(player));

            Button centerFieldButton = new Button("+");
            centerFieldButton.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #ff0000; -fx-background-radius: 5px;"); // -fx-padding: 10px 20px;");
            centerFieldButton.setOnMouseClicked((event) -> {
                opponentScrollField.setHvalue((opponentScrollField.getHmax() + opponentScrollField.getHmin()) / 2);
                opponentScrollField.setVvalue((opponentScrollField.getVmax() + opponentScrollField.getVmin()) / 2);
            });

            opponentField.getChildren().addAll(opponentScrollField, zoomedOwnFieldButton, centerFieldButton);
            zoomedOwnFieldButton.relocate(opponentField.getPrefWidth() - 50, 20);
            centerFieldButton.relocate(opponentField.getPrefWidth() - 50, 60);

            opponentData.getChildren().addAll(opponentStats, opponentField);
            opponentInfo.getChildren().addAll(opponentName, opponentData);
            OPPONENTS_FIELDS_PANE.getChildren().add(opponentInfo);
        }
    }

    @Override
    public void showInitialCardsChoice() {
        Platform.runLater(() -> {
            double popupWidth = 720, popupHeight = 480;
            String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2; -fx-padding: 10;";

            VBox initialCardsChoiceVBox = new VBox(30);
            initialCardsChoiceVBox.setAlignment(Pos.CENTER);
            initialCardsChoiceVBox.setPrefSize(popupWidth, popupHeight);
            initialCardsChoiceVBox.setStyle(style);
            Label cardLabel = new Label("Seleziona la carta iniziale: ");

            HBox initialChoiceHBox = new HBox(30);
            initialChoiceHBox.setAlignment(Pos.CENTER);
            initialChoiceHBox.setPrefSize(initialCardsChoiceVBox.getPrefWidth(), initialCardsChoiceVBox.getPrefHeight() * 0.9);

            ClientCard initialCard = ClientController.getInstance().VIEWMODEL.getCurrentGame().getCardsInHand().getFirst();

            ImageView frontCardView = new ImageView(String.valueOf(GUIView.class.getResource(initialCard.GUI_SPRITES.get(Side.FRONT))));
            ImageView backCardView = new ImageView(String.valueOf(GUIView.class.getResource(initialCard.GUI_SPRITES.get(Side.BACK))));

            frontCardView.setFitWidth(initialChoiceHBox.getPrefWidth() * 0.3);
            frontCardView.setPreserveRatio(true);
            backCardView.setFitWidth(initialChoiceHBox.getPrefWidth() * 0.3);
            backCardView.setPreserveRatio(true);

            initialChoiceHBox.getChildren().addAll(frontCardView, backCardView);
            initialCardsChoiceVBox.getChildren().addAll(cardLabel, initialChoiceHBox/*, aggiungere scritta "Scegli carta iniziale: "*/);

            OverlayPopup createdPopup = drawOverlayPopup(initialCardsChoiceVBox, false);

            frontCardView.setOnMouseClicked((event) -> {
                ViewState.getCurrentState().placeCard(new GenericPair<>(0, 0), 1, Side.FRONT);
                createdPopup.hide();
            });

            backCardView.setOnMouseClicked((event) -> {
                ViewState.getCurrentState().placeCard(new GenericPair<>(0, 0), 1, Side.BACK);
                createdPopup.hide();
            });

            createdPopup.show(stage);
        });
    }

    @Override
    public void showObjectiveCardsChoice(ArrayList<ClientCard> objectivesSelection) {
        Platform.runLater(() -> {
            double popupWidth = 720, popupHeight = 480;
            String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2; -fx-padding: 10;";

            VBox objectiveChoiceVBox = new VBox(30);
            objectiveChoiceVBox.setAlignment(Pos.CENTER);
            objectiveChoiceVBox.setPrefSize(popupWidth, popupHeight);
            objectiveChoiceVBox.setStyle(style);
            Label cardLabel = new Label("Seleziona la carta obiettivo segreto: ");

            HBox objectiveChoiceHBox = new HBox(30);
            objectiveChoiceHBox.setAlignment(Pos.CENTER);
            objectiveChoiceHBox.setPrefSize(objectiveChoiceVBox.getPrefWidth(), objectiveChoiceVBox.getPrefHeight() * 0.9);

            OverlayPopup createdPopup = drawOverlayPopup(objectiveChoiceVBox, false);

            for (int i = 0; i < objectivesSelection.size(); i++) {
                ClientCard objectiveCard = objectivesSelection.get(i);
                ImageView objectiveCardView = new ImageView(String.valueOf(GUIView.class.getResource(objectiveCard.GUI_SPRITES.get(Side.FRONT))));

                objectiveCardView.setFitWidth(objectiveChoiceHBox.getPrefWidth() * 0.3);
                objectiveCardView.setPreserveRatio(true);

                int cardPosition = i;
                objectiveCardView.setOnMouseClicked((event) -> {
                    ViewState.getCurrentState().pickObjective(cardPosition + 1);
                    createdPopup.hide();
                });

                objectiveChoiceHBox.getChildren().add(objectiveCardView);
            }

            objectiveChoiceVBox.getChildren().addAll(cardLabel, objectiveChoiceHBox/*, aggiungere scritta "Scegli carta iniziale: "*/);

            createdPopup.show(stage);
        });
    }

    @Override
    public void showHand() {
        Platform.runLater(() -> {
            OWN_HAND_PANE.setPrefSize(screenSizes.getX() * 50 / 100, screenSizes.getY() * 15 / 100);
            OWN_HAND_PANE.relocate(screenSizes.getX() * 40 / 100, screenSizes.getY() * 85 / 100);
            OWN_HAND_PANE.setAlignment(Pos.CENTER);

            double handPaneHeight = OWN_HAND_PANE.getPrefHeight();
            double handPaneWidth = OWN_HAND_PANE.getPrefWidth();
            List<ClientCard> cardsInHand = ClientController.getInstance().VIEWMODEL.getCurrentGame().getCardsInHand();

            OWN_HAND_PANE.getChildren().clear();

            for (int i = 0; i < cardsInHand.size(); i++) {
                ClientCard card = cardsInHand.get(i);
                AnchorPane pane = new AnchorPane();

                pane.setPrefSize(handPaneWidth / 3, handPaneHeight);  //FIXME: Diviso 3? (cardInHand.size()?)
                // pane.setStyle("-fx-background-color: darkorange;");

                //FIXME: mappa anche per gli sprite GUI come sprite TUI? altrimenti card.XXX_SPRITE
                ImageView frontCardView = new ImageView(String.valueOf(GUIView.class.getResource(card.GUI_SPRITES.get(Side.FRONT))));
                ImageView backCardView = new ImageView(String.valueOf(GUIView.class.getResource(card.GUI_SPRITES.get(Side.BACK))));

                pane.getChildren().addAll(backCardView, frontCardView);
                backCardView.toBack();

                frontCardView.setFitWidth(pane.getPrefWidth() * 0.7);
                frontCardView.setPreserveRatio(true);

                backCardView.setFitWidth(pane.getPrefWidth() * 0.7);
                backCardView.setPreserveRatio(true);

                //TODO: vs setLayoutX/Y ?
                frontCardView.relocate(pane.getPrefWidth() * 0.2, pane.getPrefHeight() * 0.05);
                backCardView.relocate(pane.getPrefWidth() * 0.2, pane.getPrefHeight() * 0.05);

                backCardView.setOnMouseClicked((event) -> frontCardView.toFront());
                frontCardView.setOnMouseClicked((event) -> backCardView.toFront());

                //TODO: Complete implementation of drag-and-drop of cards with all DragEvents specified, and this is horrible...
                int inHandPosition = i + 1;

                //FIXME: Refactor image/dragboard/imageview
                frontCardView.setOnDragDetected((event) -> {
                    Dragboard cardDragboard = frontCardView.startDragAndDrop(TransferMode.MOVE);
                    Image image = new Image(String.valueOf(GUIView.class.getResource(card.GUI_SPRITES.get(Side.FRONT))), 100, 150, true, true);
                    cardDragboard.setDragView(image, cardSizes.getX() / 2, cardSizes.getY() / 2);
                    ClipboardContent cardClipboard = new ClipboardContent();
                    cardClipboard.put(PLACE_CARD_DATA_FORMAT, new GenericPair<>(inHandPosition, Side.FRONT));
                    cardDragboard.setContent(cardClipboard);
                });

                frontCardView.setOnDragDone((event) -> {
                    if (event.getTransferMode() == TransferMode.MOVE && event.isDropCompleted()) {
                    }
                    //TODO: visually clear card from hand?
                });

                backCardView.setOnDragDetected((event) -> {
                    Dragboard cardDragboard = backCardView.startDragAndDrop(TransferMode.MOVE);
                    Image image = new Image(String.valueOf(GUIView.class.getResource(card.GUI_SPRITES.get(Side.BACK))), 100, 150, true, true);
                    cardDragboard.setDragView(image, cardSizes.getX() / 2, cardSizes.getY() / 2);
                    ClipboardContent cardClipboard = new ClipboardContent();
                    cardClipboard.put(PLACE_CARD_DATA_FORMAT, new GenericPair<>(inHandPosition, Side.BACK));
                    cardDragboard.setContent(cardClipboard);
                });

                backCardView.setOnDragDone((event) -> {
                    if (event.getTransferMode() == TransferMode.MOVE && event.isDropCompleted()) {
                    }
                    //TODO: visually clear card from hand?
                });

                OWN_HAND_PANE.getChildren().add(pane);
            }
        });
    }

    @Override
    public void showCommonPlacedCards() {
        Platform.runLater(() -> {
            //TODO: maybe GridPane and padding?
            DECK_AND_VISIBLE_CARDS_PANE.setPrefSize(screenSizes.getX() * 25 / 100, screenSizes.getY() * 65 / 100);
            DECK_AND_VISIBLE_CARDS_PANE.relocate(0, screenSizes.getY() * 35 / 100);

            ClientGame thisGame = ClientController.getInstance().VIEWMODEL.getCurrentGame();

            Label resourcesLabel = new Label("Resources");
            DECK_AND_VISIBLE_CARDS_PANE.getChildren().add(resourcesLabel);
            resourcesLabel.relocate(DECK_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 5 / 100, DECK_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 5 / 100);
            RESOURCE_HBOX.setPrefSize(DECK_AND_VISIBLE_CARDS_PANE.getPrefWidth(), DECK_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 15 / 100);
            RESOURCE_HBOX.relocate(0, DECK_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 7.5 / 100);

            Label goldsLabel = new Label("Golds");
            DECK_AND_VISIBLE_CARDS_PANE.getChildren().add(goldsLabel);
            goldsLabel.relocate(DECK_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 5 / 100, DECK_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 30 / 100);
            GOLD_HBOX.setPrefSize(DECK_AND_VISIBLE_CARDS_PANE.getPrefWidth(), DECK_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 15 / 100);
            GOLD_HBOX.relocate(0, DECK_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 32.5 / 100);

            Label commonObjectivesLabel = new Label("Common Objectives");
            DECK_AND_VISIBLE_CARDS_PANE.getChildren().add(commonObjectivesLabel);
            commonObjectivesLabel.relocate(DECK_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 37.5 / 100, DECK_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 55 / 100);
            OBJECTIVE_HBOX.setPrefSize(DECK_AND_VISIBLE_CARDS_PANE.getPrefWidth(), DECK_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 15 / 100);
            OBJECTIVE_HBOX.relocate(0, DECK_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 57.5 / 100);

            Label secretObjectiveLabel = new Label("Secret Objective");
            DECK_AND_VISIBLE_CARDS_PANE.getChildren().add(secretObjectiveLabel);
            secretObjectiveLabel.relocate(DECK_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 55 / 100, DECK_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 87.5 / 100);
            if (thisGame.getOwnObjective() != null) {
                ImageView secretObjective = new ImageView(String.valueOf(GUIView.class.getResource(thisGame.getOwnObjective().GUI_SPRITES.get(Side.FRONT))));
                secretObjective.setFitWidth(cardSizes.getX());
                secretObjective.setPreserveRatio(true);

                DECK_AND_VISIBLE_CARDS_PANE.getChildren().add(secretObjective);
                secretObjective.relocate(DECK_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 80 / 100, DECK_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 82.5 / 100);
            }

            //TODO. make imageView static and clear and avoid clearing children and refresh only the content
            RESOURCE_HBOX.getChildren().clear();

            ImageView resourceDeck = new ImageView(String.valueOf(GUIView.class.getResource(thisGame.getTopDeckResourceCard().GUI_SPRITES.get(Side.BACK))));

            resourceDeck.setFitWidth(cardSizes.getX());
            resourceDeck.setPreserveRatio(true);
            resourceDeck.setOnMouseClicked((event) ->
                    ViewState.getCurrentState().drawFromDeck("Resource")
            );
            RESOURCE_HBOX.getChildren().add(resourceDeck);

            for (int i = 0; i < thisGame.getPlacedResources().length; i++) {
                if (thisGame.getPlacedResources()[i] != null) {
                    ImageView resource = new ImageView(String.valueOf(GUIView.class.getResource(thisGame.getPlacedResources()[i].GUI_SPRITES.get(Side.FRONT))));
                    resource.setFitWidth(cardSizes.getX());
                    resource.setPreserveRatio(true);

                    int finalI = i + 1;
                    resource.setOnMouseClicked((event) ->
                            ViewState.getCurrentState().drawFromVisibleCards("Resource", finalI)
                    );

                    RESOURCE_HBOX.getChildren().add(resource);
                }
            }

            //TODO. make imageView static and clear and avoid clearing children and refresh only the content
            GOLD_HBOX.getChildren().clear();

            ImageView goldDeck = new ImageView(String.valueOf(GUIView.class.getResource(thisGame.getTopDeckGoldCard().GUI_SPRITES.get(Side.BACK))));

            goldDeck.setFitWidth(cardSizes.getX());
            goldDeck.setPreserveRatio(true);
            goldDeck.setOnMouseClicked((event) -> ViewState.getCurrentState().drawFromDeck("Gold"));
            GOLD_HBOX.getChildren().add(goldDeck);

            for (int i = 0; i < thisGame.getPlacedGolds().length; i++) {
                if (thisGame.getPlacedGolds()[i] != null) {
                    ImageView gold = new ImageView(String.valueOf(GUIView.class.getResource(thisGame.getPlacedGolds()[i].GUI_SPRITES.get(Side.FRONT))));
                    gold.setFitWidth(cardSizes.getX());
                    gold.setPreserveRatio(true);

                    int finalI = i + 1;
                    gold.setOnMouseClicked((event) ->
                            ViewState.getCurrentState().drawFromVisibleCards("Gold", finalI)
                    );

                    GOLD_HBOX.getChildren().add(gold);
                }
            }

            //TODO. make imageView static and clear and avoid clearing children and refresh only the content
            OBJECTIVE_HBOX.getChildren().clear();

            for (int i = 0; i < thisGame.getCommonObjectives().length; i++) {
                if (thisGame.getCommonObjectives()[i] != null) {
                    ImageView commonObjective = new ImageView(String.valueOf(GUIView.class.getResource(thisGame.getCommonObjectives()[i].GUI_SPRITES.get(Side.FRONT))));
                    commonObjective.setFitWidth(cardSizes.getX());
                    commonObjective.setPreserveRatio(true);

                    OBJECTIVE_HBOX.getChildren().add(commonObjective);
                }
            }
        });
    }

    @Override
    public void showField(ClientPlayer player) {
        Platform.runLater(() ->
        {
            AnchorPane popupContent = new AnchorPane();
            popupContent.setPrefSize(screenSizes.getX() * 70 / 100, screenSizes.getY() * 80 / 100);

            //TODO: add label with name
            // Label playerNameLabel = new Label();

            ScrollPane fieldPane = new ScrollPane();
            fieldPane.setPannable(true);
            fieldPane.setPrefSize(popupContent.getPrefWidth(), popupContent.getPrefHeight());
            //TODO: ??? fieldPane.setFitToHeight();
            drawField(fieldPane, player, false);
            popupContent.getChildren().add(/*playerNameLabel,*/ fieldPane);

            Button centerFieldButton = new Button("+");
            centerFieldButton.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #ff0000; -fx-background-radius: 5px;"); // -fx-padding: 10px 20px;");
            centerFieldButton.setOnMouseClicked((event) -> {
                fieldPane.setHvalue((fieldPane.getHmax() + fieldPane.getHmin()) / 2);
                fieldPane.setVvalue((fieldPane.getVmax() + fieldPane.getVmin()) / 2);
            });

            popupContent.getChildren().add(centerFieldButton);
            centerFieldButton.relocate(popupContent.getPrefWidth() - 50, 60);

            OverlayPopup overlayPopup = GUIView.drawOverlayPopup(popupContent, true);
            overlayPopup.setX(screenSizes.getX() * 15 / 100);
            overlayPopup.setY(screenSizes.getY() * 10 / 100);
            overlayPopup.show(stage);
        });
    }

    @Override
    public void showLeaderboard(List<Triplet<String, Integer, Integer>> POINTS_STATS, boolean gameEndedDueToDisconnections) {
        Platform.runLater(() -> {
            double popupWidth = 720, popupHeight = 480;

            LEADERBOARD_VBOX.setPrefSize(popupWidth, popupHeight);

            Button exitButton = new Button("Torna alla schermata delle lobby");
            exitButton.getStyleClass().add("button");

            for (var row : POINTS_STATS) {
                HBox playerHBox = new HBox(20);
                playerHBox.getStyleClass().add("lobbyBox");

                Label nameLabel = new Label(row.getX());
                nameLabel.getStyleClass().add("titleScreenLabel");
                Label pointsLabel = new Label(row.getY() != -1 ? "" + row.getY() : "N/A");
                pointsLabel.getStyleClass().add("titleScreenLabel");
                Label objectivePointsLabel = new Label(row.getZ() != -1 ? "" + row.getZ() : "N/A");
                objectivePointsLabel.getStyleClass().add("titleScreenLabel");

                playerHBox.getChildren().addAll(nameLabel, pointsLabel, objectivePointsLabel);
                LEADERBOARD_VBOX.getChildren().add(playerHBox);
            }

            WINNING_PLAYER_LABEL.setText(
                    ClientController.getInstance().VIEWMODEL.getCurrentGame().getPlayers().getFirst().getNickname() + " won" +
                            (gameEndedDueToDisconnections ? " as the only player left" : "") + "!"
            );

            LEADERBOARD_VBOX.getChildren().add(exitButton);

            OverlayPopup createdPopup = drawOverlayPopup(LEADERBOARD_VBOX, false);

            exitButton.setOnMouseClicked((mouseEvent -> {
                ViewState.getCurrentState().toLobbies();
                LEADERBOARD_VBOX.setVisible(false);
                createdPopup.hide();
            }));

            AWAITING_STATE_MESSAGE_BOX.setVisible(false);
            LEADERBOARD_VBOX.setVisible(true);
            createdPopup.show(stage);
        });
    }

    //FIXME: separate parameter receiver from message both in signature here, in TUI and in viewModel?
    private HBox createMessageElement(String message) {
        String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1; -fx-padding: 10;";

        // Box
        HBox messageBox = new HBox(250);
        messageBox.setPadding(new Insets(15, 12, 15, 12));

        // Label messaggio
        Label messageLabel = new Label(String.valueOf(message));
        messageLabel.setStyle("-fx-font-size: 16px;");

        messageBox.setStyle(style);

        messageBox.getChildren().add(messageLabel);

        return messageBox;
    }
}
