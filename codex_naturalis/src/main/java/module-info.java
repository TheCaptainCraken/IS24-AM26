module it.polimi.ingsw {
    requires transitive javafx.graphics;

    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires java.desktop;
    requires java.rmi;

    opens it.polimi.ingsw to javafx.fxml;

    exports it.polimi.ingsw;
    exports it.polimi.ingsw.model.exception;
    exports it.polimi.ingsw.model;
    exports it.polimi.ingsw.network.RMI;
    exports it.polimi.ingsw.network.socket;
    exports it.polimi.ingsw.network;
}
