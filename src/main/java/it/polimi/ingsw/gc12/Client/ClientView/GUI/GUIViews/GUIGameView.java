package it.polimi.ingsw.gc12.Client.ClientView.GUI.GUIViews;

import it.polimi.ingsw.gc12.Client.ClientView.GUI.OverlayPopup;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates.PlayerTurnDrawState;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates.PlayerTurnPlayState;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.ViewState;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class GUIGameView extends GUIView {

    private static GUIGameView gameScreenController = null;

    private final DataFormat PLACE_CARD_DATA_FORMAT = new DataFormat("text/genericpair<integer,side>");
    private final String RED_WHITE_STYLE = "-fx-font-size: 15px; -fx-font-family: 'Bell MT'; -fx-background-color: #f0f0f0; -fx-border-color: #D50A0AFF; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px;";
    private final Button ZOOM_OWN_FIELD_BUTTON;
    private ClientGame thisGame = null;
    private ClientPlayer thisPlayer = null;

    private final ArrayList<GenericPair<Double, Double>> RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS;

    private final double PADDING_SIZE;

    private final Parent SCENE_ROOT;
    private final AnchorPane OWN_FIELD_PANE;
    private final VBox OWN_FIELD_STATS_BOX;
    private final ScrollPane OWN_FIELD_SCROLL_PANE;
    private final Circle NEW_CHAT_MESSAGE_NOTIFICATION;
    private final Button CENTER_OWN_FIELD_BUTTON;
    private final Button TOGGLE_SCOREBOARD_BUTTON;
    private final Button TOGGLE_CHAT_BUTTON;
    private boolean shouldReset = true;
    private final Button LEAVE_BUTTON;
    private final AnchorPane SCOREBOARD_PANE;
    private final AnchorPane CHAT_PANE;
    private final VBox CHAT_VBOX;
    private final ScrollPane CHAT_SCROLL_PANE;
    private final VBox MESSAGES_BOX;
    private final ComboBox<String> RECEIVER_NICKNAME_SELECTOR;
    private final TextField MESSAGE_TEXTFIELD;
    private final Button SEND_MESSAGE_BUTTON;
    private final HBox OPPONENTS_FIELDS_PANE;
    private final HBox OWN_HAND_PANE;
    private final AnchorPane DECKS_AND_VISIBLE_CARDS_PANE;
    private final Label RESOURCES_LABEL;
    private final HBox RESOURCE_HBOX;
    private final Label GOLDS_LABEL;
    private final HBox GOLD_HBOX;
    private final Label COMMON_OBJECTIVES_LABEL;
    private final HBox OBJECTIVE_HBOX;
    private final Label SECRET_OBJECTIVE_LABEL;
    private final VBox AWAITING_STATE_MESSAGE_BOX;
    private final VBox LEADERBOARD_VBOX;
    private final Label LEADERBOARD_LABEL;
    private final Label WINNING_PLAYER_LABEL;
    private final Label TO;
    private final Label GAME_STATE_LABEL;

    private GUIGameView() {
        try {
            SCENE_ROOT = new FXMLLoader(GUIView.class.getResource("/fxml/game_screen.fxml")).load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        OWN_FIELD_PANE = (AnchorPane) SCENE_ROOT.lookup("#ownFieldPane");
        OWN_FIELD_STATS_BOX = (VBox) OWN_FIELD_PANE.lookup("#ownFieldStatsBox");
        OWN_FIELD_SCROLL_PANE = (ScrollPane) OWN_FIELD_PANE.lookup("#ownFieldScrollPane");
        ZOOM_OWN_FIELD_BUTTON = (Button) OWN_FIELD_PANE.lookup("#zoomOwnFieldButton");
        CENTER_OWN_FIELD_BUTTON = (Button) OWN_FIELD_PANE.lookup("#centerOwnFieldButton");
        TOGGLE_SCOREBOARD_BUTTON = (Button) SCENE_ROOT.lookup("#scoreboardButton");
        NEW_CHAT_MESSAGE_NOTIFICATION = (Circle) SCENE_ROOT.lookup("#newChatMessageNotification");
        TOGGLE_CHAT_BUTTON = (Button) SCENE_ROOT.lookup("#chatButton");
        LEAVE_BUTTON = (Button) SCENE_ROOT.lookup("#leaveButton");
        SCOREBOARD_PANE = (AnchorPane) SCENE_ROOT.lookup("#scoreboardPane");
        CHAT_PANE = (AnchorPane) SCENE_ROOT.lookup("#chatPane");
        CHAT_SCROLL_PANE = (ScrollPane) CHAT_PANE.lookup("#chatScrollPane");
        MESSAGES_BOX = (VBox) CHAT_SCROLL_PANE.getContent().lookup("#messagesBox");
        RECEIVER_NICKNAME_SELECTOR = (ComboBox<String>) CHAT_PANE.lookup("#receiverSelector");
        MESSAGE_TEXTFIELD = (TextField) CHAT_PANE.lookup("#messageText");
        SEND_MESSAGE_BUTTON = (Button) CHAT_PANE.lookup("#sendButton");
        OPPONENTS_FIELDS_PANE = (HBox) SCENE_ROOT.lookup("#opponentsFieldsPane");
        OWN_HAND_PANE = (HBox) SCENE_ROOT.lookup("#handPane");
        DECKS_AND_VISIBLE_CARDS_PANE = (AnchorPane) SCENE_ROOT.lookup("#decksAndVisibleCardsPane");
        RESOURCES_LABEL = (Label) DECKS_AND_VISIBLE_CARDS_PANE.lookup("#resourcesLabel");
        RESOURCE_HBOX = (HBox) DECKS_AND_VISIBLE_CARDS_PANE.lookup("#resourceHBox");
        GOLDS_LABEL = (Label) DECKS_AND_VISIBLE_CARDS_PANE.lookup("#goldsLabel");
        GOLD_HBOX = (HBox) DECKS_AND_VISIBLE_CARDS_PANE.lookup("#goldHBox");
        COMMON_OBJECTIVES_LABEL = (Label) DECKS_AND_VISIBLE_CARDS_PANE.lookup("#commonObjectivesLabel");
        OBJECTIVE_HBOX = (HBox) DECKS_AND_VISIBLE_CARDS_PANE.lookup("#objectiveHBox");
        SECRET_OBJECTIVE_LABEL = (Label) DECKS_AND_VISIBLE_CARDS_PANE.lookup("#secretObjectiveLabel");
        AWAITING_STATE_MESSAGE_BOX = (VBox) SCENE_ROOT.lookup("#awaitingStateMessageBox");
        LEADERBOARD_VBOX = (VBox) SCENE_ROOT.lookup("#leaderboardVBox");
        LEADERBOARD_LABEL = (Label) SCENE_ROOT.lookup("#leaderboardLabel");
        WINNING_PLAYER_LABEL = (Label) SCENE_ROOT.lookup("#winningPlayerLabel");
        TO = (Label) SCENE_ROOT.lookup("#to");
        CHAT_VBOX = (VBox) SCENE_ROOT.lookup("#chatBox");
        GAME_STATE_LABEL = (Label) SCENE_ROOT.lookup("#gameStateLabel");

        PADDING_SIZE = 20.0;

        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS = new ArrayList<>();
        //TODO: CAMBIARE LE COORDINATE SOLO PREVIA COMUNICAZIONE A SACRAMONE
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.187, 0.8918)); // 0
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.422, 0.8915)); // 1
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.658, 0.8915)); // 2
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.775, 0.783)); // 3
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.54, 0.783)); // 4
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.305, 0.783)); // 5
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.069, 0.783)); // 6
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.069, 0.6755)); // 7
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.303, 0.6755)); // 8
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.54, 0.6755)); // 9
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.775, 0.6755)); // 10
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.775, 0.568)); // 11
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.54, 0.568)); // 12
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.303, 0.568)); // 13
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.069, 0.568)); // 14
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.069, 0.4605)); // 15
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.303, 0.4605)); // 16
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.54, 0.4605)); // 17
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.775, 0.4605)); // 18
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.775, 0.353)); // 19
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.422, 0.3)); // 20
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.069, 0.353)); // 21
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.069, 0.245)); // 22
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.069, 0.1375)); // 23
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.205, 0.05)); // 24
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.422, 0.03)); // 25
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.639, 0.05)); // 26
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.775, 0.1375)); // 27
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.775, 0.245)); // 28
        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.add(new GenericPair<>(0.422, 0.1623)); // 29
    }

    public static GUIGameView getInstance() {
        if (gameScreenController == null) {
            gameScreenController = new GUIGameView();
        }
        return gameScreenController;
    }

    private Rectangle generateOpenCornerShape(GenericPair<Integer, Integer> openCorner, boolean isInteractive) {
        var openCornerShape = new Rectangle(cardSizes.getX(), cardSizes.getY()) {
            public final GenericPair<Integer, Integer> COORDINATES = openCorner;
        };
        openCornerShape.setFill(Color.TRANSPARENT);
        openCornerShape.setStyle("-fx-stroke: gray; -fx-stroke-width: 2; -fx-stroke-dash-array: 4 8;");
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
                    var data = event.getDragboard().getContent(PLACE_CARD_DATA_FORMAT);
                    if (!(data instanceof GenericPair<?, ?> placeCardData))
                        throw new RuntimeException(); //FIXME: should never happen...

                    ViewState.getCurrentState().placeCard(
                            openCornerShape.COORDINATES, (Integer) placeCardData.getX(), (Side) placeCardData.getY()
                    );
                    event.setDropCompleted(event.getDragboard().hasContent(PLACE_CARD_DATA_FORMAT));
                }
            });
        }
        return openCornerShape;
    }

    private void generateFieldInteractionButton(Button button, String iconResourcePath) {
        ImageView image = new ImageView(String.valueOf(GUIGameView.class.getResource(iconResourcePath)));
        image.setFitHeight(20);
        image.setPreserveRatio(true);

        button.setGraphic(image);
        button.setPrefSize(25, 25);
        button.setStyle("-fx-border-radius: 5; -fx-border-width: 1px; -fx-border-color: black; -fx-background-color: white");
    }

    private void drawField(ScrollPane fieldPane, ClientPlayer player, boolean isInteractive) {
        //TODO: resize this pane based on how many cards have been played and center the field on it? or not?
        GenericPair<Double, Double> clippedPaneSize = new GenericPair<>(
                4000.0, 4000.0 * cardSizes.getY() / cardSizes.getX()
        );

        fieldPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        fieldPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        //TODO: add background color or texture or image?
        AnchorPane clippedPane = new AnchorPane();
        clippedPane.setPrefSize(clippedPaneSize.getX(), clippedPaneSize.getY());
        clippedPane.setCenterShape(true);

        for (var cardEntry : player.getPlacedCards().sequencedEntrySet()) {
            ImageView cardImage = new ImageView(String.valueOf(GUIView.class.getResource(cardEntry.getValue().getX().GUI_SPRITES.get(cardEntry.getValue().getY()))));

            //FIXME: correct this: it is needed to get this later, but which size?
            // or maybe later when needed use cardSizes like this, after having decided if values are correct
            cardImage.setSmooth(true);
            cardImage.setFitWidth(cardSizes.getX());
            cardImage.setFitHeight(cardSizes.getY());
            cardImage.setPreserveRatio(true);

            clippedPane.getChildren().add(cardImage);

            cardImage.relocate(
                    (clippedPaneSize.getX() - cardImage.getFitWidth()) / 2 +
                            cardImage.getFitWidth() * (1 - cornerScaleFactor.getX()) * cardEntry.getKey().getX(),
                    (clippedPaneSize.getY() - cardImage.getFitHeight()) / 2 -
                            cardImage.getFitHeight() * (1 - cornerScaleFactor.getY()) * cardEntry.getKey().getY()
            );
        }

        for (var openCorner : player.getOpenCorners()) {
            var openCornerShape = generateOpenCornerShape(openCorner, isInteractive);

            clippedPane.getChildren().add(openCornerShape);

            openCornerShape.relocate(
                    (clippedPaneSize.getX() - cardSizes.getX()) / 2 +
                            cardSizes.getX() * (1 - cornerScaleFactor.getX()) * openCorner.getX(),
                    (clippedPaneSize.getY() - cardSizes.getY()) / 2 -
                            cardSizes.getY() * (1 - cornerScaleFactor.getY()) * openCorner.getY()
            );
        }

        fieldPane.setContent(clippedPane);
        fieldPane.setHvalue((fieldPane.getHmax() + fieldPane.getHmin()) / 2);
        fieldPane.setVvalue((fieldPane.getVmax() + fieldPane.getVmin()) / 2);
    }

    private void makePaneDraggable(Pane pane) {
        AtomicReference<Double> xOffset = new AtomicReference<>(0.0);
        AtomicReference<Double> yOffset = new AtomicReference<>(0.0);

        pane.setOnMousePressed((event) -> {
            xOffset.set(pane.getLayoutX() - event.getScreenX());
            yOffset.set(pane.getLayoutY() - event.getScreenY());
            pane.toFront();
        });

        pane.setOnMouseDragged((event) -> {
            pane.relocate(event.getScreenX() + xOffset.get(), event.getScreenY() + yOffset.get());
        });
    }

    private void togglePane(Pane popupPane) {
        Platform.runLater(() -> {
            popupPane.setVisible(!popupPane.isVisible());
            popupPane.toFront();
        });
    }

    private void resetGameScreen() {
        thisGame = VIEWMODEL.getCurrentGame();
        thisPlayer = thisGame.getThisPlayer();

        OPPONENTS_FIELDS_PANE.setPrefSize(
                screenSizes.getX() - 2 * PADDING_SIZE,
                screenSizes.getY() * 35 / 100 - 2 * PADDING_SIZE
        );
        OPPONENTS_FIELDS_PANE.relocate(PADDING_SIZE, PADDING_SIZE);

        DECKS_AND_VISIBLE_CARDS_PANE.setPrefSize(screenSizes.getX() * 30 / 100, screenSizes.getY() * 65 / 100);
        DECKS_AND_VISIBLE_CARDS_PANE.relocate(0, screenSizes.getY() * 35 / 100);

        RESOURCES_LABEL.relocate(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 5 / 100, DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 5 / 100);
        RESOURCE_HBOX.setPrefSize(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth(), DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 15 / 100);
        RESOURCE_HBOX.relocate(0, DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 10 / 100);

        GOLDS_LABEL.relocate(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 5 / 100, DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 27.5 / 100);
        GOLD_HBOX.setPrefSize(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth(), DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 15 / 100);
        GOLD_HBOX.relocate(0, DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 32.5 / 100);

        COMMON_OBJECTIVES_LABEL.relocate(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 27.5 / 100, DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 52.5 / 100);
        OBJECTIVE_HBOX.setPrefSize(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth(), DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 15 / 100);
        OBJECTIVE_HBOX.relocate(0, DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 57.5 / 100);

        SECRET_OBJECTIVE_LABEL.relocate(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 30 / 100, DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 86 / 100);

        OWN_FIELD_PANE.setPrefSize(
                screenSizes.getX() * 70 / 100 - PADDING_SIZE,
                screenSizes.getY() * 50 / 100 - PADDING_SIZE
        );
        OWN_FIELD_PANE.relocate(screenSizes.getX() * 30 / 100, screenSizes.getY() * 35 / 100);

        OWN_FIELD_STATS_BOX.setPrefSize(75, OWN_FIELD_PANE.getPrefHeight());

        OWN_FIELD_SCROLL_PANE.setPannable(true);
        OWN_FIELD_SCROLL_PANE.setPrefSize(OWN_FIELD_PANE.getPrefWidth() - 75, OWN_FIELD_PANE.getPrefHeight());
        OWN_FIELD_SCROLL_PANE.setLayoutX(80);

        OWN_HAND_PANE.setPrefSize(screenSizes.getX() * 50 / 100, screenSizes.getY() * 15 / 100);
        OWN_HAND_PANE.relocate(screenSizes.getX() * 40 / 100, screenSizes.getY() * 85 / 100);

        //TODO: CAMBIARE LE DIMENSIONI SOLO PREVIA COMUNICAZIONE A SACRAMONE
        SCOREBOARD_PANE.setPrefSize(screenSizes.getX() * 0.1328125, screenSizes.getY() * 0.5);
        SCOREBOARD_PANE.setStyle("-fx-background-image: url('/images/scoreboard.png'); -fx-background-size: stretch;");
        SCOREBOARD_PANE.relocate(screenSizes.getX() * 10 / 100, screenSizes.getY() * 40 / 100);
        makePaneDraggable(SCOREBOARD_PANE);

        CHAT_PANE.setPrefSize(screenSizes.getX() * 30 / 100, screenSizes.getY() * 70 / 100);
        CHAT_PANE.relocate(screenSizes.getX() * 60 / 100, screenSizes.getY() * 10 / 100);
        makePaneDraggable(CHAT_PANE);

        CHAT_SCROLL_PANE.setPrefWidth(CHAT_PANE.getPrefWidth() * 80 / 100);

        List<String> nicknames = thisGame.getPlayers().stream()
                .map(Player::getNickname).collect(Collectors.toCollection(ArrayList::new));
        nicknames.addFirst("everyone");
        nicknames.remove(VIEWMODEL.getOwnNickname());
        ObservableList<String> receiverNicknames = FXCollections.observableList(nicknames);
        RECEIVER_NICKNAME_SELECTOR.setItems(receiverNicknames);
        RECEIVER_NICKNAME_SELECTOR.getSelectionModel().selectFirst();

        RECEIVER_NICKNAME_SELECTOR.setPrefWidth(CHAT_PANE.getPrefWidth() * 60 / 100);
        RECEIVER_NICKNAME_SELECTOR.setPrefHeight(CHAT_PANE.getPrefHeight() * 2.5 / 100);
        RECEIVER_NICKNAME_SELECTOR.relocate(CHAT_PANE.getPrefWidth() * 15 / 100, CHAT_PANE.getPrefHeight() * 80 / 100);

        MESSAGE_TEXTFIELD.setPrefWidth(CHAT_PANE.getPrefWidth() * 60 / 100);
        MESSAGE_TEXTFIELD.setPrefHeight(CHAT_PANE.getPrefHeight() * 5 / 100);

        TO.relocate(CHAT_PANE.getPrefWidth() * 8.5 / 100, CHAT_PANE.getPrefHeight() * 90 / 100);

        CHAT_VBOX.relocate(CHAT_PANE.getPrefWidth() * 10 / 100, CHAT_PANE.getPrefHeight() * 2 / 100);

        MESSAGES_BOX.getChildren().clear();

        ImageView sendImage = new ImageView(String.valueOf(GUIGameView.class.getResource("/images/icons/sendBetter.png")));
        sendImage.setFitHeight(20);
        sendImage.setPreserveRatio(true);
        SEND_MESSAGE_BUTTON.setGraphic(sendImage);
        SEND_MESSAGE_BUTTON.setPrefWidth(TOGGLE_CHAT_BUTTON.getPrefWidth() * 50 / 100);
        SEND_MESSAGE_BUTTON.setOnMouseClicked((event) -> {
            String receiver = RECEIVER_NICKNAME_SELECTOR.getValue();
            String message = MESSAGE_TEXTFIELD.getText().trim();
            //FIXME: aggiungere lunghezza massima e substring anche qui? Magari aggiungerle ma più di 150 chars?
            if (receiver.equals("everyone"))
                ViewState.getCurrentState().broadcastMessage(message);
            else
                ViewState.getCurrentState().directMessage(receiver, message);
            MESSAGE_TEXTFIELD.clear();
        });

        ImageView scoreboardImage = new ImageView(String.valueOf(GUIGameView.class.getResource("/images/icons/scoreboard.png")));
        scoreboardImage.setFitHeight(30);
        scoreboardImage.setPreserveRatio(true);
        TOGGLE_SCOREBOARD_BUTTON.setGraphic(scoreboardImage);
        TOGGLE_SCOREBOARD_BUTTON.toFront();
        TOGGLE_SCOREBOARD_BUTTON.relocate(screenSizes.getX() * 2 / 100, screenSizes.getY() * 90 / 100);
        TOGGLE_SCOREBOARD_BUTTON.setOnMouseClicked((event) -> togglePane(SCOREBOARD_PANE));

        NEW_CHAT_MESSAGE_NOTIFICATION.setRadius(10);
        NEW_CHAT_MESSAGE_NOTIFICATION.relocate(screenSizes.getX() * 90 / 100 + 40, screenSizes.getY() * 90 / 100 + 5);

        ImageView chatImage = new ImageView(String.valueOf(GUIGameView.class.getResource("/images/icons/chat.png")));
        chatImage.setFitHeight(30);
        chatImage.setPreserveRatio(true);
        TOGGLE_CHAT_BUTTON.setGraphic(chatImage);
        TOGGLE_CHAT_BUTTON.toFront();
        TOGGLE_CHAT_BUTTON.relocate(screenSizes.getX() * 90 / 100, screenSizes.getY() * 90 / 100);
        TOGGLE_CHAT_BUTTON.setOnMouseClicked((event) -> {
            togglePane(CHAT_PANE);
            NEW_CHAT_MESSAGE_NOTIFICATION.setVisible(false);
        });

        ImageView leaveImage = new ImageView(String.valueOf(GUIGameView.class.getResource("/images/icons/leaveGame.png")));
        leaveImage.setFitHeight(30);
        leaveImage.setPreserveRatio(true);
        LEAVE_BUTTON.setGraphic(leaveImage);
        LEAVE_BUTTON.toFront();
        LEAVE_BUTTON.relocate(screenSizes.getX() * 95 / 100, screenSizes.getY() * 90 / 100);
        LEAVE_BUTTON.setOnMouseClicked((event) -> {
            ViewState.getCurrentState().quit();
            shouldReset = true;
        });

        GAME_STATE_LABEL.relocate(screenSizes.getX() * 32.5 / 100, screenSizes.getY() * 90 / 100);

        shouldReset = false;
    }

    @Override
    public void gameScreen() {
        Platform.runLater(() -> {
            if (shouldReset) resetGameScreen();
            stage.getScene().setRoot(SCENE_ROOT);

            showOpponentsFieldsMiniaturized();
            showCommonPlacedCards();
            showOwnField();
            showHand();

            String playingPhase = "Playing a card";
            String drawPhase = "Drawing a card";
            GAME_STATE_LABEL.setWrapText(true);
            if (ViewState.getCurrentState() instanceof PlayerTurnDrawState) {
                GAME_STATE_LABEL.setText("It's " + thisGame.getPlayers().get(thisGame.getCurrentPlayerIndex()).getNickname() + "'s turn!\n" +
                        drawPhase);
            } else if (ViewState.getCurrentState() instanceof PlayerTurnPlayState) {
                GAME_STATE_LABEL.setText("It's " + thisGame.getPlayers().get(thisGame.getCurrentPlayerIndex()).getNickname() + "'s turn!\n" +
                        playingPhase);
            }

            updateScoreboard();
        });
    }

    //TODO: centrare l'opponentField sulla carta nuova appena piazzata?
    private void showOpponentsFieldsMiniaturized() {
        OPPONENTS_FIELDS_PANE.getChildren().clear();

        for (var player : thisGame.getPlayers().stream()
                .filter((player) -> !(player.getNickname().equals(VIEWMODEL.getOwnNickname())))
                .toList()) {
            VBox opponentInfo = new VBox(5);
            //FIXME: togliere
            // opponentInfo.setStyle("-fx-background-color: green");
            opponentInfo.setAlignment(Pos.CENTER);
            opponentInfo.setPrefSize(
                    (OPPONENTS_FIELDS_PANE.getPrefWidth() - 2 * 30) / 3,
                    OPPONENTS_FIELDS_PANE.getHeight()
            );

            Label opponentName = new Label(
                    player.getNickname() +
                            ((thisGame.getPlayers().get(thisGame.getCurrentPlayerIndex())).equals(player) ? " (IN TURN)" : "")
            );
            opponentName.setAlignment(Pos.CENTER);
            opponentName.setPrefSize(opponentInfo.getPrefWidth(), opponentInfo.getPrefHeight() / 10);
            opponentName.setStyle(RED_WHITE_STYLE);

            HBox opponentData = new HBox(5);
            opponentData.setAlignment(Pos.CENTER);
            opponentData.setPrefSize(opponentInfo.getPrefWidth(), opponentInfo.getPrefHeight() * 9 / 10);

            VBox opponentStats = new VBox();
            opponentStats.setAlignment(Pos.CENTER);
            opponentStats.setPrefSize(55, opponentData.getPrefHeight());
            for (var resourceEntry : player.getOwnedResources().entrySet()) {
                HBox resourceData = new HBox();
                resourceData.setAlignment(Pos.CENTER);
                ImageView image = new ImageView(String.valueOf(GUIView.class.getResource("/images/icons/res/" + resourceEntry.getKey().SYMBOL.toLowerCase() + ".png")));
                image.setSmooth(true);
                image.setFitWidth(30);
                image.setPreserveRatio(true);

                Label resourceInfo = new Label("" + resourceEntry.getValue());
                resourceInfo.setPrefWidth(25);
                resourceInfo.setStyle(RED_WHITE_STYLE);

                resourceData.getChildren().addAll(image, resourceInfo);
                opponentStats.getChildren().add(resourceData);
            }

            AnchorPane opponentField = new AnchorPane();
            opponentField.setPrefSize(opponentData.getPrefWidth() - 60, opponentData.getPrefHeight());

            ScrollPane opponentScrollField = new ScrollPane();
            opponentScrollField.setPrefSize(opponentField.getPrefWidth(), opponentField.getPrefHeight());
            opponentScrollField.setPannable(true);
            drawField(opponentScrollField, player, false);

            opponentScrollField.getContent().setScaleX(0.75);
            opponentScrollField.getContent().setScaleY(0.75);

            Button zoomOpponentFieldButton = new Button();
            generateFieldInteractionButton(zoomOpponentFieldButton, "/images/icons/zoom.png");
            zoomOpponentFieldButton.setOnMouseClicked((event) -> showField(player));

            Button centerOpponentFieldButton = new Button();
            generateFieldInteractionButton(centerOpponentFieldButton, "/images/icons/aim.png");
            centerOpponentFieldButton.setOnMouseClicked((event) -> {
                opponentScrollField.setHvalue((opponentScrollField.getHmax() + opponentScrollField.getHmin()) / 2);
                opponentScrollField.setVvalue((opponentScrollField.getVmax() + opponentScrollField.getVmin()) / 2);
            });

            opponentField.getChildren().addAll(opponentScrollField, zoomOpponentFieldButton, centerOpponentFieldButton);
            zoomOpponentFieldButton.relocate(opponentField.getPrefWidth() - 50, 20);
            centerOpponentFieldButton.relocate(opponentField.getPrefWidth() - 50, 60);

            opponentData.getChildren().addAll(opponentStats, opponentField);
            opponentInfo.getChildren().addAll(opponentName, opponentData);
            OPPONENTS_FIELDS_PANE.getChildren().add(opponentInfo);
        }
    }

    @Override
    public void showCommonPlacedCards() {
        Platform.runLater(() -> {
            //TODO. make imageView static and clear and avoid clearing children and refresh only the content
            RESOURCE_HBOX.getChildren().clear();

            ImageView resourceDeck = new ImageView(String.valueOf(GUIView.class.getResource(thisGame.getTopDeckResourceCard().GUI_SPRITES.get(Side.BACK))));
            resourceDeck.setSmooth(true);
            resourceDeck.setFitWidth(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 25 / 100);
            resourceDeck.setPreserveRatio(true);
            resourceDeck.setOnMouseClicked((event) ->
                    ViewState.getCurrentState().drawFromDeck("Resource")
            );
            RESOURCE_HBOX.getChildren().add(resourceDeck);

            for (int i = 0; i < thisGame.getPlacedResources().length; i++) {
                if (thisGame.getPlacedResources()[i] != null) {
                    ImageView resource = new ImageView(String.valueOf(GUIView.class.getResource(thisGame.getPlacedResources()[i].GUI_SPRITES.get(Side.FRONT))));
                    resource.setSmooth(true);
                    resource.setFitWidth(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 25 / 100);
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

            goldDeck.setSmooth(true);
            goldDeck.setFitWidth(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 25 / 100);
            goldDeck.setPreserveRatio(true);
            goldDeck.setOnMouseClicked((event) -> ViewState.getCurrentState().drawFromDeck("Gold"));
            GOLD_HBOX.getChildren().add(goldDeck);

            for (int i = 0; i < thisGame.getPlacedGolds().length; i++) {
                if (thisGame.getPlacedGolds()[i] != null) {
                    ImageView gold = new ImageView(String.valueOf(GUIView.class.getResource(thisGame.getPlacedGolds()[i].GUI_SPRITES.get(Side.FRONT))));
                    gold.setSmooth(true);
                    gold.setFitWidth(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 25 / 100);
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
                    commonObjective.setSmooth(true);
                    commonObjective.setFitWidth(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 25 / 100);
                    commonObjective.setPreserveRatio(true);

                    OBJECTIVE_HBOX.getChildren().add(commonObjective);
                }
            }

            if (thisGame.getOwnObjective() != null) {
                ImageView secretObjective = new ImageView(String.valueOf(GUIView.class.getResource(thisGame.getOwnObjective().GUI_SPRITES.get(Side.FRONT))));
                secretObjective.setSmooth(true);
                secretObjective.setFitWidth(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 25 / 100);
                secretObjective.setPreserveRatio(true);

                DECKS_AND_VISIBLE_CARDS_PANE.getChildren().add(secretObjective);
                secretObjective.relocate(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 67.5 / 100, DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 82.5 / 100);
            }
        });
    }

    private void showOwnField() {
        OWN_FIELD_STATS_BOX.getChildren().clear();

        for (var resourceEntry : thisPlayer.getOwnedResources().entrySet()) {
            HBox resourceData = new HBox();
            resourceData.setAlignment(Pos.CENTER);
            ImageView image = new ImageView(String.valueOf(GUIView.class.getResource("/images/icons/res/" + resourceEntry.getKey().SYMBOL.toLowerCase() + ".png")));
            image.setFitWidth(50);
            image.setPreserveRatio(true);

            Label resourceInfo = new Label("" + resourceEntry.getValue());
            resourceInfo.setPrefWidth(25);
            resourceInfo.setStyle(RED_WHITE_STYLE);

            resourceData.getChildren().addAll(image, resourceInfo);

            OWN_FIELD_STATS_BOX.getChildren().add(resourceData);
            resourceData.relocate(0, 0);
        }

        drawField(OWN_FIELD_SCROLL_PANE, thisPlayer, true);

        generateFieldInteractionButton(ZOOM_OWN_FIELD_BUTTON, "/images/icons/zoom.png");
        ZOOM_OWN_FIELD_BUTTON.setOnMouseClicked((event) -> showField(thisPlayer));

        generateFieldInteractionButton(CENTER_OWN_FIELD_BUTTON, "/images/icons/aim.png");
        CENTER_OWN_FIELD_BUTTON.setOnMouseClicked((event) -> {
            OWN_FIELD_SCROLL_PANE.setHvalue((OWN_FIELD_SCROLL_PANE.getHmax() + OWN_FIELD_SCROLL_PANE.getHmin()) / 2);
            OWN_FIELD_SCROLL_PANE.setVvalue((OWN_FIELD_SCROLL_PANE.getVmax() + OWN_FIELD_SCROLL_PANE.getVmin()) / 2);
        });

        ZOOM_OWN_FIELD_BUTTON.relocate(OWN_FIELD_PANE.getPrefWidth() - 50, 20);
        CENTER_OWN_FIELD_BUTTON.relocate(OWN_FIELD_PANE.getPrefWidth() - 50, 60);
    }

    @Override
    public void showHand() {
        Platform.runLater(() -> {
            List<ClientCard> cardsInHand = thisGame.getCardsInHand();

            OWN_HAND_PANE.getChildren().clear();

            for (int i = 0; i < cardsInHand.size(); i++) {
                ClientCard card = cardsInHand.get(i);

                AnchorPane pane = new AnchorPane();
                pane.setPrefSize(OWN_HAND_PANE.getPrefWidth() / 3, OWN_HAND_PANE.getPrefHeight());

                ImageView frontCardView = new ImageView(String.valueOf(GUIView.class.getResource(card.GUI_SPRITES.get(Side.FRONT))));
                frontCardView.setSmooth(true);
                frontCardView.setFitWidth(pane.getPrefWidth() * 0.7);
                frontCardView.setPreserveRatio(true);

                ImageView backCardView = new ImageView(String.valueOf(GUIView.class.getResource(card.GUI_SPRITES.get(Side.BACK))));
                backCardView.setSmooth(true);
                backCardView.setFitWidth(pane.getPrefWidth() * 0.7);
                backCardView.setPreserveRatio(true);

                pane.getChildren().addAll(backCardView, frontCardView);
                backCardView.toBack();

                frontCardView.relocate(pane.getPrefWidth() * 0.2, pane.getPrefHeight() * 0.05);
                backCardView.relocate(pane.getPrefWidth() * 0.2, pane.getPrefHeight() * 0.05);

                frontCardView.setOnMouseClicked((event) -> backCardView.toFront());
                backCardView.setOnMouseClicked((event) -> frontCardView.toFront());

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

    private void updateScoreboard() {
        Platform.runLater(() -> {
            SCOREBOARD_PANE.getChildren().clear();

            int currentPoints = -1;
            int overlappingTokens = 0;

            double yOverlappingOffset = 0.0125;

            for (var player : thisGame.getPlayers().stream()
                    .sorted(Comparator.comparingInt(ClientPlayer::getPoints)).toList().reversed()
            ) {
                if (currentPoints != player.getPoints()) {
                    currentPoints = player.getPoints();
                    overlappingTokens = 0;
                } else
                    overlappingTokens++;

                GenericPair<Double, Double> scaleFactor = RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS.get(player.getPoints());
                ImageView token = new ImageView(String.valueOf(GUIView.class.getResource("/images/misc/" + player.getColor().name().toLowerCase() + ".png")));
                token.setSmooth(true);
                token.setFitHeight(SCOREBOARD_PANE.getPrefWidth() * 0.16);
                token.setPreserveRatio(true);

                SCOREBOARD_PANE.getChildren().add(token);
                token.relocate(SCOREBOARD_PANE.getPrefWidth() * scaleFactor.getX(),
                        SCOREBOARD_PANE.getPrefHeight() * (scaleFactor.getY() - (overlappingTokens * yOverlappingOffset)));
            }

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
        });
    }

    @Override
    public void updateChat() {
        Platform.runLater(() -> {
            List<String> chatLog = thisGame.getChatLog();
            if (!chatLog.isEmpty()) {
                if (chatLog.size() > 2 && !(chatLog.getLast().startsWith("[") || chatLog.getLast().startsWith("<")))
                    MESSAGES_BOX.getChildren().add(createMessageElement(chatLog.get(chatLog.size() - 2)));
                MESSAGES_BOX.getChildren().add(createMessageElement(chatLog.getLast()));
            }

            CHAT_SCROLL_PANE.setVvalue(CHAT_SCROLL_PANE.getVmax());

            if (!CHAT_PANE.isVisible())
                NEW_CHAT_MESSAGE_NOTIFICATION.setVisible(true);
        });
    }

    @Override
    public void showInitialCardsChoice() {
        Platform.runLater(() -> {
            //double popupWidth = 720, popupHeight = 480;
            //String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2; -fx-padding: 10;";

            VBox initialCardsChoiceVBox = new VBox(30);
            initialCardsChoiceVBox.setAlignment(Pos.CENTER);
            initialCardsChoiceVBox.setPrefSize(screenSizes.getX(), screenSizes.getY() * 60 / 100);
            //initialCardsChoiceVBox.setStyle(style);
            Label cardLabel = new Label("Choose which side you want to play your assigned initial card on: ");
            cardLabel.getStyleClass().add("popupText");

            HBox initialChoiceHBox = new HBox(100);
            initialChoiceHBox.setAlignment(Pos.CENTER);
            initialChoiceHBox.setPrefSize(initialCardsChoiceVBox.getPrefWidth(), initialCardsChoiceVBox.getPrefHeight() * 0.9);

            initialCardsChoiceVBox.getChildren().addAll(cardLabel, initialChoiceHBox);

            OverlayPopup createdPopup = drawOverlayPopup(initialCardsChoiceVBox, false);

            ClientCard initialCard = VIEWMODEL.getCurrentGame().getCardsInHand().getFirst();

            ParallelTransition choiceTransition = new ParallelTransition();

            for (var entry : initialCard.GUI_SPRITES.entrySet()) {
                ImageView initialCardSideView = new ImageView(String.valueOf(GUIGameView.class.getResource(entry.getValue())));
                initialCardSideView.setSmooth(true);
                initialCardSideView.setFitWidth(initialChoiceHBox.getPrefWidth() * 0.3);
                initialCardSideView.setPreserveRatio(true);

                ScaleTransition zoomInOutTransition = new ScaleTransition(Duration.millis(2000), initialCardSideView);
                zoomInOutTransition.setByX(0.05);
                zoomInOutTransition.setByY(0.05);
                zoomInOutTransition.setCycleCount(Animation.INDEFINITE);
                zoomInOutTransition.setAutoReverse(true);
                zoomInOutTransition.setInterpolator(Interpolator.EASE_BOTH);

                choiceTransition.getChildren().add(zoomInOutTransition);

                initialChoiceHBox.getChildren().add(initialCardSideView);

                initialCardSideView.setOnMouseClicked((event) -> {
                    ViewState.getCurrentState().placeCard(new GenericPair<>(0, 0), 1, entry.getKey());
                    createdPopup.hide();
                    choiceTransition.stop();
                });
            }

            createdPopup.setX(0);
            createdPopup.setY(screenSizes.getY() * 20 / 100);
            choiceTransition.play();
            createdPopup.show(stage);
        });
    }

    @Override
    public void showObjectiveCardsChoice(ArrayList<ClientCard> objectivesSelection) {
        Platform.runLater(() -> {
            //double popupWidth = 720, popupHeight = 480;
            //String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2; -fx-padding: 10;";

            VBox objectiveChoiceVBox = new VBox(30);
            objectiveChoiceVBox.setAlignment(Pos.CENTER);
            objectiveChoiceVBox.setPrefSize(screenSizes.getX(), screenSizes.getY() * 60 / 100);
            //objectiveChoiceVBox.setStyle(style);
            Label cardLabel = new Label("Choose which card you want to keep as your secret objective: ");
            cardLabel.getStyleClass().add("popupText");

            HBox objectiveChoiceHBox = new HBox(100);
            objectiveChoiceHBox.setAlignment(Pos.CENTER);
            objectiveChoiceHBox.setPrefSize(objectiveChoiceVBox.getPrefWidth(), objectiveChoiceVBox.getPrefHeight() * 0.9);

            objectiveChoiceVBox.getChildren().addAll(cardLabel, objectiveChoiceHBox);

            OverlayPopup createdPopup = drawOverlayPopup(objectiveChoiceVBox, false);

            ParallelTransition choiceTransition = new ParallelTransition();

            for (int i = 0; i < objectivesSelection.size(); i++) {
                ClientCard objectiveCard = objectivesSelection.get(i);

                ImageView objectiveCardView = new ImageView(String.valueOf(GUIView.class.getResource(objectiveCard.GUI_SPRITES.get(Side.FRONT))));
                objectiveCardView.setSmooth(true);
                objectiveCardView.setFitWidth(objectiveChoiceHBox.getPrefWidth() * 0.3);
                objectiveCardView.setPreserveRatio(true);

                ScaleTransition zoomInOutTransition = new ScaleTransition(Duration.millis(2000), objectiveCardView);
                zoomInOutTransition.setByX(0.05);
                zoomInOutTransition.setByY(0.05);
                zoomInOutTransition.setCycleCount(Animation.INDEFINITE);
                zoomInOutTransition.setAutoReverse(true);
                zoomInOutTransition.setInterpolator(Interpolator.EASE_BOTH);

                choiceTransition.getChildren().add(zoomInOutTransition);

                int cardPosition = i;
                objectiveCardView.setOnMouseClicked((event) -> {
                    ViewState.getCurrentState().pickObjective(cardPosition + 1);
                    createdPopup.hide();
                    choiceTransition.stop();
                });

                objectiveChoiceHBox.getChildren().add(objectiveCardView);
            }

            createdPopup.setX(0);
            createdPopup.setY(screenSizes.getY() * 20 / 100);
            choiceTransition.play();
            createdPopup.show(stage);
        });
    }

    @Override
    public void showField(ClientPlayer player) {
        Platform.runLater(() ->
        {
            AnchorPane popupContent = new AnchorPane();
            popupContent.setPrefSize(screenSizes.getX() * 90 / 100, screenSizes.getY() * 90 / 100);

            //FIXME: why is is not shown?
            Label playerNameLabel = new Label(
                    player.getNickname() +
                            ((thisGame.getPlayers().get(thisGame.getCurrentPlayerIndex())).equals(player) ? " (IN TURN)" : "")
            );
            playerNameLabel.setStyle(RED_WHITE_STYLE);

            ScrollPane fieldPane = new ScrollPane();
            fieldPane.setPannable(true);
            fieldPane.setPrefSize(popupContent.getPrefWidth(), popupContent.getPrefHeight());
            drawField(fieldPane, player, false);
            fieldPane.getContent().setScaleX(1.5);
            fieldPane.getContent().setScaleY(1.5);

            popupContent.getChildren().addAll(fieldPane);

            Button centerFieldButton = new Button();
            generateFieldInteractionButton(centerFieldButton, "/images/icons/aim.png");
            centerFieldButton.setOnMouseClicked((event) -> {
                fieldPane.setHvalue((fieldPane.getHmax() + fieldPane.getHmin()) / 2);
                fieldPane.setVvalue((fieldPane.getVmax() + fieldPane.getVmin()) / 2);
            });

            popupContent.getChildren().add(centerFieldButton);
            centerFieldButton.relocate(popupContent.getPrefWidth() - 50, 60);

            OverlayPopup overlayPopup = drawOverlayPopup(popupContent, true);

            ((AnchorPane) overlayPopup.getContent().getFirst()).getChildren().add(playerNameLabel);
            playerNameLabel.relocate((popupContent.getPrefWidth() - playerNameLabel.getWidth()) / 2, popupContent.getPrefHeight() / 20);

            overlayPopup.setX(screenSizes.getX() * 5 / 100);
            overlayPopup.setY(screenSizes.getY() * 5 / 100);
            overlayPopup.show(stage);
        });
    }

    @Override
    public void awaitingScreen() {
        Platform.runLater(() -> {
            OverlayPopup createdPopup = drawOverlayPopup(AWAITING_STATE_MESSAGE_BOX, false);

            createdPopup.show(stage);
            AWAITING_STATE_MESSAGE_BOX.setVisible(true);
        });
    }

    @Override
    public void leaderboardScreen(List<Triplet<String, Integer, Integer>> leaderboard, boolean gameEndedDueToDisconnections) {
        Platform.runLater(() -> {
            LEADERBOARD_VBOX.setPrefSize(screenSizes.getX() * 80 / 100, screenSizes.getY() * 90 / 100);

            Button exitButton = new Button("Back to lobbies' screen");
            exitButton.getStyleClass().add("button");

            for (var row : leaderboard) {
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

            WINNING_PLAYER_LABEL.setText((gameEndedDueToDisconnections ?
                    "Since the game ended due to disconnections of all the other players, " : "") +
                    leaderboard.getFirst().getX() + " is the WINNER!");

            LEADERBOARD_VBOX.getChildren().add(exitButton);

            OverlayPopup createdPopup = drawOverlayPopup(LEADERBOARD_VBOX, false);

            exitButton.setOnMouseClicked((mouseEvent -> {
                ViewState.getCurrentState().toLobbies();
                LEADERBOARD_VBOX.setVisible(false);
                createdPopup.hide();
                shouldReset = true;
            }));

            LEADERBOARD_VBOX.setVisible(true);
            createdPopup.setX(screenSizes.getX() * 10 / 100);
            createdPopup.setY(screenSizes.getY() * 5 / 100);
            createdPopup.show(stage);
        });
    }

    //FIXME: separate parameter receiver from message both in signature here, in TUI and in viewModel?
    private HBox createMessageElement(String message) {
        HBox messageBox = new HBox(250);
        messageBox.setPadding(new Insets(15, 12, 15, 12));

        Label messageLabel = new Label(String.valueOf(message));
        messageLabel.setPrefWidth(CHAT_SCROLL_PANE.getPrefWidth() * 80 / 100);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 14px;");

        messageBox.setPrefWidth(CHAT_SCROLL_PANE.getPrefWidth() * 80 / 100);
        messageBox.getStyleClass().add("messageElement");

        messageBox.getChildren().add(messageLabel);

        return messageBox;
    }
}
