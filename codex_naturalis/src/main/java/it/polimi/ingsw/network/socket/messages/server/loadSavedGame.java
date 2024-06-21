package it.polimi.ingsw.network.socket.messages.server;

import it.polimi.ingsw.model.GameMaster;
import it.polimi.ingsw.controller.client.Controller;

public class loadSavedGame extends ServerMessage {
    private GameMaster game;

    public loadSavedGame(GameMaster game) {
    }

    @Override
    public void callController(Controller controller) {
        controller.setModel(game);
    }
}
