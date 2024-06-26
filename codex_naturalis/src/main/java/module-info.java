/**
 * This module descriptor for the it.polimi.ingsw module.
 * It specifies the dependencies of this module and the packages it exports.
 * It also opens the it.polimi.ingsw.view.gui package to javafx.fxml.
 *
 * The module requires javafx.graphics, javafx.controls, javafx.fxml, json.simple, java.desktop, and java.rmi.
 *
 * It exports the following packages:
 * - it.polimi.ingsw
 * - it.polimi.ingsw.model.exception
 * - it.polimi.ingsw.model
 * - it.polimi.ingsw.network.RMI
 * - it.polimi.ingsw.network.socket
 * - it.polimi.ingsw.network
 * - it.polimi.ingsw.view.gui
 * - it.polimi.ingsw.view
 * - it.polimi.ingsw.view.model
 * - it.polimi.ingsw.network.socket.messages.server
 */
module it.polimi.ingsw {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires java.desktop;
    requires java.rmi;

    opens it.polimi.ingsw.view.gui to javafx.fxml;

    exports it.polimi.ingsw;
    exports it.polimi.ingsw.model.exception;
    exports it.polimi.ingsw.model;
    exports it.polimi.ingsw.network.RMI;
    exports it.polimi.ingsw.network.socket;
    exports it.polimi.ingsw.network;
    exports it.polimi.ingsw.view.gui;
    exports it.polimi.ingsw.view;
    exports it.polimi.ingsw.view.model;
    exports it.polimi.ingsw.network.socket.messages.server;
}
