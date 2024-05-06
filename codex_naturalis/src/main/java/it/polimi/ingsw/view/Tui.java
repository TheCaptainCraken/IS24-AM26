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

    public void sameName(String nickname) {
        System.out.println("The nickname " + nickname + " is already taken. Please choose another one");
    }

    public void lobbyComplete() {
        System.out.println("The lobby is full. No other players can join");
    }

    public void colorAlreadyTaken() {
        System.out.println("The color you chose is already taken. Please choose another one");
        //TODO colori disponibili
    }

    public void noTurn() {
        System.out.println("It's not your turn. You can't perform this action");
    }

    public void notEnoughResources() {
        System.out.println("You don't have enough resources to perform this action");
        //TODO show resources
    }
}
