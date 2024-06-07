package it.polimi.ingsw.network.socket.messages.server.login;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;

import java.util.HashMap;

public class PlayersAndColorPins extends ServerMessage {
    private final HashMap<String, Color> map;

    public PlayersAndColorPins(HashMap<String, Color> map) {
        this.map = map;
    }

    public HashMap<String, Color> getMap() {
        return map;
    }

    @Override
    public void callController(Controller controller) {
        HashMap<String, Color> newMap = new HashMap<>();
        for (String p : map.keySet()) {
            newMap.put(p, map.get(p));
        }
        controller.refreshUsers(newMap);
    }
}
