package it.polimi.ingsw.network.messages.server.login;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.messages.server.ServerMessage;

import java.util.HashMap;

public class PlayersAndColorPins extends ServerMessage {
   private final HashMap<Player,Color> map;

    public PlayersAndColorPins(HashMap<Player, Color> map) {
        this.map = map;
    }

    public HashMap<Player, Color> getMap() {
        return map;
    }

    @Override
    public void callController(Controller controller) {
        HashMap<String,Color> newMap = new HashMap<>();
        for(Player p: map.keySet()){
            newMap.put(p.getName(), map.get(p));
        }
        controller.refreshUsers(newMap);
    }
}
