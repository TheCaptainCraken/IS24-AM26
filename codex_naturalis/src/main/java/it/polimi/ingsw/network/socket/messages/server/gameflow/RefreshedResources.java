package it.polimi.ingsw.network.socket.messages.server.gameflow;

import it.polimi.ingsw.controller.client.Controller;
import it.polimi.ingsw.model.Sign;
import it.polimi.ingsw.network.socket.messages.server.ServerMessage;

import java.util.HashMap;

public class RefreshedResources extends ServerMessage {
    private final String nickname;
    private final HashMap<Sign,Integer> resources;

    public RefreshedResources(String nickname, HashMap<Sign, Integer> resources) {
        this.nickname = nickname;
        this.resources = resources;
    }

    public String getNickname() {
        return nickname;
    }

    public HashMap<Sign, Integer> getResources() {
        return resources;
    }

    @Override
    public void callController(Controller controller) {
        controller.updateResources(nickname,resources);
    }
}
