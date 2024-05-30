package it.polimi.ingsw.gc12.Client.ClientView.GUI.GUIControllers;

import it.polimi.ingsw.gc12.Client.ClientView.GUI.OverlayPopup;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Model.Lobby;
import it.polimi.ingsw.gc12.Utilities.Color;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.UUID;

public class GUILobbiesScreenController extends GUIView {

    static Parent sceneRoot = sceneRoots.get("lobby_menu");

    static VBox buttonsBox = (VBox) sceneRoot.lookup("#buttonsBox");

    static Label ownNicknameLabel = (Label) sceneRoot.lookup("#ownNicknameLabel");

    static Button createLobbyButton = (Button) sceneRoot.lookup("#createGameButton");

    static Button nicknameButton = (Button) sceneRoot.lookup("#nicknameButton");

    static Button backToTitleButton = (Button) sceneRoot.lookup("#backToTitleButton");

    static VBox lobbyCreationPopupBox = (VBox) sceneRoot.lookup("#lobbyCreationPopupBox");

    static Label playersNumberPrompt = (Label) sceneRoot.lookup("#playersNumberPrompt");

    static ComboBox<Integer> maxPlayersSelector = (ComboBox<Integer>) sceneRoot.lookup("#maxPlayersSelector");

    static Button confirmLobbyCreationButton = (Button) sceneRoot.lookup("#confirmLobbyCreationButton");

    static VBox changeNicknamePopupBox = (VBox) sceneRoot.lookup("#changeNicknamePopupBox");

    static Label nicknamePrompt = (Label) sceneRoot.lookup("#nicknamePrompt");

    static TextField nicknameField = (TextField) sceneRoot.lookup("#nicknameField");

    static Button confirmNicknameChangeButton = (Button) sceneRoot.lookup("#confirmNicknameChangeButton");

    static ScrollPane lobbiesPane = (ScrollPane) sceneRoot.lookup("#lobbiesPane");

    static VBox lobbiesList = (VBox) lobbiesPane.getContent();

    public static void lobbiesScreen() {
        Platform.runLater(() -> {
            buttonsBox.relocate(screenSizes.getX() * 12 / 100, screenSizes.getY() * 9 / 16);

            ownNicknameLabel.setText("Profile: " + ClientController.getInstance().viewModel.getOwnNickname());

            maxPlayersSelector.setItems(FXCollections.observableArrayList(2, 3, 4));

            createLobbyButton.setOnMouseClicked(event -> {
                maxPlayersSelector.setValue(2);

                OverlayPopup lobbyCreationPopup = drawOverlayPopup(lobbyCreationPopupBox, true);
                lobbyCreationPopup.setAutoFix(true);

                confirmLobbyCreationButton.setOnAction(event2 -> {
                    ClientController.getInstance().viewState.createLobby(maxPlayersSelector.getValue());
                    lobbyCreationPopupBox.setVisible(false);
                    lobbyCreationPopup.hide();
                });

                lobbyCreationPopup.show(stage);
                lobbyCreationPopupBox.setVisible(true);
                lobbyCreationPopup.centerOnScreen();
            });

            nicknameButton.setOnMouseClicked(event -> {
                OverlayPopup nicknameChangePopup = drawOverlayPopup(lobbyCreationPopupBox, true);
                nicknameChangePopup.setAutoFix(true);

                confirmLobbyCreationButton.setOnAction(event2 -> {
                    ClientController.getInstance().viewState.setNickname(nicknameField.getText());
                    lobbyCreationPopupBox.setVisible(false);
                    nicknameChangePopup.hide();
                });

                nicknameChangePopup.show(stage);
                lobbyCreationPopupBox.setVisible(true);
                nicknameChangePopup.centerOnScreen();
            });

            backToTitleButton.setOnAction(event -> ClientController.getInstance().viewState.quit());

            lobbiesPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            lobbiesPane.setPrefSize(screenSizes.getX() * 3 / 5, screenSizes.getY() * 13 / 16);
            lobbiesPane.relocate(screenSizes.getX() * 3 / 10, (screenSizes.getY() - lobbiesPane.getPrefHeight()) / 2);

            lobbiesList.setMinHeight(lobbiesPane.getPrefHeight() * 98 / 100);
            lobbiesList.setPrefWidth(lobbiesPane.getPrefWidth() * 98 / 100);
            lobbiesList.getChildren().clear();

            //TODO: invece di ricrearlo ogni volta, salvarlo e updatarlo?
            for (var lobby : ClientController.getInstance().viewModel.getLobbies().entrySet()) {
                lobbiesList.getChildren().add(GUILobbiesScreenController.createLobbyListElement(lobby.getKey(), (Lobby) lobby.getValue()));
            }

            stage.getScene().setRoot(sceneRoot);
        });
    }

    public static void showNickname() {
        Platform.runLater(() -> {
            Label profile = (Label) sceneRoots.get("lobby_menu").lookup("#profile");
            profile.setText("Profile: " + ClientController.getInstance().viewModel.getOwnNickname());
        });
    }

    private static HBox createLobbyListElement(UUID lobbyUUID, Lobby lobby) {
        // Box
        HBox lobbyBox = new HBox(100);
        lobbyBox.getStyleClass().add("lobbyBox");
        lobbyBox.setPrefSize(lobbiesList.getPrefWidth() - 10, 10);

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

            if (lobby.equals(ClientController.getInstance().viewModel.getCurrentLobby()))
                colorToken.setOnMouseClicked((event) -> {
                    ClientController.getInstance().viewState.selectColor(color);
                });

            availableColorsBox.getChildren().add(colorToken);
        }

        lobbyBox.getChildren().addAll(nicknamesBox, playerCount, availableColorsBox);

        if (ClientController.getInstance().viewModel.getCurrentLobby() == null && lobby.getPlayersNumber() < lobby.getMaxPlayers()) {
            Button joinButton = new Button("JOIN");
            joinButton.setOnAction(e ->
                    {
                        ClientController.getInstance().viewState.joinLobby(lobbyUUID);
                    }
            );
            lobbyBox.getChildren().add(joinButton);
        } else {
            if (ClientController.getInstance().viewModel.getCurrentLobby().equals(lobby)) {
                Button leaveButton = new Button("LEAVE");
                leaveButton.setOnAction(e ->
                        {
                            ClientController.getInstance().viewState.leaveLobby();
                        }
                );
                lobbyBox.getChildren().add(leaveButton);
            }
        }

        return lobbyBox;
    }
}
