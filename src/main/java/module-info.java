module it.polimi.ingsw.gc12 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens it.polimi.ingsw.gc12 to javafx.fxml;
    exports it.polimi.ingsw.gc12;
}