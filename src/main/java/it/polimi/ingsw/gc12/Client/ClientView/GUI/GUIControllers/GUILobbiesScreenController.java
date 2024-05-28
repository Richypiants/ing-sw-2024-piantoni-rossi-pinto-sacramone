package it.polimi.ingsw.gc12.Client.ClientView.GUI.GUIControllers;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Model.GameLobby;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;

import java.util.UUID;

public class GUILobbiesScreenController extends GUIView {

    static ObservableList<Integer> maxPlayersSelector = FXCollections.observableArrayList(2, 3, 4);

    public static void lobbiesScreen() {
        Platform.runLater(() -> {
            Parent root = sceneRoots.get("lobby_menu");

            Label profile = (Label) root.lookup("#profile");

            profile.setText("Profile: " + ClientController.getInstance().viewModel.getOwnNickname());
            profile.setTextAlignment(TextAlignment.CENTER);
            profile.setAlignment(Pos.TOP_LEFT);

            Button button = (Button) root.lookup("#BackTitleButton");
            button.setOnAction(event -> ClientController.getInstance().viewState.quit());

            ScrollPane lobbiesPane = (ScrollPane) root.lookup("#lobbiesPane");
            VBox lobbiesList = new VBox(10);
            lobbiesList.setPadding(new Insets(10));
            lobbiesList.setAlignment(Pos.TOP_CENTER);

            //TODO: invece di ricrearlo ogni volta, salvarlo e updatarlo?
            for (var lobby : ClientController.getInstance().viewModel.getLobbies().entrySet()) {
                lobbiesList.getChildren().add(GUILobbiesScreenController.createLobbyListElement(lobby.getKey(), lobby.getValue()));
            }

            lobbiesPane.setContent(lobbiesList);

            String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1; -fx-padding: 10;";

            Button lobby = (Button) root.lookup("#CreateGameButton");
            lobby.setOnAction(event -> {
                // New lobby Popup
                Popup lobbyPopup = new Popup();

                VBox lobbyCreationPopupBox = (VBox) root.lookup("#lobbyCreationPopupBox");

                ComboBox<Integer> maxPlayers = (ComboBox<Integer>) lobbyCreationPopupBox.lookup("#maxPlayersInput");
                maxPlayers.setValue(2);
                maxPlayers.setItems(maxPlayersSelector);

                Button okPlayers = (Button) root.lookup("#okPlayers");

                lobbyPopup.getContent().add(lobbyCreationPopupBox);

                lobbyPopup.setHeight(500);
                lobbyPopup.setWidth(700);
                lobbyCreationPopupBox.setStyle(style);

                lobbyPopup.setAutoFix(true);
                lobbyPopup.setAutoHide(true);
                lobbyPopup.setHideOnEscape(true);

                okPlayers.setOnAction(event2 -> {
                    ClientController.getInstance().viewState.createLobby(maxPlayers.getValue());
                    lobbyCreationPopupBox.setVisible(false);
                    lobbyPopup.hide();
                });

                lobbyPopup.show(stage);
                lobbyCreationPopupBox.setVisible(true);
                lobbyPopup.centerOnScreen();
            });

            // Nickname Popup
            Popup nickPopup = new Popup();

            VBox popupNickBox = new VBox(10);
            popupNickBox.setAlignment(Pos.CENTER);

            Label nickname = new Label("Inserisci un nuovo nickname");
            TextField nicknameField = new TextField();
            Button okNicknameButton = new Button("Ok");

            popupNickBox.getChildren().addAll(nickname, nicknameField, okNicknameButton);
            nickPopup.getContent().add(popupNickBox);

            nickPopup.setHeight(500);
            nickPopup.setWidth(700);
            popupNickBox.setStyle(style);

            nickPopup.setAutoFix(true);
            nickPopup.setAutoHide(true);
            nickPopup.setHideOnEscape(true);

            Button changeNickname = (Button) root.lookup("#nicknameButton");
            changeNickname.setOnAction(event -> {
                nickPopup.show(stage);
                nickPopup.centerOnScreen();
            });

            okNicknameButton.setOnAction(event -> {
                ClientController.getInstance().viewState.setNickname(nicknameField.getText());
                nickPopup.hide();
            });

            stage.getScene().setRoot(root);
        });
    }

    public static void showNickname() {
        Platform.runLater(() -> {
            Label profile = (Label) sceneRoots.get("lobby_menu").lookup("#profile");
            profile.setText("Profile: " + ClientController.getInstance().viewModel.getOwnNickname());
        });
    }

    private static HBox createLobbyListElement(UUID lobbyUUID, GameLobby lobby) {
        String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1; -fx-padding: 10;";

        // Box
        HBox lobbyBox = new HBox(100);
        lobbyBox.setPadding(new Insets(15, 12, 15, 12));
        lobbyBox.setStyle("-fx-alignment: CENTER; -fx-text-alignment: JUSTIFY; -fx-background-color: #00665C;");

        // Label giocatori
        Label playerCount = new Label(String.valueOf(lobby.getMaxPlayers()));
        playerCount.setStyle("-fx-font-size: 16px;");

        // Label nomi
        for (var player : lobby.getPlayers()) {
            Label playerName = new Label(player.getNickname());
            playerName.setStyle("-fx-font-size: 14px;");
            lobbyBox.getChildren().add(playerName);
        }

        lobbyBox.setStyle(style);
        lobbyBox.getChildren().add(playerCount);

        if (ClientController.getInstance().viewModel.getCurrentLobby() == null) {
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
