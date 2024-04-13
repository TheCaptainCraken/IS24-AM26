package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.exception.*;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameMasterTest {
    static GameMaster game;
    static Lobby lobby;
    static String basePath = "src/main/java/it/polimi/ingsw/model/decks/";

    @BeforeAll
    public static void setUp() throws SameNameException, LobbyCompleteException, IOException, ParseException {
        //create player
        lobby = new Lobby();
        lobby.addPlayer("pietro", Color.YELLOW);
        lobby.addPlayer("marco", Color.GREEN);
        lobby.addPlayer("giovanni", Color.RED);
        lobby.addPlayer("francesco", Color.BLUE);
    }

    @Test
    public void BasicGettersTest() throws IOException, ParseException, NoSuchFieldException {
        GameMaster game = new GameMaster(lobby,
                basePath + "resourceCardsDeck.json",
                basePath + "goldCardsDeck.json",
                basePath + "objectiveCardsDeck.json",
                basePath + "startingCardsDeck.json");

        assertEquals("pietro", game.getCurrentPlayer().getName());
        assertEquals(lobby.getPlayers().length,4);

        HashMap<Sign, Integer> test = game.getPlayerResources(game.getCurrentPlayer().getName());
        assert (!test.isEmpty()); //testing getPlayerResources()..

        assertEquals(game.getTurn(),-1);//testing getTurn()..

        assertEquals(game.getPlayerPoints(game.getCurrentPlayer().getName()),0);//testing getPlayerPoints..


    }

    @Test
    public void PlayingPhaseTest() throws IOException, ParseException, WrongGamePhaseException, NoTurnException, NotExistsPlayerException, NoSuchFieldException {
        GameMaster game = new GameMaster(lobby,
                basePath + "resourceCardsDeck.json",
                basePath + "goldCardsDeck.json",
                basePath + "objectiveCardsDeck.json",
                basePath + "startingCardsDeck.json");

        //Piazza RootCard...
        Player startingPlayer = game.getCurrentPlayer();
        for(int i = 0; i < lobby.getPlayers().length; i++){
            try{
                game.placeRootCard("marco",false);
            } catch(NoTurnException e) {
                game.placeRootCard(game.getCurrentPlayer().getName(), false);
            }
        }
        assertEquals(startingPlayer,game.getCurrentPlayer());

        //Scegli il tuo obbiettivo segreto...
        for(int i = 0; i < lobby.getPlayers().length; i++){
            try{
                game.chooseObjectiveCard("marco",0);
            } catch(NoTurnException e) {
                game.chooseObjectiveCard(game.getCurrentPlayer().getName(), 1);
            }
        }
        assertEquals(startingPlayer,game.getCurrentPlayer());

        for(int i = 0; i < lobby.getPlayers().length; i++){
            try{
                Point p = new Point(1,0);
                game.placeCard(game.getCurrentPlayer().getName(),game.getCurrentPlayer().getHand()[0],p,true );
            } catch (NotEnoughResourcesException e) {
                throw new RuntimeException(e);
            }
            assertEquals(game.getTurn(),1);
        }

    }
    /*quello sopra è inevitabilmente un test "cicciotto" poichè intende simulare un'inizio della partita, fino al piazzamento della prima carta
    * chiaramente è impossibile piazzare una carta PRIMA di avere fatto tutto il setup, dunque ho deciso di unire i due in un unica istanza*/
}
