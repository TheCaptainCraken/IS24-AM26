module it.polimi.ingsw {
    requires transitive javafx.graphics;

    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;

    opens it.polimi.ingsw to javafx.fxml;

    exports it.polimi.ingsw;
}
