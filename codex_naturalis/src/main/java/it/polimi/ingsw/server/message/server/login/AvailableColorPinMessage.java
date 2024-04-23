package it.polimi.ingsw.server.message.server.login;

import it.polimi.ingsw.model.Color;

import java.util.HashMap;

public class AvailableColorPinMessage {
    public HashMap<Color, Boolean> availableColorPin = new HashMap<>();

    public AvailableColorPinMessage(HashMap<Color, Boolean> availableColorPin) {
        this.availableColorPin = availableColorPin;
    }

    public HashMap<Color, Boolean> getAvailableColorPinMessage() {
        availableColorPin=new HashMap<>();
        for (Color color : Color.values()) {
            availableColorPin.put(color, false);
        }
        return availableColorPin;
    }
}
