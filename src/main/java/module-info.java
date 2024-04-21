module it.polimi.ingsw.gc12 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;
    requires java.rmi;
    requires jdk.javadoc;
    requires org.fusesource.jansi;

    opens it.polimi.ingsw.gc12.Model to com.google.gson;
    opens it.polimi.ingsw.gc12.Utilities to com.google.gson;
    exports it.polimi.ingsw.gc12.Model to com.google.gson;
    opens it.polimi.ingsw.gc12 to javafx.fxml;
    exports it.polimi.ingsw.gc12;
    exports it.polimi.ingsw.gc12.Model.GameStates to com.google.gson;
    opens it.polimi.ingsw.gc12.Model.GameStates to com.google.gson;
    exports it.polimi.ingsw.gc12.Model.Cards to com.google.gson;
    opens it.polimi.ingsw.gc12.Model.Cards to com.google.gson;
    exports it.polimi.ingsw.gc12.Model.Conditions to com.google.gson;
    opens it.polimi.ingsw.gc12.Model.Conditions to com.google.gson;

    exports it.polimi.ingsw.gc12.Utilities to com.google.gson, java.rmi;
    exports it.polimi.ingsw.gc12.Controller.ServerController to com.google.gson, java.rmi;
    opens it.polimi.ingsw.gc12.Controller.ServerController to com.google.gson;

    exports it.polimi.ingsw.gc12.Utilities.Exceptions;
    opens it.polimi.ingsw.gc12.Utilities.Exceptions to com.google.gson;
    exports it.polimi.ingsw.gc12.Controller;
    exports it.polimi.ingsw.gc12.Controller.ServerController.ServerCommands;
    exports it.polimi.ingsw.gc12.Controller.ClientController.ClientCommands;
    // exports it.polimi.ingsw.gc12.Client.ClientView.GUI;
}