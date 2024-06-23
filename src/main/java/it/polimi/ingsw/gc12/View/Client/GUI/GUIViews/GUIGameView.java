package it.polimi.ingsw.gc12.View.Client.GUI.GUIViews;

import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.Enums.Side;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import it.polimi.ingsw.gc12.View.Client.GUI.OverlayPopup;
import it.polimi.ingsw.gc12.View.Client.ViewStates.GameStates.AwaitingReconnectionState;
import it.polimi.ingsw.gc12.View.Client.ViewStates.GameStates.PlayerTurnPlayState;
import it.polimi.ingsw.gc12.View.Client.ViewStates.ViewState;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * The GUIGameView class represents the Graphical User Interface (GUI) view for displaying the game
 * and all the details composing the game board.
 * <p>
 * This class handles the rendering of various game elements including player stats, cards, decks, opponent fields,
 * and the player's own field.
 */
public class GUIGameView extends GUIView {

    /**
     * The singleton instance of the {@code GUIGameView}.
     */
    private static GUIGameView gameScreenController = null;

    /**
     * The width and height of a single card.
     */
    private final GenericPair<Double, Double> CARD_SIZES;
    /**
     * The values representing the percentage of a card's width and height occupied by one of the card's corners.
     */
    private final GenericPair<Double, Double> CORNER_SCALE_FACTOR;
    /**
     * The padding length used to relocate the main panes of the game screen.
     */
    private final double PADDING_SIZE;

    /**
     * The MIME type format of the data moved by card-to-openCorner drag actions.
     */
    private final DataFormat PLACE_CARD_DATA_FORMAT;
    /**
     * The format of the timestamp printed together with chat messages.
     */
    private final SimpleDateFormat TIMESTAMP_FORMATTER;
    /**
     * The string containing the CSS values used to restyle some graphic elements.
     */
    private final String LIGHT_DARK_BROWN_STYLE;
    /**
     * The list of all transitions running indefinitely, stored so that their reference isn't lost and that
     * they can be stopped and garbage collected, thus avoiding memory leaks.
     */
    private final List<Transition> RUNNING_TRANSITIONS;
    /**
     * The list of the percentage offsets of the players' tokens in relation to the width and height of the
     * scoreboard pane and its background image.
     */
    private final ArrayList<GenericPair<Double, Double>> RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS;
    private final Label RESOURCE_CARDS_LABEL;
    private final Label COMMON_OBJECTIVES_LABEL;
    /**
     * The boolean value indicating whether the content of the gameScreen and its panes should be completely
     * reset because it might have become inconsistent with the data inside the viewModel.
     */
    protected boolean shouldReset;
    /**
     * The game present in the viewModel at the moment this gameScreen was shown.
     * It is stored only for code readability and reducing synchronized accesses to the viewModel.
     */
    private ClientGame thisGame;
    /**
     * The player representing this client in the current game.
     * It is stored only for code readability and optimization purposes.
     */
    private ClientPlayer thisPlayer;
    /**
     * The eventually opened awaiting popup, stored so that it can be hidden when exiting the awaiting state.
     */
    private OverlayPopup openedAwaitingPopup;
    private final Button ZOOM_OWN_FIELD_BUTTON;

    private final Parent SCENE_ROOT;
    private final AnchorPane OWN_FIELD_PANE;
    private final VBox OWN_FIELD_STATS_BOX;
    private final AnchorPane OWN_FIELD_FRAME_PANE;
    private final ScrollPane OWN_FIELD_SCROLL_PANE;
    private final Circle NEW_CHAT_MESSAGE_NOTIFICATION;
    private final Button CENTER_OWN_FIELD_BUTTON;
    private final Button TOGGLE_SCOREBOARD_BUTTON;
    private final Button TOGGLE_CHAT_BUTTON;
    private final AnchorPane SECRET_OBJECTIVE_LABEL_PANE;
    private final HBox RESOURCE_CARDS_HBOX;
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
    private final Label GOLD_CARDS_LABEL;
    private final HBox GOLD_CARDS_HBOX;
    private final HBox COMMON_OBJECTIVES_HBOX;
    private final HBox SECRET_OBJECTIVE_HBOX;
    private final Label SECRET_OBJECTIVE_LABEL;
    private final VBox AWAITING_STATE_BOX;
    private final Button AWAITING_EXIT_BUTTON;
    private final VBox LEADERBOARD_VBOX;
    private final Label LEADERBOARD_LABEL;
    private final Label WINNING_PLAYER_LABEL;
    private final Button LEADERBOARD_EXIT_BUTTON;
    private final Label TO;
    private final Label GAME_STATE_LABEL;

    /**
     * Constructs a {@code GUIGameView} instance (private constructor to prevent external
     * instantiation at will).
     * On initialization, it loads the graphical elements from the correct .fxml file.
     */
    private GUIGameView() {
        try {
            SCENE_ROOT = new FXMLLoader(GUIView.class.getResource("/Client/fxml/game_screen.fxml")).load();
        } catch (IOException e) {
            throw new RuntimeException(e); //Should never happen
        }
        OWN_FIELD_PANE = (AnchorPane) SCENE_ROOT.lookup("#ownFieldPane");
        OWN_FIELD_STATS_BOX = (VBox) OWN_FIELD_PANE.lookup("#ownFieldStatsBox");
        OWN_FIELD_FRAME_PANE = (AnchorPane) OWN_FIELD_PANE.lookup("#ownFieldFramePane");
        OWN_FIELD_SCROLL_PANE = (ScrollPane) OWN_FIELD_FRAME_PANE.lookup("#ownFieldScrollPane");
        ZOOM_OWN_FIELD_BUTTON = (Button) OWN_FIELD_FRAME_PANE.lookup("#zoomOwnFieldButton");
        CENTER_OWN_FIELD_BUTTON = (Button) OWN_FIELD_FRAME_PANE.lookup("#centerOwnFieldButton");
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
        OWN_HAND_PANE = (HBox) SCENE_ROOT.lookup("#ownHandPane");
        DECKS_AND_VISIBLE_CARDS_PANE = (AnchorPane) SCENE_ROOT.lookup("#decksAndVisibleCardsPane");
        RESOURCE_CARDS_LABEL = (Label) DECKS_AND_VISIBLE_CARDS_PANE.lookup("#resourceCardsLabel");
        RESOURCE_CARDS_HBOX = (HBox) DECKS_AND_VISIBLE_CARDS_PANE.lookup("#resourceCardsHBox");
        GOLD_CARDS_LABEL = (Label) DECKS_AND_VISIBLE_CARDS_PANE.lookup("#goldCardsLabel");
        GOLD_CARDS_HBOX = (HBox) DECKS_AND_VISIBLE_CARDS_PANE.lookup("#goldCardsHBox");
        COMMON_OBJECTIVES_LABEL = (Label) DECKS_AND_VISIBLE_CARDS_PANE.lookup("#commonObjectivesLabel");
        COMMON_OBJECTIVES_HBOX = (HBox) DECKS_AND_VISIBLE_CARDS_PANE.lookup("#commonObjectivesHBox");
        SECRET_OBJECTIVE_HBOX = (HBox) DECKS_AND_VISIBLE_CARDS_PANE.lookup("#secretObjectiveHBox");
        SECRET_OBJECTIVE_LABEL_PANE = (AnchorPane) DECKS_AND_VISIBLE_CARDS_PANE.lookup("#secretObjectiveLabelPane");
        SECRET_OBJECTIVE_LABEL = (Label) DECKS_AND_VISIBLE_CARDS_PANE.lookup("#secretObjectiveLabel");
        AWAITING_STATE_BOX = (VBox) SCENE_ROOT.lookup("#awaitingStateBox");
        AWAITING_EXIT_BUTTON = (Button) SCENE_ROOT.lookup("#awaitingExitButton");
        LEADERBOARD_VBOX = (VBox) SCENE_ROOT.lookup("#leaderboardVBox");
        LEADERBOARD_LABEL = (Label) SCENE_ROOT.lookup("#leaderboardLabel");
        WINNING_PLAYER_LABEL = (Label) SCENE_ROOT.lookup("#winningPlayerLabel");
        LEADERBOARD_EXIT_BUTTON = (Button) SCENE_ROOT.lookup("#leaderboardExitButton");
        TO = (Label) SCENE_ROOT.lookup("#to");
        CHAT_VBOX = (VBox) SCENE_ROOT.lookup("#chatBox");
        GAME_STATE_LABEL = (Label) SCENE_ROOT.lookup("#gameStateLabel");

        CARD_SIZES = new GenericPair<>(100.0, 66.0);
        CORNER_SCALE_FACTOR = new GenericPair<>(2.0 / 9, 2.0 / 5);
        PADDING_SIZE = 20.0;

        PLACE_CARD_DATA_FORMAT = new DataFormat("text/genericpair<integer,side>");
        TIMESTAMP_FORMATTER = new SimpleDateFormat("HH:mm:ss");
        LIGHT_DARK_BROWN_STYLE = "-fx-font-family: 'Bell MT'; -fx-background-color: #AEA175; -fx-border-color: #5B5437; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px;";

        thisGame = null;
        thisPlayer = null;
        shouldReset = true;

        openedAwaitingPopup = null;

        RUNNING_TRANSITIONS = new ArrayList<>();

        RELATIVE_SCOREBOARD_TOKEN_POSITIONS_OFFSETS = new ArrayList<>();

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

    /**
     * Returns the singleton instance of the {@code GUIGameView}, also initializing it if it had never been
     * instantiated, as per the Singleton pattern.
     *
     * @return The singleton instance
     */
    public static GUIGameView getInstance() {
        if (gameScreenController == null) {
            gameScreenController = new GUIGameView();
        }
        return gameScreenController;
    }

    /**
     * Stops all the running transitions indefinitely running, avoiding memory leaks and allowing them
     * to be deallocated and garbage collected.
     */
    private void deleteAllRunningTransitions() {
        while (!RUNNING_TRANSITIONS.isEmpty()) {
            RUNNING_TRANSITIONS.getLast().stop();
            RUNNING_TRANSITIONS.removeLast();
        }
    }

    /**
     * Generates the rectangular-shaped element representing an open corner given its coordinates on the
     * field, and also specifying whether a card drag action should have any effect on it.
     *
     * @param openCorner    The coordinates of the given open corner on the field.
     * @param isInteractive True if a placeCard command should be issued when a card is dragged on the
     *                      resulting graphic element, False otherwise.
     * @return The rectangular-shaped element representing the open corner.
     */
    private Rectangle generateOpenCornerShape(GenericPair<Integer, Integer> openCorner, boolean isInteractive) {
        var openCornerShape = new Rectangle(CARD_SIZES.getX(), CARD_SIZES.getY()) {
            public final GenericPair<Integer, Integer> COORDINATES = openCorner;
        };
        openCornerShape.setFill(Color.TRANSPARENT);
        openCornerShape.setStyle("-fx-stroke: white; -fx-stroke-width: 4; -fx-stroke-dash-array: 4 8;");
        openCornerShape.setArcWidth(10);
        openCornerShape.setArcHeight(10);

        if (isInteractive) {
            openCornerShape.setOnDragOver((event) -> {
                if (event.getGestureSource() != openCornerShape && event.getDragboard().hasContent(PLACE_CARD_DATA_FORMAT)) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
            });

            openCornerShape.setOnDragDropped((event) -> {
                if (event.getTransferMode() == TransferMode.MOVE) {
                    var data = event.getDragboard().getContent(PLACE_CARD_DATA_FORMAT);
                    if (data instanceof GenericPair<?, ?> placeCardData) {
                        ViewState.getCurrentState().placeCard(
                                openCornerShape.COORDINATES, (Integer) placeCardData.getX(), (Side) placeCardData.getY()
                        );
                        event.setDropCompleted(event.getDragboard().hasContent(PLACE_CARD_DATA_FORMAT));
                    }
                }
            });
        }

        return openCornerShape;
    }

    /**
     * Restyles the given icon button, setting to it the standard wanted style.
     *
     * @param button The button element to be styled.
     * @param iconResourceURL The string representing the URL to the icon image.
     */
    private void generateRectangularIconButton(Button button, String iconResourceURL) {
        ImageView image = new ImageView(String.valueOf(GUIGameView.class.getResource(iconResourceURL)));
        image.setFitHeight(20);
        image.setPreserveRatio(true);

        button.setGraphic(image);
        button.setPrefSize(25, 25);
        button.setStyle("-fx-border-radius: 5; -fx-border-width: 1px; -fx-border-color: black; -fx-background-color: white");
    }

    /**
     * Produces an AnchorPane for styling the frame around the card fields of the players.
     *
     * @param parentPane The pane which will contain the resulting AnchorPane.
     * @return The generated AnchorPane containing the styled frame for the field.
     */
    private AnchorPane generateFieldFramePane(Pane parentPane) {
        AnchorPane fieldFramePane = new AnchorPane();
        fieldFramePane.setPrefSize(parentPane.getPrefWidth(), parentPane.getPrefHeight());
        fieldFramePane.setStyle("-fx-background-image: url('/Client/images/game/fields_empty_frame.png'); -fx-background-size: stretch;" +
                "-fx-background-repeat: no-repeat;");
        fieldFramePane.setMouseTransparent(true);

        ImageView greenCorner = new ImageView(String.valueOf(GUIGameView.class.getResource("/Client/images/game/green_frame_corner.png")));
        greenCorner.setFitWidth(fieldFramePane.getPrefWidth() * 6 / 100);
        greenCorner.setPreserveRatio(true);

        ImageView redCorner = new ImageView(String.valueOf(GUIGameView.class.getResource("/Client/images/game/red_frame_corner.png")));
        redCorner.setFitWidth(fieldFramePane.getPrefWidth() * 6 / 100);
        redCorner.setPreserveRatio(true);

        ImageView blueCorner = new ImageView(String.valueOf(GUIGameView.class.getResource("/Client/images/game/blue_frame_corner.png")));
        blueCorner.setFitWidth(fieldFramePane.getPrefWidth() * 6 / 100);
        blueCorner.setPreserveRatio(true);

        ImageView purpleCorner = new ImageView(String.valueOf(GUIGameView.class.getResource("/Client/images/game/purple_frame_corner.png")));
        purpleCorner.setFitWidth(fieldFramePane.getPrefWidth() * 6 / 100);
        purpleCorner.setPreserveRatio(true);

        fieldFramePane.getChildren().addAll(greenCorner, redCorner, blueCorner, purpleCorner);
        greenCorner.relocate(
                fieldFramePane.getPrefWidth() * 3.5 / 100 - greenCorner.getFitWidth() / 2,
                fieldFramePane.getPrefHeight() * 6 / 100 - greenCorner.getFitWidth() / 2
        );
        redCorner.relocate(
                fieldFramePane.getPrefWidth() * 96.5 / 100 - greenCorner.getFitWidth() / 2,
                fieldFramePane.getPrefHeight() * 6 / 100 - greenCorner.getFitWidth() / 2
        );
        blueCorner.relocate(
                fieldFramePane.getPrefWidth() * 3.5 / 100 - greenCorner.getFitWidth() / 2,
                fieldFramePane.getPrefHeight() * 94 / 100 - greenCorner.getFitWidth() / 2
        );
        purpleCorner.relocate(
                fieldFramePane.getPrefWidth() * 96.5 / 100 - greenCorner.getFitWidth() / 2,
                fieldFramePane.getPrefHeight() * 94 / 100 - greenCorner.getFitWidth() / 2
        );

        return fieldFramePane;
    }

    /**
     * Draws a field and all the cards places on it on the given ScrollPane.
     *
     * @param fieldPane The ScrollPane which will contain the field.
     * @param player The player of whom the field being drawn belongs to.
     * @param scaleFactor The scale factor of the field, allowing it to be resized bigger or smaller at will.
     * @param isInteractive True if the user should be able to drag and place cards on the resulting field,
     *                      False otherwise.
     */
    private void drawField(ScrollPane fieldPane, ClientPlayer player, double scaleFactor, boolean isInteractive) {
        GenericPair<Double, Double> clippedPaneSize = new GenericPair<>(
                4000.0, 4000.0 * CARD_SIZES.getY() / CARD_SIZES.getX()
        );

        fieldPane.setStyle("-fx-background-color: transparent;");

        fieldPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        fieldPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        AnchorPane clippedPane = new AnchorPane();
        clippedPane.setPrefSize(clippedPaneSize.getX(), clippedPaneSize.getY());
        clippedPane.setStyle("-fx-background-image: url('/Client/images/game/fields_bg_small.jpg'); -fx-background-size: cover;" +
                "-fx-background-repeat: no-repeat;");
        clippedPane.setCenterShape(true);

        for (var cardEntry : player.getPlacedCards().sequencedEntrySet()) {
            ImageView cardImage = new ImageView(String.valueOf(GUIView.class.getResource(cardEntry.getValue().getX().GUI_SPRITES.get(cardEntry.getValue().getY()))));
            cardImage.setSmooth(true);
            cardImage.setFitWidth(CARD_SIZES.getX());
            cardImage.setFitHeight(CARD_SIZES.getY());
            cardImage.setPreserveRatio(true);

            clippedPane.getChildren().add(cardImage);

            cardImage.relocate(
                    (clippedPaneSize.getX() - cardImage.getFitWidth()) / 2 +
                            cardImage.getFitWidth() * (1 - CORNER_SCALE_FACTOR.getX()) * cardEntry.getKey().getX(),
                    (clippedPaneSize.getY() - cardImage.getFitHeight()) / 2 -
                            cardImage.getFitHeight() * (1 - CORNER_SCALE_FACTOR.getY()) * cardEntry.getKey().getY()
            );
        }

        ParallelTransition openCornersBlinkTransition = new ParallelTransition();

        for (var openCorner : player.getOpenCorners()) {
            var openCornerShape = generateOpenCornerShape(openCorner, isInteractive);

            clippedPane.getChildren().add(openCornerShape);

            openCornerShape.relocate(
                    (clippedPaneSize.getX() - CARD_SIZES.getX()) / 2 +
                            CARD_SIZES.getX() * (1 - CORNER_SCALE_FACTOR.getX()) * openCorner.getX(),
                    (clippedPaneSize.getY() - CARD_SIZES.getY()) / 2 -
                            CARD_SIZES.getY() * (1 - CORNER_SCALE_FACTOR.getY()) * openCorner.getY()
            );

            FadeTransition openCornerTransition = new FadeTransition(Duration.millis(1000), openCornerShape);
            openCornerTransition.setFromValue(0.2);
            openCornerTransition.setToValue(1);
            openCornerTransition.setCycleCount(Animation.INDEFINITE);
            openCornerTransition.setAutoReverse(true);

            openCornersBlinkTransition.getChildren().add(openCornerTransition);
        }

        clippedPane.setScaleX(scaleFactor);
        clippedPane.setScaleY(scaleFactor);

        fieldPane.setContent(new Group(clippedPane));
        GenericPair<Integer, Integer> lastPlacedCardCoordinates;
        try {
            lastPlacedCardCoordinates = player.getPlacedCards().sequencedKeySet().getLast();
        } catch (NoSuchElementException e) {
            lastPlacedCardCoordinates = new GenericPair<>(0, 0);
        }
        fieldPane.setHvalue((fieldPane.getHmax() + fieldPane.getHmin()) * (
                clippedPaneSize.getX() / 2 + CARD_SIZES.getX() * (1 - CORNER_SCALE_FACTOR.getX()) * lastPlacedCardCoordinates.getX()
        ) / clippedPaneSize.getX());
        fieldPane.setVvalue((fieldPane.getVmax() + fieldPane.getVmin()) * (
                clippedPaneSize.getY() / 2 - CARD_SIZES.getY() * (1 - CORNER_SCALE_FACTOR.getY()) * lastPlacedCardCoordinates.getY()
        ) / clippedPaneSize.getY());

        openCornersBlinkTransition.play();
        RUNNING_TRANSITIONS.add(openCornersBlinkTransition);
    }

    /**
     * Enables the given pane to be dragged around on the screen.
     *
     * @param pane The pane to be made draggable.
     */
    private void makePaneDraggable(Pane pane) {
        AtomicReference<Double> xOffset = new AtomicReference<>(0.0);
        AtomicReference<Double> yOffset = new AtomicReference<>(0.0);

        pane.setOnMousePressed((event) -> {
            xOffset.set(pane.getLayoutX() - event.getScreenX());
            yOffset.set(pane.getLayoutY() - event.getScreenY());
            pane.toFront();
        });

        pane.setOnMouseDragged((event) -> pane.relocate(event.getScreenX() + xOffset.get(), event.getScreenY() + yOffset.get()));
    }

    /**
     * Toggles the visibility of the given pane.
     *
     * @param popupPane The pane whose visibility is to be toggled.
     */
    private void togglePane(Pane popupPane) {
        Platform.runLater(() -> {
            popupPane.setVisible(!popupPane.isVisible());
            popupPane.toFront();
        });
    }

    /**
     * Initializes the game screen elements, resetting all of them to their default values and contents.
     */
    private void resetGameScreen() {
        OverlayPopup.closeLingeringOpenedPopup();

        thisGame = VIEWMODEL.getCurrentGame();
        thisPlayer = thisGame.getThisPlayer();

        OPPONENTS_FIELDS_PANE.setPrefSize(
                windowSize.getX() - 2 * PADDING_SIZE,
                windowSize.getY() * 35 / 100 - 2 * PADDING_SIZE
        );
        OPPONENTS_FIELDS_PANE.relocate(PADDING_SIZE, PADDING_SIZE);

        DECKS_AND_VISIBLE_CARDS_PANE.setPrefSize(windowSize.getX() * 28 / 100, windowSize.getY() * 65 / 100);
        DECKS_AND_VISIBLE_CARDS_PANE.relocate(windowSize.getX() * 2 / 100, windowSize.getY() * 35 / 100);

        ImageView resourceCardsLabelBanner = new ImageView(String.valueOf(GUIGameView.class.getResource("/Client/images/game/scroll_label.png")));
        resourceCardsLabelBanner.setFitWidth(250);
        resourceCardsLabelBanner.setPreserveRatio(true);
        DECKS_AND_VISIBLE_CARDS_PANE.getChildren().add(resourceCardsLabelBanner);
        resourceCardsLabelBanner.relocate(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 5 / 100, 0);
        resourceCardsLabelBanner.toBack();
        RESOURCE_CARDS_LABEL.relocate(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 5 / 100 + 50, 12);
        RESOURCE_CARDS_HBOX.setPrefSize(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth(), DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 15 / 100);
        RESOURCE_CARDS_HBOX.relocate(0, DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 10 / 100);

        ImageView goldCardsLabelBanner = new ImageView(String.valueOf(GUIGameView.class.getResource("/Client/images/game/scroll_label.png")));
        goldCardsLabelBanner.setFitWidth(250);
        goldCardsLabelBanner.setPreserveRatio(true);
        DECKS_AND_VISIBLE_CARDS_PANE.getChildren().add(goldCardsLabelBanner);
        goldCardsLabelBanner.relocate(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 5 / 100, DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 25 / 100);
        goldCardsLabelBanner.toBack();
        GOLD_CARDS_LABEL.relocate(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 5 / 100 + 73, DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 25 / 100 + 12);
        GOLD_CARDS_HBOX.setPrefSize(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth(), DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 15 / 100);
        GOLD_CARDS_HBOX.relocate(0, DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 35 / 100);

        ImageView commonObjectivesLabelBanner = new ImageView(String.valueOf(GUIGameView.class.getResource("/Client/images/game/scroll_label.png")));
        commonObjectivesLabelBanner.setFitWidth(350);
        commonObjectivesLabelBanner.setFitHeight((double) (250 * 728) / 2408);
        DECKS_AND_VISIBLE_CARDS_PANE.getChildren().add(commonObjectivesLabelBanner);
        commonObjectivesLabelBanner.relocate((DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() - commonObjectivesLabelBanner.getFitWidth()) / 2, DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 50 / 100);
        commonObjectivesLabelBanner.toBack();
        COMMON_OBJECTIVES_LABEL.setPrefWidth(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 50 / 100);
        COMMON_OBJECTIVES_LABEL.relocate(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 25 / 100, DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 50 / 100 + 11);
        COMMON_OBJECTIVES_HBOX.setPrefSize(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth(), DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 15 / 100);
        COMMON_OBJECTIVES_HBOX.relocate(0, DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 60 / 100);

        ImageView secretObjectiveLabelBanner = new ImageView(String.valueOf(GUIGameView.class.getResource("/Client/images/game/scroll_label.png")));
        secretObjectiveLabelBanner.setFitWidth(260);
        secretObjectiveLabelBanner.setPreserveRatio(true);
        SECRET_OBJECTIVE_LABEL_PANE.getChildren().add(secretObjectiveLabelBanner);
        secretObjectiveLabelBanner.toBack();
        SECRET_OBJECTIVE_LABEL.relocate(50, 12);
        ImageView secretObjectiveCardView = (ImageView) SECRET_OBJECTIVE_HBOX.lookup("#secretObjectiveCard");
        if (secretObjectiveCardView != null)
            SECRET_OBJECTIVE_HBOX.getChildren().remove(secretObjectiveCardView);
        SECRET_OBJECTIVE_HBOX.relocate(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 22 / 100, DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 83 / 100);
        SECRET_OBJECTIVE_HBOX.setMinHeight(DECKS_AND_VISIBLE_CARDS_PANE.getPrefHeight() * 15 / 100);

        OWN_FIELD_PANE.setPrefSize(
                windowSize.getX() * 68 / 100 - PADDING_SIZE,
                windowSize.getY() * 50 / 100 - PADDING_SIZE
        );

        OWN_FIELD_PANE.relocate(windowSize.getX() * 30 / 100, windowSize.getY() * 35 / 100);

        OWN_FIELD_FRAME_PANE.setPrefSize(OWN_FIELD_PANE.getPrefWidth() - 75, OWN_FIELD_PANE.getPrefHeight());
        OWN_FIELD_FRAME_PANE.setLayoutX(80);
        AnchorPane ownFieldFramePane = generateFieldFramePane(OWN_FIELD_FRAME_PANE);
        OWN_FIELD_FRAME_PANE.getChildren().add(ownFieldFramePane);
        ownFieldFramePane.toFront();

        ImageView ownColorToken = new ImageView(String.valueOf(GUIView.class.getResource("/Client/images/misc/" +
                thisPlayer.getColor().name().toLowerCase() + ".png")));
        ownColorToken.setSmooth(true);
        ownColorToken.setFitHeight(40);
        ownColorToken.setPreserveRatio(true);

        OWN_FIELD_FRAME_PANE.getChildren().add(ownColorToken);
        ownColorToken.relocate(OWN_FIELD_FRAME_PANE.getPrefWidth() * 95.5 / 100 - 50, OWN_FIELD_FRAME_PANE.getPrefHeight() * 93.5 / 100 - 50);

        if (thisGame.getPlayers().indexOf(thisPlayer) == 0) {
            ImageView firstPlayerToken = new ImageView(String.valueOf(GUIView.class.getResource("/Client/images/misc/black.png")));
            firstPlayerToken.setSmooth(true);
            firstPlayerToken.setFitHeight(25);
            firstPlayerToken.setPreserveRatio(true);

            OWN_FIELD_FRAME_PANE.getChildren().add(firstPlayerToken);
            firstPlayerToken.relocate(ownFieldFramePane.getPrefWidth() * 95.5 / 100 - 42.25, ownFieldFramePane.getPrefHeight() * 93.5 / 100 - 42.25);
            firstPlayerToken.toFront();
        }

        OWN_FIELD_STATS_BOX.setPrefSize(75, OWN_FIELD_PANE.getPrefHeight());

        OWN_FIELD_SCROLL_PANE.setPrefSize(OWN_FIELD_FRAME_PANE.getPrefWidth() * 92 / 100, OWN_FIELD_FRAME_PANE.getPrefHeight() * 86 / 100);
        OWN_FIELD_SCROLL_PANE.relocate(OWN_FIELD_FRAME_PANE.getPrefWidth() * 4 / 100, OWN_FIELD_FRAME_PANE.getPrefHeight() * 7 / 100);

        OWN_HAND_PANE.setPrefSize(windowSize.getX() * 43 / 100, windowSize.getY() * 15 / 100);
        OWN_HAND_PANE.relocate(windowSize.getX() * 46 / 100, windowSize.getY() * 85 / 100);

        SCOREBOARD_PANE.setPrefSize(windowSize.getX() * 0.1328125, windowSize.getY() * 0.5);
        SCOREBOARD_PANE.relocate(windowSize.getX() * 10 / 100, windowSize.getY() * 40 / 100);
        makePaneDraggable(SCOREBOARD_PANE);

        CHAT_PANE.setPrefSize(windowSize.getX() * 30 / 100, windowSize.getY() * 70 / 100);
        CHAT_PANE.relocate(windowSize.getX() * 60 / 100, windowSize.getY() * 10 / 100);
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

        ImageView sendImage = new ImageView(String.valueOf(GUIGameView.class.getResource("/Client/images/icons/sendBetter.png")));
        sendImage.setFitHeight(20);
        sendImage.setPreserveRatio(true);
        SEND_MESSAGE_BUTTON.setGraphic(sendImage);
        SEND_MESSAGE_BUTTON.setPrefWidth(TOGGLE_CHAT_BUTTON.getPrefWidth() * 50 / 100);
        SEND_MESSAGE_BUTTON.setOnMouseClicked((event) -> {
            String receiver = RECEIVER_NICKNAME_SELECTOR.getValue();
            String message = MESSAGE_TEXTFIELD.getText().trim();
            if (receiver.equals("everyone"))
                ViewState.getCurrentState().broadcastMessage(message);
            else
                ViewState.getCurrentState().directMessage(receiver, message);
            MESSAGE_TEXTFIELD.clear();
        });

        ImageView scoreboardImage = new ImageView(String.valueOf(GUIGameView.class.getResource("/Client/images/icons/scoreboard.png")));
        scoreboardImage.setFitHeight(30);
        scoreboardImage.setPreserveRatio(true);
        TOGGLE_SCOREBOARD_BUTTON.setGraphic(scoreboardImage);
        TOGGLE_SCOREBOARD_BUTTON.toFront();
        TOGGLE_SCOREBOARD_BUTTON.relocate(40, windowSize.getY() - 50);
        TOGGLE_SCOREBOARD_BUTTON.setOnMouseClicked((event) -> togglePane(SCOREBOARD_PANE));

        NEW_CHAT_MESSAGE_NOTIFICATION.setRadius(10);
        NEW_CHAT_MESSAGE_NOTIFICATION.relocate(windowSize.getX() - 40 - 50 - 20 - 50 + 40, windowSize.getY() - 50 + 5);

        ImageView chatImage = new ImageView(String.valueOf(GUIGameView.class.getResource("/Client/images/icons/chat.png")));
        chatImage.setFitHeight(30);
        chatImage.setPreserveRatio(true);
        TOGGLE_CHAT_BUTTON.setGraphic(chatImage);
        TOGGLE_CHAT_BUTTON.toFront();
        TOGGLE_CHAT_BUTTON.relocate(windowSize.getX() - 40 - 50 - 20 - 50, windowSize.getY() - 50);
        TOGGLE_CHAT_BUTTON.setOnMouseClicked((event) -> {
            togglePane(CHAT_PANE);
            NEW_CHAT_MESSAGE_NOTIFICATION.setVisible(false);
        });

        ImageView leaveImage = new ImageView(String.valueOf(GUIGameView.class.getResource("/Client/images/icons/leaveGame.png")));
        leaveImage.setFitHeight(30);
        leaveImage.setPreserveRatio(true);
        LEAVE_BUTTON.setGraphic(leaveImage);
        LEAVE_BUTTON.toFront();
        LEAVE_BUTTON.relocate(windowSize.getX() - 40 - 50, windowSize.getY() - 50);
        LEAVE_BUTTON.setOnMouseClicked((event) -> showQuittingConfirmationPrompt());

        GAME_STATE_LABEL.setPrefSize(180, 150);
        GAME_STATE_LABEL.relocate(windowSize.getX() * 34 / 100, windowSize.getY() * 83.5 / 100);

        deleteAllRunningTransitions();

        stage.getScene().setRoot(SCENE_ROOT);
        shouldReset = false;
    }

    /**
     * Shows all the game elements composing the game board and illustrating the information related to the game.
     */
    @Override
    public void gameScreen() {
        Platform.runLater(() -> {
            if (openedAwaitingPopup != null) {
                openedAwaitingPopup.hide();
                openedAwaitingPopup = null;
            }
            if (shouldReset) resetGameScreen();

            deleteAllRunningTransitions();

            showOpponentsFieldsMiniaturized();
            showCommonPlacedCards();
            showOwnField();
            showHand();

            updateScoreboard();

            String printedMessage;

            if (ViewState.getCurrentState() instanceof AwaitingReconnectionState)
                printedMessage = "[GAME PAUSED] Awaiting for reconnection of other players...";
            else if (thisGame.getCurrentPlayerIndex() != -1)
                printedMessage =
                        ((thisGame.getTurnsLeftUntilGameEnds() == -1) ? "" : "[" + thisGame.getTurnsLeftUntilGameEnds() + " TURNS LEFT]\n") +
                                (ViewState.getCurrentState() instanceof PlayerTurnPlayState ? "[PLAY PHASE]" : "[DRAW PHASE]") +
                                "\nIt is " + thisGame.getPlayers().get(thisGame.getCurrentPlayerIndex()).getNickname() + "'s turn!";
            else printedMessage = "[SETUP PHASE] Every player needs to do an action!";

            GAME_STATE_LABEL.setText(printedMessage);
        });
    }

    /**
     * Helper function that encapsulates the logic used in showing the fields of opponent players.
     */
    private void showOpponentsFieldsMiniaturized() {
        OPPONENTS_FIELDS_PANE.getChildren().clear();

        for (int i = (thisGame.getPlayers().indexOf(thisPlayer) + 1) % thisGame.getPlayersNumber();
             i != thisGame.getPlayers().indexOf(thisPlayer); i = (i + 1) % thisGame.getPlayersNumber()) {
            ClientPlayer player = thisGame.getPlayers().get(i);

            VBox opponentInfo = new VBox(5);
            opponentInfo.setAlignment(Pos.CENTER);
            opponentInfo.setPrefSize(
                    (OPPONENTS_FIELDS_PANE.getPrefWidth() - 2 * 30) / 3,
                    OPPONENTS_FIELDS_PANE.getPrefHeight()
            );

            Label opponentName = new Label(
                    player.getNickname() + (thisGame.getCurrentPlayerIndex() > 0 &&
                            player.equals(thisGame.getPlayers().get(thisGame.getCurrentPlayerIndex())
                            ) ? " (IN TURN)" : "")
            );
            opponentName.setAlignment(Pos.CENTER);
            opponentName.setMaxWidth(300);
            opponentName.setPrefSize(opponentInfo.getPrefWidth(), opponentInfo.getPrefHeight() / 10);
            opponentName.getStyleClass().add("rectangularButton");

            HBox opponentData = new HBox(5);
            opponentData.setAlignment(Pos.CENTER);
            opponentData.setPrefSize(opponentInfo.getPrefWidth(), opponentInfo.getPrefHeight() * 9 / 10);

            VBox opponentStats = new VBox();
            opponentStats.setAlignment(Pos.CENTER);
            opponentStats.setPrefSize(55, opponentData.getPrefHeight());
            for (var resourceEntry : player.getOwnedResources().entrySet()) {
                HBox resourceData = new HBox();
                resourceData.setAlignment(Pos.CENTER);

                ImageView image = new ImageView(String.valueOf(GUIView.class.getResource("/Client/images/icons/res/" + resourceEntry.getKey().SYMBOL.toLowerCase() + ".png")));
                image.setSmooth(true);
                image.setFitWidth(30);
                image.setPreserveRatio(true);

                Label resourceInfo = new Label("" + resourceEntry.getValue());
                resourceInfo.setPrefWidth(25);
                resourceInfo.setStyle(LIGHT_DARK_BROWN_STYLE + "-fx-font-size: 18px;");
                resourceInfo.setAlignment(Pos.CENTER);

                resourceData.getChildren().addAll(image, resourceInfo);
                opponentStats.getChildren().add(resourceData);
            }

            AnchorPane opponentField = new AnchorPane();
            opponentField.setStyle("-fx-background-color: transparent;");
            opponentField.setPrefSize(opponentData.getPrefWidth() - 60, opponentData.getPrefHeight());

            AnchorPane fieldFramePane = generateFieldFramePane(opponentField);

            ScrollPane opponentScrollField = new ScrollPane();
            opponentScrollField.setPrefSize(opponentField.getPrefWidth() * 92 / 100,
                    opponentField.getPrefHeight() * 86 / 100);
            opponentScrollField.setPannable(true);
            drawField(opponentScrollField, player, 0.75, false);

            Button zoomOpponentFieldButton = new Button();
            generateRectangularIconButton(zoomOpponentFieldButton, "/Client/images/icons/zoom.png");
            zoomOpponentFieldButton.setOnMouseClicked((event) -> showField(player));

            Button centerOpponentFieldButton = new Button();
            generateRectangularIconButton(centerOpponentFieldButton, "/Client/images/icons/aim.png");
            centerOpponentFieldButton.setOnMouseClicked((event) -> {
                opponentScrollField.setHvalue((opponentScrollField.getHmax() + opponentScrollField.getHmin()) / 2);
                opponentScrollField.setVvalue((opponentScrollField.getVmax() + opponentScrollField.getVmin()) / 2);
            });

            ImageView opponentColorToken = new ImageView(String.valueOf(GUIView.class.getResource("/Client/images/misc/" +
                    player.getColor().name().toLowerCase() + ".png")));
            opponentColorToken.setSmooth(true);
            opponentColorToken.setFitHeight(40);
            opponentColorToken.setPreserveRatio(true);

            opponentField.getChildren().addAll(fieldFramePane, opponentScrollField, zoomOpponentFieldButton, centerOpponentFieldButton, opponentColorToken);
            opponentScrollField.relocate(opponentField.getPrefWidth() * 4 / 100, opponentField.getPrefHeight() * 7 / 100);
            zoomOpponentFieldButton.relocate(fieldFramePane.getPrefWidth() * 95.5 / 100 - 50, fieldFramePane.getPrefHeight() * 6.5 / 100 + 10);
            centerOpponentFieldButton.relocate(fieldFramePane.getPrefWidth() * 95.5 / 100 - 50, fieldFramePane.getPrefHeight() * 6.5 / 100 + 50);
            opponentColorToken.relocate(fieldFramePane.getPrefWidth() * 95.5 / 100 - 50, fieldFramePane.getPrefHeight() * 93.5 / 100 - 50);
            fieldFramePane.toFront();

            if (thisGame.getPlayers().indexOf(player) == 0) {
                ImageView firstPlayerToken = new ImageView(String.valueOf(GUIView.class.getResource("/Client/images/misc/black.png")));
                firstPlayerToken.setSmooth(true);
                firstPlayerToken.setFitHeight(25);
                firstPlayerToken.setPreserveRatio(true);

                opponentField.getChildren().add(firstPlayerToken);
                firstPlayerToken.relocate(opponentField.getPrefWidth() * 95.5 / 100 - 42.25, opponentField.getPrefHeight() * 93.5 / 100 - 42.25);
                firstPlayerToken.toFront();
            }

            if (!player.isActive()) {
                HBox inactivityPane = new HBox();
                inactivityPane.setPrefSize(opponentField.getPrefWidth(), opponentField.getPrefHeight());
                inactivityPane.setStyle("-fx-background-color: black;");
                inactivityPane.setOpacity(0.8);
                inactivityPane.setAlignment(Pos.CENTER);

                Label inactivityLabel = new Label("INACTIVE");
                inactivityLabel.getStyleClass().add("popupText");

                inactivityPane.getChildren().add(inactivityLabel);

                opponentField.getChildren().add(inactivityPane);
                inactivityPane.toFront();
            }

            opponentData.getChildren().addAll(opponentStats, opponentField);
            opponentInfo.getChildren().addAll(opponentName, opponentData);
            OPPONENTS_FIELDS_PANE.getChildren().add(opponentInfo);
        }
    }

    /**
     * Shows the common placed cards (resources, gold, objectives) on the left side of the screen.
     */
    @Override
    public void showCommonPlacedCards() {
        Platform.runLater(() -> {
            RESOURCE_CARDS_HBOX.getChildren().clear();

            if (thisGame.getTopDeckResourceCard().ID != -1) {
                ImageView resourceDeck = new ImageView(String.valueOf(GUIView.class.getResource(thisGame.getTopDeckResourceCard().GUI_SPRITES.get(Side.BACK))));
                resourceDeck.setSmooth(true);
                resourceDeck.setFitWidth(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 25 / 100);
                resourceDeck.setPreserveRatio(true);
                resourceDeck.setOnMouseClicked((event) ->
                        ViewState.getCurrentState().drawFromDeck("Resource")
                );
                RESOURCE_CARDS_HBOX.getChildren().add(resourceDeck);
            }

            for (int i = 0; i < thisGame.getPlacedResources().length; i++) {
                if (thisGame.getPlacedResources()[i].ID != -1) {
                    ImageView resource = new ImageView(String.valueOf(GUIView.class.getResource(thisGame.getPlacedResources()[i].GUI_SPRITES.get(Side.FRONT))));
                    resource.setSmooth(true);
                    resource.setFitWidth(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 25 / 100);
                    resource.setPreserveRatio(true);

                    int finalI = i + 1;
                    resource.setOnMouseClicked((event) ->
                            ViewState.getCurrentState().drawFromVisibleCards("Resource", finalI)
                    );

                    RESOURCE_CARDS_HBOX.getChildren().add(resource);
                }
            }

            GOLD_CARDS_HBOX.getChildren().clear();

            if (thisGame.getTopDeckGoldCard().ID != -1) {
                ImageView goldDeck = new ImageView(String.valueOf(GUIView.class.getResource(thisGame.getTopDeckGoldCard().GUI_SPRITES.get(Side.BACK))));

                goldDeck.setSmooth(true);
                goldDeck.setFitWidth(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 25 / 100);
                goldDeck.setPreserveRatio(true);
                goldDeck.setOnMouseClicked((event) -> ViewState.getCurrentState().drawFromDeck("Gold"));
                GOLD_CARDS_HBOX.getChildren().add(goldDeck);
            }

            for (int i = 0; i < thisGame.getPlacedGolds().length; i++) {
                if (thisGame.getPlacedGolds()[i].ID != -1) {
                    ImageView gold = new ImageView(String.valueOf(GUIView.class.getResource(thisGame.getPlacedGolds()[i].GUI_SPRITES.get(Side.FRONT))));
                    gold.setSmooth(true);
                    gold.setFitWidth(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 25 / 100);
                    gold.setPreserveRatio(true);

                    int finalI = i + 1;
                    gold.setOnMouseClicked((event) ->
                            ViewState.getCurrentState().drawFromVisibleCards("Gold", finalI)
                    );

                    GOLD_CARDS_HBOX.getChildren().add(gold);
                }
            }

            COMMON_OBJECTIVES_HBOX.getChildren().clear();

            for (int i = 0; i < thisGame.getCommonObjectives().length; i++) {
                if (thisGame.getCommonObjectives()[i] != null) {
                    ImageView commonObjective = new ImageView(String.valueOf(GUIView.class.getResource(thisGame.getCommonObjectives()[i].GUI_SPRITES.get(Side.FRONT))));
                    commonObjective.setSmooth(true);
                    commonObjective.setFitWidth(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 25 / 100);
                    commonObjective.setPreserveRatio(true);

                    COMMON_OBJECTIVES_HBOX.getChildren().add(commonObjective);
                }
            }

            if (thisGame.getOwnObjective() != null && SECRET_OBJECTIVE_HBOX.getChildren().size() < 2) {
                ImageView secretObjective = new ImageView(String.valueOf(GUIView.class.getResource(thisGame.getOwnObjective().GUI_SPRITES.get(Side.FRONT))));
                secretObjective.setId("secretObjectiveCard");
                secretObjective.setSmooth(true);
                secretObjective.setFitWidth(DECKS_AND_VISIBLE_CARDS_PANE.getPrefWidth() * 25 / 100);
                secretObjective.setPreserveRatio(true);

                SECRET_OBJECTIVE_HBOX.getChildren().add(secretObjective);
            }
        });
    }

    /**
     * Helper function that encapsulates the logic used in showing the field of the player corresponding
     * to this client.
     */
    private void showOwnField() {
        OWN_FIELD_STATS_BOX.getChildren().clear();

        for (var resourceEntry : thisPlayer.getOwnedResources().entrySet()) {
            HBox resourceData = new HBox();
            resourceData.setAlignment(Pos.CENTER);
            ImageView image = new ImageView(String.valueOf(GUIView.class.getResource("/Client/images/icons/res/" +
                    resourceEntry.getKey().SYMBOL.toLowerCase() + ".png")));
            image.setFitWidth(50);
            image.setPreserveRatio(true);

            Label resourceInfo = new Label("" + resourceEntry.getValue());
            resourceInfo.setPrefWidth(25);
            resourceInfo.setStyle(LIGHT_DARK_BROWN_STYLE + "-fx-font-size: 18px;");
            resourceInfo.setAlignment(Pos.CENTER);

            resourceData.getChildren().addAll(image, resourceInfo);

            OWN_FIELD_STATS_BOX.getChildren().add(resourceData);
            resourceData.relocate(0, 0);
        }

        drawField(OWN_FIELD_SCROLL_PANE, thisPlayer, 1, true);

        generateRectangularIconButton(ZOOM_OWN_FIELD_BUTTON, "/Client/images/icons/zoom.png");
        ZOOM_OWN_FIELD_BUTTON.setOnMouseClicked((event) -> showField(thisPlayer));

        generateRectangularIconButton(CENTER_OWN_FIELD_BUTTON, "/Client/images/icons/aim.png");
        CENTER_OWN_FIELD_BUTTON.setOnMouseClicked((event) -> {
            OWN_FIELD_SCROLL_PANE.setHvalue((OWN_FIELD_SCROLL_PANE.getHmax() + OWN_FIELD_SCROLL_PANE.getHmin()) / 2);
            OWN_FIELD_SCROLL_PANE.setVvalue((OWN_FIELD_SCROLL_PANE.getVmax() + OWN_FIELD_SCROLL_PANE.getVmin()) / 2);
        });

        ZOOM_OWN_FIELD_BUTTON.relocate(OWN_FIELD_FRAME_PANE.getPrefWidth() * 95.5 / 100 - 50, OWN_FIELD_FRAME_PANE.getPrefHeight() * 6.5 / 100 + 20);
        CENTER_OWN_FIELD_BUTTON.relocate(OWN_FIELD_FRAME_PANE.getPrefWidth() * 95.5 / 100 - 50, OWN_FIELD_FRAME_PANE.getPrefHeight() * 6.5 / 100 + 60);
    }

    /**
     * Shows the player's hand (front sides by default, but toggleable) at the bottom of the screen.
     */
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
                frontCardView.setFitWidth(pane.getPrefWidth() * 0.8);
                frontCardView.setPreserveRatio(true);

                ImageView backCardView = new ImageView(String.valueOf(GUIView.class.getResource(card.GUI_SPRITES.get(Side.BACK))));
                backCardView.setSmooth(true);
                backCardView.setFitWidth(pane.getPrefWidth() * 0.8);
                backCardView.setPreserveRatio(true);

                pane.getChildren().addAll(backCardView, frontCardView);
                backCardView.toBack();

                frontCardView.relocate(pane.getPrefWidth() * 0.1, pane.getPrefHeight() * 0.05);
                backCardView.relocate(pane.getPrefWidth() * 0.1, pane.getPrefHeight() * 0.05);

                frontCardView.setOnMouseClicked((event) -> backCardView.toFront());
                backCardView.setOnMouseClicked((event) -> frontCardView.toFront());

                int inHandPosition = i + 1;

                frontCardView.setOnDragDetected((event) -> {
                    Dragboard cardDragboard = frontCardView.startDragAndDrop(TransferMode.MOVE);

                    Image image = new Image(String.valueOf(GUIView.class.getResource(card.GUI_SPRITES.get(Side.FRONT))), 100, 150, true, true);
                    cardDragboard.setDragView(image, CARD_SIZES.getX() / 2, CARD_SIZES.getY() / 2);

                    ClipboardContent cardClipboard = new ClipboardContent();
                    cardClipboard.put(PLACE_CARD_DATA_FORMAT, new GenericPair<>(inHandPosition, Side.FRONT));
                    cardDragboard.setContent(cardClipboard);
                });

                backCardView.setOnDragDetected((event) -> {
                    Dragboard cardDragboard = backCardView.startDragAndDrop(TransferMode.MOVE);
                    Image image = new Image(String.valueOf(GUIView.class.getResource(card.GUI_SPRITES.get(Side.BACK))), 100, 150, true, true);
                    cardDragboard.setDragView(image, CARD_SIZES.getX() / 2, CARD_SIZES.getY() / 2);
                    ClipboardContent cardClipboard = new ClipboardContent();
                    cardClipboard.put(PLACE_CARD_DATA_FORMAT, new GenericPair<>(inHandPosition, Side.BACK));
                    cardDragboard.setContent(cardClipboard);
                });

                OWN_HAND_PANE.getChildren().add(pane);
            }
        });
    }

    /**
     * Helper function that encapsulates the logic used in updating the positions of the tokens on the scoreboard.
     */
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
                ImageView token = new ImageView(String.valueOf(GUIView.class.getResource("/Client/images/misc/" +
                        player.getColor().name().toLowerCase() + ".png")));
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

    /**
     * Updates the chat pane with the new log messages, also adding the timestamp indicating when
     * the message has been received.
     */
    @Override
    public void updateChat() {
        Platform.runLater(() -> {
            List<String> chatLog = thisGame.getChatLog();
            if (!chatLog.isEmpty()) {
                String fullMessage = chatLog.getLast();
                if (chatLog.size() >= 2 && !(chatLog.getLast().startsWith("[") || chatLog.getLast().startsWith("<")))
                    fullMessage = chatLog.get(chatLog.size() - 2) + fullMessage;

                fullMessage = "[" + TIMESTAMP_FORMATTER.format(new Date()) + "] " + fullMessage;

                MESSAGES_BOX.getChildren().add(createMessageElement(fullMessage));
            }

            CHAT_SCROLL_PANE.setVvalue(CHAT_SCROLL_PANE.getVmax());

            if (!CHAT_PANE.isVisible())
                NEW_CHAT_MESSAGE_NOTIFICATION.setVisible(true);
        });
    }

    /**
     * Renders the popup containing the two sides of the assigned initial card for the user to choose from.
     */
    @Override
    public void showInitialCardsChoice() {
        Platform.runLater(() -> {
            ClientCard initialCard = VIEWMODEL.getCurrentGame().getCardsInHand().getFirst();

            VBox initialCardsChoiceVBox = new VBox(30);
            initialCardsChoiceVBox.setAlignment(Pos.CENTER);
            initialCardsChoiceVBox.setPrefSize(windowSize.getX(), windowSize.getY() * 60 / 100);

            Label cardLabel = new Label("Choose which side you want to play your assigned initial card on: ");
            cardLabel.getStyleClass().add("popupText");
            cardLabel.setStyle("-fx-font-size: 24;");

            HBox initialChoiceHBox = new HBox(100);
            initialChoiceHBox.setAlignment(Pos.CENTER);
            initialChoiceHBox.setPrefSize(initialCardsChoiceVBox.getPrefWidth(), initialCardsChoiceVBox.getPrefHeight() * 0.9);

            initialCardsChoiceVBox.getChildren().addAll(cardLabel, initialChoiceHBox);

            OverlayPopup createdPopup = drawOverlayPopup(initialCardsChoiceVBox, false);

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

            choiceTransition.play();
            createdPopup.show(stage);
        });
    }

    /**
     * Renders the popup containing the two secret objective cards received for the user to choose from.
     *
     * @param objectivesSelection The list of objective cards available for selection.
     */
    @Override
    public void showObjectiveCardsChoice(ArrayList<ClientCard> objectivesSelection) {
        Platform.runLater(() -> {
            VBox objectiveChoiceVBox = new VBox(30);
            objectiveChoiceVBox.setAlignment(Pos.CENTER);
            objectiveChoiceVBox.setPrefSize(windowSize.getX(), windowSize.getY() * 60 / 100);

            Label cardLabel = new Label("Choose which card you want to keep as your secret objective: ");
            cardLabel.getStyleClass().add("popupText");
            cardLabel.setStyle("-fx-font-size: 24;");

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

            choiceTransition.play();
            createdPopup.show(stage);
        });
    }

    /**
     * Renders the field of a given player in a new popup that zooms it to make it more visible.
     *
     * @param player The player whose field is to be displayed.
     */
    @Override
    public void showField(ClientPlayer player) {
        Platform.runLater(() ->
        {
            AnchorPane popupContent = new AnchorPane();
            popupContent.setPrefSize(windowSize.getX() * 90 / 100, windowSize.getY() * 90 / 100);

            Label playerNameLabel = new Label(
                    player.getNickname() + (thisGame.getCurrentPlayerIndex() > 0 &&
                            player.equals(thisGame.getPlayers().get(thisGame.getCurrentPlayerIndex())
                            ) ? " (IN TURN)" : "")
            );
            playerNameLabel.setPrefSize(400, 50);
            playerNameLabel.getStyleClass().add("rectangularButton");
            playerNameLabel.setStyle("-fx-font-size: 20px;");

            ScrollPane fieldPane = new ScrollPane();
            fieldPane.setPannable(true);
            fieldPane.setPrefSize(popupContent.getPrefWidth(), popupContent.getPrefHeight());
            drawField(fieldPane, player, 1.5, false);

            ((Group) fieldPane.getContent()).getChildren().getFirst().setStyle("-fx-background-color: transparent;");

            popupContent.getChildren().addAll(fieldPane);

            Button centerFieldButton = new Button();
            generateRectangularIconButton(centerFieldButton, "/Client/images/icons/aim.png");
            centerFieldButton.setOnMouseClicked((event) -> {
                fieldPane.setHvalue((fieldPane.getHmax() + fieldPane.getHmin()) / 2);
                fieldPane.setVvalue((fieldPane.getVmax() + fieldPane.getVmin()) / 2);
            });

            popupContent.getChildren().add(centerFieldButton);
            centerFieldButton.relocate(popupContent.getPrefWidth() - 50, 60);

            OverlayPopup overlayPopup = drawOverlayPopup(popupContent, true);

            ((AnchorPane) overlayPopup.getContent().getFirst()).getChildren().add(playerNameLabel);
            playerNameLabel.relocate((popupContent.getPrefWidth() - playerNameLabel.getPrefWidth()) / 2, popupContent.getPrefHeight() / 20);

            overlayPopup.show(stage);
        });
    }

    /**
     * Signals to the user with a popup that he is the only remaining player connected to the game, and that
     * the game is currently paused due to disconnections.
     */
    @Override
    public void awaitingScreen() {
        OverlayPopup.closeLingeringOpenedPopup();

        Platform.runLater(() -> {
            AWAITING_STATE_BOX.setPrefSize(windowSize.getX() * 50 / 100, windowSize.getY() * 40 / 100);
            AWAITING_EXIT_BUTTON.setPrefSize(300, 50);

            openedAwaitingPopup = drawOverlayPopup(AWAITING_STATE_BOX, false);

            AWAITING_EXIT_BUTTON.setOnMouseClicked((mouseEvent -> {
                showQuittingConfirmationPrompt();
                openedAwaitingPopup.hide();
            }));

            openedAwaitingPopup.show(stage);
        });
    }

    /**
     * Shows the leaderboard at the end of the game in a new popup, containing player rankings and points.
     *
     * @param leaderboard                  List of triplets containing player names, scores, and ranks.
     * @param gameEndedDueToDisconnections True if the game ended due to disconnections.
     */
    @Override
    public void leaderboardScreen(List<Triplet<String, Integer, Integer>> leaderboard, boolean gameEndedDueToDisconnections) {
        Platform.runLater(() -> {
            LEADERBOARD_VBOX.setPrefSize(windowSize.getX() * 80 / 100, windowSize.getY() * 90 / 100);
            LEADERBOARD_VBOX.getChildren().clear();

            LEADERBOARD_LABEL.setPrefSize(400, 75);

            LEADERBOARD_EXIT_BUTTON.setPrefSize(300, 50);

            for (var row : leaderboard) {
                HBox playerHBox = new HBox(20);
                playerHBox.getStyleClass().add("lobbyBox");
                playerHBox.setMaxWidth(500);

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

            LEADERBOARD_VBOX.getChildren().addLast(WINNING_PLAYER_LABEL);
            LEADERBOARD_VBOX.getChildren().addLast(LEADERBOARD_EXIT_BUTTON);

            OverlayPopup createdPopup = drawOverlayPopup(LEADERBOARD_VBOX, false);

            LEADERBOARD_EXIT_BUTTON.setOnMouseClicked((mouseEvent -> {
                resetGameScreen();
                ViewState.getCurrentState().toLobbies();
                createdPopup.hide();
                shouldReset = true;
            }));

            createdPopup.show(stage);
        });
    }

    /**
     * Helper function that encapsulates the logic used in showing the prompt popup for asking the user to
     * confirm that they want to quit the game.
     */
    private void showQuittingConfirmationPrompt() {
        Platform.runLater(() -> {
            VBox quittingConfirmationPopupContent = new VBox();
            quittingConfirmationPopupContent.getStyleClass().add("decoratedPopup");
            quittingConfirmationPopupContent.setPrefSize(600, 350);

            Label quittingConfirmationLabel = new Label("Are you sure you want to quit?");
            quittingConfirmationLabel.getStyleClass().add("popupText");

            Button exitButton = new Button("BACK TO TITLE SCREEN");
            exitButton.getStyleClass().add("rectangularButton");
            exitButton.setMaxWidth(350);

            quittingConfirmationPopupContent.getChildren().addAll(quittingConfirmationLabel, exitButton);

            VBox.setMargin(quittingConfirmationLabel, new Insets(30, 30, 0, 30));
            VBox.setMargin(exitButton, new Insets(0, 0, 30, 0));

            OverlayPopup quittingConfirmationPopup = drawOverlayPopup(quittingConfirmationPopupContent, true);

            exitButton.setOnMouseClicked((event) -> {
                resetGameScreen();
                ViewState.getCurrentState().quit();
                shouldReset = true;
            });

            quittingConfirmationPopup.centerOnScreen();
            quittingConfirmationPopup.show(stage);
        });
    }

    /**
     * Creates a graphic element representing a message to be added to the chat.
     *
     * @param message The message to be added to the chat.
     * @return The HBox containing the given message.
     */
    private HBox createMessageElement(String message) {
        HBox messageBox = new HBox(250);
        messageBox.setPadding(new Insets(15, 12, 15, 12));

        String messageLabelStyle = "-fx-font-size: 14px; ";

        if (message.trim().startsWith("[SYSTEM]"))
            messageLabelStyle += "-fx-font-style: italic;";

        Label messageLabel = new Label(message);
        messageLabel.setPrefWidth(CHAT_SCROLL_PANE.getPrefWidth() * 80 / 100);
        messageLabel.setWrapText(true);
        messageLabel.setStyle(messageLabelStyle);

        messageBox.setPrefWidth(CHAT_SCROLL_PANE.getPrefWidth() * 80 / 100);
        messageBox.getStyleClass().add("messageElement");

        messageBox.getChildren().add(messageLabel);

        return messageBox;
    }
}
