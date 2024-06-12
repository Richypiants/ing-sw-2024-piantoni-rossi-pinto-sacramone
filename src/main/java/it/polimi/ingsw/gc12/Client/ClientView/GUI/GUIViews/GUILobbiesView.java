package it.polimi.ingsw.gc12.Client.ClientView.GUI.GUIViews;

import it.polimi.ingsw.gc12.Client.ClientView.GUI.OverlayPopup;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.ViewState;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Utilities.Color;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.UUID;

public class GUILobbiesView extends GUIView {

    private static GUILobbiesView lobbiesScreenController = null;
    private final Parent SCENE_ROOT;
    private final VBox MENU_BUTTONS_BOX;
    private final Label OWN_NICKNAME_LABEL;
    private final Button CREATE_LOBBY_BUTTON;
    private final Button CHANGE_NICKNAME_BUTTON;
    private final Button BACK_TO_TITLE_SCREEN_BUTTON;
    private final VBox LOBBY_CREATION_POPUP_BOX;
    private final Label PLAYERS_NUMBER_PROMPT;
    private final ComboBox<Integer> PLAYERS_NUMBER_SELECTOR;
    private final Button CONFIRM_LOBBY_CREATION_BUTTON;
    private final VBox CHANGE_NICKNAME_POPUP_BOX;
    private final Label CHANGE_NICKNAME_PROMPT;
    private final TextField CHANGE_NICKNAME_TEXTFIELD;
    private final Button CONFIRM_NICKNAME_CHANGE_BUTTON;
    private final ScrollPane LOBBIES_PANE;
    private final VBox LOBBIES_LIST;

    private GUILobbiesView() {
        try {
            SCENE_ROOT = new FXMLLoader(GUIView.class.getResource("/fxml/lobby_menu.fxml")).load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MENU_BUTTONS_BOX = (VBox) SCENE_ROOT.lookup("#buttonsBox");
        OWN_NICKNAME_LABEL = (Label) SCENE_ROOT.lookup("#ownNicknameLabel");
        CREATE_LOBBY_BUTTON = (Button) SCENE_ROOT.lookup("#createGameButton");
        CHANGE_NICKNAME_BUTTON = (Button) SCENE_ROOT.lookup("#nicknameButton");
        BACK_TO_TITLE_SCREEN_BUTTON = (Button) SCENE_ROOT.lookup("#backToTitleButton");
        LOBBY_CREATION_POPUP_BOX = (VBox) SCENE_ROOT.lookup("#lobbyCreationPopupBox");
        PLAYERS_NUMBER_PROMPT = (Label) SCENE_ROOT.lookup("#playersNumberPrompt");
        PLAYERS_NUMBER_SELECTOR = (ComboBox<Integer>) SCENE_ROOT.lookup("#maxPlayersSelector");
        CONFIRM_LOBBY_CREATION_BUTTON = (Button) SCENE_ROOT.lookup("#confirmLobbyCreationButton");
        CHANGE_NICKNAME_POPUP_BOX = (VBox) SCENE_ROOT.lookup("#changeNicknamePopupBox");
        CHANGE_NICKNAME_PROMPT = (Label) SCENE_ROOT.lookup("#nicknamePrompt");
        CHANGE_NICKNAME_TEXTFIELD = (TextField) SCENE_ROOT.lookup("#nicknameField");
        CONFIRM_NICKNAME_CHANGE_BUTTON = (Button) SCENE_ROOT.lookup("#confirmNicknameChangeButton");
        LOBBIES_PANE = (ScrollPane) SCENE_ROOT.lookup("#lobbiesPane");
        LOBBIES_LIST = (VBox) LOBBIES_PANE.getContent();
    }

    public static GUILobbiesView getInstance() {
        if (lobbiesScreenController == null) {
            lobbiesScreenController = new GUILobbiesView();
        }
        return lobbiesScreenController;
    }

    @Override
    public void lobbiesScreen() {
        Platform.runLater(() -> {
            MENU_BUTTONS_BOX.relocate(screenSizes.getX() * 12 / 100, screenSizes.getY() * 9 / 16);

            OWN_NICKNAME_LABEL.setText("Profile: " + ClientController.getInstance().VIEWMODEL.getOwnNickname());

            PLAYERS_NUMBER_SELECTOR.setItems(FXCollections.observableArrayList(2, 3, 4));

            CREATE_LOBBY_BUTTON.setOnMouseClicked(event -> {
                PLAYERS_NUMBER_SELECTOR.setValue(2);

                OverlayPopup lobbyCreationPopup = drawOverlayPopup(LOBBY_CREATION_POPUP_BOX, true);
                lobbyCreationPopup.setAutoFix(true);

                CONFIRM_LOBBY_CREATION_BUTTON.setOnAction(event2 -> {
                    ViewState.getCurrentState().createLobby(PLAYERS_NUMBER_SELECTOR.getValue());
                    LOBBY_CREATION_POPUP_BOX.setVisible(false);
                    lobbyCreationPopup.hide();
                });

                lobbyCreationPopup.show(stage);
                LOBBY_CREATION_POPUP_BOX.setVisible(true);
                lobbyCreationPopup.centerOnScreen();
            });

            CHANGE_NICKNAME_BUTTON.setOnMouseClicked(event -> {
                OverlayPopup nicknameChangePopup = drawOverlayPopup(CHANGE_NICKNAME_POPUP_BOX, true);
                nicknameChangePopup.setAutoFix(true);

                CONFIRM_NICKNAME_CHANGE_BUTTON.setOnAction(event2 -> {
                    ViewState.getCurrentState().setNickname(CHANGE_NICKNAME_TEXTFIELD.getText());
                    CHANGE_NICKNAME_POPUP_BOX.setVisible(false);
                    nicknameChangePopup.hide();
                });

                nicknameChangePopup.show(stage);
                CHANGE_NICKNAME_POPUP_BOX.setVisible(true);
                nicknameChangePopup.centerOnScreen();
            });

            BACK_TO_TITLE_SCREEN_BUTTON.setOnAction(event -> ViewState.getCurrentState().quit());

            LOBBIES_PANE.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            LOBBIES_PANE.setPrefSize(screenSizes.getX() * 3 / 5, screenSizes.getY() * 13 / 16);
            LOBBIES_PANE.relocate(screenSizes.getX() * 3 / 10, (screenSizes.getY() - LOBBIES_PANE.getPrefHeight()) / 2);

            LOBBIES_LIST.setMinHeight(LOBBIES_PANE.getPrefHeight() * 98 / 100);
            LOBBIES_LIST.setPrefWidth(LOBBIES_PANE.getPrefWidth() * 98 / 100);
            LOBBIES_LIST.getChildren().clear();

            if (ClientController.getInstance().VIEWMODEL.inRoom())
                LOBBIES_LIST.getChildren().add(createLobbyListElement(
                                ClientController.getInstance().VIEWMODEL.getCurrentRoomUUID(),
                                ClientController.getInstance().VIEWMODEL.getCurrentLobby()
                        )
                );

            //TODO: invece di ricrearlo ogni volta, salvarlo e updatarlo?
            for (var lobby : ClientController.getInstance().VIEWMODEL.getLobbies().entrySet()) {
                if (!lobby.getValue().equals(ClientController.getInstance().VIEWMODEL.getCurrentLobby()))
                    LOBBIES_LIST.getChildren().add(createLobbyListElement(lobby.getKey(), lobby.getValue()));
            }

            stage.getScene().setRoot(SCENE_ROOT);
        });
    }

    @Override
    public void showNickname() {
        Platform.runLater(() -> {
            OWN_NICKNAME_LABEL.setText("Profile: " + ClientController.getInstance().VIEWMODEL.getOwnNickname());
        });
    }

    private HBox createLobbyListElement(UUID lobbyUUID, Lobby lobby) {
        // Box
        HBox lobbyBox = new HBox(100);
        lobbyBox.getStyleClass().add("lobbyBox");
        lobbyBox.setPrefSize(LOBBIES_LIST.getPrefWidth() - 10, 10);

        HBox nicknamesBox = new HBox(10);
        nicknamesBox.getStyleClass().add("lobbyBox");

        // Label nomi
        for (var player : lobby.getPlayers()) {
            Label playerName = new Label(player.getNickname());
            playerName.setStyle("-fx-font-size: 14px; -fx-text-fill: " +
                    (player.getColor().equals(Color.NO_COLOR) ? "black" : player.getColor().name().toLowerCase()) + ";");
            nicknamesBox.getChildren().add(playerName);
        }

        // Label giocatori
        Label playerCount = new Label(String.valueOf(lobby.getMaxPlayers()));
        playerCount.setStyle("-fx-font-size: 16px;");

        HBox availableColorsBox = new HBox(10);
        availableColorsBox.getStyleClass().add("lobbyBox");

        for (var color : lobby.getAvailableColors()) {
            ImageView colorToken = new ImageView(String.valueOf(GUIView.class.getResource("/images/misc/" + color.name().toLowerCase() + ".png")));
            colorToken.setFitWidth(20);
            colorToken.setPreserveRatio(true);
            colorToken.setSmooth(true);

            if (lobby.equals(ClientController.getInstance().VIEWMODEL.getCurrentLobby()))
                colorToken.setOnMouseClicked((event) -> {
                    ViewState.getCurrentState().selectColor(color);
                });

            availableColorsBox.getChildren().add(colorToken);
        }

        lobbyBox.getChildren().addAll(nicknamesBox, playerCount, availableColorsBox);

        if (ClientController.getInstance().VIEWMODEL.getCurrentLobby() == null && lobby.getPlayersNumber() < lobby.getMaxPlayers()) {
            Button joinButton = new Button("JOIN");
            joinButton.setOnAction(e -> ViewState.getCurrentState().joinLobby(lobbyUUID));
            lobbyBox.getChildren().add(joinButton);
        } else {
            if (ClientController.getInstance().VIEWMODEL.getCurrentLobby().equals(lobby)) {
                Button leaveButton = new Button("LEAVE");
                leaveButton.setOnAction(e -> ViewState.getCurrentState().leaveLobby()
                );
                lobbyBox.getChildren().add(leaveButton);
            }
        }

        return lobbyBox;
    }
}
