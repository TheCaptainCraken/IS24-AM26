package it.polimi.ingsw;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Sign;
import it.polimi.ingsw.model.exception.*;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.model.GameMaster;
import it.polimi.ingsw.model.Lobby;

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

        HashMap<Sign, Integer> test = game.getPlayerResources(game.getCurrentPlayer().getName());
        assert (!test.isEmpty()); //testing getPlayerResources()..

        assertEquals(game.getTurn(),0);//testing getTurn()..

        assertEquals(game.getPlayerPoints(game.getCurrentPlayer().getName()),0);//testing getPlayerPoints..


    }
    @Test
    public void PlayingPhaseTest() throws IOException, ParseException, WrongGamePhaseException, NoTurnException, NotExistsPlayerException {
        GameMaster game = new GameMaster(lobby,
                basePath + "resourceCardsDeck.json",
                basePath + "goldCardsDeck.json",
                basePath + "objectiveCardsDeck.json",
                basePath + "startingCardsDeck.json");

        for(int i = 0; i < lobby.getPlayers().length; i++){
            try{
                game.placeRootCard("marco",false);
            } catch(NoTurnException e){
                game.placeRootCard(game.getCurrentPlayer().getName(),false);
            }
        }

    }
}
