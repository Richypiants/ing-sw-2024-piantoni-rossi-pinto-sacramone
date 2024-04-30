package it.polimi.ingsw.gc12;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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

public class HelloController {

    Parent root;
    Scene scene;
    Stage stage;
    ObservableList<String> languageList = FXCollections.observableArrayList("Italiano", "English");
    ObservableList<String> connectionList = FXCollections.observableArrayList("Socket", "RMI");

    @FXML
    Label StatusLabel;

    @FXML
    TextField NicknameField;

    @FXML
    Button startButton;

    @FXML
    TreeView newGame;
    @FXML
    Button join;

    @FXML
    protected void selectLanguage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Language.fxml"));
        Parent root = loader.load(); // Carica il file FXML e ottiene il root

        // Ottieni lo stage corrente e imposta la nuova scena
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        ComboBox language = (ComboBox) loader.getNamespace().get("language");
        language.setValue("Select a Language");
        language.setItems(languageList);
    }

    @FXML
    protected void selectConnection(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Connection.fxml"));
        Parent root = loader.load(); // Carica il file FXML e ottiene il root

        // Ottieni lo stage corrente e imposta la nuova scena
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        ComboBox connection = (ComboBox) loader.getNamespace().get("connection");
        connection.setValue("Select a Connection");
        connection.setItems(connectionList);
    }

    @FXML
    protected void newPane(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Second.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    @FXML
    protected void Yes(ActionEvent event) throws IOException {
        if (NicknameField.getCharacters().length() >= 5) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Download.fxml"));
            Parent root = loader.load(); // Carica il file FXML e ottiene il root

            // Ottieni lo stage corrente e imposta la nuova scena
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1800, 850);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

            Label downloadLabel = (Label) loader.getNamespace().get("download");
            ProgressBar progressBar = (ProgressBar) loader.getNamespace().get("progressBar");

            if (downloadLabel != null) {
                downloadLabel.setText("Nickname OK - Caricamento");
            } else {
                System.out.println("Label non trovata nel FXML.");
            }

            if (progressBar != null) {
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() {
                        for (int i = 0; i <= 100; i++) {
                            updateProgress(i, 100);
                            try {
                                Thread.sleep(25); // Regola questa durata per gestire la velocità del caricamento
                            } catch (InterruptedException e) {
                                if (isCancelled()) {
                                    break;
                                }
                            }
                        }
                        return null;
                    }
                };

                progressBar.progressProperty().bind(task.progressProperty());
                Thread thread = new Thread(task);
                thread.setDaemon(true); // Imposta il thread come daemon per non impedire la chiusura dell'applicazione
                thread.start();
            } else {
                System.out.println("ProgressBar non trovata nel FXML.");
            }

        } else if (NicknameField.getCharacters().length() < 5 && NicknameField.getCharacters().length() > 0) {
            StatusLabel.setText("Nickname troppo corto, almeno 5 caratteri");
        } else {
            StatusLabel.setText("Inserire prima un nickname");
        }
    }


    @FXML
    protected void No() throws IOException {
        StatusLabel.setText("Status: Not logged in");
    }

    public void BackToTitleScreen(ActionEvent event) throws IOException {
        // Carica il FXML e ottiene il root node correttamente
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("First.fxml"));
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
        Parent root = FXMLLoader.load(getClass().getResource("Fourth.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public void ChangeNickname(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Second.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public void LeaveGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Third.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}