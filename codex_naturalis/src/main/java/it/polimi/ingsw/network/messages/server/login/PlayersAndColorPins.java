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
        HashMap<String,Color> newmap = new HashMap<>(); //copio hashmap evitare conflitti e incongruenze con lavoro lato server e lato controller
        for(Player p: map.keySet()){
            newmap.put(p.getName(), map.get(p));
        }
        controller.refreshUsers(newmap);
    }
}
