module it.polimi.ingsw.gc12 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;

    opens it.polimi.ingsw.gc12.ServerModel to com.google.gson;
    opens it.polimi.ingsw.gc12.Utilities to com.google.gson;
    exports it.polimi.ingsw.gc12.Utilities to com.google.gson;
    exports it.polimi.ingsw.gc12.ServerModel to com.google.gson;
    opens it.polimi.ingsw.gc12 to javafx.fxml;
    exports it.polimi.ingsw.gc12;
    exports it.polimi.ingsw.gc12.ServerModel.GameStates to com.google.gson;
    opens it.polimi.ingsw.gc12.ServerModel.GameStates to com.google.gson;
    exports it.polimi.ingsw.gc12.ServerModel.Cards to com.google.gson;
    opens it.polimi.ingsw.gc12.ServerModel.Cards to com.google.gson;
    exports it.polimi.ingsw.gc12.ServerModel.Conditions to com.google.gson;
    opens it.polimi.ingsw.gc12.ServerModel.Conditions to com.google.gson;

}