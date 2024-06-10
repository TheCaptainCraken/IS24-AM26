package it.polimi.ingsw.network.socket.messages.server.endgame;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;

import java.util.HashMap;

public class ShowPointsFromObjectives extends ServerMessage {
    private final HashMap<String,Integer> extraPoints;

    public ShowPointsFromObjectives(HashMap<String, Integer> extraPoints) {
        this.extraPoints = extraPoints;
    }

    public HashMap<String, Integer> getExtraPoints() {
        return extraPoints;
    }

    @Override
    public void callController(Controller controller) {
        controller.showExtraPoints(extraPoints);
    }
}
