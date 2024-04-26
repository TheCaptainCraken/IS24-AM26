module it.polimi.ingsw {
    requires transitive javafx.graphics;

    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires java.desktop;
    requires java.rmi;

    opens it.polimi.ingsw to javafx.fxml;

    exports it.polimi.ingsw;
    exports it.polimi.ingsw.server;
    opens it.polimi.ingsw.server to javafx.fxml;
    exports it.polimi.ingsw.server.Client;
    opens it.polimi.ingsw.server.Client to javafx.fxml;
    exports it.polimi.ingsw.server.server;
    opens it.polimi.ingsw.server.server to javafx.fxml;
}
