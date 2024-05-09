package it.polimi.ingsw.gc12.Client.ClientView.GUI;

import it.polimi.ingsw.gc12.Client.ClientView.View;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class GUIView extends View {

    private static GUIView SINGLETON_GUI_INSTANCE = null;

    Stage stage;
    ObservableList<String> languageList = FXCollections.observableArrayList("Italiano", "English");
    ObservableList<String> connectionList = FXCollections.observableArrayList("Socket", "RMI");
    ObservableList<Integer> maxPlayersSelector = FXCollections.observableArrayList(2, 3, 4);

    @FXML
    TextField nicknameField;

    @FXML
    Label profile;

    @FXML
    ComboBox<String> language;

    @FXML
    ComboBox<String> connection;

    @FXML
    Label error;

    private GUIView() {
    }

    public static GUIView getInstance() {
        if (SINGLETON_GUI_INSTANCE == null)
            SINGLETON_GUI_INSTANCE = new GUIView();
        return SINGLETON_GUI_INSTANCE;
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

    @Override
    public void titleScreen() {
        FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("/fxml/title_screen.fxml"));
        fxmlLoader.setController(ClientController.getInstance().view);
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Scene scene = new Scene(root, 1280, 720);
        stage.setScene(scene);

        stage.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            if (event.getCode().equals(KeyCode.F11))
                stage.setFullScreen(!stage.isFullScreen());
            //TODO: + show hint per rientrare in fullscreen
        });

        Button startButton = (Button) fxmlLoader.getNamespace().get("startButton");
        startButton.setOnAction(this::keyPressed);
        //StackPane First = (StackPane) fxmlLoader.getNamespace().get("titleScreen");

        // Dimensione Schermo
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        StackPane.setAlignment(startButton, Pos.CENTER);
        StackPane.setMargin(startButton, new Insets(screenHeight * 0.8, 0, 0, 0));

        // Image icon = new Image("C:/Users/jacop/Desktop/Stage.png");
        // stage.getIcons().add(icon);

        stage.setTitle("Codex Naturalis");
        stage.setFullScreen(true);
        stage.setResizable(false);
        stage.centerOnScreen();
        //FIXME: non funziona e non indirizza gli input sulla schermata... stage.requestFocus();
        stage.show();
    }

    @FXML
    public void keyPressed(ActionEvent event) {
        ClientController.getInstance().viewState.keyPressed();
    }

    @Override
    public void connectToServerScreen() {
        try {
            selection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void selection() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/connection_setup.fxml"));
        fxmlLoader.setController(ClientController.getInstance().view);
        Parent root = fxmlLoader.load(); // Carica il file FXML e ottiene il root

        stage.getScene().setRoot(root);

        //TODO: al posto della lingua, far inserire indirizzo IP del server!
        ComboBox<String> language = (ComboBox<String>) fxmlLoader.getNamespace().get("language");
        language.setPromptText("Select language");
        language.setItems(languageList);

        ComboBox<String> connection = (ComboBox<String>) fxmlLoader.getNamespace().get("connection");
        connection.setValue("Select communication technology");
        connection.setItems(connectionList);

        Button button = (Button) fxmlLoader.getNamespace().get("button");
        button.setOnAction(event -> {
            try {
                waitingForConnection(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Label error = (Label) fxmlLoader.getNamespace().get("error");
        Label nicknameLabel = (Label) fxmlLoader.getNamespace().get("nicknameLabel");
        TextField nicknameField = (TextField) fxmlLoader.getNamespace().get("nicknameField");

        // Dimensione Schermo
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        StackPane.setAlignment(language, Pos.CENTER);
        StackPane.setMargin(language, new Insets(-screenHeight * 0.2, 0, 0, -screenHeight * 0.8));
        StackPane.setAlignment(connection, Pos.CENTER);
        StackPane.setMargin(connection, new Insets(-screenHeight * 0.2, 0, 0, screenHeight * 0.8));
        StackPane.setAlignment(button, Pos.CENTER);
        StackPane.setMargin(button, new Insets(screenHeight * 0.1, 0, 0, 0));
        StackPane.setAlignment(error, Pos.CENTER);
        StackPane.setMargin(error, new Insets(0, 0, 0, 0));

        StackPane.setAlignment(nicknameLabel, Pos.CENTER);
        StackPane.setMargin(nicknameLabel, new Insets(-screenHeight * 0.3, 0, 0, 0));
        StackPane.setAlignment(nicknameField, Pos.CENTER);
        StackPane.setMargin(nicknameField, new Insets(-screenHeight * 0.2, screenHeight * 0.75, 0, screenHeight * 0.75));
    }

    @FXML
    protected void waitingForConnection(ActionEvent event) throws IOException {
        if (nicknameField.getText().isEmpty()) {
            error.setText("Inserire un nickname prima di proseguire");
            return;
        }

        if (connection.valueProperty().get() == null) {
            error.setText("Selezionare una connessione prima di proseguire");
            return;
        }

        if (language.valueProperty().get() == null) {
            error.setText("Selezionare una lingua prima di proseguire");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/waiting_for_connection.fxml"));
        fxmlLoader.setController(ClientController.getInstance().view);
        Parent root = fxmlLoader.load(); // Carica il file FXML e ottiene il root

        stage.getScene().setRoot(root);

        Label downloadLabel = (Label) fxmlLoader.getNamespace().get("download");
        ProgressIndicator progressIndicator = (ProgressIndicator) fxmlLoader.getNamespace().get("progress");

        // Dimensione Schermo
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        StackPane.setAlignment(downloadLabel, Pos.CENTER);
        StackPane.setMargin(downloadLabel, new Insets(-screenHeight * 0.1, 0, 0, 0));
        StackPane.setAlignment(progressIndicator, Pos.CENTER);
        StackPane.setMargin(progressIndicator, new Insets(screenHeight * 0.1, 0, 0, 0));

        if (downloadLabel != null) {
            downloadLabel.setText("Ciao " + nicknameField.getText() + "\nStiamo caricando il Codex Naturalis");
            downloadLabel.setTextAlignment(TextAlignment.CENTER);
        } else {
            System.out.println("Label non trovata nel FXML");
        }

        // Before changing scene, we notify the chosen comm technology to the controller so that it initializes it
        new Thread(() -> ClientController.getInstance().viewState.connect(connection.valueProperty().get(), nicknameField.getText())).start();
    }

    @Override
    public void connectedConfirmation() {

    }

    @Override
    public void lobbyScreen() {
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/lobby_menu.fxml"));
            fxmlLoader.setController(ClientController.getInstance().view);
            Parent root = null;
            try {
                root = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            profile.setText("Profile: " + ClientController.getInstance().ownNickname);
            profile.setTextAlignment(TextAlignment.CENTER);
            profile.setAlignment(Pos.TOP_LEFT);

            Button button = (Button) fxmlLoader.getNamespace().get("BackTitleButton");
            button.setOnAction(event -> ClientController.getInstance().viewState.quit());

            ScrollPane lobbiesPane = (ScrollPane) fxmlLoader.getNamespace().get("lobbiesPane");
            VBox lobbiesList = new VBox(10);
            lobbiesList.setPadding(new Insets(10));
            lobbiesList.setAlignment(Pos.TOP_CENTER);

            //TODO: invece di ricrearlo ogni volta, salvarlo e updatarlo?
            for (var lobby : ClientController.getInstance().lobbies.entrySet()) {
                lobbiesList.getChildren().add(createLobbyListElement(lobby.getKey(), lobby.getValue()));
            }

            lobbiesPane.setContent(lobbiesList);

            String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1; -fx-padding: 10;";

            // New lobby Popup
            Popup lobbyPopup = new Popup();

            VBox popupLobbyBox = new VBox(10);
            popupLobbyBox.setAlignment(Pos.CENTER);

            ComboBox<Integer> maxPlayers = (ComboBox<Integer>) fxmlLoader.getNamespace().get("maxPlayersInput");
            maxPlayers.setValue(2);
            maxPlayers.setItems(maxPlayersSelector);

            Label players = (Label) fxmlLoader.getNamespace().get("players");
            Button okPlayers = (Button) fxmlLoader.getNamespace().get("okPlayers");

            popupLobbyBox.getChildren().addAll(players, maxPlayers, okPlayers);
            lobbyPopup.getContent().add(popupLobbyBox);

            lobbyPopup.setHeight(500);
            lobbyPopup.setWidth(700);
            popupLobbyBox.setStyle(style);

            lobbyPopup.setAutoFix(true);
            lobbyPopup.setAutoHide(true);
            lobbyPopup.setHideOnEscape(true);

            okPlayers.setOnAction(event -> {
                ClientController.getInstance().viewState.createLobby(maxPlayers.getValue());
                lobbyPopup.hide();
            });

            Button lobby = (Button) fxmlLoader.getNamespace().get("CreateGameButton");
            lobby.setOnAction(event -> {
                lobbyPopup.show(stage);
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

            Button changeNickname = (Button) fxmlLoader.getNamespace().get("nicknameButton");
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

    @Override
    public void gameScreen() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/game_screen.fxml"));
        fxmlLoader.setController(ClientController.getInstance().view);
        Parent root = null; // Carica il file FXML e ottiene il root
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        stage.getScene().setRoot(root);

        Button leaveGame = (Button) fxmlLoader.getNamespace().get("leaveButton");
        Button chat = (Button) fxmlLoader.getNamespace().get("chatButton");
        ImageView score = (ImageView) fxmlLoader.getNamespace().get("score");
        ImageView objective = (ImageView) fxmlLoader.getNamespace().get("objective");
        ImageView resource = (ImageView) fxmlLoader.getNamespace().get("resource");
        ImageView gold = (ImageView) fxmlLoader.getNamespace().get("gold");

        // Dimensione Schermo
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        // Posizionamento
        AnchorPane.setRightAnchor(leaveGame, 20.0);
        AnchorPane.setBottomAnchor(leaveGame, 20.0);

        AnchorPane.setRightAnchor(chat, 20.0);
        AnchorPane.setTopAnchor(chat, screenHeight * 0.5);

        AnchorPane.setTopAnchor(score, 20.0);
        AnchorPane.setLeftAnchor(score, 20.0);

        AnchorPane.setTopAnchor(objective, screenHeight * 0.6);
        AnchorPane.setLeftAnchor(objective, 20.0);

        AnchorPane.setTopAnchor(resource, screenHeight * 0.7);
        AnchorPane.setLeftAnchor(resource, 20.0);

        AnchorPane.setTopAnchor(gold, screenHeight * 0.8);
        AnchorPane.setLeftAnchor(gold, 20.0);
    }

    @Override
    public void updateNickname() {

    }

    @Override
    public void updateChat() {

    }

    @Override
    public void showInitialCardsChoice() {

    }

    @Override
    public void showObjectiveCardsChoice() {

    }

    @Override
    public void showCommonPlacedCards() {

    }

    @Override
    public void showField() {

    }

    @Override
    public void showHand() {

    }

    @Override
    public void showLeaderboard(ArrayList<Triplet<String, Integer, Integer>> POINT_STATS) {

    }

    public void NewGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/game_screen.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public void LeaveGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/lobby_menu.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private HBox createLobbyListElement(UUID lobbyUUID, GameLobby lobby) {
        String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1; -fx-padding: 10;";

        // Box
        HBox lobbyBox = new HBox(250);
        lobbyBox.setPadding(new Insets(15, 12, 15, 12));

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

        if (ClientController.getInstance().currentLobbyOrGame == null) {
            Button joinButton = new Button("JOIN");
            joinButton.setOnAction(e ->
                    {
                        ClientController.getInstance().viewState.joinLobby(lobbyUUID);
                    }
            );
            lobbyBox.getChildren().add(joinButton);
        } else {
            if (ClientController.getInstance().currentLobbyOrGame.equals(lobby)) {
                Button leaveButton = new Button("LEAVE");
                leaveButton.setOnAction(e ->
                        {
                            ClientController.getInstance().viewState.leaveLobby();
                        }
                );
                lobbyBox.getChildren().add(leaveButton);
            }
        }

        lobbyBox.getChildren().add(playerCount);

        return lobbyBox;
    }
}