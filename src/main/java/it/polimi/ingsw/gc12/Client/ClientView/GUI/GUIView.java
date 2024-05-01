package it.polimi.ingsw.gc12.Client.ClientView.GUI;

import it.polimi.ingsw.gc12.Client.ClientView.View;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
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
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class GUIView extends View {

    private static GUIView SINGLETON_GUI_INSTANCE = null;

    Parent root;
    Scene scene;
    Stage stage;
    ObservableList<String> languageList = FXCollections.observableArrayList("Italiano", "English");
    ObservableList<String> connectionList = FXCollections.observableArrayList("Socket", "RMI");

    @FXML
    Label statusLabel;

    @FXML
    TextField nicknameField;

    @FXML
    Button startButton;

    @FXML
    ComboBox<String> language;

    @FXML
    ComboBox<String> connection;

    @FXML
    Label error;

    @FXML
    TreeView newGame;

    @FXML
    Button join;

    public GUIView() {
    }

    public static GUIView getInstance() {
        if (SINGLETON_GUI_INSTANCE == null)
            SINGLETON_GUI_INSTANCE = new GUIView();
        return SINGLETON_GUI_INSTANCE;
    }

    @Override
    public void printError(Throwable t) {
        //TODO: popup con l'exception
    }

    @Override
    public void titleScreen() {
    }

    @FXML
    public void keyPressed(ActionEvent event) {
        //stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        ClientController.getInstance().viewState.keyPressed();
    }

    @Override
    public void connectToServerScreen() {
        try {
            selectLanguage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void selectLanguage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Language.fxml"));
        Parent root = fxmlLoader.load(); // Carica il file FXML e ottiene il root

        // Ottieni lo stage corrente e imposta la nuova scena
        //Scene scene = new Scene(root, 1200, 700);
        stage.getScene().setRoot(root);

        language = (ComboBox<String>) fxmlLoader.getNamespace().get("language");
        language.setPromptText("Select a Language");
        language.setItems(languageList);

        connection = (ComboBox<String>) fxmlLoader.getNamespace().get("connection");
        connection.setValue("Select a Connection");
        connection.setItems(connectionList);

        Button button = (Button) fxmlLoader.getNamespace().get("button");
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

        //FIXME: con questo nuovo metodo getScene().setRoot() non servono più!
        //stage.setScene(scene);
        //stage.setFullScreen(true);
        //stage.setMaximized(true);
        //stage.show();
    }

    @FXML
    protected void newPane(ActionEvent event) throws IOException {
        //Before changing scene, we notify the chosen comm technology to the controller so that it initializes it
        ClientController.getInstance().setCommunicationTechnology(connection.valueProperty().get());

        Parent root = FXMLLoader.load(getClass().getResource("/Second.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    @FXML
    protected void Yes(ActionEvent event) throws IOException {
        if (nicknameField.getText().length() >= 5) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Download.fxml"));
            Parent root = loader.load(); // Carica il file FXML e ottiene il root

            // Ottieni lo stage corrente e imposta la nuova scena
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1800, 850);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

            Label downloadLabel = (Label) loader.getNamespace().get("download");

            if (downloadLabel != null) {
                downloadLabel.setText("Ciao: " + nicknameField.getText() + " - Attendi la fine del caricamento");
            } else {
                System.out.println("Label non trovata nel FXML.");
            }

        } else if (nicknameField.getText().length() < 5 && !nicknameField.getText().isEmpty()) {
            statusLabel.setText("Nickname troppo corto, almeno 5 caratteri");
        } else {
            statusLabel.setText("Inserire prima un nickname");
        }

        new Thread(() -> ClientController.getInstance().viewState.connect(nicknameField.getText()));
    }


    @FXML
    protected void No() throws IOException {
        statusLabel.setText("Status: Not logged in");
    }

    @Override
    public void connectedConfirmation() {
    }

    @Override
    public void lobbyScreen() {
        if (nicknameField.getText().length() <= 0) {
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

        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Third.fxml"));
            Parent root = null; // Carica il file FXML e ottiene il root
            try {
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Stage oldStage = stage;
            stage = new Stage();
            oldStage.close();
            Scene scene = new Scene(root, 1800, 850);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        });
    }

    @Override
    public void gameScreen() {
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
    public void showField() {

    }

    @Override
    public void showHand() {

    }

    public void BackToTitleScreen(ActionEvent event) throws IOException {
        // Carica il FXML e ottiene il root node correttamente
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/First.fxml"));
        Parent root = fxmlLoader.load();  // Carica la vista FXML

        // Ottiene lo stage corrente da event source
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Crea la scena con la dimensione specificata
        Scene scene = new Scene(root, 1800, 850);

        // Accesso ai componenti nel namespace del FXMLLoader
        Label Codex = (Label) fxmlLoader.getNamespace().get("Codex");
        Button startButton = (Button) fxmlLoader.getNamespace().get("startButton");
        Rectangle backgroundLabel = (Rectangle) fxmlLoader.getNamespace().get("backgroundLabel");
        StackPane First = (StackPane) fxmlLoader.getNamespace().get("First");

        // Dimensione Schermo
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        StackPane.setAlignment(Codex, Pos.CENTER);
        StackPane.setMargin(Codex, new Insets(-screenHeight * 0.2, 0, 0, 0));
        StackPane.setAlignment(backgroundLabel, Pos.CENTER);
        StackPane.setMargin(backgroundLabel, new Insets(-screenHeight * 0.2, 0, 0, 0));
        StackPane.setAlignment(startButton, Pos.CENTER);
        StackPane.setMargin(startButton, new Insets(screenHeight * 0.425, 0, 0, 0));

        // Imposta la scena e massimizza il palcoscenico
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }


    public void NewGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fourth.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public void ChangeNickname(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Second.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public void LeaveGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Third.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}