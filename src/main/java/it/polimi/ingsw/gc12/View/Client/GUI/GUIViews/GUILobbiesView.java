package it.polimi.ingsw.gc12.View.Client.GUI.GUIViews;

import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Utilities.Enums.Color;
import it.polimi.ingsw.gc12.View.Client.GUI.OverlayPopup;
import it.polimi.ingsw.gc12.View.Client.ViewStates.ViewState;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class GUILobbiesView extends GUIView {

    private static GUILobbiesView lobbiesScreenController = null;
    private final Parent SCENE_ROOT;
    private final VBox MENU_BUTTONS_BOX;
    private final Label OWN_NICKNAME_LABEL;
    private final Button CREATE_LOBBY_BUTTON;
    private final Button CHANGE_NICKNAME_BUTTON;
    private final Button BACK_TO_TITLE_SCREEN_BUTTON;
    private final VBox LOBBY_CREATION_POPUP_BOX;
    private final ComboBox<Integer> PLAYERS_NUMBER_SELECTOR;
    private final Button CONFIRM_LOBBY_CREATION_BUTTON;
    private final VBox CHANGE_NICKNAME_POPUP_BOX;
    private final TextField NICKNAME_FIELD;
    private final TextField CHANGE_NICKNAME_TEXTFIELD;
    private final Button CONFIRM_NICKNAME_CHANGE_BUTTON;
    private final ScrollPane LOBBIES_PANE;
    private final VBox LOBBIES_LIST;

    private GUILobbiesView() {
        try {
            SCENE_ROOT = new FXMLLoader(GUIView.class.getResource("/Client/fxml/lobby_menu.fxml")).load();
        } catch (IOException e) {
            throw new RuntimeException(e); //Should never happen
        }
        MENU_BUTTONS_BOX = (VBox) SCENE_ROOT.lookup("#buttonsBox");
        OWN_NICKNAME_LABEL = (Label) SCENE_ROOT.lookup("#ownNicknameLabel");
        CREATE_LOBBY_BUTTON = (Button) SCENE_ROOT.lookup("#createGameButton");
        CHANGE_NICKNAME_BUTTON = (Button) SCENE_ROOT.lookup("#nicknameButton");
        BACK_TO_TITLE_SCREEN_BUTTON = (Button) SCENE_ROOT.lookup("#backToTitleScreenButton");
        LOBBY_CREATION_POPUP_BOX = (VBox) SCENE_ROOT.lookup("#lobbyCreationPopupBox");
        PLAYERS_NUMBER_SELECTOR = (ComboBox<Integer>) SCENE_ROOT.lookup("#maxPlayersSelector");
        CONFIRM_LOBBY_CREATION_BUTTON = (Button) SCENE_ROOT.lookup("#confirmLobbyCreationButton");
        CHANGE_NICKNAME_POPUP_BOX = (VBox) SCENE_ROOT.lookup("#changeNicknamePopupBox");
        NICKNAME_FIELD = (TextField) SCENE_ROOT.lookup("#nicknameField");
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
            MENU_BUTTONS_BOX.relocate(screenSizes.getX() * 9 / 100, screenSizes.getY() * 9 / 16);

            OWN_NICKNAME_LABEL.setText("Your nickname: " + VIEWMODEL.getOwnNickname());

            LOBBY_CREATION_POPUP_BOX.setPrefSize(screenSizes.getX() * 60 / 100, screenSizes.getY() * 60 / 100);
            CHANGE_NICKNAME_POPUP_BOX.setPrefSize(screenSizes.getX() * 60 / 100, screenSizes.getY() * 60 / 100);

            PLAYERS_NUMBER_SELECTOR.setMinWidth(100);
            PLAYERS_NUMBER_SELECTOR.setItems(FXCollections.observableArrayList(2, 3, 4));

            CONFIRM_LOBBY_CREATION_BUTTON.setMinWidth(150);

            CREATE_LOBBY_BUTTON.setPrefSize(300, 50);
            CREATE_LOBBY_BUTTON.setOnMouseClicked(event -> {
                PLAYERS_NUMBER_SELECTOR.setValue(2);

                OverlayPopup lobbyCreationPopup = drawOverlayPopup(LOBBY_CREATION_POPUP_BOX, true);

                CONFIRM_LOBBY_CREATION_BUTTON.setOnAction(event2 -> {
                    ViewState.getCurrentState().createLobby(PLAYERS_NUMBER_SELECTOR.getValue());
                    lobbyCreationPopup.hide();
                });

                lobbyCreationPopup.centerOnScreen();
                lobbyCreationPopup.show(stage);
            });

            NICKNAME_FIELD.setMaxWidth(250);

            CONFIRM_NICKNAME_CHANGE_BUTTON.setMinWidth(150);

            CHANGE_NICKNAME_BUTTON.setPrefSize(300, 50);
            CHANGE_NICKNAME_BUTTON.setOnMouseClicked(event -> {
                OverlayPopup nicknameChangePopup = drawOverlayPopup(CHANGE_NICKNAME_POPUP_BOX, true);

                CONFIRM_NICKNAME_CHANGE_BUTTON.setOnAction(event2 -> {
                    ViewState.getCurrentState().setNickname(CHANGE_NICKNAME_TEXTFIELD.getText());
                    nicknameChangePopup.hide();
                });

                nicknameChangePopup.centerOnScreen();
                nicknameChangePopup.show(stage);
            });

            BACK_TO_TITLE_SCREEN_BUTTON.setPrefSize(300, 50);
            BACK_TO_TITLE_SCREEN_BUTTON.setOnAction(event -> ViewState.getCurrentState().quit());

            LOBBIES_PANE.setPrefSize(screenSizes.getX() * 89 / 100 - 320, screenSizes.getY() * 13 / 16);
            LOBBIES_PANE.relocate(screenSizes.getX() * 9 / 100 + 320, (screenSizes.getY() - LOBBIES_PANE.getPrefHeight()) / 2);

            LOBBIES_LIST.setMinHeight(LOBBIES_PANE.getPrefHeight() * 98 / 100);
            LOBBIES_LIST.setPrefWidth(LOBBIES_PANE.getPrefWidth() * 98 / 100);
            LOBBIES_LIST.getChildren().clear();

            //FIXME: brutto...
            Lobby currentLobby;
            try {
                currentLobby = VIEWMODEL.getCurrentLobby();
                if (VIEWMODEL.inRoom() && currentLobby != null)
                    LOBBIES_LIST.getChildren().add(createLobbyListElement(currentLobby));

                for (var lobby : VIEWMODEL.getLobbies().values()) {
                    if (!lobby.equals(currentLobby))
                        LOBBIES_LIST.getChildren().add(createLobbyListElement(lobby));
                }
            } catch (ClassCastException ignored) {
            }

            stage.getScene().setRoot(SCENE_ROOT);
        });
    }

    @Override
    public void showNickname() {
        Platform.runLater(() -> {
            OWN_NICKNAME_LABEL.setText("Your nickname: " + VIEWMODEL.getOwnNickname());
        });
    }

    private HBox createLobbyListElement(Lobby lobby) {
        HBox lobbyBox = new HBox(20);
        lobbyBox.getStyleClass().add("lobbyBox");
        lobbyBox.setPrefSize(LOBBIES_LIST.getPrefWidth() - 10, 10);

        Label lobbyUUIDLabel = new Label("" + lobby.getRoomUUID());
        lobbyUUIDLabel.setMinWidth(230);

        HBox nicknamesBox = new HBox(10);
        nicknamesBox.setPrefSize(lobbyBox.getPrefWidth() - 230 - 120 - 120 - 80, lobbyBox.getPrefHeight());
        nicknamesBox.getStyleClass().add("lobbyBox");

        for (var player : lobby.getPlayers()) {
            Label playerNameLabel = new Label(player.getNickname());
            playerNameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " +
                    (player.getColor().equals(Color.NO_COLOR) ? "black" : player.getColor().name().toLowerCase()) + ";");
            nicknamesBox.getChildren().add(playerNameLabel);
        }

        Label playerCountLabel = new Label(String.valueOf(lobby.getMaxPlayers()));

        HBox availableColorsBox = new HBox(10);
        availableColorsBox.setMaxWidth(120);
        availableColorsBox.getStyleClass().add("lobbyBox");

        for (var color : lobby.getAvailableColors()) {
            ImageView colorToken = new ImageView(String.valueOf(GUIView.class.getResource("/Client/images/misc/" + color.name().toLowerCase() + ".png")));
            colorToken.setSmooth(true);
            colorToken.setFitWidth(20);
            colorToken.setPreserveRatio(true);

            if (lobby.equals(VIEWMODEL.getCurrentLobby()))
                colorToken.setOnMouseClicked((event) -> ViewState.getCurrentState().selectColor(color));

            availableColorsBox.getChildren().add(colorToken);
        }

        lobbyBox.getChildren().addAll(lobbyUUIDLabel, nicknamesBox, playerCountLabel, availableColorsBox);

        if (VIEWMODEL.getCurrentLobby() == null && lobby.getPlayersNumber() < lobby.getMaxPlayers()) {
            Button joinButton = new Button("JOIN");
            joinButton.setMinWidth(120);
            joinButton.getStyleClass().add("rectangularButton");
            joinButton.setOnAction(e -> ViewState.getCurrentState().joinLobby(lobby.getRoomUUID()));
            lobbyBox.getChildren().add(joinButton);
        } else {
            if (lobby.equals(VIEWMODEL.getCurrentLobby())) {
                Button leaveButton = new Button("LEAVE");
                leaveButton.setMinWidth(120);
                leaveButton.getStyleClass().add("rectangularButton");
                leaveButton.setOnAction(e -> ViewState.getCurrentState().leaveLobby());
                lobbyBox.getChildren().add(leaveButton);
            }
        }

        return lobbyBox;
    }
}
