package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Color;

import java.util.HashMap;

public class Tui {
    public void askNumberOfPlayer() {
        System.out.println("You are the first player. Please enter the number of players)");
        System.out.println("The number of players must be between 2 and 4");
    }

    public void waitLobby() {
        System.out.println("You are connected to the server. Please wait for the first player to choose the number of players");
    }

    public void stopWaiting() {
        //TODO
        System.out.println("You are connected to the server. Please wait for the first player to choose the number of players");
    }

    public void refreshUsers(HashMap<String, Color> playersAndPins) {
        System.out.println("The players in the lobby are:");
        for (String nickname : playersAndPins.keySet()) {
            //TODO color could be null
            System.out.println(nickname + " - " + playersAndPins.get(nickname));
        }
    }

    public void disconnect() {
        System.out.println("Lobby has been fulled with number of parameters chosen by the first player");
    }

    public void showStartingCard(int startingCardId) {
        //TODO get card from id
        System.out.println("The starting card is: " + startingCardId);
    }
}
