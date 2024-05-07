package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Card;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.PlayableCard;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Tui {
    private final ArrayList<String> availableColors;
    private final ArrayList<Card> Cards;
    private final ArrayList<Card> StartingCards;
    private final ArrayList<Card> ObjectiveCards;
    private HashMap<String, Integer> points;


    public Tui(){
        //TODO colori
        //TODO vari deck
        availableColors = new ArrayList<>();
        availableColors.add("RED");
        availableColors.add("YELLOW");
        availableColors.add("GREEN");
        availableColors.add("BLUE");

        Cards = new ArrayList<>();
        StartingCards = new ArrayList<>();
        ObjectiveCards = new ArrayList<>();
        points = new HashMap<>();
    }

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

    public PlayedCard getTable(String nickname) {
        //TODO get the table (RootCard) of the player with the given nickname
        return null;
    }

    public void defaultMenu(){
        //TODO prints the default menu
    }

    public void getView(String nickname){
        printView(getTable(nickname));
    }

    public void printView(PlayedCard card){
        if(card == null){
            System.out.println("There is no card in this place");
            defaultMenu();
            return;
        }
        System.out.println("Arrows means that the first is on the other one, example: card1 -> card2, card1 is on card2 on the corner near the arrow");
        System.out.println(focusOnCard(card));//Useless first time
        System.out.println("Options: +\n" +
                "(top left) q   e (top right)\n" +
                "             s (quit)  \n" +
                "(bot left) z   c (bot right)\n");
        char option='s';//TODO Scanf con prossima opzione
        switch(option){
            case 'q':
                printView(card.getAttachmentCorners().get(Corner.TOP_LEFT));
                break;
            case 'e':
                printView(card.getAttachmentCorners().get(Corner.TOP_RIGHT));
                break;
            case 'z':
                printView(card.getAttachmentCorners().get(Corner.BOTTOM_LEFT));
                break;
            case 'c':
                printView(card.getAttachmentCorners().get(Corner.BOTTOM_RIGHT));
                break;
            default:
                defaultMenu();
        }
    }

    public String focusOnCard(PlayedCard focusedCard) {
        String toPrint = "";
        HashMap<Corner, PlayedCard> attachments = focusedCard.getAttachmentCorners();
        HashMap<Corner, String[]> attachmentsStrings = new HashMap<>();
        for(Corner c : attachments.keySet()){
            attachmentsStrings.put(c, printCard(attachments.get(c)));
        }
        HashMap<Corner, String[]> arrows = createArrows(attachments, focusedCard);

        for(int i=0; i<7; i++){
            toPrint += attachmentsStrings.get(Corner.TOP_LEFT)[i]+"                             "+attachmentsStrings.get(Corner.TOP_RIGHT)[i]+"\n";
        }
        toPrint += "                             "+arrows.get(Corner.TOP_LEFT)[0]+"                             "+arrows.get(Corner.TOP_RIGHT)[0]+"                             "+"\n";
        toPrint += "                             "+arrows.get(Corner.TOP_LEFT)[1]+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"+arrows.get(Corner.TOP_RIGHT)[1]+"                             "+"\n";
        toPrint += "                             "+arrows.get(Corner.TOP_LEFT)[2]+"                             "+arrows.get(Corner.TOP_RIGHT)[2]+"                             "+"\n";

        for(int i=0; i<7; i++){
            toPrint += "                             "+" ~ "+attachmentsStrings.get(Corner.TOP_LEFT)[i]+" ~ "+"                             "+"\n";
        }
        toPrint += "                             "+arrows.get(Corner.BOTTOM_LEFT)[0]+"                             "+arrows.get(Corner.BOTTOM_RIGHT)[0]+"                             "+"\n";
        toPrint += "                             "+arrows.get(Corner.BOTTOM_LEFT)[1]+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"+arrows.get(Corner.BOTTOM_RIGHT)[1]+"                             "+"\n";
        toPrint += "                             "+arrows.get(Corner.BOTTOM_LEFT)[2]+"                             "+arrows.get(Corner.BOTTOM_RIGHT)[2]+"                             "+"\n";
        for(int i=0; i<7; i++){
            toPrint += attachmentsStrings.get(Corner.BOTTOM_LEFT)[i]+"                             "+attachmentsStrings.get(Corner.BOTTOM_RIGHT)[i]+"\n";
        }
        return toPrint;
    }

    public String[] printCard(PlayedCard card) {
        HashMap<Corner, String> corner = new HashMap<>();
        String midSignsString;
        if(card==null){
            midSignsString="empty";
        }else{
            ArrayList<Sign> midSigns = new ArrayList<>();
            if (card.isFacingUp()) {
                for (Corner c : Corner.values()) {
                    corner.put(c, card.getCard().getCorners().get(c).toString());
                }
                midSigns.add(fromKingdomToSign(card.getCard().getKingdom()));
            } else {
                if (card.getCard() instanceof StartingCard) {
                    for (Corner c : Corner.values()) {
                        corner.put(c, ((StartingCard) card.getCard()).getBacksideCorners().get(c).toString());
                    }
                    for (Sign s : ((StartingCard) card.getCard()).getBonusResources()) {
                        midSigns.add(s);
                    }
                } else {
                    for (Corner c : Corner.values()) {
                        corner.put(c, Sign.EMPTY.toString());
                    }
                    midSigns.add(fromKingdomToSign(card.getCard().getKingdom()));
                }
            }
            switch (midSigns.size()) {
                case 1:
                    midSignsString = "  " + midSigns.get(0) + "  ";
                    break;
                case 2:
                    midSignsString = " " + midSigns.get(0) + " " + midSigns.get(1) + " ";
                    break;
                case 3:
                    midSignsString = midSigns.get(0) + " " + midSigns.get(1) + " " + midSigns.get(2);
                    break;
                default:
                    midSignsString = "     ";
                    break;
            }
        }
        return new String[]{
                "┌-----┐---------------┌-----┐",
                "|  "+corner.get(Corner.TOP_LEFT)+"  |               |  "+corner.get(Corner.TOP_RIGHT)+"  |",
                "└-----┘               └-----┘",
                "|           "+midSignsString+"          |",
                "┌-----┐               ┌-----┐",
                "|  "+corner.get(Corner.BOTTOM_LEFT)+"  |               |  "+corner.get(Corner.BOTTOM_RIGHT)+"  |",
                "└-----┘---------------└-----┘",
        };
    }

    public HashMap<Corner, String[]> createArrows(HashMap<Corner, PlayedCard> attachments, PlayedCard focusedCard){
        HashMap<Corner, String[]> arrows = new HashMap<>();
        for(Corner corner: Corner.values()){
            switch (corner){
                case TOP_LEFT:{
                    if (attachments.get(Corner.TOP_RIGHT).getTurnOfPositioning()>focusedCard.getTurnOfPositioning()){
                        arrows.put(Corner.TOP_RIGHT, new String[]{
                                " ",
                                " \\ ",
                                " -┘"
                        });
                    }else{
                        arrows.put(Corner.TOP_RIGHT, new String[]{
                                "┌- ",
                                "\\ ",
                                "   "
                        });
                    }
                }
                case TOP_RIGHT:{
                    if (attachments.get(Corner.TOP_RIGHT).getTurnOfPositioning()>focusedCard.getTurnOfPositioning()){
                        arrows.put(Corner.TOP_RIGHT, new String[]{
                                "   ",
                                " / ",
                                "└- "
                        });
                    }else{
                        arrows.put(Corner.TOP_RIGHT, new String[]{
                                " -┐",
                                " / ",
                                "   "
                        });
                    }
                }
                case BOTTOM_LEFT:{
                    if (attachments.get(Corner.BOTTOM_LEFT).getTurnOfPositioning()>focusedCard.getTurnOfPositioning()){
                        arrows.put(Corner.BOTTOM_LEFT, new String[]{
                                " -┐",
                                " / ",
                                "   "
                        });
                    }else{
                        arrows.put(Corner.BOTTOM_LEFT, new String[]{
                                "   ",
                                " / ",
                                "└- "
                        });
                    }
                }
                case BOTTOM_RIGHT:{
                    if (attachments.get(Corner.TOP_RIGHT).getTurnOfPositioning()>focusedCard.getTurnOfPositioning()){
                        arrows.put(Corner.TOP_RIGHT, new String[]{
                                "┌- ",
                                "\\ ",
                                "   "
                        });
                    }else{
                        arrows.put(Corner.TOP_RIGHT, new String[]{
                                " ",
                                " \\ ",
                                " -┘"
                        });
                    }
                }
            }
        }
        return arrows;
    }

    /**
     * Converts Kingdom enum to Sign enum
     * @param kingdom Kingdom to convert
     * @return Sign in which the Kingdom has been converted
     */
    private Sign fromKingdomToSign(Kingdom kingdom) throws IllegalArgumentException {
        switch (kingdom) {
            case PLANT:
                return Sign.LEAF;
            case ANIMAL:
                return Sign.WOLF;
            case FUNGI:
                return Sign.MUSHROOM;
            case INSECT:
                return Sign.BUTTERFLY;
        }
        throw new IllegalArgumentException("It's not a right Kingdom to convert");
    }
}
